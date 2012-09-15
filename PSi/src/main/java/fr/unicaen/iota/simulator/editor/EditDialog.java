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

import fr.unicaen.iota.simulator.util.EPCUtilities;
import fr.unicaen.iota.simulator.util.EPCUtilities.InvalidFormatException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * EditDialog
 *
 * Displays a dialog, to edit extensions of a netobject.
 *
 * Created after de.huberlin.informatik.pnk.editor.EditDialog
 */
class EditDialog extends JDialog implements ActionListener {

    private Editor editor;
    private MemberSprite sprite;
    /**
     * Is this an modal dialog?
     */
    private static boolean modal = false;
    /**
     * Holds the JTextAreas for evaluation Store (JTextArea extensionValue ->
     * String extensionId) tupel
     */
    private Map<JTextArea, String> textAreaToId = new HashMap<JTextArea, String>();
    private Page page;

    protected EditDialog(Editor e, MemberSprite s) {
        this.sprite = s;
        this.editor = e;
        this.page = null;
        this.init();
    }

    /**
     * Creates a dialog, so that extendables can be edited.
     *
     * @param page the page where the extendable lives
     * @param sprite the sprite of the extendable choosen by user
     */
    protected EditDialog(Page page, MemberSprite sprite) {
        super(page.getEditor().getEditorwindow(), "Edit Dialog", modal);
        this.setSize(850, 700);
        this.editor = page.getEditor();
        this.page = page;
        this.sprite = sprite;

        //get extensions :

        GraphProxy graph = editor.getGraphproxy();
        Object netobject = sprite.getNetobject();
        extensions = graph.getExtensionIdToValue(netobject);

        // create menu
        this.init();

        // set Location
        JFrame frame = page.frame;
        this.setLocationRelativeTo(frame);
        this.setVisible(true);
    }

    /**
     * Listening if Ok or Cancel -Button was pressed.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("OK".equals(cmd)) {
            this.ok();
        } else if ("Cancel".equals(cmd)) {
            this.cancel();
        }
    }

    /**
     * Cancel-Button was pressed in Dialog.
     */
    private void cancel() {
        if (deactivatorModified) {
            check("epcdeactivator");
        }
        if (generatorModified) {
            check("epcgenerator");
        }
        if (pipeCbModified) {
            check("pipe");
        }
        this.dispose();
    }

    /*
     * Initialises TextAreas for editing Extensions of netobjects.
     */
    JCheckBox deactivator;
    JCheckBox generator;
    JCheckBox pipeCb;
    JTextField epcBase;
    JTextField epcNumber;
    JTextField pipeServerAddress;
    JTextField pipeServerPort;
    JTextField pipePassword;
    JRadioButton useMarkingOnGenerator;
    JRadioButton useKeyGenOnGenerator;
    private JRadioButton radio1;
    private JRadioButton radio2;
    private JTextField pipeId;
    boolean deactivatorModified = false;
    boolean generatorModified = false;
    boolean pipeCbModified = false;
    boolean generatorIsVisible = false;
    boolean deactivatorIsVisible = false;
    boolean pipeIsVisible = false;
    JLabel generatorStatus;
    JLabel pipeStatus;
    JLabel deactivatorStatus;
    Map<String, String> extensions;
    boolean generatorInitialized = false;
    boolean pipeInitialized = false;

    private void init() {
        generatorStatus = new JLabel("inactif");
        generatorStatus.setForeground(Color.red);
        pipeStatus = new JLabel("inactif");
        pipeStatus.setForeground(Color.red);
        deactivatorStatus = new JLabel("inactif");
        deactivatorStatus.setForeground(Color.red);
        // Get the hashtable of extensions
        Object netobject = this.sprite.getNetobject();
        GraphProxy graph = this.editor.getGraphproxy();
        Map<String, String> exts = graph.getExtensionIdToValue(netobject);

        //generate panels
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
        JTabbedPane extensionPanel = createExtentionPanel(exts);
        JPanel extendedExtensionPanel = new JPanel();
        extendedExtensionPanel.setLayout(new FlowLayout());
        if (exts.get("epcgenerator") != null) {
            JPanel generatorPane = createGeneratorComponent(); //createGeneratorPanel(extensions);
            extendedExtensionPanel.add(generatorPane);
        }
        extendedExtensionPanel.add(Box.createRigidArea(new Dimension(50, 10)));
        if (exts.get("pipe") != null) {
            JPanel pipePane = createPipeComponent(); //createPipePanel(extensions);
            extendedExtensionPanel.add(pipePane);
        }
        extendedExtensionPanel.add(Box.createRigidArea(new Dimension(50, 10)));
        if (exts.get("epcdeactivator") != null) {
            JPanel deactivatorPane = createDeactivatorPanel(exts);
            extendedExtensionPanel.add(deactivatorPane);
        }
        southPanel.add(extendedExtensionPanel);
        southPanel.add(createControlPanel());

        // init main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(extensionPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // add main panel in window
        this.setContentPane(mainPanel);
        //this.pack();
    }

