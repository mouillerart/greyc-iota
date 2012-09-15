package de.huberlin.informatik.pnk.kernel.base;

/*
   Petri Net Kernel,
   Copyright 1996-2000 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source DeleteAction.java is part of the
   Petri Net Kernel Java reimplementation.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: DeleteAction.java,v $
   Revision 1.10  2001/10/11 16:58:11  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:38  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:05  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:48  oschmann
   Neue Version...

   Revision 1.5  2000/09/22 08:42:45  gruenewa
 *** empty log message ***

   Revision 1.4  2000/09/18 14:37:26  oschmann
   Zwischenversion

   Revision 1.3  2000/09/04 08:31:18  hohberg
   Update of comments.


 */

import de.huberlin.informatik.pnk.kernel.*;
import java.util.Observer;

/**
 * Message to delete the observed object!
 */
public class DeleteAction extends ActionObject {
    /**
     * Constructor specifying the <code>initiator</code>. <br>
     */
    public DeleteAction(Object initiator) {
        super(initiator);
    }

    /**
     * Codes the delete action using the interface {@link NetObserver}. <br>
     * Requires: observer implements the interface {@link NetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof NetObserver) {
            ((NetObserver)observer).delete((Member)observedObject);
        }
    }     //performAction
} // DeleteAction