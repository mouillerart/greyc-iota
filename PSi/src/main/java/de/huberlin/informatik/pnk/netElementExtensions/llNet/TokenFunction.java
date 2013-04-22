package de.huberlin.informatik.pnk.netElementExtensions.llNet;

/*
   Petri Net Kernel,
   Copyright 1996-2000 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: TokenFunction.java,v $
   Revision 1.4  2001/10/11 16:59:22  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:04:12  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:23:25  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/02/22 16:11:05  hohberg
   New package structure

   Revision 1.1  2001/02/16 15:36:40  hohberg
 *** empty log message ***

 */

import java.util.Vector;

/**
 * Interpretes functions defining initial markings
 */
public interface TokenFunction {
    Vector evaluateInitial (String str);
}
