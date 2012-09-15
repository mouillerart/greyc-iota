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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReferenceTable
 *
 * This hashtable stores to each netobject
 * some information about its drawing on pages.
 * The information is hold by an object RTInfo.
 *
 * Hashtable: Netobject --> RTInfo
 *
 * Created after de.huberlin.informatik.pnk.editor.ReferenceTable
 */
class ReferenceTable extends HashMap<Object, ReferenceTable.RTInfo> {

    /**
     * Holds the editor to this referencetable.
     */
    private Editor editor;

    void changeExtensionRepresentation(Object netobject, String id, RepresentationType picture, String path) {
        // Get the infoobject of netobject, because it contains all sprites
        RTInfo rtinfo = this.get(netobject);
        if (rtinfo == null) {
            return;
        }
        // now we have a list of our sprites and their pages
        for (SpritePageTuple tupel : rtinfo.spritelist) {
            // extract the sprite and its page, to update it
            Page page = tupel.page;
            Sprite sprite = tupel.sprite;
            // look at all subsprites of sprite one of them is our extension, that we want update
            for (Sprite subsprite : sprite.subsprites) {
                // now look if it is an extension
                if (subsprite instanceof Extension) {
                    Extension ext = (Extension) subsprite;
                    // now look if id matches
                    if (ext.getId().equals(id)) {
                        // hit! change value
                        ext.setType(picture);
                        ext.setPicturePath(path);
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

    /**
     * This class represents a simple struct containing a sprite and a page
     */
    class SpritePageTuple {

        MemberSprite sprite;
        Page page;

        SpritePageTuple(MemberSprite s, Page p) {
            this.page = p;
            this.sprite = s;
        }
    } // SpritePageTuple

    /**
     * Used to store some editor information about a netobject. For Example a list of
     * sprite-page-tupels containing all sprites that represents a netobject.
     */
    class RTInfo {

        Object netobject;

        /**
         * Class constructor.
         */
        RTInfo(Object netobject) {
            this.netobject = netobject;
        }
        /**
         * A list of sprite-page-tupels. So You can locate all sprites of a netobject
         */
        List<SpritePageTuple> spritelist = new ArrayList<SpritePageTuple>();

        void add(MemberSprite s, Page p) {
            spritelist.add(new SpritePageTuple(s, p));
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
                SpritePageTuple tupel = spritelist.get(0);
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
            s.setPage(p);
        } // RTInfo.add

        /**
         * Erase a sprite-page-tupel in this RTInfo If no sprite left so delete netobject in kernel.Net!
         */
        void delete(MemberSprite s, Page p) {
            for (SpritePageTuple tupel : spritelist) {
                if (tupel.sprite == s && tupel.page == p) {
                    spritelist.remove(tupel);
                    break;
                }
            }
            // if there is only one last sprite remove joinedFlag
            if (this.spritelist.size() == 1) {
                // only one sprite left, remove the joinedFlag
                SpritePageTuple tupel = this.spritelist.get(0);
                tupel.sprite.setJoined(false);
            }
            // if there is no sprite delete object in kernel.Net
            if (this.spritelist.isEmpty()) {
                // clean up  in this referencetable
                remove(netobject);
                // delete object in net
                GraphProxy graph = getEditor().getGraphproxy();
                graph.delete(netobject);
            } // RTInfo delete
        }
    } // RTInfo

    protected ReferenceTable(Editor editor) {
        super();
        this.editor = editor;
    }

    /**
     * Annotates the sprites of netobject.
     */
    protected void annotate(Object netobject, String annotation) {
        RTInfo info = this.get(netobject);
        // if the object is not in this referencetable return
        if (info == null) {
            return;
        }
        for (SpritePageTuple tupel : info.spritelist) {
            Page page = tupel.page;
            Sprite sprite = tupel.sprite;

            // create annotation and add it to page
            Font font = page.getFont();
            FontMetrics fm = page.getFontMetrics(font);
            Dimension size = new Dimension(fm.stringWidth(annotation), fm.getHeight());
            Point position = new Point(sprite.getPosition());
            Annotation ann = new Annotation(sprite, position, size, fm, "annotation", annotation, page, null);
            sprite.subsprites.add(ann);
            page.add(ann);
        }
    }

    /*
     * Sets an extension in this editor
     */
    protected void changeExtension(Object netobject, String id, String value) {
        // Get the infoobject of netobject, because it contains all sprites
        RTInfo rtinfo = this.get(netobject);
        if (rtinfo == null) {
            return;
        }
        // now we have a list of our sprites and their pages
        for (SpritePageTuple tupel : rtinfo.spritelist) {
            // extract the sprite and its page, to update it
            Page page = tupel.page;
            Sprite sprite = (Sprite) tupel.sprite;
            // look at all subsprites of sprite
            // one of them is our extension, that we
            // want update
            for (Sprite subsprite : sprite.subsprites) {
                // now look if it is an extension
                if (subsprite instanceof Extension) {
                    Extension ext = (Extension) subsprite;
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
    } // changeExtension

    public boolean isVisible(Object netobject, String id) {
        RTInfo rtinfo = this.get(netobject);
        if (rtinfo == null) {
            return false;
        }
        for (SpritePageTuple tupel : rtinfo.spritelist) {
            Page page = tupel.page;
            Sprite sprite = (Sprite) tupel.sprite;
            for (Sprite subsprite : sprite.subsprites) {
                if (subsprite instanceof Extension) {
                    Extension ext = (Extension) subsprite;
                    if (ext.getId().equals(id)) {
                        return ext.getVisible();
                    }
                }
            }
        }
        return false;
    }

    void switchExtensionVisibility(Object netobject, String id) {
        // Get the infoobject of netobject, because it contains all sprites
        RTInfo rtinfo = this.get(netobject);
        if (rtinfo == null) {
            return;
        }
        // now we have a list of our sprites and their pages
        for (SpritePageTuple tupel : rtinfo.spritelist) {
            // extract the sprite and its page, to update it
            Page page = tupel.page;
            Sprite sprite = (Sprite) tupel.sprite;
            // look at all subsprites of sprite
            // one of them is our extension, that we
            // want update
            for (Sprite subsprite : sprite.subsprites) {
                // now look if it is an extension
                if (subsprite instanceof Extension) {
                    Extension ext = (Extension) subsprite;
                    // now look if id matches
                    if (ext.getId().equals(id)) {
                        ext.switchVisibility();
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

    /**
     * Change source node of an edge.
     */
    protected void changeSource(Object netedge, Object source) {
        GraphProxy graph = this.editor.getGraphproxy();
        RTInfo einfo = this.get(netedge);
        RTInfo sinfo = this.get(source);
        for (SpritePageTuple etupel : einfo.spritelist) {
            boolean foundNodeSprite = false;
            Edge edge = (Edge) etupel.sprite;
            /*
             * Suche zu jedem edgeSprite ein passendes sprite zum
             * umbiegen der Kante. Finde ich zu einem edgeSprite nichts.
             * So muss ich ein neues nodeSprite extra erzeugen.
             */
            for (SpritePageTuple stupel : sinfo.spritelist) {
                if (etupel.page == stupel.page) {
                    // change source sprite
                    MemberSpriteNode node = (MemberSpriteNode) stupel.sprite;
                    MemberSpriteNode oldNode = edge.getSource();
                    // unregister this edge in the oldNode
                    oldNode.subsprites.remove(edge);
                    // register edge in new node
                    node.subsprites.add(edge);
                    // set this in the edgeSprite too
                    edge.setSource(node);
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

                if (edge.getSource() instanceof Place) {
                    node = new Place(position, size);
                } else if (edge.getSource() instanceof Transition) {
                    node = new Transition(position, size);
                } else {
                    node = new Node(position, size);
                    // register node in page
                }
                page.add(node);

                // change source sprite
                MemberSpriteNode oldNode = edge.getSource();
                // unregister this edge in the oldNode
                oldNode.subsprites.remove(edge);
                // register edge in new node
                node.subsprites.add(edge);
                // set this in the edgeSprite too
                edge.setSource(node);

                // register new nodeSprite in this referencetable
                this.register(source, node, page);

                // set extensions for node
                this.checkExtensions(source);
            }
        }
    }

    /**
     * Change target node of an edge.
     */
    protected void changeTarget(Object netedge, Object target) {
        GraphProxy graph = this.editor.getGraphproxy();

        for (SpritePageTuple etupel : this.get(netedge).spritelist) {
            boolean foundNodeSprite = false;
            /*
             * Suche zu jedem edgeSprite ein passendes sprite zum
             * umbiegen der Kante. Finde ich zu einem edgeSprite nichts.
             * So muss ich ein neues nodeSprite extra erzeugen.
             */
            Edge edge = (Edge) etupel.sprite;
            for (SpritePageTuple stupel : this.get(target).spritelist) {
                if (etupel.page == stupel.page) {
                    // change target sprite
                    MemberSpriteNode node = (MemberSpriteNode) stupel.sprite;
                    MemberSpriteNode oldNode = edge.getTarget();
                    // unregister this edge in the oldNode
                    oldNode.subsprites.remove(edge);
                    // register edge in new node
                    node.subsprites.add(edge);
                    // set this in the edgeSprite too
                    edge.setTarget(node);
                    // if we found one nodeSprite for this edgeSprite
                    // take the next edgeSprite
                    foundNodeSprite = true;
                    break;
                }
            }
            if (!foundNodeSprite) {
                // we have found no sprite for the current edgeSprite
                // therefor we create a new nodeSprite
                Page page = etupel.page;
                Point position = new Point(50, 50);
                Dimension size = new Dimension(40, 40);
                MemberSpriteNode node;
                if (edge.getTarget() instanceof Place) {
                    node = new Place(position, size);
                } else if (edge.getTarget() instanceof Transition) {
                    node = new Transition(position, size);
                } else {
                    node = new Node(position, size);
                    // register node in page
                }
                page.add(node);

                // change target sprite
                MemberSpriteNode oldNode = edge.getTarget();
                // unregister this edge in the oldNode
                oldNode.subsprites.remove(edge);
                // register edge in new node
                node.subsprites.add(edge);
                // set this in the edgeSprite too
                edge.setTarget(node);

                // register new nodeSprite in this referencetable
                this.register(target, node, page);

                // set extensions for node
                this.checkExtensions(target);
            }
        }
    }

    /**
     *  Updates all extension of netobject on editors pages
     */
    protected void checkExtensions(Object netobject) {
        GraphProxy graph = this.editor.getGraphproxy();
        // enumerate over all extensions and set them on pages
        for (Map.Entry<String, String> idval : graph.getExtensionIdToValue(netobject).entrySet()) {
            // set the extension
            this.changeExtension(netobject, idval.getKey(), idval.getValue());
        }
    }

    /**
     * Emphasizes of unEmphasizes all sprites of the netobject.
     * Therefor Its sets the backgroundColor of the sprites.
     */
    protected void emphasize(Object netobject, Color c) {
        RTInfo info = (RTInfo) this.get(netobject);
        // if the object is not in this referencetable return
        if (info == null) {
            return;        // emphasize each sprite
        }
        for (SpritePageTuple tupel : info.spritelist) {
            Page page = tupel.page;
            MemberSprite sprite = (MemberSprite) tupel.sprite;
            // set sprite emphasized or not and repaint it
            sprite.setEmphasized(c);
            Rectangle updateArea = sprite.getBounds();
            updateArea.translate(page.translation.x, page.translation.y);
            page.repaint(updateArea);
        }
    }

    /**
     * Gets vector of netobjects, where sprites selected
     * on pages.
     */
    protected List<Object> getAllSelectedObjects() {
        List<Object> selectedNetobjects = new ArrayList<Object>();
        // for all netobjects
        for (Map.Entry<Object, RTInfo> pair : this.entrySet()) {
            for (SpritePageTuple tupel : pair.getValue().spritelist) {
                // for all sprites of a netobject
                MemberSprite sprite = (MemberSprite) tupel.sprite;
                if (sprite.getSelected()) {
                    // if one sprite is selected add this netobject
                    // to the selected netobjects
                    selectedNetobjects.add(pair.getKey());
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

    /**
     * Adds all sprites of netobject2 to sprites of the netobject1
     * and removes entry of netobject2 in this table.
     */
    protected void join(Object netobject1, Object netobject2) {
        // I am adding all sprites of netobject2 to sprites netobject1
        // get all spritePageTupel of msprite2
        RTInfo info = this.get(netobject2);
        // now i am adding all SpritePageTuple to netobject1
        for (SpritePageTuple tupel : info.spritelist) {
            // add tupel to this referencetable under new netobject
            this.register(netobject1, tupel.sprite, tupel.page);
        }
        // now remove entry of netobject2
        this.remove(netobject2);
        // now i must update the extensions on page
        checkExtensions(netobject1);
    } // join

    /**
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
            if (msprite instanceof Arc
                    || msprite instanceof PlaceArc
                    || msprite instanceof TransitionArc) {
                Edge a = (Edge) msprite;
                MemberSprite s = (MemberSprite) a.getSource();
                MemberSprite t = (MemberSprite) a.getTarget();
                netobject = graph.newArc(s.getNetobject(), t.getNetobject());
            } else if (msprite instanceof Edge) {
                Edge e = (Edge) msprite;
                MemberSprite s = (MemberSprite) e.getSource();
                MemberSprite t = (MemberSprite) e.getTarget();
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
        if (netobject == null) {
            return false;
        }
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

        /*
         * registering was successful
         */
        return true;
    } // register

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
        RTInfo info = this.get(netobject);
        // if the object is not in this referencetable return
        if (info == null) {
            return;
        }
        for (SpritePageTuple tupel : info.spritelist) {
            Page page = tupel.page;
            Sprite sprite = tupel.sprite;
            // remove all annotations
            for (Sprite subsprite : sprite.subsprites) {
                if (subsprite instanceof Annotation) {
                    // remove the sprite, its an annotation
                    sprite.subsprites.remove(subsprite);
                    page.remove(subsprite);
                }
            }
        }
    }

    /**
     * Unregisters a sprite in this referenctable
     * If it is the last sprite for a netobject
     * then the netobject will be deleted in kernel.Net.
     */
    void unregister(MemberSprite msprite, Page page) {
        for (RTInfo rt_info : this.values()) {
            /*
             * if this is our RTInfo the sprite will be removed, otherwise nothing happens.
             * If there are no sprites in RTInfo-object the delete()-method deletes netobject
             * in kernel.Net too.
             */
            rt_info.delete(msprite, page);
        }
    }

    /**
     * Deletes a netobject in all editorpages
     * without sending an event to net.
     * Initiator is the net or
     * a pagemouselistener
     */
    void unregister(Object netobject) {
        RTInfo info = this.get(netobject);
        if (info == null) {
            return; // the object is already deleted
        }
        for (SpritePageTuple tupel : info.spritelist) {
            Sprite sprite = (Sprite) tupel.sprite;
            Page page = tupel.page;
            // remove this sprite from page
            page.remove(sprite);
        }
        // clean up this referencetable
        this.remove(netobject);
    }
} // ReferenceTable
