package com.smash.revolance.java.monitoring;

import com.smash.revolance.process.monitoring.JvmMonitoring;
import com.smash.revolance.process.monitoring.jvm.Jvm;
import com.smash.revolance.process.monitoring.utils.PatternHelper;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by ebour on 17/11/13.
 */
public class JvmMonitoringTest
{

    @Test
    public void patternHelperShouldDetectInclusion()
    {
        String name = "jstat";
        List<String> patternsOK = asList("jstat");
        List<String> patternsKO = asList("jps");

        assertThat(PatternHelper.nameIsIn(patternsOK, name), is(true));
        assertThat(PatternHelper.nameIsIn(patternsKO, name), is(false));
    }

    @Test
    public void jvmMonitoringShouldNotTrackJps()
    {
        JvmMonitoring monitoring = new JvmMonitoring();
        assertThat(monitoring.shouldBeMonitored(new Jvm("1", "com.sun.tools.Jps")), is(false));
    }

    @Test
    public void jvmMonitoringShouldNotTrackJstat()
    {
        JvmMonitoring monitoring = new JvmMonitoring();
        assertThat(monitoring.shouldBeMonitored(new Jvm("1", "com.sun.tools.Jstat")), is(false));
    }

    @Test
    public void jvmMonitoringShouldNotTrackTheSameJvmProcessTwice()
    {
        JvmMonitoring monitoring = new JvmMonitoring();
        monitoring.addJvmToWatchList(new Jvm("foo", "bar"));
        assertThat(monitoring.shouldBeMonitored(new Jvm("foo", "bibi")), is(false));
        assertThat(monitoring.shouldBeMonitored(new Jvm("bibi", "bar")), is(true));
    }
}
