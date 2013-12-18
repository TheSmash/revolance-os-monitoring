package com.smash.revolance.commons;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by ebour on 15/12/13.
 */
public class SerieTest
{
    @Test
    public void serieShouldRetrieveDataOnlySinceTimestamp()
    {
        Serie serie = new Serie("testSerie");
        serie.addSample(1, 1);
        serie.addSample(2, 1);
        serie.addSample(3, 1);

        assertThat(serie.getDatas(0).size(), is(3));
        assertThat(serie.getDatas(1).size(), is(3));
        assertThat(serie.getDatas(2).size(), is(2));
        assertThat(serie.getDatas(3).size(), is(1));
    }

    @Test
    public void groupOfSerieShouldRetrieveOnlyDataSinceTimeStamp()
    {
        Serie serie1 = new Serie("serie1");
        serie1.addSample(1, 1);
        serie1.addSample(2, 1);
        serie1.addSample(3, 1);

        Serie serie2 = new Serie("serie2");
        serie2.addSample(1, 2);
        serie2.addSample(2, 2);
        serie2.addSample(3, 2);

        Series series = new Series();
        series.addSerie(serie1.getLegend(), serie1);
        series.addSerie(serie2.getLegend(), serie2);

        assertThat(series.getDatas(serie1.getLegend(), 0).size(), is(3));
        assertThat(series.getDatas(serie2.getLegend(), 0).size(), is(3));

        assertThat(series.getDatas(serie1.getLegend(), 1).size(), is(3));
        assertThat(series.getDatas(serie2.getLegend(), 1).size(), is(3));

        assertThat(series.getDatas(serie1.getLegend(), 2).size(), is(2));
        assertThat(series.getDatas(serie2.getLegend(), 2).size(), is(2));

        assertThat(series.getDatas(serie1.getLegend(), 3).size(), is(1));
        assertThat(series.getDatas(serie2.getLegend(), 3).size(), is(1));
    }
}
