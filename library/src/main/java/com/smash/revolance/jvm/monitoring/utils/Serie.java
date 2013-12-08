package com.smash.revolance.jvm.monitoring.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wsmash on 16/11/13.
 */
public class Serie
{
    protected String legend;

    protected List<String> datas = new ArrayList<String>();
    protected List<Date>   dates = new ArrayList<Date>();

    public Serie(String legend)
    {
        this.legend = legend;
    }

    public void addSample(Date date, String data)
    {
        dates.add(date);
        datas.add(data);
    }

    public Serie getDatas(long since)
    {
        Serie serie = new Serie(this.legend);

        for(int idx = 0; idx < datas.size(); idx++)
        {
            if(dates.get(idx).getTime()>since)
            {
                serie.addSample(dates.get(idx), datas.get(idx));
            }
        }

        return serie;
    }

    public List<Date> getDates(long since)
    {
        List<Date> dates = new ArrayList<Date>();

        for(int idx = 0; idx < datas.size(); idx++)
        {
            if(dates.get(idx).getTime()>since)
            {
                dates.add(dates.get(idx));
            }
        }

        return dates;
    }

    public List<Date> getDates()
    {
        return dates;
    }

    public String getDataAt(long date)
    {
        int idx = 0;
        while(dates.get( idx ).getTime()>date)
        {
            idx ++;
        }
        return datas.get( idx );
    }

    public String at(Date date)
    {
        return getDataAt(date.getTime());
    }

}
