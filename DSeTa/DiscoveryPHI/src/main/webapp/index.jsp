<%@page import="fr.unicaen.iota.discovery.client.model.UserInfo"%>
<%@page import="fr.unicaen.iota.discovery.client.model.PartnerInfo"%>
<%@page import="fr.unicaen.iota.discovery.client.DsClient"%>
<%@page import="fr.unicaen.iota.utils.SessionLoader"%>
<%@page import="com.sun.xacml.ctx.Result"%>
<%@page import="fr.unicaen.iota.utils.PEPRequester"%>
<%@page import="fr.unicaen.iota.xacml.conf.Configuration"%>
<%
    String sessionId = (String) session.getAttribute("session-id");
    String sid = (String) request.getParameter("sid");
    String uid = (String) request.getParameter("uid");
    if (sid != null) {
        DsClient dsClient = new DsClient(Configuration.DS_ADDRESS);
        String message = SessionLoader.loadSession(sid, dsClient, uid, session);
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
<%@page import="fr.unicaen.iota.auth.Partner"%>
<%@page import="fr.unicaen.iota.auth.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Policy Manager</title>
        <script type="text/javascript" src="script/tree.js"></script>
        <script type="text/javascript" src="script/requestDispatcher.js"></script>
        <script type="text/javascript" src="script/jquery.js"></script>
        <script type="text/javascript" src="script/jquery-ui.min.js"></script>
        <link rel="stylesheet" type="text/css" href="style/jquery-ui.css" />
        <link rel="stylesheet" type="text/css" href="style/style.css" />
    </head>
    <body>
        <div class="treeTitle">&nbsp;</div>
        <jsp:include page="dialogs.jsp" />
        <%
            PartnerInfo pInfo = (PartnerInfo) session.getAttribute("pInfo");
            UserInfo uInfo = (UserInfo) session.getAttribute("uInfo");
            Partner partner = new Partner(0, true, pInfo.getPartnerId(), new Date(), null, null, null);
            User u = new User(0, partner, "", uInfo.getUserId(), null, new Date());
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
            String userId = ((UserInfo) session.getAttribute("uInfo")).getUserId();
            String partnerId = ((PartnerInfo) session.getAttribute("pInfo")).getPartnerId();
        %>
        <div class="account">
            <div class="logout"><a href="RootAccountAuth?action=logout">[ logout ]</a></div>
            <div class="logout"><a href="#" onclick="processPartnerUpdate()" >[ Update My Account ]</a></div>
            <div class="logout"><a href="#" onclick="processUserCreate()" >[ Create User ]</a></div>
            <div style="width : 300px;"><span>*</span> User: <span class="accountDetails"><%=userId%></span> / Partner profile: <span class="accountDetails"><%=partnerId%></span></div>
        </div>
    </body>
</html>
