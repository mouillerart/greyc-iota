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
package fr.unicaen.iota.simulator.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.event.EventListenerList;

/**
 * @stereotype Singleton
 */
public class Trash {

    private static Trash instance = null;
    private final List<String> content = new ArrayList<String>();
    private final EventListenerList listeners = new EventListenerList();

    private Trash() {
    }

    public static Trash getInstance() {
        if (instance == null) {
            instance = new Trash();
        }
        return instance;
    }

    public void addEPCToTrash(Collection<String> epc) {
        content.addAll(epc);
        fireTrashEvt(epc);
    }

    public void clearTrash() {
        content.clear();
    }

    protected void fireTrashEvt(Collection<String> epcList) {
        for (TrashListener listener : getTrashListeners()) {
            listener.TrashEvt(epcList);
        }
    }

    public void addTrashListener(TrashListener listener) {
        listeners.add(TrashListener.class, listener);
    }

    public void removeTrashListener(TrashListener listener) {
        listeners.remove(TrashListener.class, listener);
    }

    public TrashListener[] getTrashListeners() {
        return listeners.getListeners(TrashListener.class);
    }
}
