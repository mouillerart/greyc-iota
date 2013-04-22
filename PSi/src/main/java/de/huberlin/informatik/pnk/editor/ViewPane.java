package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * ViewPane.java
 *
 *
 * Created: Thu May 24 13:36:43 2001
 *
 * @author Alexander GrÃ¼newald
 * @version
 */

public class ViewPane extends JPanel {
    Page page;

    /**
     * Get the value of page.
     * @return value of page.
     */
    public Page getPage() {return page; }

    /**
     * Set the value of page.
     * @param v  Value to assign to page.
     */
    public void setPage(Page v) {this.page = v; }

    static int VIEWPANE_WIDTH = 150;
    static int VIEWPANE_HEIGHT = 150;

    protected Component strut;

    public ViewPane(Page page) {
        super();
        Editor.msg("§§§ new viewpane");

        this.page = page;
        this.setSize(VIEWPANE_WIDTH, VIEWPANE_HEIGHT);
        this.setPreferredSize(new Dimension(VIEWPANE_WIDTH, VIEWPANE_HEIGHT));
        this.addMouseListener(new MouseAdapter() {
                                  public void mouseClicked(MouseEvent e) {
                                      //
                                      // Put the page of this viewpane in
                                      // the Editorwindow
                                      Page _page =
                                          getPage();
                                      EditorWindow editorwindow =
                                          _page.getEditor().getEditorwindow();
                                      JSplitPane splitpane =
                                          editorwindow.getSplitpane();
                                      JScrollPane pagescrollpane =
                                          _page.scrollpane;
                                      int dloc = splitpane.getDividerLocation();
                                      splitpane.setLeftComponent(pagescrollpane);
                                      splitpane.setDividerLocation(dloc);
                                  }

                                  public void mouseEntered(MouseEvent e) {}
                                  public void mouseExited(MouseEvent e) {}
                                  public void mousePressed(MouseEvent e) {}
                                  public void mouseReleased(MouseEvent e) {}});
    }

    public void paint(Graphics g) {
        Dimension pagesize = this.page.getSize();
        double x = (double)this.getSize().width / (double)pagesize.width;
        double y = (double)this.getSize().height / (double)pagesize.height;

        Graphics2D g2 = (Graphics2D)g;
        //	Editor.msg("VIEWPANE scale x,y :"+x+","+y);

        g2.scale(x, y);

        // draw the page into this viewpane
        this.page.paint(g);
    }

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
