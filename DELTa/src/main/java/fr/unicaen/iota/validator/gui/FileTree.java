/* @SLS Vérifier origine / modifications et copyright
 *
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Display a file system in a JTree view
 *
 * @version $Id: FileTree.java,v 1.9 2004/02/23 03:39:22 ian Exp $
 * @author Ian Darwin
 */
public class FileTree extends JPanel implements MouseListener {

    /** Construct a FileTree */
    private JTabbedPane panelStats;
    private JTree tree;
    private HashMap<String, Integer> tabs = new HashMap<String, Integer>();
    private JFrame parent;

    public FileTree(File dir, JTabbedPane statsContainer, JFrame parent) {
        panelStats = statsContainer;
        setLayout(new BorderLayout());

        // Make a tree list with all the nodes, and make it a JTree
        tree = new JTree(addNodes(null, dir));
        tree.addMouseListener(this);
        // Add a listener
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
            }
        });

        // Lastly, put the JTree into a JScrollPane.
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);
        add(BorderLayout.CENTER, scrollpane);
        tree.setRootVisible(false);
        sortTree(tree);
        tree.setCellRenderer(new StatTreeRenderer());
        this.parent = parent;

    }

    /** Add nodes from under "dir" into curTop. Highly recursive. */
    DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
        String curPath = dir.getPath();
        File tmpFile = new File(curPath);
        DefaultMutableTreeNode curDir = new AnalyseTreeNode(tmpFile.getName(), tmpFile, createImageIcon("resources/pics/folderstat2.png", ""), AnalyseTreeNode.NODE_TYPE);
        if (curTop != null) { // should only be null at root
            curTop.add(curDir);
        }
        Vector ol = new Vector();
        String[] tmp = dir.list();
        for (int i = 0; i < tmp.length; i++) {
            ol.addElement(tmp[i]);
        }
        Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
        File f;
        Vector files = new Vector();
        // Make two passes, one for Dirs and one for Files. This is #1.
        for (int i = 0; i < ol.size(); i++) {
            String thisObject = (String) ol.elementAt(i);
            String newPath;
            if (curPath.equals(".")) {
                newPath = thisObject;
            } else {
                newPath = curPath + File.separator + thisObject;
            }
            if ((f = new File(newPath)).isDirectory()) {
                addNodes(curDir, f);
            } else {
                files.addElement(newPath);
            }
        }
        // Pass two: for files.
        for (int fnum = 0; fnum < files.size(); fnum++) {
            File f1 = new File((String) files.elementAt(fnum));
            curDir.add(new AnalyseTreeNode(f1.getName(), f1, createImageIcon("resources/pics/stats.png", ""), AnalyseTreeNode.LEAF_TYPE));
        }
        return curDir;
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(200, 400);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 400);
    }

    private void sortTree(JTree tree) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        List<AnalyseTreeNode> list = new ArrayList<AnalyseTreeNode>();
        for (int i = 0; i < root.getChildCount(); i++) {
            list.add((AnalyseTreeNode) root.getChildAt(i));
        }
        Collections.sort(list);
        root.removeAllChildren();
        for (TreeNode tn : list) {
            root.add((MutableTreeNode) tn);
        }
        tree.updateUI();
    }

    protected ImageIcon createImageIcon(String img, String description) {
        return new ImageIcon(img, description);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if (SwingUtilities.isRightMouseButton(evt)) {
            int selRow = tree.getRowForLocation(evt.getX(), evt.getY());
            if (tree.getSelectionCount() > 0) {
                for (int i : tree.getSelectionRows()) {
                    tree.removeSelectionRow(i);
                }
            }
            tree.addSelectionRow(selRow);
            AnalyseTreeNode node = (AnalyseTreeNode) tree.getLastSelectedPathComponent();
            ContextualMenu menu = new ContextualMenu(node.getFile(), tree, node);
            menu.show(tree, evt.getX(), evt.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            AnalyseTreeNode node = (AnalyseTreeNode) tree.getLastSelectedPathComponent();
            if (node.getType() != AnalyseTreeNode.LEAF_TYPE) {
                return;
            }
            if (tabs.containsKey(node.getFile().getAbsolutePath())) {
                int index = tabs.get(node.getFile().getAbsolutePath());
                panelStats.setSelectedIndex(index);
                return;
            }
            JScrollPane sp = new JScrollPane();
            JPanel pan = new JPanel();
            pan.setLayout(new BorderLayout());
            pan.add(new StatPanel(node.getFile(), parent), BorderLayout.CENTER);
            panelStats.addTab(node.getFile().getName(), pan);
            panelStats.updateUI();
            panelStats.setSelectedIndex(panelStats.getTabCount() - 1);
            tabs.put(node.getFile().getAbsolutePath(), panelStats.getTabCount() - 1);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
