package de.huberlin.informatik.pnk.editor;

import java.awt.*;

/**
 * MemberSprite.java
 *
 * This class defines methods,
 * that must be implemented from sprites,
 * which represents netobjects.
 *
 * Created: Mon Jan  1 14:24:17 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class MemberSprite extends Sprite {
    Object netobject = null;
    boolean selected = false;
    Color selectColor = Color.magenta;
    Color actionColor = Color.yellow;

    /*
     * Says if this sprite is joined,
     * means that there is another sprite
     * with the same netobject.
     */
    private boolean joined = false;

    private boolean action = false;

    Color emphasize_color = null;

    protected MemberSprite() {
        super();
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

    /**
     * Get the value of emphasized.
     * @return Value of emphasized.
     */
    protected boolean getEmphasized() {
        return !(emphasize_color == null);
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

    /*
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
        this.emphasize_color = c;
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

    /*
     * Set the selected status of this sprite
     */
    protected void setSelected(boolean selected) {
        this.selected = selected;
    }
} // MemberSprite
