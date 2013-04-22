package de.huberlin.informatik.pnk.kernel.base;

import de.huberlin.informatik.pnk.kernel.*;
import java.util.Observer;

/**
 * Observed object is an {@link de.huberlin.informatik.pnk.kernel.Arc
 * arc}. <br>
 * This arc gets a new source node.
 */
public class ChangeSourceAction extends ActionObject {
    private Node source;

    /**
     * Constructor specifying the <code>initiator</code> which generates
     * this object and the new source node. <br>
     */
    public ChangeSourceAction(Object initiator, Node source) {
        super(initiator);
        this.source = source;
    }

    /**
     * Codes the change of source node of the observed object (the arc). <br>
     * Requires: observer implements the interface {@link NetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        de.huberlin.informatik.pnk.appControl.base.D.d("ChangeSourceAction.perfomAction()");
        if (observer instanceof NetObserver) {
            ((NetObserver)observer).changeSource((Edge)observedObject, source);
        }
    }
} // ChangeSourceAction
