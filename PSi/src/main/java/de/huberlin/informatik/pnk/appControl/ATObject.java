package de.huberlin.informatik.pnk.appControl;

import java.lang.reflect.*;
import java.util.Vector;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.base.*;

/**
 * Management of application types.
 * Applications are subclasses of MetaApplication.
 */

public class ATObject extends CTObject {
    private String appName = "name unset";
    private int maxInstances = 1;

    ATObject(ApplicationControl ac, String classname, int maxinst, Vector netTypes) {
        super(ac, classname, netTypes);
        this.maxInstances = maxinst;
        //D.d("ATObject: " + url);
        //D.d("ATObject: Class: " + this.mainclass);

        if (this.mainclass != null) {
            Field f = null;
            try {
                f = this.mainclass.getDeclaredField("staticAppName");
            } catch (NoSuchFieldException e) {
                D.d("NoSuchFieldException: " + e.toString());
            }
            try {
                this.appName = (String)f.get(null);
                //D.d("ATObject: InitialAppName: " + this.appName);
            } catch (IllegalAccessException e) {
                D.d("IllegalAccessException: " + e.toString());
            }
            //D.d("ATObject: NetTypes: " + netTypes);
            if (this.appName == null) {
                this.isReady = false;
            }
            //this.appName = this.mainclass.getgetInitialAppName
        }
    }

    protected String getApptypeName() {
        return this.appName;
    }

    protected int getMaxInstances() {
        return this.maxInstances;
    }

    protected MetaApplication getNewApp() {
        // gibt neue Instanz dieser Applikation zurück.
        // ##### Class c =  new FactoryURLClassLoader().loadClass(this.url, true);

        MetaApplication ma = null;
        try {
            ma = (MetaApplication) this.con.newInstance(new Object[] {this.applicationControl});
        } catch (InstantiationException e) {
            D.d("ATObject: InstantiationException: " + e.toString());
            return null;
        } catch (IllegalAccessException e) {
            D.d("ATObject: IllegalAccessException: " + e.toString());
            return null;
        } catch (IllegalArgumentException e) {
            D.d("ATObject: IllegalArgumentException: " + e.toString());
            return null;
        } catch (InvocationTargetException e) {
            D.d("ATObject: InvocationTargetException: " + e.toString());
            return null;
        }

        return ma;
    }

    protected boolean getStartImmediate() {
        if (this.mainclass != null) {
            Class c = this.mainclass;
            while (c != null) {
                try {
                    //D.d(this.mainclass.getDeclaredField("startImmediate").getBoolean(null))
                    return this.mainclass.getDeclaredField("startImmediate").getBoolean(null);
                } catch (NoSuchFieldException e) {
                    //D.d("ATO: imm NoSuchFieldException: " + e.toString());
                } catch (IllegalAccessException e) {
                    //D.d("ATO: imm IllegalAccessException: " + e.toString());
                }
                c = c.getSuperclass();
            }
        }
        return false;
    }
}
