package de.huberlin.informatik.pnk.kernel.base;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: NewTransitionArcAction.java,v $
   Revision 1.3  2001/10/11 16:58:24  oschmann
   Neue Release

   Revision 1.1  2001/06/12 09:30:47  gruenewa
 *** empty log message ***

   Revision 1.9  2001/05/11 17:22:09  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.8  2001/02/27 21:29:20  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/01/16 17:37:04  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:21:04  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:51  oschmann
   Neue Version...

   Revision 1.5  2000/09/22 08:42:49  gruenewa
 *** empty log message ***

   Revision 1.4  2000/09/18 14:37:39  oschmann
   Zwischenversion

   Revision 1.3  2000/09/04 08:31:19  hohberg
   Update of comments.

 */

import de.huberlin.informatik.pnk.kernel.*;
import java.util.Hashtable;
import java.util.Observer;

/**
        The observed object is a new {@link
        de.huberlin.informatik.pnk.kernel.Arc arc}
 */
public class NewTransitionArcAction extends ActionObject {
    /**
     * Constructor specifying the <code>initiator</code> which generates
     * the arc, the source and targed node of the arc
     * and the hashtable of extensions. <br>
     */
    public NewTransitionArcAction(Object initiator, Node source, Node target, Hashtable extIdToValue) {
        super(initiator);
    }     // public class UpdateNameAction

    /**
     * Codes the generation of an arc using the interface
     * {@link NetObserver}. <br>
     * Requires: observer implements the interface {@link NetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof NetObserver) {
            ((NetObserver)observer).newTransitionArc((TransitionArc)observedObject);
        }
    }     // performAction
} // NewTransitionArcAction
