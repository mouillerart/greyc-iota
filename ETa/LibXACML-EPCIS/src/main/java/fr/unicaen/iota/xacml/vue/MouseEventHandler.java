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

public class MouseEventHandler extends MouseAdapter {

    private JTree parent;
    private AccessPolicyManagerVue root;
    private JPopupMenu popup;

    public MouseEventHandler(JTree p, AccessPolicyManagerVue a) {
        parent = p;
        root = a;
        init();
    }

    private void init() {
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

    private void jButtonAddActionPerformed(ActionEvent evt) {
        TreePath selPath = ((JTree) parent).getSelectionPath();
        if (selPath != null) {
            //A partir du chemin, on récupère le noeud
            lastNodeSelected = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            lastObjectSelected = lastNodeSelected.getUserObject();
            if (lastObjectSelected instanceof OwnerPoliciesTreeNode) {
            } else if (lastObjectSelected instanceof GroupPolicyTreeNode) {
            } else if (lastObjectSelected instanceof RuleTreeNode) {
                RuleTreeNode ruleTreeNode = (RuleTreeNode) lastObjectSelected;
                String id = ruleTreeNode.getId();
                if (SCBizStepRule.RULEFILTER.equals(id)) {
                    FilterBizStepInterface dialog = new FilterBizStepInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCEpcsRule.RULEFILTER.equals(id)) {
                    FilterEpcInterface dialog = new FilterEpcInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCEventTimeRule.RULEFILTER.equals(id)) {
                    FilterEventTimeInterface dialog = new FilterEventTimeInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCEventTypeRule.RULEFILTER.equals(id)) {
                    FilterEventTypeInterface dialog = new FilterEventTypeInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCParentIdRule.RULEFILTER.equals(id)) {
                    FilterParentIdInterface dialog = new FilterParentIdInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCChildEpcRule.RULEFILTER.equals(id)) {
                    FilterChildEpcInterface dialog = new FilterChildEpcInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCQuantityRule.RULEFILTER.equals(id)) {
                    FilterQuantityInterface dialog = new FilterQuantityInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCReadPointRule.RULEFILTER.equals(id)) {
                    FilterReadPointInterface dialog = new FilterReadPointInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCBizLocRule.RULEFILTER.equals(id)) {
                    FilterBizLocInterface dialog = new FilterBizLocInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCDispositionRule.RULEFILTER.equals(id)) {
                    FilterDispositionInterface dialog = new FilterDispositionInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCMasterDataIdRule.RULEFILTER.equals(id)) {
                    FilterMasterDataIdInterface dialog = new FilterMasterDataIdInterface(root, true);
                    dialog.setVisible(true);
                } else if (SCgroupRule.RULEFILTER.equals(id)) {
                    FilterUserInterface dialog = new FilterUserInterface(root, true);
                    dialog.setVisible(true);
                }
            } else if (lastObjectSelected instanceof RuleTreeNode) {
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
