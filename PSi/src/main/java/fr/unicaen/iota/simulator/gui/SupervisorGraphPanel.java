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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 */
public class SupervisorGraphPanel extends JPanel {

    private TimeSeries instantAvgValueSeries;
    private TimeSeries avgValueSeries;
    private double lastValue;
    private TimeSeriesCollection timeseriescollection;
    private Map<String, TimeSeries> series = new HashMap<String, TimeSeries>();
    private Long maximumItemAge = 180000L;

    private JFreeChart createChart(XYDataset xydataset) {
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("EPCIS response time", "", "", xydataset, true, false, false);
        XYPlot xyplot = jfreechart.getXYPlot();
        ValueAxis valueaxis = xyplot.getDomainAxis();
        valueaxis.setAutoRange(true);
        valueaxis.setFixedAutoRange(60000D);
        valueaxis = xyplot.getRangeAxis();
        jfreechart.setBackgroundPaint(new Color(255, 255, 255, 0));
        return jfreechart;
    }

    public void addValue(double value, double avg) {
        lastValue = value;
        instantAvgValueSeries.add(new Millisecond(), lastValue);
        avgValueSeries.add(new Millisecond(), avg);
    }

    public void addValue(String bizLoc, double instAvg) {
        TimeSeries ts = series.get(bizLoc);
        if (ts == null) {
            ts = new TimeSeries(bizLoc);
            series.put(bizLoc, ts);
            ts.setMaximumItemAge(maximumItemAge);
            timeseriescollection.addSeries(ts);
        }
        ts.add(new Millisecond(), instAvg);
    }

    public SupervisorGraphPanel() {
        super(new BorderLayout());
        lastValue = 0D;
        instantAvgValueSeries = new TimeSeries("Instant average");
        avgValueSeries = new TimeSeries("Average");
        timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(avgValueSeries);
        timeseriescollection.addSeries(instantAvgValueSeries);
        avgValueSeries.setMaximumItemAge(maximumItemAge);
        instantAvgValueSeries.setMaximumItemAge(maximumItemAge);
        ChartPanel chartpanel = new ChartPanel(createChart(timeseriescollection));
        chartpanel.setPreferredSize(new Dimension(800, 600));
        add(chartpanel, BorderLayout.CENTER);
    }
}
