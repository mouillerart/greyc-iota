<%
    if (request.getAttribute("message") != null) {
%>
<p class="message"><%=request.getAttribute("message")%></p>
<%
    }
%>
<p class="back"><a href="index.jsp">Back</a></p>
