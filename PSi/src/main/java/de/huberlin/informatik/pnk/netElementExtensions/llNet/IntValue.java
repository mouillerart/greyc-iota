package de.huberlin.informatik.pnk.netElementExtensions.llNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: IntValue.java,v $
   Revision 1.4  2001/10/11 16:59:14  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:04:05  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:23:19  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/02/22 16:11:03  hohberg
   New package structure

   Revision 1.2  2001/02/15 12:46:28  hohberg
   Local and parse using context

   Revision 1.1  2001/02/08 11:36:57  hohberg
 *** empty log message ***

 */

import de.huberlin.informatik.pnk.kernel.*;

public class IntValue extends Extension {
    private int internState = 0;

    public IntValue(Extendable e) {
        super(e);
    }

    public IntValue(Extendable e, String value) {
        super(e, value);
    }

    public String defaultToString() {return "0"; }
    /**
           internState in extenState umwandeln
     */
    private String externRepresentation() {
        return String.valueOf(internState);
    }

    public int getValue() {
        return internState;
    }

    protected boolean isValid() {
        String str = toString();
        return isValid(str);
    }

    protected boolean isValid(Extendable extendable) {
        return true;
    }

    protected boolean isValid(String str) {
        try {
            internState = (new Integer(str)).intValue();
            return true;
        } catch (NumberFormatException e)
        {return false; }
    }

    /**
            Intern state is the int value represented by externstate.
     */
    protected void localParse() {
        String str = toString(); // str externe Darstellung
        //System.out.println("NatNumb.parse: " + str);
        try {
            internState = (new Integer(str)).intValue();
        } catch (NumberFormatException e) {
            System.out.println("ERROR: NatNumb.parse: " + str);
            internState = 0;
        }
    }
} //  public class IntValue