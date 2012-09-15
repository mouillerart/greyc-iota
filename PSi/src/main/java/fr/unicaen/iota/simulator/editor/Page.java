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

import de.huberlin.informatik.pnk.app.base.MetaJFrame;
import fr.unicaen.iota.simulator.util.Config;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Page
 *
 * A page is a frame for drawing parts of a graph.
 * Nodes, Places, Transitions, Edges, Arcs, Extensions
 * and Annotations are painted on it. You can move, edit,
 * join, split or delete them.
 *
 * Created after de.huberlin.informatik.pnk.editor.Page
 */
class Page extends JPanel
        implements MouseListener, MouseMotionListener, WindowListener, ItemListener, Printable {

    private static final Log log = LogFactory.getLog(Page.class);
    
    /**
     * An id, given from pagevector.
     */
    private int id;
    /**
     * Editor of this page.
     */
    private Editor editor;
    /**
     * The spritevector, containing all
     * sprites of this page.
     */
    private SpriteVector spritevector = new SpriteVector();
    /**
     * CheckboxItem for editormenu. To hide this page or set it visible.
     */
    JCheckBoxMenuItem page_ckb;
    /**
     * JFrame of this page. Used to set page visible or hide it.
     */
    protected MetaJFrame frame;
    /**
     * Zoom
     */
    private double zoom = 1;
    /**
     * place this page in a scrollpane
     */
    private JScrollPane scrollpane;
    private ViewPane viewpane;
    /**
     * TextField to give messages to user.
     */
    private JTextField textField;
    /**
     * Translate all sprite positions. There is a non trival problem if we load sprites
     * that have negative positions, or the user moves them to locations with negative x or y values.
     * Translate all sprites so that the negative values are positive and all sprites can be painted on this page.
     */
    Point translation = new Point(0, 0);
    int grid_x = 25;
    int grid_y = 25;

    /**
     * Class constructor
     * Creates a new page.
     */
    protected Page(int id, Editor editor) {
        super();
        this.id = id;
        this.editor = editor;
        // set size of canvas
        this.setPreferredSize(new Dimension(Props.PAGE_WIDTH, Props.PAGE_HEIGHT));
        this.setBackground(Color.white);

        // init pageCheckboxItem
        this.page_ckb = new JCheckBoxMenuItem("page " + id);
        this.page_ckb.addItemListener(this);

        // init panel and frame
        this.scrollpane = new JScrollPane(this);
        //	this.scrollpane.setSize(new Dimension(615,515));
        //this.scrollpane.setPreferredSize(new Dimension(Props.PAGE_WIDTH, Props.PAGE_HEIGHT));
        // ########## Pending: Das ist noch nicht ganz sauber...
        this.frame = new MetaJFrame(editor,
                editor.applicationControl.getAppNameAndInstanceKey(editor) + " Page " + id);
        this.frame.getContentPane().setLayout(new BorderLayout());
        this.frame.getContentPane().add(scrollpane, BorderLayout.CENTER);
        this.frame.setSize(new Dimension(Props.PAGE_WIDTH, Props.PAGE_HEIGHT));
        // if window was resized check the geometry of this page
        this.frame.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent c) {
                checkGeometry();
            }
        });
        // init textField for messages to user
        boolean editable = false;
        this.textField = new JTextField();
        // not editable
        this.textField.setEditable(editable);
        //this.textField.setBorder(new EmptyBorder(0,0,0,0));

        //this.frame.getContentPane().add(this.textField,BorderLayout.SOUTH);
        this.displayText(null); // display defaultText in textField

        // init eventlisteners
        PageMouseListener mouselistener = new PageMouseListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(mouselistener);
        this.addMouseMotionListener(mouselistener);

        // show frame
        this.frame.addWindowListener(this);
    }

    protected void open() {
        Dimension dim = this.getSize();
        if (dim.equals(new Dimension(0, 0))) {
            dim = new Dimension(Props.PAGE_WIDTH, Props.PAGE_HEIGHT);
            this.setSize(dim);
            this.scrollpane.setPreferredSize(dim);
        }
        this.setPreferredSize(dim);
        this.frame.setLocationRelativeTo(null);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    /**
     * Adds a new sprite to page. The sprites position will not translated.
     * If translation wanted, do it before adding sprite to this page.
     * @param sprite sprite to add
     */
    protected void add(Sprite sprite) {
        // check if sprite forces update of translation values
        Point pos = sprite.getPosition();
        Dimension size = sprite.getSize();
        this.spritevector.add(sprite);
        this.sort();
        this.checkGeometry();
        this.update();
    }

    protected void checkGeometry() {
        int minX, minY, maxX, maxY;
        minX = minY = Integer.MAX_VALUE;
        maxX = maxY = Integer.MIN_VALUE;

        for (Sprite sprite : this.spritevector) {
            if (!sprite.getVisible()) {
                continue;
            }
            Point sprite_pos = sprite.getBounds().getLocation(); //getPosition();
            Rectangle sprite_size = sprite.getBounds();

            // set minX and minY
            int x = sprite_pos.x; // - sprite_size.width;
            int y = sprite_pos.y; // - sprite_size.height;

            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
                // set maxX and maxY
            }
            x = sprite_pos.x + sprite_size.width;
            y = sprite_pos.y + sprite_size.height;

            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        if (minX == Integer.MAX_VALUE
                && minY == Integer.MAX_VALUE
                && maxY == Integer.MIN_VALUE
                && maxX == Integer.MIN_VALUE) {
            minY = minX = 0;
            maxX = Props.PAGE_WIDTH;
            maxY = Props.PLACE_HEIGHT;
        }

        // calculate pagewidth and pageheight

        int page_width = maxX - minX;
        int page_height = maxY - minY;

        // set width and height for this page
        if (page_width < Props.PAGE_WIDTH) {
            page_width = Props.PAGE_WIDTH;
        }
        if (page_height < Props.PAGE_HEIGHT) {
            page_height = Props.PAGE_HEIGHT;
        }
        this.setSize(new Dimension(page_width,
                page_height));
        this.setPreferredSize(new Dimension(page_width,
                page_height));

        // now change the value of translation faktor
        // of this page

        Point old_translation = new Point(this.translation);

        this.translation.x = -minX;
        this.translation.y = -minY;

        Point viewposition = new Point(this.scrollpane.getViewport().getViewPosition());
        viewposition.translate(this.translation.x - old_translation.x,
                this.translation.y - old_translation.y);

        this.scrollpane.getViewport().setViewPosition(viewposition);
        this.scrollpane.repaint();
    }

    /*
     * Closes this page. Removes CheckBoxMenuItem
     * from editor menu. Remove from pagevector of editor.
     */
    protected void close() {
        // Close frame
        this.frame.dispose();
        // remove checkboxitem
        JMenu pagemenu = this.editor.getEditormenu().pagemenu;
        pagemenu.remove(this.page_ckb);
        // remove from pagevector
        PageVector pv = this.editor.getPagevector();
        pv.closePage(this);
        // done
    }

    /**
     * Displays the text in pages textField.
     * @param text the text that should be shown if text == null so display defaultText
     */
    protected void displayText(String text) {
        if (text == null) {
            // compose a default text
            String mode1, mode2;
            EditorMenu em = this.editor.getEditormenu();
            if (em.isInMode(EditorMenu.PLACE)) {
                mode1 = "create places";
            } else if (em.isInMode(EditorMenu.TRAN)) {
                mode1 = "create transitions";
            } else {
                mode1 = "create nodes";
            }
            if (em.isInMode(EditorMenu.ARC)) {
                mode2 = "create arcs";
            } else if (em.isInMode(EditorMenu.EDIT)) {
                mode2 = "edit objects";
            } else if (em.isInMode(EditorMenu.SPLIT)) {
                mode2 = "split objects";
            } else if (em.isInMode(EditorMenu.JOIN)) {
                mode2 = "join objects";
            } else if (em.isInMode(EditorMenu.SELECT)) {
                mode2 = "select objects";
            } else if (em.isInMode(EditorMenu.DELETE)) {
                mode2 = "delete objects";
            } else {
                mode2 = "create edges";
            }
            text = mode1 + " / " + mode2;
        }
        this.textField.setText(text);
    }

    /**
     * Finds a sprite with location p.
     * @param p point that is inside sprite
     */
    protected Sprite find(Point p) {
        // make a copy of point and respect translation on this page
        Point copy = new Point(p);
        copy.translate(-this.translation.x, -this.translation.y);
        return this.spritevector.get(copy);
    }

    /**
     * Get the value of editor.
     * @return Value of editor.
     */
    protected Editor getEditor() {
        return editor;
    }

    JScrollPane getScrollpane() {
        return scrollpane;
    }

    protected SpriteVector getSpritevector() {
        return this.spritevector;
    }

    int getId() {
        return id;
    }

    /**
     * Make default settings on graphiccontext,
     * like stroke and rendering hints.
     * @param g
     */
    private void initGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //Erase old drawings
        Rectangle r = g.getClipBounds();
        g2.setPaint(this.getBackground());
        g2.fillRect(r.x, r.y, r.width, r.height);

        if (Config.grid) {
            g2.setPaint(Color.gray);

            int x = this.grid_x;
            int y = this.grid_y;

            g2.setStroke(new BasicStroke(0.05f));
            int i_tmp = (int) ((r.x) / x) * x + translation.x % x;
            int j_tmp = (int) ((r.y) / y) * y + translation.y % y;
            //Editor.msg("IJ: "+i_tmp+" "+j_tmp);

            for (int j = j_tmp; j < (r.y + r.height); j = j + y) {
                for (int i = i_tmp; i < (r.x + r.width); i = i + x) {
                    //Editor.msg(" DOT x: "+i+ " y: "+j);
                    g2.drawLine(i - 2, j, i + 2, j);
                    g2.drawLine(i, j - 2, i, j + 2);
                }
            }
            g2.setStroke(new BasicStroke(1.3f));
        }
        g2.scale(zoom, zoom);
        // translate graphics to make all objects visible
        g2.translate(this.translation.x, this.translation.y);
    }

    /**
     * Listens to page_checkboxmenuitem in editormenu.
     * if its state equals true, set this page visible hide it otherwise.
     */
    @Override
    public void itemStateChanged(ItemEvent ie) {
        JCheckBoxMenuItem ckb = (JCheckBoxMenuItem) ie.getItemSelectable();
        Editor.msg("ITEM STATE CHANGED");
        boolean show = ckb.getState();
        if (show) {
            this.setVisible(true);
            this.frame.setVisible(true);
            Editor.msg("frame show()");
        } else {
            this.setVisible(false);
            this.frame.setVisible(false);
            Editor.msg("frame hide()");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Draws a selection rectangle.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Moves a sprite to specified location.
     * @param sprite sprite to move
     * @param p new location for sprite
     */
    protected void move(Sprite sprite, Point p) {
        // respect translation on this page
        Point copy = new Point(p);
        copy.translate(-this.translation.x, -this.translation.y);
        sprite.setPosition(copy);
        this.update();
    }

    /**
     * Paint all sprites inside clipbounds of graphiccontext.
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        this.initGraphics(g);
        this.spritevector.paint(g);
    }

    /**
     * Requests a printJob which starts printing this page.
     */
    protected void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (Exception ex) {
                log.error(null, ex);
            }
        }
    }

    /**
     * Draws this page in graphics of a printjob
     */
    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        if (pi >= 1) {
            return Printable.NO_SUCH_PAGE;
            //berechnen des zu druckenden bereiches
        }
        Rectangle r = this.getBounds();
        Editor.msg(" CLIP BOUNDS: x: " + r.x + " y: " + r.y + " width: " + r.width + " height: " + r.height);

        // scaling graphics to get all sprites on page
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(0.05f));
        g2.setPaint(Color.black);
        double x = pf.getImageableWidth() / r.width;
        double y = pf.getImageableHeight() / r.height;
        //g2.translate(pf.getImageableX() + x*r.x, pf.getImageableY() + y*r.y);
        g2.translate(pf.getImageableX(), pf.getImageableY());

        if (x > y) {
            x = y;
        }
        g2.scale(x, x);

        // set backgroundColor to white, for printing on paper
        Color bg = this.getBackground();
        this.setBackground(Color.white);
        // printing the sprites ...
        this.printSprites(g);
        // set the original background
        this.setBackground(bg);

        return Printable.PAGE_EXISTS;
    }

    /**
     * print all sprites in GraphicContext
     */
    private void printSprites(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //Erase old drawings
        Rectangle r = g2.getClipBounds();
        Editor.msg("g2 CLIP BOUNDS: x: " + r.x + " y: " + r.y + " w: " + r.width + " h: " + r.height);

        g2.setStroke(new BasicStroke(1.0f));
        // translate graphics to make all objects visible
        g2.translate(this.translation.x, this.translation.y);

        this.spritevector.print(g2);
    }

    /**
     * Removes a sprite and its  subsprites from this page,
     * using the sprites delete() methode.
     * Then repaints this page.
     * @param sprite sprite to erase on this page.
     */
    protected void remove(Sprite sprite) {
        Rectangle r = this.removeWithSubsprites(sprite);
        // respect translation of this page
        r.translate(this.translation.x, this.translation.y);
        // repaint region
        this.repaint(r);
    }

    /**
     * @param sprite to delete with all its subsprites.
     * @return area that needs to be repainted
     */
    private Rectangle removeWithSubsprites(Sprite sprite) {
        Rectangle spritebounds = sprite.getBounds();

        for (Sprite s : sprite.subsprites) {
            Rectangle subbounds = this.removeWithSubsprites(s);
            if (spritebounds == null) {
                spritebounds = subbounds;
            } else {
                spritebounds = spritebounds.union(subbounds);
            }
        }
        this.spritevector.remove(sprite);
        sprite.delete();
        return spritebounds;
    }

    /**
     * Set the value of editor.
     * @param v  Value to assign to editor.
     */
    protected void setEditor(Editor v) {
        this.editor = v;
    }

    /**
     * If there are changes on page
     * (create, move, resize an object) , call this
     * update method. It requests the area that
     * needs to be repainted and starts repainting
     * this page.
     */
    protected void update() {
        Rectangle r = this.spritevector.getUpdatearea();
        if (r != null) {
            // respect translation on this page
            r.translate(this.translation.x, this.translation.y);
            // now we can repaint region
            this.repaint(r);
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    /**
     * Sets state of page_checkboxmenuitem in editormenu
     * to false.
     */
    @Override
    public void windowClosing(WindowEvent e) {
        this.page_ckb.removeItemListener(this);
        this.page_ckb.setState(false);
        this.page_ckb.addItemListener(this);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Sets state of page_checkboxmenuitem in editormenu to true.
     */
    @Override
    public void windowOpened(WindowEvent e) {
        this.page_ckb.removeItemListener(this);
        this.page_ckb.setState(true);
        this.page_ckb.addItemListener(this);
    }

    public double getZoom() {
        return zoom;
    }

    public void zoom(double zoom) {
        this.zoom = zoom;
        this.repaint();
    }

    void sort() {
        this.spritevector.sort();
    }
} // Page
