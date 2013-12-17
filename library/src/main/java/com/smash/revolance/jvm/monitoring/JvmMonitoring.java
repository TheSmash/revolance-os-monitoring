package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.commands.JpsCommand;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.jvm.Jvms;
import com.smash.revolance.jvm.monitoring.utils.PatternHelper;
import com.smash.revolance.jvm.monitoring.utils.Series;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wsmash on 16/11/13.
 */
public class JvmMonitoring implements Runnable
{
    private List<JvmWatcher> watchers = new ArrayList<JvmWatcher>();

    private List<String> excludes = new ArrayList<String>();
    private List<String> includes = new ArrayList<String>();

    public JvmMonitoring()
    {
        this.excludes.add(".*Jstat.*");
        this.excludes.add(".*Jps.*");
    }

    public JvmMonitoring(String includes)
    {
        this();
        if(!includes.isEmpty())
        {
            this.includes.addAll(Arrays.asList(includes.split(",")));
        }
    }

    public JvmMonitoring(String includes, String excludes)
    {
        this(includes);
        if(!excludes.isEmpty())
        {
            this.excludes = Arrays.asList(excludes.split(","));
        }
    }

    public void watch()
    {
        new Thread(this).start();
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                update();
                Thread.currentThread().sleep(2000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void update() throws Exception
    {
        List<Jvm> jvms = new JpsCommand().execute();
        for(Jvm jvm : jvms)
        {
            if (shouldBeMonitored(jvm))
            {
                startMonitoring(jvm);
            }
        }
    }

    public boolean shouldBeMonitored(Jvm jvm)
    {
        return !isMonitored(jvm) &&
                ( !nameIsInExcludedPatterns(jvm.getName()) && includes.isEmpty()
                || nameIsInIncludedPatterns(jvm.getName())                       );
    }

    public List<Jvm> listJvms() throws IOException
    {
        List<Jvm> jvmWatchers = new ArrayList<Jvm>();

        for(JvmWatcher jvmWatcher : this.watchers)
        {
            jvmWatchers.add(jvmWatcher.getJvm());
        }

        return jvmWatchers;
    }

    private void startMonitoring(Jvm jvm)
    {
        JvmWatcher newJvmWatcher = addJvmToWatchList(jvm);
        new Thread(newJvmWatcher).start();
    }

    public JvmWatcher addJvmToWatchList(Jvm jvm)
    {
        JvmWatcher newJvmWatcher = new JvmWatcher(jvm);
        this.watchers.add(newJvmWatcher);
        return newJvmWatcher;
    }

    private boolean isMonitored(Jvm jvm)
    {
        return !Jvms.isUnknown(getWatchers(), jvm);
    }

    private boolean nameIsInExcludedPatterns(String name)
    {
        return PatternHelper.nameIsIn(excludes, name);
    }

    private boolean nameIsInIncludedPatterns(String name)
    {
        return PatternHelper.nameIsIn(includes, name);
    }

    public List<Jvm> getWatchers()
    {
        List<Jvm> jvms = new ArrayList<Jvm>();

        for(JvmWatcher watcher : this.watchers)
        {
            jvms.add(watcher.getJvm());
        }

        return jvms;
    }

    public JvmWatcher getWatcher(Jvm jvm)
    {
        for(JvmWatcher watcher : watchers)
        {
            if(watcher.getJvm() == jvm)
            {
                return watcher;
            }
        }
        return null;
    }

    public Series getMetrics(Jvm jvm)
    {
        JvmWatcher watcher = getWatcher(jvm);
        return (watcher==null)?new Series():watcher.getMetrics();
    }
}
