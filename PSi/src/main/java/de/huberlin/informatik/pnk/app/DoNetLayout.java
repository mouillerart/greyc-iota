package de.huberlin.informatik.pnk.app;

import java.awt.*;
import java.util.*;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * DoNetLayout.java
 *
 * Prototyp Application zum automatischen Zeichnen
 * von Graphen. Ist allerdings noch nicht besonders gut
 * geraten. Probleme insbesondere bei sehr grossen Graphen.
 * Lange Rechenzeit.
 *
 * Idee des Algorithmus:
 *
 * Elektrisch negativ geladene Teilchen sind unter Umstaenden
 * durch Federn verbunden, welche eine bestimmte Federlaenge
 * bevorzugen. Die Teilchen stossen sich untereinander ab.
 * Die Federn ziehen Teilchen zueinander hin oder stossen
 * bei zu geringem Abstand die Teilchen ab.
 *
 *     OOO      <--  OOO    OOO
 *     OOOmmmmmmmmmmmOOOmmmmOOO --->
 *     OOO           OOO    OOO
 *
 * Created: Wed Jan 24 08:51:50 2001
 *
 * @author Alexander Gruenewald
 * @version
 */
public class DoNetLayout
extends SimpleApplication {
    private static int PAGE = 1;

    private static double K1 = 80;
    private static double K2 = 10;
    private static double K3 = 6000000;

    private Vector extendables = new Vector();

    public static String staticAppName =
        "DoNetLayout";

    public static boolean startAsThread =
        true;

    // When ApplicationControl quits
    // this application, then this flag
    // will be set to false and the loop
    // within the run() method break's.
    private boolean letrun = true;

    public DoNetLayout(ApplicationControl ac) {
        super(ac);
    }

    public void run() {
        // Compose list of all extendables and edges

        Net net = (Net) this.net;

        this.applicationControl.saveNet(net);

        extendables.addAll(net.getPlaces());
        extendables.addAll(net.getTransitions());
        //    extendables.addAll(net.getArcs());

        /*
           // Wir platzieren die knoten zufaellig,
           // falls sie sich auf einer Null Position befinden.

           for(int i=0; i<extendables.size(); i++) {
           Extendable extendable = (Extendable) extendables.get(i);
           Point p = p(extendable);
           if(p.x == 0 && p.y == 0) {
            int x = (int) (300 * Math.random() + 150);
            int y = (int) (300 * Math.random() + 150);
            set(extendable,x,y);
           }
           }
         */

        int j = 0;

        Hashtable h = new Hashtable(extendables.size());

        // Until user quits this application ...

        while (letrun) {
            // Berechnen die Kraft f(u), die
            // auf den Punkt u wirkt und speichern
            // diese in hashtable um schnell darauf
            // zugreifen zu koennen.

            for (int i = 0; i < extendables.size(); i++) {
                Extendable u = (Extendable)extendables.get(i);
                h.put(u, f(u));
            }

            // Die Kraefte zu den einzelnen Punkten
            // sind berechnet, nun verschieben wir
            // die Punkte um ein EPSYLON in Richtung
            // der wirkenden Kraft.

            Enumeration e = h.keys();
            while (e.hasMoreElements()) {
                Extendable u = (Extendable)e.nextElement();
                Point pu = p(u);
                Point fu = (Point)h.get(u);

                set(u,
                    pu.x + epsylon(fu.x),
                    pu.y + epsylon(fu.y));
            }

            h.clear();

            normEdges();

            try {
                Thread.sleep(250);
            } catch (Exception ecp) {}

            if (j++ > 200) {
                debug("aaaaaaaaaa " + j);
                break;
            }
        }
    }

    private void normEdges() {
        Vector edges = ((Net)net).getArcs();
        for (int i = 0; i < edges.size(); i++) {
            Edge e = (Edge)edges.get(i);
            Point src = p(e.getSource());
            Point trg = p(e.getTarget());

            set(e, (int)(src.x + trg.x) / 2, (int)(src.y + trg.y) / 2);
        }
    }

    private int epsylon(int x) {
        if (x > 200) return 2;
        if (x > 0) return 1;
        if (x < -200) return -2;
        if (x < 0) return -1;
        return 0;
    }

    // position eines knoten setzen
    private void set(Extendable u, int x, int y) {
        (new SetPositionAction(applicationControl,
                               (Net)net,
                               this, u, PAGE,
                               x, y)).invokeAction();
        u.setPosition(new Point(x, y), PAGE);
    }

    private Point p(Extendable u) {
        return u.getPosition(PAGE);
    }

    private void debug(String msg) {
        boolean debug = false;
        if (debug)
            System.out.println(msg);
    }

    // force
    // berechnet die Kraft auf einen knoten
    // als summe der elektr. abstossungskraft
    // und der federkraft.
    private Point f(Extendable u) {
        double x = 0, y = 0;
        Vector edges;
        Extendable v;
        Point z, pv, pu = p(u);
        Point middle = new Point(300, 300);

        if (u instanceof Node) {
            edges = ((Node)u).getIncomingEdges();
            for (int i = 0; i < edges.size(); i++) {
                pv = p(((Edge)edges.get(i)).getSource());
                z = e(pu, pv);
                x += z.x;
                y += z.y;
            }

            edges = ((Node)u).getOutgoingEdges();
            for (int i = 0; i < edges.size(); i++) {
                pv = p(((Edge)edges.get(i)).getTarget());
                z = e(pu, pv);
                x += z.x;
                y += z.y;
            }
        } else {
            //u instanceof Edge

            Point src = p(((Edge)u).getSource());
            Point trg = p(((Edge)u).getTarget());

            z = e(pu, src);
            x += z.x;
            y += z.y;

            z = e(pu, trg);
            x += z.x;
            y += z.y;
        }

        // Anziehende Wirkung des Zentrums
        z = e(pu, middle);
        x += z.x;
        y += z.y;

        debug(" e --> x:" + x + " y:" + y);

        for (int i = 0; i < extendables.size(); i++) {
            v = (Extendable)extendables.get(i);
            if (u == v)
                continue;
            z = g(pu, p(v));

            x += z.x;
            y += z.y;
        }

        debug(" f --> x:" + x + " y:" + y);

        return new Point((int)x, (int)y);
    }

    // elasticity force
    // federkraft, ist proportional zum
    // abstand zur defaultlaenge der feder (Nullenergieposition)
    private Point e(Point pu, Point pv) {
        double d = d(pu, pv);

        d = (Math.abs(d) < 1) ? 10 : d;

        double x = K2 * (d - K1) * (pv.x - pu.x) / d;
        double y = K2 * (d - K1) * (pv.y - pu.y) / d;

        return new Point((int)x, (int)y);
    }

    // push force, inverted gravity
    // electrische Abstossung zwischen gleich
    // geladenen teilchen.
    private Point g(Point pu, Point pv) {
        double d = d(pu, pv);

        // falls die punkte direkt uebereinander liegen
        if (Math.abs(d) < 1) {
            d = 10;
            pu.x += 10 * (Math.random() - 0.5);
            pu.y += 10 * (Math.random() - 0.5);
        }

        double x = K3 * (pu.x - pv.x) / (d * d * d);
        double y = K3 * (pu.y - pv.y) / (d * d * d);

        return new Point((int)x, (int)y);
    }

    // distance
    // abstand zwischen zwei punkten
    private double d(Point pu, Point pv) {
        int
            dx = pv.x - pu.x,
            dy = pv.y - pu.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void quit() {
        letrun = false;
    }
} // DoNetLayout

