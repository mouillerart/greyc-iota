/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.model;

import fr.unicaen.iota.eta.capture.ETaCaptureClient;
import fr.unicaen.iota.mu.Constants;
import fr.unicaen.iota.mu.Utils;
import fr.unicaen.iota.sigma.SigMaFunctions;
import fr.unicaen.iota.simulator.util.Config;
import fr.unicaen.iota.simulator.util.ServicePool;
import fr.unicaen.iota.simulator.util.StatControler;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.captureclient.CaptureClientException;
import org.fosstrak.epcis.model.EPCISBodyType;
import org.fosstrak.epcis.model.EPCISDocumentType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.EventListType;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 */
public abstract class BaseEvent {

    private static final Log log = LogFactory.getLog(BaseEvent.class);
    private String bizStep;
    private String disposition;
    private String readPoint;
    private Map<String, String> extensions;
    private Infrastructure infrastructure;
    private LatLonLocation latLonLocation;

    protected String propertiesToXML() {
        StringBuilder str = new StringBuilder();
        str.append("\t\t<bizStep>");
        str.append(bizStep);
        str.append("</bizStep>\n");
        str.append("\t\t<disposition>");
        str.append(disposition);
        str.append("</disposition>\n");
        str.append("\t\t<readPoint>");
        str.append(readPoint);
        str.append("</readPoint>\n");
        if (extensions != null) {
            str.append("\t\t<extensions>\n");
            for (String k : extensions.keySet()) {
                str.append("\t\t\t<property name=\"");
                str.append(k);
                str.append("\" value=\"");
                str.append(extensions.get(k));
                str.append("\" />\n");
            }
            str.append("\t\t</extensions>\n");
        }
        return str.toString();
    }

    public BaseEvent(Long id, Infrastructure infrastructure) {
        setInfrastructure(infrastructure);
        setBizStep("urn:unicaen:iotatester:bizstep:xxxx:tester");
        setDisposition("urn:unicaen:iotatester:disp:xxxx:tester");
        setReadPoint(infrastructure.getBizLoc() + "," + id);
        setExtensions(null);
    }

    public BaseEvent() {
        setInfrastructure(null);
        setBizStep(null);
        setDisposition(null);
        setReadPoint(null);
        setExtensions(null);
    }

    public String getBizStep() {
        return bizStep;
    }

    public void setBizStep(String bizStep) {
        this.bizStep = bizStep;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getReadPoint() {
        return readPoint;
    }

    public void setReadPoint(String readPoint) {
        this.readPoint = readPoint;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }

    public Infrastructure getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(Infrastructure service) {
        this.infrastructure = service;
    }

    protected abstract EPCISEventType prepareEventType();

    protected abstract void setExtensionsObjects(EPCISEventType event, List<Object> extensions);

    public int publish() throws CaptureClientException, IOException, JAXBException {
        EPCISEventType event = prepareEventType();

        // get the current time and set the eventTime        
        XMLGregorianCalendar now = null;
        try {
            DatatypeFactory dataFactory = DatatypeFactory.newInstance();
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date()); // force the real current time
            now = dataFactory.newXMLGregorianCalendar(gc);
            event.setEventTime(now);
        } catch (DatatypeConfigurationException e) {
            log.error(null, e);
        }
        // get the current time zone and set the eventTimeZoneOffset
        if (now != null) {
            int timezone = now.getTimezone();
            int h = Math.abs(timezone / 60);
            int m = Math.abs(timezone % 60);
            DecimalFormat format = new DecimalFormat("00");
            String sign = (timezone < 0) ? "-" : "+";
            event.setEventTimeZoneOffset(sign + format.format(h) + ":" + format.format(m));
        }

        // add extensions
        List<Object> extensionsObjects = new LinkedList<Object>();
        for (String extensionName : extensions.keySet()) {
            String namespace = "http://api.orange.com/extensions/other"; // TODO: hard value
            String prefix = "ext";
            String name = extensionName;
            String value = extensions.get(extensionName);
            String remainingString = extensionName;
            if (extensionName.contains("##")) {
                // namespace extraction
                namespace = extensionName.substring(0, extensionName.indexOf("##"));
                remainingString = extensionName.substring(extensionName.indexOf("##") + 2);
            }
            if (extensionName.contains(":")) {
                // name extraction
                name = extensionName.substring(extensionName.lastIndexOf(":") + 1);
                remainingString = remainingString.substring(0, remainingString.lastIndexOf(":"));
                if (remainingString.contains(":")) {
                    remainingString = remainingString.substring(remainingString.lastIndexOf(":") + 1);
                }
                prefix = remainingString;
            }

            Element extensionElemJdom = new Element(name, prefix, namespace);
            extensionElemJdom.setText(value);
            XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = sortie.outputString(extensionElemJdom);
            org.w3c.dom.Element nameElement = null;
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder db;
                db = factory.newDocumentBuilder();
                InputSource inStream = new InputSource();
                inStream.setCharacterStream(new StringReader(xmlString));
                org.w3c.dom.Document doc = db.parse(inStream);
                NodeList nodeList = doc.getChildNodes();
                for (int index = 0; index < nodeList.getLength(); index++) {
                    Node node = nodeList.item(index);
                    nameElement = (org.w3c.dom.Element) node;
                }
            } catch (ParserConfigurationException e) {
                log.error(null, e);
            } catch (SAXException e) {
                log.error(null, e);
            }
            extensionsObjects.add(nameElement);
        }
        setExtensionsObjects(event, extensionsObjects);

