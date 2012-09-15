package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.exceptions.NetParseException;
import de.huberlin.informatik.pnk.kernel.*;
import java.awt.Point;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
        NetInOut implements a parser to load a .net-file used by the old
        python-pnk. It inherits InOut to be compatible with pnk file formats.
        The load method starts the parser. The save method saves a net
        using the python-pnk file format.
 */
public class NetInOut extends InOut {
    final String pytToJavaTable = "netTypeSpecifications/netTypesPytJava.table";
    /*Token constants used for nextToken calls*/
    final int NUMBER = 256,
              WORD = 257,
              LF = 258,
              NET = 259,
              NET_END = 260,
              SPECIFICATION = 261,
              PLACES = 262,
              TRANSITIONS = 263,
              ARCS = 264,
              MARKING = 265,
              ARROW = 266,
              STRING = 267,
              NE_STRING = 268,
              EDITOR_INFOS = 269,
              END_EDITOR = 270,
              PAGES = 271,
              PLACE = 272,
              TRANSITION = 273,
              ARC = 274,
              QUOTED_STRING = 275,
              EOF = 65535;

    final double GRAPH_FACTOR = 1.5;
    private int line = 1; // count parsed lines
    private Vector restoredChars; // token that does not match are restored here
    private InputStream file = null; // net to load
    private Graph theNet = null;
    private Hashtable extensionTable; // translation table for extensions (Python <-> Java)
    /**
          inOutName is set to "PNK-NET". This variable is inherited from InOut.
          It is used as a file format discription. The text is displayed in the
          file/save and file/open dialog.
     */
    public static String inOutName = "PNK-Net";
    /**
          stdFileExt is set to "net". The standard file extension of this file
          format is ".net".
     */
    public static String stdFileExt = "net";
    /**
          multipleAllowed is set to true. Saving and Loading multiple nets is allowed.
     */
    public static Boolean multipleAllowed = new Boolean(true);

    int idCounter = 1; // the saved net gets new numerical ID's
    Hashtable knownIds = null; // already translated ID's (ID -> numerical ID)

    OutputStream fileOut = null;

    /**
          Constructor creates a new NetInOut object. The application control
          object must be known. It is specified by the first parameter of
          the constructor.
     */
    public NetInOut(ApplicationControl ac) {
        super(ac);
        restoredChars = new Vector(10, 1);
        extensionTable = new Hashtable();
    }

    private void dumpHash() {
        Hashtable theHash;
        for (Enumeration hashKeys = extensionTable.keys(), hashVals = extensionTable.elements();
             hashKeys.hasMoreElements() && hashVals.hasMoreElements(); ) {
            System.err.println(hashKeys.nextElement() + ":");
            theHash = (Hashtable)hashVals.nextElement();
            for (Enumeration oldExtensions = theHash.keys(), newExtensions = theHash.elements();
                 oldExtensions.hasMoreElements() && newExtensions.hasMoreElements(); ) {
                System.err.println("  " + oldExtensions.nextElement() + " = " + newExtensions.nextElement());
            }
            System.err.println(";");
        }
    }

/*****************************************************************
************************ Start of Parser ************************
*****************************************************************/

    /*Throws NetParseException; method is used by nextToken-method*/
    private void error(String message) throws NetParseException {
        NetParseException theException = new NetParseException("Parse-Error occured on line " + line + ": " + message);

        throw theException;
    }

    /*get the next character either from restoredChars buffer or
          (if buffer is empty) from input stream*/
    private int getChar() {
        int retval = 0;

        if (restoredChars.size() > 0) {
            retval = ((Integer)restoredChars.firstElement()).intValue();
            restoredChars.removeElementAt(0);
        } else {
            try {
                retval = file.read();
            } catch (Throwable e) {
                retval = EOF;
            }
            if (retval == -1)
                retval = EOF;
        }

        return retval;
    }

    /*translate the ID of a netObject into a numerical ID
          (alpha-numerical ID's are not allowed in this file format)*/
    private String getNewId(Extendable netObject) {
        String oldId = netObject.getId(); // get ID
        String newId;

        if (knownIds == null)
            knownIds = new Hashtable();
        newId = (String)knownIds.get(oldId); // already translated?
        if (newId == null) {
            newId = Integer.toString(idCounter); // get new ID from idCounter
            knownIds.put(oldId, newId);
            idCounter++;
        }
        return newId;
    }

    private String getPnmlNodeID(String pytID) {
        String testID;
        Node testNode;

        /*Belongs the ID to a place?*/
        testID = "p" + pytID;
        testNode = theNet.getNodeById(testID);
        if (testNode != null)
            return testID;

        /*Belongs the ID to a transition?*/
        testID = "t" + pytID;
        testNode = theNet.getNodeById(testID);
        if (testNode != null)
            return testID;

        /*matching node ID could not be found*/
        return null;
    }

