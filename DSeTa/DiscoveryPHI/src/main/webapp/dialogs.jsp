<%@page import="fr.unicaen.iota.discovery.client.model.UserInfo"%>
<%@page import="fr.unicaen.iota.discovery.client.model.PartnerInfo"%>
<%@page import="fr.unicaen.iota.xacml.ihm.Module"%>
<%@page import="fr.unicaen.iota.utils.HTMLUtilities"%>

<%
    String partnerId = ((PartnerInfo) session.getAttribute("pInfo")).getPartnerId();
    String pServiceId = ((PartnerInfo) session.getAttribute("pInfo")).getServiceList().get(0).getId();
    String pServiceAddress = ((PartnerInfo) session.getAttribute("pInfo")).getServiceList().get(0).getUri().toString();
    String pServiceType = ((PartnerInfo) session.getAttribute("pInfo")).getServiceList().get(0).getType();
%>

<div id="createUser" title="Create User" class="modalDialog">
    <div class="dialog_message">Enter user informations:</div>
    <div class="dialog_options">Login: <input id="userLogin" type="text" value="" /></div>
    <div class="dialog_options">Password: <input id="userPassword" type="password" /></div>
    <div class="dialog_options">Confirm password: <input id="userConfirmation" type="password" /></div>
</div>

<div id="updatePartner" title="Update Partner Informations" class="modalDialog">
    <div class="dialog_message">Enter the new partner informations:</div>
    <div class="dialog_options">Partner Id: <input disabled id="partnerID" type="text" value="<%=partnerId%>" /></div>
    <div id="dialogServiceId" class="dialog_options">Service Id: <input id="serviceID" type="text" value="<%=pServiceId%>" /></div>
    <div class="dialog_options">Service type: <%=HTMLUtilities.createSelectServiceType(pServiceType, "")%></div>
    <div class="dialog_options">Service address: <input id="serviceAddress" type="text" value="<%=pServiceAddress%>" /></div>
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
    <div class="dialog_message">Chosoe a new Partner you want to associate in this group:</div>
    <div class="dialog_options">Partner: <input type="text" value="" id="groupPartnerName" /></div>
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

<div id="epcClassFilterDialog" title="Event Class Filter" class="modalDialog">
    <div class="dialog_message">Select an Event class filter:</div>
    <div class="dialog_options">Event class: <%=HTMLUtilities.createSelectEventClassFilter()%></div>
</div>

<div id="errorDialog" title="Error !!!" class="modalDialog">
    <div class="dialog_message"><span id="errorMessage">&nbsp;</span></div>
</div>

<div id="successDialog" title="Commande Successfull" class="modalDialog">
    <div class="dialog_message"><span id="successMessage">&nbsp;</span></div>
</div>
