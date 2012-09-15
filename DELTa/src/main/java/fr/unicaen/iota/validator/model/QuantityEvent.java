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

import fr.unicaen.iota.application.model.EPCISEvent;
import java.util.Collection;
import org.jdom.Element;

/**
 *
 */
public class QuantityEvent extends BaseEvent {

    private String epcClass;
    private long quantity;

    public QuantityEvent(Long id, Infrastructure service) {
        super(id, service);
    }

    public QuantityEvent(Long id, Infrastructure service, String epcClass, long quantity) {
        super(id, service);
        this.epcClass = epcClass;
        this.quantity = quantity;
    }

    public QuantityEvent() {
        super();
        this.epcClass = null;
    }

    public String getEpcClass() {
        return epcClass;
    }

    public void setEpcClass(String epcClass) {
        this.epcClass = epcClass;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public void loadFromXML(Element elem) {
        super.loadFromXML(elem);
        for (Object o : elem.getChildren("EPCclass")) {
            Element e = (Element) o;
            epcClass = e.getValue();
        }
        for (Object o : elem.getChildren("quantity")) {
            Element e = (Element) o;
            quantity = Long.parseLong(e.getValue());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof QuantityEvent) {
            QuantityEvent quEvt = (QuantityEvent) o;
            return quEvt.getInfrastructure().getBizLoc().equals(getInfrastructure().getBizLoc())
                && quEvt.getBizStep().equals(getBizStep())
                && quEvt.getDisposition().equals(getDisposition())
                && quEvt.getEpcClass().equals(getEpcClass())
                && quEvt.getQuantity() == getQuantity();
        }
        return false;
    }

    @Override
    public boolean isContainedIn(Collection<EPCISEvent> list) {
        for (EPCISEvent event : list) {
            if (event.getBizLoc().equals(getInfrastructure().getBizLoc())
                    && event.getBizStep().equals(getBizStep())
                    && event.getDisposition().equals(getDisposition())
                    && event.getEPCClass().equals(getEpcClass())
                    && event.getQuantity().equals(String.valueOf(getQuantity()))) {
                return true;
            }
        }
        return false;
    }
}
