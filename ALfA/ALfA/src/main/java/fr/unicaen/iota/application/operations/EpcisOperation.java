/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.application.operations;

import fr.unicaen.iota.eta.query.ETaQueryControlClient;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;

public class EpcisOperation {

    private final String EPCIS_SERVICE_ADDRESS;
    private final Identity IDENTITY;
    private static final Log log = LogFactory.getLog(EpcisOperation.class);

    public EpcisOperation(Identity identity, String epcis_service_address) {
        IDENTITY = identity;
        EPCIS_SERVICE_ADDRESS = epcis_service_address;
    }

    public QueryParam createEPCISParameter(String name, String value) {
        QueryParam queryParam = new QueryParam();
        queryParam.setName(name);
        ArrayOfString queryParamValue = new ArrayOfString();
        queryParamValue.getString().add(value);
        queryParam.setValue(queryParamValue);
        return queryParam;
    }

    public List<EPCISEventType> getFilteredEvent(Map<String, String> filters) throws RemoteException {
        log.trace("getFilteredEvent");
        try {
            ETaQueryControlClient client = new ETaQueryControlClient(IDENTITY, EPCIS_SERVICE_ADDRESS);
            QueryParams queryParams = new QueryParams();
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                queryParams.getParam().add(createEPCISParameter(entry.getKey(), entry.getValue()));
            }

            Poll poll = new Poll();
            poll.setQueryName("SimpleEventQuery");
            poll.setParams(queryParams);

            QueryResults results = client.poll(poll);
            EventListType eventListType = results.getResultsBody().getEventList();
            return getEvents(eventListType);
        } catch (Exception ex) {
            log.error(null, ex);
            throw new RemoteException(ex.getMessage());
        }
    }

    public List<EPCISEventType> getEventFromEPC(String EPC) throws RemoteException {
        log.trace("getEventFromEPC: " + EPC);
        List<EPCISEventType> res = new ArrayList<EPCISEventType>();
        res.addAll(getObjectEventFromEPC(EPC, new HashMap<String, String>()));
        res.addAll(getAggregationEventFromEPC(EPC, new HashMap<String, String>()));
        res.addAll(getQuantityEventFromEPC(EPC, new HashMap<String, String>()));
        res.addAll(getTransactionEventFromEPC(EPC, new HashMap<String, String>()));
        return res;
    }

    public List<EPCISEventType> getObjectEventFromEPC(String EPC, Map<String, String> filters) throws RemoteException {
        log.trace("getObjectEventFromEPC: " + EPC);
        Map<String, String> allFilters = new HashMap<String, String>(filters);
        allFilters.put("MATCH_epc", EPC);
        allFilters.put("eventType", "ObjectEvent");
        return getFilteredEvent(allFilters);
    }

    public List<EPCISEventType> getTransactionEventFromEPC(String EPC, Map<String, String> filters) throws RemoteException {
        log.trace("getObjectEventFromEPC: " + EPC);
        Map<String, String> allFilters = new HashMap<String, String>(filters);
        allFilters.put("MATCH_parentID", EPC);
        allFilters.put("eventType", "TransactionEvent");
        return getFilteredEvent(allFilters);
    }

    public List<EPCISEventType> getQuantityEventFromEPC(String EPC, Map<String, String> filters) throws RemoteException {
        log.trace("getObjectEventFromEPC: " + EPC);
        Map<String, String> allFilters = new HashMap<String, String>(filters);
        allFilters.put("MATCH_epc", EPC);
        allFilters.put("eventType", "QuantityEvent");
        return getFilteredEvent(allFilters);
    }

    public List<EPCISEventType> getAggregationEventFromEPC(String EPC, Map<String, String> filters) throws RemoteException {
        log.trace("getAggregationEventFromEPC: " + EPC);
        Map<String, String> allFilters = new HashMap<String, String>(filters);
        allFilters.put("MATCH_parentID", EPC);
        allFilters.put("eventType", "AggregationEvent");
        return getFilteredEvent(allFilters);
    }

    private List<EPCISEventType> getEvents(EventListType eventListType) {
        List<EPCISEventType> res = new ArrayList<EPCISEventType>();
        if (eventListType.getObjectEventOrAggregationEventOrQuantityEvent() != null) {
            for (Object o : eventListType.getObjectEventOrAggregationEventOrQuantityEvent()) {
                EPCISEventType eltc = (EPCISEventType) (o instanceof JAXBElement ? ((JAXBElement) o).getValue() : o);
                res.add(eltc);
            }
        }
        return res;
    }
}
