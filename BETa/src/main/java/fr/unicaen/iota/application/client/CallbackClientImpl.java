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

import fr.unicaen.iota.application.client.listener.EventDispatcher;
import fr.unicaen.iota.application.rmi.CallbackClient;
import java.io.Serializable;
import java.rmi.RemoteException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 *
 */
public class CallbackClientImpl extends EventDispatcher implements CallbackClient, Serializable {

    private static final Log log = LogFactory.getLog(CallbackClientImpl.class);

    public CallbackClientImpl() {
    }

    @Override
    public void pushEvent(String sessionID, EPCISEventType evt) throws RemoteException {
        log.trace("received in CallBackClientImpl->pushEvent");
        addEvent(sessionID, evt);
    }
}
