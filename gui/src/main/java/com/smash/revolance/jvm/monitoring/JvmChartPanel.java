package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.statistics.JvmStats;
import com.smash.revolance.jvm.monitoring.statistics.Stats;
import com.smash.revolance.jvm.monitoring.statistics.formulas.TotalSpaceUsage;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.util.Date;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmChartPanel extends JPanel
{
    private final ChartPanel chartPanel;
    private final TimeSeries series;
    private JvmStats stats = new JvmStats();

    public JvmChartPanel(Jvm jvm)
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

        new Thread(){

            @Override
            public void run() {
                long  since = System.currentTimeMillis();
                final TotalSpaceUsage totalSpaceUsage = new TotalSpaceUsage();
                while(true)
                {
                    // Retrieve all the series and add on point for each date
                    // series.add(new Millisecond(), this.lastValue);

                    try
                    {
                        Stats statistics = stats.getSeries(since, totalSpaceUsage);
                        Serie serie = statistics.getSeries("Usage").getSerie("Usage");
                        for(Date date : serie.getDates())
                        {
                            double val = Double.valueOf(serie.getDataAt(date.getTime())).doubleValue();
                            TimeSeriesDataItem point = new TimeSeriesDataItem(new FixedMillisecond(date.getTime()), val);
                            series.add(point);
                        }
                        doAwait(800);
                        since+=800;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

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
