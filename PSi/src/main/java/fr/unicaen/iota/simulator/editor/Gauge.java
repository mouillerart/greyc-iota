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

import fr.unicaen.iota.simulator.app.CommListener;
import java.awt.*;

/**
 *
 */
public class Gauge extends Sprite implements CommListener {

    private double volume;
    private int width = 30;
    private int height = 10;
    private String pipeId = "";

    Gauge(Sprite parent, Point position, int width, int height, String pipeId) {
        super(parent, position, new Dimension(width, height));
        setVolume(0);
        setWidth(width);
        setHeight(height);
        setVisible(false);
        this.pipeId = pipeId;
    }

    /**
     * @return the volume
     */
    public double getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Shape getShape() {
        return null;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    void print(Graphics g) {
    }

    /**
     * Draws the value of this label.
     */
    @Override
    void paint(Graphics g) {
        super.paint(g); // very important, do not forget
        Graphics2D g2 = (Graphics2D) g;

        Rectangle r = getBounds();

        if (!this.getVisible()) {
            return;
        }
        int lineSize = 1;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect((int) getPosition().getX() - (width >> 1) + lineSize,
                (int) getPosition().getY() - (height >> 1) + lineSize,
                (int) ((width * volume) / 100.0) - lineSize,
                height - lineSize, 8, 8);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect((int) getPosition().getX() - (width >> 1),
                (int) getPosition().getY() - (height >> 1),
                width,
                height, 8, 8);

        g2.setFont(new Font("arial", 0, 10));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString(volume + " %", (int) getPosition().getX() - 20, (int) getPosition().getY() + (height >> 1) - 1);
        g2.setFont(new Font("arial", 0, 12));
    }

    @Override
    public void pipeStatusChanged(String pipeId, double value) {
        if (this.pipeId.equals(pipeId)) {
            this.volume = value;
        }
    }
}
