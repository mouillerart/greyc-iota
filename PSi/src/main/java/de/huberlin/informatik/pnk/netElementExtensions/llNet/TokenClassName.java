package de.huberlin.informatik.pnk.netElementExtensions.llNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: TokenClassName.java,v $
   Revision 1.5  2001/10/11 16:59:21  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:04:11  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:23:24  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/02/22 16:11:05  hohberg
   New package structure

   Revision 1.3  2001/02/15 13:03:56  hohberg
   New method checkContextAndParse()

   Revision 1.2  2001/02/05 13:22:10  hohberg
 *** empty log message ***

   Revision 1.1  2001/01/30 14:32:27  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)


 */

import de.huberlin.informatik.pnk.kernel.*;
/**
 * Extension giving the name of a token class.
 * Default value is "String". <br>
 * A class is a token class if:
 * The constructor specifies the String representation.<br>
 * The method toString gives this representation.<br>
 * The method isEqual compares the internal representations.<br>
 * Examples are Integer, String, Boolean, Character, ...
 */

public class TokenClassName extends Extension {
    /**
     * Nothing to do.

       protected void localParse( ) { }
     */

    /**
     *  Constructor specifying the extendable. <br>
     */
    public TokenClassName(Extendable extendable) {
        super(extendable);
    }

    /**
     *  Constructor specifying the extendable and the name
     * of a token class. <br>
     */
    public TokenClassName(Extendable extendable, String tokenClassName) {
        super(extendable, tokenClassName);
    }

    /**
     * Gives the extern representation of default state: "String". <br>
     */
    public String defaultToString() {
        return "java.lang.String";
    } // public String defaultToString( )

//////////////////////////////////////////////////
// Implementation of abstract class Extension  //

    protected boolean isValid() {
        return true;
    }

    protected boolean isValid(Extendable extendable) {
        return true;
    }

    protected boolean isValid(String state) {
        return true;
    }
} // public class TokenClassName
