<%@page import="fr.unicaen.iota.epcisphi.utils.SessionLoader"%>
<%@page import="com.sun.xacml.ctx.Result"%>
<%@page import="fr.unicaen.iota.epcisphi.utils.PEPRequester"%>
<%@page import="fr.unicaen.iota.eta.user.userservice.UserInfoOut"%>
<%

            String sessionId = (String) session.getAttribute("session-id");
            String sid = (String) request.getParameter("sid");
            String uid = (String) request.getParameter("uid");
            if (sid != null) {
                String message = SessionLoader.loadSession(sid, uid, session);
                if (!message.equals("")) {
                    request.setAttribute("message", message);
%>
<jsp:include page="Login.jsp" />
<%
                    return;
                }
            } else if (sessionId == null) {
%>
<jsp:include page="Login.jsp" />
<%
                return;
            }
%>

<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.Module"%>
<%@page import="fr.unicaen.iota.epcisphi.utils.HTMLUtilities"%>
<%@page import="java.util.Date"%>
<%@page import="fr.unicaen.iota.epcisphi.utils.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Policy Manager</title>
        <script type="text/javascript" src="script/tree.js"></script>
        <script type="text/javascript" src="script/requestDispatcher.js"></script>
        <script type="text/javascript" src="script/jquery.js"></script>
        <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
        <link rel="stylesheet" type="text/css" href="style/style.css" />
    </head>
    <body>
        <div class="treeTitle">&nbsp;</div>

        <jsp:include page="dialogs.jsp" />

        <%
                    //PartnerInfo pInfo = (PartnerInfo) session.getAttribute("pInfo");
                    UserInfoOut uInfo = (UserInfoOut) session.getAttribute("uInfo");
                    //Partner partner = new Partner(0, true, pInfo.getPartnerId(), new Date(), null, null, null);
                    User u = new User(uInfo.getUserID(), uInfo.getPartnerID());
                    if (PEPRequester.checkAccess(u, "superadmin") != Result.DECISION_PERMIT) {

        %>

        <jsp:include page="policyList.jsp" />

        <%                   } else {

        %>

        <jsp:include page="rootMenu.jsp" />

        <%
                        return;
                    }

                    String userId = ((UserInfoOut) session.getAttribute("uInfo")).getUserID();
            //String partnerId = ((PartnerInfo) session.getAttribute("pInfo")).getPartnerId();
                    String partnerId = ((UserInfoOut) session.getAttribute("uInfo")).getPartnerID();

        %>
        <div class="account">
            <div class="logout"><a href="RootAccountAuth?action=logout">[ logout ]</a></div>
            <div class="logout"><a href="#" onclick="processPartnerUpdate()" >[ Update My Account ]</a></div>
            <div class="logout"><a href="#" onclick="processUserCreate()" >[ Create User ]</a></div>
            <div class="logout"><a href="#" onclick="processUserDelete()" >[ Delete User ]</a></div>
            <div style="width : 300px;"><span>*</span> User : <span class="accountDetails"><%=userId%></span> / Partner profile : <span class="accountDetails"><%=partnerId%></span></div>
        </div>

    </body>
</html>
