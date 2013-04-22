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
package fr.unicaen.iota.lambda;

import fr.unicaen.iota.lambda.Utils.SignatureState;
import fr.unicaen.iota.lambda.Utils.EventsHandler;
import fr.unicaen.iota.application.soap.IoTaException;
import fr.unicaen.iota.ds.model.TEventItem;
import fr.unicaen.iota.lambda.Utils.Utils;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.sigma.xsd.Verification;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 * The main servlet.
 */
public class Demo extends HttpServlet {

    private final Views views = new Views();
    private static final Log LOG = LogFactory.getLog(Demo.class);

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request The servlet request.
     * @param response The servlet response.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request The servlet request.
     * @param response The servlet response.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request The servlet request.
     * @param response The servlet response.
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String view = processAction(request, response);
            if (view != null) {
                processView(view, request, response);
            }
        } catch (Throwable t) {
            LOG.error("Error during operation", t);
            String view = error(request, response, t);
            if (view != null) {
                try {
                    processView(view, request, response);
                } catch (Throwable e) {
                    throw new ServletException(e);
                }
            }
        }
    }

    /**
     * Process the action.
     * @param request The servlet request.
     * @param response The servlet response.
     * @return The view corresponding to the action.
     * @throws Throwable
     */
    private String processAction(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String service = request.getParameter("service");
        if ("epcis".equals(service)) {
            return queryEPCIS(request);
        }
        else if ("ds".equals(service)) {
            return queryDS(request);
        }
        else if ("ons".equals(service)) {
            return queryONS(request);
        }
        else if ("all".equals(service)) {
            return trace(request);
        }
        else {
            throw new ServletException("No service selected");
        }
    }

    /**
     * Performs the trace.
     * @param request The servlet request.
     * @return The view associated to the "trace" method.
     * @throws IoTaException If an error occurred during the IoTa operations.
     * @throws Exception If an unexpected error occurred.
     */
    private String trace(HttpServletRequest request) throws IoTaException, Exception {
        String login = (request.getUserPrincipal() != null)? request.getUserPrincipal().getName() : null;
        String epc = request.getParameter("epc");
        if (epc == null || epc.isEmpty()) {
            throw new ServletException("The EPC code is missing");
        }
        boolean useSignature = (request.getParameter("signature") != null)? true : false;
        EventsHandler eventsHandler = new EventsHandler(login, Configuration.OMEGA_URL, Configuration.PKS_FILENAME,
                Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
        String referentDS = eventsHandler.getReferentDS(epc);
        Map<String, List<EPCISEventType>> eventsByEPCIS = eventsHandler.traceEPCByEPCIS(epc);
        for (List<EPCISEventType> eventList : eventsByEPCIS.values()) {
            Utils.sortEPCISEventList(eventList);
        }
        Map<EPCISEventType, SignatureState> signatures = null;
        if (useSignature && eventsByEPCIS != null) {
            eventsHandler.initSigmaClient();
            signatures = new HashMap<EPCISEventType, SignatureState>();
            for (Entry<String, List<EPCISEventType>> epcisAndEvents : eventsByEPCIS.entrySet()) {
                for (EPCISEventType event : epcisAndEvents.getValue()) {
                    SignatureState state = SignatureState.NOT_VERIFIED;
                    try {
                        Verification verif = eventsHandler.verifySignature(event);
                        state = (verif.getVerifyResponse().isValue()) ? SignatureState.AUTHENTIC : SignatureState.WRONG;
                    } catch (Exception ex) {
                        state = SignatureState.VERIFICATION_ERROR;
                    }
                    signatures.put(event, state);
                }
            }
        }
        return views.trace(request, eventsByEPCIS, referentDS, signatures);
    }

    /**
     * Performs the queryEPCIS.
     * @param request The servlet request.
     * @return The view associated to the "queryEPCIS" method.
     * @throws IoTaException If an error occurred during the IoTa operations.
     * @throws Exception If an unexpected error occurred.
     */
    private String queryEPCIS(HttpServletRequest request) throws IoTaException, Exception {
        String login = (request.getUserPrincipal() != null)? request.getUserPrincipal().getName() : null;
        String epcis = request.getParameter("serviceURL");
        String epc = request.getParameter("epc");
        if (epcis == null || epcis.isEmpty() || epc == null || epc.isEmpty()) {
            throw new ServletException("Enter an EPC code and an EPCIS to query");
        }
        boolean useSignature = (request.getParameter("signature") != null)? true : false;
        EventsHandler eventsHandler = new EventsHandler(login, Configuration.OMEGA_URL, Configuration.PKS_FILENAME,
                Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
        List<EPCISEventType> events = eventsHandler.queryEPCIS(epc, epcis);
        Utils.sortEPCISEventList(events);
        Map<EPCISEventType, SignatureState> signatures = null;
        if (useSignature && events != null) {
            eventsHandler.initSigmaClient();
            signatures = new HashMap<EPCISEventType, SignatureState>(events.size());
            for (EPCISEventType event : events) {
                SignatureState state = SignatureState.NOT_VERIFIED;
                try {
                    Verification verif = eventsHandler.verifySignature(event);
                    state = (verif.getVerifyResponse().isValue()) ? SignatureState.AUTHENTIC : SignatureState.WRONG;
                }
                catch (Exception ex) {
                    state = SignatureState.VERIFICATION_ERROR;
                }
                signatures.put(event, state);
            }
        }
        return views.queryEPCIS(request, events, signatures);
    }

    /**
     * Performs the queryDS.
     * @param request The servlet request.
     * @return The view associated to the "queryDS" method.
     * @throws IoTaException If an error occurred during the IoTa operations.
     * @throws Exception If an unexpected error occurred.
     */
    private String queryDS(HttpServletRequest request) throws IoTaException, Exception {
        String login = (request.getUserPrincipal() != null)? request.getUserPrincipal().getName() : null;
        String ds = request.getParameter("serviceURL");
        String epc = request.getParameter("epc");
        if (ds == null || ds.isEmpty() || epc == null || epc.isEmpty()) {
            throw new ServletException("Enter an EPC code and a DS to query");
        }
        EventsHandler eventsHandler = new EventsHandler(login, Configuration.OMEGA_URL, Configuration.PKS_FILENAME,
                Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
        List<TEventItem> events = eventsHandler.queryDS(epc, ds);
        return views.queryDS(request, events);
    }

    /**
     * Performs the queryONS.
     * @param request The servlet request.
     * @return The view associated to the "queryONS" method.
     * @throws IoTaException If an error occurred during the IoTa operations.
     * @throws Exception If an unexpected error occurred.
     */
    private String queryONS(HttpServletRequest request) throws IoTaException, Exception {
        String login = (request.getUserPrincipal() != null)? request.getUserPrincipal().getName() : null;
        String epc = request.getParameter("epc");
        if (epc == null || epc.isEmpty()) {
            throw new ServletException("The EPC code is missing");
        }
        EventsHandler eventsHandler = new EventsHandler(login, Configuration.OMEGA_URL, Configuration.PKS_FILENAME,
                Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
        Map<ONSEntryType, String> entries = eventsHandler.queryONS(epc);
        return views.queryONS(request, entries);
    }

    /**
     * Forwards the request to the view.
     * @param view The view to process.
     * @param request The servlet request.
     * @param response The servlet response.
     * @throws Throwable If the view can be forwarded.
     */
    private void processView(String view, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        if (view.endsWith(".jsp") || view.endsWith(".jspx")) {
            request.getRequestDispatcher(view).forward(request, response);
        }
        else {
            throw new IllegalStateException("no view " + view);
        }
    }

    /**
     * Process the error message.
     * @param request The servlet request.
     * @param response The servlet response.
     * @param e The exception to process.
     * @return The view corresponding to the errors.
     */
    public String error(HttpServletRequest request, HttpServletResponse response, Throwable e) {
        String msg = "Internal error : " + e.getMessage();
        LOG.error(msg, e);
        return views.message(request, msg);
    }

}
