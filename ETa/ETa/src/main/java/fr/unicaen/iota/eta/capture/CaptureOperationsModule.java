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
package fr.unicaen.iota.eta.capture;

import fr.unicaen.iota.eta.constants.Constants;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.captureclient.CaptureClient;
import org.fosstrak.epcis.captureclient.CaptureClientException;
import org.fosstrak.epcis.model.VocabularyElementListType;
import org.fosstrak.epcis.model.VocabularyElementType;
import org.fosstrak.epcis.model.VocabularyType;
import org.fosstrak.epcis.repository.EpcisConstants;
import org.fosstrak.epcis.repository.InternalBusinessException;
import org.fosstrak.epcis.repository.InvalidFormatException;
import org.fosstrak.epcis.repository.model.*;
import org.fosstrak.epcis.utils.TimeParser;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This CaptureOperationsModule parses an input payload to a XML document
 * conforming to a xsd schema. The capture events contained by this document are
 * extracted and used by CaptureCheck to validate access.
 */
public class CaptureOperationsModule {

    private static final Log LOG = LogFactory.getLog(CaptureOperationsModule.class);
    /**
     * The schema used by the parser.
     */
    private Schema schema;
    /**
     * The XSD schema which validates the Masterdata incoming messages.
     */
    private Schema masterDataSchema;
    /**
     * The CaptureCheck used to check access.
     */
    private CaptureCheck captureCheck;
    /**
     * The client used to capture EPCIS events or masterdata.
     */
    private CaptureClient epcisCaptureClient;

    public CaptureCheck getCaptureCheck() {
        return captureCheck;
    }

    public void setCaptureCheck(CaptureCheck captureCheck) {
        this.captureCheck = captureCheck;
    }

    public CaptureClient getEpcisCaptureClient() {
        return epcisCaptureClient;
    }

