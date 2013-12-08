package com.smash.revolance.jvm.monitoring.utils;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wsmash on 16/11/13.
 */
public class Series
{
    private String label;
    private Map<String, Serie> series = new HashMap();

    public Series(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void addSerie(String key, Serie serie)
    {
        this.series.put(key, serie);
    }

    public Map<String, Serie> subSerie(long since)
    {
        Map<String, Serie> serieMap = new HashMap<String, Serie>();

        for(String k : series.keySet())
        {
            serieMap.put(k, serieMap.get(k).getDatas(since));
        }

        return serieMap;
    }

    public Serie getSerie(String col)
    {
        return series.get( col );
    }

    public List<Date> getDates(String col)
    {
        return getSerie( col ).getDates();
    }

    public List<Date> getDates(String col, long since)
    {
        return getSerie( col ).getDates(since);
    }

    public List<Date> getDates(JstatCommand.Column column)
    {
        return getDates( String.valueOf( column ) );
    }

    public List<Date> getDates(JstatCommand.Column column, long since)
    {
        return getDates( String.valueOf( column ), since );
    }

    public Serie getSerie(JstatCommand.Column column)
    {
        return getSerie( String.valueOf( column ) );
    }

}
