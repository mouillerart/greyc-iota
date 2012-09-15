package de.huberlin.informatik.pnk.netElementExtensions.base;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: StringExtension.java,v $
   Revision 1.2  2001/10/11 16:58:42  oschmann
   Neue Release

 */

import de.huberlin.informatik.pnk.kernel.*;
/**
 * Extension giving an information (String).
 * Default value is "". <br>
 */

public class StringExtension extends Extension {
    /**
     *  Constructor specifying the extendable. <br>
     */
    public StringExtension(Extendable extendable) {
        super(extendable);
    }

    /**
     *  Constructor specifying the extendable and the string
     */
    public StringExtension(Extendable extendable, String string) {
        super(extendable, string);
    }

    /**
     * Gives the extern representation of default state: "". <br>
     */
    public String defaultToString() {
        return "";
    } // public String defaultToString( )

//////////////////////////////////////////////////
// Implementation of abstract class Extension  //

    /**
     * All strings accepted.
     */
    protected boolean isValid() {
        return true;
    }

    /**
     * StringExtension is possible for all extendable
     */
    protected boolean isValid(Extendable extendable) {
        return true;
    }

    /**
     * All states accepted.
     */
    protected boolean isValid(String state) {
        return true;
    }
} // public class StringExtension