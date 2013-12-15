package com.smash.revolance.java.monitoring;

import com.smash.revolance.jvm.monitoring.JvmMonitoring;
import com.smash.revolance.jvm.monitoring.JvmWatcher;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.jvm.Jvms;
import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriteria;
import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;
import com.smash.revolance.jvm.monitoring.utils.Series;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by ebour on 17/11/13.
 */
public class JvmWatcherWatchersTest
{
    private final static String VMNAME = "jvm-monitoring-materials-memory-consumer-1.0.0-SNAPSHOT.jar";
    private static CmdlineHelper cmdline;

    @BeforeClass
    public static void beforeTests() throws Exception
    {
        cmdline = launchMemoryConsumer();
    }

    @AfterClass
    public static void afterTests()
    {
        if(cmdline != null)
            cmdline.kill();
    }

    @Test
    public void watcherShouldHandleNewJvm() throws Exception
    {
        JvmWatcher memoryConsumer = monitorMemoryConsumer(VMNAME);
        waitAndStopMemoryConsumer(4);

        Series metrics = memoryConsumer.getMetrics();
        assertThat(metrics.getDates(memoryConsumer.getJvmStartTime()).isEmpty(), is(false));
    }

    private void waitAndStopMemoryConsumer(long duration) throws InterruptedException
    {
        Thread.sleep(duration*1000);

        cmdline.kill(); // Stop the memory consumer
    }

    private JvmWatcher monitorMemoryConsumer(String vmName) throws Exception
    {
        JvmMonitoring watchers = new JvmMonitoring(vmName);
        watchers.update();

        List<Jvm> jvmWatchers = Jvms.find(watchers.listJvms(0), new JvmSearchCriteria(By.VMNAME, vmName));
        Jvm memoryConsumer = jvmWatchers.get(0);

        assertThat(memoryConsumer.getName(), equalTo(VMNAME));
        assertThat(memoryConsumer.getOptions().containsKey("-Xmx512M"), is(true));
        return watchers.getWatcher(memoryConsumer);
    }

    private static CmdlineHelper launchMemoryConsumer() throws Exception
    {
        // Start the memory gobbler
        CmdlineHelper cmdline = new CmdlineHelper();
        cmdline.dir(new File(new File("").getAbsoluteFile(), "target/materials"));

        cmdline.cmd("java", "-Xmx512M", "-jar", VMNAME);
        cmdline.exec().waitInMs(1000).withoutErrors();

        return cmdline;
    }

}
