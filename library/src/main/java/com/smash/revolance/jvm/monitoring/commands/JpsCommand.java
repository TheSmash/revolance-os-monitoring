package com.smash.revolance.jvm.monitoring.commands;

import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wsmash on 16/11/13.
 */
public class JpsCommand extends CmdlineHelper
{
    public JpsCommand() throws IOException
    {
        super();
    }

    public List<Map<String, String>> execute()
    {
        List<Map<String, String>> jvms = new ArrayList<Map<String, String>>();
        Pattern pattern = Pattern.compile("^([0-9]+) ([^ ]+) (.+)$");

        try
        {
            CmdlineHelper cmdline = new CmdlineHelper();
            cmdline.cmd("jps", "-lv");
            cmdline.sync().exec();

            File out = cmdline.getOut();
            String[] processes = FileUtils.readFileToString(out).split("\\n");
            for(String process : processes)
            {
                Matcher matcher = pattern.matcher(process);
                matcher.matches();

                MatchResult result = matcher.toMatchResult();

                if( result.groupCount()==3 )
                {
                    Map<String, String> processLine = new HashMap<String, String>();

                    processLine.put(String.valueOf(By.VMID),    result.group(1));
                    processLine.put(String.valueOf(By.VMNAME),   result.group(2));
                    processLine.put(String.valueOf(By.VMOPTIONS), result.group(3));

                    jvms.add(processLine);
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return jvms;
    }

}
