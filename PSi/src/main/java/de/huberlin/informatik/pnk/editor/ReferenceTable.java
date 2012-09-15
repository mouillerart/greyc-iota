package de.huberlin.informatik.pnk.editor;

import java.awt.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * ReferenceTable.java
 *
 * This hashtable stores to each netobject
 * some information about its drawing on pages.
 * The information is hold by an object RTInfo.
 *
 * Hashtable: Netobject --> RTInfo
 *
 * Created: Sat Dec 23 12:58:01 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class ReferenceTable extends Hashtable {
    /*
     * Holds the editor to this referencetable.
     */
    private Editor editor;

    /*
     * This class represents a simple struct
     * containing a sprite and a page
     */
    class SpritePageTupel {
        MemberSprite sprite;
        Page page;

        SpritePageTupel(MemberSprite s, Page p) {
            this.page = p;
            this.sprite = s;
        }
    }     // SpritePageTupel

    /*
     * Used to store
     * some editorinformation about a
     * netobject. For Example a list of
     * sprite-page-tupels containing all
     * sprites that represents a netobject.
     */
    class RTInfo {
        Object netobject;

        /*
         * Class constructor.
         */
        RTInfo(Object netobject) {
            this.netobject = netobject;
        }

        /*
         * A list of sprite-page-tupels
         * So You can locate all sprites of a netobject
         */
        Vector spritelist = new Vector();

        void add(MemberSprite s, Page p) {
            spritelist.add(new SpritePageTupel(s, p));
            // if there are more objects, with the same netobject
            // then emphasize them, setting their joined-Flag
            if (spritelist.size() == 1) {
                // nothing more to do, no flag
                s.setJoined(false);
                p.repaint(s.getBounds());
                return;
            } else if (spritelist.size() == 2) {
                // emphasize both objects as joined
                // set first sprite joined
                SpritePageTupel tupel = (SpritePageTupel)spritelist.get(0);
                tupel.sprite.setJoined(true);
                tupel.page.repaint(tupel.sprite.getBounds());
                // set the new sprite joined
                s.setJoined(true);
                p.repaint(s.getBounds());
            } else {
                // all other sprites alredy set joined
                // now set the new sprite as joined
                s.setJoined(true);
                p.repaint(s.getBounds());
            }
        }         // RTInfo.add

        /*
         * Erase a sprite-page-tupel in this RTInfo
         * If no sprite left so delete netobject in kernel.Net!
         */
        void delete(MemberSprite s, Page p) {
            for (int i = 0; i < spritelist.size(); i++) {
                SpritePageTupel tupel = (SpritePageTupel)spritelist.get(i);
                if ((tupel.sprite == s) && (tupel.page == p)) {
                    spritelist.remove(tupel);
                    break;
                }
            }
            // if there is only one last sprite remove joinedFlag
            if (this.spritelist.size() == 1) {
                // only one sprite left, remove the joinedFlag
                SpritePageTupel tupel = (SpritePageTupel) this.spritelist.get(0);
                tupel.sprite.setJoined(false);
            }
            // if there is no sprite delete object in kernel.Net
            if (this.spritelist.isEmpty()) {
                // clean up  in this referencetable
                remove(netobject);
                // delete object in net
                GraphProxy graph = getEditor().getGraphproxy();
                graph.delete(netobject);
            }             // RTInfo delete
        }
    }     // RTInfo

    protected ReferenceTable(Editor editor) {
        super();
        this.editor = editor;
    }

    /**
     * Annotates the sprites of netobject.
     */
    protected void annotate(Object netobject, String annotation) {
        RTInfo info = (RTInfo) this.get(netobject);
        // if the object is not in this referencetable return
        if (info == null) return;

        // for each sprite
        Vector tupels = info.spritelist;
        for (int i = 0; i < tupels.size(); i++) {
            SpritePageTupel tupel = (SpritePageTupel)tupels.get(i);
            Page page = tupel.page;
            Sprite sprite = tupel.sprite;

            // create annotation and add it to page
            Font font = page.getFont();
            FontMetrics fm = page.getFontMetrics(font);
            Dimension size = new Dimension(fm.stringWidth(annotation), fm.getHeight());
            Point position = new Point(sprite.getPosition());
            Annotation ann = new Annotation(sprite, position, size, fm, "annotation", annotation);
            sprite.subsprites.add(ann);
            page.add(ann);
        }
    }

    /*
     * Sets an extension in this editor
     */
    protected void changeExtension(Object netobject, String id, String value) {
        // Get the infoobject of netobject, because it contains all sprites
        RTInfo rtinfo = (RTInfo) this.get(netobject);
        if (rtinfo != null) {
            // now we have a list of our sprites and their pages
            Vector v = rtinfo.spritelist;
            for (int i = 0; i < v.size(); i++) {
                SpritePageTupel tupel = (SpritePageTupel)v.get(i);
                // extract the sprite and its page, to update it
                Page page = tupel.page;
                Sprite sprite = (Sprite)tupel.sprite;
                // look at all subsprites of sprite
                // one of them is our extension, that we
                // want update
                for (int j = 0; j < sprite.subsprites.size(); j++) {
                    Sprite subsprite = (Sprite)sprite.subsprites.get(j);
                    // now look if it is an extension
                    if (subsprite instanceof Extension) {
                        Extension ext = (Extension)subsprite;
                        // now look if id matches
                        if (ext.getId().equals(id)) {
                            // hit! change value
                            ext.setValue(value);
                            int dx = page.translation.x;
                            int dy = page.translation.y;
                            Rectangle updatearea = ext.getBounds();
                            updatearea.translate(dx, dy);
                            // repaint page
                            page.repaint(updatearea);
                            if (netobject instanceof de.huberlin.informatik.pnk.kernel.Edge && id.equals("type")) {
                                updatearea = sprite.getBounds();
                                updatearea.translate(dx, dy);
                                page.repaint(updatearea);
                            }
                        }
                    }
                }
            }
        }
    }     // changeExtension

    /*
     * Change source node of an edge.
     */
    protected void changeSource(Object netedge, Object source) {
        GraphProxy graph = this.editor.getGraphproxy();

        RTInfo einfo = (RTInfo) this.get(netedge);
        Vector elist = einfo.spritelist;

        RTInfo sinfo = (RTInfo) this.get(source);
        Vector slist = sinfo.spritelist;

        SpritePageTupel etupel;
        SpritePageTupel stupel;

        for (int i = 0; i < elist.size(); i++) {
            boolean foundNodeSprite = false;
            etupel = (SpritePageTupel)elist.get(i);
            Edge edge = (Edge)etupel.sprite;
            /*
             * Suche zu jedem edgeSprite ein passendes sprite zum
             * umbiegen der Kante. Finde ich zu einem edgeSprite nichts.
             * So muss ich ein neues nodeSprite extra erzeugen.
             */
            for (int j = 0; j < slist.size(); j++) {
                stupel = (SpritePageTupel)slist.get(j);
                if (etupel.page == stupel.page) {
                    // change source sprite
                    MemberSpriteNode node = (MemberSpriteNode)stupel.sprite;
                    MemberSpriteNode oldNode = edge.source;
                    // unregister this edge in the oldNode
                    oldNode.subsprites.remove(edge);
                    // register edge in new node
                    node.subsprites.add(edge);
                    // set this in the edgeSprite too
                    edge.source = node;
                    // if we found one nodeSprite for this edgeSprite
                    // take the next edgeSprite
                    foundNodeSprite = true;
                    break;
                }
            }
            if (!foundNodeSprite) {
                // we have found no sprite for the current edgeSprite
                // therefor we create a new nodeSprite
                MemberSpriteNode node = null;
                Page page = etupel.page;
                Point position = new Point(50, 50);
                Dimension size = new Dimension(40, 40);

                if (edge.source instanceof Place) node = new Place(position, size);
                else if (edge.source instanceof Transition) node = new Transition(position, size);
                else node = new Node(position, size);
                // register node in page
                page.add(node);

                // change source sprite
                MemberSpriteNode oldNode = edge.source;
                // unregister this edge in the oldNode
                oldNode.subsprites.remove(edge);
                // register edge in new node
                node.subsprites.add(edge);
                // set this in the edgeSprite too
                edge.source = node;

                // register new nodeSprite in this referencetable
                this.register(source, node, page);

                // set extensions for node
                this.checkExtensions(source);
            }
        }
    }

    /*
     * Change target node of an edge.
     */
    protected void changeTarget(Object netedge, Object target) {
        GraphProxy graph = this.editor.getGraphproxy();

        RTInfo einfo = (RTInfo) this.get(netedge);
        Vector elist = einfo.spritelist;

        RTInfo sinfo = (RTInfo) this.get(target);
        Vector slist = sinfo.spritelist;

        SpritePageTupel etupel;
        SpritePageTupel stupel;

        for (int i = 0; i < elist.size(); i++) {
            boolean foundNodeSprite = false;
            etupel = (SpritePageTupel)elist.get(i);
            Edge edge = (Edge)etupel.sprite;
            /*
             * Suche zu jedem edgeSprite ein passendes sprite zum
             * umbiegen der Kante. Finde ich zu einem edgeSprite nichts.
             * So muss ich ein neues nodeSprite extra erzeugen.
             */
            for (int j = 0; j < slist.size(); j++) {
                stupel = (SpritePageTupel)slist.get(j);
                if (etupel.page == stupel.page) {
                    // change target sprite
                    MemberSpriteNode node = (MemberSpriteNode)stupel.sprite;
                    MemberSpriteNode oldNode = edge.target;
                    // unregister this edge in the oldNode
                    oldNode.subsprites.remove(edge);
                    // register edge in new node
                    node.subsprites.add(edge);
                    // set this in the edgeSprite too
                    edge.target = node;
                    // if we found one nodeSprite for this edgeSprite
                    // take the next edgeSprite
                    foundNodeSprite = true;
                    break;
                }
            }
            if (!foundNodeSprite) {
                // we have found no sprite for the current edgeSprite
                // therefor we create a new nodeSprite
                MemberSpriteNode node = null;
                Page page = etupel.page;
                Point position = new Point(50, 50);
                Dimension size = new Dimension(40, 40);

                if (edge.target instanceof Place) node = new Place(position, size);
                else if (edge.target instanceof Transition) node = new Transition(position, size);
                else node = new Node(position, size);
                // register node in page
                page.add(node);

                // change target sprite
                MemberSpriteNode oldNode = edge.target;
                // unregister this edge in the oldNode
                oldNode.subsprites.remove(edge);
                // register edge in new node
                node.subsprites.add(edge);
                // set this in the edgeSprite too
                edge.target = node;

                // register new nodeSprite in this referencetable
                this.register(target, node, page);

                // set extensions for node
                this.checkExtensions(target);
            }
        }
    }

    /*
     *  Updates all extension of netobject on editors pages
     */
    protected void checkExtensions(Object netobject) {
        GraphProxy graph = this.editor.getGraphproxy();
        Hashtable extensions = graph.getExtensionIdToValue(netobject);
        // enumerate over all extensions and set them on pages
        Enumeration e = extensions.keys();
        while (e.hasMoreElements()) {
            String id = (String)e.nextElement();
            String value = (String)extensions.get(id);
            // set the extension
            this.changeExtension(netobject, id, value);
        }
    }

    /**
     * Emphasizes of unEmphasizes all sprites of the netobject.
     * Therefor Its sets the backgroundColor of the sprites.
     */
    protected void emphasize(Object netobject, Color c) {
        RTInfo info = (RTInfo) this.get(netobject);
        // if the object is not in this referencetable return
        if (info == null) return;

        // emphasize each sprite
        Vector tupels = info.spritelist;
        for (int i = 0; i < tupels.size(); i++) {
            SpritePageTupel tupel = (SpritePageTupel)tupels.get(i);
            Page page = tupel.page;
            MemberSprite sprite = (MemberSprite)tupel.sprite;
            // set sprite emphasized or not and repaint it
            sprite.setEmphasized(c);
            Rectangle updateArea = sprite.getBounds();
            updateArea.translate(page.translation.x, page.translation.y);
            page.repaint(updateArea);
        }
    }

    /*
     * Gets vector of netobjects, where sprites selected
     * on pages.
     */
    protected Vector getAllSelectedObjects() {
        Vector selectedNetobjects = new Vector();

        // for all netobjects
        Enumeration e = this.keys();
        while (e.hasMoreElements()) {
            Object netobject = e.nextElement();
            RTInfo info = (RTInfo) this.get(netobject);
            Vector spritelist = info.spritelist;
            // for all sprites of a netobject
            for (int i = 0; i < spritelist.size(); i++) {
                SpritePageTupel tupel = (SpritePageTupel)spritelist.get(i);
                MemberSprite sprite = (MemberSprite)tupel.sprite;
                if (sprite.getSelected()) {
                    // if one sprite is selected add this netobject
                    // to the selected netobjects
                    selectedNetobjects.add(netobject);
                    // carry on with next netobject
                    break;
                }
            }
        }

        return selectedNetobjects;
    }

    /**
     * Get the value of editor.
     * @return Value of editor.
     */
    protected Editor getEditor() {
        return editor;
    }

    /*
     * Adds all sprites of netobject2
     * to sprites of the netobject1
     * and removes entry of netobject2 in this table.
     */
    protected void join(Object netobject1, Object netobject2) {
        // I am adding all sprites of netobject2 to sprites netobject1
        // get all spritePageTupel of msprite2
        RTInfo info = (RTInfo) this.get(netobject2);
        Vector tupelList = info.spritelist;
        // now i am adding all SpritePageTupel to netobject1
        for (int i = 0; i < tupelList.size(); i++) {
            SpritePageTupel tupel = (SpritePageTupel)tupelList.get(i);
            // add tupel to this referencetable under new netobject
            this.register(netobject1, tupel.sprite, tupel.page);
        }
        // now remove entry of netobject2
        this.remove(netobject2);
        // now i must update the extensions on page
        checkExtensions(netobject1);
    }     // join

    /*
     * Registers a new sprite in referencetable.
     * @param netobject sprite represents this netobject,
     * if netobject is null create a new netobject in kernel.Net.
     * @param msprite sprite which register here,
     * @param page where the sprite is located
     * @return true if registering was successful
     */
    boolean register(Object netobject, MemberSprite msprite, Page page) {
        GraphProxy graph = this.getEditor().getGraphproxy();

        if (netobject != null) {
            /*
             * Check if there is alredy an entry of the netobject in
             * this table
             */
            if (this.containsKey(netobject)) {
                // entry exists, adding the new sprite and its page
                RTInfo info = (RTInfo) this.get(netobject);
                info.add(msprite, page);
                // be sure that the right netobject is set
                msprite.setNetobject(netobject);
                // thats all
                return true;
            }
        } else {
            /*
             * if netobject is null, it must be created now.
             * create new netobject
             */
            if (msprite instanceof Arc ||
                msprite instanceof PlaceArc ||
                msprite instanceof TransitionArc) {
                Edge a = (Edge)msprite;
                MemberSprite s = (MemberSprite)a.source;
                MemberSprite t = (MemberSprite)a.target;
                netobject = graph.newArc(s.getNetobject(), t.getNetobject());
            } else if (msprite instanceof Edge) {
                Edge e = (Edge)msprite;
                MemberSprite s = (MemberSprite)e.source;
                MemberSprite t = (MemberSprite)e.target;
                netobject = graph.newEdge(s.getNetobject(), t.getNetobject());
            } else if (msprite instanceof Place) {
                // create a place and set its position
                netobject = graph.newPlace(null);
                //graph.setPosition(netobject, ((Sprite) msprite).getPosition(), page.id);
            } else if (msprite instanceof Transition) {
                // create a transition and set its position
                netobject = graph.newTransition(null);
                //graph.setPosition(netobject, ((Sprite) msprite).getPosition(), page.id);
            } else {
                // create a node and set its postion
                netobject = graph.newNode(null);
                //graph.setPosition(netobject, ((Sprite) msprite).getPosition(), page.id);
            }
        }

        /*
         * netobject not found / created
         */
        if (netobject == null) return false;

        /*
         * Put the netobject to its sprite
         */
        msprite.setNetobject(netobject);

        /*
         * Now add a new entry in this referencetable.
         * Therefor create an RTInfo object.
         */
        RTInfo rt_info = new RTInfo(netobject);
        rt_info.add(msprite, page);

        /*
         * Store netobject and RTInfo in this referencetable
         */
        this.put(netobject, rt_info);

        // set initial name of the new object
        //if(graph.isNode(netobject)) {
        //    String id = graph.getMemberId(netobject);
        //    this.changeExtension(netobject,"name",id);
        //}

        /*
         * registering was successful
         */
        return true;
    }     // register

    /**
     * Set the value of editor.
     * @param v  Value to assign to editor.
     */
    protected void setEditor(Editor v) {
        this.editor = v;
    }

    /**
     * Removes all annotations of a netobject.
     */
    protected void unAnnotate(Object netobject) {
        RTInfo info = (RTInfo) this.get(netobject);
        // if the object is not in this referencetable return
        if (info == null) return;

        // for each sprite
        Vector tupels = info.spritelist;
        for (int i = 0; i < tupels.size(); i++) {
            SpritePageTupel tupel = (SpritePageTupel)tupels.get(i);
            Page page = tupel.page;
            Sprite sprite = tupel.sprite;

            // remove all annotations
            Vector sub = new Vector(sprite.subsprites);
            for (int j = 0; j < sub.size(); j++) {
                Sprite subsprite = (Sprite)sub.get(j);
                if (subsprite instanceof Annotation) {
                    // remove the sprite, its an annotation
                    sprite.subsprites.remove(subsprite);
                    page.remove(subsprite);
                }
            }
        }
    }

    /*
     * Unregisters a sprite in this referenctable
     * If it is the last sprite for a netobject
     * then the netobject will be deleted in kernel.Net.
     */
    void unregister(MemberSprite msprite, Page page) {
        Enumeration e = this.elements();
        while (e.hasMoreElements()) {
            RTInfo rt_info = (RTInfo)e.nextElement();
            /*
             * if this is our RTInfo the sprite will
             * be removed, otherwise nothing happens.
             * If there are no sprites in RTInfo-object
             * the delete()-method deletes netobject
             * in kernel.Net too.
             */
            rt_info.delete(msprite, page);
        }
    }

    /*
     * Deletes a netobject in all editorpages
     * without sending an event to net.
     * Initiator is the net or
     * a pagemouselistener
     */
    void unregister(Object netobject) {
        RTInfo info = (RTInfo) this.get(netobject);
        if (info == null) return;        // the object is already deleted
        for (int i = 0; i < info.spritelist.size(); i++) {
            SpritePageTupel tupel = (SpritePageTupel)info.spritelist.get(i);
            Sprite sprite = (Sprite)tupel.sprite;
            Page page = tupel.page;
            // remove this sprite from page
            page.remove(sprite);
        }
        // clean up this referencetable
        this.remove(netobject);
    }
} // ReferenceTable
