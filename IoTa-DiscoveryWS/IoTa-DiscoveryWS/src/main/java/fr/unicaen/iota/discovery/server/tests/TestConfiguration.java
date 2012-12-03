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
package fr.unicaen.iota.discovery.server.tests;

import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.util.Configuration;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.discovery.server.hibernate.Partner;
import fr.unicaen.iota.discovery.server.hibernate.User;
import fr.unicaen.iota.discovery.server.query.QueryOperationsModule;
import fr.unicaen.iota.discovery.server.util.HibernateUtil;
import fr.unicaen.iota.nu.ONSOperation;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.classic.Session;

/**
 *
 */
public class TestConfiguration extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(TestConfiguration.class);
    private boolean multi_ds_architecture;
    private String[] ONS_ADDRESSES;
    private String DS_LOGIN;
    private String DS_PASSWORD;
    private static String[] DB = {
        "event",
        "eventtopublish",
        "partner",
        "sc",
        "scassociation",
        "user",
        "voc_BizStep",
        "voc_EPCClass",
        "event_EPCs",
        "sc_BizStep_Restriction",
        "sc_EPCClass_Restriction",
        "sc_EPCs_Restriction",
        "sc_EventTime_Restriction"
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
        if (request.getSession().getAttribute("root-account") == null) {
            request.setAttribute("message", request.getParameter("message"));
            getServletContext().getRequestDispatcher("/rootAccountLogPage.jsp").forward(request, response);
        }
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
            out.println("table{width: 100%;}");
            out.println(".table_label{width: 200px;}");
            out.println(".table_input{width: 100%;}");
            out.println(".ds_table_account{width: 100%;}");
            out.println(".ds_table_account td{border: 1px solid gray;}");
            out.println(".ds_table_account .table_title td{padding-left: 10px;color: red;background-color: #eff0f1;font-size: 10px;}");
            out.println(".ds_table_account .table_content td{padding-left: 10px;color: blue;background-color: #eff0f1;font-size: 11px;}");
            out.println(".ds_table_account .partner td{font-weight: bold;text-align: center;color: white;background-color: #a2a2a2;font-size: 11px;}");
            out.println(".ds_table_account .partner td span{color: red;font-weight: normal;}");
            out.println(".ds_table_account .partner .action,.ds_table_account .table_content .action,.ds_table_account .table_title .action{width: 40px;font-weight: normal;text-align: center;border: 0px solid gray;background-color: white;color: black; font-size: 10px;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Test the current DS link configuration</h1>");
            out.println("<form method=\"POST\" action=\"RootAccountAuth?action=logout\" >");
            out.println("<input type=\"submit\" value=\"logout\" />");
            out.println("</form>");
            try {
                loadProperties();
            } catch (Exception ex) {
                out.println("<span class=\"error\">Impossible de charger le fichier de configuration !</span>");
                out.println("</body>");
                out.println("</html>");
                LOG.error("Can't load configuration file", ex);
                return;
            }
            out.println("<h2>DS accounts [<a href=\"createAccount.jsp\" >create</a>]: </h2>");
            QueryOperationsModule queryOperationsModule = new QueryOperationsModule();
            out.println("<table class=\"ds_table_account\"><tr class=\"table_title\"><td>ID</td><td>User ID</td><td>Login</td><td>Password</td><td>Partner ID</td><td>CAN SEE</td><td>Type</td><td class=\"action\">&nbsp;</td></tr>");
            Collection<Partner> list = queryOperationsModule.partnerLookupAll();
            for (Partner partner : list) {
                for (Object u : partner.getUserSet()) {
                    User user = (User) u;
                    out.println("<tr class=\"table_content\"><td>" + user.getId() + "</td><td>" + user.getUserID() + "</td><td>" + user.getLogin() + "</td><td>" + user.getPasswd() + "</td><td>(" + partner.getId() + ") -> " + partner.getPartnerID() + "</td><td>");
                    out.println("</td><td>" + partner.getServiceType() + "</td><td class=\"action\"> <a href=\"\">D</a> <a href=\"\">C</a> <a href=\"\">E</a> </td></tr>");
                }
            }
            out.println("</table>");
            if (multi_ds_architecture) {
                out.println("<h2>ONS configuration : </h2>");
                for (String ons : ONS_ADDRESSES) {
                    out.println("<div><span class=\"name\">ONS address: </span><span class=\"param\">" + ons + "</span></div>");
                }
                out.println("<form method=\"POST\" action=\"test?action=onsaddress\" target=\"onsaddresseframe\"><input type=\"submit\" value=\"Test addresses\" /></form>");
                out.println("<iframe name=\"onsaddresseframe\" ></iframe>");

                out.println("<h2>DS remote account configuration: </h2>");
                out.println("<div><span class=\"name\">Login: </span><span class=\"param\">" + DS_LOGIN + "</span></div>");
                out.println("<div><span class=\"name\">Password: </span><span class=\"param\">" + DS_PASSWORD + "</span></div>");
                out.println("<form method=\"POST\" action=\"test?action=login\" target=\"loginframe\"><div><table><tr><td class=\"table_label\">DS address: </td><td><input  class=\"table_input\" type=\"text\" name=\"serviceaddress\" value=\"http://server/ds/services/ESDS_Service\"/></td></tr></table></div><div><input type=\"submit\" value=\"Test login\" /></div></form>");
                out.println("<iframe name=\"loginframe\" ></iframe>");

            } else {
                out.println("<h2>Multi-ds-mode configuration: </h2>");
                out.println("<div><span class=\"name\">Multi-ds-architecture: </span><span class=\"param\">" + multi_ds_architecture + "</span></div>");
            }
            out.println("<h2>Database configuration: </h2>");
            out.println("<form method=\"POST\" action=\"test?action=db\" target=\"dbframe\"><input type=\"submit\" value=\"Test Database\" /></form>");
            out.println("<iframe name=\"dbframe\" ></iframe>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
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
            try {
                loadProperties();
            } catch (Exception ex) {
                return;
            }
            if ("onsaddress".equals(request.getParameter("action"))) {
                out.println("start test ONS connection !");
                out.println("<br/>");
                QueryOperationsModule queryOperationsModule = new QueryOperationsModule();
                boolean succed = false;
                for (String ons : ONS_ADDRESSES) {
                    ONSOperation onsop = new ONSOperation(ons);
                    try {
                        out.println("<span class=\"name\">executing ping ONS (" + ons + ") ...</span> ");
                        out.flush();
                        onsop.pingONS();
                        out.println("<span class=\"param\">[ DONE ]</span>");
                        out.flush();
                        out.println("<br/>");
                        succed = succed || true;
                    } catch (Exception e) {
                        LOG.error("Can't reach the ONS (" + ons + ")", e);
                        out.println("<span class=\"error\">[ FAILED ]</span>");
                        out.flush();
                        out.println("<br/>");
                    }
                }
                if (!succed) {
                    out.println("<span class=\"error\">finished: [ ERROR ]</span>");
                    out.println("<br/>");
                    out.flush();
                } else {
                    out.println("<span class=\"name\">finished:</span><span class=\"param\"> [ SUCCEDED ]</span>");
                    out.println("<br/>");
                    out.flush();
                }
            } else if ("login".equals(request.getParameter("action"))) {
                try {
                    out.println("start test login/logout !");
                    out.flush();
                    out.println("<br/>");
                    String addr = (String) request.getParameter("serviceaddress");
                    out.println("<span class=\"name\">executing Hello operation ...</span> ");
                    out.flush();
                    DsClient dsClient = new DsClient(addr);
                    dsClient.hello(Configuration.DEFAULT_SESSION);
                    out.println("<span class=\"param\">[ DONE ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"name\">executing login operation ... </span>");
                    out.flush();
                    fr.unicaen.iota.discovery.client.model.Session session = dsClient.userLogin(Configuration.DEFAULT_SESSION, DS_LOGIN, DS_PASSWORD);
                    out.println("<span class=\"param\">[ DONE ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"name\">executing logout operation ... </span>");
                    out.flush();
                    dsClient.userLogout(session.getSessionId());
                    out.println("<span class=\"param\">[ DONE ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"name\">finished:</span><span class=\"param\"> [ SUCCEDED ]</span>");
                    out.flush();
                    out.println("<br/>");
                } catch (EnhancedProtocolException ex) {
                    LOG.error("Can't connect to referent DS: ", ex);
                    out.println("<span class=\"error\">[ FAILED ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"error\">finished: [ ERROR ]</span>");
                    out.flush();
                }

            } else if ("db".equals(request.getParameter("action"))) {
                out.println("start test DB connection !");
                out.println("<br/>");
                Session session = null;
                try {
                    session = HibernateUtil.getSessionFactory().openSession();
                    session.beginTransaction();
                    for (String table : DB) {
                        out.println("<span class=\"name\">list " + table + " ...</span>");
                        out.flush();
                        String req = "select * from " + table + " limit 1";
                        Query query = session.createSQLQuery(req);
                        //query.setMaxResults(1);
                        query.list();
                        out.println("<span class=\"param\">[ DONE ]</span>");
                        out.flush();
                        out.println("<br/>");
                    }
                    session.getTransaction().commit();
                    session.close();
                    out.println("<span class=\"name\">finished:</span> <span class=\"param\">[ SUCCEDED ]</span>");
                    out.flush();
                    out.println("<br/>");
                } catch (Exception e) {
                    session.close();
                    LOG.error("Failed to complite database test", e);
                    out.println("<span class=\"error\">[ FAILED ]</span>");
                    out.flush();
                    out.println("<br/>");
                    out.println("<span class=\"error\">finished: [ ERROR ]</span>");
                    out.flush();

                }
            }
            out.println("</body>");
            out.println("</html>");
            out.flush();
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
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processAction(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void loadProperties() throws Exception {
        LOG.trace("loading test properties");
        Properties properties = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("publisher.properties");
        properties.load(is);
        String mda = properties.getProperty("multi-ds-architecture");
        if (mda == null) {
            LOG.error("[multi-ds-architecture] has no value");
            throw new Exception();
        } else if ("false".equals(mda)) {
            multi_ds_architecture = false;
            return;
        } else if (!"true".equals(mda)) {
            LOG.error("[multi-ds-architecture] has to be \"true\" or \"false\"!");
            throw new Exception();
        } // now, "true".equals(mda)
        multi_ds_architecture = true;

        String ONSHosts = properties.getProperty("ons-hosts");
        LOG.trace("ONSHosts: " + ONSHosts);
        if (ONSHosts == null) {
            LOG.error("[ons-hosts] has no value");
            throw new Exception();
        }
        ONS_ADDRESSES = ONSHosts.split(",");
        DS_LOGIN = properties.getProperty("ds-login");
        if (DS_LOGIN == null) {
            LOG.error("[ds-login] has no value");
            throw new Exception();
        }
        DS_PASSWORD = properties.getProperty("ds-password");
        if (DS_PASSWORD == null) {
            LOG.error("[ds-password] has no value");
            throw new Exception();
        }
    }
}
