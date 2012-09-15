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

import fr.unicaen.iota.simulator.util.Config;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * Edge
 *
 * An edge between two nodes.
 *
 * Copied from de.huberlin.informatik.pnk.editor.Edge
 */
class Edge extends MemberSprite {

    private MemberSpriteNode source;
    private MemberSpriteNode target;
    protected float strokeSize;
    protected Stroke stroke;

    /**
     * Get the value of source.
     * @return value of source.
     */
    public MemberSpriteNode getSource() {
        return source;
    }

    /**
     * Set the value of source.
     * @param v  Value to assign to source.
     */
    public void setSource(MemberSpriteNode v) {
        this.source = v;
    }

    /**
     * Get the value of target.
     * @return value of target.
     */
    public MemberSpriteNode getTarget() {
        return target;
    }

    /**
     * Set the value of target.
     * @param v  Value to assign to target.
     */
    public void setTarget(MemberSpriteNode v) {
        this.target = v;
    }

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
        int x = (int) ((p1.x + p2.x) >> 1);
        int y = (int) ((p1.y + p2.y) >> 1);
        setPosition(new Point(x, y));
    }

    @Override
    boolean contains(Point p) {
        if (!super.contains(p)) {
            return false;
        }
        Point pos = getPosition();
        Rectangle r =
                new Rectangle(pos.x - 3, pos.y - 3, 6, 6);
        return r.contains(p);
    }

    @Override
    protected void delete() {
        super.delete();
        this.source.subsprites.remove(this);
        this.target.subsprites.remove(this);
    }

    /**
     * Overwrites getBounds() of sprite. Calculate bounds dynamically.
     */
    @Override
    protected Rectangle getBounds() {
        Point pos = getPosition();
        Rectangle r = new Rectangle(pos.x - 5, pos.y - 5, 10, 10);
        Rectangle rect = gp().getBounds();
        int expand = 10;
        Rectangle rect2 = new Rectangle((int) rect.getX() - expand, (int) rect.getY() - expand, (int) rect.getWidth() + (2 * expand), (int) rect.getHeight() + (2 * expand));
        return rect2.union(r);
    }

    @Override
    void print(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(java.awt.Color.black);
        g2.draw(gp());
    }

    /**
     * Draws this edge.
     */
    @Override
    void paint(Graphics g) {
        super.paint(g); // important, do not forget

        Graphics2D g2 = (Graphics2D) g;
        GeneralPath gp = gp();
        if (this.getEmphasized()) {
            g2.setPaint(this.getEmphasizeColor());
        } else {
            g2.setPaint(Props.ARC_BORDER_COLOR);
        }
        if (isMouseOver()) {
            Color c = (Color) g2.getPaint();
            g2.setPaint(new Color(c.getRed() ^ 0x66,
                    c.getGreen() ^ 0x00,
                    c.getBlue() ^ 0x00));
        }
        if (stroke == null) {
            g2.draw(gp);
        } else {
            g2.fill(stroke.createStrokedShape(gp));
        }

        if (Config.attachPoint) {
            Point p = getPosition();
            Rectangle2D r = new Rectangle2D.Double(p.x - 2, p.y - 2, 4, 4);
            g2.draw(r);
        }
    }

    private GeneralPath gp() {
        Point p0 = getPosition();
        Point p1 = getSource().getBorderpoint(p0);
        Point p2 = getTarget().getBorderpoint(p0);
        GeneralPath gp = new GeneralPath();
        gp.moveTo(p1.x, p1.y);
        gp.quadTo(p0.x, p0.y, p2.x, p2.y);
        return gp;
    }
} // Edge
