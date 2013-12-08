package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;

import java.util.List;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmListPanelUpdater extends Thread
{
    public static final JvmWatchers watchers = new JvmWatchers(System.getProperty("jvm.includes", ""), System.getProperty("jvm.excludes", ""));
    private final JvmListPanel jvmListPanel;

    public JvmListPanelUpdater(JvmListPanel jvmListPanel)
    {
        this.jvmListPanel = jvmListPanel;
    }

    @Override
    public void run()
    {
        watchers.watch();
        long mark = System.currentTimeMillis();
        while (true)
        {
            try
            {
                List<Jvm> newJvms = watchers.listJvms(mark);
                for(Jvm newJvm : newJvms)
                {
                    jvmListPanel.addJvm(newJvm);
                }
                Thread.sleep(800);
                mark+=800;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
