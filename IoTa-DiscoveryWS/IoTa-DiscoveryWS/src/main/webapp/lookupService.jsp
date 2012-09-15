<%@page import="fr.unicaen.iota.discovery.server.querycontrol.DSControler"%>
<%@page import="fr.unicaen.iota.xacml.policy.Module"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="fr.unicaen.iota.discovery.server.hibernate.User"%>
<%@page import="fr.unicaen.iota.discovery.server.hibernate.Event"%>
<%@page import="fr.unicaen.iota.discovery.server.util.ProtocolException"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="style/style.css" />
        <script type="text/javascript" src="script/check_form.js"> </script>
        <title>Fr.Unicaen Discovery Services</title>
    </head>
    <body>
        <jsp:include page="template/checkAccess.jsp" />
        <%
            session.setAttribute("state", "Discovery services / Lookup Service /");
        %>
        <jsp:include page="header.jsp"></jsp:include>
        <form method="post" action="lookupService.jsp" id="lookupFormTag">
            <div class="categorybar">
                <div class="categoryTitle" id="categoryLookup">
                    <div><span>EPC: </span><input id="lookupForm" type="text" name="epc" value=""/><input type="image" src="./images/search.jpg"/></div>
                </div>
            </div>
        </form>
        <%
            DSControler dSControler = new DSControler();
            if (request.getParameter("epc") == null) {
                return;
            }
            String epc = request.getParameter("epc");
            String sessionId = (String) session.getAttribute("sessionID");
            List<Event> events = new ArrayList<Event>();
            String error = null;
            try {
                events = dSControler.eventLookup(sessionId, epc);
            } catch (ProtocolException pe) {
                error = pe.getMessage();
            }
        %>
        <div class="resultTitle">Results for EPC <span><%=epc%></span>:</div>
        <%
            if (events.size() == 0) {
        %>
        <div class="resultitem">No results</div>
        <%
                if (error != null) {
        %>
        <div class="resultitemerror"><%=error%></div>
        <%
                }
                return;
            }
            int i = 1;
            for (Event event : events) {
        %>
        <div class="resultitem">
            <%=i%> : <%=event.getPartner().getServiceAddress()%>
        </div>
        <div class="subresult">  <span>id:</span> <%=event.getId()%> </div>
        <div class="subresult">  <span>type:</span> <%=event.getEventType()%></div>
        <div class="subresult">  <span>class:</span> <%=event.getEPCClass()%> </div>
        <div class="subresult">  <span>life cycle step:</span> <%=event.getBizStep()%></div>
        <div class="subresult">  <span>event timestamp:</span> <%=event.getEventTimeStamp()%> </div>
        <div class="subresult">  <span>source timestamp:</span> <%=event.getSourceTimeStamp()%></div>
        <%
                i++;
            }
        %>
    </body>
</html>
