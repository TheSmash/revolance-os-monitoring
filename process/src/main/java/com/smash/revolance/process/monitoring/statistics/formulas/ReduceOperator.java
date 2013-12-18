package com.smash.revolance.process.monitoring.statistics.formulas;

import com.smash.revolance.commons.Series;

/**
 * Created by wsmash on 08/12/13.
 */
public interface ReduceOperator
{
    Series apply(Series series, long since);

    String getLabel();
}
