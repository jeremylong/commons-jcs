package org.apache.jcs.auxiliary.remote;

import java.util.Iterator;

import org.apache.jcs.auxiliary.remote.RemoteCacheManager;
import org.apache.jcs.auxiliary.remote.RemoteCacheNoWait;
import org.apache.jcs.auxiliary.remote.RemoteCacheRestore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used to monitor and repair any failed connection for the remote cache
 * service. By default the monitor operates in a failure driven mode. That is,
 * it goes into a wait state until there is an error. TODO consider moving this
 * into an active monitoring mode. Upon the notification of a connection error,
 * the monitor changes to operate in a time driven mode. That is, it attempts to
 * recover the connections on a periodic basis. When all failed connections are
 * restored, it changes back to the failure driven mode.
 *
 * @author asmuts
 * @created January 15, 2002
 */
public class RemoteCacheMonitor implements Runnable
{
    private final static Log log =
        LogFactory.getLog( RemoteCacheMonitor.class );

    private static RemoteCacheMonitor instance;
    private static long idlePeriod = 30 * 1000;
    // minimum 30 seconds.
    //private static long idlePeriod = 3*1000; // for debugging.

    // Must make sure RemoteCacheMonitor is started before any remote error can be detected!
    private boolean alright = true;

    final static int TIME = 0;
    final static int ERROR = 1;
    static int mode = ERROR;


    /**
     * Configures the idle period between repairs.
     *
     * @param idlePeriod The new idlePeriod value
     */
    public static void setIdlePeriod( long idlePeriod )
    {
        if ( idlePeriod > RemoteCacheMonitor.idlePeriod )
        {
            RemoteCacheMonitor.idlePeriod = idlePeriod;
        }
    }


    /** Constructor for the RemoteCacheMonitor object */
    private RemoteCacheMonitor() { }


    /**
     * Returns the singleton instance;
     *
     * @return The instance value
     */
    static RemoteCacheMonitor getInstance()
    {
        if ( instance == null )
        {
            synchronized ( RemoteCacheMonitor.class )
            {
                if ( instance == null )
                {
                    return instance = new RemoteCacheMonitor();
                }
            }
        }
        return instance;
    }


    /**
     * Notifies the cache monitor that an error occurred, and kicks off the
     * error recovery process.
     */
    public void notifyError()
    {
        log.debug( "Notified of an error." );
        bad();
        synchronized ( this )
        {
            notify();
        }
    }

    // Run forever.

    // Avoid the use of any synchronization in the process of monitoring for performance reason.
    // If exception is thrown owing to synchronization,
    // just skip the monitoring until the next round.
    /** Main processing method for the RemoteCacheMonitor object */
    public void run()
    {
        log.debug( "Monitoring daemon started" );
        do
        {

            if ( mode == ERROR )
            {
                if ( alright )
                {
                    synchronized ( this )
                    {
                        if ( alright )
                        {
                            // make this configurable, comment out wait to enter time driven mode
                            // Failure driven mode.
                            try
                            {
                                log.debug( "FAILURE DRIVEN MODE: cache monitor waiting for error" );
                                wait();
                                // wake up only if there is an error.
                            }
                            catch ( InterruptedException ignore )
                            {
                            }
                        }
                    }
                }
            }
            else
            {
                log.debug( "TIME DRIVEN MODE: cache monitor sleeping for " + idlePeriod );
                // Time driven mode: sleep between each round of recovery attempt.
                // will need to test not just check status
            }

            try
            {
                Thread.currentThread().sleep( idlePeriod );
            }
            catch ( InterruptedException ex )
            {
                // ignore;
            }

            // The "alright" flag must be false here.
            // Simply presume we can fix all the errors until proven otherwise.
            synchronized ( this )
            {
                alright = true;
            }
            //p("cache monitor running.");
            // Monitor each RemoteCacheManager instance one after the other.
            // Each RemoteCacheManager corresponds to one remote connection.
            for ( Iterator itr = RemoteCacheManager.instances.values().iterator(); itr.hasNext();  )
            {
                RemoteCacheManager mgr = ( RemoteCacheManager ) itr.next();
                try
                {
                    // If any cache is in error, it strongly suggests all caches managed by the
                    // same RmicCacheManager instance are in error.  So we fix them once and for all.
                    for ( Iterator itr2 = mgr.caches.values().iterator(); itr2.hasNext();  )
                    {
                        if ( itr2.hasNext() )
                        {
                            RemoteCacheNoWait c = ( RemoteCacheNoWait ) itr2.next();
                            if ( c.getStatus() == c.STATUS_ERROR )
                            {
                                RemoteCacheRestore repairer = new RemoteCacheRestore( mgr );
                                // If we can't fix them, just skip and re-try in the next round.
                                if ( repairer.canFix() )
                                {
                                    repairer.fix();
                                }
                                else
                                {
                                    bad();
                                }
                                break;
                            }
                        }
                    }
                }
                catch ( Exception ex )
                {
                    bad();
                    // Problem encountered in fixing the caches managed by a RemoteCacheManager instance.
                    // Soldier on to the next RemoteCacheManager instance.
                    log.error( ex );
                }
            }
        } while ( true );
    }


    /** Sets the "alright" flag to false in a critial section. */
    private void bad()
    {
        if ( alright )
        {
            synchronized ( this )
            {
                alright = false;
            }
        }
    }
}
