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
   $Log: ChangeExtension.java,v $
   Revision 1.13  2001/10/11 16:58:09  oschmann
   Neue Release

   Revision 1.11  2001/06/12 09:30:36  gruenewa
 *** empty log message ***

   Revision 1.9  2001/05/11 17:22:03  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.8  2001/02/27 21:29:19  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/01/16 17:37:03  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:21:04  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:46  oschmann
   Neue Version...

   Revision 1.5  2000/09/22 08:42:44  gruenewa
 *** empty log message ***

   Revision 1.4  2000/09/18 14:37:21  oschmann
   Zwischenversion

   Revision 1.3  2000/09/04 08:31:17  hohberg
   Update of comments.

 */
import de.huberlin.informatik.pnk.kernel.*;
import java.util.Observer;

/**
   The observed object is an {@link
   de.huberlin.informatik.pnk.kernel.Extendable extendable}.
   An {@link de.huberlin.informatik.pnk.kernel.Extension extension}
   of this extendable gets a new value. <br>
 */
public class ChangeExtension extends ActionObject {
    String extension;  // Identifier of extension
    String newValue; // new value

    /**
     * Constructor specifying the <code>initiator</code> which generates
     * this object, the Identifier of an extension and the new value. <br>
     * Changed is an extension of the observed object.
     */
    public ChangeExtension(Object initiator, String text1, String text2) {
        super(initiator);
        extension = text1;
        newValue = text2;
    } // public class UpdateExtension

    /**
     * Codes the change of an extension of <code>observedObject</code> using
     * the interface {@link NetObserver}. <br>
     * Requires: oserver implements the interface {@link NetObserver}. <br>
     **/
    public void performAction(Observer observer, Object observedObject) {
        if (observer instanceof NetObserver) {
            ((NetObserver)observer).changeExtension((Member)observedObject, extension, newValue);
        }
    } // performAction
} // ChangeExtension
