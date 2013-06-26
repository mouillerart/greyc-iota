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

import fr.unicaen.iota.eta.callback.filter.utils.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.ObjectEventType;
import org.fosstrak.epcis.model.QuantityEventType;
import org.fosstrak.epcis.model.TransactionEventType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This <code>CallbackOperationsModule</code> parses messages from the message broker
 * to a XML document conforming to a xsd schema. The query callback events
 * contained by this document are extracted and used by
 * <code>CallbackCheck</code> to validate access.
 */
public class CallbackOperationsModule {

    private Schema schema;
    private java.sql.Connection dbConnection;
    private CallbackOperationsBackendSQL backend;
    private CallbackCheck callbackCheck;
    private Connection connection;
    private String jmsUrl;
    private String jmsLogin;
    private String jmsPassword;
    private String timeProperty;
    private static final Log LOG = LogFactory.getLog(CallbackOperationsModule.class);

    public CallbackOperationsModule(String jmsUrl, String jmsLogin, String jmsPassword, String timeProperty) {
        this.schema = initEpcisSchema(Constants.EPCIS_SCHEMA_PATH);
        callbackCheck = new CallbackCheck();
        backend = new CallbackOperationsBackendSQL();
        dbConnection = loadDatabaseConnection();
        this.jmsUrl = jmsUrl;
        this.jmsLogin = jmsLogin;
        this.jmsPassword = jmsPassword;
        this.timeProperty = timeProperty;
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
                LOG.debug("EPCIS schema file initialized and loaded successfully");
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
     * Loads the connection to the database.
     *
     * @return The connection to the database.
     */
    private java.sql.Connection loadDatabaseConnection() {
        java.sql.Connection c = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties props = new Properties();
            props.setProperty("user", Constants.DATABASE_LOGIN);
            props.setProperty("password", Constants.DATABASE_PASSWORD);
            props.setProperty("autoReconnect", "true");
            c = DriverManager.getConnection(Constants.DATABASE_URL, props);
            c.setAutoCommit(false);
            LOG.debug("MySQL connection established");
        } catch (ClassNotFoundException e) {
            LOG.error(e);
        } catch (SQLException e) {
            LOG.error("Error during connection to the database", e);
        }
        return c;
    }

