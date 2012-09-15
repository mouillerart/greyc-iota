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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * ViewPane
 *
 * Copied from de.huberlin.informatik.pnk.editor.ViewPane
 */
public class ViewPane extends JPanel {

    private Page page;

    /**
     * Get the value of page.
     * @return value of page.
     */
    Page getPage() {
        return page;
    }

    /**
     * Set the value of page.
     * @param v  Value to assign to page.
     */
    void setPage(Page v) {
        this.page = v;
    }
    private static int VIEWPANE_WIDTH = 150;
    private static int VIEWPANE_HEIGHT = 150;
    private Component strut;

    ViewPane(Page page) {
        super();
        Editor.msg("§§§ new viewpane");

        this.page = page;
        this.setSize(VIEWPANE_WIDTH, VIEWPANE_HEIGHT);
        this.setPreferredSize(new Dimension(VIEWPANE_WIDTH, VIEWPANE_HEIGHT));
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                //
                // Put the page of this viewpane in
                // the Editorwindow
                Page _page = getPage();
                EditorWindow editorwindow = _page.getEditor().getEditorwindow();
                JSplitPane splitpane = editorwindow.getSplitpane();
                JScrollPane pagescrollpane = _page.getScrollpane();
                int dloc = splitpane.getDividerLocation();
                splitpane.setLeftComponent(pagescrollpane);
                splitpane.setDividerLocation(dloc);
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Dimension pagesize = this.page.getSize();
        double x = (double) this.getSize().width / (double) pagesize.width;
        double y = (double) this.getSize().height / (double) pagesize.height;

        Graphics2D g2 = (Graphics2D) g;
        g2.scale(x, y);

        // draw the page into this viewpane
        this.page.paint(g);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(VIEWPANE_WIDTH, VIEWPANE_HEIGHT);
    }

    public void remove() {
        Container parent = this.getParent();
        parent.remove(this.strut);
        parent.remove(this);
        parent.invalidate();
    }
} // ViewPane
