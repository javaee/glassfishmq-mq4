/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2000-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * @(#)Broker.java	1.287 07/10/07
 */ 

package com.sun.messaging.jmq.jmsserver;

import java.util.*;
import java.text.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.net.*;
import java.lang.reflect.*;

import com.sun.messaging.jmq.util.StringUtil;
import com.sun.messaging.jmq.Version;
import com.sun.messaging.jmq.io.*;
import com.sun.messaging.jmq.util.BrokerExitCode;
import com.sun.messaging.jmq.util.Password;
import com.sun.messaging.jmq.util.ServiceType;
import com.sun.messaging.jmq.util.timer.MQTimer;
import com.sun.messaging.jmq.util.log.Logger;
import com.sun.messaging.jmq.util.Rlimit;
import com.sun.messaging.jmq.util.DiagManager;
import com.sun.messaging.jmq.util.DiagDictionaryEntry;
import com.sun.messaging.jmq.util.FileUtil;
import com.sun.messaging.jmq.util.selector.Selector;
import com.sun.messaging.jmq.jmsclient.runtime.impl.BrokerInstanceImpl;
import com.sun.messaging.jmq.jmsserver.auth.*;
import com.sun.messaging.jmq.jmsserver.auth.file.JMQFileUserRepository;
import com.sun.messaging.jmq.jmsserver.auth.acl.JMQFileAccessControlModel;
import com.sun.messaging.jmq.jmsserver.data.PacketRouter;
import com.sun.messaging.jmq.jmsserver.data.TransactionList;
import com.sun.messaging.jmq.jmsserver.core.Queue;
import com.sun.messaging.jmq.jmsserver.core.*;
import com.sun.messaging.jmq.jmsserver.util.*;
import com.sun.messaging.jmq.jmsserver.util.pool.*;
import com.sun.messaging.jmq.jmsserver.service.*;
import com.sun.messaging.jmq.jmsserver.config.*;
import com.sun.messaging.jmq.jmsserver.cluster.*;
import com.sun.messaging.jmq.jmsserver.multibroker.heartbeat.HeartbeatService;
import com.sun.messaging.jmq.jmsserver.net.tcp.*;
import com.sun.messaging.jmq.jmsserver.net.tls.*;
import com.sun.messaging.jmq.jmsserver.data.handlers.*;
import com.sun.messaging.jmq.jmsserver.data.handlers.admin.AdminDataHandler;
import com.sun.messaging.jmq.jmsserver.resources.*;
import com.sun.messaging.jmq.jmsserver.persist.Store;
import com.sun.messaging.jmq.jmsserver.persist.StoreManager;
import com.sun.messaging.jmq.jmsserver.persist.sharecc.ShareConfigChangeStore;
import com.sun.messaging.jmq.jmsserver.core.BrokerAddress;
import com.sun.messaging.jmq.jmsserver.core.BrokerMQAddress;
import com.sun.messaging.jmq.jmsserver.license.*;
import com.sun.messaging.jmq.jmsserver.management.agent.Agent;
import com.sun.messaging.jmq.jmsserver.data.protocol.*;

import com.sun.messaging.jmq.jmsservice.BrokerEvent;
import com.sun.messaging.jmq.jmsservice.BrokerEventListener;

import com.sun.messaging.bridge.BridgeServiceManager;

import com.sun.messaging.jmq.jmsserver.audit.MQAuditSession;

/**
 * The Main class for the broker.
 *
 * Currently the broker supports a single protocol
 * running on a single port, however this class could
 * be changed to allow additional protocols/ports at
 * a later time 
 */
public class Broker implements GlobalErrorHandler {

    public static Broker broker = null;

    public boolean allowHA = true;

    public static boolean NO_CLUSTER = false;
    public static boolean NO_HA = false;

    private static boolean DEBUG = false;

    int DEFAULT_CLUSTER_VERSION = ClusterBroadcast.VERSION_410;

    /**
     * message bus - handles communication 
     */
    private ClusterBroadcast mbus = null;

    /**
     * Minimum Java version we need to run
     */
    private static final String MIN_JAVA_VERSION = "1.5";


    /**
     * main packet router -> contains routines to run based
     *      on messages  
     */
    private PacketRouter pktrtr = null;


    /**
     * admin packet router -> contains routines to run based
     *      on messages  
     */
    private PacketRouter admin_pktrtr = null;


    /**
     * Transaction list
     */
    private TransactionList tlist = null;


    /**
     * main tcp protocol
     */
    private BrokerResources rb = null;
    private Version version = null;
    private Store store = null;
    private ShareConfigChangeStore shareCCStore = null;
    private boolean clearProps = false;
    private boolean saveProps = false;
    private LicenseBase license = null;
    private String licenseToUse = null;

    private boolean resetTakeoverThenExit = false;

    private Logger logger = null;


    private String adminKeyFile = null;

    /**
     * Hashtable that maps debug aliases to classes. The debug aliases
     * are used to handle parameters to the -debug command line option
     */
    private Hashtable debugAliases = null;

    /**
     * Boolean used to verify that the broker is up and running
     */

    public boolean startupComplete = false;

    /* Shutdown hook */
    public Thread shutdownHook = null;

    /**
     * Used to pass Broker's exit status to NT service wrapper. When
     * we are running as an NT service we do NOT exit the JVM. We pass
     * the status to the service wrapper and let it terminate or restart
     * the VM.
     */
    private int exitStatus = 0;
    private Object exitStatusLock = new Object();

    /**
     * true if we are running as an NT service, else false
     */
    private boolean isNTService = false;

    /**
     * Interval at which to dump diagnostic information. -1 means
     * never dump. 0 means dump at startup and shutdown. Any positive
     * value n means number every n seconds.
     */
    private int diagInterval = -1;

    public boolean silent = false;
    public boolean force = false;

    /**
     * True if -bgnd was specified on the command line
     */
    public boolean background = false;
    public boolean initOnly = false;
    private boolean removeInstance = false;
    private boolean resetStore = false;

    private List<String> embeddedBrokerStartupMessages;
	
    private static BrokerEventListener bkrEvtListener = null;
    private static boolean runningInProcess = false;

    private String haltLogString = "HALT";


    public static boolean isInProcess() {
        return runningInProcess;
    }

	/**
	 * Specify a message that will be written to the broker logfile  
	 * when the broker starts as an INFO message. This is typically used to log the
	 * broker properties configured on an embedded broker, and so is logged immediately
	 * after its arguments are logged. However this method can be used for other
	 * messages which need to be logged by an embedded broker when it starts.
	 * 
	 * This can be called multiple times to specify
	 * multiple messages, each of which will be logged on a separate line.
	 * 
	 * @param embeddedBrokerStartupMessage
	 */
	public void addEmbeddedBrokerStartupMessage(String embeddedBrokerStartupMessage) {
		if (embeddedBrokerStartupMessages==null){
			embeddedBrokerStartupMessages=new ArrayList<String>();
		}
		embeddedBrokerStartupMessages.add(embeddedBrokerStartupMessage);
	}
    
    /**
     * Return whether this Broker is allowed to shutdown
     * @param restartRequested whether a subsequent restart would be performed
     * @return whether this Broker is allowed to shutdown
     */
    public static boolean imqcmdCanExit (boolean restartRequested) {  
       if (bkrEvtListener  != null) {
    	   // delegate the decision to the BrokerEventListener
    	   BrokerEvent event = null;
    	   if (restartRequested){
    		   event = new BrokerEvent (broker, BrokerEvent.Type.RESTART, "Broker is requesting restart");  
    	   } else {
    		   event = new BrokerEvent (broker, BrokerEvent.Type.SHUTDOWN, "Broker is requesting shutdown");  
    	   }
    	   return bkrEvtListener.exitRequested(event, null);
       }
       return !isInProcess();
    }

    public static void setIsInProcess( boolean inprocess)
    {
        runningInProcess = inprocess;
    }
    private static void setBrokerEventListener(BrokerEventListener listen) {
        bkrEvtListener = listen;
    }

    public static Broker getBroker() {
        synchronized (Broker.class) {
             if (broker == null)
                 broker = new Broker();
        }
        return broker;
    }

    public static void destroyBroker(boolean cleanup) {
    	boolean triggerFailover=true;
        destroyBroker(cleanup, triggerFailover);
    }

    /**
     * Shutdown the broker but don't exit the JVM
     *
     * @param cleanup Set to true if unused resources should be freed. 
     * Should be set to true if the JVM will be left running.
     * May be set to false if we intend to exit the JVM later
     * 
     * @param triggerFailover
     */
    public static void destroyBroker(boolean cleanup, boolean triggerFailover) {
       // we want to cleanup all the statics
       if (broker == null) return;
       Object tmp = broker;

       if (Globals.getBrokerStateHandler() != null)
           Globals.getBrokerStateHandler().initiateShutdown(
                  "BrokerProcess", 0, triggerFailover, 0,
                  false, false, true);

       if (cleanup) {
           BrokerMonitor.shutdownMonitor();
           Consumer.clearAllConsumers();
           Destination.clearDestinations();
           DestinationUID.clearCache();
           Producer.clearProducers();
           Session.clearSessions();
           Subscription.clearSubscriptions();
           LockFile.clearLock();
           TLSProtocol.destroy();

   	   // stop JMX connectors
	   Agent agent = Globals.getAgent();
	   if (agent != null)  {
	       agent.stop();
	       agent.unloadMBeans();
	   }
       
           PortMapper pm = Globals.getPortMapper();
     
           pm.destroy();
           Globals.cleanup();
           synchronized (Broker.class) {
                 broker = null;
           }
       }
       if (bkrEvtListener  != null) {
             BrokerEvent event = new BrokerEvent (tmp,
                    BrokerEvent.Type.SHUTDOWN, "Broker has been shutdown"); //L10N-XXX
             bkrEvtListener.brokerEvent(event);
          	 setBrokerEventListener(null);
       }
       
       if (BrokerInstanceImpl.getInstance()!=null){
    	   BrokerInstanceImpl.getInstance().setShutdown(true);
       }
    }

    private Broker() {
        version = Globals.getVersion();
        rb = Globals.getBrokerResources();
    }

    Properties convertArgs(String args[]) 
        throws IllegalStateException, EmptyStackException
    {
        // parse arguments
        Properties params = parseArgs(args);
        params.put("BrokerArgs", argsToString(args).trim());
        return params;
    }

    public static void initializePasswdFile()
        throws IOException
    {
        Logger logger = Globals.getLogger();
        BrokerConfig conf = Globals.getConfig();
        BrokerResources rb = Globals.getBrokerResources();
        //
        // INSTANCE BASED AUTHENTICATION AND AUTHORIZATION
        //
        // if (instance's etc directory does not exist) {
        //     if (global access control file exists) {
        //         copy pw & acl files;
        //     } else {
        //         create default pw and acl files;
        //     }
        // }

        // check existence of instance's etc directory
        File localetcdir = new File(Globals.getInstanceEtcDir());
        if (!localetcdir.exists()) {
            localetcdir.mkdir();

            String aclfilename = conf.getProperty(
                AccessController.PROP_ACCESSCONTROL_PREFIX
                + JMQFileAccessControlModel.PROP_FILENAME_SUFFIX,
                JMQFileAccessControlModel.DEFAULT_ACL_FILENAME);
            File gACL = new File(Globals.JMQ_ETC_HOME + File.separator
                    + aclfilename);
            File lACL = new File(localetcdir, aclfilename);

            String pwfilename = conf.getProperty(
                AccessController.PROP_USER_REPOSITORY_PREFIX
                + JMQFileUserRepository.PROP_FILENAME_SUFFIX,
                JMQFileUserRepository.DEFAULT_PW_FILENAME);
            File gPW = new File(Globals.JMQ_ETC_HOME + File.separator
                    + pwfilename);
            File lPW = new File(localetcdir, pwfilename);

            // check if global accesscontrol file exists
            if (gACL.exists()) {
                try {
                    // copy
                    RandomAccessFile graf = new RandomAccessFile(gACL, "r");
                    FileChannel rfc = graf.getChannel();
                    RandomAccessFile oraf = new RandomAccessFile(lACL, "rw");
                    FileChannel wfc = oraf.getChannel();
                    wfc.transferFrom(rfc, 0, graf.length());
                    rfc.close();
                    wfc.close();
                    graf.close();
                    oraf.close();
    
                    graf = new RandomAccessFile(gPW, "r");
                    rfc = graf.getChannel();
                    oraf = new RandomAccessFile(lPW, "rw");
                    wfc = oraf.getChannel();
                    wfc.transferFrom(rfc, 0, graf.length());
                    rfc.close();
                    wfc.close();
                    graf.close();
                    oraf.close();
        
                    Object[] oargs = { pwfilename, aclfilename,
                            Globals.JMQ_ETC_HOME };
                    logger.logToAll(Logger.INFO, rb.I_ACL_PW_FILED_COPIED, oargs);
    
                } catch (IOException ex) {
                    logger.log(Logger.ERROR, "Failed to copy files from the "
                        + Globals.JMQ_ETC_HOME + " directory:", ex);
                    throw ex;
                }
            } else {
                try {
                    // create default
                    FileOutputStream os = new FileOutputStream(lACL);
                    os.write(DEFAULT_ACL_CONTENT.getBytes());
                    os.close();
    
                    os = new FileOutputStream(lPW);
                    os.write(DEFAULT_PW_CONTENT.getBytes());
                    os.close();
                } catch (IOException ex) {
                    logger.log(Logger.ERROR, "Failed to create default files",
                        ex);
                    throw ex;
                }
            }
        }
    }


    int start(boolean inProcess, Properties params, BrokerEventListener bel, boolean initOnly) throws OutOfMemoryError,
			IllegalStateException, IllegalArgumentException {
		setBrokerEventListener(bel);
		int startCode = _start(inProcess, params, initOnly);
		if (bkrEvtListener != null) {
			BrokerEvent event = new BrokerEvent(this, BrokerEvent.Type.READY, "Broker has been started"); // L10N-XXX
			bkrEvtListener.brokerEvent(event);
		}
		return startCode;
	}   

    int _start(boolean inProcess, Properties propsFromCommandLine,
         boolean initOnly)
            throws OutOfMemoryError, IllegalStateException, IllegalArgumentException
    {
      try {
    	  

        setIsInProcess(inProcess);

        // initialize the Global properties if any arguments are passed in
        
        // read properties (including passwords) from standard input (used when the broker is managed by JMSRA)  
        Properties propsFromStdin = null;
        if (propsFromCommandLine != null &&
            propsFromCommandLine.containsKey(Globals.IMQ + ".readstdin.enabled")){
        	propsFromStdin = readPropertiesFromStandardInput();
        }
        
		// Combine any properties specified using command-line parameters with any properties read from standard input
		// The properties loaded from standard input have precedence and so are loaded second
		Properties combinedProps = new Properties();
        if (propsFromCommandLine != null) {
        	combinedProps.putAll(propsFromCommandLine);
      	}
        if (propsFromStdin != null){
        	combinedProps.putAll(propsFromStdin);
        }
        
        Globals.init(combinedProps, clearProps, saveProps);

        if (!removeInstance) {
            BrokerStateHandler.shuttingDown = false;
            BrokerStateHandler.shutdownStarted = false;
        }

        haltLogString = Globals.getBrokerResources().getKString(BrokerResources.W_HALT_BROKER);

        logger = Globals.getLogger();

        String configdir = Globals.getInstanceDir();
        File f = new File(configdir);

        // check permissions on the instance directories
        // (fix for bug 4694589)
        //
        // NOTE: we are unable to log if the directory permissions are
        // incorrect (we cant open the log file)
        //
        if (!f.exists()) { // check parent directory
            while (!f.exists()) { // loop up looking for the parent
                f = f.getParentFile();
                if (f == null) {
                    // now where else to go
                    break;
                }
                if (!f.exists()) {
                    continue;
                }
                if (!f.canWrite() || !f.canRead()) {
            if (!silent) {
		       printErr(rb.getString(rb.E_CAN_NOT_WRITE,
                f, Globals.getConfigName()));
            }
                 return (BrokerExitCode.NO_PERMISSION_ON_INSTANCE);
                }
            }
        } else if (!f.canWrite() || !f.canRead()) {
            if (!silent) {
                System.err.println(rb.getString(rb.E_CAN_NOT_WRITE,
                    f, Globals.getConfigName()));
            }
            return (BrokerExitCode.NO_PERMISSION_ON_INSTANCE);
        }

        // DON'T PUT ANY THING THAT HAS THE SIDE EFFECT
        // OF CREATING AN INSTANCE BEFORE THIS CHECK
        if (removeInstance) {
            removeInstance();
        }

        // if a password file is specified, read it
        try {
            parsePassfile();
        } catch (FileNotFoundException ex) {
            logger.log(Logger.FORCE,
                  rb.E_OPTION_VALID_ERROR, ex);
            return (1);
        }
        // Initialize any possible debug settings
        try {
            com.sun.messaging.jmq.util.Debug.setDebug(Globals.getConfig(),
                              Globals.IMQ + ".debug.");
        } catch (Exception e) {
            logger.log(Logger.WARNING, rb.W_BAD_DEBUG_CLASS, e);
        }

        // Initialize any diag settings
        try {
            com.sun.messaging.jmq.util.DiagManager.registerClasses(
                Globals.getConfig(), Globals.IMQ + ".diag.");
        } catch (Exception e) {
            logger.log(Logger.WARNING, rb.W_BAD_DIAG_CLASS, e);
        }

        BrokerConfig conf = Globals.getConfig();
        try {
            checkBrokerConfig(conf);
        } catch (Exception e) {
            logger.logToAll(logger.ERROR, e.getMessage());
            return 1;
        }
        if (!Globals.isJMSRAManagedSpecified() && Globals.isJMSRAManagedBroker()) {
            logger.log(Logger.ERROR, Globals.getBrokerResources().getKString(
                       BrokerResources.E_START_JMSRA_MANAGED_BROKER_NONMANAGED));
            return (1);
        }
        if (Globals.isJMSRAManagedBroker()) {
            try { 
            conf.updateBooleanProperty(Globals.JMSRA_MANAGED_PROPERTY, true, true);
            } catch (Exception e) {
            logger.logStack(Logger.ERROR, Globals.getBrokerResources().getKString(
                            BrokerResources.E_SET_BROKER_CONFIG_PROPERTY, 
                            Globals.JMSRA_MANAGED_PROPERTY+"=true", e.getMessage()), e);
            return (1);
            }
        }


        // get license before we log anything (logging will trigger
        // the default license to be loaded when the DestinationLogHandler
        // publish to a destination
        BrokerException licenseException = null;
        try {
            license = Globals.getCurrentLicense(licenseToUse);
        } catch (BrokerException ex) {
            // save it and deal with it after we print the banner out
            licenseException = ex;
        }

         String banner =  version.getBanner(false, Version.MINI_COPYRIGHT) +
                  rb.getString(rb.I_JAVA_VERSION) +
                  System.getProperty("java.version") + " " +
                      System.getProperty("java.vendor") + " " +
                      System.getProperty("java.home");

        logger.logToAll(Logger.INFO, rb.NL + banner);

        // Check to see if we have a version mismatch
        if (!version.isProductValid()) {
            // not valid - display an error
            //
            logger.log(Logger.ERROR, BrokerResources.E_INVALID_PRODUCT_VERSION);
            return (1);
        }


        try {
            initializePasswdFile();
        } catch (IOException ex) {
            return (1);
        }

        if (initOnly) {
            logger.log(Logger.INFO, BrokerResources.I_INIT_DONE);
            return 0;
        }

        try {
            AccessController.setSecurityManagerIfneed(); 
        } catch (Exception e) {
            logger.logStack(Logger.ERROR, e.getMessage(), e);
            return (1);
        }
        if (!MQAuthenticator.authenticateCMDUserIfset()) {
            logger.log(Logger.INFO, BrokerResources.I_SHUTDOWN_BROKER);
            return (1);
        }

        // For printing out VM heap info
        DiagManager.register(new VMDiagnostics());

        // check the license here after banner is printed
        if (licenseException != null) {
            logger.log(Logger.ERROR, licenseException.toString(), licenseException);
            return (1);
        } else if (license.getExpirationDate() != null) {
            Date date = license.getExpirationDate();
            DateFormat fmt = DateFormat.getDateInstance(DateFormat.MEDIUM);

            // print out message for using trial license
	    println(rb.getString(rb.M_TRIAL_LICENSE_MESSAGE,
                    fmt.format(date)));
        }

        if (Version.compareVersions(
                System.getProperty("java.specification.version"),
                MIN_JAVA_VERSION, true) < 0) {
            logger.logToAll(Logger.ERROR, rb.E_BAD_JAVA_VERSION,
                System.getProperty("java.specification.version"),
                MIN_JAVA_VERSION);
            return (1);
        }

        String hostname = conf.getProperty(Globals.IMQ + ".hostname");

        // Save value of imq.hostname. This may be null which is OK
        Globals.setHostname(hostname);

        /*
         * In a variety of places we need to know the name of the host
         * the broker is running on, and its IP address. Typically you
         * get this by calling getLocalHost(). But if the broker is running
         * on a multihomed machine, you may want to control which interface
         * (and IP address) the broker uses. Therefore we support the
         * imq.hostname property to let the user configure this. 
         */
        if (hostname == null || hostname.equals(Globals.HOSTNAME_ALL)) {
            // No hostname specified. Get local host
            try {
                InetAddress ia = InetAddress.getLocalHost();
                Globals.setBrokerInetAddress(ia);
            } catch (UnknownHostException e) {
                logger.log(Logger.ERROR, rb.E_NO_LOCALHOST, e);
                logger.log(Logger.INFO,  rb.M_BROKER_EXITING);
            return (1);
            }
        } else {
            // Hostname was specified. Use it. We may be on a multihomed
            // machine. Look up the address now so we have its IP
            try {
                InetAddress ia = InetAddress.getByName(hostname);
                Globals.setBrokerInetAddress(ia);
            } catch (UnknownHostException e) {
                logger.log(Logger.ERROR,
                        rb.getString(rb.E_BAD_HOSTNAME_PROP,
                        hostname, Globals.IMQ + ".hostname"),
                        e);
                logger.log(Logger.INFO,rb.M_BROKER_EXITING);
            return (1);
            }
        }

	/*
	 * Do the same thing for JMX hostname. On a multihome system,
	 * we may want to designate different IP addresses for the broker
	 * and for JMX traffic. A new property imq.jmx.hostname is
	 * created for this. By default, it will be set to whatever
	 * value imq.hostname is set to.
	 */
        String jmxHostname = conf.getProperty(Globals.IMQ + ".jmx.hostname");

        // Save value of imq.jmx.hostname. This may be null which is OK
        Globals.setJMXHostname(jmxHostname);

	/*
	 * Only check for the case where the JMX hostname is specified. If it
	 * is not, it will default to whatever value is configured for
	 * the broker hostname (imq.hostname).
	 */
        if (jmxHostname != null && !jmxHostname.equals(Globals.HOSTNAME_ALL)) {
            // JMX hostname was specified. Use it. We may be on a multihomed
            // machine. Look up the address now so we have its IP
            try {
                InetAddress ia = InetAddress.getByName(jmxHostname);
                Globals.setJMXInetAddress(ia);
            } catch (UnknownHostException e) {
                logger.log(Logger.ERROR,
                        rb.getString(rb.E_BAD_HOSTNAME_PROP,
                        hostname, Globals.IMQ + ".jmx.hostname"),
                        e);
                logger.log(Logger.INFO,rb.M_BROKER_EXITING);
            return (1);
            }
        }

        try {
            logger.logToAll(Logger.INFO, "   IMQ_HOME=" +
               new File(Globals.JMQ_HOME).getCanonicalPath());
            logger.logToAll(Logger.INFO, "IMQ_VARHOME=" +
               new File(Globals.JMQ_VAR_HOME).getCanonicalPath());
        } catch (IOException e) {
        }

        logger.logToAll(Logger.INFO,
            System.getProperty("os.name") + " " +
            System.getProperty("os.version") + " " +
            System.getProperty("os.arch") + " " +
            Globals.getBrokerHostName() + " " +
            "(" + Runtime.getRuntime().availableProcessors() + " cpu) " +
            System.getProperty("user.name"));

        try {
            // Log ulimit -n values
            Rlimit.Limits limits = Rlimit.get(Rlimit.RLIMIT_NOFILE);
            logger.logToAll(Logger.INFO, rb.getString(rb.I_NOFILES),
                ((limits.current == Rlimit.RLIM_INFINITY) ? "unlimited" :
                                            String.valueOf(limits.current)),
                ((limits.maximum == Rlimit.RLIM_INFINITY) ? "unlimited" :
                                            String.valueOf(limits.maximum)));
        } catch (Exception e) {
            // This is OK. It just means we can't log ulimit values on
            // this platform.
        }

        // Log JVM heap size information
        logger.logToAll(Logger.INFO, rb.getString(rb.I_JAVA_HEAP),
            Long.toString(Runtime.getRuntime().maxMemory() / 1024),
            Long.toString(Runtime.getRuntime().totalMemory() / 1024));

        
        // Start of logging of the various sets of properties that have been supplied in various ways
        
        // log the actual broker command-line arguments 
        logger.logToAll(Logger.INFO, rb.getString(rb.I_BROKER_ARGS),
            (propsFromCommandLine == null ? "":propsFromCommandLine.get("BrokerArgs")));
        
        // if the broker is non-embedded and started by JMSRA
        // log any properties read from standard input 
		logProperties("JMSRA BrokerProps: ", propsFromStdin);
						
		// if the broker is embedded and started by JMSRA
		// log any properties supplied programmatically
		if (embeddedBrokerStartupMessages!=null){
			for (String thisMessage : embeddedBrokerStartupMessages) {
				// Log the embeddedBrokerStartupMessages here  
				// These are typically used to log broker properties configured on an embedded broker
				// which is why we perform this logging at this point.
				// However they can also be used to log other information passed by the code that started the embedded broker
	            logger.logToAll(Logger.INFO, thisMessage);
			}
		}
        
		// log all properties specified on the command line either explicity or via command-line arguments
		logProperties(rb.getString(rb.I_BROKER_PROPERTIES), propsFromCommandLine);
		
        // End of logging of the various sets of properties that have been supplied in various ways
		
        if (inProcess) {
            logger.logToAll(Logger.INFO, rb.getString(rb.I_INPROCESS_BROKER));
        }

        // set up out of memory handler
        Globals.setGlobalErrorHandler(this);      

        // Get admin key from the key file if any. This is only used by
        // the nt service to handle shutdown.
        String key = getAdminKey(adminKeyFile);
        String propname = Globals.IMQ + ".adminkey";
        if (key == null || key.length() == 0) { 
            // Make sure property is not set
            conf.remove(propname);
        } else {
            try {
                conf.updateProperty(propname, key);
            } catch (Exception e) {
            }
        }

        /*
         * Hawk HA : retrieve ha properties and brokerid
         */
        
        boolean isHA = Globals.getHAEnabled();

        NO_HA = !(allowHA && license.getBooleanProperty(
                 LicenseBase.PROP_ENABLE_HA, false));

        if (isHA && NO_HA) {
            logger.log(Logger.FORCE, 
                       Globals.getBrokerResources().getKString(
                       BrokerResources.I_FEATURE_UNAVAILABLE,
                       Globals.getBrokerResources().getString(
                       BrokerResources.M_HA_SERVICE_FEATURE)));
            System.exit(1);
        }
        String brokerid = Globals.getBrokerID();
        String clusterid = Globals.getClusterID();

        if (isHA) {
            if (brokerid == null) {
                logger.log(Logger.ERROR,
                     BrokerResources.E_BID_MUST_BE_SET);
                return (1);
            }
            logger.log(Logger.INFO,
                 BrokerResources.I_RUNNING_IN_HA,
                 brokerid, clusterid);
        } else if (brokerid != null) {
            logger.log(Logger.INFO,
                BrokerResources.I_STARTING_WITH_BID, brokerid);
        }

        PortMapper pm = Globals.getPortMapper();
        if (pm.getServerSocket() == null && pm.isDoBind()) {
            // PortMapper failed to bind to port. Port is probably already
            // in use. An error message has already been logged so just exit
            System.err.println(rb.getString(rb.M_BROKER_EXITING));
            return (1);
        }

        /*
         * Store MQAddress in Globals so it can be accessed when needed
         * One place this is used is the "brokerAddress" property of monitoring
         * messages.
         */
        MQAddress addr = null;
        try {
            addr = BrokerMQAddress.createAddress(
                       Globals.getBrokerHostName(), pm.getPort());
        } catch (Exception e)  {
            logger.logStack(Logger.INFO,
                BrokerResources.E_CANNOT_CREATE_MQADDRESS, 
                "["+Globals.getBrokerHostName()+"]:"+pm.getPort(), e);
        }

        try {
            Globals.initClusterManager(addr);
        } catch (Exception e) {
            logger.logStack(Logger.ERROR,
                BrokerResources.E_INITING_CLUSTER, e);
            return(1);
        }

        if (Globals.useMasterBroker() &&
            Globals.dynamicChangeMasterBrokerEnabled() &&
            !Globals.isJMSRAManagedBroker()) {
            if (Globals.isMasterBrokerSpecified()) {
                String emsg = Globals.getBrokerResources().getKString(
                    BrokerResources.X_CLUSTER_NO_CMDLINE_MASTERBROKER_WHEN_DYNAMIC,
                    ClusterManagerImpl.CONFIG_SERVER, 
                    Globals.DYNAMIC_CHANGE_MASTERBROKER_ENABLED_PROP+"=true");
               logger.log(Logger.ERROR, emsg);
               return (1);
            }
        }

        pm.updateProperties();

        if (pm.isDoBind()){
        	// start the PortMapper thread
        	Thread portMapperThread = new MQThread(pm, "JMQPortMapper");
        	portMapperThread.setDaemon(true);
        	portMapperThread.start();
        }

        // Try to acquire the lock file. This makes sure no other copy
        // of the broker is running in the same instance directory as
        // we are.
        LockFile lf = null;
        try {
            lf = LockFile.getLock(
                conf.getProperty(Globals.JMQ_VAR_HOME_PROPERTY),
                Globals.getConfigName(),
                (pm.getHostname() == null || pm.getHostname().equals("") ?
                 Globals.getMQAddress().getHostName() : 
                 pm.getMQAddress().getHostName()), 
                pm.getPort());
        } catch (Exception e) {
            Object[] msgargs = {
                LockFile.getLockFilePath(conf.getProperty(
                     Globals.JMQ_VAR_HOME_PROPERTY),
                     Globals.getConfigName()),
                e.toString(),
                Globals.getConfigName()};
            logger.logStack(Logger.ERROR, rb.E_LOCKFILE_EXCEPTION, msgargs, e);
            return (1);
        }

        // Make sure we got the lock
        if (!lf.isMyLock()) {
            Object[] msgargs = {
            lf.getFilePath(),
            lf.getHost() + ":" + lf.getPort(),
            Globals.getConfigName()};

            logger.log(Logger.ERROR, rb.E_LOCKFILE_INUSE, msgargs);
            return (1);
        }

    
        // audit logging for reset store 
        if (resetStore) {
            Globals.getAuditSession().storeOperation(null, null,MQAuditSession.RESET_STORE);
            if (isHA) {
                logger.log(Logger.WARNING, BrokerResources.W_HA_NO_RESET);
            }
        }

        // create the Interest manager, which handles maping
        // interests to destinations
        try {
            store = Globals.getStore();
        } catch (BrokerException ex) {
            logger.logStack(Logger.ERROR, BrokerResources.E_PERSISTENT_OPEN, ex);
            return (1);
        }

        if (Globals.useSharedConfigRecord()) {
            try {
                shareCCStore = Globals.getStore().getShareConfigChangeStore();
            } catch (BrokerException ex) {
                logger.logStack(Logger.ERROR, 
                BrokerResources.E_SHARECC_STORE_OPEN, ex);
                return (1);
            }
        }

        
        BridgeServiceManager bridgeManager = null;

        if (BridgeBaseContextAdapter.bridgeEnabled()) {
            logger.log(Logger.INFO, BrokerResources.I_INIT_BRIDGE_SERVICE_MANAGER);
            try {
                Class c = Class.forName(BridgeBaseContextAdapter.getManagerClass());
                bridgeManager = (BridgeServiceManager)c.newInstance();
                bridgeManager.init(new BridgeBaseContextAdapter(this, resetStore));
            } catch (Throwable t) {
                bridgeManager = null;
                logger.logStack(Logger.WARNING, Globals.getBrokerResources().getKString(
                       BrokerResources.W_INIT_BRIDGE_SERVICE_MANAGER_FAILED), t);
            }
        }

        HAMonitorService haMonitor = null;
        if (isHA) {
            logger.log(Logger.INFO,
               BrokerResources.I_STARTING_MONITOR);

            try {
                // OK, in HA if the configuration already exists in the
                // store, getMQAddress reflects the old state (this
                // is because we want to do some additional startup
                // checks)
                // pass the "requested" address into HAMonitorService
                // who will handle any updates
                haMonitor = new HAMonitorService(Globals.getClusterID(),
                                Globals.getMQAddress(), resetTakeoverThenExit);
                if (resetTakeoverThenExit) {
                    return (0);
                }
                Globals.setHAMonitorService(haMonitor);
            } catch (Exception ex) {
                logger.logStack(Logger.ERROR,
                    BrokerResources.E_ERROR_STARTING_MONITOR, ex);
                if (ex instanceof StoreBeingTakenOverException) {
                    return (BrokerStateHandler.getRestartCode());
                }
                return (1);
            }

            logger.log(Logger.INFO, 
               BrokerResources.I_STARTING_HEARTBEAT);
            try {
                Globals.registerHeartbeatService(new HeartbeatService());
            } catch (Exception e) {
                logger.log(Logger.ERROR,
                    BrokerResources.E_ERROR_STARTING_HB, e);
                return (1);
            }
        }


        // creates the ClusterBroadcast
        NO_CLUSTER = !license.getBooleanProperty(
                 LicenseBase.PROP_ENABLE_CLUSTER, false);


        int maxBrokers = license.getIntProperty(
                 LicenseBase.PROP_BROKER_CONNLIMIT, -1);      

        ClusterRouter cr = null;
        if (NO_CLUSTER) {
            mbus = new com.sun.messaging.jmq.jmsserver.core.NoCluster();
            logger.log(Logger.FORCE, 
                       Globals.getBrokerResources().getKString(
                       BrokerResources.I_FEATURE_UNAVAILABLE,
                       Globals.getBrokerResources().getString(
                       BrokerResources.M_CLUSTER_SERVICE_FEATURE)));
        } else {
            try {
                Class c = Class.forName("com.sun.messaging.jmq.jmsserver"
                      + ".core.cluster.ClusterBroadcaster");
                Class[] paramTypes = { Integer.class, Integer.class };
                Constructor cons = c.getConstructor(paramTypes);
                Object[] paramArgs = { new Integer(maxBrokers), 
                        new Integer(DEFAULT_CLUSTER_VERSION) };
                mbus = (ClusterBroadcast)cons.newInstance(paramArgs);
                cr = new com.sun.messaging.jmq.jmsserver.core.cluster.MultibrokerRouter(mbus);
            } catch (ClassNotFoundException cnfe) {
                logger.logStack(Logger.DEBUG,
                     BrokerResources.E_INTERNAL_BROKER_ERROR, 
                    "unable to use cluster broadcaster", cnfe);
                logger.log(Logger.WARNING,
                    BrokerResources.I_USING_NOCLUSTER);

                mbus = new com.sun.messaging.jmq.jmsserver.core.NoCluster();
                NO_CLUSTER = true;
            } catch (InvocationTargetException ite) {
                Throwable ex = ite.getCause();
                if (ex != null && ex instanceof InvocationTargetException) {
                    ex = ex.getCause();
                }
                if (!(ex instanceof LoopbackAddressException)) {
                    logger.logStack(Logger.INFO,
                    BrokerResources.X_INTERNAL_EXCEPTION, ex.getMessage(), ex);
                }
                logger.log(Logger.WARNING, BrokerResources.I_USING_NOCLUSTER);

                mbus = new com.sun.messaging.jmq.jmsserver.core.NoCluster();
                NO_CLUSTER = true;
            } catch (Exception ex) {
                logger.logStack(Logger.INFO,
                     BrokerResources.E_INTERNAL_BROKER_ERROR, 
                    "unable to use cluster broadcaster", ex);
                logger.log(Logger.WARNING, BrokerResources.I_USING_NOCLUSTER);

                mbus = new com.sun.messaging.jmq.jmsserver.core.NoCluster();
                NO_CLUSTER = true;
            }

        }


        Globals.setClusterBroadcast(mbus);
        Globals.setClusterRouter(cr);
        Globals.setMyAddress(mbus.getMyAddress());


        /*
           HANDLE LDAP PROPERTIES
           XXX - this is not the cleanest way to handle this
           technically it should be better integrated with the
           authentication interfaces ... but I'm close to code
           freeze
           XXX-REVISIT racer 4/10/02
        */
         String type = Globals.getConfig().getProperty(
                AccessController.PROP_AUTHENTICATION_TYPE);
         
         if (type != null) {
             String userrep = Globals.getConfig().getProperty(
                AccessController.PROP_AUTHENTICATION_PREFIX 
               + type +AccessController.PROP_USER_REPOSITORY_SUFFIX);
             if (userrep.equals("ldap")) {
                 String DN = Globals.getConfig().getProperty(
                   AccessController.PROP_USER_REPOSITORY_PREFIX 
                   + userrep + ".principal");
                 String pwd = Globals.getConfig().getProperty(
                   AccessController.PROP_USER_REPOSITORY_PREFIX 
                   + userrep + ".password");
                 if (DN != null && DN.trim().length() > 0) {
                     // we have a DN
                     if (pwd == null || pwd.trim().length() == 0) {
                         int retry = 0;
                         Password pw = null;
                         boolean setProp = pwd == null || pwd.equals("");
                         while (pwd == null || 
                                  pwd.trim().equals("") && retry < 5) {
                              pw = new Password();
                              if (pw.echoPassword()) {
                                  System.err.println(Globals.getBrokerResources().
                                      getString(BrokerResources.W_ECHO_PASSWORD));
                              }
                              System.err.print(
                                  Globals.getBrokerResources().
                                  getString(BrokerResources.M_ENTER_KEY_LDAP, 
                                  DN));
                              System.err.flush();

                              pwd = pw.getPassword();

                              // Limit the number of times we try 
                              // reading the passwd.
                              // If the VM is run in the background
                              // the readLine()
                              // will always return null and 
                              // we'd get stuck in the loop
                              retry++;
                         }
                         if (pwd == null || pwd.trim().equals("")) {
                              logger.log(Logger.WARNING, 
                                  BrokerResources.W_NO_LDAP_PASSWD, pwd);
                              Globals.getConfig().put(
                                 AccessController.PROP_USER_REPOSITORY_PREFIX 
                                 + userrep 
                                 + ".principal",
                                  "");
                         } else if (setProp) {
                              Globals.getConfig().put(
                                 AccessController.PROP_USER_REPOSITORY_PREFIX 
                                 + userrep 
                                 + ".password",
                               pwd);
                         } 
                     }
                 }
 
             }
         }


        ConnectionManager cmgr = new ConnectionManager(
        license.getIntProperty(LicenseBase.PROP_CLIENT_CONNLIMIT, -1));
        Globals.setConnectionManager(cmgr);


        tlist = new TransactionList(store);
        Globals.setTransactionList(tlist);

        // get the persisted data
        try {
            Destination.init();
            Subscription.initSubscriptions();
            BrokerMonitor.init();
        } catch (BrokerException ex) {
            logger.logStack(Logger.WARNING,
                            BrokerResources.E_UNABLE_TO_RETRIEVE_DATA, ex);
        }

	// Initialize the JMX Agent
	try  {
            Class c = Class.forName("javax.management.MBeanServer");
	    Agent agent = new Agent();
	    Globals.setAgent(agent);
	    agent.start();
        } catch (Exception e)  {
            logger.log(Logger.WARNING,
                "JMX classes not present - JMX Agent is not created.");
        }

        /*
         * Check if we should support old (pre 3.0.1SP2) selector
         * type conversions (which violated the JMS spec).
         */
        Selector.setConvertTypes(conf.getBooleanProperty(Globals.IMQ +
            ".selector.convertTypes", false));
        /*
         * By default the selector code short circuits boolean expression
         * evaluation. This is a back door to disable that in case there
         * is a flaw in the implementation.
         */
        Selector.setShortCircuit(conf.getBooleanProperty(Globals.IMQ +
            ".selector.shortCircuit", true));


        // create the handlers - these handle the message
        // processing
        pktrtr = new PacketRouter();

        Globals.setProtocol( new ProtocolImpl(pktrtr));
        HelloHandler hello = new HelloHandler(cmgr);
        GetLicenseHandler getLicense = new GetLicenseHandler();
        GoodbyeHandler goodbye = new GoodbyeHandler(cmgr);

        // XXX - REVISIT 2/25/00
        // we may want to load these from properties in the future
        //
        StartStopHandler startstop = new StartStopHandler();
        ConsumerHandler conhdlr = new ConsumerHandler();
        ProducerHandler prodhandler = new ProducerHandler();
        DestinationHandler desthandler = new DestinationHandler();
        QBrowseHandler qbrowserhdlr = new QBrowseHandler();
        AuthHandler authenticate = new AuthHandler(cmgr);
        SessionHandler sessionhdlr = new SessionHandler();
        PingHandler pinghandler = new PingHandler();

        DataHandler datahdrl = new DataHandler(tlist);

        AckHandler ackhandler = new AckHandler(tlist);
        RedeliverHandler redeliverhdlr =
                                new RedeliverHandler();

        DeliverHandler deliverhdlr = new DeliverHandler();
        TransactionHandler thandler =
                                new TransactionHandler(tlist);
        VerifyDestinationHandler vdhandler = new VerifyDestinationHandler();
        ClientIDHandler clienthandler = new ClientIDHandler();

        FlowHandler flowhdlr = new FlowHandler();
        FlowPausedHandler fphandler = new FlowPausedHandler();

        GenerateUIDHandler genUIDhandler = new GenerateUIDHandler();

        InfoRequestHandler infohandler = new InfoRequestHandler();
        VerifyTransactionHandler vthandler = new VerifyTransactionHandler(tlist);


        // Map message handles -> messages
        try {

            pktrtr.addHandler(PacketType.HELLO, hello);
            pktrtr.addHandler(PacketType.AUTHENTICATE, authenticate);
            pktrtr.addHandler(PacketType.GET_LICENSE, getLicense);
            pktrtr.addHandler(PacketType.ADD_CONSUMER, conhdlr);
            pktrtr.addHandler(PacketType.DELETE_CONSUMER, conhdlr);
            pktrtr.addHandler(PacketType.ADD_PRODUCER, prodhandler);
            pktrtr.addHandler(PacketType.START, startstop);
            pktrtr.addHandler(PacketType.STOP, startstop);
            pktrtr.addHandler(PacketType.ACKNOWLEDGE, ackhandler);
            pktrtr.addHandler(PacketType.BROWSE, qbrowserhdlr);
            pktrtr.addHandler(PacketType.GOODBYE, goodbye);
            pktrtr.addHandler(PacketType.REDELIVER, redeliverhdlr);
            pktrtr.addHandler(PacketType.CREATE_DESTINATION, desthandler);
            pktrtr.addHandler(PacketType.DESTROY_DESTINATION, desthandler);
            pktrtr.addHandler(PacketType.VERIFY_DESTINATION, vdhandler);
            pktrtr.addHandler(PacketType.DELIVER, deliverhdlr);
            pktrtr.addHandler(PacketType.START_TRANSACTION, thandler);
            pktrtr.addHandler(PacketType.COMMIT_TRANSACTION, thandler);
            pktrtr.addHandler(PacketType.ROLLBACK_TRANSACTION, thandler);
            pktrtr.addHandler(PacketType.PREPARE_TRANSACTION, thandler);
            pktrtr.addHandler(PacketType.END_TRANSACTION, thandler);
            pktrtr.addHandler(PacketType.RECOVER_TRANSACTION, thandler);
            pktrtr.addHandler(PacketType.SET_CLIENTID, clienthandler);
            pktrtr.addHandler(PacketType.GENERATE_UID, genUIDhandler);
            pktrtr.addHandler(PacketType.MAP_MESSAGE, datahdrl);
            pktrtr.addHandler(PacketType.BYTES_MESSAGE, datahdrl);
            pktrtr.addHandler(PacketType.MESSAGE, datahdrl);
            pktrtr.addHandler(PacketType.MESSAGE_SET, datahdrl);
            pktrtr.addHandler(PacketType.OBJECT_MESSAGE, datahdrl);
            pktrtr.addHandler(PacketType.STREAM_MESSAGE, datahdrl);
            pktrtr.addHandler(PacketType.TEXT_MESSAGE, datahdrl);
            pktrtr.addHandler(PacketType.RESUME_FLOW, flowhdlr);
            pktrtr.addHandler(PacketType.FLOW_PAUSED, fphandler);

            pktrtr.addHandler(PacketType.CREATE_SESSION,sessionhdlr);
            pktrtr.addHandler(PacketType.DELETE_PRODUCER,prodhandler);
            pktrtr.addHandler(PacketType.DESTROY_SESSION,sessionhdlr);
            pktrtr.addHandler(PacketType.PING,pinghandler);

            pktrtr.addHandler(PacketType.INFO_REQUEST,infohandler);
            pktrtr.addHandler(PacketType.VERIFY_TRANSACTION,vthandler);

        } catch (Exception ex) {
            logger.logStack(Logger.WARNING,
                            BrokerResources.E_INTERNAL_BROKER_ERROR,
                            "adding packet handlers", ex);
        }


        // set up the admin packet router
        admin_pktrtr = new PacketRouter();
        AdminDataHandler admin_datahdrl = new
            AdminDataHandler(tlist);
        // Map message handles -> messages. For the admin service this
        // is just like the regular JMS service except we have a specialized
        // data handler
        try {
            admin_pktrtr.addHandler(PacketType.HELLO, hello);
            admin_pktrtr.addHandler(PacketType.AUTHENTICATE, authenticate);
            admin_pktrtr.addHandler(PacketType.GET_LICENSE, getLicense);
            admin_pktrtr.addHandler(PacketType.ADD_CONSUMER, conhdlr);
            admin_pktrtr.addHandler(PacketType.DELETE_CONSUMER, conhdlr);
            admin_pktrtr.addHandler(PacketType.ADD_PRODUCER, prodhandler);
            admin_pktrtr.addHandler(PacketType.START, startstop);
            admin_pktrtr.addHandler(PacketType.STOP, startstop);
            admin_pktrtr.addHandler(PacketType.ACKNOWLEDGE, ackhandler);
            admin_pktrtr.addHandler(PacketType.BROWSE, qbrowserhdlr);
            admin_pktrtr.addHandler(PacketType.GOODBYE, goodbye);
            admin_pktrtr.addHandler(PacketType.REDELIVER, redeliverhdlr);
            admin_pktrtr.addHandler(PacketType.CREATE_DESTINATION, desthandler);
            admin_pktrtr.addHandler(PacketType.DESTROY_DESTINATION,
                                                                desthandler);
            admin_pktrtr.addHandler(PacketType.VERIFY_DESTINATION, vdhandler);
            admin_pktrtr.addHandler(PacketType.DELIVER, deliverhdlr);
            admin_pktrtr.addHandler(PacketType.START_TRANSACTION, thandler);
            admin_pktrtr.addHandler(PacketType.COMMIT_TRANSACTION, thandler);
            admin_pktrtr.addHandler(PacketType.ROLLBACK_TRANSACTION, thandler);
            admin_pktrtr.addHandler(PacketType.PREPARE_TRANSACTION, thandler);
            admin_pktrtr.addHandler(PacketType.END_TRANSACTION, thandler);
            admin_pktrtr.addHandler(PacketType.RECOVER_TRANSACTION, thandler);
            admin_pktrtr.addHandler(PacketType.SET_CLIENTID, clienthandler);
            admin_pktrtr.addHandler(PacketType.GENERATE_UID, genUIDhandler);

            admin_pktrtr.addHandler(PacketType.MAP_MESSAGE, admin_datahdrl);
            admin_pktrtr.addHandler(PacketType.BYTES_MESSAGE, admin_datahdrl);
            admin_pktrtr.addHandler(PacketType.MESSAGE, admin_datahdrl);
            admin_pktrtr.addHandler(PacketType.MESSAGE_SET, admin_datahdrl);
            admin_pktrtr.addHandler(PacketType.OBJECT_MESSAGE, admin_datahdrl);
            admin_pktrtr.addHandler(PacketType.STREAM_MESSAGE, admin_datahdrl);
            admin_pktrtr.addHandler(PacketType.TEXT_MESSAGE, admin_datahdrl);
            admin_pktrtr.addHandler(PacketType.RESUME_FLOW, flowhdlr);
            admin_pktrtr.addHandler(PacketType.FLOW_PAUSED, fphandler);

            admin_pktrtr.addHandler(PacketType.CREATE_SESSION,sessionhdlr);
            admin_pktrtr.addHandler(PacketType.DELETE_PRODUCER,prodhandler);
            admin_pktrtr.addHandler(PacketType.DESTROY_SESSION,sessionhdlr);
        } catch (Exception ex) {
            logger.logStack(Logger.WARNING,
                            BrokerResources.E_INTERNAL_BROKER_ERROR,
                            "adding packet handlers to admin packet router",
                            ex);
        }

        // The admin message handlers may need to locate standard packet
        // handlers, so we give it a reference to the PacketRouter.
        admin_datahdrl.setPacketRouter(admin_pktrtr);


        PacketRouter routers[] = {pktrtr, admin_pktrtr};
        Globals.setPacketRouters(routers);

        if (mbus instanceof com.sun.messaging.jmq.jmsserver.core.cluster.ClusterBroadcaster) {
            if (Globals.useSharedConfigRecord()) {
                try {
                ((com.sun.messaging.jmq.jmsserver.core.cluster.ClusterBroadcaster)mbus).
                    getRealClusterBroadcaster().syncChangeRecordOnStartup();
                } catch (Exception e) {
                    logger.logStack(Logger.ERROR, rb.getKString(
                    rb.E_SHARCC_SYNC_ON_STARTUP_FAILED, Globals.getClusterID(),
                    e.getMessage()), e);
                    return (1);
                }
            }
        }

        TLSProtocol.init();
        ServiceManager sm = new ServiceManager(cmgr);
        Globals.setServiceManager(sm);

        sm.updateServiceList(sm.getAllActiveServiceNames(),
            ServiceType.ADMIN, false);

        /*
         * Check if we need to pause the normal services until
         * MessageBus syncs with the config server. The services
         * will be resumed by the MessageManager when it gets
         * a notification from the MessageBus
         */
        if (mbus.waitForConfigSync()) {
            sm.updateServiceList(sm.getAllActiveServiceNames(),
                                 ServiceType.NORMAL, true /* pause */);
            if (Globals.nowaitForMasterBroker()) { 
                sm.addServiceRestriction(ServiceType.NORMAL,
                   ServiceRestriction.NO_SYNC_WITH_MASTERBROKER);
                logger.log(Logger.WARNING, rb.I_MBUS_LIMITEDJMS);
                try {
                sm.resumeAllActiveServices(ServiceType.NORMAL, true);
                } catch (BrokerException e) {
                logger.logStack(Logger.ERROR,  e.getMessage(), e);
                }
            } else {
                logger.log(Logger.ERROR, rb.I_MBUS_PAUSING);
            }
        } else {
            sm.updateServiceList(sm.getAllActiveServiceNames(),
                ServiceType.NORMAL, false /* dont pause */, true);
        }

        // OK, create the BrokerStateHandler
        Globals.setBrokerStateHandler(new BrokerStateHandler());

        
        // provide an option not to add shutdown hook.
        // This makes it easier to test restarts after ungraceful exits
        boolean noShutdownHook = Boolean.getBoolean (Globals.IMQ+".noShutdownHook");
        
        
        // Add the shutdownHook. The hook gets called when the VM exits
        // and gives us a chance to cleanup. This is new in JDK1.3.
        
                       
        if (inProcess || noShutdownHook || (shutdownHook = addShutdownHook()) == null) {
            // Couldn't add shutdown hook. Probably because running against 1.2
            logger.log(Logger.DEBUG, rb.I_NO_SHUTDOWN_HOOK);
        } else {
            logger.log(Logger.DEBUG, rb.I_SHUTDOWN_HOOK);
        }

        // start the memory manager
        if (!inProcess) {
            Globals.getMemManager().startManagement();
        } else {
            Globals.setMemMgrOn(false);
        }

        // Initialize the metric manager. This is the module that
        // generates performance data reports
        MetricManager mm = new MetricManager();
        Globals.setMetricManager(mm);
        mm.setParameters(Globals.getConfig());

        /*
         * Set the list of properties that must be matched before
         * accepting connections from other brokers.
         */
        Properties matchProps = new Properties();

        matchProps.setProperty(Globals.IMQ + ".autocreate.queue",
            Globals.getConfig().getProperty(Globals.IMQ +
                                            ".autocreate.queue", "false"));

        matchProps.setProperty(Globals.IMQ + ".autocreate.topic",
            Globals.getConfig().getProperty(Globals.IMQ +
                                            ".autocreate.topic", "false"));

        //
        // "imq.queue.deliverypolicy" was used as one of the
        // "matchProps" in the 3.0.1 clusters. So even if this
        // property is now obsolete we still need to pretend that it
        // exists for cluster protocol compatibility..
        //
        int active =
            Queue.getDefaultMaxActiveConsumers();

        int failover =
            Queue.getDefaultMaxFailoverConsumers();

        if (active == 1 && failover == 0) {
            matchProps.setProperty(Globals.IMQ + ".queue.deliverypolicy",
                "single");
        }
        if (active == 1 && failover != 0) {
            matchProps.setProperty(Globals.IMQ + ".queue.deliverypolicy",
                "failover");
        }
        if ((active == Queue.UNLIMITED || active > 1) && failover == 0) {
            matchProps.setProperty(Globals.IMQ + ".queue.deliverypolicy",
                "round-robin");
        }

        if (Globals.getClusterID() != null) {
            matchProps.setProperty(Globals.IMQ + ".cluster.clusterid", 
                                   Globals.getClusterID());
        }
        if (isHA) {
            matchProps.setProperty(Globals.IMQ + ".cluster.ha",
              Globals.getConfig().getProperty(Globals.IMQ +".cluster.ha")); //must true
            matchProps.setProperty(Globals.IMQ + ".cluster.monitor.interval",
                                   String.valueOf(haMonitor.getMonitorInterval()));
            matchProps.setProperty(Globals.IMQ + ".cluster.heartbeat.class",
              Globals.getConfig().getProperty(Globals.IMQ +".cluster.heartbeat.class"));
            matchProps.setProperty(Globals.IMQ + ".service.activelist",
              Globals.getConfig().getProperty(Globals.IMQ +".service.activelist"));
            matchProps.setProperty(Globals.IMQ + ".bridge.enabled",
              Globals.getConfig().getProperty(Globals.IMQ +".bridge.enabled", "false"));

        } else if (Globals.isNewTxnLogEnabled()) {
            matchProps.setProperty(StoreManager.NEW_TXNLOG_ENABLED_PROP, "true");
        }

        if (Globals.getClusterManager().getMasterBroker() != null && Globals.nowaitForMasterBroker()) {
            matchProps.setProperty(Globals.NOWAIT_MASTERBROKER_PROP, "true");
        }
        if (Globals.useMasterBroker() && Globals.dynamicChangeMasterBrokerEnabled()) {
            matchProps.setProperty(Globals.DYNAMIC_CHANGE_MASTERBROKER_ENABLED_PROP, "true");
        }
        if (Globals.useSharedConfigRecord()) {
            matchProps.setProperty(Globals.NO_MASTERBROKER_PROP, "true");
        }
        mbus.setMatchProps(matchProps);

        /*
         * Start talking to other brokers now that all the handlers are
         * initialized and ready to process callbacks from MessageBus
         */
        mbus.startClusterIO();

        /**
         * services are up and running (although we may be paused)
         */
        startupComplete = true;

        // audit logging of broker startup
        Globals.getAuditSession().brokerOperation(null, null, MQAuditSession.BROKER_STARTUP);

        Object[] sargs = { Globals.getConfigName() + "@" +
                (pm.getHostname() == null || pm.getHostname().equals("") ?
                Globals.getMQAddress().getHostName() : pm.getMQAddress().getHostName()) + ":" +
        String.valueOf(pm.getPort())};
        logger.logToAll(Logger.INFO, rb.I_BROKER_READY, sargs);



	// Load MQ Mbeans in JMX agent
	Agent agent = Globals.getAgent();
	if (agent != null)  {
	    agent.loadMBeans();
	}

    if (BridgeBaseContextAdapter.bridgeEnabled() && bridgeManager != null) {
        try {
             logger.log(Logger.INFO, 
                    Globals.getBrokerResources().I_START_BRIDGE_SERVICE_MANAGER);

             bridgeManager.start();
             Globals.setBridgeServiceManager(bridgeManager);

             logger.log(Logger.INFO, 
                    Globals.getBrokerResources().I_STARTED_BRIDGE_SERVICE_MANAGER);
        } catch (Throwable t) {
             logger.logStack(Logger.WARNING,
                    Globals.getBrokerResources().W_START_BRIDGE_SERVICE_MANAGER_FAILED, t);
        }
    }
 
    } catch (OutOfMemoryError err) {
          Globals.handleGlobalError(err,
                                    rb.getKString(rb.M_LOW_MEMORY_STARTUP));
          return (1);
      }

      if (diagInterval > 0) {
          MQTimer timer = Globals.getTimer();
          int _interval = diagInterval * 1000;
          timer.schedule(new BrokerDiagTask(), _interval, _interval);
      } else if (diagInterval == 0) {
          logger.log(Logger.INFO, DiagManager.allToString());
      }

      return 0; //started OK

    }

    private void checkBrokerConfig(BrokerConfig bcf) throws BrokerException {
        int val = bcf.getIntProperty(Queue.MAX_ACTIVE_CNT,
                                     Queue.DEFAULT_MAX_ACTIVE_CONSUMERS); 
        if (val == 0 || val < -1) {
            String emsg = Globals.getBrokerResources().
                getKString(BrokerResources.X_INVALID_MAX_CONSUMER_COUNT,
                Queue.MAX_ACTIVE_CNT, String.valueOf(val));
            throw new BrokerException(emsg);
        }
        val = bcf.getIntProperty(Destination.AUTO_MAX_NUM_PRODUCERS,
                                 Destination.DEFAULT_MAX_PRODUCERS); 
        if (val == 0 || val < -1) {
            String emsg = Globals.getBrokerResources().
                getKString(BrokerResources.X_INVALID_MAX_PRODUCER_COUNT,
                Destination.AUTO_MAX_NUM_PRODUCERS, String.valueOf(val));
            throw new BrokerException(emsg);
        }
    }

    /**
     * Returns the command line arguments formatted into a string
     * that is suitable for displaying
     */
    private String argsToString(String args[]) {

    StringBuffer sb = new StringBuffer();
    for (int n = 0; n < args.length; n++) {
        sb.append(args[n]);
        sb.append(" ");
    }

    return sb.toString();
    }

    // Property names for 3 passwords that may be specified in command line
    private String dbPWProp = Globals.IMQ + ".persist.jdbc.password";
    private String ldapPWProp = Globals.IMQ + ".user_repository.ldap.password";
    private String keystorePWProp = Globals.IMQ + ".keystore.password";
    private String bridgeManagerPWProp = BridgeBaseContextAdapter.PROP_ADMIN_PASSWORD;
    private boolean dbPWOverride = false;
    private boolean ldapPWOverride = false;
    private boolean keystorePWOverride = false;
    private boolean bridgeManagerPWOverride = false;

    /**
     * Parse command line arguments into a Properties object
     * that can then be used during configuration initilization.
     * @throw IllegalArgumentException argument was invalid
     * @throw EmptyStackException -h was passed in (help)
     */
    private Properties parseArgs(String args[]) 
        throws IllegalArgumentException, EmptyStackException
    {

    String value;
        Properties props = new Properties();
        boolean logLevelSet = false;

    for (int n = 0; n < args.length; n ++) {
        if (args[n].equals("-loglevel")) {
        if (++n >= args.length) throw new IllegalArgumentException("missing log argument");
        try {
            // This just verifies syntax
            Logger.levelStrToInt(args[n]);
            // Set appropriate property
            props.put(Globals.IMQ + ".log.level", args[n]);
                    logLevelSet = true;
            } catch (IllegalArgumentException e) {
		   printErr(rb.getString(rb.M_BAD_LOGLEVEL));
            throw new IllegalArgumentException("Bad log level");
        }
        } else if (args[n].equals("-save")) {
            saveProps = true;

            // use shared (nio non-blocking) thread pool
        } else if (args[n].equals("-shared")) {
                props.put(Globals.IMQ + ".jms.threadpool_model", "shared");
        } else if (args[n].equals("-debug")) {
        if (++n >= args.length) throw new IllegalArgumentException("missing debug argument");
                if (!logLevelSet) {
            props.put(Globals.IMQ + ".log.level", "DEBUGHIGH");
                }
                if (!enableDebug(args[n], props)) 
                     throw new IllegalArgumentException("bad debug argument");
        } else if (args[n].equals("-dbuser")) {
        if (++n >= args.length) 
                     throw new IllegalArgumentException("missing dbuser argument");
        props.put(Globals.IMQ + ".persist.jdbc.user", args[n]);
        } else if (args[n].equals("-dbpassword")) {
            printPasswordError(args[n]);
            throw new IllegalArgumentException("argument unsupported");
        } else if (args[n].equals("-dbpwd")) {
        printPasswordWarning(args[n]);
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing dbpassword argument");
        props.put(dbPWProp, args[n]);
        dbPWOverride = true;
        } else if (args[n].equals("-diag")) {
        if (++n >= args.length)
               throw new IllegalArgumentException("missing diag interval");
                try {
                    diagInterval = Integer.parseInt(args[n]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("bad diag interval");
                }
        } else if (args[n].equals("-name")) {
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing broker instancename");
        props.put(Globals.IMQ + ".instancename", args[n]);
        } else if (args[n].equals("-port")) {
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing port");
        props.put(Globals.IMQ + ".portmapper.port", args[n]);
        } else if (args[n].equals("-nobind")) {
            props.put(Globals.IMQ + ".portmapper.bind", "false");        
        } else if (args[n].equals("-metrics")) {
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing metrics");
        props.put(Globals.IMQ + ".metrics.interval", args[n]);
        } else if (args[n].equals("-password")) {
            printPasswordError(args[n]);
            throw new IllegalArgumentException("argument unsupported");
        } else if (args[n].equals("-pwd")) {
        printPasswordWarning(args[n]);
        if (++n >= args.length)
               throw new IllegalArgumentException("missing password");
        props.put(keystorePWProp, args[n]);
        keystorePWOverride = true;
        } else if (args[n].equals("-ldappassword")) {
            printPasswordError(args[n]);
            throw new IllegalArgumentException("argument unsupported");
        } else if (args[n].equals("-ldappwd")) {
        printPasswordWarning(args[n]);
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing ldab password");
        props.put(ldapPWProp, args[n]);
        ldapPWOverride = true;
        
        } else if (args[n].equals("-read-stdin")){
        	props.put(Globals.READ_PROPERTIES_FROM_STDIN, "true");
        } else if (args[n].equals("-passfile")) {
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing passfile name");
        File passfile = null;
        try {
            passfile = (new File(args[n])).getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("unknown passfile: " + e);
        }
            props.put(Globals.KEYSTORE_USE_PASSFILE_PROP, "true");
        props.put(Globals.KEYSTORE_PASSDIR_PROP,
            passfile.getParent());
            props.put(Globals.KEYSTORE_PASSFILE_PROP,
            passfile.getName());
        } else if (args[n].equals("-backup")) {
        if (++n >= args.length)
               throw new IllegalArgumentException("missing backup argument");
        props.put(Globals.IMQ +
                        ".cluster.masterbroker.backup", args[n]);
        } else if (args[n].equals("-restore")) {
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing restore argument");
        props.put(Globals.IMQ +
                        ".cluster.masterbroker.restore", args[n]);
        } else if (args[n].equals("-cluster")) {
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing cluster list");
        props.put(Globals.MANUAL_AUTOCONNECT_CLUSTER_PROPERTY, args[n]);
        } else if (args[n].equals("-force")) {
                // Perform action without user confirmation
        force = true;
        
		} else if (args[n].equals("-silent") || args[n].equals("-s")) {
			// Send no messages to the StreamLogHandler (used for logging to the console)
			props.put(Globals.IMQ + ".log.console.output", "NONE");
			silent = true;
		} else if (args[n].equals("-ttyerrors") || args[n].equals("-te")) {
			// Send only error messages (and not "force" messages) 
			// to the StreamLogHandler (used for logging to the console)
			props.put(Globals.IMQ + ".log.console.output", "ERROR|NOFORCE");
		} else if (args[n].equals("-tty")) {
			// Send all messages to the StreamLogHandler (used for logging to the console)
			props.put(Globals.IMQ + ".log.console.output", "ALL");       	
        } else if (args[n].startsWith("-D")) {
        int value_index = 0;
        String prop_name = null, prop_value = "";

        value_index = args[n].indexOf('=');
        if (args[n].length() <= 2) {
            // -D
            continue;
        } else if (value_index < 0) {
            // -Dfoo
            prop_name = args[n].substring(2);
        } else if (value_index == args[n].length() - 1) {
            // -Dfoo=
            prop_name = args[n].substring(2, value_index);
                } else {
            // -Dfoo=bar
            prop_name = args[n].substring(2, value_index);
            prop_value = args[n].substring(value_index + 1);
                }

        props.put(prop_name, prop_value);
        } else if (args[n].equals("-varhome") ||
                       args[n].equals("-jmqvarhome")) {
        // Skip -varhome. It is there for the startup script
        ++n;
        } else if (args[n].equals("-imqhome")) {
        // Skip -imqhome. It is there for the BrokerProcess
        ++n;
        } else if (args[n].equals("-libhome")) {
        // Skip -libhome. It is there for the BrokerProcess
        ++n;
        } else if (args[n].equals("-javahome")) {
        // Skip -javahome. It is there for the startup script
        ++n;
        } else if (args[n].equals("-jrehome")) {
        // Skip -jrehome. It is there for the startup script
        ++n;
        } else if (args[n].equals("-bgnd")) {
        // Skip -bgnd. It is there for the startup script
                background = true;
        } else if (args[n].equals("-init")) {
                initOnly = true;
        } else if (args[n].equals("-version") || args[n].equals("-v")) {
                Globals.pathinit(System.getProperties());
	        println(version.getBanner(true));
	        println(rb.getString(rb.I_JAVA_VERSION) +
                System.getProperty("java.version") + " " +
                System.getProperty("java.vendor") + " " +
                System.getProperty("java.home"));
		println(rb.getString(rb.I_JAVA_CLASSPATH) +
            System.getProperty("java.class.path"));
            try {
	            println("   IMQ_HOME=" +
               new File(Globals.JMQ_HOME).getCanonicalPath());
	            println("IMQ_VARHOME=" +
               new File(Globals.JMQ_VAR_HOME).getCanonicalPath());
                } catch (IOException e) { }
                System.exit(0);
        } else if (args[n].equals("-ntservice")) {
        // We are running as an nt service. This affects the
        // way we exit
        isNTService = true;
        } else if (args[n].equals("-adminkeyfile")) {
        // Save location of admin key file
        ++n;
        adminKeyFile = args[n];
        } else if (args[n].equals("-help") || args[n].equals("-h")) {
            throw new EmptyStackException(); // LKS XXX
        } else if (args[n].equals("-license")) {
        if (++n >= args.length || args[n].startsWith("-")) {
            printLicenses();
        } else {
            licenseToUse = args[n];
        }
        } else if (args[n].equals("-remove")) {
        if (++n >= args.length) 
               throw new IllegalArgumentException("missing remove argument");

        if (args[n].equals("instance")) {
            removeInstance = true;

            // set the property so that store knows to delete the
            // data
            props.put(Store.REMOVE_STORE_PROP, "true");
        } else {
		   printErr(rb.getString(rb.M_BAD_REMOVE_ARG));
           throw new IllegalArgumentException("unknown remove argument");
        }
        } else if (args[n].equals("-reset")) {
        if (++n >= args.length) 
           throw new IllegalArgumentException("missing reset argument");
        if (args[n].equals("store")) {
            props.put(Store.RESET_STORE_PROP, "true");
            resetStore = true;
        } else if (args[n].equals("messages")) {
            props.put(Store.RESET_MESSAGE_PROP, "true");
        } else if (args[n].equals("durables")) {
            props.put(Store.RESET_INTEREST_PROP, "true");
        } else if (args[n].equals("props")) {
            clearProps = true;
        } else if (args[n].equals("takeover-then-exit")) {
            resetTakeoverThenExit = true;
        } else {
		   printErr(rb.getString(rb.M_BAD_RESET_TYPE));
                   throw new IllegalArgumentException("bad reset argument");
		}
	    } else if (args[n].equals("-upgrade-store-nobackup")) {
		props.put(Store.UPGRADE_NOBACKUP_PROP, "true");
	    } else if (args[n].equals("-useRmiRegistry")) {
		props.put(Globals.IMQ + ".jmx.rmiregistry.use", "true");
	    } else if (args[n].equals("-startRmiRegistry")) {
		props.put(Globals.IMQ + ".jmx.rmiregistry.start", "true");
	    } else if (args[n].equals("-rmiRegistryPort")) {
		if (++n >= args.length) 
                   throw new IllegalArgumentException("missing rmi port");
		props.put(Globals.IMQ + ".jmx.rmiregistry.port", args[n]);
	    } else if (args[n].equals("-activateServices")) {
		if (++n >= args.length) 
                   throw new IllegalArgumentException("missing service list");
		props.put(Globals.IMQ + ".service.activate", args[n]);
	    } else {
	       printErr(rb.getString(rb.E_INVALID_OPTION, args[n]));
               throw new IllegalArgumentException("unknown option " + args[n]);
        }
    }

        return props;

    }

    protected boolean isSilentMode() {
        return silent;
    }

    private void printPasswordWarning(String option)  {
        if (!silent) {
	   printErr(rb.getString(rb.W_PASSWD_OPTION_DEPRECATED, option));
	   printErr("");
        }

    }

    private void printPasswordError(String option)  {
        if (!silent) {
	   printErr(rb.getString(rb.E_PASSWD_OPTION_NOT_SUPPORTED, option));
	   printErr("");
        }
        Broker.getBroker().exit(1, 
              rb.getString(rb.E_PASSWD_OPTION_NOT_SUPPORTED),
              BrokerEvent.Type.FATAL_ERROR);
    }

    /**
     * Get the admin key from the admin key file and remove the file.
     * The admin key is only used when we are run as an NT service
     * since the service needs to run jmqcmd to shutdown the broker
     * and the key is a hook to allow jmqcmd to authenticate with 
     * the broker.
     */
    private String getAdminKey(String keyFile) {
    if (keyFile == null || keyFile.length() == 0) {
        return null;
    }

    byte[] buf = new byte[256];
    File f = new File(keyFile);
    FileInputStream fis = null;
    int nbytes = 0;
    try {
        fis = new FileInputStream(f);
        nbytes = fis.read(buf);
        fis.close();
    } catch (FileNotFoundException e) {
        logger.log(Logger.WARNING, BrokerResources.W_BAD_KEY_FILE,
        keyFile, e);
        return null;
    } catch (IOException e) {
        logger.log(Logger.WARNING, BrokerResources.W_BAD_KEY_FILE,
        keyFile, e);
        return null;
    }

    String key = null;

    try {
        key = new String(buf, 0, nbytes, "ASCII");
        } catch (UnsupportedEncodingException e) {
        // Should never happen
        logger.log(Logger.WARNING,
                       BrokerResources.E_INTERNAL_BROKER_ERROR,
               "Could not convert key to String using ASCII encoding ");
    }

    if (!f.delete()) {
            logger.log(Logger.WARNING, BrokerResources.W_BAD_KEY_FILE_DEL,
        keyFile);
    };

    return key;
    }

    private synchronized boolean enableDebug(String alias, Properties props) {

        String prefix = Globals.IMQ + ".debug.com.sun.messaging.jmq.";

        if (debugAliases == null) {
            debugAliases = new Hashtable();

            // The alias key is the parameter passed to -debug. The value
            // is one or more space seperated property names to set to "true".
            // Typically the property will be "imq.debug" followed by a
            // the class name to enable debugging on, but it can be any
            // boolean property. For example the packet debugging code has some
            // short-hand properties.

            debugAliases.put("admin", 
                prefix + "jmsserver.data.handlers.admin.AdminCmdHandler");

            debugAliases.put("jmx", Globals.IMQ + ".jmx.debug.all");
            debugAliases.put("jesmf", Globals.IMQ + ".jesmf.debug.all");

            debugAliases.put("pkt", Globals.IMQ + ".packet.debug.all");
            debugAliases.put("pktin", Globals.IMQ + ".packet.debug.in");
            debugAliases.put("pktout", Globals.IMQ + ".packet.debug.out");
            debugAliases.put("cluster", Globals.IMQ + ".cluster.debug.all");
            debugAliases.put("lock", Globals.IMQ + ".cluster.debug.lock");
            debugAliases.put("clscon", Globals.IMQ + ".cluster.debug.conn");
            debugAliases.put("clspkt", Globals.IMQ + ".cluster.debug.packet");
            debugAliases.put("clstxn", Globals.IMQ + ".cluster.debug.txn"+" "+
                                       Globals.IMQ + ".cluster.debug.ha");
            debugAliases.put("clsmsg", Globals.IMQ + ".cluster.debug.msg"+" "+
                                       Globals.IMQ + ".cluster.debug.txn"+" "+
                                       Globals.IMQ + ".cluster.debug.ha");
            debugAliases.put("clsha", Globals.IMQ + ".cluster.debug.ha"+" "+
                                      Globals.IMQ + ".cluster.debug.msg"+" "+
                                      Globals.IMQ + ".cluster.debug.txn");
        }

        // Get list of propNames to set to true and loop through them
        String propNames = (String)debugAliases.get(alias);

        if (propNames == null) {
            return false;
        }

        StringTokenizer st = new StringTokenizer(propNames);
        while (st.hasMoreTokens()) {
            props.put(st.nextToken(), "true");
        }

        return true;
    }
    
    String usage() {
        return rb.getString(rb.M_BROKER_USAGE);
    }

    private void printLicenses() {
    LicenseBase[] licenses =
        Globals.getLicenseManager().loadLicenses();
        //Bug Fix 4963961
	if (licenses.length == 0) {
	    println("");
	    println(rb.getString(rb.I_USING_DEFAULT_LICENSE));
	    println("");
	} else {

	    println(rb.getString(rb.M_LICENSE_MESSAGE_PREFIX));
            // dont display the same license twice
            HashSet displayed = new HashSet();
	    for (int i = 0; i < licenses.length; i++) {
		String pkg = licenses[i].getProperty(
		    LicenseBase.PROP_LICENSE_TYPE);
                if (displayed.contains(pkg)) continue;
		String description = licenses[i].getProperty(
		    LicenseBase.PROP_DESCRIPTION);
                 displayed.add(pkg);

		println("\t" + pkg + "\t-  "+ description);
            }
            println(rb.getString(rb.M_LICENSE_MESSAGE_SUBFIX));
    }
            getBroker().exit(0, "Displayed Licenses", 
             BrokerEvent.Type.SHUTDOWN);
    }

    /**
     * Add a VM shutdown hook. The hook will be called when the VM is
     * exiting.
     */
    public Thread addShutdownHook() {

    Runtime rt = Runtime.getRuntime();

    Class[] params = new Class[1];

    try {
        params[0] = Class.forName("java.lang.Thread");
        } catch (ClassNotFoundException e) {
	    // If this happens it is a coding error. No need to I18N
	   printErr(
		"addShutdownHook: can't find java.lang.Thread: " + e);
	    return null;
        }

    // Check if runtime class has addShutdownHook(Thread)
    Method m;
    try {
        m = rt.getClass().getMethod("addShutdownHook", params);
    } catch (NoSuchMethodException e) {
        // JDK1.2
        return null;
        }

    // Method exists. JDK 1.3 or newer. Add shutdownhook;
    Object[] args = new Object[1];
    args[0] = new brokerShutdownHook();
    try {
        m.invoke(rt, args);
        } catch (Exception e) {
	    // If this happens it is a coding error. No need to I18N
	   printErr(
		"addShutdownHook: could not call addShutdownHook: " + e);
	    return null;
	}

    // Return hook thread
    return (Thread)args[0];
    }

    /**
     * Remove a VM shutdown hook. 
     */
    public boolean removeShutdownHook(Thread hook) {

    Runtime rt = Runtime.getRuntime();

    Class[] params = new Class[1];

    try {
        params[0] = Class.forName("java.lang.Thread");
        } catch (ClassNotFoundException e) {
	    // If this happens it is a coding error. No need to I18N
	   printErr(
		"removeShutdownHook: can't find java.lang.Thread: " + e);
	    return false;
        }

    // Check if runtime class has removeShutdownHook(Thread)
    Method m;
    try {
        m = rt.getClass().getMethod("removeShutdownHook", params);
    } catch (NoSuchMethodException e) {
        // JDK1.2
        return false;
        }

    // Method exists. JDK 1.3 or newer. Remove shutdownhook;
    Object[] args = new Object[1];
    args[0] = hook;
    try {
        m.invoke(rt, args);
        } catch (Exception e) {
	    // If this happens it is a coding error. No need to I18N
	   printErr(
		"removeShutdownHook: could not call removeShutdownHook: " + e);
	    return false;
	}

    return true;
    }

    /**
     * main function for the broker 
     */
    public static void main(String args[]) {
        Broker b = Broker.getBroker();
        Properties p = null;
        Globals.init(System.getProperties(), false, false);
        try {
            p = b.convertArgs(args);
        } catch (EmptyStackException ex) {
            // help
            b.printErr(b.usage());
            System.exit(0);
        } catch (Exception ex) {
            // bad argument
            b.printErr(b.usage());
            System.exit(1);
        }
        try {
            int exitCode = b.start(false, p, null, b.initOnly);

            if (exitCode != 0)
                System.exit(exitCode);

        } catch (Exception ex) {
            Globals.getLogger().logStack(Logger.INFO,"Exception running broker ", ex);
            System.exit(1);
        }
    }

    // remove instance data
    public void removeInstance() {

    BrokerResources rb = Globals.getBrokerResources();
        Logger logger = Globals.getLogger();

    // check existence of the instance
    String topdir = Globals.getInstanceDir();
    if (!(new File(topdir)).exists()) {
        if (!silent) {
        // print out error message if not silent
        System.err.println(rb.getString(rb.E_INSTANCE_NOT_EXIST,
                        Globals.getConfigName()));
        }

        Broker.getBroker().exit(BrokerExitCode.INSTANCE_NOT_EXISTS, 
               rb.getString(rb.E_INSTANCE_NOT_EXIST,
                        Globals.getConfigName()),
               BrokerEvent.Type.FATAL_ERROR);
    }

    BrokerStateHandler.shuttingDown = false;
    BrokerStateHandler.shutdownStarted = false;

    // make sure no other running broker is using this instance

    BrokerConfig conf = Globals.getConfig();

        LockFile lf = null;
        try {
        lf = LockFile.getLock(
                conf.getProperty(Globals.JMQ_VAR_HOME_PROPERTY),
                Globals.getConfigName(),
                "localhost",    // no need to localize
                0);
        } catch (Exception e) {
        Object[] msgargs = {
        LockFile.getLockFilePath(conf.getProperty(
                     Globals.JMQ_VAR_HOME_PROPERTY),
                     Globals.getConfigName()),
        e.toString(),
        Globals.getConfigName()};
            logger.logStack(Logger.ERROR, rb.E_LOCKFILE_EXCEPTION, msgargs, e);
            getBroker().exit(BrokerExitCode.IOEXCEPTION,
                 rb.getKString(rb.E_LOCKFILE_EXCEPTION, msgargs),
                 BrokerEvent.Type.FATAL_ERROR);
        }

    // Make sure we got the lock
    if (!lf.isMyLock()) {
        Object[] msgargs = {
        lf.getFilePath(),
        lf.getHost() + ":" + lf.getPort(),
        Globals.getConfigName()};

        logger.log(Logger.ERROR, rb.E_LOCKFILE_INUSE, msgargs);
        getBroker().exit(BrokerExitCode.INSTANCE_BEING_USED,
                 rb.getKString(rb.E_LOCKFILE_INUSE, msgargs),
                  BrokerEvent.Type.FATAL_ERROR);
    }

    boolean loggerClosed = false;
    try {
        if (!force) {
        // get confirmation
        String yes = rb.getString(rb.M_RESPONSE_YES);
        String yes_s = rb.getString(rb.M_RESPONSE_YES_SHORT);
        String no_s = rb.getString(rb.M_RESPONSE_NO_SHORT);

        String objs[] = { Globals.getConfigName(),
                  yes_s,
                  no_s
                };

        System.out.print(rb.getString(
                rb.M_REMOVE_INSTANCE_CONFIRMATION, objs));
        System.out.flush();

        String val = (new BufferedReader(new InputStreamReader
                (System.in))).readLine();

		// if not positive confirmation, just exit!
		if (!yes_s.equalsIgnoreCase(val) &&
		    !yes.equalsIgnoreCase(val)) {
		   printErr(rb.getString(rb.I_INSTANCE_NOT_REMOVED));
		    Broker.getBroker().exit(1, 
                        rb.getString(rb.I_INSTANCE_NOT_REMOVED),
                        BrokerEvent.Type.SHUTDOWN);
		}
	    }

 
        // audit logging of remove instance
        Globals.getAuditSession().brokerOperation(null, null,MQAuditSession.REMOVE_INSTANCE);

        // delete the persistence store first
        // the property Store.I_REMOVE_PERSISTENT_STORE should
        // be set correctly in parseArgs()
        try {
            Globals.getStore();
        } catch (BrokerException ex) {
        logger.log(Logger.ERROR, ex.toString());
        Broker.getBroker().exit(BrokerExitCode.PROBLEM_REMOVING_PERSISTENT_STORE,
                   ex.toString(),
                   BrokerEvent.Type.EXCEPTION);
        }

	    // now delete the rest
	    if (!silent) {
		println(rb.getString(rb.I_REMOVE_INSTANCE));
	    }

        // close the logger to make sure we can delete the log directory
        // otherwise, in the case of NFS mounted directory, a hidden
        // file will be created when the opened log file is deleted
        // and that will prevent the log directory being removed
        logger.close();
        loggerClosed = true;

        FileUtil.removeFiles(new File(topdir), true);
        Broker.getBroker().exit(0, rb.getString(rb.I_REMOVE_INSTANCE),
                     BrokerEvent.Type.SHUTDOWN);
    } catch (IOException ex) {
        if (loggerClosed) {
        logger.open();
        }
            logger.log(Logger.ERROR, ex.toString());
        Broker.getBroker().exit(BrokerExitCode.IOEXCEPTION,
                ex.toString(), BrokerEvent.Type.FATAL_ERROR);
    }
    }



    public int getDiagInterval() {
        return diagInterval;
    }


    private void parsePassfile() throws FileNotFoundException {
    	    	
        BrokerConfig bcfg = Globals.getConfig(); 

		Properties props = new Properties();
		
		if (bcfg.getBooleanProperty(Globals.KEYSTORE_USE_PASSFILE_PROP)) {
			// read passwords from a password file

			// get password file location
        String pf_value = null;
        String pf_dir = null;
			if ((pf_value = bcfg.getProperty(Globals.KEYSTORE_PASSDIR_PROP)) != null) {
        pf_value = StringUtil.expandVariables(pf_value, bcfg);
        pf_dir = pf_value;
        } else {
				pf_dir = bcfg.getProperty(Globals.IMQ + ".etchome") + File.separator + "security";
        }
        
			String passfile_location = pf_dir + File.separator + bcfg.getProperty(Globals.KEYSTORE_PASSFILE_PROP);
        File pf = new File(passfile_location);
        if (pf.exists()) {

        // read password from passfile
        try {
					InputStream fis = FileUtil.retrieveObfuscatedFile(passfile_location);
                    props.load(fis);
				} catch (IOException ioex) {
					logger.log(Logger.ERROR, rb.getKString(BrokerResources.X_READ_PASSFILE), ioex);
				}

			} else {
				throw new FileNotFoundException(rb.getKString(rb.E_GET_PASSFILE, passfile_location));
			}
		}

		if (Globals.isReadPropertiessFromStdin() | bcfg.getBooleanProperty(Globals.KEYSTORE_USE_PASSFILE_PROP)) {

			// any passwords passed in through command line option take
			// precedence

            // db password
            if (dbPWOverride) {
            props.put(dbPWProp, bcfg.getProperty(dbPWProp));
            }

            // ldap password
            if (ldapPWOverride) {
            props.put(ldapPWProp, bcfg.getProperty(ldapPWProp));
            }

            // keystore password
            if (keystorePWOverride) {
				props.put(keystorePWProp, bcfg.getProperty(keystorePWProp));
            }

            // bridge manager password
            if (bridgeManagerPWOverride) {
				props.put(bridgeManagerPWProp, bcfg.getProperty(bridgeManagerPWProp));
            }

                bcfg.putAll(props);

    }
    }

	/**
	 * Read properties (including passwords) from standard input
	 * 
	 * @return The properties that have been read from standard input. 
	 * The caller is responsible for adding these to the broker properties
	 */
	private Properties readPropertiesFromStandardInput() {
		
		Properties props = new Properties();		
		// read properties (including passwords) from standard input
		try {
			props.load(System.in);
		} catch (IOException ioe) {
			logger.log(Logger.ERROR, rb.getKString(BrokerResources.X_READ_PASSWORD_FROM_STDIN), ioe);
		}
					
		return props;
	}

	/**
	 * Log the supplied Properties, which have been read from standard input
	 * 
	 * @param props
	 */
	private void logProperties(String prefix, Properties props) {
		
		if (props==null || props.isEmpty()) return;		
		
		// log all properties except for passwords
		boolean first=true;
		String stringToLog="";
		for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
			String thisPropertyName = (String) e.nextElement();
			String thisPropertyValue = "";
			// Broker args are logged separately
			if (!thisPropertyName.equals("BrokerArgs")){
				if (thisPropertyName.endsWith("password")){
					// don't log the password!
					thisPropertyValue = "*****";
				} else {
					thisPropertyValue = props.getProperty(thisPropertyName);
				}
				if (first) {
					first=false;
				} else {
					stringToLog += ", ";
				}
			    stringToLog+=thisPropertyName+"="+thisPropertyValue;
			}
		}
		
		if (stringToLog.equals("")) return;
		
		logger.logToAll(Logger.INFO, prefix+": "+stringToLog);
	}
	
    public void println(String line) {
        if (silent) {
            // do nothing
        } else {
            System.out.println(line);
        }
    }
    public void printErr(String line) {
        if (silent) {
            // do nothing
        } else {
           System.err.println(line);
        }
    }

    /**
     * Properly exit the Broker
     */
    public void exit(int status, String reason, BrokerEvent.Type type) {
        exit(status, reason, type, null);
    }
    public void exit(int status, String reason, BrokerEvent.Type type, Throwable thr) {
        exit(status, reason, type, thr, true, false, false);
    }
    public void exit(final int status, final String reason, 
                     final BrokerEvent.Type type, final Throwable thr, 
                     final boolean triggerFailover, 
                     final boolean threadoff, final boolean halt) {

        final boolean exitVM = !isInProcess();

        if (halt) { //should only happen when HA
            Globals.getLogger().log(Logger.WARNING, haltLogString+" "+reason); 
		    Runtime.getRuntime().halt(status);
            return;
        }

        if (threadoff) {
            Runnable r = new Runnable() {
                             public void run() {
                                 exitBroker(status, reason, type, 
                                            thr, triggerFailover, exitVM);
                             };
                         };
            Thread t = new MQThread(r, "shutdown thread");
            t.start();
        } else {
            exitBroker(status, reason, type, thr, triggerFailover, exitVM);
        }
    }


    /**
     * @param status
     * @param reason
     * @param type
     * @param thr
     * @param triggerFailover
     * @param halt Specifies how any JVM exit should be performed. If true, Runtime.getRuntime().halt() is performed.
     * @param exitVM whether to exit VM
     * If false, System.exit() is performed.
     */
    private void exitBroker(int status, String reason, BrokerEvent.Type type, Throwable thr, 
                            boolean triggerFailover, boolean exitVM) {

         Globals.getLogger().log(Logger.DEBUG,
                 "Broker exiting with status=" + status + " because " + reason);

         BridgeServiceManager bridgeManager =  Globals.getBridgeServiceManager();
         if (bridgeManager != null) {
             try {
                 Globals.getLogger().log(Logger.INFO, 
                         Globals.getBrokerResources().I_STOP_BRIDGE_SERVICE_MANAGER);

                 bridgeManager.stop();
                 Globals.setBridgeServiceManager(null);

                 Globals.getLogger().log(Logger.INFO, 
                         Globals.getBrokerResources().I_STOPPED_BRIDGE_SERVICE_MANAGER);
             } catch (Throwable t) {
                 logger.logStack(Logger.WARNING, 
                        Globals.getBrokerResources().W_STOP_BRIDGE_SERVICE_MANAGER_FAILED, t);
             }
         }

         //take a copy of bkrEvtListener as we may  null it during the course of this method
         BrokerEventListener tempListener = bkrEvtListener;
         
         // notify that the broker is about to be shut down
         // (ignore value returned by exitRequested())

         if (bkrEvtListener  != null) {
             BrokerEvent event = new BrokerEvent (this, type, reason); 
             bkrEvtListener.exitRequested(event, thr);
         }

         // Perform an orderly broker shutdown unless we're in-process, 
         // or are going to halt, in which case we want to exit as quickly as possible
         destroyBroker(!exitVM, triggerFailover);

         if (exitVM) {
             if (shutdownHook != null) {
                 ((brokerShutdownHook)shutdownHook).setTriggerFailover(triggerFailover);
             }
             System.exit(status);
         } 
          
         // if we're still here we didn't exit the broker
         if (type==BrokerEvent.Type.RESTART){
             // don't attempt to restart an embedded broker
             if (!runningInProcess){
                 // we want to restart, so tell the listener
                 if (tempListener  != null) {	  
                     BrokerEvent event = new BrokerEvent (this, type, "Broker restart");  
                     tempListener.brokerEvent(event);
                 }
             }
         }
    }

    // HANDLE THINGS LIKE OUT OF MEMORY ERRORS

    public boolean handleGlobalError(Throwable ex, String reason) {
        logger.logStack(logger.ERROR, ex.toString()+"["+ reason + "]", ex);
        int exit = Globals.getBrokerStateHandler().getRestartCode();
        if (ex instanceof OutOfMemoryError) {
            Broker.getBroker().exit(exit, 
                   "Received Out Of Memory Error ["
                    + reason + "]", BrokerEvent.Type.ERROR,
                   ex);
            if (!runningInProcess)
                Globals.getMemManager().forceRedState();
            return true; // dont sidplay internal error warning
        } else {
            Broker.getBroker().exit(exit, 
                   "Unexpected Exception " + reason,
                    BrokerEvent.Type.EXCEPTION,
                   ex);
        }
        return false;
    }

    public static void runGC() {
        System.gc();
    }

    class BrokerDiagTask extends TimerTask {
        public void run() {
            String s = DiagManager.allToString();
            if (s != null) {
                logger.log(Logger.INFO, s);
            }

        }
    }

    static final String DEFAULT_PW_CONTENT =
    "admin:-2d5455c8583c24eec82c7a1e273ea02e:admin:1\n" +
    "guest:-2c3c4a34aa2c392f39edd112333c230d:anonymous:1\n";

    static final String DEFAULT_ACL_CONTENT =
    "##########################################################\n" +
    "# MQ access control file for JMQFileAccessControlModel\n" +
    "##########################################################\n" +
    "\n" +
    "version=JMQFileAccessControlModel/100\n" +
    "\n" +
    "########################################################\n" +
    "# Please see the MQ Administration Guide for details\n" +
    "# on how to customize access control\n" +
    "#\n" +
    "# (spaces in a rule are significant)\n" +
    "########################################################\n" +
    "\n" +
    "# service connection access control\n" +
    "##################################\n" +
    "\n" +
    "connection.NORMAL.allow.user=*\n" +
    "connection.ADMIN.allow.group=admin\n" +
    "\n" +
    "# destination based access control\n" +
    "##################################\n" +
    "\n" +
    "queue.*.produce.allow.user=*\n" +
    "queue.*.consume.allow.user=*\n" +
    "queue.*.browse.allow.user=*\n" +
    "topic.*.produce.allow.user=*\n" +
    "topic.*.consume.allow.user=*\n" +
    "\n" +
    "\n" +
    "# destination auto-create access control\n" +
    "########################################\n" +
    "\n" +
    "queue.create.allow.user=*\n" +
    "topic.create.allow.user=*\n" +
    "\n" +
    "# all permissions access control setup sample\n" +
    "##############################################\n" +
    "#connection.*.usernames=*\n" +
    "#\n" +
    "#queue.*.produce.allow.user=*\n" +
    "#queue.*.consume.allow.user=*\n" +
    "#queue.*.browse.allow.user=*\n" +
    "#topic.*.produce.allow.user=*\n" +
    "#topic.*.consume.allow.user=*\n" +
    "\n" +
    "#queue.create.allow.user=*\n" +
    "#topic.create.allow.user=*\n" +
    "##############################################\n";
}

/**
 * A shutdown hook is called before the VM is going to exit.
 * This is a new feature in JDK1.3.
 */
class brokerShutdownHook extends Thread {

    boolean triggerFailover = true;

    public void setTriggerFailover(boolean v) {
        triggerFailover = v;
    }

    public void run() {

         Globals.getBrokerStateHandler().initiateShutdown(
             "brokerShutdownHook", 0, triggerFailover, 0, false, false, true);
    }
}

class VMDiagnostics implements DiagManager.Data {
    long max = 0;
    long total = 0;
    long used = 0;
    long free = 0;

    ArrayList dictionary = null;

    public VMDiagnostics() {
    }

    public synchronized void update() {
        Runtime rt = Runtime.getRuntime();

        max = rt.maxMemory();
        total = rt.totalMemory();
        free = rt.freeMemory();
        used = total - free;
    }

    public synchronized List getDictionary() {
        if (dictionary == null) {
            dictionary = new ArrayList();
            dictionary.add(new DiagDictionaryEntry("max",
                DiagManager.VARIABLE));
            dictionary.add(new DiagDictionaryEntry("total",
                DiagManager.VARIABLE));
            dictionary.add(new DiagDictionaryEntry("free",
                DiagManager.VARIABLE));
            dictionary.add(new DiagDictionaryEntry("used",
                DiagManager.VARIABLE));
        }
        return dictionary;
    }

    public String getPrefix() {
        return "java.vm.heap";
    }

    public String getTitle() {
        return "Java VM Heap";
    }
}
