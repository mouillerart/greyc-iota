package de.huberlin.informatik.pnk.editor;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

/**
 * SpriteVector.java
 *
 * A list of all graphic-objects on page
 * Some useful methods for drawing, finding or deleting them.
 *
 * Created: Sat Dec 23 11:53:57 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class SpriteVector extends Vector {
    protected SpriteVector() {
        super();
    }

    /*
     * Gets a sprite with specified location,
     * </code> null </code> otherwise.
     */
    protected Sprite get(Point p) {
        Rectangle spritebounds;
        int lastsprite = this.size() - 1;
        for (int i = lastsprite; i >= 0; i--) {
            Sprite sprite = (Sprite) this.get(i);
            spritebounds = sprite.getBounds();
            if (spritebounds.contains(p)) {
                if (sprite.contains(p)) {
                    return sprite;
                }
            }
        }
        return null;
    }

    /*
     * Page calls this method, to get the rectangle
     * area that needs to be repainted.
     * @return rectangle area that needs
     * to be updated, or </code> null </code>
     * if there are no changes
     */
    protected Rectangle getUpdatearea() {
        Rectangle updatearea = null;
        for (int i = 0; i < this.size(); i++) {
            Sprite s = (Sprite) this.get(i);
            if (s.update) {
                // update for this sprite neccessary
                Rectangle r = s.getUpdatearea();
                if (updatearea == null) updatearea = r;
                else updatearea = updatearea.union(r);
            }
        }
        return updatearea;
    }

    /*
     * Draws all sprites inside
     * clipbounds of graphiccontext.
     * @param g graphiccontext for drawing sprites
     */
    protected void paint(Graphics g) {
        Rectangle spritebounds;
        Rectangle clipbounds = g.getClipBounds();
        for (int i = 0; i < this.size(); i++) {
            Sprite sprite = (Sprite) this.get(i);
            spritebounds = sprite.getBounds();
            if (clipbounds.intersects(spritebounds) || sprite.update) {
                sprite.paint(g);
            }
        }
    }

    protected void print(Graphics g) {
        for (int i = 0; i < this.size(); i++) {
            ((Sprite) this.get(i)).print(g);
        }
    }
} // SpriteVector
