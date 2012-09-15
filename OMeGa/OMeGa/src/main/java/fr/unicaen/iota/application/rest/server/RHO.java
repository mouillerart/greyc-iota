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
package fr.unicaen.iota.application.rest.server;

import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.rmi.AccessInterface;
import fr.unicaen.iota.application.soap.server.Configuration;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class RHO extends HttpServlet {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            AccessInterface rmiServer;
            try {
                URI uri = new URI(Configuration.RMI_URL);
                int port = uri.getPort();
                port = port == -1 ? 1099 : port;
                String name = uri.getPath().substring(1);
                String host = uri.getHost();
                Registry registry = LocateRegistry.getRegistry(host, port);
                rmiServer = (AccessInterface) registry.lookup(name);
            } catch (Exception e) {
                out.println("{ \"error\": \"Failed to setup for RMI " + e.getMessage() + "\" }");
                return;
            }
            List<EPCISEvent> list;
            try {
                list = rmiServer.traceEPC(request.getParameter("epc"));
            } catch (RemoteException ex) {
                out.println("{ \"error\": \"" + ex.getMessage() + "\" }");
                return;
            }
            out.println("[");
            int i = 0;
            for (EPCISEvent evt : list) {
                out.println("  {");
                out.println("    \"type\": \"" + evt.getType() + "\",");
                out.println("    \"action\": \"" + evt.getAction() + "\",");
                out.println("    \"bizloc\": \"" + evt.getBizLoc() + "\",");
                out.println("    \"bizstep\": \"" + evt.getBizLoc() + "\",");
                out.println("    \"children\": [");
                Collection<String> children = evt.getChildren();
                int j = 0;
                for (String child: children) {
                    if (j < children.size() - 1) {
                        out.print("      \"" + child + "\",");
                    } else {
                        out.print("      \"" + child + "\"");
                    }
                    j ++;
                }
                out.println("    ],");
                out.println("    \"disposition\": \"" + evt.getDisposition() + "\",");
                out.println("    \"epcclass\": \"" + evt.getEPCClass() + "\",");
                out.println("    \"epclist\": [");
                Collection<String> epcs = evt.getEpcs();
                j = 0;
                for (String epc: epcs) {
                    if (j < epcs.size() - 1) {
                        out.println("      \"" + epc + "\",");
                    } else {
                        out.println("      \"" + epc + "\"");
                    }
                    j++;
                }
                out.println("    ],");
                out.println("    \"eventtime\": \"" + SDF.format(evt.getEventTime().getTime()) + "\",");
                out.println("    \"parentid\": \"" + evt.getParentID() + "\",");
                String q = evt.getQuantity();
                out.println("    \"quantity\": " + (q.isEmpty() ? "0" : q) + ",");
                out.println("    \"readpoint\": \"" + evt.getReadPoint() + "\",");
                out.println("    \"recordtime\": \"" + SDF.format(evt.getInsertedTime().getTime()) + "\",");
                if (i < list.size() - 1) {
                    out.println("  },");
                } else {
                    out.println("  }");
                }
                i++;
            }
            out.println("]");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
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
     * Handles the HTTP
     * <code>POST</code> method.
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
