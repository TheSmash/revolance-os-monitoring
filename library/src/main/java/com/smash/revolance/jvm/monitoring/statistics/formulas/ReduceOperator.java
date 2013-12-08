package com.smash.revolance.jvm.monitoring.statistics.formulas;

import com.smash.revolance.jvm.monitoring.utils.Series;

/**
 * Created by wsmash on 08/12/13.
 */
public interface ReduceOperator
{
    Series apply(Series series, long since);

    String getLabel();
}
