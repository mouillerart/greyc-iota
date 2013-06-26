/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
import fr.unicaen.iota.application.Configuration;
import fr.unicaen.iota.application.model.*;
import fr.unicaen.iota.application.soap.client.IoTaFault;
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.tau.model.Identity;
import fr.unicaen.iota.xi.client.EPCISPEP;
import fr.unicaen.iota.xi.utils.Utils;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
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
    @Resource
    private WebServiceContext wsContext;
    private final EPCISPEP xiclient;
    private final Identity anonymous;

    public BaseOMeGa() {
        anonymous = new Identity();
        anonymous.setAsString(Configuration.DEFAULT_IDENTITY);
        xiclient = new EPCISPEP(Configuration.XI_URL, Configuration.PKS_FILENAME, Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
    }

    protected abstract AccessInterface getAI();

    private void checkAuth(Identity id) throws IoTaException {
        Principal authId = wsContext.getUserPrincipal();
        if (authId == null && id == anonymous) {
            return;
        }
        if (authId == null) {
            throw new IoTaException("No authentication", IoTaFault.tau.getCode());
        }
        if (id == null || id.getAsString().isEmpty()) {
            throw new IoTaException("No identity to use", IoTaFault.tau.getCode());
        }
        String tlsId = fr.unicaen.iota.mu.Utils.formatId(authId.getName());
        String idToPass = fr.unicaen.iota.mu.Utils.formatId(id.getAsString());
        if (!tlsId.equals(idToPass)) {
            int chk = xiclient.canBe(tlsId, idToPass);
            if (!Utils.responseIsPermit(chk)) {
                throw new IoTaException(tlsId + " isn't allowed to pass as " + id.getAsString(), IoTaFault.tau.getCode());
            }
        }
    }

    private Map<String, String> filters(List<QueryParam> qps) {
        Map<String, String> filters = new HashMap<String, String>();
        for (QueryParam qp : qps) {
            filters.put(qp.getName(), qp.getValue().toString());
        }
        return filters;
    }

    @Override
    public QueryEPCISResponse queryEPCIS(QueryEPCISRequest queryEPCISRequest) throws IoTaException {
        checkAuth(queryEPCISRequest.getIdentity());
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
        checkAuth(queryDSRequest.getIdentity());
        AccessInterface controler = getAI();
        List<DSEvent> list;
        try {
            if (queryDSRequest.getServiceType() == null) {
                list = controler.queryDS(queryDSRequest.getIdentity(), queryDSRequest.getEpc().getValue(), queryDSRequest.getDSAddress());
            } else {
                ONSEntryType entryType = Enum.valueOf(ONSEntryType.class, queryDSRequest.getServiceType());
                list = controler.queryDS(queryDSRequest.getIdentity(), queryDSRequest.getEpc().getValue(), queryDSRequest.getDSAddress(), entryType);
            }
        } catch (RemoteException ex) {
            log.warn("A problem occurred while executing queryDS", ex);
            throw new IoTaException("Error while queryDS", IoTaFault.ds.getCode(), ex);
        }
        QueryDSResponse response = new QueryDSResponse();
        List<DSEvent> responseEvents = response.getDsEventList();
        responseEvents.addAll(list);
        return response;
    }

    @Override
    public TraceEPCResponse traceEPC(TraceEPCRequest traceEPCRequest) throws IoTaException {
        checkAuth(traceEPCRequest.getIdentity());
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
    public TraceEPCByEPCISResponse traceEPCByEPCIS(TraceEPCByEPCISRequest traceEPCByEPCISRequest) throws IoTaException {
        checkAuth(traceEPCByEPCISRequest.getIdentity());
        AccessInterface controler = getAI();
        Map<String, List<EPCISEventType>> map;
        try {
            if (traceEPCByEPCISRequest.getFilters() == null) {
                map = controler.traceEPCByEPCIS(traceEPCByEPCISRequest.getIdentity(), traceEPCByEPCISRequest.getEpc().getValue());
            } else {
                map = controler.traceEPCByEPCIS(traceEPCByEPCISRequest.getIdentity(), traceEPCByEPCISRequest.getEpc().getValue(), filters(traceEPCByEPCISRequest.getFilters().getParam()));
            }
        } catch (RemoteException ex) {
            log.warn("A problem occurred while executing traceEPCByEPCIS!", ex);
            throw new IoTaException("Error while traceEPCByEPCIS", IoTaFault.alfa.getCode(), ex);
        }
        TraceEPCByEPCISResponse response = new TraceEPCByEPCISResponse();
        for (Entry<String, List<EPCISEventType>> entry : map.entrySet()) {
            EventsByEPCIS eventsByEPCIS = new EventsByEPCIS();
            eventsByEPCIS.setEpcisAddress(entry.getKey());
            EventListType evtList = new EventListType();
            evtList.getObjectEventOrAggregationEventOrQuantityEvent().addAll(entry.getValue());
            eventsByEPCIS.setEventList(evtList);
            response.getEventsByEPCIS().add(eventsByEPCIS);
        }
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
