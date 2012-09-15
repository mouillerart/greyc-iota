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

import de.huberlin.informatik.pnk.kernel.Extendable;
import de.huberlin.informatik.pnk.kernel.Net;
import fr.unicaen.iota.simulator.app.CommCenter;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.util.*;

/**
 * NetLoader
 *
 * Created after de.huberlin.informatik.pnk.editor.NetLoader
 */
class NetLoader {

    private Editor editor;

    /**
     * Class constructor.
     */
    protected NetLoader(Editor editor) {
        this.editor = editor;
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Arc a, int actPageId) {
        // get source-netobject and target-netobject of arc
        Extendable source = a.getSource();
        Extendable target = a.getTarget();

        Page edPage = editor.getPagevector().getPage(actPageId);

        // need referencetable to get sprites of source and target
        ReferenceTable rtable = this.editor.getReferencetable();

        // find source-sprite on edPage
        ReferenceTable.RTInfo sourceInfo = rtable.get(source);
        ReferenceTable.SpritePageTuple sourceTupel = null;
        for (ReferenceTable.SpritePageTuple actSpritePageTupel : sourceInfo.spritelist) {
            if (actSpritePageTupel.page.getId() == actPageId) {
                sourceTupel = actSpritePageTupel;
            }
        }
        MemberSpriteNode sourceSprite = (MemberSpriteNode) sourceTupel.sprite;

        // target-sprite on edPage
        ReferenceTable.RTInfo targetInfo = rtable.get(target);
        ReferenceTable.SpritePageTuple targetTupel = null;
        for (ReferenceTable.SpritePageTuple actSpritePageTupel : targetInfo.spritelist) {
            if (actSpritePageTupel.page.getId() == actPageId) {
                targetTupel = actSpritePageTupel;
            }
        }
        MemberSpriteNode targetSprite = (MemberSpriteNode) targetTupel.sprite;

        // create the sprite
        Arc arc = new Arc(sourceSprite, targetSprite);

        // set position of sprite on edPage
        Point position = a.getPosition(actPageId);
        if (position == null) {
            position.y = 0;
            position.x = 0;
        }
        if (position.x == 0 && position.y == 0) {
            position.x = (int) ((sourceSprite.getPosition().x + targetSprite.getPosition().x) >> 1);
            position.y = (int) ((sourceSprite.getPosition().y + targetSprite.getPosition().y) >> 1);
        }
        arc.setPosition(position);

        // add it to edPage
        edPage.add(arc);

        // register it in referencetable
        rtable.register(a, arc, edPage);

        // load extensions
        this.loadExtensions(a, arc, edPage);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Edge a, int actPageId) {
        // get source-netobject and target-netobject of arc
        Extendable source = a.getSource();
        Extendable target = a.getTarget();

        Page edPage = editor.getPagevector().getPage(actPageId);

        // need referencetable to get sprites of source and target
        ReferenceTable rtable = this.editor.getReferencetable();

        // find source-sprite on edPage
        ReferenceTable.RTInfo sourceInfo = rtable.get(source);
        ReferenceTable.SpritePageTuple sourceTupel = null;
        for (ReferenceTable.SpritePageTuple actSpritePageTupel : sourceInfo.spritelist) {
            if (actSpritePageTupel.page.getId() == actPageId) {
                sourceTupel = actSpritePageTupel;
            }
        }
        MemberSpriteNode sourceSprite = (MemberSpriteNode) sourceTupel.sprite;

        // target-sprite on edPage
        ReferenceTable.RTInfo targetInfo = rtable.get(target);
        ReferenceTable.SpritePageTuple targetTupel = null;
        for (ReferenceTable.SpritePageTuple actSpritePageTupel : targetInfo.spritelist) {
            if (actSpritePageTupel.page.getId() == actPageId) {
                targetTupel = actSpritePageTupel;
            }
        }
        MemberSpriteNode targetSprite = (MemberSpriteNode) targetTupel.sprite;

        // create the sprite
        Edge edge = new Edge(sourceSprite, targetSprite);

        // set position of sprite on edPage
        Point position = a.getPosition(actPageId);
        if (position == null) {
            position.y = 0;
            position.x = 0;
        }
        if (position.x == 0 && position.y == 0) {
            position.x = (int) ((sourceSprite.getPosition().x + targetSprite.getPosition().x) >> 1);
            position.y = (int) ((sourceSprite.getPosition().y + targetSprite.getPosition().y) >> 1);
        }
        edge.setPosition(position);

        // add it to edPage
        edPage.add(edge);

        // register it in referencetable
        rtable.register(a, edge, edPage);

        // load extensions
        this.loadExtensions(a, edge, edPage);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Node p, int actPageId) {
        // get position and size
        Dimension size = new Dimension(Props.NODE_WIDTH, Props.NODE_HEIGHT);
        Point position = p.getPosition(actPageId);

        // create a sprite
        Node node = new Node(position, size);
        // get the edPage of the sprite
        Page edPage = editor.getPagevector().getPage(actPageId);
        // add sprite to its edPage
        edPage.add(node);

        // register netobject and sprite in referencetable
        ReferenceTable rtable = this.editor.getReferencetable();
        rtable.register(p, node, edPage);

        // load extensions of this netobject
        this.loadExtensions(p, node, edPage);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Place p, int actPageId) {
        // get position and size
        Dimension size = new Dimension(Props.PLACE_WIDTH, Props.PLACE_HEIGHT);
        Point position = p.getPosition(actPageId);
        // create a sprite
        Place place = new Place(position, size);
        // get the edPage of the sprite
        Page edPage = editor.getPagevector().getPage(actPageId);
        // add sprite to its edPage
        edPage.add(place);

        // register netobject and sprite in referencetable
        ReferenceTable rtable = this.editor.getReferencetable();
        rtable.register(p, place, edPage);

        // load extensions of this netobject
        this.loadExtensions(p, place, edPage);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.PlaceArc a, int actPageId) {
        // get source-netobject and target-netobject of arc
        Extendable source = a.getSource();
        Extendable target = a.getTarget();

        Page edPage = editor.getPagevector().getPage(actPageId);

        // need referencetable to get sprites of source and target
        ReferenceTable rtable = this.editor.getReferencetable();

        // find source-sprite on edPage
        ReferenceTable.RTInfo sourceInfo = rtable.get(source);
        ReferenceTable.SpritePageTuple sourceTupel = null;
        for (ReferenceTable.SpritePageTuple actSpritePageTupel : sourceInfo.spritelist) {
            if (actSpritePageTupel.page.getId() == actPageId) {
                sourceTupel = actSpritePageTupel;
            }
        }
        MemberSpriteNode sourceSprite = (MemberSpriteNode) sourceTupel.sprite;

        // target-sprite on edPage
        ReferenceTable.RTInfo targetInfo = rtable.get(target);
        ReferenceTable.SpritePageTuple targetTupel = null;
        for (ReferenceTable.SpritePageTuple actSpritePageTupel : targetInfo.spritelist) {
            if (actSpritePageTupel.page.getId() == actPageId) {
                targetTupel = actSpritePageTupel;
            }
        }
        MemberSpriteNode targetSprite = (MemberSpriteNode) targetTupel.sprite;

        // create the sprite
        PlaceArc arc = new PlaceArc(sourceSprite, targetSprite);
        // set position of sprite on edPage
        Point position = a.getPosition(actPageId);
        if (position == null) {
            position.y = 0;
            position.x = 0;
        }
        if (position.x == 0 && position.y == 0) {
            position.x = (int) ((sourceSprite.getPosition().x + targetSprite.getPosition().x) >> 1);
            position.y = (int) ((sourceSprite.getPosition().y + targetSprite.getPosition().y) >> 1);
        }
        arc.setPosition(position);
        // add it to edPage
        edPage.add(arc);

        // register it in referencetable
        rtable.register(a, arc, edPage);

        // load extensions
        this.loadExtensions(a, arc, edPage);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.Transition t, int actPageId) {
        // get position and size
        Dimension size = new Dimension(Props.TRANSITION_WIDTH, Props.TRANSITION_HEIGHT);
        Point position = t.getPosition(actPageId);
        // create a sprite
        Transition transition = new Transition(position, size);
        // get the edPage of the sprite
        Page edPage = editor.getPagevector().getPage(actPageId);
        // add sprite to its edPage
        edPage.add(transition);
        transition.setPage(edPage);
        // register netobject and sprite in referencetable
        ReferenceTable rtable = this.editor.getReferencetable();
        rtable.register(t, transition, edPage);
        // load extensions of this netobject
        this.loadExtensions(t, transition, edPage);
    }

