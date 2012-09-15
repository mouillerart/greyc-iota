<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Discovery Service Home Page</title>
        <link rel="stylesheet" type="text/css" href="style/style.css" />
    </head>
    <body>
        <jsp:include page="template/checkAccess.jsp" />
        <% session.setAttribute("state", "Discovery services / Profile Manager /");%>
        <jsp:include page="header.jsp"></jsp:include>
        <jsp:include page="profile.jsp"></jsp:include>
    </body>
</html>
