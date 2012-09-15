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
   $Log: NewTransitionAction.java,v $
   Revision 1.10  2001/10/11 16:58:23  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:46  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:14  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:55  oschmann
   Neue Version...

   Revision 1.5  2000/09/22 08:42:52  gruenewa
 *** empty log message ***

   Revision 1.4  2000/09/18 14:37:46  oschmann
   Zwischenversion

   Revision 1.3  2000/09/04 08:31:19  hohberg
   Update of comments.

 */

import de.huberlin.informatik.pnk.kernel.*;
import java.util.Hashtable;
import java.util.Observer;

/**
        The observed object is a new {@link
        de.huberlin.informatik.pnk.kernel.Transition transition}
 */
public class NewTransitionAction extends ActionObject {
    /**
     * Constructor specifying the <code>initiator</code> which generates
     * the transition, its name,
     * and the hashtable of its extensions. <br>
     */
    public NewTransitionAction(Object initiator, String name, Hashtable extIdToValue) {
        super(initiator);
    }

    /**
     * Codes the generation of a transition using the interface
     * {@link NetObserver}. <br>
     * Requires: observer implements the interface {@link NetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof NetObserver) {
            ((NetObserver)observer).newTransition((Transition)observedObject);
        }
    }     // performAction
} // NewTransitionAction