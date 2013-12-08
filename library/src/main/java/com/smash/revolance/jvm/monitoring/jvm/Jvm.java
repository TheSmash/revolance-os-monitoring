package com.smash.revolance.jvm.monitoring.jvm;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;
import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriteria;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriterias;
import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wsmash on 16/11/13.
 */
public class Jvm
{
    public static enum MemoryType {PERMANENT, OLD, EDEN, SURVIVOR, YOUNG, HEAP, TOTAL;};

    private long start = System.currentTimeMillis();

    private CmdlineHelper cmdline;

    private String pid = "";
    private String name = "";

    private Map<String, String> options = new HashMap<String, String>();

    // If this instance is created it's because the OS says that it's alive
    private State state = State.RUNNING;

    public File getStatiticsFile()
    {
        return cmdline.getOut();
    }

    public long getStartTimestamp()
    {
        return start;
    }

    public static enum State
    {
        /**
         * Any new instance of this class is jvm in RUNNING state
        */
        RUNNING,

        /**
         * When the Jvm pid is not visible (through jps command)
         * then the jvm state is STOPPED
         */
        STOPPED;
    }

    /**
     * @Constructor
     *
     * @param pid the pid of the running jvm
     * @param name the name of the running jvm
     */
    public Jvm(final String pid, final String name)
    {
        setPid(pid);
        setName(name);
    }


    public void startMonitoring(int frequency, JstatCommand.Option[] monitoringOptions) throws IOException, InterruptedException
    {
        cmdline = new JstatCommand(pid, monitoringOptions, frequency);
        cmdline.exec();
    }


    public void setOptions(String options)
    {
        if(options != null)
        {
            String[] optionsStrings = options.replaceAll("-D", "").replaceAll("-", "").split(" ");
            for(String optionString : optionsStrings)
            {
                String[] optionKeyValue = optionString.split("=");
                this.options.put(optionKeyValue[0].replaceAll("=", ""), optionKeyValue.length>1?optionKeyValue[1].replaceAll("=", ""):"");
            }
        }
    }

    private void setName(String id)
    {
        if(id != null)
            this.name = id;
    }

    private void setPid(String pid)
    {
        if(pid != null)
            this.pid = pid;
    }

    public void setState(State state)
    {
        if(state != null)
            this.state = state;
    }

    /**
     * Retrieve the jvm pid from jps
     * For instance: '16567'
     *
     * @return the jvm unique pid (for a host).
     */
    public String getPid()
    {
        return pid;
    }

    /**
     * Retrieve the jvm name from the jps command
     * For instance 'Jps'
     *
     * @return the jvm name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Retrieve the jvm options from the jps command
     * For instance: '-Xms128m, -Xmx750m, -XX:MaxPermSize=350m'
     *
     * @return the jvm options.
     */
    public Map<String, String> getOptions()
    {
        return options;
    }

    /**
     * Retrieve the state of this jvm.
     *
     * @return
     */
    public State getState()
    {
        return state;
    }

    /**
     * Retrieve the state of this jvm as a string.
     *
     * @return
     */
    public String getStringState()
    {
        return String.valueOf(getState());
    }

    /**
     *
     * @return true if the jvm state is RUNNING
     */
    public boolean isRunning()
    {
        return state == State.RUNNING;
    }

    /**
     * Detect if the jvm matches the by criteria with his associated value
     *
     * @param jvm the jvm to be matched
     * @param by the matching method
     * @param value the matching criteria value
     *
     * @return true if the jvm matches the criteria
     *
     * @throws InvalidParameterException
     */
    public static boolean matches(Jvm jvm, By by, String value) throws InvalidParameterException
    {
        return matches(jvm, new JvmSearchCriteria(by, value));
    }

    /**
     * Detect if the jvm matches the criteria
     *
     * @param jvm the jvm to be matched
     * @param criteria the matching criteria
     *
     * @return true if the jvm matches the criteria
     *
     * @throws InvalidParameterException
     */
    public static boolean matches(Jvm jvm, JvmSearchCriteria criteria)
    {
        JvmSearchCriterias singleCriteria = new JvmSearchCriterias();
        singleCriteria.add(criteria);

        return matches(jvm, singleCriteria);
    }

    /**
     * Detect if the jvm matches all the criteria
     *
     * @param jvm the jvm to be matched
     * @param criterias the matching criterias
     *
     * @return true if the jvm matches all the criterias
     *
     * @throws InvalidParameterException
     */
    public static boolean matches(Jvm jvm, JvmSearchCriterias criterias)
    {
        if(jvm == null)
        {
            throw new InvalidParameterException("jvm parameter is null. Unable to do any matching.");
        }
        return securedMatches(jvm, criterias);
    }

    /**
     * Detect if the jvm matches all the criterias
     *
     * @param jvm the jvm to be matched
     * @param criterias the matching criterias
     *
     * @return true if the jvm matches all the criterias
     *
     * @throws InvalidParameterException
     */
    private static boolean securedMatches(Jvm jvm, JvmSearchCriterias criterias)
    {
        boolean partialMatching = false;

        for(JvmSearchCriteria criteria : criterias.getAll())
        {
            String value = criteria.getValue();

            switch (criteria.getBy())
            {

                case VMNAME:
                    partialMatching = jvm.getName().toLowerCase().contains(value);
                    break;

                case VMID:
                    partialMatching = jvm.getPid().equalsIgnoreCase(value);
                    break;

                case VMSTATE:
                    partialMatching =  jvm.getStringState().equalsIgnoreCase(value);
                    break;

                default:
                    return false;

            }

            if(!partialMatching)
            {
                return false;
            }
        }

        return true;
    }

}
