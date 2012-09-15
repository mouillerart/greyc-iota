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
   $Log: ActionObject.java,v $
   Revision 1.10  2001/10/11 16:58:07  oschmann
   Neue Release

   Revision 1.8  2001/06/12 09:30:35  gruenewa
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:01  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:45  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:40  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:06  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 08:31:17  hohberg
   Update of comments.

 */

/**
      Codes a message from a {@link de.huberlin.informatik.pnk.kernel.Net
      net} to the observer of this net
      by implementig the method <code>performAction</code> using
      the interface {@link NetObserver}. <br>
      Steps of Communication:<br>
      An application (for instance an editor) changes an object cho of the
      net by calling a method of this net.
      The object cho generates an action object acto coding this change and
      sends this object acto to its observer obs
      (usualy the {@link ApplicationControl application control})
      by calling obs.update(cho, acto).<br>
      The application control sends this action object to all
      registered application appli by appli.update(cho, acto).<br>
      Communication path:<br>
   editor --> changed net object --> application control --> application
 */
public class ActionObject extends Object {
    protected Object initiator;     // initiator generates this object

    /**
     * Constructor specifying the <code>initiator</code> which generates
     * this object. <br>
     */
    public ActionObject(Object initiator) {
        this.initiator = initiator;
    }

    /**
     * Gets the initiator.
     */
    public Object getInitiator() {
        return initiator;
    }

    /**
     * Codes the change on a net using the interface {@link NetObserver}. <br>
     * Requires: oserver implements the interface {@link NetObserver}. <br>
     */
    public void performAction(Observer observer, Object observedObject) {
        de.huberlin.informatik.pnk.appControl.base.D.d("ActionObject.perfomAction()");
    }
} // ActionObject