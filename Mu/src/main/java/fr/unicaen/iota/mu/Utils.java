/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.mu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.ObjectEventType;
import org.fosstrak.epcis.model.QuantityEventType;
import org.fosstrak.epcis.model.TransactionEventType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class Utils {

    /**
     * Inserts an extension in the event.
     * @param event The event to process.
     * @param namespace The namespace of the extension. To set a prefix, set <code>extensionName</code>.
     * @param extensionName The name of the extension. To set prefeix, use <code>prefix:extensionName</code>.
     * @param extensionValue The value of the extension.
     * @return <code>True</code> if the extension is correctly added.
     * @throws ParserConfigurationException if an error occured during insertion of the owner of the event.
     */
    public static boolean insertExtension(EPCISEventType event, String namespace,
            String extensionName, String extensionValue) throws ParserConfigurationException {
        List<Object> extensions;
        if (event instanceof ObjectEventType) {
            extensions = ((ObjectEventType) event).getAny();
        } else if (event instanceof AggregationEventType) {
            extensions = ((AggregationEventType) event).getAny();
        } else if (event instanceof QuantityEventType) {
            extensions = ((QuantityEventType) event).getAny();
        } else if (event instanceof TransactionEventType) {
            extensions = ((TransactionEventType) event).getAny();
        } else {
            return false;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = factory.newDocumentBuilder();
        Document doc = db.newDocument();
        Element elem = doc.createElementNS(namespace, extensionName);
        Text senderText = doc.createTextNode(extensionValue);
        elem.appendChild(senderText);
        extensions.add(elem);
        return true;
    }

    /**
     * Gets the values from the given extension, in String form.
     * @param event The event to handle.
     * @param extensionNamespace The namespace of the extension.
     * @param extensionName The name of the extension.
     * @return The values associated to the extension, in String form.
     * null if a parameter is null or the extensions can't be retrieved.
     */
    public static List<String> getExtension(EPCISEventType event, String extensionNamespace, String extensionName) {
        if (event == null || extensionNamespace == null || extensionName == null) {
            return null;
        }
        List<Object> extensions;
        if (event instanceof ObjectEventType) {
            extensions = ((ObjectEventType) event).getAny();
        } else if (event instanceof AggregationEventType) {
            extensions = ((AggregationEventType) event).getAny();
        } else if (event instanceof QuantityEventType) {
            extensions = ((QuantityEventType) event).getAny();
        } else if (event instanceof TransactionEventType) {
            extensions = ((TransactionEventType) event).getAny();
        } else {
            return null;
        }
        List<String> extensionValues = new ArrayList<String>();
        for (Object object : extensions) {
            Element element = (Element) object;
            if (extensionNamespace.equals(element.getNamespaceURI()) &&
                    extensionName.equals(element.getLocalName())) {
                extensionValues.add(element.getTextContent());
            }
        }
        return extensionValues;
    }

    /**
     * Removes the extension from the event.
     * @param event The event to handle.
     * @param extensionNamespace The namespace of the extension to remove.
     * @param extensionName The name of the extension to remove.
     */
    public static void removesExtension(EPCISEventType event, String extensionNamespace, String extensionName) {
        List<Object> extensions;
        if (event instanceof ObjectEventType) {
            extensions = ((ObjectEventType) event).getAny();
        } else if (event instanceof AggregationEventType) {
            extensions = ((AggregationEventType) event).getAny();
        } else if (event instanceof QuantityEventType) {
            extensions = ((QuantityEventType) event).getAny();
        } else if (event instanceof TransactionEventType) {
            extensions = ((TransactionEventType) event).getAny();
        } else {
            return;
        }
        Iterator<Object> iterExtension = extensions.iterator();
        while (iterExtension.hasNext()) {
            Element elem = (Element) iterExtension.next();
            if ((extensionNamespace.equals(elem.getNamespaceURI())
                    && extensionName.equals(elem.getLocalName()))) {
                iterExtension.remove();
            }
        }
    }

    /**
     * Formats a user ID.
     * @param id The user ID to format.
     * @return The formated user ID.
     */
    public static String formatId(String id) {
        return id.replaceAll(" ", "");
    }

}
