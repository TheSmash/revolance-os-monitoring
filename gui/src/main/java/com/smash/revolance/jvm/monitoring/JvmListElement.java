package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.jvm.Jvms;
import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriteria;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmListElement extends JPanel implements ActionListener
{
    private final JvmChartListPanel jvmChartListPanel;

    public JvmListElement(Jvm jvm, JvmChartListPanel jvmChartListPanel)
    {
        this.jvmChartListPanel = jvmChartListPanel;
        add(createJvmCheckBox(jvm.getPid()));
        add(createJvmLabel(jvm.getPid(), jvm.getName(), jvm.getOptions()));
        setSize(300, 40);
        setVisible(true);
        doLayout();
    }

    private JLabel createJvmLabel(String vmid, String name, Map<String, String> options) {
        JLabel label = new JLabel();
        label.setText(String.format("%s:%s:%s", vmid, name, options.toString()));
        return label;
    }

    private JCheckBox createJvmCheckBox(String id)
    {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setName(id);
        return checkbox;
    }

    private void hideStatistics(String vmid)
    {
        jvmChartListPanel.delJvm(vmid);
        doLayout();
    }

    private void showStatistics(Jvm jvm)
    {
        jvmChartListPanel.addJvm(jvm);
        doLayout();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().contentEquals("foo"))
        {
            // getCheckboxName
            String vmid = ((JCheckBox)e.getSource()).getName();
            try
            {
                Jvm jvm = Jvms.find(JvmListPanelUpdater.watchers.listJvms(), new JvmSearchCriteria(By.VMNAME, vmid)).get(0);

                // isChecked
                if(((JCheckBox)e.getSource()).isSelected())
                {
                    showStatistics(jvm);
                }
                else
                {
                    hideStatistics(vmid);
                }
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }
}
