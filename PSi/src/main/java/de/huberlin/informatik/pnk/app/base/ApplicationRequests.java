package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.kernel.*;

/**
 * Using this interface an application can request information from editor.
 */
public interface ApplicationRequests {
    /**
     * Requests a reference of the net the editor works with.
     *
     * @return     a reference to the current net
     */
    public Graph getNet ();
    /**
     * Requests if the editor is locked.
     *
     * @return     <code>true</code> if editor is locked,
     *             <code>false</code> otherwise
     */
    public boolean isEditable ();
    /**
     * Unlock the editor.
     */
    public void setEditable ();
    /**
     * Starts the editor with a new net.
     *
     * @param net     set a new current net
     */
    public void setNet (Graph net);
    /**
     * Starts the editor with a new net.
     *
     * @param net     set a new current net
     */
    public void setNet (Net net);
    /**
     * Locks the editor, so that the editor cann't make changes in net.
     */
    public void setNotEditable ();
} //interface ApplicationRequests
