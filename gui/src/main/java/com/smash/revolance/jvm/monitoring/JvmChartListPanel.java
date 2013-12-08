package com.smash.revolance.jvm.monitoring;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmChartListPanel extends JList
{
    private static final Map<String, JvmChartPanel> jvmPannels = new HashMap<String, JvmChartPanel>();

    public JvmChartPanel get(String vmid)
    {
        return jvmPannels.get(vmid);
    }

}
