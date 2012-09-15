package de.huberlin.informatik.pnk.kernel;

//import de.huberlin.informatik.pnk.netElementExtensions.dawnNet.EchoSignature;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   $Log: SpecificationTable.java,v $
   Revision 1.20  2001/12/18 13:12:19  efischer
   name extension nicht fuer Kanten

   Revision 1.19  2001/10/11 16:58:04  oschmann
   Neue Release

   Revision 1.17  2001/06/12 07:03:17  oschmann
   Neueste Variante...

   Revision 1.16  2001/06/04 15:33:48  efischer
 *** empty log message ***

   Revision 1.15  2001/05/11 17:21:58  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.14  2001/04/23 08:02:12  gruenewa
 *** empty log message ***

   Revision 1.13  2001/03/30 12:57:57  hohberg
   New error handling
   r handling

   Revision 1.12  2001/03/06 09:13:57  mweber
   EchoSignature is not necessary in SpecificationTable

   Revision 1.11  2001/02/27 21:29:16  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.8  2001/02/12 12:51:36  hohberg
   RuntimeError if Place or Transition not allowed in net type

   Revision 1.7  2001/01/16 17:37:01  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:21:02  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:43  oschmann
   Neue Version...

   Revision 1.9  2000/09/12 19:47:56  juengel

   nstruktor mit Hashtable wiederhergestellt
   Revision 1.10  2000/09/22 08:43:59  gruenewa
 *** empty log message ***

   nstruktor mit Hashtable wiederhergestellt

   Revision 1.8  2000/08/30 14:22:50  hohberg
   Update of comments

   Revision 1.7  2000/08/11 09:23:16  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:27  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 15:00:05  hohberg
   New comments

 */
import de.huberlin.informatik.pnk.exceptions.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import java.util.Arrays;
import de.huberlin.informatik.pnk.appControl.base.D;

public class SpecificationTable {
    private String table; // given specification tabel
    private Hashtable classNameToExtIds = null; // created hashtable
    /**
     * Path to the extensions in specified net type. <br>
     */
    private String extensionPath = "de.huberlin.informatik.pnk";

    private int index;
    private int length;

    /**
     * Constructor generates hashtable classNameToExtIds
     * by analysing <code>table</code>. <br>
     */

    public SpecificationTable(String table) {
        this.table = table;
        // de.huberlin.informatik.pnk.appControl.base.D.d("*********Parse net type specification");

        length = table.length();
        index = 0;
        classNameToExtIds = parse();
        /*
           Enumeration keys = classNameToExtIds.keys();
           while( keys.hasMoreElements())
           { // keys sind Strings
              String k = (String) (keys.nextElement());
              de.huberlin.informatik.pnk.appControl.base.D.d(k);
           }
         */
    }

    /**
     * Constructor generates hashtable classNameToExtIds
     * from <code>theTable</code>. <br>
     */

    public SpecificationTable(Hashtable theTable) {
        classNameToExtIds = theTable;
        // Substitute leading "." by extensionPath
        Enumeration extendables = classNameToExtIds.keys();
        Vector ext = new Vector(3);
        while (extendables.hasMoreElements()) { // names of extendable classes
            String k = (String)(extendables.nextElement());
            //de.huberlin.informatik.pnk.appControl.base.D.d("Extendable in Table: "+k);
            if (k.charAt(0) == '.') {
                ext.addElement(k);
            }
            Hashtable tab = (Hashtable)(classNameToExtIds.get(k));
            // klass names of extensions?
            Enumeration e = tab.keys();
            while (e.hasMoreElements()) {
                String key = (String)(e.nextElement());
                String kn = (String)(tab.get(key));
                // extend names
                if (kn.charAt(0) == '.') {
                    tab.put(key, extensionPath + kn);
                }
            }
        }
        // extend extendable class names
        Enumeration names = ext.elements();
        while (names.hasMoreElements()) {
            String str = (String)(names.nextElement());
            classNameToExtIds.put(extensionPath + str, classNameToExtIds.get(str));
            classNameToExtIds.remove(str);
        }
    }

