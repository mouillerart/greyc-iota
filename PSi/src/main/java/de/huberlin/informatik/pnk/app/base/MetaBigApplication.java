package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JMenu;

import de.huberlin.informatik.pnk.appControl.base.*;
public abstract class MetaBigApplication extends MetaApplication
implements ApplicationRequests, ApplicationACInterface, ApplicationAWInterface {
    public static String staticAppName = "unnamed (MetaBigApplication)";
    private boolean editable = true;
    private Vector windows = new Vector();
    private MetaJFrame focusedWindow = null;

    public MetaBigApplication(ApplicationControl ac) {
        super(ac);
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

    // #################### Fensterverwaltung
    // #################### ApplicationAWInterface

    /*
     * Schliesen aller Fenster der Application durch die ApplicationControl
     */
    protected void closeWindows() {
        // #################### Das könnte schiefgehen.................
        Enumeration e = this.windows.elements();
        while (e.hasMoreElements()) {
            ((MetaJFrame)e.nextElement()).closeWindow();
        }
    }

    /**
     * Get the ApplicationControl.
     * @return ApplicationControl.
     */
    public ApplicationControl getApplicationControl() {
        return applicationControl;
    }

    /**
     * Die ApplicationControl fragt, ob ein Fenster der MetaApplication
     * den Focus hat.
     *
     * @return     das Fenster mit dem Focus
     */
    protected MetaJFrame getFocusedWindow() {
        return this.focusedWindow;
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

    public Vector getWindows() {
        return this.windows;
    }

    // #################### Fensterverwaltung
    // #################### ApplicationAWInterface

    /*
     * Schliesen aller Fenster der Application durch die ApplicationControl
     */
    protected void hideApplication() {
        Enumeration e = this.windows.elements();
        while (e.hasMoreElements()) {
            ((MetaJFrame)e.nextElement()).hideWindow();
        }
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
        // ohne Röcksicht auf eventuelle Monitore,
        // die geunlocked werden.
        //stop();
    }

    public void quitMe() {
        this.applicationControl.quitApp(this);
    }

    /**
     * Ein MetaWindow registriert sich automatisch mit dieser Methode
     * bei der MetaApplication
     *
     * @param mf    das MetaFrame welches sich anmeldet
     */
    protected void registerWindow(MetaJFrame mf) {
        this.windows.addElement(mf);
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
        super.setApplicationName(name);
        Enumeration e = this.windows.elements();
        while (e.hasMoreElements()) {
            ((MetaJFrame)e.nextElement()).setWindowName();
        }
    }

    // Interface ApplicationRequests
    public void setEditable() {
        this.editable = true;
    }

    /**
     * Ein MetaFrame benachrichtigt die MetaApplication,
     * dass es den Fokus erhalten hat.
     *
     * @param mf    das MetaFrame welches nun den Fokus besitzt
     */
    protected void setFocusedWindow(MetaJFrame mf) {
        // de.huberlin.informatik.pnk.appControl.base.D.d("MetaApp: setFocusedWindow " + this + " " + this.getApplicationName());
        this.focusedWindow = mf;
        this.applicationControl.setFocusedApplication(this);
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

    public void setWindowName(String name) {}

    /**
     * Diese Methode wird von ac aufgerufen,
     * sie startet den eigentlichen Thread....
     */
    public void startApp() {
        de.huberlin.informatik.pnk.appControl.base.D.d("MetaApplication: startApp");
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
            //de.huberlin.informatik.pnk.appControl.base.D.d("do nat start as Thread");
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

    public void toFront() {
        if (this.focusedWindow != null) {
            this.focusedWindow.toFront();
        }
    }

    // #################### MiniApplikation...
    // #################### wie heißt das Interface???
    // #################### Fensterverwaltung
    // #################### ApplicationAWInterface

    /*
     * Schliesen aller Fenster der Application durch die ApplicationControl
     */
    protected void unhideApplication() {
        Enumeration e = this.windows.elements();
        while (e.hasMoreElements()) {
            ((MetaJFrame)e.nextElement()).unhideWindow();
        }
    }

    /**
     * Ein MetaFrame traegt sich automatisch bei Aufruf seiner Methode
     * closeWindow() aus.
     *
     * @param    das MetaFrame welches sich abmeldet
     */
    protected void unregisterWindow(MetaJFrame mf) {
        this.windows.remove(mf);
    }

    private void closeAllNets() {
        this.closeNet();
        this.extraNets.clear();
    }
}
