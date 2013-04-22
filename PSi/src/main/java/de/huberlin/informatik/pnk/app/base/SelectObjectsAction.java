package de.huberlin.informatik.pnk.app.base;

import java.util.*;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * SelectObjectsAction.java
 *
 *
 * Created: Fri Jan 19 20:03:42 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

public class SelectObjectsAction extends MetaActionObject {
    Vector selectObjects;

    public SelectObjectsAction(ApplicationControl ac, Net net, MetaApplication initiator, Vector selectObjects) {
        super(ac, net, initiator);
        this.selectObjects = selectObjects;
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        return ((ApplicationNetDialog)target).selectObjects(this.selectObjects);
    }
} // SelectObjectsAction
