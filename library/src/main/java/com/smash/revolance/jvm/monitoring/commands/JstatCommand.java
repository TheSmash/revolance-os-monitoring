package com.smash.revolance.jvm.monitoring.commands;

import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ebour on 16/11/13.
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
        GCCAPACITY("gccapacity");

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
        NGCMN("Minimum new generation capacity (KB)."),
        NGCMX("Maximum new generation capacity (KB)."),
        NGC("Current new generation capacity (KB)."),
        S0C("Current survivor space 0 capacity (KB)."),
        S1C("Current survivor space 1 capacity (KB)."),
        EC("Current eden space capacity (KB)."),
        OGCMN("Minimum old generation capacity (KB)."),
        OGCMX("Maximum old generation capacity (KB)."),
        OGC("Current old generation capacity (KB)."),
        OC("Current old space capacity (KB)."),
        PGCMN("Minimum permanent generation capacity (KB)."),
        PGCMX("Maximum Permanent generation capacity (KB)."),
        PGC("Current Permanent generation capacity (KB)."),
        PC("Current Permanent space capacity (KB)."),
        YGC("Number of Young generation GC Events."),
        FGC("Number of Full GC Events.");


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
