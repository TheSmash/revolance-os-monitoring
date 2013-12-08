package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmChartListPanel extends JList
{
    private static final Map<String, JvmChartPanel> jvmPannels = new HashMap<String, JvmChartPanel>();
    private static final DefaultListModel model = new DefaultListModel();

    public JvmChartListPanel()
    {
        super();
        add(new JList(model));
    }

    public void addJvm(Jvm jvm)
    {
        JvmChartPanel chart = new JvmChartPanel(jvm);
        model.addElement(chart);
        chart.setVisible(true);
        repaint();
    }

    public void delJvm(String vmid)
    {
        jvmPannels.remove(vmid).setVisible(false);
    }
}
