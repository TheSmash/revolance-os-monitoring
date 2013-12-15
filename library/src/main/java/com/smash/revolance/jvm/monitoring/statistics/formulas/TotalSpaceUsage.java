package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.statistics.MemoryType;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

/**
 * Created by ebour on 08/12/13.
 */
public class TotalSpaceUsage implements ReduceOperator
{
    @Override
    public String getLabel()
    {
        return String.valueOf(MemoryType.TOTAL);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Series heapSpaceUsage = new HeapSpaceUsage().apply(series, since);
        Series permanentSpaceUsage = new PermanentSpaceUsage().apply(series, since);

        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Long date : series.getDates(since))
        {
            double usageData = heapSpaceUsage.getSerie("Usage").at(date)
                                 + permanentSpaceUsage.getSerie("Usage").at(date);
            usage.addSample(date, usageData);

            double maxUsageData = heapSpaceUsage.getSerie("Capacity").at(date)
                                     + permanentSpaceUsage.getSerie("Capacity").at(date);

            maxUsage.addSample(date, maxUsageData);
        }

        Series ans = new Series();
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }

}
