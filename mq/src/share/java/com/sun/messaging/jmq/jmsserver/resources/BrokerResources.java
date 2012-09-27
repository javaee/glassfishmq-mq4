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
 * @(#)BrokerResources.java	1.344 09/07/07
 */ 

package com.sun.messaging.jmq.jmsserver.resources;

import java.util.ResourceBundle;
import java.util.Locale;
import com.sun.messaging.jmq.util.MQResourceBundle;

/**
 * This class wraps a PropertyResourceBundle, and provides constants
 * to use as message keys. The reason we use constants for the message
 * keys is to provide some compile time checking when the key is used
 * in the source.
 */

public class BrokerResources extends MQResourceBundle {

    public static BrokerResources resources = null;

    public static BrokerResources getResources() {
        return getResources(null);
    }

    public static synchronized BrokerResources getResources(Locale locale) {

        if (locale == null) {
            locale = Locale.getDefault();
        }

	if (resources == null || !locale.equals(resources.getLocale())) { 
	    ResourceBundle prb =
                ResourceBundle.getBundle(
		"com.sun.messaging.jmq.jmsserver.resources.BrokerResources",
		locale);
            resources = new BrokerResources(prb);
	}
	return resources;
    }

    private BrokerResources(ResourceBundle rb) {
        super(rb);
    }


    /***************** Start of message key constants *******************
     * We use numeric values as the keys because the Broker has a requirement
     * that each error message have an associated error code (for 
     * documentation purposes). We use numeric Strings instead of primitive
     * integers because that is what ListResourceBundles support. We could
     * write our own ResourceBundle to support integer keys, but since
     * we'd just be converting them back to strings (to display them)
     * it's unclear if that would be a big win. Also the performance of
     * ListResourceBundles under Java 2 is pretty good.
     * 
     *
     * Note To Translators: Do not copy these message key String constants
     * into the locale specific resource bundles. They are only required
     * in this default resource bundle.
     */

    // 0-999     Miscellaneous messages
    final public static String M_BROKER_USAGE	 	= "B0002";
    final public static String M_BAD_LOGLEVEL	 	= "B0003";
    final public static String M_ENTER_KEY_PWD          = "B0004";
    final public static String M_LICENSE_MESSAGE_PREFIX = "B0005";
    final public static String M_LICENSE_MESSAGE_SUBFIX = "B0006";
    final public static String M_TRIAL_LICENSE_MESSAGE	= "B0007";
    final public static String M_DEV_LICENSE_MESSAGE	= "B0008";
    final public static String M_BAD_RESET_TYPE		= "B0009";
    final public static String M_BROKER_EXITING         = "B0010";
    final public static String M_DBMGR_USAGE		= "B0011";
    final public static String M_UNLIMITED		= "B0012";
    final public static String M_LOW_MEMORY_WRITE	= "B0013";
    final public static String M_LOW_MEMORY_READ	= "B0014";
    final public static String M_LOW_MEMORY_PROCESS	= "B0015";
    final public static String M_LOW_MEMORY_LOAD	= "B0016";
    final public static String M_LOW_MEMORY_STARTUP	= "B0017";
    final public static String M_LOW_MEMORY_READALLOC	= "B0018";
    final public static String M_LOW_MEMORY_CLUSTER	= "B0019";
    final public static String M_LOW_MEMORY_STORE	= "B0020";
    final public static String M_MEMORY_GREEN   	= "B0021";
    final public static String M_MEMORY_YELLOW  	= "B0022";
    final public static String M_MEMORY_ORANGE  	= "B0023";
    final public static String M_MEMORY_RED     	= "B0024";
    final public static String M_REMOVE_INSTANCE_CONFIRMATION = "B0025";
    final public static String M_RESPONSE_YES_SHORT	= "B0026";
    final public static String M_RESPONSE_NO_SHORT	= "B0027";
    final public static String M_BAD_REMOVE_ARG		= "B0028";
    final public static String M_RESPONSE_YES		= "B0029";
    final public static String M_RESPONSE_NO		= "B0030";
    final public static String M_ENTER_KEY_LDAP         = "B0031";
    final public static String M_RR_QUEUE               = "B0032";
    final public static String M_FAIL_QUEUE             = "B0033";
    final public static String M_SINGLE_QUEUE           = "B0034";
    final public static String M_SHARED_THREAD_POOL     = "B0035";
    final public static String M_SSL_JMS		= "B0036";
    final public static String M_HTTP_JMS		= "B0037";
    final public static String M_BROKER_CLUSTERS		= "B0038";
    final public static String M_ENTER_DB_PWD		= "B0039";
    final public static String M_UPGRADE_NOBACKUP_CONFIRMATION = "B0040";
    final public static String M_FAILOVER_CONSUMERS = "B0041";
    final public static String M_ACTIVE_CONSUMERS = "B0042";
    final public static String M_QUEUE = "B0043";
    final public static String M_TOPIC = "B0044";
    final public static String M_DESTINATION = "B0045";
    final public static String M_C_API = "B0046";
    final public static String M_CLIENT_FAILOVER = "B0047";
    final public static String M_MONITORING = "B0048";
    final public static String M_LOCAL_DEST = "B0049";
    final public static String M_LIC_PRIMARY_CONSUMERS = "B0050";
    final public static String M_LIC_FAILOVER_CONSUMERS = "B0051";
    final public static String M_DMQ_MSG_COMMENT	= "B0052";
    final public static String M_DMQ_MSG_EXPIRATION	= "B0053";
    final public static String M_DMQ_MSG_LIMIT 		= "B0054";
    final public static String M_DMQ_MSG_UNDELIVERABLE 	= "B0055";
    final public static String M_DMQ_MSG_ERROR	 	= "B0056";
    final public static String M_AUDIT_FEATURE	 	= "B0057";
    final public static String M_NO_ACK_FEATURE	 	= "B0058";
    final public static String M_CLIENT_SHUTDOWN="B0059";
    final public static String M_SERVICE_SHUTDOWN="B0060";
    final public static String M_CONNECTION_CLOSE="B0061";
    final public static String M_ADMIN_REQ_CLOSE="B0062";
    final public static String M_INIT_FAIL_CLOSE="B0063";
    final public static String M_AUTH_FAIL_CLOSE="B0064";
    final public static String M_DMQ_ARRIVED_EXPIRED="B0065";
    final public static String M_ADMIN_REQUEST="B0066";
    final public static String M_CLIENT_REQUEST="B0067";
    final public static String M_CONNECTION_CLOSED="B0068";
    final public static String M_AUTO_REAPED="B0069";
    final public static String M_RECONNECT_TIMEOUT="B0070";
    final public static String M_ADMIN_REMOTE="B0071";
    final public static String M_SHARED_CONS = "B0072";
    final public static String M_SSL_BROKER_CLUSTERS = "B0073";
    final public static String M_THREAD_EXITING = "B0074";
    final public static String M_CLUSTER_SERVICE_FEATURE = "B0075";
    final public static String M_ADMIN_REQ_SHUTDOWN = "B0076";
    final public static String M_ADMIN_REQ_RESTART = "B0077";
    final public static String M_HA_SERVICE_FEATURE = "B0078";
    final public static String M_LOW_MEMORY_PORTMAPPER_ACCEPT = "B0079";
    final public static String M_LOW_MEMORY_PORTMAPPER_CONNECTION = "B0080";
    final public static String M_LOW_MEMORY_PORTMAPPER_RESTART = "B0081";
    final public static String M_PORTMAPPER_EXITING = "B0082";
    final public static String M_CLUSTER_DISPATCHER_LOW_MEM = "B0083";
    final public static String M_LINK_SHUTDOWN = "B0084";
    final public static String M_MASTER_BROKER_CHANGED = "B0085";
    final public static String M_MASTER_BROKER_NOT_CONNECTED = "B0086";
    final public static String M_CHANGE_MASTER_BROKER = "B0087";
    final public static String M_MSG_EXPIRED_ON_DELIVERY = "B0088";
    final public static String M_MSG_EXPIRED_BY_CLIENT = "B0089";

    // 1000-1999 Informational Messages
    final public static String I_NEW_CONNECTION 	= "B1000";
    final public static String I_JMQ_HOME	 	= "B1001";
    final public static String I_NEW_PROP	 	= "B1002";
    final public static String I_JAVA_VERSION	 	= "B1003";
    final public static String I_SERVICE_START	 	= "B1004";
    final public static String I_SERVICE_PAUSE	 	= "B1005";
    final public static String I_SERVICE_RESUME	 	= "B1006";
    final public static String I_SERVICE_STOP	 	= "B1007";
    final public static String I_REMOVE_CONNECTION	= "B1008";
    final public static String I_AUTOCREATE_DEST	= "B1009";
    final public static String I_REMOVE_EXPIRED_MSG= "B1010";
    final public static String I_SWAPING_MESSAGE= "B1011";
    final public static String I_STATE_FILE_CORRUPTED	= "B1012";
    final public static String I_QUEUE_AUTOCREATE_ENABLED	= "B1013";
    final public static String I_TOPIC_AUTOCREATE_DISABLED	= "B1014";
    final public static String I_USERMGR_HELP_USAGE	= "B1015";
    final public static String I_USERMGR_HELP_OPTIONS	= "B1016";
    final public static String I_USERMGR_USER_ADDED	= "B1017";
    final public static String I_USERMGR_USER_NOT_ADDED	= "B1018";
    final public static String I_USERMGR_USER_DELETED	= "B1019";
    final public static String I_USERMGR_USER_NOT_DELETED	= "B1020";
    final public static String I_USERMGR_USER_UPDATED	= "B1021";
    final public static String I_USERMGR_USER_NOT_UPDATED	= "B1022";
    final public static String I_USERMGR_Q_DELETE_OK	= "B1023";
    final public static String I_USERMGR_Q_UPDATE_OK	= "B1024";
    final public static String I_USERMGR_ADD_FAILED	= "B1025";
    final public static String I_USERMGR_DELETE_FAILED	= "B1026";
    final public static String I_USERMGR_UPDATE_FAILED	= "B1027";
    final public static String I_USERMGR_LIST_FAILED	= "B1028";
    final public static String I_20_ALPHA_LICENSE	= "B1029";
    final public static String I_20_BETA_LICENSE	= "B1030";
    final public static String I_20_FCS_LICENSE		= "B1031";
    final public static String I_UNKNOWN_LICENSE_VERSION= "B1032";
    final public static String I_CLIENT_ID_IN_USE       = "B1033";
    final public static String I_PURGING_DESTINATION    = "B1034";
    final public static String I_ALL_PURGE_CRITERIA     = "B1035";
    final public static String I_USERMGR_USERNAME_TITLE = "B1036";
    final public static String I_USERMGR_ROLENAME_TITLE = "B1037";
    final public static String I_USERMGR_ACTIVESTATE_TITLE = "B1038";
    final public static String I_BROKER_READY           = "B1039";
    final public static String I_TXNACK_FILE_CORRUPTED	= "B1040";
    final public static String I_CLUSTER_INITIALIZED	= "B1041";
    final public static String I_STANDALONE_INITIALIZED	= "B1042";
    final public static String I_DATA_FILE_CORRUPTED	= "B1043";
    final public static String I_DELETE_FILE_FAILED	= "B1044";
    final public static String I_TRUNCATE_FILE_FAILED	= "B1045";
    final public static String I_DATABASE_TABLE_CREATED	= "B1046";
    final public static String I_SHUTDOWN_BROKER	= "B1047";
    final public static String I_SHUTDOWN_COMPLETE	= "B1048";
    final public static String I_SHUTDOWN_HOOK	        = "B1049";
    final public static String I_NO_SHUTDOWN_HOOK       = "B1050";
    final public static String I_DATABASE_TABLE_DELETED	= "B1051";
    final public static String I_DATABASE_SHUTDOWN	= "B1052";
    final public static String I_SERVICE_SHUTTINGDOWN   = "B1053";
    final public static String I_JAVA_CLASSPATH	 	= "B1054";
    final public static String I_BROKER_ARGS	 	= "B1055";
    final public static String I_CLEANUP_PERSISTENT_STORE = "B1056";
    final public static String I_CLEANUP_MESSAGES	= "B1057";
    final public static String I_LOADING_MESSAGES	= "B1058";
    final public static String I_RESET_PERSISTENT_STORE = "B1059";
    final public static String I_LOAD_PERSISTENT_STORE	= "B1060";
    final public static String I_USERNAME		= "B1061";
    final public static String I_PASSWORD		= "B1062";
    final public static String I_DONE		        = "B1063";
    final public static String I_BROKER_RESTART	        = "B1064";
    final public static String I_ACCEPT_CONNECTION	= "B1065";
    final public static String I_DROP_CONNECTION	= "B1066";
    final public static String I_JDBC_STORE_INFO	= "B1067";
    final public static String I_MBUS_MASTER_INIT	= "B1068";
    final public static String I_MBUS_I_AM_MASTER	= "B1069";
    final public static String I_MBUS_SYNC_INIT	= "B1070";
    final public static String I_MBUS_ADD_BROKER	= "B1071";
    final public static String I_MBUS_DEL_BROKER	= "B1072";
    final public static String I_MBUS_PAUSING	= "B1073";
    final public static String I_MBUS_RELOAD_CLS	= "B1074";
    final public static String I_MBUS_SYNC_COMPLETE	= "B1075";
    final public static String I_WAIT_FOR_OP_DESTROY = "B1076";
    final public static String I_BROADCAST_GOODBYE   = "B1077";
    final public static String I_FLUSH_GOODBYE       = "B1078";
    final public static String I_NTRANS              = "B1079";
    final public static String I_NPREPARED_TRANS     = "B1080";
    final public static String I_PREPARED_ROLLBACK   = "B1081";
    final public static String I_PREPARED_NOROLLBACK = "B1082";
    final public static String I_RESET_MESSAGE		= "B1083";
    final public static String I_RESET_INTEREST		= "B1084";
    final public static String I_LOAD_REMAINING_STORE_DATA = "B1085";
    final public static String I_JAVA_HEAP              = "B1086";
    final public static String I_CONSUMER_REMOVE_SLOW   = "B1087"; // no longer used
    final public static String I_CHANGE_OF_MEMORY_STATE   = "B1088";
    final public static String I_LOW_MEMORY_FREE   = "B1089";
    final public static String I_UPDATE_SERVICE_REQ   = "B1090";
    final public static String I_SERVICE_PROTOCOL_UPDATED   = "B1091";
    final public static String I_NOFILES                = "B1092";
    final public static String I_ADMIN_SHUTDOWN_REQUEST = "B1093";
    final public static String I_ADMIN_RESTART_REQUEST  = "B1094";
    final public static String I_REMOVE_PERSISTENT_STORE = "B1095";
    final public static String I_REMOVE_INSTANCE	= "B1096";
    final public static String I_INSTANCE_NOT_REMOVED	= "B1097";
    final public static String I_LOADING_MESSAGES_FOR_DST = "B1098";
    final public static String I_REMOVE_OLD_FILESTORE	= "B1099";
    final public static String I_RESET_OLD_PERSISTENT_STORE	= "B1100";
    final public static String I_RESET_MESSAGES_IN_OLD_STORE	= "B1101";
    final public static String I_UPGRADE_REMAINING_STORE_DATA	= "B1102";
    final public static String I_RESET_INTERESTS_IN_OLD_STORE	= "B1103";
    final public static String I_REMOVE_OLD_PERSISTENT_STORE	= "B1104";
    final public static String I_UPGRADE_STORE_IN_PROGRESS	= "B1105";
    final public static String I_UPGRADE_STORE_DONE	= "B1106";
    final public static String I_REMOVE_OLDSTORE_REMINDER = "B1107";
    final public static String I_STORE_NOT_UPGRADED	= "B1108";
    final public static String I_UPGRADE_STORE_MSG	= "B1109";
    final public static String I_REMOVE_NEW_STORE	= "B1110";
    final public static String I_REMOVE_OLDTABLES_REMINDER = "B1111";
    final public static String I_REMOVE_OLD_DATABASE_TABLES = "B1112";
    final public static String I_RESET_OLD_DATABASE_TABLES = "B1113";
    final public static String I_WILL_CREATE_NEW_STORE	= "B1114";
    final public static String I_UPGRADE_JDBCSTORE_IN_PROGRESS	= "B1115";
    final public static String I_REMOVE_NEW_JDBC_STORE	= "B1116";
    final public static String I_DROP_TABLE_FAILED	= "B1117";
    final public static String I_UPGRADING_MESSAGES	= "B1118";
    final public static String I_ACL_PW_FILED_COPIED	= "B1119";
    final public static String I_USERMGR_INSTANCE_TITLE = "B1120";
    final public static String I_REMOVE_OLD_JDBCSTORE	= "B1121";
    final public static String I_RECONNECTING = "B1122";
    final public static String I_COMPACTING = "B1123";
    final public static String I_PAUSING_SVC = "B1124";
    final public static String I_PAUSING_DST = "B1125";
    final public static String I_PAUSED_DST_NOT_EXIST = "B1126";
    final public static String I_PAUSED_ADMIN = "B1127";
    final public static String I_RESUMING_SVC = "B1128";
    final public static String I_RESUMING_DST = "B1129";
    final public static String I_AUTO_DESTROY = "B1130";
    final public static String I_DST_DURABLE_RM = "B1131";
    final public static String I_AUTO_CREATE = "B1132";
    final public static String I_FAILOVER_ACTIVE = "B1133";
    final public static String I_LOCAL_CID_OWNER = "B1134";
    final public static String I_RMT_CID_OWNER = "B1135";
    final public static String I_PROCESSING_TRANS = "B1136";
    final public static String I_USING_NOCLUSTER = "B1137";
    final public static String I_LICENSE_DESCRIPTION = "B1138";
    final public static String I_LICENSE_FILE = "B1139";
    final public static String I_REMOVE_DSTEXP_MSGS= "B1140";
    final public static String I_FILE_STORE_INFO	= "B1141";
    final public static String I_JDBC_STORE_INFO_2	= "B1142";
    final public static String I_USING_DEFAULT_LICENSE	= "B1143";
    final public static String I_DMQ_CREATING_DMQ	= "B1144";
    final public static String I_DMQ_REMOVING_DMQ_MSG	= "B1145";
    final public static String I_DMQ_REMOVING_MSG	= "B1146";
    final public static String I_DMQ_MOVING_TO_DMQ	= "B1147";
    final public static String I_DEFAULT_LICENSE	= "B1148";
    final public static String I_RECONNECT_TO_DB	= "B1149";
    final public static String I_CLUSTER_WAIT_LINKINIT	= "B1150";
    final public static String I_LOADING_DESTINATION	= "B1151";
    final public static String I_LOADING_DEST_COMPLETE	= "B1152";
    final public static String I_LOADING_DEST_IN_PROCESS= "B1153";
    final public static String I_DESTROY_CXN            = "B1154";
    final public static String I_ADMIN_BKR_NOT_READY    = "B1155";
    final public static String I_ADMIN_BKR_SHUTTINGDOWN = "B1156";
    final public static String I_DST_SHUTDOWN_DESTROY   = "B1157";
    final public static String I_DST_ADMIN_CREATE       = "B1158";
    final public static String I_DST_ADMIN_DESTROY      = "B1159";
    final public static String I_DST_TEMP_CREATE        = "B1160";
    final public static String I_INIT_DONE              = "B1161";
    final public static String I_CREATE_TABLE           = "B1162";
    final public static String I_CREATE_TABLE_INDEX     = "B1163";    
    final public static String I_DROP_TABLE             = "B1164";
    final public static String I_DATABASE_TABLE_HA_CREATED = "B1165";
    final public static String I_DATABASE_TABLE_HA_DELETED = "B1166";
    final public static String I_BROKER_REMOVE          = "B1167";
    final public static String I_TAKEOVER_LOCK_ACQUIRED = "B1168";
    final public static String I_REMOVING_TAKEOVER_LOCK = "B1169";
    final public static String I_JDBCSTORE_AUTOCREATE_ENABLED   = "B1170";
    final public static String I_JDBCSTORE_AUTOCREATE_DISABLED  = "B1171";
    final public static String I_CLUSTER_UNICAST                = "B1172";
    final public static String I_CLUSTER_RECEIVE                = "B1173";
    final public static String I_CLUSTER_WAITING_REPLY          = "B1174";
    final public static String I_CLUSTER_PRETAKEOVER            = "B1175";
    final public static String I_CLUSTER_PRETAKEOVER_ABORT      = "B1176";
    final public static String I_CLUSTER_BROADCAST_TAKEOVER_COMPLETE = "B1177";
    final public static String I_CLUSTER_RECEIVE_NOTIFICATION   = "B1178";
    final public static String I_CLUSTER_ACTIVATED_BROKER       = "B1179";
    final public static String I_CLUSTER_DEACTIVATED_BROKER     = "B1180";
    final public static String I_CLUSTER_HB_ADD_ENDPOINT        = "B1181";
    final public static String I_CLUSTER_HB_REM_ENDPOINT        = "B1182";
    final public static String I_CLUSTER_HB_UNSUSPECT           = "B1183";
    final public static String I_CLUSTER_HB_UNSUSPECTED         = "B1184";
    final public static String I_CLUSTER_HB_START_HEARTBEAT     = "B1185";
    final public static String I_CLUSTER_HB_STOP_HEARTBEAT      = "B1186";
    final public static String I_CLUSTER_HB_BIND                = "B1187";
    final public static String I_CLUSTER_SERVICE_SHUTDOWN_WAITING = "B1188";
    final public static String I_FEATURE_UNAVAILABLE            = "B1189";
    final public static String I_ADMIN_TAKEOVER                 = "B1190";
    final public static String I_NOT_TAKEOVER_BKR               = "B1191";
    final public static String I_HA_IGNORE_PROP                 = "B1192";
    final public static String I_HA_INFO_STRING                 = "B1193";
    final public static String I_HA_DEST_TAKEOVER               = "B1194";
    final public static String I_CANT_LOAD_MONITOR             = "B1195";
    final public static String I_LOADING_DST                    = "B1196";
    final public static String I_QUIESCE_START                  = "B1197";
    final public static String I_QUIESCE_DONE                  = "B1198";
    final public static String I_SHUTDOWN_REQ                   = "B1199";
    final public static String I_SHUTDOWN_IN_SEC                = "B1200";
    final public static String I_SHUTDOWN_AT                    = "B1201";
    final public static String I_RUNNING_IN_HA                  = "B1202";
    final public static String I_STARTING_WITH_BID              = "B1203";
    final public static String I_STARTING_MONITOR               = "B1204";
    final public static String I_STARTING_HEARTBEAT             = "B1205";
    final public static String I_MONITOR_INFO                   = "B1206";
    final public static String I_UPD_STORED_PORT                = "B1207";
    final public static String I_STARTUP_PAUSE                  = "B1208";
    final public static String I_TAKEOVER_RESET                 = "B1209";
    final public static String I_INDOUBT_COUNT                  = "B1210";
    final public static String I_NO_TAKEOVER_SHUTDOWN           = "B1211";
    final public static String I_OTHER_TAKEOVER                 = "B1212";
    final public static String I_BROKER_OK                      = "B1213";
    final public static String I_BROKER_NOT_OK                  = "B1214";
    final public static String I_BROKER_INDOUBT_CONTINUE_MONITOR = "B1215";
    final public static String I_START_TAKEOVER                 = "B1216";
    final public static String I_TAKEOVER_OK                    = "B1217";
    final public static String I_TAKEOVER_TXNS                  = "B1218";
    final public static String I_TAKEOVER_TXN_P_ROLLBACK        = "B1219";
    final public static String I_TAKEOVER_TXN_A_ROLLBACK        = "B1220";
    final public static String I_TAKEOVER_MSGS                  = "B1221";
    final public static String I_TAKEOVER_DSTS                  = "B1222";
    final public static String I_TAKEOVER_COMPLETE              = "B1223";
    final public static String I_REAP_DST                       = "B1224";
    final public static String I_REAP_DST_DONE                  = "B1225";
    final public static String I_CLUSTER_MASTER_BROKER_IP_CHANGED = "B1226";
    final public static String I_SERVICE_USER_REPOSITORY        = "B1227";
    final public static String I_CLUSTER_PING_INTERVAL          = "B1228";
    final public static String I_CLUSTER_USING_CLUSTERID        = "B1229";
    final public static String I_HA_NOT_ENABLE                  = "B1230";
    final public static String I_HASTORE_ALREADY_UPGRADED       = "B1231";
    final public static String I_UPGRADE_HASTORE_IN_PROGRESS    = "B1232";
    final public static String I_REMOVE_UPGRADE_HASTORE_DATA    = "B1233";

    final public static String I_JMX_CONNECTOR_STARTED    	= "B1234";
    final public static String I_JMX_CONNECTOR_STOPPED    	= "B1235";
    final public static String I_JMX_CONNECTION_OPEN    	= "B1236";
    final public static String I_JMX_CONNECTION_CLOSE    	= "B1237";
    final public static String I_JMX_CONNECTION_UNKNOWN    	= "B1238";
    final public static String I_JMX_USING_PLATFORM_MBEANSERVER	= "B1239";
    final public static String I_JMX_CREATE_MBEANSERVER	   	= "B1240";
    final public static String I_JMX_RMI_REGISTRY_STARTED    	= "B1241";
    final public static String I_CLUSTER_CLOSE_UNREACHABLE      = "B1242";
    final public static String I_AUTH_OK                        = "B1243";
    final public static String I_CLUSTER_REMOTE_IP_REACHABLE       = "B1244";
    final public static String I_CLUSTER_REMOTE_IP_UNREACHABLE     = "B1245";
    final public static String I_JMX_NO_SHUTDOWN		= "B1246";
    final public static String I_RESET_BROKER_METRICS		= "B1247";
    final public static String I_ACK_FAILED_BROKER_SHUTDOWN	= "B1248";
    final public static String I_PAUSING_DST_WITH_PAUSE_TYPE	= "B1249";
    final public static String I_PAUSING_ALL_DST_WITH_PAUSE_TYPE= "B1250";
    final public static String I_CLUSTER_WAITING_MASTER         = "B1251";
    final public static String I_PAUSING_ALL_SVCS      		= "B1252";
    final public static String I_RESUMING_ALL_SVCS     		= "B1253";
    final public static String I_REAP_INACTIVE_STORE_SESSION   	= "B1254";
    final public static String I_RECONNECT_OWNER_INDOUBT   	    = "B1255";
    final public static String I_RECONNECT_OWNER_NOTME          = "B1256";
    final public static String I_RECONNECT_NOCREATOR            = "B1257";
    final public static String I_RECONNECT_INTAKEOVER           = "B1258";
    final public static String I_RECONNECT_OWNER_NOTFOUND       = "B1259";
    final public static String I_ACK_FAILED_NO_CONSUMER         = "B1260";
    final public static String I_ACK_FAILED_MESSAGE_GONE        = "B1261";
    final public static String I_MBUS_LIMITEDJMS                = "B1262";
    final public static String I_MBUS_FULLJMS                   = "B1263";
    final public static String I_CLUSTER_WAIT_PROTOCOLINIT      = "B1264";
    final public static String I_CLUSTER_WAIT_RECORD_CONFIG_CHANGE_EVENT_REPLY  = "B1265";
    final public static String I_CONN_CLEANUP_KEEP_TXN  = "B1266";
    final public static String I_NREMOTE_TRANS          = "B1267";
    final public static String I_NREMOTE_TRANS_HA       = "B1268";

    final public static String I_TXNLOG_ENABLED         = "B1269";
    final public static String I_PROCESS_MSG_TXNLOG     = "B1270";
    final public static String I_PROCESS_ACK_TXNLOG     = "B1271";
    final public static String I_PROCESS_TXNLOG_RECORD  = "B1272";
    final public static String I_COMMIT_TXNLOG_RECORD   = "B1273";
    final public static String I_LOAD_MSG_TXNLOG        = "B1274";
    final public static String I_LOAD_ACK_TXNLOG        = "B1275";
    final public static String I_REPLACE_MSG_TXNLOG     = "B1276";
    final public static String I_RECONSTRUCT_MSG_TXNLOG = "B1277";
    final public static String I_UPDATE_INT_STATE_TXNLOG    = "B1278";
    final public static String I_DISREGARD_INT_STATE_TXNLOG = "B1279";
    final public static String I_RECONSTRUCT_STORE_DONE = "B1280";
    final public static String I_SCHEDULE_DETACHED_TXN_REAPER    = "B1281";
    final public static String I_CONN_CLEANUP_RETAIN_XA  = "B1282";
    final public static String I_PROCESSING_TAKEOVER_TRANS = "B1283";
    final public static String I_NCLUSTER_TRANS            = "B1284";
    final public static String I_COMMITTED_TRAN_REAPER_THREAD_START = "B1285";
    final public static String I_COMMITTED_TRAN_REAPER_THREAD_EXIT = "B1286";
    final public static String I_RESUMED_DST_NOT_EXIST	= "B1287";

    final public static String I_BROKER_INDOUBT_START_MONITOR = "B1288";
    final public static String I_ACK_FAILED_MESSAGE_LOCKED    = "B1289";
    final public static String I_ACK_FAILED_MESSAGE_REF_CLEARED = "B1290";

    final public static String I_TAKINGOVER_LOCAL_DSTS      = "B1291";
    final public static String I_TAKINGOVER_MSGS            = "B1292";
    final public static String I_TAKINGOVER_TXNS            = "B1293";
    final public static String I_TAKINGOVER_REMOTE_TXNS     = "B1294";
    final public static String I_TAKINGOVER_STORE_SESSIONS  = "B1295";

    final public static String I_NO_TAKEOVER_BUSY           = "B1296";
    final public static String I_TAKEOVER_REMOTE_TXNS       = "B1297";
    final public static String I_TAKEOVER_TXN               = "B1298";
    final public static String I_INPROCESS_BROKER           = "B1299";
    final public static String I_MONITOR_QUIESCING          = "B1300";
    final public static String I_UNQUIESCE_DONE             = "B1301";
    final public static String I_LOAD_DST_NOTFOUND_INSTORE  = "B1302";    
    final public static String I_RM_ALLMSG_DST_NOTFOUND_INSTORE = "B1303";    
    final public static String I_RM_DST_NOTFOUND_INSTORE    = "B1304";    
    final public static String I_AUTHENTICATE_USER_AS       = "B1305";    
    final public static String I_AUTHENTICATE_AS_USER         = "B1306";    
    final public static String I_SET_DEFAULT_SECURITY_MANAGER = "B1307";
    final public static String I_SET_JAVA_POLICY_PROVIDER     = "B1308";
    final public static String I_NO_NONRECOVERY_TXNACK_TO_ROLLBACK  = "B1309";
    final public static String I_NO_MORE_TXNACK_TO_ROLLBACK  = "B1310";
    final public static String I_REMOTE_TXN_PRESUMED_ROLLBACK  = "B1311";
    final public static String I_KEYMGRFACTORY_USE_DEFAULT_ALG  = "B1312";
    final public static String I_CLUSTER_SKIP_FORWARDING_CLOSED_CONSUMER  = "B1313";
    final public static String I_JESMF_CLASSES_NOT_PRESENT  	= "B1314";
    final public static String I_JESMF_MGR_CLASS_NOT_PRESENT  	= "B1315";
    final public static String I_JESMF_NOT_ENABLED  		= "B1316";
    final public static String I_JESMF_AGENT_NOT_INIT  		= "B1317";
    final public static String I_INIT_BRIDGE_SERVICE_MANAGER = "B1318";
    final public static String I_START_BRIDGE_SERVICE_MANAGER = "B1319";
    final public static String I_STARTED_BRIDGE_SERVICE_MANAGER = "B1320";
    final public static String I_STOP_BRIDGE_SERVICE_MANAGER = "B1321";
    final public static String I_STOPPED_BRIDGE_SERVICE_MANAGER   = "B1322";
    final public static String I_JMSBRIDGE_REMOVED_FROM_HA_STORE  = "B1323";
    final public static String I_CHECKPOINT_BROKER  = "B1324";
    final public static String I_SEND_CLUSTER_TXN_INFO  = "B1325";
    final public static String I_RECEIVED_TXN_INFO  = "B1326";
    final public static String I_REAPER_WAIT_TXN_LOAD  = "B1327";
    final public static String I_SEND_REMOTE_TXN_INFO  = "B1328";
    final public static String I_PROCESSING_REMOTE_TXN  = "B1329";
    final public static String I_TAKINGOVER_TXN_ACK_ALREADY_EXIST  = "B1330";
    final public static String I_CONSUMER_ALREADY_ADDED  = "B1331";
    final public static String I_THIS_BROKER_RESETED_TAKEOVER_BROKER  = "B1332";
    final public static String I_THIS_BROKER_CURRENT_STORE_SESSION  = "B1333";
    final public static String I_BROKER_STATE_RESTORED_TAKEOVER_FAIL  = "B1334";
    final public static String I_RESET_TAKEOVER_EXIT  = "B1335";
    final public static String I_INDOUBT_STATUS_ON_BROKER_SESSION  = "B1336";
    final public static String I_DOWN_STATUS_ON_BROKER_SESSION  = "B1337";
    final public static String I_TAKEOVER_DATA_PROCESSED  = "B1338";
    final public static String I_RETRY_DB_OP = "B1339";
    final public static String I_DB_CONN_POLL_TIMEOUT = "B1340";
    final public static String I_STORE_CLOSING = "B1341";
    final public static String I_DB_REAP_EXCESSIVE_CONNS = "B1342";
    final public static String I_DB_REAP_IDLE_CONNS = "B1343";
    final public static String I_DB_DESTROY_INACTIVE_CONN = "B1344";
    final public static String I_DB_DESTROY_ACTIVE_CONN = "B1345";
    final public static String I_CLUSTER_MB_BACKUP_SUCCESS = "B1346";
    final public static String I_CLUSTER_MB_RESTORE_SUCCESS = "B1347";
    final public static String I_NO_SYNC_WITH_MASTERBROKER = "B1348";
    final public static String I_WAIT_FOR_SYNC_WITH_MASTERBROKER = "B1349";
    final public static String I_WAIT_FOR_SYNC_WITH_MASTERBROKER_INTERRUPTED = "B1350";
    final public static String I_SHARECC_JDBCSTORE_INFO = "B1351";
    final public static String I_AUTOCREATE_ON = "B1352";
    final public static String I_AUTOCREATE_OFF = "B1353";
    final public static String I_SHARECC_JDBCSTORE_CREATE_NEW = "B1354";
    final public static String I_DATABASE_TABLE = "B1355";
    final public static String I_WAIT_ON_CLOSED_SHARECC_JDBCSTORE = "B1356";
    final public static String I_WAIT_ACCESS_SHARECC_JDBCSTORE = "B1357";
    final public static String I_MASTER_BROKER_WAITER_THREAD_EXITS = "B1358";
    final public static String I_USE_SHARECC_STORE = "B1359";
    final public static String I_USE_SHARECC_STORE_IGNORE_MB = "B1360";
    final public static String I_CLUSTER_PROCESS_CHANGE_RECORDS = "B1361";
    final public static String I_SHARECC_DATABASE_TABLE_CREATED = "B1362";
    final public static String I_SHARECC_DATABASE_TABLE_DELETED = "B1363";
    final public static String I_SHARECC_USE_RESETED_TABLE = "B1364";
    final public static String I_EXEC_CREATE_TABLE_SUPPLEMENT = "B1365";
    final public static String I_EXEC_DROP_TABLE_SUPPLEMENT = "B1366";
    final public static String I_SHARECC_SYNC_ON_JOIN = "B1367";
    final public static String I_UPDATE_BROKERLIST = "B1368";
    final public static String I_CLUSTER_RECEIVED_FIRST_INFO = "B1369";
    final public static String I_SHARECC_BACKUP_RECORDS = "B1370";
    final public static String I_SHARECC_BACKUP_RECORDS_SUCCESS = "B1371";
    final public static String I_CLUSTER_MB_RESTORE_PROCESS_RECORDS = "B1372";
    final public static String I_SHARECC_BACKUP = "B1373";
    final public static String I_SHARECC_RESTORE = "B1374";
    final public static String I_SHARECC_RESTORE_RECORDS = "B1375";
    final public static String I_SHARECC_RESTORE_RECORDS_SUCCESS = "B1376";
    final public static String I_CLUSTER_WAIT_CONFIG_CHANGE_OP_COMPLETE = "B1377";
    final public static String I_CLUSTER_SHUTDOWN = "B1378";
    final public static String I_CLUSTER_CHANGE_MASTER_BROKER_SAME = "B1379";
    final public static String I_CLUSTER_ANNOUNCE_NEW_MASTER_BROKER = "B1380";
    final public static String I_CLUSTER_CHANGE_MASTER_BROKER = "B1381";
    final public static String I_ADMIN_RECEIVED_CMD = "B1382";
    final public static String I_CLUSTER_RECEIVED_CHANGE_RECORDS_FROM = "B1383";
    final public static String I_BROKER_PROPERTIES = "B1384";
    final public static String I_RM_EXPIRED_MSG_BEFORE_DELIVER_TO_CONSUMER = "B1385";
    final public static String I_RM_EXPIRED_REMOTE_MSG_BEFORE_DELIVER_TO_CONSUMER = "B1386";
    final public static String I_NUM_MSGS_PURGED_FROM_DEST = "B1387";
    final public static String I_NUM_MSGS_INDELIVERY_NOT_PURGED_FROM_DEST = "B1388";
    final public static String I_NUM_MSGS_INDELIVERY_NOT_EXPIRED_FROM_DEST = "B1389";
    final public static String I_TXN_LOADING_COMPLETE = "B1390";
    final public static String I_RM_MSG_ON_REPLAY_MSG_REMOVAL = "B1391";
    final public static String I_OPEN_TXNLOG = "B1392";
    final public static String I_DB_POOL_POLL_WAIT = "B1393";
    final public static String I_CHECKPOINT_START = "B1422";
    final public static String I_CHECKPOINT_END = "B1423";
    
    final public static String I_CANCEL_SQL_REPLAY = "B1430";
    final public static String I_DB_CONN_EX_TOBE_DESTROYED = "B1431";    
    final public static String I_SET_TXN_TIMEOUT = "B1432";
    final public static String I_USE_JDBC_DRIVER = "B1433";


    // 2000-2999 Warning Messages
    final public static String W_AUTH_FAILED	 	= "B2000";
    final public static String W_BAD_PROPERTY_FILE 	= "B2001";
    final public static String W_CONFIG_STORE_WRITE 	= "B2002";
    final public static String W_CONNECTION_UNAVAILABLE = "B2003";
    final public static String W_UNEXPECTED_ACK 	= "B2004";
    final public static String W_CANT_UPDATE_ACK 	= "B2005";
    final public static String W_DESTROY_TEMP_DEST	= "B2006";
    final public static String W_CREATE_DEST_FAILED 	= "B2007";
    final public static String W_DESTROY_DEST_FAILED 	= "B2008";
    final public static String W_ADD_CONSUMER_FAILED 	= "B2009";
    final public static String W_DESTROY_CONSUMER_FAILED = "B2010";
    final public static String W_MESSAGE_STORE_FAILED    = "B2011";
    final public static String W_NO_DEST_FOR_MESSAGE    = "B2012";
    final public static String W_NO_INTERESTS_FOR_MESSAGE    = "B2013";
    final public static String W_BAD_DEBUG_CLASS        = "B2014";
    final public static String W_QUEUE_BROWSE_FAILED        = "B2015";
    final public static String W_SELECTOR_PARSE        = "B2016";
    final public static String W_CANT_STOP_SERVICE        = "B2017";
    final public static String W_CANT_DESTROY_SERVICE        = "B2018";
    final public static String W_BAD_FILE_NAME	= "B2019";
    final public static String W_CANNOT_READ_DATA_FILE	= "B2020";
    final public static String W_INVALID_Q_DEFAULT_TYPE	= "B2021";
    final public static String W_DELETING_LAST_ADMIN_USER	= "B2022";
    final public static String W_ADDING_USER_NAMED_GUEST= "B2023";
    final public static String W_CLIENT_ID_INVALID= "B2024";
    final public static String W_ADMIN_OPERATION_FAILED= "B2025";
    final public static String W_CLUSTER_INIT_FAILED	= "B2026";
    final public static String W_MBUS_BAD_VERSION	= "B2027";
    final public static String W_MBUS_UNKNOWN_DESTID1	= "B2028";
    final public static String W_MBUS_UNKNOWN_DESTID2	= "B2029";
    final public static String W_MBUS_ADD_REMINT_FAILED	= "B2030";
    final public static String W_MBUS_REM_REMINT_FAILED	= "B2031";
    final public static String W_MBUS_REM_CLINT_FAILED	= "B2032";
    final public static String W_MBUS_BAD_PRIMARY_INT	= "B2033";
    final public static String W_MBUS_LOCK_ABORTED  	= "B2034";
    final public static String W_MBUS_BAD_BROKERADDR  	= "B2035";
    final public static String W_MBUS_RCVPKT_ERROR  	= "B2036";
    final public static String W_BAD_INTEREST_FILE_NAME	= "B2037";
    final public static String W_CANNOT_READ_INTEREST_FILE = "B2038";
    final public static String W_PARSE_INTEREST_FAILED	= "B2039";
    final public static String W_SERVICE_ACCESS_DENIED       = "B2040";
    final public static String W_DESTINATION_ACCESS_DENIED   = "B2041";
    final public static String W_DESTINATION_CREATE_DENIED   = "B2042";
    final public static String W_DURABLE_DELETE_DENIED       = "B2043";
    final public static String W_DURABLE_REATTACH_DENIED     = "B2044";
    final public static String W_DURABLE_REPLACE_DENIED      = "B2045";
    final public static String W_CANNOT_DESTROY_OPERATION    = "B2046";
    final public static String W_ERROR_UPDATING_SERVICE      = "B2047";
    final public static String W_MBUS_BAD_CFGSERVER          = "B2048";
    final public static String W_BAD_LASTREFRESHTIME_TABLE   = "B2049";
    final public static String W_FORBIDDEN_ADMIN_OP	     = "B2050";
    final public static String W_CONNECTION_TIMEOUT          = "B2051";
    final public static String W_BAD_PROTO_VERSION	     = "B2052";
    final public static String W_MBUS_PSTORE_ERROR	     = "B2053";
    final public static String W_MBUS_CANCEL_BACKUP1	 = "B2054";
    final public static String W_MBUS_CANCEL_BACKUP2	 = "B2055";
    final public static String W_MBUS_BACKUP_ERROR  	 = "B2056";
    final public static String W_MBUS_CANCEL_RESTORE1	 = "B2057";
    final public static String W_MBUS_CANCEL_RESTORE2	 = "B2058";
    final public static String W_MBUS_CANCEL_RESTORE3	 = "B2059";
    final public static String W_MBUS_RESTORE_ERROR  	 = "B2060";
    final public static String W_MBUS_STILL_TRYING  	 = "B2061";
    final public static String W_MBUS_LOCK_TIMEOUT  	 = "B2062";
    final public static String W_METRIC_BAD_CONFIG  	 = "B2063";
    final public static String W_BAD_KEY_FILE  	 = "B2064";
    final public static String W_BAD_KEY_FILE_DEL  	 = "B2065";
    final public static String W_SHUTDOWN_BROKER  	 = "B2066";
    final public static String W_BAD_CONFIG_VERSION  	 = "B2067";
    final public static String W_MBUS_SERIALIZATION  	 = "B2068";
    final public static String W_OUT_OF_MEMORY	= "B2069";
    final public static String W_STREAM_CORRUPTED	= "B2070";
    final public static String W_BAD_DIAG_CLASS         = "B2071";
    final public static String W_BAD_PROPERTY         = "B2072";
    final public static String W_EXCEPTION_LOADING_CONFIGRECORDS = "B2073";
    final public static String W_BAD_CONFIGRECORD	= "B2074";
    final public static String W_EARLY_OUT_OF_MEMORY	= "B2075";
    final public static String W_LOW_MEM_REJECT_PRODUCER= "B2076";
    final public static String W_LOW_MEM_REJECT_DEST    = "B2077";
    final public static String W_NO_LDAP_PASSWD    = "B2078";
    final public static String W_BAD_DEFAULT_QUEUE	= "B2079";
    final public static String W_QUEUE_BROWSE_FAILED_NODEST        = "B2080";
    final public static String W_DST_ACTIVE_PRODUCERS = "B2081";
    final public static String W_DST_ACTIVE_CONSUMERS = "B2082";
    final public static String W_DST_NO_AUTOCREATE= "B2083";
    final public static String W_UNKNOWN_MONITOR= "B2084";
    final public static String W_LOAD_DST_FAIL= "B2085";
    final public static String W_MBUS_REJECT_OLD = "B2086";
    final public static String W_BAD_BROKER_LOCK = "B2087";
    final public static String W_PASSWD_OPTION_DEPRECATED = "B2088";
    final public static String W_CAN_NOT_LOAD_MSG = "B2089";
    final public static String W_DMQ_ADD_FAILURE = "B2090";
    final public static String W_DST_RECREATE_FAILED = "B2091";
    final public static String W_DST_REGENERATE = "B2092";
    final public static String W_DST_REGENERATE_ERROR = "B2093";
    final public static String W_CON_RECREATE_FAILED = "B2094";
    final public static String W_CON_CORRUPT_REMOVE = "B2095";
    final public static String W_TRANS_LOAD_ERROR = "B2096";
    final public static String W_TRANS_STATE_CORRUPT = "B2097";
    final public static String W_TRANS_ID_CORRUPT = "B2098";
    final public static String W_TRANS_ACK_CORRUPT = "B2099";
    final public static String W_CLUSTER_LOAD_LASTREFRESHTIME = "B2100";
    final public static String W_CLUSTER_SEND_LOCK_RESPONSE_EXCEPTION = "B2101";
    final public static String W_CLOCK_SKEW_EARLY = "B2102";
    final public static String W_CLOCK_SKEW_LATE = "B2103";
    final public static String W_CLOCK_SKEW_EXPIRING = "B2104";
    final public static String W_CLUSTER_LINKINIT_EXCEPTION = "B2105";
    final public static String W_ADD_AUTO_CONSUMER_FAILED  = "B2106";
    //	Bug ID 6252184 Escalation ID 1-8243878
    //
    //	Backported by Tom Ross tom.ross@sun.com
    //
    //	14 April 2005
    // new line below
    final public static String W_CONFIG_CHANGEEVENT_NOTFOUND  = "B2107";
    final public static String W_HA_NO_RESET = "B2108";
    final public static String W_UNABLE_TO_ACQUIRE_TAKEOVER_LOCK = "B2109";
    final public static String W_CLUSTER_UNICAST_FAILED = "B2110";
    final public static String W_CLUSTER_RECEIVE_STATUS = "B2111";
    final public static String W_CLUSTER_WAIT_REPLY_FAILED = "B2112";
    final public static String W_EXCEPTION_LOADING_HABROKERS = "B2113";
    final public static String W_CLUSTER_UNABLE_NOTIFY_REPLY = "B2114";
    final public static String W_CLUSTER_ACTIVATE_BROKER_FAILED   = "B2115";
    final public static String W_CLUSTER_DEACTIVATE_BROKER_FAILED = "B2116";
    final public static String W_CLUSTER_MSG_ACK_FAILED_FROM_HOME = "B2117";
    final public static String W_CLUSTER_REJECT_LESS_400VERSION   = "B2118";
    final public static String W_CLUSTER_REJECT_TAKINGOVER_TARGET = "B2119";
    final public static String W_CLUSTER_REJECT_EXISTING_SAME     = "B2120";
    final public static String W_CLUSTER_HB_REM_ENDPOINT_NOTFOUND = "B2121";
    final public static String W_CLUSTER_HB_TIMEOUT               = "B2122";
    final public static String W_CLUSTER_HB_IGNORE_UNKNOWN_SENDER = "B2123";
    final public static String W_CLUSTER_HB_STOP_SERVICE_EXCEPTION = "B2124";
    final public static String W_CLUSTER_SERVICE_SHUTDOWN_TIMEOUT  = "B2125";
    final public static String W_CLUSTER_FORCE_CLOSE_LINK          = "B2126";
    final public static String W_CLUSTER_CLOSE_DOWN_BROKER_LINK    = "B2127";
    final public static String W_CLUSTER_RELOAD_FAILED             = "B2128";
    final public static String W_CLUSTER_AUTOCONNECT_ADD_FAILED    = "B2129";
    final public static String W_LOGIN_FAILED                      = "B2130";
    final public static String W_RECONNECT_ERROR                   = "B2131";
    final public static String W_MB_UNSET                          = "B2132";
    final public static String W_BAD_MB                            = "B2133";
    final public static String W_TAKEOVER_IN_PROGRESS              = "B2134";
    final public static String W_SERVICE_USER_REPOSITORY           = "B2135";
    final public static String W_CLUSTER_REMOTE_MSG_ACK_FAILED     = "B2136";

    /*
     * JMX related warning messages
     */
    final public static String W_JMX_AGENT_STARTUP_FAILED     	   = "B2137";
    final public static String W_JMX_AGENT_STOP_EXCEPTION     	   = "B2138";
    final public static String W_JMX_REGISTER_MBEAN_EXCEPTION  	   = "B2139";
    final public static String W_JMX_UNREGISTER_MBEAN_EXCEPTION    = "B2140";
    final public static String W_JMX_DISABLED    		   = "B2141";
    final public static String W_JMX_CLASSES_NOT_FOUND 		   = "B2142";
    final public static String W_JMX_CONNECTOR_CREDENTIALS_NEEDED  = "B2143";
    final public static String W_JMX_CONNECTOR_CREDENTIALS_WRONG_TYPE
								   = "B2144";
    final public static String W_JMX_AGENT_CREATE_EXCEPTION    	   = "B2145";
    final public static String W_JMX_CONNECTOR_AUTH_FAILED    	   = "B2146";
    final public static String W_JMX_RMI_REGISTRY_STARTED_EXCEPTION= "B2147";
    final public static String W_JMX_RMI_REGISTRY_EXISTS	   = "B2148";
    final public static String W_JMX_GET_PLATFORM_MBEANSERVER_EXCEPTION= "B2149";
    final public static String W_JMX_LOADING_MBEANS_EXCEPTION	   = "B2150";
    final public static String W_JMX_UNLOADING_MBEANS_EXCEPTION	   = "B2151";
    final public static String W_JMX_SEND_NOTIFICATION_FROM_MBEAN_EXCEPTION   
								   = "B2152";
    final public static String W_JMX_SEND_NOTIFICATION_PROBLEM     = "B2153";
    final public static String W_JMX_SEND_NOTIFICATION_FROM_MBEAN_PROBLEM     
								   = "B2154";
    final public static String W_JMX_SERVICE_NAME_NULL     	   = "B2155";
    final public static String W_JMX_CANNOT_GET_SVC_NAME_FROM_CXN_ID
								   = "B2156";
    final public static String W_JMX_CONSUMER_ID_NULL     	   = "B2157";
    final public static String W_JMX_PRODUCER_ID_NULL     	   = "B2158";
    final public static String W_JMX_TXN_ID_NULL     	   	   = "B2159";
    final public static String W_JMX_CANNOT_START_INACTIVE_CONNECTOR
								   = "B2160";
    final public static String W_JMX_CREATE_URL_FOR_CONNECTOR_FAILED
								   = "B2161";
    final public static String W_JMX_ERROR_CREATING_CONNECTOR	   = "B2162";
    final public static String W_JMX_CONNECTOR_START_EXCEPTION 	   = "B2163";
    final public static String W_JMX_DELETE_CONNECTOR_NON_EXISTANT = "B2164";
    final public static String W_JMX_DELETE_LISTENER_EXCEPTION 	   = "B2165";
    final public static String W_JMX_REMOVE_CONNECTOR_EXCEPTION	   = "B2166";
    final public static String W_JMX_START_CONNECTOR_NON_EXISTANT  = "B2167";
    final public static String W_JMX_STOP_CONNECTOR_NON_EXISTANT   = "B2168";
    final public static String W_JMX_STOP_CONNECTOR_NOT_ACTIVE     = "B2169";
    final public static String W_JMX_CONNECTOR_STOP_EXCEPTION      = "B2170";

    final public static String W_CLUSTER_CANNOT_CHECK_REACHABILITY = "B2171";

    /*
     * More JMX related warnings
     */
    final public static String W_JMX_AUTHENTICATOR_INIT_FAILED     = "B2172";

    final public static String W_UNRESPONSIVE_CONNECTION = "B2173";

    final public static String W_JMX_FAILED_TO_OBTAIN_BKR_ADDRESS_FROM_ID = "B2174";
    final public static String W_JMX_FAILED_TO_OBTAIN_CONNECTION_LIST = "B2175";

    final public static String W_FORCE_ENDED_TXN = "B2176";
    final public static String W_FORCE_ROLLEDBACK_TXN = "B2177";
    final public static String W_ADMIN_COMMITTED_TXN = "B2178";

    final public static String W_UPDATE_TRAN_STATE_FAIL = "B2179";
    final public static String W_MBUS_STILL_TRYING_NOWAIT = "B2180";
    final public static String W_REMOVING_DST_WITH_MSG    = "B2181";
    final public static String W_CONN_CLEANUP_ROLLBACK_TRAN_FAIL = "B2182";
    final public static String W_ROLLBACK_TIMEDOUT_DETACHED_TXN = "B2185";
    final public static String W_ROLLBACK_TIMEDOUT_DETACHED_TXN_FAILED = "B2186";
    final public static String W_TRAN_INFO_CORRUPTED = "B2187";
    final public static String W_UNKNOWN_TRANSACTIONID_NOTIFY_CLIENT  = "B2188";
    final public static String W_UNKNOWN_TRANSACTIONID_NONOTIFY_CLIENT  = "B2189";
    final public static String W_HALT_BROKER = "B2190";
    final public static String W_CLUSTER_WAIT_LINKINIT_TIMEOUT = "B2191";
    final public static String W_UPDATE_HEARTBEAT_TS_EXCEPTION = "B2192";
    final public static String W_IN_TAKEOVER_RECONNECT_LATER = "B2193";
    final public static String W_HA_MASTER_BROKER_NOT_ALLOWED = "B2194";
    final public static String W_UNKNOWN_XID                  = "B2195";
    final public static String W_TAKEOVER_MSG_ALREADY_ACKED   = "B2196";
    final public static String W_TAKEOVER_MSG_ALREADY_ROUTED  = "B2197";    
    final public static String W_JMX_FAILED_TO_GET_IP	      = "B2198";    
    final public static String W_JAAS_UNSUPPORTED_TEXTINPUTCALLBACK  = "B2199";    
    final public static String W_JAAS_UNSUPPORTED_TEXTOUTPUTCALLBACK = "B2200";
    final public static String W_JAAS_UNSUPPORTED_CALLBACK = "B2201";
    final public static String W_TXN_TYPE_CORRUPTED                = "B2202";
    final public static String W_CLUSTER_TXN_BROKER_INFO_CORRUPTED = "B2203";
    final public static String W_REMOTE_TXN_HOME_BROKER_INFO_CORRUPTED = "B2204";
    final public static String W_NOTIFY_TXN_COMPLETE_UNREACHABLE = "B2205";
    final public static String W_TAKEOVER_ENTRY_NOT_FOUND = "B2206";
    final public static String W_RECEIVED_GOODBYE_UNAUTHENTICATED_CONN = "B2207";
    final public static String W_JESMF_MGR_CLASS_CANT_INST		= "B2208";
    final public static String W_JESMF_EXCEPTION_WHEN_INIT_MGR		= "B2209";
    final public static String W_JESMF_EXCEPTION_WHEN_STOP_MGR		= "B2210";
    final public static String W_CLUSTER_ATTACH_CONSUMER_FAIL       = "B2211";
    final public static String W_ACK_MESSAGE_GONE       = "B2212";
    final public static String W_ACK_MESSAGE_GONE_IN_TXN       = "B2213";
    final public static String W_BRIDGE_SERVICE_NOT_ENABLED = "B2214";
    final public static String W_BRIDGE_SERVICE_MANAGER_NOT_RUNNING = "B2215";
    final public static String W_INIT_BRIDGE_SERVICE_MANAGER_FAILED = "B2216";
    final public static String W_START_BRIDGE_SERVICE_MANAGER_FAILED = "B2217";
    final public static String W_STOP_BRIDGE_SERVICE_MANAGER_FAILED = "B2218";
    final public static String W_PROP_SETTING_TOBE_IGNORED = "B2219";
    final public static String W_CLUSTER_LOCK_UIDPREFIX_FAIL = "B2220";
    final public static String W_TABLE_NOT_FOUND_IN_DATABASE = "B2221";
    final public static String W_SEND_REMOTE_TXN_INFO_FAIL = "B2222";
    final public static String W_SEND_CLUSTER_TXN_INFO_FAIL = "B2223";
    final public static String W_UNABLE_PROCESS_REMOTE_ACK_BECAUSE = "B2224";
    final public static String W_WAIT_FOR_BEEN_TAKENOVER_TIMEOUT = "B2225";
    final public static String W_DB_CONN_VALIDATION_EXCEPTION = "B2226";
    final public static String W_DB_CONN_DESTROY_EXCEPTION = "B2227";
    final public static String W_DB_CONN_RETURN_NOT_FOUND_INPOOL = "B2228";
    final public static String W_DB_CONN_CLOSE_EXCEPTION = "B2229";
    final public static String W_DB_CONN_RETURN_UNKNOWN = "B2230";
    final public static String W_DB_CONN_ERROR_EVENT = "B2231";
    final public static String W_CLUSTER_ADD_REMOTE_CONSUMER_EXCEPTION = "B2232";
    final public static String W_EXCEPTION_CLOSE_MSG_ENUM_RESOURCE = "B2233";
    final public static String W_EXCEPTION_CANCEL_MSG_ENUM = "B2234";
    final public static String W_DB_POOL_REAPER_CREATE_NEW_CONN_FAIL = "B2235";
    final public static String W_WAITING_FOR_DB_CONNECTION = "B2236";
    final public static String W_TIMEOUT_WAIT_ACCESS_SHARECC_JDBCSTORE = "B2237";
    final public static String W_CLOSE_SHARECC_JDBCSTORE_EXCEPTION = "B2238";
    final public static String W_UNABLE_STORE_LAST_SEQ_FOR_SHARECC = "B2239";
    final public static String W_UNABLE_STORE_LAST_RESET_UUID_FOR_SHARECC = "B2240";
    final public static String W_UNABLE_SYNC_SHARECC_LINK_RETRY = "B2241";
    final public static String W_ECHO_PASSWORD = "B2242";
    final public static String W_UNABLE_UPDATE_REF_STATE_ON_ROLLBACK = "B2243";
    final public static String W_UNABLE_UPDATE_REF_STATE_ON_CLOSE_CONSUMER = "B2244";
    final public static String W_UNABLE_CLEANUP_REMOTE_MSG_ON_ROUTE = "B2245";
    final public static String W_DB_POOL_CLOSING = "B2246";
    final public static String W_DB_POOL_POLL_TIMEOUT = "B2247";
    final public static String W_UNABLE_DELETE_FILE_IN_DIR = "B2248";
    final public static String W_UNABLE_RENAME_FILE = "B2249";

    final public static String W_MSG_REMOVED_BEFORE_SENDER_COMMIT  = "B2254";
    final public static String W_PROCCESS_COMMITTED_ACK	 = "B2255";

    final public static String W_EXCEPTION_PROCESS_REMOTE_MSG  = "B2256";
    final public static String W_PROCESS_REMOTE_MSG_DST_LIMIT  = "B2257";
    final public static String W_CHECKPOINT_WAIT_PLAYTO_STORE_TIMEOUT  = "B2259";
    
    final public static String W_IGNORE_VALUE_IN_PROPERTY_LIST  = "B2262";
    final public static String W_UNABLE_UPDATE_MSG_DELIVERED_STATE  = "B2263";

    // 3000-3999 Error Messages
    final public static String E_PERSISTENT_OPEN 	= "B3000";
    final public static String E_FALLBACK_PROPS 	= "B3001";
    final public static String E_BAD_CONFIG_STORE 	= "B3002";
    final public static String E_PROPERTY_WRITE 	= "B3003";
    final public static String E_NOT_ENOUGH_THREADS 	= "B3004";
    final public static String E_BAD_STORE_TYPE 	= "B3005";
    final public static String E_OPEN_STORE_FAILED 	= "B3006";
    final public static String E_MSG_NOT_FOUND_IN_STORE = "B3007";
    final public static String E_MSG_EXISTS_IN_STORE	= "B3008";
    final public static String E_ERROR_STARTING_SERVICE	= "B3009";
    final public static String E_INTEREST_EXISTS_IN_STORE = "B3010";
    final public static String E_INTEREST_NOT_FOUND_IN_STORE = "B3011";
    final public static String E_DESTINATION_EXISTS_IN_STORE = "B3012";
    final public static String E_DESTINATION_NOT_FOUND_IN_STORE = "B3013";
    final public static String E_INTEREST_STATE_NOT_FOUND_IN_STORE = "B3014";
    final public static String E_TRANSACTIONID_EXISTS_IN_STORE = "B3015";
    final public static String E_TRANSACTIONID_NOT_FOUND_IN_STORE = "B3016";
    final public static String E_CANNOT_CREATE_STORE_HIERARCHY = "B3017";
    final public static String E_RUNNING_SERVICE    = "B3018";
    final public static String E_KEYSTORE_NOT_EXIST = "B3019";
    final public static String E_GET_PASSFILE       = "B3020";
    final public static String E_PASS_PHRASE_NULL   = "B3021";
    final public static String E_BAD_SERVICE_START  = "B3022";
    final public static String E_NO_JDBC_DRIVER_PROP  = "B3023";
    final public static String E_CANNOT_LOAD_JDBC_DRIVER  = "B3024";
    final public static String E_NO_DATABASE_URL_PROP  = "B3025";
    final public static String E_CANNOT_GET_DB_CONNECTION  = "B3026";
    final public static String E_PREPARE_DBSTMTS_FAILED  = "B3027";
    final public static String E_BAD_DBHOME_PROP 	= "B3028";
    final public static String E_UNRECOG_OPTION 	= "B3029";
    final public static String E_INVALID_BASE_PROPNAME 	= "B3030";
    final public static String E_INVALID_HARDCODED_VAL 	= "B3031";
    final public static String E_MISSING_ARG 		= "B3032";
    final public static String E_BAD_NV_ARG 		= "B3033";
    final public static String E_OPTION_PARSE_ERROR 	= "B3034";
    final public static String E_NO_COMMAND_SPEC 	= "B3035";
    final public static String E_BAD_COMMAND_SPEC 	= "B3036";
    final public static String E_OPTION_VALID_ERROR 	= "B3037";
    final public static String E_PASSWD_NOT_SPEC 	= "B3038";
    final public static String E_PASSWD_OR_ACTIVE_NOT_SPEC 	= "B3039";
    final public static String E_INVALID_ROLE_SPEC1 	= "B3040";
    final public static String E_INVALID_ROLE_SPEC2 	= "B3041";
    final public static String E_USERNAME_NOT_SPEC 	= "B3042";
    final public static String E_ROLE_NOT_SPEC 		= "B3043";
    final public static String E_INTERNAL_ERROR 	= "B3044";
    final public static String E_ERROR	 		= "B3045";
    final public static String E_PW_FILE_NOT_FOUND	= "B3046";
    final public static String E_PW_FILE_FORMAT_ERROR	= "B3047";
    final public static String E_USER_NOT_EXIST		= "B3048";
    final public static String E_PASSWD_INCORRECT	= "B3049";
    final public static String E_USER_ALREADY_EXIST	= "B3050";
    final public static String E_PW_FILE_WRITE_ERROR	= "B3051";
    final public static String E_PW_FILE_READ_ERROR	= "B3052";
    final public static String E_ONLY_ONE_ANON_USER	= "B3053";
    final public static String E_BAD_LICENSE_DATA	= "B3054";
    final public static String E_LICENSE_EXPIRED	= "B3055";
    final public static String E_LICENSE_NOT_LOADED_FROM_FILE	= "B3056";
    final public static String E_NO_VALID_LICENSE	= "B3057";
    final public static String E_INVALID_OPTION		= "B3058";
    final public static String E_UNEXPECTED_PACKET_NOT_AUTHENTICATED = "B3059";
    final public static String E_GET_CHALLENGE_FAILED                = "B3060";
    final public static String E_AUTHENTICATE_RESPONSE_READ          = "B3061";
    final public static String E_ACK_EXISTS_IN_STORE	= "B3062";
    final public static String E_SHUTDOWN		= "B3063";
    final public static String E_PAUSE_SERVICE		= "B3064";
    final public static String E_PAUSE_SERVICES		= "B3065";
    final public static String E_RESUME_SERVICES	= "B3066";
    final public static String E_ILLEGAL_USERNAME	= "B3067";
    final public static String E_BROKER_PORT_BIND	= "B3068";
    final public static String E_PORTMAPPER_ACCEPT	= "B3069";
    final public static String E_PORTMAPPER_EXITING	= "B3070";
    final public static String E_PORTMAPPER_WRITE	= "B3071";
    final public static String E_PORTMAPPER_CONFIG	= "B3072";
    final public static String E_CREATE_DATABASE_TABLE_FAILED = "B3073";
    final public static String E_INVALID_TIMESTAMP	= "B3074";
    final public static String E_DELETE_DATABASE_TABLE_FAILED = "B3075";
    final public static String E_RECREATE_DATABASE_TABLE_FAILED = "B3076";
    final public static String E_EXTRA_DBMGR_CMD	= "B3077";
    final public static String E_INVALID_DBMGR_CMD_ARG	= "B3078";
    final public static String E_INVALID_DBMGR_OPT	= "B3079";
    final public static String E_MISSING_DBMGR_OPT_ARG	= "B3080";
    final public static String E_INVALID_DBMGR_OPT_ARG	= "B3081";
    final public static String E_MISSING_BROKER_ID	= "B3082";
    final public static String E_MISSING_DBMGR_CMD_ARG	= "B3083";
    final public static String E_MSG_INTEREST_LIST_EXISTS = "B3084";
    final public static String E_BAD_INTEREST_LIST	= "B3085";
    final public static String E_LOCKFILE_EXCEPTION   	= "B3086";
    final public static String E_LOCKFILE_INUSE       	= "B3087";
    final public static String E_LOCKFILE_BADUPDATE   	= "B3088";
    final public static String E_BAD_ACTIVE_VALUE_SPEC 	= "B3089";
    final public static String E_PROBLEM_GETTING_INPUT 	= "B3090";
    final public static String E_MBUS_CONN_LIMIT 	= "B3091";
    final public static String E_BAD_STORE_VERSION      = "B3092";
    final public static String E_BAD_BROKER_ID		= "B3093";
    final public static String E_BROKER_ID_TOO_LONG	= "B3094";
    final public static String E_MBUS_CLUSTER_JOIN_ERROR    = "B3095";
    final public static String E_MBUS_DEST_UPDATE_ERROR     = "B3096";
    final public static String E_MBUS_CONFIG_MISMATCH1     = "B3097";
    final public static String E_MBUS_CONFIG_MISMATCH2     = "B3098";
    final public static String E_MBUS_BAD_ADDRESS     = "B3099";
    final public static String E_INTERNAL_BROKER_ERROR		= "B3100";
    final public static String E_UNABLE_TO_RETRIEVE_DATA	= "B3101";
    final public static String E_STORE_ACCESSED_AFTER_CLOSED	= "B3102";
    final public static String E_NOT_JDBC_STORE_TYPE		= "B3103";
    final public static String E_INVALID_DBMGR_CMD	= "B3104";
    final public static String E_MISSING_DBMGR_CMD	= "B3105";
    final public static String E_FATAL_CON_ERROR	= "B3106";
    final public static String E_LOW_MEMORY_FAILED	= "B3107";
    final public static String E_MBUS_LOW_MEMORY_FAILED	= "B3108";
    final public static String E_LNX_SERVICE_UPDATE_FAILED  = "B3109";
    final public static String E_ACTIVE_NOT_VALID_WITH_ADD  = "B3110";
    final public static String E_LICENSE_NOT_VALID_YET  = "B3111";
    final public static String E_LICENSE_FILE_NOT_WRITABLE	= "B3112";
    final public static String E_NO_SUCH_TRANSACTION	        = "B3113";
    final public static String E_BAD_TRANSACTION_ID	        = "B3114";
    final public static String E_TRANSACTION_NOT_PREPARED       = "B3115";
    final public static String E_BAD_CONFIGRECORD_FILE       	= "B3116";
    final public static String E_RESET_STORE_FAILED       	= "B3117";
    final public static String E_PASSWD_ENCRYPT_FAIL       	= "B3118";
    final public static String E_INSTANCE_NOT_EXIST       	= "B3119";
    final public static String E_REMOVE_STORE_FAILED       	= "B3120";
    final public static String E_REMOVE_JDBC_STORE_FAILED      	= "B3121";
    final public static String E_FEATURE_UNAVAILABLE      = "B3122";
    final public static String E_FATAL_FEATURE_UNAVAILABLE      = "B3123";
    final public static String E_CAN_NOT_WRITE      = "B3124";
    final public static String E_BAD_OLDSTORE_VERSION		= "B3125";
    final public static String E_BAD_OLDSTORE_NO_VERSIONFILE	= "B3126";
    final public static String E_NO_JDBC_TABLE_PROP		= "B3127";
    final public static String E_NO_SUCH_DESTINATION	        = "B3128";
    final public static String E_UNSUPPORTED_FILE_STORE		= "B3129";
    final public static String E_NO_SUCH_CONNECTION	        = "B3130";
    final public static String E_BAD_OLDSTORE_NO_VERSIONDATA	= "B3131";
    final public static String E_NO_DATABASE_TABLES	        = "B3132";
    final public static String E_REMOVE_OLD_TABLES_FAILED      	= "B3133";
    final public static String E_DESTINATION_NOT_PAUSED		= "B3134";
    final public static String E_SOME_DESTINATIONS_NOT_PAUSED	= "B3135";
    final public static String E_NOT_JDBC_STORE_OPERATION	= "B3136";
    final public static String E_USERMGR_INSTANCE_NOT_EXIST     = "B3137";
    final public static String E_BAD_PROPERTY_VALUE             = "B3138";
    final public static String E_BAD_JMQHOME                    = "B3139";
    final public static String E_BAD_JMQVARHOME                 = "B3140";
    final public static String E_BAD_JMQLIBHOME                 = "B3141";
    final public static String E_BAD_JMQETCHOME                 = "B3142";
    final public static String E_FORCE_CON_CLOSE                = "B3143";
    final public static String E_LOAD_MSG_ERROR                 = "B3144";
    final public static String E_REMOVE_MSG_ERROR               = "B3145";
    final public static String E_LOAD_DURABLES                  = "B3146";
    final public static String E_STORE_DURABLE                  = "B3147";
    final public static String E_REMOVE_DURABLE                 = "B3148";
    final public static String E_CREATE_DURABLE                 = "B3149";
    final public static String E_BAD_HOSTNAME                   = "B3150";
    final public static String E_BAD_HOSTNAME_PROP              = "B3151";
    final public static String E_NO_LOCALHOST                   = "B3152";
    final public static String E_CLUSTER_HOSTNAME               = "B3153";
    final public static String E_BAD_JAVA_VERSION               = "B3154";
    final public static String E_TABLE_LOCKED_BY_BROKER		= "B3155";
    final public static String E_TABLE_LOCKED_BY_DBMGR 		= "B3156";
    final public static String E_CANNOT_CREATE_MQADDRESS	= "B3157";
    final public static String E_READ_PASSFILE_FAIL 		= "B3158";
    final public static String E_LOAD_LICENSE                   = "B3159";
    final public static String E_MSG_NOT_FOUND_IN_DST		= "B3160";
    final public static String E_CLUSTER_LOAD_LASTCONFIGSERVER  = "B3161";
    final public static String E_CLUSTER_RESET_LASTREFRESHTIME	= "B3162";
    final public static String E_LICENSE_FILE_NOT_READABLE      = "B3163";
    final public static String E_PORTMAPPER_EXCEPTION           = "B3164";
    final public static String E_CLUSTER_UNSUBSCRIBE_EXCEPTION  = "B3165";
    final public static String E_USERNAME_IS_EMPTY 		= "B3166";
    final public static String E_LOCALHOST_ADDRESS              = "B3167";
    final public static String E_BADADDRESS_THIS_BROKER         = "B3168";
    final public static String E_BROKERINFO_NOT_FOUND_IN_STORE  = "B3169";
    final public static String E_UNABLE_TO_ACQUIRE_TAKEOVER_LOCK= "B3170";
    final public static String E_UNABLE_TO_REMOVE_TAKEOVER_LOCK = "B3171";
    final public static String E_UNABLE_TO_TAKEOVER_BROKER      = "B3172";
    final public static String E_TAKEOVER_WITHOUT_LOCK          = "B3173";
    final public static String E_TAKEOVER_STORE_SESSIONS_FAILED = "B3174";
    final public static String E_LOAD_DST_FOR_BROKER_FAILED     = "B3175";
    final public static String E_LOAD_MSG_FOR_BROKER_FAILED     = "B3176";
    final public static String E_INACTIVE_SESSION_REMOVAL_FAILED= "B3177";
    final public static String E_BAD_CLUSTER_ID                 = "B3178";
    final public static String E_CLUSTER_ID_TOO_LONG            = "B3179";
    final public static String E_PERSIST_INTEREST_STATE_FAILED  = "B3180";
    final public static String E_UPDATE_NONLOCAL_DST_CONNECTED_TIME = "B3181";
    final public static String E_REMOVE_BROKER_FAILED           = "B3182";
    final public static String E_REMOVE_BROKER_2_FAILED         = "B3183";
    final public static String E_UNABLE_TO_ACQUIRE_STORE_LOCK   = "B3184";
    final public static String E_CLUSTER_UID_PREFIX_CLASH_RESTART = "B3185";
    final public static String E_CLUSTER_TAKINGOVER_NOTIFY_RESTART = "B3186";
    final public static String E_CLUSTER_TAKINGOVER_RESTART        = "B3187";
    final public static String E_BAD_HABROKERS_FILE  = "B3188";
    final public static String E_CLUSTER_READ_PACKET_EXCEPTION     = "B3189";
    final public static String E_CLUSTER_MSG_ACK_NOT_HOMESESSION   = "B3190";
    final public static String E_CLUSTER_MSG_ACK_THIS_BEING_TAKEOVER  = "B3191";
    final public static String E_CLUSTER_SEND_REPLY_FAILED             = "B3192";
    final public static String E_CLUSTER_HB_ADD_ENDPOINT_FAILED    = "B3193";
    final public static String E_CLUSTER_HB_REM_ENDPOINT_FAILED    = "B3194";
    final public static String E_CLUSTER_BAD_ADDRESS_FROM          = "B3195";
    final public static String E_CLUSTER_HA_NOT_SUPPORT_MASTERBROKER = "B3196";
    final public static String E_BID_MUST_BE_SET                     = "B3197";
    final public static String E_INITING_CLUSTER                    = "B3198";
    final public static String E_ERROR_STARTING_MONITOR             = "B3199";
    final public static String E_ERROR_STARTING_HB                  = "B3200";
    final public static String E_ERROR_MONITOR_BAD_CID              = "B3201";
    final public static String E_BID_CONFLICT                       = "B3202";
    final public static String E_SPLIT_BRAIN                        = "B3203";
    final public static String E_TXN_TAKEOVER_RB                    = "B3204";
    final public static String E_REMOVE_STORE_LOCK_FAILED           = "B3205";
    final public static String E_BAD_STORE_NO_VERSIONDATA           = "B3206";
    final public static String E_UPGRADE_STORE_FAILED               = "B3207";
    final public static String E_UPGRADE_HASTORE_FAILED             = "B3208";
    final public static String E_HA_CLUSTER_STILL_ACTIVE            = "B3209";
    final public static String E_DATABASE_TABLE_ALREADY_CREATED     = "B3210";
    final public static String E_DATABASE_TABLE_ALREADY_DELETED     = "B3211";
    final public static String E_NO_DATABASE_VENDOR_PROP            = "B3212";    
    final public static String E_BAD_STORE_MISSING_TABLES           = "B3213";
    final public static String E_NOT_ACTIVE_SERVICE                 = "B3214";
    final public static String E_NOT_ADMIN_SERVICE                  = "B3215";
    final public static String E_INVALID_RESET_BROKER_TYPE          = "B3216";
    final public static String E_PASSWD_OPTION_NOT_SUPPORTED        = "B3217";

    final public static String E_ACK_EXISTS_IN_TRANSACTION          = "B3218";
    final public static String E_UPDATE_TXNBROKER_FAILED            = "B3219";
    final public static String E_CANNOT_SHUTDOWN_IN_PROCESS         = "B3220";
   
    final public static String E_HA_CLUSTER_INVALID_STORE_TYPE      = "B3221";

    final public static String E_INVALID_TXN_STATE_FOR_ROLLBACK     = "B3222";
    final public static String E_STORE_BEING_TAKEN_OVER             = "B3223";
    final public static String E_CLUSTER_RECORD_CONFIG_CHANGE_EVENT_FAILED  = "B3224";

    final public static String E_CREATE_TXNLOG_FILE_FAILED          = "B3225";
    final public static String E_PROCESS_TXNLOG_RECORD_FAILED       = "B3226";
    final public static String E_ROUTE_RECONSTRUCTED_MSG_FAILED     = "B3227";
    final public static String E_RECONSTRUCT_STORE_FAILED           = "B3228";
    final public static String E_TRAN_ACK_PROCESSING_FAILED         = "B3229";
    final public static String E_UPDATE_HEARTBEAT_FAILED            = "B3230";

    final public static String E_UNABLE_TO_TAKEOVER_BKR             = "B3231";
    final public static String E_PURGE_DST_FAILED                   = "B3232";
    final public static String E_CANNOT_PROCEED_TAKEOVER_IN_PROCESS = "B3233";

    final public static String E_INVALID_PRODUCT_VERSION = "B3234";
    final public static String E_BADADDRESS_CLUSTER_SERVICE         = "B3235";
    final public static String E_BADADDRESS_PORTMAPPER_FOR_CLUSTER  = "B3236";
    final public static String E_THE_DATABASE_TABLE_ALREADY_CREATED = "B3237";
    final public static String E_BROKER_STILL_ACTIVE                = "B3238";
    final public static String E_REMOVE_JMSBRIDGE_FAILED            = "B3239";
    final public static String E_MISSING_DBMGR_OPT                  = "B3240";
    final public static String E_MBUS_SAME_ADDRESS_AS_ME            = "B3241";
    final public static String E_MBUS_SAME_ADDRESS_PEERS            = "B3242";
    final public static String E_UNABLE_TO_CREATE_INSTANCE          = "B3243";
    final public static String E_TAKEOVER_DATA_PROCESSING_FAIL      = "B3244";
    final public static String E_DB_POOL_REAPER_THREAD_EXCEPTION    = "B3245";
    final public static String E_SHARECC_JDBCSTORE_MISSING_TABLE = "B3246";
    final public static String E_NO_SHARECC_JDBCSTORE_TABLE = "B3247";
    final public static String E_FAIL_TO_CREATE = "B3248";
    final public static String E_FAIL_ACCESS_SHARECC_JDBCSTORE = "B3249";
    final public static String E_UNEXPECTED_EXCEPTION = "B3250";
    final public static String E_SHARECC_STORE_OPEN = "B3251";
    final public static String E_FAIL_OPEN_SHARECC_STORE = "B3252";
    final public static String E_CANNOT_RESTART_IN_PROCESS          = "B3253";    
    final public static String E_FAIL_PROCESS_SHARECC_RECORDS = "B3254";
    final public static String E_SHARECC_RUNTIME_RESET_SYNC_FAIL = "B3255";
    final public static String E_SHARECC_FAIL_RECREATE_TABLE = "B3256";
    final public static String E_SHARECC_FAIL_DELETE_TABLE = "B3257";
    final public static String E_SHARECC_TABLE_ALREADY_DELETED = "B3258";
    final public static String E_NOT_CONFIGURED_USE_SHARECC = "B3259";
    final public static String E_CONFIGURED_HA_MODE = "B3260";
    final public static String E_SHARCC_SYNC_ON_STARTUP_FAILED = "B3261";
    final public static String E_OP_NOT_APPLY_TO_HA_BROKER = "B3262";
    final public static String E_SHARECC_TABLE_ALREADY_CREATED = "B3263";
    final public static String E_BROKER_NOT_JMSRA_MANAGED_IGNORE_OP = "B3264";
    final public static String E_SHARECC_BACKUP_RECORDS_FAIL = "B3265";
    final public static String E_SHARECC_TABLE_NOT_EMPTY = "B3266";
    final public static String E_SHARECC_RESTORE_RECORDS_FAIL = "B3267";
    final public static String E_SHARECC_CHECK_EXIST_EXCEPTION = "B3268";
    final public static String E_CLUSTER_PROCESS_PACKET_FROM_BROKER_FAIL = "B3269";
    final public static String E_CLUSTER_RECEIVED_ERROR_REPLY_FROM_BROKER = "B3270";
    final public static String E_OP_NOT_APPLY_NO_MASTER_BROKER_MODE= "B3271";
    final public static String E_CHANGE_MASTER_BROKER_FAIL= "B3272";
    final public static String E_CLUSTER_RESTORE_MASTER_BROKER_PROP_FAIL = "B3273";
    final public static String E_CLOSE_CONN_ON_OOM = "B3274";
    final public static String E_SET_BROKER_CONFIG_PROPERTY = "B3275";
    final public static String E_START_JMSRA_MANAGED_BROKER_NONMANAGED = "B3276";
    final public static String E_CLEANUP_MSG_AFTER_ACK = "B3277";
    final public static String E_TXN_REAPER_START = "B3278";
    final public static String E_TXN_REAPER_UNEXPECTED_EXIT = "B3279";
    final public static String E_CANT_CREATE_PWFILE = "B3280";
    final public static String E_NO_UPGRADE_OLD_FSTORE_WITH_NEWTXNLOG = "B3281";

    // 4000-4999 Exception Messages
    final public static String X_NO_FILE 		= "B4000";
    final public static String X_PORT_UNAVAILABLE 	= "B4001";
    final public static String X_PROTO_UNAVAILABLE 	= "B4002";
    final public static String X_MISSING_SERVICE_PROPERTY = "B4003";
    final public static String X_PERSIST_MESSAGE_FAILED = "B4004";
    final public static String X_RETRIEVE_MESSAGE_FAILED = "B4005";
    final public static String X_S_QUEUE_ATTACH_FAILED = "B4006";
    final public static String X_NON_EMPTY_DURABLE = "B4007";
    final public static String X_TMP_DEST_CONFLICT = "B4008";
    final public static String X_TMP_REMOVE_CONFLICT = "B4009";
    final public static String X_TMP_REMOVE_NOT_FOUND = "B4010";
    final public static String X_TMP_ADD_CONFLICT = "B4011";
    final public static String X_PERSIST_INTEREST_FAILED = "B4012";
    final public static String X_RETRIEVE_INTEREST_FAILED = "B4013";
    final public static String X_PERSIST_DESTINATION_FAILED = "B4014";
    final public static String X_PERSIST_INTEREST_STATE_FAILED = "B4015";
    final public static String X_PERSIST_MESSAGE_REMOVE_FAILED = "B4016";
    final public static String X_DESTINATION_NOT_FOUND = "B4017";
    final public static String X_TMP_VERIFY_CONFLICT = "B4018";
    final public static String X_PERSIST_TRANSACTION_FAILED = "B4019";
    final public static String X_CLEAR_TRANSACTION_FILE_FAILED = "B4020";
    final public static String X_UNKNOWN_DESTINATION = "B4021";	
    final public static String X_GET_SSL_SOCKET_FACT = "B4022";
    final public static String X_READ_PASSFILE       = "B4023";
    final public static String X_MAX_MESSAGE_COUNT_EXCEEDED   = "B4024";
    final public static String X_MAX_MESSAGE_SIZE_EXCEEDED   = "B4025";
    final public static String X_IND_MESSAGE_SIZE_EXCEEDED   = "B4026";
    final public static String X_BAD_PROPERTY_VALUE          = "B4027";
    final public static String X_BAD_PROPERTY                = "B4028";
    final public static String X_LOAD_MESSAGES_FAILED     = "B4029";
    final public static String X_CLEAR_TRANSACTION_TABLE_FAILED = "B4030";
    final public static String X_LOAD_DESTINATIONS_FAILED    = "B4031";
    final public static String X_LOAD_TRANSACTIONS_FAILED  = "B4032";
    final public static String X_REMOVE_MESSAGE_FAILED     = "B4033";
    final public static String X_REMOVE_INTEREST_FAILED     = "B4034";
    final public static String X_LOAD_INTERESTS_FAILED     = "B4035";
    final public static String X_GET_INTEREST_STATE_FAILED	= "B4036";
    final public static String X_REMOVE_INTEREST_STATE_FAILED = "B4037";
    final public static String X_LOAD_INT_STATES_FAILED     = "B4038";
    final public static String X_FAILED_TO_LOAD_DATA     = "B4039";
    final public static String X_UNSUPPORTED_AUTHTYPE              = "B4040";
    final public static String X_USER_REPOSITORY_NOT_DEFINED       = "B4041";
    final public static String X_USER_REPOSITORY_CLASS_NOT_DEFINED = "B4042";
    final public static String X_CONNECTION_NOT_AUTHENTICATED      = "B4043";
    final public static String X_ACCESSCONTROL_TYPE_NOT_DEFINED    = "B4044";
    final public static String X_ACCESSCONTROL_CLASS_NOT_DEFINED   = "B4045";
    final public static String X_ACCESSCONTROL_NOT_DEFINED         = "B4046";
    final public static String X_FAILED_TO_LOAD_ACCESSCONTROL      = "B4047";
    final public static String X_USER_NOT_DEFINED                  = "B4048";
    final public static String X_USER_NAME_RESERVED                = "B4049";
    final public static String X_ROLE_NAME_RESERVED                = "B4050";
    final public static String X_FORBIDDEN                         = "B4051";
    final public static String X_NOT_ADMINISTRATOR                 = "B4052";
    final public static String X_PURGE_UNKNOWN_DEST                = "B4053";
    final public static String X_LOAD_TXNACK_FAILED		= "B4054";
    final public static String X_CLEAR_TXNACK_FAILED		= "B4055";
    final public static String X_PERSIST_TXNACK_FAILED		= "B4056";
    final public static String X_NO_SUCH_SERVICE	        = "B4057";
    final public static String X_UNKNOWN_DURABLE_INTEREST= "B4058";
    final public static String X_CONNECTION_LIMIT_EXCEEDED= "B4059";
    final public static String X_REMOVE_TXNACK_FAILED		= "B4060";
    final public static String X_TRANSACTIONID_INUSE= "B4061";
    final public static String X_TRANSACTION_STORE_ERROR= "B4062";
    final public static String X_DESTINATION_EXISTS = "B4063";
    final public static String X_LDAP_REPOSITORY_PROPERTY_NOT_DEFINED = "B4064";
    final public static String X_NOT_UNIQUE_USER                      = "B4065";
    final public static String X_PASSWORD_NOT_PROVIDED                = "B4066";
    final public static String X_USERNAME_NOT_PROVIDED                = "B4067";
    final public static String X_DN_NOT_FOUND                         = "B4068";
    final public static String X_GROUP_NAME_RESERVED                  = "B4069";
    final public static String X_REPOSITORY_TYPE_MISMATCH             = "B4070";
    final public static String X_LDAP_SEARCH_RESULT_NOT_RELATIVE      = "B4071";
    final public static String X_ACCESSCONTROL_TYPE_MISMATCH          = "B4072";
    final public static String X_ACCESSCONTROL_FILE_MISMATCH          = "B4073";
    final public static String X_ACCESSCONTROL_FILE_PROP_NOT_SPECIFIED = "B4074";
    final public static String X_LDAP_GROUP_SEARCH_ERROR               = "B4075";
    final public static String X_FORBIDDEN_JMQ_ADMIN_DEST              = "B4076";
    final public static String X_UNDEFINED_AUTHTYPE                    = "B4077";
    final public static String X_AUTHTYPE_MISMATCH                     = "B4078";
    final public static String X_UNSUPPORTED_USER_REPOSITORY_MATCHTYPE = "B4079";
    final public static String X_DB_ROLLBACK_FAILED		= "B4080";
    final public static String X_CLEAR_ALL_FAILED		= "B4081";
    final public static String X_LOAD_CONFIGRECORDS_FAILED	= "B4082";
    final public static String X_PERSIST_CONFIGRECORD_FAILED	= "B4083";
    final public static String X_PERSIST_TIMESTAMP_FAILED	= "B4084";
    final public static String X_NO_SERVICE_PROPS_SET		= "B4085";
    final public static String X_SERVICE_PROP_EXCEPTION		= "B4086";
    final public static String X_DESTROY_DEST_EXCEPTION		= "B4087";
    final public static String X_NO_DEST_TYPE_SET		= "B4088";
    final public static String X_NO_DEST_NAME_SET		= "B4089";
    final public static String X_CREATE_DEST_EXCEPTION		= "B4090";
    final public static String X_DEST_NAME_INVALID		= "B4091";
    final public static String X_UPDATE_DEST_EXCEPTION		= "B4092";
    final public static String X_RESUME_SERVICE_EXCEPTION	= "B4093";
    final public static String X_CFG_SERVER_UNREACHABLE	= "B4094";
    final public static String X_DURABLE_CONFLICT	= "B4095";
    final public static String X_CLEAR_CONFIGTABLE_FAILED	= "B4096";
    final public static String X_LOAD_LASTREFRESHTIME_FAILED	= "B4097";
    final public static String X_CLEAR_LASTREFRESHTIME_FAILED	= "B4098";
    final public static String X_ADMINKEY_NOT_EXIST	     = "B4099";
    final public static String X_ILLEGAL_AUTHSTATE	     = "B4100";
    final public static String X_AUTHTYPE_OVERRIDE	     = "B4101";
    final public static String X_RESTRICTED_ADMIN_NON_JMQADMINDEST = "B4102";
    final public static String X_CLOSE_DATABASE_FAILED		= "B4103";
    final public static String X_CLEANUP_INT_STATES_FAILED	= "B4104";
    final public static String X_PERSIST_INTEREST_LIST_FAILED	= "B4105";
    final public static String X_LOCKFILE_BADDEL       	        = "B4106";
    final public static String X_LOAD_MESSAGE_FAILED		= "B4107";
    final public static String X_CONNECTION_LOGGEDOUT		= "B4108";
    final public static String X_INVALID_CLIENTID   	    = "B4109";
    final public static String X_THREADPOOL_MIN_GT_MAX	    = "B4110";
    final public static String X_THREADPOOL_MAX_LE_MIN	    = "B4111";
    final public static String X_PERSIST_PROPERTY_FAILED	= "B4112";
    final public static String X_LOAD_PROPERTIES_FAILED		= "B4113";
    final public static String X_STORE_VERSION_CHECK_FAILED	= "B4114";
    final public static String X_THREADPOOL_BAD_SET             = "B4115";
    final public static String X_MBUS_ACKLIST_UPDATE_ERROR  = "B4116";
    final public static String X_INTERNAL_EXCEPTION             = "B4117";
    final public static String X_CLUSTER_UNREACHABLE             = "B4118";
    final public static String X_NOT_TEMP_CONNECTION             = "B4119";
    final public static String X_DEST_MSG_CAPACITY_EXCEEDED        = "B4120";
    final public static String X_DEST_MSG_BYTES_EXCEEDED        = "B4121";
    final public static String X_DEST_MSG_SIZE_EXCEEDED        = "B4122";
    final public static String X_PARSE_CONFIGRECORD_FAILED	= "B4123";
    final public static String X_PARSE_DESTINATION_FAILED	= "B4124";
    final public static String X_PARSE_INTEREST_FAILED		= "B4125";
    final public static String X_PARSE_PROPERTY_FAILED		= "B4126";
    final public static String X_PARSE_STOREDIID_FAILED		= "B4127";
    final public static String X_PARSE_TRANSACTION_FAILED	= "B4128";
    final public static String X_PARSE_TXNACK_FAILED		= "B4129";
    final public static String X_HTTP_PORT_CONFLICT		= "B4130";
    final public static String X_TMP_NO_ADMIN_DESTROY 		= "B4131";
    final public static String X_BAD_TXN_TRANSITION   		= "B4132";
    final public static String X_ILLEGAL_PURGE_ACTIVE   	= "B4133";
    final public static String X_ILLEGAL_PURGE_NOT_FOUND   	= "B4134";
    final public static String X_NO_CLIENTID   	                = "B4135";
    final public static String X_MAX_THREAD_ILLEGAL_VALUE       = "B4136";
    final public static String X_UPDATE_TXNSTATE_FAILED		= "B4137";
    final public static String X_REMOVE_TRANSACTION_FAILED	= "B4138";
    final public static String X_CLEAR_TXN_NOTIN_STATE_FAILED	= "B4139";
    final public static String X_SHUTTING_DOWN_BROKER   	= "B4140";
    final public static String X_BAD_DURABLE			= "B4141";
    final public static String X_UPGRADE_CRECORDS_FAILED	= "B4142";
    final public static String X_PARSE_MESSAGE_FAILED		= "B4143";
    final public static String X_REMOVE_DESTINATION_FAILED	= "B4144";
    final public static String X_LOAD_MESSAGES_FOR_DST_FAILED	= "B4145";
    final public static String X_RELEASE_MSGFILE_FAILED		= "B4146";
    final public static String X_LOAD_MESSAGE_FILE_FAILED	= "B4147";
    final public static String X_READ_FROM_VRECORD_FAILED	= "B4148";
    final public static String X_RESET_MESSAGES_FAILED		= "B4149";
    final public static String X_REMOVE_MESSAGES_FOR_DST_FAILED	= "B4150";
    final public static String X_PREPARE_DBSTMT_FAILED		= "B4151";
    final public static String X_GET_COUNTS_FROM_DATABASE_FAILED= "B4152";
    final public static String X_UPGRADE_MESSAGES_FAILED	= "B4153";
    final public static String X_UPGRADE_PROPERTIES_FAILED	= "B4154";
    final public static String X_UPGRADE_INTERESTS_FAILED	= "B4155";
    final public static String X_UPGRADE_TXNACK_FAILED		= "B4156";
    final public static String X_UPGRADE_TRANSACTIONS_FAILED	= "B4157";
    final public static String X_UPGRADE_DESTINATIONS_FAILED    = "B4158";
    final public static String X_LOAD_OLD_CONFIGRECORDS_FAILED	= "B4159";
    final public static String X_COMPACT_DSTS_EXCEPTION		= "B4160";
    final public static String X_COMPACT_DST_EXCEPTION		= "B4161";
    final public static String X_JDBC_UPGRADE_DESTINATIONS_FAILED = "B4162";
    final public static String X_JDBC_UPGRADE_MESSAGES_FAILED	= "B4163";
    final public static String X_GET_METADATA_FAILED		= "B4164";
    final public static String X_JDBC_UPGRADE_MESSAGE_INTERESTS_FAILED = "B4165";
    final public static String X_JDBC_QUERY_FAILED		= "B4166";
    final public static String X_JDBC_RESET_MESSAGES_FAILED	= "B4167";
    final public static String X_JDBC_UPGRADE_INTERESTS_FAILED	= "B4168";
    final public static String X_JDBC_RESET_INTERESTS_FAILED	= "B4169";
    final public static String X_JDBC_UPGRADE_PROPERTIES_FAILED = "B4170";
    final public static String X_JDBC_UPGRADE_CRECORDS_FAILED	= "B4171";
    final public static String X_JDBC_UPGRADE_TRANSACTIONS_FAILED = "B4172";
    final public static String X_JDBC_UPGRADE_TXNACK_FAILED	= "B4173";
    final public static String X_JDBC_CLEAR_TABLE_FAILED	= "B4174";
    final public static String X_CANNOT_CREATE_INTERNAL_DEST	= "B4175";
    final public static String X_CANNOT_CREATE_PROP_FILE	= "B4176";
    final public static String X_CANNOT_DELETE_PROP_FILE	= "B4177";
    final public static String X_BAD_MAX_PRODUCER_CNT	= "B4178";
    final public static String X_BAD_MAX_CONSUMER_CNT	= "B4179";
    final public static String X_FEATURE_UNAVAILABLE	= "B4180";
    final public static String X_MONITORING_DISABLED = "B4181";
    final public static String X_CONSUMER_LIMIT_EXCEEDED = "B4182";
    final public static String X_PRODUCER_LIMIT_EXCEEDED = "B4183";
    final public static String X_MONITOR_PRODUCER = "B4184";
    final public static String E_MONITOR_DEST_DISALLOWED      = "B4185";
    final public static String X_S_DUR_ATTACH_FAILED = "B4186";
    final public static String X_MOVE_MESSAGE_FAILED		= "B4187";
    final public static String X_IND_PACKET_SIZE_EXCEEDED = "B4188";
    final public static String X_DMQ_USE_DMQ_INVALID		= "B4189";
    final public static String X_DMQ_MOVE_INVALID		= "B4190";
    final public static String X_DMQ_INVAID_BEHAVIOR		= "B4191";
    final public static String X_DMQ_INVAID_PRODUCER_CNT	= "B4192";
    final public static String X_DMQ_INVAID_DESTROY		= "B4193";
    final public static String X_FAILED_TO_LOAD_A_DEST		= "B4194";
    final public static String X_FAILED_TO_LOAD_A_CONSUMER	= "B4195";
    final public static String X_FAILED_TO_LOAD_A_PROPERTY	= "B4196";
    final public static String X_FAILED_TO_LOAD_A_TXN		= "B4197";
    final public static String X_FAILED_TO_LOAD_A_TXNACK	= "B4198";
    final public static String X_FAILED_TO_LOAD_A_DEST_FROM_OLDSTORE = "B4199";
    final public static String X_FAILED_TO_LOAD_A_CONSUMER_FROM_OLDSTORE
								= "B4200";
    final public static String X_FAILED_TO_LOAD_A_PROPERTY_FROM_OLDSTORE
								= "B4201";
    final public static String X_FAILED_TO_LOAD_A_TXN_FROM_OLDSTORE
                                                                = "B4202";
    final public static String X_FAILED_TO_LOAD_A_TXNACK_FROM_OLDSTORE
								= "B4203";
    final public static String X_FAILED_TO_GET_AUDIT_SESSION	= "B4204";
    final public static String X_CLUSTER_UNICAST_UNREACHABLE	= "B4205";
    final public static String X_RECONNECT_TO_DB_FAILED		= "B4206";
    final public static String X_TOO_MANY_SHARED		= "B4207";
    final public static String X_CLUSTER_TRANSPORT_MISMATCH	= "B4208";
    final public static String X_DEST_FOR_DURABLE_REMOVED	= "B4209";
    final public static String X_START_SERVICE_EXCEPTION	= "B4210";
    final public static String X_JAAS_NAME_INDEX_NOT_DEFINED    = "B4211";
    final public static String X_MSG_EXISTS_IN_DEST             = "B4212";
    final public static String X_CANNOT_GET_LOOPBACKADDRESS     = "B4213";
    final public static String X_LOOPBACKADDRESS                = "B4214";
    final public static String X_ADDRESS_HAMODE_NOTMATCH        = "B4215";
    final public static String X_ADDRESS_NO_BROKERID            = "B4216";

    final public static String X_PERSIST_BROKERINFO_FAILED      = "B4217";
    final public static String X_LOAD_BROKERINFO_FAILED         = "B4218";
    final public static String X_UPDATE_BROKERINFO_FAILED       = "B4219";
    final public static String X_REMOVE_BROKERINFO_FAILED       = "B4220";
    final public static String X_LOAD_ALL_BROKERINFO_FAILED     = "B4221";
    final public static String X_UPDATE_HEARTBEAT_TS_FAILED     = "B4222";
    final public static String X_UPDATE_HEARTBEAT_TS_2_FAILED   = "B4223";
    final public static String X_LOAD_DESTINATION_FAILED        = "B4224";
    final public static String X_CLEAR_TXN_FROM_INT_STATES_FAILED = "B4225";
    final public static String X_CLEAR_TXN_FROM_MSGS_FAILED     = "B4226";
    final public static String X_REMOVE_INT_STATES_FOR_DST_FAILED = "B4227";
    final public static String X_LOAD_INT_STATES_FOR_MSG_FAILED = "B4228";
    final public static String X_LOAD_ACKS_FOR_TXN_FAILED       = "B4229";
    final public static String X_GET_MSG_COUNTS_FOR_BROKER_FAILED = "B4230";
    final public static String X_LOAD_PROPERTY_FAILED           = "B4231";
    final public static String X_REMOVE_PROPERTY_FAILED         = "B4232";
    final public static String X_LOAD_TRANSACTION_FAILED        = "B4233";
    final public static String X_UPDATE_TRANSACTION_FAILED      = "B4234";
    final public static String X_LOAD_TXNS_FOR_BROKER_FAILED    = "B4235";
    final public static String X_CREATE_TABLE_FAILED            = "B4236";
    final public static String X_DROP_TABLE_FAILED              = "B4237";
    final public static String X_PERSIST_STORE_VERSION_FAILED   = "B4238";
    final public static String X_LOAD_STORE_VERSION_FAILED      = "B4239";
    final public static String X_REMOVE_INT_STATES_FOR_TXN_FAILED = "B4240";
    final public static String X_CLUSTER_PROTOCOL_NOT_READY     = "B4241";
    final public static String X_LOAD_HABROKERS_FAILED	= "B4242";
    final public static String X_CLEAR_HABROKERTABLE_FAILED = "B4243";
    final public static String X_CLUSTER_MSG_ACK_HOME_BEING_TAKEOVER = "B4244";
    final public static String X_CLUSTER_MSG_ACK_FAILED_HOME_GONE          = "B4245";
    final public static String X_CLUSTER_MSG_ACK_HOMESESSION_GONE          = "B4246";
    final public static String X_CLUSTER_MSG_ACK_GOODBYED_HOME             = "B4247";
    final public static String X_DYNAMIC_UPDATE_PROPERTY_NOT_SUPPORT       = "B4248";
    final public static String X_CLUSTER_MSG_ACK_HOME_UNREACHABLE          = "B4249";
    final public static String X_HA_MASTER_BROKER_UNSUPPORTED              = "B4250";
    final public static String X_CANT_LOAD_DEST                 = "B4251";
    final public static String X_QUIESCE_FAILED                      = "B4252";
    final public static String X_CID_MUST_BE_SET_HA                     = "B4253";
    final public static String X_CANT_STOP_SERVICE                  = "B4254";
    final public static String X_CANT_START_SERVICE                  = "B4255";
    final public static String X_CLUSTER_CANNOT_GET_REMOTE_SERVICE_PORT = "B4256";
    final public static String X_CANT_GET_LICENSE_EXCEPTION 		   = "B4257";
    final public static String X_JMX_CANT_CREATE_CONNECTOR_SVR 		   = "B4258";
    final public static String X_JMX_NULL_CONSUMER_ID_SPEC 		   = "B4259";
    final public static String X_JMX_INVALID_CONSUMER_ID_SPEC 		   = "B4260";
    final public static String X_JMX_CONSUMER_NOT_FOUND 		   = "B4261";
    final public static String X_JMX_INVALID_DEST_TYPE_SPEC 		   = "B4262";
    final public static String X_JMX_INVALID_CREATE_TIME_ATTR_SPEC_QUEUE   = "B4263";
    final public static String X_JMX_INVALID_CREATE_TIME_ATTR_SPEC_TOPIC   = "B4264";
    final public static String X_JMX_INVALID_DEST_PAUSE_TYPE_SPEC          = "B4265";
    final public static String X_JMX_NULL_PRODUCER_ID_SPEC 		   = "B4266";
    final public static String X_JMX_INVALID_PRODUCER_ID_SPEC 		   = "B4267";
    final public static String X_JMX_PRODUCER_NOT_FOUND 		   = "B4268";
    final public static String X_JMX_NULL_TXN_ID_SPEC 		           = "B4269";
    final public static String X_JMX_INVALID_TXN_ID_SPEC 		   = "B4270";
    final public static String X_JMX_TXN_NOT_FOUND 		           = "B4271";

    final public static String X_UPDATE_TXN_STATE_CONFLICT  = "B4272";
    final public static String X_ACK_EXISTS_IN_TRANSACTION  = "B4273";

    final public static String X_CONNECTION_CLOSING	    = "B4274";
    final public static String X_CONSUMER_SESSION_NOT_FOUND = "B4275";

    final public static String X_PERSIST_STORE_SESSION_FAILED           = "B4276";
    final public static String X_REMOVE_STORE_SESSION_FAILED            = "B4277";
    final public static String X_LOAD_STORE_SESSION_FAILED              = "B4278";
    final public static String X_REMOVE_INACTIVE_STORE_SESSION_FAILED   = "B4279";
    final public static String X_REMOVE_STORE_SESSIONS_FAILED           = "B4280";
    final public static String X_LOAD_CURRENT_STORE_SESSION_FAILED      = "B4281";
    final public static String X_LOAD_STORE_SESSIONS_BY_BROKER_FAILED   = "B4282";
    final public static String X_LOAD_ALL_STORE_SESSIONS_FAILED         = "B4283";
    final public static String X_JDBC_UPGRADE_STORE_SESSIONS_FAILED     = "B4284";
    final public static String X_CLUSTER_MSG_ACK_FAILED_HOME_NORESPONSE = "B4285";
    final public static String X_SERVICE_RESTRICTION_AUTO_CREATE_DEST   = "B4286";
    final public static String X_SERVICE_RESTRICTION_TOPIC_PRODUCER     = "B4287";
    final public static String X_SERVICE_RESTRICTION_TOPIC_CONSUMER     = "B4288";
    final public static String X_SERVICE_RESTRICTION_DELETE_QUEUE       = "B4289";
    final public static String X_TXN_LOCKED = "B4290";
    final public static String X_FAILSTATE_TXN_TRANSITION_1               = "B4291";
    final public static String X_FAILSTATE_TXN_TRANSITION_2               = "B4292";
    final public static String X_UNSUPPORTED_PROPERTY_VALUE               = "B4293";
    final public static String X_ATTRIBUTE_NOT_FOUND_IN                   = "B4294";
    final public static String X_ATTRIBUTE_NOT_STRING_TYPE                = "B4295";
    final public static String X_DN_BASE_NOTMATCH                         = "B4296";
    final public static String X_MESSAGE_REF_GONE                         = "B4297";
    final public static String X_MESSAGE_MAYBE_REROUTED                   = "B4298";
    final public static String X_JAAS_CALLBACK_HANDLER_NOT_INITIALIZED    = "B4299";
    final public static String X_REMOTE_TXN_STATE_UPDATE_FAIL             = "B4300";
    final public static String X_REMOTE_TXN_UNKOWN                        = "B4301";
    final public static String X_CONSUMED_MSG_NOT_FOUND_IN_TXN            = "B4302";
    final public static String X_TXN_PRODUCER_MAX_MESSAGE_COUNT_EXCEEDED  = "B4303";
    final public static String X_TXN_CONSUMER_MAX_MESSAGE_COUNT_EXCEEDED  = "B4304";
    final public static String X_NULL_PACKET_FROM_REF  = "B4305";
    final public static String X_EXCEPTION_WRITE_PKT_ON_SEND_MSG_REMOTE  = "B4306";
    final public static String X_END_ON_FAILED_STATE  = "B4307";
	final public static String X_NON_DURABLE_SHARED_NO_CLIENTID = "B4308";
	final public static String X_DB_RETURN_EMPTY_TABLENAME = "B4309";
	final public static String X_A_BROKER_TAKINGOVER_THIS_BROKER = "B4310";
	final public static String X_CANNOT_TAKEOVER_SELF = "B4311";
	final public static String X_UNKNOWN_BROKERID = "B4312";
	final public static String X_NONHA_NO_TAKEOVER_SUPPORT = "B4313";
	final public static String X_LOCKFILE_CONTENT_FORMAT = "B4314";
	final public static String X_BAD_ADDRESS_BROKER_LIST = "B4315";
    final public static String X_READ_PASSWORD_FROM_STDIN = "B4316";
    final public static String X_CID_MUST_BE_SET_NOMASTER = "B4317";
    final public static String X_SHARECC_RECORD_TYPE_CORRUPT = "B4318";
    final public static String X_SHARECC_RESET_RECORD_UUID_CORRUPT = "B4319";
    final public static String X_SHARECC_FAIL_GET_SEQ_ON_INSERT = "B4320";
    final public static String X_SHARECC_TABLE_RESET = "B4321";
    final public static String X_SHARECC_RESETUID_MISMATCH_ON_JOIN = "B4322";
    final public static String X_REMOVE_MASTERBROKER_NOT_ALLOWED = "B4323";
    final public static String X_SHARECC_INSERT_RESET_RECORD_FAIL = "B4324";
    final public static String X_SHARECC_INSERT_RECORD_FAIL = "B4325";
    final public static String X_SHARECC_QUERY_RESET_RECORD_FAIL = "B4326";
    final public static String X_SHARECC_SET_RESET_RECORD_UUID_FAIL = "B4327";
    final public static String X_SHARECC_CLEAR_RESET_RECORD_FLAG_FAIL = "B4328";
    final public static String X_SHARECC_QUERY_RESET_RECORD_UUID_FAIL = "B4329";
    final public static String X_SHARECC_QUERY_RECORDS_FAIL = "B4330";
    final public static String X_SHARECC_QUERY_ALL_RECORDS_FAIL = "B4331";
    final public static String X_SHARECC_QUERY_SEQ_BY_UUID_FAIL = "B4332";
    final public static String X_CLUSTER_NO_MASTER_BROKER_REJECT_CHANGE_MASTER = "B4333";
    final public static String X_CLUSTER_WAIT_OPERATION_COMPLETION = "B4334";
    final public static String X_CLUSTER_MASTER_BROKER_NOT_READY_REJECT_CHANGE_MASTER = "B4335";
    final public static String X_CLUSTER_WAIT_CONFIG_CHANGE_OP_COMPLETE_TIMEOUT = "B4336";
    final public static String X_CLUSTER_CHANGE_MASTER_BROKER_MISMATCH = "B4337";
    final public static String X_CLUSTER_BROKER_NOT_CONNECTED_REJECT_CHANGE_MASTER = "B4338";
    final public static String X_CLUSTER_THIS_BROKER_NOT_MASTER_BROKER_REJECT_CHANGE_MASTER = "B4339";
    final public static String X_CLUSTER_CHANGE_MASTER_BROKER_IN_PROGRESS = "B4340";
    final public static String X_CLUSTER_BROKER_LINK_DOWN = "B4341";
    final public static String X_CLUSTER_WAIT_REPLY_TIMEOUT = "B4342";
    final public static String X_CLUSTER_CHANGE_MASTER_BROKER_VERSION_MISMATCH = "B4343";
    final public static String X_CLUSTER_UNABLE_PROCESS_NOT_MASTER_BROKER = "B4344";
    final public static String X_CLUSTER_NOT_CURRENT_MASTER_BROKER_REJECT = "B4345";
    final public static String X_CLUSTER_NOT_SYNC_WITH_MASTER_BROKER_REJECT = "B4346";
    final public static String X_CLUSTER_NEW_MASTER_PREPARE_FIRST_RECORD_NOT_RESET = "B4347";
    final public static String X_CLUSTER_FAIL_CLEANUP_INCOMPLETE_CONFIG_RECORDS_AFTER_PROCESS_FAILURE = "B4348";
    final public static String X_CLUSTER_NEW_MASTER_BROKER_NO_PREPARE = "B4349";
    final public static String X_CLUSTER_NEW_MASTER_BROKER_NOT_PREPARED_ONE = "B4350";
    final public static String X_CLUSTER_NO_SUPPORT_CHANGE_MASTER_BROKER_CMDLINE = "B4351";
    final public static String X_CLUSTER_NO_CHANGE_MASTER_BROKER_CMDLINE = "B4352";
    final public static String X_CLUSTER_NO_SYNC_WITH_MASTER_BROKER = "B4353";
    final public static String X_ADMIN_CHANGE_MASTER_NOT_FROM_JMSRA = "B4354";
    final public static String X_CLUSTER_NO_CMDLINE_MASTERBROKER_WHEN_DYNAMIC = "B4355";
    final public static String X_NO_SUPPORT_DYNAMIC_CHANGE_MASTER_BROKER = "B4356";
    final public static String X_CLUSTER_RECEIVED_NEW_MASTER_FROM_NON_MASTER = "B4357";
    final public static String X_INVALID_MAX_CONSUMER_COUNT = "B4358";
    final public static String X_INVALID_MAX_PRODUCER_COUNT = "B4359";
    final public static String X_SESSION_CLOSED = "B4360";
    final public static String X_UNABLE_PROCESS_MESSAGE_ACK = "B4361";
    final public static String X_COULD_NOT_DELETE_FILE = "B4362";

    final public static String X_RECEIVED_MSG_WITH_UNKNOWN_TID = "B4391";
    final public static String X_DURABLE_SUB_EXIST_IN_STORE_ALREADY = "B4392";

    /***************** End of message key constants *******************/

}
