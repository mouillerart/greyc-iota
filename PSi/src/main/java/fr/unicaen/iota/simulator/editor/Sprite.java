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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Sprite
 *
 * This class defines useful methods for handling sprites. All objects
 * represented on a  page should extend this class.
 *
 * Created after de.huberlin.informatik.pnk.editor.Sprite
 */
class Sprite {

    private boolean mouseOver = false;
    private boolean hightLighted = false;

    void setMouseOver(boolean b) {
        mouseOver = b;
    }

    boolean isMouseOver() {
        return mouseOver;
    }

    public boolean isHightLighted() {
        return hightLighted;
    }

    public void setHightLighted(boolean bool) {
        this.hightLighted = bool;
    }
    /**
     * True, if update of this sprite neccessary.
     */
    boolean update = true;
    /**
     * A list of sprites, that are always located
     * relative to this sprite. If this sprite
     * moves, they have too.
     */
    List<Sprite> subsprites = new ArrayList<Sprite>();
    /**
     * CenterPosition of this sprite.
     * If this sprite has an parent, position
     * is relative to it.
     */
    private Point offset = new Point(0, 0);
    /**
     * Height and width of this sprite
     */
    private Dimension size = new Dimension(40, 41);
    /**
     * Some sprites have a parent. For example
     * the parent-sprite of an place-extension
     * is the place-sprite.
     */
    private Sprite parent = null;
    /**
     * Hide this sprite on page?
     */
    boolean visible = true;
    /**
     * Region on page that should be repainted
     */
    private Rectangle updatearea;

    /**
     * Class constructor.
     * Sets default values for position and size.
     */
    protected Sprite() {
        super();
    }

    /**
     * Class constructor.
     * @param parent the parentsprite of this sprite
     * @param position this location in absolute values
     * @param size this sprites height and width
     */
    protected Sprite(Sprite parent, Point position, Dimension size) {
        this.setParent(parent);
        this.setPosition(position);
        this.setSize(size);
    }

    /**
     * Class constructor.
     * @param position centerposition of this sprite
     * @param size height and width of this sprite
     */
    protected Sprite(Point position, Dimension size) {
        this.setPosition(position);
        this.setSize(size);
    }

    /**
     * @param p Point which could be inside this sprite
     * @return true if sprite contains point
     * false otherwise
     */
    boolean contains(Point p) {
        if (!this.getVisible()) {
            return false;
        }
        Rectangle r = this.getBounds();
        return r.contains(p);
    }

    protected void delete() {
        // Clean up all references
        if (this.parent != null) {
            this.parent.subsprites.remove(this);
        }
    }

    /**
     * Gets the bounding rectangle of this sprite.
     * @return bounding rectangle
     */
    Rectangle getBounds() {
        int buff = 10; // makes drawings nicer
        Point parentpos = new Point(0, 0);
        if (this.parent != null) {
            parentpos = this.parent.getPosition();
        }
        int x = parentpos.x + this.offset.x - (this.size.width >> 1);
        int y = parentpos.y + this.offset.y - (this.size.height >> 1);
        return new Rectangle(x - (buff >> 1), y - (buff >> 1),
                this.size.width + buff, this.size.height + buff);
    }

    /**
     * Get the offset of this sprite to its parentsprite.
     */
    protected Point getOffset() {
        return this.offset;
    }

    /**
     * Get the value of parent.
     * @return Value of parent.
     */
    protected Sprite getParent() {
        return parent;
    }

    /**
     * Get the value of position.
     * @return Value of position.
     */
    protected Point getPosition() {
        Point parentpos = new Point(0, 0);
        if (this.parent != null) {
            parentpos = this.parent.getPosition();
        }
        int x = this.offset.x + parentpos.x;
        int y = this.offset.y + parentpos.y;
        return new Point(x, y);
    }

    /**
     * Get the value of size.
     * @return Value of size.
     */
    protected Dimension getSize() {
        return size;
    }

    /**
     * SpriteVector calls this method to decide if this sprite needs to be updated.
     * @return the rectangle area that needs an update, or </code> null </code> if there are no changes
     */
    protected Rectangle getUpdatearea() {
        if (this.updatearea == null) {
            this.updatearea = this.getBounds();
        }
        //add updatearea of subsprites
        Rectangle subarea = this.getUpdateareaOfSubsprites();
        if (subarea != null) {
            this.updatearea = this.updatearea.union(subarea);
        }
        return this.updatearea;
    }

    /**
     * @return updatearea of all subsprites, or null
     */
    private Rectangle getUpdateareaOfSubsprites() {
        Rectangle bounds = null;
        for (Sprite s : subsprites) {
            if (bounds == null) {
                bounds = s.getUpdatearea();
            } else {
                bounds = bounds.union(s.getUpdatearea());
            }
        }
        return bounds;
    }

    /**
     * Get the value of visible.
     * @return Value of visible.
     */
    protected boolean getVisible() {
        return visible;
    }

    /**
     * Draws this sprite.
     * @param g GraphicContext for drawing.
     */
    void paint(Graphics g) {
        // if not visible, then no painting
        if (!this.getVisible()) {
            return;
        }
        // erase update flag
        this.update = false;
        // set new updatearea
        this.updatearea = this.getBounds();
    }

    void print(Graphics g) {
    }

    /**
     * Set the value of parent.
     * @param v  Value to assign to parent.
     */
    protected void setParent(Sprite v) {
        this.parent = v;
    }

    /**
     * Set the value of position.
     * @param v  Value to assign to position.
     */
    protected void setPosition(Point v) {
        Point parentpos = new Point(0, 0);
        if (this.parent != null) {
            parentpos = this.parent.getPosition();
        }
        int x = v.x - parentpos.x;
        int y = v.y - parentpos.y;
        this.offset.x = x;
        this.offset.y = y;
        // setting updatearea for drawing
        Rectangle resized_updatearea = this.getBounds();
        this.setUpdatearea(resized_updatearea);
        // this sprite needs an update
        this.update = true;
    }

    /**
     * Set the value of size.
     * @param v  Value to assign to size.
     */
    protected void setSize(Dimension v) {
        this.size = v;
        // setting updatearea for drawing
        Rectangle resized_updatearea = this.getBounds();
        this.setUpdatearea(resized_updatearea);
        // this sprite needs an update
        this.update = true;
    }

    /**
     * Set the value of updatearea.
     * @param v  Value to assign to updatearea.
     */
    protected void setUpdatearea(Rectangle v) {
        // join old and new updatearea
        if (updatearea != null) {
            this.updatearea = this.updatearea.union(v);
        } else {
            this.updatearea = v;
        }
        //set the updatearea of the subsprites
        this.setUpdateareaOfSubsprites();
    }

    private void setUpdateareaOfSubsprites() {
        Rectangle bounds;
        for (Sprite s : subsprites) {
            bounds = s.getBounds();
            s.setUpdatearea(bounds);
        }
    }

    /**
     * Set the value of visible.
     * @param v  Value to assign to visible.
     */
    protected void setVisible(boolean v) {
        this.visible = v;
    }
} // Sprite
