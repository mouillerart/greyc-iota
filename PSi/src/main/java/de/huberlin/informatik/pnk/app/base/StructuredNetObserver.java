package de.huberlin.informatik.pnk.app.base;

import de.huberlin.informatik.pnk.kernel.*;

/**
 * All applications observing a structured net (tree of blocks),
 * have to implement the following interface
 * to get information about changes in the block structure.
   $Log: StructuredNetObserver.java,v $
   Revision 1.8  2001/10/11 16:56:33  oschmann
   Neue Release

   Revision 1.7  2001/06/04 15:35:18  efischer
 *** empty log message ***

   Revision 1.6  2001/05/11 17:20:50  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2000/12/14 00:42:12  oschmann
   Neue Version...

   Revision 1.4  2000/09/22 08:42:57  gruenewa
 *** empty log message ***

   Revision 1.3  2000/09/18 14:37:54  oschmann
   Zwischenversion

   Revision 1.2  2000/09/04 09:01:40  hohberg
   Update of joinInterfaceNode.

   Revision 1.1  2000/06/21 14:48:42  hohberg
 *** empty log message ***

 */
public interface StructuredNetObserver {
    /**
     * Flatten the block.
     * The "join" of nodes will be announced, besides,
     * but not the removal of the block structure (no removeSon)
     *
     * @param netobject       the block
     */
    public void flatten (Object netobject);
    /**
     * Join the given node with its interface nodes.
     *
     * @param netobject     the given node
     */
    public void joinInterfaceNode (Object netobject);
    /**
     * New block created
     *
     * @param netobject     the created block
     */
    public void newBlock (Object netobject);
    /**
     * Remove son from block
     *
     * @param netobject     the block
     * @param son           the new son
     */
    public void newSon (Object netobject, BlockStructure son);
    /**
     * Register node as an interface node of block
     *
     * @param netobject     the block
     * @param interfaceNode the new interface node
     */
    public void registerInterface (Object netobject, Node interfaceNode);
    /**
     * A block gets a new son
     *
     * @param netobject     the block
     * @param son           the son
     */
    public void removeSon (Object netobject, BlockStructure son);
    /**
     * Split the given interface node which points to the joined node
     *
     * @param netobject     the given node
     * @param interfaceNode the interface node
     */
    public void splitInterfaceNode (Object netobject, Node interfaceNode);
    /**
     * Turn InterfaceNode to an internal node of block
     *
     * @param netobject     the block
     * @param interfaceNode the interface node
     */
    public void unregisterInterface (Object netobject, Node interfaceNode);
} //interface StructuredNetObserver