    private void initPageSprite(de.huberlin.informatik.pnk.kernel.TransitionArc a, int actPageId) {
        // get source-netobject and target-netobject of arc
        Extendable source = a.getSource();
        Extendable target = a.getTarget();

        Page edPage = editor.getPagevector().getPage(actPageId);

        // need referencetable to get sprites of source and target
        ReferenceTable rtable = this.editor.getReferencetable();

        // find source-sprite on edPage
        ReferenceTable.RTInfo sourceInfo = rtable.get(source);
        ReferenceTable.SpritePageTuple sourceTupel = null;
        for (ReferenceTable.SpritePageTuple actSpritePageTupel : sourceInfo.spritelist) {
            if (actSpritePageTupel.page.getId() == actPageId) {
                sourceTupel = actSpritePageTupel;
            }
        }
        MemberSpriteNode sourceSprite = (MemberSpriteNode) sourceTupel.sprite;

        // target-sprite on edPage
        ReferenceTable.RTInfo targetInfo = rtable.get(target);
        ReferenceTable.SpritePageTuple targetTupel = null;
        for (ReferenceTable.SpritePageTuple actSpritePageTupel : targetInfo.spritelist) {
            if (actSpritePageTupel.page.getId() == actPageId) {
                targetTupel = actSpritePageTupel;
            }
        }
        MemberSpriteNode targetSprite = (MemberSpriteNode) targetTupel.sprite;

        // create the sprite
        TransitionArc arc = new TransitionArc(sourceSprite, targetSprite);
        // set position of sprite on edPage
        Point position = a.getPosition(actPageId);
        if (position == null) {
            position.y = 0;
            position.x = 0;
        }
        if (position.x == 0 && position.y == 0) {
            position.x = (int) ((sourceSprite.getPosition().x + targetSprite.getPosition().x) >> 1);
            position.y = (int) ((sourceSprite.getPosition().y + targetSprite.getPosition().y) >> 1);
        }
        arc.setPosition(position);
        // ADD IT to edPage
        edPage.add(arc);

        // register it in referencetable
        rtable.register(a, arc, edPage);

        // load extensions
        this.loadExtensions(a, arc, edPage);
    }