    public void setEpcisCaptureClient(CaptureClient epcisCaptureClient) {
        this.epcisCaptureClient = epcisCaptureClient;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public void setEpcisSchemaFile(String epcisSchemaFile) {
        Schema schemaTmp = initEpcisSchema(epcisSchemaFile);
        setSchema(schemaTmp);
    }

    public Schema getMasterDataSchema() {
        return masterDataSchema;
    }

    public void setMasterDataSchema(Schema masterDataSchema) {
        this.masterDataSchema = masterDataSchema;
    }

    public void setEpcisMasterDataSchemaFile(String epcisMasterdataSchemaFile) {
        Schema masterDataSchemaTmp = initEpcisSchema(epcisMasterdataSchemaFile);
        setMasterDataSchema(masterDataSchemaTmp);
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
                schemaSrc.setSystemId(CaptureOperationsServlet.class.getResource(xsdFile).toString());
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
     * Configures the EPCIS capture client.
     *
     * @throws IOException
     */
    public void configureEpcisCaptureClient() throws IOException {
        if (epcisCaptureClient == null) {
            epcisCaptureClient = new CaptureClient(Constants.EPCIS_CAPTURE_URL);
        }
    }

    /**
     * Performs database reset by querying EPCIS.
     *
     * @param rsp The HTTP response
     * @throws IOException if an error occurred while configuring EPCIS capture
     * client.
     */
    public void doDbReset(final HttpServletResponse rsp, final PrintWriter out) throws IOException, Exception {
        String msg;
        configureEpcisCaptureClient();
        int response = epcisCaptureClient.dbReset();
        switch (response) {
            case HttpServletResponse.SC_OK:
                msg = "db reset successfull";
                LOG.debug(msg);
                rsp.setStatus(HttpServletResponse.SC_OK);
                out.println(msg);
                break;

            /*
             * TODO database error case
             * HttpServletResponse.SC_INTERNAL_SERVER_ERROR: String msg = "An
             * error involving the database occurred"; //LOG.error(msg, e);
             * rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
             * out.println(msg); break;
             */

            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                msg = "An unexpected error occurred";
                LOG.error(msg);
                rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.println(msg);
                break;

            case HttpServletResponse.SC_FORBIDDEN:
                msg = "'dbReset' operation not allowed!";
                LOG.debug(msg);
                rsp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println(msg);
                break;
        }
    }

    /**
     * Performs EPCIS capture after XACML check.
     *
     * @param in The input to process.
     * @param rsp The response.
     * @throws SAXException If the document parsing failed.
     * @throws IOException If an error occurred while validating the request or
     * writing the response.
     * @throws InternalBusinessException If unable to read from input.
     */
    public void doCapture(InputStream in, HttpServletResponse rsp) throws SAXException, IOException, InternalBusinessException {
        Document document = null;
        try {
            // parse the input into a DOM
            document = parseInput(in, null);

            // validate incoming document against its schema
            if (isEPCISDocument(document)) {
                validateDocument(document, schema);
            } else if (isEPCISMasterDataDocument(document)) {
                validateDocument(document, masterDataSchema);
            }
        } catch (IOException e) {
            throw new InternalBusinessException("unable to read from input: " + e.getMessage(), e);
        }
        PrintWriter out = rsp.getWriter();
        String msg;
        try {
            if (isEPCISDocument(document)) {
                processEvents(document, rsp);
            } else if (isEPCISMasterDataDocument(document)) {
                processMasterData(document, rsp);
            }
        } catch (SAXException ex) {
            msg = "An error processing the XML document occurred";
            LOG.error(msg, ex);
            rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(msg);
        } catch (InvalidFormatException ex) {
            msg = "An invalid format error occurred";
            LOG.error(msg, ex);
            rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(msg);
        } catch (FileNotFoundException ex) {
            msg = "An internal error occurred";
            LOG.error(msg, ex);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(msg);
        } catch (IOException ex) {
            msg = "An unexpected error occurred";
            LOG.error(msg, ex);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(msg);
        } catch (Exception ex) {
            msg = "An unexpected error occurred";
            LOG.error(msg, ex);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(msg);
        }
    }

    /**
     * Parses the input into a DOM. If a schema is given, the input is also
     * validated against this schema. The schema may be null.
     *
     * @param in The input to parse.
     * @param schema The schema to validate the input.
     * @return The DOM document extracted from the input.
     * @throws InternalBusinessException If unable to configure document
     * builder.
     * @throws SAXException If the document parsing failed.
     * @throws IOException If the document parsing failed.
     */
    private Document parseInput(InputStream in, Schema schema) throws InternalBusinessException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setSchema(schema);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {

                @Override
                public void warning(SAXParseException e) throws SAXException {
                    LOG.warn("warning while parsing XML input: " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    LOG.error("non-recovarable error while parsing XML input: " + e.getMessage());
                    throw e;
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    LOG.error("error while parsing XML input: " + e.getMessage());
                    throw e;
                }
            });
            Document document = builder.parse(in);
            LOG.debug("payload successfully parsed as XML document");
            return document;
        } catch (ParserConfigurationException e) {
            throw new InternalBusinessException("unable to configure document builder to parse XML input", e);
        }
    }

