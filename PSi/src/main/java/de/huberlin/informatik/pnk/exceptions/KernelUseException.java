package de.huberlin.informatik.pnk.exceptions;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: KernelUseException.java,v $
   Revision 1.4  2001/10/11 16:57:44  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:03:00  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:21:39  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/02/23 15:23:45  hohberg
 *** empty log message ***


 */

/**
 *  RuntimeException thrown if a net specification is erronius
 *
 */
public final class KernelUseException extends RuntimeException {
    public KernelUseException(String s) {
        super(s);
    }
}
