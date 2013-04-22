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
   $Log: FlattenAction.java,v $
   Revision 1.10  2001/10/11 16:58:12  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:39  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:06  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:49  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:46  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:28  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 08:31:18  hohberg
   Update of comments.


 */

import de.huberlin.informatik.pnk.app.base.*;
import java.util.Observer;

/**
        The observed object is a
        {@link de.huberlin.informatik.pnk.kernel.BlockStructure block}
        of a {@link de.huberlin.informatik.pnk.kernel.Net net}.
        All internal blocks of this block are to delete.
 */
public class FlattenAction extends ActionObject {
    /**
     * Constructor specifying the <code>initiator</code>. <br>
     */
    public FlattenAction(Object initiator) {
        super(initiator);
    }

    /**
     * Codes the flaten action using the interface {@link
     * StructuredNetObserver}. <br>
     * Requires: oserver implements the interface
     * {@link StructuredNetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof StructuredNetObserver) {
            ((StructuredNetObserver)observer).flatten(observedObject);
        }
    }     //performAction
} // FlattenAction
