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

import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.rmi.CallBackClient;
import fr.unicaen.iota.application.util.EpcisUtil;
import java.rmi.RemoteException;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISEventType;

public class EpcisRequest extends Thread {

    private final String serviceAddress;
    private final String epc;
    private final String LOGIN;
    private final String PASS;
    private final CallBackClient client;
    private final String sessionId;
    private static final boolean isDebug = true; // TODO: hard value
    private static final Log log = LogFactory.getLog(EpcisRequest.class);

    public EpcisRequest(String serviceAddress, String epc, String LOGIN,
            String PASS, CallBackClient client, String sessionId) {
        this.LOGIN = LOGIN;
        this.PASS = PASS;
        this.epc = epc;
        this.client = client;
        this.sessionId = sessionId;
        this.serviceAddress = serviceAddress;
    }

    @Override
    public void run() {
        try {
            StringBuilder debug = new StringBuilder();
            if (isDebug) {
                debug.append("\n\n");
                debug.append("EPCIS request -> service address: ");
                debug.append(serviceAddress);
                debug.append(" | epc: ");
                debug.append(epc);
                debug.append("\n");
                debug.append("Events: \n");
            }
            EpcisOperation epcisOperation = null;
            while (epcisOperation == null) {
                try {
                    epcisOperation = new EpcisOperation(serviceAddress);
                } catch (Exception e) {
                    epcisOperation = null;
                    log.warn("Unable to create service proxy port. Will retry ...");
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
            }
            for (EPCISEventType o : epcisOperation.getObjectEventFromEPC(epc)) {
                try {
                    EPCISEvent e = EpcisUtil.processEvent(o);
                    if (isDebug) {
                        debug.append("------------------------------------------\n");
                        debug.append(e.toString());
                        debug.append("\n------------------------------------------\n");
                    }
                    client.pushEvent(sessionId, e);
                } catch (RemoteException ex) {
                    log.fatal(null, ex);
                }
            }
            if (isDebug) {
                debug.append("Aggregation: \n");
            }
            Collection<EPCISEventType> aggregationEvents = epcisOperation.getAggregationEventFromEPC(epc);
            for (EPCISEventType o : aggregationEvents) {
                try {
                    EPCISEvent e = EpcisUtil.processEvent(o);
                    if (isDebug) {
                        debug.append("------------------------------------------\n");
                        debug.append(e.toString());
                        debug.append("\n------------------------------------------\n");
                    }
                    client.pushEvent(sessionId, e);
                } catch (RemoteException ex) {
                    log.fatal(null, ex);
                }
            }
            if (isDebug) {
                debug.append("\n\n");
                log.debug(debug);
            }
            if (aggregationEvents != null) {
                for (EPCISEventType o : aggregationEvents) {
                    AggregationEventType event = (AggregationEventType) o;
                    for (EPC epc2 : event.getChildEPCs().getEpc()) {
                        log.trace("new traceEPC: " + epc2.getValue());
                        new TraceEPCAsync(epc2.getValue(), client, LOGIN, PASS, sessionId).start();
                    }
                }
            }
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        }
    }
}
