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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 * Draw
 *
 * Some useful methods for drawing.
 *
 * Copied from de.huberlin.informatik.pnk.editor.Draw
 */
class Draw {

    public static double alpha = Math.PI / 8;

    /**
     * Draws an arrow. Used by arcs.
     */
    public static Point arrow(Point from, Point to, int size, Graphics g) {
        if (from == null || to == null) {
            return null;
        }
        double dx;
        double dy;
        double d;
        double hook = 0.9;

        if (size > 6) {
            size -= 2;
        }

        double mysin = Math.sin(alpha);
        double mycos = Math.cos(alpha);

        Polygon shape = new Polygon();

        dx = to.x - from.x;
        dy = to.y - from.y;
        d = Math.sqrt((double) dx * dx + (double) dy * dy);
        if (d == 0) {
            return to;
        }

        dx = dx / d * size * 1.5;
        dy = dy / d * size * 1.5;
        //Falls nur der RueckgabePunkt interessiert
        if (g == null) {
            return new Point(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
        }

        shape.addPoint(to.x, to.y);
        shape.addPoint(to.x - (int) ((dx * mycos) - (dy * mysin)), to.y - (int) ((dx * mysin) + (dy * mycos)));
        shape.addPoint(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
        shape.addPoint(to.x - (int) ((dy * mysin) + (dx * mycos)), to.y - (int) ((dy * mycos) - (dx * mysin)));

        Color isMarked = g.getColor();
        g.setColor(Props.ARC_FILL_COLOR);
        g.fillPolygon(shape);
        g.setColor(isMarked);
        g.drawPolygon(shape);

        return new Point(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
    }

    public static Point arrow2(Point from, Point to, int size, Graphics g) {
        if (from == null || to == null) {
            return null;
        }
        double dx;
        double dy;
        double d;
        double hook = 0.9;

        if (size > 6) {
            size -= 2;
        }

        double mysin = Math.sin(alpha);
        double mycos = Math.cos(alpha);

        Polygon shape = new Polygon();

        dx = to.x - from.x;
        dy = to.y - from.y;
        d = Math.sqrt((double) dx * dx + (double) dy * dy);
        if (d == 0) {
            return to;
        }

        dx = dx / d * size * 1.5;
        dy = dy / d * size * 1.5;

        //Falls nur der RueckgabePunkt interessiert
        if (g == null) {
            return new Point(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
        }

        shape.addPoint(to.x, to.y);
        shape.addPoint(to.x - (int) ((dx * mycos) - (dy * mysin)), to.y - (int) ((dx * mysin) + (dy * mycos)));
        shape.addPoint(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
        shape.addPoint(to.x - (int) ((dy * mysin) + (dx * mycos)), to.y - (int) ((dy * mycos) - (dx * mysin)));

        Color isMarked = g.getColor();
        g.setColor(Color.white);
        g.fillPolygon(shape);
        g.setColor(isMarked);
        g.drawPolygon(shape);

        return new Point(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
    }

    public static Point arrow3(Point from, Point to, int size, Graphics g) {
        if (from == null || to == null) {
            return null;
        }
        double dx;
        double dy;
        double d;
        double hook = 0.9;

        if (size > 6) {
            size -= 2;
        }
        double mysin = Math.sin(alpha);
        double mycos = Math.cos(alpha);

        Polygon shape = new Polygon();

        dx = to.x - from.x;
        dy = to.y - from.y;
        d = Math.sqrt((double) dx * dx + (double) dy * dy);

        if (d == 0) {
            return to;
        }

        dx = dx / d * size * 1.5;
        dy = dy / d * size * 1.5;

        //Falls nur der RueckgabePunkt interessiert
        if (g == null) {
            return new Point(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
        }

        Color isMarked = g.getColor();
        //g.setColor(Color.white);
        g.setColor(isMarked);
        g.drawLine(to.x, to.y, to.x - (int) ((dx * mycos) - (dy * mysin)), to.y - (int) ((dx * mysin) + (dy * mycos)));
        g.drawLine(to.x, to.y, to.x - (int) ((dy * mysin) + (dx * mycos)), to.y - (int) ((dy * mycos) - (dx * mysin)));

        return new Point(to.x - (int) ((dx * hook)),
                to.y - (int) ((dy * hook)));
    }

    public static Point dash(Point from, Point to, int size, Graphics g) {
        if (from == null || to == null) {
            return null;
        }
        double dx;
        double dy;
        double d;
        double hook = 0.9;

        if (size > 6) {
            size -= 2;
        }

        double mysin = Math.sin(alpha);
        double mycos = Math.cos(alpha);

        Polygon shape = new Polygon();

        dx = to.x - from.x;
        dy = to.y - from.y;
        d = Math.sqrt((double) dx * dx + (double) dy * dy);

        if (d == 0) {
            return to;
        }

        dx = dx / d * size * 1.5;
        dy = dy / d * size * 1.5;

        //Falls nur der RueckgabePunkt interessiert
        if (g == null) {
            return new Point(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
        }

        Color isMarked = g.getColor();
        g.setColor(isMarked);
        g.drawLine(to.x - (int) ((dx * mycos) - (dy * mysin)), to.y - (int) ((dx * mysin) + (dy * mycos)),
                to.x - (int) ((dy * mysin) + (dx * mycos)), to.y - (int) ((dy * mycos) - (dx * mysin)));

        return new Point(to.x - (int) ((dx * hook)), to.y - (int) ((dy * hook)));
    }

    public static Point bobbel1(Point from, Point to, int size, Graphics g) {
        if (from == null || to == null) {
            return null;
        }
        double dx;
        double dy;
        double d;

        if (size > 6) {
            size -= 2;
        }

        dx = to.x - from.x;
        dy = to.y - from.y;
        d = Math.sqrt((double) dx * dx + (double) dy * dy);

        if (d == 0) {
            return to;
        }
        dx = dx / d * (size / 2);
        dy = dy / d * (size / 2);

        Color isMarked = g.getColor();
        g.setColor(isMarked);
        g.fillOval(to.x - (int) (dx) - (int) (size / 2), to.y - (int) (dy) - (int) (size / 2), (int) (size), (int) (size));

        return new Point(to.x, to.y);
    }

    public static Point bobbel2(Point from, Point to, int size, Graphics g) {
        if (from == null || to == null) {
            return null;
        }
        double dx;
        double dy;
        double d;

        if (size > 6) {
            size -= 2;
        }

        dx = to.x - from.x;
        dy = to.y - from.y;
        d = Math.sqrt((double) dx * dx + (double) dy * dy);

        if (d == 0) {
            return to;
        }

        dx = dx / d * (size / 2);
        dy = dy / d * (size / 2);

        Color isMarked = g.getColor();
        g.setColor(Color.white);
        g.fillOval(to.x - (int) (dx) - (int) (size / 2), to.y - (int) (dy) - (int) (size / 2), (int) (size), (int) (size));
        g.setColor(isMarked);
        g.drawOval(to.x - (int) (dx) - (int) (size / 2), to.y - (int) (dy) - (int) (size / 2), (int) (size), (int) (size));

        return new Point(to.x, to.y);
    }
} // Draw