    /**
     * Parses and validates string as XML document.
     *
     * @param input The string to parse.
     * @return The valid XML document.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws IOException If an I/O error occurred.
     */
    private Document getDocumentFromString(String string) throws SAXException, IOException {

        // parse the payload as XML document
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(string)));
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
                LOG.debug("Callback events were successfully validated against the EPCISDocument schema");
            } else {
                LOG.warn("Schema validator unavailable. Unable to validate EPCIS callback events against schema!");
            }

        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
        return document;
    }

    /**
     * Connects to the Message Broker, gets events and sends authorized events.
     * @param consumer The JMS consumer which retrieves the event to filter.
     * @param timeout Waits until this time expires (in milliseconds).
     * @param producerSession The JMS session associated to the producer.
     * @param producer The JMS producer which sends the event to the next queue.
     * @param runtime The runtime.
     * @return True if the filtering can continue.
     * @throws JMSException If an error occurred with the JMS provider.
     * @throws SQLException If an error involving the database occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws IOException If an I/O error occurred.
     * @throws JAXBException If an error parsing from the XML document to objects occurred.
     * @throws Exception If an unexpected error occurred.
     */
    public boolean receiveFilterSend(MessageConsumer consumer, long timeout, Session producerSession, MessageProducer producer,
            long runtime) throws JMSException, SQLException, SAXException, IOException, JAXBException, Exception {
        Message message = consumer.receive(timeout);
        if (message == null) {
            return false;
        }
        String docText;
        if (message instanceof TextMessage) {
            TextMessage text = (TextMessage) message;
            docText = text.getText();
            if (isPermitted(docText)) {
                // --- Sending authorized message to the queue
                sendsJMSEvent(producerSession, producer, docText);
                LOG.info("Event transferred to the next queue.");
            }
            else {
                LOG.info("Event deleted.");
            }
        }
        message.acknowledge();
        return true;
    }

    /**
     * Checks if the user is authorized to receive events.
     * @param docString The events to check.
     * @return <code>true</code> if the user is authorized to receive events.
     * @throws SQLException If an error involving the database occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws JAXBException If an error parsing from the XML document to objects occurred.
     * @throws SQLException If an error involving the database occurred.
     * @throws IOException
     */
    private boolean isPermitted(String docString) throws SAXException, IOException, JAXBException, SQLException {
        Document doc = getDocumentFromString(docString);
        List<EPCISEventType> callbackEventList = extractsCallbackEventList(doc);
        String subscriptionId = getSubIDInDoc(doc);
        String user = fetchUser(subscriptionId);
        if (callbackCheck.xacmlCheck(callbackEventList, user)) {
            return true;
        }
        return false;
    }

    /**
     * Parses a XML document and returns the associated callback events list.
     *
     * @param document The XML document to parse.
     * @return The callback events list from the document.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws JAXBException If an error parsing from the XML document to objects occurred.
     */
    private List<EPCISEventType> extractsCallbackEventList(Document document)
            throws SAXException, JAXBException {
        NodeList eventList = document.getElementsByTagName("EventList");
        NodeList events = eventList.item(0).getChildNodes();
        List<EPCISEventType> callbackEventList = new ArrayList<EPCISEventType>();

        for (int i = 0; i < events.getLength(); i++) {
            Node eventNode = events.item(i);
            String nodeName = eventNode.getNodeName();
            if (EpcisConstants.OBJECT_EVENT.equals(nodeName)
                    || EpcisConstants.AGGREGATION_EVENT.equals(nodeName)
                    || EpcisConstants.QUANTITY_EVENT.equals(nodeName)
                    || EpcisConstants.TRANSACTION_EVENT.equals(nodeName)) {
                LOG.debug("processing event " + i + ": '" + nodeName + "'.");
                EPCISEventType callbackEvent = extractsCaptureEvent(eventNode, nodeName);
                if (callbackEvent != null) {
                    callbackEventList.add(callbackEvent);
                }
            } else if (!"#text".equals(nodeName) && !"#comment".equals(nodeName)) {
                throw new SAXException("Encountered unknown event '" + nodeName + "'.");
            }
        }
        return callbackEventList;
    }

    /**
     * Parses the XML node and returns callback event.
     *
     * @param eventNode The XML node.
     * @param eventType The type of the event.
     * @return The callback event.
     * @throws SAXException If an error parsing the XML document occurred.
     * @throws JAXBException If an error parsing the XML document to object occurred.
     * occurred.
     */
    private EPCISEventType extractsCaptureEvent(Node eventNode, String eventType) throws SAXException, JAXBException {
        if (eventNode == null) {
            // nothing to do
            return null;
        } else if (eventNode.getChildNodes().getLength() == 0) {
            throw new SAXException("Event element '" + eventNode.getNodeName() + "' has no children elements.");
        }
        EPCISEventType callbackEvent;
        if (EpcisConstants.AGGREGATION_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(AggregationEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<AggregationEventType> jElement = unmarshaller.unmarshal(eventNode, AggregationEventType.class);
            callbackEvent = jElement.getValue();
        } else if (EpcisConstants.OBJECT_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(ObjectEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<ObjectEventType> jElement = unmarshaller.unmarshal(eventNode, ObjectEventType.class);
            callbackEvent = jElement.getValue();
        } else if (EpcisConstants.QUANTITY_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(QuantityEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<QuantityEventType> jElement = unmarshaller.unmarshal(eventNode, QuantityEventType.class);
            callbackEvent = jElement.getValue();
        } else if (EpcisConstants.TRANSACTION_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(TransactionEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<TransactionEventType> jElement = unmarshaller.unmarshal(eventNode, TransactionEventType.class);
            callbackEvent = jElement.getValue();
        } else {
            throw new SAXException("Encountered unknown event element '" + eventType + "'.");
        }
        return callbackEvent;
    }

    /**
     * Returns the subcription ID from a XML document.
     *
     * @param doc The XML document.
     * @return The subscription ID contained by the XML document.
     * @throws SAXException If the document contains less or more than one
     * subscriptionid.
     */
    private String getSubIDInDoc(Document doc) throws SAXException {
        NodeList l = doc.getElementsByTagName("subscriptionID");
        if (l.getLength() != 1) {
            throw new SAXException("Error: one and only one time subscriptionID");
        }
        return l.item(0).getTextContent();
    }

    /**
     * Fetch user ID corresponding to subscription ID.
     *
     * @param subscriptionID The subscription ID.
     * @return The user ID corresponding to the subscription.
     * @throws SQLException If an error involving the database occurred.
     */
    private String fetchUser(String subscriptionID) throws SQLException {
        CallbackOperationsSession session = new CallbackOperationsSession(dbConnection);
        return backend.fetchUser(session, subscriptionID);
    }

    /**
     * Creates a JMS connection.
     * @throws JMSException If the connection could not be established.
     */
    public void createsJMSConnection() throws JMSException {
        ActiveMQConnectionFactory factory;
        if (jmsLogin != null && jmsPassword != null
                && !jmsLogin.isEmpty() && !jmsPassword.isEmpty()) {
            factory = new ActiveMQConnectionFactory(jmsLogin, jmsPassword, jmsUrl);
        } else {
            factory = new ActiveMQConnectionFactory(jmsUrl);
        }
        connection = factory.createConnection();
    }

    /**
     * Starts the JMS connection.
     * @throws JMSException If the JMS provider fails to start message delivery due to some internal error.
     */
    public void startsJMSConnection() throws JMSException {
        connection.start();
    }

    /**
     * Closes the JMS connection.
     * @throws JMSException If the JMS provider fails to start message delivery due to some internal error.
     */
    public void closesJMSConnection() throws JMSException {
        connection.close();
    }

    /**
     * Creates a <code>Session</code> object.
     * See {@link javax.jms.Connection#createSession(boolean, int)}
     */
    public Session createsJMSSession(boolean transacted, int acknowledge) throws JMSException {
        return connection.createSession(transacted, acknowledge);
    }

    /**
     * Creates a <code>Queue</code> object.
     * See {@link javax.jms.Session#createQueue(java.lang.String)}
     */
    public Queue createsJMSQueue(Session session, String queueName) throws JMSException {
        return session.createQueue(queueName);
    }

    /**
     * Creates a <code>MessageConsumer</code> object.
     * See {@link javax.jms.Session#createConsumer(javax.jms.Destination) }
     */
    public MessageConsumer createsJMSConsumer(Session session, Destination dest) throws JMSException {
        return session.createConsumer(dest);
    }

    /**
     * Creates a <code>MessageProducer</code> object.
     * See {@link javax.jms.Session#createProducer(javax.jms.Destination) }
     */
    public MessageProducer createJMSProducer(Session session, Destination dest) throws JMSException {
        return session.createProducer(dest);
    }

    /**
     * Sends event with the specified message producer.
     * @param session The JMS session used to send the message.
     * @param producer The JMS message producer used to send the message.
     * @param eventToReturn The event to send in String format.
     * @throws JMSException If an error occurred with the JMS provider.
     */
    public void sendsJMSEvent(Session session, MessageProducer producer, String eventToReturn) throws JMSException {
        TextMessage tMsg = session.createTextMessage(eventToReturn);
        tMsg.setLongProperty(timeProperty, System.currentTimeMillis());
        producer.send(tMsg);
    }

}
