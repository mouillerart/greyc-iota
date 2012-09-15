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

import fr.unicaen.iota.simulator.pnk.SpriteRepresentation;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Place
 *
 * A graphical representation of a place of a petrinet.
 *
 * Created from de.huberlin.informatik.pnk.editor.Place
 */
class Place extends MemberSpriteNode {

    private static Image imageNotFound = Toolkit.getDefaultToolkit().getImage("./pictures/nopicture.png");
    private String spriteRepresentation;
    private Image representation = null;
    private int imageWidth;
    private int imageHeight;

    protected Place(Point position, Dimension size) {
        super(position, size);
        imageWidth = this.getSize().width;
        imageHeight = this.getSize().height;
        de.huberlin.informatik.pnk.kernel.Place p = (de.huberlin.informatik.pnk.kernel.Place) this.getNetobject();
        if (p == null) {
            return;
        }
        spriteRepresentation = ((SpriteRepresentation) p.getExtension("representation")).getType();
        BufferedImage bi = Picture.toBufferedImage(representation);
        imageWidth = bi.getWidth();
        imageHeight = bi.getHeight();
    }

    /**
     * Edge needs to know borderpoint of this node.
     * @param p point, that tells where the edge comes
     * @return the point on the border of this node, where an edge begins or ends.
     */
    @Override
    Point getBorderpoint(Point p) {
        if (representation != null && !this.getAction()) {
            Point pos = this.getPosition();
            int width = (imageWidth >> 1);
            int height = (imageHeight >> 1);
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
        } else {
            Point pos = this.getPosition();
            Dimension size = this.getSize();
            int dx = p.x - pos.x;
            int dy = p.y - pos.y;
            double d = Math.sqrt((double) dx * dx + (double) dy * dy);
            return new Point((int) (pos.x + Math.round((double) dx / d * (size.width >> 1))),
                    (int) (pos.y + Math.round((double) dy / d * (size.height >> 1))));
        }
    }

    @Override
    void print(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = this.getPosition().x - (w >> 1);
        int y = this.getPosition().y - (h >> 1);

        // draw forground
        g2.setPaint(java.awt.Color.black);
        g2.drawOval(x, y, w, h);
    }

    @Override
    protected Rectangle getBounds() {
        Rectangle rectRespImg = null;
        if (representation != null) {
            Rectangle rect = super.getBounds();
            int w = imageWidth;
            int h = imageHeight;
            int x = this.getPosition().x - (w >> 1);
            int y = this.getPosition().y - (h >> 1);
            int xImage = x - ((imageWidth - w) >> 1);
            int yImage = y - ((imageHeight - h) >> 1);
            rectRespImg = new Rectangle(xImage, yImage, w, h);
        }
        Rectangle rectRespSprite = super.getBounds();
        if (rectRespImg == null) {
            return rectRespSprite;
        }
        return new Rectangle((int) Math.min(rectRespImg.getX(), rectRespSprite.getX()),
                (int) Math.min(rectRespImg.getY(), rectRespSprite.getY()),
                (int) Math.max(rectRespImg.getWidth(), rectRespSprite.getWidth()),
                (int) Math.max(rectRespImg.getHeight(), rectRespSprite.getHeight()));
    }

    /**
     * Draws this place.
     */
    @Override
    void paint(Graphics g) {
        super.paint(g); //very important, do not forget

        de.huberlin.informatik.pnk.kernel.Place p = (de.huberlin.informatik.pnk.kernel.Place) this.getNetobject();
        if (p == null) {
            return;
        }
        SpriteRepresentation sr = (SpriteRepresentation) p.getExtension("representation");
        if (!sr.getType().equals(spriteRepresentation)) {
            spriteRepresentation = sr.getType();
            if (!sr.getType().isEmpty()) {
                String path = "./pictures/" + sr.getType() + ".png";
                if (new File(path).exists()) {
                    representation = Toolkit.getDefaultToolkit().getImage(path);
                    BufferedImage bi = Picture.toBufferedImage(representation);
                    imageWidth = bi.getWidth();
                    imageHeight = bi.getHeight();
                } else {
                    representation = imageNotFound;
                }
            } else {
                representation = null;
            }
        }
        Graphics2D g2 = (Graphics2D) g;

        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = this.getPosition().x - (w >> 1);
        int y = this.getPosition().y - (h >> 1);

        boolean picture;
        // draw foreground
        if (this.getAction()) {
            picture = false;
            g2.setPaint(this.getActionColor());
        } else if (this.getSelected()) {
            picture = true;
            g2.setPaint(this.getSelectColor());
        } else if (this.getEmphasized()) {
            picture = true;
            g2.setPaint(this.getEmphasizeColor());
        } else {
            picture = true;
            g2.setPaint(Props.PLACE_FILL_COLOR);
        }
        if (isMouseOver()) {
            Color c = (Color) g2.getPaint();
            g2.setPaint(new Color(c.getRed() ^ 0x66,
                    c.getGreen() ^ 0x00,
                    c.getBlue() ^ 0x00));
        }

        if (!picture || sr.getType().isEmpty()) {
            g2.fillOval(x, y, w, h);
        } else {
            boolean result;
            do {
                int xImage = x - ((imageWidth - w) >> 1);
                int yImage = y - ((imageHeight - h) >> 1);
                result = g2.drawImage(representation, xImage, yImage, null);
            } while (!result);
            return;
        }

        // draw background
        g2.setPaint(Props.PLACE_BORDER_COLOR);
        if (this.getJoined()) {
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(x, y, w, h);
            g2.setStroke(oldStroke);
        } else {
            g2.drawOval(x, y, w, h);
        }
    }
}
