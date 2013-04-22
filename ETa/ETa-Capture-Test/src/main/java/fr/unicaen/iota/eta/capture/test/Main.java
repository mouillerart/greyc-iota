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
package fr.unicaen.iota.eta.capture.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.captureclient.CaptureClientException;

public class Main {

    private static final Log log = LogFactory.getLog(ETaCaptureTest.class);

    public static void main(String[] args) {
        String etaURL = "https://localhost:8443/eta/capture";
        String filePath = "event.xml";
        String ksFile = "keystore.jks";
        String ksPass = "store_pw";
        String tsFile = "truststore.jks";
        String tsPass = "trust_pw";

        if (args.length != 6) {
            System.out.println("Usage: Main <ETa URL> <Path to File> [<Keystore File> <Keystore Password> <Truststore file> <Truststore Password>]");
            System.out.println();
            System.out.println("example: Main " + etaURL + " " + filePath + " " + ksFile + " " + ksPass + " " + tsFile + " " + tsPass);
            System.exit(-1);
        }
        etaURL = args[0];
        filePath = args[1];
        ksFile = args[2];
        ksPass = args[3];
        tsFile = args[4];
        tsPass = args[5];
        ETaCaptureTest etaTest = new ETaCaptureTest(etaURL, filePath, ksFile, ksPass, tsFile, tsPass);
        try {
            System.out.println(etaTest.capture());
        } catch (FileNotFoundException ex) {
            String msg = "File not Found";
            System.out.println(msg);
            ex.printStackTrace(System.err);
            log.error(msg, ex);
        } catch (IOException ex) {
            String msg = "File reading error";
            System.out.println(msg);
            ex.printStackTrace(System.err);
            log.error(msg, ex);
        } catch (CaptureClientException ex) {
            String msg = "Error during capture";
            System.out.println(msg);
            ex.printStackTrace(System.err);
            log.error(msg, ex);
        }
    }

}
