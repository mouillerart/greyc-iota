<%@page import="fr.unicaen.iota.ypsilon.client.model.UserInfoOut"%>
<%@page import="fr.unicaen.iota.xacml.ihm.Module"%>
<%@page import="fr.unicaen.iota.utils.HTMLUtilities"%>

<%
    String ownerId = ((UserInfoOut) session.getAttribute("uInfo")).getOwnerID();
%>

<div id="createUser" title="Create User" class="modalDialog">
    <div class="dialog_message">Enter user informations :</div>
    <div class="dialog_options">User's certificate DN used to connect: <input id="userLogin" type="text" value="" /></div>
    <div class="dialog_options">User name (empty if the DN is compatible with the LDAP directory) : <input id="userID" type="text" value="" /></div>
</div>

<div id="deleteUser" title="Delete User" class="modalDialog">
    <div class="dialog_message">Enter user information :</div>
    <div class="dialog_options">Login : <input id="userId" type="text" value="" /></div>
</div>

<div id="updateOwner" title="Update Owner Informations" class="modalDialog">
    <div class="dialog_message">Enter the new owner informations :</div>
    <div class="dialog_options">Owner Id : <input disabled id="ownerID" type="text" value="<%=ownerId%>" /></div>
</div>

<div id="TimeDialog" title="Event Time Filter" class="modalDialog">
    <div class="dialog_message">Enter the period for the EventTime filter:</div>
    <div class="dialog_options">From: <input id="datepicker1" type="text"></div>
    <div class="dialog_options">To: <input id="datepicker2" type="text"></div>
</div>

<div id="GroupDialog" title="Group Name" class="modalDialog">
    <div class="dialog_message">Enter the name of the new user group to create:</div>
    <div class="dialog_options">Name: <input id="groupName" type="text" /></div>
</div>

<div id="usersDialog" title="Add User" class="modalDialog">
    <div class="dialog_message">Chose a new user you want to associate in this group:</div>
    <div class="dialog_options">User: <input type="text" value="" id="groupOwnerName" /></div>
</div>

<div id="userAdminPermissionDialog" title="Add user permissions" class="modalDialog">
    <div class="dialog_message">Select a method to grant access to this group:</div>
    <div class="dialog_options"><%=HTMLUtilities.createMethodSelect(Module.adminModule)%></div>
</div>

<div id="userCapturePermissionDialog" title="Add user permissions" class="modalDialog">
    <div class="dialog_message">Select a method to grant access to this group:</div>
    <div class="dialog_options"><%=HTMLUtilities.createMethodSelect(Module.captureModule)%></div>
</div>

<div id="userQueryPermissionDialog" title="Add user permissions" class="modalDialog">
    <div class="dialog_message">Select a method to grant access to this group:</div>
    <div class="dialog_options"><%=HTMLUtilities.createMethodSelect(Module.queryModule)%></div>
</div>

<div id="questionDialog" title="Are you sure ??" class="modalDialog">
    <div id="questionDialogMsg" class="dialog_message">msg</div>
</div>

<div id="updateGroupNameDialog" title="Group Name" class="modalDialog">
    <div class="dialog_message">Enter the new name of the group:</div>
    <div class="dialog_options">Name: <input id="groupNameUpdate" type="text" value="" /></div>
</div>

<div id="bizStepFilterDialog" title="Business Step Filter" class="modalDialog">
    <div class="dialog_message">Select a BizStep filter:</div>
    <div class="dialog_options">Name: <input id="bizStepFilterName" type="text" value="" /></div>
</div>

<div id="epcFilterDialog" title="EPC Filter" class="modalDialog">
    <div class="dialog_message">Enter an EPC filter:</div>
    <div class="dialog_options">Name: <input id="epcFilterName" type="text" value="" /></div>
</div>

<div id="eventTypeFilterDialog" title="Event Type Filter" class="modalDialog">
    <div class="dialog_message">Select an Event type filter:</div>
    <div class="dialog_options">Event class: <%=HTMLUtilities.createSelectEventTypeFilter()%></div>
</div>

<div id="errorDialog" title="Error !!!" class="modalDialog">
    <div class="dialog_message"><span id="errorMessage">&nbsp;</span></div>
</div>

<div id="successDialog" title="Commande Successfull" class="modalDialog">
    <div class="dialog_message"><span id="successMessage">&nbsp;</span></div>
</div>
