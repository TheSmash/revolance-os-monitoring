package com.smash.revolance.jvm.monitoring.utils;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wsmash on 16/11/13.
 */
public class Series
{
    private Map<String, Serie> series = new HashMap();

    public void addSerie(String key, Serie serie)
    {
        this.series.put(key, serie);
    }

    public Series getSeries(long since)
    {
        Series ans = new Series();

        for(String k : series.keySet())
        {
            ans.addSerie(k, series.get(k).getSerie(since));
        }

        return ans;
    }

    public Serie getSerie(String col, long since)
    {
        return getSerie(col).getSerie(since);
    }

    public Serie getSerie(String col)
    {
        if(!series.containsKey(col))
            series.put(col, new Serie(col));
        return series.get( col );
    }

    public List<Double> getDatas(String col, long since)
    {
        return getSerie(col, since).getDatas();
    }

    public List<Long> getDates(long since)
    {
        Serie col = series.values().iterator().next();
        return col.getDates(since);
    }

    public List<Long> getDates(String col, long since)
    {
        return getSerie( col ).getDates(since);
    }

    public List<Long> getDates(JstatCommand.Column column, long since)
    {
        return getDates( String.valueOf( column ), since );
    }

    public Serie getSerie(JstatCommand.Column column)
    {
        return getSerie( String.valueOf( column ) );
    }

    public Set<String> getLabels()
    {
        return series.keySet();
    }
}
