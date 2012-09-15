/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.simulator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpClient {

    private static final Log log = LogFactory.getLog(HttpClient.class);

    public String queryGET(String addr, Pair[] params) {
        StringBuilder res = new StringBuilder();
        try {
            StringBuilder paramString = new StringBuilder("?");
            boolean firstPass = true;
            for (Pair tup : params) {
                if (!firstPass) {
                    paramString.append("&");
                } else {
                    firstPass = false;
                }
                paramString.append(tup.getName());
                paramString.append("=");
                paramString.append(tup.getValue());
            }
            StringBuilder address = new StringBuilder(addr);
            if (!firstPass) {
                address.append(paramString);
            }
            URL url = new URL(address.toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String response;
            while ((response = in.readLine()) != null) {
                res.append(response);
            }
            in.close();
        } catch (MalformedURLException ex) {
            log.error(null, ex);
            return null;
        } catch (IOException ex) {
            log.error(null, ex);
            return null;
        }
        return res.toString();
    }

    public String queryPOST(String addr, Pair[] params) {
        StringBuilder res = new StringBuilder();
        try {
            // Construct data
            StringBuilder paramString = new StringBuilder();
            boolean firstPass = true;
            for (Pair nvp : params) {
                if (!firstPass) {
                    paramString.append("&");
                } else {
                    firstPass = false;
                }
                paramString.append(nvp.getName());
                paramString.append("=");
                paramString.append(nvp.getValue());
            }
            // Send data
            URL url = new URL(addr);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(paramString.toString());
            wr.flush();
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                res.append(line);
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
            log.error(null, e);
        }
        return res.toString();
    }
}
