
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
        case "epcClassFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the EPC Class policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchEPCClassPolicy(groupId,objectId,nodeValue,module);
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
                validatePartnerPolicy(module);
            } ,
            "Cancel": function() {
                $(this).dialog("close");
                return 0;
            }
        }
    });
}

function processPartnerUpdate(){
    $("#updatePartner").dialog({
        modal: true ,
        draggable: false,
        minWidth : 350,
        buttons: {
            "Validate" : function processDate(){
                $(this).dialog("close");
                var partnerID = $("#partnerID").attr("value");
                var serviceID = $("#serviceID").attr("value");
                var serviceAddress = $("#serviceAddress").attr("value");
                var serviceType = $("#serviceType").attr("value");
                updatePartner(partnerID,serviceID,serviceAddress,serviceType);
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
                var password = $("#userPassword1").attr("value");
                var partnerID = $("#partnerID1").attr("value");
                var serviceID = $("#serviceID1").attr("value");
                var serviceAddress = $("#serviceAddress1").attr("value");
                var serviceType = $("#serviceType1").attr("value");
                createAccount(partnerID,serviceID,serviceType,serviceAddress,login,password);
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
                        var partner = $("#groupPartnerName").attr("value");
                        addPartnerToGroup(groupId,objectId,blockNode,module,partner);
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
        case "epcClassFilterGroupNode":
            $("#epcClassFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var classR = $("#epcClassFilterName").attr("value");
                        addEPCClassRestriction(groupId,objectId,blockNode,module,classR);
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
                        createPartnerGroup(groupId,objectId,blockNode,module,groupName);
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
        case "epcClassFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this EPC Class filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeEPCClassRestriction(groupId,objectId,blockNode,module);
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
                        deletePartnerGroup(groupId,objectId,blockNode,module);
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
                        removePartnerFromGroup(groupId,objectId,blockNode,module);
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

function createPartnerGroup(groupId,objectId,blockNode,module, groupName){
    $.get('AccessControlPolicy', {
        a:"createPartnerGroup",
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
function deletePartnerGroup(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"deletePartnerGroup",
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

function updatePartner(partnerID,serviceID,serviceAddress,serviceType){
    $.get('AccessControlPolicy', {
        a:"updatePartner",
        f:partnerID,
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

function createAccount(partnerID,serviceID,serviceType,serviceAddress,login,password){
    $.get('AccessControlPolicy', {
        a:"createAccount",
        f:partnerID,
        g:serviceID,
        h:serviceType,
        i:serviceAddress,
        j:login,
        k:password
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


function addPartnerToGroup(groupId,objectId,blockNode,module,newName){
    $.get('AccessControlPolicy', {
        a:"addPartnerToGroup",
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
function removePartnerFromGroup(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removePartnerFromGroup",
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
function addEPCClassRestriction(groupId,objectId,blockNode,module,newEPCClass){
    $.get('AccessControlPolicy', {
        a:"addEPCClassRestriction",
        b:objectId,
        c:newEPCClass,
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
function removeEPCClassRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeEPCClassRestriction",
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
function switchEPCClassPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchEPCClassPolicy",
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

function validatePartnerPolicy(module){
    $.get('AccessControlPolicy', {
        a:"savePartnerPolicy",
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
                    a:"cancelPartnerPolicy",
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
