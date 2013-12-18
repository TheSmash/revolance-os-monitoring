package com.smash.revolance.commons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wsmash on 16/11/13.
 */
public class Series
{
    private Map<String, Serie> series = new HashMap();

    public Map<String, Serie> getSeries()
    {
        return series;
    }

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
        if(!this.series.containsKey(col))
            this.series.put(col, new Serie(col));
        return this.series.get( col );
    }

    public List<Object> getDatas(String col, long since)
    {
        return getSerie(col).getDatas(since);
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

    public void addSample(String col, long date, double data)
    {
        getSerie(col).addSample(date, data);
    }

    public int size()
    {
        return series.size();
    }

}
