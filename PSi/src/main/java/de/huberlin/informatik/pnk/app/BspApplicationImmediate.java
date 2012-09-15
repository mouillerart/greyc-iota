package de.huberlin.informatik.pnk.app;

import java.util.*;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;
import javax.swing.JMenu;

/**
 * Insert the type's description here.
 * Creation date: (03.10.00 18:43:16)
 * @author:
 */
public class BspApplicationImmediate extends MetaApplication {
    public static String staticAppName = "BspApplicationImmediate";
    public static boolean startImmediate = true;
    public boolean startAsThread = true;
    private MetaJFrame frame1 = null;
    private MetaJFrame frame2 = null;
    /**
     * BspApplication1 constructor comment.
     * @param ac de.huberlin.informatik.pnk.appControl.ApplicationControl
     */
    public BspApplicationImmediate(ApplicationControl ac) {
        super(ac);
    }

    public void run() {
        System.out.println("### Application started ###############");
        this.test();
    }

    private void test() {
        Vector netobjects = ((Net)net).getPlaces();

        // test of EmphasizeAction
        (new EmphasizeObjectsAction(applicationControl, net, this, netobjects)).invokeAction();
        // test of AnnotateObjectsAction
        Hashtable annotations = new Hashtable();
        Enumeration e = netobjects.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            annotations.put(o, "HA HA HA!!!");
        }
        (new AnnotateObjectsAction(applicationControl, (Net)net, this, annotations)).invokeAction();
        (new ResetEmphasizeAction(applicationControl, (Net)net, this)).invokeAction();
        (new ResetAnnotationsAction(applicationControl, (Net)net, this)).invokeAction();
        for (int i = 0; i < 1; i++) {
            System.out.println("#############: " +
                               (new SelectObjectAction(applicationControl,
                                                       (Net)net, this, netobjects)).invokeAction());
        }
        // Applikation beendet sich selbst...
        this.quitMe();
    }

    public JMenu[] getMenus() {
        return null;
    }
}
