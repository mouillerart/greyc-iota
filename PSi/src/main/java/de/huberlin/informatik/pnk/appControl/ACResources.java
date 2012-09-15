package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.kernel.base.NetObservable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import javax.swing.*;

import java.net.*;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.base.*;
import de.huberlin.informatik.pnk.kernel.*;

public class ACResources extends Object {
    // ##### Verwaltet Resourcen...
    //
    // Aktive Netze
    // AktiveApllikationen
    //
    // Netztypen
    // mögliche Applikationen
    // Zuordnungsliste der Erlaubten Netztypen einer Applikation...

    private ApplicationControl applicationControl = null;
    private Hashtable netTypes = new Hashtable();
    // Verwaltet NetFiles... (NFObject)
    // key: URL, element: NFObject
    private Hashtable netFiles = new Hashtable();
    private Hashtable appTypes = new Hashtable();
    private int netID = 0;
    private int dynSpecID = 0;
    private int appID = 0;
    private Hashtable ioformatTypes = new Hashtable();
    private Hashtable activeNets = new Hashtable();
    private Hashtable activeApplications = new Hashtable();
    private ToolSpecification toolSpecification = null;
    private ATObject standardApplication = null;
    private IOTObject standardIOFormat = null;
    private NTObject standardNettype = null;