    private boolean isPipe(String value) {
        String[] tab = value.split("\n");
        for (String s : tab) {
            String[] line = s.split("=");
            if (line[0].trim().equals("isPipe")) {
                return Boolean.parseBoolean(line[1].trim());
            }
        }
        return false;
    }

    private boolean isCkecked(String id) {
        Object netobject = this.sprite.getNetobject();
        ReferenceTable rtable = this.editor.getReferencetable();
        return rtable.isVisible(netobject, id);
    }

    /**
     * Ok-Button was pressed in Dialog.
     */
    private void ok() {
        // Get a reference of netobject that the sprite represents
        Object netobject = this.sprite.getNetobject();
        // change extensions in net
        GraphProxy graph = this.editor.getGraphproxy();
        for (Map.Entry<JTextArea, String> idval : textAreaToId.entrySet()) {
            // extract values of extensions in textAreas
            String id = idval.getValue();
            String value = idval.getKey().getText();
            ReferenceTable rtable = this.editor.getReferencetable();
            rtable.changeExtension(netobject, id, value);
            graph.changeExtension(netobject, id, value);
            this.dispose();
        }
        if (pipeCb != null) {
            if (pipeCb.isSelected()) {
                graph.changeExtension(netobject, "pipe", "isPipe=true" + "\n" + "address=" + pipeServerAddress.getText() + "\n" + "password=" + pipePassword.getText() + "\n" + "pipeId=" + pipeId.getText() + "\n" + "type=" + String.valueOf(radio2.isSelected() ? "reception" : "expedition"));
            } else {
                graph.changeExtension(netobject, "pipe", "isPipe=false" + "\n" + "address=" + pipeServerAddress.getText() + "\n" + "password=" + pipePassword.getText() + "\n" + "pipeId=" + pipeId.getText() + "\n" + "type=" + String.valueOf(radio2.isSelected() ? "reception" : "expedition"));
            }
        }
        if (generator != null) {
            if (generator.isSelected()) {
                graph.changeExtension(netobject, "epcgenerator", epcBase.getText() + "%" + epcNumber.getText() + "%" + String.valueOf(useKeyGenOnGenerator.isSelected()));
            } else {
                graph.changeExtension(netobject, "epcgenerator", "");
            }
        }
        if (deactivator != null) {
            if (deactivator.isSelected()) {
                graph.changeExtension(netobject, "epcdeactivator", true + "");
            } else {
                graph.changeExtension(netobject, "epcdeactivator", false + "");
            }
        }
    }

    public void check(String title) {
        Object netobject = this.sprite.getNetobject();
        ReferenceTable rtable = this.editor.getReferencetable();
        rtable.switchExtensionVisibility(netobject, title);
    }

    private JTabbedPane createExtentionPanel(Map<String, String> extensions) {
        // init the edit panel for extensions
        JTabbedPane extensionPanel = new JTabbedPane();
        int i = 0;
        int eventTab = 0;
        for (Map.Entry<String, String> idval : extensions.entrySet()) {
            String id = idval.getKey();
            if (id.equals("epcgenerator")) {
                continue;
            }
            if (id.equals("epcdeactivator")) {
                continue;
            }
            if (id.equals("pipe")) {
                continue;
            }
            if (id.equals("event")) {
                eventTab = i;
            }
            JPanel panel = new JPanel();
            String value = idval.getValue();
            JTextArea value_text = new JTextArea(value);
            JCheckBox jCheckBox = new JCheckBox();
            jCheckBox.setOpaque(false);
            if (isCkecked(id)) {
                jCheckBox.setSelected(true);
            }
            JScrollPane scrollpane = new JScrollPane();
            scrollpane.setViewportView(value_text);
            extensionPanel.addTab(id, null, scrollpane, id);
            //extensionPanel.setTabComponentAt(i, new TitlePane(id, jCheckBox, this));
            this.textAreaToId.put(value_text, id);
            i++;
        }
        extensionPanel.setSelectedIndex(eventTab);
        return extensionPanel;
    }

