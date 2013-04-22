package de.huberlin.informatik.pnk.netElementExtensions.llNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Inscription.java is part of the
   Petri Net Kernel Java reimplementation.
   Inscription.java has been created by the
   PNK JAVA code generator script.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: DelayedTr.java,v $
   Revision 1.3  2001/10/11 16:59:13  oschmann
   Neue Release

   Revision 1.1  2001/06/12 09:22:45  gruenewa
 *** empty log message ***

   Revision 1.2  2001/02/27 13:30:47  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:11:04  hohberg
   New package structure

   Revision 1.2  2001/02/15 12:46:27  hohberg
   Local and parse using context

   Revision 1.1  2001/02/08 11:36:58  hohberg
 *** empty log message ***

 */

import de.huberlin.informatik.pnk.kernel.*;

class DelayedTr {
    protected Transition tr;
    protected int time;

    protected DelayedTr(Transition tr, int time) {
        this.tr = tr;
        this.time = time;
    }
} //private class DelayedTr
