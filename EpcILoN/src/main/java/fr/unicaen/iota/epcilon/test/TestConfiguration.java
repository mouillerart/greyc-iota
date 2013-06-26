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
package fr.unicaen.iota.epcilon.test;

import fr.unicaen.iota.epcilon.conf.Configuration;
import fr.unicaen.iota.epcilon.model.EventToPublish;
import fr.unicaen.iota.epcilon.query.StandingQueryCaptureModule;
import fr.unicaen.iota.epcilon.util.HibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.Subscribe;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 *
 */
public class TestConfiguration extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(TestConfiguration.class);
    private static Class<?>[] DB = {
        EventToPublish.class
    };

    /**
     * Processes requests for HTTP
     * <code>GET</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Test configuration</title>");
            out.println("<style type=\"text/css\">");
            out.println("body{font-family: arial, sans}");
            out.println("div{padding: 10px;}");
            out.println("iframe{");
            out.println("width: 100%;");
            out.println("border: 1px solid gray;");
            out.println("background-color: #eff0f1;");
            out.println("font-size: 10px;");
            out.println("}");
            out.println("H1{color: #414651;}");
            out.println("H2{color: gray;}");
            out.println(".name{color: blue;}");
            out.println(".param{color: green;}");
            out.println(".error{color: red;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Test the current DS link configuration</h1>");
            out.println("</body>");

            out.println("<h2>Service configuration: </h2>");
            out.println("<div><span class=\"name\">Service address: </span><span class=\"param\">" + Configuration.DISCOVERY_SERVICE_ADDRESS + "</span></div>");
            out.println("<form method=\"POST\" action=\"test?action=serviceaddress\" target=\"serviceaddresseframe\"><input type=\"submit\" value=\"Test address\" /></form>");
            out.println("<iframe name=\"serviceaddresseframe\" ></iframe>");
            out.println("<h2>Authentification configuration: </h2>");
            out.println("<div><span class=\"name\">identity: </span><span class=\"param\">" + Configuration.IDENTITY + "</span></div>");
            out.println("<form method=\"POST\" action=\"test?action=login\" target=\"loginframe\"><input type=\"submit\" value=\"Test login\" /></form>");
            out.println("<iframe name=\"loginframe\" ></iframe>");
            out.println("<h2>Database configuration: </h2>");
            out.println("<form method=\"POST\" action=\"test?action=db\" target=\"dbframe\"><input type=\"submit\" value=\"Test Database\" /></form>");
            out.println("<iframe name=\"dbframe\" ></iframe>");

            out.println("<h2>Subscription configuration: </h2>");
            out.println("<form method=\"POST\" action=\"test?action=sub\" target=\"subframe\"><input type=\"submit\" value=\"Test Subscription\" /></form>");
            out.println("<iframe name=\"subframe\" ></iframe>");

            out.println("<h2>Default parameters: </h2>");

            out.println("<h2>Publisher parameters: </h2>");
            out.println("<div><span class=\"name\">publisher frequency: </span><span class=\"param\">" + Configuration.PUBLISHER_FREQUENCY + "ms</span></div>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        processAction(req, rsp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        processRequest(req, rsp);
    }

    /**
     * Processes requests for HTTP
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>tester</title>");
            out.println("<style type=\"text/css\">");
            out.println("body{font-size: 12px;}");
            out.println(".name{color: blue;}");
            out.println(".param{color: green;}");
            out.println(".error{color: red;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            String action = request.getParameter("action");
            if ("serviceaddress".equals(action)) {
                /*try {
                    out.println("start test service address !");
                    out.println("<br/>");
                    out.println("<span class=\"name\">executing Hello operation ...</span> ");
                    out.flush();
                    new DsClient(Configuration.DISCOVERY_SERVICE_ADDRESS).hello(fr.unicaen.iota.discovery.client.util.Configuration.DEFAULT_SESSION);
                    out.println("<span class=\"param\">[ DONE ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"name\">finished:</span> <span class=\"param\">[ SUCCEDED ]</span>");
                    out.flush();
                    out.println("<br/>");
                } catch (EnhancedProtocolException ex) {
                    LOG.error("Impossible de se connecter au DS référant: " + ex.getMessage(), null);
                    out.println("<span class=\"error\">[ FAILED ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"error\">finished: [ ERROR ]</span>");
                    out.flush();
                } catch (RemoteException ex) {
                    LOG.error("Impossible de se connecter au DS référant", ex);
                    out.println("<span class=\"error\">[ FAILED ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"error\">finished: [ ERROR ]</span>");
                    out.flush();
                }*/
            } else if ("db".equals(action)) {
                out.println("start test DB connection !");
                out.flush();
                out.println("<br/>");
                Session session = null;
                try {
                    session = HibernateUtil.getSessionFactory().openSession();
                    for (Class<?> c : DB) {
                        out.println("<span class=\"name\">list " + c.getSimpleName() + " ...</span>");
                        out.flush();
                        Criteria criteria = session.createCriteria(c);
                        criteria.setMaxResults(1);
                        criteria.list();
                        out.println("<span class=\"param\">[ DONE ]</span>");
                        out.flush();
                        out.println("<br/>");
                    }
                    session.close();
                    out.println("<span class=\"name\">finished:</span> <span class=\"param\">[ SUCCEDED ]</span>");
                    out.flush();
                    out.println("<br/>");
                } catch (Exception e) {
                    LOG.error("Failed to complite database test", e);
                    out.println("<span class=\"error\">[ FAILED ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"error\">finished: [ ERROR ]</span>");
                    out.flush();
                    session.close();
                }
            } else if ("sub".equals(action)) {
                out.println("start test EPCIS subscription !");
                out.flush();
                out.println("<br/>");
                QueryControlClient client;
                try {
                    out.println("<span class=\"name\"> Sending subscription to " + Configuration.DEFAULT_QUERY_CLIENT_ADDRESS + "...</span>");
                    out.flush();
                    client = new QueryControlClient(Configuration.DEFAULT_QUERY_CLIENT_ADDRESS);
                    Subscribe subscribe = StandingQueryCaptureModule.createScheduleSubscribe("SimpleEventQuery", "test",
                            Configuration.DEFAULT_QUERY_CALLBACK_ADDRESS, Configuration.SUBSCRIPTION_TYPE, Configuration.SUBSCRIPTION_VALUE);
                    client.subscribe(subscribe);
                    LOG.info("Subscription shared");
                    out.println("<span class=\"param\">[ DONE ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"name\"> trying to unsubscribe...</span>");
                    out.flush();
                    client.unsubscribe("test");
                    LOG.info("Subscription stoppped");
                    out.println("<span class=\"param\">[ DONE ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"name\">finished:</span> <span class=\"param\">[ SUCCEDED ]</span>");
                    out.flush();
                    out.println("<br/>");
                } catch (Exception e) {
                    LOG.error("Failed to complete subscription test", e);
                    out.println("<span class=\"error\">[ FAILED ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"error\">finished: [ ERROR ]</span>");
                    out.flush();
                }
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
}
