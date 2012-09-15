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
package fr.unicaen.iota.simulator.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 */
public class LogoFrame extends JFrame {

    private static final String IMAGE_PATH = "./pictures/wings.jpg";

    public LogoFrame() {
        super("Credits");
        this.setSize(360, 220);
        this.setContentPane(new LogoPanel());
    }

    public static void main(String[] arg) {
        new LogoFrame().setVisible(true);
    }

    static class LogoPanel extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            Image img = Toolkit.getDefaultToolkit().getImage(IMAGE_PATH);
            boolean result;
            do {
                result = g.drawImage(img, 0, 0, null);
            } while (!result);
        }
    }
}
