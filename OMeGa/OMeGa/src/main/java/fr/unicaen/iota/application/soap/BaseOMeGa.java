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
package fr.unicaen.iota.application.soap;

import fr.unicaen.iota.application.AccessInterface;
import fr.unicaen.iota.application.model.*;
import fr.unicaen.iota.application.soap.client.IoTaFault;
import fr.unicaen.iota.ds.model.TEventItem;
import fr.unicaen.iota.ds.model.TEventItemList;
import fr.unicaen.iota.nu.ONSEntryType;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.EventListType;
import org.fosstrak.epcis.model.QueryParam;

/**
 *
 */
public abstract class BaseOMeGa implements IoTaServicePortType {

    private static final Log log = LogFactory.getLog(BaseOMeGa.class);

    public BaseOMeGa() {
    }

    protected abstract AccessInterface getAI();

    private Map<String, String> filters(List<QueryParam> qps) {
        Map<String, String> filters = new HashMap<String, String>();
        for (QueryParam qp : qps) {
            filters.put(qp.getName(), qp.getValue().toString());
        }
        return filters;
    }

    @Override
    public QueryEPCISResponse queryEPCIS(QueryEPCISRequest queryEPCISRequest) throws IoTaException {
        AccessInterface controler = getAI();
        List<EPCISEventType> list;
        try {
            EPC epc = queryEPCISRequest.getEpc();
            if (epc == null) {
                list = controler.queryEPCIS(queryEPCISRequest.getIdentity(), filters(queryEPCISRequest.getFilters().getParam()), queryEPCISRequest.getEPCISAddress());
            } else {
                list = controler.queryEPCIS(queryEPCISRequest.getIdentity(), queryEPCISRequest.getEpc().getValue(), queryEPCISRequest.getEPCISAddress());
            }
        } catch (RemoteException ex) {
            log.warn("A problem occurred while executing queryEPCIS", ex);
            throw new IoTaException("Error while queryEPCIS", IoTaFault.epcis.getCode(), ex);
        }
        QueryEPCISResponse response = new QueryEPCISResponse();
        EventListType evList = new EventListType();
        evList.getObjectEventOrAggregationEventOrQuantityEvent().addAll(list);
        response.setEventList(evList);
        return response;
    }

    @Override
    public QueryDSResponse queryDS(QueryDSRequest queryDSRequest) throws IoTaException {
        AccessInterface controler = getAI();
        List<TEventItem> list;
        try {
            if (queryDSRequest.getServiceType() == null) {
                list = controler.queryDS(queryDSRequest.getIdentity(), queryDSRequest.getEpc().getValue(), queryDSRequest.getDSAddress());
            } else {
                list = controler.queryDS(queryDSRequest.getIdentity(), queryDSRequest.getEpc().getValue(), queryDSRequest.getDSAddress(), queryDSRequest.getServiceType());
            }
        } catch (RemoteException ex) {
            log.warn("A problem occurred while executing queryDS", ex);
            throw new IoTaException("Error while queryDS", IoTaFault.ds.getCode(), ex);
        }
        TEventItemList eventList = new TEventItemList();
        eventList.getEvent().addAll(list);
        QueryDSResponse response = new QueryDSResponse();
        response.setEventList(eventList);
        return response;
    }

    @Override
    public TraceEPCResponse traceEPC(TraceEPCRequest traceEPCRequest) throws IoTaException {
        AccessInterface controler = getAI();
        List<EPCISEventType> list;
        try {
            if (traceEPCRequest.getFilters() == null) {
                list = controler.traceEPC(traceEPCRequest.getIdentity(), traceEPCRequest.getEpc().getValue());
            } else {
                list = controler.traceEPC(traceEPCRequest.getIdentity(), traceEPCRequest.getEpc().getValue(), filters(traceEPCRequest.getFilters().getParam()));
            }
        } catch (RemoteException ex) {
            log.warn("A problem occurred while executing traceEPC!", ex);
            throw new IoTaException("Error while traceEPC", IoTaFault.alfa.getCode(), ex);
        }
        TraceEPCResponse response = new TraceEPCResponse();
        EventListType evtList = new EventListType();
        evtList.getObjectEventOrAggregationEventOrQuantityEvent().addAll(list);
        response.setEventList(evtList);
        return response;
    }

    @Override
    public GetReferentDSResponse getReferentDS(GetReferentDSRequest getReferentDSRequest) throws IoTaException {
        AccessInterface controler = getAI();
        String referentDSAddress;
        try {
            referentDSAddress = controler.getReferentDS(getReferentDSRequest.getEpc().getValue());
        } catch (RemoteException ex) {
            log.warn("A problem occurred while executing getReferentDS", ex);
            throw new IoTaException("Error while getReferentDS", IoTaFault.ons.getCode(), ex);
        }
        GetReferentDSResponse response = new GetReferentDSResponse();
        response.setUrl(referentDSAddress);
        return response;
    }

    @Override
    public QueryONSResponse queryONS(QueryONSRequest queryONSRequest) throws IoTaException {
        AccessInterface controler = getAI();
        QueryONSResponse response = new QueryONSResponse();
        Map<ONSEntryType, String> map;
        try {
            map = controler.queryONS(queryONSRequest.getEpc().getValue());
        } catch (RemoteException ex) {
            log.warn("A problem occurred while executing queryONS", ex);
            throw new IoTaException("Error while queryONS", IoTaFault.ons.getCode(), ex);
        }
        for (Map.Entry<ONSEntryType, String> oe : map.entrySet()) {
            OnsEntry entry = new OnsEntry();
            entry.setKey(oe.getKey().toString());
            entry.setValue(oe.getValue());
            response.getOnsMap().add(entry);
        }
        return response;
    }

    @Override
    public GetEPCDocURLResponse getEPCDocURL(GetEPCDocURLRequest getEPCDocURLRequest) throws IoTaException {
        AccessInterface controler = getAI();
        GetEPCDocURLResponse response = new GetEPCDocURLResponse();
        String url;
        try {
            url = controler.getEPCDocURL(getEPCDocURLRequest.getEpc().getValue());
        } catch (RemoteException ex) {
            log.warn("A problem occured while executing getEPCDocURL", ex);
            throw new IoTaException("Error while getEPCDocURL", IoTaFault.ons.getCode(), ex);
        }
        response.setUrl(url);
        return response;
    }
}