    protected void load(Net net) {
        // create a collection of all netobjects
        GraphProxy graph = this.editor.getGraphproxy();
        Collection<Object> netobjects = graph.getAllNetobjects();

        // load all netobjects
        for (Object o : netobjects) {
            //###Editor.msg(" ### editor load: "+o);
            // call to each netobject the specific loadroutine
            if (o instanceof de.huberlin.informatik.pnk.kernel.Place) {
                this.loadPlace((de.huberlin.informatik.pnk.kernel.Place) o);
            } else if (o instanceof de.huberlin.informatik.pnk.kernel.Transition) {
                this.loadTransition((de.huberlin.informatik.pnk.kernel.Transition) o);
            } else if (o instanceof de.huberlin.informatik.pnk.kernel.Node) {
                this.loadNode((de.huberlin.informatik.pnk.kernel.Node) o);
            } else if (o instanceof de.huberlin.informatik.pnk.kernel.PlaceArc) {
                this.loadPlaceArc((de.huberlin.informatik.pnk.kernel.PlaceArc) o);
            } else if (o instanceof de.huberlin.informatik.pnk.kernel.TransitionArc) {
                this.loadTransitionArc((de.huberlin.informatik.pnk.kernel.TransitionArc) o);
            } else if (o instanceof de.huberlin.informatik.pnk.kernel.Arc) {
                this.loadArc((de.huberlin.informatik.pnk.kernel.Arc) o);
            } else if (o instanceof de.huberlin.informatik.pnk.kernel.Edge) {
                this.loadEdge((de.huberlin.informatik.pnk.kernel.Edge) o);
            }
        }
        editor.getPagevector().getPage(1).open();
    }

