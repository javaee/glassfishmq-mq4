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

package com.sun.messaging.jms.ra;

import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.security.auth.Subject;

import javax.jms.JMSException;
import javax.jms.InvalidClientIDException;

import javax.resource.*;
import javax.resource.spi.*;
import javax.resource.spi.security.PasswordCredential;

import javax.transaction.xa.XAResource;

import com.sun.messaging.jmq.jmsclient.XAConnectionImpl;
import com.sun.messaging.jmq.jmsclient.XASessionImpl;

/**
 *  Implements the ManagedConnection interface of the Java EE Connector
 *  Architecture.
 */

public class ManagedConnection
implements javax.resource.spi.ManagedConnection
{
    /** The ResourceAdapter instance associated with this ManagedConnection */
    private com.sun.messaging.jms.ra.ResourceAdapter ra = null;

    /** The ManagedConnectionFactory instance associated with this ManagedConnection */
    private com.sun.messaging.jms.ra.ManagedConnectionFactory mcf = null;

    /** The Subject instance associated with this ManagedConnection */
    private Subject subject = null;

    /** The ConnectionRequestInfo instance associated with this ManagedConnection */
    private com.sun.messaging.jms.ra.ConnectionRequestInfo crInfo = null;

    /** The ConnectionAdapter for the XAConnection */
    private ConnectionAdapter ca = null;

    /** The XAResource for this ManagedConnection */
    private com.sun.messaging.jmq.jmsclient.XAResourceForMC xar = null;
    private DirectXAResource dxar = null;

    /** The XAConnection for this ManagedConnection */
    private com.sun.messaging.jmq.jmsclient.XAConnectionImpl xac = null;
    private DirectConnection dc = null;

    /** The Connection Event Listener for this ManagedConnection */
    private com.sun.messaging.jms.ra.ConnectionEventListener evtlistener = null;

    /** The LocalTransaction for this ManagedConnection */
    private com.sun.messaging.jms.ra.LocalTransaction localTransaction = null;
    private com.sun.messaging.jms.ra.DirectLocalTransaction directLocalTransaction = null;
    /** Flag to indicate whether the LocalTransaction is active*/
    private boolean ltActive = false;

    /** The ManagedConnectionMetaData for this ManagedConnection */
    private com.sun.messaging.jms.ra.ManagedConnectionMetaData mcMetaData = null;

    /** The Password Credential for this ManagedConnection */
    private javax.resource.spi.security.PasswordCredential pwCredential = null;

    // Keep only one SessionAdapter per ManagedConnection //
    // If this is enhanced, then we have to keep a Set here
    //     and update ManagedConnectionMetaData
    //
    /** The SessionAdapter for this ManagedConnection */
    private com.sun.messaging.jms.ra.SessionAdapter sa = null;

    // whether this connection uses old-style direct mode implemented in the RA
    private boolean isRADirect = false;

    /** Flag to indicate whether this ManagedConnection has been destroyed */
    private boolean destroyed = false;

    /* Indicates whether pwCredential is valid or not - invalid if null or empty */
    private boolean pwcValid;

    /** The PrintWriter set on this ManagedConnectionFactory */
    private PrintWriter logWriter = null;

    /** The identifier (unique in a VM) for this ManagedConnection */
    private transient int mcId = 0;

    /** The uniquifier */
    private static int idCounter = 0;

    /* Loggers */
    private static transient final String _className =
            "com.sun.messaging.jms.ra.ManagedConnectionFactory";
    protected static transient final String _lgrNameOutboundConnection =
            "javax.resourceadapter.mqjmsra.outbound.connection";
    protected static transient final Logger _loggerOC =
            Logger.getLogger(_lgrNameOutboundConnection);
    protected static transient final String _lgrMIDPrefix = "MQJMSRA_MC";
    protected static transient final String _lgrMID_EET = _lgrMIDPrefix + "1001: ";
    protected static transient final String _lgrMID_INF = _lgrMIDPrefix + "1101: ";
    protected static transient final String _lgrMID_WRN = _lgrMIDPrefix + "2001: ";
    protected static transient final String _lgrMID_ERR = _lgrMIDPrefix + "3001: ";
    protected static transient final String _lgrMID_EXC = _lgrMIDPrefix + "4001: ";
 
    /** Constructor */
    public ManagedConnection(com.sun.messaging.jms.ra.ManagedConnectionFactory mcf,
            Subject subject,
            com.sun.messaging.jms.ra.ConnectionRequestInfo cxRequestInfo,
            com.sun.messaging.jms.ra.ResourceAdapter ra)
    throws ResourceException
    {
        //XAConnectionImpl xac;
        String un, pw;

        _loggerOC.entering(_className, "constructor()");

        //Each instance gets its own Id
        mcId = ++idCounter;

        this.mcf = mcf;
        this.isRADirect = mcf.getEnableRADirect();
        this.subject = subject;
        this.crInfo = cxRequestInfo;
        this.ra = ra;
        pwCredential = Util.getPasswordCredential(mcf, subject, cxRequestInfo);
        pwcValid = Util.isPasswordCredentialValid(subject);
        if (pwCredential != null){
            if (pwcValid) {
                //CONT AUTH case - app must not use createConnection(u, p);
                if ((cxRequestInfo != null) && (cxRequestInfo.getUserName() != null)) {
                    _loggerOC.fine(_lgrMID_WRN+"createConnection API used w/ username, password for Container Auth");
                }
            } else {
                //APP AUTH case - app must use createConnection(u, p);
                if ((cxRequestInfo != null) && (cxRequestInfo.getUserName() == null)) {
                    _loggerOC.fine(_lgrMID_WRN+"createConnection API used w/o username, password for Application Auth");
                }
            }
            un = pwCredential.getUserName();
            pw = new String (pwCredential.getPassword());
            _loggerOC.finer(_lgrMID_INF+"constructor:Using pwCred:u,p="+un);
        } else {
            un = mcf.getUserName();
            pw = mcf.getPassword();
            _loggerOC.finer(_lgrMID_INF+"constructor:Using mcfConfig:u,p="+un);
        }
        try {
            _loggerOC.finer(_lgrMID_INF+"constructor:Creating mcId="+mcId+":Using xacf config="+mcf._getXACF().getCurrentConfiguration());
            
            ConnectionCreator cc = mcf.getConnectionCreator();
            if (this.isRADirect) {
                this.dc = (DirectConnection)cc._createConnection(un, pw);
                this.dc.setManaged(true, this);
            } else {
                xac = (XAConnectionImpl)(mcf._getXACF()).createXAConnection(un, pw);
            }
            
        } catch (JMSException jmse) {
            ResourceAdapterInternalException raie = new ResourceAdapterInternalException(
                    _lgrMID_EXC+"constructor:Aborting:JMSException on createConnection="+jmse.getMessage());
            raie.initCause(jmse);
            _loggerOC.severe(raie.getMessage());
            jmse.printStackTrace();
            _loggerOC.throwing(_className, "constructor()", raie);
            throw raie;
        }
        if (true) {
        } else { //XXX:tharakan REMOVE
            //System.out.println("MQRA:MC:Constr:Using MCF:u,p="+mcf.getUserName()+","+mcf.getPassword());
            try {
                //System.out.println("MQRA:MC:Constr:using xacf config="+mcf._getXACF().getCurrentConfiguration());
                xac = (XAConnectionImpl)(mcf._getXACF()).createXAConnection(
                                                            mcf.getUserName(), mcf.getPassword());
            } catch (JMSException jmse) {
                String errMsg = "MQRA:MC:Constr:Exception on cnxn creation-"+jmse.getMessage();
                System.err.println(errMsg);
                jmse.printStackTrace();
                throw new ResourceAdapterInternalException(errMsg, jmse);
            }
        }

        evtlistener = new com.sun.messaging.jms.ra.ConnectionEventListener(this);

        try {
            if (this.isRADirect){
                this.dc._setExceptionListener(evtlistener);
                this.directLocalTransaction = new
                        com.sun.messaging.jms.ra.DirectLocalTransaction(this, dc);
            } else {
                xac._setExceptionListenerFromRA(evtlistener);
                xac.setExtendedEventNotification(true);
                ca = new ConnectionAdapter(this, xac, ra);
                mcMetaData = new
                        com.sun.messaging.jms.ra.ManagedConnectionMetaData(this);
                localTransaction = new
                        com.sun.messaging.jms.ra.LocalTransaction(this, xac);
                _loggerOC.fine(_lgrMID_INF+"constructor:Created mcId="+
                        mcId+":xacId="+
                        xac._getConnectionID()+
                        ":Using xacf config="+
                        mcf._getXACF().getCurrentConfiguration());
                ((com.sun.messaging.jms.Connection)xac).setEventListener(evtlistener);
            }
        } catch (JMSException jmse) {
            String errMsg = "MQRA:MC:Constr:Exception on setExceptionListener-"+jmse.getMessage();
                throw new ResourceAdapterInternalException
                    ("MQRA:MC:JMSException upon setExceptionListener", jmse);
        }
        
        try {
            if (this.isRADirect){
                this.dxar = this.dc._getXAResource();
            } else {
                xar = new
                com.sun.messaging.jmq.jmsclient.XAResourceForMC(this, xac, xac);
            }
        } catch (JMSException jmse) {
            String errMsg = "MQRA:MC:Constr:Exception on xar creation-"+jmse.getMessage();
            System.err.println(errMsg);
            jmse.printStackTrace();
            throw new ResourceAdapterInternalException(errMsg, jmse);
        }
        logWriter = null;
        _loggerOC.exiting(_className, "constructor()");
    }


    // ManagedConnection interface methods //
    // 

    /** Adds a ConnectionEventListener to this ManagedConnection
     *
     *  @param listener The ConnectionEventListener to be added
     */
    public void
    addConnectionEventListener(javax.resource.spi.ConnectionEventListener listener)
    {
        _loggerOC.entering(_className, "addConnectionEventListener():mcId="+mcId, listener);
        evtlistener.addConnectionEventListener(listener);
    }

    /** Removes a ConnectionEventListener from this ManagedConnection
     *
     *  @param listener The ConnectionEventListener to be removed
     */
    public void
    removeConnectionEventListener (javax.resource.spi.ConnectionEventListener listener)
    {
        _loggerOC.entering(_className, "removeConnectionEventListener():mcId="+mcId, listener);
        evtlistener.removeConnectionEventListener(listener);
    }

    /** Forces this ManagedConnection to cleanup any client maintained
     *  state that it holds. Any subsequent attempt by an application
     *  component to use this connection after this must result in an
     *  Exception.
     */
    public void
    cleanup()
    throws ResourceException
    {
        _loggerOC.entering(_className, "cleanup():mcId="+mcId);
        checkDestroyed();
        if (this.isRADirect) {
            try {
                this.dc._cleanup();
            } catch (JMSException ex) {
                throw new ResourceException(ex);
                //ex.printStackTrace();
            }
        } else {
            //Close the sessions on the ca for this mc
            if (ca != null) {
                ca.cleanup();
            }
        }
    }

    /** Destroys this ManagedConnection and any client maintained
     *  state that it holds. Any subsequent attempt by an application
     *  component to use this connection after this must result in an
     *  Exception.
     */
    public void
    destroy()
    throws ResourceException
    {
        //_loggerOC.entering(_className, "destroy():mcId="+mcId+":xacId="+xac._getConnectionID());
        if (destroyed) {
            _loggerOC.warning(_lgrMID_WRN+"destroy:Previously destroyed-mcId="+mcId);
        } else {
            if (this.isRADirect) {
                try {
               	   this.dc.closeAndDestroy(); 
                } catch (JMSException ex) {
                    throw new ResourceException(ex);
                    //ex.printStackTrace();
                }
            } else { 
            //Close the physical connection
                if (ca != null) {
                    //System.out.println("MQRA:MC:destroy:mcId="+mcId+":destroy ca");
                    ca.destroy();
                }
            }
            //System.out.println("MQRA:MC:destroy:mcId="+mcId+":mark destroyED");
            destroyed = true;
        }
    }

    /** Returns the XAResource instance for this ManagedConnection
     *  instance
     *
     *  @return A javax.transaction.xa.XAResource instance
     */
    public javax.transaction.xa.XAResource
    getXAResource()
    throws ResourceException
    {
        _loggerOC.entering(_className, "getXAResource():mcId="+mcId);
        checkDestroyed();
        if (this.isRADirect){
            return dxar;
        } else {
            return xar;
        }
    }

    /** Returns the LocalTransaction instance for this ManagedConnection
     *  instance
     *
     *  @return A javax.resource.spi.LocalTransaction instance
     */
    public javax.resource.spi.LocalTransaction
    getLocalTransaction()
    throws ResourceException
    {
        _loggerOC.entering(_className, "getLocalTransaction():mcId="+mcId);
        checkDestroyed();
        if (this.isRADirect){
            return this.directLocalTransaction;
        } else {
            return localTransaction;
        }
    }

    /** Returns the ManagedConnectionMetaData instance for this
     *  ManagedConnection instance
     *
     *  @return A javax.resource.spi.ManagedConnectionMetaData instance
     */
    public javax.resource.spi.ManagedConnectionMetaData
    getMetaData()
    throws ResourceException
    {
        _loggerOC.entering(_className, "getMetaData():mcId="+mcId);
        checkDestroyed();
        return mcMetaData;
    }

    /** Returns a new connection handle.
     *  A ConnectionAdapter is returned. ConnectionMetaData informs that
     *  MaxConnections is 1.
     *  Hence this is called only once per ManagedConnection instance.
     *
     *  @param subject The javax.security.auth.Subject that is to be
     *         used for credentials
     *  @param cxRequestInfo The ConnectionRequestInfo that is to be used
     *         for connection matching
     *
     *  @return A JMS SessionAdapter instance
     */
    public java.lang.Object
    getConnection(Subject subject,
            javax.resource.spi.ConnectionRequestInfo cxRequestInfo)
    throws ResourceException
    {

        Object params[] = new Object[2];
        params[0] = subject;
        params[1] = cxRequestInfo;

        javax.resource.spi.security.PasswordCredential pwCred;
        com.sun.messaging.jms.ra.ConnectionRequestInfo cri =
                (com.sun.messaging.jms.ra.ConnectionRequestInfo)cxRequestInfo;

        //_loggerOC.entering(_className, "getConnection():mcId="+mcId+":xacId="+xac._getConnectionID(), params);

        checkDestroyed();

        pwCred = Util.getPasswordCredential(mcf, subject, cri);
        //System.out.println("MQRA:MC:getConn:subject="+ ((subject!= null) ? subject.toString() : "null-subject" ));
        //System.out.println("MQRA:MC:getConn:cxReqInfo="+ ((cri!= null) ? cri.toString() : "null-cxRequestInfo" ));

        if (!Util.isPasswordCredentialEqual(pwCred, pwCredential)) {
            throw new javax.resource.spi.SecurityException(
                "MQRA:MC:getConnection-auth failed for Subject-"+((subject!= null) ? subject.toString() : "null-subject" ));
        }
//        if (false) {
//        if ( (this.subject != null && !this.subject.equals(subject)) ||
//             ((this.subject == null) && (subject != null))
//            ) {
//            System.err.println("MQRA:MC:getConnection():Exception:Cannot use Subject");
//            throw new javax.resource.spi.SecurityException("MQRA:MC:getConnection-cannot use Subject-"
//                        +((subject!= null) ? subject.toString() : "null-subject" )
//                        +" for this MC Subject-"
//                        +((this.subject != null) ? this.subject.toString() : "null-MC-subject"));
//        }
//        if ( (this.crInfo != null && !this.crInfo.equals(cxRequestInfo)) ||
//             ((this.crInfo == null) && (cxRequestInfo != null))
//            ) {
//            System.err.println("MQRA:MC:getConnection():Exception:Cannot use ConnectionRequestInfo");
//            throw new javax.resource.spi.SecurityException("MQRA:MC:getConnection-cannot use ConnectionRequestInfo-"
//                        +cxRequestInfo.toString()
//                        +" for this MC ConnectionRequestInfo-"
//                        +crInfo.toString() );
//        }
//        }
        //System.out.println("MQRA:MC:getConnection()-returning ca");
        String cid = mcf.getClientId();
        if (cid != null) {
            try {
                if (this.isRADirect){
                    _loggerOC.fine(_lgrMID_INF+"getConnection():mcId=" +
                            mcId + ":xacId=" + this.dc.getConnectionId() +
                            ":opening CA;setting ClientId:"+cid);
                    this.dc._activate(cid);
                } else{
                    _loggerOC.fine(_lgrMID_INF+"getConnection():mcId=" +
                            mcId + ":xacId=" + xac._getConnectionID() +
                            ":opening CA;setting ClientId:"+cid);
                    ca.open(cid);
                }
            } catch (InvalidClientIDException icide) {
                ResourceException re = new EISSystemException("MQRA:MC:InvalidClientIDException-"+icide.getMessage());
                re.initCause(icide);
                throw re;
            } catch (JMSException jmse) {
                ResourceException re = new EISSystemException("MQRA:MC:JMSException-"+jmse.getMessage());
                re.initCause(jmse);
                throw re;
            }
        } else {
            if (this.isRADirect){
                try {
                    this.dc._activate(null);

                } catch (InvalidClientIDException icide) {
                    ResourceException re = new EISSystemException(
                            "MQRA:MC:InvalidClientIDException-" +
                            icide.getMessage());
                    re.initCause(icide);
                    throw re;
                } catch (JMSException jmse) {
                    ResourceException re = new EISSystemException(
                            "MQRA:MC:JMSException-" + jmse.getMessage());
                    re.initCause(jmse);
                    throw re;
                }
            } else {
                _loggerOC.fine(_lgrMID_INF+"getConnection():mcId="+mcId+":xacId="+xac._getConnectionID()+":opening CA;NO ClientId");
                ca.open();
        }
        }
        if (this.isRADirect){
            return this.dc;
        } else {
            return ca;
        }
    }

    /** Associates an application-level connection handle
     *  with this ManagedConnection instance.
     *
     *  @param connection The connection to associate
     *
     */
    public void
    associateConnection(java.lang.Object connection)
    throws ResourceException
    {
        _loggerOC.entering(_className, "associateConnection():mcId="+mcId, connection);

        checkDestroyed();

        if (this.isRADirect){
            return;
        }
        //Check that 'connection' is our ConnectionAdapter
        if (connection instanceof ConnectionAdapter) {
            ConnectionAdapter connection_adapter = (ConnectionAdapter)connection;
            connection_adapter.associateManagedConnection(this);
            this.ca = connection_adapter;

        } else {
            throw new ResourceException("MQRA:MC:associateConnection-invalid connection:class="+
                connection.getClass()+":toString="+connection.toString());
        }
    }

    /** Sets the PrintWriter to be used by the ResourceAdapter for logging
     *
     *  @param out The PrintWriter to be used
     */
    public void
    setLogWriter(PrintWriter out)
    throws ResourceException
    {
        _loggerOC.entering(_className, "setLogWriter():mcId="+mcId, out);
        logWriter = out;
    }
 
    /** Returns the PrintWriter being used by the ResourceAdapter for logging
     *
     *  @return The PrintWriter being used
     */
    public PrintWriter
    getLogWriter()
    throws ResourceException
    {
        _loggerOC.entering(_className, "getLogWriter():mcId="+mcId, logWriter);
        return logWriter;
    }

    // Public Methods //
    //

    public int
    getMCId()
    {
        return mcId;
    }

    public com.sun.messaging.jms.ra.ManagedConnectionFactory
    getManagedConnectionFactory()
    {
        return mcf;
    }

    public javax.resource.spi.security.PasswordCredential
    getPasswordCredential()
    {
        return pwCredential;
    }

    public ConnectionAdapter
    getConnectionAdapter()
    {
        return ca;
    }

    public boolean
    isDestroyed()
    {
        return destroyed;
    }

    public boolean
    xaTransactionStarted()
    {
        if (ltActive)
        {
            if (this.isRADirect){
                return this.directLocalTransaction.started();
            } else {
                return localTransaction.started();
            }
        }
        else
        {
            if (this.isRADirect){
                return this.dxar.isEnlisted();
            } else {
                return xar.started();
            }
        }
    }

    public boolean
    xaTransactionActive()
    {
        if (ltActive)
        {
            if (this.isRADirect){
                return this.directLocalTransaction.isActive();
            } else {
                return localTransaction.isActive();
            }
        }
        else
        {
            if (this.isRADirect){
                return this.dxar.isEnlisted();
            } else {
                return xar.isActive();
            }
        }
    }

    public long
    getTransactionID()
    {
        if (ltActive)
        {
            if (this.isRADirect){
                return this.directLocalTransaction.getTransactionID();
            } else {
                return localTransaction.getTransactionID();
            }
        }
        else
        {
            if (this.isRADirect){
                return this.dxar._getTransactionId();
            } else {
                return xar.getTransactionID();
            }
        }
    }

    public void
    setLTActive(boolean active)
    {
        ltActive = active;
    }

    public void
    sendEvent(int type, Exception ex)
    {
        evtlistener.sendEvent(type, ex, null);
    }

    public void
    sendEvent(int type, Exception ex, Object handle)
    {
        //System.out.println("MQRA:MC:sent Event type="+type);
        evtlistener.sendEvent(type, ex, handle);
    }

    protected void _setDirect(boolean value) {
        this.isRADirect = value;
    }

    // Private Methods
    //

    /** Checks if this ManagedConnection has been destoyed and
     *  throws an IllegalStateException if it has
     */
    private void
    checkDestroyed()
    throws ResourceException
    {
        if (destroyed) {
            throw new javax.resource.spi.IllegalStateException(
                "MQRA:MC:Destroyed-Id="+mcId);
        }
    }

    
}

