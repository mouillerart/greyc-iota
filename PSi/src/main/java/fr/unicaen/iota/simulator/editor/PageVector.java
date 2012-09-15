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

import java.util.ArrayList;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 * PageVector
 *
 * A net or a graph can be displayed on different pages.
 * This Vector represents a list of all these pages.
 *
 * Copied from de.huberlin.informatik.pnk.editor.PageVector
 */
class PageVector extends ArrayList<Page> {

    /**
     * Counts all pages, gives ids to pages.
     */
    private int pagecounter = 0;
    /**
     * Editor of this pagevector.
     */
    private Editor editor;

    protected PageVector(Editor editor) {
        super();
        this.editor = editor;
    }

    /**
     * Closes all pages. Sets pagecounter 0.
     */
    protected void close() {
        // Make a copy
        for (Page page : new ArrayList<Page>(this)) {
            page.close();
        }
        this.pagecounter = 0;
    }

    /**
     * Opens a new page.
     * @return created page
     */
    protected Page createPage() {
        int page_id = ++this.pagecounter;

        Page page = new Page(page_id, this.editor);
        this.add(page);

        // Register checkboxmenuitem of page in editormenu
        JMenu pagemenu = this.editor.getEditormenu().pagemenu;
        JCheckBoxMenuItem ckb = page.page_ckb;
        pagemenu.add(ckb);

        return page;
    }

    /**
     * Get the value of editor.
     * @return Value of editor.
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * Returns the Page with id pageId. If no such Page exists,
     * a new page is createted
     * @return created page
     */
    protected Page getPage(int pageId) {
        for (Page actPage : this) {
            if (actPage.getId() == pageId) {
                return actPage;
            }
        }
        Page toReturn;
        do {
            toReturn = this.createPage();
        } while (toReturn.getId() != pageId);
        return toReturn;
    }

    protected void repaint() {
        for (Page page : this) {
            page.repaint();
        }
    }

    /**
     * Set the value of editor.
     * @param v  Value to assign to editor.
     */
    public void setEditor(Editor v) {
        this.editor = v;
    }

    public void closePage(Page page) {
        this.remove(page);
    }
} // PageVector
