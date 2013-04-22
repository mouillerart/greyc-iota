package de.huberlin.informatik.pnk.exceptions;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: ExtensionValueException.java,v $
   Revision 1.4  2001/10/11 16:57:43  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:02:59  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:21:38  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/02/23 15:23:44  hohberg
 *** empty log message ***


 */

import de.huberlin.informatik.pnk.kernel.Extendable;

/**
 *  RuntimeException thrown if a net specification is erronius
 *
 */
public final class ExtensionValueException extends RuntimeException {
    private String extId;
    private Extendable extendable;

    public ExtensionValueException(String s, String id, Extendable e) {
        super(s);
        extId = id;
        extendable = e;
    }

    public Extendable getExtendable() {return extendable; }
    public String getExtendableId() {return extId; }
}
