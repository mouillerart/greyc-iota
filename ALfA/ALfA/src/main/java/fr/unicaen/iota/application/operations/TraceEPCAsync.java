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
import fr.unicaen.iota.application.rmi.CallBackClient;
import java.rmi.RemoteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TraceEPCAsync extends Thread {

    private final CallBackClient client;
    private final String sessionId;
    private final String LOGIN;
    private final String PASS;
    private final String EPC;
    private final ONSOperation onsOperation;
    private static final Log log = LogFactory.getLog(TraceEPCAsync.class);

    public TraceEPCAsync(String epc, CallBackClient client, String login, String pass, String sessionId) {
        this.client = client;
        this.LOGIN = login;
        this.PASS = pass;
        this.sessionId = sessionId;
        this.EPC = epc;
        this.onsOperation = new ONSOperation(Constants.ONS_HOSTS);
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
        String dsAddress = onsOperation.getReferentDS(epc);
        if (dsAddress == null) {
            log.warn("Unable to retreive referent ds address for this epc code");
            return;
        } else {
            log.trace("referent ds address found: " + dsAddress);
        }
        log.trace("Start discover");
        DiscoveryOperation dsOp = new DiscoveryOperation(LOGIN, PASS, dsAddress, client, sessionId);
        dsOp.discover(epc);
    }
}
