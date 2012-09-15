package de.huberlin.informatik.pnk.app;


import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.*;

/**
 * Insert the type's description here.
 * Creation date: (03.10.00 18:43:16)
 * @author:
 */
public class BspApplication1 extends MetaApplication {
    public static String staticAppName = "BspApplication1";
    /**
     * BspApplication1 constructor comment.
     * @param ac de.huberlin.informatik.pnk.appControl.ApplicationControl
     */
    public BspApplication1(ApplicationControl ac) {
        super(ac);
    }

    public void run() {
        System.out.println(" Application started ###############");

        this.quitMe();
    }
}