    ACResources(ApplicationControl ac, URL tsURL) {
        this.applicationControl = ac;

        // D.d("### ACResources Anfang");

        this.toolSpecification = new ToolSpecification(tsURL);

        if (this.toolSpecification != null) {
            Hashtable tsNetTypes = this.toolSpecification.getNettypes();
            //D.d("ACResources: Nettypes: " + tsNetTypes.toString());
            Enumeration e = tsNetTypes.keys();
            while (e.hasMoreElements()) {
                String id = (String)e.nextElement();
                URL url = (URL)tsNetTypes.get(id);
                NTObject nto = new NTObject(ac, url);
                // If o.k.: register everywhere...
                if (nto.isReady()) {
                    // Hier nochmal testen, ob schon vorhanden!!!
                    if (!this.netTypes.containsKey(nto.getNettypeName())) {
                        this.netTypes.put(nto.getNettypeName(), nto);
                    } else {
                        D.d("### Error! Nettype doppelt??? " + nto.getNettypeName());
                    }
                    tsNetTypes.put(id, nto);
                } else {
                    D.d("### Error: Nettype not ready: " + url.toString());
                    tsNetTypes.remove(id);
                }
            }

            Enumeration ee;

            //D.d("ACResources: netTypes: " + this.netTypes);
            //D.d("ACResources: Netztypen");

            ee = this.netTypes.keys();
            if (ee.hasMoreElements()) {
                D.d(": " + ee.nextElement());
            }
            while (ee.hasMoreElements()) {
                D.d(", " + ee.nextElement());
            }
            D.d();
            //D.d("ACResources: netTypes2: " + tsNetTypes);

            Hashtable tsAppTypes = this.toolSpecification.getApptypes();
            //D.d("ACResources: Apptypes: " + tsAppTypes.toString());

            e = tsAppTypes.keys();
            while (e.hasMoreElements()) {
                String id = (String)e.nextElement();
                Vector v = (Vector)tsAppTypes.get(id);
                //D.d("AppType: " + v);

                String classname = (String)v.elementAt(0);
                int maxinst = ((Integer)v.elementAt(1)).intValue();
                Vector nt = (Vector)v.elementAt(2);

                // wenn nt == null, alle Netztypen erlaubt!
                Vector nt2 = new Vector();
                if (nt == null) {
                    //D.d("########## ALLE Netztypen!!!");
                    for (Enumeration en = netTypes.elements(); en.hasMoreElements(); ) {
                        nt2.add(en.nextElement());
                    }
                } else {
                    for (int i = 0; i < nt.size(); i++) {
                        Object oo = tsNetTypes.get(nt.elementAt(i));
                        if (oo != null) {
                            nt2.add(oo);
                        }
                    }
                }
                //D.d("########## appnettypes: " + nt2);

                if (nt != null) {
                    nt = nt2;
                }

                ATObject ato = new ATObject(ac, classname, maxinst, nt);
                // If o.k.: register everywhere...
                if (ato.isReady()) {
                    // ########## Hier nochmal testen, ob schon vorhanden!!!
                    if (!this.appTypes.containsKey(ato.getApptypeName())) {
                        this.appTypes.put(ato.getApptypeName(), ato);
                    } else {
                        D.d("### Error! Applicationtype doppelt??? " + ato.getApptypeName());
                    }
                    tsAppTypes.put(id, ato);

                    for (int i = 0; i < nt2.size(); i++) {
                        ((NTObject)nt2.elementAt(i)).addApplication(ato);
                    }
                }
            }

            //D.d("ACResources: appTypes: " + this.appTypes);
            //D.d("ACResources: Applikationen");
            ee = this.appTypes.keys();
            if (ee.hasMoreElements()) {
                D.d(": " + ee.nextElement());
            }
            while (ee.hasMoreElements()) {
                D.d(", " + ee.nextElement());
            }
            D.d();

            //D.d("ACResources: apptypes2: " + tsAppTypes);

            Hashtable tsIOTypes = this.toolSpecification.getIOFormattypes();
            //D.d("ACResources: IOtypes: " + tsIOTypes.toString());

            e = tsIOTypes.keys();
            while (e.hasMoreElements()) {
                String id = (String)e.nextElement();
                Vector v = (Vector)tsIOTypes.get(id);
                //D.d("IOType: " + v);

                String classname = (String)v.elementAt(0);
                Vector nt = (Vector)v.elementAt(1);

                // wenn nt == null, alle Netztypen erlaubt!
                Vector nt2 = new Vector();
                if (nt == null) {
                    for (Enumeration en = netTypes.elements(); en.hasMoreElements(); ) {
                        nt2.add(en.nextElement());
                    }
                    //D.d("########## ALLE Netztypen!!!");
                } else {
                    for (int i = 0; i < nt.size(); i++) {
                        Object oo = tsNetTypes.get(nt.elementAt(i));
                        if (oo != null) {
                            nt2.add(oo);
                        }
                    }
                }
                //D.d("########## ioformatnettypes: " + nt2);

                if (nt != null) {
                    nt = nt2;
                }

                IOTObject ioto = new IOTObject(ac, classname, nt);
                // If o.k.: register everywhere...
                if (ioto.isReady()) {
                    if (!this.ioformatTypes.containsKey(ioto.getInOutName())) {
                        this.ioformatTypes.put(ioto.getInOutName(), ioto);
                    } else {
                        D.d("### Error! InOuttype doppelt??? " + ioto.getInOutName());
                    }
                    tsIOTypes.put(id, ioto);

                    for (int i = 0; i < nt2.size(); i++) {
                        ((NTObject)nt2.elementAt(i)).addInOut(ioto);
                    }
                }
            }

            //D.d("ACResources: ioformatTypes: " + this.ioformatTypes);
            D.d("ACResources: InOut");
            ee = this.ioformatTypes.keys();
            if (ee.hasMoreElements()) {
                D.d(": " + ee.nextElement());
            }
            while (ee.hasMoreElements()) {
                D.d(", " + ee.nextElement());
            }
            D.d();
            //D.d("ACResources: ioformattypes2: " + tsIOTypes);

            // ########## PENDING abfangen wenn gar keine... oder kein Std...
            this.standardNettype = (NTObject)tsNetTypes.get(this.toolSpecification.getIdOfStandardNettype());
            if (this.standardNettype != null) {
                D.d("ACResources: Standardnetztyp: " + this.standardNettype.getNettypeName());
            }
            this.standardApplication = (ATObject)tsAppTypes.get(this.toolSpecification.getIdOfStandardApplication());
            if (this.standardApplication != null) {
                D.d("ACResources: Standardapplikation: " + this.standardApplication.getApptypeName());
            }
            this.standardIOFormat = (IOTObject)tsIOTypes.get(this.toolSpecification.getIdOfStandardIOFormat());
            if (this.standardIOFormat != null) {
                D.d("ACResources: StandardInOut: " + this.standardIOFormat.getInOutName());
            }
        } else {
            D.d("ACResources: no toolSpecification!!!");
        }

        // D.d("### ACResources Ende");
    }

    public void addApp(Graph net, MetaApplication app) {
        ((ANObject)activeNets.get(net)).addApplication((AAObject)activeApplications.get(app));
        ((AAObject)activeApplications.get(app)).addNet((ANObject)activeNets.get(net));
    }

    /**
     * Add new Nettype according to Hashtable
     * Returns name of Nettype - or null
     */
    public String addNewNettype(Hashtable specificationTable) {
        return addNewNettype(specificationTable, null);
    }

