package com.smash.revolance.jvm.monitoring.jvm;

/**
 * Created by ebour on 14/12/13.
 */
public enum  JvmState
{
    /**
     * Any new instance of this class is jvm in RUNNING state
     */
    RUNNING,

    /**
     * When the JvmWatcher pid is not visible (through jps command)
     * then the jvm state is STOPPED
     */
    STOPPED;
}
