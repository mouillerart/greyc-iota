package de.huberlin.informatik.pnk.editor;

import java.awt.*;

/**
 * MemberSpriteNode.java
 *
 *
 * Created: Sat Apr 28 15:08:00 2001
 *
 * @author Alexander Grænewald
 * @version
 */

public class MemberSpriteNode extends MemberSprite {
    public MemberSpriteNode(Sprite parent, Point position, Dimension size) {
        super(parent, position, size);
    }

    public MemberSpriteNode(Point position, Dimension size) {
        super(position, size);
    }

    Point getBorderpoint(Point edgeFrom) {
        return null;
    }
} // MemberSpriteNode
