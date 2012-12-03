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
import java.util.ArrayList;
import java.util.List;

public class Node implements TreeNode {

    private String value;
    private NodeType nodeType;
    private List<TreeNode> children;
    private String objectID;
    private Module module;
    private String groupID;

    public Node(String value, NodeType type, String id, Module module, String groupID) {
        this.value = value;
        this.nodeType = type;
        this.children = new ArrayList<TreeNode>();
        this.objectID = id;
        this.module = module;
        this.groupID = groupID;
    }

    @Override
    public void addChild(TreeNode node) {
        children.add(node);
    }

    @Override
    public String getLabel() {
        return value;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public List<TreeNode> getChildren() {
        return children;
    }

    @Override
    public String getObjectID() {
        return objectID;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public String getGroupID() {
        return groupID;
    }
}
