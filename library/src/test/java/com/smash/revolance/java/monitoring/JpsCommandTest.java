package com.smash.revolance.java.monitoring;

import com.smash.revolance.jvm.monitoring.commands.JpsCommand;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by ebour on 15/12/13.
 */
public class JpsCommandTest
{
    private static String[] processes = new String[]{"16524 some-artifact.jar", "7294 mainClass with several options"};

    @Test
    public void parserShouldCreateProperJvmFromJpsCommand() throws IOException
    {
        JpsCommand cmd = new JpsCommand();
        List<Jvm> jvms = cmd.parse(processes);

        assertThat(jvms.size(), is(2));

        assertThat(jvms.get(0).getName(), equalTo("some-artifact.jar"));
        assertThat(jvms.get(0).getId(), equalTo("16524"));
        assertThat(jvms.get(0).getOptions().isEmpty(), is(true));

        assertThat(jvms.get(1).getName(), equalTo("mainClass"));
        assertThat(jvms.get(1).getId(), equalTo("7294"));
        assertThat(jvms.get(1).getOptions().isEmpty(), is(false));
    }

}
