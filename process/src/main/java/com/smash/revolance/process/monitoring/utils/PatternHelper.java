package com.smash.revolance.process.monitoring.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wsmash on 17/11/13.
 */
public class PatternHelper
{
    public static boolean nameIsIn(List<String> patterns, String name)
    {
        boolean match = false;
        for(String pattern : patterns)
        {
            Matcher matcher = Pattern.compile(pattern).matcher(name);
            match = matcher.matches();

            // Optimization
            if(match)
            {
                break;
            }
        }
        return match;
    }
}
