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

import de.huberlin.informatik.pnk.app.base.ApplicationNetDialog;
import de.huberlin.informatik.pnk.app.base.MetaBigApplication;
import de.huberlin.informatik.pnk.appControl.ApplicationControl;
import de.huberlin.informatik.pnk.kernel.Graph;
import de.huberlin.informatik.pnk.kernel.Member;
import de.huberlin.informatik.pnk.kernel.Net;
import de.huberlin.informatik.pnk.kernel.base.ActionObject;
import de.huberlin.informatik.pnk.kernel.base.NetObserver;
import java.awt.Color;
import java.awt.Point;
import java.util.*;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

/**
 * Editor
 *
 * Created after de.huberlin.informatik.pnk.editor.Editor
 */
public class Editor extends MetaBigApplication implements NetObserver, ApplicationNetDialog, Observer {

    /**
     * if this is true an application called the selectObject method of this
     * editor so this editor, will return the next object that recives a
     * mouseclick.
     */
    protected boolean selectOneObject = false;
    /**
     * If an object is selected. Store it here.
     */
    private Object selectedNetobject = null;
    /**
     * This contains the netobjects from which to select
     */
    private java.util.List<Object> selectableObjects = null;
    /**
     * String contains name of this application. Important for
     * ApplicationControl.
     */
    public static String staticAppName = "Petri net Editor";
    /**
     * Manages different pages, for drawing the graph
     */
    private PageVector pagevector;
    /**
     * Manages different pages, for drawing the graph
     */
    private SelectDialog selectDialog = null;
    /**
     * Hashtable, with a list of all graphic representants of netobjects
     */
    private ReferenceTable referencetable;
    /**
     * proxy for the kernel-net. the editor not directly accesses the net
     * therefor it uses this proxy
     */
    private GraphProxy graphproxy;
    /**
     * Specifies action on pages. Tells what to do on mouseclick.
     */
    private EditorMenu editormenu;
    /**
     * Helps to load a net.
     */
    private NetLoader netloader = null;
    /**
     * Helps to save a net.
     */
    private NetWriter netwriter = null;
    /**
     * If we want to join two objects we must store the first object until the
     * second object is selceted.
     */
    MemberSprite joinSprite = null;
    private EditorWindow editorwindow;

    /**
     * Get the value of editorwindow.
     *
     * @return Value of editorwindow.
     */
    public EditorWindow getEditorwindow() {
        return editorwindow;
    }

    /**
     * Set the value of editorwindow.
     *
     * @param v Value to assign to editorwindow.
     */
    public void setEditorwindow(EditorWindow v) {
        this.editorwindow = v;
    }

    /**
     * Class constructor. Start editor with applicationcontrol
     */
    public Editor(ApplicationControl applicationcontrol) {
        super(applicationcontrol);

        this.netloader = new NetLoader(this);
        this.netwriter = new NetWriter(this);

        this.pagevector = new PageVector(this);
        this.referencetable = new ReferenceTable(this);
        this.graphproxy = new GraphProxy(this, null);
        this.editormenu = new EditorMenu(this);
        //this.editorwindow = new EditorWindow(this);
    }

    /**
     * Requests this editor to anotate some objects.
     *
     * @param anotations a hashtable(Object netobject -> String anotation)
     */
    @Override
    public void anotateObjects(Hashtable annotations) {
        anotateObjects((Map<Object, String>) annotations);
    }

    public void anotateObjects(Map<Object, String> annotations) {
        for (Map.Entry<Object, String> neto_ann : annotations.entrySet()) {
            this.referencetable.annotate(neto_ann.getKey(), neto_ann.getValue());
        }
    }

    /*
     * Implementation of interface NetObserver
     */
    /**
     * The value of identified extension of
     * <code>netobject</code> changed.
     *
     * @param netobject the object which extension changed
     * @param extension the extension identifier of the changed extension
     * @param newValue the new value of the changed extension
     */
    @Override
    public void changeExtension(de.huberlin.informatik.pnk.kernel.Member netobject, String extension, String newValue) {
        //        Editor.msg("````````````Editor> change extension");
        this.referencetable.changeExtension(netobject, extension, newValue);
    }

    /**
     * The source-node of an arc has changed.
     *
     * @param netobject the arc with the changed source-node
     * @param source the new source of the arc
     */
    @Override
    public void changeSource(de.huberlin.informatik.pnk.kernel.Edge netobject,
            de.huberlin.informatik.pnk.kernel.Node source) {
        this.referencetable.changeSource(netobject, source);
    }

