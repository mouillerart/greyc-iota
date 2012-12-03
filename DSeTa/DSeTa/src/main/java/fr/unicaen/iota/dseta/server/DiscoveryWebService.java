/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.dseta.server;

import fr.unicaen.iota.ds.model.*;
import fr.unicaen.iota.ds.soap.DSServicePortType;

/**
 *
 */
public class DiscoveryWebService extends DSeTaWebService implements DSServicePortType {

    @Override
    public EventLookupOut eventLookup(EventLookupIn parms) {
        return iDedEventLookup(parms, anonymous);
    }

    @Override
    public MultipleEventCreateOut multipleEventCreate(MultipleEventCreateIn parms) {
        return iDedMultipleEventCreate(parms, anonymous);
    }

    @Override
    public EventCreateOut eventCreate(EventCreateIn parms) {
        return iDedEventCreate(parms, anonymous);
    }
}
