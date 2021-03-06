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

import com.sun.messaging.jmq.jmsclient.XAConnectionImpl;
import com.sun.messaging.jmq.jmsclient.XASessionImpl;

/**
 *  ConnectionRequestInfo encapsulates the S1 MQ RA
 *  per connection information that is needed to
 *  create and match connections.
 */

public class ConnectionRequestInfo implements javax.resource.spi.ConnectionRequestInfo
{
    /** The ManagedConnectionFactory for this instance */
    private com.sun.messaging.jms.ra.ManagedConnectionFactory mcf = null;

    /** The XAConnection for this instance 
    private XAConnectionImpl xac = null; */

    /** The XASession for this instance 
    private XASessionImpl xas = null; */

    /** The User Name parameter for this instance */
    private String userName = null;

    /** The Password parameter for this instance */
    private String password = null;

    /** The clientID for this instance
    private String clientId = null; */

    /** The identifier (unique) for this instance */
    private transient int criId = 0;
 
    /** The uniquifier */
    private static int idCounter = 0;
 
    /* Constructor */
    public ConnectionRequestInfo(com.sun.messaging.jms.ra.ManagedConnectionFactory mcf,
        String userName, String password)
    {
        criId = ++idCounter;
        //System.out.println("MQRA:CRI:Constructor-mcf,u,p:criId="+criId);

        this.mcf = mcf;
        this.userName = userName;
        this.password = password;

        //this.clientId = mcf.getClientId();
    }

 
    /* Constructor 
    public ConnectionRequestInfo(XAConnectionImpl xac, XASessionImpl xas,
        String userName, String password)
    {
        criId = ++idCounter;
        //System.out.println("MQRA:CRI:Constructor-xac,xas,u,p:criId="+criId);

        this.xac = xac;
        this.xas = xas;
        this.userName = userName;
        this.password = password;
    }
    */

    // ConnectionRequestInfo interface methods //
    //

    /** Compares this ConnectionRequestInfo instance to one
     *  passed in for equality.
     *   
     *  @return true If the two instances are equal, otherwise
     *          return false.
     */  
    public boolean
    equals(java.lang.Object other)
    {
        //System.out.println("MQRA:CRI:equals:criId="+criId);
        if (other == null) {
            return false;
        }
        if (other instanceof com.sun.messaging.jms.ra.ConnectionRequestInfo) {
            com.sun.messaging.jms.ra.ConnectionRequestInfo otherCRI =
                (com.sun.messaging.jms.ra.ConnectionRequestInfo)other;

            //System.out.println("MQRA:CRI:equals:\tthis="+this.toString()+"\n\t\tother="+otherCRI.toString());
            String oUserName = otherCRI.getUserName();
            String oPassword = otherCRI.getPassword();
            com.sun.messaging.jms.ra.ManagedConnectionFactory oMCF = otherCRI.getMCF();

            if (
                ((oUserName != null && oUserName.equals(userName)) ||
                 (oUserName == null && userName == null))
               &&
                ((oPassword != null && oPassword.equals(password)) ||
                 (oPassword == null && password == null))
               &&
                ((oMCF != null && oMCF.equals(mcf)) ||
                 (oMCF == null && mcf == null))
               ){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }      
    }
 
    /** Returns the hash code for this ConnectionRequestInfo instance
     *   
     * @return The hash code
     */  
    public int   
    hashCode()
    {
        //The rule here is that if two objects have the same Id
        //i.e. they are equal and the .equals method returns true
        //     then the .hashCode method *must* return the same
        //     hash code for those two objects
        //So, we can simply use the criId.

        //Concat data
        String hashStr = "" + userName + password + criId;
        return hashStr.hashCode();
    }

    // Public Accessor Methods //
    //

    public com.sun.messaging.jms.ra.ManagedConnectionFactory
    getMCF()
    {
        return mcf;
    }

    /*
    public XAConnectionImpl
    getXAConnectionImpl()
    {
        return xac;
    }

    public XASessionImpl
    getXASessionImpl()
    {
        return xas;
    }
    */

    public String
    getUserName()
    {
        return userName;
    }

    public String
    getPassword()
    {
        return password;
    }

    public int
    getCRIId()
    {
        return criId;
    }

    public String toString()
    {
        return ("ConnectionRequestInfo configuration=\n"+
            "\tcriId                               ="+criId+"\n"+
            "\tUserName                            ="+userName+"\n"+
            "\tPassword                            ="+password+"\n"+
            "\tMCF configuration                   ="+(mcf !=null ? mcf.toString() : "NULL" )+"\n");
            //"\tClientId                            ="+clientId+"\n");
    }

    // Private Methods //

    private boolean
    isEqual(Object one, Object two)
    {
        if (one == null) {
            return (two == null);
        } else {
            return one.equals(two);
        }
    }
}

