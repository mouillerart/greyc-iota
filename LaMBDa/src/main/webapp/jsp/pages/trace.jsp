</head>
<body>
    <%@page import="fr.unicaen.iota.lambda.Utils.SignatureState,
            fr.unicaen.iota.lambda.Utils.Utils,
            org.w3c.dom.Element,
            java.util.Map.Entry,
            java.util.Map,
            fr.unicaen.iota.mu.EPCISEventTypeHelper,
            java.util.ArrayList,
            java.util.List,
            java.util.HashMap,
            org.fosstrak.epcis.model.EPCISEventType"%>
    <jsp:include page="/jsp/common/message.jsp"/>
    <div class="trace">
        <div class="trace referentDS"><%
            if (request.getAttribute("referentDS") != null) {
                String referentDS = (String) request.getAttribute("referentDS");
            %>
            <div class="trace refDS">The referent DS is: <span class="trace ds"><%= referentDS%></span></div><%
            }
            else {
                %>
            <div class="trace norefDS">Referent DS not found.</div><%
            }
            %>
        </div>
        <div class="trace events" id="trace"><%
            boolean eventFound = false;
            if (request.getAttribute("events") != null) {
                Object o = request.getAttribute("events");
                if (o instanceof Map) {
                    Map<String, List<EPCISEventType>> eventsByEpcis = (Map<String, List<EPCISEventType>>) o;
                    if (eventsByEpcis != null && !eventsByEpcis.isEmpty()) {
                        eventFound = true;
                        Map<EPCISEventType, String> epcisByEvent = new HashMap<EPCISEventType, String>();
                        for (Entry<String, List<EPCISEventType>> eventsAndEPCIS : eventsByEpcis.entrySet()) {
                            List<EPCISEventType> eventList = eventsAndEPCIS.getValue();
                            for (EPCISEventType event : eventList) {
                                epcisByEvent.put(event, eventsAndEPCIS.getKey());
                            }
                        }
                        int i = 0;
                        List<EPCISEventType> eventList = new ArrayList<EPCISEventType>(epcisByEvent.keySet());
                        Utils.sortEPCISEventList(eventList);
                        for (EPCISEventType event : eventList) {
                            i++;
                            EPCISEventTypeHelper eventHelper = new EPCISEventTypeHelper(event);
            %>
            <div class="trace event eventItems" id="event<%=i%>">
                <div class="trace event epcis"><%= epcisByEvent.get(event)%></div>
                <div class="trace event eventType"><%= eventHelper.getType()%></div>
                <div class="trace event epcList"><%= eventHelper.getEpcList()%></div>
                <div class="trace event bizStep"><%= eventHelper.getBizStep()%></div>
                <div class="trace event eventTime"><%= eventHelper.getEventTime().getTime().toString()%></div>
                <div class="trace event recordTime"><%= eventHelper.getRecordTime().getTime().toString()%> <%= eventHelper.getEventTimeZoneOffset()%></div>
                <div class="trace event action"><%= eventHelper.getAction()%></div>
                <div class="trace event parentId"><%= eventHelper.getParentID()%></div>
                <div class="trace event children"><%= eventHelper.getChildren()%></div>
                <div class="trace event bizLoc"><%= eventHelper.getBizLocation()%></div>
                <div class="trace event bizTrans"><%= eventHelper.getBizTransactions()%></div>
                <div class="trace event quantity"><%= eventHelper.getQuantity()%></div>
                <div class="trace event readPoint"><%= eventHelper.getReadPoint()%></div>
                <div class="trace event disposition"><%= eventHelper.getDisposition()%></div>
                <div class="trace event extensions"><%
                    for (Object obj : eventHelper.getAny()) {
                        if (obj instanceof Element) {
                            Element el = (Element) obj;
                            String namespace = el.getNamespaceURI();
                            String name = el.getLocalName();
                            String extensionName = namespace + "##" + name;
                            String extensionValue = el.getTextContent();
                    %><span class="trace event extensions extensionName"><%= extensionName%></span> <span class="trace event extensions extensionValue"><%= extensionValue%></span><br><%
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
                <div class="trace event signature"><%= signature%></div>
            </div>
            <%
                        }
                    }
                }
            %>
        </div><%
            }
            if (!eventFound) {
        %>
        <div class="trace noevent">No event found.</div>
        <%
            }
        %>
    </div>
    <script type="text/javascript">
        initToggleDisplay("eventItems");
    </script>
</body>
