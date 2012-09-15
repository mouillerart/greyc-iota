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

import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 */
public final class Util {

    private Util() {
    }

    public static Timestamp getActualTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static Timestamp convert(Calendar cal) {
        return new Timestamp(cal.getTimeInMillis());
    }
}
