<%@page import="org.fosstrak.epcis.model.EPCISEventExtensionType,
        java.util.Map,
        fr.unicaen.iota.lambda.Utils.SignatureState,
        org.w3c.dom.Element,
        fr.unicaen.iota.mu.EPCISEventTypeHelper,
        java.util.ArrayList,
        java.util.List,
        org.fosstrak.epcis.model.EPCISEventType"%>
<div class="back"><a href="index.jsp">Back</a></div>
<div class="queryEpcis">
    <p class="queryEpcis epcCode">EPC code: <span class="queryEpcis epc"><%= request.getParameter("epc")%></span></p>
    <p class="queryEpcis queriedService">Queried EPCIS: <span class="queryEpcis url"><%= request.getParameter("serviceURL")%></span></p>
    <div class="queryEpcis events">
        <%
            boolean eventFound = false;
            if (request.getAttribute("events") != null) {
                Object o = request.getAttribute("events");
                if (o instanceof List) {
                    List<EPCISEventType> eventList = (List<EPCISEventType>) o;
                    if (eventList != null && !eventList.isEmpty()) {
                        eventFound = true;
        %>
        <div class="queryEpcis eventsFound">
            <h3>Event list associated to the EPC code and contained by the EPCIS</h3>
            <%
                for (EPCISEventType event : eventList) {
                    EPCISEventTypeHelper eventHelper = new EPCISEventTypeHelper(event);
            %>
            <div class="queryEpcis eventItems">
                <div class="queryEpcis event eventType"><%= eventHelper.getType()%></div>
                <div class="queryEpcis event epcList"><%= eventHelper.getEpcList()%></div>
                <div class="queryEpcis event bizStep"><%= eventHelper.getBizStep()%></div>
                <div class="queryEpcis event eventTime"><%= eventHelper.getEventTime().getTime().toString()%></div>
                <div class="queryEpcis event recordTime"><%= eventHelper.getRecordTime().getTime().toString()%> <%= eventHelper.getEventTimeZoneOffset()%></div>
                <div class="queryEpcis event action"><%= eventHelper.getAction()%></div>
                <div class="queryEpcis event parentId"><%= eventHelper.getParentID()%></div>
                <div class="queryEpcis event children"><%= eventHelper.getChildren()%></div>
                <div class="queryEpcis event bizLoc"><%= eventHelper.getBizLocation()%></div>
                <div class="queryEpcis event bizTrans"><%= eventHelper.getBizTransactions()%></div>
                <div class="queryEpcis event quantity"><%= eventHelper.getQuantity()%></div>
                <div class="queryEpcis event readPoint"><%= eventHelper.getReadPoint()%></div>
                <div class="queryEpcis event disposition"><%= eventHelper.getDisposition()%></div>
                <div class="queryEpcis event extensions"><%
                    for (Object obj : eventHelper.getAny()) {
                        if (obj instanceof Element) {
                            Element el = (Element) obj;
                            String namespace = el.getNamespaceURI();
                            String name = el.getLocalName();
                            String extensionName = namespace + "##" + name;
                            String extensionValue = el.getTextContent();
                    %><span class="queryEpcis event extensions extensionName"><%= extensionName%></span> <span class="queryEpcis event extensions extensionValue"><%= extensionValue%></span><br><%
                            }
                        }
                    %>
                </div><%
                    String signature = SignatureState.NOT_VERIFIED.getState();
                    if (request.getAttribute("signatures") != null) {
                        Map<EPCISEventType, SignatureState> signatures = (Map<EPCISEventType, SignatureState>) request.getAttribute("signatures");
                        signature = signatures.get(event).getState();
                    }
                %>
                <div class="queryEpcis event signature"><%= signature%></div>
            </div>
                <%
                    }
                }%>
            <script type="text/javascript">
                initToggleDisplay("eventItems");
            </script><%
                    }
                }
                if (!eventFound) {
            %>
            <p class="queryEpcis noevent">No event found</p>
            <%
                }
            %>
        </div>
    </div>
</div>
