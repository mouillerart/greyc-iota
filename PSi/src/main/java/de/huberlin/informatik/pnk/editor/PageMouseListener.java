package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * PageMouseListener.java
 *
 * This class handels the mouseevents
 * on an editors page.
 * Move objects on mousemotionevents.
 * Create, delete, edit objects on mouseclickevents.
 *
 * Created: Sun Dec 24 13:07:45 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class PageMouseListener
implements MouseListener, MouseMotionListener, ActionListener, ItemListener {
    /*
     * Page where this listener is registered.
     */
    Page page;

    /*
     * Sprite should be moved by mousedrag.
     */
    private Sprite movesprite = null;

    /*
     * Stores initial node of an edge.
     */
    private Sprite initialnode = null;

    /*
     * If mousepointer is over a sprite, so
     * emphasize the sprite. This stores
     * the sprite, that is currently emphasized
     */
    private Sprite emphasizedSprite = null;

    /*
     * Popupmenu of this page.
     */
    private JPopupMenu popup = new JPopupMenu("Editor Menu");

    /*
     * if popumenu is visible, the value will be true
     * so the next mouseclick will only destroy the popupmenu
     * and not init some other actions on page, like creating a place
     * or a transition.
     */
    private boolean popupVisible = false;
    /*
     * Aktiviert den Gravitationsmodus beim plazieren
     * und Bewegen der Sprites
     */
    private boolean gravitationOn = false;

    protected PageMouseListener(Page page) {
        this.page = page;
        this.page.add(this.popup);
        //this.test();
    }

    /*
     * If Destroy button pushed, close page.
     * If Print button pushed, print page.
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Close Page")) this.close();
        if (cmd.equals("Print")) this.page.print();
        if (cmd.equals("Grav")) this.grav();
    }

    /*
     * Adds an arc to page.
     */
    private void addArc(Sprite s) {
        // checking if there is a node:
        if (!(s instanceof MemberSpriteNode)) {
            // undo highlighting of sprite
            boolean initAction = false;
            this.highlightInitial((MemberSprite) this.initialnode,
                                  initAction);
            this.initialnode = null; // erase initialnode
            // give user feedback, that initialnode is not set anymore
            this.page.displayText(null);
            return;
        }

        if (this.initialnode == null) {
            // give user feedback, that initialnode is set
            this.page.displayText("Set initial node ...");
            this.initialnode = s;
            // highlight sprite
            boolean initAction = true;
            this.highlightInitial((MemberSprite) this.initialnode,
                                  initAction);
        } else {
            Edge arc = null;
            // Display Arc on page
            MemberSpriteNode n1 = (MemberSpriteNode) this.initialnode;
            MemberSpriteNode n2 = (MemberSpriteNode)s;
            if (n1 != n2) {
                if (n1 instanceof Place && n2 instanceof Place)
                    arc = new PlaceArc(n1, n2);
                else if (n1 instanceof Transition && n2 instanceof Transition)
                    arc = new TransitionArc(n1, n2);
                else arc = new Arc(n1, n2);
            }

            // undo highlighting of sprite
            boolean initAction = false;
            this.highlightInitial((MemberSprite) this.initialnode,
                                  initAction);
            this.initialnode = null;
            // remove old text in pages textfield
            this.page.displayText(null);

            if (arc != null) {
                // Register arc in referencetable and kernel.Net
                ReferenceTable r = this.page.getEditor().getReferencetable();
                if (r.register(null, arc, this.page)) {
                    // registering was successful so add sprite to page
                    this.page.add(arc);
                    // set extensions on page
                    this.addExtensions(arc);
                }
            }
        }
    }

    /*
     * Adds an edge to page.
     */
    private void addEdge(Sprite s) {
        // checking if there is a node:
        if (!(s instanceof MemberSpriteNode)) {
            boolean initAction = false;
            this.highlightInitial((MemberSprite) this.initialnode,
                                  initAction);
            this.initialnode = null; // erase initialnode
            // give user feedback that initialnode was removed
            this.page.displayText(null);
            return;
        }

        if (this.initialnode == null) {
            // give user feedback that initialnode is set
            this.page.displayText("Set initial node ...");
            this.initialnode = s;
            // highlight sprite
            boolean initAction = true;
            this.highlightInitial((MemberSprite) this.initialnode,
                                  initAction);
        } else {
            Edge edge = null;
            // Display edge on page
            MemberSpriteNode n1 = (MemberSpriteNode) this.initialnode;
            MemberSpriteNode n2 = (MemberSpriteNode)s;
            if (n1 != n2) {
                edge = new Edge(n1, n2);
            }

            // undo highlight sprite
            boolean initAction = false;
            this.highlightInitial((MemberSprite) this.initialnode,
                                  initAction);
            this.initialnode = null;
            // remove old text in pages textfield
            this.page.displayText(null);

            // Register edge in referencetable and kernel.Net
            if (edge != null) {
                ReferenceTable r = this.page.getEditor().getReferencetable();
                if (r.register(null, edge, this.page)) {
                    // registering in referencetable was successful so add sprite to page
                    this.page.add(edge);
                    // set extensions on page
                    this.addExtensions(edge);
                }
            }
        }
    }

    /*
     * Sets extensions to sprite,
     * if sprite is a netobject.
     */
    private void addExtensions(Sprite sprite) {
        Editor editor = this.page.getEditor();
        EditorMenu editormenu = editor.getEditormenu();
        GraphProxy graph = editor.getGraphproxy();
        Object netobj = ((MemberSprite)sprite).getNetobject();
        Hashtable extensions = graph.getExtensionIdToValue(netobj);
        if (extensions == null) return;

        int extCount = 0;
        Enumeration extKeys = extensions.keys();
        while (extKeys.hasMoreElements()) {
            // Get Extension id and value
            String id = (String)extKeys.nextElement();
            String value = (String)extensions.get(id);

            //###Editor.msg(" ### "+netobj+" id: "+id+" value: "+value);

            // Set position, height and width
            Font font = this.page.getFont();
            FontMetrics fm = this.page.getFontMetrics(font);
            Dimension size = new Dimension(fm.stringWidth(value), fm.getHeight());
            Point labelpos = new Point(sprite.getPosition());
            int dx = sprite.getSize().width;
            int dy = (extCount++) * fm.getHeight();
            labelpos.translate(dx, dy);

            // Create extensionlabel, add it to page
            Extension ext = new Extension(sprite, labelpos, size, fm, id, value);
            sprite.subsprites.add(ext);

            // is this extension visible?
            boolean visible = editormenu.isExtensionVisible(sprite, id);
            ext.setVisible(visible);

            // add extension to page
            this.page.add(ext);
        }
    }

    /*
     * Adds a node to page.
     */
    private void addNode(Point position) {
        // Display node on page
        Dimension dim = new Dimension(Props.NODE_WIDTH, Props.NODE_HEIGHT);

        Node node = new Node(position, dim);

        // emphasize the new object
        this.emphasize(node);

        // Register node in referencetable and kernel.Net
        ReferenceTable r = this.page.getEditor().getReferencetable();
        if (r.register(null, node, this.page)) {
            // registering was successful add sprite to page
            this.page.add(node);
            // set extensions on page
            this.addExtensions(node);
        }
    }

    /*
     * Adds a place to page.
     */
    private void addPlace(Point position) {
        // Display place on page
        Dimension dim = new Dimension(Props.PLACE_WIDTH, Props.PLACE_HEIGHT);
        Place place = new Place(position, dim);

        // emphasize the new object
        this.emphasize(place);

        // Register place in referencetable and kernel.Net
        ReferenceTable r = this.page.getEditor().getReferencetable();
        if (r.register(null, place, this.page)) {
            // registering was successful add sprite to page
            this.page.add(place);
            // set extensions on page
            this.addExtensions(place);
        }
    }

    /*
     * Adds a transition to page.
     */
    private void addTransition(Point position) {
        // Display transition on page
        Dimension dim = new Dimension(Props.TRANSITION_WIDTH, Props.TRANSITION_HEIGHT);
        Transition transition = new Transition(position, dim);

        // emphasize the new object
        this.emphasize(transition);

        // Register transition in referencetab and kernel.Net
        ReferenceTable r = this.page.getEditor().getReferencetable();
        if (r.register(null, transition, this.page)) {
            //registering was successful, add sprite to page
            this.page.add(transition);
            // set the extensions on page
            this.addExtensions(transition);
        }
    }

    private void close() {
        boolean editable = this.page.getEditor().isEditable();
        if (!editable) {
            // net not editable so only hide the page
            this.page.page_ckb.setState(false);
            return;
        }
        // ask the user if objects on page should be deleted
        int ok;
        if (this.page.getSpritevector().size() == 0) ok = 0;
        else ok = this.page.getEditor().showConfirmDialog("Delete all objects on page?");
        if (ok == 0) {
            // OK - Button was pressed -> delete all objects
            Vector v = new Vector(this.page.getSpritevector());
            for (int i = 0; i < v.size(); i++) {
                Sprite s = (Sprite)v.get(i);
                if (s instanceof MemberSpriteNode) this.split((MemberSpriteNode)s);
                this.delete(s);
            }
            this.page.close();
        } else {
            this.page.page_ckb.setState(false);
        }
    }

    /*
     * Deletes a sprite.
     * Removes sprite from page.
     * using the page remove(sprite)-method.
     * If sprite is a MemberSprite it also will
     * be remove in editors Referencetable. If it
     * is last MemberSprite of a kernel.Nets object
     * the Referencetable will delete netobject in
     * kernel.Net automaticaly
     */
    private void delete(Sprite sprite) {
        // erase sprite from the page
        //this.page.remove(sprite);
        // check if it must be removed in referencetable
        if (sprite instanceof MemberSprite) {
            //erase sprite in referencetable
            MemberSprite msprite = (MemberSprite)sprite;
            Editor editor = this.page.getEditor();
            ReferenceTable rtable = editor.getReferencetable();
            // deletes kernel.Netobject in this editor
            Object netobject = msprite.getNetobject();
            rtable.unregister(netobject);
            // deletes object in net
            GraphProxy graph = this.page.getEditor().getGraphproxy();
            graph.delete(netobject);
        }
    }

    /*
     * Displays frame for editing sprite.
     */
    private void edit(Sprite sprite) {
        if (sprite instanceof MemberSprite) {
            MemberSprite msprite = (MemberSprite)sprite;
            EditDialog edi = new EditDialog(this.page, msprite);
        }
    }

    private void emphasize(Sprite s) {
        // emphasize objects with this color
        Color red = Color.red;
        // unemphasize color
        Color gray = Color.gray;
        // store the region that needs update
        Rectangle updateRegion = null;

        if (this.emphasizedSprite == s) {
            // Both are null or they are equal
            // nothing to do
            return;
        } else if ((this.emphasizedSprite == null) && (s != null)) {
            // entered a new sprite, emphasize it
            this.emphasizedSprite = s;
            this.emphasizedSprite.setMouseOver(true);
            updateRegion = this.emphasizedSprite.getBounds();
            // set cursor to crosshair
            this.page.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        } else if ((this.emphasizedSprite != null) && (s == null)) {
            // leaved a sprite, unemphasize old sprite
            updateRegion = this.emphasizedSprite.getBounds();
            this.emphasizedSprite.setMouseOver(false);
            this.emphasizedSprite = null;
            // set cursor back to default
            this.page.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            // emphasizedSprite changed
            // unemphasize oldSprite
            // emphasize new Sprite
            updateRegion = this.emphasizedSprite.getBounds();
            this.emphasizedSprite.setMouseOver(false);
            this.emphasizedSprite = s;
            this.emphasizedSprite.setMouseOver(true);
            updateRegion = updateRegion.union(this.emphasizedSprite.getBounds());
        }

        // pay attention to translation on page
        Point translation = this.page.translation;
        updateRegion.translate(translation.x, translation.y);
        // repaint page
        this.page.repaint(updateRegion);
    }

    /*
     * Set position of all nodes relative to a grid.
     */
    private void grav() {
        Point position = new Point();
        Vector spritevector = this.page.getSpritevector();
        for (int i = 0; i < spritevector.size(); i++) {
            Sprite sprite = (Sprite)spritevector.get(i);
            if (sprite instanceof MemberSpriteNode
                || sprite instanceof Edge) {
                // a node, move it to grid
                Point p = sprite.getPosition();
                p.translate(this.page.translation.x, this.page.translation.y);
                int x = this.page.grid_x;
                int y = this.page.grid_y;
                int mod_x = this.page.translation.x % x;
                int mod_y = this.page.translation.y % y;

                int j;
                for (j = mod_x; j <= p.x; j += x) {; }
                Editor.msg(" j: " + j + " und j+x: " + ((int)(j + x)) + " spritepos.x: " + p.x);
                if (p.x <= j - (x >> 1))
                    position.x = j - x;
                else
                    position.x = j;

                for (j = mod_y; j <= p.y; j += y) {; }
                if (p.y <= j - (y >> 1))
                    position.y = j - y;
                else
                    position.y = j;

                //position.x = (int) (((p.x + (x >> 1)) /x) * x );
                //position.y = (int) (((p.y + (y >> 1)) /y) * y );
                Editor.msg("X: " + x + " MOD x: " + mod_x + " Position.x: " + position.x);
                // nun noch kerrekturterm
                /*if(mod_x < (x >> 1)) {
                   position.x += mod_x;
                   } else {
                   position.x -= (x - mod_x);
                   }
                 */

                //position.x += ((int) (mod_x / (x >> 1))) * (x - mod_x);
                //position.y += mod_y;

                this.page.move(sprite, position);
            }
        }
    }

    private void highlightInitial(MemberSprite m, boolean emph) {
        if (m == null) return;
        m.setAction(emph);
        Rectangle bounds = m.getBounds();

        // mybe bounds must be translated
        int x = this.page.translation.x;
        int y = this.page.translation.y;
        bounds.translate(x, y);

        this.page.repaint(bounds);
    }

    public void itemStateChanged(ItemEvent ie) {
        String label = ((JCheckBoxMenuItem)ie.getItemSelectable()).getText();
        EditorMenu em = this.page.getEditor().getEditormenu();

        if (label.equals("Node")) em.setMode(EditorMenu.NODE);
        if (label.equals("Place")) em.setMode(EditorMenu.PLACE);
        if (label.equals("Transition")) em.setMode(EditorMenu.TRAN);
        if (label.equals("Select")) em.setMode(EditorMenu.SELECT);
        if (label.equals("Edit")) em.setMode(EditorMenu.EDIT);
        if (label.equals("Delete")) em.setMode(EditorMenu.DELETE);
        if (label.equals("Edge")) em.setMode(EditorMenu.EDGE);
        if (label.equals("Arc")) em.setMode(EditorMenu.ARC);
        if (label.equals("PlaceArc")) em.setMode(EditorMenu.ARC);
        if (label.equals("TransitionArc")) em.setMode(EditorMenu.ARC);
        if (label.equals("Join")) em.setMode(EditorMenu.JOIN);
        if (label.equals("Split")) em.setMode(EditorMenu.SPLIT);
        if (label.equals("Gravitation")) {
            this.gravitationOn = !this.gravitationOn;
            if (this.gravitationOn) this.grav();
        }

        this.popupVisible = false;
    }

    /*
     * Sets a sprite in editor for join, with another sprite
     */
    private void join(Sprite sprite) {
        if (sprite instanceof MemberSprite) {
            MemberSprite msprite = (MemberSprite)sprite;
            this.page.getEditor().join(msprite);
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.isControlDown() ||
            e.isMetaDown() ||
            e.isShiftDown()) {
            this.showPopup(e);
            this.popupVisible = true;
            return;
        }
        if (this.popupVisible) {
            this.popupVisible = false;
            return;
        }

        Point point = new Point(e.getPoint());
        EditorMenu em = this.page.getEditor().getEditormenu();
        Sprite sprite = this.page.find(point);

        boolean editable = this.page.getEditor().isEditable();

        if ((this.initialnode != null) &&
            (! //and if not Editor is in Mode Edge or Arc
             ((em.isInMode(EditorMenu.EDGE)) ||
         (em.isInMode(EditorMenu.ARC)))
            )
            ) {
            // something did go wrong when creating an arc
            // this resets initialnode and does unhighlighting
            this.addArc(null);
        }

        if (sprite == null) {
            if (editable) { // check if editor permitts this operation
                // not clicked in an object,
                // create place or transition
                // but before creation translate position respecting
                // the translation on page
                Point translation = this.page.translation;
                point.translate(-translation.x, -translation.y);

                if (this.initialnode != null) this.addArc(null);
                else if (em.isInMode(EditorMenu.TRAN)) this.addTransition(point);
                else if (em.isInMode(EditorMenu.PLACE)) this.addPlace(point);
                else if (em.isInMode(EditorMenu.NODE)) this.addNode(point);
                if (this.gravitationOn) this.grav();
            } // editor is editable
        } else {
            // click in an object
            // create arc, delete, edit, join, split
            if (this.page.getEditor().selectOneObject) this.selectOneObject(sprite);
            else if (em.isInMode(EditorMenu.SELECT)) this.select(sprite);
            else if (editable && em.isInMode(EditorMenu.EDGE)) this.addEdge(sprite);
            else if (editable && em.isInMode(EditorMenu.ARC)) this.addArc(sprite);
            else if (editable && em.isInMode(EditorMenu.EDIT)) this.edit(sprite);
            else if (editable && em.isInMode(EditorMenu.DELETE)) this.delete(sprite);
            else if (editable && em.isInMode(EditorMenu.JOIN)) this.join(sprite);
            else if (editable && em.isInMode(EditorMenu.SPLIT)) this.split(sprite);
        }
    }

    /*
     * Moves sprites around.
     */
    public void mouseDragged(MouseEvent e) {
        //Editor.msg("###dragged");
        this.move(e);
    }

    public void mouseEntered(MouseEvent e) {
        //Editor.msg("###entered");
    }

    public void mouseExited(MouseEvent e) {
        //Editor.msg("###exited");
    }

    public void mouseMoved(MouseEvent e) {
        Sprite s = this.page.find(e.getPoint());
        this.emphasize(s);
    }

    public void mousePressed(MouseEvent e) {
        //Editor.msg("###pressed");
    }

    public void mouseReleased(MouseEvent e) {
        //Editor.msg("###released");
        this.reset();
    }

    /*
     * Moves a sprite.
     */
    private void move(MouseEvent e) {
        Point p = e.getPoint();
        if (this.movesprite == null) {
            // reset initial node
            this.initialnode = null;
            // set sprite to move
            Sprite s = this.page.find(p);
            this.movesprite = s;
        } else {
            this.page.move(this.movesprite, p);
            // give the user a feedback of position
            this.page.displayText("Position [" + p.x + "," + p.y + "]");
        }
    }

    /*
     * Sets default values for some variables.
     */
    private void reset() {
        if (this.movesprite != null) {
            // clean up movesprite
            this.movesprite = null;
            this.page.displayText(null);
            // check if the page needs a new value for geometry
            this.page.checkGeometry();
            if (this.gravitationOn) this.grav();
        }
    }

    /*
     * Inverse selectionStatus of sprite.
     * Sets the sprite as selected if it is not selected.
     * Sets the sprite as not selected if it is selected.
     */
    private void select(Sprite sprite) {
        //###Editor.msg("### selecting "+sprite);
        if (sprite instanceof MemberSprite) {
            MemberSprite msprite = (MemberSprite)sprite;
            boolean selected = msprite.getSelected();
            msprite.setSelected(!selected);
            // Repaint sprite in new status
            Rectangle r = msprite.getBounds();
            this.page.repaint(r);
        }
    }

    /*
     * Editor is waiting for a netobject selected on pages.
     * Sets the selected Netobject in editor.
     */
    private void selectOneObject(Sprite sprite) {
        if (sprite instanceof MemberSprite) {
            MemberSprite msprite = (MemberSprite)sprite;
            Object netobject = msprite.getNetobject();
            Editor editor = this.page.getEditor();
            editor.setSelectedNetobject(netobject);
        }
    }

    /*
     * Displays a popup menu on page.
     */
    private void showPopup(MouseEvent e) {
        JMenuItem jmi;
        JCheckBoxMenuItem ckb;
        Editor editor = this.page.getEditor();
        GraphProxy graph = editor.getGraphproxy();
        Hashtable specification = graph.getSpecificationTable();

        Point clickpos = e.getPoint();
        EditorMenu em = this.page.getEditor().getEditormenu();
        Sprite currentobject = (Sprite) this.page.find(clickpos);

        this.popup.removeAll();
        if (currentobject == null) {
            //menu des editors einblenden
            this.popup.setLabel("Editor");

            //zuerst das place checkboxitem
            ckb = new JCheckBoxMenuItem("Place");
            if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Place"))
                this.popup.add(ckb);
            ckb.setState(em.isInMode(EditorMenu.PLACE));
            ckb.addItemListener(this);

            //nun checkbox fuer transitionen
            ckb = new JCheckBoxMenuItem("Transition");
            if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Transition"))
                this.popup.add(ckb);
            ckb.setState(em.isInMode(EditorMenu.TRAN));
            ckb.addItemListener(this);

            //nun checkbox fuer node
            ckb = new JCheckBoxMenuItem("Node");
            if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Node"))
                this.popup.add(ckb);
            ckb.setState(em.isInMode(EditorMenu.NODE));
            ckb.addItemListener(this);

            this.popup.addSeparator();
            //nun fuer normale kanten
            ckb = new JCheckBoxMenuItem("Arc");
            ckb.setState(em.isInMode(EditorMenu.ARC));
            ckb.addItemListener(this);
            if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Arc"))
                this.popup.add(ckb);
            ckb = new JCheckBoxMenuItem("PlaceArc");
            ckb.setState(em.isInMode(EditorMenu.ARC));
            ckb.addItemListener(this);
            if (specification.containsKey("de.huberlin.informatik.pnk.kernel.PlaceArc"))
                this.popup.add(ckb);
            ckb = new JCheckBoxMenuItem("TransitionArc");
            ckb.setState(em.isInMode(EditorMenu.ARC));
            ckb.addItemListener(this);
            if (specification.containsKey("de.huberlin.informatik.pnk.kernel.TransitionArc"))
                this.popup.add(ckb);
            ckb = new JCheckBoxMenuItem("Edge");
            ckb.setState(em.isInMode(EditorMenu.EDGE));
            ckb.addItemListener(this);
            if (specification.containsKey("de.huberlin.informatik.pnk.kernel.Edge"))
                this.popup.add(ckb);
            //editiermodus
            ckb = new JCheckBoxMenuItem("Edit");
            ckb.setState(em.isInMode(EditorMenu.EDIT));
            ckb.addItemListener(this);
            this.popup.add(ckb);
            //selectmodus
            /*
               ckb = new JCheckBoxMenuItem("Select");
               ckb.setState(em.isInMode(EditorMenu.SELECT));
               ckb.addItemListener(this);
               this.popup.add(ckb);
             */
            //deletemodus
            ckb = new JCheckBoxMenuItem("Delete");
            ckb.setState(em.isInMode(EditorMenu.DELETE));
            ckb.addItemListener(this);
            this.popup.add(ckb);
            //joinmodus
            ckb = new JCheckBoxMenuItem("Join");
            ckb.setState(em.isInMode(EditorMenu.JOIN));
            ckb.addItemListener(this);
            this.popup.add(ckb);
            //splitmodus
            ckb = new JCheckBoxMenuItem("Split");
            ckb.setState(em.isInMode(EditorMenu.SPLIT));
            ckb.addItemListener(this);
            this.popup.add(ckb);

            this.popup.addSeparator();

            ckb = new JCheckBoxMenuItem("Gravitation");
            ckb.setState(this.gravitationOn);
            ckb.addItemListener(this);
            this.popup.add(ckb);

            //print page menuitem
            this.popup.addSeparator();
            jmi = new JMenuItem("Print");
            this.popup.add(jmi);
            jmi.addActionListener(this);
            //jmi = new JMenuItem("Grav");
            //this.popup.add(jmi);
            //jmi.addActionListener(this);
            this.popup.addSeparator();
            //destroy page item
            jmi = new JMenuItem("Close Page");
            this.popup.add(jmi);
            jmi.addActionListener(this);
        }
        this.popup.show(this.page, clickpos.x, clickpos.y);
    }

    /*
     * Splits a sprite, which is joined.
     */
    private void split(Sprite sprite) {
        if (sprite instanceof MemberSprite) {
            MemberSprite msprite = (MemberSprite)sprite;
            Editor editor = this.page.getEditor();
            GraphProxy graph = editor.getGraphproxy();
            ReferenceTable rtable = editor.getReferencetable();
            ReferenceTable.RTInfo info;
            ReferenceTable.SpritePageTupel tupel;
            Object netobject = msprite.getNetobject();

            // check if split is possible, else return
            info = (ReferenceTable.RTInfo)rtable.get(netobject);
            if (info.spritelist.size() < 2) return;

            // vector of edges for the splited node
            Vector edges = new Vector();

            // get edges in subspriteVector
            Vector subsprites = msprite.subsprites;
            for (int i = 0; i < subsprites.size(); i++) {
                Sprite s = (Sprite)subsprites.get(i);
                if (s instanceof Edge) {
                    Object edgeNetobj = ((Edge)s).getNetobject();
                    edges.add(edgeNetobj);
                }
            }

            // split node in kernel.Net and store the new netobject
            Object newNetobject = graph.split(edges, netobject);

            // unregister the splitSprite under old netobject in referencetable
            rtable.unregister(msprite, this.page);

            // add the splitSprite with new netobject to referencetable
            rtable.register(newNetobject, msprite, this.page);
            this.page.getEditor().getPagevector().repaint();
        }
    }
} // PageMouseListener
