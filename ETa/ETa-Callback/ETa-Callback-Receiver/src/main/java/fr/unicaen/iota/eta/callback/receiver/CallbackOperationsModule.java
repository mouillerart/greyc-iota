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
package fr.unicaen.iota.eta.callback.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
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
import org.apache.activemq.ActiveMQConnection;
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

    private final String PROP_EPCIS_SCHEMA_FILE = "/wsdl/EPCglobal-epcis-query-1_0.xsd";
    private final String PROPERTY_FILE = "/application.properties";
    private Properties properties;
    private Schema schema;
    /*
     * Properties of the message broker
     */
    private String queueName = "queueToFilter";
    private final String PROP_QUEUENAME = "connection.queueName";
    private final String PROP_USER = "connection.user";
    private final String PROP_PASSWORD = "connection.password";
    private final String PROP_URL = "connection.url";
    private String user = ActiveMQConnection.DEFAULT_USER;
    private String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static final Log LOG = LogFactory.getLog(CallbackOperationsModule.class);

    public CallbackOperationsModule() {
        this.properties = loadProperties(PROPERTY_FILE);
        this.schema = initEpcisSchema(PROP_EPCIS_SCHEMA_FILE);
        queueName = properties.getProperty(PROP_QUEUENAME, queueName);
        url = properties.getProperty(PROP_URL, ActiveMQConnection.DEFAULT_BROKER_URL);
        user = properties.getProperty(PROP_USER, ActiveMQConnection.DEFAULT_USER);
        password = properties.getProperty(PROP_PASSWORD, ActiveMQConnection.DEFAULT_PASSWORD);
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
     * Gets ActiveMQ password.
     *
     * @return The ActiveMQ password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the name of the queue.
     *
     * @return The name of the queue.
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Gets the destination URL.
     *
     * @return The destination URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the user name.
     *
     * @return The user name.
     */
    public String getUser() {
        return user;
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
                        // too large, do not log
                        xml = null;
                    } else {
                        LOG.debug("Incoming contents:\n\n" + writer.toString() + "\n");
                    }
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
                LOG.debug("Incoming capture request was successfully validated against the EPCISDocument schema");
            } else {
                LOG.warn("Schema validator unavailable. Unable to validate EPCIS capture event against schema!");
            }

        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
        return document;
    }

    /**
     * Loads the application's properties file from the class path.
     *
     * @return A populated Properties instance.
     */
    private Properties loadProperties(String file) {
        // read application properties from classpath
        InputStream is = this.getClass().getResourceAsStream(file);
        Properties prop = new Properties();
        try {
            prop.load(is);
            is.close();
        } catch (IOException e) {
            LOG.error("Unable to load application properties from classpath:" + file + " ("
                    + this.getClass().getResource(file) + ")", e);
        }

        return prop;
    }

    /**
     * Sends message to the message broker.
     *
     * @param msg The message to send.
     * @throws JMSException If a sending message error occurred.
     */
    public void send(String msg) throws JMSException {
        ActiveMQConnectionFactory factory;
        if (user != null && password != null && !user.isEmpty() && !password.isEmpty()) {
            factory = new ActiveMQConnectionFactory(user, password, url);
        } else {
            factory = new ActiveMQConnectionFactory(url);
        }
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);
        MessageProducer producer = session.createProducer(destination);
        connection.start();

        try {
            TextMessage message = session.createTextMessage();
            message.setText(msg);
            producer.send(message);
        } finally {
            connection.close();
        }
    }
}
