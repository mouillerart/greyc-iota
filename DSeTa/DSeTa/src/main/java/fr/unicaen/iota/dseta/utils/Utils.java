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
package fr.unicaen.iota.dseta.utils;

import fr.unicaen.iota.dseta.model.EventCreateReq;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Utils {

    /**
     * Converts an EventCreateReq to String.
     * @param eventCreateReq The EventCreateReq to convert.
     * @return The String associated to the EventCreateReq.
     * @throws JAXBException If an error occurred during the conversion.
     */
    public static String convertsEventCreateReqToString(EventCreateReq eventCreateReq) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("fr.unicaen.iota.dseta.model");
        Marshaller marshaller = context.createMarshaller();
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(new JAXBElement(new QName("urn:unicaen:iota:dseta:xsd","dseta"), EventCreateReq.class, eventCreateReq), stringWriter);
        return stringWriter.toString();
    }

    /**
     * Extracts a EventCreateReq object from a String.
     * @param xmlString The string to parse.
     * @return The EventCreateReq associated to the string.
     * @throws JAXBException If an error occurred during the conversion.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws IOException If an I/O error occurred.
     */
    public static EventCreateReq extractsEventCreateReq(String xmlString) throws JAXBException,
            ParserConfigurationException, SAXException, IOException {
        Document doc = fr.unicaen.iota.ds.commons.Utils.extractsDocument(xmlString);
        JAXBContext context = JAXBContext.newInstance("fr.unicaen.iota.dseta.model");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<EventCreateReq> jaxbObject = (JAXBElement<EventCreateReq>) unmarshaller.unmarshal(doc, EventCreateReq.class);
        return jaxbObject.getValue();
    }

    /**
     * Extracts a EventCreateReq object from a XML document.
     * @param doc The XML document to parse.
     * @return The EventCreateReq object associated to the string.
     * @throws JAXBException If an error occurred during the conversion.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws IOException If an I/O error occurred.
     */
    public static EventCreateReq extractsEventCreateReq(Document doc) throws JAXBException,
            ParserConfigurationException, SAXException, IOException {
        JAXBContext context = JAXBContext.newInstance("fr.unicaen.iota.dseta.model");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<EventCreateReq> jaxbObject = (JAXBElement<EventCreateReq>) unmarshaller.unmarshal(doc, EventCreateReq.class);
        return jaxbObject.getValue();
    }

}
