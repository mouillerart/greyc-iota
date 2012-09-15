/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.eta.callback.filter;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.eta.callback.filter.xacml.EPCISPEP;
import fr.unicaen.iota.xacml.XACMLConstantsEventType;
import fr.unicaen.iota.xacml.pep.ExtensionEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.fosstrak.epcis.repository.model.*;

/**
 * This CallbackCheck class initializes the EPCIS PEP to send XACML request to
 * the XACML module and to receive the results used by the
 * CallbackOperationsModule
 */
public class CallbackCheck {

    /**
     * The EPCIS PEP which sends and receives XACML requests
     */
    private EPCISPEP epcisPEP;

    public CallbackCheck() {
        epcisPEP = new EPCISPEP();
    }

    public boolean xacmlCheck(XACMLEPCISEvent xacmlEvent, String user) {
        int baseEventResponse = epcisPEP.queryEvent(user, xacmlEvent, "Query");
        if (baseEventResponse == Result.DECISION_PERMIT) {
            return true;
        }
        return false;
    }

    /**
     * Checks the callback events by XACML requests to the XACML module.
     *
     * @param baseEventList The callback events.
     * @return
     * <code>true</code> if the callback is permitted.
     */
    public boolean xacmlCheck(List<BaseEvent> baseEventList, String user) {
        Iterator<BaseEvent> iterBaseEvent = baseEventList.iterator();
        boolean decisionPermit = true;
        while (iterBaseEvent.hasNext() && decisionPermit) {
            BaseEvent baseEvent = iterBaseEvent.next();
            // TODO: owner + user
            String owner = null;
            if (baseEvent instanceof AggregationEvent) {
                decisionPermit = aggregationCallbackCheck((AggregationEvent) baseEvent, user, owner);
            } else if (baseEvent instanceof ObjectEvent) {
                decisionPermit = objectCallbackCheck((ObjectEvent) baseEvent, user, owner);
            } else if (baseEvent instanceof QuantityEvent) {
                decisionPermit = quantityCallbackCheck((QuantityEvent) baseEvent, user, owner);
            } else if (baseEvent instanceof TransactionEvent) {
                decisionPermit = transactionCallbackCheck((TransactionEvent) baseEvent, user, owner);
            }
        }
        return decisionPermit;
    }

