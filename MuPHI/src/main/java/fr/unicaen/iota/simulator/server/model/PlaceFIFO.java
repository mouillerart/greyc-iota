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
package fr.unicaen.iota.simulator.server.model;

import fr.unicaen.iota.simulator.server.util.MD5;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class PlaceFIFO {

    private final int placeSize;
    private String placeId;
    private int contentSize;
    private BlockingQueue<String> fifo;
    private List<String> reserverdID;
    private final long travelTimeInMilis;
    private Long departureTime = Long.MAX_VALUE;
    private static final Log log = LogFactory.getLog(PlaceFIFO.class);

    public String getPlaceId() {
        return placeId;
    }

    public double getContentOccupation() {
        return ((double) (placeSize - contentSize) / (double) placeSize) * 100.0;
    }

    public PlaceFIFO(String placeID, int placeSize, long travelTime) {
        super();
        this.placeSize = placeSize;
        this.contentSize = this.placeSize;
        this.placeId = placeID;
        this.fifo = new ArrayBlockingQueue<String>(placeSize);
        reserverdID = new ArrayList<String>();
        travelTimeInMilis = travelTime;
    }

    private boolean fullTruck = false;

    public synchronized String reserve(int canalSize) {
        if (contentSize - canalSize < 0) {
            return null;
        }
        fullTruck = false;
        contentSize = contentSize - canalSize;
        String id = generateId();
        reserverdID.add(id);
        return id;
    }

    public synchronized boolean put(String id, String epc) {
        int i;
        if ((i = reserverdID.indexOf(id)) == -1) {
            return false;
        }
        reserverdID.remove(i);
        try {
            for (String s : epc.split("%")) {
                fifo.put(s);
            }
            if (contentSize == 0) {
                departure();
            }
            return true;
        } catch (InterruptedException ex) {
            log.error(null, ex);
            return false;
        }
    }

    public synchronized List<String> peek() {
        List<String> list = new ArrayList<String>();
        if (!pipeIsReadyToPeek()) {
            return list;
        }
        for (String s : fifo) {
            try {
                list.add(fifo.take());
            } catch (InterruptedException ex) {
                log.error(null, ex);
            }
        }
        arrival();
        return list;
    }
    private long autoIncrementId = 0;

    private String generateId() {
        autoIncrementId++;
        try {
            return MD5.MD5_Algo(String.valueOf(new Date().getTime())) + String.valueOf(autoIncrementId);
        } catch (NoSuchAlgorithmException ex) {
            log.error(null, ex);
            return null;
        } catch (UnsupportedEncodingException ex) {
            log.error(null, ex);
            return null;
        }
    }

    private boolean pipeIsReadyToPeek() {
        if (fullTruck && new Date().getTime() > departureTime + travelTimeInMilis) {
            return true;
        }
        return false;
    }

    private void departure() {
        fullTruck = true;
        log.trace("#####################################################################");
        this.departureTime = new Date().getTime();
    }

    private void arrival() {
        this.departureTime = Long.MAX_VALUE;
        contentSize = placeSize;
    }

    public String getRemainingTime() {
        if (departureTime == Long.MAX_VALUE) {
            return null;
        }
        return String.valueOf(departureTime + travelTimeInMilis - (new Date().getTime()));
    }

    public int getSize() {
        return this.placeSize;
    }
}
