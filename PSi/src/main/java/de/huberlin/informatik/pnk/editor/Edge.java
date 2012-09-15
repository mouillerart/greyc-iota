package de.huberlin.informatik.pnk.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.*;

import java.lang.Math;

/**
 * Edge.java
 *
 * An edge between two nodes.
 *
 * Created: Mon Dec 25 09:32:37 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class Edge extends MemberSprite {
    MemberSpriteNode source;

    /**
     * Get the value of source.
     * @return value of source.
     */
    public MemberSpriteNode getSource() {return source; }

    /**
     * Set the value of source.
     * @param v  Value to assign to source.
     */
    public void setSource(MemberSpriteNode v) {this.source = v; }

    MemberSpriteNode target;

    /**
     * Get the value of target.
     * @return value of target.
     */
    public MemberSpriteNode getTarget() {return target; }

    /**
     * Set the value of target.
     * @param v  Value to assign to target.
     */
    public void setTarget(MemberSpriteNode v) {this.target = v; }

    /*
     * Creates an edge between nodes.
     * @param source first node, where edge comes from
     * @param target second node, where edge goes to
     */
    protected Edge(MemberSpriteNode source, MemberSpriteNode target) {
        super();

        this.source = source;
        this.target = target;
        this.source.subsprites.add(this);
        this.target.subsprites.add(this);
        // calculate position of this edge as middle
        Point p1 = getSource().getPosition();
        Point p2 = getTarget().getPosition();
        int x = (int)((p1.x + p2.x) >> 1);
        int y = (int)((p1.y + p2.y) >> 1);
        setPosition(new Point(x, y));
    }

    boolean contains(Point p) {
        //###Editor.msg("###line contains ");

        if (!super.contains(p))
            return false;
        Point pos = getPosition();
        Rectangle r =
            new Rectangle(pos.x - 3, pos.y - 3, 6, 6);
        return r.contains(p);

        /*        Point src = this.source.getPosition();
           Point trg = this.target.getPosition();
           Line2D line = new Line2D.Double(src.x,src.y,trg.x,trg.y);

           //###Editor.msg("### line contains "+(4 > line.ptLineDist(p.x,p.y)));

           return (4 > line.ptLineDist(p.x,p.y)); */
    }

    protected void delete() {
        super.delete();
        this.source.subsprites.remove(this);
        this.target.subsprites.remove(this);
    }

    /*
     * Overwrites getBounds() of sprite.
     * Calculate bounds dynamically.
     */
    Rectangle getBounds() {
        Point pos = getPosition();
        Rectangle r =
            new Rectangle(pos.x - 5, pos.y - 5, 10, 10);
        return gp().getBounds().union(r);

        /*
             int x,y,dx,dy;
             int buff = 10; // makes drawings nicer

             if((this.source == null) ||
                (this.target == null)) return null;

             //set position and size:
             Point srcpos = this.source.getPosition();
             Point trgpos = this.target.getPosition();
             srcpos = this.source.getBorderpoint(trgpos);
             trgpos = this.target.getBorderpoint(srcpos);
             if(srcpos.x < trgpos.x) x = srcpos.x;
             else x = trgpos.x;
             if(srcpos.y < trgpos.y) y = srcpos.y;
             else y = trgpos.y;
             dx = Math.abs(srcpos.x - trgpos.x);
             dy = Math.abs(srcpos.y - trgpos.y);
             return new Rectangle(x - (buff >> 1),
                                                      y - (buff >> 1),
                                                      dx  + buff,
                                                      dy + buff);*/
    }

    /*
     * Gets position of this edge, useful to
     * locate annotations and extensions.
     * @return middle of this edge
     */
    /*protected Point getPosition() {
            Point src = this.source.getPosition();
            Point trg = this.target.getPosition();
            int x = (src.x + trg.x) >> 1;
            int y = (src.y + trg.y) >> 1;
            return new Point(x,y);
       }*/

    void print(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(java.awt.Color.black);
        g2.draw(gp());
    }

    /*
     * Draws this edge.
     */
    void paint(Graphics g) {
        super.paint(g);         // important, do not forget

        Graphics2D g2 = (Graphics2D)g;
        GeneralPath gp = gp();

        if (this.getEmphasized()) g2.setPaint(emphasize_color);
        else g2.setPaint(Props.ARC_BORDER_COLOR);

        if (isMouseOver()) {
            Color c = (Color)g2.getPaint();
            g2.setPaint(new Color(c.getRed() ^ 0x66,
                                  c.getGreen() ^ 0x00,
                                  c.getBlue() ^ 0x00));
        }

        g2.draw(gp);
        Point p = getPosition();
        Rectangle2D r =
            new Rectangle2D.Double(p.x - 2, p.y - 2, 4, 4);
        g2.draw(r);
        /*
           //###Editor.msg("### edge paint");
           Graphics2D g2 = (Graphics2D) g;
           Point p1 = source.getPosition();
           Point p2 = target.getPosition();
           p1 = source.getBorderpoint(p2);
           p2 = target.getBorderpoint(p1);
           if(this.getSelected()) g2.setPaint(this.selectColor);
           else if(this.getEmphasized()) g2.setPaint(Color.green);
           else g2.setPaint(this.background_color);
           g2.drawLine(p1.x,p1.y,p2.x,p2.y);
         */
    }

    private GeneralPath gp() {
        Point p0 = getPosition();
        Point p1 = getSource().getBorderpoint(p0);
        Point p2 = getTarget().getBorderpoint(p0);
        GeneralPath gp = new GeneralPath();
        gp.moveTo(p1.x, p1.y);
        gp.quadTo(p0.x, p0.y, p2.x, p2.y);
        //gp.closePath();
        return gp;
    }
} // Edge
