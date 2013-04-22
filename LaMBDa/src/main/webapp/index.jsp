<h1 class="lambda">Luxe and Mark Beautiful Demo application</h1><%
    if (request.getAttribute("message") != null) {
%>
<p class="message"><%=request.getAttribute("message")%></p><%
    }
%>
<form method="post" action="demo">
    <div id="epcCode">
        <label>EPC code:<input class="input text" type="text" name="epc" value="urn:epc:id:sgtin:1.2.3.4" /></label><br>
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
        <label>Service URL:<input class="input text" type="text" name="serviceURL" value ="https://localhost:8443/eta/ided_query" /></label><br>
    </div>
    <div id="signature">
        <label>Verify the signatures?<input class="input checkbox" type="checkbox" name="signature" /></label><br>
    </div>
    <script type="text/javascript">
        hideId("serviceURL");
    </script>
    <div class="button submit">
        <input class="submit" type="submit" value="Submit">
    </div>
</form>
