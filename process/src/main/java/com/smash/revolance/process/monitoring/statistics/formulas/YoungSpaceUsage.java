package com.smash.revolance.process.monitoring.statistics.formulas;

import com.smash.revolance.commons.Serie;
import com.smash.revolance.commons.Series;
import com.smash.revolance.process.monitoring.statistics.MemoryType;

/**
 * Created by ebour on 08/12/13.
 *
 * YOUNG = EDEN + SURVIVOR
 */
public class YoungSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(MemoryType.YOUNG);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Series edenSpaceSeries = new EdenSpaceUsage().apply(series, since);
        Series survivorSpaceSeries = new SurvivorSpaceUsage().apply(series, since);

        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Long date : series.getDates(since))
        {
            double usageData = edenSpaceSeries.getSerie("Usage").at(date)
                                + survivorSpaceSeries.getSerie("Usage").at(date);
            usage.addSample(date, usageData);

            double maxUsageData = edenSpaceSeries.getSerie("Capacity").at(date)
                                    + survivorSpaceSeries.getSerie("Capacity").at(date);

            maxUsage.addSample(date, maxUsageData);
        }

        Series ans = new Series();
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }
}
