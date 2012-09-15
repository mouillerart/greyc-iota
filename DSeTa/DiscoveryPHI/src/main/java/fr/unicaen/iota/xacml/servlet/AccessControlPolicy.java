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
package fr.unicaen.iota.xacml.servlet;

import fr.unicaen.iota.auth.Partner;
import fr.unicaen.iota.auth.User;
import fr.unicaen.iota.discovery.client.model.PartnerInfo;
import fr.unicaen.iota.discovery.client.model.Service;
import fr.unicaen.iota.discovery.client.model.UserInfo;
import fr.unicaen.iota.utils.InterfaceHelper;
import fr.unicaen.iota.utils.MapSessions;
import fr.unicaen.iota.xacml.ihm.*;
import fr.unicaen.iota.xacml.ihm.factory.AccessPolicies;
import fr.unicaen.iota.xacml.ihm.factory.Node;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class AccessControlPolicy extends HttpServlet {

    private Services services = new Services();

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
                if (request.getSession().getAttribute("session-id") == null) {
                    throw new ServiceException("Session expired you have to reconnect!", ServiceErrorType.ds);
                }
                UserInfo userInfo = (UserInfo) request.getSession().getAttribute("uInfo");
                PartnerInfo partnerInfo = (PartnerInfo) request.getSession().getAttribute("pInfo");
                if (userInfo == null || partnerInfo == null) {
                    throw new ServiceException("User not well connected!", ServiceErrorType.ds);
                }
                Service service = partnerInfo.getServiceList().get(0);
                Partner partner = new Partner(partnerInfo.getUid(), true, partnerInfo.getPartnerId(),
                        new Date(), service.getId(), service.getType(), service.getUri().toString());
                User user = new User(userInfo.getUid(), partner, "", userInfo.getUserId(), userInfo.getUserId(), new Date());
                Module module = (request.getParameter("d") != null)? Module.valueOf(request.getParameter("d")) : null;
                String objectId = request.getParameter("b");
                String groupId = request.getParameter("e");
                String sessionId = (String) request.getSession().getAttribute("session-id");
                synchronized (services) {
                    String a = request.getParameter("a");
                    // **************************  CREATE *********************************
                    if ("createPartnerGroup".equals(a)) {
                        String newValue = request.getParameter("c");
                        String resp = services.createPartnerGroup(sessionId, user, module, newValue);
                        TreeNode node = createEmptyPolicies(user, newValue, module, resp);
                        html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                    } else if ("addPartnerToGroup".equals(a)) {
                        String newValue = request.getParameter("c");
                        services.addPartnerToGroup(sessionId, user, module, objectId, groupId, newValue);
                        TreeNode node = new Node(newValue, NodeType.userNode, newValue, module, groupId);
                        html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                    } else if ("addBizStepRestriction".equals(a)) {
                        String newValue = request.getParameter("c");
                        services.addBizStepRestriction(sessionId, user, module, objectId, groupId, newValue);
                        TreeNode node = new Node(newValue, NodeType.bizStepFilterNode, newValue, module, groupId);
                        html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                    } else if ("addEPCRestriction".equals(a)) {
                        String newValue = request.getParameter("c");
                        services.addEPCRestriction(sessionId, user, module, objectId, groupId, newValue);
                        TreeNode node = new Node(newValue, NodeType.epcFilterNode, newValue, module, groupId);
                        html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                    } else if ("addEPCClassRestriction".equals(a)) {
                        String newValue = request.getParameter("c");
                        services.addEPCClassRestriction(sessionId, user, module, objectId, groupId, newValue);
                        TreeNode node = new Node(newValue, NodeType.epcClassFilterNode, newValue, module, groupId);
                        html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                    } else if ("addTimeRestriction".equals(a)) {
                        String d1 = request.getParameter("d1");
                        String d2 = request.getParameter("d2");
                        services.addTimeRestriction(sessionId, user, module, objectId, groupId, d1, d2);
                        TreeNode node = new Node(d1 + " -> " + d2, NodeType.eventTimeFilterNode, d1 + " -> " + d2, module, groupId);
                        html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                    } else if ("addUserPermission".equals(a)) {
                        String newValue = request.getParameter("c");
                        services.addUserPermission(sessionId, user, module, objectId, groupId, newValue);
                        TreeNode node = new Node(newValue, NodeType.methodFilterNode, newValue, module, groupId);
                        html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                    } // **************************  SWITCH *********************************
                    else if ("switchBizStepPolicy".equals(a)) {
                        html.append(services.switchBizStepPolicy(sessionId, user, module, objectId, groupId));
                    } else if ("switchEPCPolicy".equals(a)) {
                        html.append(services.switchEPCPolicy(sessionId, user, module, objectId, groupId));
                    } else if ("switchEPCClassPolicy".equals(a)) {
                        html.append(services.switchEPCClassPolicy(sessionId, user, module, objectId, groupId));
                    } else if ("switchTimePolicy".equals(a)) {
                        html.append(services.switchTimePolicy(sessionId, user, module, objectId, groupId));
                    } else if ("switchPermissionPolicy".equals(a)) {
                        html.append(services.switchUserPermissionPolicy(sessionId, user, module, objectId, groupId));
                    } // **************************  REMOVE *********************************
                    else if ("removeBizStepRestriction".equals(a)) {
                        services.removeBizStepRestriction(sessionId, user, module, objectId, groupId);
                    } else if ("removeEPCRestriction".equals(a)) {
                        services.removeEPCRestriction(sessionId, user, module, objectId, groupId);
                    } else if ("removeEPCClassRestriction".equals(a)) {
                        services.removeEPCClassRestriction(sessionId, user, module, objectId, groupId);
                    } else if ("removeTimeRestriction".equals(a)) {
                        services.removeTimeRestriction(sessionId, user, module, objectId, groupId);
                    } else if ("removeUserPermission".equals(a)) {
                        services.removeUserPermission(sessionId, user, module, objectId, groupId);
                    } else if ("deletePartnerGroup".equals(a)) {
                        services.deletePartnerGroup(sessionId, user, module, objectId, groupId);
                    } else if ("removePartnerFromGroup".equals(a)) {
                        services.removePartnerFromGroup(sessionId, user, module, objectId, groupId);
                    } // *************************  DS ADMIN ********************************
                    else if ("updatePartner".equals(a)) {
                        String partnerID = request.getParameter("f");
                        String serviceID = request.getParameter("g");
                        String serviceAddress = request.getParameter("h");
                        String serviceType = request.getParameter("i");
                        services.updatePartner(sessionId, user, partnerInfo.getUid(), partnerID, serviceID, serviceAddress, serviceType);
                    } else if ("createUser".equals(a)) {
                        String login = request.getParameter("f");
                        String pass = request.getParameter("g");
                        services.createUser(sessionId, user, login, pass);
                    } else if ("updateUser".equals(a)) {
                        String login = request.getParameter("f");
                        String pass = request.getParameter("g");
                        services.updateUser(sessionId, user, login, pass);
                    } else if ("createAccount".equals(a)) {
                        String partnerId = request.getParameter("f");
                        String serviceId = request.getParameter("g");
                        String serviceType = request.getParameter("h");
                        String serviceAddress = request.getParameter("i");
                        String login = request.getParameter("j");
                        String pass = request.getParameter("k");
                        boolean rtr = services.createAccount(sessionId, user, partnerId, serviceId, serviceType, serviceAddress, login, pass);
                        if (rtr) {
                            html.append("Account successfull created.");
                        }
                    } // **************************  UPDATE *********************************
                    else if ("updateGroupName".equals(a)) {
                        String newValue = request.getParameter("c");
                        services.updateGroupName(sessionId, user, module, objectId, groupId, newValue);

                    } // **************************  SAVE  **********************************
                    else if ("savePartnerPolicy".equals(a)) {
                        services.savePolicyPartner(sessionId, user, module);

                    } // **************************  CANCEL  **********************************
                    else if ("cancelPartnerPolicy".equals(a)) {
                        services.cancelPartnerPolicy(user, module);

                    } // **************************  LOAD POLICIES  *************************
                    else if ("loadPolicyTree".equals(a)) {
                        services.loadPolicyTree(user, module);
                        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, user.getPartner().getPartnerID());
                        interfaceHelper.reload();
                        AccessPolicies policies = new AccessPolicies(sessionId, user.getPartner().getPartnerID(), module);
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
                    } else {
                        throw new ServiceException("service method " + a + " not found!", ServiceErrorType.unknown);
                    }
                    out.print(createXMLEnvelop(createXMLRespondeHeader(Response.Value.OK, "") + createXMLHTMLTag(html.toString())));
                }
            } catch (ServiceException se) {
                out.print(createXMLEnvelop(createXMLRespondeHeader(Response.Value.ERROR, se.getMessage()) + createXMLHTMLTag(html.toString())));
                return;
            } catch (Exception e) {
                out.print(createXMLEnvelop(createXMLRespondeHeader(Response.Value.ERROR, "INTERNAL ERROR: "
                        + e.getMessage()) + createXMLHTMLTag(html.toString())));
                return;
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
        GroupPolicy gpq = new GroupPolicy(name, user.getPartner().getPartnerID());
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
