/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.application.model;

import java.io.Serializable;
import java.util.*;

public class EPCISEvent implements Serializable {

    public static enum EventType {

        OBJECT,
        TRANSACTION,
        AGGREGATION,
        QUANTITY
    }

    public static enum ActionType {

        ADD,
        OBSERVE,
        DELETE
    }
    private EventType type;
    private Calendar eventTime;
    private Calendar insertedTime;
    private String parentID = "";
    private ActionType action;
    private String bizStep = "";
    private String bizLoc = "";
    private String disposition = "";
    private String readPoint = "";
    private String quantity = "";
    private String EPCClass = "";
    private List<String> children = new ArrayList<String>();
    private List<String> epcs = new ArrayList<String>();
    private Map<String, String> bizTrans = new HashMap<String, String>();

    public EPCISEvent() {
        super();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (this == that) {
            return true;
        }
        if (that instanceof EPCISEvent) {
            EPCISEvent e = (EPCISEvent) that;
            return type.equals(e.getType())
                    && eventTime.equals(e.getEventTime())
                    && insertedTime.equals(e.getInsertedTime())
                    && parentID.equals(e.getParentID())
                    && action.equals(e.getAction())
                    && bizStep.equals(e.getBizStep())
                    && bizLoc.equals(e.getBizLoc())
                    && disposition.equals(e.getDisposition())
                    && readPoint.equals(e.getReadPoint())
                    && quantity.equals(e.getQuantity())
                    && EPCClass.equals(e.getEPCClass())
                    && children.equals(e.getChildren()) // List/Map.equals apply
                    && epcs.equals(e.getEpcs()) // equals to their elements
                    && bizTrans.equals(e.getBizTrans());
        }
        return false;

    }

    public boolean isExpedition() {
        // TODO: hard value
        return bizStep.equals("urn:orange:demo:bizstep:fmcg:expedition");
    }

    public boolean isReception() {
        // TODO: hard value
        return bizStep.equals("urn:orange:demo:bizstep:fmcg:reception");
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Calendar getEventTime() {
        return eventTime;
    }

    public void setEventTime(Calendar eventTime) {
        this.eventTime = eventTime;
    }

    public Calendar getInsertedTime() {
        return insertedTime;
    }

    public void setInsertedTime(Calendar insertedTime) {
        this.insertedTime = insertedTime;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(Collection<String> childs) {
        this.children.clear();
        this.children.addAll(childs);
    }

    public List<String> getEpcs() {
        return epcs;
    }

    public void setEpcs(Collection<String> epcs) {
        this.epcs.clear();
        this.epcs.addAll(epcs);
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getBizStep() {
        return bizStep;
    }

    public void setBizStep(String bizStep) {
        this.bizStep = bizStep;
    }

    public String getBizLoc() {
        return bizLoc;
    }

    public void setBizLoc(String bizLoc) {
        this.bizLoc = bizLoc;
    }

    public Map<String, String> getBizTrans() {
        return bizTrans;
    }

    public void setBizTrans(Map<String, String> bizTrans) {
        this.bizTrans = bizTrans;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getReadPoint() {
        return readPoint;
    }

    public void setReadPoint(String readPoint) {
        this.readPoint = readPoint;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getEPCClass() {
        return EPCClass;
    }

    public void setEPCClass(String class1) {
        EPCClass = class1;
    }

    public boolean isSameProduct(EPCISEvent event) {
        return epcs.equals(event.getEpcs()); // List.equals applique equals en profondeur
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("   | EPCs:");
        buf.append(getEpcs());
        buf.append("\n");
        buf.append("   | Action:");
        buf.append(getAction());
        buf.append("\n");
        buf.append("   | BizLoc:");
        buf.append(getBizLoc());
        buf.append("\n");
        buf.append("   | BizTrans:");
        buf.append(getBizTrans());
        buf.append("\n");
        buf.append("   | Childs:");
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
        buf.append(getInsertedTime());
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
