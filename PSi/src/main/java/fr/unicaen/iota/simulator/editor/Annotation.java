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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;

/**
 * Annotation
 *
 * Represents a Label that annotates an object on an editors page.
 * For example an application can request the editor to annotate an object
 * for some reason.
 *
 * Copied from de.huberlin.informatik.pnk.editor.Annotation
 */
class Annotation extends Representation {

    Annotation(Sprite parent, Point position, Dimension size, FontMetrics fm, String id, String value, Page p, String imagePath) {
        super(parent, position, size, fm, id, value, p, imagePath);
    }
} // Annotation
