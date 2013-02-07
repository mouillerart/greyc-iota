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

import fr.unicaen.iota.validator.gui.StatPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

/**
 *
 */
public class ChartBuilder extends Thread implements ChartProgressListener {

    private StatPanel statPanel;
    private List<List<String>> series;
    private ChartPreferences chartPreference;
    private static final Log log = LogFactory.getLog(ChartBuilder.class);

    public ChartBuilder(StatPanel panel, List<List<String>> series, ChartPreferences pref) {
        this.statPanel = panel;
        this.series = series;
        this.chartPreference = pref;
    }

    @Override
    public void run() {
        statPanel.getjButton1().setEnabled(false);
        log.trace("Creating chart...");
        createChart(series);
        statPanel.getjProgressBar1().setValue(100);
        statPanel.getjLabel2().setText(100 + "%");
        log.trace("DONE");
        statPanel.getjButton2().setEnabled(true);
    }

    private void createChart(List<List<String>> dataList) {
        switch (chartPreference.getChartType()) {
            case XY_CHART:
                List<String> list = dataList.get(0);
                DefaultCategoryDataset dcd = new DefaultCategoryDataset();
                Number lastValue = 0;
                Integer threashold = ((XYChartPreferences) chartPreference).getValue("adaptive");
                boolean useThreashold = threashold != null;
                for (int i = 0; i < list.size(); i++) {
                    Number d = Long.parseLong(list.get(i));
                    if (useThreashold) {
                        if (Math.abs(d.intValue() - lastValue.intValue()) > threashold) {
                            dcd.addValue(d, "responseTime", i);
                        }
                    } else {
                        dcd.addValue(d, "responseTime", i);
                    }
                    lastValue = d;
                    float status = ((((float) i) / ((float) list.size())) * 100f);
                    status = (float) ((int) status * 100) / 100;
                    statPanel.getjLabel2().setText(status + "%");
                    statPanel.getjProgressBar1().setValue((int) status);
                }
                JFreeChart chart = ChartFactory.createLineChart(statPanel.getFileToAnalyse().getName(), "queries", "Response Time (ms)", dcd, PlotOrientation.VERTICAL, false, false, false);
                chart.addProgressListener(this);
                ChartPanel chartPanel = new ChartPanel(chart);
                JLabel lblChart = new JLabel();
                statPanel.getContentPane().add(chartPanel, BorderLayout.CENTER);
                break;
            case BOX_AND_WISKER:
                statPanel.getContentPane().add(new BoxAndWhiskerPanel(dataList), BorderLayout.CENTER);
                break;
            case BAR_CHART:
                statPanel.getContentPane().add(new BarChartPanel(dataList, ((BarChartPreferences) chartPreference).getMilestones()), BorderLayout.CENTER);
                break;
            default:
                break;
        }
    }

    @Override
    public void chartProgress(ChartProgressEvent cpe) {
    }

    public class BoxAndWhiskerPanel extends JPanel {

        public BoxAndWhiskerPanel(List<List<String>> list) {
            this.setLayout(new BorderLayout());
            final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(list);
            final CategoryAxis xAxis = new CategoryAxis("Type");
            final NumberAxis yAxis = new NumberAxis("Value");
            yAxis.setAutoRangeIncludesZero(false);
            final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
            renderer.setFillBox(false);
            renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
            final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

            final JFreeChart chart = new JFreeChart("Box-and-Whisker",
                    new Font("SansSerif", Font.BOLD, 14), plot, true);
            final ChartPanel chartPanel = new ChartPanel(chart);
            this.add(chartPanel, BorderLayout.CENTER);
        }

        /**
         * Creates a sample dataset.
         *
         * @return A sample dataset.
         */
        private BoxAndWhiskerCategoryDataset createSampleDataset(List<List<String>> dataList) {
            log.trace(dataList.size());
            final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
            final List list = new ArrayList();
            int serie = 0;
            for (List<String> l : dataList) {
                for (String val : l) {
                    list.add(Long.parseLong(val));
                }
                dataset.add(list, "Serie " + serie, "");
                serie++;
                list.clear();
            }
            return dataset;
        }
    }

    public class BarChartPanel extends JPanel {

        public BarChartPanel(List<List<String>> list, List<Integer> bornes) {
            this.setLayout(new BorderLayout());
            List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();
            List<Interval> bList = new ArrayList<Interval>();
            Integer lastI = Integer.MIN_VALUE;
            for (Integer b : bornes) {
                bList.add(new Interval(lastI, b));
                lastI = b;
            }
            bList.add(new Interval(lastI, Integer.MAX_VALUE));
            for (List<String> l : list) {
                mapList.add(computeMap(l, bList));
            }
            this.add(createChart(createDataset(mapList, bList)), BorderLayout.CENTER);
        }

        private CategoryDataset createDataset(List<Map<String, Integer>> dataList, List<Interval> bList) {
            // create the dataset...
            final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            int i = 0;
            // sorting:
            SortedSet<Interval> set = new TreeSet<Interval>();

            for (Interval interval : bList) {
                set.add(interval);
            }
            for (Map<String, Integer> map : dataList) {
                for (Interval keyInt : set) {
                    dataset.addValue(map.get(keyInt.toString()), keyInt.toString(), "server" + i);
                }
                i++;
            }
            return dataset;
        }

        private ChartPanel createChart(final CategoryDataset dataset) {
            // create the chart...
            final JFreeChart chart = ChartFactory.createBarChart(
                    "Bar Chart Demo",         // chart title
                    "Category",               // domain axis label
                    "Value",                  // range axis label
                    dataset,                  // data
                    PlotOrientation.VERTICAL, // orientation
                    true,                     // include legend
                    true,                     // tooltips?
                    false                     // URLs?
                    );
            final ChartPanel chartPanel = new ChartPanel(chart);
            return chartPanel;
        }

        private Map<String, Integer> computeMap(List<String> list, List<Interval> bList) {
            Map<String, Integer> occurences = new HashMap<String, Integer>();
            for (String s : list) {
                Long val = Long.parseLong(s);
                for (Interval interval : bList) {
                    if (interval.contains(val)) {
                        Integer nbOccurence = occurences.get(interval.toString());
                        if (nbOccurence == null) {
                            nbOccurence = 0;
                        }
                        occurences.put(interval.toString(), nbOccurence + 1);
                    }
                }
            }
            return occurences;
        }

        class Interval implements Comparable<Interval> {

            private long i;
            private long j;

            public Interval(long i, long j) {
                this.i = i;
                this.j = j;
            }

            public boolean contains(long val) {
                return val > getI() && (val < getJ() || val == getJ());
            }

            @Override
            public String toString() {
                String di = String.valueOf(getI());
                String dj = String.valueOf(getJ());
                if (getI() == Long.MIN_VALUE) {
                    di = "-INF";
                }
                if (getJ() == Long.MIN_VALUE) {
                    dj = "INF";
                }
                return "[ " + di + " : " + dj + " ]";
            }

            @Override
            public int compareTo(Interval t) {
                if (getI() == t.getI() && getJ() == t.getJ()) {
                    return 0;
                }
                return getI() < t.getI() ? -1 : 1;
            }

            /**
             * @return the i
             */
            public long getI() {
                return i;
            }

            /**
             * @return the j
             */
            public long getJ() {
                return j;
            }
        }
    }
}
