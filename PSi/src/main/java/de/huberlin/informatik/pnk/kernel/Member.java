package de.huberlin.informatik.pnk.kernel;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Member.java is part of the
   Petri Net Kernel Java reimplementation.
   Member.java has been created by the
   PNK JAVA code generator script.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Member.java,v $
   Revision 1.12  2001/10/11 16:57:56  oschmann
   Neue Release

   Revision 1.10  2001/06/12 07:03:11  oschmann
   Neueste Variante...

   Revision 1.9  2001/05/11 17:21:51  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.8  2001/02/27 21:29:08  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/01/16 17:36:53  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:55  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:36  oschmann
   Neue Version...

   Revision 1.9  2000/09/22 08:43:47  gruenewa
 *** empty log message ***

   Revision 1.8  2000/08/30 14:22:47  hohberg
   Update of comments

   Revision 1.7  2000/08/11 09:23:07  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:24  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:42:48  hohberg
   New comments

   Revision 1.1  2000/04/06 10:36:21  gruenewa
 */

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Represents the common features of all {@link Graph graph} and {@link
 * Net Petri Net} <em> members </em>: ({@link Node nodes}, {@link Edge
 * edges}, {@link Place places}, {@link Transition transitions} and
 * {@link Arc arcs}).
 * <br>
 * @version 1.0
 */
public abstract class Member extends Extendable {
    /**
     * Initializes a new member of a {@link Graph graph} or a {@link Net
     * Petri Net} with its {@link Extension extensions} set to {@link
     * Extension#isDefault default} states. <BR>
     * For a detailed description of the treatment of the
     * extensions during instantiation refer to {@link
     * Extendable#Extendable(Graph graph)}. <br>
     * @param graph refers to the {@link Graph graph} or {@link Net Petri
     * Net}, the new member is going to register with.
     */
    protected Member(Graph graph) {
        super(graph);
    } // protected  Member( Graph graph)

    /**
     * Gets a list of pairs (key, value) for all
     * {@link Extension extensions} of this member.
     */
    public Hashtable getExtIdToValue() {
        Hashtable extIdToValue = new Hashtable(5);
        Hashtable extIdToObject = getExtIdToObject();
        if (extIdToObject == null) return null;
        Enumeration keys = extIdToObject.keys(); // enumeration of keys
        while (keys.hasMoreElements()) { // copy the values
            String k = (String)(keys.nextElement());
            extIdToValue.put(k,
                             ((Extension)(extIdToObject.get(k))).toString());
        }
        return extIdToValue;
    } // public HashTable getExtIdToValue()
} // abstract class Member extends Extendable