    /*inserts an extension-translation into a specific hashtable (one hashtable for each
          block type: PLACE, TRANSITION, ARC and NET) in extensionTable*/
    private void insertExtTab(String block, String oldExt, String newExt) {
        Hashtable blockHash;

        blockHash = (Hashtable)extensionTable.get(block);
        if (blockHash == null)
            blockHash = new Hashtable();
        blockHash.put(oldExt, newExt);
        extensionTable.put(block, blockHash);
    }

    /*translates a given extension using the extension translation table*/
    private String javaExt2Pyt(String ext, String type) {
        Hashtable hashtab = (Hashtable)extensionTable.get(type);
        String pytNameOfExt = null;
        if (hashtab != null)
            pytNameOfExt = (String)hashtab.get(ext);  // translate Extension
        if (pytNameOfExt == null || hashtab == null)
            pytNameOfExt = ext;
        return pytNameOfExt;
    }

    /**
          Loads a new net whose location is specified by 'theURL' and starts the
          parsing process. The file format of the net corresponds to the file format
          used by the pnk python implementation. This method returns a vector, that
          contains the new net at the first position.
     */
    public Vector load(URL theURL) {
        Vector v = new Vector();

        try {
            file = new FileInputStream(theURL.getFile());
        } catch (FileNotFoundException e) {
            System.err.println("Net-File not found!!!");
            return null;
        }

        try {
            parse_net(); // start parsing
            v.addElement(theNet);
        } catch (NetParseException e) { // an error occured while parsing process
            System.err.println(e.getMessage());
        }

        return v;
    }

    /*'String LF {LF}' can be used instead of a simple LF*/
    private void newline() throws NetParseException {
        nextToken(STRING, true);
        nextToken(LF, true);
        while (nextToken(LF, false) != null) {; }
    }

