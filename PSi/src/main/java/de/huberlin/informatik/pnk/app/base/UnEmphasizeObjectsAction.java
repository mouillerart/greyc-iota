package de.huberlin.informatik.pnk.app.base;

import java.util.*;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * UnEmphasizeObjectsAction.java
 *
 *
 * Created: Fri Jan 19 20:03:42 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

public class UnEmphasizeObjectsAction extends MetaActionObject {
    Vector unEmphasizeObjects;

    public UnEmphasizeObjectsAction(ApplicationControl ac, Net net, MetaApplication initiator, Vector unEmphasizeObjects) {
        super(ac, net, initiator);
        this.unEmphasizeObjects = unEmphasizeObjects;
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        ((ApplicationNetDialog)target).unEmphasizeObjects(this.unEmphasizeObjects);
        return null;
    }
} // UnEmphasizeObjectsAction