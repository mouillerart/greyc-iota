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
   $Log: ScalarTypeInt.java,v $
   Revision 1.4  2001/10/11 16:59:09  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:04:00  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:23:14  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/03/28 07:55:52  hohberg
   Implementation of Inscription variables with values of a subrange

 */

/**
 * int extended to ScalarType
 */
import de.huberlin.informatik.pnk.exceptions.*;

public class ScalarTypeInt implements ScalarType {
    private int val;

    public ScalarTypeInt(int i) {
        val = i;
    }

    public ScalarTypeInt(String s) {
        try {val = Integer.parseInt(s); } catch (NumberFormatException nE) {
            throw(new NetSpecificationException("Error in signature: Wrong value of ScalarTypeInt " + s));
        }
    }

    public boolean equals(Object i) {
        try {return val == ((ScalarTypeInt)i).val; } catch (ClassCastException cE) {return false; }
    }

    public static ScalarTypeInt multiply(ScalarTypeInt i1, ScalarTypeInt i2) {
        return new ScalarTypeInt(i1.val * i2.val);
    }

    public ScalarType next() {
        return new ScalarTypeInt(val + 1);
    }

    public String toString() {
        return String.valueOf(val);
    }
} // class ScalarTypeInt
