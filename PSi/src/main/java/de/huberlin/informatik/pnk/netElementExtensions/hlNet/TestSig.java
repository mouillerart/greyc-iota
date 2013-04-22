package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

/*
   Petri Net Kernel,
   Copyright 1996-2001 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: TestSig.java,v $
   Revision 1.4  2001/10/11 16:59:11  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:04:03  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:23:16  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/03/28 07:58:38  hohberg
   Definition of Inscription variables of subrange type

 */

import de.huberlin.informatik.pnk.kernel.*;

/**
 * Test der Klasse TestSig
 */
public class TestSig extends Signature {
    public Object[] testVar[] =
    {
        {"x", null, "0..10", "de.huberlin.informatik.pnk.netElementExtensions.hlNet.ScalarTypeInt"},
        {"z", null, null}
    };
    private Object[] echoFkt[] =
    {
        {"multiply", "de.huberlin.informatik.pnk.netElementExtensions.hlNet.ScalarTypeInt",
         "multiply(de.huberlin.informatik.pnk.netElementExtensions.hlNet.ScalarTypeInt," +
         "de.huberlin.informatik.pnk.netElementExtensions.hlNet.ScalarTypeInt)", null},
        {"sub", "de.huberlin.informatik.pnk.netElementExtensions.hlNet.ScalarTypeInt",
         "subMessageFrom(java.util.Vector)", null}
    };

    private String[] tokenType[] =
    {
        {"Int", "de.huberlin.informatik.pnk.netElementExtensions.hlNet.ScalarTypeInt"},
        {"Agent", "de.huberlin.informatik.pnk.netElementExtensions.graphAlgorithms.EchoAgent"}
    };
    /**
     *  Constructor specifying the extendable. <br>
     */
    public TestSig(Extendable extendable) {
        super(extendable);
        varField = testVar;
        fktField = echoFkt;
        typeField = tokenType;
        System.out.println("Varfield gesetzt");
    }

    /**
     *  Constructor specifying the extendable and the name
     * of a token class. <br>
     */
    public TestSig(Extendable extendable, String signature) {
        super(extendable, signature);
        varField = testVar;
        fktField = echoFkt;
        typeField = tokenType;
        System.out.println("Varfield gesetzt");
    }
} //public  class TestSig
