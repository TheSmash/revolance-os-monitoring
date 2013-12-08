package com.smash.revolance.jvm.monitoring.commands;

import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wsmash on 16/11/13.
 */
public class JstatCommand extends CmdlineHelper
{
    public JstatCommand(String vmid, Option[] options, int frequency) throws IOException
    {
        super();
        List<String> cmdline = new ArrayList<String>();
        cmdline.add("jstat");
        for(Option option : options)
        {
            cmdline.add(option.getValue());
        }
        cmdline.add("-t");
        cmdline.add(vmid);
        cmdline.add(frequency+"s");
        cmd(cmdline.toArray(new String[cmdline.size()]));
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
        YGC("Number of young generation GC Events."),
        YGCT("Young generation garbage collection time."),
        FGC("Number of full GC events."),
        FGCT("Full garbage collection time."),
        GCT("Total garbage collection time.");


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
