package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.appControl.ApplicationControl;
import de.huberlin.informatik.pnk.kernel.Graph;

/**
 * Insert the type's description here.
 * Creation date: (12.1.2001 16:43:02)
 * @author:
 */
public abstract class MetaActionObject {
    MetaApplication initiator = null;
    Graph net = null;
    ApplicationControl ac = null;
    // Hier wird die Methode des Zielobjektes aufgerufen...
/**
 * MetaActionObject constructor comment.
 *
 *	result = (new MetaActionObject(ac, graph, this)).getResult();
 */
    public MetaActionObject(ApplicationControl ac, Graph net, MetaApplication initiator) {
        super();
        this.initiator = initiator;
        this.net = net;
        this.ac = ac;
    }

/**
 * MetaActionObject constructor comment.
 *
 *	result = (new MetaActionObject(ac,this)).getResult();
 */
    public abstract boolean checkInterface (Object target);
    // Hier wird das Interface des Zielobjektes geprüft...
    public final MetaApplication getInitiator() {
        return initiator;
    }

    public final Graph getNet() {
        return net;
    }

/**
 * Calls invokeAction method of the {@link ApplicationControl}.
 */
    public final Object invokeAction() {
        return ac.invokeAction(this);
    }

    public abstract Object performAction (MetaApplication target);
}