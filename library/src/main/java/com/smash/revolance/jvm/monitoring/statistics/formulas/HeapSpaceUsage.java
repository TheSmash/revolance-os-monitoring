package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

import java.util.Date;

/**
 * Created by ebour on 08/12/13.
 *
 * HEAP = YOUNG (a.k.a EDEN+SURVIVOR) + OLD
 */
public class HeapSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(Jvm.MemoryType.HEAP);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Series youngSpaceSeries = new YoungSpaceUsage().apply(series, since);
        Series oldSpaceSeries = new OldSpaceUsage().apply(series, since);


        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Date date : series.getDates("Usage"))
        {
            int usageData = Integer.parseInt(youngSpaceSeries.getSerie("Usage").at(date))
                                + Integer.parseInt(oldSpaceSeries.getSerie("Usage").at(date));
            usage.addSample(date, ""+usageData);

            int maxUsageData = Integer.parseInt(youngSpaceSeries.getSerie("Capacity").at(date))
                                    + Integer.parseInt(oldSpaceSeries.getSerie("Capacity").at(date));

            maxUsage.addSample(date, ""+maxUsageData);
        }

        Series ans = new Series(getLabel());
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }
}