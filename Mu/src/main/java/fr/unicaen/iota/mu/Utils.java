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
package fr.unicaen.iota.mu;

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
     * Formats a user ID.
     * @param id The user ID to format.
     * @return The formated user ID.
     */
    public static String formatId(String id) {
        return id.replaceAll(" ", "");
    }

}
