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

import fr.unicaen.iota.validator.gui.ChartType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BarChartPreferences extends ChartPreferences {

    private List<Integer> milestones = new ArrayList<Integer>();

    public BarChartPreferences() {
        super(ChartType.BAR_CHART);
    }

    public BarChartPreferences(String intervals) {
        super(ChartType.BAR_CHART);
        String[] tab = intervals.split(",");
        for (String s : tab) {
            milestones.add(Integer.valueOf(s));
        }
    }

    /**
     * @return the bornes
     */
    public List<Integer> getMilestones() {
        return milestones;
    }

    /**
     * @param bornes the bornes to set
     */
    public void setMilestones(List<Integer> milestones) {
        this.milestones = milestones;
    }
}
