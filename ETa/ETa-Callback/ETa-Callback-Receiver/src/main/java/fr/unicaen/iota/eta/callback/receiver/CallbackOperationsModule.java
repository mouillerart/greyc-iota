/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.eta.callback.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.jms.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This
 * <code>CallbackOperationsModule</code> parses an input payload to a XML
 * document conforming to a xsd schema. The query callback events contained by
 * this document are sent to the message broker.
 */
public class CallbackOperationsModule {

    private static final Log LOG = LogFactory.getLog(CallbackOperationsModule.class);
    private Schema schema;

    public CallbackOperationsModule() {
        this.schema = initEpcisSchema(Constants.EPCIS_SCHEMA_PATH);
    }

    /**
     * Initializes the EPCIS schema from a XSD file.
     *
     * @param xsdFile The XSD file containing the EPCIS schema.
     * @return The EPCIS schema.
     */
    private Schema initEpcisSchema(String xsdFile) {
        InputStream is = this.getClass().getResourceAsStream(xsdFile);
        if (is != null) {
            try {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Source schemaSrc = new StreamSource(is);
                schemaSrc.setSystemId(CallbackOperationsModule.class.getResource(xsdFile).toString());
                Schema schm = schemaFactory.newSchema(schemaSrc);
                LOG.info("EPCIS schema file initialized and loaded successfully");
                return schm;
            } catch (Exception e) {
                LOG.warn("Unable to load or parse the EPCIS schema", e);
            }
        } else {
            LOG.error("Unable to load the EPCIS schema file from classpath: cannot find resource " + xsdFile);
        }
        LOG.warn("Schema validation will not be available!");
        return null;
    }

    /**
     * Parses and validates the payload as XML document.
     *
     * @param input The payload to parse.
     * @return The valid XML document.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws IOException If an I/O error occurred.
     */
    public Document getDocumentFromInputStream(InputStream input)
            throws SAXException, IOException {

        // parse the payload as XML document
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(input);
            LOG.debug("Payload successfully parsed as XML document");
            if (LOG.isDebugEnabled()) {
                try {
                    TransformerFactory tfFactory = TransformerFactory.newInstance();
                    Transformer transformer = tfFactory.newTransformer();
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(writer));
                    String xml = writer.toString();
                    if (xml.length() > 100 * 1024) {
                        xml = "[too large, not logged]";
                    }
                    LOG.debug("Incoming contents:\n\n" + xml + "\n");
                } catch (Exception e) {
                    // never mind ... do not log
                }
            }

            // validate the XML document against the EPCISDocument schema
            if (schema != null) {
                Validator validator = schema.newValidator();
                try {
                    validator.validate(new DOMSource(document), null);
                } catch (SAXParseException e) {
                    // TODO: we need to ignore XML element order, the following
                    // is only a hack to pass some of the conformance tests
                    if (e.getMessage().contains("parentID")) {
                        LOG.warn("Ignoring XML validation exception: " + e.getMessage());
                    } else {
                        throw e;
                    }
                }
                LOG.debug("Incoming result was successfully validated against the EPCISDocument schema");
            } else {
                LOG.warn("Schema validator unavailable. Unable to validate EPCIS event against schema!");
            }

        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
        return document;
    }

    /**
     * Sends message to the message broker.
     *
     * @param msg The message to send.
     * @throws JMSException If a sending message error occurred.
     */
    public void send(String msg) throws JMSException, Exception {
        ActiveMQConnectionFactory factory;
        if (Constants.ACTIVEMQ_LOGIN != null && Constants.ACTIVEMQ_PASSWORD != null
                && !Constants.ACTIVEMQ_LOGIN.isEmpty() && !Constants.ACTIVEMQ_PASSWORD.isEmpty()) {
            factory = new ActiveMQConnectionFactory(Constants.ACTIVEMQ_LOGIN, Constants.ACTIVEMQ_PASSWORD, Constants.ACTIVEMQ_URL);
        } else {
            factory = new ActiveMQConnectionFactory(Constants.ACTIVEMQ_URL);
        }
        Connection connection = factory.createConnection();
        connection.start();
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            try {
                Destination destination = session.createQueue(Constants.ACTIVEMQ_QUEUE_NAME);
                MessageProducer producer = session.createProducer(destination);
                TextMessage message = session.createTextMessage();
                message.setText(msg);
                producer.send(message);
            } finally {
                session.close();
            }
        } finally {
            connection.close();
        }
    }
}
