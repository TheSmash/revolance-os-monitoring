package com.smash.revolance.java.monitoring;

import com.smash.revolance.process.monitoring.commands.JstatCommand;
import com.smash.revolance.process.monitoring.jvm.Jvm;
import com.smash.revolance.process.monitoring.statistics.formulas.TotalSpaceUsage;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by ebour on 11/12/13.
 */
public class JvmWatcherStatsTest
{
    private File memoryConsumer = new File("target/materials/memoryConsumer.stats");

    private TotalSpaceUsage totalSpaceUsage = new TotalSpaceUsage();

    @Test
    public void statsShouldHandleJvmMemoryStatisitcs() throws Exception
    {
        JstatCommand cmd = new JstatCommand(new Jvm("foo", "bar"), JstatCommand.Option.values());
        List<Map<String, String>> metrics = cmd.parse(System.currentTimeMillis(), FileUtils.readFileToString(memoryConsumer).split("\\n"));

        assertThat(metrics.size(), is(351));
    }


}
