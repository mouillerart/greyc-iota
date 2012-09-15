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
package fr.unicaen.iota.simulator.app;

import de.huberlin.informatik.pnk.app.base.MetaApplication;
import de.huberlin.informatik.pnk.netElementExtensions.base.FiringRule;
import fr.unicaen.iota.simulator.gui.LogoFrame;
import fr.unicaen.iota.simulator.gui.SupervisorGUI;
import fr.unicaen.iota.simulator.gui.TrashGUI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class SelfActingSimulator extends MetaApplication {

    public boolean startAsThread = true;
    public static String staticAppName = "Selfacting Simulator";
    private static final Log log = LogFactory.getLog(SelfActingSimulator.class);

    public SelfActingSimulator(de.huberlin.informatik.pnk.appControl.ApplicationControl ac) {
        super(ac);
    }

    public void run() {
        log.trace("starting trash GUI ...");
        new TrashGUI().setVisible(true);
        log.trace("starting supervisor GUI ...");
        new SupervisorGUI().setVisible(true);
        log.trace("starting credit GUI ...");
        new LogoFrame().setVisible(true);
        FiringRule rule = (FiringRule) net.getExtension("firingRule");
        rule.simulateWithoutUserInteraction(this);
    }
} // SelfActingSimulator
