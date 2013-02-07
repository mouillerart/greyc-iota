/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.eta.callback.filter;

public class Main {

    public static void main(String[] args) {
        long delay = Constants.STARTUP_DELAY;
        long period = Constants.POLLING_DELAY;
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("------------");
                System.err.println("Usage: arg1 arg2");
                System.err.println("arg1: delay in milliseconds before task is to be executed");
                System.err.println("arg2: time in milliseconds between successive task executions");
                System.err.println("------------");
            }
            switch (args.length) {
                case 2:
                    period = Long.parseLong(args[1]);
                    /* fall-through */
                case 1:
                    delay = Long.parseLong(args[0]);
                    break;
            }
        }
        new Filter(delay, period).start();
    }
}
