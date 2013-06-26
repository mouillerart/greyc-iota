/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.eta.callback.filter.utils;

import java.util.List;
import org.fosstrak.epcis.model.EPCISEventType;

public class Utils {

    /**
     * Gets the owner of the event from the corresponding extension.
     * @param event The event to process.
     * @return The event owner.
     */
    public static String getEventOwner(EPCISEventType event) {
        List<String> owners = fr.unicaen.iota.mu.Utils.getExtension(event,
                fr.unicaen.iota.mu.Constants.URN_IOTA, fr.unicaen.iota.mu.Constants.EXTENSION_OWNER_ID);
        if (owners != null && !owners.isEmpty()) {
            return owners.get(0);
        }
        return null;
    }

}
