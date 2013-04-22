package de.huberlin.informatik.pnk.editor;

import java.util.*;
import javax.swing.*;

/**
 * PageVector.java
 *
 * A net or a graph can be displayed on different pages.
 * This Vector represents a list of all these pages.
 *
 * Created: Sat Dec 23 11:03:02 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class PageVector extends Vector {
    /*
     * Counts all pages, gives ids to pages.
     */
    private int pagecounter = 0;

    /*
     * Editor of this pagevector.
     */
    private Editor editor;

    protected PageVector(Editor editor) {
        super();
        this.editor = editor;
    }

    /*
     * Closes all pages. Sets pagecounter 0.
     */
    protected void close() {
        // Make a copy
        Vector pages = new Vector(this);
        for (int i = 0; i < pages.size(); i++) {
            Page page = (Page)pages.get(i);
            page.close();
        }
        this.pagecounter = 0;
    }

    /*
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

    /*
     * Returns the Page with id pageId. If no such Page exists,
     * a new page is createted
     * @return created page
     */
    protected Page getPage(int pageId) {
        //		int page_id = ++this.pagecounter;

        for (Enumeration e = this.elements(); e.hasMoreElements(); ) {
            Page actPage = (Page)e.nextElement();
            if (actPage.id == pageId)
                return actPage;
        }
        Page toReturn;
        do {
            toReturn = this.createPage();
        } while (toReturn.id != pageId);
        return toReturn;
    }

    protected void repaint() {
        for (int i = 0; i < this.size(); i++) {
            Page page = (Page) this.get(i);
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
        if (this.remove(page)) {
            if (this.isEmpty()) {
                //createPage();
            } else {
                // set another page
                // in the editorwindow
                //Page p = (Page) this.get(0);
                //EditorWindow w = editor.getEditorwindow();
                //JSplitPane s = w.getSplitpane();
                //s.setLeftComponent(p.scrollpane);
            }
        }
    }
} // PageVector
