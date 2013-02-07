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
import javax.xml.parsers.ParserConfigurationException;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.ObjectEventType;
import org.fosstrak.epcis.model.QuantityEventType;
import org.fosstrak.epcis.model.TransactionEventType;
import org.w3c.dom.Element;

public class Utils {

    /**
     * Gets the owner of the event from the corresponding extension.
     * @param event The event to process.
     * @return The event owner.
     */
    public static String getEventOwner(EPCISEventType event) {
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
        for (Object object : extensions) {
            Element element = (Element) object;
            if (fr.unicaen.iota.mu.Constants.URN_IOTA.equals(element.getNamespaceURI()) &&
                    fr.unicaen.iota.mu.Constants.EXTENSION_OWNER_ID.equals(element.getLocalName())) {
                return element.getTextContent();
            }
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
        boolean isMissing = true;
        for (Object object : extensions) {
            Element element = (Element) object;
            if (fr.unicaen.iota.mu.Constants.URN_IOTA.equals(element.getNamespaceURI()) &&
                    fr.unicaen.iota.mu.Constants.EXTENSION_OWNER_ID.equals(element.getLocalName())) {
                isMissing = false;
                break;
            }
        }
        if (isMissing) {
            fr.unicaen.iota.mu.Utils.insertExtension(event, fr.unicaen.iota.mu.Constants.URN_IOTA,
                    fr.unicaen.iota.mu.Constants.EXTENSION_OWNER_ID, owner);
        }
    }

}