    /**
     * Add new Nettype according to Hashtable with name name
     * Returns name of Nettype - or null
     */
    protected String addNewNettype(Hashtable specificationTable, String name) {
        // get new Net with dynamic specificationTable...
        // First: NetTypeObject
        String n;
        if (name == null || name == "") {
            n = "dynamic Nettype <" + dynSpecID++ + ">";
        } else {
            n = name;
            if (netTypes.containsKey(n)) {
                D.d("Nettype with name " + n + " already exists, new name: " +
                    "dynamic Nettype <" + dynSpecID + ">");
                n = "dynamic Nettype <" + dynSpecID++ + ">";
            }
        }
        NTObject nto = new NTObject(applicationControl, specificationTable, n);
        if (nto.isReady()) {
            D.d("New Nettype: " + nto.getNettypeName());
            netTypes.put(nto.getNettypeName(), nto);
            // Allowed applications eintragen;-)
            Enumeration e = appTypes.elements();
            ATObject ato;
            while (e.hasMoreElements()) {
                ato = (ATObject)e.nextElement();
                if (ato.getAllowedNettypes() == null) {
                    D.d("AT erlaubt: " + ato.getApptypeName());
                    nto.addApplication(ato);
                } else {
                    D.d("AT nicht erlaubt: " + ato.getApptypeName());
                }
            }
            // Allowed InOuts eintragen;-)
            e = ioformatTypes.elements();
            IOTObject ioto;
            while (e.hasMoreElements()) {
                ioto = (IOTObject)e.nextElement();
                if (ioto.getAllowedNettypes() == null) {
                    D.d("IOT erlaubt: " + ioto.getInOutName());
                    nto.addInOut(ioto);
                } else {
                    D.d("IOT nicht erlaubt: " + ioto.getInOutName());
                }
            }
            return n;
        } else {
            D.d("Dynamic Specification corrupted!");
            return null;
        }
    }

    protected Graph closeNet(Graph net) {
        // Netz schließen...
        D.d("ACResources: Close Net: " + getNetLongName(net), 2);
        // Applikationen schließen
        ANObject ano = (ANObject) this.activeNets.get(net);
        Vector v = new Vector(ano.getApplications());
        // Seiteneffekt vermeiden - deshalb Kopie!
        for (int i = 0; i < v.size(); i++) {
            AAObject aao = (AAObject)v.get(i);
            // Applikationen beenden...
            aao.quitApplication();
            ano.removeApplication(aao);
        }
        // Netzdateien schließen...
        v = new Vector(ano.getNetFiles());
        // Seiteneffekt vermeiden - deshalb Kopie!
        for (int i = 0; i < v.size(); i++) {
            NFObject nfo = (NFObject)v.get(i);
            nfo.close();
            netFiles.remove(nfo.getFileURL());

            // Datei in die Liste der letzten eintragen...
            if (!lastNetFiles.contains(nfo.getFileURL())) {
                lastNetFiles.add(nfo.getFileURL());
                if (lastNetFiles.size() > 5) {
                    lastNetFiles.removeElementAt(0);
                }
            }
        }
        this.activeNets.remove(net);
        if (this.activeNets.size() == 0) {
            return null;
        } else {
            return ((ANObject) this.activeNets.elements().nextElement()).getNet();
        }
    } // protected void closeNet(net)

    protected Vector getAAObjects() {
        // get all AAObjects
        Vector a = new Vector();
        Enumeration e = activeApplications.elements();
        while (e.hasMoreElements()) {
            a.addElement((AAObject)e.nextElement());
        }
        return a;
    }

    protected MetaApplication getActiveApp(Graph net) {
        // get active Application of Net
        ANObject ano = (ANObject)activeNets.get(net);
        if (ano != null) {
            return ano.getActiveApp();
        } else {
            return null;
        }
    } // public MetaApplication getActiveApp(net)

    protected Vector getActiveApplications() {
        // get all active Applications
        Vector a = new Vector();
        Enumeration e = activeApplications.elements();
        while (e.hasMoreElements()) {
            a.addElement(((AAObject)e.nextElement()).getApplication());
        }
        return a;
    }

    protected Vector getActiveApplications(Graph graph) {
        // get all active Applications known by net net
        Vector a = new Vector();

        /*		Enumeration f = ((ANObject)activeNets.get(graph)).getApplications().elements();
           while (f.hasMoreElements()) {
           D.d("***************   " + ((AAObject) f.nextElement()).getApplicationName() );
           }
         */

        Enumeration e = ((ANObject)activeNets.get(graph)).getApplications().elements();
        while (e.hasMoreElements()) {
            a.addElement(((AAObject)e.nextElement()).getApplication());
        }
        return a;
    }

