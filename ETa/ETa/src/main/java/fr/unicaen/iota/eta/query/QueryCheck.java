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
package fr.unicaen.iota.eta.query;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.eta.xacml.EPCISPEP;
import fr.unicaen.iota.xacml.XACMLConstantsEventType;
import fr.unicaen.iota.xacml.pep.ExtensionEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISMasterData;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.utils.TimeParser;

/**
 * This QueryCheck class initializes the EPCIS PEP to send XACML request to the
 * XACML module and to receive the results used by the QueryModule
 */
public class QueryCheck {

    /**
     * The EPCIS PEP which sends and receives XACML requests
     */
    private final EPCISPEP epcisPEP;
    private static final Log LOG = LogFactory.getLog(QueryOperationsModule.class);

    public QueryCheck() {
        epcisPEP = new EPCISPEP();
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
        int xacmlResponse = epcisPEP.queryEvent(user, xacmlEvent, "Query");
        return xacmlResponse == Result.DECISION_PERMIT;
    }

    /**
     * Filters the list of events.
     *
     * @param objects The list of events to filter.
     * @param owner The owner to check.
     * @param user The user name to check.
     * @return The filtered list.
     * @throws ImplementationExceptionResponse If an unknown event type is
     * encountered.
     */
    public List<Object> xacmlCheck(List<Object> objects, String user, String owner)
            throws ImplementationExceptionResponse {
        Iterator<Object> iterObject = objects.iterator();
        while (iterObject.hasNext()) {
            Object result = iterObject.next();

            if (result instanceof JAXBElement<?>) {
                result = ((JAXBElement<?>) result).getValue();
            }

            if (result instanceof ObjectEventType) {
                // Remove the result if denied for all
                if (!filterObjectEventType((ObjectEventType) result, user, owner)) {
                    iterObject.remove();
                }
            } else if (result instanceof AggregationEventType) {
                // Remove the result if denied for all
                if (!filterAggregationEventType((AggregationEventType) result, user, owner)) {
                    iterObject.remove();
                }
            } else if (result instanceof QuantityEventType) {
                // Remove the result if denied
                if (!filterQuantityEventType((QuantityEventType) result, user, owner)) {
                    iterObject.remove();
                }
            } else if (result instanceof TransactionEventType) {
                // Remove the result if denied for all
                if (!filterTransactionEventType((TransactionEventType) result, user, owner)) {
                    iterObject.remove();
                }
            } else {
                String msg = "Unknown event type: " + result.getClass().getName();
                LOG.error(msg);
                ImplementationException ie = new ImplementationException();
                ie.setReason(msg);
                throw new ImplementationExceptionResponse(msg, ie);
            }
        }
        return null;
    }

