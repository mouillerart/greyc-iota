</head>
<body>
<%@page import="java.util.Map.Entry,
        fr.unicaen.iota.nu.ONSEntryType,
        java.util.Map"%>
<jsp:include page="/jsp/common/message.jsp"/>
<div class="queryOns onsEntries"><%
        boolean entryFound = false;
        if (request.getAttribute("entries") != null) {
            Object o = request.getAttribute("entries");
            if (o instanceof Map) {
                Map<ONSEntryType, String> entriesList = (Map<ONSEntryType, String>) o;
                if (entriesList != null && !entriesList.isEmpty()) {
                    entryFound = true;
    %>
    <h3>ONS entries associated to the EPC code and contained by the ONS</h3><%
        for (Entry<ONSEntryType, String> entry : entriesList.entrySet()) {
    %>
    <div class="queryOns onsEntryItems">
        <div class="queryOns onsEntryItem onsEntryType"><%= entry.getKey() %></div>
        <div class="queryOns onsEntryItem onsEntryUrl"><%= entry.getValue() %></div>
    </div><%
                    }
                }
            }
        }
        if (!entryFound) {
    %>
    <div class="queryOns noentry">No entry found</div><%
        }
    %>
</div>
</body>
