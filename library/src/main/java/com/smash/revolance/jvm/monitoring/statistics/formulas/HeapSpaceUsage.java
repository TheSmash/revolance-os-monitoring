package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.statistics.MemoryType;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

/**
 * Created by ebour on 08/12/13.
 *
 * HEAP = YOUNG (a.k.a EDEN+SURVIVOR) + OLD
 */
public class HeapSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(MemoryType.HEAP);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Series youngSpaceSeries = new YoungSpaceUsage().apply(series, since);
        Series oldSpaceSeries = new OldSpaceUsage().apply(series, since);


        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Long date : series.getDates(since))
        {
            double usageData = youngSpaceSeries.getSerie("Usage").at(date)
                                + oldSpaceSeries.getSerie("Usage").at(date);
            usage.addSample(date, usageData);

            double maxUsageData = youngSpaceSeries.getSerie("Capacity").at(date)
                                    + oldSpaceSeries.getSerie("Capacity").at(date);

            maxUsage.addSample(date, maxUsageData);
        }

        Series ans = new Series();
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }
}