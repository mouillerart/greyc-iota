<%@page import="fr.unicaen.iota.ypsilon.client.model.UserInfoOut"%>
<%@page import="fr.unicaen.iota.utils.HTMLUtilities"%>

<div class="rootMenu">
    <div class="rootMenuItem"><a href="#" onclick="processAccountCreate()" >Create Account</a></div>
    <div class="rootMenuItem"><a href="#" onclick="processUpdateRootUser()" >Update root Account</a></div>
    <div id="rootMenuItemLogout" class="rootMenuItem"><a href="RootAccountAuth?action=logout" >Logout</a></div>
</div>

<div id="createAccount" title="Create Account" class="modalDialog">
    <div class="dialog_message_category">Enter the new owner informations :</div>
    <div class="dialog_options">Owner Id : <input id="ownerID1" type="text" value="" /></div>
    <div class="dialog_message_category">Enter the root user informations :</div>
    <div class="dialog_options">User's certificate DN used to connect: <input id="userLogin1" type="text" value="" /></div>
    <div class="dialog_options">User name (empty if the DN is compatible with the LDAP directory) : <input id="userID1" type="text" value="" /></div>
</div>

<div id="updateRootUser" title="Update User" class="modalDialog">
    <div class="dialog_message">Enter user informations :</div>
    <div class="dialog_options">Login : <input disabled id="userLogin2" type="text" value="<%=((UserInfoOut) session.getAttribute("uInfo")).getOwnerID()%>" /></div>
</div>
