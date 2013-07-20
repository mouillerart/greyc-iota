
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
                        switchEpcPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "eventTypeFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Event Type policy Filter ?");
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
            $("#questionDialogMsg").html("Do you want to switch the Event Time policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchEventTimePolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "recordTimeFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Record Time policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchRecordTimePolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "operationFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Action policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchOperationPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
         case "parentIdFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Parent EPC policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchParentIdPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "childEpcFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Child EPC policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchChildEpcPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "quantityFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Quantity policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchQuantityPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "readPointFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Read Point policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchReadPointPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "bizLocFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Business Location policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchBizLocPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "dispositionFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the Disposition policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchDispositionPolicy(groupId,objectId,nodeValue,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "masterDataIdFilterGroupNode":
            $("#questionDialogMsg").html("Do you want to switch the MasterData ID policy Filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        switchMasterDataIdPolicy(groupId,objectId,nodeValue,module);
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
                var userName = $("#userID").attr("value");
                createUser(login,userName);
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
                updateUser(login);
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
                        addEpcRestriction(groupId,objectId,blockNode,module,epc);
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
                        var eventType = $("#eventTypeFilterName").attr("value");
                        addEventTypeRestriction(groupId,objectId,blockNode,module,eventType);
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
            $("#eventTimeFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        var date1 = $("#datepicker1").attr("value");
                        var date2 = $("#datepicker2").attr("value");
                        if (date1.length == 0 || date2.length == 0) {
                            errorDialog("A parameter is missing.");
                            return;
                        }
                        $(this).dialog("close");
                        if (checkDates(date1,date2))
                            addEventTimeRestriction(groupId,objectId,blockNode,module,date1,date2);
                        else
                            addEventTimeRestriction(groupId,objectId,blockNode,module,date2,date1);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "recordTimeFilterGroupNode":
            $( "#rdatepicker1" ).datepicker();
            $( "#rdatepicker2" ).datepicker();
            $("#recordTimeFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var date1 = $("#rdatepicker1").attr("value");
                        var date2 = $("#rdatepicker2").attr("value");
                        if (checkDates(date1,date2))
                            addRecordTimeRestriction(groupId,objectId,blockNode,module,date1,date2);
                        else
                            addRecordTimeRestriction(groupId,objectId,blockNode,module,date2,date1);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
         case "operationFilterGroupNode":
            $("#operationFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var operation = $("#operationFilterName").attr("value");
                        addOperationRestriction(groupId,objectId,blockNode,module,operation);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "parentIdFilterGroupNode":
            $("#parentIdFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var parentId = $("#parentIdFilterName").attr("value");
                        addParentIdRestriction(groupId,objectId,blockNode,module,parentId);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "childEpcFilterGroupNode":
            $("#childEpcFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var childEpc = $("#childEpcFilterName").attr("value");
                        addChildEpcRestriction(groupId,objectId,blockNode,module,childEpc);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "quantityFilterGroupNode":
            $("#quantityFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var quantity1 = $("#quantitypicker1").attr("value");
                        var quantity2 = $("#quantitypicker2").attr("value");
                        if (is_int(quantity1) && is_int(quantity2)) {
                            if (parseInt(quantity1,10) <= parseInt(quantity2,10))
                                addQuantityRestriction(groupId,objectId,blockNode,module,quantity1,quantity2);
                            else
                                addQuantityRestriction(groupId,objectId,blockNode,module,quantity2,quantity1);
                        }
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "readPointFilterGroupNode":
            $("#readPointFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var readPoint = $("#readPointFilterName").attr("value");
                        addReadPointRestriction(groupId,objectId,blockNode,module,readPoint);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "bizLocFilterGroupNode":
            $("#bizLocFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var bizLoc = $("#bizLocFilterName").attr("value");
                        addBizLocRestriction(groupId,objectId,blockNode,module,bizLoc);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "dispositionFilterGroupNode":
            $("#dispositionFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var disposition = $("#dispositionFilterName").attr("value");
                        addDispositionRestriction(groupId,objectId,blockNode,module,disposition);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                    }
                }
            });
            break;
        case "masterDataIdFilterGroupNode":
            $("#masterDataIdFilterDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function processDate(){
                        $(this).dialog("close");
                        var masterDataId = $("#masterDataIdFilterName").attr("value");
                        addMasterDataIdRestriction(groupId,objectId,blockNode,module,masterDataId);
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
            $("#questionDialogMsg").html("Do you want to remove this Business Step filter ?");
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
                        removeEpcRestriction(groupId,objectId,blockNode,module);
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
            $("#questionDialogMsg").html("Do you want to remove this Event Time filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeEventTimeRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "recordTimeFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Record Time filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeRecordTimeRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "operationFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Action filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeOperationRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "parentIdFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Parent EPC filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeParentIdRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "childEpcFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Child EPC filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeChildEpcRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "quantityFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Quantity filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeQuantityRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "readPointFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Read Point filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeReadPointRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "bizLocFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Business Location filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeBizLocRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "dispositionFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this Disposition filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeDispositionRestriction(groupId,objectId,blockNode,module);
                    } ,
                    "Cancel": function() {
                        $(this).dialog("close");
                        return 0;
                    }
                }
            });
            break;
        case "masterDataIdFilterNode":
            $("#questionDialogMsg").html("Do you want to remove this MasterData ID filter ?");
            $("#questionDialog").dialog({
                modal: true ,
                draggable: false,
                buttons: {
                    "Validate" : function process(){
                        $(this).dialog("close");
                        removeMasterDataIdRestriction(groupId,objectId,blockNode,module);
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

function createUser(login,userName){
    $.get('AccessControlPolicy', {
        a:"createUser",
        f:login,
        g:userName
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


function updateUser(login){
    $.get('AccessControlPolicy', {
        a:"updateUser",
        f:login
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
        if (isRequestSuccessfull(data)) { //si la requête s'est bien déroulée
            successDialog("SUCESSFULL EXECUTED !");
        }
        else{ // sinon
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
function addEpcRestriction(groupId,objectId,blockNode,module,newEpc){
    $.get('AccessControlPolicy', {
        a:"addEpcRestriction",
        b:objectId,
        c:newEpc,
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
function removeEpcRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeEpcRestriction",
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
function addEventTimeRestriction(groupId,objectId,blockNode,module,date1,date2){
    $.get('AccessControlPolicy', {
        a:"addEventTimeRestriction",
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
function removeEventTimeRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeEventTimeRestriction",
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
function addRecordTimeRestriction(groupId,objectId,blockNode,module,date1,date2){
    $.get('AccessControlPolicy', {
        a:"addRecordTimeRestriction",
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
function removeRecordTimeRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeRecordTimeRestriction",
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
function addOperationRestriction(groupId,objectId,blockNode,module,newOperation){
    $.get('AccessControlPolicy', {
        a:"addOperationRestriction",
        b:objectId,
        c:newOperation,
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
function removeOperationRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeOperationRestriction",
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
function addParentIdRestriction(groupId,objectId,blockNode,module,newParentId){
    $.get('AccessControlPolicy', {
        a:"addParentIdRestriction",
        b:objectId,
        c:newParentId,
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
function removeParentIdRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeParentIdRestriction",
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
function addChildEpcRestriction(groupId,objectId,blockNode,module,newChildEpc){
    $.get('AccessControlPolicy', {
        a:"addChildEpcRestriction",
        b:objectId,
        c:newChildEpc,
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
function removeChildEpcRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeChildEpcRestriction",
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
function addQuantityRestriction(groupId,objectId,blockNode,module,quantity1,quantity2){
    $.get('AccessControlPolicy', {
        a:"addQuantityRestriction",
        b:objectId,
        d1:quantity1,
        d2:quantity2,
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
function removeQuantityRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeQuantityRestriction",
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
function addReadPointRestriction(groupId,objectId,blockNode,module,newReadPoint){
    $.get('AccessControlPolicy', {
        a:"addReadPointRestriction",
        b:objectId,
        c:newReadPoint,
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
function removeReadPointRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeReadPointRestriction",
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
function addBizLocRestriction(groupId,objectId,blockNode,module,newBizLoc){
    $.get('AccessControlPolicy', {
        a:"addBizLocRestriction",
        b:objectId,
        c:newBizLoc,
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
function removeBizLocRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeBizLocRestriction",
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
function addDispositionRestriction(groupId,objectId,blockNode,module,newDisposition){
    $.get('AccessControlPolicy', {
        a:"addDispositionRestriction",
        b:objectId,
        c:newDisposition,
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
function removeDispositionRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeDispositionRestriction",
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
function addMasterDataIdRestriction(groupId,objectId,blockNode,module,newMasterDataId){
    $.get('AccessControlPolicy', {
        a:"addMasterDataIdRestriction",
        b:objectId,
        c:newMasterDataId,
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
function removeMasterDataIdRestriction(groupId,objectId,blockNode,module){
    $.get('AccessControlPolicy', {
        a:"removeMasterDataIdRestriction",
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
function switchEpcPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchEpcPolicy",
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
function switchEventTimePolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchEventTimePolicy",
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
function switchRecordTimePolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchRecordTimePolicy",
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
function switchOperationPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchOperationPolicy",
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
function switchParentIdPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchParentIdPolicy",
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
function switchChildEpcPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchChildEpcPolicy",
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
function switchQuantityPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchQuantityPolicy",
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
function switchReadPointPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchReadPointPolicy",
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
function switchBizLocPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchBizLocPolicy",
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
function switchDispositionPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchDispositionPolicy",
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
function switchMasterDataIdPolicy(groupId,objectId,elem,module){
    $.get('AccessControlPolicy', {
        a:"switchMasterDataIdPolicy",
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

function checkDates(date1, date2) {
    var date1s = date1.split('/');
    var date2s = date2.split('/');
    var day1 = parseInt(date1s[1]);
    // Javascript begins month to 0
    var month1 = parseInt(date1s[0]) - 1;
    var year1 = parseInt(date1s[2]);
    var dateobj1 = new Date(year1, month1, day1);

    var day2 = parseInt(date2s[1]);
    // Javascript begins month to 0
    var month2 = parseInt(date2s[0]) - 1;
    var year2 = parseInt(date2s[2]);
    var dateobj2 = new Date(year2, month2, day2);
    return dateobj1 < dateobj2;
}

function is_int(input){
    return !isNaN(input) && parseInt(input) == input;
  }
