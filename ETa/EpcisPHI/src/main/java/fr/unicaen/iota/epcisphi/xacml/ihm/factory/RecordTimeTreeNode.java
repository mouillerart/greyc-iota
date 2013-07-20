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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordTimeTreeNode implements TreeNode {

    private Date minDate;
    private Date maxDate;
    private List<TreeNode> children;
    private String groupID;
    private Module module;
    private String id;

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public RecordTimeTreeNode(Date minD, Date maxD, String gID, Module m) {
        maxDate = maxD;
        minDate = minD;
        this.id = getLabel();
        this.groupID = gID;
        this.module = m;
        this.children = new ArrayList<TreeNode>();
    }

    @Override
    public String getLabel() {
        Calendar calMin = Calendar.getInstance();
        calMin.setTime(minDate);
        Calendar calMax = Calendar.getInstance();
        calMax.setTime(maxDate);
        return String.format("%1$tm/%1$td/%1$tY -> %2$tm/%2$td/%2$tY", calMin, calMax);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.recordTimeFilterNode;
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
