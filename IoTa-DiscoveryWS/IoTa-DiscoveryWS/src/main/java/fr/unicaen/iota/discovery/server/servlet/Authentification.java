/*
 *  This program is a part of the IoTa project.
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

import fr.unicaen.iota.discovery.server.hibernate.User;
import fr.unicaen.iota.discovery.server.querycontrol.DSControler;
import fr.unicaen.iota.discovery.server.util.ProtocolException;
import fr.unicaen.iota.discovery.server.util.Session;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class for Servlet: Authentification
 *
 */
public class Authentification extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Java-doc)
     *
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public Authentification() {
        super();
    }

    /*
     * (non-Java-doc)
     *
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     * HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /*
     * (non-Java-doc)
     *
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
     * HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String sessionId;
        try {
            sessionId = new DSControler().userLogin(request.getParameter("login"), request.getParameter("passwd"));
            if (sessionId == null) {
                throw new ProtocolException(2002, "not valid session");
            }
        } catch (ProtocolException pe) {
            request.getRequestDispatcher("index.jsp?value=false").forward(request, response);
            return;
        }
        User user = Session.getUser(sessionId);
        session.setAttribute("user", user);
        session.setAttribute("sessionID", sessionId);
        request.getRequestDispatcher("lookupService.jsp").forward(request, response);
    }
}