    protected Vector getStdApplications(Graph graph) {
        Vector a = new Vector();

        Enumeration e = ((ANObject)activeNets.get(graph)).getApplications().elements();
        while (e.hasMoreElements()) {
            AAObject aao = (AAObject)e.nextElement();
            if (aao.getAppType() == this.standardApplication) {
                a.addElement(aao.getApplication());
            }
        }
        return a;
    }

    protected Vector getActiveApplicationsOfType(Graph graph, String apptype) {
        Vector a = new Vector();

        Enumeration e = ((ANObject)activeNets.get(graph)).getApplications().elements();
        while (e.hasMoreElements()) {
            AAObject aao = (AAObject)e.nextElement();
            if (aao.getAppType() == appTypes.get(apptype)) {
                a.addElement(aao.getApplication());
            }
        }
        return a;
    }

    protected Vector getActiveNets() {
        // get all active Nets
        Vector v = new Vector();
        Enumeration e = activeNets.elements();
        while (e.hasMoreElements()) {
            v.addElement(((ANObject)e.nextElement()).getNet());
        }
        return v;
    }

    public Vector getActiveNets(MetaApplication a) {
        // get all active Nets known by app a
        AAObject aao = (AAObject) this.activeApplications.get(a);
        if (aao == null) {
            D.d("Applikation nicht gefunden!!!");
            return null;
        }
        return aao.getNets();
    }

    protected Vector getAllowedApplications(Graph graph) {
        // liefert alle erlaubten Applikationen eines Netztyps zurück
        Vector v = ((ANObject)activeNets.get(graph)).getAllowedApplications();
        Vector w = new Vector();
        for (int i = 0; i < v.size(); i++) {
            w.add(((ATObject)v.elementAt(i)).getApptypeName());
        }
        return w;
//		return v;
    }

    protected Vector getAllowedApplications(String netname) {
        // lifert alle erlaubten Applikationen eines Netztyps zurück
        Vector v = ((NTObject)netTypes.get(netname)).getAllowedApplications();
        Vector w = new Vector();
        for (int i = 0; i < v.size(); i++) {
            w.add(((ATObject)v.elementAt(i)).getApptypeName());
        }
        return w;
    }

    protected Vector getAllowedInOuts(Graph net) {
        // lifert alle erlaubten InOuts eines Netzes zurück
        Vector v = ((ANObject)activeNets.get(net)).getNTObject().getAllowedInOuts();
        Vector w = new Vector();
        for (int i = 0; i < v.size(); i++) {
            w.add(((IOTObject)v.elementAt(i)).getInOutName());
        }
        return w;
    }

    protected Vector getAllowedInOuts(String netname) {
        // lifert alle erlaubten InOuts eines Netztyps zurück
        Vector v = ((NTObject)netTypes.get(netname)).getAllowedInOuts();
        Vector w = new Vector();
        for (int i = 0; i < v.size(); i++) {
            w.add(((IOTObject)v.elementAt(i)).getInOutName());
        }
        return w;
    }

    protected Vector getANObjects() {
        // get all ANObjects
        Vector a = new Vector();
        Enumeration e = activeNets.elements();
        while (e.hasMoreElements()) {
            a.addElement((ANObject)e.nextElement());
        }
        return a;
    }

    protected String getAppID(MetaApplication app) {
        AAObject aao = (AAObject)activeApplications.get(app);
        if (aao != null) {
            return "" + aao.getAppID();
        } else {
            return null;
        }
    }

    protected boolean getStartImmediate(MetaApplication app) {
        return ((AAObject) this.activeApplications.get(app)).getStartImmediate();
    }

    protected String getApplicationName(MetaApplication app) {
        AAObject aao = (AAObject) this.activeApplications.get(app);
        if (aao == null) {
            D.d("Applikation nicht gefunden!!!");
            return null;
        }
        return aao.getApplicationName();
    }

    // Eine Applikation setzt ihr Menu...

    public JMenu[] getAppMenu(MetaApplication app) {
        AAObject aao = (AAObject) this.activeApplications.get(app);
        if (aao == null) {
            D.d("Applikation nicht gefunden!!!");
            return null;
        }
        return aao.getAppMenu();
    } // setAppMenu

    public String getAppNameAndInstanceKey(MetaApplication ma) {
        AAObject aao = (AAObject) this.activeApplications.get(ma);
        if (aao == null) {
            D.d("Applikation nicht gefunden!!!");
            return null;
        } else {
            return aao.getApplicationName() + " <" + aao.getInstanceKey() + ">";
        }
    }

    public Vector getAppTypes() {
        Vector a = new Vector();
        Enumeration e = appTypes.keys();
        while (e.hasMoreElements()) {
            a.addElement(e.nextElement());
        }
        return a;
    }

