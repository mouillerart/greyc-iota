package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.kernel.Member;
import java.awt.Color;
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (13.1.2001 12:54:50)
 * @author:
 */
public class EmphasizeObjectsAction extends MetaActionObject {
    private Color color = null;
    private Vector emobjs = null;
    private Member netobject = null;

    /**
     * EmphasizeAction constructor comment.
     * @param ac de.huberlin.informatik.pnk.appControl.ApplicationControl
     * @param net de.huberlin.informatik.pnk.kernel.Graph
     * @param initiator de.huberlin.informatik.pnk.app.base.MetaApplication
     */
    public EmphasizeObjectsAction(de.huberlin.informatik.pnk.appControl.ApplicationControl ac, de.huberlin.informatik.pnk.kernel.Graph net, MetaApplication initiator, Vector emobjs) {
        super(ac, net, initiator);
        this.emobjs = emobjs;
    }

    public EmphasizeObjectsAction(de.huberlin.informatik.pnk.appControl.ApplicationControl ac, de.huberlin.informatik.pnk.kernel.Graph net, MetaApplication initiator, Member netobject) {
        super(ac, net, initiator);
        this.netobject = netobject;
    }

    public EmphasizeObjectsAction(de.huberlin.informatik.pnk.appControl.ApplicationControl ac, de.huberlin.informatik.pnk.kernel.Graph net, MetaApplication initiator, Vector emobjs, Color color) {
        super(ac, net, initiator);
        this.emobjs = emobjs;
        this.color = color;
    }

    public EmphasizeObjectsAction(de.huberlin.informatik.pnk.appControl.ApplicationControl ac, de.huberlin.informatik.pnk.kernel.Graph net, MetaApplication initiator, Member netobject, Color color) {
        super(ac, net, initiator);
        this.netobject = netobject;
        this.color = color;
    }

    public boolean checkInterface(Object target) {
        // Hier wird das Interface des Zielobjektes gepr't...
        return target instanceof ApplicationNetDialog;
    }

    public Object performAction(MetaApplication target) {
        // Hier wird die Methode des Zielobjektes aufgerufen...
        if (emobjs != null)
            if (color != null)
                ((ApplicationNetDialog)target).emphasizeObjects(emobjs, color);
            else
                ((ApplicationNetDialog)target).emphasizeObjects(emobjs);
        else if (netobject != null)
            if (color != null)
                ((ApplicationNetDialog)target).emphasizeObject(netobject, color);
            else
                ((ApplicationNetDialog)target).emphasizeObject(netobject);
        else
            System.err.println("Initiated an emphasize action without an object ot emphasize.");

        return null;
    }
}
