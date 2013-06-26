/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.sigma.test.controler;

import fr.unicaen.iota.eta.capture.ETaCaptureClient;
import fr.unicaen.iota.mu.Constants;
import fr.unicaen.iota.mu.Utils;
import fr.unicaen.iota.sigma.SigMaFunctions;
import fr.unicaen.iota.sigma.client.SigMaClient;
import fr.unicaen.iota.sigma.model.VerifyResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import org.fosstrak.epcis.captureclient.CaptureClientException;
import org.fosstrak.epcis.model.ActionType;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.BusinessLocationType;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISBodyType;
import org.fosstrak.epcis.model.EPCISDocumentType;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.EPCListType;
import org.fosstrak.epcis.model.EventListType;
import org.fosstrak.epcis.model.ObjectEventType;
import org.fosstrak.epcis.model.ObjectFactory;
import org.fosstrak.epcis.model.QuantityEventType;
import org.fosstrak.epcis.model.ReadPointType;
import org.fosstrak.epcis.model.TransactionEventType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Controler {

    private String captureUrl;
    private String sigmaUrl;
    private String tlsKeystore;
    private String tlsKsPassword;
    private String tlsTruststore;
    private String tlsTsPassword;
    private String signKeystore;
    private String signKsPassword;

    public Controler(String captureUrl, String sigmaUrl, String tlsKeystore, String tlsKsPassword,
            String tlsTruststore, String tlsTsPassword, String signKeystore, String signKsPassword) {
        this.captureUrl = captureUrl;
        this.sigmaUrl = sigmaUrl;
        this.tlsKeystore = tlsKeystore;
        this.tlsKsPassword = tlsKsPassword;
        this.tlsTruststore = tlsTruststore;
        this.tlsTsPassword = tlsTsPassword;
        this.signKeystore = signKeystore;
        this.signKsPassword = signKsPassword;
    }

    public ObjectEventType sign(String epcCode, String bizStepCode, String dispositionCode, String readPointCode, String bizLocationCode) {
        ObjectEventType objEvent = new ObjectEventType();
        XMLGregorianCalendar now = null;
        try {
            DatatypeFactory dataFactory = DatatypeFactory.newInstance();
            now = dataFactory.newXMLGregorianCalendar(new GregorianCalendar());
            objEvent.setEventTime(now);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        if (now != null) {
            int timezone = now.getTimezone();
            int h = Math.abs(timezone / 60);
            int m = Math.abs(timezone % 60);
            DecimalFormat format = new DecimalFormat("00");
            String sign = (timezone < 0) ? "-" : "+";
            objEvent.setEventTimeZoneOffset(sign + format.format(h) + ":" + format.format(m));
        }
        objEvent.setAction(ActionType.OBSERVE);
        EPC epc = new EPC();
        epc.setValue(epcCode);
        EPCListType epcList = new EPCListType();
        epcList.getEpc().add(epc);
        objEvent.setEpcList(epcList);
        objEvent.setBizStep(bizStepCode);
        objEvent.setDisposition(dispositionCode);
        ReadPointType readPoint = new ReadPointType();
        readPoint.setId(readPointCode);
        objEvent.setReadPoint(readPoint);
        BusinessLocationType bizLocation = new BusinessLocationType();
        bizLocation.setId(bizLocationCode);
        objEvent.setBizLocation(bizLocation);
        EPCISDocumentType epcisDoc = new EPCISDocumentType();
        EPCISBodyType epcisBody = new EPCISBodyType();
        EventListType eventList = new EventListType();
        try {
            SigMaFunctions sigMAFunctions = new SigMaFunctions(signKeystore, signKsPassword);
            sigMAFunctions.sign(objEvent);
        } catch (Exception e) {
            System.err.println("Exception during signing");
            e.printStackTrace();
            return null;
        }
        return objEvent;
    }

    public boolean publish(ObjectEventType event) {

        XMLGregorianCalendar now = null;
        try {
            DatatypeFactory dataFactory = DatatypeFactory.newInstance();
            now = dataFactory.newXMLGregorianCalendar(new GregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        EPCISDocumentType epcisDoc = new EPCISDocumentType();
        EPCISBodyType epcisBody = new EPCISBodyType();
        EventListType eventList = new EventListType();
        JAXBElement<ObjectEventType> jaxbevt = new ObjectFactory().createEventListTypeObjectEvent(event);

        eventList.getObjectEventOrAggregationEventOrQuantityEvent().add(jaxbevt);
        epcisBody.setEventList(eventList);
        epcisDoc.setEPCISBody(epcisBody);
        epcisDoc.setSchemaVersion(new BigDecimal("1.0"));
        epcisDoc.setCreationDate(now);

        ETaCaptureClient client = new ETaCaptureClient(captureUrl, tlsKeystore, tlsKsPassword, tlsTruststore, tlsTsPassword);
        int httpResponseCode;
        try {
            httpResponseCode = client.capture(epcisDoc);
            if (httpResponseCode != 200) {
            System.err.println("The event could NOT be captured!");
            return false;
        }
        } catch (CaptureClientException ex) {
            Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public VerifyResponse verify(ObjectEventType event){
        SigMaClient sigMaClient = new SigMaClient(sigmaUrl,tlsKeystore, tlsKsPassword, tlsTruststore, tlsTsPassword);
        return sigMaClient.verify(event).getVerifyResponse();
    }

    public String getSignature(EPCISEventType event) {
        List<String> signatures = Utils.getExtension(event, Constants.URN_IOTA, Constants.EXTENSION_SIGNATURE);
        if (signatures != null && !signatures.isEmpty()) {
            return signatures.get(0);
        }
        return "";
    }

    public boolean insertErrors(ObjectEventType objectEvent){
        String signature = getSignature(objectEvent);
        signature = "1"+signature;
        try {
            insertWrongSignature(objectEvent, signature);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private void insertWrongSignature(EPCISEventType event, String signature) throws IOException, ParserConfigurationException, SAXException {
        if (event instanceof ObjectEventType) {
            ((ObjectEventType) event).getAny().clear();
        } else if (event instanceof AggregationEventType) {
            ((AggregationEventType) event).getAny().clear();
        } else if (event instanceof QuantityEventType) {
            ((QuantityEventType) event).getAny().clear();
        } else if (event instanceof TransactionEventType) {
            ((TransactionEventType) event).getAny().clear();
        }
        Utils.insertExtension(event, Constants.URN_IOTA, Constants.EXTENSION_SIGNATURE, signature);
    }

}
