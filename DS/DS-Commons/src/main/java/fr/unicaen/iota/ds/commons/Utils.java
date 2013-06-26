/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013 Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.ds.commons;

import fr.unicaen.iota.ds.model.DSEvent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Utils {

    /**
     * Extracts a DSEvent object from a String.
     * @param xmlString The string to parse.
     * @return The DSEvent associated to the string.
     * @throws JAXBException If an error occurred during the conversion.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws IOException If an I/O error occurred.
     */
    public static DSEvent extractsDSEvent(String xmlString) throws JAXBException,
            ParserConfigurationException, SAXException, IOException {
        Document doc = extractsDocument(xmlString);
        JAXBContext context;
        context = JAXBContext.newInstance("fr.unicaen.iota.ds.model");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<DSEvent> jaxbObject = (JAXBElement<DSEvent>) unmarshaller.unmarshal(doc, DSEvent.class);
        return jaxbObject.getValue();
    }

    /**
     * Extracts a DSEvent object from a XML document.
     * @param doc The XML document to parse.
     * @return The DSEvent object associated to the string.
     * @throws JAXBException If an error occurred during the conversion.
     */
    public static DSEvent extractsDSEvent(Document doc) throws JAXBException {
        JAXBContext context;
        context = JAXBContext.newInstance("fr.unicaen.iota.ds.model");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<DSEvent> jaxbObject = (JAXBElement<DSEvent>) unmarshaller.unmarshal(doc, DSEvent.class);
        return jaxbObject.getValue();
    }

    /**
     * Converts a DSEvent object to XML document.
     * @param dsEvent The DSEvent to convert.
     * @return The XML document associated to the DSEvent.
     * @throws JAXBException If an error occurred during the conversion.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     */
    public static Document convertsDSEventToDocument(DSEvent dsEvent)
            throws JAXBException, ParserConfigurationException {
        JAXBContext context = JAXBContext.newInstance("fr.unicaen.iota.ds.model");
        Marshaller marshaller = context.createMarshaller();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        marshaller.marshal(new JAXBElement(new QName("urn:unicaen:iota:ds:xsd","ds"), DSEvent.class, dsEvent), doc);
        return doc;
    }

    /**
     * Converts a DSEvent to String.
     * @param dsEvent The DSEvent to convert.
     * @return The String associated to the DSEvent.
     * @throws JAXBException If an error occurred during the conversion.
     */
    public static String convertsDSEventToString(DSEvent dsEvent) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("fr.unicaen.iota.ds.model");
        Marshaller marshaller = context.createMarshaller();
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(new JAXBElement(new QName("urn:unicaen:iota:ds:xsd","ds"), DSEvent.class, dsEvent), stringWriter);
        return stringWriter.toString();
    }

    /**
     * Extracts a XML document from a String.
     * @param xmlString The string to parse.
     * @return The Document object.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws IOException If an I/O error occurred.
     */
    public static Document extractsDocument(String xmlString)
            throws ParserConfigurationException, SAXException, IOException {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new InputSource(new StringReader(xmlString)));
        return document;
    }

}
