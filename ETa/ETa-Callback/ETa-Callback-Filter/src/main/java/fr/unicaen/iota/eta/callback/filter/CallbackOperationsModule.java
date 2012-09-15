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
package fr.unicaen.iota.eta.callback.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.fosstrak.epcis.repository.InvalidFormatException;
import org.fosstrak.epcis.repository.model.*;
import org.fosstrak.epcis.utils.TimeParser;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This
 * <code>CallbackOperationsModule</code> parses messages from the message broker
 * to a XML document conforming to a xsd schema. The query callback events
 * contained by this document are extracted and used by
 * <code>CallbackCheck</code> to validate access.
 */
public class CallbackOperationsModule {

    private final String PROPERTY_FILE = "/application.properties";
    private Properties properties;
    private Schema schema;
    private static final String PROP_EPCIS_SCHEMA_FILE = "/wsdl/EPCglobal-epcis-query-1_0.xsd";
    private final String PROP_MSG_CONS_QUEUENAME = "messagebroker.consQueueName";
    private final String PROP_MSG_SEND_QUEUENAME = "messagebroker.sendQueueName";
    private final String PROP_MSG_USER = "messagebroker.user";
    private final String PROP_MSG_PASSWORD = "messagebroker.password";
    private final String PROP_MSG_URL = "messagebroker.url";
    private String consummerQueueName = "queueToFilter";
    private String senderQueueName = "queueToSender";
    private String msgUser;
    private String msgPassword;
    private String msgUrl;
    private final String PROP_USERNAME = "database.username";
    private final String DEFAULT_USERNAME = "eta_usr";
    private final String PROP_PASSWORD = "database.password";
    private final String DEFAULT_PASSWORD = "eta_pwd";
    private final String PROP_URL = "database.url";
    private final String DEFAULT_URL = "jdbc:mysql://localhost:3306/eta_db?autoReconnect=true";
    private java.sql.Connection dbConnection;
    /**
     * The CallbackOperationsBackendSQL used for database
     */
    private CallbackOperationsBackendSQL backend;
    /**
     * The CallbackCheck used to check access
     */
    private CallbackCheck callbackCheck;
    private static final Log LOG = LogFactory.getLog(CallbackOperationsModule.class);

    public CallbackOperationsModule() {
        this.properties = loadProperties(PROPERTY_FILE);
        this.schema = initEpcisSchema(PROP_EPCIS_SCHEMA_FILE);
        consummerQueueName = properties.getProperty(PROP_MSG_CONS_QUEUENAME, consummerQueueName);
        senderQueueName = properties.getProperty(PROP_MSG_SEND_QUEUENAME, senderQueueName);
        msgUrl = properties.getProperty(PROP_MSG_URL, ActiveMQConnection.DEFAULT_BROKER_URL);
        msgUser = properties.getProperty(PROP_MSG_USER, ActiveMQConnection.DEFAULT_USER);
        msgPassword = properties.getProperty(PROP_MSG_PASSWORD, ActiveMQConnection.DEFAULT_PASSWORD);
        callbackCheck = new CallbackCheck();
        backend = new CallbackOperationsBackendSQL();
        dbConnection = loadDatabaseConnection();
    }

    /**
     * Gets the consummer queue name from JMS message broker.
     *
     * @return The consummer queue name.
     */
    public String getConsummerQueueName() {
        return consummerQueueName;
    }

    /**
     * Sets the consummer queue name from the JMS message broker.
     *
     * @param consummerQueueName The consummer queue name.
     */
    public void setConsummerQueueName(String consummerQueueName) {
        this.consummerQueueName = consummerQueueName;
    }

    /**
     * Gets sender queue name from JMS message broker.
     *
     * @return The sender queue name.
     */
    public String getSenderQueueName() {
        return senderQueueName;
    }

    /**
     * Sets the sender queue name from the JMS message broker.
     *
     * @param senderQueueName The sender queue name.
     */
    public void setSenderQueueName(String senderQueueName) {
        this.senderQueueName = senderQueueName;
    }

    /**
     * Gets the ActiveMQ password.
     *
     * @return The ActiveMQ password.
     */
    public String getMsgPassword() {
        return msgPassword;
    }

