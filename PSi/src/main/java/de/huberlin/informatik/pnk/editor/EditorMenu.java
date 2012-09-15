package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import de.huberlin.informatik.pnk.appControl.ApplicationControl;
import de.huberlin.informatik.pnk.appControl.ApplicationControlMenu;

/**
 * EditorMenu.java
 *
 * This class sets the menu in applicationcontrol.
 * It specifies action on pages.
 *
 * Created: Fri Dec 29 09:42:33 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class EditorMenu
implements ItemListener, ActionListener {
    Editor editor;

    /*
     * Menu for creating places, transition, arcs,
     * and edit, select or delete them.
     */
    JMenu editormenu;
    /*
     * Menu for creating places, transition, arcs,
     * and edit, select or delete them.
     */
    JMenu[] menu;

    /*
     * Menu containing CheckboxItems for
     * for every editorpage. It tells if
     * a page is visible or hidden.
     */
    JMenu pagemenu;

    /*
     * Menu that lists Extendables and their
     * extensions.
     */
    JMenu extensionmenu;

    /*
     * This variable stores the mode of
     * the menu. It tells if places, transitions
     * or nodes should be created, when click in empty area.
     * Furthermore it says, if objects should
     * be edited, deleted, selected, when click in object.
     */
    int mode;

    /*
     * Things that should be created.
     */
    final static int NODE = 1;
    final static int PLACE = 2;
    final static int TRAN = 4;

    /*
     * Things that should be done
     * when click in object.
     */
    final static int DELETE = 8;
    final static int SELECT = 16;
    final static int EDIT = 32;
    final static int JOIN = 64;
    final static int SPLIT = 128;
    final static int ARC = 256;
    final static int EDGE = 512;

    final static int CREATE_MASK = NODE | PLACE | TRAN;
    final static int MODE_MASK = DELETE | SELECT | EDIT
                                 | JOIN | SPLIT | ARC | EDGE;

    /*
     * CheckBoxMenuItems in menu.
     */
    JCheckBoxMenuItem node_ckb = new JCheckBoxMenuItem("Node");
    JCheckBoxMenuItem place_ckb = new JCheckBoxMenuItem("Place");
    JCheckBoxMenuItem tran_ckb = new JCheckBoxMenuItem("Tran");
    JCheckBoxMenuItem placearc_ckb = new JCheckBoxMenuItem("PlaceArc");
    JCheckBoxMenuItem transitionarc_ckb = new JCheckBoxMenuItem("TranArc");
    JCheckBoxMenuItem edge_ckb = new JCheckBoxMenuItem("Edge");
    JCheckBoxMenuItem arc_ckb = new JCheckBoxMenuItem("Arc");
    JCheckBoxMenuItem edit_ckb = new JCheckBoxMenuItem("Edit");
    JCheckBoxMenuItem select_ckb = new JCheckBoxMenuItem("Select");
    JCheckBoxMenuItem delete_ckb = new JCheckBoxMenuItem("Delete");
    JCheckBoxMenuItem join_ckb = new JCheckBoxMenuItem("Join");
    JCheckBoxMenuItem split_ckb = new JCheckBoxMenuItem("Split");

    Hashtable arcExtensionVis = new Hashtable();
    Hashtable edgeExtensionVis = new Hashtable();
    Hashtable nodeExtensionVis = new Hashtable();
    Hashtable placeExtensionVis = new Hashtable();
    Hashtable placeArcExtensionVis = new Hashtable();
    Hashtable transitionArcExtensionVis = new Hashtable();
    Hashtable transitionExtensionVis = new Hashtable();

    protected EditorMenu(Editor editor) {
        // set default mode
        int new_mode = PLACE | ARC;
        this.editor = editor;
        this.setMenu();
        this.setMode(new_mode);
    }

    /*
     * If "new Page"-Button pressed, create a new page.
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("New Page")) {
            this.editor.getPagevector().createPage();
        }
    }

    private void disableItemListeners() {
        //remove itemlisteners so that items doesnt sends events
        if (this.arc_ckb != null)
            this.arc_ckb.removeItemListener(this);
        if (this.edge_ckb != null)
            this.edge_ckb.removeItemListener(this);
        if (this.placearc_ckb != null)
            this.placearc_ckb.removeItemListener(this);
        if (this.transitionarc_ckb != null)
            this.transitionarc_ckb.removeItemListener(this);
        if (this.node_ckb != null)
            this.node_ckb.removeItemListener(this);
        if (this.place_ckb != null)
            this.place_ckb.removeItemListener(this);
        if (this.tran_ckb != null)
            this.tran_ckb.removeItemListener(this);

        this.edit_ckb.removeItemListener(this);
        this.select_ckb.removeItemListener(this);
        this.delete_ckb.removeItemListener(this);
        this.join_ckb.removeItemListener(this);
        this.split_ckb.removeItemListener(this);
    }

    /*
     * Adds an itemlistener to the checkboxmenuitems
     * in this editormenu
     */
    private void enableItemListeners() {
        //add itemlistener to checkboxmenuitem
        if (this.edge_ckb != null)
            this.edge_ckb.addItemListener(this);
        if (this.placearc_ckb != null)
            this.placearc_ckb.addItemListener(this);
        if (this.transitionarc_ckb != null)
            this.transitionarc_ckb.addItemListener(this);
        if (this.node_ckb != null)
            this.node_ckb.addItemListener(this);
        if (this.arc_ckb != null)
            this.arc_ckb.addItemListener(this);
        if (this.place_ckb != null)
            this.place_ckb.addItemListener(this);
        if (this.tran_ckb != null)
            this.tran_ckb.addItemListener(this);

        this.edit_ckb.addItemListener(this);
        this.select_ckb.addItemListener(this);
        this.delete_ckb.addItemListener(this);
        this.join_ckb.addItemListener(this);
        this.split_ckb.addItemListener(this);
    }

    /*
     * Sets the editormenu in ApplicationControl.
     */
    protected JMenu[] getMenu() {
        return this.menu;
    }

    /*
     * Tells if extension should be visible or hidden.
     */
    protected boolean isExtensionVisible(Sprite parent, String extId) {
        // Achtung! Reihenfolge der Vergleiche ist wichtig
        // da ein Arc auch ein Edge und ein Place auch ein Node ist u.s.w.
        if (parent instanceof Arc) {
            // Achtung es kann sich hier um ein Arc oder PlaceArc oder TranstionArc handeln
            Boolean bool = null;
            MemberSpriteNode source = ((Arc)parent).source;
            MemberSpriteNode target = ((Arc)parent).target;
            if (source instanceof Place && target instanceof Place)
                bool = (Boolean) this.placeArcExtensionVis.get(extId);
            else if (source instanceof Transition && target instanceof Transition)
                bool = (Boolean) this.transitionExtensionVis.get(extId);
            else
                bool = (Boolean) this.arcExtensionVis.get(extId);
            return bool.booleanValue();
        } else if (parent instanceof PlaceArc) {
            Boolean bool = (Boolean) this.placeArcExtensionVis.get(extId);
            return bool.booleanValue();
        } else if (parent instanceof TransitionArc) {
            Boolean bool = (Boolean) this.transitionArcExtensionVis.get(extId);
            return bool.booleanValue();
        } else if (parent instanceof Edge) {
            Boolean bool = (Boolean) this.edgeExtensionVis.get(extId);
            return bool.booleanValue();
        } else if (parent instanceof Place) {
            Boolean bool = (Boolean) this.placeExtensionVis.get(extId);
            return bool.booleanValue();
        } else if (parent instanceof Transition) {
            Boolean bool = (Boolean) this.transitionExtensionVis.get(extId);
            return bool.booleanValue();
        } else if (parent instanceof Node) {
            Boolean bool = (Boolean) this.nodeExtensionVis.get(extId);
            return bool.booleanValue();
        } else return false;
    }

    /*
     * A page can request, if a mode is set
     * @param mask of requested mode
     * @return true if mode is set
     */
    boolean isInMode(int mask) {
        return (mode & mask) == mask;
    }

    /*
     * Set the menu-mode of this editor,
     * to create Place or Transition,
     * Edit, Select or Delete objects.
     */
    public void itemStateChanged(ItemEvent ie) {
        String label = ((JCheckBoxMenuItem)ie.getItemSelectable()).getText();

        if (label.equals("Place")) this.setMode(EditorMenu.PLACE);
        if (label.equals("Node")) this.setMode(EditorMenu.NODE);
        if (label.equals("Tran")) this.setMode(EditorMenu.TRAN);
        if (label.equals("Select")) this.setMode(EditorMenu.SELECT);
        if (label.equals("Edit")) this.setMode(EditorMenu.EDIT);
        if (label.equals("Delete")) this.setMode(EditorMenu.DELETE);
        if (label.equals("Arc")) this.setMode(EditorMenu.ARC);
        if (label.equals("PlaceArc")) this.setMode(EditorMenu.ARC);
        if (label.equals("TranArc")) this.setMode(EditorMenu.ARC);
        if (label.equals("Edge")) this.setMode(EditorMenu.EDGE);
        if (label.equals("Join")) this.setMode(EditorMenu.JOIN);
        if (label.equals("Split")) this.setMode(EditorMenu.SPLIT);
    }

    void setExtensionsVisible(String  className,
                              String  extId,
                              boolean visible) {
        Enumeration e =
            editor.getReferencetable().keys();

        while (e.hasMoreElements()) {
            Object netobject = e.nextElement();
            if (netobject.getClass().getName().equals(className))
                setExtensionVisible(netobject, extId, visible);
        }

        Boolean b = new Boolean(visible);

        if (className.equals("de.huberlin.informatik.pnk.kernel.Arc"))
            arcExtensionVis.put(extId, b);

        if (className.equals("de.huberlin.informatik.pnk.kernel.PlaceArc"))
            placeArcExtensionVis.put(extId, b);

        if (className.equals("de.huberlin.informatik.pnk.kernel.TransitionArc"))
            transitionArcExtensionVis.put(extId, b);

        if (className.equals("de.huberlin.informatik.pnk.kernel.Edge"))
            edgeExtensionVis.put(extId, b);

        if (className.equals("de.huberlin.informatik.pnk.kernel.Node"))
            nodeExtensionVis.put(extId, b);

        if (className.equals("de.huberlin.informatik.pnk.kernel.Place"))
            placeExtensionVis.put(extId, b);

        if (className.equals("de.huberlin.informatik.pnk.kernel.Transition"))
            transitionExtensionVis.put(extId, b);
    }

    void setExtensionVisible(Object  netobject,
                             String  extId,
                             boolean visible) {
        ReferenceTable.RTInfo rInfo =
            (ReferenceTable.RTInfo)
            editor.getReferencetable().get(netobject);

        if (rInfo != null) {
            Vector spList =
                rInfo.spritelist;

            MemberSprite mSprite;
            ReferenceTable.SpritePageTupel spTupel;

            for (int i = 0; i < spList.size(); i++) {
                spTupel =
                    (ReferenceTable.SpritePageTupel)spList.get(i);
                mSprite =
                    (MemberSprite)spTupel.sprite;

                for (int j = 0; j < mSprite.subsprites.size(); j++) {
                    Sprite s =
                        (Sprite)mSprite.subsprites.get(j);
                    if (s instanceof Extension) {
                        if (((Extension)s).getId().equals(extId))
                            s.setVisible(visible);
                    } // s instanceof Extension
                } // for j in mSprite.subsprites
            } // for i in spList
        } // rInfo != null

        // Alle pages neu zeichnen.
        editor.getPagevector().repaint();
    } // setExtensionVisible()

    void setExtensionLabelsVisible(String  extId,
                                   String  extClass,
                                   boolean visible) {
        setExtensionsVisible(extClass,
                             extId,
                             visible);
    }

    private void setExtensionLabelsVisible2(String extId, String extClass, boolean visible) {
        Editor.msg("##### set extension visible: " + extId + " " + extClass + " " + visible);
        Vector pagevector = this.editor.getPagevector();
        for (int i = 0; i < pagevector.size(); i++) {
            Page page = (Page)pagevector.get(i);
            Vector spritevector = page.getSpritevector();
            for (int j = 0; j < spritevector.size(); j++) {
                Sprite sprite = (Sprite)spritevector.get(j);
                if (sprite instanceof Extension) {
                    Extension ext = (Extension)sprite;
                    if (ext.getId() == extId) {
                        Sprite parent = ext.getParent();
                        if ((parent instanceof Arc && extClass.equals("Arc")) ||
                            (parent instanceof Edge && extClass.equals("Edge")) ||
                            (parent instanceof Edge && extClass.equals("PlaceArc")) ||
                            (parent instanceof Edge && extClass.equals("TransitionArc")) ||
                            (parent instanceof Node && extClass.equals("Node")) ||
                            (parent instanceof Place && extClass.equals("Place")) ||
                            (parent instanceof Transition && extClass.equals("Transition"))) {
                            ext.setVisible(visible);
                            Rectangle r = ext.getBounds();
                            r.translate(page.translation.x, page.translation.y);
                            page.repaint(r);
                        }
                    }
                }
            }
        }
    }

    /*
     * Sets the editormenu in ApplicationControl.
     */
    void setMenu() {
        this.editormenu = new JMenu("Editor");
        this.pagemenu = new JMenu("Pages");

        // set the pagemenu
        JMenuItem mi;
        mi = new JMenuItem("New Page");
        this.pagemenu.add(mi);
        mi.addActionListener(this);
        this.pagemenu.addSeparator();

        // create a menu to show/hide extensions of Extendables
        this.extensionmenu = new JMenu("Extensions");
        //        this.editormenu.add(extensionmenu);

        this.menu = new JMenu[] {this.editormenu, this.extensionmenu, this.pagemenu};

        //this.menu = new JMenu[] {this.editormenu, this.extensionmenu, this.pagemenu};
        //ApplicationControl ac = this.editor.getApplicationControl();
        //ac.setMenu(this.editor, test);
    }

    /*
     * Pages can set mode in editor.
     * @param mask the new mode
     */
    void setMode(int mask) {
        if ((mask & CREATE_MASK) != 0)
            mode = ((~CREATE_MASK) & mode) | mask;
        if ((mask & MODE_MASK) != 0)
            mode = ((~MODE_MASK) & mode) | mask;
        //update the new state in editormenu
        this.update();
    }

    protected void setNet() {
        Enumeration enm;
        JMenu mi;
        JMenuItem jmi;
        JCheckBoxMenuItem ckb;

        // now the reference of the net is given
        // to the editor so we can set menu

        Editor ed = this.editor;
        GraphProxy graph = ed.getGraphproxy();
        Hashtable specification = graph.getSpecificationTable();

        /*
           JToolBar toolbar = ed.getEditorwindow().getToolbar();
           toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
           toolbar.setFloatable(false);
         */

        this.disableItemListeners();

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Node")) {
            editormenu.add(this.node_ckb);
            //  toolbar.add(this.node_ckb);
            //toolbar.add(Box.createVerticalGlue());
            this.node_ckb.setState(this.isInMode(EditorMenu.NODE));
        }

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Place")) {
            //Icon icon = new ImageIcon("icons/place1.gif");
            //this.place_ckb.setIcon(icon);

            editormenu.add(this.place_ckb);
            //toolbar.add(this.place_ckb);
            //toolbar.add(Box.createVerticalGlue());
            this.place_ckb.setState(this.isInMode(EditorMenu.PLACE));
        }

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Transition")) {
            //Icon icon = new ImageIcon("icons/tran1.gif");
            //this.tran_ckb.setIcon(icon);

            editormenu.add(this.tran_ckb);
            //toolbar.add(this.tran_ckb);
            //toolbar.add(Box.createVerticalGlue());
            this.tran_ckb.setState(this.isInMode(EditorMenu.TRAN));
        }

        editormenu.addSeparator();

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Arc")) {
            editormenu.add(this.arc_ckb);
            //toolbar.add(this.arc_ckb);
            //toolbar.add(Box.createVerticalGlue());
            this.arc_ckb.setState(this.isInMode(EditorMenu.ARC));
        }

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.PlaceArc")) {
            editormenu.add(this.placearc_ckb);
            //toolbar.add(this.placearc_ckb);
            //toolbar.add(Box.createVerticalGlue());
            this.placearc_ckb.setState(this.isInMode(EditorMenu.ARC));
        }

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.TransitionArc")) {
            editormenu.add(this.transitionarc_ckb);
            //toolbar.add(this.transitionarc_ckb);
            //toolbar.add(Box.createVerticalGlue());
            this.transitionarc_ckb.setState(this.isInMode(EditorMenu.ARC));
        }

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Edge")) {
            editormenu.add(this.edge_ckb);
            //toolbar.add(this.edge_ckb);
            //toolbar.add(Box.createVerticalGlue());
            this.edge_ckb.setState(this.isInMode(EditorMenu.EDGE));
        }

        editormenu.add(this.edit_ckb);
        //toolbar.add(this.edit_ckb);
        //toolbar.add(Box.createVerticalGlue());
        this.edit_ckb.setState(this.isInMode(EditorMenu.EDIT));

        editormenu.add(this.delete_ckb);
        //toolbar.add(this.delete_ckb);
        //toolbar.add(Box.createVerticalGlue());
        this.delete_ckb.setState(this.isInMode(EditorMenu.DELETE));

        /*
           editormenu.add(this.select_ckb);
           toolbar.add(this.select_ckb);
           toolbar.add(Box.createVerticalGlue());
           this.select_ckb.setState(this.isInMode(EditorMenu.SELECT));
         */

        editormenu.add(this.join_ckb);
        // toolbar.add(this.join_ckb);
        //toolbar.add(Box.createVerticalGlue());
        this.join_ckb.setState(this.isInMode(EditorMenu.JOIN));

        editormenu.add(this.split_ckb);
        //toolbar.add(this.split_ckb);
        //toolbar.add(Box.createVerticalGlue());
        this.split_ckb.setState(this.isInMode(EditorMenu.SPLIT));

        this.enableItemListeners();

        this.extensionmenu.removeAll();

        //menupunkt zum editieren von Netznamen und Netzextensions
        jmi = new JMenuItem("Set Net Extensions");
        jmi.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent ev) {
                                      MemberSprite netsprite = new MemberSprite();
                                      Object net = editor.getGraphproxy().graph;
                                      netsprite.setNetobject(net);
                                      new EditDialog(editor, netsprite);
                                  }
                              });

        this.extensionmenu.add(jmi);
        this.extensionmenu.addSeparator();

        enm = graph.getExtensionIds("de.huberlin.informatik.pnk.kernel.Edge");
        if (enm != null) {
            mi = new JMenu("Edge");
            this.extensionmenu.add(mi);
            while (enm.hasMoreElements()) {
                String extId = (String) enm.nextElement();
                ckb = new JCheckBoxMenuItem(extId);
                mi.add(ckb);
                edgeExtensionVis.put(extId, new Boolean(true));
                ckb.setState(true);
                ckb.addItemListener(new ItemListener() {
                                        public void itemStateChanged(ItemEvent ie) {
                                            JCheckBoxMenuItem ckbox = (JCheckBoxMenuItem)ie.getItemSelectable();
                                            String extIdent = ckbox.getText();
                                            String extClass = "de.huberlin.informatik.pnk.kernel.Edge";
                                            boolean visible = ckbox.getState();
                                            setExtensionLabelsVisible(extIdent, extClass, visible);
                                            //edgeExtensionVis.put(extIdent, new Boolean(visible));
                                        }
                                    });
            }
        }
        enm = graph.getExtensionIds("de.huberlin.informatik.pnk.kernel.PlaceArc");
        if (enm != null) {
            mi = new JMenu("PlaceArc");
            this.extensionmenu.add(mi);
            while (enm.hasMoreElements()) {
                String extId = (String) enm.nextElement();
                ckb = new JCheckBoxMenuItem(extId);
                mi.add(ckb);
                placeArcExtensionVis.put(extId, new Boolean(true));
                ckb.setState(true);
                ckb.addItemListener(new ItemListener() {
                                        public void itemStateChanged(ItemEvent ie) {
                                            JCheckBoxMenuItem ckbox = (JCheckBoxMenuItem)ie.getItemSelectable();
                                            String extIdent = ckbox.getText();
                                            String extClass = "de.huberlin.informatik.pnk.kernel.PlaceArc";
                                            boolean visible = ckbox.getState();
                                            setExtensionLabelsVisible(extIdent, extClass, visible);
                                            //              placeArcExtensionVis.put(extIdent, new Boolean(visible));
                                        }
                                    });
            }
        }

        enm = graph.getExtensionIds("de.huberlin.informatik.pnk.kernel.TransitionArc");
        if (enm != null) {
            mi = new JMenu("TransitionArc");
            this.extensionmenu.add(mi);
            while (enm.hasMoreElements()) {
                String extId = (String) enm.nextElement();
                ckb = new JCheckBoxMenuItem(extId);
                mi.add(ckb);
                transitionArcExtensionVis.put(extId, new Boolean(true));
                ckb.setState(true);
                ckb.addItemListener(new ItemListener() {
                                        public void itemStateChanged(ItemEvent ie) {
                                            JCheckBoxMenuItem ckbox = (JCheckBoxMenuItem)ie.getItemSelectable();
                                            String extIdent = ckbox.getText();
                                            String extClass = "de.huberlin.informatik.pnk.kernel.TransitionArc";
                                            boolean visible = ckbox.getState();
                                            setExtensionLabelsVisible(extIdent, extClass, visible);
                                            //              transitionArcExtensionVis.put(extIdent, new Boolean(visible));
                                        }
                                    });
            }
        }

        enm = graph.getExtensionIds("de.huberlin.informatik.pnk.kernel.Arc");
        if (enm != null) {
            mi = new JMenu("Arc");
            this.extensionmenu.add(mi);
            while (enm.hasMoreElements()) {
                String extId = (String) enm.nextElement();
                ckb = new JCheckBoxMenuItem(extId);
                mi.add(ckb);
                ckb.setState(true);
                arcExtensionVis.put(extId, new Boolean(true));
                ckb.addItemListener(new ItemListener() {
                                        public void itemStateChanged(ItemEvent ie) {
                                            JCheckBoxMenuItem ckbox = (JCheckBoxMenuItem)ie.getItemSelectable();
                                            String extIdent = ckbox.getText();
                                            String extClass = "de.huberlin.informatik.pnk.kernel.Arc";
                                            boolean visible = ckbox.getState();
                                            setExtensionLabelsVisible(extIdent, extClass, visible);
                                            //              arcExtensionVis.put(extIdent, new Boolean(visible));
                                        }
                                    });
            }
        }

        enm = graph.getExtensionIds("de.huberlin.informatik.pnk.kernel.Node");
        if (enm != null) {
            mi = new JMenu("Node");
            this.extensionmenu.add(mi);
            while (enm.hasMoreElements()) {
                String extId = (String) enm.nextElement();
                ckb = new JCheckBoxMenuItem(extId);
                mi.add(ckb);
                ckb.setState(true);
                nodeExtensionVis.put(extId, new Boolean(true));
                ckb.addItemListener(new ItemListener() {
                                        public void itemStateChanged(ItemEvent ie) {
                                            JCheckBoxMenuItem ckbox = (JCheckBoxMenuItem)ie.getItemSelectable();
                                            String extIdent = ckbox.getText();
                                            String extClass = "de.huberlin.informatik.pnk.kernel.Node";
                                            boolean visible = ckbox.getState();
                                            setExtensionLabelsVisible(extIdent, extClass, visible);
                                            //              nodeExtensionVis.put(extIdent, new Boolean(visible));
                                        }
                                    });
            }
        }

        enm = graph.getExtensionIds("de.huberlin.informatik.pnk.kernel.Place");
        if (enm != null) {
            mi = new JMenu("Place");
            this.extensionmenu.add(mi);
            while (enm.hasMoreElements()) {
                String extId = (String) enm.nextElement();
                ckb = new JCheckBoxMenuItem(extId);
                mi.add(ckb);
                ckb.setState(true);
                placeExtensionVis.put(extId, new Boolean(true));
                ckb.addItemListener(new ItemListener() {
                                        public void itemStateChanged(ItemEvent ie) {
                                            JCheckBoxMenuItem ckbox = (JCheckBoxMenuItem)ie.getItemSelectable();
                                            String extIdent = ckbox.getText();
                                            String extClass = "de.huberlin.informatik.pnk.kernel.Place";
                                            boolean visible = ckbox.getState();
                                            setExtensionLabelsVisible(extIdent, extClass, visible);
                                            //              placeExtensionVis.put(extIdent, new Boolean(visible));
                                        }
                                    });
            }
        }

        enm = graph.getExtensionIds("de.huberlin.informatik.pnk.kernel.Transition");
        if (enm != null) {
            mi = new JMenu("Transition");
            this.extensionmenu.add(mi);
            while (enm.hasMoreElements()) {
                String extId = (String) enm.nextElement();
                ckb = new JCheckBoxMenuItem(extId);
                mi.add(ckb);
                ckb.setState(true);
                transitionExtensionVis.put(extId, new Boolean(true));
                ckb.addItemListener(new ItemListener() {
                                        public void itemStateChanged(ItemEvent ie) {
                                            JCheckBoxMenuItem ckbox = (JCheckBoxMenuItem)ie.getItemSelectable();
                                            String extIdent = ckbox.getText();
                                            String extClass = "de.huberlin.informatik.pnk.kernel.Transition";
                                            boolean visible = ckbox.getState();
                                            setExtensionLabelsVisible(extIdent, extClass, visible);
                                            //              transitionExtensionVis.put(extIdent, new Boolean(visible));
                                        }
                                    });
            }
        }

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Edge")) setMode(EDGE);
        else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Arc")) setMode(ARC);
        else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.PlaceArc")) setMode(ARC);
        else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.TransitionArc")) setMode(ARC);
        else Editor.msg("No Edge Type specified!!!");

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Node")) setMode(NODE);
        else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Place")) setMode(PLACE);
        else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Transition")) setMode(TRAN);
        else Editor.msg("No Node Type specified1!!!");
    }

    /*
     * State has changed.
     * Update CheckboxMenuItems of editormenu.
     */
    protected void update() {
        // string that will be displayed in editors pages
        String pageMsg = null;

        if (this.editor.joinSprite != null) {
            if (this.isInMode(JOIN))
                pageMsg = "First object for join selected ...";
            else this.editor.join(null);
        } else if (this.editor.selectOneObject) {
            pageMsg = "Please, select an object ...";
        } else {
            this.disableItemListeners();

            //set the state in the checkboxitems
            if (this.arc_ckb != null)
                this.arc_ckb.setState(this.isInMode(ARC));
            if (this.edge_ckb != null)
                this.edge_ckb.setState(this.isInMode(ARC));
            if (this.placearc_ckb != null)
                this.placearc_ckb.setState(this.isInMode(ARC));
            if (this.transitionarc_ckb != null)
                this.transitionarc_ckb.setState(this.isInMode(ARC));
            if (this.place_ckb != null)
                this.place_ckb.setState(this.isInMode(PLACE));
            if (this.tran_ckb != null)
                this.tran_ckb.setState(this.isInMode(TRAN));
            if (this.node_ckb != null)
                this.node_ckb.setState(this.isInMode(NODE));

            this.edit_ckb.setState(this.isInMode(EDIT));
            this.select_ckb.setState(this.isInMode(SELECT));
            this.delete_ckb.setState(this.isInMode(DELETE));
            this.join_ckb.setState(this.isInMode(JOIN));
            this.split_ckb.setState(this.isInMode(SPLIT));

            this.enableItemListeners();
        }

        // update displayed text about mode in all pages
        Vector pages = this.editor.getPagevector();
        if (pages != null) {
            for (int i = 0; i < pages.size(); i++) {
                Page page = (Page)pages.get(i);
                page.displayText(pageMsg);
            }
        }
    }
} // EditorMenu
