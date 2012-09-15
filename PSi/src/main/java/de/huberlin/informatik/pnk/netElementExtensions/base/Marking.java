package de.huberlin.informatik.pnk.netElementExtensions.base;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Marking.java is part of the
   Petri Net Kernel Java reimplementation.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Marking.java,v $
   Revision 1.13  2001/10/11 16:58:40  oschmann
   Neue Release

   Revision 1.12  2001/06/12 07:03:38  oschmann
   Neueste Variante...

   Revision 1.11  2001/05/11 17:22:22  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.10  2001/03/08 08:37:13  gruenewa
 *** empty log message ***

   Revision 1.9  2001/02/27 21:29:23  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/02/20 15:39:55  hohberg
   Is extendable an arc or place?

   Revision 1.6  2001/02/08 13:57:12  hohberg
   New interface Inscription

   Revision 1.5  2000/12/14 00:43:16  oschmann
   Neue Version...

   Revision 1.8  2000/09/22 08:43:46  gruenewa
 *** empty log message ***

   Revision 1.7  2000/08/30 14:22:47  hohberg
   Update of comments

   Revision 1.6  2000/08/11 09:23:07  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:23  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:36:04  hohberg
 *** empty log message ***

   Revision 1.1  2000/04/06 10:36:21  gruenewa
 *** empty log message ***

 */

import de.huberlin.informatik.pnk.kernel.*;

/**
 * Offers the framework for the implementation of a <em> marking </em> of
 * a  {@link Place place}.
 * <br>
 * The <code> "marking" </code> and the <code> "initial marking" </code>
 * of a {@link Place place} are standard {@link
 * Extension extensions}. Whenever you design your own {@link Net Petri
 * Net} type you either need to implement a custom marking class (derived
 * from class Extension and implementing <code> Marking </code>) or you
 * use one of the standard implementations (for example a multiset of 'black'
 * tokens for Place/Transition-Nets). <br>
 *
 * @author Supervisor
 * @version 1.0
 */
public abstract class Marking extends Extension implements Inscription {
    /**
     * Constructor for an empty marking. <br>
     */
    protected Marking(Extendable extendable) {
        super(extendable);
        String name = extendable.getClass().getName();
        if (!(name.equals("de.huberlin.informatik.pnk.kernel.Place") ||
              name.equals("de.huberlin.informatik.pnk.kernel.Arc"))
            ) {
            throw(new RuntimeException(
                      "Error: Place or Arc expected! Received: " + name));
        }
    }

    /**
     * Constructor specifying the <code>marking</code>. <br>
     */
    protected Marking(Extendable extendable, String marking) {
        super(extendable, marking);
        String name = extendable.getClass().getName();
        if (!(name.equals("de.huberlin.informatik.pnk.kernel.Place") ||
              name.equals("de.huberlin.informatik.pnk.kernel.Arc"))
            ) {
            throw(new RuntimeException(
                      "Error: Place or Arc expected! Received: " + name));
        }
    }

    /**
     * Provides the add operation on markings.
     * Sends the new value to the observer of the {@link Net net}.
     */
    final public void add(Marking marking) {
        localAdd(marking);
        updateValue();
    }

    /**
     * Returns true if <code>marking</code> is contained in this marking.
     * If <code>marking</code> is contained in this marking, then it may be
     * {@link #sub( Marking marking) subtracted} from this marking. <br>
     */
    abstract public boolean contains (Marking marking);
//------------------------- Inscription -----------------------------------//

    public Marking evaluate() {return this; }
    /**
     * Returns whether the marking is empty. <br>
     * The operations {@link #add( Marking marking)
     * add} and {@link #sub( Marking marking) sub} with the empty marking as
     * parameter do <em>not</em> alter the state of a marking. <br>
     */
    final public boolean isEmpty() {return isDefault(); }
    /**
     * Adds <code>marking</code> to this marking.
     */
    abstract protected void localAdd (Marking marking);
    /**
     * Subtracts <code>marking</code> from this marking.
     */
    abstract protected void localSub (Marking marking);
    /**
     * Provides the substract operation on markings.
     * Sends the new value to the observer of the {@link Net net}.
     */
    final public void sub(Marking marking) {
        localSub(marking);
        updateValue();
    }

    /**
     * Sends the value of this marking to the observer
     * of the {@link Net net}.
     */
    final protected void updateValue() {
        Extendable e = getExtendable();
        if (e != null)
            e.updateExtension(null, "marking", toString());
    }
} // public interface Marking