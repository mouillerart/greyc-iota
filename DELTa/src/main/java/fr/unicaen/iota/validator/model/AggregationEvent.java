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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.fosstrak.epcis.model.ActionType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.jdom.Element;

/**
 *
 */
public class AggregationEvent extends BaseEvent {

    private String parentId;
    private List<String> childEpcs;
    private ActionType action;
    private Timestamp eventTime;

    public AggregationEvent(Long id, Infrastructure service, String parentId, List<String> childEpcs, ActionType action, Timestamp eventTime) {
        super(id, service);
        this.parentId = parentId;
        this.childEpcs = childEpcs;
        this.action = action;
        this.eventTime = eventTime;
    }

    public AggregationEvent() {
        super();
        this.parentId = null;
        this.childEpcs = new ArrayList<String>();
        this.action = null;
        this.eventTime = null;
    }

    public AggregationEvent(Long id, Infrastructure service) {
        this(id, service, null, new ArrayList<String>(), null, null);
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public List<String> getChildEpcs() {
        return childEpcs;
    }

    public void setChildEpcs(List<String> childEpcs) {
        this.childEpcs = childEpcs;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public void loadFromXML(Element elem) {
        super.loadFromXML(elem);
        for (Object o : elem.getChildren("parentId")) {
            Element e = (Element) o;
            parentId = e.getValue();
        }
        for (Object o : elem.getChildren("childEPC")) {
            Element e = (Element) o;
            for (Object o2 : e.getChildren("epc")) {
                Element e2 = (Element) o2;
                childEpcs.add(e2.getValue());
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
            for (String epc : event.getChildren()) {
                if (!getChildEpcs().contains(epc)) {
                    return false;
                }
            }
            return event.getAction() == getAction()
                && event.getBizLocation().equals(getInfrastructure().getBizLoc())
                && event.getBizStep().equals(getBizStep())
                && event.getDisposition().equals(getDisposition())
                && event.getParentID().equals(getParentId());
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
        if (o instanceof AggregationEvent) {
            AggregationEvent aggEvt = (AggregationEvent) o;
            for (String epc : aggEvt.getChildEpcs()) {
                if (!getChildEpcs().contains(epc)) {
                    return false;
                }
            }
            return aggEvt.getAction() == getAction()
                && aggEvt.getInfrastructure().getBizLoc().equals(getInfrastructure().getBizLoc())
                && aggEvt.getBizStep().equals(getBizStep())
                && aggEvt.getDisposition().equals(getDisposition())
                && aggEvt.getParentId().equals(getParentId());
        }
        return false;
    }

    /**
     * @return the eventTime
     */
    public Timestamp getEventTime() {
        return eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }
}
