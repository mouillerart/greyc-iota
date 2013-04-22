package de.huberlin.informatik.pnk.kernel.base;

import de.huberlin.informatik.pnk.kernel.Net;
/**
 * Insert the type's description here.
 * Creation date: (30.09.00 18:55:50)
 * @author:
 */
public abstract class NetObservable extends java.util.Observable {
/**
 * NetObservable constructor comment.
 */
    public NetObservable() {
        super();
    }

/**
 * NetObservable constructor comment.
 */
    public abstract Net getNet ();
}
