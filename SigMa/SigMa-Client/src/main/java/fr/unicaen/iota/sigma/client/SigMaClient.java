/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.sigma.client;

import fr.unicaen.iota.sigma.wsdl.SigMaService;
import fr.unicaen.iota.sigma.wsdl.SigMaServicePortType;
import fr.unicaen.iota.sigma.xsd.Principal;
import fr.unicaen.iota.sigma.xsd.Verification;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 * This
 * <code>SigMAClient</code> performs the requests to the SigMA web service. This
 * class receives the address of the SigMA web service and sends the signature
 * verification resquests.
 */
public class SigMaClient {

    private static final Log log = LogFactory.getLog(SigMaClient.class);
    private SigMaServicePortType port;

    public SigMaClient(String address) {
        try {
            this.configureService(address);
        } catch (MalformedURLException ex) {
            log.error(ex.getMessage());
        }
    }

    public void configureService(String address) throws MalformedURLException {
        URL wsdlUrl = new URL(address + "?wsdl");
        SigMaService service = new SigMaService(wsdlUrl);
        port = service.getPort(SigMaServicePortType.class);

        // turn off chunked transfer encoding
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpConduit.setClient(httpClientPolicy);
    }

    public Verification verify(Principal principal) {
        Verification verification = port.verify(principal);
        return verification;
    }

    public Verification verify(EPCISEventType event) {
        Principal principal = new Principal();
        principal.setEvent(event);
        return verify(principal);
    }
}
