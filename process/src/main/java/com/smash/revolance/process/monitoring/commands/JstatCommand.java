package com.smash.revolance.process.monitoring.commands;

import com.smash.revolance.process.monitoring.jvm.Jvm;
import com.smash.revolance.process.monitoring.utils.CmdlineHelper;

import java.io.IOException;
import java.util.*;

/**
 * Created by wsmash on 16/11/13.
 */
public class JstatCommand extends CmdlineHelper
{
    private final long startTime;
    private long date;

    public JstatCommand(Jvm jvm, Option[] options) throws IOException
    {
        super();
        this.startTime = jvm.getStartTime();
        List<String> cmdline = new ArrayList<String>();
        cmdline.add("jstat");
        for(Option option : options)
        {
            cmdline.add(option.getValue());
        }
        cmdline.add(jvm.getId());
        cmd(cmdline.toArray(new String[cmdline.size()]));
    }

    public List<Map<String, String>> execute() throws IOException, InterruptedException
    {
        date = System.currentTimeMillis();
        sync().exec();
        return parse(startTime, out().split("\\n"));
    }

    public List<Map<String, String>> parse(long start, String[] statistics) throws IOException
    {
        String header = "";
        if (statistics.length > 0) {
            header = statistics[0];
        }

        List<String> columns = new ArrayList<String>();
        List<Map<String, String>> series = new ArrayList<Map<String, String>>();
        for (String col : header.split(" "))
        {
            if (col.isEmpty())
                continue;

            columns.add(String.valueOf(col));
        }

        boolean firstRow = true;
        for (String sample : statistics)
        {
            if(firstRow)
            {
                firstRow = false; // First row is the columns
            }
            else
            {
                Map<String, String> serie = new HashMap<String, String>();
                serie.put(String.valueOf(Column.Timestamp), ""+date);

                String[] samples = sample.split(" ");
                Iterator<String> it = columns.iterator();
                for (int sampleIdx = 0; sampleIdx < samples.length; sampleIdx++) {
                    if (!samples[sampleIdx].isEmpty())
                    {
                        String column = it.next();
                        String data = samples[sampleIdx].replaceAll(",", ".");
                        serie.put(column, data);
                    }
                }
                series.add(serie);
            }
        }

        return series;
    }

    public static enum Option
    {
        GC("gc");

        private String value = "";

        private Option(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return "-"+this.value;
        }

    }

    public static enum Column
    {
        Timestamp("Time elpased since jvm start time"),
        S0C("Current survivor space 0 capacity (KB)."),
        S1C("Current survivor space 1 capacity (KB)."),
        S0U("Survivor space 0 utilization (KB)."),
        S1U("Survivor space 1 utilization (KB)."),
        EC("Current eden space capacity (KB)."),
        EU("Eden space utilization (KB)."),
        OC("Current old space capacity (KB)."),
        OU("Old space utilization (KB)."),
        PC("Current permanent space capacity (KB)."),
        PU("Permanent space utilization (KB)."),
        // YGC("Number of young generation GC Events."),
        // YGCT("Young generation garbage collection time."),
        // FGC("Number of full GC events."),
        // FGCT("Full garbage collection time."),
        // GCT("Total garbage collection time.")
        ;

        private String description = "";

        private Column(String description)
        {
            this.description = description;
        }

        public String getDescription()
        {
            return description;
        }

        public static String getLegend(String col)
        {
            for(Column column : Column.values())
            {
                if(String.valueOf(column).equalsIgnoreCase(col))
                {
                    return column.getDescription();
                }
            }
            return "";
        }
    }

}
