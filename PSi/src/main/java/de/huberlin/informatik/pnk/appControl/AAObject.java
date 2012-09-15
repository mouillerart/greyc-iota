package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.kernel.base.*;
import de.huberlin.informatik.pnk.kernel.base.NetObserver;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Vector;
import javax.swing.*;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.base.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * Verwaltet aktive Applikationen...
 */

public class AAObject extends Object {
    // Applikation...
    private MetaApplication application = null;
    // Applikationstyp
    private ATObject apptype = null;
    // Eindeutige Applikations-ID (für Menu!)
    private int appID = -1;
    // Zwischenablage für Applikationsmenu...
    private JMenu menu[] = null;

    // beteiligte Netze (ANObject)
    // Das erste Netz ist das Netz, mit dem die Applikation gestartet wurde.
    private Vector activeNets = new Vector();

    AAObject(MetaApplication application, ATObject apptype) {
        this.application = application;
        this.apptype = apptype;
        //this.menu = this.application.getMenus();
    }

    protected void setMenus() {
        this.menu = this.application.getMenus();
        D.d("AAO: setMenu: " + this.menu);
    }

    protected void addNet(ANObject netobject) {
        //D.d("### AAObject.addNet: " + netobject);
        if (this.activeNets.contains(netobject)) {
            D.d("####### Fehler: Netz schon bei Applikation angemeldet!!!");
            return;
        }
        this.activeNets.addElement(netobject);
    }

    /**
     * ACResources fragt nach der ApplikationID
     *
     * @return     int appID
     */
    protected int getAppID() {
        return this.appID;
    }

    /**
     * ACResources fragt nach den Netzen der Applikation
     *
     * @return     MetaApplication application
     */
    protected MetaApplication getApplication() {
        return this.application;
    }

    protected String getApplicationName() {
        return this.application.getApplicationName();
    }

    // Menu der Applikation...
    protected JMenu[] getAppMenu() {
        return this.menu;
    }     // getAppMenu

    // Typ der Applikation...
    protected ATObject getAppType() {
        return this.apptype;
    }     // getAppType

    /**
     * ACResources fragt nach dem Typ der Applikation
     *
     * @return     zugehöriges ATObject (Application Type Object)
     */
    protected ATObject getATObject() {
        return this.apptype;
    }

    public String getInitialAppName() {
        return this.application.getInitialAppName();
    }

    protected int getInstanceKey() {
        return this.application.getInstanceKey();
    }

    /**
     * ACResources fragt nach den Netzen der Applikation
     *
     * @return     Vektor mit den Netzen (Typ ANObject)
     */
    public Graph getNet() {
        if (this.activeNets.size() != 0) {
            return ((ANObject) this.activeNets.get(0)).getNet();
        } else {
            return null;
        }
    }

    /**
     * ACResources fragt nach den Netzen der Applikation
     *
     * @return     Vektor mit den Netzen (Typ ANObject)
     */
    protected Vector getNets() {
        return this.activeNets;
    }

    protected void quitApplication() {
        // Applikation soll aufräumen...
        this.application.quitApp();
        // Application bei Netzen abmelden...
        D.d("AAObject: Quit Application: " + getApplicationName(), 2);
        //D.d("*** " + this.activeNets + " " + this.activeNets.size());
        Enumeration e = this.activeNets.elements();
        while (e.hasMoreElements()) {
            ANObject ano = (ANObject)e.nextElement();
            //D.d("*** " + ano);
            ano.removeApplication(this);
        }
        this.activeNets = null;
        this.menu = null;
        this.apptype = null;
    }

    protected void removeNet(ANObject netobject) {
        // ########## Pending: Wenn das erste Netz im Vector geschlossen wird,
        // wird auch die Applikation geschlossen???????????????????????
        this.activeNets.remove(netobject);
    }

    protected void save(Graph graph) {
        application.save(graph);
    }

    protected void setAppID(int appID) {
        this.appID = appID;
    }

    // Eine Applikation setzt ihr Menu...
    protected void setAppMenu(JMenu menu[]) {
        D.d("AAO.setAppMenu: " + menu);
        this.menu = menu;
    }     // setAppMenu

    protected void setInstanceKey(int ik) {
        this.application.setInstanceKey(ik);
    }

    protected void setName(String name) {
        this.application.setApplicationName(name);
    }

    protected void update(Observable netobject, Object actionobject) {
        MetaApplication app = getApplication();
        // Benachrichtigung des Initiators vermeiden:
        if (app != ((ActionObject)actionobject).getInitiator()) {
            if (app instanceof NetObserver) {
                ((NetObserver)app).update(netobject, actionobject);
            }
        }
    }

    protected boolean getStartImmediate() {
        return this.apptype.getStartImmediate();
    }
}
