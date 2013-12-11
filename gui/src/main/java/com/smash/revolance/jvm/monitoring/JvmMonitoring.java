package com.smash.revolance.jvm.monitoring;

import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmMonitoring extends ApplicationFrame
{
    private static JPanel content;
    private static JvmListPanel jvmListPanel;
    private static JvmChartListPanel chartListPanel;

    private JMenuBar menuBar = new JMenuBar();

    private JMenu fileMenu = new JMenu("File");
    private JMenuItem openItem = new JMenuItem("Open");

    private JMenuItem exitItem = new JMenuItem("Exit");
    private JMenuItem saveItem = new JMenuItem("Save");
    private JMenuItem saveAsItem = new JMenuItem("Save As...");

    private JMenu editMenu = new JMenu("Edit");
    private JMenuItem preferencesItem = new JMenuItem("Preferences");

    public JvmMonitoring(final String title)
    {
        super(title);

        chartListPanel = new JvmChartListPanel();
        jvmListPanel = new JvmListPanel(chartListPanel);

        initUI();
    }

    private void initUI()
    {
        buildMenuBar();

        content = new JPanel(new BorderLayout());
        // content.add(chartListPanel, BorderLayout.CENTER);
        // content.add(jvmListPanel, BorderLayout.WEST);
        content.add(new JvmChartPanel(new Jvm("pid","name")));

        setContentPane(content);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        pack();
        setVisible(true);
    }

    private void buildMenuBar()
    {
        buildFileMenu();

        buildEditMenu();

        setJMenuBar(menuBar);
    }

    private void buildEditMenu()
    {
        editMenu.add(preferencesItem);
        menuBar.add(editMenu);
    }

    private void buildFileMenu()
    {
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
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
