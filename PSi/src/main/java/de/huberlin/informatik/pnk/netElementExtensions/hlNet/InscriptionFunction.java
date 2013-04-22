package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: InscriptionFunction.java,v $
   Revision 1.5  2001/10/11 16:59:04  oschmann
   Neue Release

   Revision 1.4  2001/06/12 07:03:56  oschmann
   Neueste Variante...

   Revision 1.3  2001/05/11 17:23:09  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.2  2001/02/27 13:36:15  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:09:57  hohberg
   New package structure

   Revision 1.2  2001/02/05 13:22:11  hohberg
 *** empty log message ***

   Revision 1.1  2001/01/30 14:32:23  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)


 */

import de.huberlin.informatik.pnk.exceptions.*;

/**
   Codes the semantic of an inscription given as a function call
   by implementig the method <code>evaluate</code>. <br>
   Parameters are specified in the constructure.
 */
public abstract class InscriptionFunction implements InscriptionExpression {
    /**
     * Returns the marking this funktion defines. <br>
     */
    public Object evaluate() {return null; }
    /**
     * Sets the i-th parameter of this function. <br>
     */
    public void setParam(int i, Object tt) {
        throw(new KernelUseException("InscriptionExpression.setParam: No parameter expected!"));
    }
} // InscriptionFunction
