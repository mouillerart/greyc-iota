package de.huberlin.informatik.pnk.app;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,

   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Simulator.java,v $
   Revision 1.13  2001/12/12 12:51:22  gruenewa
   Neues tool zum auffalten von dawn netzen.

   Revision 1.11  2001/10/11 16:56:20  oschmann
   Neue Release

   Revision 1.10  2001/06/12 07:02:19  oschmann
   Neueste Variante...

   Revision 1.9  2001/06/04 15:44:59  efischer
 *** empty log message ***

   Revision 1.8  2001/05/11 17:20:36  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.6  2001/03/06 09:12:14  mweber
   Patch: simpleRule was moved to llNet

   Revision 1.5  2001/02/27 21:28:22  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.2  2001/02/14 15:14:52  hohberg
   Uses new method simulateWithUserInteraction from SimpleRule

 */

/**
 * SelfActingSimulator.java
 *
 *
 * Created: Wed Jan 24 08:51:50 2001
 *
 * @author Alexander Gruenewald
 * @version
 */
/**
 * This class implements the standard PNK simulator application.
 */
public class SelfActingSimulator extends MetaApplication {
    public boolean startAsThread = true;
    public static String staticAppName = "Selfacting Simulator";

    public SelfActingSimulator(ApplicationControl ac) {
        super(ac);
    }

    /**
     * Start the simulator, that means:
     * 1. Get the {@link de.huberlin.informatik.pnk.netElementExtensions.base.FiringRule firingRule} for the net and
     * 2. call its method simulateWithUserInteraction
     */
    public void run() {
        FiringRule rule = (FiringRule)net.getExtension("firingRule");
        rule.simulateWithoutUserInteraction(this);
    }
} // SelfActingSimulator
