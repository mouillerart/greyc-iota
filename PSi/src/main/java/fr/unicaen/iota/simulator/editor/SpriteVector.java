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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * SpriteVector
 *
 * A list of all graphic-objects on page
 * Some useful methods for drawing, finding or deleting them.
 *
 * Copied from de.huberlin.informatik.pnk.editor.SpriteVector
 */
class SpriteVector extends ArrayList<Sprite> {

    protected SpriteVector() {
        super();
    }

    /**
     * Gets a sprite with specified location,
     * </code> null </code> otherwise.
     */
    protected Sprite get(Point p) {
        Rectangle spritebounds;
        for (Sprite sprite : this) {
            spritebounds = sprite.getBounds();
            if (spritebounds.contains(p)) {
                if (sprite.contains(p)) {
                    return sprite;
                }
            }
        }
        return null;
    }

    /**
     * Page calls this method, to get the rectangle area that needs to be repainted.
     * @return rectangle area that needs to be updated, or </code> null </code>
     * if there are no changes
     */
    protected Rectangle getUpdatearea() {
        Rectangle updatearea = null;
        for (Sprite s : this) {
            if (s.update) {
                // update for this sprite neccessary
                Rectangle r = s.getUpdatearea();
                if (updatearea == null) {
                    updatearea = r;
                } else {
                    updatearea = updatearea.union(r);
                }
            }
        }
        return updatearea;
    }

    /**
     * Draws all sprites inside clipbounds of graphiccontext.
     * @param g graphiccontext for drawing sprites
     */
    protected void paint(Graphics g) {
        Rectangle spritebounds;
        Rectangle clipbounds = g.getClipBounds();
        for (Sprite sprite : this) {
            spritebounds = sprite.getBounds();
            if (clipbounds.intersects(spritebounds) || sprite.update) {
                sprite.paint(g);
            }
        }
    }

    protected void print(Graphics g) {
        for (Sprite s : this) {
            s.print(g);
        }
    }

    protected void sort() {
        List<Sprite> markingLayer = new ArrayList<Sprite>();
        List<Sprite> nameLayer = new ArrayList<Sprite>();
        List<Sprite> backgroundLayer = new ArrayList<Sprite>();
        for (Sprite sprite : this) {
            if (sprite instanceof Representation) {
                Representation rep = (Representation) sprite;
                if ("marking".equals(rep.getId())) {
                    markingLayer.add(sprite);
                    continue;
                }
                if ("name".equals(rep.getId())) {
                    nameLayer.add(sprite);
                    continue;
                }
            }
            backgroundLayer.add(sprite);
        }
        this.clear();
        this.addAll(backgroundLayer);
        this.addAll(nameLayer);
        this.addAll(markingLayer);
    }
} // SpriteVector
