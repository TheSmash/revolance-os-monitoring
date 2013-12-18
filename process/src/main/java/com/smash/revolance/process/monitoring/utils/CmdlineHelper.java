package com.smash.revolance.process.monitoring.utils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static java.io.File.createTempFile;

/**
 * User: wsmash
 * Date: 10/10/13
 * Time: 22:14
 */
public class CmdlineHelper
{
    private Logger LOG;

    private final String id;
    private boolean sync = false;
    private File     out;
    private String[] cmd;
    private File     dir;
    private Process  process;
    private File     in;
    private File     err;


    public CmdlineHelper() throws IOException
    {
        id = UUID.randomUUID().toString();
        LOG = Logger.getLogger(id);

        err = createTempFile ( "err-" + id, ".txt" );
        out = createTempFile( "out-" + id, ".txt" );
        in = createTempFile ( "in-"  + id, ".txt" );
    }

    public CmdlineHelper cmd(String... cmd)
    {
        this.cmd = cmd;
        return this;
    }

    public CmdlineHelper dir(File dir)
    {
        this.dir = dir;
        return this;
    }

    public CmdlineHelper dir(String target)
    {
        dir( new File( target ) );
        return this;
    }

    public CmdlineHelper sync()
    {
        sync = true;
        return this;
    }

    public CmdlineHelper exec() throws InterruptedException, IOException
    {
        ProcessBuilder pb = new ProcessBuilder(  );

        if(dir != null)
        {
            pb.directory( dir );
        }

        pb.command( cmd );

        pb.redirectError( ProcessBuilder.Redirect.to( err ) );
        pb.redirectOutput( ProcessBuilder.Redirect.to( out ) );
        pb.redirectInput( ProcessBuilder.Redirect.from( in ) );

        LOG.log(Level.INFO, "Executing cmd: " + cmd[0] + " from dir: " + dir  );
        LOG.log(Level.INFO, "Redirecting out to: " + out.getAbsolutePath() );
        LOG.log(Level.INFO, "Redirecting err to: " + err.getAbsolutePath() );

        Process process = pb.start();
        Thread.sleep(300);
        if(sync)
        {
            process.waitFor();
        }

        this.process = process;

        return this;
    }

    public int exitValue()
    {
        return process.exitValue();
    }

    public String id()
    {
        return id;
    }

    public CmdlineHelper write(String in) throws IOException
    {
        FileUtils.writeStringToFile( this.in, in );
        return this;
    }

    public CmdlineHelper waitInMs(int ms)
    {
        sleep( ms );
        return this;
    }


    public CmdlineHelper awaitOut(String line, int timeout) throws Exception
    {
        long mark = System.currentTimeMillis();
        timeout = timeout*1000;

        do
        {
            sleep( 1000 );
            if( outContains( line ) )
            {
                return this;
            }
        }
        while( (System.currentTimeMillis()-mark) <timeout );

        if( !outContains( line ) )
        {
            throw new Exception( "Unable to find: '" + line + "' in out" );
        }
        return this;
    }

    public boolean hasExited()
    {
        try
        {
            process.exitValue();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public CmdlineHelper kill()
    {
        process.destroy();
        return this;
    }

    public File getOut()
    {
        return out;
    }

    public File getErr()
    {
        return err;
    }

    public File getIn()
    {
        return in;
    }

    public String out() throws IOException
    {
        return FileUtils.readFileToString( getOut() );
    }

    public String err() throws IOException
    {
        return FileUtils.readFileToString( getErr() );
    }

    public CmdlineHelper withoutErrors() throws Exception
    {
        if(!err().isEmpty())
        {
            throw new Exception( "Process execution generated errors in file: " + err );
        }
        return this;
    }

    public CmdlineHelper awaitErr(String line, int timeout) throws Exception
    {
        long mark = System.currentTimeMillis();
        timeout = timeout*1000;

        do
        {
            if( errContains( line ) )
            {
                return this;
            }
            else
            {
                sleep( 1000 );
            }
        }
        while( (System.currentTimeMillis()-mark) <timeout );

        if( !errContains( line ) )
        {
            throw new Exception( "Unable to find: '" + line + "' in err" );
        }
        return this;
    }

    public boolean errContains(String line) throws IOException
    {
        return err().contains( line );
    }

    public boolean outContains(String line) throws IOException
    {
        return out().contains( line );
    }

    private void sleep(int seconds)
    {
        try
        {
            Thread.sleep( seconds );
        }
        catch (InterruptedException e)
        {
            // Ignore gently
        }
    }
}
