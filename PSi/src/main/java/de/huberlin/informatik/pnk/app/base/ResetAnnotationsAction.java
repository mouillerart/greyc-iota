package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * ResetAnnotationsAction.java
 *
 *
 * Created: Thu Jan 18 13:27:10 2001
 *
 * @author Alexander Gruenewald
 */

public class ResetAnnotationsAction extends MetaActionObject {
    public ResetAnnotationsAction(ApplicationControl ac, Net net, MetaApplication initiator) {
        super(ac, net, initiator);
    }

    public boolean checkInterface(Object target) {
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        ((ApplicationNetDialog)target).resetAnnotations();
        return null;
    }
} // ResetAnnotationsAction
