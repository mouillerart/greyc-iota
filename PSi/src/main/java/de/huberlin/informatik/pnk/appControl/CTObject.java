package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.appControl.base.D;
import java.lang.reflect.*;
import java.util.Vector;

public class CTObject {
    /**
     * stores the classname of the desired object
     */
    protected String classname = null;
    /**
     * stores the application control object
     */
    protected ApplicationControl applicationControl = null;
    /**
     * stores the constructor of the class
     */
    protected Constructor con = null;
    /**
     * stores the class of the desired object
     */
    protected Class mainclass = null;
    /**
     * stors the allowed nettypes...
     */
    private Vector allowedNettypes = null;

    /**
     * CTObject constructor comment.
     */
    public CTObject(ApplicationControl ac, String classname, Vector netTypes) {
        this.applicationControl = ac;
        this.classname = classname;
        this.allowedNettypes = netTypes;

        this.mainclass = new PNKClassLoader(classname).getMainClass();
        if (this.mainclass != null) {
            try {
                this.con = this.mainclass.getConstructor(new Class[] {ApplicationControl.class});
            } catch (NoSuchMethodException e) {
                D.d("IOTObject: NoSuchMethodException: " + e.toString());
            }
        } else {
            this.isReady = false;
        }
    }     // CTObject Constructor

    public void addAllowedNettype(NTObject nto) {
        if (!allowedNettypes.contains(nto)) {
            allowedNettypes.add(nto);
        } else {
            D.d("Nettype already known...", 2);
        }
    }

    public Vector getAllowedNettypes() {
        return allowedNettypes;
    }

    public boolean isReady() {
        return this.isReady;
    }

    /**
     * isReady
     */
    protected boolean isReady = true;
}