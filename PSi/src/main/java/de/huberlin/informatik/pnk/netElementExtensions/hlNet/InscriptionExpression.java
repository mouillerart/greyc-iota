package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

/*
   Petri Net Kernel,
   Copyright 1996-2001 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Inscription.java is part of the
   Petri Net Kernel Java reimplementation.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: InscriptionExpression.java,v $
   Revision 1.5  2001/10/11 16:59:03  oschmann
   Neue Release

   Revision 1.4  2001/06/04 15:29:10  efischer
 *** empty log message ***

   Revision 1.3  2001/05/11 17:23:08  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.2  2001/03/28 08:05:43  hohberg
   Implementation of Inscription variables of subrange type

   Revision 1.1  2001/02/22 16:09:57  hohberg
   New package structure

   Revision 1.2  2001/02/05 13:25:23  hohberg
 *** empty log message ***

   Revision 1.1  2001/01/30 14:32:23  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)

 */

/**
 * The template for the implementation of an <em> inscription </em> of an
 *  {@link de.huberlin.informatik.pnk.kernel.Arc arc}.
 * <br>
 * The <code> "inscription" </code> of an
 * arc is a standard {@link de.huberlin.informatik.pnk.kernel.Extension  extension}. Whenever you design
 * your own {@link de.huberlin.informatik.pnk.kernel.Net Petri Net} type you either need to implement a
 * custom inscription class (derived from class Extension and
 * implementing <code>Inscription</code>) or you use one of the
 * standard implementations (e.g. {@link de.huberlin.informatik.pnk.kernel.Arc arc} multiplicities for
 * Place/Transition-Nets). <br>
 * @author Supervisor
 * @version 1.0
 */
public interface InscriptionExpression {
    Object evaluate ();
}
