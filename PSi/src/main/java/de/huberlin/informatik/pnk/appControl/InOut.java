package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.appControl.base.D;
import de.huberlin.informatik.pnk.kernel.Net;
import de.huberlin.informatik.pnk.kernel.Specification;
import java.net.URL;
import java.util.*;
import java.util.Hashtable;

/**
 * Insert the type's description here.
 * Creation date: (03.08.00 15:05:05)
 * @author:
 */
public abstract class InOut {
    ApplicationControl ac = null;
    /**
     * name of the file format
     */
    public static String inOutName = "name unset";
    /**
     * the standard file extension
     */
    public static String stdFileExt = "*";
    /**
     * is set to true, if it's possible to save
     * multiple nets per file for this file format
     */
    public static Boolean multipleAllowed = new Boolean(false);

    /**
     * Insert the method's description here.
     * Creation date: (03.08.00 15:07:06)
     */
    public InOut(ApplicationControl ac) {
        this.ac = ac;
    }

    /**
     * Insert the method's description here.
     * Creation date: (03.08.00 15:07:06)
     */
    abstract Vector load (URL theURL);

    /**
     * Insert the method's description here.
     * Creation date: (03.08.00 15:07:06)
     */
    abstract void save (Vector theNets, URL theURL);

    public String xxxgetInOutName() {
        String name = null;
        try {
            name = (String) this.getClass().getDeclaredField("inOutName").get(null);
            D.d("++++++++++ InOut: " + name);
        } catch (NoSuchFieldException e) {
            D.d("NoSuchFieldException: " + e.toString());
            System.exit(0);
        } catch (IllegalAccessException e) {
            D.d("IllegalAccessException: " + e.toString());
            System.exit(0);
        }
        return name;
    }
}
