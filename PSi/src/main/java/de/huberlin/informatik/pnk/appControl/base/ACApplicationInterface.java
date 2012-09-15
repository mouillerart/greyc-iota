package de.huberlin.informatik.pnk.appControl.base;

/**
 * Interface for a dialog between application and ApplicationControl.
 * Implemented by AC
 */

import de.huberlin.informatik.pnk.app.base.*;

public interface ACApplicationInterface {
    /*???????????????????????????????????
     * fuer Infos nach dem Laden...???
       public getsavedInfo(        Netzobjekt      )
     */

    /**
     * Application reports...
     *
     * @param app     the new application
     */
    public void addApplication (MetaApplication app);
    public MetaApplication getfocusedApplication ();
    /**
     * Application gives notice of removal...
     *
     * @param app     the removed application
     */
    public void removeApplication (MetaApplication app);
    /**
     * Application gets focus
     *
     * @param app     the application
     */
    public void setFocusedApplication (MetaApplication app);
} // interface ACApplicationInterface