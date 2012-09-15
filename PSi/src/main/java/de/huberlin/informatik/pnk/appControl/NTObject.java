package de.huberlin.informatik.pnk.appControl;

/*
   Petri Net Kernel,
   Copyright 1996-2001 Petri Net Kernel Team,

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: NTObject.java,v $
   Revision 1.13  2002/03/20 21:19:38  oschmann
   Neue Version...

   Revision 1.12  2001/10/11 16:35:40  oschmann
   Neue Release

   Revision 1.9  2001/06/04 15:40:24  efischer
 *** empty log message ***

   Revision 1.8  2001/05/11 17:21:03  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.7  2001/02/27 21:28:44  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.6  2001/02/13 08:57:50  hohberg
   Class names beginning with '.' are expanded by de.huberlin.informatik.pnk

 */

import java.util.*;
import java.util.Vector;

import java.net.*;

import de.huberlin.informatik.pnk.appControl.base.*;
import de.huberlin.informatik.pnk.kernel.*;

import java.lang.reflect.*; import de.huberlin.informatik.pnk.tools.base.MetaNetType;
public class NTObject extends Object {
    // ##### Verwaltet Nettype...

    final String STD_PATH = "de.huberlin.informatik.pnk";

    private URL url = null;
    private String name = "unnamed nettype";
    // erlaubte Applikationen
    private Vector allowedApplications = new Vector();
    // erlaubte Applikationen
    private Vector allowedInOuts = new Vector();
    // erlaubte Applikationen
    private ApplicationControl applicationControl = null;
    private Specification specification = null;

    /**
     * Constructs a new net type object, specified by the URL of its net type specification.
     */
    NTObject(ApplicationControl ac, URL url) {
        this.nettypeLoader = new NettypeLoader(url);
        if (nettypeLoader.isReady()) {
            this.name = nettypeLoader.getName();
            if (nettypeLoader.isParametric()) {
                isParametric = true;
                String classname = nettypeLoader.getClassname();
                Hashtable parameters = nettypeLoader.getParameters();
                Class mainclass = new PNKClassLoader(classname).getMainClass();
                if (mainclass != null) {
                    Constructor con;
                    try {
                        con = mainclass.getConstructor(new Class[] {new Hashtable().getClass()});
                    } catch (NoSuchMethodException e) {
                        D.d("NetLoader: No Such Method: " + e.toString());
                        return;
                    }
                    try {
                        metaNetType = (MetaNetType)con.newInstance(new Object[] {parameters});
                    } catch (InstantiationException e) {
                        D.d("NetLoader: InstantiationException" + e.toString());
                        return;
                    } catch (InvocationTargetException e) {
                        D.d("NetLoader: InvocationTargetException" + e.toString());
                        return;
                    } catch (IllegalAccessException e) {
                        D.d("NetLoader: IllegalAccessException" + e.toString());
                        return;
                    }
                    isReady = true;
                }
            } else {
                // not Parametric
                specification = new Specification(nettypeLoader.getSpecificationTable());
                isReady = true;
            }
        }
    }

    /**
     * Constructs a new net type object, specified by the specificationTable
     * of its net type specification.
     */
    NTObject(ApplicationControl ac, Hashtable specificationTable) {
        this.applicationControl = ac;
        this.specification = new Specification(specificationTable);
        this.name = "dynamic Nettype";
        isDynamic = true;
        isReady = true;
    }

    /**
     * Constructs a new net type object, specified by the specificationTable
     * of its net type specification.
     */
    NTObject(ApplicationControl ac, Hashtable specificationTable, String name) {
        this.applicationControl = ac;
        this.specification = new Specification(specificationTable);
        this.name = name;
        isDynamic = true;
        isReady = true;
    }

    /**
     * Add ApplicationTypeObject 'app' to the list of allowed applications.
     */
    public void addApplication(ATObject app) {
        if (!(allowedApplications.contains(app))) {
            allowedApplications.add(app);
            //D.d("~~~ NTObject: allowed Apps: " + allowedApplications);
        }
    }

    /**
     * Add IOTypeObject 'app' to the list of allowed file formats.
     */
    public void addInOut(IOTObject app) {
        if (!(allowedInOuts.contains(app))) {
            allowedInOuts.add(app);
            //D.d("~~~ NTObject: allowed InOuts: " + allowedInOuts);
        }
    }

    /**
     * returns a vector of allowed applications
     */
    public Vector getAllowedApplications() {
        return allowedApplications;
    }

    /**
     * returns a vector of allowed file formats
     */
    public Vector getAllowedInOuts() {
        return allowedInOuts;
    }

    /**
     * returns name of this net type
     */
    public String getNettypeName() {
        return this.name;
    }

    /**
     * get a new net object (Graph) of this net type
     */
    public Graph getNewNet() {
        if (isReady) {
            if (isParametric) {
                Hashtable specificationTable = metaNetType.getSpecification();
                if (specificationTable != null) {
                    lastSpecification = new Specification(specificationTable);
                    lastParameters = metaNetType.getParameters();
                    Graph theNet = new Net(lastSpecification);
                    return theNet;
                }
            } else {
                Graph theNet = new Net(specification);
                return theNet;
            }
        }
        return null;
    }

    /**
     * get net type specification
     */
    public Specification getSpecification() {
        return this.specification;
    }

    public boolean isReady() {
        return isReady;
    }

    /**
     * Remove ApplicationTypeObject 'app' from the list of allowed applications.
     */
    public void removeApplication(ATObject app) {
        allowedApplications.remove(app);
    }

    /**
     * Remove IOTypeObject 'app' from the list of allowed file formats.
     */
    public void removeInOut(IOTObject app) {
        allowedInOuts.remove(app);
    }

    private boolean isDynamic = false;      private boolean isParametric = false;   private boolean isReady = false;            // erlaubte Applikationen
    private Vector lastParameters = null;   private Specification lastSpecification = null; private MetaNetType metaNetType = null; private NettypeLoader nettypeLoader = null;    /**
                                                                                                                                                                                    * Insert the method's description here.
                                                                                                                                                                                    * Creation date: (19.9.2001 21:48:18)
                                                                                                                                                                                    * @return java.util.Vector
                                                                                                                                                                                    */
    public java.util.Vector getLastParameters() {
        return lastParameters;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 21:48:18)
       * @return de.huberlin.informatik.pnk.kernel.Specification
       */

    public de.huberlin.informatik.pnk.kernel.Specification getLastSpecification() {
        return lastSpecification;
    }   /**
         * returns name of this net type
         */

    public String getNettypeLongName() {
        String s = getNettypeName();
        if (isParametric) {
            s = s + "(*)";
        }
        if (isDynamic) {
            s = s + " <dynamic>";
        }
        return s;
    }           /**
                 * get a new net object (Graph) of this net type
                 */

    public Graph getNewNet(Vector parameters) {
        if (isReady) {
            if (isParametric) {
                this.lastParameters = parameters;
                this.lastSpecification = new Specification(metaNetType.getSpecification(parameters));
                Graph theNet = new Net(lastSpecification);
                return theNet;
            } else {
                D.d("Net not parametric!");
            }
        } else {
            D.d("Nettype not ready!");
        }
        return null;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public boolean isParametric() {
        return isParametric;
    }
}
