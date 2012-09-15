/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
 *  Copyright © 2007       ETH Zurich
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
/*
 * Derived from org.fosstrak.epcis.utils.QueryCallbackListener
 */
package fr.unicaen.iota.eta.callback.sender;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements a simple web server listening for responses from the
 * EPCIS Query Callback interface. The server is not multi-threaded, so it will
 * only accept one request at a time. It will only allow one instance
 * (singleton) and will be bound to a predefined port on localhost.
 */
public final class QueryCallbackListener extends Thread {

    private static QueryCallbackListener instance = null;

    private ServerSocket server = null;

    private boolean isRunning = false;

    private String response = null;

    private static final Log log = LogFactory.getLog(QueryCallbackListener.class);

    /**
     * Instantiates a new SubscriptionResponseListener listening on the given
     * port.
     * 
     * @throws IOException
     *             If an error setting up the communication socket occurred.
     */
    private QueryCallbackListener(int port) throws IOException {
        log.trace("listening for query callbacks on port " + port + " ...");
        server = new ServerSocket(port);
    }

    /**
     * @return The only instance of this class (singleton).
     * @throws IOException
     *             If an error setting up the communication socket occurred.
     */
    public static QueryCallbackListener getInstance(int port) throws IOException {
        if (instance == null) {
            instance = new QueryCallbackListener(port);
        }
        return instance;
    }

    /**
     * Keeps this listener running until {@link #stopRunning()} is called.
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
        isRunning = true;
        while (isRunning) {
            Socket client = null;
            try {
                client = server.accept();
                handleConnection(client);
            }
            catch (SAXException ex) {
                log.fatal(null, ex);
            }
            catch (SocketException e) {
                // server socket closed (stopRunning was called)
            }
            catch (IOException e) {
                log.fatal(null, e);
            }
            finally {
                if (client != null) {
                    try {
                        client.close();
                    }
                    catch (IOException e) {
                        log.fatal(null, e);
                    }
                }
            }
        }
    }

    /**
     * Handles an incoming HTTP connection, reading the contents, and parsing it
     * as XML.
     *
     * @param client
     *            The client Socket.
     * @throws IOException
     *             If an I/O error occurred.
     */
    private void handleConnection(final Socket client) throws IOException, SAXException {
        InputStream in = client.getInputStream();

        String s = IOUtils.toString(in);
        log.trace("Socket: " + s);
        this.response = "OKK";

        // notify everyone waiting on us
        synchronized (this) {
            this.notifyAll();
        }
        in.close();
    }

    /**
     * @return The received XML response.
     */
    public String fetchResponse() {
        String resp = this.response;
        this.response = null; // reset
        return resp;
    }

    /**
     * @return Wheter this thread is running.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Stops this thread from running.
     */
    public void stopRunning() {
        isRunning = false;
        instance = null;
        try {
            server.close();
        }
        catch (IOException e) {
            log.error(null, e);
        }
    }
}
