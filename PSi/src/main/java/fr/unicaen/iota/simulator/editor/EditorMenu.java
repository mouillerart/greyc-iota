/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.editor;

import fr.unicaen.iota.simulator.util.Config;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * EditorMenu
 *
 * This class sets the menu in applicationcontrol.
 * It specifies action on pages.
 *
 * Created after de.huberlin.informatik.pnk.editor.EditorMenu
 */
class EditorMenu implements ItemListener, ActionListener {

    private Editor editor;
    /**
     * Menu for creating places, transition, arcs, and edit, select or delete them.
     */
    private JMenu editormenu;
    /**
     * Menu for creating places, transition, arcs, and edit, select or delete them.
     */
    private JMenu[] menu;
    /**
     * Menu containing CheckboxItems for for every editorpage. It tells if
     * a page is visible or hidden.
     */
    JMenu pagemenu;
    /**
     * Menu that lists Extendables and their extensions.
     */
    private JMenu extensionmenu;
    /**
     * Menu that lists view functions
     */
    private JMenu viewmenu;
    /**
     * This variable stores the mode of the menu. It tells if places, transitions
     * or nodes should be created, when click in empty area.
     * Furthermore it says, if objects should be edited, deleted, selected, when click in object.
     */
    private int mode;
    /**
     * Things that should be created.
     */
    final static int NODE = 1;
    final static int PLACE = 2;
    final static int TRAN = 4;
    /**
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
    final static int MODE_MASK = DELETE | SELECT | EDIT | JOIN | SPLIT | ARC | EDGE;
    private final static Map<String, Integer> MAP_MODE = new HashMap<String, Integer>();

    static {
        MAP_MODE.put("Place", PLACE);
        MAP_MODE.put("Node", NODE);
        MAP_MODE.put("Tran", TRAN);
        MAP_MODE.put("Select", SELECT);
        MAP_MODE.put("Edit", EDIT);
        MAP_MODE.put("Delete", DELETE);
        MAP_MODE.put("Arc", ARC);
        MAP_MODE.put("PlaceArc", ARC);
        MAP_MODE.put("TranArc", ARC);
        MAP_MODE.put("Edge", EDGE);
        MAP_MODE.put("Join", JOIN);
        MAP_MODE.put("Split", SPLIT);
    }
    /**
     * CheckBoxMenuItems in menu.
     */
    private JCheckBoxMenuItem node_ckb = new JCheckBoxMenuItem("Node");
    private JCheckBoxMenuItem place_ckb = new JCheckBoxMenuItem("Place");
    private JCheckBoxMenuItem tran_ckb = new JCheckBoxMenuItem("Tran");
    private JCheckBoxMenuItem placearc_ckb = new JCheckBoxMenuItem("PlaceArc");
    private JCheckBoxMenuItem transitionarc_ckb = new JCheckBoxMenuItem("TranArc");
    private JCheckBoxMenuItem edge_ckb = new JCheckBoxMenuItem("Edge");
    private JCheckBoxMenuItem arc_ckb = new JCheckBoxMenuItem("Arc");
    private JCheckBoxMenuItem edit_ckb = new JCheckBoxMenuItem("Edit");
    private JCheckBoxMenuItem select_ckb = new JCheckBoxMenuItem("Select");
    private JCheckBoxMenuItem delete_ckb = new JCheckBoxMenuItem("Delete");
    private JCheckBoxMenuItem join_ckb = new JCheckBoxMenuItem("Join");
    private JCheckBoxMenuItem split_ckb = new JCheckBoxMenuItem("Split");
    private Map<String, Map<String, Boolean>> extensionVis;

    protected EditorMenu(Editor editor) {
        // set default mode
        int new_mode = PLACE | ARC;
        this.editor = editor;
        this.setMenu();
        this.setMode(new_mode);

        this.extensionVis = new HashMap<String, Map<String, Boolean>>();
        this.extensionVis.put("Arc", new HashMap<String, Boolean>());
        this.extensionVis.put("Edge", new HashMap<String, Boolean>());
        this.extensionVis.put("Node", new HashMap<String, Boolean>());
        this.extensionVis.put("Place", new HashMap<String, Boolean>());
        this.extensionVis.put("PlaceArc", new HashMap<String, Boolean>());
        this.extensionVis.put("Transition", new HashMap<String, Boolean>());
        this.extensionVis.put("TransitionArc", new HashMap<String, Boolean>());
    }

