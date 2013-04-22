function expand(elem){
    var block = elem.parentNode.parentNode;
    var fils = block.childNodes;
    var nbFils = fils.length;
    for(var i=0;i<nbFils;i++){
        if(fils[i].className=="TreeNodeContent"){
            processAction(fils[i],elem);
        }
    }
}


function processAction(elem,root){
    if(elem.style.display == "none"){
        elem.style.display = "block";
        root.style.backgroundImage = "url(pics/collapse.png)";
    }else{
        elem.style.display = "none";
        root.style.backgroundImage = "url(pics/expand.png)";
    }
}

var selectedNode=null;

function selectBoxNode(elem){
    while((block = elem.parentNode.parentNode)==null){
        
    }
    if(selectedNode!=null){
        processCommandForSelection(selectedNode,"hide");
        var selectedNodeParent = selectedNode.parentNode.parentNode
//        selectedNodeParent.style.backgroundColor = "transparent";
        selectedNodeParent.style.background ="none";
        selectedNodeParent.style.borderColor = "transparent";
    }
    if(elem==selectedNode){
        selectedNode=null;
        return;
    }
    //    block.style.backgroundColor = "#ececec";
    block.style.background ="url(pics/back-selection.png) repeat-x scroll 0% 0% #e6e6e6";
    block.style.borderColor = "darkgray";
    processCommandForSelection(elem,"show");
    selectedNode=elem;
}

function processCommandForSelection(elem,action){
    var block = elem.parentNode;
    var fils = block.childNodes;
    var nbFils = fils.length;
    for(var i=0;i<nbFils;i++){
        if(fils[i].className=="nodeCommand"){
            if(action == "show")
                fils[i].style.display = "block";
            if(action == "hide")
                fils[i].style.display = "none";
        }
    }
}