        try {
            Utils.insertExtension(event, Constants.URN_IOTA, Constants.EXTENSION_OWNER_ID, Config.eventOwner);
        } catch (ParserConfigurationException ex) {
            log.error("An error during insertion of the owner of the event occurred", ex);
        }

        if (Config.sign) {
            SigMaFunctions sigMaFunctions = new SigMaFunctions(Config.sigma_keystore, Config.sigma_keystore_password);
            try {
                if (Config.sigma_signer_id != null && !Config.sigma_signer_id.isEmpty()) {
                    sigMaFunctions.sign(event, Config.sigma_signer_id);
                }
                else {
                    sigMaFunctions.sign(event);
                }
            } catch (Exception ex) {
                log.error("an error has been thrown during the signature of the event", ex);
            }
        }

        // create the EPCISDocument containing a single ObjectEvent
        EPCISDocumentType epcisDoc = new EPCISDocumentType();
        EPCISBodyType epcisBody = new EPCISBodyType();
        EventListType eventList = new EventListType();
        eventList.getObjectEventOrAggregationEventOrQuantityEvent().add(event);
        epcisBody.setEventList(eventList);
        epcisDoc.setEPCISBody(epcisBody);
        epcisDoc.setSchemaVersion(new BigDecimal("1.0"));
        epcisDoc.setCreationDate(now);
        if (Config.PRINT_MESSAGE) {
            log.trace(toXML());
        }
        if (!Config.publish) {
            return 0;
        }
        // get the capture client and capture the event
        String captureUrl = getInfrastructure().getServiceAddress();
        ETaCaptureClient client;
        try {
            client = ServicePool.getInstance().getServiceInstance(captureUrl);
        } catch (InterruptedException ex) {
            log.fatal("Interrupted while publishing an event", ex);
            return 0;
        }
        int httpResponseCode = -1;
        try {
            httpResponseCode = client.capture(epcisDoc);
        } catch (CaptureClientException ex) {
            try {
                ServicePool.getInstance().releaseInstance(client);
            } catch (InterruptedException ex1) {
                log.fatal(null, ex1);
            }
            throw new CaptureClientException(ex);
        }
        if (httpResponseCode != 200) {
            log.warn("The quantity event could NOT be captured (response code: " + httpResponseCode + " ) !");
            StatControler.addPublicationError(getInfrastructure().getBizLoc());
        }
        try {
            ServicePool.getInstance().releaseInstance(client);
        } catch (InterruptedException ex) {
            log.fatal("Interrupted while releasing capture client", ex);
        }
        return httpResponseCode;
    }

    public abstract String toXML();

    // JDOM / DOM convenience functions
    public org.w3c.dom.Document JDOMtoDOM(org.jdom.Document jdomDoc) throws Exception {
        DOMOutputter outputter = new DOMOutputter();
        return outputter.output(jdomDoc);
    }

    org.jdom.Document DOMtoJDOM(org.w3c.dom.Document documentDOM) throws Exception {
        DOMBuilder builder = new DOMBuilder();
        org.jdom.Document documentJDOM = builder.build(documentDOM);
        return documentJDOM;
    }

    org.jdom.Element DOMtoJDOM(org.w3c.dom.Element documentDOM) throws Exception {
        DOMBuilder builder = new DOMBuilder();
        org.jdom.Element documentJDOM = builder.build(documentDOM);
        return documentJDOM;
    }

    public void loadFromXML(Element elem, Infrastructure infrastructure, LatLonLocation latLonLocation) {
        this.setLatLonLocation(latLonLocation);
        this.infrastructure = infrastructure;
        extensions = new HashMap<String, String>();
        try {
            org.w3c.dom.Document domElem = JDOMtoDOM(elem.getDocument());
            bizStep = domElem.getElementsByTagName("bizStep").item(0).getFirstChild().getNodeValue();
            disposition = domElem.getElementsByTagName("disposition").item(0).getFirstChild().getNodeValue();
            readPoint = domElem.getElementsByTagName("readPoint").item(0).getFirstChild().getNodeValue();
            NodeList extensionList = domElem.getElementsByTagName("property");
            for (int i = 0; i < extensionList.getLength(); i++) {
                String name = extensionList.item(i).getAttributes().getNamedItem("name").getNodeValue();
                String value;
                try {
                    value = extensionList.item(i).getAttributes().getNamedItem("value").getNodeValue();
                } catch (Exception e) {
                    value = extensionList.item(i).getFirstChild().getNodeValue();
                }
                extensions.put(name, value);
            }
        } catch (Exception e1) {
            LogFactory.getLog(BaseEvent.class).error("DOM error: ", e1);
        }
    }

    /**
     * @return the latLonLocation
     */
    public LatLonLocation getLatLonLocation() {
        return latLonLocation;
    }

    /**
     * @param latLonLocation the latLonLocation to set
     */
    public void setLatLonLocation(LatLonLocation latLonLocation) {
        this.latLonLocation = latLonLocation;
    }
}
