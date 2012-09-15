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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.StringTokenizer;

/**
 * 
 */
class Representation extends Sprite {

    private int drag_size = 10;
    private int buff = 5;
    private RepresentationType type = RepresentationType.TEXT;
    private Image imgageRepresentation;
    private int imageWidth;
    private int imageHeight;
    private javax.swing.Timer timer = new javax.swing.Timer((Config.animation_speed - (Config.animation_speed * 20 / 100)),
            new ActionListener() {

        @Override
                public void actionPerformed(ActionEvent ae) {
                    Representation.this.forceHighlight = false;
                    int dx = page.translation.x;
                    int dy = page.translation.y;
                    Rectangle updatearea = getBounds();
                    updatearea.translate(dx, dy);
                    page.repaint(updatearea);
                    Representation.this.timer.stop();
                }
            });
    /**
     * Store FontMetrics, so size can be
     * calculated dynamicaly.
     */
    private FontMetrics fm = null;
    /**
     * Allows to identify the type of this label
     */
    private String id;
    /**
     * Text that is represented by this label
     */
    private String value;
    private boolean forceHighlight = false;
    private double initialWidth;
    private double initialHeight;
    private Page page;

    /**
     * class constructor
     *
     * This label will always be positioned relative to his parent-sprite-object. The id-string allows to
     * identify this label. The value-string is the text that will be drawn on page.
     */
    protected Representation(Sprite parent, Point position, Dimension size, FontMetrics fm, String id, String value, Page p, String imagePath) {
        super(parent, position, size);
        this.id = id;
        this.fm = fm;
        initialWidth = size.getWidth();
        initialHeight = size.getHeight();
        page = p;
        this.setValue(value);
        if (imagePath != null) {
            setPicturePath(imagePath);
        }
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

    /**
     * Draws the value of this label.
     */
    @Override
    void paint(Graphics g) {
        super.paint(g); // very important, do not forge
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        Rectangle r = getBounds();

        if (!this.getVisible()) {
            return;
        }
        switch (getType()) {
            case TEXT:
                int x = r.x + buff;
                int y = r.y + buff;
                if (id.equals("marking")) {
                    if (isHightLighted() || Config.attachPoint || forceHighlight) {
                        drawMarking(g2, x, y, r);
                        this.timer.start();
                    }
                    return;
                }
                if ((this.value == null)
                        || (this.value.length() == 0)) {
                    return;
                }
                g2.setPaint(Props.EXTENSION_COLOR);

                if (isMouseOver()) {
                    Color c = (Color) g2.getPaint();
                    g2.setPaint(new Color(c.getRed() ^ 0x66,
                            c.getGreen() ^ 0x00,
                            c.getBlue() ^ 0x00));
                }
                StringTokenizer st = new StringTokenizer(this.value, "\n");
                Rectangle rBounds = getBounds();
                g2.setColor(isMouseOver() ? Color.GREEN : Color.WHITE);
                g2.fillRoundRect((int) rBounds.getX() + 1, (int) rBounds.getY() + 1, (int) rBounds.getWidth() - 2, (int) rBounds.getHeight() - 2, 10, 10);
                if (isMouseOver()) {
                    g2.setColor(Color.GREEN);
                } else {
                    g2.setPaint(Props.EXTENSION_COLOR);
                }
                g2.drawRoundRect((int) rBounds.getX() + 1, (int) rBounds.getY() + 1, (int) rBounds.getWidth() - 2, (int) rBounds.getHeight() - 2, 10, 10);
                if (isMouseOver()) {
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setPaint(Props.EXTENSION_COLOR);
                }
                while (st.hasMoreTokens()) {
                    g2.drawString(st.nextToken(), x, y + (fm.getHeight() - fm.getHeight() / 4));
                    y += fm.getHeight();
                }
                if (Config.attachPoint) {
                    int drag = 4;
                    g2.drawRect(r.x + (drag >> 1), r.y + (drag >> 1), drag, drag);
                }
                break;
            case PICTURE:
                boolean result;
                do {
                    result = g2.drawImage(imgageRepresentation, r.x, r.y, null);
                } while (!result);
                break;
            default:
                break;
        }
    }

    @Override
    protected Rectangle getBounds() {
        if (imgageRepresentation != null) {
            Point p = getPosition();
            return new Rectangle(p.x - buff, p.y - buff, imageWidth + 2 * buff, imageHeight + 2 * buff);
        }
        if (id != null && "marking".equals(id)) {
            Point p = getPosition();
            return new Rectangle(p.x - buff, p.y - buff, getSize().width + 2 * buff, getSize().height + 2 * buff);
        }
        if (value != null) {
            Point p = getPosition();
            int w = 0;
            for (String s : value.split("\n")) {
                int w2 = fm.stringWidth(s);
                w = Math.max(w, w2);
            }
            StringTokenizer st = new StringTokenizer(this.value, "\n");
            int h = fm.getHeight() * st.countTokens();
            return new Rectangle(p.x - buff, p.y - buff, w + 2 * buff, h + 2 * buff);
        } else {
            return null;
        }
    }

    @Override
    protected boolean contains(Point p) {
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
        if (!v.equals(this.value) && this.value != null && v.length() > this.value.length()) {
            forceHighlight = true;
        }
        // change value of value
        this.value = v;
    }

    public RepresentationType getType() {
        return type;
    }

    public void setType(RepresentationType type) {
        this.type = type;
    }

    public void setPicturePath(String picturePath) {
        if (picturePath == null) {
            imgageRepresentation = null;
            return;
        }
        imgageRepresentation = Toolkit.getDefaultToolkit().getImage(picturePath);
        BufferedImage bi = Picture.toBufferedImage(imgageRepresentation);
        this.imageWidth = bi.getWidth();
        this.imageHeight = bi.getHeight();
    }

    private void drawMarking(Graphics2D g2, int x, int y, Rectangle r) {
        StringTokenizer st = new StringTokenizer(this.value, "\n");
        int customWidth = 0;
        while (st.hasMoreTokens()) {
            int size = fm.stringWidth(st.nextToken());
            if (size > customWidth) {
                customWidth = size;
            }
        }
        customWidth += 10;
        this.setSize(new Dimension(Math.max(customWidth, (int) initialWidth), getSize().height));
        g2.setPaint(Props.EXTENSION_COLOR);
        if (isMouseOver()) {
            Color c = (Color) g2.getPaint();
            g2.setPaint(new Color(c.getRed() ^ 0x66, c.getGreen() ^ 0x00, c.getBlue() ^ 0x00));
        }
        g2.drawRoundRect(x, y, getSize().width, getSize().height, 10, 10);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.fillRoundRect(x + 1, y + 1, getSize().width, getSize().height, 10, 10);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        if (Config.attachPoint) {
            int drag = 4;
            g2.drawRect(r.x + (drag >> 1), r.y + (drag >> 1), drag, drag);
        }

        g2.setPaint(Props.EXTENSION_COLOR);
        g2.setColor(Color.white);

        st = new StringTokenizer(this.value, "\n");
        y += fm.getHeight();
        int textSize = 0;
        x += 5;
        int i = 0;
        while (st.hasMoreTokens()) {
            if (textSize + 2 * fm.getHeight() >= getSize().getHeight()) {
                g2.drawString("... open the edit window for the complet list ...", x, y);
                break;
            }
            g2.drawString(st.nextToken(), x, y);
            y += fm.getHeight();
            textSize += fm.getHeight();
            i++;
        }
        g2.setPaint(Props.EXTENSION_COLOR);
    }
} // Representation
