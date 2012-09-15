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
package fr.unicaen.iota.simulator.model;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.BusinessLocationType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.QuantityEventType;
import org.fosstrak.epcis.model.ReadPointType;
import org.jdom.Element;

/**
 *
 */
public class QuantityEvent extends BaseEvent {

    private static final Log log = LogFactory.getLog(QuantityEvent.class);
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
    protected EPCISEventType prepareEventType() {
        QuantityEventType quantityEvent = new QuantityEventType();

        // set EPC CLASS
        quantityEvent.setEpcClass(getEpcClass());

        // set quantity
        quantityEvent.setQuantity((int) getQuantity());

        // set bizStep
        quantityEvent.setBizStep(getBizStep());

        // set disposition
        quantityEvent.setDisposition(getDisposition());

        // set readPoint
        ReadPointType readPoint = new ReadPointType();
        readPoint.setId(getReadPoint());
        quantityEvent.setReadPoint(readPoint);

        // set bizLocation
        BusinessLocationType bizLocation = new BusinessLocationType();
        bizLocation.setId(getInfrastructure().getBizLoc());
        quantityEvent.setBizLocation(bizLocation);

        return quantityEvent;
    }

    @Override
    protected void setExtensionsObjects(EPCISEventType event, List<Object> extensionsObjects) {
        ((QuantityEventType) event).getAny().addAll(extensionsObjects);
    }
    
    @Override
    public String toXML() {
        StringBuilder str = new StringBuilder();
        str.append("\t<node type=\"quantity\" >\n");
        str.append(propertiesToXML());
        str.append(getInfrastructure().toXML());
        str.append("\t\t<EPCclass>");
        str.append(epcClass);
        str.append("</EPCclass>\n");
        str.append("\t\t<quantity>");
        str.append(quantity);
        str.append("</quantity>\n");
        str.append("\t</node>\n");
        return str.toString();
    }

    @Override
    public void loadFromXML(Element elem, Infrastructure infrastructure, LatLonLocation latLonLocation) {
        super.loadFromXML(elem, infrastructure, latLonLocation);
        for (Object o : elem.getChildren("EPCclass")) {
            Element e = (Element) o;
            epcClass = e.getValue();
        }
        for (Object o : elem.getChildren("quantity")) {
            Element e = (Element) o;
            quantity = Long.parseLong(e.getValue());
        }
    }
}
