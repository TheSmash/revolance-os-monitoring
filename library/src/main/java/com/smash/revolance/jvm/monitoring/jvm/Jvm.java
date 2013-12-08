package com.smash.revolance.jvm.monitoring.jvm;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;
import com.smash.revolance.jvm.monitoring.jvm.filter.By;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriteria;
import com.smash.revolance.jvm.monitoring.jvm.filter.JvmSearchCriterias;
import com.smash.revolance.jvm.monitoring.utils.CmdlineHelper;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * Created by ebour on 16/11/13.
 */
public class Jvm
{
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

    public Map<String, Serie> getSeries(long since) throws IOException
    {
        String[] statistics = FileUtils.readFileToString(getStatiticsFile()).split("\\n");

        String header = "";
        if(statistics.length>0)
        {
            header = statistics[0];
        }

        List<Serie> columns = new ArrayList<Serie>();
        Map<String, Serie> series = new HashMap<String, Serie>();
        boolean first = true;
        for(String col : header.split(" "))
        {
            if(col.isEmpty())
                continue;

            if(first)
            {
                first = false; // Do not add the timestamp column
            }
            else
            {
                Serie serie = new Serie(col, JstatCommand.Column.getLegend(col));
                columns.add( serie );

                series.put(col, serie);
            }
        }

        first = true;
        for(String sample : statistics)
        {
            if(first)
            {
                first = false; // Do not handle the header column
            }
            else
            {
                String[] samples = sample.split(" ");
                boolean dateFound = false;
                Date date = null;
                Iterator<Serie> it = columns.iterator();
                for(int sampleIdx = 0; sampleIdx < samples.length; sampleIdx++)
                {
                    String data = samples[sampleIdx];
                    if(data.isEmpty())
                        continue;

                    if(!dateFound)
                    {
                        long timestamp = getStartTimestamp()+Integer.parseInt(data.split(",")[0]);
                        date = new Date(timestamp);
                        dateFound=true;
                    }
                    else
                    {
                        if(date.getTime()>since)
                        {
                            Serie serie = it.next();
                            serie.addSample( date, data );
                        }
                    }
                }
            }
        }

        return series;
    }


}
