package com.smash.revolance.java.monitoring;

import com.smash.revolance.jvm.monitoring.statistics.JvmStats;
import com.smash.revolance.jvm.monitoring.statistics.Stats;
import com.smash.revolance.jvm.monitoring.statistics.formulas.EdenSpaceUsage;
import com.smash.revolance.jvm.monitoring.statistics.formulas.HeapSpaceUsage;
import com.smash.revolance.jvm.monitoring.statistics.formulas.TotalSpaceUsage;
import com.smash.revolance.jvm.monitoring.statistics.formulas.YoungSpaceUsage;
import com.smash.revolance.jvm.monitoring.utils.Series;
import org.junit.Test;

import java.io.File;

/**
 * Created by ebour on 11/12/13.
 */
public class JvmStatsTest
{
    private File memoryConsumer = new File("target/materials/memoryConsumer.stats");

    private EdenSpaceUsage edenSpaceUsage = new EdenSpaceUsage();
    private YoungSpaceUsage youngSpaceUsage = new YoungSpaceUsage();
    private HeapSpaceUsage heapSpaceUsage = new HeapSpaceUsage();
    private TotalSpaceUsage totalSpaceUsage = new TotalSpaceUsage();

    @Test
    public void statsShouldHandleJvmMemoryStatisitcs() throws Exception
    {
        JvmStats jvm = new JvmStats(System.currentTimeMillis(), memoryConsumer);
        Stats stats = jvm.getSeries(0, edenSpaceUsage, youngSpaceUsage, heapSpaceUsage, totalSpaceUsage);
        Series heapSpaceSerie = stats.getSeries(heapSpaceUsage.getLabel());
    }


}
