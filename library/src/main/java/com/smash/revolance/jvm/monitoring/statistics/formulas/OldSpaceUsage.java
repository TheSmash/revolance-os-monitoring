package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

import java.util.Date;

/**
 * User: wsmash
 * Date: 20/11/13
 * Time: 22:41
 */
public class OldSpaceUsage implements ReduceOperator
{
    public String getLabel()
    {
        return String.valueOf(Jvm.MemoryType.OLD);
    }

    @Override
    public Series apply(Series series, long since)
    {
        Serie usage = new Serie("Usage");
        Serie maxUsage = new Serie("Capacity");

        for(Date date : series.getDates(JstatCommand.Column.OC, since))
        {
            usage.addSample(date, series.getSerie(JstatCommand.Column.OU).at(date));
            maxUsage.addSample(date, series.getSerie(JstatCommand.Column.OC).at(date));
        }

        Series ans = new Series(getLabel());
        ans.addSerie("Usage", usage);
        ans.addSerie("Capacity", maxUsage);

        return ans;
    }

}
