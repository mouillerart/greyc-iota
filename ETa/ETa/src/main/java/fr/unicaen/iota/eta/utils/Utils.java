/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.eta.utils;

import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.AttributeType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.EPCISMasterDataDocumentType;
import org.fosstrak.epcis.model.ObjectEventType;
import org.fosstrak.epcis.model.QuantityEventType;
import org.fosstrak.epcis.model.TransactionEventType;
import org.fosstrak.epcis.model.VocabularyElementType;
import org.fosstrak.epcis.repository.EpcisConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Utils {

    /**
     * Gets the owner of the event from the corresponding extension.
     * @param event The event to process.
     * @return The event owner.
     */
    public static String getEventOwner(EPCISEventType event) {
        List<String> owners = fr.unicaen.iota.mu.Utils.getExtension(event,
                fr.unicaen.iota.mu.Constants.URN_IOTA, fr.unicaen.iota.mu.Constants.EXTENSION_OWNER_ID);
        if (owners != null && !owners.isEmpty()) {
            return owners.get(0);
        }
        return null;
    }

    /**
     * Inserts the owner in the event if it is missing.
     * @param event The event to process.
     * @param owner The owner of the event.
     * @throws ParserConfigurationException if an error occured during insertion of the owner of the event.
     */
    public static void insertEventOwnerIfMissing(EPCISEventType event, String owner) throws ParserConfigurationException {
        if (getEventOwner(event) == null) {
            fr.unicaen.iota.mu.Utils.insertExtension(event, fr.unicaen.iota.mu.Constants.URN_IOTA,
                    fr.unicaen.iota.mu.Constants.EXTENSION_OWNER_ID, owner);
        }
    }

    /**
     * Gets the owner of the VocabularyElementType object.
     * @param vocEl The vocabulary to handle.
     * @return The owner of the VocabularyElementType or <code>null</code>.
     */
    public static String getVocabularyOwner(VocabularyElementType vocEl) {
        String owner = null;
        for (Object object : vocEl.getAny()) {
            JAXBElement elem = (JAXBElement) object;
            if (fr.unicaen.iota.mu.Constants.URN_IOTA.equals(elem.getName().getNamespaceURI())
                    && fr.unicaen.iota.mu.Constants.EXTENSION_OWNER_ID.equals(elem.getName().getLocalPart())) {
                owner = elem.getValue().toString();
                break;
            }
        }
        // If the owner is not in the extensions, checks in the attributes and Content
        if (owner == null) {
            String ownerAttribute = fr.unicaen.iota.mu.Constants.URN_IOTA + ":" + fr.unicaen.iota.mu.Constants.EXTENSION_OWNER_ID;
            for (AttributeType attr : vocEl.getAttribute()) {
                if (ownerAttribute.equals(attr.getId())) {
                    if (attr.getOtherAttributes() != null && !attr.getOtherAttributes().isEmpty()) {
                        for (Map.Entry<QName, String> entry : attr.getOtherAttributes().entrySet()) {
                            if ("value".equals(entry.getKey().getLocalPart())) {
                                owner = entry.getValue();
                                break;
                            }
                        }
                    }
                    else if (attr.getContent() != null && !attr.getContent().isEmpty()) {
                        owner = attr.getContent().get(0).toString();
                        break;
                    }
                }
            }
        }
        return owner;
    }

    /**
     * Parses the XML node and returns capture event.
     *
     * @param eventNode The XML node.
     * @param eventType The type of the event.
     * @return The capture event.
     * @throws SAXException If an error parsing the XML document occurred.
     * @throws JAXBException If an error parsing the XML document to object occurred.
     */
    public static EPCISEventType extractsCaptureEvent(Node eventNode, String eventType) throws SAXException, JAXBException {
        if (eventNode == null) {
            // nothing to do
            return null;
        } else if (eventNode.getChildNodes().getLength() == 0) {
            throw new SAXException("Event element '" + eventNode.getNodeName() + "' has no children elements.");
        }
        EPCISEventType captureEvent;
        if (EpcisConstants.AGGREGATION_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(AggregationEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<AggregationEventType> jElement = unmarshaller.unmarshal(eventNode, AggregationEventType.class);
            captureEvent = jElement.getValue();
        } else if (EpcisConstants.OBJECT_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(ObjectEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<ObjectEventType> jElement = unmarshaller.unmarshal(eventNode, ObjectEventType.class);
            captureEvent = jElement.getValue();
        } else if (EpcisConstants.QUANTITY_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(QuantityEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<QuantityEventType> jElement = unmarshaller.unmarshal(eventNode, QuantityEventType.class);
            captureEvent = jElement.getValue();
        } else if (EpcisConstants.TRANSACTION_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(TransactionEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<TransactionEventType> jElement = unmarshaller.unmarshal(eventNode, TransactionEventType.class);
            captureEvent = jElement.getValue();
        } else {
            throw new SAXException("Encountered unknown event element '" + eventType + "'.");
        }
        return captureEvent;
    }

    /**
     * Extracts a EPCISMasterDataDocumentType object from a XML document.
     * @param doc The XML document to parse.
     * @return The EPCISMasterDataDocumentType object associated to the string.
     * @throws JAXBException If an error occurred during the conversion.
     */
    public static EPCISMasterDataDocumentType extractsEPCISMasterDataDocument(Document doc) throws JAXBException {
            JAXBContext context;
        context = JAXBContext.newInstance("org.fosstrak.epcis.model");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<EPCISMasterDataDocumentType> jaxbObject =
                (JAXBElement<EPCISMasterDataDocumentType>) unmarshaller.unmarshal(doc, EPCISMasterDataDocumentType.class);
        return jaxbObject.getValue();
    }

}
