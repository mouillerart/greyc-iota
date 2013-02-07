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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 */
public class ContextualMenu extends JPopupMenu implements ActionListener {

    private JMenuItem itemSpr;
    private File file;
    private JTree parent;
    private DefaultMutableTreeNode node;

    public ContextualMenu(File f, JTree parent, DefaultMutableTreeNode node) {
        super();
        this.file = f;
        this.node = node;
        this.parent = parent;
        itemSpr = new JMenuItem("supprimer");
        itemSpr.addActionListener(this);
        this.add(itemSpr);
    }

    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == itemSpr) {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, "Are you sure you want to delete the file \"" + file.getName() + "\"?")) {
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, "You are trying to delete a directory, all files under this directory will be deleted to.\nDo you wan't to continue?")) {
                        deleteDir(file);
                    } else {
                        return;
                    }
                }
                node.removeFromParent();
                parent.updateUI();
            }
        }
    }
}
