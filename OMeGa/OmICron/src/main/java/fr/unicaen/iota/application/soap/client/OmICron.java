/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.application.soap.client;

import fr.unicaen.iota.application.model.*;
import fr.unicaen.iota.application.soap.IoTaException;
import fr.unicaen.iota.application.soap.IoTaService;
import fr.unicaen.iota.application.soap.IoTaServicePortType;
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.mu.EPCISEventTypeHelper;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.tau.model.Identity;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
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
public class OmICron {

    private Identity identity;
    private IoTaServicePortType port;
    private static final Log log = LogFactory.getLog(OmICron.class);

    public OmICron(Identity id, String oAddress) {
        this(id, oAddress, null, null, null, null);
    }

    public OmICron(Identity id, String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        log.trace("new OmICron: " + id + " @ " + address);
        this.identity = id;
        try {
            configureService(address, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        } catch (Exception e) {
            throw new RuntimeException("Can’t configure service: " + e.getMessage(), e);
        }
    }

    public void configureService(String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) throws Exception {
        if (pksFilename != null && pksPassword != null && trustPksFilename != null && trustPksPassword != null) {
            System.setProperty("javax.net.ssl.keyStore", pksFilename);
            System.setProperty("javax.net.ssl.keyStorePassword", pksPassword);
            System.setProperty("javax.net.ssl.trustStore", trustPksFilename);
            System.setProperty("javax.net.ssl.trustStorePassword", trustPksPassword);
        }
        URL wsdlUrl = new URL(address + "?wsdl");
        IoTaService service = new IoTaService(wsdlUrl);
        port = service.getPort(IoTaServicePortType.class);

        // turn off chunked transfer encoding
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpConduit.setClient(httpClientPolicy);

        if (pksFilename != null) {
            log.debug("Authenticating with certificate in file: " + pksFilename);

            if (!wsdlUrl.getProtocol().equalsIgnoreCase("https")) {
                throw new Exception("Authentication method requires the use of HTTPS");
            }

            KeyStore keyStore = KeyStore.getInstance(pksFilename.endsWith(".p12") ? "PKCS12" : "JKS");
            keyStore.load(new FileInputStream(new File(pksFilename)), pksPassword.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, pksPassword.toCharArray());

            KeyStore trustStore = KeyStore.getInstance(trustPksFilename.endsWith(".p12") ? "PKCS12" : "JKS");
            trustStore.load(new FileInputStream(new File(trustPksFilename)), trustPksPassword.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            TLSClientParameters tlscp = new TLSClientParameters();
            tlscp.setSecureRandom(new SecureRandom());
            tlscp.setKeyManagers(keyManagerFactory.getKeyManagers());
            tlscp.setTrustManagers(trustManagerFactory.getTrustManagers());

            httpConduit.setTlsClientParameters(tlscp);
        }
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity id) {
        this.identity = id;
    }

    /**
     * Queries the ONS for all NAPTR entries related to the given EPC code.
     *
     * @param EPC the EPC code
     * @return a mapping of all entries by service type
     * @throws RemoteException
     */
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

    /**
     * Queries the ONS for the URL of the referent Discovery Service for the
     * given EPC code.
     *
     * @param EPC the EPC code
     * @return the URL of the referent Discovery Service
     * @throws RemoteException
     */
    public String getReferentDS(String epc) throws IoTaException {
        GetReferentDSRequest in = new GetReferentDSRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        GetReferentDSResponse out = port.getReferentDS(in);
        return out.getUrl();
    }

    /**
     * Gets all the EPCIS events concerning a given EPC code.
     *
     * @param EPC the EPC code
     * @return a list of EPCIS events
     * @throws RemoteException
     */
    public List<EPCISEventType> traceEPC(String epc) throws IoTaException {
        TraceEPCRequest in = new TraceEPCRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        TraceEPCResponse out = port.traceEPC(in);
        return EPCISEventTypeHelper.listFromEventList(out.getEventList());
    }

    /**
     * Gets all the EPCIS events sorted by EPCIS concerning a given EPC code.
     *
     * @param EPC the EPC code
     * @return a list of EPCIS events by EPCIS
     * @throws RemoteException
     */
    public Map<String, List<EPCISEventType>> traceEPCByEPCIS(String epc) throws IoTaException {
        TraceEPCByEPCISRequest in = new TraceEPCByEPCISRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        TraceEPCByEPCISResponse out = port.traceEPCByEPCIS(in);
        Map<String, List<EPCISEventType>> results = new HashMap<String, List<EPCISEventType>>();
        for (EventsByEPCIS eventsByEPCIS: out.getEventsByEPCIS()) {
            List<EPCISEventType> eventList = EPCISEventTypeHelper.listFromEventList(eventsByEPCIS.getEventList());
            results.put(eventsByEPCIS.getEpcisAddress(), eventList);
        }
        return results;
    }

    /**
     * Gets all the EPCIS events concerning a given EPC code and matching the
     * given filters.
     *
     * @param EPC the EPC code
     * @param filters the filters
     * @return a list of EPCIS events
     * @throws RemoteException
     */
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

    /**
     * Gets all the EPCIS events sorted by EPCIS concerning a given EPC code and matching the
     * given filters.
     *
     * @param EPC the EPC code
     * @param filters the filters
     * @return a list of EPCIS events by EPCIS
     * @throws RemoteException
     */
    public Map<String, List<EPCISEventType>> traceEPCByEPCIS(String epc, Map<String, String> filters) throws IoTaException {
        TraceEPCByEPCISRequest in = new TraceEPCByEPCISRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        in.setFilters(createQueryParams(filters));
        TraceEPCByEPCISResponse out = port.traceEPCByEPCIS(in);
        Map<String, List<EPCISEventType>> results = new HashMap<String, List<EPCISEventType>>();
        for (EventsByEPCIS eventsByEPCIS: out.getEventsByEPCIS()) {
            List<EPCISEventType> eventList = EPCISEventTypeHelper.listFromEventList(eventsByEPCIS.getEventList());
            results.put(eventsByEPCIS.getEpcisAddress(), eventList);
        }
        return results;
    }

    public String getEPCDocURL(String epc) throws IoTaException {
        GetEPCDocURLRequest in = new GetEPCDocURLRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        GetEPCDocURLResponse out = port.getEPCDocURL(in);
        return out.getUrl();
    }

    /**
     * Gets all the EPCIS events concerning a given EPC code from a given ECPIS
     * repository.
     *
     * @param EPC the EPC code
     * @param EPCISAddress the URL of the EPCIS repository
     * @return a list of EPCIS events
     * @throws RemoteException
     */
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

    /**
     * Gets all the EPCIS events matching the given filters from a given ECPIS
     * repository.
     *
     * @param filters the EPC code
     * @param EPCISAddress the URL of the EPCIS repository
     * @return a list of EPCIS events
     * @throws RemoteException
     */
    public List<EPCISEventType> queryEPCIS(Map<String, String> filters, String EPCISAddress) throws IoTaException {
        QueryEPCISRequest in = new QueryEPCISRequest();
        in.setFilters(createQueryParams(filters));
        in.setEPCISAddress(EPCISAddress);
        in.setIdentity(identity);
        QueryEPCISResponse out = port.queryEPCIS(in);
        return EPCISEventTypeHelper.listFromEventList(out.getEventList());
    }

    /**
     * Queries a given Discovery Service for all events concerning a given EPC
     * code.
     *
     * @param EPC the EPC code
     * @param DSAddress the DS URL
     * @return a list of DS events
     * @throws RemoteException
     */
    public List<DSEvent> queryDS(String epc, String DSAddress) throws IoTaException {
        QueryDSRequest in = new QueryDSRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        in.setDSAddress(DSAddress);
        QueryDSResponse out = port.queryDS(in);
        return out.getDsEventList();
    }

    /**
     * Queries a given Discovery Service for all events concerning a given EPC
     * code.
     *
     * @param EPC the EPC code
     * @param DSAddress the DS URL
     * @param serviceType the service type
     * @return a list of DS events
     * @throws RemoteException
     */
    public List<DSEvent> queryDS(String epc, String DSAddress, ONSEntryType serviceType) throws IoTaException {
        QueryDSRequest in = new QueryDSRequest();
        EPC tepc = new EPC();
        tepc.setValue(epc);
        in.setEpc(tepc);
        in.setIdentity(identity);
        in.setDSAddress(DSAddress);
        in.setServiceType(serviceType.name());
        QueryDSResponse out = port.queryDS(in);
        return out.getDsEventList();
    }

    public static void main(String args[]) throws Exception {
        String service = "traceEPC";
        String serviceURL = "https://localhost:8443/omega";
        String epcisOrDsURL = null;
        String sid = "anonymous";
        String epc = "urn:epc:id:sgtin:40000.00002.1298283877319";
        String ksFile = null;
        String ksPass = null;
        String tsFile = null;
        String tsPass = null;
        switch (args.length) {
            case 8:
            case 9:
                ksFile = args[4];
                ksPass = args[5];
                tsFile = args[6];
                tsPass = args[7];
            // fall-through
            case 4:
            case 5:
                service = args[0];
                serviceURL = args[1];
                sid = args[2];
                epc = args[3];
                break;
            default:
                System.err.println("Usage: OmICron <Service> <Service URL> <IDENTITY> <EPC URN ID> [<Keystore File> <Keystore Password> <Truststore file> <Truststore Password>] [<EPCIS or DS URL>]");
                System.err.println();
                System.err.println("example: OmICron " + service + " " + serviceURL + " " + sid + " " + epc + " /srv/keystore.jks store_pw /srv/truststore.jks trust_pw");
                System.err.println("example: OmICron queryEPCIS " + serviceURL + " " + sid + " " + epc + " /srv/keystore.jks store_pw /srv/truststore.jks trust_pw https://localhost:8443/eta/ided_query");
                System.exit(-1);
                break;
        }
        Identity id = new Identity();
        id.setAsString(sid);

        List<EPCISEventType> eventList = null;
        List<DSEvent> dsEventList = null;
        if ("traceEPC".equals(service)) {
            System.out.println("Processing traceEPC ...");
            OmICron client = new OmICron(id, serviceURL, ksFile, ksPass, tsFile, tsPass);
            eventList = client.traceEPC(epc);
        }
        else if ("queryEPCIS".equals(service)) {
            epcisOrDsURL = (args.length == 5)? args[4] : args[8];
            System.out.println("Processing queryEPCIS ...");
            OmICron client = new OmICron(id, serviceURL, ksFile, ksPass, tsFile, tsPass);
            eventList = client.queryEPCIS(epc, epcisOrDsURL);
        }
        else if ("queryDS".equals(service)) {
            epcisOrDsURL = (args.length == 5)? args[4] : args[8];
            System.out.println("Processing queryDS ...");
            OmICron client = new OmICron(id, serviceURL, ksFile, ksPass, tsFile, tsPass);
            dsEventList = client.queryDS(epc, epcisOrDsURL);
        }
        else {
            System.out.println("Service:");
            System.out.println("traceEPC:   gets all events concerning an EPC code");
            System.out.println("queryEPCIS: gets events concerning an EPC code contained by an EPCIS");
            System.exit(-1);
        }
        if (eventList != null && !eventList.isEmpty()) {
            for (EPCISEventType evt : eventList) {
                EPCISEventTypeHelper e = new EPCISEventTypeHelper(evt);
                System.out.println("  Event found:");
                System.out.println(e.toString());
            }
        }
        else if (dsEventList != null && !dsEventList.isEmpty()) {
            for (DSEvent event : dsEventList) {
                System.out.println("Event found: ");
                System.out.println("   | EPC: " + event.getEpc());
                System.out.println("   | Event type: " + event.getEventType());
                System.out.println("   | Business Step: " + event.getBizStep());
                System.out.println("   | Event time: " + event.getEventTime().toXMLFormat());
                System.out.println("   | Service type: " + event.getServiceType());
                System.out.println("   | Service address: " + event.getServiceAddress());
            }
        }
        else {
            System.out.println("  No event found.");
        }
        System.out.println("Bye.");
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

}
