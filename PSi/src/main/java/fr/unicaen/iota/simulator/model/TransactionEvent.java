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
public class TransactionEvent extends BaseEvent {

    private static final Log log = LogFactory.getLog(TransactionEvent.class);
    private String epcBase;
    private String parentId;
    private List<String> epcList;
    private ActionType action;
    private BusinessTransactionListType buiBusinessTransactionListType;

    public TransactionEvent(TransactionEvent te) {
        this.setAction(te.getAction());
        this.setBizStep(te.getBizStep());
        this.setDisposition(te.getDisposition());
        this.setExtensions(te.getExtensions());
        this.setInfrastructure(te.getInfrastructure());
        this.setReadPoint(te.getReadPoint());
        this.setEPCBase(te.getEPCBase());
        this.setBuiBusinessTransactionListType(te.getBuiBusinessTransactionListType());
        this.epcList = new ArrayList<String>();
    }

    public TransactionEvent(Long id, Infrastructure service) {
        super(id, service);
        this.epcList = new ArrayList<String>();
    }

    public TransactionEvent(Long id, Infrastructure service, String parentId, Collection<String> epcList, ActionType action, String epcBase) {
        super(id, service);
        this.parentId = parentId;
        this.epcList = epcList == null ? new ArrayList<String>() : new ArrayList<String>(epcList);
        this.action = action;
        this.buiBusinessTransactionListType = new BusinessTransactionListType();
        this.epcBase = epcBase;
    }

    public TransactionEvent() {
        super();
        this.parentId = null;
        this.epcList = new ArrayList<String>();
        this.action = null;
        this.buiBusinessTransactionListType = new BusinessTransactionListType();
        this.epcBase = null;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public List<String> getEpcList() {
        return epcList;
    }

    public void setEpcList(Collection<String> epcList) {
        this.epcList.clear();
        this.epcList.addAll(epcList);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public EPCISEventType prepareEventType() {
        TransactionEventType transEvent = new TransactionEventType();

        // set EPCs
        EPCListType epcList2 = new EPCListType();
        for (String epcValue : getEpcList()) {
            EPC epc = new EPC();
            epc.setValue(epcValue);
            epcList2.getEpc().add(epc);
        }
        transEvent.setEpcList(epcList2);

        // set biztransaction list
        transEvent.setBizTransactionList(getBuiBusinessTransactionListType());

        // set action
        transEvent.setAction(getAction());

        // set bizStep
        transEvent.setBizStep(getBizStep());

        // set disposition
        transEvent.setDisposition(getDisposition());

        // set readPoint
        ReadPointType readPoint = new ReadPointType();
        readPoint.setId(getReadPoint());
        transEvent.setReadPoint(readPoint);

        // set bizLocation
        BusinessLocationType bizLocation = new BusinessLocationType();
        bizLocation.setId(getInfrastructure().getBizLoc());
        transEvent.setBizLocation(bizLocation);

        return transEvent;
    }

    @Override
    protected void setExtensionsObjects(EPCISEventType event, List<Object> extensionsObjects) {
        ((TransactionEventType) event).getAny().addAll(extensionsObjects);
    }

    public BusinessTransactionListType getBuiBusinessTransactionListType() {
        return buiBusinessTransactionListType;
    }

    public void setBuiBusinessTransactionListType(BusinessTransactionListType buiBusinessTransactionListType) {
        this.buiBusinessTransactionListType = buiBusinessTransactionListType;
    }

    @Override
    public String toXML() {
        StringBuilder str = new StringBuilder();
        str.append("\t<node type=\"transaction\" >\n");
        str.append(propertiesToXML());
        str.append(getInfrastructure().toXML());
        str.append("\t\t<epcBase>");
        str.append(getEPCBase());
        str.append("</epcBase>\n");
        str.append("\t\t<parentId>");
        str.append(parentId);
        str.append("</parentId>\n");
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
        str.append("\t\t<bizTransList>\n");
        for (BusinessTransactionType btlt : buiBusinessTransactionListType.getBizTransaction()) {
            str.append("\t\t\t<bizTrans type=\"");
            str.append(btlt.getType());
            str.append("\">");
            str.append(btlt.getValue());
            str.append("</bizTrans>\n");
        }
        str.append("\t\t</bizTransList>\n");
        str.append("\t</node>\n");
        return str.toString();
    }

    @Override
    public void loadFromXML(Element elem, Infrastructure infrastructure, LatLonLocation latLonLocation) {
        super.loadFromXML(elem, infrastructure, latLonLocation);
        for (Object o : elem.getChildren("epcBase")) {
            Element e = (Element) o;
            setEPCBase(e.getValue());
        }
        for (Object o : elem.getChildren("parentId")) {
            Element e = (Element) o;
            parentId = e.getValue();
        }
        for (Object o : elem.getChildren("action")) {
            Element e = (Element) o;
            action = ActionType.valueOf(e.getValue());
        }
        for (Object o : elem.getChildren("EPCs")) {
            Element e = (Element) o;
            for (Object o2 : e.getChildren("epc")) {
                Element e2 = (Element) o2;
                epcList.add(e2.getValue());
            }
        }
        for (Object o : elem.getChildren("bizTransList")) {
            Element e = (Element) o;
            for (Object o2 : e.getChildren("bizTrans")) {
                Element e2 = (Element) o2;
                BusinessTransactionType btt = new BusinessTransactionType();
                btt.setType(e2.getAttributeValue("type"));
                btt.setValue(e2.getValue());
                buiBusinessTransactionListType.getBizTransaction().add(btt);
            }
        }
    }

    public String getEPCBase() {
        return epcBase;
    }

    public void setEPCBase(String epcBase) {
        this.epcBase = epcBase;
    }
}
