/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.xacml.socket;

import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.utils.MapSessions;
import fr.unicaen.iota.xacml.conf.Configuration;
import fr.unicaen.iota.xacml.pep.XACMLResponse;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class XACMLServiceMessageReceiverInOut extends Thread {

    private static final Log log = LogFactory.getLog(XACMLServiceMessageReceiverInOut.class);
    private ServerSocket serverSocket = null;

    public synchronized void stopListening() throws IOException {
        log.info("STOPPING XACML SERVICE!");
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            log.info("opening socket on port " + Configuration.XACML_SERVICE_PORT + " ...");
            serverSocket = new ServerSocket(Configuration.XACML_SERVICE_PORT);
            while (true) {
                log.info("waiting for connexions ...");
                Socket soc = serverSocket.accept();
                new Thread(new ConnectionHandler(soc)).start();
            }
        } catch (SocketException sex) {
            log.debug("socket closed by server app!", sex);
        } catch (IOException ex) {
            log.debug("error IO exception!", ex);
        }
    }

    private static class ConnectionHandler implements Runnable {

        private Socket socket;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                log.info("Receiving socket connexion");
                String request = readXACMLRequest(socket.getInputStream());
                log.info("create XACML request ...");
                RequestCtx req = RequestCtx.getInstance(new ByteArrayInputStream(request.getBytes()));
                log.info("process policy");
                String resp = processRequest(req);
                log.info("process response");
                PrintWriter socketPrinter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                socketPrinter.println(resp);
                log.info("socket closed");
                socketPrinter.close();
            } catch (ParsingException ex) {
                log.error(null, ex);
            } catch (IOException ex) {
                log.error(null, ex);
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    log.error(null, ex);
                }
            }
        }
        private static final char[] END = {'<', '/', 'R', 'e', 'q', 'u', 'e', 's', 't', '>'};

        private String readXACMLRequest(InputStream is) throws IOException {
            int endIndex = 0;
            StringBuilder response = new StringBuilder();
            int value = 0;
            boolean active = true;
            while (active) {
                value = is.read();
                if (value == -1) {
                    throw new IOException("End of Stream");
                }
                response.append((char) value);
                if (value == END[endIndex]) {
                    endIndex++;
                } else {
                    endIndex = 0;
                }
                if (endIndex == END.length) {
                    active = false;
                }
            }
            return response.toString();
        }

        public String processRequest(RequestCtx request) {
            try {
                ResponseCtx result = MapSessions.APM.evaluate(request);
                Iterator it = result.getResults().iterator();
                while (it.hasNext()) {
                    Result res = (Result) it.next();
                    if (res != null) {
                        return new XACMLResponse(res).toString();
                    }
                }
            } catch (Exception ex) {
                log.warn("error: " + ex.getMessage(), ex);
                return "DENY";
            }
            return "DENY";
        }
    }
}