    /**
     * The target-node of an arc has changed.
     *
     * @param netobject the arc with the new target-node
     * @param target the new target-node
     */
    @Override
    public void changeTarget(de.huberlin.informatik.pnk.kernel.Edge netobject, de.huberlin.informatik.pnk.kernel.Node target) {
        this.referencetable.changeTarget(netobject, target);
    }

    /**
     * The netobject was deleted.
     *
     * @param netobject the deleted object
     */
    @Override
    public void delete(de.huberlin.informatik.pnk.kernel.Member netobject) {
        this.referencetable.unregister(netobject);
    }

    /**
     * Requests this editor to emphasize some objects.
     *
     * @param objects a vector of the objects, which should be emphasized
     */
    @Override
    public void emphasizeObjects(Vector objects) {
        emphasizeObjects((Collection<Object>) objects);
    }

    public void emphasizeObjects(Collection<Object> objects) {
        Color c = Color.magenta;
        for (Object obj : objects) {
            this.referencetable.emphasize(obj, c);
        }
    }

    @Override
    public void emphasizeObjects(Vector objects, Color c) {
        emphasizeObjects((Collection<Object>) objects, c);
    }

    public void emphasizeObjects(Collection<Object> objects, Color c) {
        for (Object obj : objects) {
            this.referencetable.emphasize(obj, c);
        }
    }

    @Override
    public void emphasizeObject(Object o) {
        this.referencetable.emphasize(o, Color.magenta);
    }

    @Override
    public void emphasizeObject(Object o, Color c) {
        this.referencetable.emphasize(o, c);
    }

    /**
     * Displays an error message.
     *
     * @param msg the errormessage
     */
    public void error(String msg) {
        this.showInformation("ERROR MESSAGE: " + msg);
    }

    /**
     * Get the value of editormenu.
     *
     * @return Value of editormenu.
     */
    EditorMenu getEditormenu() {
        return editormenu;
    }

    /**
     * Get the value of graphproxy.
     *
     * @return Value of graphproxy.
     */
    GraphProxy getGraphproxy() {
        return graphproxy;
    }

    /**
     * An application requests this editor to show a text and get a user answer.
     *
     * @param infoText the text which should be displayed in editor, as an
     * information
     * @return the answer of the user
     */
    public Object getInformation(String infoText) {
        return this.getInformation(infoText, null);
    }

    /**
     * An application requests this editor to show a text and get a user answer.
     *
     * @param infoText the text which should be displayed in editor, as an
     * information
     * @return the answer of the user
     */
    public Object getInformation(String infoText, String defaultInputText) {
        return JOptionPane.showInputDialog(null, infoText, "Get Information",
                JOptionPane.QUESTION_MESSAGE, null, null, defaultInputText);
    }

    @Override
    public JMenu[] getMenus() {
        return this.editormenu.getMenu();
    }

    /**
     * Get the value of pagevector.
     *
     * @return Value of pagevector.
     */
    PageVector getPagevector() {
        return pagevector;
    }

    /**
     * Get the value of referencetable.
     *
     * @return Value of referencetable.
     */
    ReferenceTable getReferencetable() {
        return referencetable;
    }

    /**
     * Stores the first object for join. Or if first object is alredy choiced
     * join the netobjects of the two sprites.
     */
    void join(MemberSprite msprite) {
        // user clicked twice one object
        if (this.joinSprite == msprite) {
            return;
        }
        if ((msprite == null) || (!(msprite instanceof MemberSpriteNode))) {
            // abort join, maybe user choiced wrong object
            // UNemphasize the first join object
            boolean highlight = false;
            this.joinSprite.setAction(highlight);
            this.pagevector.repaint();
            // delete reference
            this.joinSprite = null;
        } else if (this.joinSprite == null) {
            // store the first object for join
            this.joinSprite = msprite;
            // emphasize the first join object
            boolean highlight = true;
            this.joinSprite.setAction(highlight);
            this.pagevector.repaint();
        } else if ((this.joinSprite instanceof Place && msprite instanceof Place)
                || (this.joinSprite instanceof Transition && msprite instanceof Transition)) {
            // join the netobjects of msprite and joinsprite
            Object netobj1 = this.joinSprite.getNetobject();
            Object netobj2 = msprite.getNetobject();
            // join them in net
            this.graphproxy.join(netobj1, netobj2);
            // clean up referencetable
            this.referencetable.join(netobj1, netobj2);
            // UNemphasize the first join object
            boolean highlight = false;
            this.joinSprite.setAction(highlight);
            this.pagevector.repaint();
            this.joinSprite = null;
        } else { // something was wrong
            // UNemphasize the first join object
            boolean highlight = false;
            this.joinSprite.setAction(highlight);
            this.pagevector.repaint();
            this.joinSprite = null;
        }
        //display a message on all pages
        this.editormenu.update();
    }

