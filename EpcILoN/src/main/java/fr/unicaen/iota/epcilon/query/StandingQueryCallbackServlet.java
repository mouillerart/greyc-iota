/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.epcilon.query;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPCISQueryDocumentType;

/**
 * Servlet implementation class StandingQueryCallbackServlet
 */
public class StandingQueryCallbackServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(StandingQueryCallbackServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public StandingQueryCallbackServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        rsp.setContentType("text/plain");
        BufferedReader br = req.getReader();
        JAXBContext context;
        try {
            context = JAXBContext.newInstance("org.fosstrak.epcis.model");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<EPCISQueryDocumentType> queryResults = (JAXBElement<EPCISQueryDocumentType>) unmarshaller.unmarshal(br);
            EPCISQueryDocumentType doc = queryResults.getValue();
            StandingQueryCallbackModule cm = new StandingQueryCallbackModule();
            cm.saveEvents(doc);
        } catch (NullPointerException e) {
            LOG.error(null, e);
        } catch (JAXBException e) {
            LOG.error(null, e);
        }
    }
}