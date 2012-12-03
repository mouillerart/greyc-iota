/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.xacml.pep;

import java.util.Date;

public class XACMLEPCISEvent {

    private final String owner;
    private final String bizStep;
    private final String epc;
    private final Date eventTime;
    private final Date recordTime;
    private final String operation;
    private final String eventType;
    private final String parentId;
    private final String childEpc;
    private final Long quantity;
    private final String readPoint;
    private final String bizLoc;
    private final String bizTrans;
    private final String disposition;
    private final ExtensionEvent extension;

    public XACMLEPCISEvent(String owner, String bizStep, String epc, Date eventTime,
            Date recordTime, String operation, String eventType, String parentId,
            String childEpc, Long quantity, String readPoint, String bizLoc, String bizTrans,
            String disposition, ExtensionEvent extension) {
        this.owner = owner;
        this.bizStep = bizStep;
        this.epc = epc;
        this.eventTime = eventTime;
        this.recordTime = recordTime;
        this.operation = operation;
        this.eventType = eventType;
        this.parentId = parentId;
        this.childEpc = childEpc;
        this.quantity = quantity;
        this.readPoint = readPoint;
        this.bizLoc = bizLoc;
        this.bizTrans = bizTrans;
        this.disposition = disposition;
        this.extension = extension;
    }

    public String getBizStep() {
        return bizStep;
    }

    public String getEpc() {
        return epc;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public String getOperation() {
        return operation;
    }

    public String getEventType() {
        return eventType;
    }

    public String getParentId() {
        return parentId;
    }

    public String getChildEpc() {
        return childEpc;
    }

    public Long getQuantity() {
        return quantity;
    }

    public String getReadPoint() {
        return readPoint;
    }

    public String getBizLoc() {
        return bizLoc;
    }
    // TODO biztrans type

    public String getBizTrans() {
        return bizTrans;
    }

    public String getDisposition() {
        return disposition;
    }

    public String getOwner() {
        return owner;
    }

    public ExtensionEvent getExtension() {
        return extension;
    }
}
