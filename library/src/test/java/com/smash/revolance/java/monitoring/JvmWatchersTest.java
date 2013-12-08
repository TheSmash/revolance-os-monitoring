package com.smash.revolance.java.monitoring;

import com.smash.revolance.jvm.monitoring.JvmWatchers;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.jvm.Jvms;
import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriteria;
import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

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
        JvmWatchers watchers = new JvmWatchers("memory-consumer");

        // Start the memory gobbler
        CmdlineHelper cmdline = new CmdlineHelper();
        cmdline.dir(new File(new File("").getAbsoluteFile(), "target/materials"));

        cmdline.cmd("java", "-Xmx512M", "-jar", VMNAME);
        cmdline.exec();

        long mark = System.currentTimeMillis();


        List<Jvm> jvms = Jvms.find(watchers.listJvms(), new JvmSearchCriteria(By.VMNAME, "memory-consumer"));
        Jvm memoryConsumer = jvms.get(0);
        assertThat(memoryConsumer.getOptions().containsKey("Xmx256M"), is(true));

        Map<String, Serie> series = memoryConsumer.getSeries(mark);
    }

}
