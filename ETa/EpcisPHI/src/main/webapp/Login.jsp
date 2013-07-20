<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>EPCIS ACCESS CONTROL CENTER</title>
        <link rel="stylesheet" type="text/css" href="style/style_DS.css" />
    </head>
    <body>
        <h2>EPCIS ACCESS CONTROL CENTER</h2>
        <%
            if (request.getAttribute("message") != null) {
                String message = (String) request.getAttribute("message");
                message = message.replaceAll("<", "&#60;");
                message = message.replaceAll(">", "&#62;");
        %>
        <div style="color:red;text-align:center;font-weight:bold;"><%= message %></div>
        <%
            }
        %>
        <div id="logForm">
            <form method="post" action="RootAccountAuth?action=login">
                <div><input type="image" src="./pics/login.jpg"/></div>
            </form>
        </div>
    </body>
</html>
