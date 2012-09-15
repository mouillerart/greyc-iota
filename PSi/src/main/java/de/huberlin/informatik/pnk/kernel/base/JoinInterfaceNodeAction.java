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
   $Log: JoinInterfaceNodeAction.java,v $
   Revision 1.10  2001/10/11 16:58:14  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:40  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:07  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:50  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:47  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:30  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 08:31:18  hohberg
   Update of comments.

 */

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.kernel.Node;
import java.util.Observer;

/**
        The observed object is an interface node of an
        {@link de.huberlin.informatik.pnk.kernel.BlockStructure block}.
        This node is to joined with its joined interface nodes. <BR>
 */
public class JoinInterfaceNodeAction extends ActionObject {
    Node interfaceNode;
    /**
     * Constructor specifying the <code>initiator</code>. <br>
     */
    public JoinInterfaceNodeAction(Object initiator) {
        super(initiator);
    }

    /**
     * Codes the "join with interface" action using the interface
     * {@link StructuredNetObserver}. <br>
     * Requires: observer implements the interface
     *  {@link StructuredNetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof StructuredNetObserver) {
            ((StructuredNetObserver)observer).joinInterfaceNode(observedObject);
        }
    }     //performAction
} // JoinInterfaceNodeAction