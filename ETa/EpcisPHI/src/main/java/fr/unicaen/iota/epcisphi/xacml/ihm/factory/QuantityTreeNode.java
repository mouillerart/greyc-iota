/*
 *  This program is a part of the IoTa project.
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

public class QuantityTreeNode implements TreeNode {

    private Long minQuantity;
    private Long maxQuantity;
    private String id;
    private String groupID;
    private Module module;
    private List<TreeNode> children;

    public QuantityTreeNode(Long minQuantity, Long maxQuantity, String gID, Module module) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.id = getLabel();
        this.groupID = gID;
        this.module = module;
        this.children = new ArrayList<TreeNode>();
    }

    public Long getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Long maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Long getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Long minQuantity) {
        this.minQuantity = minQuantity;
    }

    @Override
    public String getLabel() {
        return minQuantity.toString() + " -> " + maxQuantity.toString();
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.quantityFilterNode;
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
