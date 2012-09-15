package de.huberlin.informatik.pnk.kernel.base;

import de.huberlin.informatik.pnk.kernel.*;
import java.util.Observer;

/**
 * Observed object is an {@link de.huberlin.informatik.pnk.kernel.Edge
 * arc}. <br>
 * This arc gets a new target node.
 */
public class ChangeTargetAction extends ActionObject {
    Node target;

    /**
     * Constructor specifying the <code>initiator</code> which generates
     * this object and the new target node. <br>
     */
    public ChangeTargetAction(Object initiator, Node target) {
        super(initiator);
        this.target = target;
    }

    /**
     * Codes the change of target node of the observed object (the arc). <br>
     * Requires: observer implements the interface {@link NetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        de.huberlin.informatik.pnk.appControl.base.D.d("ChangeTargetAction.perfomAction()");
        if (observer instanceof NetObserver) {
            ((NetObserver)observer).changeTarget((Edge)observedObject, target);
        }
    }
} // ChangeTargetAction