package de.huberlin.informatik.pnk.app.base;

import java.util.*;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * UnAnnotateObjectsAction.java
 *
 *
 * Created: Fri Jan 19 20:03:42 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

public class UnAnnotateObjectsAction extends MetaActionObject {
    Vector unAnnotateObjects;

    public UnAnnotateObjectsAction(ApplicationControl ac, Net net, MetaApplication initiator, Vector unAnnotateObjects) {
        super(ac, net, initiator);
        this.unAnnotateObjects = unAnnotateObjects;
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        ((ApplicationNetDialog)target).unAnotateObjects(this.unAnnotateObjects);
        return null;
    }
} // UnAnnotateObjectsAction
