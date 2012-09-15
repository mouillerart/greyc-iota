/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.epcisphi.xacml.ihm;

public class TreeFactory {

    private Mode mode;

    public TreeFactory(Mode mode) {
        this.mode = mode;
    }

    public String createTree(TreeNode treeNode) {
        StringBuilder result = new StringBuilder();
        String button = treeNode.getNodeType() == NodeType.policyNode && this.mode == Mode.Create_Mode
                ? "<a href=\"#\" onclick=\"expand(this);\" class=\"icon_tree expanderButton2\"></a>"
                : "<a href=\"#\" onclick=\"expand(this);\" class=\"icon_tree expanderButton\"></a>";
        String img = treeNode.getNodeType() == NodeType.userNode ? "user"
                : (treeNode.getNodeType().isFilter() ? "filter" : "");
        String desc = createTitleDescription(treeNode);
        String icon = "<div class=\"icon_tree icon_tree_" + img + "\">&nbsp;</div>";
        if (treeNode.getNodeType() == NodeType.policiesNode) {
            result.append("<div class=\"rootzone\" id=\"root");
            result.append(treeNode.getModule());
            result.append("\">\n");
        }
        result.append("<div class=\"TreeNodeBlock\"><div class=\"TreeNodeTitle ");
        result.append(treeNode.getNodeType());
        result.append("\">");
        result.append(treeNode.getNodeType().isExpandable() ? button : icon);
        result.append("<a href=\"#\" class=\"TreeNodeTitleInnerHtml\" onclick=\"selectBoxNode(this);\"><span class=\"TreeNodeTitleInnerHtmlDesc\">");
        result.append(desc);
        result.append("</span><span class=\"TreeNodeTitleInnerHtmlValue\">");
        result.append(treeNode.getLabel());
        result.append("</span></a>");
        result.append(createCommand(treeNode));
        result.append("</div>\n");
        if (!treeNode.getChildren().isEmpty()) {
            result.append("<div class=\"TreeNodeContent\" ");
            result.append((treeNode.getNodeType() == NodeType.policyNode && this.mode == Mode.Create_Mode) ? "style=\"display:none\" " : "");
            result.append(">\n");
            for (TreeNode node : treeNode.getChildren()) {
                result.append(createTree(node));
            }
            result.append("</div>\n");
        } else if (treeNode.getNodeType().isFilterGroup()
                || treeNode.getNodeType() == NodeType.usersNode
                || treeNode.getNodeType() == NodeType.policiesNode) {
            result.append("<div class=\"TreeNodeContent\">\n</div>\n");
        }
        result.append("</div>\n");
        if (treeNode.getNodeType() == NodeType.policiesNode) {
            result.append("</div>\n");
        }
        return result.toString();
    }

    private String createCommand(TreeNode treeNode) {
        String command_start = "<div class=\"nodeCommand\">";
        String command_end = "</div>";
        String switchPolicy = "<a class=\"switch\" href=\"#\" onclick=\"processSwitchPolicy('" + treeNode.getNodeType() + "','" + treeNode.getGroupID() + "','" + treeNode.getObjectID() + "',this,'" + treeNode.getModule() + "');\"></a>";
        String create = "<a class=\"add\" href=\"#\" onclick=\"processRequestCreate('" + treeNode.getNodeType() + "','" + treeNode.getGroupID() + "','" + treeNode.getObjectID() + "',this,'" + treeNode.getModule() + "');\"></a>";
        String remove = "<a class=\"remove\" href=\"#\" onclick=\"processRequestRemove('" + treeNode.getNodeType() + "','" + treeNode.getGroupID() + "','" + escapeRegExp(treeNode.getObjectID()) + "',this,'" + treeNode.getModule() + "');\"></a>";
        String update = "<a class=\"update\" href=\"#\" onclick=\"processRequestUpdate('" + treeNode.getNodeType() + "','" + treeNode.getGroupID() + "','" + treeNode.getObjectID() + "',this,'" + treeNode.getModule() + "');\"></a>";
        if (treeNode.getNodeType().isFilter()) {
            return command_start + remove + command_end;
        }
        if (treeNode.getNodeType().isFilterGroup()) {
            if (treeNode.getNodeType() == NodeType.methodFilterGroupNode) {
                return command_start + create + command_end;
            } else {
                return command_start + switchPolicy + create + command_end;
            }
        }
        if (treeNode.getNodeType() == NodeType.rulesNode) {
            return command_start + command_end;
        }
        if (treeNode.getNodeType() == NodeType.usersNode) {
            return command_start + switchPolicy + create + command_end;
        }
        if (treeNode.getNodeType() == NodeType.userNode) {
            return command_start + remove + command_end;
        }
        if (treeNode.getNodeType() == NodeType.policyNode) {
            return command_start + update + remove + command_end;
        }
        if (treeNode.getNodeType() == NodeType.policiesNode) {
            return command_start + create + command_end;
        }
        return command_start + remove + create + update + command_end;
    }

    private String createTitleDescription(TreeNode treeNode) {
        switch (treeNode.getNodeType()) {
            case usersNode:
                return "Users / default policy: ";
            case policyNode:
                return "Group name: ";
            case bizStepFilterGroupNode:
                return "Business Step Filters / default policy: ";
            case epcFilterGroupNode:
                return "EPC Filters / default policy: ";
            case eventTypeFilterGroupNode:
                return "Event Type Filters / default policy: ";
            case eventTimeFilterGroupNode:
                return "Event Time Filters / default policy: ";
            case recordTimeFilterGroupNode:
                return "Record Time Filters / default policy: ";
            case operationFilterGroupNode:
                return "Action Filters / default policy: ";
            case parentIdFilterGroupNode:
                return "Parent EPC Filters / default policy: ";
            case childEpcFilterGroupNode:
                return "Child EPC Filters / default policy: ";
            case quantityFilterGroupNode:
                return "Quantity Filters / default policy: ";
            case readPointFilterGroupNode:
                return "ReadPoint Filters / default policy: ";
            case bizLocFilterGroupNode:
                return "Business Location Filters / default policy: ";
            case dispositionFilterGroupNode:
                return "Disposition Filters / default policy: ";
            case rulesNode:
                return "Restricted filters";
            case policiesNode:
                return "Group list";
            case methodFilterGroupNode:
                return "Method filters";
            case methodFilterNode:
                return "Method: ";
            case userNode:
                return "User ID: ";
            case bizStepFilterNode:
                return "Business Step filter: ";
            case eventTimeFilterNode:
                return "Event period filter: ";
            case recordTimeFilterNode:
                return "Record period filter: ";
            case eventTypeFilterNode:
                return "Event Type filter: ";
            case epcFilterNode:
                return "EPC filter: ";
            case operationFilterNode:
                return "Action filter: ";
            case parentIdFilterNode:
                return "Parent EPC filter: ";
            case childEpcFilterNode:
                return "Child EPC filter: ";
            case quantityFilterNode:
                return "Quantity filter: ";
            case readPointFilterNode:
                return "Read Point filter: ";
            case bizLocFilterNode:
                return "Business Location filter: ";
            case dispositionFilterNode:
                return "Disposition Location filter: ";
            default:
                return "";
        }
    }

    private String escapeRegExp(String objectID) {
        if (objectID == null) {
            return "null";
        }
        return objectID.replaceAll("\\\\", "\\\\\\\\");
    }
}
