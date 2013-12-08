package com.smash.revolance.jvm.monitoring;

import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmMonitoring extends ApplicationFrame
{
    private static JPanel content;
    private static JvmListPanel jvmListPanel;
    private static JvmChartListPanel chartListPanel;

    public JvmMonitoring(final String title)
    {
        super(title);

        chartListPanel = new JvmChartListPanel();
        jvmListPanel = new JvmListPanel(chartListPanel);

        initUI();
    }

    private void initUI()
    {
        content = new JPanel(new BorderLayout());
        setContentPane(content);
        content.add(chartListPanel, BorderLayout.CENTER);
        content.add(jvmListPanel, BorderLayout.WEST);

        setSize(1100, 600);
        pack();
        setVisible(true);
    }

    private void doMonitor()
    {
        new JvmListPanelUpdater(jvmListPanel).start();
    }

    public static void main(final String[] args)
    {
        new JvmMonitoring("Jvm Monitoring Application").doMonitor();

    }

}
