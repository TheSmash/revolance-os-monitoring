package com.smash.revolance.jvm.monitoring.jvm.filter;

import java.util.ArrayList;
import java.util.List;


/**
 * JvmMatchingCrieterias defines several criterias that a jvm should be
 * matched against in order to be victorious.
 *
 * Created by wsmash on 16/11/13.
 */
public class JvmSearchCriterias
{
    private List<JvmSearchCriteria> criterias = new ArrayList<JvmSearchCriteria>();

    public void add(JvmSearchCriteria criteria)
    {
        this.criterias.add(criteria);
    }

    public void add(By by, String value)
    {
        add(new JvmSearchCriteria(by, value));
    }

    public List<JvmSearchCriteria> getAll()
    {
        return criterias;
    }
}
