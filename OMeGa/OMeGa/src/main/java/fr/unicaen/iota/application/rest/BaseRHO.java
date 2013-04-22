/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.application.rest;

import fr.unicaen.iota.application.AccessInterface;
import fr.unicaen.iota.application.Configuration;
import fr.unicaen.iota.application.soap.IoTaException;
import fr.unicaen.iota.application.soap.client.IoTaFault;
import fr.unicaen.iota.mu.EPCISEventTypeHelper;
import fr.unicaen.iota.mu.ExtensionTypeHelper;
import fr.unicaen.iota.tau.model.Identity;
import fr.unicaen.iota.xi.client.EPCISPEP;
import fr.unicaen.iota.xi.utils.Utils;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import org.fosstrak.epcis.model.EPCISEventType;
import org.w3c.dom.Element;

/**
 *
 */
public abstract class BaseRHO extends HttpServlet {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    protected AccessInterface controler;
    private final Identity anonymous;
    private final EPCISPEP xiclient;

    public BaseRHO() {
        anonymous = new Identity();
        anonymous.setAsString(Configuration.DEFAULT_IDENTITY);
        xiclient = new EPCISPEP(Configuration.XI_URL, Configuration.PKS_FILENAME, Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
    }

    private void checkAuth(Principal authId, Identity id) throws IoTaException {
        if (authId == null && id == anonymous) {
            return;
        }
        if (authId == null) {
            throw new IoTaException("No authentication", IoTaFault.tau.getCode());
        }
        if (id == null || id.getAsString().isEmpty()) {
            throw new IoTaException("No identity to use", IoTaFault.tau.getCode());
        }
        String tlsId = fr.unicaen.iota.mu.Utils.formatId(authId.getName());
        String idToPass = fr.unicaen.iota.mu.Utils.formatId(id.getAsString());
        int chk = xiclient.canBe(tlsId, idToPass);
        if (!Utils.responseIsPermit(chk)) {
            throw new IoTaException(authId.getName() + " isn't allowed to pass as " + id.getAsString(), IoTaFault.tau.getCode());
        }
    }

    protected abstract AccessInterface getControler() throws Exception;

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
            if (controler == null) {
                try {
                    controler = getControler();
                } catch (Exception ex) {
                    out.println("{ \"error\": \"Failed to setup: " + ex.getMessage() + "\" }");
                    return;
                }
            }
            List<EPCISEventType> list;
            try {
                Identity id = new Identity();
                String pid = request.getParameter("id");
                id.setAsString(pid == null ? Configuration.DEFAULT_IDENTITY : pid);
                checkAuth(request.getUserPrincipal(), id);
                list = controler.traceEPC(id, request.getParameter("epc"));
            } catch (IoTaException ex) {
                out.println("{ \"error\": \"" + IoTaFault.explain(ex) + ": " + ex.getMessage() + "\" }");
                return;
            } catch (RemoteException ex) {
                out.println("{ \"error\": \"" + ex.getMessage() + "\" }");
                return;
            }
            out.println("[");
            int i = 0;
            boolean lookForLocation = "true".equalsIgnoreCase(request.getParameter("theta"));
            for (EPCISEventType event : list) {
                EPCISEventTypeHelper evt = new EPCISEventTypeHelper(event);
                out.println("  {");
                out.println("    \"type\": \"" + evt.getType() + "\",");
                out.println("    \"eventTime\": \"" + SDF.format(evt.getEventTime().getTime()) + "\",");
                out.println("    \"recordTime\": \"" + SDF.format(evt.getRecordTime().getTime()) + "\",");
                out.println("    \"bizloc\": \"" + evt.getBizLocation() + "\",");
                out.println("    \"bizstep\": \"" + evt.getBizStep() + "\",");
                out.println("    \"disposition\": \"" + evt.getDisposition() + "\",");
                out.println("    \"readPoint\": \"" + evt.getReadPoint() + "\",");
                switch (evt.getType()) {
                    case OBJECT:
                        out.println("    \"action\": \"" + evt.getAction() + "\",");
                        printStringList(out, "epcList", evt.getEpcList(), "    ", false);
                        break;
                    case TRANSACTION:
                        out.println("    \"action\": \"" + evt.getAction() + "\",");
                        out.println("    \"parentID\": \"" + evt.getParentID() + "\",");
                        printStringList(out, "epcList", evt.getEpcList(), "    ", false);
                        break;
                    case AGGREGATION:
                        out.println("    \"action\": \"" + evt.getAction() + "\",");
                        out.println("    \"parentID\": \"" + evt.getParentID() + "\",");
                        printStringList(out, "children", evt.getChildren(), "    ", false);
                        break;
                    case QUANTITY:
                        out.println("    \"epcClass\": \"" + evt.getEPCClass() + "\",");
                        out.println("    \"quantity\": " + evt.getQuantity() + ",");
                        break;
                }
                if (lookForLocation) {
                    String location = evt.getExtension("location");
                    if (location != null) {
                        out.println("    \"location\": \"" + location + "\",");
                    }
                }
                printExtension(out, "baseExtension", evt.getBaseExtension(), "    ", false);
                printMap(out, "otherAttributes", evt.getOtherAttributes(), "    ", false);
                printAny(out, evt.getAny(), "    ", false);
                printExtension(out, "extension", evt.getExtension(), "    ", true);
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

    private void printAny(PrintWriter out, List<Object> list, String indent, boolean lastOne) {
        out.println(indent + "\"any\": {");
        if (list != null) {
            int j = 0;
            int last = list.size() - 1;
            for (Object obj : list) {
                String key = "?";
                String val;
                if (obj instanceof Element) {
                    Element el = (Element) obj;
                    // TODO: do we use prefix and/or namespace?
                    key = el.getLocalName();
                    val = el.getTextContent().replace("\"", "\\\"");
                } else {
                    val = obj.toString();
                }
                if (j < last) {
                    out.println(indent + "  \"" + key + "\": \"" + val + "\",");
                } else {
                    out.println(indent + "  \"" + key + "\": \"" + val + "\"");
                }
                j++;
            }
        }
        out.println(indent + (lastOne ? "}" : "},"));
    }

    private void printStringList(PrintWriter out, String name, List<String> list, String indent, boolean lastOne) {
        out.println(indent + "\"" + name + "\": [");
        if (list != null) {
            int j = 0;
            int last = list.size() - 1;
            for (Object obj : list) {
                if (j < last) {
                    out.println(indent + "  \"" + obj + "\",");
                } else {
                    out.println(indent + "  \"" + obj + "\"");
                }
                j++;
            }
        }
        out.println(indent + (lastOne ? "]" : "],"));
    }

    private void printMap(PrintWriter out, String name, Map<QName, String> map, String indent, boolean lastOne) {
        out.println(indent + "\"" + name + "\": {");
        if (map != null) {
            Collection<Map.Entry<QName, String>> entries = map.entrySet();
            int j = 0;
            int last = entries.size() - 1;
            for (Map.Entry<QName, String> attr : entries) {
                if (j < last) {
                    out.println(indent + "  \"" + attr.getKey() + "\": \"" + attr.getValue() + "\",");
                } else {
                    out.println(indent + "  \"" + attr.getKey() + "\": \"" + attr.getValue() + "\"");
                }
            }
        }
        out.println(indent + (lastOne ? "}" : "},"));
    }

    private void printExtension(PrintWriter out, String name, ExtensionTypeHelper extension, String indent, boolean lastOne) {
        out.println(indent + "\"" + name + "\": {");
        printMap(out, "otherAttributes", extension.getOtherAttributes(), indent + "  ", false);
        printAny(out, extension.getAny(), indent + "  ", true);
        out.println(indent + (lastOne ? "}" : "},"));
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
