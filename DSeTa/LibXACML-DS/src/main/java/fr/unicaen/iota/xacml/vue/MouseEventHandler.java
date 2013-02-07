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
package fr.unicaen.iota.xacml.vue;

import fr.unicaen.iota.xacml.policy.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 */
public class MouseEventHandler extends MouseAdapter {

    private JTree parent;
    private AccessPolicyManagerVue root;
    private JPopupMenu popup;

    public MouseEventHandler(JTree p, AccessPolicyManagerVue a) {
        parent = p;
        root = a;
        init();
    }

    public void init() {
        popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Add");
        menuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        popup.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });
        popup.add(menuItem);
    }
    private Object lastObjectSelected;
    private DefaultMutableTreeNode lastNodeSelected;

    public Object getLastObjectSelected() {
        return lastObjectSelected;
    }

    public DefaultMutableTreeNode getLastNodeSelected() {
        return lastNodeSelected;
    }

    // TODO: awful code
    private void jButtonAddActionPerformed(ActionEvent evt) {
        TreePath selPath = parent.getSelectionPath();
        if (selPath != null) {
            // get the node from the path
            lastNodeSelected = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            lastObjectSelected = lastNodeSelected.getUserObject();
            if (lastObjectSelected instanceof OwnerPoliciesTreeNode) {
                // nothing
            } else if (lastObjectSelected instanceof GroupPolicyTreeNode) {
                // nothing
            } else if (lastObjectSelected instanceof RuleTreeNode) {
                RuleTreeNode ruleTreeNode = (RuleTreeNode) lastObjectSelected;
                String id = ruleTreeNode.getId();
                if (SCBizStepRule.RULEFILTER.equals(id)) {
                    FilterBizStepInterface dialog = new FilterBizStepInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCEPCClassRule.RULEFILTER.equals(id)) {
                    FilterEpcClassInterface dialog = new FilterEpcClassInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCEPCsRule.RULEFILTER.equals(id)) {
                    FilterEpcInterface dialog = new FilterEpcInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCEventTimeRule.RULEFILTER.equals(id)) {
                    FilterEventTimeInterface dialog = new FilterEventTimeInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCgroupRule.RULEFILTER.equals(id)) {
                    FilterUserInterface dialog = new FilterUserInterface(root, true);
                    dialog.setVisible(true);
                }
            } else if(lastObjectSelected instanceof RuleTreeNode){
                // nothing
            }
        }

    }

    private void jButtonDeleteActionPerformed(ActionEvent evt) {
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON3) {
            TreePath selPath = ((JTree) parent).getPathForLocation(me.getX(), me.getY());
            if (selPath != null) {
                //A partir du chemin, on récupère le noeud
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                if ((node != null) && (!node.isRoot())) {
                    ((JTree) parent).setSelectionPath(selPath);
                    maybeShowPopup(me);
                }
            }
        }
    }

    private void maybeShowPopup(MouseEvent e) {
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
}
