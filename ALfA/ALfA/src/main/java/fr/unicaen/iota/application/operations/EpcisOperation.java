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

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.queryclient.QueryControlClient;

public class EpcisOperation {

    private final String EPCIS_SERVICE_ADDRESS;
    private static final Log log = LogFactory.getLog(EpcisOperation.class);

    public EpcisOperation(String epcis_service_address) throws Exception {
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

    private List<EPCISEventType> getFilteredEvent(Map<String, String> filters) throws RemoteException {
        log.trace("getFilteredEvent");
        try {
            QueryControlClient client = new QueryControlClient();
            client.configureService(new URL(EPCIS_SERVICE_ADDRESS), null);
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
        Map<String, String> map = new HashMap<String, String>();
        map.put("MATCH_epc", EPC);
        return getFilteredEvent(map);
    }

    public List<EPCISEventType> getObjectEventFromEPC(String EPC) throws RemoteException {
        log.trace("getObjectEventFromEPC: " + EPC);
        Map<String, String> map = new HashMap<String, String>();
        map.put("MATCH_epc", EPC);
        map.put("eventType", "ObjectEvent");
        return getFilteredEvent(map);
    }

    public List<EPCISEventType> getAggregationEventFromEPC(String EPC) throws RemoteException {
        log.trace("getAggregationEventFromEPC: " + EPC);
        Map<String, String> map = new HashMap<String, String>();
        map.put("MATCH_parentID", EPC);
        map.put("eventType", "AggregationEvent");
        return getFilteredEvent(map);
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
