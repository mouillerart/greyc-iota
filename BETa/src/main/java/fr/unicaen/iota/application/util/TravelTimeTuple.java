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
package fr.unicaen.iota.application.util;

import java.sql.Timestamp;

/**
 *
 */
public class TravelTimeTuple {

    private Timestamp older;
    private double travelTime;
    private Timestamp newer;

    public TravelTimeTuple() {
        travelTime = 0;
        older = null;
        newer = null;
    }

    public void addEventTimestamp(Timestamp time) {
        if (older == null) {
            older = time;
            return;
        } else if (newer == null) {
            if (older.after(time)) {
                newer = older;
                older = time;
            } else {
                newer = time;
            }
        } else if (time.before(older)) {
            older = time;
        } else if (time.after(newer)) {
            newer = time;
        }
        travelTime = newer.getTime() - older.getTime();
    }

    /**
     * @return the older
     */
    public Timestamp getOlder() {
        return older;
    }

    /**
     * @param older the older to set
     */
    public void setOlder(Timestamp older) {
        this.older = older;
    }

    /**
     * @return the travelTime
     */
    public double getTravelTime() {
        return travelTime;
    }

    /**
     * @param travelTime the travelTime to set
     */
    public void setTravelTime(double travelTime) {
        this.travelTime = travelTime;
    }

    /**
     * @return the newer
     */
    public Timestamp getNewer() {
        return newer;
    }

    /**
     * @param newer the newer to set
     */
    public void setNewer(Timestamp newer) {
        this.newer = newer;
    }
}
