/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.validator.operations;

import fr.unicaen.iota.validator.Configuration;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ThreadManager {

    private final List<Thread> threads;

    public ThreadManager() {
        this.threads = new LinkedList<Thread>();
    }

    public synchronized void startThread(Analyser analyser) throws InterruptedException {
        while (threads.size() >= Configuration.NUMBER_OF_ACTIVE_THREAD) {
            wait();
        }
        threads.add(analyser);
        analyser.setThreadManager(this);
        analyser.start();
    }

    public synchronized void stopThread(Analyser analyser) {
        threads.remove(analyser);
        notifyAll();
    }
}
