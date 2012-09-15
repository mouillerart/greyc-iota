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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;
import org.jdom.Element;

/**
 *
 */
public class ObjectEvent extends BaseEvent {

    private static final Log log = LogFactory.getLog(QuantityEvent.class);
    private List<String> epcList;
    private ActionType action;

    public ObjectEvent(ObjectEvent oe) {
        this.setAction(oe.getAction());
        this.setBizStep(oe.getBizStep());
        this.setDisposition(oe.getDisposition());
        this.setExtensions(oe.getExtensions());
        this.setInfrastructure(oe.getInfrastructure());
        this.setReadPoint(oe.getReadPoint());
        this.epcList = new ArrayList<String>();
    }

    public ObjectEvent(Long id, Infrastructure service) {
        super(id, service);
        this.epcList = new ArrayList<String>();
    }

    public ObjectEvent(Long id, Infrastructure service, Collection<String> epcList, ActionType action) {
        super(id, service);
        this.epcList = new ArrayList<String>(epcList);
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

    public void setEpcList(Collection<String> epcList) {
        this.epcList.clear();
        this.epcList.addAll(epcList);
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    @Override
    public EPCISEventType prepareEventType() {
        ObjectEventType objEvent = new ObjectEventType();

        // set EPCs
        EPCListType epcList2 = new EPCListType();
        for (String epcValue : getEpcList()) {
            EPC epc = new EPC();
            epc.setValue(epcValue);
            epcList2.getEpc().add(epc);
        }
        objEvent.setEpcList(epcList2);

        // set action
        objEvent.setAction(getAction());

        // set bizStep
        objEvent.setBizStep(getBizStep());

        // set disposition
        objEvent.setDisposition(getDisposition());

        // set readPoint
        ReadPointType readPoint = new ReadPointType();
        readPoint.setId(getReadPoint());
        objEvent.setReadPoint(readPoint);

        // set bizLocation
        BusinessLocationType bizLocation = new BusinessLocationType();
        bizLocation.setId(getInfrastructure().getBizLoc());
        objEvent.setBizLocation(bizLocation);

        return objEvent;
    }

    @Override
    protected void setExtensionsObjects(EPCISEventType event, List<Object> extensionsObjects) {
        ((ObjectEventType) event).getAny().addAll(extensionsObjects);
    }
    
    @Override
    public String toXML() {
        StringBuilder str = new StringBuilder();
        str.append("\t<node type=\"object\" >\n");
        str.append(propertiesToXML());
        str.append(getInfrastructure().toXML());
        str.append("\t\t<action>");
        str.append(action);
        str.append("</action>\n");
        str.append("\t\t<EPCs>\n");
        for (String epc : epcList) {
            str.append("\t\t\t<epc>");
            str.append(epc);
            str.append("</epc>\n");
        }
        str.append("\t\t</EPCs>\n");
        str.append("\t</node>\n");
        return str.toString();
    }

    @Override
    public void loadFromXML(Element elem, Infrastructure infrastructure, LatLonLocation latLonLocation) {
        super.loadFromXML(elem, infrastructure, latLonLocation);
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
}
