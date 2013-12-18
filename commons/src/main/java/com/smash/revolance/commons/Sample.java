package com.smash.revolance.commons;

/**
 * Created by ebour on 15/12/13.
 */
public class Sample implements Comparable<Sample>
{
    public long date;
    public Object data;

    public Sample(long date, Object data)
    {
        this.date = date;
        this.data = data;
    }

    public long getDate()
    {
        return date;
    }

    public Object getData()
    {
        return data;
    }

    @Override
    public int compareTo(Sample sample)
    {
        if(sample.getDate()<getDate())
        {
            return 1;
        }
        else
        {
            return 1;
        }
    }
}
