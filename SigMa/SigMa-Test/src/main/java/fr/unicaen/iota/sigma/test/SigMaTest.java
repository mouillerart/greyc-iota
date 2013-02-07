/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.sigma.test;

import fr.unicaen.iota.eta.capture.ETaCaptureClient;
import fr.unicaen.iota.sigma.SigMaFunctions;
import fr.unicaen.iota.sigma.client.SigMaClient;
import fr.unicaen.iota.sigma.xsd.VerifyResponse;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.GregorianCalendar;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.fosstrak.epcis.model.*;

/**
 * This
 * <code>App</code> class is a simple application of the
 * <code>SigMAFunctions</code> class. Its shows how to sign and verify an
 * electronic signature of an EPCIS event.
 */
public class SigMaTest {

    public static void main(String[] args) {
        if (args.length != 8) {
            System.err.println("usage: SigMaTest ETaCaptureURL SigMaURL keystoreTLS ksPasswordTLS truststoreTLS tsPasswordTLS keystoreSign ksPasswordSign");
            System.err.println();
            System.err.println("example: SigMaTest https://localhost:8443/eta/capture https://localhost:8443/sigma /srv/keystore.jks store_pw /srv/truststore.jks trust_pw /srv/sigma-cert.p12 store_pw");
            System.exit(1);
        }
        String captureUrl = args[0];
        String sigmaAddress = args[1];
        String keystoreTLS = args[2];
        String ksPasswordTLS = args[3];
        String truststoreTLS = args[4];
        String tsPasswordTLS = args[5];
        String keystoreSign = args[6];
        String ksPasswordSign = args[7];

        ObjectEventType objEvent = new ObjectEventType();
        XMLGregorianCalendar now = null;
        try {
            DatatypeFactory dataFactory = DatatypeFactory.newInstance();
            now = dataFactory.newXMLGregorianCalendar(new GregorianCalendar());
            objEvent.setEventTime(now);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
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
        epc.setValue("urn:epc:id:sgtin:0614141.107346.2017");
        EPCListType epcList = new EPCListType();
        epcList.getEpc().add(epc);
        objEvent.setEpcList(epcList);
        objEvent.setBizStep("urn:epcglobal:epcis:bizstep:fmcg:shipped");
        objEvent.setDisposition("urn:epcglobal:epcis:disp:fmcg:unknown");
        ReadPointType readPoint = new ReadPointType();
        readPoint.setId("urn:epc:id:sgln:0614141.07346.1234");
        objEvent.setReadPoint(readPoint);
        BusinessLocationType bizLocation = new BusinessLocationType();
        bizLocation.setId("urn:epcglobal:fmcg:loc:0614141073467.A23-49");
        objEvent.setBizLocation(bizLocation);
        EPCISDocumentType epcisDoc = new EPCISDocumentType();
        EPCISBodyType epcisBody = new EPCISBodyType();
        EventListType eventList = new EventListType();

        try {
            SigMaFunctions sigMAFunctions = new SigMaFunctions(keystoreSign, ksPasswordSign);
            sigMAFunctions.sign(objEvent);
            JAXBElement<ObjectEventType> jaxbevt = new ObjectFactory().createEventListTypeObjectEvent(objEvent);

            eventList.getObjectEventOrAggregationEventOrQuantityEvent().add(jaxbevt);
            epcisBody.setEventList(eventList);
            epcisDoc.setEPCISBody(epcisBody);
            epcisDoc.setSchemaVersion(new BigDecimal("1.0"));
            epcisDoc.setCreationDate(now);

            ETaCaptureClient client = new ETaCaptureClient(captureUrl, keystoreTLS, ksPasswordTLS, truststoreTLS, tsPasswordTLS);
            int httpResponseCode = client.capture(epcisDoc);
            if (httpResponseCode != 200) {
                System.err.println("The event could NOT be captured!");
            }

            SigMaClient sigMaClient = new SigMaClient(sigmaAddress, keystoreTLS, ksPasswordTLS, truststoreTLS, tsPasswordTLS);
            VerifyResponse response = sigMaClient.verify(objEvent).getVerifyResponse();
            boolean verification = response.isValue();
            System.out.println("This signature is " + verification);
            String message = response.getMessage();
            System.out.println("Response message: " + message);
        } catch (Exception e) {
            System.err.println("Exception during signing");
            e.printStackTrace();
        }
    }
}