    /**
          Context sensitive lexer: tries to match the next token with the token-type
          specified by 'token' (-> see token constants above) and returns the string,
          if it matches. If the next token does not match the token-type, a
          NetParserException will be thrown, if 'do_error' is true. Else 'null' will
          be returned (without throwing an exception).
     */
    private String nextToken(int token, boolean do_error) throws NetParseException {
        final int ST_NUMBER0 = 0,
                  ST_NUMBER1 = 1,
                  ST_NUMBER2 = 2,
                  ST_NUMBER3 = 3,
                  ST_QUOTSTRING0 = 0,
                  ST_QUOTSTRING1 = 1;
        StringBuffer strbuf = new StringBuffer();
        String tokenValue, expectedToken = null;
        int lastbyte = 0, state = 0;

        lastbyte = getChar(); // read first char
        /*eat up white spaces*/
        while (lastbyte == ' ' || lastbyte == '\t' || lastbyte == '\r') {
            lastbyte = getChar();
        }

        /*return next sign, if requested*/
        if (token <= 255) {
            if (token == lastbyte)
                return new Character((char)token).toString();
            else {
                if (do_error)
                    error("'" + new Character((char)token).toString() + "' expected!!!");
                else {
                    restoreToken(lastbyte);
                    return null;
                }
            }
        }
        /*get expectedToken-String for keywords*/
        switch (token) {
        case NET: expectedToken = "NET"; break;
        case NET_END: expectedToken = "NET_END"; break;
        case SPECIFICATION: expectedToken = "SPECIFICATION"; break;
        case PLACES: expectedToken = "PLACES"; break;
        case TRANSITIONS: expectedToken = "TRANSITIONS"; break;
        case ARCS: expectedToken = "ARCS"; break;
        case MARKING: expectedToken = "MARKING"; break;
        case ARROW: expectedToken = "-->"; break;
        case EDITOR_INFOS: expectedToken = "EDITOR_INFOS"; break;
        case END_EDITOR: expectedToken = "END_EDITOR"; break;
        case PAGES: expectedToken = "PAGES"; break;
        case PLACE: expectedToken = "PLACE"; break;
        case TRANSITION: expectedToken = "TRANSITION"; break;
        case ARC: expectedToken = "ARC"; break;
        }

        while (true) {
            tokenValue = strbuf.toString();
            strbuf.append((char)lastbyte);
            switch (token) {
            case WORD:     /*read a word*/
                if (lastbyte == ' ' || lastbyte == '\t' || lastbyte == '\n' || lastbyte == EOF) {
                    if (tokenValue.length() == 0) {
                        if (do_error)
                            error("Word expected!!!");
                        else {
                            restoreToken(strbuf.toString());
                            return null;
                        }
                    } else {
                        restoreToken(lastbyte);
                        return tokenValue;
                    }
                }
                break;
            case NE_STRING:     /*read a non-empty string*/
                if (lastbyte == '\n' || lastbyte == EOF) {
                    if (do_error)
                        error("String expected!!!");
                    else {
                        restoreToken(strbuf.toString());
                        return null;
                    }
                } else
                    token = STRING;
                break;
            case STRING:     /*read a string*/
                if (lastbyte == '\n' || lastbyte == EOF) {
                    restoreToken(lastbyte);
                    return tokenValue;
                }
                break;
            case QUOTED_STRING:     /*read a quoted 'string'*/
                switch (state) {
                case ST_QUOTSTRING0:
                    if (lastbyte != '\'') {
                        if (do_error)
                            error("Quoted String expected!!!");
                        else {
                            restoreToken(strbuf.toString());
                            return null;
                        }
                    } else
                        state = ST_QUOTSTRING1;
                    break;
                case ST_QUOTSTRING1:
                    if (lastbyte == EOF) {
                        if (do_error)
                            error("closing ' expected!!!");
                        else {
                            restoreToken(strbuf.toString());
                            return null;
                        }
                    } else if (lastbyte == '\'') {
                        return tokenValue.substring(1);
                    }
                    break;
                }
                break;
            case NUMBER:     /*read a number*/
                switch (state) {
                case ST_NUMBER0:
                    if (Character.isDigit((char)lastbyte))
                        state = ST_NUMBER1;
                    else if (lastbyte == '-')
                        state = ST_NUMBER0;
                    else {
                        if (do_error)
                            error("Number expected!!!");
                        else {
                            restoreToken(strbuf.toString());
                            return null;
                        }
                    }
                    break;
                case ST_NUMBER1:
                    if (lastbyte == '.')
                        state = ST_NUMBER2;
                    else if (!Character.isDigit((char)lastbyte)) {
                        restoreToken(lastbyte);
                        return tokenValue;
                    }
                    break;
                case ST_NUMBER2:
                    if (Character.isDigit((char)lastbyte))
                        state = ST_NUMBER3;
                    else {
                        if (do_error)
                            error("Number expected!!!");
                        else {
                            restoreToken(strbuf.toString());
                            return null;
                        }
                    }
                    break;
                case ST_NUMBER3:
                    if (!Character.isDigit((char)lastbyte)) {
                        restoreToken(lastbyte);
                        return tokenValue;
                    }
                    break;
                }
                break;
            case NET:     /*read a specific keyword*/
            case NET_END:
            case SPECIFICATION:
            case PLACES:
            case TRANSITIONS:
            case ARCS:
            case MARKING:
            case ARROW:
            case EDITOR_INFOS:
            case END_EDITOR:
            case PAGES:
            case PLACE:
            case TRANSITION:
            case ARC:
                if (tokenValue.compareTo(expectedToken) == 0) {
                    restoreToken(lastbyte);
                    return tokenValue;
                } else if (tokenValue.length() >= expectedToken.length() || lastbyte == EOF) {
                    if (do_error)
                        error(expectedToken + " expected!!!");
                    else {
                        restoreToken(strbuf.toString());
                        return null;
                    }
                }
                break;
            case LF:     /*read a line feed*/
                if (tokenValue.compareTo("\n") == 0) {
                    line++;
                    restoreToken(lastbyte);
                    return tokenValue;
                } else if (tokenValue.length() >= 1 || lastbyte == EOF) {
                    if (do_error)
                        error("LineFeed expected!!!");
                    else {
                        restoreToken(strbuf.toString());
                        return null;
                    }
                }
                break;
            }
            lastbyte = getChar(); // next character
        }
        //return null;
    }

    /*write a line feed*/
    private void nl() throws IOException {
        fileOut.write(10);
    }

    /**
          parse an arc-block
     */
    private void parse_arcs() throws NetParseException {
        String source, target, ID, ArcInscription;
        Member Arc;

        newline();

        while (nextToken('|', false) != null) {
            source = getPnmlNodeID(nextToken(NUMBER, true)); //Source-Node
            if (source == null)
                error("Source-Node of Arc not defined before!");
            nextToken(ARROW, true);
            target = getPnmlNodeID(nextToken(NUMBER, true)); //Target-Node
            if (target == null)
                error("Target-Node of Arc not defined before!");
            ID = "a" + nextToken(NUMBER, true); //ID of Arc
            Arc = new Arc((Net)theNet, source, target, this, ID); // register new Arc!!
            newline();

            /*Read-arcInscription (String-Collection)*/
            ArcInscription = parse_StringCollection();
            nextToken(';', true);
            setExtension(Arc, "inscription", ArcInscription); //set Arc-Inscription!!!

            newline();
            parse_Extension(Arc, "ARC");
            newline();
        }
        nextToken(';', true);
        newline();
    }

    /**
          place-, transition-, arc-, and marking-blocks can be mixed
     */
    private void parse_blocks() throws NetParseException {
        nextToken('|', true);
        if (nextToken(NET_END, false) != null)
            return;

        if (nextToken(PLACES, false) != null)
            parse_places();
        else if (nextToken(TRANSITIONS, false) != null)
            parse_transitions();
        else if (nextToken(ARCS, false) != null)
            parse_arcs();
        else if (nextToken(MARKING, false) != null)
            parse_marking();
        else
            parse_globalExtension();

        parse_blocks();
    }

