package de.huberlin.informatik.pnk.kernel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Extension.java,v $
   Revision 1.17  2001/10/11 16:57:52  oschmann
   Neue Release

   Revision 1.15  2001/06/12 07:03:08  oschmann
   Neueste Variante...

   Revision 1.14  2001/06/04 15:25:15  efischer
 *** empty log message ***

   Revision 1.13  2001/05/11 17:21:47  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.12  2001/04/11 09:58:19  efischer
 *** empty log message ***

   Revision 1.11  2001/02/27 21:29:05  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.10  2001/02/15 12:40:13  hohberg
   Local parse and parse using context

   Revision 1.9  2001/02/04 17:45:33  juengel
 *** empty log message ***

   Revision 1.8  2001/02/03 12:33:15  juengel
 *** empty log message ***

   Revision 1.7  2001/01/16 17:36:50  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:53  juengel
   fast fertig

   Revision 1.8  2000/09/22 08:43:41  gruenewa
 *** empty log message ***

   Revision 1.7  2000/08/30 14:22:46  hohberg
   Update of comments

   Revision 1.6  2000/08/11 09:23:03  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:22  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:31:29  hohberg
   New methods save and load

 */


/**
 * Defines common attributes and behaviour of an <em> extension </em> of
 * an {@link Extendable extendable} that is
 * a {@link Graph graph} or a {@link Net Petri Net} or their {@link
 * Member members}.
 * The state of an attribute has an extern representation defined by
 * {@link #setExternState setExternState}.
 * Each extension class has a default state.
 */
public abstract class Extension extends Extendable {
    /*
     * Refers to the extendable object, the extension has been registered
     * with.
     */
    private Extendable extendable;
    /*
     * Contains boolean indicating if this extension is editable
     */
    private static boolean is_editable = true;

    /*
     * The given extern representation of this extension.
     */
    private String externState = " ";

    /**
     *  Constructor specifying the extendable.
     */
    protected Extension(Extendable extendable) {
        super(extendable.getGraph());
        setExtendable(extendable);
        externState = defaultToString();
        localParse();
    } // protected  Extension( Extendable extendable)

    /**
     *  Constructor specifying the extendable and the extern representation.
     */
    protected Extension(Extendable extendable, String state) {
        super(extendable.getGraph());
        setExtendable(extendable);
        externState = state;
        localParse();
    } // protected  Extension( Extendable extendable, String state)

    /**
     * Interprets the string representation of this extension
     * depending on other extensions and sets the state  accordingly.
     */
    public void checkContextAndParse() {}
    /**
     * Gives the extern representation of default state.
     * There are extensions without a default state
     * (for example {@link de.huberlin.informatik.pnk.netElementExtensions.base.FiringRule FiringRule}).
     */
    public String defaultToString() {
        return " ";
    } // public String defaultToString( )

    /**
     * Gives the extended extendable.
     */
    public Extendable getExtendable() {
        return extendable;
    } // public Extendable getExtendable( )

    /**
     * Gives the extended extendable.
     */
    public Net getNet() {
        return getExtendable().getNet();
    } // public Extendable getExtendable( )

    /**
     * Returns <code>true</code> if the extension is in its default state,
     * otherwise <code>false</code>.
     */
    final public boolean isDefault() {
        return externState.equals(defaultToString());
    } // public boolean isDefault( )

    /**
     * Returns <code>true</code> if the extension is in editable,
     * otherwise <code>false</code>. The pnk editior can be used
     * to set/edit the value of editable extensions.
     */
    public boolean isEditable() {
        // de.huberlin.informatik.pnk.appControl.base.D.d("Extension Editable: " + this.is_editable);
        boolean b = true;
        Class c = this.getClass();
        while (c != null) {
            try {
                b = (boolean)c.getDeclaredField("is_editable").getBoolean(null);
                return b;
            } catch (NoSuchFieldException e) {
                //de.huberlin.informatik.pnk.appControl.base.D.d("NoSuchFieldException: " + c.getName() + ".is_editable");
            } catch (IllegalAccessException e) {
                //de.huberlin.informatik.pnk.appControl.base.D.d("IllegalAccessException: " + c.getName() + ".is_editable");
            }
            c = c.getSuperclass();
        }
        return b;
    }     // public static boolean isEditable( )

    public static boolean isMutable() {
        return true;
    } // public static boolean isMutable( )

    /**
     * Returns <code>true</code> if the extension is in a valid state,
     * otherwise <code>false</code>.
     */
    protected abstract boolean isValid ();
    /**
     * Returns <code>true</code> if the extension is in a valid state with
     * respect to the extended extendable, otherwise
     * <code>false</code>. <br>

     */
    protected abstract boolean isValid (Extendable extendable);
    /**
     * Returns <code>true</code> if the string <code>state</code> represents
     * a valid state for this extension with respect to the
     * extendable of this extension,
     * otherwise <code>false</code>.
     */
    protected abstract boolean isValid (String state);
    public void load(org.w3c.dom.Node theNode) {
        org.w3c.dom.Node valueChild = ((Element)theNode).getElementsByTagName("value").item(0);
        if (valueChild != null) {
            org.w3c.dom.Node firstChild = valueChild.getFirstChild();
            if (firstChild != null)
                valueOf(firstChild.getNodeValue());
        }
        org.w3c.dom.Node graphicsChild = ((Element)theNode).getElementsByTagName("graphics").item(0);
        if (graphicsChild != null) {
            setDynamicExtension("graphics", "graphics", "graphics", graphicsChild);
        }
    }

    /**
     * Interprets the string representation of this extension
     * and sets the state  accordingly.
     */
    protected void localParse() {}
    public void save(Document doc, org.w3c.dom.Node extensionNode) {
        if (hasGraphicsInfo()) {
            org.w3c.dom.Node graphicsNode = getGraphicsInfo();
            extensionNode.appendChild(doc.importNode(graphicsNode, true));
        }
        if (externState != null) {
            org.w3c.dom.Node valueNode = extensionNode.appendChild(doc.createElement("value"));
            valueNode.appendChild(doc.createTextNode(externState));
            extensionNode.appendChild(valueNode);
        }
    }

/**
 * Sets the extendable of this extension to <code>extendable<code>.
 */
    final private void setExtendable(Extendable extendable) {
        this.extendable = extendable;
    } // private void setExtendable( Extendable extendable)

    /**
     * Sets the extern representation (not the state) to <code>state</code>.
     */
    final protected void setExternState(String state) {
        externState = state;
    }

    /**
     * Sets the extension to its default state. <br>
     */
    final public void toDefault() {
        if (isMutable()) {
            externState = defaultToString();
            localParse();
        }
    } // public void toDefault( )

    /**
     * Returns the extern representation of this extension.
     * <br>
     */
    final public String toString() {
        return externState;
    } // public  String toString( )

    /**
     * Sets the extern representation and the (internal) state to
     * <code>state</code>. <BR>
     */
    final public void valueOf(String state) {
        if (isMutable()) {
            externState = state;
            localParse();
        }
    } // public void valueOf( String state)
} // public abstract class Extension extends Observable