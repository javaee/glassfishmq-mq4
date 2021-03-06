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
 * @(#)AdminConsole.java	1.39 06/27/07
 */ 

package com.sun.messaging.jmq.admin.apps.console;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JOptionPane;

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.sun.messaging.jmq.Version;
import com.sun.messaging.jmq.admin.util.Globals;
import com.sun.messaging.jmq.admin.bkrutil.BrokerAdmin;
import com.sun.messaging.jmq.admin.objstore.ObjStoreManager;
import com.sun.messaging.jmq.admin.resources.AdminResources;
import com.sun.messaging.jmq.admin.resources.AdminConsoleResources;

/** 
 * This is the main application for the JMQ Administration Console. It
 * allows one to administer:
 *
 * <OL>
 * <LI>JMS Objects in object stores
 * <LI>JMQ Message brokers
 * </OL>
 * 
 * The user will execute this application by running a script.
 *
 */
public class AdminConsole extends JFrame implements AdminApp {

    private static AdminConsoleResources acr = Globals.getAdminConsoleResources();
    private static AdminResources ar = Globals.getAdminResources();

    private AMenuBar		menuBar = null;
    private AToolBar		toolBar = null;
    private AExplorer		explorer = null;
    private AInspector		inspector = null;
    private AStatusArea		statusArea = null;
    private ActionManager	actionMgr = null;
    private AController		controller = null;
    private ObjStoreListCObj	oslCObj = null;
    private BrokerListCObj	blCObj = null;

    private ConsoleObj		selObj = null;

    public static final String CONSOLE_VERSION1 = "-v";
    public static final String CONSOLE_VERSION2 = "-version";
    public static final String OPTION_HELP1 = "-h";
    public static final String OPTION_HELP2 = "-help";
    public static final String OPTION_DEBUG	= "-debug";
    public static final String OPTION_VERBOSE	= "-verbose";
    public static final String OPTION_VARHOME	= "-varhome";
    public static final String OPTION_RECV_TIMEOUT	= "-rtm";
    public static final String OPTION_NUM_RETRIES	= "-rtr";
    public static final String OPTION_JAVAHOME	= "-javahome";

    private static String close[] = {acr.getString(acr.I_DIALOG_CLOSE)};

    /**
     * Constructor
     */
    public AdminConsole() {
	super("");
	setTitle(acr.getString(acr.I_ADMIN_CONSOLE,
		Globals.getVersion().getProductName()));

	initMgrs();
	initGui();
	controller.init();
    } 

    /*
     * BEGIN INTERFACE AdminApp
     */
    public Frame getFrame()  {
	return ((Frame)this);
    }
    public AMenuBar getMenubar()  {
	return (menuBar);
    }
    public AToolBar getToolbar()  {
	return (toolBar);
    }
    public AExplorer getExplorer()  {
	return (explorer);
    }
    public AInspector getInspector()  {
	return (inspector);
    }
    public AStatusArea getStatusArea()  {
	return (statusArea);
    }
    public ActionManager getActionManager()  {
	return (actionMgr);
    }
    public ObjStoreListCObj getObjStoreListCObj()  {
        return (oslCObj);
    }
    public BrokerListCObj getBrokerListCObj()  {
        return (blCObj);
    }
    public void setSelectedObj(ConsoleObj obj)  {
	this.selObj = obj;
    }
    public ConsoleObj getSelectedObj()  {
	return (this.selObj);
    }
    public void setSelectedObjs(ConsoleObj obj[])  {
    }
    public ConsoleObj[] getSelectedObjs()  {
	return (null);
    }
    /*
     * END INTERFACE AdminApp
     */

    private void initMgrs()  {
        ConsoleObjStoreManager	osMgr;
	ConsoleBrokerAdminManager baMgr;

	osMgr = ConsoleObjStoreManager.getConsoleObjStoreManager();
	oslCObj = new ObjStoreListCObj(osMgr);

	baMgr = new ConsoleBrokerAdminManager();
        blCObj = new BrokerListCObj(baMgr);
    }

    private void initGui()  {
	/*
	 * set layout to be border layout
	 */
	getContentPane().setLayout(new BorderLayout());

	/*
	 * Create all the UI components
	 */
        actionMgr = new ActionManager();
	menuBar = new AMenuBar(actionMgr);
	toolBar = new AToolBar(actionMgr);
        explorer = new AExplorer(actionMgr, oslCObj, blCObj);
        inspector = new AInspector();
        statusArea = new AStatusArea();
        controller = new AController(this);

	/*
	 * Hook up all components to send their
	 * events to the controller.
	 */
	explorer.addAdminEventListener(controller);
	inspector.addAdminEventListener(controller);
	actionMgr.addAdminEventListener(controller);

	actionMgr.setActiveActions(0);

	/*
	 * Set the menubar of the application
	 */
	setJMenuBar(menuBar);

	/*
	 * Add the toolbar
	 */
        getContentPane().add(toolBar, BorderLayout.NORTH);

	/*
	 * Create a split pane for the explorer/inspector
	 * pane
	 */
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200); //XXX: ignored in some releases
                                           //of Swing. bug 4101306
        splitPane.setOneTouchExpandable(true);
        splitPane.setPreferredSize(new Dimension(750, 450));
        splitPane.setTopComponent(explorer);
        splitPane.setBottomComponent(inspector);

	/*
	 * Create another split pane containing the splitpane above
	 * and the status area.
	 */
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane2.setOneTouchExpandable(true);
        splitPane2.setTopComponent(splitPane);
        splitPane2.setBottomComponent(statusArea);

	/*
	 * Add splitpanes containing explorer, inspector and status area.
	 */
        getContentPane().add(splitPane2, BorderLayout.CENTER);

	statusArea.appendText(acr.getString(acr.I_ADMIN_CONSOLE,
				Globals.getVersion().getProductName())
				+ "\n");

	setIconImage(AGraphics.adminImages[AGraphics.DESKTOP_ICON].getImage());
    }

    private static void processCmdlineArgs(String args[]) {
        for (int i = 0; i < args.length; ++i)  {
            if (args[i].equals(CONSOLE_VERSION1) ||
                    args[i].equals(CONSOLE_VERSION2)) {
                printBanner();
                printVersion();
                System.exit(0);
            } else if (args[i].equals(OPTION_HELP1) ||
                    args[i].equals(OPTION_HELP2)) {
                printHelp();
                System.exit(0);
            } else if (args[i].equals(OPTION_DEBUG))  {
	        BrokerAdmin.setDebug(true);
            } else if (args[i].equals(OPTION_RECV_TIMEOUT))  {
		if (i == (args.length - 1))  {
		    Globals.stdErrPrintln(
			ar.getString(ar.I_ERROR_MESG),
			acr.getKString(acr.I_ARG_EXPECTED, args[i]), false);
                    System.exit(1);
		}

		++i;

		String val = args[i];
		long longVal = 0;

		try  {
		    longVal = Long.parseLong(val);
		} catch (Exception e) {
		    Globals.stdErrPrintln(
			ar.getString(ar.I_ERROR_MESG),
			acr.getKString(acr.E_BAD_RECV_TIMEOUT_VAL, val), false);
                    System.exit(1);
		}

	        BrokerAdmin.setDefaultTimeout(longVal * 1000);
            } else if (args[i].equals(OPTION_NUM_RETRIES))  {
		if (i == (args.length - 1))  {
		    Globals.stdErrPrintln(
			ar.getString(ar.I_ERROR_MESG),
			acr.getKString(acr.I_ARG_EXPECTED, args[i]), false);
                    System.exit(1);
		}

		++i;

		String val = args[i];
		int intVal = 0;

		try  {
		    intVal = Integer.parseInt(val);
		} catch (Exception e) {
		    Globals.stdErrPrintln(
			ar.getString(ar.I_ERROR_MESG),
			acr.getKString(acr.E_BAD_NUM_RETRIES_VAL, val), false);
                    System.exit(1);
		}

	        BrokerAdmin.setDefaultNumRetries(intVal);
            } else if (args[i].equals(OPTION_JAVAHOME))  {
		if (i == (args.length - 1))  {
		    Globals.stdErrPrintln(
			ar.getString(ar.I_ERROR_MESG),
			acr.getKString(acr.I_ARG_EXPECTED, args[i]), false);
                    System.exit(1);
		}

		++i;
            } else if (args[i].equals(OPTION_VERBOSE))  {
		// ignore. -verbose is handled by wrapper script
            } else if (args[i].equals(OPTION_VARHOME))  {
		// ignore. -varhome is handled by wrapper script
		++i;
	    } else  {
		Globals.stdErrPrintln(
			ar.getString(ar.I_ERROR_MESG),
			acr.getKString(acr.I_UNRECOGNIZED_OPT, args[i]), false);

                System.exit(1);
	    } 
        }
    }

    private static void printVersion() {

        Version version = Globals.getVersion();
        Globals.stdOutPrintln(version.getVersion());
        Globals.stdOutPrintln(ar.getString(ar.I_JAVA_VERSION) +
            System.getProperty("java.version") + " " +
            System.getProperty("java.vendor") + " " +
            System.getProperty("java.home")
            );
        Globals.stdOutPrintln(ar.getString(ar.I_JAVA_CLASSPATH) +
            System.getProperty("java.class.path")
            );
    }

    private static void printHelp() {
        Globals.stdOutPrintln(acr.getString(acr.I_USAGE_HELP));
    }

    private static void printBanner() {
        Version version = new Version(false);
        Globals.stdOutPrintln(version.getBanner(false));
    }

    public static void main(String[] args)  {
        JFrame frame;

	processCmdlineArgs(args);

	AGraphics.loadImages();
	ConsoleHelp.loadHelp();

        frame = new AdminConsole();
 
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });  
 
        frame.pack();
        frame.setVisible(true);

	if (!ConsoleHelp.helpLoaded())  {
	    String s1 = acr.getString(acr.E_ONLINE_HELP_INIT_FAILED);
	    String s2 = acr.getString(acr.I_ONLINE_HELP_INIT) + ": " +
				acr.getString(acr.I_ERROR_CODE,
				AdminConsoleResources.E_ONLINE_HELP_INIT_FAILED);

	    Exception e = ConsoleHelp.getHelpLoadException();
	    if (e != null)  {
		s1 = s1 + "\n" + e.toString();
	    }

	    JOptionPane.showOptionDialog(frame,
		s1,
                s2,
		JOptionPane.YES_NO_OPTION,
		JOptionPane.ERROR_MESSAGE, null, close, close[0]);

	}
    }    

}
