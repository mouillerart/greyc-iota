package de.huberlin.informatik.pnk.appControl.base;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * LogoWin.java
 *
 *
 * Created: Mon May 28 08:43:34 2001
 *
 * @author Alexander Grænewald
 * @version
 */

public class LogoWin extends JWindow {
    JProgressBar progressbar;

    public LogoWin(int min, int max, String iconstr) {
        super();
        Container cont = this.getContentPane();
        cont.setLayout(new BorderLayout());

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();

        Icon icon = new ImageIcon(iconstr);
        JLabel label = new JLabel(icon);
        JPanel panel = new JPanel();
        panel.add(label);
        cont.add(panel, BorderLayout.CENTER);

        JPanel progresspanel = new JPanel(new BorderLayout());
        progresspanel.setBorder(new EmptyBorder(4, 4, 4, 4));

        this.progressbar = new JProgressBar(min, max);
        progresspanel.add(progressbar, BorderLayout.CENTER);
        cont.add(progresspanel, BorderLayout.SOUTH);
        progressbar.setValue(min);

        this.setSize(411, 334);
        //        this.pack();
        int x = (dim.width >> 1) - (this.getWidth() >> 1);
        int y = (dim.height >> 1) - (this.getHeight() >> 1);
        this.setLocation(x, y);
        this.show();
    }

    public static void main(String[] args) {
        int min = 0;
        int max = 10;
        LogoWin l = new LogoWin(min, max, "icons/logo.gif");
        for (int i = min; i <= max; i++) {
            try {
                Thread.sleep(300);
            } catch (Exception e) {}
            l.setValue(i);
        }
    }

    public void setValue(int i) {
        if (this.progressbar.getMaximum() <= i)
            this.dispose();
        else this.progressbar.setValue(i);
    }
} // LogoWin