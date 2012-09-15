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

import java.awt.Color;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.logging.LogFactory;

/**
 * Props
 *
 * Properties for editor.
 * Default settings read from file 'config/Editor.properties'.
 * The content of this file has to look like:
 *<pre>
 * #
 * # This is a comment in the property file.
 * #
 *
 * # Set dimensions of place sprites
 * # on editor's pages
 *
 * place_width=40
 * place_height=80
 *</pre>
 *
 * Created after de.huberlin.informatik.pnk.editor.Props
 */
class Props {

    static int ANIMATION_SPEED = 1000;
    static int NODE_WIDTH = 40;
    static int NODE_HEIGHT = 41;
    static int PLACE_WIDTH = 40;
    static int PLACE_HEIGHT = 40;
    static int TRANSITION_WIDTH = 40;
    static int TRANSITION_HEIGHT = 41;
    static int PAGE_WIDTH = 700;
    static int PAGE_HEIGHT = 700;
    static Color ARC_FILL_COLOR = new Color(235, 235, 235);
    static Color ARC_BORDER_COLOR = Color.gray;
    static Color NODE_FILL_COLOR = new Color(235, 235, 235);
    static Color NODE_BORDER_COLOR = Color.gray;
    static Color PLACE_FILL_COLOR = new Color(235, 235, 235);
    static Color PLACE_BORDER_COLOR = Color.gray;
    static Color TRANSITION_FILL_COLOR = new Color(235, 235, 235);
    static Color TRANSITION_BORDER_COLOR = Color.gray;
    static Color EXTENSION_COLOR = Color.gray;
    //****************************************
    private static ResourceBundle resources;

    static {
        try {
            resources = ResourceBundle.getBundle("config.Editor", Locale.getDefault());

            try {
                NODE_WIDTH = Integer.parseInt(resources.getString("node_width"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                ANIMATION_SPEED = Integer.parseInt(resources.getString("animation_speed"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                NODE_HEIGHT = Integer.parseInt(resources.getString("node_height"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                PLACE_WIDTH = Integer.parseInt(resources.getString("place_width"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                PLACE_HEIGHT = Integer.parseInt(resources.getString("place_height"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                TRANSITION_WIDTH = Integer.parseInt(resources.getString("transition_width"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                TRANSITION_HEIGHT = Integer.parseInt(resources.getString("transition_height"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                PAGE_WIDTH = Integer.parseInt(resources.getString("page_width"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                PAGE_HEIGHT = Integer.parseInt(resources.getString("page_height"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                NODE_FILL_COLOR = Color.decode(resources.getString("node_fill_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                NODE_BORDER_COLOR = Color.decode(resources.getString("node_border_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                PLACE_FILL_COLOR = Color.decode(resources.getString("place_fill_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                PLACE_BORDER_COLOR = Color.decode(resources.getString("place_border_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                TRANSITION_FILL_COLOR = Color.decode(resources.getString("transition_fill_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                TRANSITION_BORDER_COLOR = Color.decode(resources.getString("transition_border_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                ARC_FILL_COLOR = Color.decode(resources.getString("arc_fill_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                ARC_BORDER_COLOR = Color.decode(resources.getString("arc_border_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
            try {
                EXTENSION_COLOR = Color.decode(resources.getString("extension_color"));
            } catch (MissingResourceException e) {
                /* Nothing, keep default */
            }
        } catch (MissingResourceException mre) {
            LogFactory.getLog(Props.class).fatal("Ooops! Configuration file 'config/Editor.properties' not found");
        }
    } // static
} // Props
