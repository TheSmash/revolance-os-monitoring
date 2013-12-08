package com.smash.revolance.jvm.monitoring.jvm;

import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriteria;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriterias;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wsmash on 16/11/13.
 */
public class Jvms
{
    private List<Jvm> jvms = new ArrayList<Jvm>();

    public List<Jvm> getAll()
    {
        return jvms;
    }

    public static boolean isUnknown(Jvms jvms, Jvm jvm)
    {
        return isUnknown(jvms.getAll(), jvm);
    }

    public static boolean isUnknown(List<Jvm> jvms, Jvm jvm)
    {
        if(jvms.isEmpty())
        {
            return true;
        }

        JvmSearchCriterias criterias = new JvmSearchCriterias();
        criterias.add(By.VMID, jvm.getPid());

        for(Jvm knownJvm : jvms)
        {
            if(Jvm.matches(knownJvm, criterias))
            {
                return false;
            }
        }

        return true;
    }

    public static List<Jvm> find(List<Jvm> jvms, JvmSearchCriteria criteria)
    {
        JvmSearchCriterias criterias = new JvmSearchCriterias();
        criterias.add(criteria);
        return find(jvms, criterias);
    }

    public static List<Jvm> find(List<Jvm> jvms, JvmSearchCriterias criterias)
    {
        List<Jvm> jvmList = new ArrayList<Jvm>();

        for(Jvm jvm : jvms)
        {
            if(Jvm.matches(jvm, criterias))
            {
                jvmList.add(jvm);
            }
        }

        return jvmList;
    }

    public void updateStatesIfMissing(List<Jvm> jvmList)
    {
        for(Jvm jvm : jvms)
        {
            if(isUnknown(jvmList, jvm))
            {
                jvm.setState(Jvm.State.STOPPED);
            }
        }
    }

    public boolean isUnknown(Jvm jvm)
    {
        return Jvms.isUnknown(this, jvm);
    }

    public void addJvm(Jvm jvm)
    {
        jvms.add(jvm);
    }
}
