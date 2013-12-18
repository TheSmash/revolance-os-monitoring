package com.smash.revolance.monitoring;

import com.smash.revolance.commons.Series;
import com.smash.revolance.filesystem.monitoring.FileSystemMonitoring;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;


@Service
@Controller
/**
 * Created by wsmash on 27/10/13.
 */
public class FileSystemController
{
    public static FileSystemMonitoring monitoring;

    static
    {
        String root = System.getProperty("fs.root", "");
        try
        {
            monitoring = new FileSystemMonitoring(new File(root));
            monitoring.watch();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/api/filesystem", method = RequestMethod.GET, headers="Accept=application/json")
    public Series listFsChanges(@RequestParam(value = "since", defaultValue = "0") final long since) throws IOException
    {
        return monitoring.listChanges(since);
    }

}
