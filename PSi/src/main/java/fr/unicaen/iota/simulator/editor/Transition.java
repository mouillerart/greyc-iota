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
import java.awt.image.ImageObserver;

/**
 * Transition
 *
 * A graphical representation of a transition of a petrinet.
 *
 * Created after de.huberlin.informatik.pnk.editor.Transition
 */
class Transition extends MemberSpriteNode implements ImageObserver {

    protected Transition(Point position, Dimension size) {
        super(position, size);
    }

    /**
     * Edge needs to know borderpoint of this transition.
     *
     * @param p point, that tells where the edge comes
     * @return the point on the border of this transition, where an edge begins
     * or ends.
     */
    @Override
    Point getBorderpoint(Point p) {
        Point pos = this.getPosition();
        Dimension dim = getSize();
        int width = (dim.width >> 1);
        int height = (dim.height >> 1);
        int dx = p.x - pos.x;
        int dy = p.y - pos.y;
        if (Math.abs(dx) > width || Math.abs(dy) > height) {
            if (dy >= Math.abs(dx)) {
                return new Point(pos.x + (int) (((double) dx * width) / dy), pos.y + height);
            } else if (-dy >= Math.abs(dx)) {
                return new Point(pos.x - (int) (((double) dx * width) / dy), pos.y - height);
            } else if (dx >= Math.abs(dy)) {
                return new Point(pos.x + width, pos.y + (int) (((double) dy * height) / dx));
            } else if (-dx >= Math.abs(dy)) {
                return new Point(pos.x - width, pos.y - (int) (((double) dy * height) / dx));
            } else {
                return new Point(pos.x, pos.y);
            }
        } else {
            return new Point(pos.x, pos.y + height);
        }
    }

    @Override
    void print(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = this.getPosition().x - (w >> 1);
        int y = this.getPosition().y - (h >> 1);

        g2.setPaint(java.awt.Color.black);
        g2.drawRect(x, y, w, h);
    }

    private static final int LAST_ANIM = 6;
    private static int initAnim = 0;
    private Image imgFullAnim = Toolkit.getDefaultToolkit().getImage("./pictures/reader_full_anim.gif");
    private Image imgEmpty = Toolkit.getDefaultToolkit().getImage("./pictures/reader_empty_40.png");
    private Image imgFull = Toolkit.getDefaultToolkit().getImage("./pictures/reader_full_40.gif");

    /**
     * Draws this transition.
     */
    @Override
    void paint(Graphics g) {
        super.paint(g); //very important, do not forget

        Graphics2D g2 = (Graphics2D) g;

        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = this.getPosition().x - (w >> 1);
        int y = this.getPosition().y - (h >> 1);

        boolean picture = true;
        boolean pictureFull = false;

        // draw foreground
        if (this.getAction()) {
            picture = false;
            g2.setPaint(this.getActionColor());
        } else if (this.getSelected()) {
            picture = false;
            g2.setPaint(this.getSelectColor());
        } //else g2.setPaint(new GradientPaint(x,y,this.foreground_color,x+w,y+h,Color.white));
        else if (this.getEmphasized()) {
            pictureFull = true;
            g2.setPaint(this.getEmphasizeColor());
        } else {
            g2.setPaint(Props.TRANSITION_FILL_COLOR);
        }

        if (isMouseOver()) {
            Color c = (Color) g2.getPaint();
            g2.setPaint(new Color(c.getRed() ^ 0x66,
                    c.getGreen() ^ 0x00,
                    c.getBlue() ^ 0x00));
        }
        boolean result;
        if (picture) {
            if (pictureFull) {
                if (Config.animated) {
                    g2.drawImage(imgFullAnim, x, y, this);
                    if (Transition.initAnim == LAST_ANIM) {
                        imgFullAnim.flush();
                        Transition.initAnim = 0;
                    }
                    Transition.initAnim++;
                } else {
                    g2.drawImage(imgFull, x, y, this);
                }
            } else {
                do {
                    result = g2.drawImage(imgEmpty, x, y, null);
                } while (!result);
            }
        } else {
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
    }

    public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        if (Config.animated) {
            if (arg1 == ImageObserver.FRAMEBITS) {
                getPage().repaint(getUpdatearea());
            }
        } else {
            getPage().repaint(getUpdatearea());
        }
        return true;
    }
} // Transition