    private Hashtable extensionTupel() {
        Hashtable extensions = new Hashtable(5);
        String extensionName;
        String extensionClassName;
        if (!readOpenBracket()) {
            throw(new NetSpecificationException("Missing '(' at "
                                                + table.substring(index)));
        }
        // Oeffnende Klammer gelesen!
        if (readCloseBracket()) { // keine Extensions
            return extensions;
        }
        do {
            extensionName = readName();
            if (extensionName == null) {
                throw(new NetSpecificationException("Missing ')' or name at "
                                                    + table.substring(index)));
            }
            if (!readColon()) {
                throw(new NetSpecificationException("Missing ':' at "
                                                    + table.substring(index)));
            }
            extensionClassName = readName();
            if (extensionClassName == null) {
                throw(new NetSpecificationException("Missing class name at "
                                                    + table.substring(index)));
            }

            // add extension path if extensionClassName starts with "."
            if (extensionClassName.charAt(0) == '.') {
                extensionClassName =
                    extensionPath + extensionClassName;
            }
            extensions.put(extensionName, extensionClassName);
        } while (readKomma());  // ',' trennt mehrere Angaben
        if (!readCloseBracket()) { // Missing ')'
            de.huberlin.informatik.pnk.appControl.base.D.d("extensionTupel: Missing ')'");
            throw(new NetSpecificationException("Missing ')' after " +
                                                extensionName + ", " + extensionClassName));
        }
        return extensions;
    } // private Hashtable extensionTupel()

    /**
     * Gets a hashtable for <code>extendableobject</code> with
     * entrys of the form <br>
     * (extension name, default extension object) for each extension.

     */
    public Hashtable genExtIdToObject(Extendable extendableObject) {
        String extendable = extendableObject.getClass().getName();

        // de.huberlin.informatik.pnk.appControl.base.D.d("Klasse gefunden: "+extendable);
        Hashtable extIdToObject = new Hashtable(8);
        // Tabelle t: ExtensionId to ExtensionClassId
        if (!classNameToExtIds.containsKey(extendable)) {
            //de.huberlin.informatik.pnk.appControl.base.D.d("SpecTab: No entry for " + extendable);
            return null;
        }
        Hashtable t = (Hashtable)classNameToExtIds.get(extendable);
        if (t.isEmpty()) {
            //de.huberlin.informatik.pnk.appControl.base.D.d("SpecTab: No Extensions for " + extendable);
            return extIdToObject;
        }

        Enumeration extIds = t.keys();         // Enumeration of ExtensionIds
        // parameter is the extendableObject
        Object[] params = {extendableObject};
        // parameter is of type Extendable:
        Class[] paramTypes = {Extendable.class };
        while (extIds.hasMoreElements()) {
            // extIds are Strings
            String id = (String)(extIds.nextElement());
            Class extClass;
            // de.huberlin.informatik.pnk.appControl.base.D.d("genExtIdToObject "+id);
            String extClassName = (String)t.get(id);
            try {
                // de.huberlin.informatik.pnk.appControl.base.D.d("genExtIdToObject "+extClassName);
                extClass = Class.forName(extClassName);
                //de.huberlin.informatik.pnk.appControl.base.D.d("Klasse gefunden");
                Constructor extClassConstructor = extClass.getConstructor(paramTypes);
                //de.huberlin.informatik.pnk.appControl.base.D.d("Konstruktor gefunden");
                Extension extObject = (Extension)extClassConstructor.newInstance(params);
                //de.huberlin.informatik.pnk.appControl.base.D.d("Objekt erzeugt");
                extIdToObject.put(id, extObject);
            } catch (ClassNotFoundException cE) {
                de.huberlin.informatik.pnk.appControl.base.D.d("genExtIdToObject: Class " + extClassName + "not found");
                throw(new NetSpecificationException("Class " +
                                                    extClassName + " not found"));
            } catch (NoSuchMethodException cE) {
                de.huberlin.informatik.pnk.appControl.base.D.d("genExtIdToObject: Class " + extClassName + " constructor?");
                throw(new NetSpecificationException("Class " + extClassName + " constructor?"));
            } catch (InstantiationException cE) {
                de.huberlin.informatik.pnk.appControl.base.D.d("genExtIdToObject: Class " + extClassName + " constructor?");
                throw(new NetSpecificationException("Object of Class " + extClassName + " not crated"));
            } catch (InvocationTargetException cE) {
                de.huberlin.informatik.pnk.appControl.base.D.d("genExtIdToObject: Error in constructor of " + ": " +
                                                               extClassName + ", " + cE.toString());
                Throwable e = cE.getTargetException();
                de.huberlin.informatik.pnk.appControl.base.D.d("Exception:" + e.toString());
                throw(new NetSpecificationException("Class " + extClassName + " constructor?"));
            } catch (IllegalAccessException cE) {
                de.huberlin.informatik.pnk.appControl.base.D.d("genExtIdToObject: " + cE.toString());
                throw(new NetSpecificationException("Class " + extClassName + " constructor?"));
            }
        }
        if (extIdToObject.isEmpty()) {
            de.huberlin.informatik.pnk.appControl.base.D.d("Leere Tabelle");
        }
        return extIdToObject;
    } // public Hastable genExtIdToObject()

