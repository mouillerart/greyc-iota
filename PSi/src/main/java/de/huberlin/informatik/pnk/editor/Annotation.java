package de.huberlin.informatik.pnk.editor;

import java.awt.*;

/**
 * Annotation.java
 *
 * Represents a Label that annotates an object on an editors page.
 * For example an application can request the editor to annotate an object
 * for some reason.
 *
 * Created: Mon Jan  8 15:58:13 2001
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

public class Annotation extends Label {
    public Annotation(Sprite parent, Point position, Dimension size, FontMetrics fm, String id, String value) {
        super(parent, position, size, fm, id, value);
    }
} // Annotation
