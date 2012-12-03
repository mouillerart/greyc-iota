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
package fr.unicaen.iota.eta.capture;

import fr.unicaen.iota.eta.constants.Constants;
import fr.unicaen.iota.sigma.client.SigMaClient;
import fr.unicaen.iota.sigma.xsd.Verification;
import java.io.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.repository.EpcisConstants;
import org.fosstrak.epcis.repository.InternalBusinessException;
import org.fosstrak.epcis.repository.InvalidFormatException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    /**
     * The SigMAClient used to check the signature of each events in the
     * capture.
     */
    private SigMaClient sigmaClient;

    public SigMaClient getSigMaClient() {
        return sigmaClient;
    }

    public void setSigMaClient(SigMaClient sigmaClient) {
        this.sigmaClient = sigmaClient;
    }

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
     * @param out 
     * @throws IOException if an error occurred while configuring EPCIS capture
     * client.
     * @throws Exception  
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
     * @param req 
     * @param rsp The response.
     * @throws SAXException If the document parsing failed.
     * @throws IOException If an error occurred while validating the request or
     * writing the response.
     * @throws InternalBusinessException If unable to read from input.
     */
    public void doCapture(HttpServletRequest req, HttpServletResponse rsp) throws SAXException, IOException, InternalBusinessException {
        Principal authId = req.getUserPrincipal();
        String user = authId != null ? authId.toString() : Constants.XACML_DEFAULT_USER;
        InputStream in = req.getInputStream();
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
                processEvents(user, document, rsp);
            } else if (isEPCISMasterDataDocument(document)) {
                processMasterData(user, document, rsp);
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
     * @throws SAXException If an error processing the XML document occurred.
     * @throws JAXBException If an error parsing from the XML document to objects occurred.
     * @throws IOException If unable to configure EPCIS capture client.
     * @throws TransformerConfigurationException If an error while transforming document occurred.
     * @throws TransformerException If an error while transforming document occurred.
     * @throws CaptureClientException If the capture failed.
     * @throws Exception If an unexpected error occurred.
     */
    private void processEvents(String user, Document document, HttpServletResponse rsp) throws SAXException, JAXBException,
            IOException, TransformerConfigurationException, TransformerException, CaptureClientException, Exception {
        List<EPCISEventType> captureEventList = extractsCaptureEventList(document);

        if (Constants.SIGMA_VERIFICATION) {
            for (EPCISEventType event : captureEventList) {
                Verification res = sigmaClient.verify(event);
                if (res.getVerifyResponse().isValue()) {
                    LOG.info("Event signature verified by the SigMA server.");
                } else {
                    LOG.info("Event signature is not correct. Please verify your partner capturer.");
                }
            }
        }

        PrintWriter out = rsp.getWriter();
        String msg;

        // XACML check start
        LOG.debug("START OF XACML check");
        //TODO reset allowed after tests
        // TODO user + owner
        String owner = "anonymous";
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
     * @throws SAXException If an error processing the XML document occurred.
     * @throws JAXBException If an error parsing from the XML document to objects occurred.
     */
    private List<EPCISEventType> extractsCaptureEventList(Document document)
            throws SAXException, JAXBException {
        NodeList eventList = document.getElementsByTagName("EventList");
        NodeList events = eventList.item(0).getChildNodes();
        List<EPCISEventType> captureEventList = new ArrayList<EPCISEventType>();

        for (int i = 0; i < events.getLength(); i++) {
            Node eventNode = events.item(i);
            String nodeName = eventNode.getNodeName();
            if (EpcisConstants.OBJECT_EVENT.equals(nodeName)
                    || EpcisConstants.AGGREGATION_EVENT.equals(nodeName)
                    || EpcisConstants.QUANTITY_EVENT.equals(nodeName)
                    || EpcisConstants.TRANSACTION_EVENT.equals(nodeName)) {
                LOG.debug("processing event " + i + ": '" + nodeName + "'.");
                EPCISEventType captureEvent = extractsCaptureEvent(eventNode, nodeName);
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
        EPCISEventType captureEvent;
        if (EpcisConstants.AGGREGATION_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(AggregationEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<AggregationEventType> jElement = unmarshaller.unmarshal(eventNode, AggregationEventType.class);
            captureEvent = jElement.getValue();
        } else if (EpcisConstants.OBJECT_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(ObjectEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<ObjectEventType> jElement = unmarshaller.unmarshal(eventNode, ObjectEventType.class);
            captureEvent = jElement.getValue();
        } else if (EpcisConstants.QUANTITY_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(QuantityEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<QuantityEventType> jElement = unmarshaller.unmarshal(eventNode, QuantityEventType.class);
            captureEvent = jElement.getValue();
        } else if (EpcisConstants.TRANSACTION_EVENT.equals(eventType)) {
            JAXBContext context = JAXBContext.newInstance(TransactionEventType.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<TransactionEventType> jElement = unmarshaller.unmarshal(eventNode, TransactionEventType.class);
            captureEvent = jElement.getValue();
        } else {
            throw new SAXException("Encountered unknown event element '" + eventType + "'.");
        }
        return captureEvent;
    }

    private void processMasterData(String user, Document document, HttpServletResponse rsp) throws SAXException, IOException,
            CaptureClientException, TransformerConfigurationException, TransformerException {
        List<VocabularyType> vocList = extractsVocabularies(document);
        PrintWriter out = rsp.getWriter();
        String msg;

        // XACML check start
        LOG.debug("START OF XACML check");
        //TODO reset allowed after tests
        // TODO user + owner
        String owner = "anonymous";
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
        List<VocabularyType> vocTypeList = new ArrayList<VocabularyType>();
        if (vocabularyList.item(0).hasChildNodes()) {
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
        return vocTypeList;
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
