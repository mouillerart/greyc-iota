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
package fr.unicaen.iota.dseta.client;

import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.ds.model.EventCreateResp;
import fr.unicaen.iota.ds.model.EventLookupReq;
import fr.unicaen.iota.ds.model.EventLookupResp;
import fr.unicaen.iota.ds.model.MultipleEventCreateResp;
import fr.unicaen.iota.ds.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ds.soap.InternalExceptionResponse;
import fr.unicaen.iota.dseta.model.EventCreateReq;
import fr.unicaen.iota.dseta.model.MultipleEventCreateReq;
import fr.unicaen.iota.dseta.soap.DSeTaService;
import fr.unicaen.iota.dseta.soap.DSeTaServicePortType;
import fr.unicaen.iota.dseta.soap.SecurityExceptionResponse;
import fr.unicaen.iota.tau.model.Identity;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public class DSeTaClient {

    private Identity identity;
    private DSeTaServicePortType port;
    private static final Log log = LogFactory.getLog(DSeTaClient.class);

    public DSeTaClient(Identity id, String dsAddress) {
        this(id, dsAddress, null, null, null, null);
    }

    public DSeTaClient(Identity id, String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        log.trace("new DSeTaClient: " + id + " @ " + address);
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
        DSeTaService service = new DSeTaService(wsdlUrl);
        port = service.getPort(DSeTaServicePortType.class);

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
     * Sends the given DS event to the DS.
     * @param epc The EPC of the event.
     * @param eventType The type of the event.
     * @param bizStep The business step of the event.
     * @param eventTime The creation time of the event.
     * @param serviceAddress The service address of the event.
     * @param serviceType The type of the service.
     * @param owner The owner of the event.
     * @return The response.
     * @throws ImplementationExceptionResponse If this event could not be captured.
     * @throws InternalExceptionResponse If a DSeTa error occurred.
     * @throws SecurityExceptionResponse If the operation is denied.
     */
    public EventCreateResp eventCreate(String epc, String eventType, String bizStep, String serviceAddress, XMLGregorianCalendar eventTime,
            String serviceType, String owner) throws ImplementationExceptionResponse, InternalExceptionResponse, SecurityExceptionResponse {
        EventCreateReq createReq = new EventCreateReq();
        DSEvent event = new DSEvent();
        event.setEpc(epc);
        event.setEventType(eventType);
        event.setBizStep(bizStep);
        event.setEventTime(eventTime);
        event.setServiceAddress(serviceAddress);
        event.setServiceType(serviceType);
        createReq.setDsEvent(event);
        Identity ownerId = new Identity();
        ownerId.setAsString(owner);
        createReq.setOwner(ownerId);
        return port.iDedEventCreate(createReq, identity);
    }

    /**
     * Sends the given DS event to DSeTa.
     * @param dsEvent The DS event to send.
     * @param owner The owner of the event.
     * @return The response.
     * @throws ImplementationExceptionResponse If this event could not be captured.
     * @throws InternalExceptionResponse If a DSeTa error occurred.
     * @throws SecurityExceptionResponse If the operation is denied.
     */
    public EventCreateResp eventCreate(DSEvent event, String owner)
            throws ImplementationExceptionResponse, InternalExceptionResponse, SecurityExceptionResponse {
        EventCreateReq createReq = new EventCreateReq();
        createReq.setDsEvent(event);
        Identity ownerId = new Identity();
        ownerId.setAsString(owner);
        createReq.setOwner(ownerId);
        return port.iDedEventCreate(createReq, identity);
    }

    /**
     * Sends the given list of DS events to DSeTa.
     * @param dsEventMap The Map of events with owner to add. The Map is linked to keep order in the response.
     * @return The responses or null if the Map is <code>null</code>.
     * @throws ImplementationExceptionResponse If these events could not be captured.
     * @throws InternalExceptionResponse If a DSeTa error occurred.
     * @throws SecurityExceptionResponse If the operation is denied.
     */
    public MultipleEventCreateResp multipleEventCreate(LinkedHashMap<DSEvent, String> dsetaEventMap)
            throws ImplementationExceptionResponse, InternalExceptionResponse, SecurityExceptionResponse {
        if (dsetaEventMap == null) {
            return null;
        }
        MultipleEventCreateReq multipleCreateReq = new MultipleEventCreateReq();
        for (Map.Entry<DSEvent, String> dsetaEvent : dsetaEventMap.entrySet()) {
            EventCreateReq eventCreate = new EventCreateReq();
            eventCreate.setDsEvent(dsetaEvent.getKey());
            Identity ownerId = new Identity();
            ownerId.setAsString(dsetaEvent.getValue());
            eventCreate.setOwner(ownerId);
            multipleCreateReq.getEventCreate().add(eventCreate);
        }
        return port.iDedMultipleEventCreate(multipleCreateReq, identity);
    }

    /**
     * Performs a query to get DS events.
     * @param epc The EPC to look up.
     * @param eventType The event type to look up.
     * @param bizStep The business step to look up.
     * @param startingAt The lower time limit.
     * @param endingAt The higher time limit.
     * @param serviceType The type of the service.
     * @return The list of DS events associated to the query.
     * @throws ImplementationExceptionResponse If the query could not be performed.
     * @throws InternalExceptionResponse If a DS error occurred.
     * @throws SecurityExceptionResponse If the operation is denied.
     */
    public List<DSEvent> eventLookup(String epc, String eventType, String bizStep, XMLGregorianCalendar startingAt,
            XMLGregorianCalendar endingAt, String serviceType) throws ImplementationExceptionResponse, InternalExceptionResponse,
            SecurityExceptionResponse {
        EventLookupReq lookupReq = new EventLookupReq();
        lookupReq.setEpc(epc);
        lookupReq.setEventType(eventType);
        lookupReq.setBizStep(bizStep);
        lookupReq.setStartingAt(startingAt);
        lookupReq.setEndingAt(endingAt);
        lookupReq.setServiceType(serviceType);
        EventLookupResp lookupResp = port.iDedEventLookup(lookupReq, identity);
        return lookupResp.getDsEventList();
    }

}
