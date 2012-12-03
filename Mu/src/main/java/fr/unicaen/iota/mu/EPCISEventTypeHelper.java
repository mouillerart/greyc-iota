/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.mu;

import java.util.*;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.fosstrak.epcis.model.*;
import org.w3c.dom.Element;

/**
 *
 */
public class EPCISEventTypeHelper {

    public static enum EventType {

        OBJECT,
        TRANSACTION,
        AGGREGATION,
        QUANTITY
    }
    private final EPCISEventType epcisEvent;
    private final EventType type;
    private final String parentID;
    private final ActionType action;
    private final String bizStep;
    private final String bizLocation;
    private final String disposition;
    private final String readPoint;
    private final String quantity;
    private final String epcClass;
    private final List<String> children = new ArrayList<String>();
    private final List<String> epcList = new ArrayList<String>();
    private final Map<String, String> bizTransactions = new HashMap<String, String>();
    private final List<Object> any;
    private final ExtensionTypeHelper extension;

    public static List<EPCISEventType> listFromEventList(EventListType eventList) {
        List<EPCISEventType> list = new ArrayList<EPCISEventType>();
        for (Object obj : eventList.getObjectEventOrAggregationEventOrQuantityEvent()) {
            EPCISEventType elt = (EPCISEventType) (obj instanceof JAXBElement ? ((JAXBElement) obj).getValue() : obj);
            list.add(elt);
        }
        return list;
    }

    public static List<EPCISEventTypeHelper> helpersFromEventList(EventListType eventList) {
        List<EPCISEventTypeHelper> list = new ArrayList<EPCISEventTypeHelper>();
        for (Object obj : eventList.getObjectEventOrAggregationEventOrQuantityEvent()) {
            EPCISEventType eltc = (EPCISEventType) (obj instanceof JAXBElement ? ((JAXBElement) obj).getValue() : obj);
            list.add(new EPCISEventTypeHelper(eltc));
        }
        return list;
    }

    public EPCISEventTypeHelper(EPCISEventType event) {
        epcisEvent = event;
        if (event instanceof ObjectEventType) {
            ObjectEventType oe = (ObjectEventType) event;
            type = EventType.OBJECT;
            extension = new ExtensionTypeHelper(oe.getExtension());
            parentID = null;
            for (EPC epc : oe.getEpcList().getEpc()) {
                epcList.add(epc.getValue());
            }
            action = oe.getAction();
            quantity = null;
            epcClass = null;
            bizStep = oe.getBizStep();
            disposition = oe.getDisposition();
            readPoint = oe.getReadPoint() == null ? null : oe.getReadPoint().getId();
            bizLocation = oe.getBizLocation() == null ? null : oe.getBizLocation().getId();
            if (oe.getBizTransactionList() != null) {
                for (BusinessTransactionType bizTran : oe.getBizTransactionList().getBizTransaction()) {
                    bizTransactions.put(bizTran.getType(), bizTran.getValue());
                }
            }
            any = oe.getAny();
        } else if (event instanceof TransactionEventType) {
            TransactionEventType te = (TransactionEventType) event;
            type = EventType.TRANSACTION;
            extension = new ExtensionTypeHelper(te.getExtension());
            parentID = te.getParentID();
            for (EPC epc : te.getEpcList().getEpc()) {
                epcList.add(epc.getValue());
            }
            action = te.getAction();
            quantity = null;
            epcClass = null;
            bizStep = te.getBizStep();
            disposition = te.getDisposition();
            readPoint = te.getReadPoint() == null ? null : te.getReadPoint().getId();
            bizLocation = te.getBizLocation() == null ? null : te.getBizLocation().getId();
            if (te.getBizTransactionList() != null) {
                for (BusinessTransactionType bizTran : te.getBizTransactionList().getBizTransaction()) {
                    bizTransactions.put(bizTran.getType(), bizTran.getValue());
                }
            }
            any = te.getAny();
        } else if (event instanceof AggregationEventType) {
            AggregationEventType ae = (AggregationEventType) event;
            type = EventType.AGGREGATION;
            extension = new ExtensionTypeHelper(ae.getExtension());
            parentID = ae.getParentID();
            for (EPC epc : ae.getChildEPCs().getEpc()) {
                children.add(epc.getValue());
            }
            action = ae.getAction();
            quantity = null;
            epcClass = null;
            bizStep = ae.getBizStep();
            disposition = ae.getDisposition();
            readPoint = ae.getReadPoint() == null ? null : ae.getReadPoint().getId();
            bizLocation = ae.getBizLocation() == null ? null : ae.getBizLocation().getId();
            if (ae.getBizTransactionList() != null) {
                for (BusinessTransactionType bizTran : ae.getBizTransactionList().getBizTransaction()) {
                    bizTransactions.put(bizTran.getType(), bizTran.getValue());
                }
            }
            any = ae.getAny();
        } else if (event instanceof QuantityEventType) {
            QuantityEventType qe = (QuantityEventType) event;
            type = EventType.QUANTITY;
            extension = new ExtensionTypeHelper(qe.getExtension());
            parentID = null;
            action = null;
            quantity = String.valueOf(qe.getQuantity());
            epcClass = qe.getEpcClass();
            bizStep = qe.getBizStep();
            disposition = qe.getDisposition();
            readPoint = qe.getReadPoint() == null ? null : qe.getReadPoint().getId();
            bizLocation = qe.getBizLocation() == null ? null : qe.getBizLocation().getId();
            if (qe.getBizTransactionList() != null) {
                for (BusinessTransactionType bizTran : qe.getBizTransactionList().getBizTransaction()) {
                    bizTransactions.put(bizTran.getType(), bizTran.getValue());
                }
            }
            any = qe.getAny();
        } else {
            type = null;
            parentID = null;
            action = null;
            quantity = null;
            epcClass = null;
            bizStep = null;
            disposition = null;
            readPoint = null;
            bizLocation = null;
            extension = null;
            any = new ArrayList<Object>();
        }
    }

