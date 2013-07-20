<%@page import="com.sun.xacml.ctx.Result"%>
<%@page import="fr.unicaen.iota.epcisphi.utils.PEPRequester"%>
<%
    String cert = (session.getAttribute("cert") != null)? (String) session.getAttribute("cert") : null;
    if (cert == null) {
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
        <script type="text/javascript" src="script/jquery-min.js"></script>
        <script type="text/javascript" src="script/jquery-ui.min.js"></script>
        <link rel="stylesheet" type="text/css" href="style/jquery-ui.css" />
        <link rel="stylesheet" type="text/css" href="style/style.css" />
    </head>
    <body>
        <div class="treeTitle">&nbsp;</div>

        <jsp:include page="dialogs.jsp" />
        <%
            User user = (User) session.getAttribute("user");
            if (PEPRequester.checkAccess(user, "superadmin") != Result.DECISION_PERMIT) {
        %>
        <jsp:include page="policyList.jsp" />
        <%
            }
            else {
        %>
        <jsp:include page="rootMenu.jsp" />
        <%
                return;
            }
            String userId = user.getUserID();
            String ownerId = user.getOwnerID();
        %>
        <div class="account">
            <div class="logout"><a href="RootAccountAuth?action=logout">[ logout ]</a></div>
            <div class="logout"><a href="#" onclick="processOwnerUpdate()" >[ Update My Account ]</a></div>
            <div class="logout"><a href="#" onclick="processUserCreate()" >[ Create User ]</a></div>
            <div class="logout"><a href="#" onclick="processUserDelete()" >[ Delete User ]</a></div>
            <div style="width : 300px;"><span>*</span> User : <span class="accountDetails"><%=userId%></span> / Owner profile : <span class="accountDetails"><%=ownerId%></span></div>
        </div>

    </body>
</html>
