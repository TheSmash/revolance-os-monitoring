package com.smash.revolance.process.monitoring.jvm;

import com.smash.revolance.process.monitoring.jvm.filter.By;
import com.smash.revolance.process.monitoring.jvm.filter.JvmSearchCriteria;
import com.smash.revolance.process.monitoring.jvm.filter.JvmSearchCriterias;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jvm
{
    private long start = 0;

    private String id = "";
    private String name = "";
    private Map<String, String> options = new HashMap();
    private JvmState state;

    private Jvm()
    {
        // If this instance is created it's because the OS says that it's alive
        start = System.currentTimeMillis();
        this.state = JvmState.RUNNING;
    }

    public Jvm(String id, String name)
    {
        this();
        this.id = id;
        this.name = name;
    }

    public long getStartTime() {
        return start;
    }

    public void addOption(String option)
    {
        String[] keyAndValue = option.split("=");
        options.put(keyAndValue[0], keyAndValue.length>1?keyAndValue[1]:"");
    }

    public Jvm setName(String id) {
        if (id != null)
            this.name = id;
        return this;
    }

    public Jvm setId(String id) {
        if (id != null)
            this.id = id;
        return this;
    }

    public void setState(JvmState state) {
        if (state != null)
            this.state = state;
    }

    /**
     * Retrieve the jvm pid from jps
     * For instance: '16567'
     *
     * @return the jvm unique pid
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieve the jvm name from the jps command
     * For instance 'Jps'
     *
     * @return the jvm name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the jvm options from the jps command
     * For instance: '-Xms128m, -Xmx750m, -XX:MaxPermSize=350m'
     *
     * @return the jvm options.
     */
    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * Retrieve the state of this jvm.
     *
     * @return
     */
    public JvmState getState() {
        return state;
    }

    /**
     * Retrieve the state of this jvm as a string.
     *
     * @return
     */
    public String getStringState() {
        return String.valueOf(getState());
    }

    /**
     * @return true if the jvm state is RUNNING
     */
    public boolean isRunning() {
        return state == JvmState.RUNNING;
    }

    /**
     * Detect if the jvmWatcher matches the by criteria with his associated value
     *
     * @param jvm the jvmWatcher to be matched
     * @param by         the matching method
     * @param value      the matching criteria value
     * @return true if the jvmWatcher matches the criteria
     * @throws java.security.InvalidParameterException
     */
    public static boolean matches(Jvm jvm, By by, String value) throws InvalidParameterException {
        return matches(jvm, new JvmSearchCriteria(by, value));
    }

    /**
     * Detect if the jvmWatcher matches the criteria
     *
     * @param jvm the jvmWatcher to be matched
     * @param criteria   the matching criteria
     * @return true if the jvmWatcher matches the criteria
     * @throws java.security.InvalidParameterException
     */
    public static boolean matches(Jvm jvm, JvmSearchCriteria criteria) {
        JvmSearchCriterias singleCriteria = new JvmSearchCriterias();
        singleCriteria.add(criteria);

        return matches(jvm, singleCriteria);
    }

    /**
     * Detect if the jvmWatcher matches all the criteria
     *
     * @param jvm the jvmWatcher to be matched
     * @param criterias  the matching criterias
     * @return true if the jvmWatcher matches all the criterias
     * @throws java.security.InvalidParameterException
     */
    public static boolean matches(Jvm jvm, JvmSearchCriterias criterias) {
        if (jvm == null) {
            throw new InvalidParameterException("jvm parameter is null. Unable to do any matching.");
        }
        return securedMatches(jvm, criterias);
    }

    /**
     * Detect if the jvmWatcher matches all the criterias
     *
     * @param jvm the jvmWatcher to be matched
     * @param criterias  the matching criterias
     * @return true if the jvmWatcher matches all the criterias
     * @throws java.security.InvalidParameterException
     */
    static boolean securedMatches(Jvm jvm, JvmSearchCriterias criterias) {
        boolean partialMatching = false;

        for (JvmSearchCriteria criteria : criterias.getAll()) {
            String value = criteria.getValue();

            switch (criteria.getBy()) {

                case VMNAME:
                    Pattern pattern = Pattern.compile(value);
                    Matcher matcher = pattern.matcher(jvm.getName());
                    partialMatching = matcher.matches();
                    break;

                case VMID:
                    partialMatching = jvm.getId().equalsIgnoreCase(value);
                    break;

                case VMSTATE:
                    partialMatching = jvm.getStringState().equalsIgnoreCase(value);
                    break;

                default:
                    return false;

            }

            if (!partialMatching) {
                return false;
            }
        }

        return true;
    }

    public String toString()
    {
        return String.format("%s[%s]", name, id);
    }

}