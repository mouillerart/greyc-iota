package de.huberlin.informatik.pnk.netElementExtensions.base;

import de.huberlin.informatik.pnk.kernel.Transition;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source FiringRule.java is part of the
   Petri Net Kernel Java reimplementation.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: FiringRule.java,v $
   Revision 1.11  2001/10/11 16:58:38  oschmann
   Neue Release

   Revision 1.10  2001/06/04 15:21:19  efischer
 *** empty log message ***

   Revision 1.9  2001/05/11 17:22:20  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.8  2001/02/27 21:29:21  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.6  2001/02/15 12:52:13  hohberg
   New methods simulateWithUserInteraction() and checkContextAndParseExtensions()

   Revision 1.5  2000/12/14 00:43:14  oschmann
   Neue Version...

   Revision 1.7  2000/09/22 08:43:42  gruenewa
 *** empty log message ***

   Revision 1.6  2000/08/30 14:22:46  hohberg
   Update of comments

   Revision 1.5  2000/08/11 09:23:04  gruenewa
 *** empty log message ***

   Revision 1.2  2000/05/17 14:11:22  juengel
   vorbereitung xml laden / speichern

   Revision 1.1  2000/04/06 10:36:19  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:25  rschulz
   import of paradigm java sources

 */

import de.huberlin.informatik.pnk.app.base.MetaApplication;
import java.util.Vector;

/**
 * Is the template for implementing the <em> firing rule </em> of a
 * {@link de.huberlin.informatik.pnk.kernel.Net Petri Net}.
 * <br>
 * The firing rule of a net is a
 * standard {@link de.huberlin.informatik.pnk.kernel.Extension extension}. Whenever you design your own
 * Petri Net type you either need to implement a custom
 * firing rule class (derived from class{@link de.huberlin.informatik.pnk.kernel.Extension Extension}
 * and implementing
 * <code> FiringRule </code>) or you use one of the standard
 * implementations (e.g. the 'Hamburg' rule for Place/Transition-Nets).
 * <br>
 * @author Supervisor
 * @version 1.0
 */
public interface FiringRule {
    void  checkContextAndParseExtensions ();
    /**
     * Fires the set of {@link Transition transitions} given by
     **<code>transitions</code>. <BR>
     * The  transitions are fired simultaneously. Usually
     * an order is practically introduced due to the sequential nature of
     * the implementation of <code>fire</code>, but no assumptions should me
     * made about this order. <BR>
     */
    void fire (Vector transitions);
    /**
     * Returns the set of all {@link #isConcessioned concessioned} {@link
     * Transition transitions}. <BR>
     */
    Vector getAllConcessioned ();
    /**
     * Returns the set of all {@link #isConcessioned concessioned}
     * {@link Transition transitions}, which are in the
     *  set <code>inclTrans</code> and not in  <code>exclTrans</code>. <BR>
     * The returned set contains  each {@link #isConcessioned
     * concessioned} transition meeting the additional
     * criteria. <BR>
     * Both sets may possibly be empty. Set <code>inclTrans</code> is empty
     * or set <code>exclTrans</code>  equals the set of all transitions
     * implies that an empty set will be returned. If <code>exclTrans</code>
     * is empty, no  transition is a priori excluded from
     * the eventually returned set. <BR>
     */
    Vector getAllConcessioned (Vector inclTrans, Vector exclTrans);
    /**
     * Returns the set of all simultaneously fireable sets ({@link #isStep
     * steps}) of {@link Transition transitions}. <BR>
     * @see #getStep()
     */
    Vector getAllSteps ();
    /**
     * Returns the set of all sets ({@link #isStep steps}) of
     * simultaneously fireable {@link Transition transitions}. <br>
     * Each returned {@link #isStep step} has at least one
     *  transition in common with <code>inclTrans</code> and the
     * intersection with <code>exclTrans</code> is empty. <BR>
     */
    Vector getAllSteps (Vector inclTrans, Vector exclTrans);
    /**
     * Returns a reference to a {@link #isConcessioned concessioned}
     *  transition. <BR>
     */
    Transition getConcessioned ();
    /**
     * Returns a reference to a {@link #isConcessioned concessioned}
     * transition, which is in the  transition
     * set <code>inclTrans</code> and not in <code>exclTrans</code>. <BR>
     * Both sets may possibly be empty. An empty set <code>inclTrans</code>
     * or a set <code>exclTrans</code> equal to the set of all
     * transitions always causes an exception. If
     * <code>exclTrans</code> is empty, no  transition is
     * a priori excluded from the set of possible return values. <BR>
     */
    Transition getConcessioned (Vector inclTrans, Vector exclTrans);
    /**
     * Returns a simultaneously fireable set ({@link #isStep step}) of
     * {@link Transition transitions}. <BR>
     * The step is simultaneously fireable according to
     * the implemented firing rule. Naturally all
     * transitions in a  step are {@link #isConcessioned
     * concessioned}. <BR>
     * The returned {@link #isStep step} may serve as input for {@link
     * #fire fire(step)}. <BR>
     */
    Vector getStep ();
    /**
     * Returns a simultaneously fireable subset ({@link #isStep step}) of
     * set <code> transitions</code>. <br>
     * The {@link #isStep step} has at least one
     * transition in common with <code>inclTrans</code> and the
     * intersection with <code>exclTrans</code> is empty. <BR>
     * The  step is fireable according to the implemented
     * firing rule. Naturally all transitions in the
     * step are {@link #isConcessioned concessioned}. <BR>
     */
    Vector getStep (Vector transitions);
    /**
     * Returns whether the given  <code>transition</code> is
     * concessioned. <BR>
     * The definition of concessioned varies even among the same class of
     * {@link de.huberlin.informatik.pnk.kernel.Net Petri Nets}. The most commonly used definition for
     * P/T-Nets simply demands that each {@link de.huberlin.informatik.pnk.kernel.Place place} in the preset
     * of a {@link Transition transition} must carry at least as many tokens
     * as the multiplicity of the {@link de.huberlin.informatik.pnk.kernel.Arc arc} from the
     * place to the  transition. <br>
     */
    boolean isConcessioned (Transition transition);
    /**
     * Returns whether a set of {@link Transition transitions} is
     * simultaneously fireable. <BR>
     * Be aware of the difference between <em>simultaneously fireable</em>
     * and <em>fireable in an arbitrary order</em> under certain
     * circumstances. <br>
     * The maximal step firing rule for P/T-Nets is an example for the
     * importance of that difference. In that case a step is defined as an
     * inclusion-maximal set of simultaneously fireable {@link Transition
     * transitions}. Under the normal firing rule for P/T-Nets the {@link
     * Transition transitions} in this set could be {@link #fire fired} in
     * an arbitrary order. One can imagine a situation where a
     * transition t, which is not member of the step, can only
     * be {@link #isConcessioned concessioned} <em>during</em> the {@link
     * #fire firing} of the  transitions in the step in
     * an appropriate order. The freedom of choosing the order is taken away
     * by the maximal step firing rule, since there is no 'during'. It all
     * happens in the very same moment. Consequently
     * transition t can never become {@link #isConcessioned concessioned}.
     * <br>
     * What is even more astonishing: because of that feature of P/T-Nets
     * with the maximal step firing rule it is possible to simulate the
     * <em>decrement if not zero and goto i, otherwise goto j</em> operation
     * of a counter automata, which is as expressive as and as hard to
     * analyze as a Turing machine. <br>
     */
    boolean isStep (Vector transitions);

    void simulateWithUserInteraction (MetaApplication app);
    void simulateWithoutUserInteraction (MetaApplication app);
} // public interface FiringRule
