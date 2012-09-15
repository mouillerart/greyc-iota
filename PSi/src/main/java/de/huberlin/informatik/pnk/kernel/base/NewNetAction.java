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
   $Log: NewNetAction.java,v $
   Revision 1.11  2001/10/11 16:58:19  oschmann
   Neue Release

   Revision 1.9  2001/06/12 09:30:44  gruenewa
 *** empty log message ***

   Revision 1.7  2001/05/11 17:22:11  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.6  2001/01/16 17:37:05  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.5  2000/12/14 00:42:53  oschmann
   Neue Version...

   Revision 1.5  2000/09/22 08:42:51  gruenewa
 *** empty log message ***

   Revision 1.4  2000/09/18 14:37:42  oschmann
   Zwischenversion

   Revision 1.3  2000/09/04 08:31:19  hohberg
   Update of comments.

 */

import de.huberlin.informatik.pnk.app.base.MetaApplication;
import de.huberlin.informatik.pnk.kernel.*;
import java.util.Observer;

/**
        The observed object is a new {@link
        de.huberlin.informatik.pnk.kernel.Net net}
 */
public class NewNetAction extends ActionObject {
    /**
     * Constructor specifying the <code>initiator</code> which generates
     * the net and the specification table defining the
     * {@link de.huberlin.informatik.pnk.kernel.Specification net type}.
     */
    public NewNetAction(Object initiator, SpecificationTable extendableIdToExtensions) {
        super(initiator);
    }

    /**
     * Codes the generation of a net using the interface
     * {@link NetObserver}. <br>
     * Requires: observer implements the interface {@link NetObserver}. <br>
     */
    public void performAction(MetaApplication observer, Object observedObject) {
        // Neues Netz!
        de.huberlin.informatik.pnk.appControl.base.D.d("..... NewNetAction... " + observer + " " + observedObject);
        // KEIN Test auf Interface NetObserver!!!
        ((MetaApplication)observer).newNet((Net)observedObject);
    }     // performAction

    /**
     * Codes the generation of a net using the interface
     * {@link NetObserver}. <br>
     * Requires: observer implements the interface {@link NetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        // Neues Netz!
        de.huberlin.informatik.pnk.appControl.base.D.d("..... NewNetAction... " + observer + " " + observedObject);
        // KEIN Test auf Interface NetObserver!!!
        ((MetaApplication)observer).newNet((Net)observedObject);
    }     // performAction
} // NewNetAction