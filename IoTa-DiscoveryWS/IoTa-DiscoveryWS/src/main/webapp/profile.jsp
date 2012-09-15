<%@page import="fr.unicaen.iota.discovery.server.hibernate.User"%>
<%@page import="fr.unicaen.iota.discovery.server.hibernate.Partner"%>
<jsp:include page="template/checkAccess.jsp" />
<%
    User user = (User) (session.getAttribute("user"));
    Partner partner = user.getPartner();
%>
<div class="accountbox accountboxuser">
    <div class="accountboxcontainer" style="margin-right:20px;margin-left:20px;">
        <div class="profiletopic3"><span>User Name: </span><%=user.getLogin()%></div>
        <div class="profiletopic3"><span>User Password: </span><% for (int i = 0; i < user.getPasswd().length(); i++) {%>*<% }%></div>
        <div class="profiletopic3"><span>Session ID: </span><%=session.getAttribute("sessionID")%></div>
        <div class="profiletopic3"><span>Creation Time: </span><%=user.getDate()%></div>
    </div>
</div>
<div class="accountbox accountboxprofile">
    <div class="accountboxcontainer" style="margin-right:20px;margin-left:20px;">
        <div class="profiletopic3"><span>Partner ID: </span><%=partner.getPartnerID()%></div>
        <div class="profiletopic3"><span>Partner Service Type: </span><%=partner.getServiceType()%></div>
        <div class="profiletopic3"><span>Address: </span><%=partner.getServiceAddress()%></div>
        <div class="profiletopic3"><span>Creation Time: </span><%=partner.getDate()%></div>
    </div>
</div>
