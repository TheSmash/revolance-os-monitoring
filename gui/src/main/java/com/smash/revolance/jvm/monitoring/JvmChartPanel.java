package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.statistics.JvmStats;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmChartPanel extends JPanel
{
    private final ChartPanel chartPanel;
    private final TimeSeries series;
    private final JvmStats stats;

    public JvmChartPanel(Jvm jvm) throws IOException
    {
        this.series = new TimeSeries("Random Data", Millisecond.class);
        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        final JFreeChart chart = createChart(dataset);
        this.chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        stats = new JvmStats(jvm);
        autoUpdate();
    }

    private JFreeChart createChart(final XYDataset dataset)
    {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "Dynamic Data Demo",
                "Time",
                "Value",
                dataset,
                true,
                true,
                false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 200.0);
        return result;
    }

    private void autoUpdate()
    {
        new Runnable(){

            @Override
            public void run() {
                long  mark = System.currentTimeMillis();
                while(true)
                {
                    // Retrieve all the series and add on point for each date
                    // series.add(new Millisecond(), this.lastValue);
                    doAwait(800);
                    mark+=800;
                }
            }
        }.run();

    }

    private void doAwait(long duration)
    {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
