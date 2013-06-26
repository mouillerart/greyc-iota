<%
    String epc = (request.getParameter("epc") != null)? request.getParameter("epc") : "urn:epc:id:sgtin:1.2.3.4";
    String serviceURL = (request.getParameter("serviceURL") != null)? request.getParameter("serviceURL") : "https://localhost:8443/eta/ided_query";
%><form class="initForm" method="post" action="demo">
    <div id="epcCode">
        <label>EPC code:<input class="input text" type="text" name="epc" value="<%= epc %>" /></label><br>
    </div>
    <div id="service">Service to use:
        <select name="service" onchange="toggleDisplayServiceURL(this.value)">
            <option value="all">Complete trace</option>
            <option value="epcis">EPCIS</option>
            <option value="ds">DS</option>
            <option value="ons">ONS</option>
        </select>
    </div>
    <div id="serviceURL">
        <label>Service URL:<input class="input text" type="text" name="serviceURL" value ="<%= serviceURL %>" /></label><br>
    </div>
    <div id="signature">
        <label>Verify the signatures?<input class="input checkbox" type="checkbox" name="signature" /></label><br>
    </div>
    <div class="button submit">
        <input class="submit" type="submit" value="Submit">
    </div>
</form><%
    if ("ds".equals(request.getParameter("service")) || "ons".equals(request.getParameter("service"))) {
%>
<script type="text/javascript">
    hideId("serviceURL");
    hideId("signature");
</script><%
    }
%>
