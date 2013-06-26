<jsp:include page="title.jsp"/><%
    if (request.getAttribute("message") != null) {
%>
<div class="message"><%=request.getAttribute("message")%></div><%
    }
%>
<jsp:include page="form.jsp"/>
