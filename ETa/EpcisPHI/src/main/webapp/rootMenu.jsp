<%@page import="fr.unicaen.iota.epcisphi.utils.HTMLUtilities"%>
<%@page import="fr.unicaen.iota.eta.user.userservice.UserInfoOut"%>

<div class="rootMenu">
    <div class="rootMenuItem"><a href="#" onclick="processAccountCreate()" >Create Account</a></div>
    <div class="rootMenuItem"><a href="#" onclick="processUpdateRootUser()" >Update root Account</a></div>
    <div id="rootMenuItemLogout" class="rootMenuItem"><a href="RootAccountAuth?action=logout" >Logout</a></div>
</div>

<div id="createAccount" title="Create Account" class="modalDialog">
    <div class="dialog_message_category">Enter the new partner informations :</div>
    <div class="dialog_options">Partner Id : <input id="partnerID1" type="text" value="" /></div>
    <div class="dialog_options">Service Id : <input id="serviceID1" type="text" value="" /></div>
    <div class="dialog_options">Service type :
        <%=HTMLUtilities.createSelectServiceType("", "1")%>
    </div>
    <div class="dialog_options">Service Address : <input id="serviceAddress1" type="text" value="" /></div>
    <div class="dialog_message_category">Enter the root user informations :</div>
    <div class="dialog_options">Login : <input id="userLogin1" type="text" value="" /></div>
    <div class="dialog_options">Password : <input id="userPassword1" type="password" /></div>
    <div class="dialog_options">Password : <input id="userPassword12" type="password" /></div>
</div>

<div id="updateRootUser" title="Update User" class="modalDialog">
    <div class="dialog_message">Enter user informations :</div>
    <div class="dialog_options">Login : <input disabled id="userLogin2" type="text" value="<%=((UserInfoOut) session.getAttribute("uInfo")).getPartnerID()%>" /></div>
    <div class="dialog_options">Password : <input id="userPassword2" type="password" /></div>
    <div class="dialog_options">Confirm password : <input id="userConfirmation2" type="password" /></div>
</div>