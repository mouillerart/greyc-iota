package de.huberlin.informatik.pnk.app.base;

import java.util.*;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * SelectObjectAction.java
 *
 *
 * Created: Fri Jan 19 20:03:42 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

public class SelectObjectAction extends MetaActionObject {
    Vector selectObject;
    boolean visible;

    public SelectObjectAction(ApplicationControl ac, Net net, MetaApplication initiator, Vector selectObject) {
        super(ac, net, initiator);
        this.selectObject = selectObject;
        this.visible = true;
    }

    public SelectObjectAction(ApplicationControl ac, Net net, MetaApplication initiator, Vector selectObject, boolean visible) {
        super(ac, net, initiator);
        this.selectObject = selectObject;
        this.visible = visible;
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        return ((ApplicationNetDialog)target).selectObject(this.selectObject, this.visible);
    }
} // SelectObjectAction