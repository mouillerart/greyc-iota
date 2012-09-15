package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * SetPositionAction.java
 *
 *
 * Created: Thu Jan 18 13:27:10 2001
 *
 * @author Alexander Gruenewald
 */

public class SetPositionAction extends MetaActionObject {
    private Object netobject;
    private int pageId;
    private int x;
    private int y;

    public SetPositionAction(ApplicationControl ac, Net net, MetaApplication initiator, Object netobject, int pageId, int x, int y) {
        super(ac, net, initiator);
        this.netobject = netobject;
        this.pageId = pageId;
        this.x = x;
        this.y = y;
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        ((ApplicationNetDialog)target).setPosition(netobject, pageId, x, y);
        return null;
    }
} // SetPositionAction
