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
package fr.unicaen.iota.discovery.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class RootAccountAuth extends HttpServlet {

    private static final String PROP_FILE = "root-account.properties";
    private static final Log log = LogFactory.getLog(RootAccountAuth.class);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String message = "";
        String action = request.getParameter("action");
        if ("login".equals(action)) {
            if (!login(request)) {
                message = "?message=bad login or password";
            } else {
                request.getSession().setAttribute("root-account", "logged");
            }
        } else if ("logout".equals(action)) {
            request.getSession().setAttribute("root-account", null);
        }
        response.sendRedirect(getServletContext().getContextPath() + "/test" + message);
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

    private boolean login(HttpServletRequest request) {
        String login = request.getParameter("login");
        String pass = request.getParameter("passwd");
        Properties properties = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(PROP_FILE);
        try {
            properties.load(is);
        } catch (IOException ex) {
            log.fatal(null, ex);
            return false;
        }
        String pLogin = properties.getProperty("login");
        if (pLogin == null || !pLogin.equals(login)) {
            return false;
        }
        String pPass = properties.getProperty("password");
        if (pPass == null || !pPass.equals(pass)) {
            return false;
        }
        return true;
    }
}