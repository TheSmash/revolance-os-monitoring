package com.smash.revolance.jvm.monitoring.jvm.filter;

import java.security.InvalidParameterException;

/**
 * Created by wsmash on 16/11/13.
 */
public class JvmSearchCriteria
{
    private By by;
    private String value;

    public JvmSearchCriteria(By by, String value)
    {
        setByCriteria(by);
        setValueCriteria(value);
    }

    private void setValueCriteria(String value)
    {
        if(value == null || value.isEmpty())
        {
            throw new InvalidParameterException("value parameter is " +value==null?"null":"empty. Use getJvms method without any parameters." + " Unable to do any matching.");
        }

        this.value = value;
    }

    private void setByCriteria(By by)
    {
        if(by == null)
        {
            throw new InvalidParameterException("by parameter is null. Unable to do any matching");
        }

        this.by = by;
    }

    public String getValue()
    {
        return value;
    }

    public By getBy()
    {
        return by;
    }
}
