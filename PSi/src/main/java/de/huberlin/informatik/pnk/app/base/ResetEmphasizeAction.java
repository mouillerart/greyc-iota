package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * ResetEmphasizeAction.java
 *
 *
 * Created: Thu Jan 18 13:27:10 2001
 *
 * @author Alexander Gruenewald
 */

public class ResetEmphasizeAction extends MetaActionObject {
    public ResetEmphasizeAction(ApplicationControl ac, Net net, MetaApplication initiator) {
        super(ac, net, initiator);
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        ((ApplicationNetDialog)target).resetEmphasize();
        return null;
    }
} // ResetEmphasizeAction
