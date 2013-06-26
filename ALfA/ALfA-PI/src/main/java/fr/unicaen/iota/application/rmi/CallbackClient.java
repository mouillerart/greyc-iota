/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.application.rmi;

import java.rmi.RemoteException;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 * RMI interface for a càllback client for the asynchronous tracing function of an <code>AccessInterface</code>.
 *
 * @see AccessInterface
 */
public interface CallbackClient extends java.rmi.Remote {

    /**
     * This method is remotely called by an <code>AccessInterface</code> when an event is found.
     * 
     * @param sessionID the session for the request
     * @param evt a new event
     * @throws RemoteException 
     */
    public void pushEvent(String sessionID, EPCISEventType evt) throws RemoteException;
}
