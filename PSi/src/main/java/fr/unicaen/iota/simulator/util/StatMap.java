/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.util;

import java.util.HashMap;

/**
 *
 */
public class StatMap extends HashMap<String, Stat> {

    public StatMap() {
        super();
    }

    public Stat get(String bizLoc, String folder) {
        Stat stat = get(bizLoc);
        if (stat == null) {
            stat = new Stat(bizLoc, folder);
            put(bizLoc, stat);
        }
        return stat;
    }
}
