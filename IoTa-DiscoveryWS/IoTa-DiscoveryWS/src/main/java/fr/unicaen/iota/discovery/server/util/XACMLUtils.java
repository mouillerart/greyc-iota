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
package fr.unicaen.iota.discovery.server.util;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.discovery.server.hibernate.Event;
import fr.unicaen.iota.xacml.pep.XACMLDSEvent;

/**
 *
 */
public final class XACMLUtils {

    private XACMLUtils() {
    }

    public static XACMLDSEvent createXACMLEvent(Event event) {
        return new XACMLDSEvent(event.getPartner().getPartnerID(), event.getBizStep(),
                event.getEpc(), event.getEPCClass(), event.getEventTimeStamp());
    }

    public static int createXACMLResponse(String resp) {
        return "ACCEPT".equals(resp) ? Result.DECISION_PERMIT : Result.DECISION_DENY;
    }
}