    /**
     * Insert the method's description here.
     * Creation date: (14.09.00 20:16:13)
     * @param name String
     */
    public PnkFileFilter getFileFilter(String name) {
        // get new InOut with name nettype
        IOTObject ioto = (IOTObject)ioformatTypes.get(name);
        PnkFileFilter ff = ioto.getFileFilter();

        // ######### sollen aktive InOuts auch verwaltet werden???
        //AIOObject aioo = new AIOObject(io, ioto);
        //activeInOuts.put(io, aioo);

        return ff;
    }

    public Vector getFileNets(URL name) {
        Enumeration e = ((NFObject) this.netFiles.get(name)).getFileNets().elements();
        Vector v = new Vector();
        while (e.hasMoreElements()) {
            v.add(((ANObject)e.nextElement()).getNet());
        }
        return v;
    }

    public int getInstanceKey(MetaApplication ma) {
        AAObject aao = (AAObject) this.activeApplications.get(ma);
        if (aao == null) {
            D.d("Applikation nicht gefunden!!!");
            return 0;
        } else {
            return aao.getInstanceKey();
        }
    }

    public Vector getIOFormatTypes() {
        Vector v = new Vector();
        Enumeration e = ioformatTypes.keys();
        while (e.hasMoreElements()) {
            v.addElement(e.nextElement());
        }
        return v;
    }

    public Graph getNet(MetaApplication app) {
        // get Net of this Application
        AAObject aao = (AAObject)activeApplications.get(app);
        if (aao == null) {
            D.d("### Unbekannte Applikation! " + app);
            return null;
        }
        return aao.getNet();
    }

    public Vector getNetFiles(Graph graph) {
        return ((ANObject) this.activeNets.get(graph)).getNetFiles();
    }

    public String getNetName(Graph graph) {
        return ((ANObject) this.activeNets.get(graph)).getNetName();
    }

    public Vector getNetTypes() {
        Vector a = new Vector();
        Enumeration e = netTypes.keys();
        while (e.hasMoreElements()) {
            a.addElement(e.nextElement());
        }
        return a;
    }

    public MetaApplication getNewApp(String name) {
        // get new App with name name
        ATObject ato = (ATObject)appTypes.get(name);
        MetaApplication app = ato.getNewApp();
        AAObject aao = new AAObject(app, ato);
        activeApplications.put(app, aao);
        aao.setAppID(appID++);
        aao.setMenus();
        return app;
    }

    public MetaApplication getNewApp(String name, Graph graph) {
        // get new standardApplication für ein bestimmtes Netz
        ATObject ato = (ATObject)appTypes.get(name);
        // Netztyp rauskriegen... allowedApplications rauskriegen...
        ANObject ano = (ANObject)activeNets.get(graph);
        /*
           Enumeration e = ano.getApplications().elements();
           while (e.hasMoreElements()) {
           AAObject a = (AAObject) e.nextElement();
           D.d("..........0 " + a.getApplication() + " " + a.getApplicationName());
           }
         */
        // prüfen, ob Applikation erlaubt...
        Vector v = ano.getAllowedApplications();
        // ist angeforderte App drin? dann:
        if (v.contains(ato)) {
            MetaApplication app = ato.getNewApp();
            /*
               e = ano.getApplications().elements();
               while (e.hasMoreElements()) {
               AAObject a = (AAObject) e.nextElement();
               D.d("..........2 " + a.getApplication() + " " + a.getApplicationName());
               }
             */
            AAObject aao = new AAObject(app, ato);
            activeApplications.put(app, aao);
            aao.setAppID(appID++);
            aao.setMenus();

            // Das Netz beim AAO und die App beim ANO eintragen!!!
            aao.addNet(ano);
            ano.addApplication(aao);
            /*
               e = ano.getApplications().elements();
               while (e.hasMoreElements()) {
               AAObject a = (AAObject) e.nextElement();
               D.d("..........3 " + a.getApplication() + " " + a.getApplicationName());
               }
             */
            // ########## PENDING hier runimmediate!!!
            if (this.getStartImmediate(app)) {
                D.d("--- start immediate: " + getAppNameAndInstanceKey(app));
                app.startApp();
            }
            if (activeApplications.containsKey(app)) {
                return app;
            } else {
                D.d("--- Applikation verschwunden;-)");
            }
        } else {
            D.d("### ERROR: ApplicationType not compatible to NetType!");
        }
        return null;
    }

