package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.util.*;

import de.huberlin.informatik.pnk.kernel.*;

/**
 * NetLoader.java
 *
 *
 * Created: Tue Jan  2 20:55:21 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class NetLoader {
    private Editor editor;

    /*
     * Page auf welche die netzobjecte geladen werden
     * wenn der Kernel pages unterstuetzt faellt dieser
     * Teil wieder weg, dann werden pages dynamisch angefordert oder erzeugt
     */
    private Page page;

    /*
     * Class constructor.
     */
    protected NetLoader(Editor editor) {
        this.editor = editor;
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Arc a, int actPageId) {
        // get source-netobject and target-netobject of arc
        Extendable source = a.getSource();
        Extendable target = a.getTarget();

        Page page = editor.getPagevector().getPage(actPageId);

        // need referencetable to get sprites of source and target
        ReferenceTable rtable = this.editor.getReferencetable();

        // find source-sprite on page
        ReferenceTable.RTInfo sourceInfo = (ReferenceTable.RTInfo)rtable.get(source);
        ReferenceTable.SpritePageTupel sourceTupel = null;
        for (Enumeration f = sourceInfo.spritelist.elements(); f.hasMoreElements(); ) {
            ReferenceTable.SpritePageTupel actSpritePageTupel = (ReferenceTable.SpritePageTupel)f.nextElement();
            if (actSpritePageTupel.page.id == actPageId)
                sourceTupel = actSpritePageTupel;
        }
        MemberSpriteNode sourceSprite = (MemberSpriteNode)sourceTupel.sprite;

        // target-sprite on page
        ReferenceTable.RTInfo targetInfo = (ReferenceTable.RTInfo)rtable.get(target);
        ReferenceTable.SpritePageTupel targetTupel = null;
        for (Enumeration f = targetInfo.spritelist.elements(); f.hasMoreElements(); ) {
            ReferenceTable.SpritePageTupel actSpritePageTupel = (ReferenceTable.SpritePageTupel)f.nextElement();
            if (actSpritePageTupel.page.id == actPageId)
                targetTupel = actSpritePageTupel;
        }
        MemberSpriteNode targetSprite = (MemberSpriteNode)targetTupel.sprite;

        // create the sprite
        Arc arc = new Arc(sourceSprite, targetSprite);

        // set position of sprite on page
        Point position = a.getPosition(actPageId);
        if (position == null) {
            position.y = 0;
            position.x = 0;
        }
        if (position.x == 0 && position.y == 0) {
            position.x = (int)((sourceSprite.getPosition().x + targetSprite.getPosition().x) >> 1);
            position.y = (int)((sourceSprite.getPosition().y + targetSprite.getPosition().y) >> 1);
        }
        arc.setPosition(position);

        // add it to page
        page.add(arc);

        // register it in referencetable
        rtable.register(a, arc, page);

        // load extensions
        this.loadExtensions(a, arc, page);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Edge a, int actPageId) {
        // get source-netobject and target-netobject of arc
        Extendable source = a.getSource();
        Extendable target = a.getTarget();

        Page page = editor.getPagevector().getPage(actPageId);

        // need referencetable to get sprites of source and target
        ReferenceTable rtable = this.editor.getReferencetable();

        // find source-sprite on page
        ReferenceTable.RTInfo sourceInfo = (ReferenceTable.RTInfo)rtable.get(source);
        ReferenceTable.SpritePageTupel sourceTupel = null;
        for (Enumeration f = sourceInfo.spritelist.elements(); f.hasMoreElements(); ) {
            ReferenceTable.SpritePageTupel actSpritePageTupel = (ReferenceTable.SpritePageTupel)f.nextElement();
            if (actSpritePageTupel.page.id == actPageId)
                sourceTupel = actSpritePageTupel;
        }
        MemberSpriteNode sourceSprite = (MemberSpriteNode)sourceTupel.sprite;

        // target-sprite on page
        ReferenceTable.RTInfo targetInfo = (ReferenceTable.RTInfo)rtable.get(target);
        ReferenceTable.SpritePageTupel targetTupel = null;
        for (Enumeration f = targetInfo.spritelist.elements(); f.hasMoreElements(); ) {
            ReferenceTable.SpritePageTupel actSpritePageTupel = (ReferenceTable.SpritePageTupel)f.nextElement();
            if (actSpritePageTupel.page.id == actPageId)
                targetTupel = actSpritePageTupel;
        }
        MemberSpriteNode targetSprite = (MemberSpriteNode)targetTupel.sprite;

        // create the sprite
        Edge edge = new Edge(sourceSprite, targetSprite);

        // set position of sprite on page
        Point position = a.getPosition(actPageId);
        if (position == null) {
            position.y = 0;
            position.x = 0;
        }
        if (position.x == 0 && position.y == 0) {
            position.x = (int)((sourceSprite.getPosition().x + targetSprite.getPosition().x) >> 1);
            position.y = (int)((sourceSprite.getPosition().y + targetSprite.getPosition().y) >> 1);
        }
        edge.setPosition(position);

        // add it to page
        page.add(edge);

        // register it in referencetable
        rtable.register(a, edge, page);

        // load extensions
        this.loadExtensions(a, edge, page);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Node p, int actPageId) {
        // get position and size
        Dimension size = new Dimension(Props.NODE_WIDTH, Props.NODE_HEIGHT);
        Point position = p.getPosition(actPageId);

        // create a sprite
        Node node = new Node(position, size);
        // get the page of the sprite
        Page page = editor.getPagevector().getPage(actPageId);
        // add sprite to its page
        page.add(node);

        // register netobject and sprite in referencetable
        ReferenceTable rtable = this.editor.getReferencetable();
        rtable.register(p, node, page);

        // load extensions of this netobject
        this.loadExtensions(p, node, page);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Place p, int actPageId) {
        // get position and size
        Dimension size = new Dimension(Props.PLACE_WIDTH, Props.PLACE_HEIGHT);
        Point position = p.getPosition(actPageId);

        // create a sprite
        Place place = new Place(position, size);
        // get the page of the sprite
        Page page = editor.getPagevector().getPage(actPageId);
        // add sprite to its page
        page.add(place);

        // register netobject and sprite in referencetable
        ReferenceTable rtable = this.editor.getReferencetable();
        rtable.register(p, place, page);

        // load extensions of this netobject
        this.loadExtensions(p, place, page);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.PlaceArc a, int actPageId) {
        // get source-netobject and target-netobject of arc
        Extendable source = a.getSource();
        Extendable target = a.getTarget();

        Page page = editor.getPagevector().getPage(actPageId);

        // need referencetable to get sprites of source and target
        ReferenceTable rtable = this.editor.getReferencetable();

        // find source-sprite on page
        ReferenceTable.RTInfo sourceInfo = (ReferenceTable.RTInfo)rtable.get(source);
        ReferenceTable.SpritePageTupel sourceTupel = null;
        for (Enumeration f = sourceInfo.spritelist.elements(); f.hasMoreElements(); ) {
            ReferenceTable.SpritePageTupel actSpritePageTupel = (ReferenceTable.SpritePageTupel)f.nextElement();
            if (actSpritePageTupel.page.id == actPageId)
                sourceTupel = actSpritePageTupel;
        }
        MemberSpriteNode sourceSprite = (MemberSpriteNode)sourceTupel.sprite;

        // target-sprite on page
        ReferenceTable.RTInfo targetInfo = (ReferenceTable.RTInfo)rtable.get(target);
        ReferenceTable.SpritePageTupel targetTupel = null;
        for (Enumeration f = targetInfo.spritelist.elements(); f.hasMoreElements(); ) {
            ReferenceTable.SpritePageTupel actSpritePageTupel = (ReferenceTable.SpritePageTupel)f.nextElement();
            if (actSpritePageTupel.page.id == actPageId)
                targetTupel = actSpritePageTupel;
        }
        MemberSpriteNode targetSprite = (MemberSpriteNode)targetTupel.sprite;

        // create the sprite
        PlaceArc arc = new PlaceArc(sourceSprite, targetSprite);
        // set position of sprite on page
        Point position = a.getPosition(actPageId);
        if (position == null) {
            position.y = 0;
            position.x = 0;
        }
        if (position.x == 0 && position.y == 0) {
            position.x = (int)((sourceSprite.getPosition().x + targetSprite.getPosition().x) >> 1);
            position.y = (int)((sourceSprite.getPosition().y + targetSprite.getPosition().y) >> 1);
        }
        arc.setPosition(position);
        // add it to page
        page.add(arc);

        // register it in referencetable
        rtable.register(a, arc, page);

        // load extensions
        this.loadExtensions(a, arc, page);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Transition t, int actPageId) {
        // get position and size
        Dimension size = new Dimension(Props.TRANSITION_WIDTH, Props.TRANSITION_HEIGHT);
        Point position = t.getPosition(actPageId);

        // create a sprite
        Transition transition = new Transition(position, size);
        // get the page of the sprite
        Page page = editor.getPagevector().getPage(actPageId);
        // add sprite to its page
        page.add(transition);

        // register netobject and sprite in referencetable
        ReferenceTable rtable = this.editor.getReferencetable();
        rtable.register(t, transition, page);

        // load extensions of this netobject
        this.loadExtensions(t, transition, page);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.TransitionArc a, int actPageId) {
        // get source-netobject and target-netobject of arc
        Extendable source = a.getSource();
        Extendable target = a.getTarget();

        Page page = editor.getPagevector().getPage(actPageId);

        // need referencetable to get sprites of source and target
        ReferenceTable rtable = this.editor.getReferencetable();

        // find source-sprite on page
        ReferenceTable.RTInfo sourceInfo = (ReferenceTable.RTInfo)rtable.get(source);
        ReferenceTable.SpritePageTupel sourceTupel = null;
        for (Enumeration f = sourceInfo.spritelist.elements(); f.hasMoreElements(); ) {
            ReferenceTable.SpritePageTupel actSpritePageTupel = (ReferenceTable.SpritePageTupel)f.nextElement();
            if (actSpritePageTupel.page.id == actPageId)
                sourceTupel = actSpritePageTupel;
        }
        MemberSpriteNode sourceSprite = (MemberSpriteNode)sourceTupel.sprite;

        // target-sprite on page
        ReferenceTable.RTInfo targetInfo = (ReferenceTable.RTInfo)rtable.get(target);
        ReferenceTable.SpritePageTupel targetTupel = null;
        for (Enumeration f = targetInfo.spritelist.elements(); f.hasMoreElements(); ) {
            ReferenceTable.SpritePageTupel actSpritePageTupel = (ReferenceTable.SpritePageTupel)f.nextElement();
            if (actSpritePageTupel.page.id == actPageId)
                targetTupel = actSpritePageTupel;
        }
        MemberSpriteNode targetSprite = (MemberSpriteNode)targetTupel.sprite;

        // create the sprite
        TransitionArc arc = new TransitionArc(sourceSprite, targetSprite);
        // set position of sprite on page
        Point position = a.getPosition(actPageId);
        if (position == null) {
            position.y = 0;
            position.x = 0;
        }
        if (position.x == 0 && position.y == 0) {
            position.x = (int)((sourceSprite.getPosition().x + targetSprite.getPosition().x) >> 1);
            position.y = (int)((sourceSprite.getPosition().y + targetSprite.getPosition().y) >> 1);
        }
        arc.setPosition(position);
        // ADD IT to page
        page.add(arc);

        // register it in referencetable
        rtable.register(a, arc, page);

        // load extensions
        this.loadExtensions(a, arc, page);
    }

    protected void load(Net net) {
        //provisorisch ersteinmal eine page zum laden oeffnen
        //this.page = editor.getPagevector().createPage();

        // create a collection of all netobjects
        GraphProxy graph = this.editor.getGraphproxy();
        Vector netobjects = graph.getAllNetobjects();

        // load all netobjects
        for (int i = 0; i < netobjects.size(); i++) {
            Object o = netobjects.elementAt(i);
            //###Editor.msg(" ### editor load: "+o);
            // call to each netobject the specific loadroutine

            if (o instanceof de.huberlin.informatik.pnk.kernel.Place)
                this.loadPlace((de.huberlin.informatik.pnk.kernel.Place)o);
            else if (o instanceof de.huberlin.informatik.pnk.kernel.Transition)
                this.loadTransition((de.huberlin.informatik.pnk.kernel.Transition)o);
            else if (o instanceof de.huberlin.informatik.pnk.kernel.Node)
                this.loadNode((de.huberlin.informatik.pnk.kernel.Node)o);
            else if (o instanceof de.huberlin.informatik.pnk.kernel.PlaceArc)
                this.loadPlaceArc((de.huberlin.informatik.pnk.kernel.PlaceArc)o);
            else if (o instanceof de.huberlin.informatik.pnk.kernel.TransitionArc)
                this.loadTransitionArc((de.huberlin.informatik.pnk.kernel.TransitionArc)o);
            else if (o instanceof de.huberlin.informatik.pnk.kernel.Arc)
                this.loadArc((de.huberlin.informatik.pnk.kernel.Arc)o);
            else if (o instanceof de.huberlin.informatik.pnk.kernel.Edge)
                this.loadEdge((de.huberlin.informatik.pnk.kernel.Edge)o);
        }
    }

    /*
     * Loads an arc of the net in  this editor.
     */
    protected void loadArc(de.huberlin.informatik.pnk.kernel.Arc a) {
        // get page of a
        Vector pageList = a.getPages();
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first page
            int actPageId = 1;
            this.initPageSprite(a, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements(); ) {
                int actPageId = ((Integer)e.nextElement()).intValue();
                this.initPageSprite(a, actPageId);
            }
        }
    }

    /*
     * Loads an arc of the net in  this editor.
     */
    protected void loadEdge(de.huberlin.informatik.pnk.kernel.Edge a) {
        // get page of a
        Vector pageList = a.getPages();
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first page
            int actPageId = 1;
            this.initPageSprite(a, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements(); ) {
                int actPageId = ((Integer)e.nextElement()).intValue();
                this.initPageSprite(a, actPageId);
            }
        }
    }

    private void loadExtension(Extendable extendable, String id, String value,
                               Sprite parentSprite, Page page) {
        // get graphproxy
        GraphProxy graph = this.editor.getGraphproxy();
        // Get The Net.Extension
        Extendable extension = (Extendable)graph.getExtension(extendable, id);
        // extract position of extension
        Point position = extension.getOffset(page.id);
        position.x += parentSprite.getPosition().x;
        position.y += parentSprite.getPosition().y;
        // get size of extensionSprite
        Font font = page.getFont();
        FontMetrics fm = page.getFontMetrics(font);
        Dimension size = new Dimension(fm.stringWidth(value), fm.getHeight());

        // crate extensionSprite
        Extension extensionSprite = new Extension(parentSprite, position, size, fm, id, value);
        parentSprite.subsprites.add(extensionSprite);
        page.add(extensionSprite);
    }

    private void loadExtensions(Extendable extendable, Sprite parentSprite, Page page) {
        // get an enumeration of all extension ids
        GraphProxy graph = this.editor.getGraphproxy();
        Hashtable extensions = graph.getExtensionIdToValue(extendable);
        if (extensions == null) return;
        Enumeration extIds = extensions.keys();
        // enumerate over ids and load the extensions
        while (extIds.hasMoreElements()) {
            String id = (String)extIds.nextElement();
            String value = (String)extensions.get(id);
            this.loadExtension(extendable, id, value, parentSprite, page);
        }
    }

    /*
     * Loads a node of the net in this editor.
     */
    protected void loadNode(de.huberlin.informatik.pnk.kernel.Node p) {
        // get a list of all pages
        Vector pageList = p.getPages();
        // init all graphical representants of netobject
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first page
            int actPageId = 1;
            this.initPageSprite(p, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements(); ) {
                int actPageId = ((Integer)e.nextElement()).intValue();
                this.initPageSprite(p, actPageId);
            }
        }
    }

    /*
     * Loads a place of the net in this editor.
     */
    protected void loadPlace(de.huberlin.informatik.pnk.kernel.Place p) {
        // get a list of all pages
        Vector pageList = p.getPages();
        // init all graphical representants of netobject
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first page
            int actPageId = 1;
            this.initPageSprite(p, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements(); ) {
                int actPageId = ((Integer)e.nextElement()).intValue();
                this.initPageSprite(p, actPageId);
            }
        }
    }

    /*
     * Loads an arc of the net in  this editor.
     */
    protected void loadPlaceArc(de.huberlin.informatik.pnk.kernel.PlaceArc a) {
        // get page of a
        Vector pageList = a.getPages();
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first page
            int actPageId = 1;
            this.initPageSprite(a, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements(); ) {
                int actPageId = ((Integer)e.nextElement()).intValue();
                this.initPageSprite(a, actPageId);
            }
        }
    }

    /*
     * Loads a transition of the net in this editor.
     */
    protected void loadTransition(de.huberlin.informatik.pnk.kernel.Transition t) {
        // get a list of all pages
        Vector pageList = t.getPages();
        // init all graphical representants of netobject
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first page
            int actPageId = 1;
            this.initPageSprite(t, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements(); ) {
                int actPageId = ((Integer)e.nextElement()).intValue();
                this.initPageSprite(t, actPageId);
            }
        }
    }

    /*
     * Loads an arc of the net in  this editor.
     */
    protected void loadTransitionArc(de.huberlin.informatik.pnk.kernel.TransitionArc a) {
        // get page of a
        Vector pageList = a.getPages();
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first page
            int actPageId = 1;
            this.initPageSprite(a, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements(); ) {
                int actPageId = ((Integer)e.nextElement()).intValue();
                this.initPageSprite(a, actPageId);
            }
        }
    }
} // NetLoader