    private JPanel createGeneratorComponent() {
        final JPanel headerCheckedPanel = new JPanel();
        JLabel question = new JLabel("Use as EPC generator: ");
        JButton generatorParams = new JButton(new ImageIcon("pictures/edit.png"));
        generatorParams.setBorder(null);
        generatorParams.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                new GeneratorParamDialog(page.frame, EditDialog.this, EditDialog.this.extensions).setVisible(true);
            }
        });
        headerCheckedPanel.add(question);
        headerCheckedPanel.add(generatorStatus);
        headerCheckedPanel.add(generatorParams);
        String value = (String) extensions.get("epcgenerator");
        updateGeneratorStatus(parseGenerationParams(value));
        return headerCheckedPanel;
    }

    private JPanel createPipeComponent() {
        // header panel
        String value = (String) extensions.get("pipe");
        final JPanel headerCheckedPanel = new JPanel();
        JLabel question = new JLabel("Use as pipe: ");
        headerCheckedPanel.add(question);
        JButton pipeParams = new JButton(new ImageIcon("pictures/edit.png"));
        pipeParams.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                new PipeParamDialog(page.frame, EditDialog.this, EditDialog.this.extensions).setVisible(true);
            }
        });
        pipeParams.setBorder(null);
        headerCheckedPanel.add(pipeStatus);
        headerCheckedPanel.add(pipeParams);
        updatePipeStatus(parsePipeParams(value));
        return headerCheckedPanel;
    }

    private JPanel createDeactivatorPanel(Map<String, String> extensions) {
        String value = extensions.get("epcdeactivator");
        deactivatorIsVisible = Boolean.parseBoolean(value);
        //deactivatorPanel
        final JPanel deactivatorPanel = new JPanel();
        deactivatorPanel.setLayout(new BorderLayout());

        //header Panel
        JPanel headerCheckedPanel = new JPanel();
        JLabel question = new JLabel("Use as EPC deactivator: ");
        deactivator = new JCheckBox();
        headerCheckedPanel.add(question);
        headerCheckedPanel.add(deactivatorStatus);
        headerCheckedPanel.add(deactivator);

        //manage panels
        deactivatorPanel.add(headerCheckedPanel, BorderLayout.NORTH);

        deactivator.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                deactivatorModified = !deactivatorModified;
                deactivatorIsVisible = !deactivatorIsVisible;
                updateDeactivatorStatus(deactivatorIsVisible);
                EditDialog.this.check("epcdeactivator");
            }
        });

        updateDeactivatorStatus(Boolean.parseBoolean(value));
        return deactivatorPanel;
    }

    private void updateDeactivatorStatus(boolean active) {
        deactivator.setSelected(active);
        deactivatorStatus.setText(active ? "active" : "inactive");
        deactivatorStatus.setForeground(active ? Color.GREEN : Color.RED);
    }

    private void updateGeneratorStatus(boolean active) {
        generatorStatus.setText(active ? "active" : "inactive");
        generatorStatus.setForeground(active ? Color.GREEN : Color.RED);
    }

    private void updatePipeStatus(boolean active) {
        pipeStatus.setText(active ? "active" : "inactive");
        pipeStatus.setForeground(active ? Color.GREEN : Color.RED);
    }

    private boolean parseGenerationParams(String generationValue) {
        int paramListSize = generationValue.split("%").length;
        if (paramListSize >= 2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean parsePipeParams(String extensionValue) {
        String[] tab = extensionValue.split("\n");
        if (tab.length > 0) {
            String[] line = tab[0].split("=");
            if (line[0].trim().equals("isPipe")) {
                if (Boolean.parseBoolean(line[1].trim())) {
                    return Boolean.parseBoolean(line[1].trim());
                }
            }
        }
        return false;
    }

    private JPanel createControlPanel() {
        JPanel buttonPanel = new JPanel();
        // init button panel with 'ok' and 'cancel' button
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        return buttonPanel;
    }

    private class GeneratorParamDialog extends JDialog implements ActionListener {

        private EditDialog parent;
        private JLabel epcClassLabel;
        private JLabel keyGenLabel;
        private JPanel paramsPanel;

        public GeneratorParamDialog(JFrame subparent, EditDialog parent, Map<String, String> extensions) {
            super(parent);
            this.parent = parent;
            this.setContentPane(createExtentionPanel(extensions));
            //this.pack();
            setTitle("generator configuration");
            setSize(350, 200);
            setLocationRelativeTo(parent);
        }

        private JPanel createExtentionPanel(Map<String, String> extensions) {
            // init the edit panel for extensions
            String value = extensions.get("epcgenerator");
            //main panel
            final JPanel generatorPanel = new JPanel();
            generatorPanel.setLayout(new BorderLayout());
            // header panel
            JLabel question = new JLabel("Use as EPC generator: ");
            generator = new JCheckBox();
            //generator content panel
            final JPanel generatorContent = new JPanel();
            generatorContent.setLayout(new BoxLayout(generatorContent, BoxLayout.PAGE_AXIS));
            JPanel lineContent0 = new JPanel(new FlowLayout());
            lineContent0.add(question);
            lineContent0.add(generator);
            JPanel controlContent = new JPanel();
            JButton okButton = new JButton("Ok");
            okButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        if (generatorIsVisible && useKeyGenOnGenerator.isSelected() && !EPCUtilities.checkEpcOrUri(epcBase.getText() + ".0")) {
                            epcBase.setBackground(Color.RED);
                            return;
                        }
                    } catch (InvalidFormatException ex) {
                        epcBase.setBackground(Color.RED);
                        return;
                    }
                    GeneratorParamDialog.this.dispose();
                }
            });
            controlContent.add(okButton);
            generatorPanel.add(lineContent0, BorderLayout.NORTH);
            generatorPanel.add(controlContent, BorderLayout.SOUTH);
            if (useMarkingOnGenerator == null) {
                useMarkingOnGenerator = new JRadioButton("use marking");
            }
            if (useKeyGenOnGenerator == null) {
                useKeyGenOnGenerator = new JRadioButton("use KeyGen");
            }
            useMarkingOnGenerator.addActionListener(this);
            useKeyGenOnGenerator.addActionListener(this);
            JPanel lineContent1 = new JPanel(new FlowLayout());
            lineContent1.add(useKeyGenOnGenerator);
            lineContent1.add(useMarkingOnGenerator);
            ButtonGroup bg = new ButtonGroup();
            bg.add(useKeyGenOnGenerator);
            bg.add(useMarkingOnGenerator);
            generatorContent.add(lineContent1);
            epcClassLabel = new JLabel("Base EPC code: ");
            if (epcBase == null) {
                epcBase = new JTextField();
            }
            JPanel lineContent2 = new JPanel(new BorderLayout());
            lineContent2.add(epcClassLabel, BorderLayout.WEST);
            lineContent2.add(epcBase, BorderLayout.CENTER);
            generatorContent.add(lineContent2);
            keyGenLabel = new JLabel("number of EPC: ");
            generatorContent.add(keyGenLabel);
            if (epcNumber == null) {
                epcNumber = new JTextField();
            }
            generatorContent.add(epcNumber);
            JPanel lineContent3 = new JPanel(new BorderLayout());
            lineContent3.add(keyGenLabel, BorderLayout.WEST);
            lineContent3.add(epcNumber, BorderLayout.CENTER);
            generatorContent.add(lineContent3);
            //option panels
            paramsPanel = new JPanel();
            TitledBorder title = BorderFactory.createTitledBorder("generator preferences");
            paramsPanel.setBorder(title);
            paramsPanel.add(generatorContent);

            generator.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    generatorModified = !generatorModified;
                    if (!generatorIsVisible) {
                        generatorIsVisible = true;
                        updateGeneratorStatus(true);
                        updateGeneratorComponent(true);
                    } else {
                        generatorIsVisible = false;
                        GeneratorParamDialog.this.parent.check("epcgenerator");
                        updateGeneratorStatus(false);
                        updateGeneratorComponent(false);
                    }
                }
            });
            if (!generatorInitialized) {
                initGeneratorPanel(generatorPanel, value);
                generatorInitialized = true;
            } else {
                initGeneratorPanel(generatorPanel, epcBase.getText() + "%" + epcNumber.getText() + "%" + useKeyGenOnGenerator.isSelected());
            }

            return generatorPanel;
        }

        private void initGeneratorPanel(JPanel generatorPanel, String extensionValue) {
            String[] params = extensionValue.split("%");
            switch (params.length) {
                case 2:
                    generator.setSelected(true);
                    epcBase.setText(params[0]);
                    epcNumber.setText(params[1]);
                    useKeyGenOnGenerator.setSelected(true);
                    changeKeyGenStatus(true);
                    generatorIsVisible = true;
                    break;
                case 3:
                    generator.setSelected(true);
                    epcBase.setText(params[0]);
                    epcNumber.setText(params[1]);
                    if (Boolean.parseBoolean(params[2])) {
                        useKeyGenOnGenerator.setSelected(true);
                        changeKeyGenStatus(true);
                    } else {
                        useMarkingOnGenerator.setSelected(true);
                        changeKeyGenStatus(false);
                    }
                    generatorIsVisible = true;
                    break;
                default:
                    generator.setSelected(false);
                    generatorIsVisible = false;
                    break;
            }
            if (!generatorIsVisible) {
                updateGeneratorComponent(false);
            }
            generatorPanel.add(paramsPanel, BorderLayout.CENTER);
        }

        private void updateGeneratorComponent(boolean bool) {
            useMarkingOnGenerator.setEnabled(bool);
            useKeyGenOnGenerator.setEnabled(bool);
            epcBase.setEnabled(bool);
            epcClassLabel.setEnabled(bool);
            keyGenLabel.setEnabled(bool);
            epcNumber.setEnabled(bool);
            paramsPanel.setEnabled(bool);
            TitledBorder border = (TitledBorder) paramsPanel.getBorder();
            border.setTitleColor(bool ? Color.BLACK : Color.GRAY);
            epcBase.setBackground(Color.WHITE);
            if (useMarkingOnGenerator.isSelected()) {
                changeKeyGenStatus(false);
            }
        }

        private void changeKeyGenStatus(boolean bool) {
            epcBase.setEnabled(bool);
            epcClassLabel.setEnabled(bool);
            keyGenLabel.setEnabled(bool);
            epcNumber.setEnabled(bool);
            paramsPanel.setEnabled(bool);
            epcBase.setBackground(Color.WHITE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == useKeyGenOnGenerator) {
                changeKeyGenStatus(true);
            } else if (e.getSource() == useMarkingOnGenerator) {
                changeKeyGenStatus(false);
            }
        }
    }

    private class PipeParamDialog extends JDialog {

        private EditDialog parent;
        private JLabel serverLabel;
        private JLabel passLabel;
        private JLabel pipeLabel;
        private JPanel optionPanel;

        public PipeParamDialog(JFrame subparent, EditDialog parent, Map<String, String> extensions) {
            super(parent);
            this.parent = parent;
            this.setContentPane(createExtentionPanel(extensions));
            setSize(400, 200);
            setLocationRelativeTo(parent);
        }

        private JPanel createExtentionPanel(Map<String, String> extensions) {
            String value = extensions.get("pipe");

            //main panel
            final JPanel pipePanel = new JPanel();
            pipePanel.setLayout(new BorderLayout());

            // header panel
            final JPanel headerCheckedPanel = new JPanel();
            JLabel question = new JLabel("Use as pipe: ");
            headerCheckedPanel.add(question);
            if (pipeCb == null) {
                pipeCb = new JCheckBox();
            }
            headerCheckedPanel.add(pipeCb);

            final JPanel pipeContent = new JPanel();
            pipeContent.setLayout(new BoxLayout(pipeContent, BoxLayout.PAGE_AXIS));
            serverLabel = new JLabel("server address: ");
            if (pipeServerAddress == null) {
                pipeServerAddress = new JTextField();
            }
            JPanel lineContent1 = new JPanel(new BorderLayout());
            lineContent1.add(serverLabel, BorderLayout.WEST);
            lineContent1.add(pipeServerAddress, BorderLayout.CENTER);
            pipeContent.add(lineContent1);
            passLabel = new JLabel("password: ");
            if (pipePassword == null) {
                pipePassword = new JTextField();
            }
            JPanel lineContent3 = new JPanel(new BorderLayout());
            lineContent3.add(passLabel, BorderLayout.WEST);
            lineContent3.add(pipePassword, BorderLayout.CENTER);
            pipeContent.add(lineContent3);
            pipeLabel = new JLabel("pipeId: ");
            if (pipeId == null) {
                pipeId = new JTextField();
            }
            JPanel lineContent4 = new JPanel(new BorderLayout());
            lineContent4.add(pipeLabel, BorderLayout.WEST);
            lineContent4.add(pipeId, BorderLayout.CENTER);
            pipeContent.add(lineContent4);

            if (radio1 == null) {
                radio1 = new JRadioButton(": expedition");
            }
            if (radio2 == null) {
                radio2 = new JRadioButton(": reception");
            }
            ButtonGroup bg = new ButtonGroup();
            bg.add(radio1);
            bg.add(radio2);
            JPanel lineContent5 = new JPanel(new FlowLayout());
            lineContent5.add(radio1);
            lineContent5.add(radio2);
            pipeContent.add(lineContent5);

            //option panels
            optionPanel = new JPanel();
            TitledBorder title = BorderFactory.createTitledBorder("pipe preferences");
            optionPanel.setBorder(title);
            optionPanel.add(pipeContent);

            JButton okButton = new JButton("Ok");
            okButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    PipeParamDialog.this.dispose();
                }
            });
            pipePanel.add(okButton, BorderLayout.SOUTH);

            //manage panel
            pipePanel.add(headerCheckedPanel, BorderLayout.NORTH);

            // checkBox manager
            pipeCb.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    pipeCbModified = !pipeCbModified;
                    if (!pipeIsVisible) {
                        EditDialog.this.check("pipe");
                        pipeIsVisible = true;
                        updatePipeStatus(true);
                        updatePipeComponent(true);
                    } else {
                        pipeIsVisible = false;
                        updatePipeComponent(false);
                        updatePipeStatus(false);
                        EditDialog.this.check("pipe");
                    }
                }
            });

            if (!pipeInitialized) {
                initPipePanel(pipePanel, value);
                pipeInitialized = true;
            } else {
                initPipePanel(pipePanel, "isPipe=" + pipeCb.isSelected()
                        + "\naddress=" + pipeServerAddress.getText()
                        + "\npassword=" + pipePassword.getText()
                        + "\npipeId=" + pipeId.getText()
                        + "\ntype=" + (radio1.isSelected() ? "expedition" : "reception"));
            }
            return pipePanel;
        }

        private void updatePipeComponent(boolean bool) {
            radio1.setEnabled(bool);
            radio2.setEnabled(bool);
            serverLabel.setEnabled(bool);
            passLabel.setEnabled(bool);
            pipeLabel.setEnabled(bool);
            pipeId.setEnabled(bool);
            pipePassword.setEnabled(bool);
            pipeServerAddress.setEnabled(bool);
//            TitledBorder border = (TitledBorder) optionPanel.getBorder();
//            border.setTitleColor(bool ? Color.BLACK : Color.GRAY);
        }

        private void initPipePanel(JPanel pipePanel, String extensionValue) {
            String[] tab = extensionValue.split("\n");
            for (String s : tab) {
                String[] line = s.split("=");
                if (line[0].trim().equals("isPipe")) {
                    if (Boolean.parseBoolean(line[1].trim())) {
                        pipeIsVisible = true;
                        updatePipeComponent(true);
                        updatePipeStatus(true);
                        pipeCb.setSelected(true);
                    } else {
                        updatePipeComponent(false);
                        pipeCb.setSelected(false);
                        updatePipeStatus(false);
                        pipeIsVisible = false;
                    }
                } else if (line[0].trim().equals("address")) {
                    pipeServerAddress.setText(line[1].trim());
                } else if (line[0].trim().equals("password")) {
                    pipePassword.setText(line[1].trim());
                } else if (line[0].trim().equals("pipeId")) {
                    pipeId.setText(line[1].trim());
                } else if (line[0].trim().equals("type")) {
                    if (line[1].trim().equals("expedition")) {
                        radio1.setSelected(true);
                    } else {
                        radio2.setSelected(true);
                    }
                }
            }
            pipePanel.add(optionPanel, BorderLayout.CENTER);
        }
    }
} // EditDialog
