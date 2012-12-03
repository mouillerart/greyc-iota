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
package fr.unicaen.iota.validator.model;

import fr.unicaen.iota.mu.EPCISEventTypeHelper;
import org.fosstrak.epcis.model.EPCISEventType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.fosstrak.epcis.model.ActionType;
import org.jdom.Element;

/**
 *
 */
public class ObjectEvent extends BaseEvent {

    private List<String> epcList;
    private ActionType action;

    public ObjectEvent(Long id, Infrastructure service) {
        super(id, service);
    }

    public ObjectEvent(Long id, Infrastructure service, List<String> epcList, ActionType action) {
        super(id, service);
        this.epcList = epcList;
        this.action = action;
    }

    public ObjectEvent() {
        super();
        this.epcList = new ArrayList<String>();
        this.action = null;
    }

    public List<String> getEpcList() {
        return epcList;
    }

    public void setEpcList(List<String> epcList) {
        this.epcList = epcList;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    @Override
    public void loadFromXML(Element elem) {
        super.loadFromXML(elem);
        for (Object o : elem.getChildren("EPCs")) {
            Element e = (Element) o;
            for (Object o2 : e.getChildren("epc")) {
                Element e2 = (Element) o2;
                epcList.add(e2.getValue());
            }
        }
        for (Object o : elem.getChildren("action")) {
            Element e = (Element) o;
            action = ActionType.fromValue(e.getValue());
        }
    }

    @Override
    public boolean isContainedIn(Collection<EPCISEventType> list) {
        for (EPCISEventType evt : list) {
            EPCISEventTypeHelper event = new EPCISEventTypeHelper(evt);
            for (String epc : event.getEpcs()) {
                if (!getEpcList().contains(epc)) {
                    return false;
                }
            }
            if (event.getAction() == getAction()
                    && event.getBizLocation().equals(getInfrastructure().getBizLoc())
                    && event.getBizStep().equals(getBizStep())
                    && event.getDisposition().equals(getDisposition())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof ObjectEvent) {
            ObjectEvent objEvt = (ObjectEvent) o;
            for (String epc : objEvt.getEpcList()) {
                if (!getEpcList().contains(epc)) {
                    return false;
                }
            }
            return objEvt.getAction() == getAction()
                    && objEvt.getInfrastructure().getBizLoc().equals(getInfrastructure().getBizLoc())
                    && objEvt.getBizStep().equals(getBizStep())
                    && objEvt.getDisposition().equals(getDisposition());
        }
        return false;
    }
}
