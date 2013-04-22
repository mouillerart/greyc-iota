package de.huberlin.informatik.pnk.app.base;

import java.util.*;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * AnnotateObjectsAction.java
 *
 *
 * Created: Thu Jan 18 13:05:27 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

public class AnnotateObjectsAction extends MetaActionObject {
    Hashtable annotations;

    public AnnotateObjectsAction(ApplicationControl ac, Net net, MetaApplication initiator, Hashtable annotations) {
        super(ac, net, initiator);
        this.annotations = annotations;
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        ((ApplicationNetDialog)target).anotateObjects(annotations);
        return null;
    }
} // AnnotateObjectsAction