    /** Gets an Enumeration of the names of all {@link Extension extensions}
          of the extendable class with <code>extendableClassId</code>.
     */
    public Enumeration getExtIds(String extendableClassId) {
        Hashtable t = (Hashtable)classNameToExtIds.get(extendableClassId);
        if (t == null) return null;
        return t.keys();
    } // getExtIds

    /**
     * Gets the table (extensin identifier, extension class name)
     * for class <code>extendable</code>.
     */
    private Hashtable getExtIdToExtClass(String extendable) {
        return (Hashtable)classNameToExtIds.get(extendable);
    }

    public Hashtable getSpecificationTable() {
        return this.classNameToExtIds;
    }

    private Hashtable parse() { // read the sequence of extensionTupel
        Hashtable specTable = new Hashtable(15);
        do {
            String extendableClass = readName();
            // add extension path if extendableClass starts with "."
            if (extendableClass.charAt(0) == '.') {
                extendableClass =
                    extensionPath + extendableClass;
            }
            // de.huberlin.informatik.pnk.appControl.base.D.d("New entry for "+extendableClass);

            if (!readColon() || extendableClass == null) {
                throw(new NetSpecificationException("Parse: class name and ':' expected"));
            }
            Hashtable extensionTable = extensionTupel();
            specTable.put(extendableClass, extensionTable);
        } while (index < length);
        return specTable;
    } // public Hashtable parse()

    private boolean readCloseBracket() {
        skipSpace();
        if (table.charAt(index) != ')') {
            return false;
        } else {
            index++; return true;
        }
    }

    private boolean readColon() {
        skipSpace();
        if (table.charAt(index) != ':') {
            return false;
        } else {
            index++; return true;
        }
    }

    private boolean readKomma() {
        skipSpace();
        if (table.charAt(index) != ',') {
            return false;
        } else {
            index++; return true;
        }
    }

    private String readName() {
        skipSpace();
        int startIndex = index;
        // de.huberlin.informatik.pnk.appControl.base.D.d("Index:"+index+"  "+length);
        if (table.charAt(index) != '"') {
            return null;
        } else {
            index++;
            while ((table.charAt(index) != '"') && (index < length)) {
                index++;
            }
            //de.huberlin.informatik.pnk.appControl.base.D.d("Index:"+index);
            return table.substring(startIndex + 1, index++);
        }
    }

    boolean readOpenBracket() {
        skipSpace();
        if ((index > length - 1) || (table.charAt(index) != '(')) {
            return false;
        } else {
            index++; return true;
        }
    }

    /**
     * Sets the extension path
     */
    private void setExtensionPath(String str) {
        extensionPath = str;
    }

    private void skipSpace() {
        while (index < length &&
               (table.charAt(index) == ' ' || table.charAt(index) == '\n')) {
            index++;
        }
    }

    /**
           Standard {@link  Extension extension} <code>extensionClassName</code>
           is added for {@link Extendable extendable}.
     */
    final public void addExtension(String extendable, String name, String extensionClassName) {
        Hashtable extTab = (Hashtable)classNameToExtIds.get(extendable);
        if (extTab != null)
            extTab.put(name, extensionClassName);
        else {
            extTab = new Hashtable();
            extTab.put(name, extensionClassName);
            classNameToExtIds.put(extendable, extTab);
        }
    }             /**
                     Standard {@link  Extension extension} <code>extensionClassName</code>
                     is added for {@link Extendable extendable}.
                   */