    /**
     * If "new Page"-Button pressed, create a new page.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("New Page".equals(cmd)) {
            Page p = this.editor.getPagevector().createPage();
            p.open();
        }
        if (e.getSource() == attachMenu) {
            Config.attachPoint = !Config.attachPoint;
            attachMenu.setText(Config.attachPoint ? "Hide attach points" : "Show attach points");
            for (Object o : editor.getPagevector().toArray()) {
                ((Page) o).repaint();
            }
        } else if (e.getSource() == gridMenu) {
            Config.grid = !Config.grid;
            gridMenu.setText(Config.attachPoint ? "Hide grid" : "Show grid");
            for (Object o : editor.getPagevector().toArray()) {
                ((Page) o).repaint();
            }
        } else if (e.getSource() == markingActionAlign) {
            for (Page p : editor.getPagevector()) {
                for (Sprite sprite : p.getSpritevector()) {
                    for (Sprite o3 : sprite.subsprites) {
                        if (o3 instanceof Extension) {
                            Extension ext = (Extension) o3;
                            if (ext.getId().equals("marking")) {
                                Dimension size = ext.getSize();
                                Point translate = new Point((int) (-size.getWidth() + sprite.getBounds().getWidth()) / 2, (int) (-size.getHeight() + sprite.getBounds().getHeight()) / 2);
                                Point spritePosition = sprite.getBounds().getLocation();
                                ext.setPosition(new Point((int) (spritePosition.getX() + translate.getX()), (int) (spritePosition.getY() + translate.getY())));
                            }
                        }
                    }
                }
            }
        }
    }

    private void disableItemListeners() {
        //remove itemlisteners so that items doesnt sends events
        if (this.arc_ckb != null) {
            this.arc_ckb.removeItemListener(this);
        }
        if (this.edge_ckb != null) {
            this.edge_ckb.removeItemListener(this);
        }
        if (this.placearc_ckb != null) {
            this.placearc_ckb.removeItemListener(this);
        }
        if (this.transitionarc_ckb != null) {
            this.transitionarc_ckb.removeItemListener(this);
        }
        if (this.node_ckb != null) {
            this.node_ckb.removeItemListener(this);
        }
        if (this.place_ckb != null) {
            this.place_ckb.removeItemListener(this);
        }
        if (this.tran_ckb != null) {
            this.tran_ckb.removeItemListener(this);
        }
        this.edit_ckb.removeItemListener(this);
        this.select_ckb.removeItemListener(this);
        this.delete_ckb.removeItemListener(this);
        this.join_ckb.removeItemListener(this);
        this.split_ckb.removeItemListener(this);
    }

    /**
     * Adds an itemlistener to the checkboxmenuitems
     * in this editormenu
     */
    private void enableItemListeners() {
        //add itemlistener to checkboxmenuitem
        if (this.edge_ckb != null) {
            this.edge_ckb.addItemListener(this);
        }
        if (this.placearc_ckb != null) {
            this.placearc_ckb.addItemListener(this);
        }
        if (this.transitionarc_ckb != null) {
            this.transitionarc_ckb.addItemListener(this);
        }
        if (this.node_ckb != null) {
            this.node_ckb.addItemListener(this);
        }
        if (this.arc_ckb != null) {
            this.arc_ckb.addItemListener(this);
        }
        if (this.place_ckb != null) {
            this.place_ckb.addItemListener(this);
        }
        if (this.tran_ckb != null) {
            this.tran_ckb.addItemListener(this);
        }
        this.edit_ckb.addItemListener(this);
        this.select_ckb.addItemListener(this);
        this.delete_ckb.addItemListener(this);
        this.join_ckb.addItemListener(this);
        this.split_ckb.addItemListener(this);
    }

    /**
     * Sets the editormenu in ApplicationControl.
     */
    protected JMenu[] getMenu() {
        return this.menu;
    }

    /**
     * Tells if extension should be visible or hidden.
     */
    protected boolean isExtensionVisible(Sprite parent, String extId) {
        // Achtung! Reihenfolge der Vergleiche ist wichtig
        // da ein Arc auch ein Edge und ein Place auch ein Node ist u.s.w.
        if (parent instanceof Arc) {
            // Achtung es kann sich hier um ein Arc oder PlaceArc oder TranstionArc handeln
            MemberSpriteNode source = ((Arc) parent).getSource();
            MemberSpriteNode target = ((Arc) parent).getTarget();
            if (source instanceof Place && target instanceof Place) {
                return this.extensionVis.get("PlaceArc").get(extId);
            } else if (source instanceof Transition && target instanceof Transition) {
                return this.extensionVis.get("Transition").get(extId);
            } else {
                return this.extensionVis.get("Arc").get(extId);
            }
        } else { // PlaceArc, TransitionArc, Edge, Place, Transition, Node
            return this.extensionVis.get(parent.getClass().getName()).get(extId);
        }
    }

    /**
     * A page can request, if a mode is set
     * @param mask of requested mode
     * @return true if mode is set
     */
    boolean isInMode(int mask) {
        return (mode & mask) == mask;
    }

