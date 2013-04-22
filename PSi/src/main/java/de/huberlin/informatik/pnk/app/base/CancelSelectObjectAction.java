package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

public class CancelSelectObjectAction extends MetaActionObject {
    public CancelSelectObjectAction(ApplicationControl ac, Net net, MetaApplication initiator) {
        super(ac, net, initiator);
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        ((ApplicationNetDialog)target).cancelSelectObject();
        return null;
    }
}
