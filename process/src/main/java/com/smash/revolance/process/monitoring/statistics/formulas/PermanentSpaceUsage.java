package com.smash.revolance.process.monitoring.statistics.formulas;

import com.smash.revolance.commons.Serie;
import com.smash.revolance.commons.Series;
import com.smash.revolance.process.monitoring.commands.JstatCommand;
import com.smash.revolance.process.monitoring.statistics.MemoryType;

/**
 * Created by ebour on 08/12/13.
 */
public class PermanentSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(MemoryType.PERMANENT);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Long date : series.getDates(since))
        {
            usage.addSample(date, series.getSerie(String.valueOf(JstatCommand.Column.PU)).at(date));
            maxUsage.addSample(date, series.getSerie(String.valueOf(JstatCommand.Column.PC)).at(date));
        }

        Series ans = new Series();
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }
}
