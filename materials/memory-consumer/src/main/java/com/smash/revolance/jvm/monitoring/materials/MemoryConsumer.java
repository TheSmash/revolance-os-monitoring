package com.smash.revolance.jvm.monitoring.materials;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import java.util.Vector;

/**
 * Created by ebour on 17/11/13.
 */
public class MemoryConsumer
{
    private final static Logger LOG = Logger.getLogger(MemoryConsumer.class);

    public static void main(String[] args)
    {
        LOG.addAppender(new ConsoleAppender(new SimpleLayout()));

        Runtime rt = Runtime.getRuntime();
        Vector v = new Vector();
        while (true)
        {
            byte b[] = new byte[128];
            v.add(b);

            LOG.log(Level.INFO, "Memory usage is " + 100 * rt.freeMemory() / rt.totalMemory());
        }
    }

}
