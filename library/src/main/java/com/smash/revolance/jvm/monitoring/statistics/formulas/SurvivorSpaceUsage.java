package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

import java.util.Date;

/**
 * Created by ebour on 08/12/13.
 *
 * SURVIVOR = S0+S1
 */
public class SurvivorSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(Jvm.MemoryType.SURVIVOR);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Date date : series.getDates(JstatCommand.Column.S0C, since))
        {
            int usageData = Integer.parseInt(series.getSerie(JstatCommand.Column.S0U).at(date))
                                + Integer.parseInt(series.getSerie(JstatCommand.Column.S1U).at(date));
            usage.addSample(date, ""+usageData);

            int maxUsageData = Integer.parseInt(series.getSerie(JstatCommand.Column.S0C).at(date))
                                    + Integer.parseInt(series.getSerie(JstatCommand.Column.S1C).at(date));

            maxUsage.addSample(date, ""+maxUsageData);
        }

        Series ans = new Series(getLabel());
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }
}
