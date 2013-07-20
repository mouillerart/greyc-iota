<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.Module"%>
<%@page import="fr.unicaen.iota.epcisphi.utils.HTMLUtilities"%>

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
    <div class="dialog_message">Enter the new Owner informations :</div>
    <div class="dialog_options">Owner Id : <input disabled id="ownerID" type="text" /></div>
</div>

<div id="GroupDialog" title="Group Name" class="modalDialog">
    <div class="dialog_message">Enter the name of the new user group to create :</div>
    <div class="dialog_options">Name : <input id="groupName" type="text" /></div>
</div>

<div id="usersDialog" title="Add User" class="modalDialog">
    <div class="dialog_message">Choose a new user you want to associate in this group :</div>
    <div class="dialog_options">User : <input type="text" value="" id="groupOwnerName" /></div>
</div>

<div id="userAdminPermissionDialog" title="Add user permissions" class="modalDialog">
    <div class="dialog_message">Select a method to grant access to this group :</div>
    <div class="dialog_options">
        <%=HTMLUtilities.createMethodSelect(Module.adminModule)%>
    </div>
</div>

<div id="userCapturePermissionDialog" title="Add user permissions" class="modalDialog">
    <div class="dialog_message">Select a method to grant access to this group :</div>
    <div class="dialog_options">
        <%=HTMLUtilities.createMethodSelect(Module.captureModule)%>
    </div>
</div>

<div id="userQueryPermissionDialog" title="Add user permissions" class="modalDialog">
    <div class="dialog_message">Select a method to grant access to this group :</div>
    <div class="dialog_options">
        <%=HTMLUtilities.createMethodSelect(Module.queryModule)%>
    </div>
</div>

<div id="questionDialog" title="Are you sure ??" class="modalDialog">
    <div id="questionDialogMsg" class="dialog_message">msg</div>
</div>


<div id="updateGroupNameDialog" title="Group Name" class="modalDialog">
    <div class="dialog_message">Enter the new name of the group :</div>
    <div class="dialog_options">Name : <input id="groupNameUpdate" type="text" value="" /></div>
</div>

<div id="bizStepFilterDialog" title="Business Step Filter" class="modalDialog">
    <div class="dialog_message">Select a Business Step filter :</div>
    <div class="dialog_options">Name : <input id="bizStepFilterName" type="text" value="" /></div>
</div>

<div id="epcFilterDialog" title="EPC Filter" class="modalDialog">
    <div class="dialog_message">Enter an EPC filter :</div>
    <div class="dialog_options">Name : <input id="epcFilterName" type="text" value="" /></div>
</div>

<div id="eventTypeFilterDialog" title="Event Type Filter" class="modalDialog">
    <div class="dialog_message">Select an Event Type filter :</div>
    <div class="dialog_options">Event Type : <%=HTMLUtilities.createSelectEventTypeFilter()%></div>
</div>

<div id="eventTimeFilterDialog" title="Event Time Filter" class="modalDialog">
    <div class="dialog_message">Enter the period for the Event Time filter :</div>
    <div class="dialog_options">FROM : <input id="datepicker1" type="text"></div>
    <div class="dialog_options">TO : <input id="datepicker2" type="text"></div>
</div>

<div id="recordTimeFilterDialog" title="Record Time Filter" class="modalDialog">
    <div class="dialog_message">Enter the period for the Record Time filter :</div>
    <div class="dialog_options">FROM : <input id="rdatepicker1" type="text"></div>
    <div class="dialog_options">TO : <input id="rdatepicker2" type="text"></div>
</div>

<div id="operationFilterDialog" title="Action Filter" class="modalDialog">
    <div class="dialog_message">Select an Action filter :</div>
    <div class="dialog_options">Action : <%=HTMLUtilities.createSelectOperationFilter()%></div>
</div>

<div id="parentIdFilterDialog" title="Parent EPC Filter" class="modalDialog">
    <div class="dialog_message">Enter a Parent EPC filter :</div>
    <div class="dialog_options">Name : <input id="parentIdFilterName" type="text" value="" /></div>
</div>

<div id="childEpcFilterDialog" title="Child EPC Filter" class="modalDialog">
    <div class="dialog_message">Enter a Child EPC filter :</div>
    <div class="dialog_options">Name : <input id="childEpcFilterName" type="text" value="" /></div>
</div>

<div id="quantityFilterDialog" title="Quantity Filter" class="modalDialog">
    <div class="dialog_message">Enter Quantity filter :</div>
    <div class="dialog_options">FROM : <input id="quantitypicker1" type="text"></div>
    <div class="dialog_options">TO : <input id="quantitypicker2" type="text"></div>
</div>

<div id="readPointFilterDialog" title="Read Point Filter" class="modalDialog">
    <div class="dialog_message">Enter a Read Point filter :</div>
    <div class="dialog_options">Name : <input id="readPointFilterName" type="text" value="" /></div>
</div>

<div id="bizLocFilterDialog" title="Business Location Filter" class="modalDialog">
    <div class="dialog_message">Enter a Business Location filter :</div>
    <div class="dialog_options">Name : <input id="bizLocFilterName" type="text" value="" /></div>
</div>

<div id="dispositionFilterDialog" title="Disposition Filter" class="modalDialog">
    <div class="dialog_message">Enter a Disposisition filter :</div>
    <div class="dialog_options">Name : <input id="dispositionFilterName" type="text" value="" /></div>
</div>

<div id="masterDataIdFilterDialog" title="MasterData ID Filter" class="modalDialog">
    <div class="dialog_message">Enter a MasterData ID filter :</div>
    <div class="dialog_options">Name : <input id="masterDataIdFilterName" type="text" value="" /></div>
</div>

<div id="errorDialog" title="Error !!!" class="modalDialog">
    <div class="dialog_message"><span id="errorMessage">&nbsp;</span></div>
</div>

<div id="successDialog" title="Commande Successfull" class="modalDialog">
    <div class="dialog_message"><span id="successMessage">&nbsp;</span></div>
</div>
