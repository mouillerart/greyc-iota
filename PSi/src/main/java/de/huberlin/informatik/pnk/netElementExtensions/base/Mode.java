package de.huberlin.informatik.pnk.netElementExtensions.base;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Mode.java is part of the
   Petri Net Kernel Java reimplementation.
   Mode.java has been created by the
   PNK JAVA code generator script.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Mode.java,v $
   Revision 1.8  2001/10/11 16:58:41  oschmann
   Neue Release

   Revision 1.7  2001/06/04 15:27:41  efischer
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:23  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:43:17  oschmann
   Neue Version...

   Revision 1.7  2000/09/22 08:43:48  gruenewa
 *** empty log message ***

   Revision 1.6  2000/08/30 14:22:47  hohberg
   Update of comments

   Revision 1.5  2000/08/11 09:23:08  gruenewa
 *** empty log message ***

   Revision 1.2  2000/05/17 14:11:24  juengel
   vorbereitung xml laden / speichern

   Revision 1.1  2000/04/06 10:36:22  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:26  rschulz
   import of paradigm java sources

 */

/**
 * Is the abstract prototype for an implementation of a <em> mode </em>
 * of a {@link de.huberlin.informatik.pnk.kernel.Transition transition}.
 * <br>
 * The <code> mode </code> of a
 * transition is a standard {@link de.huberlin.informatik.pnk.kernel.Extension extension}. Whenever you
 * design your own {@link de.huberlin.informatik.pnk.kernel.Net Petri Net} type you either need to
 * implement a custom mode class or you use one of the standard
 * implementations (e.g. the 'constant' mode for Place/Transition-Nets).
 * <br>
 * @author Supervisor
 * @version 1.0
 */
public interface Mode   //Iterator
{
    /**
     * An {@link Inscription inscription} of an {@link de.huberlin.informatik.pnk.kernel.Arc arc} is evaluated
     * to a marking depending on this mode.
     */
    Marking evaluate (Inscription inscription);
} // public interface Mode extends Iterator
