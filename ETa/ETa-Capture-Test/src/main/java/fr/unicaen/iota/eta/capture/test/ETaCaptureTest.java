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

import fr.unicaen.iota.eta.capture.ETaCaptureClient;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.fosstrak.epcis.captureclient.CaptureClientException;

public class ETaCaptureTest {

    private ETaCaptureClient etaClient;
    private String filePath;

    public ETaCaptureTest(String url, String filePath, String pksFilename, String pksPassword,
            String trustPksFilename, String trustPksPassword) {
        etaClient = new ETaCaptureClient(url, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        this.filePath = filePath;
    }

    public String getFileContent(String filePath) throws FileNotFoundException, IOException {
        StringBuilder buf = new StringBuilder();
        BufferedReader buff = new BufferedReader(new FileReader(filePath));
        try {
            String line;
            while ((line = buff.readLine()) != null) {
                buf.append(line);
            }
        } finally {
            buff.close();
        }
        return buf.toString();
    }

    public int capture(String filePath) throws FileNotFoundException, IOException, CaptureClientException {
        String event = getFileContent(filePath);
        return etaClient.capture(event);
    }

    public int capture() throws FileNotFoundException, IOException, CaptureClientException {
        String event = getFileContent(filePath);
        return etaClient.capture(event);
    }

}