    /**
     * Loads an arc of the net in  this editor.
     */
    protected void loadArc(de.huberlin.informatik.pnk.kernel.Arc a) {
        // get edPage of a
        Collection<Integer> pageList = a.getPages();
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first edPage
            int actPageId = 1;
            this.initPageSprite(a, actPageId);
        } else {
            // there is a pagelist
            for (Integer actPageId : pageList) {
                this.initPageSprite(a, actPageId);
            }
        }
    }

    /**
     * Loads an arc of the net in  this editor.
     */
    protected void loadEdge(de.huberlin.informatik.pnk.kernel.Edge a) {
        // get edPage of a
        Collection<Integer> pageList = a.getPages();
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first edPage
            int actPageId = 1;
            this.initPageSprite(a, actPageId);
        } else {
            // there is a pagelist
            for (int actPageId : pageList) {
                this.initPageSprite(a, actPageId);
            }
        }
    }

    private void loadExtension(Extendable extendable, String id, String value, Sprite parentSprite, Page page) {
        // get graphproxy
        GraphProxy graph = this.editor.getGraphproxy();
        // Get The Net.Extension
        Extendable extension = (Extendable) graph.getExtension(extendable, id);
        // extract position of extension
        Point position = extension.getOffset(page.getId());
        position.x += parentSprite.getPosition().x;
        position.y += parentSprite.getPosition().y;
        // get size of extensionSprite
        Font font = page.getFont();
        FontMetrics fm = page.getFontMetrics(font);
        Dimension size = new Dimension(fm.stringWidth(value), fm.getHeight());

        if ("marking".equals(id)) {
            size = new Dimension(305, 200);
        }

        // create extensionSprite
        Extension extensionSprite = new Extension(parentSprite, position, size, fm, id, value, page, null);
        String eid = extensionSprite.getId();

        if ("epcgenerator".equals(eid)) {
            extensionSprite.setType(RepresentationType.PICTURE);
            extensionSprite.setPicturePath("./pictures/test.png");
            if (value.split("%").length > 1) {
                extensionSprite.setVisible(true);
            }
        } else if ("epcdeactivator".equals(eid)) {
            extensionSprite.setType(RepresentationType.PICTURE);
            extensionSprite.setPicturePath("./pictures/trash.png");
            if (Boolean.parseBoolean(value)) {
                extensionSprite.setVisible(true);
            }
        } else if ("pipe".equals(eid)) {
            extensionSprite.setType(RepresentationType.PICTURE);
            extensionSprite.setPicturePath("./pictures/pipe.png");
            Point p2 = (Point) parentSprite.getPosition().clone();
            int width = 50;
            int height = 10;
            p2.setLocation(p2.getX() + (width >> 1) + 20, p2.getY() - 20 - (height >> 1));
            String[] tab = value.split("\n");
            Map<String, String> map = new HashMap<String, String>();
            for (String s : tab) {
                String[] line = s.split("=");
                map.put(line[0].trim(), line[1].trim());
            }
            Gauge gauge = new Gauge(parentSprite, p2, width, height, map.get("pipeId"));
            CommCenter.getInstance().addCommListener(gauge);
            parentSprite.subsprites.add(gauge);
            page.add(gauge);
            if (Boolean.parseBoolean(map.get("isPipe")) && "expedition".equals(map.get("type"))) {
                extensionSprite.setVisible(true);
                gauge.setVisible(true);
            }
        }
        parentSprite.subsprites.add(extensionSprite);
        page.add(extensionSprite);
    }

    private void loadExtensions(Extendable extendable, Sprite parentSprite, Page page) {
        // get an enumeration of all extension ids
        GraphProxy graph = this.editor.getGraphproxy();
        Map<String, String> extensions = graph.getExtensionIdToValue(extendable);
        if (extensions == null) {
            return;
        }
        for (Map.Entry<String, String> idval : extensions.entrySet()) {
            this.loadExtension(extendable, idval.getKey(), idval.getValue(), parentSprite, page);
        }
        page.sort();
    }

    /**
     * Loads a node of the net in this editor.
     */
    protected void loadNode(de.huberlin.informatik.pnk.kernel.Node p) {
        // get a list of all pages
        Vector pageList = p.getPages();
        // init all graphical representants of netobject
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first edPage
            int actPageId = 1;
            this.initPageSprite(p, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements();) {
                int actPageId = (Integer) e.nextElement();
                this.initPageSprite(p, actPageId);
            }
        }
    }

    /**
     * Loads a place of the net in this editor.
     */
    protected void loadPlace(de.huberlin.informatik.pnk.kernel.Place p) {
        // get a list of all pages
        Vector pageList = p.getPages();
        // init all graphical representants of netobject
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first edPage
            int actPageId = 1;
            this.initPageSprite(p, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements();) {
                int actPageId = (Integer) e.nextElement();
                this.initPageSprite(p, actPageId);
            }
        }
    }

    /**
     * Loads an arc of the net in  this editor.
     */
    protected void loadPlaceArc(de.huberlin.informatik.pnk.kernel.PlaceArc a) {
        // get edPage of a
        Vector pageList = a.getPages();
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first edPage
            int actPageId = 1;
            this.initPageSprite(a, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements();) {
                int actPageId = (Integer) e.nextElement();
                this.initPageSprite(a, actPageId);
            }
        }
    }

    /**
     * Loads a transition of the net in this editor.
     */
    protected void loadTransition(de.huberlin.informatik.pnk.kernel.Transition t) {
        // get a list of all pages
        Vector pageList = t.getPages();
        // init all graphical representants of netobject
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first edPage
            int actPageId = 1;
            this.initPageSprite(t, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements();) {
                int actPageId = (Integer) e.nextElement();
                this.initPageSprite(t, actPageId);
            }
        }
    }

    /**
     * Loads an arc of the net in  this editor.
     */
    protected void loadTransitionArc(de.huberlin.informatik.pnk.kernel.TransitionArc a) {
        // get edPage of a
        Vector pageList = a.getPages();
        if (pageList.isEmpty()) {
            // there is no pagelist
            // then set the object on first edPage
            int actPageId = 1;
            this.initPageSprite(a, actPageId);
        } else {
            // there is a pagelist
            for (Enumeration e = pageList.elements(); e.hasMoreElements();) {
                int actPageId = (Integer) e.nextElement();
                this.initPageSprite(a, actPageId);
            }
        }
    }
} // NetLoader
