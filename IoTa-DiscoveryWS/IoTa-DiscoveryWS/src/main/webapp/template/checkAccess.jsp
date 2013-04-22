<jsp:include  page ="checkSession.jsp" />
<%
           if (session.getAttribute("sessionID") == null) {
%>
<h3>Permission Denied !</h3>
<%
                return;
            }
%>
