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
package fr.unicaen.iota.dphi.xacml.servlet;

import fr.unicaen.iota.dphi.xacml.ihm.NodeType;
import fr.unicaen.iota.dphi.xacml.ihm.TreeNode;
import fr.unicaen.iota.dphi.xacml.ihm.Mode;
import fr.unicaen.iota.dphi.xacml.ihm.TreeFactory;
import fr.unicaen.iota.dphi.xacml.ihm.Module;
import fr.unicaen.iota.dphi.auth.User;
import fr.unicaen.iota.mu.Utils;
import fr.unicaen.iota.dphi.utils.InterfaceHelper;
import fr.unicaen.iota.dphi.utils.MapSessions;
import fr.unicaen.iota.dphi.xacml.ihm.factory.AccessPolicies;
import fr.unicaen.iota.dphi.xacml.ihm.factory.Node;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class AccessControlPolicy extends HttpServlet {

    private Services services = new Services();

    private static final Log log = LogFactory.getLog(AccessControlPolicy.class);

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
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        try {
            // **************************  CREATE *********************************
            try {
                if (request.getSession().getAttribute("user") == null || request.getSession().getAttribute("cert") == null) {
                    throw new ServiceException("Session expired you have to reconnect !", ServiceErrorType.ds);
                }
                String dn = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : null;
                if (request.getSession().getAttribute("cert") != null) {
                    String cert = (String) request.getSession().getAttribute("cert");
                    if (!cert.equals(Utils.formatId(dn))) {
                        throw new ServiceException("Don't change your certificate!", ServiceErrorType.unknown);
                    }
                }
                User user = (User) request.getSession().getAttribute("user");
                Module module = (request.getParameter("d") != null) ? Module.valueOf(request.getParameter("d")) : null;
                String objectId = request.getParameter("b");
                String groupId = request.getParameter("e");
                String userId = user.getUserID();
                String a = request.getParameter("a");
                // **************************  CREATE *********************************
                if ("createOwnerGroup".equals(a)) {
                    String newValue = request.getParameter("c");
                    String resp = services.createOwnerGroup(userId, user, module, newValue);
                    TreeNode node = createEmptyPolicies(user, newValue, module, resp);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                }
                else if ("addOwnerToGroup".equals(a)) {
                    String newValue = request.getParameter("c");
                    services.addOwnerToGroup(userId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.userNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                }
                else if ("addBizStepRestriction".equals(a)) {
                    String newValue = request.getParameter("c");
                    services.addBizStepRestriction(userId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.bizStepFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                }
                else if ("addEPCRestriction".equals(a)) {
                    String newValue = request.getParameter("c");
                    services.addEPCRestriction(userId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.epcFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                }
                else if ("addEventTypeRestriction".equals(a)) {
                    String newValue = request.getParameter("c");
                    services.addEventTypeRestriction(userId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.eventTypeFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                }
                else if ("addTimeRestriction".equals(a)) {
                    String d1 = request.getParameter("d1");
                    String d2 = request.getParameter("d2");
                    services.addTimeRestriction(userId, user, module, objectId, groupId, d1, d2);
                    TreeNode node = new Node(d1 + " -> " + d2, NodeType.eventTimeFilterNode, d1 + " -> " + d2, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                }
                else if ("addUserPermission".equals(a)) {
                    String newValue = request.getParameter("c");
                    services.addUserPermission(userId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.methodFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } // **************************  SWITCH *********************************
                else if ("switchBizStepPolicy".equals(a)) {
                    html.append(services.switchBizStepPolicy(userId, user, module, objectId, groupId));
                }
                else if ("switchEPCPolicy".equals(a)) {
                    html.append(services.switchEPCPolicy(userId, user, module, objectId, groupId));
                }
                else if ("switchEventTypePolicy".equals(a)) {
                    html.append(services.switchEventTypePolicy(userId, user, module, objectId, groupId));
                }
                else if ("switchTimePolicy".equals(a)) {
                    html.append(services.switchTimePolicy(userId, user, module, objectId, groupId));
                }
                else if ("switchPermissionPolicy".equals(a)) {
                    html.append(services.switchUserPermissionPolicy(userId, user, module, objectId, groupId));
                } // **************************  REMOVE *********************************
                else if ("removeBizStepRestriction".equals(a)) {
                    services.removeBizStepRestriction(userId, user, module, objectId, groupId);
                }
                else if ("removeEPCRestriction".equals(a)) {
                    services.removeEPCRestriction(userId, user, module, objectId, groupId);
                }
                else if ("removeEventTypeRestriction".equals(a)) {
                    services.removeEventTypeRestriction(userId, user, module, objectId, groupId);
                }
                else if ("removeTimeRestriction".equals(a)) {
                    services.removeTimeRestriction(userId, user, module, objectId, groupId);
                }
                else if ("removeUserPermission".equals(a)) {
                    services.removeUserPermission(userId, user, module, objectId, groupId);
                }
                else if ("deleteOwnerGroup".equals(a)) {
                    services.deleteOwnerGroup(userId, user, module, objectId, groupId);
                }
                else if ("removeOwnerFromGroup".equals(a)) {
                    services.removeOwnerFromGroup(userId, user, module, objectId, groupId);
                } // *************************  DS ADMIN ********************************
                else if ("createUser".equals(a)) {
                    String userDN = request.getParameter("f");
                    String userName = request.getParameter("g");
                    services.createUser(user, userDN, userName);
                }
                else if ("updateUser".equals(a)) {
                    String login = request.getParameter("f");
                    String pass = request.getParameter("g");
                    //services.updateUser(sessionId, user, login, pass);
                }
                else if ("deleteUser".equals(a)) {
                    String login = request.getParameter("f");
                    services.deleteUser(user, login);
                }
                else if ("createAccount".equals(a)) {
                    String userDN = request.getParameter("f");
                    String owner = request.getParameter("g");
                    String userName = request.getParameter("h");
                    boolean rtr = services.createAccount(user, owner, userDN, userName);
                    if (rtr) {
                        html.append("Account successfull created.");
                    }
                } // **************************  UPDATE *********************************
                else if ("updateGroupName".equals(a)) {
                    String newValue = request.getParameter("c");
                    services.updateGroupName(userId, user, module, objectId, groupId, newValue);

                } // **************************  SAVE  **********************************
                else if ("savePolicyOwner".equals(a)) {
                    services.savePolicyOwner(userId, user, module);

                } // **************************  CANCEL  **********************************
                else if ("cancelOwnerPolicy".equals(a)) {
                    services.cancelOwnerPolicy(user, module);

                } // **************************  LOAD POLICIES  *************************
                else if ("loadPolicyTree".equals(a)) {
                    services.loadPolicyTree(user, module);
                    InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, user.getOwnerID());
                    interfaceHelper.reload();
                    AccessPolicies policies = new AccessPolicies(userId, user.getOwnerID(), module);
                    switch (module) {
                        case adminModule:
                            html.append(new TreeFactory(Mode.Create_Mode).createTree(policies.getPoliciesAdmin().get(0)));
                            break;
                        case queryModule:
                            html.append(new TreeFactory(Mode.Create_Mode).createTree(policies.getPoliciesQuery().get(0)));
                            break;
                        case captureModule:
                            html.append(new TreeFactory(Mode.Create_Mode).createTree(policies.getPoliciesCapture().get(0)));
                            break;
                    }
                }
                else {
                    throw new ServiceException("service method " + a + " not found!", ServiceErrorType.unknown);
                }
                out.print(createXMLEnvelop(createXMLRespondeHeader(Response.Value.OK, "") + createXMLHTMLTag(html.toString())));
            } catch (ServiceException se) {
                log.info("", se);
                out.print(createXMLEnvelop(createXMLRespondeHeader(Response.Value.ERROR, se.getMessage())
                        + createXMLHTMLTag(html.toString())));
            } catch (Exception e) {
                log.info("", e);
                out.print(createXMLEnvelop(createXMLRespondeHeader(Response.Value.ERROR, "INTERNAL ERROR: "
                        + e.getMessage()) + createXMLHTMLTag(html.toString())));
            }
        } finally {
            out.close();
        }
    }

    public String createXMLEnvelop(String content) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<response>\n" + content + "</response>";
    }

    public String createXMLRespondeHeader(Response.Value code, String message) {
        return "<result>\n<id>" + code.toString() + "</id>\n<desc>" + message + "</desc>\n</result>\n";
    }

    private String createXMLHTMLTag(String html) {
        return "<htmlcontent>\n<![CDATA[" + html + "]]>\n</htmlcontent>";
    }

    public TreeNode createEmptyPolicies(User user, String name, Module module, String groupId) {
        GroupPolicy gpq = new GroupPolicy(name, user.getOwnerID());
        AccessPolicies accessPolicies = new AccessPolicies();
        return accessPolicies.createGroupPolicy(gpq, module);
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
