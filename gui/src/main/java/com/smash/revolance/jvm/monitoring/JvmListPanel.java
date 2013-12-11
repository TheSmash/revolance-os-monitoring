package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmListPanel extends JScrollPane
{
    private static final Logger LOG = Logger.getLogger(JvmListPanel.class);

    static
    {
        LOG.addAppender(new ConsoleAppender());
    }

    private static final DefaultListModel model = new DefaultListModel();

    private final JvmChartListPanel chartListPanel;

    public JvmListPanel(JvmChartListPanel chartListPanel)
    {
        super();
        this.chartListPanel = chartListPanel;

        add(new JList(model));
    }

    public void addJvm(Jvm jvm)
    {
        model.addElement(new JvmListElement(jvm, chartListPanel));
        LOG.log(Level.INFO, "Adding new jvm: " + jvm.getName());
        doLayout();
    }

}
