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
package fr.unicaen.iota.application.operations;

import fr.unicaen.iota.application.conf.Constants;
import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.util.EpcisUtil;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 *
 */
public class TraceEPC {

    private final String DS_LOGIN;
    private final String DS_PASS;
    private final ONSOperation onsOperation;
    private List<EPCISEventType> eventList;
    private static final Log log = LogFactory.getLog(TraceEPC.class);

    public TraceEPC(String login, String pass) {
        this.DS_LOGIN = login;
        this.DS_PASS = pass;
        this.onsOperation = new ONSOperation(Constants.ONS_HOSTS);
    }

    public List<EPCISEvent> traceEPC(String epc) throws RemoteException {
        log.trace("EPC = " + epc);
        eventList = new ArrayList<EPCISEventType>();
        return traceEPCaux(epc);
    }

    private List<EPCISEvent> traceEPCaux(String epc) throws RemoteException {
        log.trace("[TRACE EPC]: " + epc);
        log.trace("CALLED METHOD: <traceEPC>");
        log.trace("Get Referent ds address");
        String dsAddress = onsOperation.getReferentDS(epc);
        if (dsAddress == null) {
            log.warn("Unable to retreive referent ds address for this epc code");
            return new ArrayList<EPCISEvent>();
        } else {
            log.trace("referent ds address found: " + dsAddress);
        }
        log.trace("Start discover");
        DiscoveryOperation dsOp = new DiscoveryOperation(DS_LOGIN, DS_PASS, dsAddress);
        traceEPC(epc, dsOp);
        List<EPCISEvent> tab = processEventList(eventList);
        return tab;
    }

    private void traceEPC(String epc, DiscoveryOperation dsOp) throws RemoteException {
        for (String EPCIS_SERVICE_ADDRESS : dsOp.discover(epc)) {
            EpcisOperation epcisOperation = null;
            while (epcisOperation == null) {
                try {
                    epcisOperation = new EpcisOperation(EPCIS_SERVICE_ADDRESS);
                } catch (Exception e) {
                    epcisOperation = null;
                    log.warn("Unable to create service proxy port! [RETRYING]");
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
            }
            Collection<EPCISEventType> list = epcisOperation.getObjectEventFromEPC(epc);
            eventList.addAll(list);
            log.trace("nb epc events: " + list.size());
            Collection<EPCISEventType> childs = epcisOperation.getAggregationEventFromEPC(epc);
            log.trace("nb child events: " + childs.size());
            eventList.addAll(childs);
            if (childs != null) {
                for (EPCISEventType o : childs) {
                    AggregationEventType event = (AggregationEventType) o;
                    for (EPC epc2 : event.getChildEPCs().getEpc()) {
                        log.trace("new traceEPC: " + epc2.getValue());
                        traceEPCaux(epc2.getValue());
                    }
                }
            }
        }
    }

    private List<EPCISEvent> processEventList(Collection<EPCISEventType> eventList) {
        List<EPCISEvent> result = new ArrayList<EPCISEvent>();

        for (EPCISEventType o : eventList) {
            result.add(EpcisUtil.processEvent(o));
        }
        return result;
    }
}
