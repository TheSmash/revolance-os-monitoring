package com.smash.revolance.process.monitoring.commands;

import com.smash.revolance.process.monitoring.utils.CmdlineHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: wsmash
 * Date: 20/11/13
 * Time: 22:38
 */
public class JmapCommand extends CmdlineHelper
{
    public JmapCommand(String vmid, String dumpFile) throws IOException
    {
        super();
        List<String> cmdline = new ArrayList<String>();
        cmdline.add("jmap");
        cmdline.add("-dump:file="+dumpFile);
        cmdline.add(vmid);
        cmd(cmdline.toArray(new String[cmdline.size()]));
    }
}
