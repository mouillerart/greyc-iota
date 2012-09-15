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
package fr.unicaen.iota.simulator.server.model;

import fr.unicaen.iota.simulator.server.util.Configuration;
import fr.unicaen.iota.simulator.server.util.PipeSpecParser;
import java.util.*;

/**
 * @stereotype Singleton
 */
public class PipeContainer {

    private Map<String, PlaceFIFO> pipes;

    private static final PipeContainer instance = new PipeContainer();

    private PipeContainer(){
        Map<String, PlaceFIFO> pipesTmp = PipeSpecParser.parse(Configuration.XML_PIPE_CONFIG_FILE);
        List<String> keySet = new ArrayList<String>();
        keySet.addAll(pipesTmp.keySet());
        Collections.sort(keySet,new Comparator<String>() {
            public int compare(String o1, String o2) {
                String[] tab1 = o1.split("_");
                String[] tab2 = o2.split("_");
                int i1 = Integer.parseInt(tab1[1]);
                int i2 = Integer.parseInt(tab2[1]);
                return new Integer(i1).compareTo(i2);
            }
        });
        pipes = new LinkedHashMap<String, PlaceFIFO>();
        for (String k : keySet){
            pipes.put(k, pipesTmp.get(k));
        }
    }

    public static PipeContainer getInstance(){
        return instance;
    }

    public Map<String, PlaceFIFO> getPipes() {
        return pipes;
    }

    public void init(){
        Map<String, PlaceFIFO> pipesTmp = PipeSpecParser.parse(Configuration.XML_PIPE_CONFIG_FILE);
        List<String> keySet = new ArrayList<String>();
        keySet.addAll(pipesTmp.keySet());
        Collections.sort(keySet, new Comparator<String>() {
            public int compare(String o1, String o2) {
                String[] tab1 = o1.split("_");
                String[] tab2 = o2.split("_");
                int i1 = Integer.parseInt(tab1[1]);
                int i2 = Integer.parseInt(tab2[1]);
                return new Integer(i1).compareTo(i2);
            }
        } );
        instance.setPipes(new LinkedHashMap<String, PlaceFIFO>());
        for (String k : keySet){
            instance.getPipes().put(k, pipesTmp.get(k));
        }
    }

    /**
     * @param pipes the pipes to set
     */
    public void setPipes(Map<String, PlaceFIFO> pipes) {
        this.pipes = pipes;
    }
}