    /**
          graphical information of arcs
     */
    private void parse_edArcs() throws NetParseException {
        String Page;
        int pageNum;
        Arc anArc;

        newline();

        while (nextToken(';', false) == null) {
            anArc = (Arc)theNet.getEdgeById("a" + nextToken(NUMBER, true)); //the Arc-Object
            if (anArc == null)
                error("Arc was not defined before!");
            nextToken(':', true);
            newline();
            Page = nextToken(NUMBER, true); //ID of Page
            pageNum = (int)Double.parseDouble(Page);
            nextToken(NUMBER, true); //first Node-Object????
            nextToken(NUMBER, true); //second Node-Object???

            anArc.setPosition(new Point(), pageNum); // ???

            /*************************************/
            /*Drag-Point-Liste fehlt hier noch!!!*/
            /*IDofNode-Reference wofür??????     */
            /*************************************/
            newline();
            parse_objectExtension(anArc, Page, "ARC");
            nextToken('.', true);
            newline();
        }

        newline();
    }

    /**
          blocks of the editor information part can be mixed
     */
    private void parse_edBlocks() throws NetParseException {
        if (nextToken(PAGES, false) != null)
            parse_edPages();
        else if (nextToken(PLACE, false) != null)
            parse_edPlaces();
        else if (nextToken(TRANSITION, false) != null)
            parse_edTransitions();
        else if (nextToken(ARC, false) != null)
            parse_edArcs();
        else
            return;

        parse_edBlocks();
    }

    /**
          start parsing editor information
     */
    private void parse_edNet() throws NetParseException {
        if (nextToken(EDITOR_INFOS, false) != null) {
            newline();
            parse_edBlocks();
            nextToken(END_EDITOR, true);
            newline();
        }
    }

    /**
          parse page information
     */
    private void parse_edPages() throws NetParseException {
        String ID, width, height, xPos, yPos, visible;

        newline();

        while (nextToken(';', false) == null) {
            ID = nextToken(NUMBER, true); //ID of Page
            width = nextToken(NUMBER, true); //width
            nextToken('x', true);
            height = nextToken(NUMBER, true); //height
            nextToken('+', true);
            xPos = nextToken(NUMBER, true); //aboveLeftX
            nextToken('+', true);
            yPos = nextToken(NUMBER, true); //aboveLeftY

            /************************************************/
            /*View-State in PNK2 noch nicht implementiert!!!*/
            /************************************************/
            visible = nextToken('0', false);
            if (visible == null)          //viewstate
                visible = nextToken('1', true);

            /*******************************/
            /*Pages noch nicht eingefügt!!!*/
            /*******************************/

            newline();
        }

        newline();
    }

    /**
          graphical information of places
     */
    private void parse_edPlaces() throws NetParseException {
        String ID, Page, absX, absY;
        Place aPlace;

        newline();

        while (nextToken(';', false) == null) {
            ID = "p" + nextToken(NUMBER, true); //ID of Place
            aPlace = (Place)theNet.getNodeById(ID);
            if (aPlace == null)
                error("Place was not defined before!");
            nextToken(':', true);
            newline();
            Page = nextToken(NUMBER, true); //ID of Page
            absX = nextToken(NUMBER, true); //absolute X
            absY = nextToken(NUMBER, true); //absolute Y
            aPlace.setPosition(new Point((int)(Double.parseDouble(absX) * GRAPH_FACTOR),
                                         (int)(Double.parseDouble(absY) * GRAPH_FACTOR)),
                               (int)Double.parseDouble(Page));
            newline();
            parse_objectExtension(aPlace, Page, "PLACE");
            nextToken('.', true);
            newline();
        }

        newline();
    }

    /**
          graphical information of transitions
     */
    private void parse_edTransitions() throws NetParseException {
        String ID, Page, absX, absY;
        Transition aTransition;

        newline();

        while (nextToken(';', false) == null) {
            ID = "t" + nextToken(NUMBER, true); //ID of Transition
            aTransition = (Transition)theNet.getNodeById(ID);
            if (aTransition == null)
                error("Transition was not defined before!");
            nextToken(':', true);
            newline();
            Page = nextToken(NUMBER, true); //ID of Page
            absX = nextToken(NUMBER, true); //absolute X
            absY = nextToken(NUMBER, true); //absolute Y
            aTransition.setPosition(new Point((int)(Double.parseDouble(absX) * GRAPH_FACTOR),
                                              (int)(Double.parseDouble(absY) * GRAPH_FACTOR)),
                                    (int)Double.parseDouble(Page));
            newline();
            parse_objectExtension(aTransition, Page, "TRANSITION");
            nextToken('.', true);
            newline();
        }

        newline();
    }

