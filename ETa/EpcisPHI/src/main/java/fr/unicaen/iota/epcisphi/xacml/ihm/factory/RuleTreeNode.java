/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.epcisphi.xacml.ihm.factory;

import fr.unicaen.iota.epcisphi.xacml.ihm.Module;
import fr.unicaen.iota.epcisphi.xacml.ihm.NodeType;
import fr.unicaen.iota.epcisphi.xacml.ihm.TreeNode;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import fr.unicaen.iota.xacml.policy.*;
import java.util.ArrayList;
import java.util.List;

public class RuleTreeNode implements TreeNode {

    private OneOrGlobalFunction function;
    private String id;
    private List<TreeNode> children;
    private Module module;
    private String groupID;

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public OneOrGlobalFunction getFunction() {
        return function;
    }

    public RuleTreeNode(OneOrGlobalFunction f, String groupID, Module m) {
        this.function = f;
        this.groupID = groupID;
        this.module = m;
        this.children = new ArrayList<TreeNode>();
    }

    @Override
    public String getLabel() {
        if (OneOrGlobalFunction.NAME_GLOBAL_PERMIT.equals(function.getFunctionName())) {
            return "ACCEPT";
        } else if (OneOrGlobalFunction.NAME_GLOBAL_DENY.equals(function.getFunctionName())) {
            return "DENY";
        }
        return "";
    }

    @Override
    public NodeType getNodeType() {
        if (SCBizStepRule.RULEFILTER.equals(id)) {
            return NodeType.bizStepFilterGroupNode;
        } else if (SCEventTypeRule.RULEFILTER.equals(id)) {
            return NodeType.eventTypeFilterGroupNode;
        } else if (SCEpcsRule.RULEFILTER.equals(id)) {
            return NodeType.epcFilterGroupNode;
        } else if (SCEventTimeRule.RULEFILTER.equals(id)) {
            return NodeType.eventTimeFilterGroupNode;
        } else if (SCRecordTimeRule.RULEFILTER.equals(id)) {
            return NodeType.recordTimeFilterGroupNode;
        } else if (SCOperationRule.RULEFILTER.equals(id)) {
            return NodeType.operationFilterGroupNode;
        } else if (SCParentIdRule.RULEFILTER.equals(id)) {
            return NodeType.parentIdFilterGroupNode;
        } else if (SCChildEpcRule.RULEFILTER.equals(id)) {
            return NodeType.childEpcFilterGroupNode;
        } else if (SCQuantityRule.RULEFILTER.equals(id)) {
            return NodeType.quantityFilterGroupNode;
        } else if (SCReadPointRule.RULEFILTER.equals(id)) {
            return NodeType.readPointFilterGroupNode;
        } else if (SCBizLocRule.RULEFILTER.equals(id)) {
            return NodeType.bizLocFilterGroupNode;
        } else if (SCDispositionRule.RULEFILTER.equals(id)) {
            return NodeType.dispositionFilterGroupNode;
        } else if (SCgroupRule.RULEFILTER.equals(id)) {
            return NodeType.usersNode;
        }
        return null;
    }

    @Override
    public List<TreeNode> getChildren() {
        return children;
    }

    @Override
    public String getObjectID() {
        return id;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public String getGroupID() {
        return groupID;
    }

    @Override
    public void addChild(TreeNode child) {
        this.children.add(child);
    }
}
