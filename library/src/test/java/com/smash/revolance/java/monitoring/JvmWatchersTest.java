package com.smash.revolance.java.monitoring;

import com.smash.revolance.jvm.monitoring.JvmWatchers;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.jvm.Jvms;
import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriteria;
import com.smash.revolance.jvm.monitoring.statistics.JvmStats;
import com.smash.revolance.jvm.monitoring.statistics.Stats;
import com.smash.revolance.jvm.monitoring.statistics.formulas.EdenSpaceUsage;
import com.smash.revolance.jvm.monitoring.statistics.formulas.HeapSpaceUsage;
import com.smash.revolance.jvm.monitoring.statistics.formulas.TotalSpaceUsage;
import com.smash.revolance.jvm.monitoring.statistics.formulas.YoungSpaceUsage;
import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;
import com.smash.revolance.jvm.monitoring.utils.Series;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by ebour on 17/11/13.
 */
public class JvmWatchersTest
{
    private final static String VMNAME = "java-monitoring-materials-memory-consumer-1.0-SNAPSHOT.jar";

    @Test
    public void watcherShouldHandleJvmStatitics() throws Exception
    {
        CmdlineHelper cmdline = launchMemoryConsumer();

        JvmStats memoryConsumer = monitorMemoryConsumer("memory-consumer");

        long mark = awaitForSomeDataAndStopMemoryConsumption(cmdline, 5000);

        EdenSpaceUsage edenSpaceUsage = new EdenSpaceUsage();
        YoungSpaceUsage youngSpaceUsage = new YoungSpaceUsage();
        HeapSpaceUsage heapSpaceUsage = new HeapSpaceUsage();
        TotalSpaceUsage totalSpaceUsage = new TotalSpaceUsage();

        Stats stats = memoryConsumer.getSeries(mark, edenSpaceUsage, youngSpaceUsage, heapSpaceUsage, totalSpaceUsage);
        Series heapSpaceSerie = stats.getSerie(heapSpaceUsage.getLabel());

    }

    private long awaitForSomeDataAndStopMemoryConsumption(CmdlineHelper cmdline, long duration) throws InterruptedException
    {
        long mark = System.currentTimeMillis();
        Thread.sleep(duration);

        cmdline.kill(); // Stop the momory consumer
        return mark;
    }

    private JvmStats monitorMemoryConsumer(String vmName) throws IOException
    {
        JvmWatchers watchers = new JvmWatchers(vmName);
        List<Jvm> jvms = Jvms.find(watchers.listJvms(), new JvmSearchCriteria(By.VMNAME, vmName));
        Jvm memoryConsumer = jvms.get(0);

        assertThat(memoryConsumer.getName(), equalTo(VMNAME));
        assertThat(memoryConsumer.getOptions().containsKey("Xmx512M"), is(true));
        return new JvmStats(memoryConsumer);
    }

    private CmdlineHelper launchMemoryConsumer() throws IOException, InterruptedException
    {
        // Start the memory gobbler
        CmdlineHelper cmdline = new CmdlineHelper();
        cmdline.dir(new File(new File("").getAbsoluteFile(), "target/materials"));

        cmdline.cmd("java", "-Xmx512M", "-jar", VMNAME);
        cmdline.exec();

        return cmdline;
    }

}
