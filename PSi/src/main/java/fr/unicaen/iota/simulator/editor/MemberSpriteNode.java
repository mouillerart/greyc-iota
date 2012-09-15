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
import java.awt.Point;

/**
 * MemberSpriteNode
 *
 * Copied from de.huberlin.informatik.pnk.editor.MemberSpriteNode
 */
class MemberSpriteNode extends MemberSprite {

    MemberSpriteNode(Sprite parent, Point position, Dimension size) {
        super(parent, position, size);
    }

    public MemberSpriteNode(Point position, Dimension size) {
        super(position, size);
    }

    Point getBorderpoint(Point edgeFrom) {
        return null;
    }
} // MemberSpriteNode
