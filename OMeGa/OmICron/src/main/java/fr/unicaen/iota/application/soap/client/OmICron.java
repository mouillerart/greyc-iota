/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.application.soap.client;

import fr.unicaen.iota.application.model.*;
import fr.unicaen.iota.application.soap.IoTaException;
import fr.unicaen.iota.application.soap.IoTaService;
import fr.unicaen.iota.application.soap.IoTaServicePortType;
import fr.unicaen.iota.ds.model.TEventItem;
import fr.unicaen.iota.ds.model.TServiceType;
import fr.unicaen.iota.mu.EPCISEventTypeHelper;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.tau.model.Identity;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.QueryParam;
import org.fosstrak.epcis.model.QueryParams;

/**
 */
public class OmICron implements X509TrustManager {

    private Identity identity;
    private IoTaServicePortType port;
    private static final Log log = LogFactory.getLog(OmICron.class);

    public OmICron(Identity id, String oAddress) {
        this(id, oAddress, null, null);
    }

    public OmICron(Identity id, String address, String pksFilename, String pksPassword) {
        log.trace("new OmICron: " + id + " @ " + address);
        this.identity = id;
        // TODO: TLS
        try {
            configureService(address, pksFilename, pksPassword);
        } catch (Exception e) {
            throw new RuntimeException("Can’t configure service: " + e.getMessage(), e);
        }
    }

    // TODO: TLS
    public void configureService(String address, String pksFilename, String pksPassword) throws Exception {
        URL wsdlUrl = new URL(address + "?wsdl");
        IoTaService service = new IoTaService(wsdlUrl);
        port = service.getPort(IoTaServicePortType.class);

        // turn off chunked transfer encoding
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpConduit.setClient(httpClientPolicy);

        // TODO: TLS
        if (pksFilename != null) {
            //log.debug("Authenticating with certificate in file: " + pksFilename);

            if (!wsdlUrl.getProtocol().equalsIgnoreCase("https")) {
                throw new Exception("Authentication method requires the use of HTTPS");
            }

            KeyStore keyStore = KeyStore.getInstance(pksFilename.endsWith(".p12") ? "PKCS12" : "JKS");
            keyStore.load(new FileInputStream(new File(pksFilename)), pksPassword.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, pksPassword.toCharArray());

            TLSClientParameters tlscp = new TLSClientParameters();
            tlscp.setKeyManagers(keyManagerFactory.getKeyManagers());
            tlscp.setSecureRandom(new SecureRandom());
            tlscp.setDisableCNCheck(true);
            tlscp.setTrustManagers(new TrustManager[]{this});

            httpConduit.setTlsClientParameters(tlscp);
        }
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity id) {
        this.identity = id;
    }

    public Map<ONSEntryType, String> queryONS(String epc) throws IoTaException {
        QueryONSRequest in = new QueryONSRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        QueryONSResponse out = port.queryONS(in);
        Map<ONSEntryType, String> res = new EnumMap<ONSEntryType, String>(ONSEntryType.class);
        for (OnsEntry entry : out.getOnsMap()) {
            ONSEntryType key = ONSEntryType.valueOf(entry.getKey());
            res.put(key, entry.getValue());
        }
        return res;
    }

    public String getReferentDS(String epc) throws IoTaException {
        GetReferentDSRequest in = new GetReferentDSRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        GetReferentDSResponse out = port.getReferentDS(in);
        return out.getUrl();
    }

    public List<EPCISEventType> traceEPC(String epc) throws IoTaException {
        TraceEPCRequest in = new TraceEPCRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        TraceEPCResponse out = port.traceEPC(in);
        return EPCISEventTypeHelper.listFromEventList(out.getEventList());
    }

    private QueryParams createQueryParams(Map<String, String> filters) {
        QueryParams queryParams = new QueryParams();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            QueryParam qp = new QueryParam();
            qp.setName(entry.getKey());
            qp.setValue(entry.getValue());
            queryParams.getParam().add(qp);
        }
        return queryParams;
    }

    public List<EPCISEventType> traceEPC(String epc, Map<String, String> filters) throws IoTaException {
        TraceEPCRequest in = new TraceEPCRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        in.setFilters(createQueryParams(filters));
        TraceEPCResponse out = port.traceEPC(in);
        return EPCISEventTypeHelper.listFromEventList(out.getEventList());
    }

    public String getEPCDocURL(String epc) throws IoTaException {
        GetEPCDocURLRequest in = new GetEPCDocURLRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        GetEPCDocURLResponse out = port.getEPCDocURL(in);
        return out.getUrl();
    }

    public List<EPCISEventType> queryEPCIS(String epc, String EPCISAddress) throws IoTaException {
        QueryEPCISRequest in = new QueryEPCISRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setEPCISAddress(EPCISAddress);
        in.setIdentity(identity);
        QueryEPCISResponse out = port.queryEPCIS(in);
        return EPCISEventTypeHelper.listFromEventList(out.getEventList());
    }

    public List<EPCISEventType> queryEPCIS(Map<String, String> filters, String EPCISAddress) throws IoTaException {
        QueryEPCISRequest in = new QueryEPCISRequest();
        in.setFilters(createQueryParams(filters));
        in.setEPCISAddress(EPCISAddress);
        in.setIdentity(identity);
        QueryEPCISResponse out = port.queryEPCIS(in);
        return EPCISEventTypeHelper.listFromEventList(out.getEventList());
    }

    public List<TEventItem> queryDS(String epc, String DSAddress) throws IoTaException {
        QueryDSRequest in = new QueryDSRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        in.setDSAddress(DSAddress);
        QueryDSResponse out = port.queryDS(in);
        return out.getEventList().getEvent();
    }

    public List<TEventItem> queryDS(String epc, String DSAddress, TServiceType serviceType) throws IoTaException {
        QueryDSRequest in = new QueryDSRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        in.setDSAddress(DSAddress);
        in.setServiceType(serviceType);
        QueryDSResponse out = port.queryDS(in);
        return out.getEventList().getEvent();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void main(String args[]) throws Exception {
        String serviceURL = "http://localhost:8080/omega";
        String sid = "anonymous";
        String epc = "urn:epc:id:sgtin:40000.00002.1298283877319";
        if (args.length != 3) {
            System.err.println("Usage: OmICron <OMeGa Web Service URL> <IDENTITY> <EPC URN ID>");
            System.err.println();
            System.err.println("example: OmICron " + serviceURL + " " + sid + " " + epc);
            System.exit(-1);
        } else {
            serviceURL = args[0];
            sid = args[1];
            epc = args[2];
        }
        Identity id = new Identity();
        id.setAsString(sid);
        OmICron client = new OmICron(id, serviceURL);

        System.out.println("Processing traceEPC ...");
        List<EPCISEventType> eventList = client.traceEPC(epc);
        if (eventList.isEmpty()) {
            System.out.println("  No events found.");
        } else {
            for (EPCISEventType evt : eventList) {
                EPCISEventTypeHelper e = new EPCISEventTypeHelper(evt);
                System.out.println("  Event found: " + e.getBizStep() + " " + e.getDisposition());
            }
        }
        System.out.println("Bye.");
    }
}
