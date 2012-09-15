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

/**
 * TransitionArc
 *
 * Copied from de.huberlin.informatik.pnk.editor.TransitionArc
 */
class TransitionArc extends Edge {

    private int arrowSize = 8;

    /**
     * Class constructor.
     * @param source sourcenode/beginning  of this arc
     * @param target targetnode/ending of this arc
     */
    TransitionArc(MemberSpriteNode source, MemberSpriteNode target) {
        super(source, target);
    }

    private void drawArrow(Graphics g) {
        Point p1 = this.getPosition();
        Point p2 = getTarget().getBorderpoint(p1);
        Draw.arrow(p1, p2, this.arrowSize, g);
    }

    /**
     * Draws an edge with an arrow.
     */
    @Override
    void paint(Graphics g) {
        super.paint(g);
        this.drawArrow(g);
    }
} // TransitionArc
