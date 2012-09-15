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
package fr.unicaen.iota.simulator.app;

import fr.unicaen.iota.simulator.util.HttpClient;
import fr.unicaen.iota.simulator.util.MD5;
import fr.unicaen.iota.simulator.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @stereotype Singleton
 */
public class CommCenter {

    private static final Log log = LogFactory.getLog(CommCenter.class);
    private static CommCenter instance = new CommCenter();
    private final EventListenerList listeners = new EventListenerList();

    public static CommCenter getInstance() {
        return instance;
    }

    public CommListener[] getCommListeners() {
        return listeners.getListeners(CommListener.class);
    }

    protected void firePipeStatusChanged(String pipeId, double value) {
        for (CommListener listener : getCommListeners()) {
            listener.pipeStatusChanged(pipeId, value);
        }
    }

    public void addCommListener(CommListener listener) {
        listeners.add(CommListener.class, listener);
    }

    public void removeCommListener(CommListener listener) {
        listeners.remove(CommListener.class, listener);
    }

    public List<String> loadPipe(String address, String pipeId, String passwd) {
        List<String> result = new ArrayList<String>();
        HttpClient client = new HttpClient();
        String res = client.queryGET(address, createParamsForReception(pipeId, passwd));
        InputStream stream = new ByteArrayInputStream(res.getBytes());
        SAXBuilder sab = new SAXBuilder();
        try {
            Document doc = sab.build(stream);
            for (Object o : doc.getRootElement().getChild("objects").getChildren("object")) {
                Element e = (Element) o;
                String epc = e.getText();
                result.add(epc);
            }
            if (!result.isEmpty()) {
                firePipeStatusChanged(pipeId, 0);
            }
        } catch (JDOMException ex) {
            log.fatal(null, ex);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        return result;
    }

    private Pair[] createParamsForReception(String pipeId, String passwd) {
        Pair[] params = new Pair[4];
        String id = Long.toString(new Date().getTime());
        params[0] = new Pair("id", id);
        params[1] = new Pair("m", createEncodedPass(id, passwd));
        params[2] = new Pair("placeId", pipeId);
        params[3] = new Pair("action", "peek");
        return params;
    }

    public void publish(String address, String pipeId, String passwd, String reservedId, String epcList) {
        HttpClient client = new HttpClient();
        String result = client.queryPOST(address, createParamsForPipePublication(pipeId, passwd, reservedId, epcList));
        InputStream stream = new ByteArrayInputStream(result.getBytes());
        try {
            SAXBuilder sab = new SAXBuilder();
            Document doc = sab.build(stream);
            double volume = Double.parseDouble(doc.getRootElement().getChild("report").getChild("remainingVolume").getText());
            firePipeStatusChanged(pipeId, volume);
        } catch (JDOMException ex) {
            log.fatal(null, ex);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
    }

    private Pair[] createParamsForPipePublication(String pipeId, String passwd, String reservedId, String epcList) {
        Pair[] params = new Pair[6];
        String id = Long.toString(new Date().getTime());
        params[0] = new Pair("id", id);
        params[1] = new Pair("m", createEncodedPass(id, passwd));
        params[2] = new Pair("placeId", pipeId);
        params[3] = new Pair("action", "put");
        params[4] = new Pair("reservedId", reservedId);
        params[5] = new Pair("epc", epcList);
        return params;
    }

    public String reserve(String address, String pipeId, String passwd, String reservedId, int canalSize) {
        if (reservedId != null) {
            return reservedId;
        }
        HttpClient client = new HttpClient();
        String result = client.queryGET(address, createParamsForReservation(pipeId, canalSize, passwd));
        InputStream stream = new ByteArrayInputStream(result.getBytes());
        try {
            SAXBuilder sab = new SAXBuilder();
            Document doc = sab.build(stream);
            boolean resp = Boolean.parseBoolean(doc.getRootElement().getChild("report").getChild("succeed").getText());
            if (resp) {
                reservedId = doc.getRootElement().getChild("report").getChild("reservedId").getText();
                return reservedId;
            }
        } catch (JDOMException ex) {
            log.fatal(null, ex);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        return null;
    }

    private Pair[] createParamsForReservation(String pipeId, int canalSize, String passwd) {
        Pair[] params = new Pair[5];
        String id = Long.toString(new Date().getTime());
        params[0] = new Pair("id", id);
        params[1] = new Pair("m", createEncodedPass(id, passwd));
        params[2] = new Pair("placeId", pipeId);
        params[3] = new Pair("action", "reserve");
        params[4] = new Pair("canalSize", String.valueOf(canalSize));
        return params;
    }

    public void reportPipeVolumes(String address, String pipeId, String passwd) {
        HttpClient client = new HttpClient();
        String result = client.queryGET(address, createParamsForReportPipeVolumes(pipeId, passwd));
        InputStream stream = new ByteArrayInputStream(result.getBytes());
        try {
            SAXBuilder sab = new SAXBuilder();
            Document doc = sab.build(stream);
            Integer res = Integer.parseInt(doc.getRootElement().getChild("pipeVolume").getText());
            firePipeStatusChanged(pipeId, res);
        } catch (JDOMException ex) {
            log.fatal(null, ex);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
    }

    private Pair[] createParamsForReportPipeVolumes(String pipeId, String passwd) {
        Pair[] params = new Pair[4];
        String id = Long.toString(new Date().getTime());
        params[0] = new Pair("id", id);
        params[1] = new Pair("m", createEncodedPass(id, passwd));
        params[2] = new Pair("placeId", pipeId);
        params[3] = new Pair("action", "pipeVolume");
        return params;
    }

    public String createEncodedPass(String id, String passwd) {
        try {
            return MD5.MD5_Algo(id + passwd);
        } catch (NoSuchAlgorithmException ex) {
            log.fatal(null, ex);
        } catch (UnsupportedEncodingException ex) {
            log.fatal(null, ex);
        }
        return null;
    }
}
