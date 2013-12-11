package com.smash.revolance.jvm.monitoring.utils;

import java.util.List;

/**
 * Created by wsmash on 17/11/13.
 */
public class PatternHelper
{
    public static boolean nameIsIn(List<String> patterns, String name, boolean check)
    {
        boolean match = false;
        for(String exclude : patterns)
        {
            if(!exclude.isEmpty() && !check)
            {
                if(name.toLowerCase().contains(exclude))
                {
                    match = true;
                }
            }
            if(match)
            {
                break;
            }
        }
        if(match)
        {
            return true;
        }
        return false;
    }
}
