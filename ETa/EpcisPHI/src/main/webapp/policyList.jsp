
<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.Module"%>
<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.factory.AccessPolicies"%>
<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.Mode"%>
<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.TreeFactory"%>
<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.factory.Policies"%>
<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.NodeType"%>
<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.factory.Node"%>
<%@page import="java.util.ArrayList"%>
<%@page import="fr.unicaen.iota.epcisphi.xacml.ihm.TreeNode"%>
<%@page import="java.util.List"%>
<%@page import="java.util.LinkedList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<script language="javascript">
    $(document).ready(function() {
        $("#tabs").tabs();
        loadPolicyTrees();

    });
</script>

<div id="tabs">
    <ul>
        <li><div class="titleWrapper"><a id="fragment-<%=Module.queryModule%>" href="#fragment-1"><span>Query Module Policy</span></a><a id="fragment-<%=Module.queryModule%>-control-valid" class="tabControl" href="#" onclick="processRequestSave('<%=Module.queryModule%>')" ><span></span></a><a id="fragment-<%=Module.queryModule%>-control-cancel" class="tabControlCancel" href="#" onclick="processRequestCancelPolicyChanges('<%=Module.queryModule%>')" ><span></span></a></div></li>
        <li><div class="titleWrapper"><a id="fragment-<%=Module.adminModule%>" href="#fragment-2"><span>Admin Module Policy</span></a><a id="fragment-<%=Module.adminModule%>-control-valid"  class="tabControl" href="#" onclick="processRequestSave('<%=Module.adminModule%>')" ><span></span></a><a id="fragment-<%=Module.adminModule%>-control-cancel" class="tabControlCancel" href="#" onclick="processRequestCancelPolicyChanges('<%=Module.adminModule%>')" ><span></span></a></div></li>
        <li><div class="titleWrapper"><a id="fragment-<%=Module.captureModule%>" href="#fragment-3"><span>CaptureModulePolicy</span></a><a id="fragment-<%=Module.captureModule%>-control-valid"  class="tabControl" href="#" onclick="processRequestSave('<%=Module.captureModule%>')" ><span></span></a><a id="fragment-<%=Module.captureModule%>-control-cancel" class="tabControlCancel" href="#" onclick="processRequestCancelPolicyChanges('<%=Module.captureModule%>')" ><span></span></a></div></li>
    </ul>
    <div id="fragment-1" class="TreeTabContent">
        <div class="PolicyTree">
            <div id="treeQueryModule">&nbsp;</div>
        </div>
    </div>
    <div id="fragment-2" class="TreeTabContent">
        <div class="PolicyTree">
            <div id="treeAdminModule">&nbsp;</div>
        </div>
    </div>
    <div id="fragment-3" class="TreeTabContent">
        <div class="PolicyTree">
            <div id="treeCaptureModule">&nbsp;</div>
        </div>
    </div>
</div>
