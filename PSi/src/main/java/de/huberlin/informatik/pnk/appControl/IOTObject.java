package de.huberlin.informatik.pnk.appControl;

import java.lang.reflect.*;
import java.util.Vector;

import de.huberlin.informatik.pnk.appControl.base.*;


/**
 * Management of file formats (IO types).
 * PNK file formats are subclasses of InOut.
 */
public class IOTObject extends CTObject {
    /**
     * contains the in/out name (the file format description)
     */
    public String inOutName = "unknown";
    public PnkFileFilter fileFilter = null;
    /**
     * multipleAllowed is true if more than one net can be stored or loaded at once
     * for a file format
     */
    public static Boolean multipleAllowed;
    /**
     * contains the standard file extension of a file format
     */
    public String stdFileExt = "*";

    /**
     * Construct a new IOTObject for an InOut file format specified by classname.
     * The application control object and a vector of all allowed net types must be passed.
     */
    IOTObject(ApplicationControl ac, String classname, Vector netTypes) {
        super(ac, classname, netTypes);
        //D.d("IOTObject: " + classname);
        //D.d("IOTObject: Class: " + this.mainclass);

        if (this.mainclass != null) {
            Field f = null;
            try {
                f = this.mainclass.getDeclaredField("inOutName");
            } catch (NoSuchFieldException e) {
                D.d("NoSuchFieldException: " + e.toString());
            }
            try {
                this.inOutName = (String)f.get(null);
            } catch (IllegalAccessException e) {
                D.d("IllegalAccessException: " + e.toString());
            }
            //D.d("------------------> InOutName: " + this.inOutName);

            try {
                f = this.mainclass.getDeclaredField("stdFileExt");
            } catch (NoSuchFieldException e) {
                D.d("NoSuchFieldException: " + e.toString());
            }
            try {
                this.stdFileExt = (String)f.get(null);
            } catch (IllegalAccessException e) {
                D.d("IllegalAccessException: " + e.toString());
            }
            //D.d("------------------> StdFileExt: " + this.stdFileExt);

            try {
                f = this.mainclass.getDeclaredField("multipleAllowed");
            } catch (NoSuchFieldException e) {
                D.d("NoSuchFieldException: " + e.toString());
            }
            try {
                this.multipleAllowed = (Boolean)f.get(null);
            } catch (IllegalAccessException e) {
                D.d("IllegalAccessException: " + e.toString());
            }
            //D.d("------------------> StdFileExt: " + this.stdFileExt);
        }
        this.fileFilter = new PnkFileFilter(this.inOutName, this.stdFileExt);
    }

    /**
     * returns the file filter used
     */
    public PnkFileFilter getFileFilter() {
        return fileFilter;
    }

    /**
     * returns the status of inOutName
     */
    public String getInOutName() {
        return inOutName;
    }

    public InOut getNewInOut() {
        // gibt neue Instanz dieser Applikation zurück.
        // ##### Class c =  new FactoryURLClassLoader().loadClass(this.url, true);

        InOut io = null;
        try {
            io = (InOut) this.con.newInstance(new Object[] {this.applicationControl});
        } catch (InstantiationException e) {
            D.d("IOTObject: InstantiationException: " + e.toString());
            return null;
        } catch (IllegalAccessException e) {
            D.d("IOTObject: IllegalAccessException: " + e.toString());
            return null;
        } catch (IllegalArgumentException e) {
            D.d("IOTObject: IllegalArgumentException: " + e.toString());
            return null;
        } catch (InvocationTargetException e) {
            D.d("IOTObject: InvocationTargetException: " + e.toString());
            return null;
        }

        return io;
    }

    /**
     * returns the status of multipleAllowed
     */
    public boolean multipleAllowed() {
        return multipleAllowed.booleanValue();
    }
}
