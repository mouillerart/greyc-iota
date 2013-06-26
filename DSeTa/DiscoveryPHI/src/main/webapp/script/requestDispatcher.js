
function processRequestUpdate(type,groupId,objectId,treeNode,module){
    switch (type)
    {
        case "policyNode" :
            var elem = treeNode.parentNode.parentNode;
            var fils = elem.childNodes;
            var nbFils = fils.length;
            for(var i=0;i<nbFils;i++){
                if(fils[i].className=="TreeNodeTitleInnerHtml"){
                    var elem2 = fils[i].childNodes;
                    var nbFils2 = elem2.length;
                    for(var i=0;i<nbFils2;i++){
                        if(elem2[i].className=="TreeNodeTitleInnerHtmlValue"){
                            var element = elem2[i];
                            $("#groupNameUpdate").attr("value",element.innerHTML);
                            $("#updateGroupNameDialog").dialog({
                                modal: true ,
                                draggable: false,
                                buttons: {
                                    "Validate" : function processDate(){
                                        $(this).dialog("close");
                                        var newName = $("#groupNameUpdate").attr("value");
                                        updateGroupName(groupId,objectId,element,module,newName);
                                    } ,
                                    "Cancel": function() {
                                        $(this).dialog("close");
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }
            break;
        default:
            errorDialog("update ACTION NOT IMPLEMENTED");
            break;
    }
}

function processSwitchPolicy(type,groupId,objectId,treeNode,module){
    var elem = treeNode.parentNode.parentNode;
    var fils = elem.childNodes;
    var nbFils = fils.length;
    var nodeValue = null;
    for(var i=0;i<nbFils;i++){
        if(fils[i].className=="TreeNodeTitleInnerHtml"){
            var elem2 = fils[i].childNodes;
            var nbFils2 = elem2.length;
            for(var i=0;i<nbFils2;i++){
                if(elem2[i].className=="TreeNodeTitleInnerHtmlValue"){
                    nodeValue = elem2[i];
                    break;
                }
            }
        }
    }
    switch (type){
        case "bizStepFilterGroupNode" :
            $("#questionDialogMsg").html("Do you want to switch the Business Step policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchBizStepPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });

            break;
        case "epcFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the EPC policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchEPCPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "eventTypeFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Event type policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchEventTypePolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });

            break;
        case "eventTimeFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Event Time Class policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchTimePolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "usersNode":
            $("#questionDialogMsg").html("Do you want to switch the users global policy ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchPermissionPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        default:
            errorDialog("switch ACTION NOT IMPLEMENTED");
            break;
    }
}

function getTreeBlock(node){
    var foo= node.parentNode.parentNode.parentNode.childNodes;
    for(var i=0;foo.length;i++){
        if(foo[i].className == "TreeNodeContent"){
            return foo[i];
        }
    }
    return null;
}



function processRequestSave(module){
    $("#questionDialogMsg").html("Do you realy want to save the policy ?<br>This will erase all previous configuration !");
    $("#questionDialog").dialog({
        modal: true ,
        draggable: false,
        buttons: {
            "Validate" : function process(){
                $(this).dialog("close");
                validateOwnerPolicy(module);
            } ,
            "Cancel": function() {
                $(this).dialog("close");
                return 0;
            }
        }
    });
}

function processOwnerUpdate(){
    $("#updateOwner").dialog({
        modal: true ,
        draggable: false,
        minWidth : 350,
        buttons: {
            "Validate" : function processDate(){
                $(this).dialog("close");
                var ownerID = $("#ownerID").attr("value");
                var serviceID = $("#serviceID").attr("value");
                var serviceAddress = $("#serviceAddress").attr("value");
                var serviceType = $("#serviceType").attr("value");
                updateOwner(ownerID,serviceID,serviceAddress,serviceType);
            } ,
            "Cancel": function() {
                $(this).dialog("close");
            }
        }
    });
}

function processUserCreate(){
    $("#createUser").dialog({
        modal: true ,
        draggable: false,
        minWidth : 360,
        buttons: {
            "Validate" : function processDate(){
                $(this).dialog("close");
                var login = $("#userLogin").attr("value");
                var password = $("#userPassword").attr("value");
                createUser(login,password);
            } ,
            "Cancel": function() {
                $(this).dialog("close");
            }
        }
    });
}

function processUserDelete(){
    $("#deleteUser").dialog({
        modal: true ,
        draggable: false,
        minWidth : 360,
        buttons: {
            "Validate" : function processDate(){
                $(this).dialog("close");
                var login = $("#userId").attr("value");
                deleteUser(login);
            } ,
            "Cancel": function() {
                $(this).dialog("close");
            }
        }
    });
}

function processUpdateRootUser(){
    $("#updateRootUser").dialog({
        modal: true ,
        draggable: false,
        minWidth : 360,
        buttons: {
            "Validate" : function processDate(){
                $(this).dialog("close");
                var login = $("#userLogin2").attr("value");
                var password = $("#userPassword2").attr("value");
                updateUser(login,password);
            } ,
            "Cancel": function() {
                $(this).dialog("close");
            }
        }
    });
}


function processAccountCreate(){
    $("#createAccount").dialog({
        modal: true ,
        draggable: false,
        minWidth : 350,
        buttons: {
            "Validate" : function processAccountCreate(){
                $(this).dialog("close");
                var login = $("#userLogin1").attr("value");
                var ownerID = $("#ownerID1").attr("value");
                var userID = $("#userID1").attr("value");
                createAccount(ownerID,login,userID);
            } ,
            "Cancel": function() {
                $(this).dialog("close");
            }
        }
    });
}

function processRequestCreate(type,groupId,objectId,treeNode,module){
    var blockNode = getTreeBlock(treeNode);
    switch (type)
    {
        case "usersNode" :
            $("#usersDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var owner = $("#groupOwnerName").attr("value");
                        addOwnerToGroup(groupId,objectId,blockNode,module,owner);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "bizStepFilterGroupNode" :
            $("#bizStepFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var bizStep = $("#bizStepFilterName").attr("value");
                        addBizStepRestriction(groupId,objectId,blockNode,module,bizStep);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "epcFilterGroupNode":
            $("#epcFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var epc = $("#epcFilterName").attr("value");
                        addEPCRestriction(groupId,objectId,blockNode,module,epc);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "eventTypeFilterGroupNode":
            $("#eventTypeFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var classR = $("#eventTypeFilterName").attr("value");
                        addEventTypeRestriction(groupId,objectId,blockNode,module,classR);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "eventTimeFilterGroupNode":
            $( "#datepicker1" ).datepicker();
            $( "#datepicker2" ).datepicker();
            $("#TimeDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var date1 = $("#datepicker1").attr("value");
                        var date2 = $("#datepicker2").attr("value");
                        addTimeRestriction(groupId,objectId,blockNode,module,date1,date2);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "methodFilterGroupNode":
            var id="";
            switch(module){
                case "queryModule" :
                    id="userQueryPermissionDialog";
                    break;
                case "captureModule" :
                    id="userCapturePermissionDialog";
                    break;
                case "adminModule" :
                    id="userAdminPermissionDialog";
                    break;
            }
            $("#"+id).dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var method = "";
                        switch(module){
                            case "queryModule" :
                                method = $("#methodNameQuery").attr("value");
                                break;
                            case "captureModule" :
                                method = $("#methodNameCapture").attr("value");
                                break;
                            case "adminModule" :
                                method = $("#methodNameAdmin").attr("value");
                                break;
                        }
                        addUserPermission(groupId,objectId,blockNode,module,method);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "policiesNode":
            $("#GroupDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var groupName = $("#groupName").attr("value");
                        createOwnerGroup(groupId,objectId,blockNode,module,groupName);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        default:
            errorDialog("create ACTION NOT IMPLEMENTED");
            break;
    }
}



function processRequestRemove(type,groupId,objectId,treeNode,module){
    var blockNode = treeNode.parentNode.parentNode.parentNode;
    switch (type)
    {
        case "bizStepFilterNode" :
            $("#questionDialogMsg").html("Do you want to remove this Business step filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeBizStepRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "epcFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this EPC filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeEPCRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "eventTypeFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Event Type filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeEventTypeRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "eventTimeFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Event time filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeTimeRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "methodFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this method filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeUserPermission(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "policyNode":
            $("#questionDialogMsg").html("Do you want to remove this group ? All the corresponding information will be lost !");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        deleteOwnerGroup(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "userNode":
            $("#questionDialogMsg").html("Do you want to remove this user from the group ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeOwnerFromGroup(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        default:
            errorDialog("delete ACTION NOT IMPLEMENTED");
            break;
    }
}

function confirmAction(filter) {
    return confirm(filter);
}

/* UTIL METHODS */

function removeNode(elem){
    elem.parentNode.removeChild(elem);
}

function changeContentText(elem,value){
    elem.innerHTML = value;
}

function changeElementToValidate(module){
    $("#fragment-"+module).parent().css("background-color","transparent");
    $("#fragment-"+module+"-control-valid").css("display","none");
    $("#fragment-"+module+"-control-cancel").css("display","none");
}

function setPolicyModified(module){
    $("#fragment-"+module).parent().css("background-color","pink");
    $("#fragment-"+module+"-control-valid").css("display","block");
    $("#fragment-"+module+"-control-cancel").css("display","block");
}

function addNode(html,blockNode){
    blockNode.innerHTML = html+blockNode.innerHTML;
}


/* SERVICE METHODS */

function createOwnerGroup(groupId,objectId,blockNode,module, groupName){
    $.get('AccessControlPolicy', {
        a:"createOwnerGroup",
        b:objectId,
        c:groupName,
        e:groupId,
        d:module
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            addNode(getHTML(data),blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function deleteOwnerGroup(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"deleteOwnerGroup",
        e:groupId,
        b:objectId,
        d:module
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            removeNode(blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}

function updateOwner(ownerID,serviceID,serviceAddress,serviceType){
    $.get('AccessControlPolicy', {
        a:"updateOwner",
        f:ownerID,
        g:serviceID,
        h:serviceAddress,
        i:serviceType
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            successDialog("SUCESSFULL EXECUTED !");
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}

function createUser(login,password){
    $.get('AccessControlPolicy', {
        a:"createUser",
        f:login,
        g:password
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            successDialog("SUCESSFULL EXECUTED !");
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}


function updateUser(login,password){
    $.get('AccessControlPolicy', {
        a:"updateUser",
        f:login,
        g:password
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            successDialog("SUCESSFULL EXECUTED !");
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}

function deleteUser(login){
    $.get('AccessControlPolicy', {
        a:"deleteUser",
        f:login
    },
    function(data){
        if (isRequestSuccessfull(data)) {
            successDialog("SUCESSFULL EXECUTED !");
        }
        else{
            errorDialog(getRepsonseDescription(data));
        }
    });
}

function createAccount(ownerID,login,userName){
    $.get('AccessControlPolicy', {
        a:"createAccount",
        f:login,
        g:ownerID,
        h:userName
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            successDialog("SUCESSFULL EXECUTED !");
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}


function addOwnerToGroup(groupId,objectId,blockNode,module,newName){
    $.get('AccessControlPolicy', {
        a:"addOwnerToGroup",
        b:objectId,
        c:newName,
        e:groupId,
        d:module
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            addNode(getHTML(data),blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function removeOwnerFromGroup(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeOwnerFromGroup",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            removeNode(blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function addBizStepRestriction(groupId,objectId,blockNode,module,newBizStep){
    $.get('AccessControlPolicy', {
        a:"addBizStepRestriction",
        b:objectId,
        c:newBizStep,
        e:groupId,
        d:module
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            addNode(getHTML(data),blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function removeBizStepRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeBizStepRestriction",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            removeNode(blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function addEPCRestriction(groupId,objectId,blockNode,module,newEPC){
    $.get('AccessControlPolicy', {
        a:"addEPCRestriction",
        b:objectId,
        c:newEPC,
        e:groupId,
        d:module
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            addNode(getHTML(data),blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function removeEPCRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeEPCRestriction",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            removeNode(blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function addEventTypeRestriction(groupId,objectId,blockNode,module,newEventType){
    $.get('AccessControlPolicy', {
        a:"addEventTypeRestriction",
        b:objectId,
        c:newEventType,
        e:groupId,
        d:module
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            addNode(getHTML(data),blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function removeEventTypeRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeEventTypeRestriction",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            removeNode(blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function addTimeRestriction(groupId,objectId,blockNode,module,date1,date2){
    $.get('AccessControlPolicy', {
        a:"addTimeRestriction",
        b:objectId,
        d1:date1,
        d2:date2,
        e:groupId,
        d:module
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            addNode(getHTML(data),blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function removeTimeRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeTimeRestriction",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            removeNode(blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function switchBizStepPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchBizStepPolicy",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            changeContentText(elem,getHTML(data));
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function switchEPCPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchEPCPolicy",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            changeContentText(elem,getHTML(data));
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function switchEventTypePolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchEventTypePolicy",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            changeContentText(elem,getHTML(data));
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function switchTimePolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchTimePolicy",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            changeContentText(elem,getHTML(data));
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function switchPermissionPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchPermissionPolicy",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            changeContentText(elem,getHTML(data));
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function removeUserPermission(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeUserPermission",
        e:groupId,
        d:module,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            removeNode(blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function addUserPermission(groupId,objectId,blockNode,module,methodName){
    $.get('AccessControlPolicy', {
        a:"addUserPermission",
        b:objectId,
        c:methodName,
        e:groupId,
        d:module
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            addNode(getHTML(data),blockNode);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}
function updateGroupName(groupId,objectId,element,module,newName){
    $.get('AccessControlPolicy', {
        a:"updateGroupName",
        e:groupId,
        d:module,
        c:newName,
        b:objectId
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            changeContentText(element,newName);
            setPolicyModified(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}

function validateOwnerPolicy(module){
    $.get('AccessControlPolicy', {
        a:"savePolicyOwner",
        e:null,
        d:module,
        b:null
    },
    function(data){
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            changeElementToValidate(module);
            loadPolicyTrees(module);
        }
        else{ // sinon
            errorDialog(getRepsonseDescription(data));
        }
    });
}

function processRequestCancelPolicyChanges(module){
    $("#questionDialogMsg").html("Do you realy want to cancel all changes in the policy ?<br>This will erase your actual configuration !");
    $("#questionDialog").dialog({
        modal: true ,
        draggable: false,
        buttons: {
            "Validate" : function process(){
                $(this).dialog("close");
                $.get('AccessControlPolicy', {
                    a:"cancelOwnerPolicy",
                    e:null,
                    d:module,
                    b:null
                },
                function(data){
                    if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
                        changeElementToValidate(module);
                        loadPolicyTrees(module);
                    }
                    else{ // sinon
                        errorDialog(getRepsonseDescription(data));
                    }
                });
            } ,
            "Cancel": function() {
                $(this).dialog("close");
                return 0;
            }
        }
    });
}

function isRequestSuccessfull(result){
    var res = result.getElementsByTagName("result");
    var resId = res[0].getElementsByTagName("id")[0].firstChild.nodeValue;
    return resId==1;
}

function getRepsonseDescription(result){
    var res = result.getElementsByTagName("result");
    var resStr = res[0].getElementsByTagName("desc")[0].firstChild.nodeValue;
    return resStr;
}


function getHTML(data){
    return data.getElementsByTagName("htmlcontent")[0].textContent;
}

function loadPolicyTrees(module){
    if(module=="queryModule" || module == null ){
        $.ajax({
            url: 'AccessControlPolicy',
            type: "GET",
            data: "a=loadPolicyTree&d=queryModule&e=null&b=null",
            beforeSend : function(){
                $('#treeQueryModule').html("<img src=\"pics/load2.gif\" />");
            },
            success: function(data) {
                if (isRequestSuccessfull(data)) {
                    $('#treeQueryModule').html(getHTML(data));
                }
                else{
                    errorDialog(getRepsonseDescription(data));
                }
            },
            error : function(){
                errorDialog("Unable to access service !");
            }
        });
    }
    if(module=="captureModule" || module == null ){
        $.ajax({
            url: 'AccessControlPolicy',
            type: "GET",
            data: "a=loadPolicyTree&d=captureModule&e=null&b=null",
            beforeSend : function(){
                $('#treeCaptureModule').html("<img src=\"pics/load2.gif\" />");
            },
            success: function(data) {
                if (isRequestSuccessfull(data)) {
                    $('#treeCaptureModule').html(getHTML(data));
                }
                else{
                    errorDialog(getRepsonseDescription(data));
                }
            },
            error : function(){
                errorDialog("Unable to access service !");
            }
        });
    }
    if(module=="adminModule" || module == null ){
        $.ajax({
            url: 'AccessControlPolicy',
            type: "GET",
            data: "a=loadPolicyTree&d=adminModule&e=null&b=null",
            beforeSend : function(){
                $('#treeAdminModule').html("<img src=\"pics/load2.gif\" />");
            },
            success: function(data) {
                if (isRequestSuccessfull(data)) {
                    $('#treeAdminModule').html(getHTML(data));
                }
                else{
                    errorDialog(getRepsonseDescription(data));
                }
            },
            error : function(){
                errorDialog("Unable to access service !");
            }
        });
    }
}

function errorDialog(msg){
    $('#errorMessage').html(msg);
    $("#errorDialog").dialog({
        modal: true ,
        draggable: false,
        buttons: {
            "Validate" : function processDate(){
                $(this).dialog("close");
            }
        }
    });
}

function successDialog(msg){
    $('#successMessage').html(msg);
    $("#successDialog").dialog({
        modal: true ,
        draggable: false,
        buttons: {
            "Validate" : function processDate(){
                $(this).dialog("close");
            }
        }
    });
}
