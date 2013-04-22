package de.huberlin.informatik.pnk.netElementExtensions.base;

import de.huberlin.informatik.pnk.kernel.*;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Inscription.java is part of the
   Petri Net Kernel Java reimplementation.
   Inscription.java has been created by the
   PNK JAVA code generator script.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Inscription.java,v $
   Revision 1.11  2001/10/11 16:58:39  oschmann
   Neue Release

   Revision 1.10  2001/06/12 07:03:37  oschmann
   Neueste Variante...

   Revision 1.9  2001/05/11 17:22:21  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.8  2001/02/27 21:29:22  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.6  2001/02/08 13:58:33  hohberg
   Method getPlace() removed

   Revision 1.5  2000/12/14 00:43:15  oschmann
   Neue Version...

   Revision 1.7  2000/09/22 08:43:44  gruenewa
 *** empty log message ***

   Revision 1.2  2000/05/17 14:11:23  juengel
   vorbereitung xml laden / speichern

   Revision 1.1  2000/04/06 10:36:20  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:26  rschulz
   import of paradigm java sources

 */

/**
 * The template for the implementation of an <em> inscription </em> of an
 *  {@link Arc arc}.
 * <br>
 * The <code> "inscription" </code> of an
 * arc is a standard {@link Extension  extension}. Whenever you design
 * your own {@link Net Petri Net} type you either need to implement a
 * custom inscription class (derived from class Extension and
 * implementing <code>Inscription</code>) or you use one of the
 * standard implementations (e.g. {@link Arc arc} multiplicities for
 * Place/Transition-Nets). <br>
 * @author Supervisor
 * @version 1.0
 */
public interface Inscription {
/*	void setPlace(Place p);
 */
    Marking evaluate ();
}
