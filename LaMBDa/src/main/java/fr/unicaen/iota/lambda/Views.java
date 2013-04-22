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
import fr.unicaen.iota.ds.model.TEventItem;
import fr.unicaen.iota.nu.ONSEntryType;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.fosstrak.epcis.model.EPCISEventType;

public class Views {

    /**
     * Gets the view associated to the messages.
     * @param request The HTTP request to handle.
     * @param message The message to display.
     * @return The view associated to the messages.
     */
    public String message(HttpServletRequest request, String message) {
        request.setAttribute("message", message);
        return "/message.jsp";
    }

    /**
     * Gets the view associated to the tracing request.
     * @param request The HTTP request to handle.
     * @param events The EPCIS events grouped by EPCIS of the trace.
     * @param referentDS The referent DS of the EPC code associated to the trace.
     * @param signatures The signature checking of the events.
     * @return The view associated to the tracing request.
     */
    public String trace(HttpServletRequest request, Map<String, List<EPCISEventType>> events,
            String referentDS, Map<EPCISEventType, SignatureState> signatures) {
        request.setAttribute("events", events);
        request.setAttribute("referentDS", referentDS);
        request.setAttribute("signatures", signatures);
        return "/trace.jsp";
    }

    /**
     * Gets the view associated to the "queryEPCIS" request.
     * @param request The HTTP request to handle.
     * @param events The EPCIS events of the query.
     * @param signatures The signature checking of the events.
     * @return The view associated to the "queryEPCIS" request.
     */
    public String queryEPCIS(HttpServletRequest request, List<EPCISEventType> events,
            Map<EPCISEventType, SignatureState> signatures) {
        request.setAttribute("events", events);
        request.setAttribute("signatures", signatures);
        return "/queryepcis.jsp";
    }

    /**
     * Gets the view associated to the "queryDS" request.
     * @param request The HTTP request to handle.
     * @param events The DS events of the query.
     * @return The view associated to the "queryDS" request.
     */
    public String queryDS(HttpServletRequest request, List<TEventItem> events) {
        request.setAttribute("message", "queryDS not yet impleted");
        return "/message.jsp";
    }

    /**
     * Gets the view associated to the "queryONS" request.
     * @param request The HTTP request to handle.
     * @param entries The ONS entries of the query.
     * @return The view associated to the "queryONS" request.
     */
    public String queryONS(HttpServletRequest request, Map<ONSEntryType, String> entries) {
        request.setAttribute("entries", entries);
        return "/queryons.jsp";
    }

}
