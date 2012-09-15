package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.kernel.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * If an application want's to request the editor
 * to emphasize, anotate or select one/some object[s],
 * it has to use the methods of this interface.
 */
public interface ApplicationNetDialog {
    /**
     * The application requests the editor to anotate some objects.
     *
     * @param anotations  a hashtable(Object netobject -> String anotation)
     */
    public void anotateObjects (Hashtable anotations);
    /**
     * The application requests the editor to emphasize some objects.
     *
     * @param objects    a vector of the objects, which should be emphasized
     */
    public void emphasizeObject (Object netobject);
    public void emphasizeObject (Object netobject, java.awt.Color c);

    public void emphasizeObjects (Vector objects);
    public void emphasizeObjects (Vector objects, java.awt.Color c);
    /**
     * The application requests the editor to reset all anotations.
     */
    public void resetAnnotations ();
    /**
     * The application requests the editor to unemphasize all objects.
     */
    public void resetEmphasize ();
    /**
     * The application requests the editor to select an object.
     *
     * @param objects    a vector of objects where one object should be selected
     * @return           the selected object
     */
    public Member selectObject (Vector objects);
    /**
     * The application requests the editor to select an object.
     *
     * @param objects    a vector of objects where one object should be selected
     * @param visible    requests the editor to display the cancel dialog
     * @return           the selected object
     */
    public Member selectObject (Vector objects, boolean visible);
    /**
     * The application requests the editor to cancel the selectObject action.
     */
    public void cancelSelectObject ();
    /**
     * The application requests the editor to select some objects...
     *
     * @param objects    a vector of objects where some should be selected
     * @return           a vector of all selected objects
     */
    public Vector selectObjects (Vector objects);
    /**
     * The application requests the editor to unanotate the objects.
     *
     * @param objects     a vector of all objects, where anotations should be removed
     */
    public void unAnotateObjects (Vector objects);
    /**
     * The application requests the editor to unEmphasize the objects.
     *
     * @param objects    a vector of the objects, which should be unemphasized
     */
    public void unEmphasizeObjects (Vector objects);

    public void setPosition (Object netobject, int pageId, int x, int y);
} //interface ApplicationNetDialog
