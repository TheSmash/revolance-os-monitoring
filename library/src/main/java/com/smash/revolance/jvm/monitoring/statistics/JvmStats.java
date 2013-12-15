package com.smash.revolance.jvm.monitoring.statistics;

import com.smash.revolance.jvm.monitoring.statistics.formulas.ReduceOperator;
import com.smash.revolance.jvm.monitoring.utils.Series;
import org.apache.log4j.Logger;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmStats
{
    private static final Logger LOG = Logger.getLogger(JvmStats.class);

    private long start = 0;
    private Series series = new Series();

    public JvmStats(long start, Series series)
    {
        this.start = start;
        this.series = series;
    }

    public Stats getSeries(long since, ReduceOperator... reduceOperators) throws Exception
    {
        Stats stats = new Stats();

        for(ReduceOperator formula : reduceOperators)
        {
            stats.addSeries(formula.getLabel(), formula.apply(series.getSeries(0), since));
        }

        return stats;
    }

    public void add(String metric, long time, double value)
    {
        series.getSerie(metric).addSample(time, value);
    }



    public long getStart()
    {
        return start;
    }
}
