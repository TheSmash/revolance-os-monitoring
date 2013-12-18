package com.smash.revolance.process.monitoring.commands;

import com.smash.revolance.process.monitoring.jvm.Jvm;
import com.smash.revolance.process.monitoring.utils.CmdlineHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wsmash on 16/11/13.
 */
public class JpsCommand extends CmdlineHelper
{
    public JpsCommand() throws IOException
    {
        super();
        cmd("jps", "-lv");
    }

    public List<Jvm> execute() throws Exception
    {
        sync().exec().waitInMs(100);
        return parse(out().split("\\n"));
    }

    public List<Jvm> parse(String[] processes)
    {
        List<Jvm> jvms = new ArrayList<Jvm>();
        for(String process : processes)
        {
            if(!process.isEmpty()
                    && !process.contains("process information unavailable"))
            {
                String[] datas = process.split(" ");
                if( datas.length>=2 )
                {
                    Jvm jvm = new Jvm(datas[0], datas[1]);
                    if(datas.length>2)
                    {
                        for(int optionIdx = 2; optionIdx<datas.length; optionIdx++)
                        {
                            jvm.addOption(datas[optionIdx]);
                        }
                    }
                    jvms.add(jvm);
                }
            }
        }

        return jvms;
    }

}