    /**
     * A new arc in net.
     *
     * @param netobject the new object in net
     */
    @Override
    public void newArc(de.huberlin.informatik.pnk.kernel.Arc netobject) {
        this.netloader.loadArc(netobject);
    }

    @Override
    public void newPlaceArc(de.huberlin.informatik.pnk.kernel.PlaceArc netobject) {
        this.netloader.loadPlaceArc(netobject);
    }

    @Override
    public void newTransitionArc(de.huberlin.informatik.pnk.kernel.TransitionArc netobject) {
        this.netloader.loadTransitionArc(netobject);
    }

    /**
     * Set a new net.
     *
     * @param net the new net
     */
    @Override
    public void newNet(Net net) {
        this.closeNet();
        this.openNet(net);
    }

    /**
     * A new place in net.
     *
     * @param netobject the new object in net
     */
    @Override
    public void newPlace(de.huberlin.informatik.pnk.kernel.Place netobject) {
        this.netloader.loadPlace(netobject);
    }

    /**
     * A new transition in net.
     *
     * @param netobject the new object in net
     * @param name the name of the new object
     * @param extIdToValue a hashtable (String extensionIdent -> String
     * extensionValue)
     */
    @Override
    public void newTransition(de.huberlin.informatik.pnk.kernel.Transition netobject) {
        this.netloader.loadTransition(netobject);
    }

    private void openNet(Net net) {
        this.graphproxy.setGraph(net);
        this.referencetable = new ReferenceTable(this);
        //        this.editorwindow.setNet();
        this.editormenu.setNet();
        if (this.pagevector.isEmpty()) {
            this.pagevector.createPage();
        }
        this.netloader.load(net);
    }

    /**
     * Requests this editor to reset all anotations.
     */
    @Override
    public void resetAnnotations() {
        this.unAnotateObjects(this.graphproxy.getAllNetobjects());
    }

    //###
    //### Implementation of interface: ApplicationNetDialog
    //###
    /**
     * Requests this editor to unemphasize all objects.
     */
    @Override
    public void resetEmphasize() {
        this.unEmphasizeObjects(this.graphproxy.getAllNetobjects());
    }

    /**
     * ApplicationControl calls this method, to tell this editor that it should
     * save the graph.
     */
    @Override
    public void save(Graph graph) {
        this.netwriter.write(graph);
    }

    /**
     * Requests this editor to select an object.
     *
     * @param objects a vector of objects where one object should be selected
     * @return the selected object
     */
    @Override
    public Member selectObject(Vector objects, boolean visible) {
        return selectObject((Collection<Object>) objects, visible);
    }

    public Member selectObject(Collection<Object> objects, boolean visible) {
        this.selectableObjects = new ArrayList(objects);
        // erase an old selected object
        this.selectedNetobject = null;
        // set SelectOneObject Mode in this editor
        this.selectOneObject = true;
        this.editormenu.update();

        // emphasize all objects that can be selected
        this.emphasizeObjects(objects);
        this.setNotEditable();

        this.selectDialog = new SelectDialog("Select", "Please select one emphasized Object.", false);
        if (visible) {
            this.selectDialog.show();
        }
        int i = this.selectDialog.waitWindow();

        // remove selectOneObject-Mode in editormenu
        this.selectOneObject = false;
        this.editormenu.update();

        this.unEmphasizeObjects(objects);
        this.setEditable();

        if (i == SelectDialog.SELECTDIALOG_CANCEL) {
            return null;
        } else {
            return (de.huberlin.informatik.pnk.kernel.Member) this.selectedNetobject;
        }
    }

    @Override
    public Member selectObject(Vector objects) {
        return selectObject((Collection<Object>) objects, true);
    }

    @Override
    public void cancelSelectObject() {
        this.selectedNetobject = null;
        this.selectDialog.finish();
    }

    /**
     * Requests this editor to select some objects...
     *
     * @param objects a vector of objects where some should be selected
     * @return a vector of all selected objects
     */
    @Override
    public synchronized Vector selectObjects(Vector objects) {
        return new Vector(selectObjects((Collection<Object>) objects));
    }

