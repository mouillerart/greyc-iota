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
   $Log: ScalarType.java,v $
   Revision 1.4  2001/10/11 16:59:08  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:04:00  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:23:13  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/03/28 07:55:52  hohberg
   Implementation of Inscription variables with values of a subrange

 */

/**
 * Interface for Variables of InscriptionExpression in HLNets
 * which values are a subrange from a value a to value b
 * defined by "a..b".
 * That is, the ScalarType has to implement a method next(ScalarType o) to compute the Object following o
 * and a constructor with String parameter to construct o = new ScalarType("a");
 * The method toString() should give the string parameter of constructor.
 * The method equals(Object o) should compute true if o is of ScalarType and
 * the String representations of this object and o are equal.
 */
public interface ScalarType {
    ScalarType next ();
} // interface ScalarType