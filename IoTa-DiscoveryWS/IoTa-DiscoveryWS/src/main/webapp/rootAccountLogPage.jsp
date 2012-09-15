<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Root Account Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link rel="stylesheet" type="text/css" href="style/style.css" />
        <script type="text/javascript" src="script/check_form.js"> </script>
    </head>
    <body>
        <h2>Root Account Logging Page</h2>
        <jsp:include page="template/checkSession.jsp" />
        <%
            if (request.getAttribute("message") != null) {
        %>
        <div style="color:red;text-align:center;font-weight:bold;"><%=request.getAttribute("message")%></div>
        <%
            }
        %>
        <div id="logForm">
            <form method="post" action="RootAccountAuth?action=login" onSubmit="return verifFormLog(this);">
                <span>login: </span><input class="inputText" type="text" name="login" value="" /><br/> 
                <span>password: </span><input class="inputText" type="password" name="passwd" value="" />
                <div><input type="image" src="./images/login.jpg"/></div>
            </form>
        </div>
    </body>
</html>