    public MetaApplication getNewAppNew(String name, Graph graph) {
        // get new standardApplication für ein bestimmtes Netz

        // Netztyp rauskriegen... allowedApplications rauskriegen...
        ANObject ano = (ANObject)activeNets.get(graph);
        /*
           Enumeration e = ano.getApplications().elements();
           while (e.hasMoreElements()) {
           D.d(".......... " + ((AAObject) e.nextElement()).getApplicationName());
           }
         */
        // prüfen, ob Applikation erlaubt...
//		NTObject nto = ano.getNTObject();
//		Vector v = nto.getAllowedApplications();
        Vector v = ano.getAllowedApplications();
        // ist stdApp drin? dann:
/*
        e = ano.getApplications().elements();
        while (e.hasMoreElements()) {
        D.d("..........0 " + ((AAObject) e.nextElement()).getApplicationName());
        }
 */
        if (v.contains(name)) {
            ATObject ato = (ATObject)appTypes.get(name);

            MetaApplication app = ato.getNewApp();
            /*
               e = ano.getApplications().elements();
               while (e.hasMoreElements()) {
               D.d("..........1 " + ((AAObject) e.nextElement()).getApplicationName());
               }
             */
            AAObject aao = new AAObject(app, ato);
            activeApplications.put(app, aao);
            aao.setAppID(appID++);
            aao.setMenus();

            // Das Netz beim AAO und die App beim ANO eintragen!!!
            aao.addNet(ano);
            ano.addApplication(aao);
            /*
               e = ano.getApplications().elements();
               while (e.hasMoreElements()) {
               D.d("..........2 " + ((AAObject) e.nextElement()).getApplicationName());
               }
             */

            return app;
        } else {
            D.d("### ERROR: ApplicationType not compatible to NetType!");
            return null;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (14.09.00 20:16:13)
     * @param name String
     */
    public InOut getNewInOut(String name) {
        // get new InOut with name nettype
        IOTObject ioto = (IOTObject)ioformatTypes.get(name);
        InOut io = ioto.getNewInOut();

        // ######### sollen aktive InOuts auch verwaltet werden???
        //AIOObject aioo = new AIOObject(io, ioto);
        //activeInOuts.put(io, aioo);

        return io;
    }

    public Graph getNewNet(String name) {
        // get new Net with name nettype
        NTObject nto = (NTObject)netTypes.get(name);
        if (nto != null) {
            // Auf Anhieb gefunden...
            Graph graph = nto.getNewNet();
            if (graph != null) {
                ANObject ano;
                if (nto.isParametric()) {
                    Vector parameters = nto.getLastParameters();
                    ano = new ANObject(graph, nto, parameters);
                } else {
                    ano = new ANObject(graph, nto);
                }
                ano.setNetID(netID++);
                activeNets.put(graph, ano);
                // erst jetzt den Observer hinzufügen...
                graph.addObserver(this.applicationControl);
            }
            return graph;
        } else {
            Enumeration e = netTypes.keys();
            while (e.hasMoreElements()) {
                String s = (String)e.nextElement();
                if (name.startsWith(s)) {
                    String t = name.substring(s.length()).trim();
                    if ((t.charAt(0) == '(') && (t.charAt(t.length() - 1) == ')')) {
                        t = t.substring(1, t.length() - 1);
                        Vector v = new Vector();
                        boolean endebanane = false;
                        while (!endebanane) {
                            int i = t.indexOf(",");
                            if (i != -1) {
                                v.add(t.substring(0, i).trim());
                                t = t.substring(i + 1);
                            } else {
                                v.add(t.trim());
                                endebanane = true;
                            }
                        }
                        nto = (NTObject)netTypes.get(s);
                        Graph graph = nto.getNewNet(v);
                        if (graph != null) {
                            ANObject ano = new ANObject(graph, nto, v);
                            ano.setNetID(netID++);
                            activeNets.put(graph, ano);
                            // erst jetzt den Observer hinzufügen...
                            graph.addObserver(this.applicationControl);
                            D.d("***** Parametric Net: " + name + ": " + graph);
                        }
                        return graph;
                    }
                }
            }
            D.d("ACR.getNewNet: unknown Nettype: " + name);
            return null;
        }
    }

    public Graph getNewNet(Hashtable specificationTable) {
        return this.getNewNet(specificationTable, null);
    }

    public Graph getNewNet(Hashtable specificationTable, String desiredName) {
        // get new Net with dynamic specificationTable...
        String name = this.addNewNettype(specificationTable, desiredName);
        if (name != null) {
            return this.getNewNet(name);
        }
        return null;
    }

    public MetaApplication getNewStandardApp() {
        // get new standardApplication
        if (this.standardApplication != null) {
            MetaApplication app = this.standardApplication.getNewApp();
            AAObject aao = new AAObject(app, this.standardApplication);
            activeApplications.put(app, aao);
            aao.setAppID(appID++);
            aao.setMenus();
            return app;
        } else {
            return null;
        }
    }

    public MetaApplication getNewStandardApp(Graph graph) {
        // get new standardApplication für ein bestimmtes Netz
        if (this.standardApplication != null) {
            // ############### Hier muß jetzt die Prüfung erfolgen, ob das erlaubt ist...
            // Netztyp rauskriegen... allowedApplications rauskriegen...
            ANObject ano = (ANObject)activeNets.get(graph);
            NTObject nto = ano.getNTObject();
            Vector v = nto.getAllowedApplications();
            // ist stdApp drin? dann:
            if (v.contains(this.standardApplication)) {
                MetaApplication app = this.standardApplication.getNewApp();
                AAObject aao = new AAObject(app, this.standardApplication);
                activeApplications.put(app, aao);
                aao.setAppID(appID++);
                aao.setMenus();

                // Das Netz beim AAO und die App beim ANO eintragen!!!
                aao.addNet(ano);
                ano.addApplication(aao);

                return app;
            } else {
                D.d("### ERROR: ApplicationType not compatible to NetType!");
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (14.09.00 20:16:13)
     * @param name String
     */
    public InOut getNewStandardInOut() {
        // get new StdInOut
        InOut io = standardIOFormat.getNewInOut();

        // ######### sollen aktive InOuts auch verwaltet werden???
        //AIOObject aioo = new AIOObject(io, ioto);
        //activeInOuts.put(io, aioo);

        return io;
    }

    public Graph getNewStandardNet() {
        // get new standardNet
        if (this.standardNettype != null) {
            Graph graph = this.standardNettype.getNewNet();
            ANObject ano = new ANObject(graph, this.standardNettype);
            ano.setNetID(netID++);
            activeNets.put(graph, ano);
            // erst jetzt den Observer hinzufügen...
            graph.addObserver(this.applicationControl);
            return graph;
        }
        return null;
    }

    /**
     * Insert the method's description here.
     * Creation date: (14.09.00 20:16:13)
     * @param name String
     */
    public PnkFileFilter getStdFileFilter() {
        return standardIOFormat.getFileFilter();
    }

    protected Object invokeAction(MetaActionObject mao) {
        D.d(mao);
        if (this.activeNets.containsKey(mao.getNet())) {
            ANObject ano = (ANObject) this.activeNets.get(mao.getNet());
            Enumeration e = ano.getApplications().elements();
            MetaApplication ma;
            while (e.hasMoreElements()) {
                ma = ((AAObject)e.nextElement()).getApplication();
                if (mao.checkInterface(ma)) {
                    return mao.performAction(ma);
                }
            }
        }
        return null;
    } // public void invokeAction

    /** load lädt eine Netzdatei mit dem Format ioname...
     */
    public Vector load(URL name, String ioname) {
        if (ioname == null) {
            ioname = (String)ioformatTypes.keySet().iterator().next();
        }
        if (isFileOpen(name)) {
            D.err("ERROR: Die Datei ist bereits geöffnet!");
            return null;
        } else {
            InOut io = getNewInOut(ioname);
            Vector v = io.load(name);
            if (v != null && v.size() != 0) {
                D.d("ACR.load: Netze erhalten!", 3);
                // ANOs finden...
                Vector anos = new Vector();
                Enumeration e = v.elements();
                while (e.hasMoreElements()) {
                    Graph graph = (Graph)e.nextElement();
                    anos.add((ANObject) this.activeNets.get(graph));
                }
                // Hier NFObject anlegen
                NFObject nfo = new NFObject(name, (IOTObject)ioformatTypes.get(ioname), anos);
                this.netFiles.put(name, nfo);
                // NFO eintragen und stdApp starten...
                e = v.elements();
                while (e.hasMoreElements()) {
                    Graph graph = (Graph)e.nextElement();
                    // nfo einsortieren...
                    ((ANObject) this.activeNets.get(graph)).addNetFile(nfo);
                    if (graph != null) {
                        getNewStandardApp(graph);
                    }
                }
            } else {
                D.d("ERROR: Die Datei enthielt keine Netze!!!");
            }
            return v;
        }
    } // public void load

    public boolean multipleAllowed(String name) {
        return ((IOTObject)ioformatTypes.get(name)).multipleAllowed();
    }

    public void removeApplication(MetaApplication a) {
        // Applikation beenden...
        AAObject aao = (AAObject) this.activeApplications.get(a);
        if (aao == null) {
            D.d("Applikation nicht gefunden!!!");
            return;
        }
        aao.quitApplication();
        this.activeApplications.remove(a);
    }

    /** save gibt den Aufruf an alle Applikationen weiter...
     */
    public void save(Graph graph) {
        ANObject ano = (ANObject)activeNets.get(graph);
        ano.save();
    } // public void save

    /** save gibt den Aufruf an alle Applikationen
     * der beteiligten Netze weiter...
     */
    public void save(URL name) {
        if (netFiles.containsKey(name)) {
            Vector v = this.getFileNets(name);
            for (int i = 0; i < v.size(); i++) {
                this.save((Graph)v.elementAt(i));
            }
            ((NFObject)netFiles.get(name)).getIotype().getNewInOut().save(v, name);
        } else {
            D.d("File unknown: " + name.toString());
        }
    }

    /** save gibt den Aufruf an alle Applikationen
     * der beteiligten Netze weiter...
     */
    public void save(URL name, Vector theNets, String ioname) {
        IOTObject ioto = (IOTObject) this.ioformatTypes.get(ioname);
        for (int i = 0; i < theNets.size(); i++) {
            this.save((Graph)theNets.elementAt(i));
        }
        // save
        try {
            ioto.getNewInOut().save(theNets, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // nfobject...
        Vector v = new Vector();
        Enumeration e = theNets.elements();
        while (e.hasMoreElements()) {
            v.add(this.activeNets.get(e.nextElement()));
        }
        NFObject nfo = new NFObject(name, ioto, v);
        this.netFiles.put(name, nfo);
        e = theNets.elements();
        while (e.hasMoreElements()) {
            ((ANObject) this.activeNets.get(e.nextElement())).addNetFile(nfo);
        }
    }

    public void setActiveApp(Graph net, MetaApplication app) {
        // set active Application of Net
        if (this.activeApplications.containsKey(app)) {
            ((ANObject)activeNets.get(net)).setActiveApp(app);
        } else {
            D.d("### Unbekannte Applikation!!!" + app);
        }
    } // public void setActiveApp(net, app)

    // Eine Applikation setzt ihr Menu...

    public void setAppMenu(MetaApplication app, JMenu menu[]) {
        D.d("ACR: setAppMenu: " + app + " " + menu);
        AAObject aao = (AAObject) this.activeApplications.get(app);
        if (aao != null) {
            aao.setAppMenu(menu);
        } else {
            D.d("acr.setAppMenu: Applikation nicht bekannt...");
            // ########## FEHLER!!!
        }
    } // setAppMenu

    /** Implementation der Schnittstelle Observer: Methode update
       Es wird von allen beteiligten Applikationen
       die update-Methode aufgerufen
     */
    public void update(Observable netobject, Object actionobject) {
        // D.d("=== Initiator: " + ((ActionObject)actionobject).getInitiator());
        Net net = ((NetObservable)netobject).getNet();
        ANObject ano = (ANObject)activeNets.get(net);
        ano.update(netobject, actionobject);
    } // public void update

    private Vector lastNetFiles = new Vector();

    /**
     * gibt die letzten geöffneten Dateien zurück...
     */
    public Vector getLastNetFiles() {
        return lastNetFiles;
    } // public Vector getLastNetFiles()

    public String getNetLongName(Graph graph) {
        return getNetName(graph) + " <" + getNettype(graph) + ">";
    }

    public String getNettype(Graph graph) {
        return ((ANObject) this.activeNets.get(graph)).getNettypeName();
    }

    public String getNettypeLong(Graph graph) {
        return ((ANObject) this.activeNets.get(graph)).getNettypeLongName();
    }

    public String getNettypeLong(String name) {
        return ((NTObject) this.netTypes.get(name)).getNettypeLongName();
    }

    public Vector getNetTypesLong() {
        Vector a = new Vector();
        Enumeration e = netTypes.elements();
        while (e.hasMoreElements()) {
            a.addElement(((NTObject)e.nextElement()).getNettypeLongName());
        }
        return a;
    }

    public boolean isCompatibleNetInOut(Graph g, String ioname) {
        return ((ANObject)activeNets.get(g)).getNTObject().getAllowedInOuts().contains((IOTObject)ioformatTypes.get(ioname));
    }

    /** tests if a file is already opened...
     */
    public boolean isFileOpen(URL name) {
        if (this.netFiles.containsKey(name)) {
            return true;
        }
        return false;
    } // public void isFileOpen
}

