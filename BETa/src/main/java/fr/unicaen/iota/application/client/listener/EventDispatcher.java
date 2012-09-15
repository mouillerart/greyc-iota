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
import fr.unicaen.iota.application.util.TimeParser;
import fr.unicaen.iota.application.util.TravelTimeTuple;
import java.util.*;
import javax.swing.event.EventListenerList;

/**
 *
 */
public class EventDispatcher {

    private Map<String, Set<EPCISEvent>> eventHashtable = new HashMap<String, Set<EPCISEvent>>();
    private Map<String, TravelTimeTuple> travelTime = new HashMap<String, TravelTimeTuple>();
    private Map<String, ArrayList<String>> usedObjects = new HashMap<String, ArrayList<String>>();
    private final EventListenerList listeners = new EventListenerList();

    public EventDispatcher() {
    }

    public synchronized void addEvent(String session, EPCISEvent e) {
        if (eventHashtable.get(session) == null) {
            eventHashtable.put(session, new HashSet<EPCISEvent>());
            travelTime.put(session, new TravelTimeTuple());
            usedObjects.put(session, new ArrayList<String>());
        }
        eventHashtable.get(session).add(e);
        TravelTimeTuple ttt = travelTime.get(session);
        ArrayList<String> nbObjects = usedObjects.get(session);
        processNbObjects(session, e, nbObjects);
        ttt.addEventTimestamp(TimeParser.convert(e.getEventTime()));
        fireEventReiceved(session, e);
        fireTravelTimeChanged(session, ttt);
    }

    public void addEPCEventListener(EPCEventListener listener) {
        listeners.add(EPCEventListener.class, listener);
    }

    public void removeTemperatureListener(EPCEventListener listener) {
        listeners.remove(EPCEventListener.class, listener);
    }

    public EPCEventListener[] getEPCEventListeners() {
        return listeners.getListeners(EPCEventListener.class);
    }

    protected void fireEventReiceved(String session, EPCISEvent e) {
        for (EPCEventListener listener : getEPCEventListeners()) {
            listener.eventReveived(session, e);
        }
    }

    private void fireTravelTimeChanged(String session, TravelTimeTuple ttt) {
        for (EPCEventListener listener : getEPCEventListeners()) {
            listener.travelTimeChanged(session, ttt);
        }
    }

    private void fireUsedObjectsChanged(String session, int objects) {
        for (EPCEventListener listener : getEPCEventListeners()) {
            listener.usedObjectsChanged(session, objects);
        }
    }

    private void processNbObjects(String session, EPCISEvent e, ArrayList<String> nbObjects) {
        if (EPCISEvent.ActionType.ADD == e.getAction()) {
            switch (e.getType()) {
                case OBJECT:
                {
                    String epc = e.getEpcs().get(0);
                    if (!nbObjects.contains(epc)) {
                        nbObjects.add(epc);
                    }
                    break;
                }
                case AGGREGATION:
                {
                    String epc = e.getParentID();
                    if (!nbObjects.contains(epc)) {
                        nbObjects.add(epc);
                    }
                    break;
                }
                case TRANSACTION:
                    throw new UnsupportedOperationException("Not yet implemented (Transaction) class: eventDispatcher");
                    //break;
                case QUANTITY:
                    throw new UnsupportedOperationException("Not yet implemented (Quantity) class: eventDispatcher");
                    //break;
            }
        }
        fireUsedObjectsChanged(session, nbObjects.size());
    }
}
