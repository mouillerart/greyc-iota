package de.huberlin.informatik.pnk.editor;

import java.awt.*;

/**
 * Transition.java
 *
 * A graphical representation of a transition of a petrinet.
 *
 * Created: Thu Dec 28 17:09:59 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class Transition extends MemberSpriteNode {
    protected Transition(Point position, Dimension size) {
        super(position, size);
    }

    /*
     * Edge needs to know borderpoint of this transition.
     * @param p point, that tells where the edge comes
     * @return the point on the border
     * of this transition, where an edge begins
     * or ends.
     */
    Point getBorderpoint(Point p) {
        Point pos = this.getPosition();
        Dimension dim = getSize();
        int width = (dim.width >> 1);
        int height = (dim.height >> 1);
        int dx = p.x - pos.x;
        int dy = p.y - pos.y;
        if (Math.abs(dx) > width || Math.abs(dy) > height) {
            if (dy >= Math.abs(dx)) {
                return new Point(pos.x + (int)(((double)dx * width) / dy),
                                 pos.y + height);
            } else if (-dy >= Math.abs(dx)) {
                return new Point(pos.x - (int)(((double)dx * width) / dy),
                                 pos.y - height);
            } else if (dx >= Math.abs(dy)) {
                return new Point(pos.x + width,
                                 pos.y + (int)(((double)dy * height) / dx));
            } else if (-dx >= Math.abs(dy)) {
                return new Point(pos.x - width,
                                 pos.y - (int)(((double)dy * height) / dx));
            } else return new Point(pos.x, pos.y);
        } else return new Point(pos.x, pos.y + height);
    }

    void print(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = this.getPosition().x - (w >> 1);
        int y = this.getPosition().y - (h >> 1);

        g2.setPaint(java.awt.Color.black);
        g2.drawRect(x, y, w, h);
    }

    /*
     * Draws this transition.
     */
    void paint(Graphics g) {
        super.paint(g);         //very important, do not forget

        Graphics2D g2 = (Graphics2D)g;
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = this.getPosition().x - (w >> 1);
        int y = this.getPosition().y - (h >> 1);

        // draw foreground
        if (this.getAction()) g2.setPaint(this.actionColor);
        else if (this.getSelected()) g2.setPaint(this.selectColor);
        //else g2.setPaint(new GradientPaint(x,y,this.foreground_color,x+w,y+h,Color.white));
        else if (this.getEmphasized()) g2.setPaint(emphasize_color);
        else g2.setPaint(Props.TRANSITION_FILL_COLOR);

        if (isMouseOver()) {
            Color c = (Color)g2.getPaint();
            g2.setPaint(new Color(c.getRed() ^ 0x66,
                                  c.getGreen() ^ 0x00,
                                  c.getBlue() ^ 0x00));
        }

        g2.fillRect(x, y, w, h);

        // draw background
        g2.setPaint(Props.TRANSITION_BORDER_COLOR);
        if (this.getJoined()) {
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRect(x, y, w, h);
            g2.setStroke(oldStroke);
        }
        g2.drawRect(x, y, w, h);
    }
} // Transition
