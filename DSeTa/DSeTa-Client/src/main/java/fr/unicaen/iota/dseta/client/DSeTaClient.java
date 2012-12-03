/*
 *  This program is a part of the IoTa Project.
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
package fr.unicaen.iota.dseta.client;

import fr.unicaen.iota.discovery.client.model.EventInfo;
import fr.unicaen.iota.discovery.client.model.Service;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.discovery.client.util.StatusCodeHelper;
import fr.unicaen.iota.ds.model.*;
import fr.unicaen.iota.dseta.soap.IDedDSService;
import fr.unicaen.iota.dseta.soap.IDedDSServicePortType;
import fr.unicaen.iota.tau.model.Identity;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

//import org.fosstrak.epcis.utils.AuthenticationType;

/**
 *
 */
public class DSeTaClient implements X509TrustManager {

    private Identity identity;
    private IDedDSServicePortType port;
    private static final Log log = LogFactory.getLog(DSeTaClient.class);

    public DSeTaClient(Identity id, String dsAddress) {
        this(id, dsAddress, null, null);
    }
    
    public DSeTaClient(Identity id, String address, String pksFilename, String pksPassword) {
        log.trace("new DSeTaClient: " + id + " @ " + address);
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
        IDedDSService service = new IDedDSService(wsdlUrl);
        port = service.getPort(IDedDSServicePortType.class);

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
            tlscp.setTrustManagers(new TrustManager[] { this });

            httpConduit.setTlsClientParameters(tlscp);
        }
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity id) {
        this.identity = id;
    }

    public List<TEventItem> eventLookup(String objectId, GregorianCalendar start, GregorianCalendar end, String BizStep)
            throws EnhancedProtocolException, MalformedURIException {
        EventLookupIn in = new EventLookupIn();
        in.setSid("not_used"); // session ID, not used in DSeTa
        in.setObjectID(objectId);
        in.setLifeCycleStepID(BizStep);
        try {
            DatatypeFactory DF = DatatypeFactory.newInstance();
            if (start != null) {
                XMLGregorianCalendar xmlCal = DF.newXMLGregorianCalendar(start);
                in.setStartingAt(xmlCal);
            }
            if (end != null) {
                XMLGregorianCalendar xmlCal = DF.newXMLGregorianCalendar(end);
                in.setEndingAt(xmlCal);
            }
        } catch (DatatypeConfigurationException ex) {
            log.error("Impossible date conversion", ex);
        }
        EventLookupOut out = port.iDedEventLookup(in, identity);
        int statusCode = out.getResult().getCode();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        TEventItemList tEventList = out.getEventList();
        return tEventList.getEvent();
    }

    public int eventCreate(String partnerId, String objectId, String bizStep, String eventClass,
            GregorianCalendar sourceTimeStamp, int ttl, Collection<String> serviceIds, int priority, Map<String, String> extensions)
            throws MalformedURIException, EnhancedProtocolException {
        EventCreateIn in = new EventCreateIn();
        in.setSid("not_used"); // session ID, not used in DSeTa
        in.setEvent(createTObjectEventTypeChoice(objectId, bizStep, eventClass,
                sourceTimeStamp, ttl, serviceIds, priority, extensions));
        in.setSupplyChainID("not_used");
        in.setProxyPartnerID(partnerId);
        EventCreateOut out = port.iDedEventCreate(in, identity);
        int statusCode = out.getResult().getCode();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        return out.getEventID().intValue();
    }

    public List<Integer> multipleEventCreate(String partnerId, Collection<EventInfo> eventList)
            throws MalformedURIException, EnhancedProtocolException {
        MultipleEventCreateIn in = new MultipleEventCreateIn();
        in.setSid("not_used"); // session ID, not used in DSeTa
        in.setProxyPartnerID(partnerId);
        in.setSupplyChainID("not_used");
        TObjectEventList objectEventList = new TObjectEventList();
        for (EventInfo event : eventList) {
            TObjectEvent tObjectEvent = createTObjectEventTypeChoice(event.getEvent().getObjectId(),
                    event.getEvent().getBizStep(),
                    event.getEvent().getEventClass(),
                    event.getEvent().getSourceTimeStamp(),
                    event.getTtl(),
                    createServiceIds(event.getEvent().getServiceList()),
                    event.getPriority(),
                    event.getEvent().getExtensions()).getObjectEvent();
            objectEventList.getObjectEvent().add(tObjectEvent);
        }
        in.setEvents(objectEventList);
        MultipleEventCreateOut out = port.iDedMultipleEventCreate(in, identity);
        int statusCode = out.getResult().getCode();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }

        List<Integer> result = new ArrayList<Integer>();
        if (out.getEventIDList().getEventID() == null) {
            return result;
        }
        for (BigInteger tEventID : out.getEventIDList().getEventID()) {
            result.add(tEventID.intValue());
        }
        return result;
    }

    private TEventTypeChoice createTObjectEventTypeChoice(String objectId, String bizStep, String eventClass,
            Calendar sourceTimeStamp, int ttl, Collection<String> serviceIds, int priority, Map<String, String> extensions)
            throws MalformedURIException {
        TEventTypeChoice tEventTypeChoice = new TEventTypeChoice();
        TObjectEvent tObjectEvent = new TObjectEvent();

        TServiceIDList tServiceIDList = new TServiceIDList();
        tServiceIDList.getId().addAll(serviceIds);
        tObjectEvent.setServiceList(tServiceIDList);

        tObjectEvent.setTtl(BigInteger.valueOf(ttl));

        try {
            DatatypeFactory DF = DatatypeFactory.newInstance();
            GregorianCalendar gCal = new GregorianCalendar();
            gCal.setTime(sourceTimeStamp.getTime());
            XMLGregorianCalendar xmlCal = DF.newXMLGregorianCalendar(gCal);
            tObjectEvent.setSourceTS(xmlCal);
        } catch (DatatypeConfigurationException ex) {
            log.error("Impossible date conversion", ex);
        }

        tObjectEvent.setPriority(priority);
        tObjectEvent.setObjectID(objectId);
        tObjectEvent.setLifeCycleStepID(bizStep);
        tObjectEvent.setEventClass(eventClass);

        // extensions:
        if (extensions != null) {
            TExtension exts = new TExtension();
            for (Map.Entry<String, String> idval : extensions.entrySet()) {
                /*
                ESDS_ServiceStub.TExtension tExtension = new ESDS_ServiceStub.TExtension();
                OMFactory factory = OMAbstractFactory.getOMFactory();
                OMElement elemExtension = factory.createOMElement(new QName("fr:unicaen:extension"));
                OMElement key = factory.createOMElement(new QName("fr:unicaen:key"));
                key.setText(idval.getKey());
                OMElement value = factory.createOMElement(new QName("fr:unicaen:value"));
                value.setText(idval.getValue());
                elemExtension.addChild(key);
                elemExtension.addChild(value);
                tExtension.addExtraElement(elemExtension);
                */
            }
            tObjectEvent.setExtension(exts);
        }
        tEventTypeChoice.setObjectEvent(tObjectEvent);
        return tEventTypeChoice;
    }

    private List<String> createServiceIds(Collection<Service> serviceList) {
        List<String> serviceIds = new ArrayList<String>();
        for (Service s : serviceList) {
            serviceIds.add(s.getId());
        }
        return serviceIds;
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
}
