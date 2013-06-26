<%@page import="fr.unicaen.iota.ypsilon.client.model.UserInfoOut"%>
<%@page import="fr.unicaen.iota.utils.Constants"%>
<%@page import="fr.unicaen.iota.ypsilon.client.YPSilonClient"%>
<%@page import="fr.unicaen.iota.utils.SessionLoader"%>
<%@page import="com.sun.xacml.ctx.Result"%>
<%@page import="fr.unicaen.iota.utils.PEPRequester"%>
<%
    String sessionId = (String) session.getAttribute("session-id");
    String sid = (String) request.getParameter("sid");
    String uid = (String) request.getParameter("uid");
    if (sid != null) {
        YPSilonClient ypsilonClient = new YPSilonClient(Constants.YPSILON_ADDRESS, Constants.PKS_FILENAME,
                            Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
        String message = SessionLoader.loadSession(sid, ypsilonClient, uid, session);
        if (!message.isEmpty()) {
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
<!-- SLS: c’est-il pas un peu crade ? -->
<%@page import="fr.unicaen.iota.xacml.ihm.Module"%>
<%@page import="fr.unicaen.iota.utils.HTMLUtilities"%>
<%@page import="java.util.Date"%>
<%@page import="fr.unicaen.iota.auth.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Policy Manager</title>
        <script type="text/javascript" src="script/tree.js"></script>
        <script type="text/javascript" src="script/requestDispatcher.js"></script>
        <script type="text/javascript" src="script/jquery-min.js"></script>
        <script type="text/javascript" src="script/jquery-ui.min.js"></script>
        <link rel="stylesheet" type="text/css" href="style/jquery-ui.css" />
        <link rel="stylesheet" type="text/css" href="style/style.css" />
    </head>
    <body>
        <div class="treeTitle">&nbsp;</div>
        <jsp:include page="dialogs.jsp" />
        <%
            UserInfoOut uInfo = (UserInfoOut) session.getAttribute("uInfo");
            User u = new User(uInfo.getUserID(), uInfo.getOwnerID());
            if (PEPRequester.checkAccess(u, "superadmin") != Result.DECISION_PERMIT) {
        %>
        <jsp:include page="policyList.jsp" />
        <%
            } else {
        %>
        <jsp:include page="rootMenu.jsp" />
        <%
                return;
            }
            String userId = ((UserInfoOut) session.getAttribute("uInfo")).getUserID();
            String ownerId = ((UserInfoOut) session.getAttribute("uInfo")).getOwnerID();
        %>
        <div class="account">
            <div class="logout"><a href="RootAccountAuth?action=logout">[ logout ]</a></div>
            <div class="logout"><a href="#" onclick="processOwnerUpdate()" >[ Update My Account ]</a></div>
            <div class="logout"><a href="#" onclick="processUserCreate()" >[ Create User ]</a></div>
            <div class="logout"><a href="#" onclick="processUserDelete()" >[ Delete User ]</a></div>
            <div style="width : 300px;"><span>*</span> User: <span class="accountDetails"><%=userId%></span> / Owner profile: <span class="accountDetails"><%=ownerId%></span></div>
        </div>
    </body>
</html>
