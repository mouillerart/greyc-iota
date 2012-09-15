package de.huberlin.informatik.pnk.app.base;

/**
 * Interface for a dialog between application and user.
 */
public interface Viewer {
    /**
     * An application requests the editor to show a text and get a user answer.
     *
     * @param infoText    the text which should be displayed in editor, as an information
     * @return            the answer of the user
     */
    public String getInformation (String infoText);
    /**
     * Application requests the editor to show some information.
     *
     * @param infoText    the text which should be displayed in editor, as an information
     */
    public void showInformation (String infoText);
} //interface Viewer