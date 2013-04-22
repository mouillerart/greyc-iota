package de.huberlin.informatik.pnk.exceptions;

/**
        The Exception-Class NetParseException is thrown by all parser methods
        in de.hu-berlin.informatik.pnk.appControl.NetInOut.java
 */
public class NetParseException extends RuntimeException {
    public NetParseException(String errmsg) {
        super(errmsg);
    }
}