    /**
     * Set the menu-mode of this editor, to create Place or Transition,
     * Edit, Select or Delete objects.
     */
    @Override
    public void itemStateChanged(ItemEvent ie) {
        String label = ((JCheckBoxMenuItem) ie.getItemSelectable()).getText();
        this.setMode(label);
    }

    public void setMode(String label) {
        this.setMode(EditorMenu.MAP_MODE.get(label));
    }

    void setExtensionsVisible(String klass, String extId, boolean visible) {
        for (Object netobject : editor.getReferencetable().keySet()) {
            if (netobject.getClass().getName().equals(klass)) {
                setExtensionVisible(netobject, extId, visible);
            }
        }
        extensionVis.get(klass).put(extId, visible);
    }

    void setExtensionVisible(Object netobject, String extId, boolean visible) {
        ReferenceTable.RTInfo rInfo = editor.getReferencetable().get(netobject);

        if (rInfo != null) {
            MemberSprite mSprite;
            for (ReferenceTable.SpritePageTuple spTupel : rInfo.spritelist) {
                mSprite = (MemberSprite) spTupel.sprite;
                for (Sprite s : mSprite.subsprites) {
                    if (s instanceof Extension) {
                        if (((Extension) s).getId().equals(extId)) {
                            s.setVisible(visible);
                        }
                    }
                }
            }
        }
        // Alle pages neu zeichnen.
        editor.getPagevector().repaint();
    } // setExtensionVisible()

    void setExtensionLabelsVisible(String extId, String extClass, boolean visible) {
        setExtensionsVisible(extClass, extId, visible);
    }
    /**
     * Sets the editormenu in ApplicationControl.
     */
    private JMenuItem attachMenu;
    private JMenuItem gridMenu;
    private JMenuItem markingActionAlign;

    void setMenu() {
        this.editormenu = new JMenu("Editor");
        this.pagemenu = new JMenu("Pages");
        this.viewmenu = new JMenu("View");

        // set the menu view

        markingActionAlign = new JMenuItem("Center markings");
        gridMenu = new JMenuItem(Config.attachPoint ? "Hide grid" : "Show grid");
        attachMenu = new JMenuItem(Config.attachPoint ? "Hide attach points" : "Show attach points");
        //JMenuItem zoomOut = new JMenuItem("Zoom OUT");
        this.viewmenu.add(attachMenu);
        this.viewmenu.add(gridMenu);
        this.viewmenu.add(markingActionAlign);
        //this.viewmenu.add(zoomOut);
        //this.viewmenu.addSeparator();
        attachMenu.addActionListener(this);
        gridMenu.addActionListener(this);
        markingActionAlign.addActionListener(this);
        //zoomOut.addActionListener(this);

        // set the pagemenu
        JMenuItem mi;
        mi = new JMenuItem("New Page");
        this.pagemenu.add(mi);
        mi.addActionListener(this);
        this.pagemenu.addSeparator();

        // create a menu to show/hide extensions of Extendables
        this.extensionmenu = new JMenu("Extensions");
        this.menu = new JMenu[]{this.editormenu, this.viewmenu, this.extensionmenu, this.pagemenu};
    }

    /**
     * Pages can set mode in editor.
     * @param mask the new mode
     */
    void setMode(int mask) {
        if ((mask & CREATE_MASK) != 0) {
            mode = ((~CREATE_MASK) & mode) | mask;
        }
        if ((mask & MODE_MASK) != 0) {
            mode = ((~MODE_MASK) & mode) | mask;
            //update the new state in editormenu
        }
        this.update();
    }
    private static final String[] CLASSES = {"Edge", "PlaceArc", "TransitionArc", "Arc", "Node", "Place", "Transition"};

