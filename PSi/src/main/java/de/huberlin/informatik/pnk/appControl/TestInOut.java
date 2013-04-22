package de.huberlin.informatik.pnk.appControl;

import java.net.URL;
import java.util.Vector;

public class TestInOut extends InOut {
    public static String inOutName = "TestInOut";
    public static Boolean multipleAllowed = new Boolean(true);
    public static String stdFileExt = "xml,abc";
/**
 * Insert the method's description here.
 * Creation date: (03.08.00 15:07:06)
 */
    public TestInOut(ApplicationControl ac) {
        super(ac);
    }

    public Vector load(URL theURL) {
        return new Vector();
    }

    public void save(Vector theNets, URL theURL) {}
}
