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
package fr.unicaen.iota.xacml.servlet;

import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.model.Session;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.utils.MapSessions;
import fr.unicaen.iota.utils.SessionLoader;
import fr.unicaen.iota.xacml.conf.Configuration;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String message = "";
        if ("login".equals(request.getParameter("action"))) {
            String login = (String) request.getParameter("login");
            String pass = (String) request.getParameter("passwd");
            DsClient dsClient = new DsClient(Configuration.DS_ADDRESS);
            String SESSION_ID = null;
            try {
                Session session = dsClient.userLogin(fr.unicaen.iota.discovery.client.util.Configuration.DEFAULT_SESSION, login, pass);
                SESSION_ID = session.getSessionId();
                message = SessionLoader.loadSession(SESSION_ID, dsClient, login, request.getSession());
            } catch (EnhancedProtocolException ex) {
                message = "?message=" + ex.getMessage();
            } catch (RemoteException e) {
                message = "?message=" + "Internal server error";
            }
        } else if ("logout".equals(request.getParameter("action"))) {
            DsClient dsClient = new DsClient(Configuration.DS_ADDRESS);
            String sessionId = (String) (request.getSession().getAttribute("session-id"));
            try {
                dsClient.userLogout(sessionId);
                SessionLoader.clearSession(request.getSession());
                MapSessions.releaseSession(sessionId);

            } catch (EnhancedProtocolException ex) {
                message = "?message=" + ex.getMessage();
            } catch (RemoteException e) {
                message = "?message=Internal server error";
            }
            SessionLoader.clearSession(request.getSession());
            MapSessions.releaseSession(sessionId);
        }
        //getServletContext().getRequestDispatcher("/test").forward(request, response);
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
