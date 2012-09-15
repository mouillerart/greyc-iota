package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.appControl.ApplicationControl;

abstract public class SimpleApplication
extends MetaApplication {
    public static String staticAppName = "unnamed (SimpleApplication)";

    public static boolean startAsThread = true;

    public SimpleApplication(ApplicationControl ac) {
        super(ac);
    }

    /**
     * The applications code for execution.
     * The ApplicationControl requests
     * this method to perform the application.
     */
    public abstract void run ();

    /**
     * ApplicationControl requests an application
     * to quit. Fill this method with code that
     * closes your application.
     */
    public void quit() {
        // Empty, if you don't overload this method
        // the application doesn't quit.
        de.huberlin.informatik.pnk.appControl.base.D.d("app.quit()");

        ;
    }
}
