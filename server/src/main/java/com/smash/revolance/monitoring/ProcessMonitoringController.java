package com.smash.revolance.monitoring;

import com.smash.revolance.NullJvm;
import com.smash.revolance.commons.Serie;
import com.smash.revolance.commons.Series;
import com.smash.revolance.process.monitoring.JvmMonitoring;
import com.smash.revolance.process.monitoring.jvm.Jvm;
import com.smash.revolance.process.monitoring.jvm.Jvms;
import com.smash.revolance.process.monitoring.jvm.filter.By;
import com.smash.revolance.process.monitoring.jvm.filter.JvmSearchCriteria;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

@Service
@Controller
/**
 * Created by wsmash on 27/10/13.
 */
public class ProcessMonitoringController
{
    private static Jvm mock = new Jvm("vmid", "vmname");

    public static final JvmMonitoring monitoring;

    static
    {
        String includes = System.getProperty("jvm.includes", "");
        String excludes = System.getProperty("jvm.excludes", "");
        monitoring = new JvmMonitoring(includes, excludes);
        monitoring.watch();
    }

    @ResponseBody
    @RequestMapping(value = "/api/processes", method = RequestMethod.GET, headers="Accept=application/json")
    public List<Jvm> listJvms(@RequestParam(value = "simulate", defaultValue = "false")  final boolean simulate) throws IOException
    {
        if(simulate)
            return asList(mock);

        return monitoring.listJvms();
    }

    @ResponseBody
    @RequestMapping(value = "/api/processes/{pid}", method = RequestMethod.GET, headers="Accept=application/json")
    public Jvm getJvm(@PathVariable("pid") final String vmid, @RequestParam(value = "since", defaultValue = "0") final long since, @RequestParam(value = "simulate", defaultValue = "false") final boolean simulate) throws IOException
    {
        List<Jvm> jvms = Jvms.find(listJvms(simulate), new JvmSearchCriteria(By.VMID, vmid));
        if(jvms.isEmpty())
        {
            jvms.add(new NullJvm());
        }
        return jvms.get(0);
    }

    @ResponseBody
    @RequestMapping(value = "/api/processes/{pid}/metrics", method = RequestMethod.GET, headers="Accept=application/json")
    public Series getJvmMetrics(@PathVariable("pid") final String vmid, @RequestParam(value = "since", defaultValue = "0") final long since, @RequestParam(value = "simulate", defaultValue = "false")  final boolean simulate) throws Exception
    {
        Jvm jvm = getJvm(vmid, since, simulate);
        Series series = monitoring.getMetrics(jvm).getSeries(since);
        return series;
    }

    @ResponseBody
    @RequestMapping(value = "/api/processes/{pid}/metrics/{metric}", method = RequestMethod.GET, headers="Accept=application/json")
    public Serie getJvmMetric(@PathVariable("pid") final String vmid, @PathVariable("metric") final String metric, @RequestParam(value = "since", defaultValue = "0") final long since, @RequestParam(value = "simulate", defaultValue = "false") final boolean simulate) throws Exception
    {
        Jvm jvm = getJvm(vmid, since, simulate);
        Serie serie = monitoring.getMetrics(jvm).getSerie(metric, since);
        return serie;
    }

    @RequestMapping(value = "/", headers="Accept=text/html")
    public ModelAndView displayInterface() throws IOException
    {
        return new ModelAndView("ProcessList");
    }

}
