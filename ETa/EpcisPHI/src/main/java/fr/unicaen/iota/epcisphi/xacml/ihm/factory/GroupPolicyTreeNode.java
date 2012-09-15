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
package fr.unicaen.iota.epcisphi.xacml.ihm.factory;

import fr.unicaen.iota.epcisphi.xacml.ihm.Module;
import fr.unicaen.iota.epcisphi.xacml.ihm.NodeType;
import fr.unicaen.iota.epcisphi.xacml.ihm.TreeNode;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import java.util.ArrayList;
import java.util.List;

public class GroupPolicyTreeNode implements TreeNode {

    private GroupPolicy groupPolicy;
    private List nodeChildren;
    private String groupID;
    private Module module;

    public GroupPolicy getGroupPolicy() {
        return groupPolicy;
    }

    public GroupPolicyTreeNode(GroupPolicy gp, String groupID, Module module) {
        groupPolicy = gp;
        this.groupID = groupID;
        this.module = module;
        this.nodeChildren = new ArrayList();
    }

    @Override
    public String getLabel() {
        return groupPolicy.getName();
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.policyNode;
    }

    @Override
    public List<TreeNode> getChildren() {
        return nodeChildren;
    }

    @Override
    public String getObjectID() {
        return groupID;
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
        nodeChildren.add(child);
    }
}
