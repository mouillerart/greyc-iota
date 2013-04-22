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
   $Log: TimedNetRule.java,v $
   Revision 1.7  2001/10/11 16:59:19  oschmann
   Neue Release

   Revision 1.5  2001/06/12 07:04:09  oschmann
   Neueste Variante...

   Revision 1.4  2001/06/04 15:19:09  efischer
 *** empty log message ***

   Revision 1.3  2001/05/11 17:23:22  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

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
import java.util.*;

/**
 * The {@link de.huberlin.informatik.pnk.netElementExtensions.base.FiringRule firing rule}
 * of a {@link Net Petri Net} is a standard {@link Extension extension}. <br>
 * The TimedNetRule requires
 * an {@link Arc arc} inscription of type {@link de.huberlin.informatik.pnk.netElementExtensions.base.Inscription}
 * and a {@link IntValue} extension for {@link Transition transitions} with id "delay".
 */
public class TimedNetRule extends SimpleRule {
    protected int clock = 0;
    protected boolean newStep = false; // true: if step started, not finished
    protected Vector delayedTrans = new Vector(5);

    /**
     * Constructor specifying the extended extendable (a net).
     */
    public TimedNetRule(Extendable ext) {
        super(ext);
    }

    private void finishTimeStep() {
        newStep = false;
    }

    /**
     * Fires the given <code>transition</code>.
     */
    public void fire(Transition tr) {
        finishTimeStep();
        int delay = ((IntValue)tr.getExtension("delay")).getValue();
        // System.out.println("Delaye time "+ delay);
        // add delayed transition tr
        delayedTrans.addElement(new DelayedTr(tr, delay + clock));
        subMarkings(tr);
    } // public void fire

    protected void fireDelayedTransitions() {
        Vector del = new Vector(3);
        Enumeration e = delayedTrans.elements();
        while (e.hasMoreElements()) {
            DelayedTr dtr = (DelayedTr)e.nextElement();
            if (dtr.time <= clock) {
                addMarkings(dtr.tr);
                del.addElement(dtr);
                // System.out.println("Delayed transition "+ dtr.tr.getName() + " fired at time " + clock);
            }
        }
        e = del.elements();
        while (e.hasMoreElements()) {
            DelayedTr delTr = (DelayedTr)e.nextElement();
            delayedTrans.removeElement(delTr);
        }
    }

    /**
     * Gets the set of all {@link #isConcessioned concessioned} {@link
     * Transition transitions}. <BR>
     * If no transition is activated finish time step.
     */
    public Vector getAllConcessioned() {
        Vector v = super.getAllConcessioned();
        while (v.isEmpty()) {
            if (delayedTrans.isEmpty()) return null;
            else {
                startTimeStep();
                fireDelayedTransitions();
                v = super.getAllConcessioned();
            }
        }
        return v;
    }

    /**
     * Gets  a {@link #isConcessioned concessioned}
     * transition. <BR>
     * If no transition is activated finish time step.
     */
    public Transition getConcessioned() {
        Transition tr = super.getConcessioned();
        while (tr == null) {
            if (delayedTrans.isEmpty()) return null;
            else {
                startTimeStep();
                fireDelayedTransitions();
                tr = super.getConcessioned();
            }
        }
        return tr;
    }

    public int getTime() {return clock; }
    /**
     * Returns whether the given
     * <code>transition</code> is concessioned. <BR>
     * If for the first transition isConcessioned is called
     * time step begins
     * If one transition fires a time step is completed.
     */
    public boolean isConcessioned(Transition tr) {
        if (newStep == false) {
            startTimeStep();
        }
        return super.isConcessioned(tr);
    } // boolean isConcessioned

    private void startTimeStep() {
        //System.out.println("Timestep at "+clock);
        clock++;
        newStep = true;
        fireDelayedTransitions();
    }
} // TimedNetRule
