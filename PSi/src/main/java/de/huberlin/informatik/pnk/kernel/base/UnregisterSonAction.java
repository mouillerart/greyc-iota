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
   $Log: UnregisterSonAction.java,v $
   Revision 1.10  2001/10/11 16:58:29  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:51  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:19  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:43:01  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:59  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:57  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 08:31:20  hohberg
   Update of comments.

   Revision 1.1  2000/06/21 14:48:43  hohberg
 *** empty log message ***

 */

import de.huberlin.informatik.pnk.app.base.StructuredNetObserver;
import de.huberlin.informatik.pnk.kernel.BlockStructure;
import java.util.Observer;

/**
        The observed object is a {@link
        de.huberlin.informatik.pnk.kernel.BlockStructure block}.
        From this block is a {@link
        de.huberlin.informatik.pnk.kernel.BlockStructure son}
        removed. <br>
 */
public class UnregisterSonAction extends ActionObject {
    BlockStructure son;

    /**
     * Constructor specifying the <code>initiator</code> which
     * removes a <code>son</code> and the removed son. <br>
     */
    public UnregisterSonAction(Object initiator, BlockStructure son) {
        super(initiator);
        this.son = son;
    }

    /**
     * Codes the deletion of the son using the interface
     * {@link StructuredNetObserver}. <br>
     * Requires: observer implements the interface
     * {@link StructuredNetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof StructuredNetObserver) {
            ((StructuredNetObserver)observer).removeSon(observedObject, son);
        }
    }     //performAction
} // UnregisterSonAction
