package de.huberlin.informatik.pnk.kernel;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   $Log: Specification.java,v $
   Revision 1.14  2001/10/11 16:58:03  oschmann
   Neue Release

   Revision 1.12  2001/06/12 07:03:16  oschmann
   Neueste Variante...

   Revision 1.11  2001/05/11 17:21:57  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.9  2001/02/27 21:29:15  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/01/16 17:37:00  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:21:01  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:42  oschmann
   Neue Version...

   Revision 1.10  2000/09/22 08:43:58  gruenewa
 *** empty log message ***

   Revision 1.9  2000/09/12 19:46:51  juengel
   Konstruktor mit Hashtable wiederhergestellt

   Revision 1.8  2000/08/30 14:22:49  hohberg
   Update of comments

   Revision 1.7  2000/08/11 09:23:15  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:26  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 14:38:34  hohberg
   New comments

 */

import java.util.Hashtable;

/**
 * The backbone for any {@link Extension extensions} to a {@link Graph
 * graph} or a {@link Net Petri Net}. <br>
 */
final public class Specification extends Object {
    /**
     * Description of this specification as a list of
     * extendable classes and its {@link Extension extensions}.
     */
    private SpecificationTable specTab = null;

    private String verbalDesciptionOfSpecification = null;

    /**
     * Constructor specifying the specification by verbalSpec. <BR>

     * The verbal specification is a text like the following one:
       <pre>
     * "Extendable" : ()
     * "Graph" : ()
     * "Net" : ("firingRule" : "HamburgRule")
     * "Member" : ("type" : "UniformType")
     * "Node" : ()
     * "Edge" : ()
     * "Place" : ("marking" : "NaturalNumber", "initialMarking" :
     *     "NaturalNumber")
     * "Transition" : ("mode" : "ConstantMode")
     * "Arc" : ("inscription" : "Multiplicities")
       </pre>
     */
    public Specification(String verbalSpec) {
        verbalDesciptionOfSpecification = verbalSpec;
        specTab = new SpecificationTable(verbalSpec);
        specTab.addNameExtensions();
    } // public  Specification( )

    /**
     * Initializes a new specification with theTable. <BR>
     */
    public Specification(Hashtable theTable) {
        specTab = new SpecificationTable(theTable);
        specTab.addNameExtensions();
    } // public  Specification( )

    /**  Generates for an {@link Extendable extendable} class
     * ( the class of <code>extendable</code>) a table with entrys of the
     * form ( Identifier of extension, object for this extension). <BR>
     * The extension objects are initialised with its default value.
     */
    public Hashtable genExtIdToObject(Extendable extendable) {
        return specTab.genExtIdToObject(extendable);
    }

    /**
     * Gets a description of the specification as a list of
     * extendable classes and its {@link Extension extensions}.
     */
    public SpecificationTable getSpecTab() {
        return specTab;
    }

    /**
            Gets the String representation of the specification.
     */
    public String toString() {
        return verbalDesciptionOfSpecification;
    } // public String toString()
} // final class Specification extends Object