    protected void setNet() {
        JMenu mi;
        JMenuItem jmi;
        JCheckBoxMenuItem ckb;

        // now the reference of the net is given
        // to the editor so we can set menu

        Editor ed = this.editor;
        GraphProxy graph = ed.getGraphproxy();
        Hashtable specification = graph.getSpecificationTable();

        this.disableItemListeners();

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Node")) {
            editormenu.add(this.node_ckb);
            this.node_ckb.setState(this.isInMode(EditorMenu.NODE));
        }
        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Place")) {
            editormenu.add(this.place_ckb);
            this.place_ckb.setState(this.isInMode(EditorMenu.PLACE));
        }
        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Transition")) {
            editormenu.add(this.tran_ckb);
            this.tran_ckb.setState(this.isInMode(EditorMenu.TRAN));
        }

        editormenu.addSeparator();

        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Arc")) {
            editormenu.add(this.arc_ckb);
            this.arc_ckb.setState(this.isInMode(EditorMenu.ARC));
        }
        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.PlaceArc")) {
            editormenu.add(this.placearc_ckb);
            this.placearc_ckb.setState(this.isInMode(EditorMenu.ARC));
        }
        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.TransitionArc")) {
            editormenu.add(this.transitionarc_ckb);
            this.transitionarc_ckb.setState(this.isInMode(EditorMenu.ARC));
        }
        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Edge")) {
            editormenu.add(this.edge_ckb);
            this.edge_ckb.setState(this.isInMode(EditorMenu.EDGE));
        }
        editormenu.add(this.edit_ckb);
        this.edit_ckb.setState(this.isInMode(EditorMenu.EDIT));

        editormenu.add(this.delete_ckb);
        this.delete_ckb.setState(this.isInMode(EditorMenu.DELETE));

        editormenu.add(this.join_ckb);
        this.join_ckb.setState(this.isInMode(EditorMenu.JOIN));

        editormenu.add(this.split_ckb);
        this.split_ckb.setState(this.isInMode(EditorMenu.SPLIT));

        this.enableItemListeners();

        this.extensionmenu.removeAll();

        //menupunkt zum editieren von Netznamen und Netzextensions
        jmi = new JMenuItem("Set Net Extensions");
        jmi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ev) {
                MemberSprite netsprite = new MemberSprite();
                Object net = editor.getGraphproxy().getGraph();
                netsprite.setNetobject(net);
                new EditDialog(editor, netsprite).setVisible(true);
            }
        });

        this.extensionmenu.add(jmi);
        this.extensionmenu.addSeparator();

        for (String cls : CLASSES) {
            final String extClass = "de.huberlin.informatik.pnk.kernel." + cls;
            Enumeration enumeration = graph.getExtensionIds(extClass);
            if (enumeration != null) {
                mi = new JMenu(cls);
                this.extensionmenu.add(mi);
                while (enumeration.hasMoreElements()) {
                    String extId = (String) enumeration.nextElement();
                    ckb = new JCheckBoxMenuItem(extId);
                    mi.add(ckb);
                    extensionVis.get(cls).put(extId, true);
                    ckb.setState(true);
                    ckb.addItemListener(new ItemListener() {

                        @Override
                        public void itemStateChanged(ItemEvent ie) {
                            JCheckBoxMenuItem ckbox = (JCheckBoxMenuItem) ie.getItemSelectable();
                            String extIdent = ckbox.getText();
                            boolean visible = ckbox.getState();
                            setExtensionLabelsVisible(extIdent, extClass, visible);
                        }
                    });
                }
            }
        }
        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Edge")) {
            setMode(EDGE);
        } else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Arc")) {
            setMode(ARC);
        } else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.PlaceArc")) {
            setMode(ARC);
        } else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.TransitionArc")) {
            setMode(ARC);
        } else {
            Editor.msg("No Edge Type specified!!!");
        }
        if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Node")) {
            setMode(NODE);
        } else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Place")) {
            setMode(PLACE);
        } else if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Transition")) {
            setMode(TRAN);
        } else {
            Editor.msg("No Node Type specified1!!!");
        }
    }

    /**
     * State has changed.
     * Update CheckboxMenuItems of editormenu.
     */
    protected void update() {
        // string that will be displayed in editors pages
        String pageMsg = null;

        if (this.editor.joinSprite != null) {
            if (this.isInMode(JOIN)) {
                pageMsg = "First object for join selected ...";
            } else {
                this.editor.join(null);
            }
        } else if (this.editor.selectOneObject) {
            pageMsg = "Please, select an object ...";
        } else {
            this.disableItemListeners();

            //set the state in the checkboxitems
            if (this.arc_ckb != null) {
                this.arc_ckb.setState(this.isInMode(ARC));
            }
            if (this.edge_ckb != null) {
                this.edge_ckb.setState(this.isInMode(ARC));
            }
            if (this.placearc_ckb != null) {
                this.placearc_ckb.setState(this.isInMode(ARC));
            }
            if (this.transitionarc_ckb != null) {
                this.transitionarc_ckb.setState(this.isInMode(ARC));
            }
            if (this.place_ckb != null) {
                this.place_ckb.setState(this.isInMode(PLACE));
            }
            if (this.tran_ckb != null) {
                this.tran_ckb.setState(this.isInMode(TRAN));
            }
            if (this.node_ckb != null) {
                this.node_ckb.setState(this.isInMode(NODE));
            }
            this.edit_ckb.setState(this.isInMode(EDIT));
            this.select_ckb.setState(this.isInMode(SELECT));
            this.delete_ckb.setState(this.isInMode(DELETE));
            this.join_ckb.setState(this.isInMode(JOIN));
            this.split_ckb.setState(this.isInMode(SPLIT));

            this.enableItemListeners();
        }
        // update displayed text about mode in all pages
        Collection<Page> pages = this.editor.getPagevector();
        if (pages != null) {
            for (Page page : this.editor.getPagevector()) {
                page.displayText(pageMsg);
            }
        }
    }
} // EditorMenu
