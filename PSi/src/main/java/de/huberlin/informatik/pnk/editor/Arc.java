package de.huberlin.informatik.pnk.editor;

import de.huberlin.informatik.pnk.kernel.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import java.lang.Math;
import java.lang.reflect.*;
import java.util.Vector;

/**
 * Arc.java
 *
 * An arc is an edge with an arrow.
 *
 * Created: Thu Dec 28 16:28:23 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class Arc extends Edge {
    private int arrowSize = 8;

    /*
     * Class constructor.
     * @param source sourcenode/beginning  of this arc
     * @param target targetnode/ending of this arc
     */
    Arc(MemberSpriteNode source, MemberSpriteNode target) {
        super(source, target);
    }

    private void drawArrow(Graphics g) {
        Point p1 = this.getPosition();
        Point p2 = this.target.getPosition();
        p2 = target.getBorderpoint(p1);
        Draw.arrow(p1, p2, this.arrowSize, g);
    }

    protected void drawArcDecoration(de.huberlin.informatik.pnk.kernel.Extension arcType, Graphics g) {
        MemberSpriteNode place = null;
        MemberSpriteNode transition = null;
        if (source.getNetobject() instanceof de.huberlin.informatik.pnk.kernel.Place) {
            place = source;
            transition = target;
        } else {
            place = target;
            transition = source;
        }
        Point p1 = getPosition();
        Point p2 = transition.getBorderpoint(p1);
        switch (getTestCond(arcType)) {
        case 0:     /*POT.OUT*/
            break;
        case 1:     /*POT.IN*/
            Draw.arrow(p1, p2, this.arrowSize, g);
            break;
        case 2:     /*NO*/
            Draw.dash(p1, p2, this.arrowSize, g);
            break;
        case 3:     /*EQUAL*/
            Draw.arrow3(p1, p2, this.arrowSize, g);
            break;
        case 4:     /*ZERO*/
            Draw.arrow2(p1, p2, this.arrowSize, g);
            break;
        case 5:     /*PUT-INHIB*/
            Draw.bobbel1(p1, p2, this.arrowSize, g);
            break;
        case 6:     /*INHIB*/
            Draw.bobbel2(p1, p2, this.arrowSize, g);
            break;
        }
        p1 = getPosition();
        p2 = place.getBorderpoint(p1);
        switch (getEffect(arcType)) {
        case 0:     /*TEST*/
            Draw.dash(p1, p2, this.arrowSize, g);
            break;
        case 1:     /*ADD*/
            Draw.arrow(p1, p2, this.arrowSize, g);
            break;
        case 2:     /*SUB*/
            break;
        case 3:     /*RESERVE*/
            Draw.arrow3(p1, p2, this.arrowSize, g);
            break;
        case 4:     /*SET*/
            Draw.arrow2(p1, p2, this.arrowSize, g);
            break;
        case 5:     /*CLEAR*/
            Draw.bobbel2(p1, p2, this.arrowSize, g);
            break;
        case 6:     /*CLEAR*/
            Draw.bobbel1(p1, p2, this.arrowSize, g);
            break;
        }
    }

    private boolean paintArcType(Graphics g) {
        try {
            de.huberlin.informatik.pnk.kernel.Edge edge = (de.huberlin.informatik.pnk.kernel.Edge)getNetobject();
            de.huberlin.informatik.pnk.kernel.Extension arcType = edge.getExtension("type");
            if (arcType != null) {
                drawArcDecoration(arcType, g);
                return true;
            }
        } catch (ClassCastException e) {}
        return false;
    }

    /*
     * Draws an edge with an arrow.
     */
    void paint(Graphics g) {
        super.paint(g);
        if (!paintArcType(g))
            this.drawArrow(g);
    }

    void print(Graphics g) {
        super.print(g);
        if (!paintArcType(g))
            this.drawArrow(g);
    }

    private int getTestCond(Object obj) {
        try {
            Class fctClass = Class.forName("de.huberlin.informatik.pnk.netElementExtensions.PNCube.ArcType");
            Method functionObject = fctClass.getMethod("getTestCond", null);
            return ((Integer)functionObject.invoke(obj, null)).intValue();
        } catch (Exception e) {}
        return -1;
    }

    private int getEffect(Object obj) {
        try {
            Class fctClass = Class.forName("de.huberlin.informatik.pnk.netElementExtensions.PNCube.ArcType");
            Method functionObject = fctClass.getMethod("getEffect", null);
            return ((Integer)functionObject.invoke(obj, null)).intValue();
        } catch (Exception e) {}
        return -1;
    }
} // Arc
