package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;
import java.util.Vector;
import javax.swing.JMenu;

import de.huberlin.informatik.pnk.appControl.base.*;

public abstract class MetaApplication extends Thread
implements ApplicationRequests, ApplicationACInterface {
    public static String staticAppName = "unnamed (MetaApplication)";
    protected String tagName = "unnamed";
    protected String appName = "unnamed";
    public boolean letrun = true;
    protected int instanceKey = 0;
    private boolean editable = true;
    public boolean startAsThread = false;
    public Graph net = null;
    public ApplicationControl applicationControl = null;
    public static boolean startImmediate = false;
    protected Vector extraNets = new Vector();
    protected JMenu[] menus = null;

    public MetaApplication(ApplicationControl ac) {
        super();
        this.applicationControl = ac;
        Class c = this.getClass();
        while (true) {
            try {
                this.appName = (String) this.getClass().getDeclaredField("staticAppName").get(null);
                //de.huberlin.informatik.pnk.appControl.base.D.d("++++++++++ App: " + this.getClass().getDeclaredField("staticAppName").get(null));
                break;
            } catch (NoSuchFieldException e) {
                //de.huberlin.informatik.pnk.appControl.base.D.d("NoSuchFieldException: " + e.toString());
            } catch (IllegalAccessException e) {
                //de.huberlin.informatik.pnk.appControl.base.D.d("IllegalAccessException: " + e.toString());
            }
            c = c.getSuperclass();
        }
    }

    protected void closeNet() {
        this.net = null;
    }

    private void closeNet(Graph net) {
        if (this.net == net) {
            this.closeNet();
        } else {
            if (this.extraNets.contains(net)) {
                this.extraNets.remove(net);
            } else {
                de.huberlin.informatik.pnk.appControl.base.D.d("### unbekanntes Netz: " + net);
            }
        }
    }

    /**
     * Get the ApplicationControl.
     * @return ApplicationControl.
     */
    public ApplicationControl getApplicationControl() {
        return applicationControl;
    }

    public final String getApplicationName() {
        return this.appName;
    }

    // Interface ApplicationRequests
    public final String getInitialAppName() {
        Class c = this.getClass();
        while (true) {
            try {
                return (String) this.getClass().getDeclaredField("staticAppName").get(null);
                //de.huberlin.informatik.pnk.appControl.base.D.d("++++++++++ App: " + this.getClass().getDeclaredField("staticAppName").get(null));
            } catch (NoSuchFieldException e) {
                //de.huberlin.informatik.pnk.appControl.base.D.d("NoSuchFieldException: " + e.toString());
            } catch (IllegalAccessException e) {
                //de.huberlin.informatik.pnk.appControl.base.D.d("IllegalAccessException: " + e.toString());
            }
            c = c.getSuperclass();
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (23.2.2001 02:58:39)
     * @return java.lang.String
     */
    public final int getInstanceKey() {
        return instanceKey;
    }

    // #################### MiniApplikation...
    // #################### wie heißt das Interface???

    /**
     * Die ApplicationControl ruft diese Methode auf,
     * um die Application zu starten.
     */
    public JMenu[] getMenus() {
        // Start-Stop-Menu bauen... ##########
        //Vector v = new Vector();
        //Object o[] = {new Integer(0), new Object[] {new Integer(2)} };
        //v = new Vector({new Integer(0)});

        Object[] sep = {ApplicationControlMenu.MENU_SEPARATOR};
        // Entrys
        Object[] e1 = {ApplicationControlMenu.MENU_ENTRY, ApplicationControlMenu.MENU_ACTIVE, "Start", this, "startApp"};
        Object[] e2 = {ApplicationControlMenu.MENU_ENTRY, ApplicationControlMenu.MENU_ACTIVE, "Stop", this, "stopApp"};
        Object[] e3 = {ApplicationControlMenu.MENU_ENTRY, ApplicationControlMenu.MENU_ACTIVE, "Quit", this, "quitMe"};
        // Menus
        Object[] m1 = {this.appName, ApplicationControlMenu.MENU_ACTIVE, e1, e2, sep, e3};
        // Object[] m1 = {this.appName, ApplicationControlMenu.MENU_ACTIVE, e1, e2};

        return this.applicationControl.setMenu(this, new Object[] {m1});
    }

    // #?????????????????????????????

    public Graph getNet() {
        return this.net;
    }

    // #################### Fensterverwaltung
    // #################### ApplicationAWInterface

    /*
     * Schliesen aller Fenster der Application durch die ApplicationControl
     */
    public String getTagName() {
        return this.tagName;
    }

    // Interface ApplicationRequests
    public boolean isEditable() {
        return this.editable;
    }

    //****************************************************************************************************
    // interface ApplicationRequest
    //****************************************************************************************************

    public void newNet(Graph net) {
        if (this.net == null) {
            this.net = net;
        } else {
            if (this.net == net) {
                de.huberlin.informatik.pnk.appControl.base.D.d("MetaApplication.setNet: kennen wir schon! " + net);
            } else if (this.extraNets.contains(net)) {
                de.huberlin.informatik.pnk.appControl.base.D.d("MetaApplication.setNet: Bekanntes extra Netz! " + net);
            } else {
                de.huberlin.informatik.pnk.appControl.base.D.d("MetaApplication.setNet: extra Netz! " + net);
                this.extraNets.add(net);
            }
        }
        this.setNet(net);
    }

    //****************************************************************************************************
    // interface ApplicationRequest
    //****************************************************************************************************

    public void newNet(Net net) {
        newNet((Graph)net);
    }

    public void newNet(Net net, SpecificationTable specification) {
        newNet((Graph)net);
    }

    /**
     * method is called when app quits.
     * It's for closing windows or files etc.
     */
    public void quit() {
        // Hier die Sachen, die die App noch machen will soll muß
        D.d("MetaApp.quit");
    }

    /**
     * Die ApplicationControl ruft diese Methode auf,
     * um die Application zu beenden.
     */
    public void quitApp() {
        D.d("MetaApplication: quitApp: " + this.getApplicationName());
        // quit ist für die Applikation...
        this.quit();
        // Alle Netze schließen...
        this.closeAllNets();
        // ruft die nicht-empfohlene Methode stop von Thread auf...
        // die beendet den Thread unsanft
        // ohne Rücksicht auf eventuelle Monitore,
        // die geunlocked werden.
        //stop();
    }

    public void quitMe() {
        D.d("MA: quitMe.");
        this.applicationControl.quitApp(this);
    }

    /**
     * Diese Methode wird von start aufgerufen,
     * sie ist der eigentliche Thread....
     */
    public void run() {
        de.huberlin.informatik.pnk.appControl.base.D.d("MetaApplication: run");
        // Beispielimplemetierung - siehe auch: stopApp()
        letrun = true;
        while (letrun) {
            de.huberlin.informatik.pnk.appControl.base.D.d("---------- die Applikation " + getApplicationName() + " läuft...");
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                de.huberlin.informatik.pnk.appControl.base.D.d("Oh, unterbrochen...");
            }
        }
    }

    // #################### MiniApplikation...
    // #################### wie heißt das Interface???

    /**
     * Die ApplicationControl ruft diese Methode auf,
     * um die Application zu starten.
     */
    public void save(Graph graph) {
        de.huberlin.informatik.pnk.appControl.base.D.d("MetaApplication: save " + graph);
    }

    // #################### Fensterverwaltung
    // #################### ApplicationAWInterface

    /*
     * Schliesen aller Fenster der Application durch die ApplicationControl
     */
    public void setApplicationName(String name) {
        // #################### Das könnte schiefgehen.................
        this.appName = name;
    }

    // Interface ApplicationRequests

    public void setEditable() {
        this.editable = true;
    }

    /**
     * Insert the method's description here.
     * Creation date: (23.2.2001 02:58:39)
     * @param newInstanceKey java.lang.String
     */
    public final void setInstanceKey(int newInstanceKey) {
        instanceKey = newInstanceKey;
    }

    /**
     * Die ApplicationControl ruft diese Methode auf,
     * um die Application zu starten.
     */
    public void setMenus() {
        this.menus = this.getMenus();
    }

    //****************************************************************************************************
    // interface ApplicationRequest
    //****************************************************************************************************

    public void setNet(Graph net) {
        // Hier soll die Applikation ihrs machen...
    }

    //****************************************************************************************************
    // interface ApplicationRequest
    //****************************************************************************************************

    public void setNet(Net net) {
        this.setNet((Graph)net);
    }

    // Interface ApplicationRequests

    public void setNotEditable() {
        this.editable = false;
    }

    // #################### Fensterverwaltung
    // #################### ApplicationAWInterface

    /*
     * Schliesen aller Fenster der Application durch die ApplicationControl
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Diese Methode wird von ac aufgerufen,
     * sie startet den eigentlichen Thread....
     */
    public void startApp() {
        D.d("MetaApplication: startApp");
        // start-Methode von Thread ruft run auf und kehrt sofort zurück...
        boolean s;
        Class c = this.getClass();
        while (true) {
            try {
                //de.huberlin.informatik.pnk.appControl.base.D.d("suche boolean..." + c.getName());
                s = c.getDeclaredField("startAsThread").getBoolean(this);
                //de.huberlin.informatik.pnk.appControl.base.D.d("++++++++++ Start as thread: " + s);
                break;
            } catch (NoSuchFieldException e) {
                //de.huberlin.informatik.pnk.appControl.base.D.d("NoSuchFieldException: " + e.toString());
            } catch (IllegalAccessException e) {
                //de.huberlin.informatik.pnk.appControl.base.D.d("IllegalAccessException: " + e.toString());
            }
            c = c.getSuperclass();
        }
        if (s) {
            //de.huberlin.informatik.pnk.appControl.base.D.d("start as Thread");
            start();
        } else {
            //de.huberlin.informatik.pnk.appControl.base.D.d("do not start as Thread");
            run();
        }
    }

    /**
     * Diese Methode wird von ac aufgerufen,
     * sie stoppt den Thread über eine Kontrollvariable...
     */
    public void stopApp() {
        // de.huberlin.informatik.pnk.appControl.base.D.d("MetaApplication: stopApp");
        letrun = false;
    }

    public void toFront() {}

    private void closeAllNets() {
        this.closeNet();
        this.extraNets.clear();
    }
}