    /**
     * Validates the given document against the given schema.
     *
     * @param document The DOM document to validate.
     * @param schema The schema.
     */
    private void validateDocument(Document document, Schema schema) throws SAXException, IOException {
        if (schema != null) {
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(document));
            LOG.debug("Incoming capture request was successfully validated against the EPCISDocument schema");
        } else {
            LOG.warn("Schema validator unavailable. Unable to validate EPCIS capture event against schema!");
        }
    }

    /**
     * @return
     * <code>true</code> if the given Document is an <i>EPCISDocument</i>.
     */
    private boolean isEPCISDocument(Document document) {
        return document.getDocumentElement().getLocalName().equals("EPCISDocument");
    }

    /**
     * @return
     * <code>true</code> if the given Document is an
     * <i>EPCISMasterDataDocument</i>.
     */
    private boolean isEPCISMasterDataDocument(Document document) {
        return document.getDocumentElement().getLocalName().equals("EPCISMasterDataDocument");
    }

    /**
     * Processes the given document, performs access control and sends this
     * document to the EPCIS if permited.
     *
     * @param document The document to capture.
     * @param rsp The HTTP response.
     * @throws IOException If unable to configure EPCIS capture client.
     * @throws DOMException If an error processing XML nodes occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws InvalidFormatException If an error parsing the XML document
     * occurred.
     * @throws TransformerConfigurationException If an error while transforming
     * document occurred.
     * @throws TransformerException If an error while transforming document
     * occurred.
     * @throws CaptureClientException If the capture failed.
     * @throws Exception If an unexpected error occurred.
     */
    private void processEvents(Document document, HttpServletResponse rsp) throws IOException, DOMException, SAXException,
            InvalidFormatException, TransformerConfigurationException, TransformerException, CaptureClientException, Exception {
        List<BaseEvent> captureEventList = extractsCaptureEventList(document);
        PrintWriter out = rsp.getWriter();
        String msg;

        // XACML check start
        LOG.debug("START OF XACML check");
        //TODO reset allowed after tests
        // TODO user + owner
        String user = "anonymous";
        String owner = "anonymous";
        if (captureCheck == null) {
            captureCheck = new CaptureCheck();
        }
        boolean allowed = captureCheck.xacmlCheck(captureEventList, user, owner);
        if (allowed) {
            msg = "XACML check result: PERMITTED";
            LOG.debug(msg);
            // EPCIS operations start !
            LOG.debug("START OF EPCIS OPERATION");
            configureEpcisCaptureClient();
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            String stringDocument = writer.toString();

            int response = epcisCaptureClient.capture(stringDocument);

            switch (response) {
                case HttpServletResponse.SC_OK:
                    rsp.setStatus(HttpServletResponse.SC_OK);
                    msg = "Capture request succeeded";
                    LOG.debug(msg);
                    out.println();
                    break;
                case HttpServletResponse.SC_BAD_REQUEST:
                    msg = "An error processing the XML document occurred";
                    LOG.error(msg);
                    rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println(msg);
                    break;
                case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                    msg = "An unexpected error occurred";
                    LOG.error(msg);
                    rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println(msg);
                    break;
                default:
                    msg = "An unexpected error occurred";
                    LOG.error(msg);
                    rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println(msg);
                    break;
            }
            LOG.debug("END OF EPCIS OPERATION");
        } else {
            LOG.debug("END OF EPCIS OPERATION");
            msg = "XACML check result: DENIED";
            LOG.debug(msg);
            rsp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println(msg);
        }
    }

    /**
     * Parses a XML document and returns associated capture events list.
     *
     * @param document The XML document to parse.
     * @return The capture events list from the document.
     * @throws DOMException If an error processing XML nodes occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws InvalidFormatException If an error parsing the XML document
     * occurred.
     */
    private List<BaseEvent> extractsCaptureEventList(Document document)
            throws DOMException, SAXException, InvalidFormatException {
        NodeList eventList = document.getElementsByTagName("EventList");
        NodeList events = eventList.item(0).getChildNodes();
        List<BaseEvent> captureEventList = new ArrayList<BaseEvent>();

        for (int i = 0; i < events.getLength(); i++) {
            Node eventNode = events.item(i);
            String nodeName = eventNode.getNodeName();

            if (EpcisConstants.OBJECT_EVENT.equals(nodeName)
                    || EpcisConstants.AGGREGATION_EVENT.equals(nodeName)
                    || EpcisConstants.QUANTITY_EVENT.equals(nodeName)
                    || EpcisConstants.TRANSACTION_EVENT.equals(nodeName)) {
                LOG.debug("processing event " + i + ": '" + nodeName + "'.");
                BaseEvent captureEvent = extractsCaptureEvent(eventNode, nodeName);
                if (captureEvent != null) {
                    captureEventList.add(captureEvent);
                }
            } else if (!"#text".equals(nodeName) && !"#comment".equals(nodeName)) {
                throw new SAXException("Encountered unknown event '" + nodeName + "'.");
            }
        }
        return captureEventList;
    }

    /**
     * Parses the XML node and returns capture event.
     *
     * @param eventNode The XML node.
     * @param eventType The type of the event.
     * @return The capture event.
     * @throws DOMException If an error processing the XML document occurred.
     * @throws SAXException If an error parsing the XML document occurred.
     * @throws InvalidFormatException If an error parsing the XML document
     * occurred.
     */
    private BaseEvent extractsCaptureEvent(Node eventNode, String eventType)
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
        BaseEvent captureEvent;

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
            captureEvent = ae;
        } else if (EpcisConstants.OBJECT_EVENT.equals(nodeName)) {
            ObjectEvent oe = new ObjectEvent();
            oe.setAction(Action.valueOf(action));
            if (epcs != null && epcs.size() > 0) {
                oe.setEpcList(epcs);
            }
            captureEvent = oe;
        } else if (EpcisConstants.QUANTITY_EVENT.equals(nodeName)) {
            QuantityEvent qe = new QuantityEvent();
            qe.setEpcClass(epcClass);
            if (quantity != null) {
                qe.setQuantity(quantity);
            }
            captureEvent = qe;
        } else if (EpcisConstants.TRANSACTION_EVENT.equals(nodeName)) {
            TransactionEvent te = new TransactionEvent();
            te.setParentId(parentId);
            te.setEpcList(epcs);
            te.setAction(Action.valueOf(action));
            captureEvent = te;
        } else {
            throw new SAXException("Encountered unknown event element '" + nodeName + "'.");
        }

        captureEvent.setEventTime(eventTime);
        captureEvent.setRecordTime(recordTime);
        captureEvent.setEventTimeZoneOffset(eventTimeZoneOffset);
        captureEvent.setBizStep(bizStepId);
        captureEvent.setDisposition(dispositionId);
        captureEvent.setBizLocation(bizLocationId);
        captureEvent.setReadPoint(readPointId);
        if (bizTransList != null && bizTransList.size() > 0) {
            captureEvent.setBizTransList(bizTransList);
        }
        if (!fieldNameExtList.isEmpty()) {
            captureEvent.setExtensions(fieldNameExtList);
        }

        return captureEvent;
    }

    /**
     * Parses the xml tree for epc nodes and returns a list of EPC URIs.
     *
     * @param eventType The event type.
     * @param epcNode The parent Node from which EPC URIs should be extracted.
     * @return An array of vocabularies containing all the URIs found in the
     * given node.
     * @throws SAXException If an unknown tag (no &lt;epc&gt;) is encountered.
     * @throws InvalidFormatException If an EPC is invalid.
     * @throws DOMException If an error processing the XML document occurred.
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
     * Parses the xml tree for epc nodes and returns a List of BizTransaction
     * URIs with their corresponding type.
     *
     * @param bizNode The parent Node from which BizTransaction URIs should be
     * extracted.
     * @return A list of BizTransaction.
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
     * Checks the event time zone.
     *
     * @param textContent The event time zone to check.
     * @return The checked event time zone.
     * @throws InvalidFormatException If the event time zone format is invalid.
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
     * @param epcOrUri The EPC or URI to check
     * @param epcRequired
     * <code>true</code> if an EPC is required (will throw an
     * InvalidFormatException if the given
     * <code>epcOrUri</code> is an invalid EPC, but might be a valid URI),
     * <code>false</code> otherwise.
     * @return
     * <code>true</code> if the given
     * <code>epcOrUri</code> is a valid EPC,
     * <code>false</code> otherwise.
     * @throws InvalidFormatException If the EPC is invalid.
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
                    "Invalid 'pure identity' EPC format: check allowed number of characters for EPC type '" + epcType + "'");
        }
    }

    /**
     * Checks the URI.
     *
     * @param textContent The URI to check.
     * @return
     * <code>true</code> if the URI is valid.
     * @throws InvalidFormatException If the URI format is invalid.
     */
    private boolean checkUri(String textContent) throws InvalidFormatException {
        try {
            new URI(textContent);
        } catch (URISyntaxException e) {
            throw new InvalidFormatException(e.getMessage(), e);
        }
        return true;
    }

    private void processMasterData(Document document, HttpServletResponse rsp) throws SAXException, IOException,
            CaptureClientException, TransformerConfigurationException, TransformerException {
        List<VocabularyType> vocList = extractsVocabularies(document);
        PrintWriter out = rsp.getWriter();
        String msg;

        // XACML check start
        LOG.debug("START OF XACML check");
        //TODO reset allowed after tests
        // TODO user + owner
        String user = "ppda";
        String owner = "test";
        if (captureCheck == null) {
            captureCheck = new CaptureCheck();
        }
        boolean allowed = captureCheck.xacmlCheckMasterD(vocList, user, owner);
        if (allowed) {
            msg = "XACML check result: PERMITTED";
            LOG.debug(msg);
            // EPCIS operations start !
            LOG.debug("START OF EPCIS OPERATION");
            configureEpcisCaptureClient();
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            String stringDocument = writer.toString();

            int response = epcisCaptureClient.capture(stringDocument);

            switch (response) {
                case HttpServletResponse.SC_OK:
                    rsp.setStatus(HttpServletResponse.SC_OK);
                    msg = "Capture request succeeded";
                    LOG.debug(msg);
                    out.println();
                    break;
                case HttpServletResponse.SC_BAD_REQUEST:
                    msg = "An error processing the XML document occurred";
                    LOG.error(msg);
                    rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println(msg);
                    break;
                case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                    msg = "An unexpected error occurred";
                    LOG.error(msg);
                    rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println(msg);
                    break;
                default:
                    msg = "An unexpected error occurred";
                    LOG.error(msg);
                    rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println(msg);
                    break;
            }
            LOG.debug("END OF EPCIS OPERATION");
        } else {
            LOG.debug("END OF EPCIS OPERATION");
            msg = "XACML check result: DENIED";
            LOG.debug(msg);
            rsp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println(msg);
        }
    }

    /**
     * Parses a XML document and returns associated Masterdatas list.
     *
     * @param document The XML document to parse.
     * @return The list of Masterdata.
     * @throws SAXException If a parsing error occurred.
     */
    private List<VocabularyType> extractsVocabularies(Document document) throws SAXException {
        NodeList vocabularyList = document.getElementsByTagName("VocabularyList");
        if (vocabularyList.item(0).hasChildNodes()) {
            List<VocabularyType> vocTypeList = new ArrayList<VocabularyType>();
            NodeList vocabularys = vocabularyList.item(0).getChildNodes();
            for (int i = 0; i < vocabularys.getLength(); i++) {
                Node vocabularyNode = vocabularys.item(i);
                String nodeName = vocabularyNode.getNodeName();
                if (nodeName.equals("Vocabulary")) {
                    String vocabularyType = vocabularyNode.getAttributes().getNamedItem("type").getNodeValue();
                    if (EpcisConstants.VOCABULARY_TYPES.contains(vocabularyType)) {
                        LOG.debug("processing " + i + ": '" + nodeName + "':" + vocabularyType + ".");
                        VocabularyType vocType = new VocabularyType();
                        VocabularyElementListType vocElementListType = new VocabularyElementListType();
                        List<VocabularyElementType> vocElementTypeList = vocElementListType.getVocabularyElement();
                        vocElementTypeList = extractsVocabulary(vocabularyNode, vocabularyType);
                        vocType.setVocabularyElementList(vocElementListType);
                        vocTypeList.add(vocType);
                    }
                } else if (!nodeName.equals("#text") && !nodeName.equals("#comment")) {
                    throw new SAXException("Encountered unknown vocabulary '" + nodeName + "'.");
                }
            }
        }
        return null;
    }

    /**
     * Parses the XML node and returns Masterdata.
     *
     * @param vocNode The XML node to parse.
     * @param vocType The type of vocabulary element.
     * @return Masterdata to capture.
     * @throws SAXException If a parsing error occurred.
     */
    private List<VocabularyElementType> extractsVocabulary(final Node vocNode, final String vocType) throws SAXException {
        if (vocNode == null) {
            // nothing to do
            return null;
        } else if (vocNode.getChildNodes().getLength() == 0) {
            throw new SAXException("Vocabulary element '" + vocNode.getNodeName() + "' has no children elements.");
        }
        List<VocabularyElementType> vocElementList = new ArrayList<VocabularyElementType>();
        for (int i = 0; i < vocNode.getChildNodes().getLength(); i++) {
            Node curVocNode = vocNode.getChildNodes().item(i);
            if (isTextOrComment(curVocNode)) {
                continue;
            }
            for (int j = 0; j < curVocNode.getChildNodes().getLength(); j++) {
                Node curVocElemNode = curVocNode.getChildNodes().item(j);
                if (isTextOrComment(curVocElemNode)) {
                    continue;
                }
                LOG.debug("  processing vocabulary '" + curVocElemNode.getNodeName() + "'");
                VocabularyElementType vocElementType = new VocabularyElementType();
                String curVocElemId = curVocElemNode.getAttributes().getNamedItem("id").getNodeValue();
                vocElementType.setId(curVocElemId);
                /*
                 * //vocabularyElementEditMode 1: insert((it can be anything
                 * except 2,3,4)) 2: alterURI 3: singleDelete 4: //Delete
                 * element with it's direct or indirect descendants
                 *
                 * String vocElemEditMode = "";
                 *
                 * if (!(curVocElemNode.getAttributes().getNamedItem("mode") ==
                 * null)) { vocElemEditMode =
                 * curVocElemNode.getAttributes().getNamedItem("mode").getNodeValue();
                 * } else { vocElemEditMode = "1"; }
                 */
                vocElementList.add(vocElementType);
            }
        }
        return vocElementList;
    }

    /**
     * @return
     * <code>true</code> if the given Node is a text or a comment.
     */
    private boolean isTextOrComment(Node node) {
        return node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.COMMENT_NODE;
    }
}
