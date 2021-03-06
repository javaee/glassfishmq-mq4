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
 * @(#)CmdRunner.java	1.18 06/28/07
 */ 

package com.sun.messaging.jmq.jmsserver.auth.usermgr;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;

import com.sun.messaging.jmq.jmsserver.Globals;
import com.sun.messaging.jmq.jmsserver.resources.BrokerResources;
import com.sun.messaging.jmq.util.FileUtil;

/** 
 * This class contains the logic to execute the user commands
 * specified in the UserMgrProperties object. It has one
 * public entry point which is the runCommands() method. It
 * is expected to display to the user if the command execution
 * was successful or not.
 * @see  ObjMgr
 *
 */
public class CmdRunner implements UserMgrOptions  {

    private static BrokerResources br = Globals.getBrokerResources();
    private UserMgrProperties userMgrProps;
    private PasswdDB pwDB;

    /**
     * Constructor
     */
    public CmdRunner(UserMgrProperties props) {
	this.userMgrProps = props;
    } 

    /*
     * Run/execute the user commands specified in the UserMgrProperties object.
     */
    public int runCommands() {
	int exitcode = 0;

	/*
	 * Determine type of command and invoke the relevant run method
	 * to execute the command.
	 *
	 */
	String cmd = userMgrProps.getCommand();

	pwDB = new PasswdDB();
	pwDB.setPasswordFileName(userMgrProps.getPasswordFile());

	if (cmd.equals(PROP_VALUE_CMD_ADD))  {
            exitcode = runAdd(userMgrProps);
	} else if (cmd.equals(PROP_VALUE_CMD_DELETE))  {
            exitcode = runDelete(userMgrProps);
	} else if (cmd.equals(PROP_VALUE_CMD_LIST))  {
            exitcode = runList(userMgrProps);
	} else if (cmd.equals(PROP_VALUE_CMD_UPDATE))  {
            exitcode = runUpdate(userMgrProps);

	/*
	 * Private subcommands - to support testing only
	 */
	} else if (cmd.equals(PROP_VALUE_CMD_EXISTS))  {
            exitcode = runExists(userMgrProps);
	} else if (cmd.equals(PROP_VALUE_CMD_GETGROUP))  {
            exitcode = runGetGroup(userMgrProps);
	} else if (cmd.equals(PROP_VALUE_CMD_GETGROUPSIZE))  {
            exitcode = runGetGroupSize(userMgrProps);

	} else if (cmd.equals(PROP_VALUE_CMD_ENCODE))  {
            exitcode = runEncode(userMgrProps);
	} else if (cmd.equals(PROP_VALUE_CMD_DECODE))  {
            exitcode = runDecode(userMgrProps);
	}

	return (exitcode);
    }

    private int runAdd(UserMgrProperties userMgrProps) {
	String username = userMgrProps.getUserName(),
		role = userMgrProps.getRole(),
		passwd;

	if (role == null)  {
	    role = UserInfo.ROLE_USER;
	}

	Output.stdOutPrintln(br.getString(br.I_USERMGR_INSTANCE_TITLE,
					userMgrProps.getInstance()));

	try  {
	    if (username.equals(UserInfo.DEFAULT_ANON_USERNAME) &&
		!role.equals(UserInfo.ROLE_ANON))  {
	        Output.stdErrPrintln(
		    br.getString(br.W_ADDING_USER_NAMED_GUEST));
	    }

	    passwd = getPasswordFromFileOrCmdLine(userMgrProps);

	    pwDB.addUser(username, passwd, role);
	} catch (UserMgrException ume)  {
	    handleUserMgrExceptions(ume);

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln(
		br.getString(br.I_USERMGR_ADD_FAILED));

	    return (1);
	}

	Output.stdOutPrintln(
		br.getString(br.I_USERMGR_USER_ADDED, username));
	return (0);
    }

    private int runDelete(UserMgrProperties userMgrProps) {
	String	username = userMgrProps.getUserName(),
		input;
	boolean	force = userMgrProps.forceModeSet();

	Output.stdOutPrintln(br.getString(br.I_USERMGR_INSTANCE_TITLE,
					userMgrProps.getInstance()));

	try  {
	    if (!force)  {
		String noShort = br.getString(br.M_RESPONSE_NO_SHORT);

	        input = UserMgrUtils.getUserInput(null,
			br.getString(br.I_USERMGR_Q_DELETE_OK, username),
			noShort);

	        if (noShort.equalsIgnoreCase(input))  {
	            Output.stdErrPrintln("");
	            Output.stdErrPrintln(
		        br.getString(br.I_USERMGR_USER_NOT_DELETED, username));

	            return (0);
	        }
	    }

	    UserInfo uInfo = pwDB.getUserInfo(username);
	    if ((uInfo != null) && uInfo.getRole().equals(UserInfo.ROLE_ADMIN))  {
	        int count = pwDB.getUserCount(UserInfo.ROLE_ADMIN);

		if (count == 1)  {
	            Output.stdErrPrintln(
		        br.getString(br.W_DELETING_LAST_ADMIN_USER, username));
		}
	    }

	    pwDB.deleteUser(username);
	} catch (UserMgrException ume)  {
	    handleUserMgrExceptions(ume);

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln(
	        br.getString(br.I_USERMGR_DELETE_FAILED));

	    return (1);
	}

	Output.stdOutPrintln(
		br.getString(br.I_USERMGR_USER_DELETED, username));
	return (0);
    }

    private int runList(UserMgrProperties userMgrProps) {
        UserInfo oneUser;
	String  username = userMgrProps.getUserName();
	UserPrinter up = new UserPrinter(3, 4, "-");
	String[] oneRow = new String[3];

	try  {
	    Output.stdOutPrintln(br.getString(br.I_USERMGR_INSTANCE_TITLE,
					userMgrProps.getInstance()));
	    oneRow[0] = br.getString(br.I_USERMGR_USERNAME_TITLE);
	    oneRow[1] = br.getString(br.I_USERMGR_ROLENAME_TITLE);
	    oneRow[2] = br.getString(br.I_USERMGR_ACTIVESTATE_TITLE);
	    up.addTitle(oneRow);

	    if (username == null)  {
	        for (Enumeration e=pwDB.getUsers(); e.hasMoreElements();) {
		    oneUser = (UserInfo)e.nextElement();
		    oneRow[0] = oneUser.getUser();
		    oneRow[1] = oneUser.getRole();
		    oneRow[2] = String.valueOf(oneUser.isActive());
		    up.add(oneRow);
	        }
	    } else  {
	        oneUser = pwDB.getUserInfo(username);

		if (oneUser == null)  {
	            Output.stdErrPrintln(
                        br.getString(br.E_ERROR), 
		        br.getKString(br.E_USER_NOT_EXIST, username));
	            Output.stdErrPrintln("");
	            Output.stdErrPrintln( br.getString(br.I_USERMGR_LIST_FAILED));
	            return (1);
		}

		oneRow[0] = oneUser.getUser();
		oneRow[1] = oneUser.getRole();
		oneRow[2] = String.valueOf(oneUser.isActive());
		up.add(oneRow);
	    }
	} catch (UserMgrException ume)  {
	    handleUserMgrExceptions(ume);

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln(
		br.getString(br.I_USERMGR_LIST_FAILED));

	    return (1);
	}

	up.println();

	return (0);
    }

    /*
     * Return values:
     * Old:
     *	-1	Error
     *	0	User does not exist
     *	1	User exists
     *
     * New:
     *  0       Success
     *  1       Failure
     *  Output:  "true", "false"
     */
    private int runExists(UserMgrProperties userMgrProps) {
        UserInfo oneUser;
	String  username = userMgrProps.getUserName();
	int retValue;

	try  {
	        oneUser = pwDB.getUserInfo(username);

		if (oneUser == null)  {
	            Output.stdOutPrintln(Boolean.FALSE.toString());
		    retValue = 0;
		} else  {
	            Output.stdOutPrintln(Boolean.TRUE.toString());
		    retValue = 0;
		}

	} catch (UserMgrException ume)  {
	    handleUserMgrExceptions(ume);

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln("Checking if a user exist failed.");

	    return (1);
	}

	return (retValue);
    }

    /*
     * Return values:
     * Old:
     *	-1	Error
     *	0	admin
     *	1	user
     *	2	anonymous
     * 
     * New:
     *  0       Success
     *  1       Failure
     *  Output: "admin", "user", "anonymous"
     */
    private int runGetGroup(UserMgrProperties userMgrProps) {
        UserInfo oneUser;
	String  username = userMgrProps.getUserName(),
		group;
	int retValue;

	try  {
	        oneUser = pwDB.getUserInfo(username);

		if (oneUser == null)  {
	            Output.stdErrPrintln(
                        br.getString(br.E_ERROR), 
		        br.getKString(br.E_USER_NOT_EXIST, username));
	            Output.stdErrPrintln("");
	            Output.stdErrPrintln("Getting a user's group failed.");
	            return (1);
		}

		group = oneUser.getRole();

		if (group.equals(UserInfo.ROLE_ADMIN))  {
	            Output.stdOutPrintln(UserInfo.ROLE_ADMIN);
		    retValue = 0;
		} else if (group.equals(UserInfo.ROLE_USER))  {
	            Output.stdOutPrintln(UserInfo.ROLE_USER);
		    retValue = 0;
		} else if (group.equals(UserInfo.ROLE_ANON))  {
	            Output.stdOutPrintln(UserInfo.ROLE_ANON);
		    retValue = 0;
		} else  {
	            Output.stdErrPrintln("");
	            Output.stdErrPrintln("User "
				+ username
				+ " is in unknown group: "
				+ group);
	            return (1);
		}
	} catch (UserMgrException ume)  {
	    handleUserMgrExceptions(ume);

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln("Getting a user's group failed.");

	    return (1);
	}

	return (retValue);
    }

    /*
     * Return values:
     * Old:
     *	-1	Error
     *	n	where n is number of users in group
     *
     * New:
     *  0       Success
     *  1       Failure
     */
    private int runGetGroupSize(UserMgrProperties userMgrProps) {
        UserInfo oneUser;
	String  role = userMgrProps.getRole();
	int     nEntries = 0;

	try  {
	    nEntries = pwDB.getUserCount(role);
	    Output.stdOutPrintln(Integer.toString(nEntries));
	} catch (UserMgrException ume)  {
	    handleUserMgrExceptions(ume);

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln("Getting a group's size failed.");

	    return (1);
	}

	return (0);
    }

    private int runEncode(UserMgrProperties userMgrProps) {
	String srcFile = userMgrProps.getSrc(),
		targetFile = userMgrProps.getTarget(),
		input;
	boolean	force = userMgrProps.forceModeSet();

	if (targetFile == null)  {
	    targetFile = srcFile + DEFAULT_ENCODE_PREFIX;
	}

	Output.stdOutPrintln("Encoding file where:\n");
	printSrcTarget(srcFile, targetFile);

	try  {
	    if (!force)  {
		String noShort = br.getString(br.M_RESPONSE_NO_SHORT);

	        input = UserMgrUtils.getUserInput(null,
			"Are you sure you want to encode this file ? (y/n)[n] ",
			noShort);

	        if (noShort.equalsIgnoreCase(input))  {
	            Output.stdErrPrintln("");
	            Output.stdErrPrintln(
			"File was not encoded."
			);

	            return (0);
	        }
	    }

	    /*
	     * ENCODE_LOGIC
	     * Add logic to encode here
	     *     src file:	srcFile
	     *     target file:	targetFile
	     */
	    FileUtil.obfuscateFile(srcFile, targetFile);
	} catch (Exception e)  {
	    /*
	    handleUserMgrExceptions(e);
	    */

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln(
		"Encode operation failed.\n"
		+ e.toString()
		);

	    return (1);
	}

	Output.stdOutPrintln("Encode operation successful.");

	return (0);
    }

    private int runDecode(UserMgrProperties userMgrProps) {
	String srcFile = userMgrProps.getSrc(),
		targetFile = userMgrProps.getTarget(),
		input;
	boolean	force = userMgrProps.forceModeSet();

	if (targetFile == null)  {
	    targetFile = srcFile + DEFAULT_DECODE_PREFIX;
	}

	Output.stdOutPrintln("Decoding file where:\n");
	printSrcTarget(srcFile, targetFile);

	try  {
	    if (!force)  {
		String noShort = br.getString(br.M_RESPONSE_NO_SHORT);

	        input = UserMgrUtils.getUserInput(null,
			"Are you sure you want to decode this file ? (y/n)[n] ",
			noShort);

	        if (noShort.equalsIgnoreCase(input))  {
	            Output.stdErrPrintln("");
	            Output.stdErrPrintln(
			"File was not decoded."
			);

	            return (0);
	        }
	    }

	    /*
	     * DECODE LOGIC
	     * Add logic to decode here
	     *     src file:	srcFile
	     *     target file:	targetFile
	     */
	    FileUtil.deobfuscateFile(srcFile, targetFile);
	} catch (Exception e)  {
	    /*
	    handleUserMgrExceptions(e);
	    */

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln(
		"Decode operation failed.\n"
		+ e.toString()
		);

	    return (1);
	}

	Output.stdOutPrintln("Decode operation successful.");

	return (0);
    }

    private void printSrcTarget(String srcFile, String targetFile)  {
	UserPrinter up = new UserPrinter(2, 4);
	String[] row = new String[2];


	row[0] = "Source File";
	row[1] = srcFile;
	up.add(row);

	row[0] = "Target File";
	row[1] = targetFile;
	up.add(row);

	up.println();
    }

    private String getActiveString(int activeState)  {
	return (Integer.toString(activeState));
    }

    private int runUpdate(UserMgrProperties userMgrProps) {
	String	username = userMgrProps.getUserName(),
		newPasswd, input;
	Boolean	isActive = userMgrProps.isActive();
	boolean	force = userMgrProps.forceModeSet();

	Output.stdOutPrintln(br.getString(br.I_USERMGR_INSTANCE_TITLE,
					userMgrProps.getInstance()));

	try  {
	    if (!force)  {
		String noShort = br.getString(br.M_RESPONSE_NO_SHORT);

	        input = UserMgrUtils.getUserInput(null,
			br.getString(br.I_USERMGR_Q_UPDATE_OK, username),
			noShort);

	        if (noShort.equalsIgnoreCase(input))  {
	            Output.stdErrPrintln("");
	            Output.stdErrPrintln(
		        br.getString(br.I_USERMGR_USER_NOT_UPDATED, username));

	            return (0);
	        }
	    }

	    newPasswd = getPasswordFromFileOrCmdLine(userMgrProps);

	    pwDB.updateUser(username, newPasswd, isActive);
	} catch (UserMgrException ume)  {
	    handleUserMgrExceptions(ume);

	    Output.stdErrPrintln("");
	    Output.stdErrPrintln(
		br.getString(br.I_USERMGR_UPDATE_FAILED));

	    return (1);
	}

	Output.stdOutPrintln(
		br.getString(br.I_USERMGR_USER_UPDATED, username));
	return (0);
    }

    private void handleUserMgrExceptions(UserMgrException ume)  {
	Exception ex = ume.getLinkedException();
	String	pwFile = ume.getPasswordFile(),
		userName = ume.getUserName();
	int	type = ume.getType();

	switch (type)  {
	case UserMgrException.PW_FILE_NOT_FOUND:
	    Output.stdErrPrintln(
                br.getString(br.E_INTERNAL_ERROR), 
		br.getKString(br.E_PW_FILE_NOT_FOUND, pwFile));
	break;

	case UserMgrException.PW_FILE_FORMAT_ERROR:
	    Output.stdErrPrintln(
                br.getString(br.E_INTERNAL_ERROR), 
		br.getKString(br.E_PW_FILE_FORMAT_ERROR, pwFile));
	break;

	case UserMgrException.USER_NOT_EXIST:
	    Output.stdErrPrintln(
                br.getString(br.E_ERROR), 
		br.getKString(br.E_USER_NOT_EXIST, userName));
	break;

	case UserMgrException.USER_ALREADY_EXIST:
	    Output.stdErrPrintln(
                br.getString(br.E_ERROR), 
		br.getKString(br.E_USER_ALREADY_EXIST, userName));
	break;

	case UserMgrException.PASSWD_INCORRECT:
	    Output.stdErrPrintln(
                br.getString(br.E_ERROR), 
		br.getKString(br.E_PASSWD_INCORRECT));
	break;

	case UserMgrException.PW_FILE_WRITE_ERROR:
	    Output.stdErrPrintln(
                br.getString(br.E_INTERNAL_ERROR), 
		br.getKString(br.E_PW_FILE_WRITE_ERROR, pwFile, ex));
	break;

	case UserMgrException.PW_FILE_READ_ERROR:
	    Output.stdErrPrintln(
                br.getString(br.E_INTERNAL_ERROR), 
		br.getKString(br.E_PW_FILE_READ_ERROR, pwFile, ex));
	break;

	case UserMgrException.ONLY_ONE_ANON_USER:
	    Output.stdErrPrintln(
                br.getString(br.E_ERROR), 
		br.getKString(br.E_ONLY_ONE_ANON_USER));
	break;

	case UserMgrException.PROBLEM_GETTING_INPUT:
	    Output.stdErrPrintln(
                br.getString(br.E_ERROR), 
		br.getKString(br.E_PROBLEM_GETTING_INPUT));
	break;

	case UserMgrException.PASSWD_ENCRYPT_FAIL:
	    Output.stdErrPrintln(
                br.getString(br.E_INTERNAL_ERROR), 
		br.getKString(br.E_PASSWD_ENCRYPT_FAIL, ex));
	break;

	case UserMgrException.READ_PASSFILE_FAIL:
	    Output.stdErrPrintln(
                br.getString(br.E_INTERNAL_ERROR), 
		br.getKString(br.E_READ_PASSFILE_FAIL, ex));
	break;

	}
    }

    /*
     * Get password from either the passfile or -p option.
     * In some future release, the -p option will go away
     * leaving the passfile the only way to specify the 
     * password (besides prompting the user for it).
     */
    private String getPasswordFromFileOrCmdLine(UserMgrProperties userMgrProps) 
		throws UserMgrException  {
        String passwd = userMgrProps.getPassword(),
	       passfile = userMgrProps.getPassfile();
	
	if (passwd != null)  {
	    return (passwd);
	}

	if (passfile != null)  {
	    String ret = null;
	    try  {
	        File f = new File(passfile);
	        FileReader fr = new FileReader(f);
	        BufferedReader bfr = new BufferedReader(fr);

		ret = bfr.readLine();

		bfr.close();
		fr.close();
	    } catch(Exception e)  {
		UserMgrException ume = 
			new UserMgrException(UserMgrException.READ_PASSFILE_FAIL);
		ume.setProperties(userMgrProps);
		ume.setLinkedException(e);

		throw (ume);
	    }
	    return (ret);
	}
	
	return (null);
    }
}
