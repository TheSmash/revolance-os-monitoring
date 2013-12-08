package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

import java.util.Date;

/**
 * Created by ebour on 08/12/13.
 */
public class PermanentSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(Jvm.MemoryType.PERMANENT);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Date date : series.getDates(JstatCommand.Column.PC, since))
        {
            usage.addSample(date, series.getSerie(JstatCommand.Column.PU).at(date));
            maxUsage.addSample(date, series.getSerie(JstatCommand.Column.PC).at(date));
        }

        Series ans = new Series(getLabel());
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }
}
