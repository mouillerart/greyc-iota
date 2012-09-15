package de.huberlin.informatik.pnk.appControl;

import java.util.Enumeration;
import java.util.Observable;
import java.util.Vector;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.base.*;
import de.huberlin.informatik.pnk.kernel.*;
import de.huberlin.informatik.pnk.kernel.base.NewNetAction;

public class ANObject extends Object {
    // ##### Verwaltet Aktives Netz...

    // Netz... und Typ.
    private Graph net = null;
    // ##### Verwaltet Aktives Netz...

    // Netz... und Typ.
    private int netID = -1;
    private NTObject nettype = null;
    private NewNetAction newNetAction = null;

    // beteiligte Applikationen... (AAObjects)
    private Vector applicationobjects = new Vector();
    // beteiligte Applikationen... (AAObjects)
    private Vector netFiles = new Vector();
    // beteiligte Applikationen... (AAObjects)
    private MetaApplication activeApplication = null;

    ANObject(Graph graph, NTObject nettype) {
        this.net = graph;
        this.nettype = nettype;
        // ########## Hier die DynamicExtensionTags verwalten!!!
    }

    public void addApplication(AAObject application) {
        // Sicherheitsabfrage (doppelt)
        //D.d("### ANObject.addApplication: " + application);
        if (this.applicationobjects.contains(application)) {
            D.d("####### Fehler: Applikation schon beim Netz angemeldet!!!");
            return;
        }
        // ########### PENDING Die nächste Zeile muß weg und woanders hin...
        //application.addNet(this);
        if (newNetAction != null) {
            // ########## PENDING Hier den Tag der Application
            // (für dynamic Extensions!!!) setzen!!!
            Enumeration e = applicationobjects.elements();
            int i = 0;
            ATObject ato = application.getAppType();
            while (e.hasMoreElements()) {
                AAObject aao = (AAObject)e.nextElement();
                if (ato == aao.getAppType() && i < aao.getInstanceKey()) {
                    i = aao.getInstanceKey();
                }
            }
            application.setInstanceKey(i + 1);
            //D.d("ANO:addApp: " + (i+1) + " " + ato.getApptypeName());
            this.applicationobjects.addElement(application);
            newNetAction.performAction(application.getApplication(), this.net);
        } else {
            this.applicationobjects.addElement(application);
        }
    }

    public void addNetFile(NFObject nfo) {
        //this.netFiles.addElement(nfo);
        if (!netFiles.contains(nfo)) {
            this.netFiles.add(nfo);
        } else {
            D.d("### Enthält das NFO schon!");
        }
    }

    public MetaApplication getActiveApp() {
        return this.activeApplication;
    }

    /**
     * Die ApplicationControl fragt nach den Applikationen des Netzes
     *
     * @return     Vektor mit den Namen der erlaubten Applikationen (Typ AAObject)
     */
    public Vector getAllowedApplications() {
        Vector v1 = nettype.getAllowedApplications();
        int anz[] = new int[v1.size()];
        for (int i = 0; i < v1.size(); i++) {
            anz[i] = ((ATObject)v1.get(i)).getMaxInstances();
        }
        //D.d("*** ANObject.getAllowedApplications: " + anz);
        for (int i = 0; i < applicationobjects.size(); i++) {
            int j = v1.indexOf(((AAObject)applicationobjects.get(i)).getATObject());
            if (j != -1) {
                anz[j] = anz[j] - 1;
            }
        }
        //D.d("*** ANObject.getAllowedApplications: " + anz);

        Vector v2 = new Vector(v1.size());
        for (int i = 0; i < anz.length; i++) {
            if (anz[i] > 0) {
                v2.add(v1.get(i));
            }
        }
        //D.d("*** ANObject.getAllowedApplications: " + anz + v2);

        return v2;

//		return v1;
    }

    /**
     * Die ApplicationControl fragt nach den Applikationen des Netzes
     *
     * @return     Vektor mit den Applikationen (Typ AAObject)
     */
    public Vector getApplications() {
        return this.applicationobjects;
    }

    public Graph getNet() {
        return this.net;
    }

    /**
     * gibt die URLs der Netzdateien zurück...
     */
    public Vector getNetFiles() {
        return netFiles;
    }

    /**
     * ACResources fragt nach der NetID
     *
     * @return     int netID
     */
    public int getNetID() {
        return this.netID;
    }

    public String getNetName() {
        return this.net.getName();
    }

    public String getNettypeName() {
        String s = nettype.getNettypeName();
        if (nettype.isParametric()) {
            s = s + "(";
            Enumeration e = parameters.elements();
            if (e.hasMoreElements()) {
                s = s + (String)e.nextElement();
            }
            while (e.hasMoreElements()) {
                s = s + ", " + (String)e.nextElement();
            }
            s = s + ")";
        }
        return s;
    }

    public NTObject getNTObject() {
        return this.nettype;
    }

    public void removeApplication(AAObject application) {
        // ##### Sicherheitsabfrage (vorhanden) fehlt...
        this.applicationobjects.remove(application);
        if (activeApplication == application.getApplication()) {
            if (applicationobjects.size() != 0) {
                activeApplication = ((AAObject)applicationobjects.get(0)).getApplication();
            } else {
                activeApplication = null;
            }
        }
    }

    public void removeNetFile(NFObject nfo) {
        //this.netFiles.addElement(nfo);
        if (netFiles.contains(nfo)) {
            this.netFiles.remove(nfo);
        } else {
            D.d("### Datei bei NFO unbekannt!");
        }
    }

    public void save() {
        Enumeration o = applicationobjects.elements();
        while (o.hasMoreElements()) {
            AAObject aao = (AAObject)(o.nextElement());
            D.d("ANO: save: " + aao.getApplicationName());
            aao.save(this.net);
        }
        //D.d("... ANO.save: beendet!");
    }

    public void setActiveApp(MetaApplication app) {
        this.activeApplication = app;
    }

    /**
     * ACResources fragt nach der NetID
     *
     * @return     int netID
     */
    public void setNetID(int netID) {
        this.netID = netID;
    }

    public void update(Observable netobject, Object actionobject) {
        //D.d("   ANO: Update: " + applicationobjects);
        Enumeration o = applicationobjects.elements();
        while (o.hasMoreElements()) {
            AAObject aao = (AAObject)(o.nextElement());
            aao.update(netobject, actionobject);
        }
        if (actionobject.getClass().getName().endsWith(".NewNetAction")) {
            //D.d("%%%%%% " + actionobject);
            newNetAction = (NewNetAction)actionobject;
        }
    }

    private Vector parameters;      ANObject(Graph graph, NTObject nettype, Vector parameters) {
        this.net = graph;
        this.nettype = nettype;
        this.parameters = parameters;
        // ########## Hier die DynamicExtensionTags verwalten!!!
    }       public String getNettypeLongName() {
        String s = getNettypeName();
        if (nettype.isDynamic()) {
            s = s + " <dynamic>";
        }
        return s;
    }
}
