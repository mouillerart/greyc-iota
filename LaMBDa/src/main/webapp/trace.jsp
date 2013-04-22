<%@page import="fr.unicaen.iota.lambda.Utils.SignatureState,
        org.w3c.dom.Element,
        java.util.Map.Entry,
        java.util.Map,
        fr.unicaen.iota.mu.EPCISEventTypeHelper,
        java.util.ArrayList,
        java.util.List,
        org.fosstrak.epcis.model.EPCISEventType"%>
<div class="back"><a href="index.jsp">Back</a></div>
<div class="trace">
    <div class="trace referentDS"><%
            if (request.getAttribute("referentDS") != null) {
                String referentDS = (String) request.getAttribute("referentDS");
        %>
        <p class="trace refDS">The referent DS is: <span class="trace ds"><%= referentDS%></span></p><%
            }
            else {
        %>
        <p class="trace norefDS">Referent DS not found.</p><%
            }
        %>
    </div>
    <div class="trace events"><%
            boolean eventFound = false;
            if (request.getAttribute("events") != null) {
                Object o = request.getAttribute("events");
                if (o instanceof Map) {
                    Map<String, List<EPCISEventType>> eventsByEpcis = (Map<String, List<EPCISEventType>>) o;
                    if (!eventsByEpcis.isEmpty()) {
                        eventFound = true;
                    }
                    for (Entry<String, List<EPCISEventType>> eventsAndEPCIS : eventsByEpcis.entrySet() {
                        List<EPCISEventType> eventList = eventsAndEPCIS.getValue();
        %>
        <div class="trace eventsbyepcis">
            <p class="trace eventEpcis">EPCIS: <span class="trace epcis"><%= eventsAndEPCIS.getKey()%></span></p>
            <%
                if (eventList != null && !eventList.isEmpty()) {
            %>
            <div class="trace eventsFound">
                <h3>Event list associated to the EPC code and contained by the EPCIS</h3>
                <%
                    for (EPCISEventType event : eventList) {
                %>
                <%
                    EPCISEventTypeHelper eventHelper = new EPCISEventTypeHelper(event);
                %>
                <div class="trace event">
                    <div class="trace event eventType"><%= eventHelper.getType()%></div>
                    <div class="trace event epciList"><%= eventHelper.getEpcList()%></div>
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
                %>
            </div>
            <%
                }
            %>
            <p class="trace noeventForEpcis">No event</p>
        </div>
        %>
        <%
                    }
                }
            }
            if (!eventFound) {
        %>
        <p class="trace noevent">No event found.</p>
        <%
            }
        %>
    </div>
</div>
