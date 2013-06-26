/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.ds.client;

import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.ds.model.EventCreateReq;
import fr.unicaen.iota.ds.model.EventCreateResp;
import fr.unicaen.iota.ds.model.EventLookupReq;
import fr.unicaen.iota.ds.model.EventLookupResp;
import fr.unicaen.iota.ds.model.MultipleEventCreateReq;
import fr.unicaen.iota.ds.model.MultipleEventCreateResp;
import fr.unicaen.iota.ds.soap.DSService;
import fr.unicaen.iota.ds.soap.DSServicePortType;
import fr.unicaen.iota.ds.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ds.soap.InternalExceptionResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;


public class DSClient {

    private static final Log log = LogFactory.getLog(DSClient.class);

    private DSServicePortType port;

    public DSClient(String address) {
        try {
            this.configureService(address);
        } catch (MalformedURLException ex) {
            log.error(ex.getMessage(), ex);
        } catch (Exception e) {
            throw new RuntimeException("Can’t configure service: " + e.getMessage(), e);
        }
    }

    public void configureService(String address) throws MalformedURLException, Exception {
        URL wsdlUrl = new URL(address + "?wsdl");
        DSService service = new DSService(wsdlUrl);
        port = service.getPort(DSServicePortType.class);

        // turn off chunked transfer encoding
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpConduit.setClient(httpClientPolicy);
    }

    /**
     * Sends the given DS event to the DS.
     * @param epc The EPC of the event.
     * @param eventType The type of the event.
     * @param bizStep The business step of the event.
     * @param eventTime The creation time of the event.
     * @param serviceAddress The service address of the event.
     * @param serviceType The type of the service.
     * @return The response.
     * @throws ImplementationExceptionResponse If this event could not be captured.
     * @throws InternalExceptionResponse If a DS error occurred.
     */
    public EventCreateResp eventCreate(String epc, String eventType, String bizStep, XMLGregorianCalendar eventTime,
            String serviceAddress, String serviceType) throws ImplementationExceptionResponse, InternalExceptionResponse{
        EventCreateReq eventCreate = new EventCreateReq();
        DSEvent event = new DSEvent();
        event.setEpc(epc);
        event.setEventType(eventType);
        event.setBizStep(bizStep);
        event.setEventTime(eventTime);
        event.setServiceAddress(serviceAddress);
        event.setServiceType(serviceType);
        eventCreate.setDsEvent(event);
        return port.eventCreate(eventCreate);
    }

    /**
     * Sends the given DS event to the DS.
     * @param dsEvent The DS event to send.
     * @return The response.
     * @throws ImplementationExceptionResponse If this event could not be captured.
     * @throws InternalExceptionResponse If a DS error occurred.
     */
    public EventCreateResp eventCreate(DSEvent dsEvent) throws ImplementationExceptionResponse, InternalExceptionResponse{
        EventCreateReq eventCreate = new EventCreateReq();
        eventCreate.setDsEvent(dsEvent);
        return port.eventCreate(eventCreate);
    }

    /**
     * Sends the given list of DS events to the DS.
     * @param dsEventList The list of DS events to send.
     * @return The responses.
     * @throws ImplementationExceptionResponse If these events could not be captured.
     * @throws InternalExceptionResponse If a DS error occurred.
     */
    public MultipleEventCreateResp multipleEventCreate(List<DSEvent> dsEventList) throws ImplementationExceptionResponse, InternalExceptionResponse{
        MultipleEventCreateReq mEventCreateReq = new MultipleEventCreateReq();
        mEventCreateReq.getDsEvent().addAll(dsEventList);
        return port.multipleEventCreate(mEventCreateReq);
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
     */
    public EventLookupResp eventLookup(String epc, String eventType, String bizStep,
            XMLGregorianCalendar startingAt, XMLGregorianCalendar endingAt, String serviceType)
            throws ImplementationExceptionResponse, InternalExceptionResponse{
        EventLookupReq eventLookupReq = new EventLookupReq();
        eventLookupReq.setEpc(epc);
        eventLookupReq.setEventType(eventType);
        eventLookupReq.setBizStep(bizStep);
        eventLookupReq.setStartingAt(startingAt);
        eventLookupReq.setEndingAt(endingAt);
        eventLookupReq.setServiceType(serviceType);
        return port.eventLookup(eventLookupReq);
    }

}
