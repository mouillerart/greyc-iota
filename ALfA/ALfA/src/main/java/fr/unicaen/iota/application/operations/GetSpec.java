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
package fr.unicaen.iota.application.operations;

import fr.unicaen.iota.application.model.Spec;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 */
public class GetSpec {

    private GetSpec() {
    }
    private static final Log log = LogFactory.getLog(GetSpec.class);

    public static Spec getSpecs(String address) throws RemoteException {
        Spec result = new Spec();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(address);

            // epc
            Node epcNode = document.getElementsByTagName("epc").item(0);
            log.trace(epcNode.getChildNodes().item(0).getNodeValue());
            result.setEpc(epcNode.getChildNodes().item(0).getNodeValue());

            // manufacturer
            Node manufacturerNode = document.getElementsByTagName("manufacturer").item(0);
            NodeList manufacterPropertyList = manufacturerNode.getChildNodes();
            for (int i = 0; i < manufacterPropertyList.getLength(); i++) {
                Node foo = manufacterPropertyList.item(i);
                if (foo.getNodeName().equals("#text")) {
                    continue;
                }
                NamedNodeMap attr = foo.getAttributes();
                log.trace(attr.getNamedItem("name").getNodeValue() + ", " + attr.getNamedItem("value").getNodeValue());
                result.getManufacturer().put(attr.getNamedItem("name").getNodeValue(), attr.getNamedItem("value").getNodeValue());
            }

            // product
            Node productNode = document.getElementsByTagName("product").item(0);
            NodeList productPropertyList = productNode.getChildNodes();
            for (int i = 0; i < productPropertyList.getLength(); i++) {
                Node foo = productPropertyList.item(i);
                if (foo.getNodeName().equals("#text")) {
                    continue;
                }
                NamedNodeMap attr = foo.getAttributes();
                log.trace(attr.getNamedItem("name").getNodeValue() + ", " + attr.getNamedItem("value").getNodeValue());
                result.getProduct().put(attr.getNamedItem("name").getNodeValue(), attr.getNamedItem("value").getNodeValue());
            }

            // extension
            Node extensionNode = document.getElementsByTagName("product").item(0);
            NodeList extensionPropertyList = extensionNode.getChildNodes();
            for (int i = 0; i < extensionPropertyList.getLength(); i++) {
                Node foo = extensionPropertyList.item(i);
                if (foo.getNodeName().equals("#text")) {
                    continue;
                }
                NamedNodeMap attr = foo.getAttributes();
                log.trace(attr.getNamedItem("name").getNodeValue() + ", " + attr.getNamedItem("value").getNodeValue());
                result.getExtension().put(attr.getNamedItem("name").getNodeValue(), attr.getNamedItem("value").getNodeValue());
            }
        } catch (ParserConfigurationException e) {
            log.error(null, e);
            return null;
        } catch (SAXException e) {
            log.error(null, e);
            return null;
        } catch (IOException e) {
            log.error(null, e);
            return null;
        }
        return result;
    }
}
