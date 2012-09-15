package de.huberlin.informatik.pnk.app;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.*;

/**
 * Insert the type's description here.
 * Creation date: (03.10.00 18:43:16)
 * @author:
 */
public class BspApplicationQuit extends MetaApplication {
    public static String staticAppName = "BspApplicationQuit";
    /**
     * BspApplication1 constructor comment.
     * @param ac de.huberlin.informatik.pnk.appControl.ApplicationControl
     */
    public BspApplicationQuit(ApplicationControl ac) {
        super(ac);
    }

    public void run() {
        System.out.println(" Application started ###############");

        this.quitMe();
    }
}
