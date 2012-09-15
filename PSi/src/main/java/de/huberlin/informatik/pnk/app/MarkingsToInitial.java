package de.huberlin.informatik.pnk.app;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,

   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: MarkingsToInitial.java,v $
   Revision 1.10  2002/03/20 21:19:26  oschmann
   Neue Version...

   Revision 1.9  2001/10/11 16:56:19  oschmann
   Neue Release

   Revision 1.8  2001/06/12 07:02:18  oschmann
   Neueste Variante...

   Revision 1.7  2001/06/04 15:45:29  efischer
 *** empty log message ***

   Revision 1.6  2001/05/11 17:20:35  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.4  2001/02/27 21:28:22  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.1  2001/02/15 15:08:25  hohberg
 *** empty log message ***

 */

/**
 * Application: Sets markings of all places to initial markings
 */
/**
 * This application sets the markings of all places to their
 * initial marking value. The "initialMarking" is an extension of places.
 */
public class MarkingsToInitial extends MetaApplication {
    public static String staticAppName = "MarkingsToInitial";
    public static boolean startAsThread = false;
    public static boolean startImmediate = true;

    public MarkingsToInitial(ApplicationControl ac) {
        super(ac);
    }

    /**
     * Returns always <code>null</code>
     * so this MetaApplication starts immediately.
     */
    public javax.swing.JMenu[] getMenus() {
        return null;
    }

    /**
     * Just runs the resetMarkings method of the {@link Net net}.
     * @see Net
     */
    public void run() {
        ((Net)net).resetMarkings(null);
        // Applikation beendet sich selbst...
        this.quitMe();
    }
} // MarkingsToInitial
