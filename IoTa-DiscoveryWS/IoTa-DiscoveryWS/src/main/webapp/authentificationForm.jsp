<jsp:include page="template/checkSession.jsp" />
<div id="logForm">
    <form method="post" action="Authentification" onSubmit="return verifFormLog(this);">
        <span>login: </span><input class="inputText" type="text" name="login" value="" /><br/>
        <span>password: </span><input class="inputText" type="password" name="passwd" value="" />
        <div><input type="image" src="./images/login.jpg"/></div>
    </form>
</div>