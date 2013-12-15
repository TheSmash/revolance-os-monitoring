package com.smash.revolance.jvm.monitoring.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wsmash on 16/11/13.
 */
public class Serie
{
    public String getLegend() {
        return legend;
    }

    protected String legend;

    protected List<Double> datas = new ArrayList<Double>();
    protected List<Long>   dates = new ArrayList<Long>();

    public Serie(String legend)
    {
        this.legend = legend;
    }

    public void addSample(long date, double data)
    {
        dates.add(date);
        datas.add(data);
    }

    public List<Double> getDatas(long since)
    {
        List<Double> datas = new ArrayList<Double>();

        for(int idx = 0; idx < this.datas.size(); idx++)
        {
            if(this.dates.get(idx)>since)
            {
                datas.add(this.datas.get(idx));
            }
        }

        return datas;
    }

    public Serie getSerie(long since)
    {
        Serie serie = new Serie(this.legend);

        for(int idx = 0; idx < datas.size(); idx++)
        {
            if(dates.get(idx)>since)
            {
                serie.addSample(dates.get(idx), datas.get(idx));
            }
        }

        return serie;
    }

    public List<Long> getDates(long since)
    {
        List<Long> dates = new ArrayList<Long>();

        for(int idx = 0; idx < this.datas.size(); idx++)
        {
            if(this.dates.get(idx)>since)
            {
                dates.add(this.dates.get(idx));
            }
        }

        return dates;
    }

    public List<Long> getDates()
    {
        return dates;
    }

    public double getDataAt(long date)
    {
        int idx = 0;
        while(dates.get(idx)>date)
        {
            idx ++;
        }
        return datas.get( idx );
    }

    public double at(long date)
    {
        return getDataAt(date);
    }

    public List<Double> getDatas()
    {
        return getDatas(0);
    }
}
