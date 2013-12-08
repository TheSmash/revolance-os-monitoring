package com.smash.revolance.filesystem.monitoring;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ebour on 16/11/13.
 */
public class FilesystemObserver implements Runnable
{
    private final Path root;

    private final static Logger LOG = Logger.getLogger(FilesystemObserver.class);
    private final static File changeLog = new File("fsChangeLog.log");

    public FilesystemObserver(File root) throws IOException
    {
        this.root = Paths.get(root.getAbsolutePath());
        LOG.addAppender(new FileAppender(new Layout(){

            @Override
            public String format(LoggingEvent event)
            {
                return System.currentTimeMillis() + " - " + event.getMessage();
            }

            @Override
            public boolean ignoresThrowable()
            {
                return false;
            }

            @Override
            public void activateOptions()
            {

            }

        }, changeLog.getAbsolutePath()));
        run();
    }


    @Override
    public void run()
    {
        try
        {
            WatchService watcher = root.getFileSystem().newWatchService();
            root.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                                   StandardWatchEventKinds.ENTRY_DELETE,
                                   StandardWatchEventKinds.ENTRY_MODIFY  );

            WatchKey watckKey = watcher.take();

            List<WatchEvent<?>> events = watckKey.pollEvents();
            for (WatchEvent event : events) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                {
                    LOG.info("Created: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
                {
                    LOG.info("Delete: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
                {
                    LOG.info("Modify: " + event.context().toString());
                }
            }

        }
        catch (Exception e)
        {
            LOG.warn("Error: " + e.toString());
        }
    }

    public List<File> listFileChanges(long since) throws IOException
    {
        Pattern pattern = Pattern.compile("^(\\w+) ([^ ]+)");

        List<File> files = new ArrayList<File>();
        for(String change : FileUtils.readFileToString(changeLog).split("\\n"))
        {
            Matcher matcher = pattern.matcher(change);
            matcher.matches();

            if(matcher.groupCount()==2)
            {
                long timestamp = Long.parseLong(matcher.group(1));

                if(timestamp>since)
                {
                    File file = new File(matcher.group(2));
                    files.add( file );
                }
            }
        }

        return files;
    }
}
