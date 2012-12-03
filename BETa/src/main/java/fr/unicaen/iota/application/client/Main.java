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

import fr.unicaen.iota.application.client.gui.GUI;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class Main {

    private static final Log log = LogFactory.getLog(Main.class);

    public static void main(String[] args) throws RemoteException {
        log.info("Starting GUI ...");
        String policyFile = Main.class.getClassLoader().getResource("java.policy").toString();
        System.setProperty("java.security.policy", policyFile);
        System.setProperty("java.rmi.server.hostname", Configuration.RMI_CALLBACK_HOST);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        CallbackClientImpl cbci = new CallbackClientImpl();
        UnicastRemoteObject.exportObject(cbci, Configuration.RMI_CALLBACK_PORT);
        GUI gui = new GUI(cbci);
        gui.setVisible(true);
    }
}
