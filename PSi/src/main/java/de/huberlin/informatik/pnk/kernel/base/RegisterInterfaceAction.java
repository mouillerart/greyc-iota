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
   $Log: RegisterInterfaceAction.java,v $
   Revision 1.10  2001/10/11 16:58:25  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:48  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:15  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:56  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:54  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:49  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 08:31:19  hohberg
   Update of comments.

   Revision 1.1  2000/06/21 14:50:03  hohberg
 *** empty log message ***

 */

import de.huberlin.informatik.pnk.app.base.StructuredNetObserver;
import de.huberlin.informatik.pnk.kernel.Node;
import java.util.Observer;

/**
        The observed object is a {@link
        de.huberlin.informatik.pnk.kernel.BlockStructure block}.
        This block gets a new interface node.
 */
public class RegisterInterfaceAction extends ActionObject {
    Node interfaceNode;

    /**
     * Constructor specifying the <code>initiator</code> which
     * defines the new interface node
     * and the new interface <code>node</code>. <br>
     */
    public RegisterInterfaceAction(Object initiator, Node n) {
        super(initiator);
        interfaceNode = n;
    }

    /**
     * Codes the generation of the new interface node using the interface
     * {@link StructuredNetObserver}. <br>
     * Requires: observer implements the interface
     * {@link StructuredNetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof StructuredNetObserver) {
            ((StructuredNetObserver)observer).registerInterface(observedObject, interfaceNode);
        }
    }     //performAction
} // RegisterInterfaceAction