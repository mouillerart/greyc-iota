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

import fr.unicaen.iota.application.rmi.CallbackClient;
import fr.unicaen.iota.nu.ONSOperation;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TraceEPCAsync extends Thread {

    private final CallbackClient client;
    private final String sessionID;
    private final Identity identity;
    private final String EPC;
    private final ONSOperation onsOperation;
    private static final Log log = LogFactory.getLog(TraceEPCAsync.class);

    public TraceEPCAsync(String epc, String sessionID, CallbackClient client, Identity identity) {
        this.client = client;
        this.sessionID = sessionID;
        this.identity = identity;
        this.EPC = epc;
        this.onsOperation = new ONSOperation();
    }

    @Override
    public void run() {
        try {
            traceEPC(EPC);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        }
    }

    private void traceEPC(String epc) throws RemoteException {
        log.trace("[TRACE EPC]: " + epc);
        log.trace("CALLED METHOD: <traceEPC>");
        log.trace("Get Referent ds address");
        String dsAddress = onsOperation.getReferentIDedDS(epc);
        if (dsAddress == null) {
            log.warn("Unable to retreive referent ds address for this epc code");
            return;
        } else {
            log.trace("referent ds address found: " + dsAddress);
        }
        log.trace("Start discover");
        DiscoveryOperation dsOp = new DiscoveryOperation(identity, dsAddress, sessionID, client);
        dsOp.discover(epc);
    }
}
