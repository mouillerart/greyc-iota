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
package fr.unicaen.iota.application.client;

import fr.unicaen.iota.application.rmi.AccessInterface;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class TraceEPCRMIAsync extends Thread {

    private static final Log log = LogFactory.getLog(TraceEPCRMIAsync.class);
    private String epc;
    private String sessionId;
    private CallBackClientImpl callBackHandler;

    public TraceEPCRMIAsync(String epc, String sessionId, CallBackClientImpl callBackHandler) {
        this.epc = epc;
        this.sessionId = sessionId;
        this.callBackHandler = callBackHandler;
    }

    @Override
    public void run() {
        AccessInterface server;
        try {
            log.debug("Looking up server at " + Configuration.RMI_SERVICE_URL);
            URI uri = new URI(Configuration.RMI_SERVICE_URL);
            int port = uri.getPort();
            port = port == -1 ? 1099 : port;
            String name = uri.getPath().substring(1);
            String host = uri.getHost();
            log.trace("that is: rmi://" + host + ":" + port + "/" + name);
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (AccessInterface) registry.lookup(name);
        } catch (Exception e) {
            log.fatal("Failed to get RMI server", e);
            return;
        }
        try {
            log.trace("Calling server.traceEPCAsync");
            server.traceEPCAsync(sessionId, callBackHandler, epc);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        }
    }
}
