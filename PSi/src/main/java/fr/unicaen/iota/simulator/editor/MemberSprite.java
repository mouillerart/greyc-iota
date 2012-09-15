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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * MemberSprite
 *
 * This class defines methods that must be implemented from sprites that represents netobjects.
 *
 * Created from de.huberlin.informatik.pnk.editor.MemberSprite
 */
class MemberSprite extends Sprite {

    private Object netobject = null;
    private boolean selected = false;
    private Color selectColor = Color.magenta;
    private Color actionColor = Color.yellow;
    private Page page;
    /**
     * Says if this sprite is joined,
     * means that there is another sprite
     * with the same netobject.
     */
    private boolean joined = false;
    private boolean action = false;
    private Color emphasizeColor = null;

    protected MemberSprite() {
        super();
    }

    @Override
    protected Rectangle getBounds() {
        return super.getBounds();
    }

    protected MemberSprite(Sprite parent, Point position, Dimension size) {
        super(parent, position, size);
    }

    protected MemberSprite(Point position, Dimension size) {
        super(position, size);
    }

    protected boolean getAction() {
        return this.action;
    }

    protected Color getActionColor() {
        return actionColor;
    }

    protected Color getSelectColor() {
        return selectColor;
    }

    protected Color getEmphasizeColor() {
        return emphasizeColor;
    }

    /**
     * Get the value of emphasized.
     * @return Value of emphasized.
     */
    protected boolean getEmphasized() {
        return !(emphasizeColor == null);
    }

    /**
     * Get the value of joined.
     * @return Value of joined.
     */
    public boolean getJoined() {
        return joined;
    }

    /**
     * Get the value of netobject.
     * @return Value of netobject.
     */
    protected Object getNetobject() {
        return this.netobject;
    }

    /**
     * @return <code> true </code>
     * if this membersprite is selected
     */
    protected boolean getSelected() {
        return this.selected;
    }

    protected void setAction(boolean highlight) {
        this.action = highlight;
    }

    /**
     * Set the value of emphasized.
     * @param v  Value to assign to emphasized.
     */
    protected void setEmphasized(Color c) {
        this.emphasizeColor = c;
    }

    /**
     * Set the value of joined.
     * @param v  Value to assign to joined.
     */
    public void setJoined(boolean v) {
        this.joined = v;
    }

    /**
     * Set the value of netobject.
     * @param v  Value to assign to netobject.
     */
    protected void setNetobject(Object v) {
        this.netobject = v;
    }

    /**
     * Set the selected status of this sprite
     */
    protected void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
} // MemberSprite
