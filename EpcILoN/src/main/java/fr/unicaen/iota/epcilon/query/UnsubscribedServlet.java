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

import fr.unicaen.iota.epcilon.conf.Configuration;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;
import org.fosstrak.epcis.soap.NoSuchSubscriptionExceptionResponse;
import org.fosstrak.epcis.soap.SecurityExceptionResponse;
import org.fosstrak.epcis.soap.ValidationExceptionResponse;

/**
 *
 */
public class UnsubscribedServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(UnsubscribedServlet.class);

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            QueryControlClient client = new QueryControlClient(Configuration.DEFAULT_QUERY_CLIENT_ADDRESS);
            try {
                client.unsubscribe(Configuration.SUBSCRIPTION_KEY);
                LOG.info("Subscription stoppped");
                request.getRequestDispatcher("subscribe.jsp").forward(request, response);
            } catch (RemoteException ex) {
                LOG.error("unable to unsubscribe", ex);
            } catch (NoSuchSubscriptionExceptionResponse ex) {
                LOG.error("unable to unsubscribe", ex);
            } catch (ImplementationExceptionResponse ex) {
                LOG.error("unable to unsubscribe", ex);
            } catch (SecurityExceptionResponse ex) {
                LOG.error("unable to unsubscribe", ex);
            } catch (ValidationExceptionResponse ex) {
                LOG.error("unable to unsubscribe", ex);
            }
        } catch (Exception ex) {
            LOG.error("unable to subscribe", ex);
        }
    } 

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
