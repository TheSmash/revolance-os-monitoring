package com.smash.revolance.java.monitoring;

import com.smash.revolance.jvm.monitoring.utils.PatternHelper;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by ebour on 17/11/13.
 */
public class HelperTests
{
    @Test
    public void patternHelperShouldDetectInclusion()
    {
        String name = "jstat";
        List<String> patternsOK = Arrays.asList(new String[]{"jstat"});
        List<String> patternsKO = Arrays.asList(new String[]{"jps"});

        assertThat(PatternHelper.nameIsIn(patternsOK, name), is(true));
        assertThat(PatternHelper.nameIsIn(patternsKO, name), is(false));
    }
}
