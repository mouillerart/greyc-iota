</head>
<body>
<%@page import="fr.unicaen.iota.ds.model.DSEvent,
        java.util.List"%>
<jsp:include page="/jsp/common/message.jsp"/>
<div class="queryDs events">
    <%
        boolean eventFound = false;
        if (request.getAttribute("events") != null) {
            Object o = request.getAttribute("events");
            if (o instanceof List) {
                List<DSEvent> eventList = (List<DSEvent>) o;
                if (eventList != null && !eventList.isEmpty()) {
                    eventFound = true;
    %>
    <div class="queryDs eventsFound">
        <h3>Event list associated to the EPC code and contained by the DS</h3>
        <%
            for (DSEvent event : eventList) {
        %>
        <div class="queryDs eventItems">
            <div class="queryDs event eventType"><%= event.getEventType()%></div>
            <div class="queryDs event epc"><%= event.getEpc()%></div>
            <div class="queryDs event bizStep"><%= event.getBizStep()%></div>
            <div class="queryDs event eventTime"><%= event.getEventTime().toXMLFormat()%></div>
            <div class="queryDs event serviceType"><%= event.getServiceType()%></div>
            <div class="queryDs event serviceAddress"><%= event.getServiceAddress()%></div>
        </div><%
                }
            }%>
        <script type="text/javascript">
            initToggleDisplay("eventItems");
        </script><%
                }
            }
            if (!eventFound) {
        %>
        <p class="queryDs noevent">No event found</p>
        <%
            }
        %>
    </div>
</div>
</body>
