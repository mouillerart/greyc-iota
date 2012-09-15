package de.huberlin.informatik.pnk.tools.base;

import java.util.*;

/**
 * The MetaNetType is superclass of all parametric Nettypes wich means that
 * they can create a dynamic net type, that is only specified
 * by certain parameters, such as "BlackToken, MultiSet" given as Vector of Strings.
 * The meaning of each parameter is declared in an extern XML configuration file
 * or somehow else. The Hashtable for the constructor contains parameters of the
 * nettype specification file for this nettype such as URL of configfile or
 * instructions how to interpret the dynamic parameters.
 */
public abstract class MetaNetType {
    /**
          Returns a hashtable, that represents a specification,
          defined by the passed argument vector.
     */
    public abstract Hashtable getSpecification (Vector parameters);

    /**
     * The constructor parses the Hashtable and
     * initializes for parametric nettypes
     */
    public MetaNetType(Hashtable config) {}

    /**
     * Return arguemts of the last net type created.
     */
    public abstract Vector getParameters ();

    /**
     * This method evokes a dialog to request the net type parameters.
     * The resulting specification is returned as Hashtable.
     * The parent parameter is the parent frame of the dialog.
     */
    public abstract Hashtable getSpecification ();
}