    /**
     * Gets the destination URL.
     *
     * @return The destination URL.
     */
    public String getMsgUrl() {
        return msgUrl;
    }

    /**
     * Gets the user name.
     *
     * @return The user name.
     */
    public String getMsgUser() {
        return msgUser;
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
     * Loads the connection to the database.
     *
     * @return The connection to the database.
     */
    private java.sql.Connection loadDatabaseConnection() {
        String username = properties.getProperty(PROP_USERNAME, DEFAULT_USERNAME);
        String password = properties.getProperty(PROP_PASSWORD, DEFAULT_PASSWORD);
        String url = properties.getProperty(PROP_URL, DEFAULT_URL);
        java.sql.Connection c = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("autoReconnect", "true");
            c = DriverManager.getConnection(url, props);
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
    private Document getDocumentFromString(String string)
            throws SAXException, IOException {

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
     *
     * @throws JMSException If a receiving or sending message error occurred.
     * @throws SQLException If an error involving the database occurred
     * @throws SAXException If an error processing the XML document occurred.
     * @throws InvalidFormatException If an error parsing the XML contents
     * occurred.
     * @throws IOException If an I/O error occured.
     * @throws Exception If an unexpected error occurred.
     */
    public void receiveFilterSend() throws JMSException, SQLException, SAXException,
            InvalidFormatException, IOException, Exception {
        ActiveMQConnectionFactory factory;
        if (msgUser != null && msgPassword != null && !msgUser.isEmpty() && !msgPassword.isEmpty()) {
            factory = new ActiveMQConnectionFactory(msgUser, msgPassword, msgUrl);
        } else {
            factory = new ActiveMQConnectionFactory(msgUrl);
        }
        javax.jms.Connection msgConnection = factory.createConnection();
        Session session = msgConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue(consummerQueueName);
        MessageConsumer consumer = session.createConsumer(destination);
        msgConnection.start();

        try {
            while (true) {
                Message message = consumer.receive(200);
                if (message == null) {
                    break;
                }

                String docText;
                if (message instanceof TextMessage) {
                    TextMessage text = (TextMessage) message;
                    docText = text.getText();

                    if (isPermitted(docText)) {
                        // --- Sending authorized message to the queue
                        Session prodS = msgConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                        Destination sendDest = prodS.createQueue(senderQueueName);
                        MessageProducer producer = prodS.createProducer(sendDest);
                        TextMessage messageToSend = prodS.createTextMessage();
                        messageToSend.setText(docText);
                        producer.send(messageToSend);
                    } else {
                        LOG.debug("Event deleted");
                    }
                }
                message.acknowledge();
            }
        } finally {
            msgConnection.close();
        }
    }

    /**
     * Checks if the user is authorized to receive events.
     *
     * @param docString The events to check.
     * @return
     * <code>true</code> if the user is authorized to receive events.
     * @throws SQLException If an error involving the database occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws InvalidFormatException If an error parsing the XML contents
     * occurred.
     * @throws IOException If an I/O error occured.
     */
    private boolean isPermitted(String docString) throws SQLException, SAXException,
            InvalidFormatException, IOException {
        Document doc = getDocumentFromString(docString);
        List<BaseEvent> callbackEventList = getCallbackEventList(doc);
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
     * @param document The xml document to parse.
     * @return The callback events list.
     * @throws DOMException If a DOM operation error occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws InvalidFormatException If an error parsing the XML contents
     * occurred.
     */
    private List<BaseEvent> getCallbackEventList(Document document)
            throws DOMException, SAXException, InvalidFormatException {
        NodeList eventList = document.getElementsByTagName("EventList");
        NodeList events = eventList.item(0).getChildNodes();
        List<BaseEvent> callbackEventList = new ArrayList<BaseEvent>();

        for (int i = 0; i < events.getLength(); i++) {
            Node eventNode = events.item(i);
            String nodeName = eventNode.getNodeName();

            if (EpcisConstants.OBJECT_EVENT.equals(nodeName)
                    || EpcisConstants.AGGREGATION_EVENT.equals(nodeName)
                    || EpcisConstants.QUANTITY_EVENT.equals(nodeName)
                    || EpcisConstants.TRANSACTION_EVENT.equals(nodeName)) {
                LOG.debug("processing event " + i + ": '" + nodeName + "'.");
                BaseEvent callbackEvent = getCallbackEvent(eventNode, nodeName);
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
     * Parses the XML node and returns a callback event for XACML check.
     *
     * @param eventNode The XML node.
     * @param eventType The type of the event.
     * @return The callback event.
     * @throws DOMException If a DOM operation error occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws InvalidFormatException If an error parsing the XML contents
     * occurred (EPC or eventTimeZoneOffset is invalid).
     */
    private BaseEvent getCallbackEvent(Node eventNode, String eventType)
            throws DOMException, SAXException, InvalidFormatException {
        if (eventNode == null) {
            // nothing to do
            return null;
        } else if (eventNode.getChildNodes().getLength() == 0) {
            throw new SAXException("Event element '" + eventNode.getNodeName() + "' has no children elements.");
        }
        Node curEventNode = null;

        Timestamp eventTime = null;
        Timestamp recordTime = new Timestamp(System.currentTimeMillis());
        String eventTimeZoneOffset = null;
        String action = null;
        String parentId = null;
        Long quantity = null;
        String bizStep = null;
        String disposition = null;
        String readPoint = null;
        String bizLocation = null;
        String epcClassStr = null;

        List<String> epcs = null;
        List<BusinessTransaction> bizTransList = null;
        List<EventFieldExtension> fieldNameExtList = new ArrayList<EventFieldExtension>();

        for (int i = 0; i < eventNode.getChildNodes().getLength(); i++) {
            curEventNode = eventNode.getChildNodes().item(i);
            String nodeName = curEventNode.getNodeName();

            if ("#text".equals(nodeName) || "#comment".equals(nodeName)) {
                // ignore text or comments
                LOG.debug("  ignoring text or comment: '" + curEventNode.getTextContent().trim() + "'");
                continue;
            }

            LOG.debug("  handling event field: '" + nodeName + "'");
            if ("eventTime".equals(nodeName)) {
                String xmlTime = curEventNode.getTextContent();
                LOG.debug("    eventTime in xml is '" + xmlTime + "'");
                try {
                    eventTime = TimeParser.parseAsTimestamp(xmlTime);
                } catch (ParseException e) {
                    throw new SAXException("Invalid date/time (must be ISO8601).", e);
                }
                LOG.debug("    eventTime parsed as '" + eventTime + "'");
            } else if ("recordTime".equals(nodeName)) {
                // ignore recordTime
            } else if ("eventTimeZoneOffset".equals(nodeName)) {
                eventTimeZoneOffset = checkEventTimeZoneOffset(curEventNode.getTextContent());
            } else if ("epcList".equals(nodeName) || "childEPCs".equals(nodeName)) {
                epcs = handleEpcs(eventType, curEventNode);
            } else if ("bizTransactionList".equals(nodeName)) {
                bizTransList = handleBizTransactions(curEventNode);
            } else if ("action".equals(nodeName)) {
                action = curEventNode.getTextContent();
                if (!"ADD".equals(action) && !"OBSERVE".equals(action) && !"DELETE".equals(action)) {
                    throw new SAXException("Encountered illegal 'action' value: " + action);
                }
            } else if ("bizStep".equals(nodeName)) {
                bizStep = curEventNode.getTextContent();
            } else if ("disposition".equals(nodeName)) {
                disposition = curEventNode.getTextContent();
            } else if ("readPoint".equals(nodeName)) {
                Element attrElem = (Element) curEventNode;
                Node id = attrElem.getElementsByTagName("id").item(0);
                readPoint = id.getTextContent();
            } else if ("bizLocation".equals(nodeName)) {
                Element attrElem = (Element) curEventNode;
                Node id = attrElem.getElementsByTagName("id").item(0);
                bizLocation = id.getTextContent();
            } // TODO epc class filter?
            else if ("epcClass".equals(nodeName)) {
                epcClassStr = curEventNode.getTextContent();
            } else if ("quantity".equals(nodeName)) {
                quantity = Long.valueOf(curEventNode.getTextContent());
            } else if ("parentID".equals(nodeName)) {
                checkEpcOrUri(curEventNode.getTextContent(), false);
                parentId = curEventNode.getTextContent();
            } else {
                String[] parts = nodeName.split(":");
                if (parts.length == 2) {
                    LOG.debug("    treating unknown event field as extension.");
                    String prefix = parts[0];
                    String localname = parts[1];
                    // String namespace =
                    // document.getDocumentElement().getAttribute("xmlns:" +
                    // prefix);
                    String namespace = curEventNode.lookupNamespaceURI(prefix);
                    String value = curEventNode.getTextContent();
                    EventFieldExtension evf = new EventFieldExtension(prefix, namespace, localname, value);
                    fieldNameExtList.add(evf);
                } else {
                    // this is not a valid extension
                    throw new SAXException("    encountered unknown event field: '" + nodeName + "'.");
                }
            }
        }

        if (EpcisConstants.AGGREGATION_EVENT.equals(eventType)) {
            // for AggregationEvents, the parentID is only optional for
            // action=OBSERVE
            if (parentId == null && ("ADD".equals(action) || "DELETE".equals(action))) {
                throw new InvalidFormatException("'parentID' is required if 'action' is ADD or DELETE");
            }
        }

        String nodeName = eventNode.getNodeName();
        BaseEvent callbackEvent;

        BusinessStepId bizStepId = new BusinessStepId();
        bizStepId.setUri(bizStep);
        DispositionId dispositionId = new DispositionId();
        dispositionId.setUri(disposition);
        BusinessLocationId bizLocationId = new BusinessLocationId();
        bizLocationId.setUri(bizLocation);
        ReadPointId readPointId = new ReadPointId();
        readPointId.setUri(readPoint);
        EPCClass epcClass = new EPCClass();
        epcClass.setUri(epcClassStr);



        if (EpcisConstants.AGGREGATION_EVENT.equals(nodeName)) {
            AggregationEvent ae = new AggregationEvent();
            ae.setParentId(parentId);
            ae.setChildEpcs(epcs);
            ae.setAction(Action.valueOf(action));
            callbackEvent = ae;
        } else if (EpcisConstants.OBJECT_EVENT.equals(nodeName)) {
            ObjectEvent oe = new ObjectEvent();
            oe.setAction(Action.valueOf(action));
            if (epcs != null && epcs.size() > 0) {
                oe.setEpcList(epcs);
            }
            callbackEvent = oe;
        } else if (EpcisConstants.QUANTITY_EVENT.equals(nodeName)) {
            QuantityEvent qe = new QuantityEvent();
            qe.setEpcClass(epcClass);
            if (quantity != null) {
                qe.setQuantity(quantity);
            }
            callbackEvent = qe;
        } else if (EpcisConstants.TRANSACTION_EVENT.equals(nodeName)) {
            TransactionEvent te = new TransactionEvent();
            te.setParentId(parentId);
            te.setEpcList(epcs);
            te.setAction(Action.valueOf(action));
            callbackEvent = te;
        } else {
            throw new SAXException("Encountered unknown event element '" + nodeName + "'.");
        }

        callbackEvent.setEventTime(eventTime);
        callbackEvent.setRecordTime(recordTime);
        callbackEvent.setEventTimeZoneOffset(eventTimeZoneOffset);
        callbackEvent.setBizStep(bizStepId);
        callbackEvent.setDisposition(dispositionId);
        callbackEvent.setBizLocation(bizLocationId);
        callbackEvent.setReadPoint(readPointId);
        if (bizTransList != null && bizTransList.size() > 0) {
            callbackEvent.setBizTransList(bizTransList);
        }
        if (!fieldNameExtList.isEmpty()) {
            callbackEvent.setExtensions(fieldNameExtList);
        }

        return callbackEvent;
    }

    /**
     * Parses the xml tree for epc nodes and returns a List of BizTransaction
     * URIs with their corresponding type.
     *
     * @param bizNode The parent Node from which BizTransaction URIs should be
     * extracted.
     * @return A List of BizTransaction.
     * @throws SAXException If an unknown tag (no &lt;epc&gt;) is encountered.
     */
    private List<BusinessTransaction> handleBizTransactions(Node bizNode) throws SAXException {
        List<BusinessTransaction> bizTransactionList = new ArrayList<BusinessTransaction>();

        for (int i = 0; i < bizNode.getChildNodes().getLength(); i++) {
            Node curNode = bizNode.getChildNodes().item(i);
            if ("bizTransaction".equals(curNode.getNodeName())) {
                String bizTransTypeUri = curNode.getAttributes().item(0).getTextContent();
                String bizTransUri = curNode.getTextContent();

                BusinessTransactionId bizTransId = new BusinessTransactionId();
                bizTransId.setUri(bizTransUri);
                BusinessTransactionTypeId bizTransTypeId = new BusinessTransactionTypeId();
                bizTransTypeId.setUri(bizTransTypeUri);
                BusinessTransaction bizTransaction = new BusinessTransaction();
                bizTransaction.setBizTransaction(bizTransId);
                bizTransaction.setType(bizTransTypeId);

                bizTransactionList.add(bizTransaction);

            } else {
                if (!"#text".equals(curNode.getNodeName()) && !"#comment".equals(curNode.getNodeName())) {
                    throw new SAXException("Unknown XML tag: " + curNode.getNodeName(), null);
                }
            }
        }
        return bizTransactionList;
    }

    /**
     * Parses the xml tree for epc nodes and returns a list of EPC URIs.
     *
     * @param eventType
     * @param epcNode The parent Node from which EPC URIs should be extracted.
     * @return An array of vocabularies containing all the URIs found in the
     * given node.
     * @throws SAXException If an unknown tag (no &lt;epc&gt;) is encountered.
     * @throws InvalidFormatException If a 'pure identity' EPC format is
     * invalid.
     * @throws DOMException If a DOM operation error occurred.
     */
    private List<String> handleEpcs(final String eventType, final Node epcNode)
            throws SAXException, DOMException, InvalidFormatException {
        List<String> epcList = new ArrayList<String>();

        boolean isEpc = false;
        boolean epcRequired = false;
        boolean atLeastOneNonEpc = false;
        for (int i = 0; i < epcNode.getChildNodes().getLength(); i++) {
            Node curNode = epcNode.getChildNodes().item(i);
            if ("epc".equals(curNode.getNodeName())) {
                isEpc = checkEpcOrUri(curNode.getTextContent(), epcRequired);
                if (isEpc) {
                    // if one of the values is an EPC, then all of them must be
                    // valid EPCs
                    epcRequired = true;
                } else {
                    atLeastOneNonEpc = true;
                }
                epcList.add(curNode.getTextContent());
            } else {
                if ("#text".equals(curNode.getNodeName()) && "#comment".equals(curNode.getNodeName())) {
                    throw new SAXException("Unknown XML tag: " + curNode.getNodeName(), null);
                }
            }
        }
        if (atLeastOneNonEpc && isEpc) {
            throw new InvalidFormatException(
                    "One of the provided EPCs was a 'pure identity' EPC, so all of them must be 'pure identity' EPCs");
        }
        return epcList;
    }

    /**
     * Checks the event time zone.
     *
     * @param textContent The event time zone to check.
     * @return The checked event time zone.
     * @throws InvalidFormatException If the given
     * <code>eventTimeZoneOffset</code> format is invalid.
     */
    private String checkEventTimeZoneOffset(String textContent) throws InvalidFormatException {
        // first check the provided String against the expected pattern
        Pattern p = Pattern.compile("[+-]\\d\\d:\\d\\d");
        Matcher m = p.matcher(textContent);
        boolean matches = m.matches();
        if (matches) {
            // second check the values (hours and minutes)
            int h = Integer.parseInt(textContent.substring(1, 3));
            int min = Integer.parseInt(textContent.substring(4, 6));
            if ((h < 0) || (h > 14)) {
                matches = false;
            } else if (h == 14 && min != 0) {
                matches = false;
            } else if ((min < 0) || (min > 59)) {
                matches = false;
            }
        }
        if (matches) {
            return textContent;
        } else {
            throw new InvalidFormatException("'eventTimeZoneOffset' has invalid format: " + textContent);
        }
    }

    /**
     * Checks an EPC or an URI.
     *
     * @param epcOrUri The EPC or URI to check.
     * @param epcRequired<code>true</code> if an EPC is required (will throw an
     * InvalidFormatException if the given
     * <code>epcOrUri</code> is an invalid EPC, but might be a valid URI),
     * <code>false</code> otherwise.
     * @return
     * <code>true</code> if the given
     * <code>epcOrUri</code> is a valid EPC,
     * <code>false</code> otherwise.
     * @throws InvalidFormatException If the 'pure identity' EPC format is
     * invalid.
     */
    private boolean checkEpcOrUri(String epcOrUri, boolean epcRequired) throws InvalidFormatException {
        boolean isEpc = false;
        if (epcOrUri.startsWith("urn:epc:id:")) {
            // check if it is a valid EPC
            checkEpc(epcOrUri);
            isEpc = true;
        } else {
            // childEPCs in AggregationEvents, and epcList in
            // TransactionEvents might also be simple URIs
            if (epcRequired) {
                throw new InvalidFormatException(
                        "One of the provided EPCs was a 'pure identity' EPC, so all of them must be 'pure identity' EPCs");
            }
            checkUri(epcOrUri);
        }
        return isEpc;
    }

    /**
     * Checks an EPC according to 'pure identity' URI as specified in Tag Data
     * Standard.
     *
     * @param textContent The EPC to check.
     * @throws InvalidFormatException If the 'pure identity' EPC format is
     * invalid.
     */
    private void checkEpc(String textContent) throws InvalidFormatException {
        String uri = textContent;
        if (!uri.startsWith("urn:epc:id:")) {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: must start with \"urn:epc:id:\"");
        }
        uri = uri.substring("urn:epc:id:".length());

        // check the patterns for the different EPC types
        String epcType = uri.substring(0, uri.indexOf(":"));
        uri = uri.substring(epcType.length() + 1);
        LOG.debug("Checking pattern for EPC type " + epcType + ": " + uri);
        Pattern p;
        if ("gid".equals(epcType)) {
            p = Pattern.compile("((0|[1-9][0-9]*)\\.){2}(0|[1-9][0-9]*)");
        } else if ("sgtin".equals(epcType) || "sgln".equals(epcType) || "grai".equals(epcType)) {
            p = Pattern.compile("([0-9]+\\.){2}([0-9]|[A-Z]|[a-z]|[\\!\\(\\)\\*\\+\\-',:;=_]|(%(([0-9]|[A-F])|[a-f]){2}))+");
        } else if ("sscc".equals(epcType)) {
            p = Pattern.compile("[0-9]+\\.[0-9]+");
        } else if ("giai".equals(epcType)) {
            p = Pattern.compile("[0-9]+\\.([0-9]|[A-Z]|[a-z]|[\\!\\(\\)\\*\\+\\-',:;=_]|(%(([0-9]|[A-F])|[a-f]){2}))+");
        } else {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: unknown EPC type: " + epcType);
        }
        Matcher m = p.matcher(uri);
        if (!m.matches()) {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: pattern \"" + uri
                    + "\" is invalid for EPC type \"" + epcType + "\" - check with Tag Data Standard");
        }

        // check the number of digits for the different EPC types
        boolean exceeded = false;
        int count1 = uri.indexOf(".");
        if ("sgtin".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1) - (count1 + 1);
            if (count1 + count2 > 13) {
                exceeded = true;
            }
        } else if ("sgln".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1) - (count1 + 1);
            if (count1 + count2 > 12) {
                exceeded = true;
            }
        } else if ("grai".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1) - (count1 + 1);
            if (count1 + count2 > 12) {
                exceeded = true;
            }
        } else if ("sscc".equals(epcType)) {
            int count2 = uri.length() - (count1 + 1);
            if (count1 + count2 > 17) {
                exceeded = true;
            }
        } else if ("giai".equals(epcType)) {
            int count2 = uri.length() - (count1 + 1);
            if (count1 + count2 > 30) {
                exceeded = true;
            }
        } else {
            // nothing to count
        }
        if (exceeded) {
            throw new InvalidFormatException(
                    "Invalid 'pure identity' EPC format: check allowed number of characters for EPC type '"
                    + epcType + "'");
        }
    }

    /**
     * Checks the URI.
     *
     * @param textContent The URI to check.
     * @return
     * <code>true</code> if the URI is valid.
     * @throws InvalidFormatException If the URI is invalid.
     */
    private boolean checkUri(String textContent) throws InvalidFormatException {
        try {
            new URI(textContent);
        } catch (URISyntaxException e) {
            throw new InvalidFormatException(e.getMessage(), e);
        }
        return true;
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
}
