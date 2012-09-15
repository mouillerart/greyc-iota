/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.xacml.ihm.factory;

import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import fr.unicaen.iota.xacml.ihm.Module;
import fr.unicaen.iota.xacml.ihm.NodeType;
import fr.unicaen.iota.xacml.ihm.TreeNode;
import fr.unicaen.iota.xacml.policy.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RuleTreeNode implements TreeNode {

    private OneOrGlobalFunction function;
    protected String id;
    private List<TreeNode> children;
    private Module module;
    private String groupID;

    public String getId() {
        return id;
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
        if (function.getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_PERMIT)) {
            return "ACCEPT";
        } else if (function.getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_DENY)) {
            return "DENY";
        }
        return "";
    }

    @Override
    public NodeType getNodeType() {
        if (SCBizStepRule.RULEFILTER.equals(id)) {
            return NodeType.bizStepFilterGroupNode;
        } else if (SCEPCClassRule.RULEFILTER.equals(id)) {
            return NodeType.epcClassFilterGroupNode;
        } else if (SCEPCsRule.RULEFILTER.equals(id)) {
            return NodeType.epcFilterGroupNode;
        } else if (SCEventTimeRule.RULEFILTER.equals(id)) {
            return NodeType.eventTimeFilterGroupNode;
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