    /**
          parses extension section of arc-, place- and transition-blocks
     */
    private void parse_Extension(Extendable netObject, String type) throws NetParseException {
        String name, nameOfExt = null, valueOfExt;
        Hashtable hashtab = (Hashtable)extensionTable.get(type);

        while (nextToken(';', false) == null) {
            nextToken('|', true);
            name = nextToken(NE_STRING, true);
            if (hashtab != null)
                nameOfExt = (String)hashtab.get(name);  // translate Extension
            if (nameOfExt == null || hashtab == null)
                nameOfExt = name;
            newline();
            valueOfExt = parse_StringCollection();
            nextToken(';', true);
            newline();
            setExtension(netObject, nameOfExt, valueOfExt);
        }
    }

    /**
          parses global extensions
     */
    private void parse_globalExtension() throws NetParseException {
        String name, nameOfExt = null, valueOfExt;
        Hashtable hashtab = (Hashtable)extensionTable.get("NET");

        name = nextToken(NE_STRING, false);
        if (hashtab != null)
            nameOfExt = (String)hashtab.get(name);  // translate Extension
        if (nameOfExt == null || hashtab == null)
            nameOfExt = name;
        newline();
        valueOfExt = parse_StringCollection();
        nextToken(';', true);
        newline();
        setExtension(theNet, nameOfExt, valueOfExt);
    }

    /**
          parse a marking-block
     */
    private void parse_marking() throws NetParseException {
        Place place;
        String marking;

        newline();

        while (nextToken('|', false) != null) {
            place = (Place)theNet.getNodeById("p" + nextToken(NUMBER, true)); //ID of Place
            if (place == null) // Place must be defined before
                error("Place was not defined before!");
            newline();

            /*initialMarking = StringCollection*/
            marking = parse_StringCollection();
            nextToken(';', true);
            setExtension(place, "initialMarking", marking); //Marking of Place
            setExtension(place, "marking", marking);

            newline();
        }
        nextToken(';', true);
        newline();
    }

    /**
          The parsing process starts with this method. It calls the
          methods parse_netSpecification, parse_blocks and parse_edNet
     */
    private void parse_net() throws NetParseException {
        String net_name;

        nextToken('|', true);
        nextToken(NET, true);
        newline();
        nextToken('|', true);
        net_name = nextToken(STRING, true); //name of net
        newline();

        parse_netSpecification();
        theNet.setName(net_name);
        theNet.setId(net_name);

        parse_blocks();

        newline();
        parse_edNet();
    }

    /**
          parses net specification and constructs new net
     */
    private void parse_netSpecification() throws NetParseException {
        String net_spec;

        nextToken('|', true);
        nextToken(SPECIFICATION, true);
        net_spec = nextToken(WORD, true); //name of Specification
        net_spec = setExtensionTablePytJava(net_spec);

        theNet = ac.getNewNet(net_spec); // get new net!!!
        if (theNet == null)
            error("Net type unknown!");
        newline();
    }

    /**
          graphical information of object extensions (e.g. initial marking position)
     */
    private void parse_objectExtension(Member member, String page, String type) throws NetParseException {
        Extension extension;
        String name, nameOfExt = null, relativX, relativY;
        Hashtable hashtab = (Hashtable)extensionTable.get(type);
        if (nextToken('{', false) != null) {
            while (nextToken('}', false) == null) {
                name = nextToken(QUOTED_STRING, true);

                /*old extension  ===>>>  new Extension!!!*/
                if (hashtab != null)
                    nameOfExt = (String)hashtab.get(name);  // translate Extension
                if (nameOfExt == null || hashtab == null)
                    nameOfExt = name;
                extension = member.getExtension(nameOfExt); //Get Extension

                nextToken(':', true);
                nextToken('(', true);
                relativX = nextToken(NUMBER, true); //relativX
                nextToken(',', true);
                relativY = nextToken(NUMBER, true); //relativY
                /*set relative position of object extension*/
                if (extension != null)
                    extension.setOffset(new Point((int)(Double.parseDouble(relativX) * GRAPH_FACTOR),
                                                  (int)(Double.parseDouble(relativY) * GRAPH_FACTOR)),
                                        (int)Double.parseDouble(page));
                nextToken(',', true);

                /************************************************/
                /*View-State in PNK2 noch nicht implementiert!!!*/
                /************************************************/
                if (nextToken('0', false) == null) //viewstate
                    nextToken('1', true);
                nextToken(')', true);
                nextToken(',', false);
            }
            newline();
        }
    }

    /**
          parse a place-block
     */
    private void parse_places() throws NetParseException {
        String Name, ID;
        Member Place;

        newline();
        while (nextToken('|', false) != null) {
            Name = nextToken(WORD, true); //name of Place
            ID = "p" + nextToken(NUMBER, true); //ID of Place
            Place = new Place((Net)theNet, Name, this, ID); // register new Place!!
            newline();
            parse_Extension(Place, "PLACE");
            newline();
        }
        nextToken(';', true);
        newline();
    }

