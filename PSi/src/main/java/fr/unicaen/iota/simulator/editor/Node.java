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

/**
 * Node
 *
 * Sprite painted as node on page.
 *
 * Copied from de.huberlin.informatik.pnk.editor.Node
 */
class Node extends MemberSpriteNode {

    protected Node(Sprite parent, Point position, Dimension size) {
        super(parent, position, size);
    }

    protected Node(Point position, Dimension size) {
        super(position, size);
    }

    /**
     * Edge needs to know borderpoint of this node.
     * @param p point, that tells where the edge comes
     * @return the point on the border of this node, where an edge begins or ends.
     */
    @Override
    Point getBorderpoint(Point p) {
        Point pos = this.getPosition();
        Dimension size = this.getSize();
        int dx = p.x - pos.x;
        int dy = p.y - pos.y;
        double d = Math.sqrt((double) dx * dx + (double) dy * dy);
        return new Point((int) (pos.x + Math.round((double) dx / d * (size.width >> 1))),
                (int) (pos.y + Math.round((double) dy / d * (size.height >> 1))));
    }

    /**
     * Draws this place.
     */
    @Override
    void paint(Graphics g) {
        super.paint(g); //very important, do not forget

        Graphics2D g2 = (Graphics2D) g;
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = this.getPosition().x - (w >> 1);
        int y = this.getPosition().y - (h >> 1);

        // draw forground
        if (this.getAction()) {
            g2.setPaint(this.getActionColor());
        } else if (this.getSelected()) {
            g2.setPaint(this.getSelectColor());
        } //else g2.setPaint(new GradientPaint(x,y, ,x+w,y+h,Color.white));
        else if (this.getEmphasized()) {
            g2.setPaint(getEmphasizeColor());
        } else {
            g2.setPaint(Props.NODE_FILL_COLOR);
        }

        if (isMouseOver()) {
            Color c = (Color) g2.getPaint();
            g2.setPaint(new Color(c.getRed() ^ 0x66,
                    c.getGreen() ^ 0x00,
                    c.getBlue() ^ 0x00));
        }

        g2.fillOval(x, y, w, h);

        // draw background
        g2.setPaint(Props.NODE_BORDER_COLOR);
        if (this.getJoined()) {
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(x, y, w, h);
            g2.setStroke(oldStroke);
        } else {
            g2.drawOval(x, y, w, h);
        }
    }
} // Node
