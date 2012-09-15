<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link rel="stylesheet" type="text/css" href="style/style.css" />
        <title>Discovery Services Home Page</title>
        <script type="text/javascript" src="script/check_form.js"> </script>
    </head>
    <body>
        <% session.setAttribute("state", "Discovery services / Home /");%>
        <jsp:include page="header.jsp"></jsp:include>
        <jsp:include page="template/checkSession.jsp" />
        <%
            if (session.getAttribute("sessionID") == null) {
        %>
        <%
                String value = request.getParameter("value");
                if (value != null && "false".equals(request.getParameter("value"))) {
        %>
        <div style="color:red;text-align:center;font-weight:bold;">Bad login or password.</div>
        <%
                }
        %>
        <jsp:include page="authentificationForm.jsp" />
        <%
                return;
            }
        %>
    </body>
</html>
