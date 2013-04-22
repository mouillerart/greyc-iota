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
   $Log: RegisterSonAction.java,v $
   Revision 1.10  2001/10/11 16:58:26  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:48  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:16  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:57  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:55  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:50  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 08:31:20  hohberg
   Update of comments.

   Revision 1.1  2000/06/21 14:48:42  hohberg
 *** empty log message ***

 */

import de.huberlin.informatik.pnk.app.base.StructuredNetObserver;
import de.huberlin.informatik.pnk.kernel.BlockStructure;
import java.util.Observer;

/**
        The observed object is a {@link
        de.huberlin.informatik.pnk.kernel.BlockStructure block}.
        This block gets a new {@link
        de.huberlin.informatik.pnk.kernel.BlockStructure son}.
 */
public class RegisterSonAction extends ActionObject {
    BlockStructure son;

    /**
     * Constructor specifying the <code>initiator</code> which
     * defines the new <code>son</code> and the new son. <br>
     */
    public RegisterSonAction(Object initiator, BlockStructure son) {
        super(initiator);
        this.son = son;
    }

    /**
     * Codes the generation of the new son using the interface
     * {@link StructuredNetObserver}. <br>
     * Requires: observer implements the interface
     * {@link StructuredNetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof StructuredNetObserver) {
            ((StructuredNetObserver)observer).newSon(observedObject, son);
        }
    }     //performAction
} // RegisterSonAction
