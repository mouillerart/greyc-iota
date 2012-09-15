/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.application.soap.server;

import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.model.ONSEntryType;
import fr.unicaen.iota.application.rmi.AccessInterface;
import fr.unicaen.iota.application.soap.model.*;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class IOTA_Service implements IOTA_ServiceSkeletonInterface {

    private static final Log log = LogFactory.getLog(IOTA_Service.class);
    private AccessInterface rmiServer;

    /*
     * Helper functions
     */
    private EpcList createEpcListFromStringList(Collection<String> param) {
        if (param == null || param.isEmpty()) {
            return new EpcList();
        }
        EpcList localEpcList = new EpcList();
        for (String epc : param) {
            localEpcList.addEpcs(epc);
        }
        return localEpcList;
    }

    public ChildList createChildListFromStringList(Collection<String> param) {
        if (param == null || param.isEmpty()) {
            return new ChildList();
        }
        ChildList localChildList = new ChildList();
        for (String epc : param) {
            localChildList.addChilds(epc);
        }
        return localChildList;
    }

    private EventType getEventType(EPCISEvent.EventType param) {
        switch (param) {
            case OBJECT:
                return EventType.OBJECT;
            case AGGREGATION:
                return EventType.AGGREGATION;
            case TRANSACTION:
                return EventType.TRANSACTION;
            case QUANTITY:
                return EventType.QUANTITY;
        }
        return null;
    }

    private EventAction getEventAction(EPCISEvent.ActionType param) {
        switch (param) {
            case ADD:
                return EventAction.ADD;
            case DELETE:
                return EventAction.DELETE;
            case OBSERVE:
                return EventAction.OBSERVE;
        }
        return null;
    }

    /**
     * @param queryEPCISRequest
     */
    @Override
    public fr.unicaen.iota.application.soap.model.QueryEPCISResponse queryEPCIS(
            fr.unicaen.iota.application.soap.model.QueryEPCISRequest queryEPCISRequest) {
        initServer();
        List<EPCISEvent> list;
        try {
            list = rmiServer.queryEPCIS(queryEPCISRequest.getQueryEPCISRequest().getEpc(), queryEPCISRequest.getQueryEPCISRequest().getEPCISAddress());
        } catch (RemoteException ex) {
            throw new UnsupportedOperationException("A problem occurred while executing queryEPCIS on RMI interface!", ex);
        }
        QueryEPCISResponse response = new QueryEPCISResponse();
        QueryEPCISResponseOut out = new QueryEPCISResponseOut();
        response.setQueryEPCISResponse(out);
        EventList evtList = new EventList();
        for (EPCISEvent evt : list) {
            Event event = new Event();
            event.setType(getEventType(evt.getType()));
            event.setAction(getEventAction(evt.getAction()));
            event.setBizLoc(evt.getBizLoc());
            event.setBizStep(evt.getBizLoc());
            event.setChildList(createChildListFromStringList(evt.getChildren()));
            event.setDisposition(evt.getDisposition());
            event.setEpcClass(evt.getEPCClass());
            event.setEpcList(createEpcListFromStringList(evt.getEpcs()));
            event.setEventTime(evt.getEventTime());
            event.setParentId(evt.getParentID());
            event.setQuantity(evt.getQuantity() != null ? Integer.parseInt(evt.getQuantity()) : null);
            event.setReadPoint(evt.getReadPoint());
            event.setRecordTime(evt.getEventTime());
        }
        out.setEventList(evtList);
        return response;
    }

    /**
     * @param helloRequest
     */
    @Override
    public fr.unicaen.iota.application.soap.model.HelloResponse hello(
            fr.unicaen.iota.application.soap.model.HelloRequest helloRequest) {
        initServer();
        HelloResponse helloResponse = new HelloResponse();
        HelloResponseOut helloResponseOut = new HelloResponseOut();
        helloResponseOut.setHello("IoTa APPLICATION LEVEL INTERFACE 1.0\nstarted");
        helloResponse.setHelloResponse(helloResponseOut);
        return helloResponse;
    }

    /**
     * @param queryDSRequest
     */
    @Override
    public fr.unicaen.iota.application.soap.model.QueryDSResponse queryDS(
            fr.unicaen.iota.application.soap.model.QueryDSRequest queryDSRequest) {
        initServer();
        List<DSEvent> list;
        try {
            if (queryDSRequest.getQueryDSRequest().getServiceType() == null) {
                list = rmiServer.queryDS(queryDSRequest.getQueryDSRequest().getEpc(), queryDSRequest.getQueryDSRequest().getDSAddress(), queryDSRequest.getQueryDSRequest().getLogin(), queryDSRequest.getQueryDSRequest().getPassword());
            } else {
                list = rmiServer.queryDS(queryDSRequest.getQueryDSRequest().getEpc(), queryDSRequest.getQueryDSRequest().getDSAddress(), queryDSRequest.getQueryDSRequest().getLogin(), queryDSRequest.getQueryDSRequest().getPassword(), queryDSRequest.getQueryDSRequest().getServiceType());
            }
        } catch (RemoteException ex) {
            throw new UnsupportedOperationException("A problem occurred while executing queryDS on RMI interface!", ex);
        }
        QueryDSResponse response = new QueryDSResponse();
        QueryDSResponseOut out = new QueryDSResponseOut();
        response.setQueryDSResponse(out);
        DsEventList eventList = new DsEventList();
        out.setEventList(eventList);
        for (DSEvent evt : list) {
            DsEvent event = new DsEvent();
            event.setEpc(evt.getEPC());
            event.setReferenceAddress(evt.getReferenceAddress());
            event.setBizStep(evt.getBizStep());
            Calendar cal = new GregorianCalendar();
            cal.setTime(evt.getEventTime());
            event.setEventTime(cal);
            eventList.addEvent(event);
        }
        return response;

    }

    /**
     * @param traceEPCRequest
     */
    @Override
    public fr.unicaen.iota.application.soap.model.TraceEPCResponse traceEPC(
            fr.unicaen.iota.application.soap.model.TraceEPCRequest traceEPCRequest) {
        initServer();
        List<EPCISEvent> list;
        try {
            list = rmiServer.traceEPC(traceEPCRequest.getTraceEPCRequest().getEpc());
        } catch (RemoteException ex) {
            throw new UnsupportedOperationException("A problem occurred while executing traceEPC on RMI interface!", ex);
        }
        TraceEPCResponse response = new TraceEPCResponse();
        TraceEPCResponseOut out = new TraceEPCResponseOut();
        response.setTraceEPCResponse(out);
        EventList evtList = new EventList();

        for (EPCISEvent evt : list) {
            Event event = new Event();
            event.setType(getEventType(evt.getType()));
            event.setAction(getEventAction(evt.getAction()));
            event.setBizLoc(evt.getBizLoc());
            event.setBizStep(evt.getBizStep());
            event.setChildList(createChildListFromStringList(evt.getChildren()));
            event.setDisposition(evt.getDisposition());
            event.setEpcClass(evt.getEPCClass() != null ? evt.getEPCClass() : " ");
            event.setEpcList(createEpcListFromStringList(evt.getEpcs()));
            event.setEventTime(evt.getEventTime());
            event.setParentId(evt.getParentID() != null ? evt.getParentID() : " ");
            try {
                Integer.parseInt(evt.getQuantity());
                event.setQuantity(Integer.parseInt(evt.getQuantity()));
            } catch (NumberFormatException e) {
                event.setQuantity(0);
            }
            event.setReadPoint(evt.getReadPoint());
            event.setRecordTime(evt.getInsertedTime());
            evtList.addEvent(event);
        }
        out.setEventList(evtList);
        return response;
    }

    /**
     * @param getReferenteDSRequest
     */
    @Override
    public fr.unicaen.iota.application.soap.model.GetReferenteDSResponse getReferenteDS(
            fr.unicaen.iota.application.soap.model.GetReferenteDSRequest getReferenteDSRequest) {
        initServer();
        String referentDSAddress = null;
        try {
            referentDSAddress = rmiServer.getReferenteDS(getReferenteDSRequest.getGetReferenteDSRequest().getEpc());
        } catch (RemoteException ex) {
            throw new UnsupportedOperationException("A problem occurred while executing getReferentDS on RMI interface!", ex);
        }
        GetReferenteDSResponse response = new GetReferenteDSResponse();
        GetReferenteDSResponseOut out = new GetReferenteDSResponseOut();
        out.set_return(referentDSAddress);
        return response;
    }

    /**
     * @param queryONSRequest
     */
    @Override
    public fr.unicaen.iota.application.soap.model.QueryONSResponse queryONS(
            fr.unicaen.iota.application.soap.model.QueryONSRequest queryONSRequest) {
        initServer();
        QueryONSResponse response = new QueryONSResponse();
        QueryONSResponseOut out = new QueryONSResponseOut();
        response.setQueryONSResponse(out);
        Map<ONSEntryType, String> map;
        try {
            map = rmiServer.queryONS(queryONSRequest.getQueryONSRequest().getEpc());
        } catch (RemoteException ex) {
            throw new UnsupportedOperationException("A problem occurred while executing queryONS on RMI interface", ex);
        }
        for (Map.Entry<ONSEntryType, String> oe : map.entrySet()) {
            OnsEntry entry = new OnsEntry();
            entry.setKey(oe.getKey().toString());
            entry.setValue(oe.getValue());
            out.addOnsMap(entry);
        }
        return response;
    }

    /**
     * @param getProductInfoRequest
     */
    @Override
    public fr.unicaen.iota.application.soap.model.GetProductInfoResponse getProductInfo(
            fr.unicaen.iota.application.soap.model.GetProductInfoRequest getProductInfoRequest) {
        initServer();
        GetProductInfoResponse response = new GetProductInfoResponse();
        GetProductInfoResponseOut out = new GetProductInfoResponseOut();
        out.set_return(getProductInfoRequest.getGetProductInfoRequest().getAddress());
        response.setGetProductInfoResponse(out);
        return response;
    }

    private void initServer() {
        if (rmiServer == null) {
            try {
                log.debug("Looking up server at " + Configuration.RMI_URL);
                URI uri = new URI(Configuration.RMI_URL);
                int port = uri.getPort();
                port = port == -1 ? 1099 : port;
                String name = uri.getPath().substring(1);
                String host = uri.getHost();
                log.trace("that is: rmi://" + host + ":" + port + "/" + name);
                Registry registry = LocateRegistry.getRegistry(host, port);
                rmiServer = (AccessInterface) registry.lookup(name);
            } catch (Exception e) {
                log.error("Failed to setup for RMI", e);
                throw new UnsupportedOperationException("Unable to access RMI interface", e);
            }
        }
    }
}
