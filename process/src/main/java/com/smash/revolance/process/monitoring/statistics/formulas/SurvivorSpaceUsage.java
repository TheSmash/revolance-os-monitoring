package com.smash.revolance.process.monitoring.statistics.formulas;

import com.smash.revolance.commons.Serie;
import com.smash.revolance.commons.Series;
import com.smash.revolance.process.monitoring.commands.JstatCommand;
import com.smash.revolance.process.monitoring.statistics.MemoryType;

/**
 * Created by ebour on 08/12/13.
 *
 * SURVIVOR = S0+S1
 */
public class SurvivorSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(MemoryType.SURVIVOR);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Long date : series.getDates(since))
        {
            double usageData = series.getSerie(String.valueOf(JstatCommand.Column.S0U)).at(date)
                                + series.getSerie(String.valueOf(JstatCommand.Column.S1U)).at(date);
            usage.addSample(date, usageData);

            double maxUsageData = series.getSerie(String.valueOf(JstatCommand.Column.S0C)).at(date)
                                    + series.getSerie(String.valueOf(JstatCommand.Column.S1C)).at(date);

            maxUsage.addSample(date, maxUsageData);
        }

        Series ans = new Series();
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }
}
