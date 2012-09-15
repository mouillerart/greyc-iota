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
package fr.unicaen.iota.validator.gui;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeEditorPreferences extends DefaultTreeCellEditor {

    public TreeEditorPreferences(JTree tree, DefaultTreeCellRenderer renderer) {
        super(tree, renderer);
    }

    @Override
    protected boolean canEditImmediately(EventObject event) {
        return true;
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object obj, boolean isSelected, boolean expanded, boolean leaf, int row) {
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) obj;
        Object userObject = dmt.getUserObject();
        if (userObject instanceof PreferencesLeaf) {
            PreferencesLeaf pLeaf = (PreferencesLeaf) userObject;
            return new TreePanel(pLeaf.getText(), pLeaf.getIcon(), pLeaf.getCheckbox());
        }
        if (userObject instanceof PreferencesNode) {
            PreferencesNode pNode = (PreferencesNode) userObject;
            return new TreePanel(pNode.getText(), pNode.getIcon(), pNode.getStatusIcon());
        }
        return null;
    }
}
