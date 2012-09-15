/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.editor;

import de.huberlin.informatik.pnk.app.base.MetaJFrame;
import java.awt.BorderLayout;
import javax.swing.*;

/**
 * EditorWindow
 *
 * Copied from de.huberlin.informatik.pnk.editor.EditorWindow
 */
public class EditorWindow extends MetaJFrame {

    private Editor editor;
    private JToolBar toolbar;
    private JTextField textfield;
    private JSplitPane splitpane;
    private JScrollPane viewscrollpane;
    private JPanel viewpanes;

    public EditorWindow(Editor editor) {
        super(editor, "Editor");
        this.editor = editor;
    }

    JSplitPane getSplitpane() {
        return splitpane;
    }

    void setNet() {
        //open the editorwindow
        this.setSize(900, 600);
        this.getContentPane().setLayout(new BorderLayout());
        this.toolbar = new JToolBar(JToolBar.VERTICAL);
        this.getContentPane().add(this.toolbar, BorderLayout.WEST);
        this.textfield = new JTextField();
        this.textfield.setEditable(false);
        this.getContentPane().add(this.textfield, BorderLayout.SOUTH);

        this.viewpanes = new JPanel();
        this.viewpanes.setLayout(new BoxLayout(this.viewpanes, BoxLayout.Y_AXIS));
        this.viewpanes.add(Box.createVerticalStrut(4));
        this.viewscrollpane = new JScrollPane(this.viewpanes);

        // Insert a first page in this editorwindow
        Page page;
        PageVector pagevector = this.editor.getPagevector();
        if (!pagevector.isEmpty()) {
            page = (Page) pagevector.get(0);
        } else {
            page = pagevector.createPage();
            page.open();
        }

        this.splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, page.getScrollpane(), this.viewscrollpane);
        this.splitpane.setDividerSize(3);
        this.getContentPane().add(this.splitpane, BorderLayout.CENTER);

        //this.pack();
        this.setVisible(true);
    }

    void close() {
        dispose();
    }
} // EditorWindow
