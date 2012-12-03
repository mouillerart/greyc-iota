/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.xacml.servlet;

import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.utils.MapSessions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XiServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(XiServlet.class);

    @Override
    public void init() {
        MapSessions.init();
    }

    /**
     * Processes incoming XACML request and sends XACML response.
     *
     * @param req The HttpServletRequest.
     * @param rsp The HttpServletResponse.
     * @throws IOException If an error occurred while validating the request or
     * writing the response.
     */
    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse rsp) throws IOException {
        PrintWriter out = rsp.getWriter();
        try {
            rsp.setContentType("text/plain");
            log.debug("Receiving XACML request...");
            InputStream is = req.getInputStream();
            String request = readXACMLRequest(is);
            log.debug("create XACML request ...");
            RequestCtx reqCtx = RequestCtx.getInstance(new ByteArrayInputStream(request.getBytes()));
            log.debug("process policy");
            String resp = String.valueOf(processRequest(reqCtx));
            log.debug("process response");
            out.println(resp);
            rsp.setStatus(HttpServletResponse.SC_OK);
        } catch (ParsingException ex) {
            log.error("Error during parsing request.", ex);
            rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(String.valueOf(Result.DECISION_DENY));
        }
        out.flush();
        out.close();
    }

    private String readXACMLRequest(InputStream is) throws IOException {
        char[] end = {'<', '/', 'R', 'e', 'q', 'u', 'e', 's', 't', '>'};
        int endIndex = 0;
        StringBuilder response = new StringBuilder();
        int value = 0;
        boolean active = true;
        while (active) {
            value = is.read();
            if (value == -1) {
                throw new IOException("End of Stream");
            }
            response.append((char) value);
            if (value == end[endIndex]) {
                endIndex++;
            } else {
                endIndex = 0;
            }
            if (endIndex == end.length) {
                active = false;
            }
        }
        return response.toString();
    }

    public int processRequest(RequestCtx request) {
        try {
            ResponseCtx result = MapSessions.APM.evaluate(request);
            Iterator it = result.getResults().iterator();
            while (it.hasNext()) {
                Result res = (Result) it.next();
                if (res != null) {
                    return res.getDecision();
                }
            }
        } catch (Exception ex) {
            log.error("error", ex);
            return Result.DECISION_DENY;
        }
        return Result.DECISION_DENY;
    }
}
