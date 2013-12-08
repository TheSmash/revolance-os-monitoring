package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.commands.JpsCommand;
import com.smash.revolance.jvm.monitoring.commands.JstatCommand;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.jvm.Jvms;
import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.utils.PatternHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by ebour on 16/11/13.
 */
public class JvmWatchers implements Runnable
{
    private Jvms jvms = new Jvms();

    private List<String> excludes = new ArrayList<String>();
    private List<String> includes = new ArrayList<String>();

    public JvmWatchers()
    {
        this.excludes.add("jstat");
        this.excludes.add("jps");
        this.excludes.add("junit");
        this.excludes.add("surefire");
        this.excludes.add("maven");
        this.excludes.add("jenkins");
    }

    public JvmWatchers(String includes)
    {
        this();
        this.includes.addAll(Arrays.asList(includes.split(",")));
    }

    @Override
    public void run()
    {
        while(true)
        {
            _monitorJvms();
            sleep(2000);
        }
    }

    private void _monitorJvms()
    {
        List<Jvm> jvmList = null;
        try
        {
            // Add any new jvm to the jvms field object and start monitoring
            jvmList = listJvms();
            jvms.updateStatesIfMissing(jvmList);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void monitorJvms()
    {
        run();
    }

    public List<Jvm> listJvms() throws IOException
    {
        List<Jvm> jvmList = new ArrayList<Jvm>();

        List<Map<String, String>> jvmsDatas = new JpsCommand().execute();
        for(Map<String, String> jvmDatas : jvmsDatas)
        {
            String pid  = jvmDatas.get(String.valueOf(By.VMID));
            String name = jvmDatas.get(String.valueOf(By.VMNAME));

            if (!nameIsInExcludedPatterns(name) && nameIsInIncludedPatterns(name))
            {
                Jvm newJvm = monitorJvmIfUnknown(jvmDatas, pid, name, JstatCommand.Option.values());

                if(newJvm != null)
                {
                    jvms.addJvm( newJvm );
                    jvmList.add(newJvm);
                }

            }
        }
        return jvmList;
    }

    private Jvm monitorJvmIfUnknown(Map<String, String> jvmDatas, String pid, String name, JstatCommand.Option[] monitoringOptions)
    {
        Jvm newJvm = null;
        try
        {
            newJvm = new Jvm(pid, name);

            if( jvms.isUnknown(newJvm) )
            {
                newJvm.setOptions( jvmDatas.get(String.valueOf(By.VMOPTIONS)) );
                newJvm.startMonitoring(2, monitoringOptions);
            }
            else
            {
                newJvm = null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return newJvm;
    }

    private boolean nameIsInExcludedPatterns(String name)
    {
        return PatternHelper.nameIsIn(excludes, name);
    }

    private boolean nameIsInIncludedPatterns(String name)
    {
        return PatternHelper.nameIsIn(includes, name);
    }

    private void sleep(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {

        }
    }

    public List<Jvm> getJvms()
    {
        return jvms.getAll();
    }


}
