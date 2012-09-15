package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.util.*;

/**
 * Label.java
 *
 * Text shown on an editors page. For example an annotation
 * or an extension of a netobject.
 *
 * Created: Wed Dec 27 16:11:25 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class Label extends Sprite {
    int drag_size = 10;
    int buff = 5;

    /*
     * Store FontMetrics, so size can be
     * calculated dynamicaly.
     */
    private FontMetrics fm = null;

    /*
     * Allows to identify the type of this label
     */
    private String id;

    /*
     * Text that is represented by this label
     */
    private String value;

    /*
     * class constructor
     *
     * This label will always be positioned relative to
     * his parent-sprite-object. The id-string allows to
     * identify this label. The value-string is the text
     * that will be drawn on page.
     */
    protected Label(Sprite parent, Point position, Dimension size, FontMetrics fm, String id, String value) {
        super(parent, position, size);
        this.id = id;
        this.fm = fm;
        this.setValue(value);
    }

    /**
     * Get the value of id.
     * @return Value of id.
     */
    protected String getId() {
        return id;
    }

    /**
     * Get the value of value.
     * @return Value of value.
     */
    protected String getValue() {
        return value;
    }

    void print(Graphics g) {
        if (!this.getVisible()) return;

        Graphics2D g2 = (Graphics2D)g;

        g2.setPaint(java.awt.Color.black);

        if ((this.value == null) ||
            (this.value.length() == 0)) return;

        Rectangle r = getBounds();
        int x = r.x + buff;
        int y = r.y + fm.getHeight() + buff;

        StringTokenizer st = new StringTokenizer(this.value, "\n");
        while (st.hasMoreTokens()) {
            g2.drawString(st.nextToken(), x, y);
            y += fm.getHeight();
        }
    }

    /*
     * Draws the value of this label.
     */
    void paint(Graphics g) {
        super.paint(g); // very important, do not forget

        if (!this.getVisible()) return;

        if ((this.value == null) ||
            (this.value.length() == 0)) return;

        Rectangle r = getBounds();

        int x = r.x + buff;
        int y = r.y + fm.getHeight() + buff;
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(Props.EXTENSION_COLOR);

        if (isMouseOver()) {
            Color c = (Color)g2.getPaint();
            g2.setPaint(new Color(c.getRed() ^ 0x66,
                                  c.getGreen() ^ 0x00,
                                  c.getBlue() ^ 0x00));
        }

        StringTokenizer st = new StringTokenizer(this.value, "\n");
        while (st.hasMoreTokens()) {
            g2.drawString(st.nextToken(), x, y);
            y += fm.getHeight();
        }

        int drag = 4;
        g2.drawRect(r.x + (drag >> 1), r.y + (drag >> 1), drag, drag);
    }

    protected Rectangle getBounds() {
        if (value != null) {
            Point p = getPosition();
            int w = fm.stringWidth(value);
            StringTokenizer st = new StringTokenizer(this.value, "\n");
            int h = fm.getHeight() * st.countTokens();
            return new Rectangle(p.x - buff, p.y - buff, w + (1 << buff), h + (1 << buff));
        } else return null;
    }

    protected boolean contains(Point p) {
        if ((this.value == null) ||
            (this.value.length() == 0)) return false;

        Rectangle r = getBounds();
        Rectangle i = new Rectangle(r.x, r.y, drag_size, drag_size);
        return i.contains(p);
    }

    /**
     * Set the value of id.
     * @param v  Value to assign to id.
     */
    protected void setId(String v) {
        this.id = v;
    }

    /**
     * Set the value of value.
     * @param v  Value to assign to value.
     */
    protected void setValue(String v) {
        //remove whitespaces from begin and end
        v = v.trim();
        // change value of value
        this.value = v;
    }
} // Label
//  LocalWords:  getPaint
