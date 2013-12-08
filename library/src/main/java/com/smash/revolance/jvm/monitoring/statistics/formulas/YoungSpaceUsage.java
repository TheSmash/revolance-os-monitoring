package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

import java.util.Date;

/**
 * Created by ebour on 08/12/13.
 *
 * YOUNG = EDEN + SURVIVOR
 */
public class YoungSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(Jvm.MemoryType.YOUNG);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Series edenSpaceSeries = new EdenSpaceUsage().apply(series, since);
        Series survivorSpaceSeries = new SurvivorSpaceUsage().apply(series, since);


        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Date date : series.getDates("Usage", since))
        {
            int usageData = Integer.parseInt(edenSpaceSeries.getSerie("Usage").at(date))
                                + Integer.parseInt(survivorSpaceSeries.getSerie("Usage").at(date));
            usage.addSample(date, ""+usageData);

            int maxUsageData = Integer.parseInt(edenSpaceSeries.getSerie("Capacity").at(date))
                                + Integer.parseInt(survivorSpaceSeries.getSerie("Capacity").at(date));

            maxUsage.addSample(date, ""+maxUsageData);
        }

        Series ans = new Series(getLabel());
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }
}
