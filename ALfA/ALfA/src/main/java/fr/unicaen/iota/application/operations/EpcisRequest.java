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

import fr.unicaen.iota.application.rmi.CallbackClient;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.TransactionEventType;

public class EpcisRequest extends Thread {

    private final String serviceAddress;
    private final String epc;
    private final Identity identity;
    private final String sessionID;
    private final CallbackClient client;
    private static final boolean isDebug = true; // TODO: hard value
    private static final Log log = LogFactory.getLog(EpcisRequest.class);

    public EpcisRequest(String serviceAddress, String epc, Identity identity, String sessionID, CallbackClient client) {
        this.identity = identity;
        this.epc = epc;
        this.sessionID = sessionID;
        this.client = client;
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
            EpcisOperation epcisOperation = new EpcisOperation(identity, serviceAddress);
            Collection<EPCISEventType> evts = epcisOperation.getEventFromEPC(epc);
            for (EPCISEventType evt : evts) {
                try {
                    if (isDebug) {
                        debug.append("------------------------------------------\n");
                        debug.append(evt.toString());
                        debug.append("\n------------------------------------------\n");
                    }
                    client.pushEvent(sessionID, evt);
                } catch (RemoteException ex) {
                    log.fatal(null, ex);
                }
            }
            if (isDebug) {
                debug.append("Aggregation: \n");
            }
            for (EPCISEventType o : evts) {
                if (o instanceof AggregationEventType) {
                    AggregationEventType event = (AggregationEventType) o;
                    for (EPC childEpc : event.getChildEPCs().getEpc()) {
                        log.trace("new traceEPC: " + childEpc.getValue());
                        new TraceEPCAsync(childEpc.getValue(), sessionID, client, identity).start();
                    }
                } else if (o instanceof TransactionEventType) {
                    TransactionEventType event = (TransactionEventType) o;
                    for (EPC childEpc : event.getEpcList().getEpc()) {
                        log.trace("new traceEPC: " + childEpc.getValue());
                        new TraceEPCAsync(childEpc.getValue(), sessionID, client, identity).start();
                    }
                }
            }
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        }
    }
}
