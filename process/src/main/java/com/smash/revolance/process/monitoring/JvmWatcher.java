package com.smash.revolance.process.monitoring;

import com.smash.revolance.commons.Series;
import com.smash.revolance.process.monitoring.commands.JstatCommand;
import com.smash.revolance.process.monitoring.jvm.Jvm;
import com.smash.revolance.process.monitoring.jvm.JvmState;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by wsmash on 16/11/13.
 */
public class JvmWatcher implements Runnable
{
    private final Jvm jvm;

    private Series metrics = new Series();

    public long getJvmStartTime()
    {
        return jvm.getStartTime();
    }

    public JvmWatcher(Jvm jvm)
    {
        this.jvm = jvm;
    }

    @Override
    public void run()
    {
        while(isJvmRunning())
        {
            try
            {
                watch();
                Thread.currentThread().sleep(2000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                setJvmState(JvmState.STOPPED);
            }
        }
    }

    public void watch() throws IOException, InterruptedException
    {
        List<Map<String, String>> series = new JstatCommand(jvm, JstatCommand.Option.values()).execute();
        for(Map<String, String> serie : series)
        {
            long date = Long.parseLong(serie.get(String.valueOf(JstatCommand.Column.Timestamp)));
            for(String metric : serie.keySet())
            {
                if(!metric.contentEquals(String.valueOf(JstatCommand.Column.Timestamp)))
                {
                    double data = Double.parseDouble(serie.get(metric));
                    this.metrics.addSample(metric, date, data);
                }
            }
        }
    }

    private void setJvmState(JvmState state)
    {
        jvm.setState(state);
    }

    /**
     *
     * @return true if the jvm is RUNNING
     */
    public boolean isJvmRunning()
    {
        return jvm.isRunning();
    }

    public Jvm getJvm()
    {
        return jvm;
    }

    public Series getMetrics()
    {
        return metrics;
    }

    public void setMetrics(Series metrics)
    {
        this.metrics = metrics;
    }

}
