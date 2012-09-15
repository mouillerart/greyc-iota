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
package fr.unicaen.iota.eta.capture;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.eta.xacml.EPCISPEP;
import fr.unicaen.iota.xacml.XACMLConstantsEventType;
import fr.unicaen.iota.xacml.pep.ExtensionEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISMasterData;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.fosstrak.epcis.model.VocabularyElementType;
import org.fosstrak.epcis.model.VocabularyType;
import org.fosstrak.epcis.repository.model.*;

/**
 * This
 * <code>CaptureCheck</code> class initializes the EPCIS PEP to send XACML
 * request to the XACML module and to receive the results used by the
 * <code>CaptureOperationsModule</code>.
 */
public class CaptureCheck {

    /**
     * The EPCIS PEP which sends and receives XACML requests.
     */
    private EPCISPEP epcisPEP;

    public CaptureCheck() {
        epcisPEP = new EPCISPEP();
    }

    /**
     * Checks the capture events by XACML requests to the XACML module.
     *
     * @param baseEventList The capture events.
     * @return
     * <code>true</code> if the capture is permitted.
     */
    public boolean xacmlCheck(List<BaseEvent> baseEventList, String user, String owner) {
        Iterator<BaseEvent> iterBaseEvent = baseEventList.iterator();
        boolean onePermit = true;
        while (iterBaseEvent.hasNext() && onePermit) {
            BaseEvent baseEvent = iterBaseEvent.next();
            if (baseEvent instanceof AggregationEvent) {
                onePermit = aggregationCaptureCheck((AggregationEvent) baseEvent, user, owner);
            } else if (baseEvent instanceof ObjectEvent) {
                onePermit = objectCaptureCheck((ObjectEvent) baseEvent, user, owner);
            } else if (baseEvent instanceof QuantityEvent) {
                onePermit = quantityCaptureCheck((QuantityEvent) baseEvent, user, owner);
            } else if (baseEvent instanceof TransactionEvent) {
                onePermit = transactionCaptureCheck((TransactionEvent) baseEvent, user, owner);
            }
        }
        return onePermit;
    }