    private String parse_StringCollection() throws NetParseException {
        String valueOfSC = "";
        while (nextToken('|', false) != null) {
            if (valueOfSC.length() > 0) valueOfSC += "\n";
            valueOfSC += nextToken(STRING, true); //concatinate String-Collection
            newline();
        }
        return valueOfSC;
    }

    /**
          parse a transition-block
     */
    private void parse_transitions() throws NetParseException {
        String Name, ID;
        Member Transition;

        newline();
        while (nextToken('|', false) != null) {
            Name = nextToken(WORD, true); //name of Transition
            ID = "t" + nextToken(NUMBER, true); //ID of Transition
            Transition = new Transition((Net)theNet, Name, this, ID); // register new Transition!!
            newline();
            parse_Extension(Transition, "TRANSITION");
            newline();
        }
        nextToken(';', true);
        newline();
    }

    private void restoreToken(int chr) {
        restoredChars.insertElementAt(new Integer(chr), 0);
    }

/***************************************************************
************************ lexer methods ************************
***************************************************************/

    /*restoreToken-methods are used by nextToken*/
    private void restoreToken(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            restoredChars.insertElementAt(new Integer(str.charAt(i)), 0);
        }
    }

    /**
          Saves the given net (theNets) in a file specified by theURL
          using the grammar of the file format used by the old pnk
          python implementation.
     */
    public void save(Vector theNets, URL theURL) {
        theNet = (Net)theNets.elementAt(0);

        try {
            fileOut = new FileOutputStream(theURL.getFile()); // open output stream
            try {
                write_net(); // write the net into the output stream
                fileOut.close(); // close output stream
            } catch (IOException e) {
                System.err.println("IOException occured while trying to save the net!!!");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot write into '" + theURL.getFile() + "'!!!");
        }
    }

    private void setExtension(Extendable netObject, String name, String value) {
        try {
            netObject.setExtension(this, name, value);
        } catch (IllegalArgumentException e) {
            System.err.println("Extension '" + name + "' not found! It will be ignored: continue loading!");
        }
    }

    /* ************************************************************************************* */
    /* ************** following methods are used to save a net as .net-File **************** */
    /* ************************************************************************************* */

    /*builds up a translation table for extensions (Java -> Python)
       (see file: netTypeSpecifications/netTypesPytJava.table)*/
    private String setExtensionTableJavaPyt(String netType) throws NetParseException {
        String oldExt, newExt, block, oldNetType, newNetType = null;

        try {
            file = new FileInputStream(pytToJavaTable);
        } catch (FileNotFoundException e) {
            System.err.println("Pyt-Java-Translation-Table not found!");
            return netType;
        }
        restoredChars = new Vector(10, 1);
        line = 1;

        /*start parsing translation file*/
        do {
            while (nextToken(LF, false) != null) {; }
            oldNetType = nextToken(WORD, false);
            if (oldNetType != null) {
                newNetType = nextToken(WORD, true);
                if (newNetType.compareTo(netType) != 0)
                    newNetType = null;
                while (nextToken(LF, false) != null) {; }

                while (nextToken(';', false) == null) {
                    block = nextToken(WORD, true); // block-name
                    while (nextToken(LF, false) != null) {; }
                    while (nextToken(';', false) == null) {
                        oldExt = nextToken(QUOTED_STRING, true); // old extension
                        newExt = nextToken(QUOTED_STRING, true); // new extension
                        if (newNetType != null)
                            insertExtTab(block, newExt, oldExt);  // insert into extensionTable
                        while (nextToken(LF, false) != null) {; }
                    }
                    while (nextToken(LF, false) != null) {; }
                }
                while (nextToken(LF, false) != null) {; }
            }
        } while (oldNetType != null && newNetType == null);

        if (oldNetType == null)
            oldNetType = netType;

        return oldNetType;
    }

    /*builds up a translation table for extensions (see file: netTypeSpecifications/
          netTypesPytJava.table)*/
    private String setExtensionTablePytJava(String netType) throws NetParseException {
        InputStream oldFIStream = file;
        Vector oldRestoredChars = restoredChars;
        int oldLines = line;
        String oldExt, newExt, block, oldNetType, newNetType = null;

        try {
            file = new FileInputStream(pytToJavaTable);
        } catch (FileNotFoundException e) {
            System.err.println("Pyt-Java-Translation-Table not found!");
            file = oldFIStream;
            return netType;
        }
        restoredChars = new Vector(10, 1);
        line = 1;

        /*start parsing translation file*/
        do {
            while (nextToken(LF, false) != null) {; }
            oldNetType = nextToken(WORD, false);
            if (oldNetType != null) {
                if (oldNetType.compareTo(netType) == 0)
                    newNetType = nextToken(WORD, true);
                else
                    nextToken(WORD, true);
                while (nextToken(LF, false) != null) {; }

                while (nextToken(';', false) == null) {
                    block = nextToken(WORD, true); // block-name
                    while (nextToken(LF, false) != null) {; }
                    while (nextToken(';', false) == null) {
                        oldExt = nextToken(QUOTED_STRING, true); // old extension
                        newExt = nextToken(QUOTED_STRING, true); // new extension
                        if (newNetType != null)
                            insertExtTab(block, oldExt, newExt);  // insert into extensionTable
                        while (nextToken(LF, false) != null) {; }
                    }
                    while (nextToken(LF, false) != null) {; }
                }
                while (nextToken(LF, false) != null) {; }
            }
        } while (oldNetType != null && newNetType == null);

        if (newNetType == null)
            newNetType = netType;

        file = oldFIStream;
        restoredChars = oldRestoredChars;
        line = oldLines;
        return newNetType;
    }

    /*write the String str into the output stream (fileOut)*/
    private void write(String str) throws IOException {
        fileOut.write(str.getBytes());
    }

    /*write the arc block*/
    private void write_arcs() throws IOException {
        write("|ARCS");
        nl();
        Vector arcs = ((Net)theNet).getArcs();
        for (int i = 0; i < arcs.size(); i++) {
            Arc arc = (Arc)arcs.get(i);
            /* write the new ID's for source, target and the arc itself */
            write("|" + getNewId(arc.getSource()) + " --> " + getNewId(arc.getTarget()) + " " + getNewId(arc));
            nl();
            /*write arc inscription*/
            write_StringCollection(arc.getExtension("inscription").toString());
            write(";");
            nl();
            /* write arc extensions */
            write_extensions(arc, "ARC");
            write(";");
            nl();
        }
        write(";");
        nl();
    }

    /*write graphical information of arcs*/
    private void write_edArcs() throws IOException {
        write("ARC");
        nl();
        Vector arcs = ((Net)theNet).getArcs();
        for (int i = 0; i < arcs.size(); i++) {
            Arc arc = (Arc)arcs.get(i);
            Vector pages = arc.getPages();
            for (int j = 0; j < pages.size(); j++) {
                int page = ((Integer)pages.get(j)).intValue(); // actual page
                write("   " + getNewId(arc) + " :"); // write new ID of the arc
                nl();
                /**********************************/
                /* dragpoint list not implemented */
                /**********************************/
                write("   " + page + " 0 0"); // numberOfSourceReference, numberOfTargetReference???
                nl();
                /* write graphical information of extensions */
                write_objectExtensions(arc, page, "ARC");
                write("   .");
                nl();
            }
        }
        write(";");
        nl();
    }

    /*write editor infos*/
    private void write_edNet() throws IOException {
        write("EDITOR_INFOS");
        nl();
        write_edPages(); // write page information
        write_edPlaces(); // write graphical information of places
        write_edTransitions(); // write graphical information of transitions
        write_edArcs(); // write graphical information of arcs
        write("END_EDITOR");
        nl();
    }

    /*write page information*/
    private void write_edPages() throws IOException {
        write("PAGES");
        nl();
        /************************************/
        /* Page information not implemented */
        /************************************/
        write(";");
        nl();
    }

    /*write graphical information of places*/
    private void write_edPlaces() throws IOException {
        write("PLACE");
        nl();
        Vector places = ((Net)theNet).getPlaces();
        for (int i = 0; i < places.size(); i++) {
            Place place = (Place)places.get(i);
            Vector pages = place.getPages();
            for (int j = 0; j < pages.size(); j++) {
                int page = ((Integer)pages.get(j)).intValue(); // actual page
                /*x- and y-coordinates on actual page*/
                double x = Math.rint(place.getPosition(page).getX() / GRAPH_FACTOR);
                double y = Math.rint(place.getPosition(page).getY() / GRAPH_FACTOR);
                write("   " + getNewId(place) + " :"); // write new ID of the place
                nl();
                write("   " + page + " " + x + " " + y); // write page, x and y
                nl();
                /* write graphical information of extensions (optional)*/
                if (!place.getExtIdToObject().isEmpty())
                    write_objectExtensions(place, page, "PLACE");
                write("   .");
                nl();
            }
        }
        write(";");
        nl();
    }

    /*write graphical information of transitions*/
    private void write_edTransitions() throws IOException {
        write("TRANSITION");
        nl();
        Vector transitions = ((Net)theNet).getTransitions();
        for (int i = 0; i < transitions.size(); i++) {
            Transition transition = (Transition)transitions.get(i);
            Vector pages = transition.getPages();
            for (int j = 0; j < pages.size(); j++) {
                int page = ((Integer)pages.get(j)).intValue(); // actual page
                /*x- and y-coordinates on actual page*/
                double x = Math.rint(transition.getPosition(page).getX() / GRAPH_FACTOR);
                double y = Math.rint(transition.getPosition(page).getY() / GRAPH_FACTOR);
                write("   " + getNewId(transition) + " :"); // write new ID of the transition
                nl();
                write("   " + page + " " + x + " " + y); // write page, x and y
                nl();
                /* write graphical information of extensions (optional)*/
                if (!transition.getExtIdToObject().isEmpty())
                    write_objectExtensions(transition, page, "TRANSITION");
                write("   .");
                nl();
            }
        }
        write(";");
        nl();
    }

    /*writes extensions of a netObject; type specifies the netObject
          (PLACE/ARC/TRANSITION/NET(for global extensions))*/
    private void write_extensions(Extendable netObject, String type) throws IOException {
        Enumeration extNames = netObject.getExtIdToObject().keys();
        while (extNames.hasMoreElements()) {
            String actExtName = (String)extNames.nextElement();
            Extension actExtension = netObject.getExtension(actExtName);
            write("|" + javaExt2Pyt(actExtName, type)); // translate and write actual extension name
            nl();
            write_StringCollection(actExtension.toString()); // its value is a string collection
            write(";");
            nl();
        }
    }

    /*write initial markings*/
    private void write_marking() throws IOException {
        write("|MARKING");
        nl();
        Vector places = ((Net)theNet).getPlaces();
        for (int i = 0; i < places.size(); i++) {
            Place place = (Place)places.get(i);
            write("|" + getNewId(place)); // write the new ID of the place
            nl();
            /*the initial marking is a string collection*/
            write_StringCollection(place.getExtension("initialMarking").toString());
            write(";");
            nl();
        }
        write(";");
        nl();
    }

    /*This method is called by the save method. It writes the net (theNet) into
          an open output stream (fileOut).*/
    private void write_net() throws IOException {
        write("|NET");
        nl();
        write("|" + theNet.getName()); // write net name
        nl();
        /* translate specification and build up hastable for extension translation */
        String spec = null;
        try {
            spec = setExtensionTableJavaPyt(ac.getNetType(theNet));
        } catch (NetParseException e) {
            e.printStackTrace();
        }
        write("|SPECIFICATION " + spec); // write net specification
        nl();
        write_extensions(theNet, "NET"); //write global extensions
        write_places(); // write place block
        write_transitions(); // write transition block
        write_arcs(); // write arc block
        write_marking(); // write initial markings
        write("|NET_END");
        nl();
        write_edNet(); // write editor infos
    }

    /* write graphical information of all extensions from member on page 'page' */
    private void write_objectExtensions(Member member, int page, String type) throws IOException {
        write("   {");
        Enumeration extNames = member.getExtIdToObject().keys(); // get all extensions
        while (extNames.hasMoreElements()) {
            String actExtName = (String)extNames.nextElement();
            Extension actExtension = member.getExtension(actExtName);
            /*relative position (x- and y-coordinates)*/
            long relX = Math.round(actExtension.getOffset(page).getX() / GRAPH_FACTOR);
            long relY = Math.round(actExtension.getOffset(page).getY() / GRAPH_FACTOR);
            /*write translated extension name, relative x and y and the view state;
                  view states not implemented yet (extensions are always visible)*/
            write("'" + javaExt2Pyt(actExtName, type) + "': (" + relX + ", " + relY + ", 1)");
            if (extNames.hasMoreElements())
                write(", ");
        }
        write("}");
        nl();
    }

    /*write the place block*/
    private void write_places() throws IOException {
        write("|PLACES");
        nl();
        Vector places = ((Net)theNet).getPlaces();
        for (int i = 0; i < places.size(); i++) {
            Place place = (Place)places.get(i);
            /* write the name of the place (replace spaces by '_') and write the new ID*/
            write("|" + place.getName().replace(' ', '_') + " " + getNewId(place));
            nl();
            /* write place extensions */
            write_extensions(place, "PLACE");
            write(";");
            nl();
        }
        write(";");
        nl();
    }

    /*write a string collection*/
    private void write_StringCollection(String str) throws IOException {
        if (str.length() == 0) { // an empty string collection
            write("|");
            nl();
            return;
        }

        int index = str.indexOf(10); // the first line feed
        while (index > 0) {
            write("|");
            write(str.substring(0, index));
            nl();
            if (index < str.length() - 1)
                str = str.substring(index + 1, str.length());
            else
                str = "";
            index = str.indexOf(10); // the next line feed
        }
        if (str.length() > 0) { // write the rest of the string collection
            write("|");
            write(str);
            nl();
        }
    }

    /*write the transition block*/
    private void write_transitions() throws IOException {
        write("|TRANSITIONS");
        nl();
        Vector transitions = ((Net)theNet).getTransitions();
        for (int i = 0; i < transitions.size(); i++) {
            Transition transition = (Transition)transitions.get(i);
            /* write the name of the transition (replace spaces by '_') and write the new ID*/
            write("|" + transition.getName().replace(' ', '_') + " " + getNewId(transition));
            nl();
            /* write transition extensions */
            write_extensions(transition, "TRANSITION");
            write(";");
            nl();
        }
        write(";");
        nl();
    }
}