    /**
     * Filters object events by XACML requests
     *
     * @param result The object event to filter by XACML requests.
     * @param user The user to check.
     * @param owner The owner to check.
     * @return
     * <code>true</code> if one (or more) request is permited.
     */
    private boolean filterObjectEventType(ObjectEventType result, String user, String owner) {
        Date eventTime = (result.getEventTime() != null) ? result.getEventTime().toGregorianCalendar().getTime() : null;
        Date recordTime = (result.getRecordTime() != null) ? result.getRecordTime().toGregorianCalendar().getTime() : null;
        String eventType = XACMLConstantsEventType.OBJECT;
        String bizStep = result.getBizStep();
        String operation = (result.getAction() != null) ? result.getAction().value() : null;
        String bizLoc = (result.getBizLocation() != null) ? result.getBizLocation().getId() : null;
        String readPoint = (result.getReadPoint() != null) ? result.getReadPoint().getId() : null;
        String disposition = result.getDisposition();
        Long quantity = null;
        String parentId = null;
        String childEpc = null;
        String epc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        boolean onePermit = false;

        if (result.getEpcList().getEpc() == null || result.getEpcList().getEpc().isEmpty()) {
            if (result.getBizTransactionList() == null || result.getBizTransactionList().getBizTransaction() == null
                    || result.getBizTransactionList().getBizTransaction().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (xacmlCheck(xacmlEvent, user)) {
                    onePermit = true;
                }
            } else {
                Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                while (iterBizTransType.hasNext()) {
                    bizTrans = iterBizTransType.next().getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterBizTransType.remove();
                    }
                }
            }
        } else {
            if (result.getBizTransactionList() == null || result.getBizTransactionList().getBizTransaction() == null
                    || result.getBizTransactionList().getBizTransaction().isEmpty()) {
                Iterator<EPC> iterEpc = result.getEpcList().getEpc().iterator();
                while (iterEpc.hasNext()) {
                    epc = iterEpc.next().getValue();
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
                Iterator<EPC> iterEpc = result.getEpcList().getEpc().iterator();
                while (iterEpc.hasNext()) {
                    EPC epcItem = iterEpc.next();
                    Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                    while (iterBizTransType.hasNext()) {
                        BusinessTransactionType bizTransType = iterBizTransType.next();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (xacmlCheck(xacmlEvent, user)) {
                            allowedEpc = epcItem.getValue();
                            allowedBizTrans = bizTransType.getValue();
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
                    Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                    while (iterBizTransType.hasNext()) {
                        bizTrans = iterBizTransType.next().getValue();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (!xacmlCheck(xacmlEvent, user)) {
                            iterBizTransType.remove();
                        }
                    }
                }
                if (allowedBizTrans != null) {
                    iterEpc = result.getEpcList().getEpc().iterator();
                    while (iterEpc.hasNext()) {
                        EPC epcItem = iterEpc.next();
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
            Iterator<Object> iter = result.getAny().iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                ElementNSImpl element = (ElementNSImpl) obj;
                String namespace = element.getNamespaceURI();
                String extensionName = element.getLocalName();
                String value = element.getTextContent();

                // Gets the extension value
                Object extensionValue = null;
                try {
                    extensionValue = new Integer(Integer.parseInt(value));
                } catch (Exception ex) {
                    // Extension value is not an integer
                    try {
                        extensionValue = new Float(Float.parseFloat(value));
                    } catch (Exception exF) {
                        // Extension value is not a float
                        try {
                            extensionValue = new Double(Double.parseDouble(value));
                        } catch (Exception exD) {
                            // Extension value is not a double
                            try {
                                extensionValue = TimeParser.parseAsDate(value);
                            } catch (Exception exP) {
                                // Extension value is not a date
                            }
                        }
                    }
                }
                // Extension value is a string
                if (extensionValue == null) {
                    extensionValue = value;
                }

                extension = new ExtensionEvent(namespace, extensionName, extensionValue);
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
     * Filter aggregation events by XACML requests.
     *
     * @param result The AggregationEventType to filter by XACML requests.
     * @param user The user to check.
     * @param owner The owner to check.
     * @return
     * <code>true</code> if one (or more) request is permited.
     */
    private boolean filterAggregationEventType(AggregationEventType result, String user, String owner) {
        Date eventTime = (result.getEventTime() != null) ? result.getEventTime().toGregorianCalendar().getTime() : null;
        Date recordTime = (result.getRecordTime() != null) ? result.getRecordTime().toGregorianCalendar().getTime() : null;
        String eventType = XACMLConstantsEventType.AGGREGATION;
        String operation = (result.getAction() != null) ? result.getAction().value() : null;
        String readPoint = (result.getReadPoint() != null) ? result.getReadPoint().getId() : null;
        String bizLoc = (result.getBizLocation() != null) ? result.getBizLocation().getId() : null;
        String bizStep = result.getBizStep();
        String disposition = result.getDisposition();
        String parentId = result.getParentID();
        Long quantity = null;
        String epc = null;
        String childEpc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        boolean onePermit = false;

        if (result.getChildEPCs() == null || result.getChildEPCs().getEpc() == null || result.getChildEPCs().getEpc().isEmpty()) {
            if (result.getBizTransactionList() == null || result.getBizTransactionList().getBizTransaction() == null
                    || result.getBizTransactionList().getBizTransaction().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (xacmlCheck(xacmlEvent, user)) {
                    onePermit = true;
                }
            } else {
                Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                while (iterBizTransType.hasNext()) {
                    bizTrans = iterBizTransType.next().getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterBizTransType.remove();
                    }
                }
            }
        } else {
            if (result.getBizTransactionList() == null || result.getBizTransactionList().getBizTransaction() == null
                    || result.getBizTransactionList().getBizTransaction().isEmpty()) {
                Iterator<EPC> iterChildEpc = result.getChildEPCs().getEpc().iterator();
                while (iterChildEpc.hasNext()) {
                    childEpc = iterChildEpc.next().getValue();
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
                Iterator<EPC> iterChildEpc = result.getChildEPCs().getEpc().iterator();
                while (iterChildEpc.hasNext()) {
                    childEpc = iterChildEpc.next().getValue();
                    Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                    while (iterBizTransType.hasNext()) {
                        bizTrans = iterBizTransType.next().getValue();
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
                    Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                    while (iterBizTransType.hasNext()) {
                        bizTrans = iterBizTransType.next().getValue();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (xacmlCheck(xacmlEvent, user)) {
                            iterBizTransType.remove();
                        }
                    }
                }
                if (allowedBizTrans != null) {
                    iterChildEpc = result.getChildEPCs().getEpc().iterator();
                    while (iterChildEpc.hasNext()) {
                        childEpc = iterChildEpc.next().getValue();
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
            childEpc = null;
            bizTrans = null;
            Iterator<Object> iter = result.getAny().iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                ElementNSImpl element = (ElementNSImpl) obj;
                String namespace = element.getNamespaceURI();
                String extensionName = element.getLocalName();
                String value = element.getTextContent();

                // Gets the extension value
                Object extensionValue = null;
                try {
                    extensionValue = new Integer(Integer.parseInt(value));
                } catch (Exception ex) {
                    // Extension value is not an integer
                    try {
                        extensionValue = new Float(Float.parseFloat(value));
                    } catch (Exception exF) {
                        // Extension value is not a float
                        try {
                            extensionValue = new Double(Double.parseDouble(value));
                        } catch (Exception exD) {
                            // Extension value is not a double
                            try {
                                extensionValue = TimeParser.parseAsDate(value);
                            } catch (Exception exP) {
                                // Extension value is not a date
                            }
                        }
                    }
                }
                // Extension value is a string
                if (extensionValue == null) {
                    extensionValue = value;
                }

                extension = new ExtensionEvent(namespace, extensionName, extensionValue);
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
     * Filters quantity event by XACML requests.
     *
     * @param result The QuantityEventType to filter by XACML requests.
     * @param user The user to check.
     * @param owner The owner to check.
     * @return
     * <code>true</code> if the request is permited.
     */
    private boolean filterQuantityEventType(QuantityEventType result, String user, String owner) {
        Date eventTime = (result.getEventTime() != null) ? result.getEventTime().toGregorianCalendar().getTime() : null;
        Date recordTime = (result.getRecordTime() != null) ? result.getRecordTime().toGregorianCalendar().getTime() : null;
        String eventType = XACMLConstantsEventType.QUANTITY;
        String readPoint = (result.getReadPoint() != null) ? result.getReadPoint().getId() : null;
        String bizLoc = (result.getBizLocation() != null) ? result.getBizLocation().getId() : null;
        String bizStep = result.getBizStep();
        String disposition = result.getDisposition();
        String epc = result.getEpcClass();
        Long quantity = new Long(result.getQuantity());
        String operation = null;
        String parentId = null;
        String childEpc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        if (result.getBizTransactionList() == null || result.getBizTransactionList().getBizTransaction() == null
                || result.getBizTransactionList().getBizTransaction().isEmpty()) {
            XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                    eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
            return xacmlCheck(xacmlEvent, user);
        }

        boolean onePermit = false;
        Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
        while (iterBizTransType.hasNext()) {
            bizTrans = iterBizTransType.next().getValue();
            XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                    eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
            if (xacmlCheck(xacmlEvent, user)) {
                onePermit = true;
            } else {
                iterBizTransType.remove();
            }
        }
        if (onePermit) {
            bizTrans = null;
            Iterator<Object> iter = result.getAny().iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                ElementNSImpl element = (ElementNSImpl) obj;
                String namespace = element.getNamespaceURI();
                String extensionName = element.getLocalName();
                String value = element.getTextContent();

                // Gets the extension value
                Object extensionValue = null;
                try {
                    extensionValue = new Integer(Integer.parseInt(value));
                } catch (Exception ex) {
                    // Extension value is not an integer
                    try {
                        extensionValue = new Float(Float.parseFloat(value));
                    } catch (Exception exF) {
                        // Extension value is not a float
                        try {
                            extensionValue = new Double(Double.parseDouble(value));
                        } catch (Exception exD) {
                            // Extension value is not a double
                            try {
                                extensionValue = TimeParser.parseAsDate(value);
                            } catch (Exception exP) {
                                // Extension value is not a date
                            }
                        }
                    }
                }
                // Extension value is a string
                if (extensionValue == null) {
                    extensionValue = value;
                }

                extension = new ExtensionEvent(namespace, extensionName, extensionValue);
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
     * Filters transaction event by XACML requests.
     *
     * @param result The TransactionEventType to filter by XACML requests.
     * @param user The user to check.
     * @param owner The owner to check.
     * @return
     * <code>true</code> if one (or more) request is permited.
     */
    private boolean filterTransactionEventType(TransactionEventType result, String user, String owner) {
        Date eventTime = (result.getEventTime() != null) ? result.getEventTime().toGregorianCalendar().getTime() : null;
        Date recordTime = (result.getRecordTime() != null) ? result.getRecordTime().toGregorianCalendar().getTime() : null;
        String eventType = XACMLConstantsEventType.TRANSACTION;
        String operation = (result.getAction() != null) ? result.getAction().value() : null;
        String readPoint = (result.getReadPoint() != null) ? result.getReadPoint().getId() : null;
        String bizLoc = (result.getBizLocation() != null) ? result.getBizLocation().getId() : null;
        String bizStep = result.getBizStep();
        String disposition = result.getDisposition();
        String parentId = result.getParentID();
        Long quantity = null;
        String childEpc = null;
        String epc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        boolean onePermit = false;

        if (result.getEpcList() == null || result.getEpcList().getEpc() == null || result.getEpcList().getEpc().isEmpty()) {
            if (result.getBizTransactionList() == null || result.getBizTransactionList().getBizTransaction() == null
                    || result.getBizTransactionList().getBizTransaction().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (xacmlCheck(xacmlEvent, user)) {
                    onePermit = true;
                }
            } else {
                Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                while (iterBizTransType.hasNext()) {
                    bizTrans = iterBizTransType.next().getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (xacmlCheck(xacmlEvent, user)) {
                        onePermit = true;
                    } else {
                        iterBizTransType.remove();
                    }
                }
            }
        } else {
            if (result.getBizTransactionList() == null || result.getBizTransactionList().getBizTransaction() == null
                    || result.getBizTransactionList().getBizTransaction().isEmpty()) {
                Iterator<EPC> iterEpc = result.getEpcList().getEpc().iterator();
                while (iterEpc.hasNext()) {
                    epc = iterEpc.next().getValue();
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
                Iterator<EPC> iterEpc = result.getEpcList().getEpc().iterator();
                while (iterEpc.hasNext()) {
                    epc = iterEpc.next().getValue();
                    Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                    while (iterBizTransType.hasNext()) {
                        bizTrans = iterBizTransType.next().getValue();
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
                    Iterator<BusinessTransactionType> iterBizTransType = result.getBizTransactionList().getBizTransaction().iterator();
                    while (iterBizTransType.hasNext()) {
                        bizTrans = iterBizTransType.next().getValue();
                        XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                                eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                        if (!xacmlCheck(xacmlEvent, user)) {
                            iterBizTransType.remove();
                        }
                    }
                }
                if (allowedBizTrans != null) {
                    iterEpc = result.getEpcList().getEpc().iterator();
                    while (iterEpc.hasNext()) {
                        epc = iterEpc.next().getValue();
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
            Iterator<Object> iter = result.getAny().iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                ElementNSImpl element = (ElementNSImpl) obj;
                String namespace = element.getNamespaceURI();
                String extensionName = element.getLocalName();
                String value = element.getTextContent();

                // Gets the extension value
                Object extensionValue = null;
                try {
                    extensionValue = new Integer(Integer.parseInt(value));
                } catch (Exception ex) {
                    // Extension value is not an integer
                    try {
                        extensionValue = new Float(Float.parseFloat(value));
                    } catch (Exception exF) {
                        // Extension value is not a float
                        try {
                            extensionValue = new Double(Double.parseDouble(value));
                        } catch (Exception exD) {
                            // Extension value is not a double
                            try {
                                extensionValue = TimeParser.parseAsDate(value);
                            } catch (Exception exP) {
                                // Extension value is not a date
                            }
                        }
                    }
                }
                // Extension value is a string
                if (extensionValue == null) {
                    extensionValue = value;
                }

                extension = new ExtensionEvent(namespace, extensionName, extensionValue);
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
     * Checks wehther the user has access rights to subscribe.
     *
     * @param user The user who wants to subscribe.
     * @return The decision result.
     */
    public int checkSubscribe(String user, String partner) {
        return epcisPEP.subscribe(user, partner, "Query");
    }

    /**
     * Filters the list of master data.
     *
     * @param vocList The list of master data to filter.
     * @param user The user name to check.
     * @param owner The owner to check.
     */
    public void xacmlCheckMasterD(List<VocabularyType> vocList, String user, String owner) {
        for (VocabularyType voc : vocList) {
            xacmlCheckMasterDType(voc.getVocabularyElementList().getVocabularyElement(), user, owner);
        }
    }

    /**
     * Filters the list of master data, by element type.
     *
     * @param vocElList The list of master data to filter.
     * @param user The user name to check
     * @param owner The owner to check.
     */
    private void xacmlCheckMasterDType(List<VocabularyElementType> vocElList, String user, String owner) {
        Iterator<VocabularyElementType> iterVoc = vocElList.iterator();
        while (iterVoc.hasNext()) {
            VocabularyElementType vocEl = iterVoc.next();
            String id = vocEl.getId();
            XACMLEPCISMasterData xacmlMasterData = new XACMLEPCISMasterData(owner, id);
            if (!xacmlCheckMasterData(xacmlMasterData, user)) {
                iterVoc.remove();
            }
        }
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
        int xacmlResponse = epcisPEP.queryMasterData(user, xacmlMasterData, "Query");
        return xacmlResponse == Result.DECISION_PERMIT;
    }
}