    public EPCISEventType getEpcisEvent() {
        return epcisEvent;
    }

    public EventType getType() {
        return type;
    }

    public GregorianCalendar getEventTime() {
        return epcisEvent.getEventTime().toGregorianCalendar();
    }

    public GregorianCalendar getRecordTime() {
        return epcisEvent.getRecordTime().toGregorianCalendar();
    }

    public String getEventTimeZoneOffset() {
        return epcisEvent.getEventTimeZoneOffset();
    }

    public ExtensionTypeHelper getBaseExtension() {
        return new ExtensionTypeHelper(epcisEvent.getBaseExtension());
    }

    public ExtensionTypeHelper getExtension() {
        return extension;
    }

    public String getExtension(String name) {
        String res = getBaseExtension().getExtension(name);
        if (res != null) {
            return res;
        }
        res = extension.getExtension(name);
        if (res != null) {
            return res;
        }
        return getAny(name);
    }

    public List<Object> getAny() {
        return any;
    }

    public String getAny(String name) {
        if (any != null) {
            for (Object obj : any) {
                if (obj instanceof Element) {
                    Element el = (Element) obj;
                    if (el.getLocalName().equals(name)) {
                        return el.getTextContent();
                    }
                }
            }
        }
        return null;
    }
    
    public Map<QName, String> getOtherAttributes() {
        return epcisEvent.getOtherAttributes();
    }

    public String getParentID() {
        return parentID;
    }

    public List<String> getChildren() {
        return children;
    }

    public List<String> getEpcList() {
        return epcList;
    }

    public ActionType getAction() {
        return action;
    }

    public String getBizStep() {
        return bizStep;
    }

    public String getBizLocation() {
        return bizLocation;
    }

    public Map<String, String> getBizTransactions() {
        return bizTransactions;
    }

    public String getDisposition() {
        return disposition;
    }

    public String getReadPoint() {
        return readPoint;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getEPCClass() {
        return epcClass;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("   | EPCs:");
        buf.append(getEpcList());
        buf.append("\n");
        buf.append("   | Action:");
        buf.append(getAction());
        buf.append("\n");
        buf.append("   | BizLoc:");
        buf.append(getBizLocation());
        buf.append("\n");
        buf.append("   | BizTrans:");
        buf.append(getBizTransactions());
        buf.append("\n");
        buf.append("   | Children:");
        buf.append(getChildren());
        buf.append("\n");
        buf.append("   | Dispo:");
        buf.append(getDisposition());
        buf.append("\n");
        buf.append("   | Class:");
        buf.append(getEPCClass());
        buf.append("\n");
        buf.append("   | EventTime:");
        buf.append(getEventTime());
        buf.append("\n");
        buf.append("   | InsertedTime:");
        buf.append(getRecordTime());
        buf.append("\n");
        buf.append("   | ParentID:");
        buf.append(getParentID());
        buf.append("\n");
        buf.append("   | Quantity:");
        buf.append(getQuantity());
        buf.append("\n");
        buf.append("   | ReadPoint:");
        buf.append(getReadPoint());
        buf.append("\n");
        buf.append("   | Type:");
        buf.append(getType());
        buf.append("\n");
        return buf.toString();
    }
}