    public synchronized Collection<Object> selectObjects(Collection<Object> objects) {
        // set the selectMode in this editor
        this.editormenu.setMode(EditorMenu.SELECT);
        // emphasize all objects that could be selected
        this.emphasizeObjects(objects);
        this.setNotEditable();

        // show the dialog, that the user
        // can tell me when he finished his joice
        // and all threads that wait can be notified
        new AllSelectedDialog(this).setVisible(true);

        // now let the current thread wait until selection finished
        try {
            wait();
        } catch (InterruptedException ie) {
        }

        // now get the selected objects
        Collection<Object> selected = this.referencetable.getAllSelectedObjects();
        // check if users choice is valid
        for (Object o : selected) {
            //###Editor.msg("selected: "+o);
            // if wrong object was selected by user
            // then slection failed, therefor return null
            if (!objects.contains(o)) {
                selected = null;
                break;
            }
        }

        this.unEmphasizeObjects(objects);
        this.setEditable();

        // return users choice
        return selected;
    }

    /**
     * A page sets a netobject as selected. Notifies Threads, which are waiting
     * for the selectedObject.
     */
    synchronized void setSelectedNetobject(Object netobject) {
        if (selectableObjects.contains(netobject)) {
            // store the netobject
            this.selectedNetobject = netobject;
            // erase selectOneObject flag
            this.selectOneObject = false;
            this.editormenu.update();
            // notify the waiting thread
            this.selectDialog.finish();
        }
    }

    /**
     * Displays the message with Yes, No and Cancel - option.
     */
    public int showConfirmDialog(String msg) {
        return JOptionPane.showConfirmDialog(null, msg);
    }

    //###
    //### Implementation of interface: Viewer
    //###
    /**
     * Requests this editor to show some information.
     *
     * @param msg text, that this editor displays in a dialog
     */
    public void showInformation(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    /**
     * Requests this editor to unanotate the objects.
     *
     * @param objects a vector of all objects, where anotations should be
     * removed
     */
    @Override
    public void unAnotateObjects(Vector objects) {
        unAnotateObjects((Collection<Object>) objects);
    }

    public void unAnotateObjects(Collection<Object> objects) {
        for (Object etc : objects) {
            this.referencetable.unAnnotate(etc);
        }
    }

    /**
     * Requests this editor to unEmphasize the objects.
     *
     * @param objects a vector of the objects, which should be unemphasized
     */
    @Override
    public void unEmphasizeObjects(Vector objects) {
        unEmphasizeObjects((Collection<Object>) objects);
    }

    public void unEmphasizeObjects(Collection<Object> objects) {
        Color c = null;
        for (Object obj : objects) {
            this.referencetable.emphasize(obj, c);
        }
    }

    /**
     * If there are changes in kernel.Net, the ApplicationControl calls this
     * method to inform all Applications that are registered as NetObserver.
     */
    @Override
    public void update(Observable netobject, Object actionobject) {
        Object initiator = ((ActionObject) actionobject).getInitiator();
        if (initiator != this) {
            ((ActionObject) actionobject).performAction(this, netobject);
        } else {
            //###Editor.msg("Editor.update: Eigene Aktion gekriegt: ignoriert!");
        }
    }

    public static void msg(String str) {
    }

    public void setExtensionVisible(Object netobject, String extensionId, boolean visible) {
        if (netobject instanceof String) {
            editormenu.setExtensionsVisible((String) netobject, extensionId, visible);
        } else {
            editormenu.setExtensionVisible(netobject, extensionId, visible);
        }
    }

    @Override
    public void setPosition(Object netobject, int pageId, int x, int y) {
        ReferenceTable.RTInfo info = getReferencetable().get(netobject);

        if (info != null) {
            for (ReferenceTable.SpritePageTuple spt : info.spritelist) {
                if (spt.page.getId() == pageId) {
                    spt.page.move(spt.sprite, new Point(x, y));
                    break;
                }
            }
        }
    }

    public void setOffset(Object netobject, int pageId, String extensionId, int x, int y) {
        ReferenceTable.RTInfo info = getReferencetable().get(netobject);

        if (info != null) {
            for (ReferenceTable.SpritePageTuple spt : info.spritelist) {
                if (spt.page.getId() == pageId) {
                    Sprite s = spt.sprite;
                    for (Sprite e : s.subsprites) {
                        if (e instanceof Representation
                                && ((Representation) e).getId().equals(extensionId)) {
                            Point p = new Point();
                            p.x = s.getPosition().x + x;
                            p.y = s.getPosition().y + y;
                            spt.page.move(e, p);
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void quit() {
        this.pagevector.close();
        this.graphproxy.closeGraph();
    }
} // Editor
