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

import fr.unicaen.iota.application.util.TimeParser;
import fr.unicaen.iota.application.util.TravelTimeTuple;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.event.EventListenerList;
import org.fosstrak.epcis.model.*;

/**
 *
 */
public class EventDispatcher {

    private Map<String, TravelTimeTuple> travelTime = new HashMap<String, TravelTimeTuple>();
    private Map<String, Set<String>> usedObjects = new HashMap<String, Set<String>>();
    private final EventListenerList listeners = new EventListenerList();

    public EventDispatcher() {
    }

    public synchronized void addEvent(String session, EPCISEventType e) {
        if (!travelTime.containsKey(session)) {
            travelTime.put(session, new TravelTimeTuple());
            usedObjects.put(session, new HashSet<String>());
        }
        TravelTimeTuple ttt = travelTime.get(session);
        Set<String> nbObjects = usedObjects.get(session);
        processNbObjects(session, e, nbObjects);
        ttt.addEventTimestamp(TimeParser.convert(e.getEventTime().toGregorianCalendar()));
        fireEventReiceved(session, e);
        fireTravelTimeChanged(session, ttt);
    }

    public void addEPCEventListener(EPCEventListener listener) {
        listeners.add(EPCEventListener.class, listener);
    }

    public void removeTemperatureListener(EPCEventListener listener) {
        listeners.remove(EPCEventListener.class, listener);
    }

    private EPCEventListener[] getEPCEventListeners() {
        return listeners.getListeners(EPCEventListener.class);
    }

    protected void fireEventReiceved(String session, EPCISEventType e) {
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

    private void processNbObjects(String session, EPCISEventType e, Set<String> nbObjects) {
        if (e instanceof ObjectEventType) {
            ObjectEventType oe = (ObjectEventType) e;
            if (oe.getAction() == ActionType.ADD) {
                String epc = oe.getEpcList().getEpc().get(0).getValue();
                nbObjects.add(epc);
            }
        } else if (e instanceof AggregationEventType) {
            AggregationEventType ae = (AggregationEventType) e;
            if (ae.getAction() == ActionType.ADD) {
                String epc = ae.getParentID();
                nbObjects.add(epc);
            }
        } else if (e instanceof TransactionEventType) {
            throw new UnsupportedOperationException("Not yet implemented (Transaction) class: eventDispatcher");
        } else if (e instanceof QuantityEventType) {
            throw new UnsupportedOperationException("Not yet implemented (Quantity) class: eventDispatcher");
        }
        fireUsedObjectsChanged(session, nbObjects.size());
    }
}
