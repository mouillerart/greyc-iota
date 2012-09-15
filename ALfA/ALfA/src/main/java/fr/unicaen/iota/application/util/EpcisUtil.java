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
package fr.unicaen.iota.application.util;

import fr.unicaen.iota.application.model.EPCISEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;

/**
 *
 */
public final class EpcisUtil {

    private EpcisUtil() {
    }
    private static final Log log = LogFactory.getLog(EpcisUtil.class);

    private static EPCISEvent.ActionType getActionType(ActionType at) {
        if (at == ActionType.ADD) {
            return EPCISEvent.ActionType.ADD;
        } else if (at == ActionType.DELETE) {
            return EPCISEvent.ActionType.DELETE;
        } else if (at == ActionType.OBSERVE) {
            return EPCISEvent.ActionType.OBSERVE;
        } else {
            return null;
        }
    }
    
    /* EPCISEventType are generated, the Visitor design pattern not appliable */
    public static EPCISEvent processEvent(EPCISEventType event) {
        EPCISEvent epcevent = new EPCISEvent();
        XMLGregorianCalendar eventTime = event.getEventTime();
        epcevent.setEventTime(eventTime.toGregorianCalendar());
        XMLGregorianCalendar recordTime = event.getRecordTime();
        epcevent.setInsertedTime(recordTime.toGregorianCalendar());
        if (event instanceof ObjectEventType) {
            ObjectEventType e = (ObjectEventType) event;
            epcevent.setType(EPCISEvent.EventType.OBJECT);
            List<String> list = new ArrayList<String>();
            for (EPC epc : e.getEpcList().getEpc()) {
                list.add(epc.getValue());
            }
            epcevent.setEpcs(list);
            epcevent.setAction(getActionType(e.getAction()));
            epcevent.setBizStep(e.getBizStep().toString());
            epcevent.setDisposition(e.getDisposition().toString());
            if (e.getReadPoint() != null) {
                epcevent.setReadPoint(e.getReadPoint().getId().toString());
            }
            if (e.getBizLocation() != null) {
                epcevent.setBizLoc(e.getBizLocation().getId().toString());
            }
            if (e.getBizTransactionList() != null) {
                Map<String, String> map = new HashMap<String, String>();
                for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                    map.put(bizTrans.getType().toString(), bizTrans.toString());
                }
                epcevent.setBizTrans(map);
            }
        } else if (event instanceof TransactionEventType) {
            TransactionEventType e = (TransactionEventType) event;
            epcevent.setType(EPCISEvent.EventType.TRANSACTION);
            epcevent.setParentID(e.getParentID().toString());
            List<String> list = new ArrayList<String>();
            for (EPC epc : e.getEpcList().getEpc()) {
                list.add(epc.getValue());
            }
            epcevent.setEpcs(list);
            epcevent.setAction(getActionType(e.getAction()));
            epcevent.setBizStep(e.getBizStep().toString());
            epcevent.setDisposition(e.getDisposition().toString());
            if (e.getReadPoint() != null) {
                epcevent.setReadPoint(e.getReadPoint().getId().toString());
            }
            if (e.getBizLocation() != null) {
                epcevent.setBizLoc(e.getBizLocation().getId().toString());
            }
            if (e.getBizTransactionList() != null) {
                Map<String, String> map = new HashMap<String, String>();
                for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                    map.put(bizTrans.getType().toString(), bizTrans.toString());
                }
                epcevent.setBizTrans(map);
            }
        } else if (event instanceof AggregationEventType) {
            AggregationEventType e = (AggregationEventType) event;
            log.trace(e.getParentID().toString());
            epcevent.setType(EPCISEvent.EventType.AGGREGATION);
            epcevent.setParentID(e.getParentID().toString());
            List<String> list = new ArrayList<String>();
            for (EPC epc : e.getChildEPCs().getEpc()) {
                list.add(epc.getValue());
            }
            epcevent.setChildren(list);
            epcevent.setAction(getActionType(e.getAction()));
            epcevent.setBizStep(e.getBizStep().toString());
            epcevent.setDisposition(e.getDisposition().toString());
            if (e.getReadPoint() != null) {
                epcevent.setReadPoint(e.getReadPoint().getId().toString());
            }
            if (e.getBizLocation() != null) {
                epcevent.setBizLoc(e.getBizLocation().getId().toString());
            }
            if (e.getBizTransactionList() != null) {
                Map<String, String> map = new HashMap<String, String>();
                for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                    map.put(bizTrans.getType().toString(), bizTrans.toString());
                }
                epcevent.setBizTrans(map);
            }
        } else if (event instanceof QuantityEventType) {
            QuantityEventType e = (QuantityEventType) event;
            epcevent.setType(EPCISEvent.EventType.QUANTITY);
            epcevent.setQuantity(String.valueOf(Integer.valueOf(e.getQuantity())));
            epcevent.setEPCClass(e.getEpcClass().toString());
            epcevent.setBizStep(e.getBizStep().toString());
            epcevent.setDisposition(e.getDisposition().toString());
            if (e.getReadPoint() != null) {
                epcevent.setReadPoint(e.getReadPoint().getId().toString());
            }
            if (e.getBizLocation() != null) {
                epcevent.setBizLoc(e.getBizLocation().getId().toString());
            }
            if (e.getBizTransactionList() != null) {
                Map<String, String> map = new HashMap<String, String>();
                for (BusinessTransactionType bizTrans : e.getBizTransactionList().getBizTransaction()) {
                    map.put(bizTrans.getType().toString(), bizTrans.toString());
                }
                epcevent.setBizTrans(map);
            }
        }
        return epcevent;
    }
}
