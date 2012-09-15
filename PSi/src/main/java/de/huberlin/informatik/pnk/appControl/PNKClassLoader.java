package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.appControl.base.D;

/**
 * Insert the type's description here.
 * Creation date: (13.10.00 01:44:13)
 * @author:
 */
/**
 * load a class object specified the class name
 */
public class PNKClassLoader {
    /**
     * name of the class
     */
    public String classname;
    /**
     * class object corresponding to the class name
     */
    public Class mainclass = null;
/**
 * construct a class object specified by classname (if the class exists)
 */
    public PNKClassLoader(String classname) {
        this.classname = classname;

        // Pending: Hier Fallunterscheidungen für verschiedene url-formate...
        // #############################

        ClassLoader cl = ClassLoader.getSystemClassLoader();
        try {
            this.mainclass = cl.loadClass(classname);
        } catch (ClassNotFoundException e) {
            D.d("   Error: Class not found: " + classname);
        }
        //D.d("CTObject: " + classname + " " + this.mainclass.getClass().getName());
        //D.d("CTObject: " + this.mainclass);
    }

/**
 * returns the loaded class object
 */
    public Class getMainClass() {
        return this.mainclass;
    }
}