    /**
     * Checks an aggregation callback event.
     *
     * @param baseEvent The aggregation callback event to check.
     * @return
     * <code>true</code> if the aggregation callback event is permitted.
     */
    private boolean aggregationCallbackCheck(AggregationEvent aggregationEvent, String user, String owner) {
        Date eventTime = aggregationEvent.getEventTime();
        Date recordTime = aggregationEvent.getRecordTime();
        String eventType = XACMLConstantsEventType.AGGREGATION;
        String readPoint = (aggregationEvent.getReadPoint() != null) ? aggregationEvent.getReadPoint().getUri() : null;
        String bizLoc = (aggregationEvent.getBizLocation() != null) ? aggregationEvent.getBizLocation().getUri() : null;
        String bizStep = (aggregationEvent.getBizStep() != null) ? aggregationEvent.getBizStep().getUri() : null;
        String operation = (aggregationEvent.getAction() != null) ? aggregationEvent.getAction().name() : null;
        String disposition = (aggregationEvent.getDisposition() != null) ? aggregationEvent.getDisposition().getUri() : null;
        String parentId = aggregationEvent.getParentId();
        Long quantity = null;
        String epc = null;
        String childEpc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        boolean onePermit = false;

        if (aggregationEvent.getChildEpcs() == null || aggregationEvent.getChildEpcs().isEmpty()) {
            if (aggregationEvent.getBizTransList() == null || aggregationEvent.getBizTransList().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (xacmlCheck(xacmlEvent, user)) {
                    onePermit = true;
                }
            } else {
                Iterator<BusinessTransaction> iterBizTrans = aggregationEvent.getBizTransList().iterator();
                while (iterBizTrans.hasNext()) {
                    bizTrans = iterBizTrans.next().getBizTransaction().getUri();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterBizTrans.remove();
                    }
                }
            }
        } else {
            if (aggregationEvent.getBizTransList() == null || aggregationEvent.getBizTransList().isEmpty()) {
                Iterator<String> iterChildEpc = aggregationEvent.getChildEpcs().iterator();
                while (iterChildEpc.hasNext()) {
                    childEpc = iterChildEpc.next();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterChildEpc.remove();
                    }
                }
            } else {
                String allowedChildEpc = null;
                String allowedBizTrans = null;
                Iterator<String> iterChildEpc = aggregationEvent.getChildEpcs().iterator();
                while (iterChildEpc.hasNext()) {
                    childEpc = iterChildEpc.next();
                    Iterator<BusinessTransaction> iterBizTrans = aggregationEvent.getBizTransList().iterator();
                    while (iterBizTrans.hasNext()) {
                        bizTrans = iterBizTrans.next().getBizTransaction().getUri();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (xacmlCheck(xacmlEvent, user)) {
                            allowedChildEpc = childEpc;
                            allowedBizTrans = bizTrans;
                            onePermit = true;
                            break;
                        }
                    }
                    if (allowedChildEpc == null) {
                        iterChildEpc.remove();
                    } else {
                        break;
                    }
                }
                if (allowedChildEpc != null) {
                    Iterator<BusinessTransaction> iterBizTrans = aggregationEvent.getBizTransList().iterator();
                    while (iterBizTrans.hasNext()) {
                        bizTrans = iterBizTrans.next().getBizTransaction().getUri();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (xacmlCheck(xacmlEvent, user)) {
                            iterBizTrans.remove();
                        }
                    }
                }
                if (allowedBizTrans != null) {
                    iterChildEpc = aggregationEvent.getChildEpcs().iterator();
                    while (iterChildEpc.hasNext()) {
                        childEpc = iterChildEpc.next();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (!xacmlCheck(xacmlEvent, user)) {
                            iterChildEpc.remove();
                        }
                    }
                }
            }
        }
        if (onePermit) {
            bizTrans = null;
            childEpc = null;
            Iterator<EventFieldExtension> iter = aggregationEvent.getExtensions().iterator();
            while (iter.hasNext()) {
                EventFieldExtension extensionField = iter.next();
                String extensionId = extensionField.getFieldname();
                String extensionValueType = extensionField.getValueColumnName();
                Object extensionValue = null;
                try {
                    if ("intValue".equals(extensionValueType)) {
                        extensionValue = new Integer(extensionField.getIntValue());
                    } else if ("floatValue".equals(extensionValueType)) {
                        extensionValue = new Float(extensionField.getFloatValue());
                    } else if ("dateValue".equals(extensionValueType)) {
                        extensionValue = new Date(extensionField.getDateValue().getTime());
                    } else if ("strValue".equals(extensionValueType)) {
                        extensionValue = extensionField.getStrValue();
                    } else {
                        iter.remove();
                        continue;
                    }
                } catch (Exception ex) {
                    iter.remove();
                    continue;
                }
                extension = new ExtensionEvent(extensionId, extensionValue);
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (!xacmlCheck(xacmlEvent, user)) {
                    iter.remove();
                }
            }
        }
        return onePermit;
    }

    /**
     * Checks an object callback event.
     *
     * @param objectEvent The object callback event to check.
     * @return
     * <code>true</code> if the object callback event is permitted.
     */
    private boolean objectCallbackCheck(ObjectEvent objectEvent, String user, String owner) {
        String readPoint = (objectEvent.getReadPoint() != null) ? objectEvent.getReadPoint().getUri() : null;
        String bizLoc = (objectEvent.getBizLocation() != null) ? objectEvent.getBizLocation().getUri() : null;
        String bizStep = (objectEvent.getBizStep() != null) ? objectEvent.getBizStep().getUri() : null;
        Date eventTime = objectEvent.getEventTime();
        Date recordTime = objectEvent.getRecordTime();
        String operation = (objectEvent.getAction() != null) ? objectEvent.getAction().name() : null;
        String eventType = XACMLConstantsEventType.OBJECT;
        String disposition = (objectEvent.getDisposition() != null) ? objectEvent.getDisposition().getUri() : null;
        String parentId = null;
        String childEpc = null;
        Long quantity = null;
        String epc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        boolean onePermit = false;

        if (objectEvent.getEpcList() == null || objectEvent.getEpcList().isEmpty()) {
            if (objectEvent.getBizTransList() == null || objectEvent.getBizTransList().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (xacmlCheck(xacmlEvent, user)) {
                    onePermit = true;
                }
            } else {
                Iterator<BusinessTransaction> iterBizTrans = objectEvent.getBizTransList().iterator();
                while (iterBizTrans.hasNext()) {
                    bizTrans = iterBizTrans.next().getBizTransaction().getUri();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterBizTrans.remove();
                    }
                }
            }
        } else {
            if (objectEvent.getBizTransList() == null || objectEvent.getBizTransList().isEmpty()) {
                Iterator<String> iterEpc = objectEvent.getEpcList().iterator();
                while (iterEpc.hasNext()) {
                    epc = iterEpc.next();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterEpc.remove();
                    }
                }
            } else {
                String allowedEpc = null;
                String allowedBizTrans = null;
                Iterator<String> iterEpc = objectEvent.getEpcList().iterator();
                while (iterEpc.hasNext()) {
                    epc = iterEpc.next();
                    Iterator<BusinessTransaction> iterBizTrans = objectEvent.getBizTransList().iterator();
                    while (iterBizTrans.hasNext()) {
                        bizTrans = iterBizTrans.next().getBizTransaction().getUri();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (xacmlCheck(xacmlEvent, user)) {
                            allowedEpc = epc;
                            allowedBizTrans = bizTrans;
                            onePermit = true;
                            break;
                        }
                    }
                    if (allowedEpc == null) {
                        iterEpc.remove();
                    } else {
                        break;
                    }
                }
                if (allowedEpc != null) {
                    Iterator<BusinessTransaction> iterBizTrans = objectEvent.getBizTransList().iterator();
                    while (iterBizTrans.hasNext()) {
                        bizTrans = iterBizTrans.next().getBizTransaction().getUri();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (!xacmlCheck(xacmlEvent, user)) {
                            iterBizTrans.remove();
                        }
                    }
                }
                if (allowedBizTrans != null) {
                    iterEpc = objectEvent.getEpcList().iterator();
                    while (iterEpc.hasNext()) {
                        epc = iterEpc.next();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (!xacmlCheck(xacmlEvent, user)) {
                            iterEpc.remove();
                        }
                    }
                }
            }
        }
        if (onePermit) {
            epc = null;
            bizTrans = null;
            Iterator<EventFieldExtension> iter = objectEvent.getExtensions().iterator();
            while (iter.hasNext()) {
                EventFieldExtension extensionField = iter.next();
                String extensionId = extensionField.getFieldname();
                String extensionValueType = extensionField.getValueColumnName();
                Object extensionValue = null;
                try {
                    if ("intValue".equals(extensionValueType)) {
                        extensionValue = new Integer(extensionField.getIntValue());
                    } else if ("floatValue".equals(extensionValueType)) {
                        extensionValue = new Float(extensionField.getFloatValue());
                    } else if ("dateValue".equals(extensionValueType)) {
                        extensionValue = new Date(extensionField.getDateValue().getTime());
                    } else if ("strValue".equals(extensionValueType)) {
                        extensionValue = extensionField.getStrValue();
                    } else {
                        iter.remove();
                        continue;
                    }
                } catch (Exception ex) {
                    iter.remove();
                    continue;
                }
                extension = new ExtensionEvent(extensionId, extensionValue);
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (!xacmlCheck(xacmlEvent, user)) {
                    iter.remove();
                }
            }
        }
        return onePermit;
    }

    /**
     * Checks an quantity callback event.
     *
     * @param quantityEvent The quantity callback event to check.
     * @return
     * <code>true</code> if the quantity callback event is permitted.
     */
    private boolean quantityCallbackCheck(QuantityEvent quantityEvent, String user, String owner) {
        String readPoint = (quantityEvent.getReadPoint() != null) ? quantityEvent.getReadPoint().getUri() : null;
        String bizLoc = (quantityEvent.getBizLocation() != null) ? quantityEvent.getBizLocation().getUri() : null;
        String bizStep = (quantityEvent.getBizStep() != null) ? quantityEvent.getBizStep().getUri() : null;
        Date eventTime = quantityEvent.getEventTime();
        Date recordTime = quantityEvent.getRecordTime();
        String eventType = XACMLConstantsEventType.QUANTITY;
        String disposition = (quantityEvent.getDisposition() != null) ? quantityEvent.getDisposition().getUri() : null;
        Long quantity = quantityEvent.getQuantity();
        String epc = (quantityEvent.getEpcClass() != null) ? quantityEvent.getEpcClass().getUri() : null;
        String operation = null;
        String parentId = null;
        String childEpc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        boolean onePermit = false;

        Iterator<BusinessTransaction> iterBizTrans = quantityEvent.getBizTransList().iterator();
        while (iterBizTrans.hasNext()) {
            bizTrans = iterBizTrans.next().getType().getUri();
            XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                    eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
            if (xacmlCheck(xacmlEvent, user)) {
                onePermit = true;
            } else {
                iterBizTrans.remove();
            }
        }
        if (onePermit) {
            bizTrans = null;
            Iterator<EventFieldExtension> iter = quantityEvent.getExtensions().iterator();
            while (iter.hasNext()) {
                EventFieldExtension extensionField = iter.next();
                String extensionId = extensionField.getFieldname();
                String extensionValueType = extensionField.getValueColumnName();
                Object extensionValue = null;
                try {
                    if ("intValue".equals(extensionValueType)) {
                        extensionValue = new Integer(extensionField.getIntValue());
                    } else if ("floatValue".equals(extensionValueType)) {
                        extensionValue = new Float(extensionField.getFloatValue());
                    } else if ("dateValue".equals(extensionValueType)) {
                        extensionValue = new Date(extensionField.getDateValue().getTime());
                    } else if ("strValue".equals(extensionValueType)) {
                        extensionValue = extensionField.getStrValue();
                    } else {
                        iter.remove();
                        continue;
                    }
                } catch (Exception ex) {
                    iter.remove();
                    continue;
                }
                extension = new ExtensionEvent(extensionId, extensionValue);
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (!xacmlCheck(xacmlEvent, user)) {
                    iter.remove();
                }
            }
        }
        return onePermit;
    }

    /**
     * Checks a transaction callback event.
     *
     * @param transactionEvent The transaction callback event to check.
     * @return
     * <code>true</code> if the transaction callback event is permitted.
     */
    private boolean transactionCallbackCheck(TransactionEvent transactionEvent, String user, String owner) {
        String readPoint = (transactionEvent.getReadPoint() != null) ? transactionEvent.getReadPoint().getUri() : null;
        String bizLoc = (transactionEvent.getBizLocation() != null) ? transactionEvent.getBizLocation().getUri() : null;
        String bizStep = (transactionEvent.getBizStep() != null) ? transactionEvent.getBizStep().getUri() : null;
        Date eventTime = transactionEvent.getEventTime();
        Date recordTime = transactionEvent.getRecordTime();
        String operation = (transactionEvent.getAction() != null) ? transactionEvent.getAction().name() : null;
        String eventType = XACMLConstantsEventType.TRANSACTION;
        String disposition = (transactionEvent.getDisposition() != null) ? transactionEvent.getDisposition().getUri() : null;
        String parentId = transactionEvent.getParentId();
        Long quantity = null;
        String childEpc = null;
        String epc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        boolean onePermit = false;

        if (transactionEvent.getEpcList() == null || transactionEvent.getEpcList().isEmpty()) {
            if (transactionEvent.getBizTransList() == null || transactionEvent.getBizTransList().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (xacmlCheck(xacmlEvent, user)) {
                    onePermit = true;
                }
            } else {
                Iterator<BusinessTransaction> iterBizTrans = transactionEvent.getBizTransList().iterator();
                while (iterBizTrans.hasNext()) {
                    bizTrans = iterBizTrans.next().getBizTransaction().getUri();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterBizTrans.remove();
                    }
                }
            }
        } else {
            if (transactionEvent.getBizTransList() == null || transactionEvent.getBizTransList().isEmpty()) {
                Iterator<String> iterEpc = transactionEvent.getEpcList().iterator();
                while (iterEpc.hasNext()) {
                    epc = iterEpc.next();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterEpc.remove();
                    }
                }
            } else {
                String allowedEpc = null;
                String allowedBizTrans = null;
                Iterator<String> iterEpc = transactionEvent.getEpcList().iterator();
                while (iterEpc.hasNext()) {
                    epc = iterEpc.next();
                    Iterator<BusinessTransaction> iterBizTrans = transactionEvent.getBizTransList().iterator();
                    while (iterBizTrans.hasNext()) {
                        bizTrans = iterBizTrans.next().getType().getUri();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (xacmlCheck(xacmlEvent, user)) {
                            allowedEpc = epc;
                            allowedBizTrans = bizTrans;
                            onePermit = true;
                            break;
                        }
                    }
                    if (allowedEpc == null) {
                        iterEpc.remove();
                    } else {
                        break;
                    }
                }
                if (allowedEpc != null) {
                    Iterator<BusinessTransaction> iterBizTrans = transactionEvent.getBizTransList().iterator();
                    while (iterBizTrans.hasNext()) {
                        bizTrans = iterBizTrans.next().getType().getUri();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (!xacmlCheck(xacmlEvent, user)) {
                            iterBizTrans.remove();
                        }
                    }
                }
                if (allowedBizTrans != null) {
                    iterEpc = transactionEvent.getEpcList().iterator();
                    epc = iterEpc.next();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        iterEpc.remove();
                    }
                }
            }
        }
        if (onePermit) {
            epc = null;
            bizTrans = null;
            Iterator<EventFieldExtension> iter = transactionEvent.getExtensions().iterator();
            while (iter.hasNext()) {
                EventFieldExtension extensionField = iter.next();
                String extensionId = extensionField.getFieldname();
                String extensionValueType = extensionField.getValueColumnName();
                Object extensionValue = null;
                try {
                    if ("intValue".equals(extensionValueType)) {
                        extensionValue = new Integer(extensionField.getIntValue());
                    } else if ("floatValue".equals(extensionValueType)) {
                        extensionValue = new Float(extensionField.getFloatValue());
                    } else if ("dateValue".equals(extensionValueType)) {
                        extensionValue = new Date(extensionField.getDateValue().getTime());
                    } else if ("strValue".equals(extensionValueType)) {
                        extensionValue = extensionField.getStrValue();
                    } else {
                        iter.remove();
                        continue;
                    }
                } catch (Exception ex) {
                    iter.remove();
                    continue;
                }
                extension = new ExtensionEvent(extensionId, extensionValue);
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (!xacmlCheck(xacmlEvent, user)) {
                    iter.remove();
                }
            }
        }
        return onePermit;
    }
}