    /**
     * Checks an aggregation capture event.
     *
     * @param baseEvent The aggregation capture event to check.
     * @return
     * <code>true</code> if the aggregation capture event is permitted.
     */
    private boolean aggregationCaptureCheck(AggregationEvent aggregationEvent, String user, String owner) {
        Date eventTime = aggregationEvent.getEventTime();
        Date recordTime = new Date();
        String eventType = XACMLConstantsEventType.AGGREGATION;
        String operation = (aggregationEvent.getAction() != null) ? aggregationEvent.getAction().name() : null;
        String readPoint = (aggregationEvent.getReadPoint() != null) ? aggregationEvent.getReadPoint().getUri() : null;
        String bizLoc = (aggregationEvent.getBizLocation() != null) ? aggregationEvent.getBizLocation().getUri() : null;
        String bizStep = (aggregationEvent.getBizStep() != null) ? aggregationEvent.getBizStep().getUri() : null;
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
                        if (!xacmlCheck(xacmlEvent, user)) {
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
        if (onePermit && aggregationEvent.getExtensions() != null) {
            bizTrans = null;
            childEpc = null;
            Iterator<EventFieldExtension> iter = aggregationEvent.getExtensions().iterator();
            while (iter.hasNext()) {
                EventFieldExtension extensionField = iter.next();
                String extensionId = extensionField.getFieldname();
                String extensionValueType = extensionField.getValueColumnName();
                Object extensionValue;
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
     * Checks an object capture event.
     *
     * @param objectEvent The object capture event to check.
     * @return
     * <code>true</code> if the object capture event is permitted.
     */
    private boolean objectCaptureCheck(ObjectEvent objectEvent, String user, String owner) {
        Date eventTime = objectEvent.getEventTime();
        Date recordTime = new Date();
        String eventType = XACMLConstantsEventType.OBJECT;
        String operation = (objectEvent.getAction() != null) ? objectEvent.getAction().name() : null;
        String readPoint = (objectEvent.getReadPoint() != null) ? objectEvent.getReadPoint().getUri() : null;
        String bizLoc = (objectEvent.getBizLocation() != null) ? objectEvent.getBizLocation().getUri() : null;
        String bizStep = (objectEvent.getBizStep() != null) ? objectEvent.getBizStep().getUri() : null;
        String disposition = (objectEvent.getDisposition() != null) ? objectEvent.getDisposition().getUri() : null;
        Long quantity = null;
        String parentId = null;
        String childEpc = null;
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
        if (onePermit && objectEvent.getExtensions() != null) {
            epc = null;
            bizTrans = null;
            Iterator<EventFieldExtension> iter = objectEvent.getExtensions().iterator();
            while (iter.hasNext()) {
                EventFieldExtension extensionField = iter.next();
                String extensionId = extensionField.getFieldname();
                String extensionValueType = extensionField.getValueColumnName();
                Object extensionValue;
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
     * Checks an quantity capture event.
     *
     * @param quantityEvent The quantity capture event to check.
     * @return
     * <code>true</code> if the quantity capture event is permitted.
     */
    private boolean quantityCaptureCheck(QuantityEvent quantityEvent, String user, String owner) {
        Date eventTime = quantityEvent.getEventTime();
        Date recordTime = new Date();
        String eventType = XACMLConstantsEventType.QUANTITY;
        String readPoint = (quantityEvent.getReadPoint() != null) ? quantityEvent.getReadPoint().getUri() : null;
        String bizLoc = (quantityEvent.getBizLocation() != null) ? quantityEvent.getBizLocation().getUri() : null;
        String bizStep = (quantityEvent.getBizStep() != null) ? quantityEvent.getBizStep().getUri() : null;
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
        if (onePermit && quantityEvent.getExtensions() != null) {
            bizTrans = null;
            Iterator<EventFieldExtension> iter = quantityEvent.getExtensions().iterator();
            while (iter.hasNext()) {
                EventFieldExtension extensionField = iter.next();
                String extensionId = extensionField.getFieldname();
                String extensionValueType = extensionField.getValueColumnName();
                Object extensionValue;
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
     * Checks a transaction capture event.
     *
     * @param transactionEvent The transaction capture event to check.
     * @return
     * <code>true</code> if the transaction capture event is permitted.
     */
    private boolean transactionCaptureCheck(TransactionEvent transactionEvent, String user, String owner) {
        Date eventTime = transactionEvent.getEventTime();
        Date recordTime = new Date();
        String eventType = XACMLConstantsEventType.TRANSACTION;
        String operation = (transactionEvent.getAction() != null) ? transactionEvent.getAction().name() : null;
        String readPoint = (transactionEvent.getReadPoint() != null) ? transactionEvent.getReadPoint().getUri() : null;
        String bizLoc = (transactionEvent.getBizLocation() != null) ? transactionEvent.getBizLocation().getUri() : null;
        String bizStep = (transactionEvent.getBizStep() != null) ? transactionEvent.getBizStep().getUri() : null;
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
        if (onePermit && transactionEvent.getExtensions() != null) {
            epc = null;
            bizTrans = null;
            Iterator<EventFieldExtension> iter = transactionEvent.getExtensions().iterator();
            while (iter.hasNext()) {
                EventFieldExtension extensionField = iter.next();
                String extensionId = extensionField.getFieldname();
                String extensionValueType = extensionField.getValueColumnName();
                Object extensionValue;
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
     * Checks an EPCIS Event by querying XACML.
     *
     * @param xacmlEvent The EPCIS event to check
     * @param user The user name
     * @return
     * <code>true</code> if permitted.
     */
    public boolean xacmlCheck(XACMLEPCISEvent xacmlEvent, String user) {
        int baseEventResponse = epcisPEP.captureEvent(user, xacmlEvent, "Capture");
        if (baseEventResponse == Result.DECISION_PERMIT) {
            return true;
        }
        return false;
    }

    /**
     * Filters the list of master data.
     *
     * @param vocList The list of master data to filter.
     * @param user The user name to check.
     * @param owner The owner to check.
     * @return
     * <code>true</code> if permitted.
     */
    public boolean xacmlCheckMasterD(List<VocabularyType> vocList, String user, String owner) {
        boolean onePermit = false;
        for (VocabularyType voc : vocList) {
            if (xacmlCheckMasterDType(voc.getVocabularyElementList().getVocabularyElement(), user, owner)) {
                onePermit = true;
            }
        }
        return onePermit;
    }

    /**
     * Filters the list of master data, by element type.
     *
     * @param vocElList The list of master data to filter.
     * @param user The user name to check
     * @param owner The owner to check.
     * @return
     * <code>true</code> if permitted.
     */
    private boolean xacmlCheckMasterDType(List<VocabularyElementType> vocElList, String user, String owner) {
        Iterator<VocabularyElementType> iterVoc = vocElList.iterator();
        boolean onePermit = false;
        while (iterVoc.hasNext()) {
            VocabularyElementType vocEl = iterVoc.next();
            String id = vocEl.getId();
            XACMLEPCISMasterData xacmlMasterData = new XACMLEPCISMasterData(owner, id);
            if (!xacmlCheckMasterData(xacmlMasterData, user)) {
                iterVoc.remove();
            } else {
                onePermit = true;
            }
        }
        return onePermit;
    }

    /**
     * Checks Master Data by querying XACML.
     *
     * @param xacmlMasterData The Master Data to check.
     * @param user The user name to check.
     * @return
     * <code>true</code> if permitted.
     */
    private boolean xacmlCheckMasterData(XACMLEPCISMasterData xacmlMasterData, String user) {
        int xacmlResponse = epcisPEP.captureMasterData(user, xacmlMasterData, "Capture");
        if (xacmlResponse == Result.DECISION_PERMIT) {
            return true;
        }
        return false;
    }
}
