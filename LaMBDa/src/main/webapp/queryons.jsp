<%@page import="java.util.Map.Entry,
        fr.unicaen.iota.nu.ONSEntryType,
        java.util.Map"%>
<div class="back"><a href="index.jsp">Back</a></div>
<div class="queryOns">
    <p class="queryOns epcCode">EPC code: <span class="queryOns epc"><%= request.getParameter("epc")%></span></p>
    <p class="queryOns queriedService">Queried ONS: <span class="queryOns url"><%= request.getParameter("serviceURL")%></span></p>
    <div class="queryOns onsEntries">
        <%
            boolean entryFound = false;
            if (request.getAttribute("entries") != null) {
                Object o = request.getAttribute("entries");
                if (o instanceof Map) {
                    Map<ONSEntryType, String> entriesList = (Map<ONSEntryType, String>) o;
                    if (entriesList != null && !entriesList.isEmpty()) {
                        entryFound = true;
        %>
        <h3>ONS entries associated to the EPC code and contained by the ONS</h3>
        <%
            for (Entry<ONSEntryType, String> entry : entriesList.entrySet()) {
        %>
        <div class="queryOns onsEntryItems">
            <div class="queryOns onsEntryItem onsEntryType"><%= entry.getKey() %></div>
            <div class="queryOns onsEntryItem onsEntryUrl"><%= entry.getValue() %></div>
        </div>
        <%
                        }
                    }
                }
            }
            if (!entryFound) {
        %>
        <p class="queryOns noentry">No entry found</p>
        <%
            }
        %>
    </div>
</div>
