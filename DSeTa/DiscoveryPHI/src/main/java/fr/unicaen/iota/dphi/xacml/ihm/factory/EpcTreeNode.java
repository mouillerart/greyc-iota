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
package fr.unicaen.iota.dphi.xacml.ihm.factory;

import fr.unicaen.iota.dphi.xacml.ihm.Module;
import fr.unicaen.iota.dphi.xacml.ihm.NodeType;
import fr.unicaen.iota.dphi.xacml.ihm.TreeNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EpcTreeNode implements TreeNode {

    private String value;
    private String id;
    private String groupID;
    private Module module;
    private List<TreeNode> children;

    public EpcTreeNode(String v, String id, String gID, Module module) {
        value = v;
        this.id = id;
        this.groupID = gID;
        this.module = module;
        this.children = new ArrayList<TreeNode>();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getLabel() {
        return value;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.epcFilterNode;
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
        children.add(child);
    }
}