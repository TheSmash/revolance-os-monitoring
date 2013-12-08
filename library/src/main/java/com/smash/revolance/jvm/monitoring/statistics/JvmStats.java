package com.smash.revolance.jvm.monitoring.statistics;

import com.smash.revolance.jvm.monitoring.commands.JstatCommand;
import com.smash.revolance.jvm.monitoring.jvm.Jvm;
import com.smash.revolance.jvm.monitoring.statistics.formulas.ReduceOperator;
import com.smash.revolance.jvm.monitoring.utils.Serie;
import com.smash.revolance.jvm.monitoring.utils.Series;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ebour on 08/12/13.
 */
public class JvmStats
{
    private static final Logger LOG = Logger.getLogger(JvmStats.class);

    private final long startTime;
    private final File statsFile;

    public JvmStats(Jvm jvm) throws IOException
    {
        this.statsFile = jvm.getStatiticsFile();
        this.startTime = jvm.getStartTimestamp();
    }

    public Stats getSeries(long since, ReduceOperator... reduceOperators) throws Exception
    {
        Stats stats = new Stats();

        for(ReduceOperator formula : reduceOperators)
        {
            try
            {
                stats.addSeries(formula.getLabel(), formula.apply(getRawSeries(since), since));
            }
            catch (IOException e)
            {
                LOG.error("Unable to read file: " + getStatiticsFile(), e);
            }
        }

        return stats;
    }

    private Series getRawSeries(long since) throws IOException
    {
        String[] statistics = FileUtils.readFileToString(getStatiticsFile()).split("\\n");

        String header = "";
        if (statistics.length > 0) {
            header = statistics[0];
        }

        List<Serie> columns = new ArrayList<Serie>();
        Series series = new Series(String.valueOf(JstatCommand.Option.GC));
        boolean first = true;
        for (String col : header.split(" ")) {
            if (col.isEmpty())
                continue;

            if (first) {
                first = false; // Do not add the timestamp column
            } else {
                Serie serie = new Serie(JstatCommand.Column.getLegend(col));
                columns.add(serie);

                series.addSerie(col, serie);
            }
        }

        first = true;
        for (String sample : statistics) {
            if (first) {
                first = false; // Do not handle the header column
            } else {
                String[] samples = sample.split(" ");
                boolean dateFound = false;
                Date date = null;
                Iterator<Serie> it = columns.iterator();
                for (int sampleIdx = 0; sampleIdx < samples.length; sampleIdx++) {
                    String data = samples[sampleIdx];
                    if (data.isEmpty())
                        continue;

                    if (!dateFound) {
                        long timestamp = getStartTime() + Integer.parseInt(data.split(",")[0]);
                        date = new Date(timestamp);
                        dateFound = true;
                    } else {
                        if (date.getTime() > since) {
                            Serie serie = it.next();
                            serie.addSample(date, data);
                        }
                    }
                }
            }
        }

        return series;
    }

    public File getStatiticsFile()
    {
        return statsFile;
    }

    public long getStartTime()
    {
        return startTime;
    }
}
