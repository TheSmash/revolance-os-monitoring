package com.smash.revolance.java.monitoring;

import com.smash.revolance.jvm.monitoring.JvmWatchers;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.jvm.Jvms;
import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriteria;
import com.smash.revolance.jvm.monitoring.statistics.JvmStats;
import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;
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
    private final static String VMNAME = "jvm-monitoring-materials-memory-consumer-1.0.0-SNAPSHOT.jar";

    @Test
    public void watcherShouldHandleNewJvm() throws Exception
    {
        CmdlineHelper cmdline = launchMemoryConsumer();

        JvmStats memoryConsumer = monitorMemoryConsumer("memory-consumer");

        awaitForSomeDataAndStopMemoryConsumption(cmdline, 1000);
    }

    private void awaitForSomeDataAndStopMemoryConsumption(CmdlineHelper cmdline, long duration) throws InterruptedException
    {
        long mark = System.currentTimeMillis();
        Thread.sleep(duration);

        cmdline.kill(); // Stop the momory consumer
    }

    private JvmStats monitorMemoryConsumer(String vmName) throws IOException
    {
        JvmWatchers watchers = new JvmWatchers(vmName);
        watchers.watch();

        List<Jvm> jvms = Jvms.find(watchers.listJvms(), new JvmSearchCriteria(By.VMNAME, vmName));
        Jvm memoryConsumer = jvms.get(0);

        assertThat(memoryConsumer.getName(), equalTo(VMNAME));
        assertThat(memoryConsumer.getOptions().containsKey("Xmx512M"), is(true));
        return new JvmStats(memoryConsumer);
    }

    private CmdlineHelper launchMemoryConsumer() throws Exception
    {
        // Start the memory gobbler
        CmdlineHelper cmdline = new CmdlineHelper();
        cmdline.dir(new File(new File("").getAbsoluteFile(), "target/materials"));

        cmdline.cmd("java", "-Xmx512M", "-jar", VMNAME);
        cmdline.exec().withoutErrors();

        return cmdline;
    }

}
