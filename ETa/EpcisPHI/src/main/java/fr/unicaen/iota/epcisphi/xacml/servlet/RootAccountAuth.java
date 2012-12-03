/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.epcisphi.xacml.servlet;

import fr.unicaen.iota.epcisphi.utils.Constants;
import fr.unicaen.iota.epcisphi.utils.MapSessions;
import fr.unicaen.iota.epcisphi.utils.SHA1;
import fr.unicaen.iota.epcisphi.utils.SessionLoader;
import fr.unicaen.iota.eta.user.client.GatewayClient;
import fr.unicaen.iota.eta.user.userservice.UserLoginOut;
import fr.unicaen.iota.eta.user.userservice_wsdl.ImplementationExceptionResponse;
import fr.unicaen.iota.eta.user.userservice_wsdl.SecurityExceptionResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RootAccountAuth extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    public static final String PROP_FILE = "root-account.properties";
    private static final Log LOG = LogFactory.getLog(RootAccountAuth.class);

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String message = "";
        if ("login".equals(request.getParameter("action"))) {
            String login = (String) request.getParameter("login");
            String pass = (String) request.getParameter("passwd");
            UserLoginOut userLoginOut;
            try {
                String hashPass = SHA1.makeSHA1Hash(pass);
                GatewayClient client = new GatewayClient(Constants.USERSERVICE_ADDRESS);
                userLoginOut = client.userLogin(login, hashPass);
                request.setAttribute("session-id", userLoginOut.getSid());
                message = SessionLoader.loadSession(userLoginOut.getSid(), login, request.getSession());
            } catch (NoSuchAlgorithmException ex) {
                message = "?message=" + ex.getMessage();
                LOG.error("nosuch", ex);
            } catch (ImplementationExceptionResponse ex) {
                message = "?message=" + ex.getMessage();
                LOG.error("impl", ex);
            } catch (SecurityExceptionResponse ex) {
                message = "?message=" + ex.getMessage();
                LOG.error("secur", ex);
            }
        } else if ("logout".equals(request.getParameter("action"))) {
            String sessionId = (String) (request.getSession().getAttribute("session-id"));
            try {
                GatewayClient client = new GatewayClient(Constants.USERSERVICE_ADDRESS);
                client.userLogout(sessionId);
                SessionLoader.clearSession(request.getSession());
                MapSessions.releaseSession(sessionId);
            } catch (ImplementationExceptionResponse ex) {
                message = "?message=" + ex.getMessage();
            } catch (SecurityExceptionResponse ex) {
                message = "?message=" + ex.getMessage();
            }
            request.getSession().setAttribute("session-id", null);
            MapSessions.releaseSession(sessionId);
        }
        response.sendRedirect(getServletContext().getContextPath() + "/index.jsp" + message);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);


    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);


    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";

    }// </editor-fold>
}