    final public void addNameExtensions() {
        Enumeration e = classNameToExtIds.keys();
        String[] ccn = {
            "de.huberlin.informatik.pnk.kernel.Graph",
            "de.huberlin.informatik.pnk.kernel.Net",
            "de.huberlin.informatik.pnk.kernel.Node",
            "de.huberlin.informatik.pnk.kernel.Place",
            "de.huberlin.informatik.pnk.kernel.Transition",
            "de.huberlin.informatik.pnk.kernel.Edge",
            "de.huberlin.informatik.pnk.kernel.Arc",
            "de.huberlin.informatik.pnk.kernel.PlaceArc",
            "de.huberlin.informatik.pnk.kernel.TransitionArc"
        };
        String[] elementsWithName = {
            "de.huberlin.informatik.pnk.kernel.Graph",
            "de.huberlin.informatik.pnk.kernel.Net",
            "de.huberlin.informatik.pnk.kernel.Node",
            "de.huberlin.informatik.pnk.kernel.Place",
            "de.huberlin.informatik.pnk.kernel.Transition"
        };
        Vector vcn = new Vector(Arrays.asList(ccn));
        Vector ewn = new Vector(Arrays.asList(elementsWithName));
        boolean[] found = {false, false, false, false, false, false, false, false, false};
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            try {
                Class c = Class.forName(name);
                while (c != null) {
                    if (!vcn.contains(c.getName())) {
                        c = c.getSuperclass();
                    } else {
                        if (ewn.contains(c.getName()))
                            addExtension(name, "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                        found[vcn.indexOf(c.getName())] = true;
                        break;
                    }
                }
                if (c == null) {
                    D.err("Specification: Class not part of PetriNetKernel: " + name);
                }
            } catch (ClassNotFoundException ex) {
                D.err("Specification: Class not found: " + name);
            }
        }
        // PENDING!!! Auf Legalität pr_en!!! Und ergötzen!!!
        boolean graph = found[0] || found[2] || found[5];
        boolean net = found[1] || found[3] || found[4] || found[6] || found[7] || found[8];
        if (graph && net) {
            D.err("ERROR: Nettype contains elements of Graph and Net!");
        } else {
            if (graph) {
                if (!found[0]) {
                    addExtension("de.huberlin.informatik.pnk.kernel.Graph", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                }
                if (!found[2]) {
                    addExtension("de.huberlin.informatik.pnk.kernel.Graph", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                }
                if (!found[5]) {
                    addExtension("de.huberlin.informatik.pnk.kernel.Graph", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                }
            } else if (net) {
                if (!found[1]) {
                    addExtension("de.huberlin.informatik.pnk.kernel.Net", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                }
                if ((!found[3]) && (!found[4])) {
                    D.err("ERROR: Nettype Net contains no Places or Transitions!");
                }
                if (found[7] && (!found[3])) {
                    addExtension("de.huberlin.informatik.pnk.kernel.Place", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                }
                if (found[8] && (!found[4])) {
                    addExtension("de.huberlin.informatik.pnk.kernel.Transition", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                }
                if ((!found[6]) && (!found[7]) && (!found[8])) {
                    // keine Kanten...
                    if (found[3] && found[4]) {
                        addExtension("de.huberlin.informatik.pnk.kernel.Arc", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                    } else if (found[3]) {
                        addExtension("de.huberlin.informatik.pnk.kernel.PlaceArc", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                    } else if (found[3] && found[4]) {
                        addExtension("de.huberlin.informatik.pnk.kernel.TransitionArc", "name", "de.huberlin.informatik.pnk.kernel.NameExtension");
                    } else {
                        D.err("ERROR: Nettype Net contains no Places or Transitions!");
                    }
                }
            } else {
                D.err("ERROR: Nettype contains no Petrinetelements!");
            }
        }
    }
}                                                                                                                                                                             // public class SpecificationTable