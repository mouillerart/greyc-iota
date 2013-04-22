package de.huberlin.informatik.pnk.kernel;

import de.huberlin.informatik.pnk.kernel.base.*;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source ModulDescription.java is part of the
   Petri Net Kernel Java reimplementation.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: BlockStructure.java,v $
   Revision 1.8  2001/10/11 16:57:48  oschmann
   Neue Release

   Revision 1.7  2001/06/12 07:03:05  oschmann
   Neueste Variante...

   Revision 1.6  2001/05/11 17:21:44  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:29  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:43:37  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/11 09:04:35  hohberg
   Implementation of structured nets

   Revision 1.2  2000/08/30 14:22:44  hohberg
   Update of comments

   Revision 1.1  2000/06/22 07:10:18  hohberg

   Implementation of structured nets.
 */

import java.util.Enumeration;
import java.util.Observer;
import java.util.Vector;

/**
 * A {@link Net net} may be structured by a tree of blocks
 * of type BlockStructure.
 * A block has a name and id,
 * a list of sons and one father,
 * and contains some places and transitions of the net.
 * Some of the nodes are visible from outside (interface nodes).
 */
public class BlockStructure extends NetObservable {
    private Net net;
    private String name = null;
    private String id = null;
    private Vector sons = new Vector(3);
    private BlockStructure father = null;
    private Vector places = new Vector(8);
    private Vector transitions = new Vector(8);
    private Vector interfaceNodes = new Vector(5);

    /**
     * Class Constructor specifying the name, id and net.
     * This block is empty and has no father and no suns.
     */
    public BlockStructure(Net net, String blockName, String blockId, Object initiator) {
        name = blockName;
        id = blockId;
        this.net = net;
        Observer o = net.getObserver();
        if (o != null) {
            NewBlockAction b = new NewBlockAction(initiator);
            o.update(this, b);
        }
    }

    /**
     * Deletes all contained blocks.
     * All nodes of contained blocks turn to nodes of this block.
     * Interface places stay interface places.
     */
    public void flatten(Object initiator) {
        Observer o = net.getObserver();
        if (o != null) {
            ActionObject b = new FlattenAction(initiator);
            o.update(this, b);
        }
        // flatten of sons
        Enumeration sonBlocks = sons.elements();
        de.huberlin.informatik.pnk.appControl.base.D.d("Start flatten");
        sons = null;
        while (sonBlocks.hasMoreElements()) {
            BlockStructure b = (BlockStructure)sonBlocks.nextElement();
            b.flatten(initiator);
            // b contains no block
            b.joinInterfaceNodes(initiator);
            // register nodes of b
            Enumeration bPlaces = b.places.elements();
            while (bPlaces.hasMoreElements()) {
                Place p = (Place)bPlaces.nextElement();
                registerPlace(p);
            }
            Enumeration bTransitions = b.transitions.elements();
            while (bTransitions.hasMoreElements()) {
                Transition t = (Transition)bTransitions.nextElement();
                registerTransition(t);
            }
        }
        joinInterfaceNodes(initiator);
    }

    public String getId() {return id; }
    public Vector getInterfaceNodes() {
        return interfaceNodes;
    }

    public String getName() {return name; }
    public Net getNet() {
        return this.net;
    }

    public Vector getPlaces() {
        return places;
    }

    public Vector getSons() {
        return sons;
    }

    public Vector getTransitions() {
        return transitions;
    }

    public boolean isInterfaceNode(Node n) {
        return interfaceNodes.contains(n);
    }

    public boolean isSon(BlockStructure bl) {
        return sons.contains(bl);
    }

    /**
           Joins all interface nodes of this block
           with its joined interface nodes of other blocks.
           Requires: This block contains no block.
     */
    public void joinInterfaceNodes(Object initiator) {
        Enumeration bInterfaces = interfaceNodes.elements();
        de.huberlin.informatik.pnk.appControl.base.D.d("Start joinInterfaceNodes");
        while (bInterfaces.hasMoreElements()) {
            Node i = (Node)bInterfaces.nextElement();
            i.joinWithInterface(initiator);
        }
    }

    /**
     * If node <code>n</code> is a place or transition
     * of this block register as interface node and return true
     * else false.
     */
    public boolean registerInterface(Node n, Object initiator) {
        if (places.contains(n) || transitions.contains(n)) {
            interfaceNodes.addElement(n);
            return true;
        }
        return false;
    }

    /**
     * Registers place <code>pl</code> as place of this block.
     */
    public void registerPlace(Place pl) {
        places.addElement(pl);
    }

    public void registerSon(BlockStructure bl, Object initiator) {
        if (bl.father == null) {
            sons.addElement(bl);
            bl.father = this;
            Observer o = net.getObserver();
            if (o != null) {
                ActionObject b = new RegisterSonAction(initiator, bl);
                o.update(this, b);
            }
        } // else bl is son of an other block
    }

    /**
     * Registers transition <code>tr</code> as transition of this block.
     */
    public void registerTransition(Transition tr) {
        transitions.addElement(tr);
    }

    public void unregisterInterface(Node n, Object initiator) {
        interfaceNodes.removeElement(n);
    }

    /**
     * Removes  node <code>n</code> (place or transition) from this block.
     */
    public void unregisterNode(Node n) {
        try {
            Transition t = (Transition)n;
            unregisterTransition(t);
        } catch (ClassCastException e) {
            Place p = (Place)n;
            unregisterPlace(p);
        }
    }

    /**
     *  Removes place <code>pl</code> as place of this block.
     */
    public void unregisterPlace(Place pl) {
        places.removeElement(pl);
    }

    public void unregisterSon(BlockStructure bl, Object initiator) {
        if (bl.father == this) {
            sons.removeElement(bl);
            bl.father = null;
            Observer o = net.getObserver();
            if (o != null) {
                ActionObject b = new UnregisterSonAction(initiator, bl);
                o.update(this, b);
            }
        }
    }

    /**
     * Removes transition <code>tr</code> as transition of this block.
     */
    public void unregisterTransition(Transition tr) {
        transitions.removeElement(tr);
    }
} // public class BlockStructure
