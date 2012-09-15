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
public class AggregationEvent extends BaseEvent {

    private static final Log log = LogFactory.getLog(AggregationEvent.class);
    private String parentId;
    private List<String> childEpcs;
    private ActionType action;

    public AggregationEvent(AggregationEvent ae) {
        this.setAction(ae.getAction());
        this.setBizStep(ae.getBizStep());
        this.setDisposition(ae.getDisposition());
        this.setExtensions(ae.getExtensions());
        this.setInfrastructure(ae.getInfrastructure());
        this.setReadPoint(ae.getReadPoint());
        this.childEpcs = new ArrayList<String>();
    }

    public AggregationEvent(Long id, Infrastructure service, String parentId, Collection<String> childEpcs, ActionType action, String epcBase) {
        super(id, service);
        this.parentId = parentId;
        this.childEpcs = new ArrayList<String>();
        if (childEpcs != null) {
            this.childEpcs.addAll(childEpcs);
        }
        this.action = action;
    }

    public AggregationEvent() {
        super();
        this.parentId = null;
        this.childEpcs = new ArrayList<String>();
        this.action = null;
    }

    public AggregationEvent(Long id, Infrastructure service) {
        super(id, service);
        this.childEpcs = new ArrayList<String>();
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

    public void setChildEpcs(Collection<String> childEpcs) {
        this.childEpcs.clear();
        this.childEpcs.addAll(childEpcs);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public EPCISEventType prepareEventType() {
        AggregationEventType aggregEvent = new AggregationEventType();

        // set EPCs
        EPCListType epcList2 = new EPCListType();
        for (String epcValue : getChildEpcs()) {
            EPC epc = new EPC();
            epc.setValue(epcValue);
            epcList2.getEpc().add(epc);
        }
        aggregEvent.setParentID(parentId);
        aggregEvent.setChildEPCs(epcList2);

        // set action
        aggregEvent.setAction(getAction());

        // set bizStep
        aggregEvent.setBizStep(getBizStep());

        // set disposition
        aggregEvent.setDisposition(getDisposition());

        // set readPoint
        ReadPointType readPoint = new ReadPointType();
        readPoint.setId(getReadPoint());
        aggregEvent.setReadPoint(readPoint);

        // set bizLocation
        BusinessLocationType bizLocation = new BusinessLocationType();
        bizLocation.setId(getInfrastructure().getBizLoc());
        aggregEvent.setBizLocation(bizLocation);

        return aggregEvent;
    }

    @Override
    protected void setExtensionsObjects(EPCISEventType event, List<Object> extensionsObjects) {
        ((AggregationEventType) event).getAny().addAll(extensionsObjects);
    }
    
    @Override
    public String toXML() {
        StringBuilder str = new StringBuilder();
        str.append("\t<node type=\"aggregation\" >\n");
        str.append(propertiesToXML());
        str.append(getInfrastructure().toXML());
        str.append("\t\t<parentId>");
        str.append(parentId);
        str.append("</parentId>\n");
        str.append("\t\t<action>");
        str.append(action);
        str.append("</action>\n");
        str.append("\t\t<childEPC>\n");
        for (String epc : childEpcs) {
            str.append("\t\t\t<epc>");
            str.append(epc);
            str.append("</epc>\n");
        }
        str.append("\t\t</childEPC>\n");
        str.append("\t</node>\n");
        return str.toString();
    }

    @Override
    public void loadFromXML(Element elem, Infrastructure infrastructure, LatLonLocation latLonLocation) {
        super.loadFromXML(elem, infrastructure, latLonLocation);
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
}
