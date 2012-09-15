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
package fr.unicaen.iota.application.rmi;

import fr.unicaen.iota.application.conf.Constants;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RMIServer {

    private static final Log log = LogFactory.getLog(RMIServer.class);

    public static void main(String[] args) throws Exception {
        String policyFile = RMIServer.class.getClassLoader().getResource("java.policy").toString();
        System.setProperty("java.security.policy", policyFile);
        System.setProperty("java.rmi.server.hostname", Constants.RMI_SERVER_HOST);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        log.info("Creating server...");
        AccessInterface im = new AccessModule();
        log.trace("Exporting...");
        AccessInterface ali = (AccessInterface) UnicastRemoteObject.exportObject(im, Constants.RMI_SERVER_PORT);
        log.trace("Locating registry...");
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(Constants.RMI_SERVER_PORT);
            registry.list(); // throws if registry not running
        } catch (RemoteException e) {
            log.trace("Registry not found. We create one...");
            registry = LocateRegistry.createRegistry(Constants.RMI_SERVER_PORT);
        }
        log.trace("Binding...");
        registry.rebind(Constants.RMI_SERVER_NAME, ali);
        log.info("RMI server started at: rmi://" + Constants.RMI_SERVER_HOST + ":" + Constants.RMI_SERVER_PORT + "/" + Constants.RMI_SERVER_NAME);
    }
}
