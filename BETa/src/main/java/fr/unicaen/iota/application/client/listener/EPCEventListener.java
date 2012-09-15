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

import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.util.TravelTimeTuple;
import java.util.EventListener;

/**
 *
 */
public interface EPCEventListener extends EventListener {

    public void eventReveived(String session, EPCISEvent e);

    public void travelTimeChanged(String session, TravelTimeTuple ttt);

    public void usedObjectsChanged(String session, int objects);
}
