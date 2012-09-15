package de.huberlin.informatik.pnk.editor;

import java.awt.*;

/**
 * Extension.java
 *
 * Represents an extension of a netobject.
 *
 * Created: Tue Jan  2 11:48:48 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class Extension extends Label {
    //    implements MemberSprite {

    protected Extension(Sprite parent, Point position, Dimension size, FontMetrics fm, String id, String value) {
        super(parent, position, size, fm, id, value);
    }
} // Extension