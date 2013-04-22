package de.huberlin.informatik.pnk.kernel;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,

   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: NameExtension.java,v $
   Revision 1.13  2001/10/11 16:57:57  oschmann
   Neue Release

   Revision 1.11  2001/06/12 07:03:12  oschmann
   Neueste Variante...

   Revision 1.10  2001/05/11 17:21:52  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.9  2001/02/27 21:29:09  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.8  2001/02/15 12:40:13  hohberg
   Local parse and parse using context

   Revision 1.7  2001/01/16 17:36:55  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:56  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:37  oschmann
   Neue Version...

   Revision 1.8  2000/09/22 08:43:49  gruenewa
 *** empty log message ***

   Revision 1.7  2000/08/30 14:22:48  hohberg
   Update of comments

   Revision 1.6  2000/08/11 09:23:09  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:24  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:46:00  hohberg
   New comments

 */

/**
 * Defines an extension for node  names. <br>
 * The extensions state (value) is the name.
 */
public class NameExtension extends Extension {
    /**
     *  Constructor specifying the extendable.
     */
    public NameExtension(Extendable extendable) {
        super(extendable);
    } // public NameExtension (

    /**
     *  Constructor specifying the <code>extendable</code>
     *  and its <code>name</code>.
     */
    public NameExtension(Extendable extendable, String name) {
        super(extendable, name);
    } // public NameExtension( Extendable extendable, String name)

    /**
     * Returns <code>true</code> if the extension is in a valid state with
     * respect to the named <code>extendable</code>,
     * otherwise <code>false</code>.
     */
    protected boolean isValid() {
        // all names are possible
        return true;
    }

    /**
     * Returns <code>true</code> if the extension is in a valid state with
     * respect to an {@link Extendable extendable} object, otherwise
     * <code>false</code>. <br>
     */
    protected boolean isValid(Extendable extendable) {
        // all extendable may have a name
        return true;
    }

    /**
     * Returns <code>true</code> if the string <code>state</code> represents
     * a valid state for the extension with respect to the extendable
     * of this nameExtension, otherwise <code>false</code>. <br>
     */
    protected boolean isValid(String state)
    {return true; }
} // public class NameExtension
