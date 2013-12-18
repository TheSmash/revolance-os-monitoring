package com.smash.revolance.commons;

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

    private String legend;

    private List<Sample> samples = new ArrayList<Sample>();

    public List<Sample> getSamples()
    {
        return samples;
    }

    public Serie(String legend)
    {
        this.legend = legend;
    }

    public void addSample(long date, double data)
    {
        samples.add(new Sample(date, data));
    }

    public List<Sample> getSamples(long since)
    {
        List<Sample> samples = new ArrayList<Sample>();

        for(Sample sample : this.samples)
        {
            if(sample.date>=since)
            {
                samples.add(sample);
            }
        }

        return samples;
    }

    public Serie getSerie(long since)
    {
        Serie serie = new Serie(this.legend);

        for(Sample sample : getSamples(since))
        {
            serie.addSample(sample);
        }

        return serie;
    }

    public void addSample(Sample sample)
    {
        this.samples.add(sample);
    }

    public void addSamples(List<Sample> samples)
    {
        this.samples.addAll(samples);
    }

    public List<Long> getDates(long since)
    {
        List<Long> dates = new ArrayList<Long>();

        for(Sample sample : getSamples(since))
        {
            dates.add(sample.date);
        }

        return dates;
    }

    public double getDataAt(long date)
    {
        int idx = 0;
        while(this.samples.get(idx).date>=date)
        {
            idx ++;
        }
        return this.samples.get( idx ).date;
    }

    public double at(long date)
    {
        return getDataAt(date);
    }

    public List<Object> getDatas(long since)
    {
        List<Object> datas = new ArrayList<Object>();

        for(Sample sample : getSamples(since))
        {
            datas.add(sample.data);
        }

        return datas;
    }

    public void clear()
    {
        samples.clear();
    }

    public int size()
    {
        return samples.size();
    }
}
