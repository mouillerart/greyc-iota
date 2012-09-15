package de.huberlin.informatik.pnk.kernel.base;

import java.util.Observer;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: NewBlockAction.java,v $
   Revision 1.10  2001/10/11 16:58:18  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:43  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:10  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:52  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:50  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:41  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 08:31:19  hohberg
   Update of comments.

 */

import de.huberlin.informatik.pnk.app.base.*;

/**
        The observed object is a new {@link
        de.huberlin.informatik.pnk.kernel.BlockStructure block}
 */
public class NewBlockAction extends ActionObject {
    /**
     * Constructor specifying the <code>initiator</code> which generates
     * the block. <br>
     */
    public NewBlockAction(Object initiator) {
        super(initiator);
    }

    /**
     * Codes the generation of an block using the interface
     * {@link StructuredNetObserver}. <br>
     * Requires: oserver implements the interface
     * {@link StructuredNetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof StructuredNetObserver) {
            ((StructuredNetObserver)observer).newBlock(observedObject);
        }
    }     //performAction
} // NewBlockAction