/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
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

import fr.unicaen.iota.xacml.XACMLConstantsEventType;
import fr.unicaen.iota.xacml.pep.ExtensionEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import fr.unicaen.iota.xi.client.EPCISPEP;
import fr.unicaen.iota.xi.utils.Utils;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.utils.TimeParser;
import org.w3c.dom.Element;

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
        epcisPEP = new EPCISPEP(Constants.XACML_URL, Constants.PKS_FILENAME, Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
    }

    public boolean xacmlCheck(XACMLEPCISEvent xacmlEvent, String user) {
        int xacmlResponse = epcisPEP.queryEvent(user, xacmlEvent);
        return Utils.responseIsPermit(xacmlResponse);
    }

    /**
     * Checks the callback events by XACML requests to the XACML module.
     *
     * @param epcisEventList The callback events.
     * @param user The user name.
     * @return <code>true</code> if the callback is permitted.
     */
    public boolean xacmlCheck(List<EPCISEventType> epcisEventList, String user) {
        for (EPCISEventType epcisEvent : epcisEventList) {
            String owner = "anonymous";
            if (epcisEvent instanceof ObjectEventType) {
                if (!checkObjectEvent((ObjectEventType) epcisEvent, user, owner)) {
                    return false;
                }
            } else if (epcisEvent instanceof AggregationEventType) {
                if (!checkAggregationEvent((AggregationEventType) epcisEvent, user, owner)) {
                    return false;
                }
            } else if (epcisEvent instanceof QuantityEventType) {
                if (!checkQuantityEvent((QuantityEventType) epcisEvent, user, owner)) {
                    return false;
                }
            } else if (epcisEvent instanceof TransactionEventType) {
                if (!checkTransactionEvent((TransactionEventType) epcisEvent, user, owner)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks an aggregation callback event.
     *
     * @param baseEvent The aggregation callback event to check.
     * @param user The user name.
     * @return <code>true</code> if the aggregation callback event is permitted.
     */
    private boolean checkAggregationEvent(AggregationEventType aggregationEvent, String user, String owner) {
        Date eventTime = (aggregationEvent.getEventTime() != null) ? aggregationEvent.getEventTime().toGregorianCalendar().getTime() : null;
        Date recordTime = (aggregationEvent.getRecordTime() != null) ? aggregationEvent.getRecordTime().toGregorianCalendar().getTime() : null;
        String eventType = XACMLConstantsEventType.AGGREGATION;
        String operation = (aggregationEvent.getAction() != null) ? aggregationEvent.getAction().value() : null;
        String readPoint = (aggregationEvent.getReadPoint() != null) ? aggregationEvent.getReadPoint().getId() : null;
        String bizLoc = (aggregationEvent.getBizLocation() != null) ? aggregationEvent.getBizLocation().getId() : null;
        String bizStep = aggregationEvent.getBizStep();
        String disposition = aggregationEvent.getDisposition();
        String parentId = aggregationEvent.getParentID();
        Long quantity = null;
        String epc = null;
        String childEpc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        if (aggregationEvent.getChildEPCs() == null || aggregationEvent.getChildEPCs().getEpc() == null || aggregationEvent.getChildEPCs().getEpc().isEmpty()) {
            if (aggregationEvent.getBizTransactionList() == null || aggregationEvent.getBizTransactionList().getBizTransaction() == null
                    || aggregationEvent.getBizTransactionList().getBizTransaction().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (!xacmlCheck(xacmlEvent, user)) {
                    return false;
                }
            } else {
                for (BusinessTransactionType bizTransType : aggregationEvent.getBizTransactionList().getBizTransaction()) {
                    bizTrans = bizTransType.getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            }
        } else {
            if (aggregationEvent.getBizTransactionList() == null || aggregationEvent.getBizTransactionList().getBizTransaction() == null
                    || aggregationEvent.getBizTransactionList().getBizTransaction().isEmpty()) {
                for (EPC epcType : aggregationEvent.getChildEPCs().getEpc()) {
                    childEpc = epcType.getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            } else {
                Iterator<BusinessTransactionType> iterBizTransType = aggregationEvent.getBizTransactionList().getBizTransaction().iterator();
                for (EPC epcType : aggregationEvent.getChildEPCs().getEpc()) {
                    childEpc = epcType.getValue();
                    if (iterBizTransType.hasNext()) {
                        bizTrans = iterBizTransType.next().getValue();
                    }
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
                while (iterBizTransType.hasNext()) {
                    bizTrans = iterBizTransType.next().getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            }
        }
        childEpc = null;
        bizTrans = null;
        for (Object obj: aggregationEvent.getAny()) {
            Element element = (Element) obj;
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
                return false;
            }

        }
        return true;
    }

    /**
     * Checks an object callback event.
     *
     * @param objectEvent The object callback event to check.
     * @param user The user name.
     * @return <code>true</code> if the object callback event is permitted.
     */
    private boolean checkObjectEvent(ObjectEventType objectEvent, String user, String owner) {
        Date eventTime = (objectEvent.getEventTime() != null) ? objectEvent.getEventTime().toGregorianCalendar().getTime() : null;
        Date recordTime = (objectEvent.getRecordTime() != null) ? objectEvent.getRecordTime().toGregorianCalendar().getTime() : null;
        String eventType = XACMLConstantsEventType.OBJECT;
        String bizStep = objectEvent.getBizStep();
        String operation = (objectEvent.getAction() != null) ? objectEvent.getAction().value() : null;
        String bizLoc = (objectEvent.getBizLocation() != null) ? objectEvent.getBizLocation().getId() : null;
        String readPoint = (objectEvent.getReadPoint() != null) ? objectEvent.getReadPoint().getId() : null;
        String disposition = objectEvent.getDisposition();
        Long quantity = null;
        String parentId = null;
        String childEpc = null;
        String epc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        if (objectEvent.getEpcList().getEpc() == null || objectEvent.getEpcList().getEpc().isEmpty()) {
            if (objectEvent.getBizTransactionList() == null || objectEvent.getBizTransactionList().getBizTransaction() == null
                    || objectEvent.getBizTransactionList().getBizTransaction().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (!xacmlCheck(xacmlEvent, user)) {
                    return false;
                }
            } else {
                for (BusinessTransactionType bizTransType : objectEvent.getBizTransactionList().getBizTransaction()) {
                    bizTrans = bizTransType.getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            }
        } else {
            if (objectEvent.getBizTransactionList() == null || objectEvent.getBizTransactionList().getBizTransaction() == null
                    || objectEvent.getBizTransactionList().getBizTransaction().isEmpty()) {
                for (EPC epcType : objectEvent.getEpcList().getEpc()) {
                    epc = epcType.getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            } else {
                Iterator<BusinessTransactionType> iterBizTransType = objectEvent.getBizTransactionList().getBizTransaction().iterator();
                for (EPC epcType : objectEvent.getEpcList().getEpc()) {
                    epc = epcType.getValue();
                    if (iterBizTransType.hasNext()) {
                        bizTrans = iterBizTransType.next().getValue();
                    }
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
                while (iterBizTransType.hasNext()) {
                    bizTrans = iterBizTransType.next().getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            }
        }
        epc = null;
        bizTrans = null;
        for (Object obj : objectEvent.getAny()) {
            Element element = (Element) obj;
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
                return false;
            }
        }
        return true;
    }

    /**
     * Checks an quantity callback event.
     *
     * @param quantityEvent The quantity callback event to check.
     * @param user The user name.
     * @return <code>true</code> if the quantity callback event is permitted.
     */
    private boolean checkQuantityEvent(QuantityEventType quantityEvent, String user, String owner) {
        Date eventTime = (quantityEvent.getEventTime() != null) ? quantityEvent.getEventTime().toGregorianCalendar().getTime() : null;
        Date recordTime = (quantityEvent.getRecordTime() != null) ? quantityEvent.getRecordTime().toGregorianCalendar().getTime() : null;
        String eventType = XACMLConstantsEventType.QUANTITY;
        String readPoint = (quantityEvent.getReadPoint() != null) ? quantityEvent.getReadPoint().getId() : null;
        String bizLoc = (quantityEvent.getBizLocation() != null) ? quantityEvent.getBizLocation().getId() : null;
        String bizStep = quantityEvent.getBizStep();
        String disposition = quantityEvent.getDisposition();
        String epc = quantityEvent.getEpcClass();
        Long quantity = new Long(quantityEvent.getQuantity());
        String operation = null;
        String parentId = null;
        String childEpc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        if (quantityEvent.getBizTransactionList() == null || quantityEvent.getBizTransactionList().getBizTransaction() == null
                || quantityEvent.getBizTransactionList().getBizTransaction().isEmpty()) {
            XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                    eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
            if (!xacmlCheck(xacmlEvent, user)) {
                return false;
            }
        } else {
            for (BusinessTransactionType bizTransType : quantityEvent.getBizTransactionList().getBizTransaction()) {
                bizTrans = bizTransType.getValue();
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (!xacmlCheck(xacmlEvent, user)) {
                    return false;
                }
            }
        }

        bizTrans = null;
        for (Object obj : quantityEvent.getAny()) {
            Element element = (Element) obj;
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
                return false;
            }

        }
        return true;
    }

    /**
     * Checks a transaction callback event.
     *
     * @param transactionEvent The transaction callback event to check.
     * @param user The user name.
     * @return <code>true</code> if the transaction callback event is permitted.
     */
    private boolean checkTransactionEvent(TransactionEventType transactionEvent, String user, String owner) {
        Date eventTime = (transactionEvent.getEventTime() != null) ? transactionEvent.getEventTime().toGregorianCalendar().getTime() : null;
        Date recordTime = (transactionEvent.getRecordTime() != null) ? transactionEvent.getRecordTime().toGregorianCalendar().getTime() : null;
        String eventType = XACMLConstantsEventType.TRANSACTION;
        String operation = (transactionEvent.getAction() != null) ? transactionEvent.getAction().value() : null;
        String readPoint = (transactionEvent.getReadPoint() != null) ? transactionEvent.getReadPoint().getId() : null;
        String bizLoc = (transactionEvent.getBizLocation() != null) ? transactionEvent.getBizLocation().getId() : null;
        String bizStep = transactionEvent.getBizStep();
        String disposition = transactionEvent.getDisposition();
        String parentId = transactionEvent.getParentID();
        Long quantity = null;
        String childEpc = null;
        String epc = null;
        String bizTrans = null;
        ExtensionEvent extension = null;

        if (transactionEvent.getEpcList() == null || transactionEvent.getEpcList().getEpc() == null || transactionEvent.getEpcList().getEpc().isEmpty()) {
            if (transactionEvent.getBizTransactionList() == null || transactionEvent.getBizTransactionList().getBizTransaction() == null
                    || transactionEvent.getBizTransactionList().getBizTransaction().isEmpty()) {
                XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                        eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                if (!xacmlCheck(xacmlEvent, user)) {
                    return false;
                }
            } else {
                for (BusinessTransactionType bizTransType : transactionEvent.getBizTransactionList().getBizTransaction()) {
                    bizTrans = bizTransType.getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            }
        } else {
            if (transactionEvent.getBizTransactionList() == null || transactionEvent.getBizTransactionList().getBizTransaction() == null
                    || transactionEvent.getBizTransactionList().getBizTransaction().isEmpty()) {
                for (EPC epcType : transactionEvent.getEpcList().getEpc()) {
                    epc = epcType.getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            } else {
                Iterator<BusinessTransactionType> iterBizTransType = transactionEvent.getBizTransactionList().getBizTransaction().iterator();
                for (EPC epcType : transactionEvent.getEpcList().getEpc()) {
                    epc = epcType.getValue();
                    if (iterBizTransType.hasNext()) {
                        bizTrans = iterBizTransType.next().getValue();
                    }
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
                while (iterBizTransType.hasNext()) {
                    bizTrans = iterBizTransType.next().getValue();
                    XACMLEPCISEvent xacmlEvent = new XACMLEPCISEvent(owner, bizStep, epc, eventTime, recordTime, operation,
                            eventType, parentId, childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
                    if (!xacmlCheck(xacmlEvent, user)) {
                        return false;
                    }
                }
            }
        }
        epc = null;
        bizTrans = null;
        for (Object obj : transactionEvent.getAny()) {
            Element element = (Element) obj;
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
                return false;
            }
        }
        return true;
    }
}
