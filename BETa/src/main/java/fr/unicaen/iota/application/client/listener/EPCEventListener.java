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
package fr.unicaen.iota.application.client.listener;

import fr.unicaen.iota.application.util.TravelTimeTuple;
import java.util.EventListener;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 *
 */
public interface EPCEventListener extends EventListener {

    /**
     * Called when a new event is received for a request.
     *
     * @param sessionID the session ID of the request
     * @param evt the event
     */
    public void eventReveived(String sessionID, EPCISEventType evt);

    /**
     * Called when the travel time tuple changed.
     *
     * @param sessionID the session ID of the request
     * @param ttt the travel time tuple
     */
    public void travelTimeChanged(String sessionID, TravelTimeTuple ttt);

    /**
     * Called when object have changed.
     *
     * @param sessionID the session ID of the request
     * @param objects the number of changed objects
     */
    public void usedObjectsChanged(String sessionID, int objects);
}
