/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.epcisphi.xacml.servlet;

import fr.unicaen.iota.epcisphi.utils.InterfaceHelper;
import fr.unicaen.iota.epcisphi.utils.MapSessions;
import fr.unicaen.iota.epcisphi.utils.User;
import fr.unicaen.iota.epcisphi.xacml.ihm.*;
import fr.unicaen.iota.epcisphi.xacml.ihm.factory.AccessPolicies;
import fr.unicaen.iota.epcisphi.xacml.ihm.factory.Node;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import fr.unicaen.iota.ypsilon.client.model.UserInfoOut;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
                    throw new ServiceException("Session expired you have to reconnect !", ServiceErrorType.epcis);
                }
                UserInfoOut userInfo = (UserInfoOut) request.getSession().getAttribute("uInfo");
                if (userInfo == null) {
                    throw new ServiceException("User not well connected !", ServiceErrorType.epcis);
                }
                /*
                 * TODO Partner? PartnerInfo partnerInfo = (PartnerInfo)
                 * request.getSession().getAttribute("pInfo"); if (userInfo ==
                 * null || partnerInfo == null) { throw new
                 * ServiceException("User not well connected !",
                 * ServiceErrorType.epcis); } Service service =
                 * partnerInfo.getServiceList().get(0); Partner partner = new
                 * Partner(partnerInfo.getUid(), true,
                 * partnerInfo.getPartnerId(), new Date(), service.getId(),
                 * service.getType(), service.getUri().toString()); User user =
                 * new User(userInfo.getUid(), partner, "",
                 * userInfo.getUserId(), userInfo.getUserId(), new Date());
                 */
                User user = new User(userInfo.getUserID(), userInfo.getOwnerID());
                Module module = (request.getParameter("d") != null)? Module.valueOf(request.getParameter("d")) : null;
                String objectId = request.getParameter("b");
                String groupId = request.getParameter("e");
                String methodName = request.getParameter("a");
                String sessionId = (String) request.getSession().getAttribute("session-id");
                // **************************  CREATE *********************************

                if ("createPartnerGroup".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    String resp = services.createPartnerGroup(sessionId, user, module, newValue);
                    TreeNode node = createEmptyPolicies(user, newValue, module, resp);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addPartnerToGroup".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addPartnerToGroup(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.userNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addBizStepRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addBizStepRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.bizStepFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addEpcRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addEpcRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.epcFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addEventTimeRestriction".equals(methodName)) {
                    String d1 = request.getParameter("d1");
                    String d2 = request.getParameter("d2");
                    services.addEventTimeRestriction(sessionId, user, module, objectId, groupId, d1, d2);
                    TreeNode node = new Node(d1 + " -> " + d2, NodeType.eventTimeFilterNode, d1 + " -> " + d2, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addRecordTimeRestriction".equals(methodName)) {
                    String d1 = request.getParameter("d1");
                    String d2 = request.getParameter("d2");
                    services.addRecordTimeRestriction(sessionId, user, module, objectId, groupId, d1, d2);
                    TreeNode node = new Node(d1 + " -> " + d2, NodeType.recordTimeFilterNode, d1 + " -> " + d2, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addOperationRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addOperationRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.operationFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addEventTypeRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addEventTypeRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.eventTypeFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addParentIdRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addParentIdRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.parentIdFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addChildEpcRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addChildEpcRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.childEpcFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addQuantityRestriction".equals(methodName)) {
                    String d1 = request.getParameter("d1");
                    String d2 = request.getParameter("d2");
                    services.addQuantityRestriction(sessionId, user, module, objectId, groupId, d1, d2);
                    TreeNode node = new Node(d1 + " -> " + d2, NodeType.quantityFilterNode, d1 + " -> " + d2, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addReadPointRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addReadPointRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.readPointFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addBizLocRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addBizLocRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.bizLocFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addDispositionRestriction".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addDispositionRestriction(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.dispositionFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } else if ("addUserPermission".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.addUserPermission(sessionId, user, module, objectId, groupId, newValue);
                    TreeNode node = new Node(newValue, NodeType.methodFilterNode, newValue, module, groupId);
                    html.append(new TreeFactory(Mode.Assert_Mode).createTree(node));
                } // **************************  SWITCH *********************************
                else if ("switchBizStepPolicy".equals(methodName)) {
                    html.append(services.switchBizStepPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchEpcPolicy".equals(request.getParameter("a"))) {
                    html.append(services.switchEpcPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchEventTimePolicy".equals(methodName)) {
                    html.append(services.switchEventTimePolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchRecordTimePolicy".equals(methodName)) {
                    html.append(services.switchRecordTimePolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchOperationPolicy".equals(methodName)) {
                    html.append(services.switchOperationPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchEventTypePolicy".equals(methodName)) {
                    html.append(services.switchEventTypePolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchParentIdPolicy".equals(methodName)) {
                    html.append(services.switchParentIdPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchChildEpcPolicy".equals(methodName)) {
                    html.append(services.switchChildEpcPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchQuantityPolicy".equals(methodName)) {
                    html.append(services.switchQuantityPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchReadPointPolicy".equals(methodName)) {
                    html.append(services.switchReadPointPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchBizLocPolicy".equals(methodName)) {
                    html.append(services.switchBizLocPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchDispositionPolicy".equals(methodName)) {
                    html.append(services.switchDispositionPolicy(sessionId, user, module, objectId, groupId));
                } else if ("switchPermissionPolicy".equals(methodName)) {
                    html.append(services.switchUserPermissionPolicy(sessionId, user, module, objectId, groupId));
                } // **************************  REMOVE *********************************
                else if ("removeBizStepRestriction".equals(methodName)) {
                    services.removeBizStepRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeEpcRestriction".equals(methodName)) {
                    services.removeEpcRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeEventTimeRestriction".equals(methodName)) {
                    services.removeEventTimeRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeRecordTimeRestriction".equals(methodName)) {
                    services.removeRecordTimeRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeOperationRestriction".equals(methodName)) {
                    services.removeOperationRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeEventTypeRestriction".equals(methodName)) {
                    services.removeEventTypeRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeParentIdRestriction".equals(methodName)) {
                    services.removeParentIdRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeChildEpcRestriction".equals(methodName)) {
                    services.removeChildEpcRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeQuantityRestriction".equals(methodName)) {
                    services.removeQuantityRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeReadPointRestriction".equals(methodName)) {
                    services.removeReadPointRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeBizLocRestriction".equals(methodName)) {
                    services.removeBizLocRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeDispositionRestriction".equals(methodName)) {
                    services.removeDispositionRestriction(sessionId, user, module, objectId, groupId);
                } else if ("removeUserPermission".equals(methodName)) {
                    services.removeUserPermission(sessionId, user, module, objectId, groupId);
                } else if ("deletePartnerGroup".equals(methodName)) {
                    services.deletePartnerGroup(sessionId, user, module, objectId, groupId);
                } else if ("removePartnerFromGroup".equals(methodName)) {
                    services.removePartnerFromGroup(sessionId, user, module, objectId, groupId);
                } // *************************  EPCIS ADMIN ********************************
                else if ("updatePartner".equals(methodName)) {
                    String partnerID = request.getParameter("f");
                    String serviceID = request.getParameter("g");
                    String serviceAddress = request.getParameter("h");
                    String serviceType = request.getParameter("i");
                    //TODO services.updatePartner(sessionId, user,user.getPartnerID(), partnerID, serviceID, serviceAddress, serviceType);
                } else if ("createUser".equals(methodName)) {
                    String login = request.getParameter("f");
                    String userName = request.getParameter("g");
                    services.createUser(sessionId, user, login, userName);
                } else if ("updateUser".equals(methodName)) {
                    String login = request.getParameter("f");
                    //TODO services.updateUser(sessionId, user, login, pass);
                } else if ("deleteUser".equals(methodName)) {
                    String login = request.getParameter("f");
                    services.deleteUser(sessionId, user, login);
                } else if ("createAccount".equals(methodName)) {
                    String userDN = request.getParameter("f");
                    String partnerId = request.getParameter("g");
                    String userName = request.getParameter("h");
                    boolean rtr = services.createAccount(sessionId, user, partnerId, userDN, userName);
                    if (rtr) {
                        html.append("Account successfull created.");
                    }
                } // **************************  UPDATE *********************************
                else if ("updateGroupName".equals(methodName)) {
                    String newValue = request.getParameter("c");
                    services.updateGroupName(sessionId, user, module, objectId, groupId, newValue);

                } // **************************  SAVE  **********************************
                else if ("saveOwnerPolicy".equals(methodName)) {
                    services.savePolicyPartner(sessionId, user, module);

                } // **************************  CANCEL  **********************************
                else if ("cancelPartnerPolicy".equals(methodName)) {
                    services.cancelPartnerPolicy(user, module);

                } // **************************  LOAD POLICIES  *************************
                else if ("loadPolicyTree".equals(methodName)) {
                    services.loadPolicyTree(user, module);
                    InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, user.getPartnerID());
                    interfaceHelper.reload();
                    AccessPolicies policies = new AccessPolicies(sessionId, user.getPartnerID(), module);
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
                    throw new ServiceException("service method " + methodName + " not found !", ServiceErrorType.Unknown);
                }
                out.print(createXMLEnvelop(createXMLRespondeHeader(Response.RESPONSE_OK, "") + createXMLHTMLTag(html.toString())));

            } catch (ServiceException se) {
                out.print(createXMLEnvelop(createXMLRespondeHeader(Response.RESPONSE_ERROR, se.getMessage()) + createXMLHTMLTag(html.toString())));
                return;
            } catch (Exception e) {
                out.print(createXMLEnvelop(createXMLRespondeHeader(Response.RESPONSE_ERROR, "INTERNAL ERROR: " + e.getMessage()) + createXMLHTMLTag(html.toString())));
                return;
            }
        } finally {
            out.close();
        }
    }

    public String createXMLEnvelop(String content) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<response>\n" + content + "</response>";
    }

    public String createXMLRespondeHeader(Response resp, String message) {
        return "<result>\n<id>" + resp.getCode() + "</id>\n<desc>" + message + "</desc>\n</result>\n";
    }

    private String createXMLHTMLTag(String html) {
        return "<htmlcontent>\n<![CDATA[" + html + "]]>\n</htmlcontent>";
    }

    public TreeNode createEmptyPolicies(User user, String name, Module module, String groupId) {
        GroupPolicy gpq = new GroupPolicy(name, user.getPartnerID());
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
