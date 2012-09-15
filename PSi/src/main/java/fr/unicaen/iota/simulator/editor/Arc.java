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

import fr.unicaen.iota.simulator.pnk.EPCGenerator;
import fr.unicaen.iota.simulator.pnk.EPCSubscription;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.reflect.Method;

/**
 * Arc
 *
 * An arc is an edge with an arrow.
 *
 * Created after de.huberlin.informatik.pnk.editor.Arc
 */
class Arc extends Edge {

    enum ArrowHead {

        POT_OUT,
        POT_IN,
        NO,
        EQUAL,
        ZERO,
        PUT_INHIB,
        INHIB
    }

    enum ArrowTail {

        TEST,
        TEST1,
        ADD,
        SUB,
        RESERVE,
        SET,
        CLEAR,
        CLEAR1
    }
    private int arrowSize = 8;
    private ArrowHead arrowHead = ArrowHead.POT_IN;
    private ArrowTail arrowTail = ArrowTail.TEST;

    /**
     * Class constructor.
     * @param source sourcenode/beginning  of this arc
     * @param target targetnode/ending of this arc
     */
    Arc(MemberSpriteNode source, MemberSpriteNode target) {
        super(source, target);
    }

    private void drawArrow(Graphics g) {
        Point p1 = this.getPosition();
        Point p2 = this.getTarget().getPosition();
        p2 = getTarget().getBorderpoint(p1);
        Draw.arrow(p1, p2, this.arrowSize, g);
    }

    protected void drawArcDecoration(Graphics g) {
        MemberSpriteNode place;
        MemberSpriteNode transition;
        if (getSource().getNetobject() instanceof de.huberlin.informatik.pnk.kernel.Place) {
            place = getSource();
            transition = getTarget();
        } else {
            place = getTarget();
            transition = getSource();
        }
        Point p1 = getPosition();
        Point p2 = transition.getBorderpoint(p1);
        switch (arrowHead) {
            case POT_OUT:
                break;
            case POT_IN:
                Draw.arrow(p1, p2, this.arrowSize, g);
                break;
            case NO:
                Draw.dash(p1, p2, this.arrowSize, g);
                break;
            case EQUAL:
                Draw.arrow3(p1, p2, this.arrowSize, g);
                break;
            case ZERO:
                Draw.arrow2(p1, p2, this.arrowSize, g);
                break;
            case PUT_INHIB:
                Draw.bobbel1(p1, p2, this.arrowSize, g);
                break;
            case INHIB:
                Draw.bobbel2(p1, p2, this.arrowSize, g);
                break;
        }
        p1 = getPosition();
        p2 = place.getBorderpoint(p1);
        switch (arrowTail) {
            case TEST:
                break;
            case TEST1:
                Draw.dash(p1, p2, this.arrowSize, g);
                break;
            case ADD:
                Draw.arrow(p1, p2, this.arrowSize, g);
                break;
            case SUB:
                break;
            case RESERVE:
                Draw.arrow3(p1, p2, this.arrowSize, g);
                break;
            case SET:
                Draw.arrow2(p1, p2, this.arrowSize, g);
                break;
            case CLEAR:
                Draw.bobbel2(p1, p2, this.arrowSize, g);
                break;
            case CLEAR1:
                Draw.bobbel1(p1, p2, this.arrowSize, g);
                break;
        }
    }

    private boolean paintArcType(Graphics g) {
        if (this.stroke == null) {
            return false;
        }
        drawArcDecoration(g);
        return true;
    }

    /**
     * Draws an edge with an arrow.
     */
    @Override
    void paint(Graphics g) {
        this.stroke = null;
        de.huberlin.informatik.pnk.kernel.Edge edge = (de.huberlin.informatik.pnk.kernel.Edge) getNetobject();
        int arcWidth = Integer.parseInt(edge.getExtension("inscription").toString());
        this.strokeSize = arcWidth / 5;
        if (this.strokeSize < 1) {
            this.strokeSize = 1;
        }
        if (this.strokeSize > 2.5f) {
            this.strokeSize = 2.5f;
        }
        EPCSubscription subscription = (EPCSubscription) (edge.getExtension("subscription"));
        boolean isSuscriber = subscription.isUnderSubscription();
        boolean isAggregator = edge.getTarget().getIncomingEdges().size() > 1 && this.getTarget() instanceof Transition;
        float dash[] = {10.0f};
        if (isSuscriber) {
            arrowTail = ArrowTail.RESERVE;
            arrowHead = ArrowHead.POT_OUT;
            this.stroke = new BasicStroke(strokeSize, 1, 1, 10.0f, dash, 0.0f);
        } else if (isAggregator) {
            EPCGenerator ePCGenerator = (EPCGenerator) edge.getSource().getExtension("epcgenerator");
            if (ePCGenerator.isGenerator()) {
                dash[0] = 2.0f;
                arrowHead = ArrowHead.EQUAL;
            } else {
                dash[0] = 2.0f;
                arrowHead = ArrowHead.INHIB;
            }
            this.stroke = new BasicStroke(strokeSize, 0, 0, 10.0f, dash, 0.0f);
        }
        super.paint(g);
        if (!paintArcType(g)) {
            this.drawArrow(g);
        }
    }

    @Override
    void print(Graphics g) {
        super.print(g);
        if (!paintArcType(g)) {
            this.drawArrow(g);
        }
    }
} // Arc
