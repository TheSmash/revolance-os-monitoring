package com.smash.revolance.filesystem.monitoring;

import com.smash.revolance.commons.Sample;
import com.smash.revolance.commons.Serie;
import com.smash.revolance.commons.Series;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wsmash on 16/11/13.
 */
public class FileSystemMonitoring implements Runnable
{
    private final Path root;
    private final String relPath;


    private final static Logger LOG = Logger.getLogger(FileSystemMonitoring.class);
    private final FileSystemMonitoring parent;
    private Serie samples;
    private final Map<String, FileSystemMonitoring> watchers = new HashMap<String, FileSystemMonitoring>();

    private boolean stop = false;
    private WatchService watcher;

    public FileSystemMonitoring(FileSystemMonitoring parent, File root) throws IOException
    {
        this.parent = parent;
        this.relPath = root.getPath()+"/";
        this.root = Paths.get(root.getAbsolutePath());
        this.samples = new Serie(relPath);

        watcher = this.root.getFileSystem().newWatchService();

        this.root.register(watcher,  StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY  );

        listPreExistingSubDirectories();
    }

    public FileSystemMonitoring(File target) throws IOException
    {
        this(null, target);
    }

    private List<File> getSubDirectories(File root) throws IOException
    {
        final List<File> subDirectories = new ArrayList<File>();

        for(File file : root.listFiles())
        {
            if(file.isDirectory())
            {
                String fileName = file.getAbsolutePath();
                File relDir = new File(fileName.substring(fileName.indexOf(relPath)));
                subDirectories.add(relDir);
            }
        }

        return subDirectories;
    }

    public void watch() throws IOException, InterruptedException
    {
        stop = false;
        new Thread(this, "Watching["+relPath+"]").start();
    }

    private void _stop()
    {
        this.stop = true;
    }

    public synchronized void clearSamples()
    {
        for(FileSystemMonitoring watcher : watchers.values())
        {
            watcher.clearSamples();
        }
        this.samples.clear();
    }

    public synchronized void stop()
    {
        for(FileSystemMonitoring watcher : watchers.values())
        {
            watcher._stop();
        }
        watchers.clear();
        _stop();
    }

    @Override
    public void run()
    {
        listPreExistingSubDirectories();

        listenToFileSystemEvents();
    }

    private void listenToFileSystemEvents()
    {
        try
        {

            while(!stop)
            {

                WatchKey watckKey = watcher.take();

                List<WatchEvent<?>> events = watckKey.pollEvents();
                Sample sample = null;
                for (WatchEvent event : events)
                {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                    {
                        sample = handleCreation(event);
                    }
                    else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
                    {
                        sample = handleDeletion(event);
                    }
                    else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
                    {
                        sample = handleUpdate(event);
                    }

                    if(sample != null)
                    {
                        samples.addSample(sample);
                    }
                }

                watckKey.reset();

                if(!watckKey.isValid())
                {
                    stop = true;
                    if(this.parent!=null)
                        this.parent.remove(getPath()+"/");
                }
            }

        }
        catch (Exception e)
        {
            LOG.warn("Error: " + e.toString());
        }
    }

    private void remove(String path)
    {
        if(watchers.containsKey(path))
        {
            watchers.remove(path);
        }
    }

    private void listPreExistingSubDirectories()
    {
        try
        {
            for(File subDir : getSubDirectories(root.toFile()))
            {
                FileSystemMonitoring watcher = new FileSystemMonitoring(this, subDir);
                if(!watchers.containsKey(subDir.getPath()))
                {
                    watchers.put(subDir.getPath(), watcher);
                    watcher.watch();
                }
            }
        }
        catch (Exception e)
        {
            System.err.print(e);
        }
    }

    private Sample handleUpdate(WatchEvent event)
    {
        Sample sample;
        sample = new Sample(System.currentTimeMillis(), "U "+ getPath() +event.context().toString());
        return sample;
    }

    private Sample handleDeletion(WatchEvent event)
    {
        Sample sample;
        sample = new Sample(System.currentTimeMillis(), "D "+ getPath() +event.context().toString());
        File oldDir = new File(relPath, event.context().toString());
        if(oldDir.isDirectory())
        {
            if(watchers.containsKey(oldDir.getPath()))
            {
                watchers.get(oldDir).stop();
                watchers.remove(oldDir);
            }
        }
        return sample;
    }

    private Sample handleCreation(WatchEvent event) throws IOException, InterruptedException
    {
        Sample sample;
        sample = new Sample(System.currentTimeMillis(), "A "+ getPath() +event.context().toString());
        File newDir = new File(relPath, event.context().toString());
        if(newDir.isDirectory())
        {
            if(!watchers.containsKey(newDir.getPath()))
            {
                FileSystemMonitoring monitoring = new FileSystemMonitoring(this, newDir);
                watchers.put(newDir.getPath(), monitoring);
                monitoring.watch();
            }
        }
        return sample;
    }

    private String getPath()
    {
        return relPath;
    }

    public synchronized Series listChanges(long since)
    {
        Series series = new Series();

        series.addSerie(relPath, samples);

        for (Serie serie: getSeries().values())
        {
            series.addSerie(serie.getLegend(), serie);
        }

        return series;
    }

    public Serie getSamples()
    {
        return samples;
    }

    public synchronized Map<String, Serie> getSeries()
    {
        Map<String, Serie> series = new HashMap<String, Serie>();
        for (FileSystemMonitoring watcher : watchers.values())
        {
            series.put(watcher.getPath(), watcher.getSamples());
            series.putAll(watcher.getSeries());
        }
        return series;
    }
}
