package de.huberlin.informatik.pnk.editor;

import java.awt.Color;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Props.java
 *
 * Properties for editor.
 * Default settings read from file 'config/Editor.properties'.
 * The content of this file has to look like:
 *
 * #
 * # This is a comment in the property file.
 * #
 *
 * # Set dimensions of place sprites
 * # on editor's pages
 *
 * place_width=40
 * place_height=80
 *
 *
 *
 * Created: Thu Nov 22 21:37:05 2001
 *
 * @author Alexander Gruenewald
 * @version
 */

class Props  {
    static int NODE_WIDTH = 20;

    static int NODE_HEIGHT = 20;

    static int PLACE_WIDTH = 20;

    static int PLACE_HEIGHT = 20;

    static int TRANSITION_WIDTH = 20;

    static int TRANSITION_HEIGHT = 20;

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

    static
    {
        try {
            resources =
                ResourceBundle.getBundle("config.Editor",
                                         Locale.getDefault());

            try {
                NODE_WIDTH =
                    Integer.parseInt(resources.getString("node_width"));
            } catch (MissingResourceException e) {}

            try {
                NODE_HEIGHT =
                    Integer.parseInt(resources.getString("node_height"));
            } catch (MissingResourceException e) {}

            try {
                PLACE_WIDTH =
                    Integer.parseInt(resources.getString("place_width"));
            } catch (MissingResourceException e) {}

            try {
                PLACE_HEIGHT =
                    Integer.parseInt(resources.getString("place_height"));
            } catch (MissingResourceException e) {}

            try {
                TRANSITION_WIDTH =
                    Integer.parseInt(resources.getString("transition_width"));
            } catch (MissingResourceException e) {}

            try {
                TRANSITION_HEIGHT =
                    Integer.parseInt(resources.getString("transition_height"));
            } catch (MissingResourceException e) {}

            try {
                PAGE_WIDTH =
                    Integer.parseInt(resources.getString("page_width"));
            } catch (MissingResourceException e) {}

            try {
                PAGE_HEIGHT =
                    Integer.parseInt(resources.getString("page_height"));
            } catch (MissingResourceException e) {}

            try {
                NODE_FILL_COLOR =
                    Color.decode(resources.getString("node_fill_color"));
            } catch (MissingResourceException e) {}

            try {
                NODE_BORDER_COLOR =
                    Color.decode(resources.getString("node_border_color"));
            } catch (MissingResourceException e) {}

            try {
                PLACE_FILL_COLOR =
                    Color.decode(resources.getString("place_fill_color"));
            } catch (MissingResourceException e) {}

            try {
                PLACE_BORDER_COLOR =
                    Color.decode(resources.getString("place_border_color"));
            } catch (MissingResourceException e) {}

            try {
                TRANSITION_FILL_COLOR =
                    Color.decode(resources.getString("transition_fill_color"));
            } catch (MissingResourceException e) {}

            try {
                TRANSITION_BORDER_COLOR =
                    Color.decode(resources.getString("transition_border_color"));
            } catch (MissingResourceException e) {}

            try {
                ARC_FILL_COLOR =
                    Color.decode(resources.getString("arc_fill_color"));
            } catch (MissingResourceException e) {}

            try {
                ARC_BORDER_COLOR =
                    Color.decode(resources.getString("arc_border_color"));
            } catch (MissingResourceException e) {}

            try {
                EXTENSION_COLOR =
                    Color.decode(resources.getString("extension_color"));
            } catch (MissingResourceException e) {}
        } catch (MissingResourceException mre) {
            System.err.println("Ooops! Configuration file 'config/Editor.properties' not found");
            System.err.flush();
        }
    } // static
} // Properties
