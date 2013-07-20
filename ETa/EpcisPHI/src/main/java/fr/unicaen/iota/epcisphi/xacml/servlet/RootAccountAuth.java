/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
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
import fr.unicaen.iota.epcisphi.utils.User;
import fr.unicaen.iota.mu.Utils;
import fr.unicaen.iota.ypsilon.client.YPSilonClient;
import fr.unicaen.iota.ypsilon.client.model.UserLookupOut;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RootAccountAuth extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(RootAccountAuth.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("login".equals(request.getParameter("action"))) {
            String login = (request.getUserPrincipal() != null)? request.getUserPrincipal().getName() : null;
            if (login == null || login.isEmpty()) {
                request.setAttribute("message", "You are not authenticated.");
            } else {
                login = Utils.formatId(login);
                try {
                    YPSilonClient ypsilonClient = new YPSilonClient(Constants.YPSILON_ADDRESS, Constants.PKS_FILENAME,
                            Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
                    UserLookupOut userLookupOut = ypsilonClient.userLookup(login);
                    if (userLookupOut.getUserList().isEmpty()) {
                        throw new ImplementationExceptionResponse("User not found");
                    }
                    User user = new User();
                    user.setUserID(login);
                    user.setOwnerID(userLookupOut.getUserList().get(0).getOwner());
                    request.getSession().setAttribute("user", user);
                    request.getSession().setAttribute("cert", login);
                } catch (ImplementationExceptionResponse ex) {
                    request.setAttribute("message", ex.getMessage());
                    LOG.error("impl", ex);
                }
            }
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else if ("logout".equals(request.getParameter("action"))) {
            String cert = (String) (request.getSession().getAttribute("cert"));
            MapSessions.releaseSession(cert);
            request.getSession().setAttribute("user", null);
            request.getSession().setAttribute("cert", null);
            response.sendRedirect("index.jsp");
        }
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
