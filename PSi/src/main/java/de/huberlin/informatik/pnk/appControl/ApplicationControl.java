package de.huberlin.informatik.pnk.appControl;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.*;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.base.*;
import de.huberlin.informatik.pnk.kernel.*;

import java.io.File;

public class ApplicationControl
implements Observer, ACApplicationInterface, ApplicationAWInterface {
    private ACResources acResources = null;
    protected ApplicationControlMenu menu = null;
    // Sinnvoll???
    private Vector windows = new Vector();
    private Graph activeNet = null;
    private MetaApplication focusedApplication = null;

    public ApplicationControl(String toolspec) {
        try {
            this.acResources = new ACResources(this, new URL(toolspec));
        } catch (MalformedURLException e) {
            D.d("MalformedURLException: " + e.toString());
            System.exit(1);
        }
        // Menu
        this.menu = new ApplicationControlMenu(this);
        this.setMenu();
        this.menu.makeVisible();
    } // ApplicationControl
    // #################### Application Schnittstelle...
    // #################### ACApplicationInterface

    public void addApplication(MetaApplication app) {}

    /**
     * Add new Nettype according to Hashtable
     * Returns name of Nettype - or null
     */
    public synchronized String addNewNettype(Hashtable specificationTable) {
        return this.acResources.addNewNettype(specificationTable);
    }

    // #################### Fensterverwaltung
    // #################### ApplicationAWInterface

    /*
     * Schliesen aller Fenster der Application durch die ApplicationControl
     */
    protected void closeWindows() {
        // ########## Brauch ich das???
        Enumeration e = this.windows.elements();
        while (e.hasMoreElements()) {
            ((MetaJFrame)e.nextElement()).closeWindow();
        }
    }

    public String getAppNameAndInstanceKey(MetaApplication ma) {
        return this.acResources.getAppNameAndInstanceKey(ma);
    }

    public MetaApplication getfocusedApplication() {
        return this.focusedApplication;
    }

    public int getInstanceKey(MetaApplication ma) {
        return this.acResources.getInstanceKey(ma);
    }

    public String getNetType(Graph net) {
        return acResources.getNettype(net);
    }

    public synchronized MetaApplication getNewApp(String name) {
        // ### Diese Methode sollte eigentlich nicht benutzt werden...
        // weil apps nur mit Netz generiert werden...
        MetaApplication app = this.acResources.getNewApp(name);
        this.setFocusedApplication(app);
        return app;
    }

    public synchronized MetaApplication getNewApp(String name, Graph graph) {
        MetaApplication app = this.acResources.getNewApp(name, graph);
        return app;
    }

    public synchronized Graph getNewNet(String name) {
        Graph graph = this.acResources.getNewNet(name);
        this.activeNet = graph;
        this.setFocusedApplication(null);
        return graph;
    }

    public synchronized Graph getNewNet(String name, MetaApplication initiator) {
        // Diese Methode ist öffentlich und liefert Netz mit StdApp zurück
        D.d("getNewNet " + name, 3);
        Graph graph = this.acResources.getNewNet(name);
        D.d("getNewNet " + graph, 3);
        if (graph != null) {
            this.acResources.addApp(graph, initiator);
            this.activeNet = graph;
            // ########## Pending wird die stdapp nicht gleich in acr miterzeugt??? Eher nicht...
            MetaApplication ma = this.getNewStandardApp(graph);
            // this.setFocusedApplication(ma);
            D.d("getNewNet " + ma, 3);
            // this.setMenu();
        } else {
            D.err("ERROR: AC.getNewNet: Netz nicht erzeugt!");
        }
        return graph;
    }

    public synchronized Graph getNewNet(Hashtable specificationTable) {
        return getNewNet(specificationTable, null);
    }

    public synchronized Graph getNewNet(Hashtable specificationTable, String name) {
        Graph graph = this.acResources.getNewNet(specificationTable, name);
        this.activeNet = graph;
        this.setFocusedApplication(null);
        return graph;
    }

    public synchronized MetaApplication getNewStandardApp() {
        // ### Diese Methode sollte eigentlich nicht benutzt werden...
        // weil apps nur mit Netz generiert werden...
        MetaApplication app = this.acResources.getNewStandardApp();
        this.setFocusedApplication(app);
        return app;
    }

    public synchronized MetaApplication getNewStandardApp(Graph graph) {
        MetaApplication app = this.acResources.getNewStandardApp(graph);
        this.setFocusedApplication(app);
        return app;
    }

    public Object invokeAction(MetaActionObject mao) {
        return this.acResources.invokeAction(mao);
    } // public void invokeAction

    public Vector getActiveApplications(Graph graph) {
        // Aktive Applikationen des Netzes
        return this.acResources.getActiveApplications(graph);
    }

    public Vector getStdApplications(Graph graph) {
        // Standard Applikationen des Netzes (zumeist "default Editor")
        return this.acResources.getStdApplications(graph);
    }

    public Vector getActiveApplicationsOfType(Graph graph, String apptype) {
        // Applikationen des Netzes vom Typ apptype
        return this.acResources.getActiveApplicationsOfType(graph, apptype);
    }

    /**
     * Insert the method's description here.
     * Creation date: (14.09.00 20:16:13)
     * @param theURL java.net.URL
     */
    public final Vector loadNet(URL theURL) {
        return acResources.getNewStandardInOut().load(theURL);
    }

    public static void main(String args[]) {
        // debuglevel setzen... hier später Kommandozeile auswerten.
        //D.debug = 3;

        //main_test3();

        System.out.println("Petri Net Kernel,\n" +
                           "Copyright 1999-2002 Petri Net Kernel Team\n" +
                           "Petri Net Technology Group,\n" +
                           "Humboldt-Universitaet zu Berlin, Germany\n");

        Vector myargs = new Vector();
        boolean options = true;
        boolean debugset = false;
        for (int i = 0; i < args.length; i++) {
            if ((args[i].charAt(0) == '-') && (options == true)) {
                String s = args[i];
                for (int j = 1; j < s.length(); j++) {
                    switch (s.charAt(j)) {
                    case 'd':
                        i++;
                        try {
                            D.debug = Integer.parseInt(args[i]);
                            debugset = true;
                        } catch (NumberFormatException e) {
                            System.err.println("wrong option, integer expected:" + args[i]);
                            usage();
                        }
                        break;
                    case '-':
                        // Optionen zuende...
                        options = false;
                        break;
                    default:
                        System.err.println("unknown option:" + s.charAt(j));
                        usage();
                    }
                }
            } else {
                myargs.add(args[i]);
            }
        }
        if (!debugset) {
            D.debug = 0;
        }

        String toolspec = null;
        if (myargs.size() == 0) {
            // Standard-toolSpecification
            toolspec = "file:toolSpecifications/toolSpecification.xml";
        } else {
            if (myargs.size() == 1) {
                toolspec = "file:" + myargs.firstElement();
            } else {
                System.err.println("too many arguments");
                usage();
            }
        }
        ApplicationControl m = new ApplicationControl(toolspec);
    } // main

    public static void usage() {
        System.err.println("usage:");
        System.exit(-1);
    }

    public static void main_test() {
        URL urls[] = null;
        URL url1 = null;
        ClassLoader ucl = null;
        ClassLoader cl = null;
        Class c = null;
        Constructor con = null;

        cl = ClassLoader.getSystemClassLoader();
        D.d("SystemClassLoader: " + ClassLoader.getSystemClassLoader());

        try {
            //ucl = new URLClassLoader(new URL[] {new URL("file:")});
            ucl = new URLClassLoader(new URL[] {new URL("http://frosch/classloadertest/")});
            D.d("Parent ClassLoader: " + ucl.getParent());
        } catch (MalformedURLException e) {
            D.d("MalformedURLException: " + e.toString());
            System.exit(0);
        }
        try {
            c = ucl.loadClass("de.huberlin.informatik.pnk.kernel.Net");
        } catch (ClassNotFoundException e) {
            D.d("ClassNotFoundException: " + e.toString());
            System.exit(0);
        }
        D.d("Class: " + c);

        try {
            ucl = new URLClassLoader(new URL[] {new URL("http://frosch/classloadertest/")});
        } catch (MalformedURLException e) {
            D.d("MalformedURLException: " + e.toString());
            System.exit(0);
        }
        try {
            c = ucl.loadClass("simple");
        } catch (ClassNotFoundException e) {
            D.d("ClassNotFoundException: " + e.toString());
            System.exit(0);
        }
        D.d("Class: " + c);

        try {
            ucl = new URLClassLoader(new URL[] {new URL("http://frosch/classloadertest/")});
        } catch (MalformedURLException e) {
            D.d("MalformedURLException: " + e.toString());
            System.exit(0);
        }
        try {
            c = ucl.loadClass("quatro");
        } catch (ClassNotFoundException e) {
            D.d("ClassNotFoundException: " + e.toString());
            System.exit(0);
        }
        D.d("Class: " + c);

        try {
            ucl = new URLClassLoader(new URL[] {new URL("http://frosch/classloadertest/test.jar")});
        } catch (MalformedURLException e) {
            D.d("MalformedURLException: " + e.toString());
            System.exit(0);
        }
        try {
            c = ucl.loadClass("doubles");
        } catch (ClassNotFoundException e) {
            D.d("ClassNotFoundException: " + e.toString());
            System.exit(0);
        }
        D.d("Class: " + c);

        try {
            ucl = new URLClassLoader(new URL[] {new URL("file:/o:/httpd/htdocs/classloadertest/")});
        } catch (MalformedURLException e) {
            D.d("MalformedURLException: " + e.toString());
            System.exit(0);
        }
        try {
            c = ucl.loadClass("simple");
        } catch (ClassNotFoundException e) {
            D.d("ClassNotFoundException: " + e.toString());
            System.exit(0);
        }
        D.d("Class: " + c);

        try {
            con = c.getConstructor(new Class[0]);
        } catch (NoSuchMethodException e) {
            D.d("NoSuchMethodException: " + e.toString());
            System.exit(0);
        }
        D.d("Constructor: " + con);
        try {
            D.d("Instanz: " + con.newInstance(new Object[0]));
        } catch (Exception e) {
            D.d("NoSuchMethodException: " + e.toString());
            System.exit(0);
        }

        try {
            ucl = new URLClassLoader(new URL[] {new URL("file:/o:/httpd/htdocs/classloadertest/")});
        } catch (MalformedURLException e) {
            D.d("MalformedURLException: " + e.toString());
            System.exit(0);
        }
        try {
            c = ucl.loadClass("test.simple2");
        } catch (ClassNotFoundException e) {
            D.d("ClassNotFoundException: " + e.toString());
            System.exit(0);
        }
        D.d("Class: " + c);

        try {
            c = cl.loadClass("de.huberlin.informatik.pnk.kernel.Graph");
        } catch (ClassNotFoundException e) {
            D.d("ClassNotFoundException: " + e.toString());
            System.exit(0);
        }
        D.d("Class: " + c);

        System.exit(0);
    } // main_test

    protected void menuClose() {
        D.d("menuClose", 2);
        // leitet weiter...
        if (this.activeNet != null) {
            this.menuClose(this.activeNet);
        }
    } // menuClose

    protected void menuClose(Graph net) {
        // D.d("menuClose " + net);
        if (net != null) {
            /* Wenn ein Netz geschlossen wird, soll folgendes passieren:
             *
             * Erstmal den Nutzer fragen...
             *
             *  Alle beteiligten Applikationen schließen...
             *  Alle beteiligten Netfiles entfernen
             *  anderes (welches?) Netz als aktuelles Netz...
             */
            Vector u = acResources.getNetFiles(net);
//			if (true) {
            if (u.size() == 0) {
                // keine Netzdatei... das ist einfach.
                int a = JOptionPane.showConfirmDialog(null,
                                                      "Do you want to close net \"" + net.getName() + "\"?",
                                                      "Close " + net.getName(),
                                                      JOptionPane.YES_NO_OPTION);
                if (a == JOptionPane.YES_OPTION) {
                    this.setActiveNet(this.acResources.closeNet(net));
                    this.setMenu();
                }
                return;
            }
            if (u.size() == 1) {
                // nur eine Netzdatei...
                Vector v = ((NFObject)u.get(0)).getFileNets();
                if (v.size() == 1) {
                    int a = JOptionPane.showConfirmDialog(null,
                                                          "Do you want to close net \"" + acResources.getNetLongName(net)
                                                          + "\" in file\n\"" + ((NFObject)u.get(0)).getFile() + "\"?",
                                                          "Close " + acResources.getNetLongName(net),
                                                          JOptionPane.YES_NO_OPTION);
                    if (a == JOptionPane.YES_OPTION) {
                        this.setActiveNet(this.acResources.closeNet(net));
                        this.setMenu();
                    }
                    return;
                }
                Hashtable items = new Hashtable();
                Hashtable items2 = new Hashtable();
                Vector pre = new Vector();
                for (int i = 0; i < v.size(); i++) {
                    Graph g = ((ANObject)v.get(i)).getNet();
                    items.put(Integer.toString(i), acResources.getNetLongName(g));
                    items2.put(Integer.toString(i), g);
                    if (g == net) {
                        pre.add(Integer.toString(i));
                    }
                }
                // Netze zum auswählen anbieten...
                Vector vv = new SelectSomeBox("Select Nets", "The file containing this net contains other nets too. Select the nets you want to close.", items, pre).getSelection();
                // Netze schließen...
                if (vv.size() != 0) {
                    Graph last = null;
                    for (int i = 0; i < vv.size(); i++) {
                        last = this.acResources.closeNet((Graph)items2.get(vv.get(i)));
                    }
                    this.setActiveNet(last);
                    this.setMenu();
                }
            } else {
                // Hier den Fall behandeln, daß das Netz Bestandteil mehrerer Dateien ist...
            }
        }
    } // menuClose

    protected void menuNew() {
        // D.d("menuNew");
        Graph net = acResources.getNewStandardNet();
        // D.d("menuNew " + net);
        if (net != null) {
            this.activeNet = net;
            MetaApplication ma = this.getNewStandardApp(net);
            this.focusedApplication = ma;
            // D.d("menuNewNet "+ ma);
            this.setMenu();
        } else {
            D.d("ERROR: AC.menuNewNet: Netz nicht erzeugt!");
        }
    } // menuNew

    protected void menuNewNet(String name) {
        // D.d("menuNewNet "+ name);
        Graph net = acResources.getNewNet(name);
        // D.d("menuNewNet "+ net);
        if (net != null) {
            this.activeNet = net;
            MetaApplication ma = this.getNewStandardApp(net);
            this.setFocusedApplication(ma);
            // D.d("menuNewNet "+ ma);
            this.setMenu();
        } else {
            D.d("ERROR: AC.menuNewNet: Netz nicht erzeugt!");
        }
    } // menuNewNet

    protected void menuOpen() {
        //D.d("menuLoad");
        Vector nets = null;

        JFileChooser fc = new JFileChooser(".");
        Vector v = acResources.getIOFormatTypes();
        for (int i = 0; i < v.size(); i++) {
            fc.addChoosableFileFilter(acResources.getFileFilter((String)v.elementAt(i)));
        }
        fc.setFileFilter(acResources.getStdFileFilter());
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        if (lastNetDir != null) {
            fc.setCurrentDirectory(lastNetDir);
        }
        int i = fc.showOpenDialog(menu.frame);
        if (i == JFileChooser.APPROVE_OPTION) {
            lastNetDir = fc.getCurrentDirectory();
            //D.d("xxx---> Ausgewählt: " + fc.getSelectedFile().getName());
            try {
                nets = acResources.load(fc.getSelectedFile().toURL(), ((PnkFileFilter)fc.getFileFilter()).getShortDescription());
                if (nets != null && nets.size() > 0) {
                    //D.d("AC.Load....." + nets);
                    //D.d("AC.Load....." + nets.get(0));

                    this.activeNet = (Graph)nets.get(0);
                    this.setMenu();
                }
            } catch (MalformedURLException e) {
                D.d("AC.menuLoad: MalformedURLException");
            }
        } else {
            //D.d("xxx---> Abgebrochen!!!");
        }
    } // menuLoad

    protected void menuQuit() {
        // D.d("menuQuit: hard quit");
        int a = JOptionPane.showConfirmDialog(null,
                                              "Do you want to quit the PetriNetKernel?",
                                              "Quit PetriNetKernel",
                                              JOptionPane.YES_NO_OPTION);
        if (a == JOptionPane.YES_OPTION) {
// PENDING ist das richtig so???
            Vector v = new Vector(acResources.getActiveNets());
            for (int i = 0; i < v.size(); i++) {
                this.acResources.closeNet((Graph)v.get(i));
            }
// bis hierher

            System.exit(0);
        }
    } // menuQuit

    protected void menuRemoveApp(MetaApplication app) {
        //D.d("menuRemoveApp " + app);
        this.quitApp(app);
    } // menuSelectNet

    protected void menuSave() {
        //D.d("menuSave");
        Vector v = this.acResources.getNetFiles(this.activeNet);
        if (v.size() == 0) {
            D.err("ERROR: Keine Datei für dieses Netz gefunden!");
            // ##### Hier dann saveAs aufrufen???
        } else if (v.size() > 1) {
            Hashtable h = new Hashtable();
            for (int i = 0; i < v.size(); i++) {
                h.put(((NFObject)v.get(i)).getFileURL().toString(), ((NFObject)v.get(i)).getFileURL().toString());
            }
            Vector vv = new Vector();
            vv.add(((NFObject)v.get(0)).getFileURL().toString());
            Vector vvv = new SelectSomeBox("Select files for save", "This Net is part of more than one file.\nPlease select the files you want to save now.", h, vv).getSelection();
            for (int i = 0; i < vvv.size(); i++) {
                URL u = null;
                try {
                    u = new URL((String)vvv.elementAt(i));
                } catch (MalformedURLException e) {}
                if (u != null) {
                    this.acResources.save(u);
                }
            }
        } else {
            URL u = ((NFObject)v.elementAt(0)).getFileURL();
            this.acResources.save(u);
        }
    } // menuSave

    protected void menuSaveAs() {
        // D.d("menuSaveAs");
        String ioname = null;
        JFileChooser fc = new JFileChooser(".");
        Vector v = acResources.getAllowedInOuts(this.activeNet);
        if (v.size() == 0) {
            D.d("### ERROR: Keine erlaubten InOuts gefunden!");
            return;
        }
        for (int i = 0; i < v.size(); i++) {
            fc.addChoosableFileFilter(acResources.getFileFilter((String)v.elementAt(i)));
        }
        fc.setFileFilter(acResources.getStdFileFilter());
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        if (lastNetDir != null) {
            fc.setCurrentDirectory(lastNetDir);
        }
        int i = fc.showSaveDialog(menu.frame);
        if (i == JFileChooser.APPROVE_OPTION) {
            lastNetDir = fc.getCurrentDirectory();
            java.io.File file = fc.getSelectedFile();
            // D.d("xxx---> Ausgewählt: " + file.toString());
            PnkFileFilter ff = (PnkFileFilter)fc.getFileFilter();
            if (!ff.accept(file)) {
                file = ff.extend(file);
            }
            //D.d("xxx---> Ausgewählt: " + file.toString());
            URL url = null;
            try {
                url = file.toURL();
            } catch (MalformedURLException e) {
                D.d("menuLoad: MalformedURLException");
            }
            if (url != null) {
                if (file.exists()) {
                    if (acResources.isFileOpen(url)) {
                        // bei bereits geöffneter Datei abbrechen!!!
                        // ########## Das muß nicht sein! PENDING
                        // bei bereits geöffneter Datei Strukturünderungen nachfragen!!!
                        D.err("Error: File is already opened.");
                        return;
                    }
                    // bei bereits existierender Datei überschreiben nachfragen!!!
                    int a = JOptionPane.showConfirmDialog(null,
                                                          "File " + file.toString() + " exists. Do you want to overwrite it?",
                                                          "Overwrite File?",
                                                          JOptionPane.YES_NO_OPTION);
                    if (a == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                ioname = ((PnkFileFilter)fc.getFileFilter()).getShortDescription();
                if (acResources.multipleAllowed(ioname)) {
                    // D.d("Viele Netze pro Datei erlaubt...");
                    v = acResources.getActiveNets();
                    if (v.size() == 1) {
                        // D.d("Es gibt nur ein Netz...");
                        acResources.save(url, v, ioname);
                    } else {
                        // Hier dann die verbliebenen Netze zur Auswahl anbieten!
                        Hashtable items = new Hashtable();
                        Hashtable items2 = new Hashtable();
                        for (i = 0; i < v.size(); i++) {
                            Graph g = (Graph)v.get(i);
                            if (g != this.activeNet) {
                                // Hier noch auf die Erlaubnis von Netz zu InOut beachten!!!
                                if (acResources.isCompatibleNetInOut(g, ioname)) {
                                    items.put(Integer.toString(i), acResources.getNetLongName(g));
                                    items2.put(Integer.toString(i), g);
                                }
                            }
                        }
                        // Netze zum auswählen anbieten...
                        Vector vv = new SelectSomeBox("Select Nets", "Select some Nets you want to save in the same file.", items).getSelection();
                        // Netze einsortieren...
                        Vector vvv = new Vector();
                        vvv.add(this.activeNet);
                        for (i = 0; i < vv.size(); i++) {
                            vvv.add(items2.get(vv.get(i)));
                        }
                        // Und nun speichern...
                        acResources.save(url, vvv, ioname);
                    }
                } else {
                    //D.d("Nur ein Netz pro Datei... also los!");
                    Vector x = new Vector();
                    x.add(this.activeNet);
                    acResources.save(url, x, ioname);
                }
            }
        } else {
            // D.d("xxx---> Abgebrochen!!!");
        }
        this.setMenu();
    } // menuSaveAs

    protected void menuSelectApp(MetaApplication app) {
        //D.d("menuSelectApp " + app);
        this.setFocusedApplication(app);
        app.toFront();
    } // menuSelectNet

    protected void menuSelectNet(Graph net) {
        //D.d("menuSelectNet " + net);
        if (net != this.activeNet) {
            this.activeNet = net;
            if (net != null) {
                this.setFocusedApplication(this.acResources.getActiveApp(net));
            } else {
                this.setMenu();
            }
        }
    } // menuSelectNet

    protected void menuStartApplication(Graph net, String appName) {
        D.d("menuStartApplication " + net + " " + appName);
        MetaApplication ma = getNewApp(appName, net);
        if (ma != null) {
            D.d("menuStartApplication " + ma, 3);
            this.setFocusedApplication(ma);
            this.setMenu();
        } else {
            D.d("menuStartApplication: Applikation nicht gestarted - oder schon wieder zuende!");
        }
    } // menuStartApplication (Graph net, String appName)

    protected void menuStartApplication(String appName) {
        // reicht weiter...
        menuStartApplication(this.activeNet, appName);
    } // menuStartApplication (String appName)

    protected void menuTest() {
        //D.d("menuTest...");

        D.dumpVector(getActiveApplications(this.activeNet));
        D.dumpVector(getStdApplications(this.activeNet));
        D.dumpVector(getActiveApplicationsOfType(this.activeNet, "default Editor"));
        /**
           Vector v = acResources.getNetTypes();
           Hashtable items = new Hashtable();
           for (int i = 0; i < v.size(); i++) {
           items.put(Integer.toString(i), v.get(i));
           }
           Vector v2 = new Vector();
           v2.add(Integer.toString(3));
           v2.add(Integer.toString(6));
           Vector erg = new SelectSomeBox("Select Nettype", "please select some items...", items, false,
           v2).getSelection();

           D.d("----->>>>> " + erg);
           Enumeration e = erg.elements();
           while (e.hasMoreElements()) {
           D.d("   -> " + items.get(e.nextElement()));
           }
         */
//D.d("menuTest: Fertig!");
    } // menuTest

    /**
     * Ein MetaWindow registriert sich automatisch mit dieser Methode
     * bei der MetaApplication
     *
     * @param mf    das MetaFrame welches sich anmeldet
     */
    protected void registerWindow(MetaJFrame mf) {
        this.windows.addElement(mf);
    }

    public void removeApplication(MetaApplication app) {}

    /**
     * Insert the method's description here.
     * Creation date: (14.09.00 20:16:13)
     * @param theURL java.net.URL
     */
    public final void saveNet(Net theNet, URL theURL) {
        // ########## diese Methode brauche ich nicht und sie funktioniert auch nicht!!!
        // läßt save-Aufruf bei Applikationen weg...
        Vector v = new Vector();
        v.add(theNet);
        acResources.getNewInOut("pnml").save(v, theURL);
    }

    public final void saveNet(Net theNet) {
        acResources.save(theNet);
    }

    public void setActiveNet(Graph net) {
        this.activeNet = net;
        if (net == null) {
            this.setFocusedApplication(null);
        } else {
            this.setFocusedApplication(this.acResources.getActiveApp(net));
        }
    }

    public synchronized void setFocusedApplication(MetaApplication app) {
        if (app != this.focusedApplication) {
            this.focusedApplication = app;
            if (app != null) {
                this.activeNet = this.acResources.getNet(app);
                this.acResources.setActiveApp(this.activeNet, app);

                this.menu.setStatusLabel(this.acResources.getNetName(this.activeNet) + " <" + this.acResources.getNettypeLong(this.activeNet) + ">");
                this.menu.setInformationLabel(this.acResources.getApplicationName(app) + " <" + this.acResources.getInstanceKey(app) + ">");

                // ########### Menu holen
                JMenu[] menus = this.acResources.getAppMenu(app);
                // Menu setzen... bei acmenu...
                if (menus != null) {
                    // Menu setzen... ##########
                    this.menu.setMenu(1, menus);
                } else {
                    D.d("####### Das darf nicht sein!!!");
                }
            } else {
                if (this.activeNet == null) {
                    this.menu.setStatusLabel(null);
                    this.menu.setInformationLabel("...");
                } else {
                    this.menu.setStatusLabel(this.acResources.getNetName(this.activeNet) + " <" + this.acResources.getNettypeLong(this.activeNet) + ">");
                    this.menu.setInformationLabel("...");
                    // ApplicationMenu entfernen...
                    // ########## Pending: geht da nicht auch null als 2. Parameter???
                }
                menu.setMenu(1, new JMenu[] {});
            }
            this.setMenu();
        }
    }

    public synchronized void netNameChanged() {
        this.menu.setStatusLabel(this.acResources.getNetName(this.activeNet) + " <" + this.acResources.getNettypeLong(this.activeNet) + ">");
    }

    private void setMenu() {
        Object[] sep = {menu.MENU_SEPARATOR};

        // ##### FILE Menu

        Vector v = acResources.getNetTypes();
        java.util.Collections.sort(v);
        Object[] netmenu = new Object[v.size() + 2];
        netmenu[0] = "new Net";
        if (v.size() > 0) {
            netmenu[1] = menu.MENU_ACTIVE;
            for (int i = 0; i < v.size(); i++) {
                netmenu[i + 2] = new Object[] {
                    menu.MENU_ENTRY, menu.MENU_ACTIVE,
                    acResources.getNettypeLong((String)v.get(i)),
                    this, "menuNewNet", v.get(i)
                };
            }
        } else {
            netmenu[1] = menu.MENU_INACTIVE;
        }
        Object[] netentry = {menu.MENU_SUB, netmenu};
        // Entrys
        Object[] open = {menu.MENU_ENTRY, menu.MENU_ACTIVE, "Open", this, "menuOpen"};
        Object[] save;
        Object[] saveas;
        Object[] close;
        if (this.activeNet != null) {
            if (this.acResources.getNetFiles(this.activeNet).size() != 0) {
                save = new Object[] {menu.MENU_ENTRY, menu.MENU_ACTIVE, "Save", this, "menuSave"};
            } else {
                save = new Object[] {menu.MENU_ENTRY, menu.MENU_INACTIVE, "Save"};
            }
            saveas = new Object[] {menu.MENU_ENTRY, menu.MENU_ACTIVE, "Save As", this, "menuSaveAs"};
            close = new Object[] {menu.MENU_ENTRY, menu.MENU_ACTIVE, "Close", this, "menuClose"};
        } else {
            save = new Object[] {menu.MENU_ENTRY, menu.MENU_INACTIVE, "Save"};
            saveas = new Object[] {menu.MENU_ENTRY, menu.MENU_INACTIVE, "Save As"};
            close = new Object[] {menu.MENU_ENTRY, menu.MENU_INACTIVE, "Close"};
        }
        Object[] test = {menu.MENU_ENTRY, menu.MENU_ACTIVE, "Test", this, "menuTest"};
        Object[] hardquit = {menu.MENU_ENTRY, menu.MENU_ACTIVE, "Quit", this, "menuQuit"};

        // Menu File
        Object[] m1;
        if (D.debug > 1) {
            m1 = new Object[] {
                "File", menu.MENU_ACTIVE, netentry, sep, open, save, saveas,
                close, sep, test, sep, hardquit
            };
        } else {
            m1 = new Object[] {
                "File", menu.MENU_ACTIVE, netentry, sep, open, save, saveas,
                close, sep, hardquit
            };
        }

        // ##### NET Menu
        Object[] startappentry = null;
        Object[] selectappentry = null;
        Object[] removeappentry = null;
        Object[] selectnetentry = null;
        if (this.activeNet != null) {
            v = acResources.getAllowedApplications(this.activeNet);
            java.util.Collections.sort(v);
            if (v.size() > 0) {
                Object[] startappmenu = new Object[v.size() + 2];
                startappmenu[0] = "Start Application";
                startappmenu[1] = menu.MENU_ACTIVE;
                for (int i = 0; i < v.size(); i++) {
                    startappmenu[i + 2] = new Object[] {menu.MENU_ENTRY, menu.MENU_ACTIVE, v.get(i), this, "menuStartApplication", this.activeNet, (String)v.get(i)};
                }

                startappentry = new Object[] {menu.MENU_SUB, startappmenu};
            } else {
                startappentry = new Object[] {menu.MENU_SUB, new Object[] {"Start Application", menu.MENU_INACTIVE}};
            }
            v = this.acResources.getActiveApplications(this.activeNet);
//			java.util.Collections.sort(v);
            if (v.size() != 0) {
                Object[] selectappmenu = new Object[v.size() + 2];
                Object[] removeappmenu = new Object[v.size() + 2];
                selectappmenu[0] = "Select Application";
                selectappmenu[1] = menu.MENU_ACTIVE;
                removeappmenu[0] = "Remove Application";
                removeappmenu[1] = menu.MENU_ACTIVE;
                for (int i = 0; i < v.size(); i++) {
                    //D.d("AC.setMenu: " + acResources.getApplicationName((MetaApplication) v.get(i)));
                    selectappmenu[i + 2] = new Object[] {
                        menu.MENU_ENTRY, menu.MENU_ACTIVE,
                        acResources.getApplicationName((MetaApplication)v.get(i)) + " <" + this.acResources.getInstanceKey((MetaApplication)v.get(i)) + ">",
                        this, "menuSelectApp", (MetaApplication)v.get(i)
                    };
                    removeappmenu[i + 2] = new Object[] {
                        menu.MENU_ENTRY, menu.MENU_ACTIVE,
                        acResources.getApplicationName((MetaApplication)v.get(i)) + " <" + this.acResources.getInstanceKey((MetaApplication)v.get(i)) + ">",
                        this, "menuRemoveApp", (MetaApplication)v.get(i)
                    };
                }
                selectappentry = new Object[] {menu.MENU_SUB, selectappmenu};
                removeappentry = new Object[] {menu.MENU_SUB, removeappmenu};
            } else {
                selectappentry = new Object[] {
                    menu.MENU_SUB, new Object[]
                    {"Select Application", menu.MENU_INACTIVE}};
                removeappentry = new Object[] {
                    menu.MENU_SUB, new Object[]
                    {"Remove Application", menu.MENU_INACTIVE}};
            }
        } else {
            startappentry = new Object[] {
                menu.MENU_SUB, new Object[]
                {"Start Application", menu.MENU_INACTIVE}};
            selectappentry = new Object[] {
                menu.MENU_SUB, new Object[]
                {"Select Application", menu.MENU_INACTIVE}};
            removeappentry = new Object[] {
                menu.MENU_SUB, new Object[]
                {"Remove Application", menu.MENU_INACTIVE}};
        }
/*
                v = this.acResources.getActiveNets();
   //		java.util.Collections.sort(v);
                if (v.size() != 0) {
                        Object[] selectnetmenu = new Object[v.size() + 2];
                        selectnetmenu[0] = "Select Net";
                        selectnetmenu[1] = menu.MENU_ACTIVE;
                        for (int i=0; i < v.size(); i++) {
                                selectnetmenu[i+2] = new Object[] {menu.MENU_ENTRY, menu.MENU_ACTIVE,
                                                        acResources.getNetName((Graph) v.get(i)) +
                                                        " <" + this.acResources.getNettypeLong((Graph) v.get(i)) + ">",
                                                        this, "menuSelectNet", (Graph) v.                         }

                        selectnetentry = new Object[] {menu.MENU_SUB, selectnetmenu};
                }
                else {
                        selectnetentry = new Object[] {menu.MENU_SUB, new Object[]
                                                       {"Select Net", menu.MENU_INACTIVE} };
                }
 */
        // Test...
        v = this.acResources.getActiveNets();
//		java.util.Collections.sort(v);
        Object[] checknetentry = new Object[v.size() + 1];
        checknetentry[0] = menu.MENU_RADIO;
        for (int i = 0; i < v.size(); i++) {
            if (this.activeNet == (Graph)v.get(i)) {
                checknetentry[i + 1] = new Object[] {
                    menu.MENU_ACTIVE,
                    acResources.getNetName((Graph)v.get(i))
                    + " <" + this.acResources.getNettypeLong((Graph)v.get(i)) + ">",
                    menu.MENU_SELECTED, this, "menuSelectNet", (Graph)v.get(i)
                };
            } else {
                checknetentry[i + 1] = new Object[] {
                    menu.MENU_ACTIVE,
                    acResources.getNetName((Graph)v.get(i))
                    + " <" + this.acResources.getNettypeLong((Graph)v.get(i)) + ">",
                    menu.MENU_UNSELECTED, this, "menuSelectNet", (Graph)v.get(i)
                };
            }
        }

        // Menu Net
        // Object[] m2 = new Object[] { "Net", menu.MENU_ACTIVE, startappentry, sep, selectappentry, sep, removeappentry, sep, selectnetentry, sep ,checknetentry};
        Object[] m2 = new Object[] {"Net", menu.MENU_ACTIVE, startappentry, sep, selectappentry, sep, removeappentry, sep, checknetentry};
        menu.setMenu(0, "*", new Object[] {m1, m2});
    } // setMenu

    // Eine Applikation setzt ihr Menu...
    public JMenu[] setMenu(MetaApplication app, Object[] menustruct) {
        // menu erstellen lassen... und zurückbekommen... und ablegen...
        if (this.focusedApplication == app) {
            JMenu[] menus = menu.setMenu(1, this.acResources.getAppID(app), menustruct);
            this.acResources.setAppMenu(app, menus);
            return menus;
        } else {
            JMenu[] menus = menu.setMenu(-1, this.acResources.getAppID(app), menustruct);
            this.acResources.setAppMenu(app, menus);
            return menus;
        }
    } // setMenu

    // Eine Applikation setzt ihr Menu...

    public void setMenu(MetaApplication app, JMenu[] menus) {
        //D.d("AC.setMenu menus...");
        // menu bei acresources ablegen...
        this.acResources.setAppMenu(app, menus);
        if (app == this.focusedApplication) {
            //D.d("AC.setMenu menus aktualisieren...");
            // ACMenu aktualisieren...
            this.menu.setMenu(1, menus);
        }
    } // setMenu

    public void setWindowName(String name) {}

    /**
     * Ein MetaFrame traegt sich automatisch bei Aufruf seiner Methode
     * closeWindow() aus.
     *
     * @param    das MetaFrame welches sich abmeldet
     */
    protected void unregisterWindow(MetaJFrame mf) {
        this.windows.remove(mf);
    }

    // ###################### Observable...

    /** Implementation der Schnittstelle Observer: Methode update
       Es wird von allen beteiligten Applikationen
       die update-Methode aufgerufen
     */
    public void update(Observable netobject, Object actionobject) {
        //D.d("========== AC O update " + observer);

        // Hier wird also jetzt das Objekt weitergereicht an alle beteiligten Applikationen...
        // also zuerst mal an die ACResources...
        this.acResources.update(netobject, actionobject);
    } // public void update

    private File lastNetDir = null;

    /**
     * Add new Nettype according to Hashtable with name name
     * Returns name of Nettype - or null
     */
    public synchronized String addNewNettype(Hashtable specificationTable, String name) {
        return this.acResources.addNewNettype(specificationTable, name);
    }

    public synchronized Graph getNewNetWithoutStdApp(String name, MetaApplication initiator) {
        // Diese Methode ist öffentlich und liefert Netz mit StdApp zurück
        D.d("getNewNet " + name, 3);
        Graph graph = this.acResources.getNewNet(name);
        D.d("getNewNet " + graph, 3);
        if (graph != null) {
            this.acResources.addApp(graph, initiator);
            this.activeNet = graph;
            // ########## Pending wird die stdapp nicht gleich in acr miterzeugt??? Eher nicht...
            //MetaApplication ma = this.getNewStandardApp(net);
            // this.setFocusedApplication(ma);
            //D.d("getNewNet "+ ma);
            // this.setMenu();
        } else {
            D.err("ERROR: AC.getNewNet: Netz nicht erzeugt!");
        }
        return graph;
    }

    public void quitApp(MetaApplication app) {
        this.acResources.removeApplication(app);
        if (this.focusedApplication == app) {
            this.setFocusedApplication(acResources.getActiveApp(this.activeNet));
        }
        this.setMenu();
    } // menuSelectNet
} // class ApplicationControl

