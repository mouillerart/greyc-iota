package de.huberlin.informatik.pnk.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;

import java.lang.Math;

/**
 * PageArc.java
 *
 *
 * Created: Wed Apr 18 23:13:02 2001
 *
 * @author Alexander Gruenewald
 * @version
 */

public class PlaceArc extends Edge {
    private int arrowSize = 8;

    /*
     * Class constructor.
     * @param source sourcenode/beginning  of this arc
     * @param target targetnode/ending of this arc
     */
    PlaceArc(MemberSpriteNode source, MemberSpriteNode target) {
        super(source, target);
    }

    private void drawArrow(Graphics g) {
        Point p1 = this.getPosition();
        Point p2 = this.target.getPosition();
        p2 = target.getBorderpoint(p1);
        Draw.arrow(p1, p2, this.arrowSize, g);
    }

    /*
     * Draws an edge with an arrow.
     */
    void paint(Graphics g) {
        super.paint(g);
        this.drawArrow(g);
    }
} // PlaceArc