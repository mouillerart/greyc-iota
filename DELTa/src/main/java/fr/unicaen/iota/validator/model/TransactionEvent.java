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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.fosstrak.epcis.model.ActionType;
import org.fosstrak.epcis.model.BusinessTransactionListType;
import org.fosstrak.epcis.model.BusinessTransactionType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.jdom.Element;

/**
 *
 */
public class TransactionEvent extends BaseEvent {

    private String epcBase;
    private String parentId;
    private List<String> epcList;
    private ActionType action;
    private BusinessTransactionListType buiBusinessTransactionListType;

    public TransactionEvent(Long id, Infrastructure service) {
        super(id, service);
    }

    public TransactionEvent(Long id, Infrastructure service, String parentId, List<String> epcList, ActionType action, String epcBase) {
        super(id, service);
        this.parentId = parentId;
        this.epcList = epcList;
        this.action = action;
        this.buiBusinessTransactionListType = new BusinessTransactionListType();
        this.epcBase = epcBase;
    }

    public TransactionEvent() {
        super();
        this.parentId = null;
        this.epcList = null;
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

    public void setEpcList(List<String> epcList) {
        this.epcList = epcList;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public BusinessTransactionListType getBuiBusinessTransactionListType() {
        return buiBusinessTransactionListType;
    }

    public void setBuiBusinessTransactionListType(BusinessTransactionListType buiBusinessTransactionListType) {
        this.buiBusinessTransactionListType = buiBusinessTransactionListType;
    }

    @Override
    public void loadFromXML(Element elem) {
        super.loadFromXML(elem);
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
            for (Object o2 : e.getChildren("epc")) {
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

    @Override
    public boolean isContainedIn(Collection<EPCISEventType> list) {
        for (EPCISEventType evt : list) {
            EPCISEventTypeHelper event = new EPCISEventTypeHelper(evt);
            for (String epc : event.getEpcList()) {
                if (!getEpcList().contains(epc)) {
                    return false;
                }
            }
            if (event.getAction() == getAction()
                    && event.getBizLocation().equals(getInfrastructure().getBizLoc())
                    && event.getBizStep().equals(getBizStep())
                    && event.getDisposition().equals(getDisposition())
                    && event.getParentID().equals(getParentId())
                    && verifyBizTransList(event.getBizTransactions())) {
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
        if (o instanceof TransactionEvent) {
            TransactionEvent traEvt = (TransactionEvent) o;
            for (String epc : traEvt.getEpcList()) {
                if (!getEpcList().contains(epc)) {
                    return false;
                }
            }
            return traEvt.getAction().value().equals(getAction().value())
                && traEvt.getInfrastructure().getBizLoc().equals(getInfrastructure().getBizLoc())
                && traEvt.getBizStep().equals(getBizStep())
                && traEvt.getDisposition().equals(getDisposition())
                && traEvt.getParentId().equals(getParentId())
                && verifyBizTransList(traEvt);
        }
        return false;
    }

    private boolean verifyBizTransList(Map<String, String> bizTrans) {
        for (BusinessTransactionType btt : getBuiBusinessTransactionListType().getBizTransaction()) {
            if (!bizTrans.containsKey(btt.getType())) {
                return false;
            }
            if (!bizTrans.get(btt.getType()).equals(btt.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyBizTransList(TransactionEvent traEvt) {
        return false; //TODO:
    }
}
