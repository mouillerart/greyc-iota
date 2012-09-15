package de.huberlin.informatik.pnk.kernel.base;

import de.huberlin.informatik.pnk.kernel.*;
import java.util.*;

/**
 * All applications observing a net, have to implement the following interface
 * to get information about changes in the net.
 */
public interface NetObserver {
    /**
     * The value of identified extension of <code>netobject</code> changed.
     *
     * @param netobject     the object which extension changed
     * @param extension     the extension identifier of the changed extension
     * @param newValue      the new value of the changed extension
     */
    public void changeExtension (Member netobject, String extension, String newValue);
    /**
     * The source-node of an arc has changed.
     *
     * @param netobject     the arc with the changed source-node
     * @param source        the new source of the arc
     */
    public void changeSource (Edge netobject, Node source);
    /**
     * The target-node of an arc has changed.
     *
     * @param netobject     the arc with the new target-node
     * @param target        the new target-node
     */
    public void changeTarget (Edge netobject, Node target);
    /**
     * The netobject was deleted.
     *
     * @param netobject     the deleted object
     */
    public void delete (Member netobject);
    /**
     * A new arc in net.
     *
     * @param netobject     the new object in net
     */
    public void newArc (Arc netobject);
    /**
     * Set a new net.
     *
     * @param net             the new net
     */
    public void newNet (Net net);
    /**
     * A new place in net.
     *
     * @param netobject     the new object in net
     */
    public void newPlace (Place netobject);
    /**
     * <code>newPlaceArc</code>
     *
     * @param netobject a <code>PlaceArc</code> value
     */
    public void newPlaceArc (PlaceArc netobject);
    /**
     * A new transition in net.
     *
     * @param netobject     the new object in net
     */
    public void newTransition (Transition netobject);
    /**
     * <code>newTranstionArc</code>
     *
     * @param netobject a <code>TransitionArc</code> value
     */
    public void newTransitionArc (TransitionArc netobject);
    public void update (Observable observable, Object o);
} //interface NetObserver
