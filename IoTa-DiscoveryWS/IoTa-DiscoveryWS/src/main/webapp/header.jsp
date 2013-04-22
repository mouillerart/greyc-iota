<%@page import="fr.unicaen.iota.discovery.server.hibernate.User"%>
<%@page import="fr.unicaen.iota.discovery.server.util.Constants"%>

<jsp:include page="template/checkSession.jsp" />

<%--<div id="titleBack"><div id="title">&nbsp;</div></div>--%>

<div id="mainmenu">
    <%
        if (session.getAttribute("sessionID") != null) {
    %>
    <a href="profileManager.jsp">Profile Manager</a> <span>|</span> <a href="lookupService.jsp">Lookup Service</a> <span>|</span>
    <a href="<%=Constants.XACML_IHM_URL + "?sid=" + session.getAttribute("sessionID")
            + "&uid=" + ((User) (session.getAttribute("user"))).getUserID()%>">Policy Manger</a>
    <%
        } else {
    %>
    Restricted area
    <%
        }
    %>
</div>

<div id="statebar">
    <div id="stateinfo"><%=session.getAttribute("state")%></div>
    <div id="statelogout">
        <%
            if (session.getAttribute("sessionID") != null) {
        %>
        Click here to [ <a href="Logout">logout</a> ]
        <%
            }
        %>
    </div>
    <div class="wrapper"></div>
</div>
<div id="ombreHeader">&nbsp;</div>
