package com.smash.revolance.jvm.monitoring.statistics;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;

import java.util.Date;

/**
 * User: wsmash
 * Date: 20/11/13
 * Time: 22:41
 */
public class MemoryUsage extends Serie
{
    public static JstatCommand.Column[] columns = new JstatCommand.Column[]{JstatCommand.Column.S0C,
                                                                            JstatCommand.Column.S1C,
                                                                            JstatCommand.Column.EC,
                                                                            JstatCommand.Column.OC,
                                                                            JstatCommand.Column.PC};
    public MemoryUsage(Series series)
    {
        super( );
        for(Date date : series.getDates(JstatCommand.Column.EC))
        {
            float data = 0;
            for(JstatCommand.Column column : columns)
            {
                Serie serie = series.getSerie( column );
                String stringData = serie.getDataAt( date.getTime() );

                data += Float.valueOf( stringData );
            }
            addSample( date, String.valueOf( data ) );
        }
    }

}
