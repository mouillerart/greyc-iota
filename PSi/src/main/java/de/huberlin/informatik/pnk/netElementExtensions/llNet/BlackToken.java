package de.huberlin.informatik.pnk.netElementExtensions.llNet;

import de.huberlin.informatik.pnk.kernel.Extendable;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   $Log: BlackToken.java,v $
   Revision 1.7  2001/10/11 16:59:12  oschmann
   Neue Release

   Revision 1.5  2001/06/12 07:04:04  oschmann
   Neueste Variante...

   Revision 1.4  2001/06/04 15:30:41  efischer
 *** empty log message ***

   Revision 1.3  2001/05/11 17:23:18  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.2  2001/02/27 13:30:47  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:11:02  hohberg
   New package structure

   Revision 1.9  2001/02/16 15:32:48  hohberg
   New external representation

   Revision 1.8  2001/02/15 12:46:28  hohberg
   Local and parse using context

   Revision 1.7  2001/01/16 17:37:07  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:21:06  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:43:03  oschmann
   Neue Version...

   Revision 1.8  2000/09/22 08:43:36  gruenewa
 *** empty log message ***

   Revision 1.7  2000/08/30 14:22:44  hohberg
   Update of comments

   Revision 1.6  2000/08/11 09:23:00  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:21  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 10:44:40  hohberg
   New comments

 */

import de.huberlin.informatik.pnk.netElementExtensions.base.*;

/**
   Defines the marking "black token".
   The marking contains at most one token.
   (The external and internal representations are equal.)
 */
public class BlackToken extends Marking {
    /**
     * Class constructor specifying the {@link de.huberlin.informatik.pnk.kernel.Place place}
     * for this marking.
     */

    final String EMPTY = "0";
    final String NOTEMPTY = "1";

    /**
     * Nothing to do. No internal representation.

       protected void localParse()
       {
          if(!isValid())
            throw new ExtensionValueException(
                  "Marking " + toString() + "is not a BlackToken!", "marking", getExtendable());
       }
     */
    public BlackToken(Extendable place) {
        super(place);
    }

    /**
     * Class constructor specifying the {@link de.huberlin.informatik.pnk.kernel.Place place}
     * for this marking and the value (blank or 'o').
     */
    public BlackToken(Extendable place, String marking) {
        super(place, marking);
    }

    /**
          Contains this marking as much token as <code> marking</code>?
     */
    public boolean contains(Marking marking) {
        if (marking.isEmpty()) return true;
        // ein Token in marking enthalten
        if (isEmpty()) return false;
        else return true;
    }

    protected boolean isValid() {
        String str = toString();
        if ((str == EMPTY) || (str == NOTEMPTY)) return true;
        return false;
    }

    protected boolean isValid(Extendable extendable) {
        return false;
    }

    protected boolean isValid(String str) {
        if ((str == EMPTY) || (str == NOTEMPTY)) return true;
        return false;
    }

////////////////  Implementation of the interface Marking  //////////

    protected void localAdd(Marking marking) {
        if (toString() == NOTEMPTY) return;
        if (!marking.isEmpty()) {valueOf(NOTEMPTY); }
    } // void localAdd(Marking marking)

    /**
     * Subtracts <code>marking</code> from this marking.
     * Precondition: This marking contains <code>marking</code>.
     */
    protected void localSub(Marking marking) {
        if (marking.isEmpty()) return;
        toDefault();  // empty marking
    }
} // class BlackToken