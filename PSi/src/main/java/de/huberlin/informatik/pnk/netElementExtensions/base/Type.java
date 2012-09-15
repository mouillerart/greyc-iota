package de.huberlin.informatik.pnk.netElementExtensions.base;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Type.java is part of the
   Petri Net Kernel Java reimplementation.
   Type.java has been created by the
   PNK JAVA code generator script.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Type.java,v $
   Revision 1.8  2001/10/11 16:58:43  oschmann
   Neue Release

   Revision 1.7  2001/06/04 15:28:13  efischer
 *** empty log message ***

   Revision 1.6  2001/05/11 17:22:24  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:43:19  oschmann
   Neue Version...

   Revision 1.6  2000/09/22 08:44:05  gruenewa
 *** empty log message ***

   Revision 1.5  2000/08/11 09:23:18  gruenewa
 *** empty log message ***

   Revision 1.2  2000/05/17 14:11:28  juengel
   vorbereitung xml laden / speichern

   Revision 1.1  2000/04/06 10:36:29  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:26  rschulz
   import of paradigm java sources

 */

/**
 * Is the template for an implementation of a <em> type </em> of a {@link
 * de.huberlin.informatik.pnk.kernel.Net Petri Net} {@link de.huberlin.informatik.pnk.kernel.Member member}.
 * <br>
 * The <code> "type" </code> of a {@link de.huberlin.informatik.pnk.kernel.Net Petri Net}
 * {@link de.huberlin.informatik.pnk.kernel.Member member}
 * is a standard {@link de.huberlin.informatik.pnk.kernel.Extension  extension}. Whenever you
 * design your own {@link de.huberlin.informatik.pnk.kernel.Net Petri Net} type you either need to
 * implement a custom type class or you use one of the standard
 * implementations (e.g. the 'uniform' type for Place/Transition-Nets).
 * <br>
 * <p>
 * <b> To do: </b> Standard implementations of the extension <code>
 * "type" </code> need to be provided for Place/Transition- and
 * High-Level-Nets. <br>
 * @author Supervisor
 * @version 1.0
 */
public interface Type {}