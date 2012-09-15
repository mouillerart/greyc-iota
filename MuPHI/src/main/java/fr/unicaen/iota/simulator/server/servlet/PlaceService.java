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
package fr.unicaen.iota.simulator.server.servlet;

import fr.unicaen.iota.simulator.server.model.PipeContainer;
import fr.unicaen.iota.simulator.server.model.PlaceFIFO;
import fr.unicaen.iota.simulator.server.util.AccessControlModule;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class PlaceService extends HttpServlet {

    private static final Log log = LogFactory.getLog(PlaceService.class);

    @Override
    public void init() throws ServletException {
        super.init();
        PipeContainer pc = PipeContainer.getInstance();
        pc.init();
        pipes = pc.getPipes();

    }
    private Map<String, PlaceFIFO> pipes;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String publicKey;
        if ((publicKey = request.getParameter("id")) == null) {
            throw new HTTPException(500);
        }
        String m;
        if ((m = request.getParameter("m")) == null) {
            throw new HTTPException(500);
        }
        if (!AccessControlModule.isAuthenticated(publicKey, m)) {
            throw new HTTPException(500);
        }
        if ("reserve".equals(request.getParameter("action"))) {
            log.trace("RESERVE ACTION CALLED");
            String placeId;
            if ((placeId = request.getParameter("placeId")) == null) {
                throw new HTTPException(500);
            }
            String canalSize;
            if ((canalSize = request.getParameter("canalSize")) == null) {
                throw new HTTPException(500);
            }
            PlaceFIFO pipe = pipes.get(placeId);
            String reservedId = pipe.reserve(Integer.parseInt(canalSize));
            out.write(buildReserveXmlResponse(reservedId));
            out.flush();
        } else if ("put".equals(request.getParameter("action"))) {
            log.trace("PUT ACTION CALLED");
            String placeId;
            if ((placeId = request.getParameter("placeId")) == null) {
                throw new HTTPException(500);
            }
            String reservedId;
            if ((reservedId = request.getParameter("reservedId")) == null) {
                throw new HTTPException(500);
            }
            String epc;
            if ((epc = request.getParameter("epc")) == null) {
                throw new HTTPException(500);
            }
            PlaceFIFO place = pipes.get(placeId);
            boolean res = place.put(reservedId, epc);
            out.write(buildPutXmlResponse(res,place.getContentOccupation()));
            out.flush();
        } else if ("peek".equals(request.getParameter("action"))) {
            String placeId;
            if ((placeId = request.getParameter("placeId")) == null) {
                throw new HTTPException(500);
            }
            String msg = createXMLResponse(pipes.get(placeId).peek());
            out.write(msg);
            out.flush();
        } else if ("pipeSize".equals(request.getParameter("action"))) {
            String placeId;
            if ((placeId = request.getParameter("placeId")) == null) {
                throw new HTTPException(500);
            }
            String msg = buildPipeSizeXmlResponse(String.valueOf(pipes.get(placeId).getSize()));
            out.write(msg);
            out.flush();
        } else {
            throw new HTTPException(500);
        }
        out.close();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
    }// </editor-fold>

    private String createXMLResponse(List<String> peek) {
        StringBuilder res = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        res.append("<simulator>\n");
        res.append("\t<objects>\n");
        for (String code : peek) {
            res.append("\t\t<object>");
            res.append(code);
            res.append("</object>\n");
        }
        res.append("\t</objects>\n");
        res.append("</simulator>\n");
        return res.toString();
    }

    private String buildReserveXmlResponse(String reservedId) {
        boolean report = reservedId != null;
        StringBuilder res = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        res.append("<simulator>\n");
        res.append("\t<report>\n");
        res.append("\t\t<succeed>");
        res.append(report);
        res.append("</succeed>\n");
        if (report) {
            res.append("\t\t<reservedId>");
            res.append(reservedId);
            res.append("</reservedId>\n");
        }
        res.append("\t</report>\n");
        res.append("</simulator>\n");
        return res.toString();
    }

    private String buildPipeSizeXmlResponse(String size) {
        StringBuilder res = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        res.append("<simulator>\n");
        res.append("\t<pipeSize>");
        res.append(size);
        res.append("</pipeSize>\n");
        res.append("</simulator>\n");
        return res.toString();
    }
    
    private String buildPutXmlResponse(boolean report, double volume) {
        StringBuilder res = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        res.append("<simulator>\n");
        res.append("\t<report>\n");
        res.append("\t\t<succeed>");
        res.append(report);
        res.append("</succeed>\n");
        res.append("\t\t<remainingVolume>");
        res.append(volume);
        res.append("</remainingVolume>\n");
        res.append("\t</report>\n");
        res.append("</simulator>\n");
        return res.toString();
    }
}
