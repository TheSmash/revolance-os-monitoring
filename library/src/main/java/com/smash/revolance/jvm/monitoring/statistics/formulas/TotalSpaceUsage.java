package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

import java.util.Date;

/**
 * Created by ebour on 08/12/13.
 */
public class TotalSpaceUsage implements ReduceOperator
{
    @Override
    public String getLabel()
    {
        return String.valueOf(Jvm.MemoryType.TOTAL);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Series heapSpaceUsage = new HeapSpaceUsage().apply(series, since);
        Series permanentSpaceUsage = new PermanentSpaceUsage().apply(series, since);

        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Date date : series.getDates("Usage"))
        {
            int usageData = Integer.parseInt(heapSpaceUsage.getSerie("Usage").at(date))
                                 + Integer.parseInt(permanentSpaceUsage.getSerie("Usage").at(date));
            usage.addSample(date, ""+usageData);

            int maxUsageData = Integer.parseInt(heapSpaceUsage.getSerie("Capacity").at(date))
                                     + Integer.parseInt(permanentSpaceUsage.getSerie("Capacity").at(date));

            maxUsage.addSample(date, ""+maxUsageData);
        }

        Series ans = new Series(getLabel());
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }

}
