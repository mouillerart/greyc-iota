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
package fr.unicaen.iota.application.soap;

import fr.unicaen.iota.application.AccessInterface;
import fr.unicaen.iota.application.Configuration;
import java.net.URI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
// re-implements IoTaServicePortType to get the web service annotations
public class OMeGaRMI extends BaseOMeGa implements IoTaServicePortType {

    private static final Log log = LogFactory.getLog(OMeGaRMI.class);
    private AccessInterface controler;

    @Override
    protected AccessInterface getAI() {
        if (controler == null) {
            try {
                log.debug("Looking up server at " + Configuration.RMI_URL);
                URI uri = new URI(Configuration.RMI_URL);
                int port = uri.getPort();
                port = port == -1 ? 1099 : port;
                String name = uri.getPath().substring(1);
                String host = uri.getHost();
                log.trace("that is: rmi://" + host + ":" + port + "/" + name);
                Registry registry = LocateRegistry.getRegistry(host, port);
                controler = (AccessInterface) registry.lookup(name);
            } catch (Exception e) {
                log.error("Failed to setup for RMI", e);
                throw new UnsupportedOperationException("Unable to access RMI interface", e);
            }
        }
        return controler;
    }
}
