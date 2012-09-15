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
   $Log: UnregisterInterfaceAction.java,v $
   Revision 1.10  2001/10/11 16:58:28  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:50  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:18  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:59  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:58  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:55  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 08:31:20  hohberg
   Update of comments.

   Revision 1.1  2000/06/21 14:48:42  hohberg
 *** empty log message ***

 */

import de.huberlin.informatik.pnk.app.base.StructuredNetObserver;
import de.huberlin.informatik.pnk.kernel.Node;
import java.util.Observer;

/**
        The observed object is a {@link
        de.huberlin.informatik.pnk.kernel.BlockStructure block}.
        From this block is an interface node removed.
 */
public class UnregisterInterfaceAction extends ActionObject {
    Node interfaceNode;

    /**
     * Constructor specifying the <code>initiator</code> which
     * removes the interface node
     * and the removed interface <code>node</code>. <br>
     */
    public UnregisterInterfaceAction(Object initiator, Node n) {
        super(initiator);
        interfaceNode = n;
    }

    /**
     * Codes the deletion of the interface node using the interface
     * {@link StructuredNetObserver}. <br>
     * Requires: observer implements the interface
     * {@link StructuredNetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof StructuredNetObserver) {
            ((StructuredNetObserver)observer).unregisterInterface(observedObject, interfaceNode);
        }
    }     //performAction
} // UnregisterInterfaceAction