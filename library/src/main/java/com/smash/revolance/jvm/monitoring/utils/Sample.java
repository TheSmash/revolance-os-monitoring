package com.smash.revolance.jvm.monitoring.utils;

/**
 * Created by ebour on 15/12/13.
 */
public class Sample
{
    public long date;
    public double data;

    public Sample(long date, double data)
    {
        this.date = date;
        this.data = data;
    }

    public long getDate()
    {
        return date;
    }

    public double getData()
    {
        return data;
    }
}
