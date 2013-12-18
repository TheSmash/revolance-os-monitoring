package com.smash.revolance.filesystem.monitoring;

import com.smash.revolance.commons.Series;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by ebour on 17/12/13.
 */
public class FileSytemMonitoringTest
{
    private static File target = new File("target/root");
    private static File aFile;
    private static FileSystemMonitoring monitoring;
    private Series samples;

    @BeforeClass
    public static void setupTests() throws IOException
    {
        if(target.exists())
        {
            FileUtils.forceDelete(target);
        }
        target.mkdirs();

        aFile = new File(target, "aFile.txt");

        monitoring = new FileSystemMonitoring(target);
    }

    @Before
    public void setupTest() throws IOException, InterruptedException
    {
        aFile.delete();
        monitoring.clearSamples();
        monitoring.watch();
    }

    @After
    public void teardownTest() throws InterruptedException
    {
        monitoring.stop();
    }

    @Test
    public void fileSystemMonitoringShouldDetectFileCreation() throws InterruptedException, IOException
    {
        aFile.createNewFile();
        Thread.sleep(1000);

        samples = monitoring.listChanges(0);

        assertThat(samples.size(), is(1));
        assertThat(samples.getSerie("target/root/").size(), is(1));
        assertThat((String) samples.getSerie("target/root/").getDatas(0).get(0), equalTo("A target/root/aFile.txt"));
    }

    @Test
    public void fileSystemMonitoringShouldDetectFileUpdate() throws InterruptedException, IOException
    {
        aFile.createNewFile();
        FileUtils.writeStringToFile(aFile, "foo");
        Thread.sleep(1000);

        samples = monitoring.listChanges(0);

        assertThat(samples.size(), is(1));
        assertThat(samples.getSerie("target/root/").size(), is(3));
        assertThat((String) samples.getSerie("target/root/").getDatas(0).get(0), equalTo("A target/root/aFile.txt"));
        assertThat((String) samples.getSerie("target/root/").getDatas(0).get(1), equalTo("U target/root/aFile.txt"));
        assertThat((String) samples.getSerie("target/root/").getDatas(0).get(2), equalTo("U target/root/aFile.txt"));
    }

    @Test
    public void fileSystemMonitoringShouldDetectFileDeletion() throws InterruptedException, IOException
    {
        aFile.createNewFile();
        aFile.delete();
        Thread.sleep(2000);
        samples = monitoring.listChanges(0);

        assertThat(samples.size(), is(1));
        assertThat(samples.getSerie("target/root/").size(), is(2));
        assertThat((String) samples.getSerie("target/root/").getDatas(0).get(0), equalTo("A target/root/aFile.txt"));
        assertThat((String) samples.getSerie("target/root/").getDatas(0).get(1), equalTo("D target/root/aFile.txt"));
    }

    @Test
    public void fileSystemMonitoringShouldHandleAFileInSubFolder() throws InterruptedException, IOException
    {
        // Create the subfolder
        File aSubFolder = new File(target, "subFolder");
        aSubFolder.mkdir();
        Thread.sleep(2000);

        // List the fs changes
        samples = monitoring.listChanges(0);

        // Verify the events retrieved
        assertThat(samples.size(), is(2));
        assertThat(samples.getSerie("target/root/subFolder/").size(), is(0));
        assertThat(samples.getSerie("target/root/").size(), is(1));
        assertThat((String) samples.getSerie("target/root/").getDatas(0).get(0), equalTo("A target/root/subFolder"));

        // Create a file in the subfolder
        File aFileInSubFolder = new File(aSubFolder, "aFile.txt");
        aFileInSubFolder.createNewFile();
        Thread.sleep(2000);

        // List the fs changes
        samples = monitoring.listChanges(0);

        // Verify the events retrieved
        assertThat(samples.getSerie("target/root/subFolder/").size(), is(1));
        assertThat((String) samples.getSerie("target/root/subFolder/").getDatas(0).get(0), equalTo("A target/root/subFolder/aFile.txt"));

        // Update the file content in the subfolder
        FileUtils.writeStringToFile(aFileInSubFolder, "foo");
        Thread.sleep(1000);

        // List the fs changes
        samples = monitoring.listChanges(0);

        // Verify the events retrieved
        assertThat(samples.getSerie("target/root/subFolder/").size(), is(3));
        assertThat((String) samples.getSerie("target/root/subFolder/").getDatas(0).get(1), equalTo("U target/root/subFolder/aFile.txt"));
        assertThat((String) samples.getSerie("target/root/subFolder/").getDatas(0).get(2), equalTo("U target/root/subFolder/aFile.txt"));

        // Delete the file in the subfolder
        aFileInSubFolder.delete();
        Thread.sleep(1000);

        // List the fs changes
        samples = monitoring.listChanges(0);

        // Verify the events retrieved
        assertThat(samples.getSerie("target/root/subFolder/").size(), is(4));
        assertThat((String) samples.getSerie("target/root/subFolder/").getDatas(0).get(3), equalTo("D target/root/subFolder/aFile.txt"));


        // Delete the subFolder
        aSubFolder.delete();
        Thread.sleep(1000);

        // List the fs changes
        samples = monitoring.listChanges(0);

        // Verify the events retrieved
        assertThat(samples.getSerie("target/root/").size(), is(2));
        assertThat((String) samples.getSerie("target/root/").getDatas(0).get(2), equalTo("D target/root/subFolder"));
    }
}
