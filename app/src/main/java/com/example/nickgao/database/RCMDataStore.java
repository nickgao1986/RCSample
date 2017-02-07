/**
 * Copyright (C) 2010-2013, RingCentral, Inc.
 * All Rights Reserved.
 */

package com.example.nickgao.database;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.provider.BaseColumns;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.logging.BUILD;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.network.RestApiErrorCodes;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.utils.RCMConstants;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * RC Database
 */
public final class RCMDataStore {
	private static final String TAG = "[RC]RCMDataStore";

	private RCMDataStore() {
	}

	public static final int PREVIOUS_DB_VERSION = 149;

	public static final int DB_VERSION = 19000;//17500;

	static final String DB_FILE = "rcm.db";

	public interface RCMColumns {
		String MAILBOX_ID = "mailboxId";                   //INTEGER (long)
		String DEFAULT_SORT_ORDER = "_ID ASC";
	}

	public interface MailboxStateEnum {

		int MAILBOX_STATE_ONLINE = 1;
		int MAILBOX_STATE_ONLINE_NO_CELLULAR = 2;
		int MAILBOX_STATE_OFFLINE = 3;
		int MAILBOX_STATE_OFFLINE_SUSPENDED = 4;
		int MAILBOX_STATE_LOGIN = 5;
		int MAILBOX_STATE_LOGOUT = 6;
	}

	public interface SyncStatusEnum {
		int SYNC_STATUS_NOT_LOADED = 0;
		int SYNC_STATUS_LOADING = 1;
		int SYNC_STATUS_LOADED = 2;
		int SYNC_STATUS_ERROR = 3;
	}

	public enum VMTranscriptionStatusEnum {
		Unknown,
		NotAvailable,
		InProgress,
		TimedOut,
		Completed,
		CompletedPartially,
		Failed
	}

	public final static String VMTranscriptionStatusEnumDataMigrationNotSet = "NotSet";
	public static VMTranscriptionStatusEnum getVMTranscriptionStatusEnumValue(String status) {
		try {
			return VMTranscriptionStatusEnumDataMigrationNotSet.equals(status) ? VMTranscriptionStatusEnum.Unknown : VMTranscriptionStatusEnum.valueOf(status);
		}catch (Throwable th) {
			return VMTranscriptionStatusEnum.NotAvailable;
		}
	}

	public static final class MailboxCurrentTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private MailboxCurrentTable() {
		}

		private static final MailboxCurrentTable sInstance = new MailboxCurrentTable();

		public static MailboxCurrentTable getInstance() {
			return sInstance;
		}

		public static final String POLLING_STATUS = "POLLING_STATUS";    // INTEGER (boolean)
		public static final String TABLE_NAME = "MAILBOX_CURRENT";
		public static final String HTTP_COOKIE = "HTTP_COOKIE";        // TEXT


		public static final long MAILBOX_ID_NONE = 0;            // ZERO never used as mailboxId

		public static final int POLLING_DISABLE = 0;
		public static final int POLLING_ENABLE = 1;            // 0: disable polling, 1: enable polling

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ POLLING_STATUS + " INTEGER,"
						+ HTTP_COOKIE + " TEXT"
						+ ");";

		private static final String INIT_TABLE_STMT =
				"INSERT INTO " + TABLE_NAME + " (" + MAILBOX_ID + ',' + POLLING_STATUS + "," + HTTP_COOKIE + ") "
						+ "VALUES (" + MAILBOX_ID_NONE + ',' + POLLING_ENABLE + "," + "''" + ")";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			db.execSQL(INIT_TABLE_STMT);
		}
	}

	public static final class DeviceAttribTable extends RCMDbTable implements BaseColumns {

		private DeviceAttribTable() {
		}

		private static final DeviceAttribTable sInstance = new DeviceAttribTable();

		static DeviceAttribTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "DEVICE_ATTRIB";

		public static final int FORCED_RESTART_NONE = 0;
		public static final int FORCED_RESTART_START = 1;
		public static final int FORCED_RESTART_FINISH = 2;
		public static final int FORCED_RESTART_NO_PERMISSION = 3;

		/* Columns */
		public static final String RCM_DEVICE_PHONE_NUMBER = "device_phone_number"; //TEXT
		public static final String RCM_DEVICE_IMSI = "device_IMSI"; // TEXT
		public static final String RCM_UI_LAUNCHED_AFTER_BOOT = "ui_launched_after_boot";     //INTEGER (boolean)
		public static final String RCM_FORCED_RESTART = "forced_restart";     //INTEGER (enum)
		public static final String RCM_TOS_ACCEPTED = "TOS_accepted";
		public static final String RCM_TOS911_ACCEPTED = "TOS911_accepted";
		public static final String RCM_DEVICE_ECHO_STATE = "device_echo_state"; // INTEGER
		public static final String RCM_DEVICE_ECHO_VALUE_SPEAKER = "device_echo_value_speaker"; // INTEGER
		public static final String RCM_DEVICE_ECHO_VALUE_EARPIECE = "device_echo_value_earpiece"; // INTEGER
		public static final String RCM_DEVICE_AUDIO_SETUP_WIZARD = "device_audio_setup_wizard"; // INTEGER
		public static final String RCM_LOG_CAT_ENABLED = "device_logcat_enabled"; // INTEGER



		/**
		 * Defines if EDGE network shall be included for 3G/4G calls for testing
		 */
		public static final String RCM_INCLUDE_EDGE_INTO_3G4G_VOIP = "edge_incl_mob_voip"; // INTEGER

		/**
		 * Defines if needed keeping partial lock when stack is active. ATTENTION: CAN CAUSE BATTERY DRAIN
		 */
		public static final String RCM_ALWAYS_CPU_PARTAIL_LOCK_WHEN_STACK_IS_ACTIVE = "always_partial_lock_for_stack"; // INTEGER

		/**
		 * app version code
		 */
		public static final String RCM_APP_VERSION = "app_version";        //INTEGER

		/**
		 * Defines if VoIP DSCP enabled.
		 */
		public static final String RCM_DSCP_ENABLED = "dscp_enabled";     //INTEGER

		//public static final String RCM_PHONE_SEVICE_STATE = "phone_service_state"; //INTEGER

		/**
		 * SIP TLS certificate.
		 */
		public static final String RCM_SIP_TLS_CERT = "sip_tls_cert";     //TEXT

		/**
		 * SIP TLS certificate version.
		 */
		public static final String RCM_SIP_TLS_CERT_VER = "sip_tls_cert_ver";     //INTEGER

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ RCM_DEVICE_PHONE_NUMBER + " TEXT,"
						+ RCM_DEVICE_IMSI + " TEXT,"
						+ RCM_UI_LAUNCHED_AFTER_BOOT + " INTEGER DEFAULT 1,"
						+ RCM_FORCED_RESTART + " INTEGER DEFAULT " + FORCED_RESTART_NONE + ","
						+ RCM_TOS_ACCEPTED + " INTEGER DEFAULT 0,"
						+ RCM_TOS911_ACCEPTED + " INTEGER DEFAULT 0,"
						+ RCM_DEVICE_ECHO_STATE + " TEXT DEFAULT \'1\',"
						+ RCM_DEVICE_ECHO_VALUE_SPEAKER + " INTEGER DEFAULT " + RCMConstants.DEFAULT_AEC_DELAY_VALUE + ","
						+ RCM_DEVICE_ECHO_VALUE_EARPIECE + " INTEGER DEFAULT " + RCMConstants.DEFAULT_AEC_DELAY_VALUE + ","
						+ RCM_DEVICE_AUDIO_SETUP_WIZARD + " INTEGER DEFAULT 0,"
						+ RCM_LOG_CAT_ENABLED + " INTEGER DEFAULT 0,"
						+ RCM_INCLUDE_EDGE_INTO_3G4G_VOIP + " INTEGER DEFAULT 0,"

						+ RCM_APP_VERSION + " INTEGER DEFAULT 0,"

						+ RCM_DSCP_ENABLED + " INTEGER DEFAULT 1,"
						// + RCM_PHONE_SEVICE_STATE + " INTEGER DEFAULT " + android.telephony.ServiceState.STATE_IN_SERVICE + ","
						+ RCM_SIP_TLS_CERT + " TEXT,"
						+ RCM_SIP_TLS_CERT_VER + " INTEGER DEFAULT 0"
						+ ");";



		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			//db.execSQL(INIT_TABLE_STMT);
		}
	}


	public static final class UserCredentialsTable extends RCMDbTable implements BaseColumns {

		private UserCredentialsTable() {
		}

		private static final UserCredentialsTable sInstance = new UserCredentialsTable();

		static UserCredentialsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "USER_CREDENTIALS";

		/* Columns */
		public static final String RCM_LOGIN_NUMBER = "login_number";        // TEXT
		public static final String RCM_LOGIN_EXT = "login_ext";              // TEXT
		public static final String RCM_PASSWORD = "password";               // TEXT
		public static final String JEDI_LOGIN_IP_ADDRESS = "login_ip_address";       // TEXT
		public static final String JEDI_LOGIN_REQUEST_ID = "login_request_id";       // INTEGER
		public static final String JEDI_LOGIN_START_TIME = "login_start_time";       // INTEGER
		public static final String JEDI_LOGIN_HASH = "login_hash";             // TEXT
		public static final String JEDI_LOGIN_COOKIE = "login_cookie";           // TEXT
		public static final String JEDI_USER_ID = "userId";                 // INTEGER (long)
		public static final String RCM_LOGIN_DATE = "login_date";                // INTEGER (long), login time
		public static final String RCM_TIER_ID = "tier_id";
		public static final String RCM_SIP_PASSWORD = "sip_password";               // TEXT
		public static final String RCM_SIP_USERNAME = "sip_username";               //TEXT

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ RCM_LOGIN_NUMBER + " TEXT,"
						+ RCM_LOGIN_EXT + " TEXT,"
						+ RCM_PASSWORD + " TEXT,"
						+ JEDI_LOGIN_IP_ADDRESS + " TEXT,"
						+ JEDI_LOGIN_REQUEST_ID + " INTEGER,"
						+ JEDI_LOGIN_START_TIME + " INTEGER,"
						+ JEDI_LOGIN_HASH + " TEXT,"
						+ JEDI_LOGIN_COOKIE + " TEXT,"
						+ JEDI_USER_ID + " INTEGER,"
						+ RCM_TIER_ID + " INTEGER,"
						+ RCM_SIP_PASSWORD + " TEXT,"
						+ RCM_SIP_USERNAME + " TEXT,"
						+ RCM_LOGIN_DATE + " INTEGER DEFAULT " + SystemClock.elapsedRealtime()
						+ ");";

		private static final String INIT_TABLE_STMT =
				"INSERT INTO " + TABLE_NAME
						+ " (" + RCM_LOGIN_NUMBER + ','
						+ RCM_LOGIN_EXT + ','
						+ RCM_PASSWORD + ','
						+ JEDI_LOGIN_IP_ADDRESS + ','
						+ JEDI_LOGIN_REQUEST_ID + ','
						+ JEDI_LOGIN_START_TIME + ','
						+ JEDI_LOGIN_HASH + ','
						+ JEDI_LOGIN_COOKIE + ','
						+ JEDI_USER_ID + ","
						+ RCM_TIER_ID + ","
						+ RCM_SIP_PASSWORD + ","
						+ RCM_LOGIN_DATE
						+ ") "
						+ "VALUES (NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			db.execSQL(INIT_TABLE_STMT);
		}
	}


	public static final class ServiceInfoTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private ServiceInfoTable() {
		}

		private static final ServiceInfoTable sInstance = new ServiceInfoTable();

		static ServiceInfoTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "SERVICE_INFO";

		/* Columns */
		public static final String REST_URI = "REST_URI";                //TEXT
		public static final String REST_SERVER_VERSION = "REST_SERVER_VERSION";    //TEXT
		public static final String REST_SERVER_REVISION = "REST_SERVER_REVISION";    //TEXT
		public static final String REST_API_URI = "REST_API_URI";            //TEXT
		public static final String REST_API_VERSION_STRING = "REST_API_VERSION_STRING";//TEXT
		public static final String REST_API_RELEASE_DATA = "REST_API_RELEASE_DATA";    //TEXT
		public static final String REST_API_URI_STRING = "REST_API_URI_STRING";    //TEXT

        /* Columns */
//        public static final String JEDI_API_VERSION = "api_version"; //TEXT

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ REST_URI + " TEXT,"
						+ REST_SERVER_VERSION + " TEXT,"
						+ REST_SERVER_REVISION + " TEXT,"
						+ REST_API_URI + " TEXT,"
						+ REST_API_VERSION_STRING + " TEXT,"
						+ REST_API_RELEASE_DATA + " TEXT,"
						+ REST_API_URI_STRING + " TEXT"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class ConferenceInfoTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private ConferenceInfoTable() {
		}

		private static final ConferenceInfoTable sInstance = new ConferenceInfoTable();

		static ConferenceInfoTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CONFERENCE_INFO";

		/* Columns */
		public static final String REST_URI = "uri";                // Text
		public static final String REST_PHONE_NUMBER = "phone_number";        // Text
		public static final String REST_HOST_CODE = "host_code";            // Text
		public static final String REST_PARTICIPANT_CODE = "participant_code";    // Text

		public static final String REST_LOCAL_NUMBER = "local_phoneNumber";    // Text
		public static final String REST_COUNTRY_URI = "country_uri";    // Text
		public static final String REST_COUNTRY_ID = "country_id";    // Text
		public static final String REST_COUNTRY_NAME = "country_name";    // Text
		public static final String REST_COUNTRY_ISOCODE = "country_isoCode";    // Text
		public static final String REST_COUNTRY_CALLINGCODE = "country_callingCode";    // Text

		public static final String SHOW_CHOOSE_DIAL_IN_ICON = "show_choose_dial_in_icon";    // Text
		public static final String REST_LOCATION = "location";    // Text
		public static final String REST_STATE_INFO = "state_info";    // Text
		public static final String REST_HASGREETING = "hasGreeting";    // Integer
		public static final String REST_DEFAULT = "isdefault";    // Integer
		public static final String REST_ALLOW_JOIN_BEFORE_HOST = "allow_join_before_host"; // Integer
		public static final String TAP_TO_JOIN_URI = "Tap_to_join_uri"; // Integer
		public static final int IS_DEFAULT_NUMBER = 1;
		public static final int IS_NOT_DEFAULT_NUMBER = 0;

		public static final int IS_SHOW_CHOOSE_DIAL_IN_ICON = 1;
		public static final int IS_NOT_SHOW_CHOOSE_DIAL_IN_ICON = 0;

		public static final String REST_MODE="mode";// RCC/FCC mode

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

		private static final String CREATE_TABLE_STMT = "CREATE TABLE IF NOT EXISTS " +
				TABLE_NAME + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MAILBOX_ID + " INTEGER,"
				+ REST_URI + " TEXT,"
				+ REST_HOST_CODE + " TEXT,"
				+ TAP_TO_JOIN_URI + " TEXT,"
				+ REST_PHONE_NUMBER + " TEXT,"
				+ REST_PARTICIPANT_CODE + " TEXT,"
				+ REST_LOCAL_NUMBER + " TEXT,"
				+ REST_COUNTRY_URI + " TEXT,"
				+ REST_COUNTRY_ID + " TEXT,"
				+ REST_COUNTRY_NAME + " TEXT,"
				+ REST_COUNTRY_ISOCODE + " TEXT,"
				+ REST_COUNTRY_CALLINGCODE + " TEXT,"
				+ REST_LOCATION + " TEXT,"
				+ REST_HASGREETING + " INTEGER DEFAULT 0,"
				+ REST_DEFAULT + " INTEGER DEFAULT 0,"
				+ SHOW_CHOOSE_DIAL_IN_ICON + " INTEGER DEFAULT 0,"
				+ REST_STATE_INFO + " TEXT,"
				+ REST_ALLOW_JOIN_BEFORE_HOST + " INTEGER DEFAULT -1,"
				+ REST_MODE + " TEXT"
				+ ");";
	}

	public static final class AccountInfoTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private AccountInfoTable() {
		}

		private static final AccountInfoTable sInstance = new AccountInfoTable();

		static AccountInfoTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "ACCOUNT_INFO";

		public static final int SERVICE_VERSION_4 = 1;  //JEDI 4.0
		public static final int SERVICE_VERSION_5 = 2;  //JEDI 5.0.x
		public static final int SERVICE_VERSION_6 = 3;  //JEDI 5.10.x

		public static final String ACCESS_LEVEL_ADMIN = "Admin";
		public static final String ACCESS_LEVEL_USER = "User";
		public static final String ACCESS_LEVEL_VIEW = "View";

		public static final String AGENT_STATUS_OFFLINE = "Offline";
		public static final String AGENT_STATUS_ONLINE = "Online";

		public static final int IS_FIRST_RECORD = 0;
		public static final int IS_NOT_FIRST_RECORD = 1;

		/* DND status values as defined in JEDI iPhone API (5.0.x only) */
		public static final String DND_STATUS_TAKE_ALL_CALLS = "TakeAllCalls";
		public static final String DND_STATUS_DO_NOT_ACCEPT_DEPARTMENT_CALLS = "DoNotAcceptDepartmentCalls";
		public static final String DND_STATUS_TAKE_DEPARTMENT_CALLS_ONLY = "TakeDepartmentCallsOnly";
		public static final String DND_STATUS_DO_NOT_ACCEPT_ANY_CALLS = "DoNotAcceptAnyCalls";

		public static final String EXTENSION_TYPE_UNKNOWN = "Unknown";
		public static final String EXTENSION_TYPE_USER = "User";
		public static final String EXTENSION_TYPE_DEPARTMENT = "Department";
		public static final String EXTENSION_TYPE_ANNOUNCEMENT = "Announcement";
		public static final String EXTENSION_TYPE_VOICEMAIL = "Voicemail";
		public static final String EXTENSION_TYPE_VIRTUAL_USER = "VirtualUser";
		public static final String EXTENSION_TYPE_DIGITAL_USER = "DigitalUser";
		public static final String EXTENSION_TYPE_SHARED_LINES_GROUP = "SharedLinesGroup";

		/* Account tier type (Tier Service type, e.g. "RCMobile", "RCOffice", "RCVoice", "RCFax"). Valid from 5.0.x. */
		public static final String TIER_SERVICE_TYPE_RCMOBILE = "RCMobile";
		public static final String TIER_SERVICE_TYPE_RCOFFICE = "RCOffice";
		public static final String TIER_SERVICE_TYPE_RCVOICE = "RCVoice";
		public static final String TIER_SERVICE_TYPE_RCFAX = "RCFax";
		public static final String TIER_SERVICE_TYPE_RCFAX_ANOTHER_NAME = "Fax500";
		/* Setup Wizard state */
		public static final String SETUP_WIZARD_STATE_NOT_STARTED = "NotStarted";
		public static final String SETUP_WIZARD_STATE_INCOMPLETE = "Incomplete";
		public static final String SETUP_WIZARD_STATE_COMPLETED = "Completed";

		/* Columns */
		public static final String JEDI_USER_ID = "USER_ID";                        // INTEGER (long)
		public static final String JEDI_ACCOUNT_NUMBER = "ACCOUNT_NUMBER";                // TEXT
		public static final String JEDI_EMAIL = "EMAIL";                            // TEXT
		public static final String JEDI_PIN = "PIN";                            // TEXT
		public static final String JEDI_FIRST_NAME = "FIRST_NAME";                    // TEXT
		public static final String JEDI_LAST_NAME = "LAST_NAME";                        // TEXT
		public static final String JEDI_AGENT = "AGENT";                        // INTEGER (boolean)
		public static final String JEDI_AGENT_STATUS = "AGENT_STATUS";                // TEXT (enum)
		public static final String JEDI_DND = "DND";                            // INTEGER (boolean) , 4.x only
		public static final String JEDI_DND_STATUS = "DND_STATUS";                    // TEXT (enum)   , 5.0.x only
		public static final String JEDI_DND_IS_CLOSED = "JEDI_DND_IS_CLOSED";             // INTEGER (enum)
		public static final String JEDI_FREE = "FREE";                        // INTEGER (boolean)
		public static final String JEDI_ACCESS_LEVEL = "ACCESS_LEVEL";                // TEXT
		public static final String JEDI_EXTENSION_TYPE = "EXTENSION_TYPE";                // TEXT
		public static final String JEDI_SYSTEM_EXTENSION = "SYSTEM_EXTENSION";                // INTEGER (boolean)
		public static final String JEDI_SERVICE_VERSION = "SERVICE_VERSION";                // INTEGER
		public static final String JEDI_TIER_SETTINGS = "TIER_SETTINGS";                    // INTEGER (long)
		public static final String JEDI_TIER_SERVICE_TYPE = "TIER_SERVICE_TYPE";            // TEXT
		public static final String JEDI_EXT_MOD_COUNTER = "GLOBAL_EXT_MOD_COUNTER";            // INTEGER (long)
		public static final String JEDI_EXT_MOD_COUNTER_TEMP = "GLOBAL_EXT_MOD_COUNTER_TEMP";    // INTEGER (long)
		public static final String JEDI_MSG_MOD_COUNTER = "MSG_MOD_COUNTER";                // INTEGER (long)
		public static final String JEDI_MSG_MOD_COUNTER_TEMP = "MSG_MOD_COUNTER_TEMP";            // INTEGER (long)
		public static final String JEDI_SETUP_WIZARD_STATE = "SETUP_WIZARD_STATE";            // TEXT (enum)
		public static final String JEDI_EXPRESS_SETUP_MOBILE_URL = "EXPRESS_SETUP_MOBILE_URL";        // TEXT
		public static final String JEDI_TRIAL_EXPIRATION_STATE = "TRIAL_EXPIRATION_STATE";            // TEXT
		public static final String JEDI_DAYS_TO_EXPIRE = "DAYS_TO_EXPIRE";                    // INTEGER
		public static final String JEDI_BRAND_ID = "BRAND_ID";                        // TEXT
		public static final String JEDI_BRAND_NAME = "BRAND_NAME";                     // TEXT

		public static final String HTTPREG_INSTANCE_ID = "HTTPREG_INSTANCE_ID";            // TEXT
		public static final String RCM_LOGIN_NUMBER = "LOGIN_NUMBER";                // TEXT
		public static final String RCM_LOGIN_EXT = "LOGIN_EXT";                        // TEXT
		public static final String RCM_PASSWORD = "PASSWORD";                        // TEXT
		public static final String RCM_CALLER_ID = "CALLER_ID";                        // _ID in CallerIDs table or actual number????
		public static final String RCM_DEFAULT_CALLER_ID = "DEFAULT_CALLER_ID";    //TEXT
		public static final String RCM_RINGOUT_MODE = "RINGOUT_MODE";                // INTEGER ("My Android", "Another phone")
		public static final String RCM_RINGOUT_ANOTHER_PHONE = "RINGOUT_ANOTHER_PHONE";            // TEXT
		public static final String RCM_CUSTOM_NUMBER = "CUSTOM_NUMBER";                    // TEXT
		public static final String RCM_CONFIRM_CONNECTION = "CONFIRM_CONNECTION";            // INTEGER (boolean)
		public static final String RCM_LAST_CALL_NUMBER = "LAST_CALL_NUMBER";            // TEXT
		public static final String RCM_MAILBOX_STATE = "MAILBOX_STATE";                    // INTEGER ("online", "online_no_cellular", "offline", "offline_suspended", "login", "logout")
		public static final String RCM_LAST_SYNC = "LAST_SYNC";                        // INTEGER (long): Time of last (successful) sync
		public static final String RCM_LAST_LOADED_MSG_ID = "LAST_LOADED_MSG_ID";            // INTEGER (long)
		public static final String RCM_LAST_COMPLETE_SETUP_REQ = "LAST_COMPLETE_SETUP_REQUEST";    // INTEGER (long) : Time of last request ro proceed with setup
		public static final String RCM_LAST_EXPIRED_REQ = "LAST_EXPIRED_REQUEST";            // INTEGER (long) : Time of last notification of expiration
		public static final String RCM_VOIP_SETTINGS = "VOIP_SETTINGS";                // INTEGER (long)

		public static final String RCM_POLLING_STATE = "RCM_POLLING_STATE";                                // boolean
		public static final String RCM_LAST_MESSAGE_SENT = "RCM_LAST_MESSAGE_SENT";                            // INTEGER (long)
		public static final String RCM_LAST_POLL_TIME = "RCM_LAST_POLL_TIME";                            // INTEGER (long)
		public static final String RCM_LAST_CALL_LOG_POLL_TIME = "RCM_LAST_CALL_LOG_POLL_TIME";                    // INTEGER (long)
		public static final String RCM_LAST_ACCOUNT_POLL_TIME = "RCM_LAST_ACCOUNT_POLL_TIME";                    // INTEGER (long)
		public static final String RCM_LAST_ACCOUNT_API_POLL_TIME = "RCM_LAST_ACCOUNT_API_POLL_TIME";                    // INTEGER (long)

		public static final String RCM_LAST_CONFERENCE_POLL_TIME = "RCM_LAST_CONFERENCE_POLL_TIME";                    // INTEGER(long)
		public static final String RCM_LAST_API_VERSION_POLL_TIME = "RCM_LAST_API_VERSION_POLL_TIME";                // INTEGER(long)
		public static final String RCM_LAST_REST_SERVICE_INFO_POLL_TIME = "RCM_LAST_REST_SERVICE_INFO_POLL_TIME";            // INTEGER (long)
		public static final String RCM_LAST_REST_PHONE_NUMBERS_POLL_TIME = "RCM_LAST_REST_PHONE_NUMBERS_POLL_TIME";            // INTEGER (long)
		public static final String RCM_LAST_REST_BLOCKED_NUMBERS_POLL_TIME = "RCM_LAST_REST_BLOCKED_NUMBERS_POLL_TIME";        // INTEGER (long)
		public static final String RCM_LAST_REST_CALL_FLIP_POLL_TIME = "RCM_LAST_REST_CALL_FLIP_POLL_TIME";                // INTEGER (long)
		public static final String RCM_LAST_REST_ADMIN_CALL_LOG_POLL_TIME = "RCM_LAST_REST_ADMIN_CALL_LOG_POLL_TIME";            // INTEGER (long)
		public static final String RCM_LAST_REST_SPECIAL_NUMBERS_POLL_TIME = "RCM_LAST_REST_SPECIAL_NUMBERS_POLL_TIME";        // INTEGER (long)
		public static final String RCM_LAST_REST_EXTENSION_LIST_POLL_TIME = "RCM_LAST_REST_EXTENSION_LIST_POLL_TIME";        // INTEGER (long)
		public static final String RCM_LAST_GET_CLIENT_INFO_TIME = "RCM_LAST_GET_CLIENT_INFO_TIME";                    // INTEGER(long)
		public static final String RCM_LAST_REST_DIALING_PLANS_POLL_TIME = "RCM_LAST_REST_DIALING_PLANS_POLL_TIME";            // INTEGER (long)
		public static final String RCM_LAST_REST_PERMISSIONS_POLL_TIME = "RCM_LAST_REST_PERMISSIONS_POLL_TIME";                    // INTEGER(long)
		public static final String RCM_LAST_REST_BLF_LIST_POLL_TIME = "RCM_LAST_BLF_LIST_POLL_TIME";                    // INTEGER(long)

		public static final String RCM_URI_SIP_PROVIDER = "URI_SIP_PROVIDER";                    // TEXT
		public static final String RCM_URI_SIP_OUTBOUND_PROXY = "URI_SIP_OUT_BOUND_PROXY";            // TEXT
		public static final String RCM_URI_PRE_CALL_OPTIONS_PROXY = "URI_PRE_CALL_OPTIONS_PROXY";
		public static final String RCM_SIP_TRANSPORT_TYPE = "TRANSPORT_TYPE";               // TEXT
		public static final String RCM_VOIP_WHATS_NEW = "VOIP_WHATS_NEW_DIALOG";            // INTEGER
		public static final String RCM_VOIP_HTTPREG_SIPFLAGS = "HTTP_REG_SIP_FLAGS";                // INTEGER (long)
		public static final String RCM_VOIP_COUNTRY_BLOCKING = "VOIP_COUNTRY_BLOCKING";             // INTEGER (boolean)
		public static final String RCM_VOIP_UNAVAILABLE = "VOIP_UNAVAILABLE";                       // INTEGER (boolean)

		public static final String RCM_MESSAGES_LAST_UPDATED_TIMESTAMP = "RCM_MESSAGES_LAST_UPDATED_TIMESTAMP";    // INTEGER (long)
		public static final String RCM_CALL_LOG_LAST_UPDATED_TIMESTAMP = "RCM_CALL_LOG_LAST_UPDATED_TIMESTAMP";    // INTEGER (long)
		public static final String RCM_MAX_TEXT_MESSAGE_ID_IN_LAST_SYNC = "RCM_TEXT_MESSAGES_MAX_ID_IN_LIST_SYNC";  // INTEGER (long)
		public static final String RCM_ADMIN_CALL_LOG_LAST_UPDATED_TIMESTAMP = "RCM_ADMIN_CALL_LOG_LAST_UPDATED_TIMESTAMP";  //INTEGER (long)

		public static final String EXTENSION_GROUPS_TYPE = "EXTENSION_GROUPS_TYPE";
		public static final String DEFAULT_TEXT_DID = "DEFAULT_TEXT_DID";
		public static final String DEFAULT_FAX_DID = "DEFAULT_FAX_DID";
		public static final String DEFAULT_CALL_LOG_TYPE = "DEFAULT_CALL_LOG_TYPE";

		public static final String RCM_FIRST_RECORDING = "RCM_FIRST_RECORDING";
		public static final String RCM_CREATE_MESSAGE_TIP_DIALOG = "CREATE_MESSAGE_TIP_DIALOG";    // Boolean

		public static final String LANGUAGE_CONFLICT_UPGRADE_STATE = "LANGUAGE_CONFLICT_UPGRADE_STATE"; //Boolean
		public static final String CBC_ENCRYPTION_MIGRATED = "CBC_ENCRYPTION_MIGRATED"; //Boolean
        /* Columns */

		/* RingOut mode enumeration */
		public static final int RINGOUT_MODE_MY_ANDROID = RCMConstants.RINGOUT_MODE_MY_ANDROID;
		public static final int RINGOUT_MODE_ANOTHER_PHONE = RCMConstants.RINGOUT_MODE_ANOTHER_PHONE;

		/* Extension Groups Type */
		public static final int TYPE_EXTENSION_NONE_GROUPS = 0;
		public static final int TYPE_EXTENSION_USERS_GROUPS = 1;
		public static final int TYPE_EXTENSION_DEPARTMENT_GROUPS = 2;
		public static final int TYPE_EXTENSION_ALL_GROUPS = 3;

		public static final int DND_ISNOT_CLOSED = 0;
		public static final int DND_IS_CLOSED = 1;

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ JEDI_USER_ID + " INTEGER,"
						+ JEDI_ACCOUNT_NUMBER + " TEXT,"
						+ JEDI_EMAIL + " TEXT,"
						+ JEDI_PIN + " TEXT,"
						+ JEDI_FIRST_NAME + " TEXT,"
						+ JEDI_LAST_NAME + " TEXT,"
						+ JEDI_AGENT + " INTEGER,"
						+ JEDI_AGENT_STATUS + " TEXT,"
						+ JEDI_DND + " INTEGER,"
						+ JEDI_DND_STATUS + " TEXT,"
						+ JEDI_DND_IS_CLOSED + " INTEGER DEFAULT " + DND_ISNOT_CLOSED + ","
						+ JEDI_FREE + " INTEGER,"
						+ JEDI_ACCESS_LEVEL + " TEXT,"
						+ JEDI_EXTENSION_TYPE + " TEXT,"
						+ JEDI_SYSTEM_EXTENSION + " INTEGER,"
						+ JEDI_SERVICE_VERSION + " INTEGER,"
						+ JEDI_TIER_SETTINGS + " INTEGER,"
						+ JEDI_TIER_SERVICE_TYPE + " TEXT,"
						+ JEDI_EXT_MOD_COUNTER + " INTEGER,"
						+ JEDI_EXT_MOD_COUNTER_TEMP + " INTEGER,"
						+ JEDI_MSG_MOD_COUNTER + " INTEGER,"
						+ JEDI_MSG_MOD_COUNTER_TEMP + " INTEGER,"
						+ JEDI_SETUP_WIZARD_STATE + " TEXT,"
						+ JEDI_EXPRESS_SETUP_MOBILE_URL + " TEXT,"
						+ JEDI_TRIAL_EXPIRATION_STATE + " TEXT,"
						+ JEDI_DAYS_TO_EXPIRE + " INTEGER,"
						+ JEDI_BRAND_ID + " TEXT,"
						+ JEDI_BRAND_NAME + " TEXT,"
						+ HTTPREG_INSTANCE_ID + " TEXT,"
						+ RCM_LOGIN_NUMBER + " TEXT,"
						+ RCM_LOGIN_EXT + " TEXT,"
						+ RCM_PASSWORD + " TEXT,"
						+ RCM_CALLER_ID + " INTEGER,"
						+ RCM_DEFAULT_CALLER_ID + " TEXT,"
						+ RCM_RINGOUT_MODE + " INTEGER,"
						+ RCM_RINGOUT_ANOTHER_PHONE + " TEXT,"
						+ RCM_CUSTOM_NUMBER + " TEXT,"
						+ RCM_CONFIRM_CONNECTION + " INTEGER,"
						+ RCM_LAST_CALL_NUMBER + " TEXT,"
						+ RCM_MAILBOX_STATE + " INTEGER,"
						+ RCM_LAST_SYNC + " INTEGER,"
						+ RCM_LAST_LOADED_MSG_ID + " INTEGER,"
						+ RCM_LAST_COMPLETE_SETUP_REQ + " INTEGER,"
						+ RCM_LAST_EXPIRED_REQ + " INTEGER,"
						+ RCM_LAST_GET_CLIENT_INFO_TIME + " INTEGER,"
						+ RCM_POLLING_STATE + " INTEGER DEFAULT 1,"
						+ RCM_LAST_MESSAGE_SENT + " INTEGER DEFAULT 0,"
						+ RCM_LAST_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_ADMIN_CALL_LOG_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_CALL_LOG_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_URI_SIP_PROVIDER + " TEXT DEFAULT \'" + BUILD.URI.URI_SIP_PROVIDER + "\' ,"
						+ RCM_URI_SIP_OUTBOUND_PROXY + " TEXT DEFAULT \'" + BUILD.URI.URI_SIP_OUTBOUND_PROXY + "\' ,"
						+ RCM_URI_PRE_CALL_OPTIONS_PROXY + " TEXT DEFAULT \'" + BUILD.URI.URI_SIP_OUTBOUND_PROXY + "\' ,"
						+ RCM_SIP_TRANSPORT_TYPE + " TEXT,"
						+ RCM_VOIP_WHATS_NEW + " INTEGER DEFAULT 0,"
						+ RCM_VOIP_HTTPREG_SIPFLAGS + " INTEGER DEFAULT 0,"
						+ RCM_VOIP_COUNTRY_BLOCKING + " INTEGER DEFAULT 0,"
						+ RCM_VOIP_UNAVAILABLE + " INTEGER DEFAULT 0,"
						+ RCM_MESSAGES_LAST_UPDATED_TIMESTAMP + " INTEGER DEFAULT 0,"
						+ RCM_CALL_LOG_LAST_UPDATED_TIMESTAMP + " INTEGER DEFAULT 0,"
						+ RCM_ADMIN_CALL_LOG_LAST_UPDATED_TIMESTAMP + " INTEGER DEFAULT 0,"
						+ RCM_LAST_ACCOUNT_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_ACCOUNT_API_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_CONFERENCE_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_API_VERSION_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_SERVICE_INFO_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_PHONE_NUMBERS_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_BLOCKED_NUMBERS_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_CALL_FLIP_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_SPECIAL_NUMBERS_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_DIALING_PLANS_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_EXTENSION_LIST_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_MAX_TEXT_MESSAGE_ID_IN_LAST_SYNC + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_PERMISSIONS_POLL_TIME + " INTEGER DEFAULT 0,"
						+ RCM_LAST_REST_BLF_LIST_POLL_TIME + " INTEGER DEFAULT 0,"
						+ EXTENSION_GROUPS_TYPE + " INTEGER DEFAULT 3,"
						+ DEFAULT_TEXT_DID + " INTEGER,"
						+ DEFAULT_FAX_DID + " INTEGER,"
						+ RCM_FIRST_RECORDING + " INTEGER,"
						+ DEFAULT_CALL_LOG_TYPE + " INTEGER DEFAULT " + CallLogTable.TYPE_PERSONAL + ","
						+ RCM_CREATE_MESSAGE_TIP_DIALOG + " INTEGER DEFAULT 0,"
						+ LANGUAGE_CONFLICT_UPGRADE_STATE + " INTEGER DEFAULT 1,"
						+ CBC_ENCRYPTION_MIGRATED + " INTEGER DEFAULT 0"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}


	public static final class ServiceExtensionInfoTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private ServiceExtensionInfoTable() {
		}

		private static final ServiceExtensionInfoTable sInstance = new ServiceExtensionInfoTable();

		static ServiceExtensionInfoTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "SERVICE_EXTENSION_INFO";

		/* Columns */
		public static final String REST_URI = "REST_URI";                           // TEXT
		public static final String REST_SMS_ENABLED = "SMS_ENABLED";                        // INTEGER (boolean)
		public static final String REST_PAGER_ENABLED = "PAGER_ENABLED";                        // INTEGER (boolean)
		public static final String REST_PRESENCE_ENABLED = "PRESENCE_ENABLED";                   // INTEGER (boolean)
		public static final String REST_RINGOUT_ENABLED = "RINGOUT_ENABLED";                    // INTEGER (boolean)
		public static final String REST_DND_ENABLED = "DND_ENABLED";                        // INTEGER (boolean)
		public static final String REST_FAX_ENABLED = "FAX_ENABLED";                        // INTEGER (boolean)
		public static final String REST_VOICEMAIL_ENABLED = "VOICEMAIL_ENABLED";                  // INTEGER (boolean)
		public static final String REST_VOIP_CALLING_ENABLED = "VOIPCALLING_ENABLED";                // INTEGER (boolean)
		public static final String REST_CONFERENCING_ENABLED = "CONFERENCING_ENABLED";                // INTEGER (boolean)
		public static final String REST_VIDEO_ENABLED = "REST_VIDEO_ENABLED";                    // INTEGER (boolean)

		public static final String REST_SALES_FORCE_ENABLED = "REST_SALES_FORCE_ENABLED";                    // INTEGER (boolean)
		public static final String REST_INTERCOM_ENABLED = "REST_INTERCOM_ENABLED";                        // INTEGER (boolean)
		public static final String REST_PAGING_ENABLED = "REST_PAGING_ENABLED";                        // INTEGER (boolean)

		public static final String REST_CALL_PARK_ENABLED = "REST_CALL_PARK_ENABLED";                        // INTEGER (boolean)
		public static final String REST_CALL_RECORDING_ENABLED = "REST_CALL_RECORDING_ENABLED";                // INTEGER (boolean)
		public static final String REST_CALL_FORWARDING_ENABLED = "REST_CALL_FORWARDING_ENABLED";                // INTEGER (boolean)
		public static final String REST_SMSRECEIVING_ENABLED = "REST_SMSRECEIVING_ENABLED";                    // INTEGER (boolean)
		public static final String REST_PAGERRECEIVING_ENABLED = "REST_PAGERRECEIVING_ENABLED";                // INTEGER (boolean)
		public static final String REST_FAXRECEIVING_ENABLED = "REST_FAXRECEIVING_ENABLED";                    // INTEGER (boolean)
		public static final String REST_HIPAACOMPLIANCE_ENABLED = "REST_HIPAACOMPLIANCE_ENABLED";                // INTEGER (boolean)
		public static final String REST_FREESOFTPHONELINES_ENABLED = "REST_FREESOFTPHONELINES_ENABLED";            // INTEGER (boolean)
		public static final String REST_HDVOICE_ENABLED = "REST_HDVOICE_ENABLED";
		public static final String REST_RCTEAMS_ENABLED="RCTEAMS_ENABLED";// INTEGER (boolean)
		public static final String REST_USERMANAGEMENT_ENABLED="REST_USERMANAGEMENT_ENABLED";// INTEGER (boolean)
		public static final String REST_CALENDAR_ENABLED="REST_CALENDAR_ENABLED";// INTEGER (boolean)

		public static final String EXTENSION_INFO_ID = "EXTENSION_INFO_ID";                // TEXT
		public static final String EXTENSION_INFO_EXT_NUMBER = "EXTENSION_INFO_EXT_NUMBER";        // TEXT
		public static final String EXTENSION_INFO_FIRST_NAME = "EXTENSION_INFO_FIRST_NAME";        // TEXT
		public static final String EXTENSION_INFO_LAST_NAME = "EXTENSION_INFO_LAST_NAME";        // TEXT
		public static final String EXTENSION_INFO_COMPANY = "EXTENSION_INFO_COMPANY";            // TEXT
		public static final String EXTENSION_INFO_EMAIL = "EXTENSION_INFO_EMAIL";            // TEXT
		public static final String EXTENSION_INFO_FULL_NAME = "EXTENSION_INFO_FULL_NAME";        // TEXT
		public static final String EXTENSION_INFO_TYPE = "EXTENSION_INFO_TYPE";            // INTEGER (enum EXTENSION_INFO_TYPE_*)
		public static final String EXTENSION_INFO_STATUS = "EXTENSION_INFO_STATUS";            // INTEGER (enum EXTENSION_INFO_STATUS_*)

		public static final String REST_REFRESH_TOKEN = "REST_REFRESH_TOKEN";                 // TEXT
		public static final String REST_INTERNATIONAL_CALLING_ENABLED = "INTERNATIONAL_CALLING_ENABLED";        // INTEGER (boolean)

		public static final String EXTENSION_INFO_ISO_CODE = "EXTENSION_INFO_ISO_CODE";            // TEXT
		public static final String REST_REPORT_ENABLED = "REST_REPORT_ENABLED";                    // INTEGER (boolean)
		public static final String SINGLE_EXTENSION_MODE = "SINGLE_EXTENSION_MODE";                    // INTEGER (boolean)
		public static final String REST_IS_ADMIN = "REST_IS_ADMIN";                    // INTEGER (boolean)
		//language
		public static final String USER_SERVER_LANGUAGE_ID = "USER_SERVER_LANGUAGE_ID";//TEXT
		public static final String USER_SERVER_LANGUAGE_NAME = "USER_SERVER_LANGUAGE_NAME";//TEXT
		public static final String USER_SERVER_LANGUAGE_LOCALE = "USER_SERVER_LANGUAGE_LOCALE";//TEXT

		public static final String USER_GREETING_LANGUAGE_ID = "USER_GREETING_LANGUAGE_ID";//TEXT
		public static final String USER_GREETING_LANGUAGE_NAME = "USER_GREETING_LANGUAGE_NAME";//TEXT
		public static final String USER_GREETING_LANGUAGE_LOCALE = "USER_GREETING_LANGUAGE_LANGUAGE_LOCALE";//TEXT

		public static final String USER_FORMATTING_LANGUAGE_ID = "USER_FORMATTING_LANGUAGE_ID";//TEXT
		public static final String USER_FORMATTING_LANGUAGE_NAME = "USER_FORMATTING_LANGUAGE_NAME"; //TEXT
		public static final String USER_FORMATTING_LANGUAGE_LOCALE = "USER_FORMATTING_LANGUAGE_LOCALE";//TEXT


		///////////////////////////////////////////////////////////////////////////
		// PROFILE IMAGES
		///////////////////////////////////////////////////////////////////////////
		public static final String EXTENSION_PROFILE_IMAGE_URI = "EXTENSION_PROFILE_IMAGE_URI";
		public static final String EXTENSION_PROFILE_IMAGE_ETAG = "EXTENSION_PROFILE_IMAGE_ETAG";
		public static final String EXTENSION_PROFILE_IMAGE_LAST_MODIFIED = "EXTENSION_PROFILE_IMAGE_LAST_MODIFIED";
		public static final String EXTENSION_PROFILE_IMAGE_CONTENT_TYPE = "EXTENSION_PROFILE_IMAGE_CONTENT_TYPE";

        /* Columns */

		public static final int EXTENSION_INFO_TYPE_USER = 1;
		public static final int EXTENSION_INFO_TYPE_FAX_USER = 2;
		public static final int EXTENSION_INFO_TYPE_VIRTUAL_USER = 3;
		public static final int EXTENSION_INFO_TYPE_DIGITAL_USER = 4;
		public static final int EXTENSION_INFO_TYPE_Department = 5;
		public static final int EXTENSION_INFO_TYPE_ANNOUNCEMENT = 6;
		public static final int EXTENSION_INFO_TYPE_VOICEMAIL = 7;

		public static final int EXTENSION_INFO_STATUS_ENABLED = 1;
		public static final int EXTENSION_INFO_STATUS_DISABLED = 2;
		public static final int EXTENSION_INFO_STATUS_FROZEN = 3;

		public static final int DISABLE = 0;
		public static final int ENABLE = 1;

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ REST_URI + " TEXT,"
						+ REST_SMS_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_PAGER_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_PRESENCE_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_RINGOUT_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_INTERNATIONAL_CALLING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_DND_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_FAX_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_VOICEMAIL_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_VOIP_CALLING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_CONFERENCING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_VIDEO_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_REFRESH_TOKEN + " TEXT,"
						+ REST_SALES_FORCE_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_INTERCOM_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_PAGING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_CALL_PARK_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_CALL_RECORDING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_CALL_FORWARDING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_SMSRECEIVING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_PAGERRECEIVING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_FAXRECEIVING_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_HIPAACOMPLIANCE_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_FREESOFTPHONELINES_ENABLED + " INTEGER DEFAULT 0,"
						+ EXTENSION_INFO_ID + " TEXT,"
						+ EXTENSION_INFO_EXT_NUMBER + " TEXT,"
						+ EXTENSION_INFO_FIRST_NAME + " TEXT,"
						+ EXTENSION_INFO_LAST_NAME + " TEXT,"
						+ EXTENSION_INFO_COMPANY + " TEXT,"
						+ EXTENSION_INFO_EMAIL + " TEXT,"
						+ EXTENSION_INFO_FULL_NAME + " TEXT,"
						+ EXTENSION_INFO_TYPE + " INTEGER,"
						+ EXTENSION_INFO_STATUS + " INTEGER,"
						+ EXTENSION_INFO_ISO_CODE + " TEXT,"
						+ REST_REPORT_ENABLED + " TEXT,"
						+ SINGLE_EXTENSION_MODE + " INTEGER DEFAULT 0,"
						+ REST_IS_ADMIN + " INTEGER,"
						+ USER_SERVER_LANGUAGE_ID + " TEXT,"
						+ USER_SERVER_LANGUAGE_NAME + " TEXT,"
						+ USER_SERVER_LANGUAGE_LOCALE + " TEXT,"
						+ USER_GREETING_LANGUAGE_ID + " TEXT,"
						+ USER_GREETING_LANGUAGE_NAME + " TEXT,"
						+ USER_GREETING_LANGUAGE_LOCALE + " TEXT,"
						+ USER_FORMATTING_LANGUAGE_ID + " TEXT,"
						+ USER_FORMATTING_LANGUAGE_NAME + " TEXT,"
						+ USER_FORMATTING_LANGUAGE_LOCALE + " TEXT,"
						+ REST_HDVOICE_ENABLED + " INTEGER DEFAULT 0,"
						+ REST_RCTEAMS_ENABLED + " INTEGER DEFAULT 0, "
						+ REST_USERMANAGEMENT_ENABLED + " INTEGER DEFAULT 0,"
						+ EXTENSION_PROFILE_IMAGE_URI + " TEXT,"
						+ EXTENSION_PROFILE_IMAGE_ETAG + " TEXT,"
						+ EXTENSION_PROFILE_IMAGE_LAST_MODIFIED + " TEXT,"
						+ EXTENSION_PROFILE_IMAGE_CONTENT_TYPE + " TEXT,"
						+ REST_CALENDAR_ENABLED + " INTEGER DEFAULT 0"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class CallerIDsTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private CallerIDsTable() {
		}

		private static final CallerIDsTable sInstance = new CallerIDsTable();

		static CallerIDsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CALLER_IDS";

		/* Columns */
		//public static final String JEDI_CNAM_NAME = "cnamName";             //TEXT     //DO WE NEED THIS???
		public static final String JEDI_NUMBER = "number";                  //TEXT
		public static final String JEDI_USAGE_TYPE = "usageType";           //TEXT

		/* JEDI_USAGE_TYPE value for Main Number */
		public static final String USAGE_TYPE_MAIN_NUMBER = "MainNumber";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						//+ JEDI_CNAM_NAME + " TEXT,"
						+ JEDI_NUMBER + " TEXT,"
						+ JEDI_USAGE_TYPE + " TEXT"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}


	public static final class FwNumbersTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private FwNumbersTable() {
		}

		private static final FwNumbersTable sInstance = new FwNumbersTable();

		static FwNumbersTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "FW_NUMBERS";

		/* Columns */
		public static final String JEDI_NAME = "name";                  //TEXT          //!Added for legacy code compatibility (AccountInfoPOJO) Do we really need this?
		public static final String JEDI_NUMBER = "number";              //TEXT
		public static final String JEDI_ORDER_BY = "orderBy";           //INTEGER (int) //!Added for legacy code compatibility (AccountInfoPOJO) Do we really need this?
		public static final String JEDI_TYPE = "type";                  //TEXT

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ JEDI_NAME + " TEXT,"
						+ JEDI_NUMBER + " TEXT,"
						+ JEDI_ORDER_BY + " TEXT,"
						+ JEDI_TYPE + " TEXT"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class PhoneNumbersTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private PhoneNumbersTable() {
		}

		private static final PhoneNumbersTable sInstance = new PhoneNumbersTable();

		static PhoneNumbersTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "PHONE_NUMBERS";

		/* Columns */
		public static final String REST_ID = "REST_id";                     //INTEGER
		public static final String REST_URI = "REST_uri";                   //TEXT
		public static final String REST_PHONE_NUMBER = "REST_phoneNumber";  //TEXT
		public static final String REST_PAYMENT_TYPE = "REST_paymentType";  //TEXT
		public static final String REST_TYPE = "REST_type";                 //TEXT (enum)
		public static final String REST_USAGE_TYPE = "REST_usageType";      //TEXT (enum)
		public static final String REST_LOCATION = "REST_location";         //TEXT (enum)
		public static final String REST_FEATURES_CALLER_ID = "REST_features_CallerId";      //INTEGER (boolean)
		public static final String REST_FEATURES_SMS_SENDER = "REST_features_SmsSender";    //INTEGER (boolean)
		public static final String REST_PROCESSED = "REST_processed";       //INTEGER (boolean)
		//public static final String EXTENSION_NAME = "extension_name";
		public static final String ACCOUNT_NUMBER = "account_number";
		public static final String COUNTRY_ID = "country_id";
		public static final String LABEL = "label";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ REST_ID + " INTEGER,"
						+ REST_URI + " TEXT,"
						+ REST_PHONE_NUMBER + " TEXT,"
						+ REST_PAYMENT_TYPE + " TEXT,"
						+ REST_TYPE + " TEXT,"
						+ REST_USAGE_TYPE + " TEXT,"
						+ REST_LOCATION + " TEXT,"
						+ REST_FEATURES_CALLER_ID + " INTEGER DEFAULT 0,"
						+ REST_FEATURES_SMS_SENDER + " INTEGER DEFAULT 0,"
						+ REST_PROCESSED + " INTEGER DEFAULT 1,"
						+ ACCOUNT_NUMBER + " TEXT,"
						// + EXTENSION_NAME + " TEXT,"
						+ COUNTRY_ID + " INTEGER,"
						+ LABEL + " TEXT,"
						//AB-9224 The default Call ID cannot be set automatically when user login.
						+ "CONSTRAINT UK_REST_PHONE_NUMBER_ID UNIQUE (" + MAILBOX_ID + ", " + REST_PHONE_NUMBER + ") ON CONFLICT REPLACE "
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	/**
	 * Defines a table for blocked numbers.
	 */
	public static final class BlockedNumbersTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private BlockedNumbersTable() {
		}

		private static final BlockedNumbersTable sInstance = new BlockedNumbersTable();

		static BlockedNumbersTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "BLOCKED_NUMBERS";

		/* Columns */
		public static final String REST_ID = "REST_id";           //TEXT
		public static final String REST_PHONE_NUMBER = "REST_phoneNumber";  //TEXT
		public static final String REST_NAME = "REST_name";         //TEXT
		public static final String REST_PROCESSED = "REST_processed";    //INTEGER (boolean)

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ REST_ID + " TEXT,"
						+ REST_PHONE_NUMBER + " TEXT,"
						+ REST_NAME + " TEXT,"
						+ REST_PROCESSED + " INTEGER DEFAULT 1"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class ExtensionsTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private ExtensionsTable() {
		}

		private static final ExtensionsTable sInstance = new ExtensionsTable();

		static ExtensionsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "EXTENSIONS";

		/* Columns */
		public static final String JEDI_EMAIL = "email";                    //TEXT
		public static final String JEDI_FIRST_NAME = "firstName";           //TEXT
		public static final String JEDI_MIDDLE_NAME = "middleName";         //TEXT
		public static final String JEDI_LAST_NAME = "lastName";             //TEXT
		public static final String JEDI_MAILBOX_ID_EXT = "ext_mailboxId";   //INTEGER (long)
		public static final String JEDI_PIN = "pin";                        //TEXT
		//public static final String JEDI_TIER_SETTINGS = "tierSettings";   //TEXT => //INTEGER (long)
		public static final String JEDI_ADDRESS_LINE_1 = "addressLine1";    //TEXT
		public static final String JEDI_ADDRESS_LINE_2 = "addressLine2";    //TEXT
		public static final String JEDI_CITY = "city";                      //TEXT
		public static final String JEDI_COUNTRY = "country";                //TEXT
		public static final String JEDI_STATE = "state";                    //TEXT
		public static final String JEDI_ZIPCODE = "zipCode";                //TEXT
		public static final String RCM_DISPLAY_NAME = "display_name";       //TEXT
		public static final String RCM_STARRED = "starred";                //INTEGER
		public static final String JEDI_CONTACT_PHONE = "phonenumber";      //TEXT
		public static final String JEDI_MOBILE_PHONE = "mobilenumber";      //TEXT
		public static final String JEDI_QUEUE_STATE = "queueState";        //TEXT (enum)
		public static final String RCM_PHONE_NUMBER_REFRESH_TIME = "phoneNumberRefreshTime";

		public static final String REST_STATUS = "rest_status"; //TEXT 'Enabled' | 'Disabled' | 'Frozen' | 'NotActivated'
		public static final String REST_TYPE = "rest_type";  //TEXT 'User' | 'Fax User' | | 'VirtualUser' | 'DigitalUser' | 'Department' | 'Announcement' | 'Voicemail' | 'SharedLinesGroup' | 'PagingOnly'
		public static final String REST_ADMIN_PERMISSION = "rest_admin_permission"; //TEXT
		public static final String REST_INTERNATIONALCALLING_PERMISSION = "rest_internationalcalling_permission"; //TEXT

		//queueState values
//        public static final String QUEUE_STATE_NORMAL = "Normal";
//        public static final String QUEUE_STATE_QUEUE = "Queue";
//        public static final String QUEUE_STATE_AGENT = "Agent";

		public static final String USER_TYPE_DEPARTMENT = "Department";
		public static final String UER_TYPE_PARK_LOCATION="ParkLocation";
		public static final String USER_TYPE_IVR="IvrMenu";
		public static final String USER_TYPE_PAGING_ONLY ="PagingOnly";

		public static final String USER_STATUS_ENABLED = "Enabled";
		public static final String USER_STATUS_NOT_ACTIVATED = "NotActivated";

		public static final String PROFILE_IMAGE = "profileImage";
		public static final String PROFILE_IMAGE_ETAG = "Etag";

		public static final String PROFILE_IMAGE_SMALL_URI = "ProfileImageSmallUri";
		public static final String PROFILE_IMAGE_MEDIUM_URI = "ProfileImageMediumUri";
		public static final String PROFILE_IMAGE_LARGE_URI = "ProfileImageLargeUri";

		public static final String RCM_SORT = "sort";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ JEDI_EMAIL + " TEXT,"
						+ JEDI_FIRST_NAME + " TEXT,"
						+ JEDI_MIDDLE_NAME + " TEXT,"
						+ JEDI_LAST_NAME + " TEXT,"
						+ JEDI_MAILBOX_ID_EXT + " INTEGER,"
						+ JEDI_PIN + " TEXT,"
						//+ JEDI_TIER_SETTINGS + " INTEGER,"
						+ JEDI_ADDRESS_LINE_1 + " TEXT,"
						+ JEDI_ADDRESS_LINE_2 + " TEXT,"
						+ JEDI_CITY + " TEXT,"
						+ JEDI_COUNTRY + " TEXT,"
						+ JEDI_STATE + " TEXT,"
						+ JEDI_ZIPCODE + " TEXT,"
						+ RCM_DISPLAY_NAME + " TEXT,"
						+ RCM_STARRED + " INTEGER,"
						+ JEDI_CONTACT_PHONE + " TEXT,"
						+ JEDI_MOBILE_PHONE + " TEXT,"
						+ JEDI_QUEUE_STATE + " TEXT,"
						+ RCM_PHONE_NUMBER_REFRESH_TIME + " INTEGER,"
						+ RCM_SORT + " INTEGER,"
						+ REST_TYPE + " TEXT,"
						+ REST_STATUS + " TEXT,"
						+ REST_ADMIN_PERMISSION + " TEXT,"
						+ PROFILE_IMAGE + " TEXT,"
						+ PROFILE_IMAGE_ETAG + " TEXT,"
						+ PROFILE_IMAGE_SMALL_URI + " TEXT,"
						+ PROFILE_IMAGE_MEDIUM_URI + " TEXT,"
						+ PROFILE_IMAGE_LARGE_URI + " TEXT,"

						+ REST_INTERNATIONALCALLING_PERMISSION + " TEXT"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class CalendarTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private CalendarTable() {
		}

		private static final CalendarTable sInstance = new CalendarTable();

		static CalendarTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CalendarTable";

		public static final String START_TIME = "start_time";            //TEXT
		public static final String END_TIME = "end_time";            //TEXT
		public static final String DAY = "day";            //eg,2015,08,22
		public static final String MONTH = "month";    //eg.2015,08
		public static final String LOCATION = "location";              //TEXT
		public static final String MESSAGE = "message";           //TEXT
		public static final String IS_CALL_REMINDER = "is_call_reminder";           //TEXT

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ START_TIME + " TEXT,"
						+ END_TIME + " TEXT,"
						+ DAY + " TEXT,"
						+ MONTH + " TEXT,"
						+ LOCATION + " TEXT,"
						+ IS_CALL_REMINDER + " INTEGER DEFAULT 0,"
						+ MESSAGE + " TEXT"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}



	public static final class FavoritesTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private FavoritesTable() {
		}

		private static final FavoritesTable sInstance = new FavoritesTable();

		static FavoritesTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "FAVORITES";

		/* Columns */
		public static final String DISPLAY_NAME = "display_name";
		public static final String NORMALIZED_NUMBER = "normalized_number";
		public static final String ORIGINAL_NUMBER = "original_number";
		public static final String TYPE = "type";
		public static final String LABEL = "label";
		public static final String CONTACT_ID = "contact_id";
		public static final String PHONE_ID = "phone_id";
		public static final String PHOTO_ID = "photo_id";
		public static final String LOOKUP_KEY = "lookup_key";
		public static final String RCM_SORT = "sort";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ DISPLAY_NAME + " TEXT,"
						+ NORMALIZED_NUMBER + " TEXT,"
						+ ORIGINAL_NUMBER + " TEXT,"
						+ TYPE + " INTEGER,"
						+ LABEL + " TEXT,"
						+ CONTACT_ID + " INTEGER,"
						+ PHONE_ID + " INTEGER,"
						+ PHOTO_ID + " INTEGER,"
						+ LOOKUP_KEY + " TEXT,"
						+ RCM_SORT + " INTEGER"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}


	public static final class CallLogTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private CallLogTable() {
		}

		private static final CallLogTable sInstance = new CallLogTable();

		static CallLogTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CALL_LOG";

		/* Columns */
		public static final String REST_CSYNC_ACTION = "REST_CSYNC_ACTION";                // INTEGER (enum) - OLD(JEDI -> Type)
		public static final String CSYNC_RECORD_ID = "CSYNC_RECORD_ID";                // INTEGER (long)
		public static final String REST_CSYNC_RESULT = "REST_CSYNC_RESULT";              // INTEGER (int) - OLD(JEDI -> Status)

		public static final String REST_CYSNC_SESSION_ID = "REST_CYSNC_SESSION_ID";      // TEXT
		public static final String REST_CSYNC_START = "REST_CSYNC_START";               // INTEGER (long)
		public static final String REST_CSYNC_DURATION = "REST_CSYNC_DURATION";            // REAL (double) !!!!
		public static final String REST_CSYNC_DIRECTION = "REST_CSYNC_DIRECTION";        // INTEGER (emnu "Incoming|Inbound", "Outgoing|Outbound")

		public static final String CSYNC_PIN = "CSYNC_PIN";                        // TEXT
		public static final String CSYNC_LOCATION = "CSYNC_LOCATION";                // TEXT

		public static final String REST_CSYNC_TO_PHONE = "REST_CSYNC_TO_PHONE";            // TEXT
		public static final String REST_CSYNC_TO_NAME = "REST_CSYNC_TO_NAME";             // TEXT
		public static final String REST_CSYNC_FROM_NAME = "REST_CSYNC_FROM_NAME";           // TEXT
		public static final String REST_CSYNC_FROM_PHONE = "REST_CSYNC_FROM_PHONE";          // TEXT

		public static final String REST_TO_EXT_NUMBER = "REST_TO_EXT_NUMBER";                // TEXT
		public static final String REST_FROM_EXT_NUMBER = "REST_FROM_EXT_NUMBER";            // TEXT
		public static final String REST_TO_LOCATION = "REST_TO_LOCATION";                // TEXT
		public static final String REST_FROM_LOCATION = "REST_FROM_LOCATION";                // TEXT

		public static final String RCM_FOLDER = "RCM_FOLDER";                        // INTEGER (LOGS_ALL, LOGS_MISSED)

		public static final String HAS_MORE_RECORD_ITEM = "HAS_MORE_RECORD_ITEM";
		public static final String NORMALIZED_NUMBER = "NORMALIZED_NUMBER"; ///<------ phone number
		//public static final String BIND_CALLING_CODE = "BIND_CALLING_CODE";                //TEXT

		public static final String RCM_DISPLAY_NAME = "RCM_DISPLAY_NAME";
		public static final String RCM_REST_API = "RCM_REST_API";            // INTEGER (boolean)

		public static final String REST_URI = "REST_URI";                // TEXT
		public static final String REST_CALL_LOG_ID = "REST_CALL_LOG_ID";        // TEXT
		public static final String REST_TYPE = "REST_TYPE";                // INTEGER (emnu)
		public static final String REST_AVAILABILITY = "REST_AVAILABILITY";        // INTEGER (emnu)
		public static final String RCM_INSERT_DATE = "RCM_INSERT_DATE";        // INTEGER (long)
		public static final String RCM_IS_INTERCOM = "RCM_IS_INTERCOM";        // INTEGER (boolean)
		public static final String RCM_LOG_TYPE = "RCM_LOG_TYPE";           // INTEGER (emnu)
        /* Columns End */


		/* Log type enumeration - stored in RCM_FOLDER */
		public static final int LOGS_ALL = 0;
		public static final int LOGS_MISSED = 1;

		public static final String STATUS_GROUP_ALL = "All";
		public static final String STATUS_GROUP_MISSED = "Missed";

		public static final int RCM_REST_API_NOT_USED = 0;
		public static final int RCM_REST_API_USED = 1;

		public static final int AVAILABILITY_ALIVE = 0;
		public static final int AVAILABILITY_DELETED = 1;
		public static final int AVAILABILITY_PURGED = 2;

		public static final int RCM_NUMBER_ISNOT_INTERCOM = 0;
		public static final int RCM_NUMBER_IS_INTERCOM = 1;

		public static final int TYPE_PERSONAL = 0;
		public static final int TYPE_COMPANY = 1;

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ REST_CSYNC_ACTION + " INTEGER,"
						+ CSYNC_RECORD_ID + " INTEGER,"
						+ REST_CSYNC_RESULT + " INTEGER,"
						+ REST_CYSNC_SESSION_ID + " TEXT,"
						+ REST_CSYNC_START + " INTEGER,"
						+ REST_CSYNC_DURATION + " REAL,"
						+ REST_CSYNC_DIRECTION + " INTEGER,"
						+ CSYNC_PIN + " TEXT,"
						+ CSYNC_LOCATION + " TEXT,"
						+ REST_CSYNC_TO_PHONE + " TEXT,"
						+ REST_CSYNC_TO_NAME + " TEXT,"
						+ REST_CSYNC_FROM_NAME + " TEXT,"
						+ REST_CSYNC_FROM_PHONE + " TEXT,"
						+ REST_TO_EXT_NUMBER + " TEXT,"
						+ REST_FROM_EXT_NUMBER + " TEXT,"
						+ REST_TO_LOCATION + " TEXT,"
						+ REST_FROM_LOCATION + " TEXT,"
						+ RCM_FOLDER + " INTEGER,"
						+ HAS_MORE_RECORD_ITEM + " INTEGER DEFAULT 0,"
						+ NORMALIZED_NUMBER + " TEXT,"
						//+ BIND_CALLING_CODE + " TEXT,"
						+ RCM_DISPLAY_NAME + " TEXT,"
						+ RCM_REST_API + " INTEGER DEFAULT " + RCM_REST_API_NOT_USED + ','
						+ REST_URI + " TEXT,"
						+ REST_CALL_LOG_ID + " TEXT,"
						+ REST_TYPE + " TEXT,"
						+ REST_AVAILABILITY + " INTEGER DEFAULT " + AVAILABILITY_ALIVE + ","
						+ RCM_INSERT_DATE + " INTEGER DEFAULT " + System.currentTimeMillis() + ","
						+ RCM_IS_INTERCOM + " INTEGER DEFAULT " + RCM_NUMBER_ISNOT_INTERCOM + ","
						+ RCM_LOG_TYPE + " INTEGER DEFAULT " + CallLogTable.TYPE_PERSONAL + ","
						+ "CONSTRAINT UK_REST_CALL_LOG_ID UNIQUE (" + REST_CALL_LOG_ID + ", " + RCM_FOLDER + ", " + REST_CSYNC_DIRECTION + "," + RCM_LOG_TYPE + ") ON CONFLICT REPLACE "
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class CallLogTokenTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private CallLogTokenTable() {
		}

		private static final CallLogTokenTable sInstance = new CallLogTokenTable();

		static CallLogTokenTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CALL_LOG_TOKEN";

		/* Columns */
		public static final String RCM_STATUS_GROUP = "RCM_STATUS_GROUP";      // INTEGER (enum)
		public static final String REST_SYNC_TOKEN = "REST_SYNC_TOKEN";       // TEXT
		public static final String REST_SYNC_TIME = "REST_SYNC_TIME";        // INTEGER (long): last sync time in milliseconds
		public static final String RCM_CALL_LOG_TYPE = "RCM_CALL_LOG_TYPE";     // INTEGER (enum)

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ RCM_STATUS_GROUP + " INTEGER,"
						+ REST_SYNC_TOKEN + " TEXT,"
						+ REST_SYNC_TIME + " INTEGER,"
						+ RCM_CALL_LOG_TYPE + " INTEGER DEFAULT " + CallLogTable.TYPE_PERSONAL
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	/**
	 * Messages Table
	 */
	public static final class MessagesTable extends RCMDbTable implements RCMColumns, BaseColumns, SyncStatusEnum {

		private MessagesTable() {
		}

		private static final MessagesTable sInstance = new MessagesTable();

		static MessagesTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "MESSAGES";

		public static final String MSYNC_MSG_ID = "MsgID";               //INTEGER (long)
		public static final String MSYNC_MSG_TYPE = "MsgType";           //INTEGER (enum MSG_TYPE_*)
		public static final String MSYNC_COMP_TYPE = "CompType";         //INTEGER (enum COMP_TYPE_*)
		//(old MsgSync API only)
		//(for REST API see REST_ATTACHMENT_CONTENT_TYPE)
		public static final String MSYNC_FILE_EXT = "FileExt";           //TEXT (old MsgSync API only)
		public static final String MSYNC_DURATION = "Duration";          //INTEGER: Duration of voice mail in seconds.
		public static final String MSYNC_CREATE_DATE = "CreateDate";     //INTEGER (long): creation time in milliseconds
		public static final String MSYNC_FROM_PHONE = "FromPhone";       //TEXT
		public static final String MSYNC_FROM_NAME = "FromName";         //TEXT
		public static final String MSYNC_TO_PHONE = "ToPhone";           //TEXT
		public static final String MSYNC_TO_NAME = "ToName";             //TEXT
		public static final String MSYNC_READ_STATUS = "Status";         //INTEGER (boolean)

		public static final String RCM_SYNC_STATUS = "sync_status";     //INTEGER (enum SyncStatusEnum.SYNC_STATUS_*)
		public static final String RCM_FILE_PATH = "file_path";         //TEXT
		public static final String RCM_LOCALLY_DELETED = "locally_deleted"; //INTEGER (boolean)(old MsgSync API only)
		public static final String RCM_DURATION_SYNCHRONIZED = "duration_synchronized";     //INTEGER (boolean)
		public static final String RCM_FROM_PHONE_IS_VALID_NUMBER = "FromPhone_isValidNumber"; //INTEGER (-1 : not init, 0 - invalid, 1 - valid)
		public static final String RCM_FROM_PHONE_NORMALIZED_NUMBER = "FromPhone_normalizedNumber"; //TEXT
		public static final String BIND_HAS_CONTACT = "bind_has_contact";                    //INTEGER (boolean)
		public static final String BIND_CONTACT_TYPE = "bind_contact_type";   //INTEGER (MENU)
		public static final String BIND_ID = "bind_id";                                     //TEXT
		public static final String BIND_DISPLAY_NAME = "bind_display_name";                 //TEXT
		public static final String BIND_ORIGINAL_NUMBER = "bind_original_number";           //TEXT
		public static final String BIND_PHONE_TYPE = "bind_phone_type";                    //TEXT
		public static final String BIND_CALLING_CODE = "bind_calling_code";                //TEXT
		public static final String RCM_DISPLAY_NAME = "display_name";                        //TEXT

		public static final String RCM_REST_API = "RCM_REST_API";                       //INTEGER (boolean)
		public static final String RCM_SEND_STATUS = "RCM_SEND_STATUS";                 //INTEGER (enum)
		public static final String RCM_DRAFT_TO_PHONE = "RCM_DRAFT_TO_PHONE";           //TEXT
		public static final String RCM_DRAFT_FROM_PHONE = "RCM_DRAFT_FROM_PHONE";       //TEXT

		public static final String RCM_REST_ERROR_CODE = "RCM_REST_ERROR_CODE";         //INTEGER (enum)

		public static final String REST_URI = "REST_uri";                               //TEXT
		public static final String REST_DIRECTION = "REST_direction";                   //INTEGER (enum: DIRECTION_*)
		public static final String REST_FROM_EXT_NUMBER = "REST_from_extensionNumber";  //TEXT
		public static final String REST_FROM_LOCATION = "REST_from_location";           //TEXT
		public static final String REST_TO_EXT_NUMBER = "REST_to_extensionNumber";      //TEXT
		public static final String REST_TO_LOCATION = "REST_to_location";               //TEXT
		public static final String REST_SUBJECT = "REST_subject";                       //TEXT: Message subject.
		public static final String REST_CONFERENCE = "REST_conference";                 //INTEGER (boolean)
		// For SMS and Pager messages it replicates
		// message text which is also returned as attachment.
		public static final String REST_PG_TO_DEPARTMENT = "REST_pgToDepartment";       //INTEGER (boolean)
		public static final String REST_GROUP_TYPE = "REST_group_type";            //INTEGER
		public static final String REST_CONVERSATION_ID = "REST_conversationId";        //INTEGER (long)
		public static final String REST_PRIORITY = "REST_priority";                     //TEXT
		public static final String REST_LAST_MODIFIED_TIME = "REST_lastModifiedTime";   //INTEGER (long):
		//The timestamp when the message
		//was modified on server
		public static final String REST_MESSAGE_STATUS = "REST_messageStatus";            //TEXT
		public static final String REST_AVAILABILITY = "REST_availability";             //INTEGER (enum: AVAILABILITY_*)
		public static final String REST_SMS_DELIVERY_TIME = "REST_smsdeliveryTime";    //TEXT (SMS only)
		public static final String REST_SMS_SENDING_ATTEMPTS_COUNT = "REST_smsSendingAttemptsCount";    //INTEGER (SMS only)
		public static final String REST_DELIVERY_ERROR_CODE = "REST_deliveryErrorCode";    //TEXT (SMS only)
		public static final String REST_FAX_PAGE_COUNT = "REST_faxPageCount";           //INTEGER
		public static final String REST_FAX_RESOLUTION = "REST_faxResolution";          //TEXT
		public static final String REST_ATTACHMENT_ID = "REST_attachment_id";           //INTEGER (long)
		public static final String REST_ATTACHMENT_URI = "REST_attachment_uri";         //TEXT
		public static final String REST_ATTACHMENT_CONTENT_TYPE = "REST_attachment_contentType"; //TEXT
		public static final String REST_ERROR_CODE = "REST_error_code"; //INTEGER

		//Voicemail transcription colums
		/**
		 * INTEGER (long)
		 */
		public static final String REST_TRANS_ID = "REST_transcription_id";
		/**
		 * TEXT
		 */
		public static final String REST_TRANS_TEXT = "REST_transcription_text";
		/**
		 * TEXT
		 */
		public static final String REST_TRANS_CONTENT_URI = "REST_transcription_content_uri";
		/**
		 * INTEGER (enum SyncStatusEnum.SYNC_STATUS_*)
		 */
		public static final String REST_TRANS_SYNC_STATUS = "REST_transcription_sync_status";
		/**
		 * TEXT (enum TranscriptionStatusEnum.*)
		 */
		public static final String REST_TRANS_STATUS = "REST_transcription_status";

		/* Message type enumeration
         * (see http://jira.rcoffice.ringcentral.com/secure/attachment/24330/RCClientSync_Ver_1_4_2008-01-16.pdf
         * These constants are defined on the MsgSync backend; don't change them!!!
         */
		public static final int MSG_TYPE_ANY = 0;    // MsgSync and REST
		public static final int MSG_TYPE_VOICE = 1;    // MsgSync and REST
		public static final int MSG_TYPE_FAX = 2;    // MsgSync and REST
		public static final int MSG_TYPE_GENERIC = 3;    // MsgSync only
		public static final int MSG_TYPE_CALL = 4;    // MsgSync only
		public static final int MSG_TYPE_EXTENSION = 5;    // MsgSync only
		public static final int MSG_TYPE_MULTIPLE = 6;    // Only to identify create new message draft message's multiple recipient.
		// REST API message types
		public static final int MSG_TYPE_SMS = 10;   // REST only
		public static final int MSG_TYPE_PAGER = 20;   // REST only
		public static final int MSG_TYPE_TEXT = 30;   // REST only

		public static final int MSG_UNREAD = 0;
		public static final int MSG_READ = 1;

		public static final int SEND_STATUS_NONE = 0;    //Incoming message
		public static final int SEND_STATUS_DRAFT = 1;
		public static final int SEND_STATUS_SEND_INIT = 2;
		public static final int SEND_STATUS_SENT = 3;
		public static final int SEND_STATUS_ERROR = 4;

		public static final String MESSAGE_STATUS_SENDING_FAILED = "SendingFailed";
		public static final String MESSAGE_STATUS_DELIVERY_FAILED = "DeliveryFailed";

		/**
		 * Message direction enumeration: REST_DIRECTION column values
		 */
		public static final int DIRECTION_INBOUND = 0;
		public static final int DIRECTION_OUTBOUND = 1;

		/**
		 * Availability status enumeration: REST_AVAILABILITY column values
		 */
		public static final int AVAILABILITY_ALIVE = 0;
		public static final int AVAILABILITY_DELETED = 10;
		public static final int AVAILABILITY_PURGED = 20;
		public static final int AVAILABILITY_LOCALLY_DELETED = 100;  //set locally by the application; does not come from server

		/**
		 * Group type enumeration: REST_GROUP_TYPE column values
		 */
		public static final int GROUP_TYPE_NONE = 0;
		public static final int GROUP_TYPE_GROUPED = 1;

		public static final int MESSAGE_NOT_CONFERNECE = 0;
		public static final int MESSAGE_IS_CONFERNECE = 1;

		public static final int COMP_TYPE_UNKNOWN = 0;
		public static final int COMP_TYPE_GSM = 1;
		public static final int COMP_TYPE_WAVE = 2;
		public static final int COMP_TYPE_AIFF = 3;
		public static final int COMP_TYPE_GIF = 4;
		public static final int COMP_TYPE_TIFF = 5;
		public static final int COMP_TYPE_R0Z = 6;

		public static final long MESSAGE_ID_INVALID = 0;
		public static final long CONVERSATION_ID_INVALID = -1;

		public static final String READ_STATUS = "readStatus";    // For query, This isn't a database field
		public static final String ITEM_AMOUNT = "amount"; // For query, This isn't a database column
		public static final String MSG_INFO_ID = "msg_info_id";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER ,"
						+ MSYNC_MSG_ID + " INTEGER,"
						+ MSYNC_MSG_TYPE + " INTEGER,"
						+ MSYNC_COMP_TYPE + " INTEGER,"
						+ MSYNC_FILE_EXT + " TEXT,"
						+ MSYNC_DURATION + " INTEGER,"
						+ MSYNC_CREATE_DATE + " INTEGER,"
						+ MSYNC_FROM_PHONE + " TEXT,"
						+ MSYNC_FROM_NAME + " TEXT,"
						+ MSYNC_TO_PHONE + " TEXT,"
						+ MSYNC_TO_NAME + " TEXT,"
						+ MSYNC_READ_STATUS + " INTEGER,"
						+ RCM_SYNC_STATUS + " INTEGER DEFAULT " + SyncStatusEnum.SYNC_STATUS_NOT_LOADED + ','
						+ RCM_FILE_PATH + " TEXT,"
						+ RCM_LOCALLY_DELETED + " INTEGER DEFAULT 0,"
						+ RCM_DURATION_SYNCHRONIZED + " INTEGER,"
						+ RCM_FROM_PHONE_IS_VALID_NUMBER + " INTEGER DEFAULT -1,"
						+ RCM_FROM_PHONE_NORMALIZED_NUMBER + " TEXT,"
						+ BIND_HAS_CONTACT + " INTEGER,"
						+ BIND_CONTACT_TYPE + " INTEGER DEFAULT -1"+ ','
						+ BIND_ID + " INTEGER,"
						+ BIND_DISPLAY_NAME + " TEXT,"
						+ BIND_ORIGINAL_NUMBER + " TEXT,"
						+ BIND_PHONE_TYPE + " TEXT,"
						+ BIND_CALLING_CODE + " TEXT,"
						+ RCM_DISPLAY_NAME + " TEXT,"
						+ RCM_REST_API + " INTEGER DEFAULT 0,"
						+ RCM_SEND_STATUS + " INTEGER DEFAULT " + SEND_STATUS_NONE + ","
						+ RCM_DRAFT_TO_PHONE + " TEXT,"
						+ RCM_DRAFT_FROM_PHONE + " TEXT,"
						+ RCM_REST_ERROR_CODE + " INTEGER DEFAULT " + RestApiErrorCodes.NO_ERROR + ","
						+ REST_URI + " TEXT,"
						+ REST_DIRECTION + " INTEGER DEFAULT " + DIRECTION_INBOUND + ","
						+ REST_FROM_EXT_NUMBER + " TEXT,"
						+ REST_FROM_LOCATION + " TEXT,"
						+ REST_TO_EXT_NUMBER + " TEXT,"
						+ REST_TO_LOCATION + " TEXT,"
						+ REST_SUBJECT + " TEXT,"
						+ REST_CONFERENCE + " INTEGER DEFAULT " + MESSAGE_NOT_CONFERNECE + ","
						+ REST_PG_TO_DEPARTMENT + " INTEGER,"
						+ REST_GROUP_TYPE + " INTEGER DEFAULT " + GROUP_TYPE_NONE + ","
						+ REST_CONVERSATION_ID + " INTEGER DEFAULT " + CONVERSATION_ID_INVALID + ","
						+ REST_PRIORITY + " TEXT,"
						+ REST_LAST_MODIFIED_TIME + " INTEGER,"
						+ REST_MESSAGE_STATUS + " TEXT,"
						+ REST_AVAILABILITY + " INTEGER DEFAULT " + AVAILABILITY_ALIVE + ","
						+ REST_SMS_DELIVERY_TIME + " TEXT,"
						+ REST_SMS_SENDING_ATTEMPTS_COUNT + " INTEGER,"
						+ REST_DELIVERY_ERROR_CODE + " TEXT,"
						+ REST_FAX_PAGE_COUNT + " INTEGER,"
						+ REST_FAX_RESOLUTION + " INTEGER,"
						+ REST_ATTACHMENT_ID + " INTEGER DEFAULT -1,"
						+ REST_ATTACHMENT_URI + " TEXT,"
						+ REST_ATTACHMENT_CONTENT_TYPE + " TEXT,"
						+ REST_TRANS_ID + " INTEGER DEFAULT -1,"
						+ REST_TRANS_TEXT + " TEXT,"
						+ REST_TRANS_CONTENT_URI + " TEXT,"
						+ REST_TRANS_SYNC_STATUS + " INTEGER DEFAULT " + SyncStatusEnum.SYNC_STATUS_NOT_LOADED + ","
						+ REST_TRANS_STATUS + " TEXT DEFAULT " + VMTranscriptionStatusEnum.Unknown.name() + ","
						+ REST_ERROR_CODE + " INTEGER DEFAULT -999,"
						+ "CONSTRAINT UK_MSG_ID UNIQUE (" + MSYNC_MSG_ID + ") ON CONFLICT REPLACE "
						+ ");";

		private static final String CREATE_INDEX_STMT =
				"CREATE INDEX IF NOT EXISTS IDX_CREATE_DATE ON " + TABLE_NAME
						+ "(" + MSYNC_CREATE_DATE + " DESC );";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			db.execSQL(CREATE_INDEX_STMT);
		}
	}

	public static final class MessageRecipientsTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private MessageRecipientsTable() {
		}

		private static final MessageRecipientsTable sInstance = new MessageRecipientsTable();

		static MessageRecipientsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "MESSAGE_RECIPIENTS";

		/* Columns */
		public static final String RCM_REST_MSG_ID = "RCM_REST_MSG_ID";            // INTEGER (long)
		public static final String REST_MSG_TYPE = "RCM_REST_MSG_TYPE";            // INTEGER (enum)
		public static final String REST_CONVERSATION_ID = "REST_CONVERSATION_ID";        // INTEGER (long)
		public static final String REST_PHONE_NUMBER = "REST_PHONE_NUMBER";            // TEXT
		public static final String REST_EXTENSION_NUMBER = "REST_EXTENSIO_NNUMBER";        // TEXT
		public static final String REST_LOCATION = "REST_LOCATION";              // TEXT
		public static final String REST_NAME = "REST_NAME";                  // TEXT
		public static final String RCM_NUMBER_IS_VALID = "RCM_NUMBER_IS_VALID";        // INTEGER (enum)

		public static final int NUMBER_ISNOT_VALID = 0;
		public static final int NUMBER_IS_VALID = 1;

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("

						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ RCM_REST_MSG_ID + " INTEGER,"
						+ REST_MSG_TYPE + " INTEGER,"
						+ REST_CONVERSATION_ID + " INTEGER,"
						+ REST_PHONE_NUMBER + " TEXT,"
						+ REST_EXTENSION_NUMBER + " TEXT,"
						+ REST_LOCATION + " TEXT,"
						+ REST_NAME + " TEXT,"
						+ RCM_NUMBER_IS_VALID + " INTEGER DEFAULT " + NUMBER_ISNOT_VALID

						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}


	public static final class MessageListTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private MessageListTable() {
		}

		private static final MessageListTable sInstance = new MessageListTable();

		static MessageListTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "MESSAGE_LIST";

		/* Columns */
		public static final String RCM_MESSAGE_LIST_TYPE = "RCM_message_list_type";     //INTEGER (enum)
		public static final String REST_SYNC_TOKEN = "REST_syncToken";         //TEXT
		public static final String REST_SYNC_TIME = "REST_syncTime";         //INTEGER (long): last sync time in milliseconds

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ RCM_MESSAGE_LIST_TYPE + " INTEGER,"
						+ REST_SYNC_TOKEN + " TEXT,"
						+ REST_SYNC_TIME + " INTEGER"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class MessageConversationsTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private MessageConversationsTable() {
		}

		private static final MessageConversationsTable sInstance = new MessageConversationsTable();

		static MessageConversationsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "MESSAGE_CONVERSATIONS";

		/* Columns */
		public static final String RCM_CONVERSATION_ID = "RCM_conversationId";        //INTEGER
		public static final String REST_SYNC_TOKEN = "REST_syncToken";                //TEXT
		public static final String REST_SYNC_TIME = "REST_syncTime";                //INTEGER (long): last sync time in milliseconds(server time)
		public static final String REST_LOCAL_SYNC_TIME = "REST_local_syncTime";    //INTEGER (long): last sync time in milliseconds(Local time)

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ RCM_CONVERSATION_ID + " INTEGER,"
						+ REST_SYNC_TOKEN + " TEXT,"
						+ REST_SYNC_TIME + " INTEGER,"
						+ REST_LOCAL_SYNC_TIME + " INTEGER"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}


	public static final class MessageDraftTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private MessageDraftTable() {
		}

		private static final MessageDraftTable sInstance = new MessageDraftTable();

		static MessageDraftTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "MESSAGE_DRAFT";

		/* Columns */
		public static final String NEW_MESSAGE_DRAFT_URI = "new_message_draft_uri";     //TEXT

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ NEW_MESSAGE_DRAFT_URI + " TEXT"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}


	public static final class SyncStatusTable extends RCMDbTable implements RCMColumns, BaseColumns, SyncStatusEnum {

		private SyncStatusTable() {
		}

		private static final SyncStatusTable sInstance = new SyncStatusTable();

		static SyncStatusTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "SYNC_STATUS";

		/* Columns */
		public static final String LOGIN_STATUS = "login_status";     //INTEGER (enum)
		public static final String LOGIN_LASTSYNC = "login_lastsync";       //INTEGER (long)
		public static final String LOGIN_ERROR = "login_error";           //TEXT : error code, example [JEDI, 200]

		public static final String API_VERSION_STATUS = "api_version_status";     //INTEGER (enum)
		public static final String API_VERSION_LASTSYNC = "api_version_lastsync";       //INTEGER (long)
		public static final String API_VERSION_ERROR = "api_version_error";           //TEXT : error code, example [JEDI, 200]

		public static final String ACCOUNT_INFO_STATUS = "account_info_status";     //INTEGER (enum)
		public static final String ACCOUNT_INFO_LASTSYNC = "account_info_lastsync";       //INTEGER (long)
		public static final String ACCOUNT_INFO_ERROR = "account_info_error";           //TEXT : error code, example [JEDI, 200]

		public static final String ALL_CALL_LOGS_STATUS = "all_call_logs_status";     //INTEGER (enum)
		public static final String ALL_CALL_LOGS_LASTSYNC = "all_call_logs_lastsync";       //INTEGER (long)
		public static final String ALL_CALL_LOGS_ERROR = "all_call_logs_error";           //TEXT : error code, example [JEDI, 200]

		public static final String MISSED_CALL_LOGS_STATUS = "misses_call_logs_status";     //INTEGER (enum)
		public static final String MISSED_CALL_LOGS_LASTSYNC = "misses_call_logs_lastsync";       //INTEGER (long)
		public static final String MISSED_CALL_LOGS_ERROR = "misses_call_logs_error";           //TEXT : error code, example [JEDI, 200]

		public static final String CALLER_IDS_STATUS = "caller_ids_status";     //INTEGER (enum)
		public static final String CALLER_IDS_LASTSYNC = "caller_ids_lastsync";       //INTEGER (long)
		public static final String CALLER_IDS_ERROR = "caller_ids_error";           //TEXT : error code, example [JEDI, 200]

		public static final String RINGOUT_CALL_STATUS = "ringout_call_status";     //INTEGER (enum)
		public static final String RINGOUT_CALL_LASTSYNC = "ringout_call_lastsync";       //INTEGER (long)
		public static final String RINGOUT_CALL_ERROR = "ringout_call_error";           //TEXT : error code, example [JEDI, 200]

		public static final String DIRECT_RINGOUT_STATUS = "direct_ringout_status";     //INTEGER (enum)
		public static final String DIRECT_RINGOUT_LASTSYNC = "direct_ringout_lastsync";       //INTEGER (long)
		public static final String DIRECT_RINGOUT_ERROR = "direct_ringout_error";           //TEXT : error code, example [JEDI, 200]

		public static final String CALL_STATUS = "call_status";     //INTEGER (enum)
		public static final String CALL_STATUS_LASTSYNC = "call_status_lastsync";       //INTEGER (long)
		public static final String CALL_STATUS_ERROR = "call_status_error";           //TEXT : error code, example [JEDI, 200]

		public static final String RINGOUT_CANCEL_STATUS = "ringout_cancel_status";     //INTEGER (enum)
		public static final String RINGOUT_CANCEL_LASTSYNC = "ringout_cancel_lastsync";       //INTEGER (long)
		public static final String RINGOUT_CANCEL_ERROR = "ringout_cancel_error";           //TEXT : error code, example [JEDI, 200]

		public static final String DND_STATUS = "dnd_status";     //INTEGER (enum)
		public static final String DND_STATUS_LASTSYNC = "dnd_status_lastsync";       //INTEGER (long)
		public static final String DND_STATUS_ERROR = "dnd_status_error";           //TEXT : error code, example [JEDI, 200]

		public static final String LIST_EXTENSIONS_STATUS = "list_extensions_status";     //INTEGER (enum)
		public static final String LIST_EXTENSIONS_LASTSYNC = "list_extensions_lastsync";       //INTEGER (long)
		public static final String LIST_EXTENSIONS_ERROR = "list_extensions_error";           //TEXT : error code, example [JEDI, 200]
		public static final String SETUP_WIZARD_STATE_STATUS = "setup_wizard_state_status";                       //INTEGER (enum)
		public static final String SETUP_WIZARD_STATE_STATUS_LASTSYNC = "setup_wizard_state_status_lastsync";     //INTEGER (long)
		public static final String SETUP_WIZARD_STATE_STATUS_ERROR = "setup_wizard_state_status_error";           //TEXT : error code, example [JEDI, 200]

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

						+ LOGIN_STATUS + " INTEGER,"
						+ LOGIN_LASTSYNC + " INTEGER,"
						+ LOGIN_ERROR + " TEXT,"
						+ API_VERSION_STATUS + " INTEGER,"
						+ API_VERSION_LASTSYNC + " INTEGER,"
						+ API_VERSION_ERROR + " TEXT,"
						+ ACCOUNT_INFO_STATUS + " INTEGER,"
						+ ACCOUNT_INFO_LASTSYNC + " INTEGER,"
						+ ACCOUNT_INFO_ERROR + " TEXT,"
						+ ALL_CALL_LOGS_STATUS + " INTEGER,"
						+ ALL_CALL_LOGS_LASTSYNC + " INTEGER,"
						+ ALL_CALL_LOGS_ERROR + " TEXT,"
						+ MISSED_CALL_LOGS_STATUS + " INTEGER,"
						+ MISSED_CALL_LOGS_LASTSYNC + " INTEGER,"
						+ MISSED_CALL_LOGS_ERROR + " TEXT,"
						+ CALLER_IDS_STATUS + " INTEGER,"
						+ CALLER_IDS_LASTSYNC + " INTEGER,"
						+ CALLER_IDS_ERROR + " TEXT,"
						+ RINGOUT_CALL_STATUS + " INTEGER,"
						+ RINGOUT_CALL_LASTSYNC + " INTEGER,"
						+ RINGOUT_CALL_ERROR + " TEXT,"
						+ DIRECT_RINGOUT_STATUS + " INTEGER,"
						+ DIRECT_RINGOUT_LASTSYNC + " INTEGER,"
						+ DIRECT_RINGOUT_ERROR + " TEXT,"
						+ CALL_STATUS + " INTEGER,"
						+ CALL_STATUS_LASTSYNC + " INTEGER,"
						+ CALL_STATUS_ERROR + " TEXT,"
						+ RINGOUT_CANCEL_STATUS + " INTEGER,"
						+ RINGOUT_CANCEL_LASTSYNC + " INTEGER,"
						+ RINGOUT_CANCEL_ERROR + " TEXT,"
						+ DND_STATUS + " INTEGER,"
						+ DND_STATUS_LASTSYNC + " INTEGER,"
						+ DND_STATUS_ERROR + " TEXT,"
						+ LIST_EXTENSIONS_STATUS + " INTEGER,"
						+ LIST_EXTENSIONS_LASTSYNC + " INTEGER,"
						+ LIST_EXTENSIONS_ERROR + " TEXT,"
						+ SETUP_WIZARD_STATE_STATUS + " INTEGER,"
						+ SETUP_WIZARD_STATE_STATUS_LASTSYNC + " INTEGER,"
						+ SETUP_WIZARD_STATE_STATUS_ERROR + " TEXT"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class AppConfigTable extends RCMDbTable implements BaseColumns {

		private AppConfigTable() {
		}

		private static final AppConfigTable sInstance = new AppConfigTable();

		static AppConfigTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "APP_CONFIG";

		/* Columns */
		public static final String ENV_NAME = "env_name";
		public static final String CONFIG_REVISION = "config_revision";                           //INTEGER
		public static final String URL_JEDI = "url_jedi";                                          //TEXT
		public static final String URL_REST = "url_rest";                                         //TEXT
		public static final String URL_MSGSYNC = "url_msgsync";                                      //TEXT
		public static final String URL_SIGNUP = "url_signup";                                     //TEXT
		public static final String URL_SETUP = "url_setup";                                       //TEXT
		public static final String URL_SETUP_SIGNUP = "url_setup_signup";                         //TEXT
		public static final String URL_WEBSETTING_BASE = "url_websettings_base";                  //TEXT
		public static final String IS_SETUP_OVERLOAD_ENABLED = "is_setup_overload_enabled";       //INTEGER(BOOLEAN)
		public static final String URL_HTTPREG = "url_httpreg";                                      //TEXT
		public static final String URL_SIP_PROVIDER = "url_sip_provider";                          //TEXT
		public static final String URI_SIP_OUTBOUND_PROXY = "uri_sip_outbound_proxy";              //TEXT

		// For http://jira.ringcentral.com/browse/UIA-22188
		public static final String URL_WEBSETTING_BASE_ENHANCE = "url_websettings_base_enhance";  //TEXT

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ CONFIG_REVISION + " INTEGER,"
						+ URL_JEDI + " TEXT,"
						+ URL_REST + " TEXT,"
						+ URL_MSGSYNC + " TEXT,"
						+ URL_SIGNUP + " TEXT,"
						+ URL_SETUP + " TEXT,"
						+ URL_SETUP_SIGNUP + " TEXT,"
						+ URL_WEBSETTING_BASE + " TEXT,"
						+ IS_SETUP_OVERLOAD_ENABLED + " INTEGER,"
						+ URL_HTTPREG + " TEXT,"
						+ URL_SIP_PROVIDER + " TEXT,"
						+ URI_SIP_OUTBOUND_PROXY + " TEXT,"
						+ URL_WEBSETTING_BASE_ENHANCE + " TEXT,"
						+ ENV_NAME + " TEXT"
						+ ");";


		private static final String INIT_TABLE_STMT =
				"INSERT INTO " + TABLE_NAME + " ("
						+ CONFIG_REVISION + ','
						+ URL_JEDI + ','
						+ URL_REST + ','
						+ URL_MSGSYNC + ','
						+ URL_SIGNUP + ','
						+ URL_SETUP + ','
						+ URL_SETUP_SIGNUP + ','
						+ URL_WEBSETTING_BASE + ','
						+ IS_SETUP_OVERLOAD_ENABLED + ','
						+ URL_HTTPREG + ','
						+ URL_SIP_PROVIDER + ','
						+ URI_SIP_OUTBOUND_PROXY + ","
						+ URL_WEBSETTING_BASE_ENHANCE + ","
						+ ENV_NAME
						+ ") VALUES (NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL)";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			db.execSQL(INIT_TABLE_STMT);
		}
	}

	public static final class MobileWebEnhanceURLParamTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private MobileWebEnhanceURLParamTable() {
		}

		private static final MobileWebEnhanceURLParamTable sInstance = new MobileWebEnhanceURLParamTable();

		static MobileWebEnhanceURLParamTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "MOBILE_WEB_ENHANCE_URL_PARAME";

		public static final String MOBILE_WEB_URL = "MOBILE_WEB_URL";            //TEXT
		public static final String LOCALE_ID = "locale_id";              //TEXT
		public static final String PHONE_SYSTEM = "phone_system";           //TEXT
		public static final String USER_SETTING = "user_setting";           //TEXT
		public static final String BILLING = "billing";                //TEXT
		public static final String INTERNATIONAL_CALLING = "international_calling";  //TEXT
		public static final String TELL_FRIEND = "tell_friend";            //TEXT

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ MOBILE_WEB_URL + " TEXT,"
						+ LOCALE_ID + " TEXT,"
						+ PHONE_SYSTEM + " TEXT,"
						+ USER_SETTING + " TEXT,"
						+ BILLING + " TEXT,"
						+ TELL_FRIEND + " TEXT,"
						+ INTERNATIONAL_CALLING + " TEXT"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	public static final class CloudStorageDropBoxTable extends RCMDbTable implements BaseColumns, RCMColumns {
		private CloudStorageDropBoxTable() {
		}

		private static final CloudStorageDropBoxTable sInstance = new CloudStorageDropBoxTable();

		static CloudStorageDropBoxTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CLOUD_STORAGE_DROPBOX_TABLE";

		/* Columns */
		public static final String DROPBOX_APP_KEY = "dropbox_app_key";
		public static final String DROPBOX_APP_SECRET = "dropbox_app_secret";

		public static final String DROPBOX_ACCESS_KEY_NAME = "dropbox_access_key_name";
		public static final String DROPBOX_ACCESS_SECRET_NAME = "dropbox_access_secret_name";
		public static final String DROPBOX_ACCOUNT_NAME = "dropbox_account_name";
		public static final String DROPBOX_ACCOUNT_USERID = "dropbox_account_userid";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ DROPBOX_APP_KEY + " TEXT,"
						+ DROPBOX_APP_SECRET + " TEXT,"
						+ DROPBOX_ACCESS_KEY_NAME + " TEXT,"
						+ DROPBOX_ACCESS_SECRET_NAME + " TEXT,"
						+ DROPBOX_ACCOUNT_NAME + " TEXT,"
						+ DROPBOX_ACCOUNT_USERID + " INTEGER"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	/**
	 * Attachment Table	(Store file path)
	 *
	 * @author simon.yang
	 */
	public static final class RCMAttachmentTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private RCMAttachmentTable() {
		}

		private static final RCMAttachmentTable sInstance = new RCMAttachmentTable();

		static RCMAttachmentTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "RCM_ATTACHMENT_TABLE";

		/* Columns */
		public static final String ATT_ID = "ATT_ID";                // TEXT
		public static final String FILE_NAME = "FILE_NAME";                // TEXT
		public static final String FILE_PATH = "FILE_PATH";                // TEXT
		public static final String FILE_SIZE = "FILE_SIZE";                // LONG
		public static final String FILE_KIND = "FILE_KIND";                // INTEGER(enum)
		public static final String FILE_LAST_MODIFIED = "FILE_LAST_MODIFIED";    // LONG
		public static final String PARENT_ATT_ID = "PARENT_ATT_ID";            // TEXT
		public static final String FULL_NAME = "FULL_NAME";                // TEXT
		public static final String SOURCES = "SOURCES";                // INTEGER(enum)
		public static final String FILE_URI = "FILE_URI";                // TEXT
		public static final String IS_LEFT = "IS_LEFT";                // INTEGER(boolean)
		public static final String CREATED = "CREATED";                // LONG
		public static final String ATT_MAIN_PATH = "ATT_MAIN_PATH";            // INTEGER(enum)
		public static final String REF_COUNT = "REF_COUNT";                // INTEGER
        /* Columns */

		public static final int SOURCES_RC_DOC = 0;
		public static final int SOURCES_CLOUD = 1;
		public static final int SOURCES_FORWARD = 8;
		public static final int SOURCES_IMG = 9;

		public static final int SOURCES_DROP_BOX = 2;
		public static final int SOURCES_BOX = 3;
		public static final int SOURCES_GOOGLE_DRIVE = 4;

		public static final int ATT_IS_NOT_LEFT = 0;
		public static final int ATT_IS_LEFT = 1;

		public static final int MAIN_PATH_RC = 1;
		public static final int MAIN_PATH_DRAFT = 2;


		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ ATT_ID + " TEXT UNIQUE ON CONFLICT REPLACE,"
						+ FILE_NAME + " TEXT,"
						+ FILE_PATH + " TEXT,"
						+ FILE_SIZE + " TEXT,"
						+ FILE_KIND + " INTEGER,"
						+ FILE_LAST_MODIFIED + " INTEGER,"
						+ PARENT_ATT_ID + " TEXT,"
						+ FULL_NAME + " TEXT,"
						+ SOURCES + " INTEGER,"
						+ FILE_URI + " TEXT,"
						+ IS_LEFT + " INTEGER DEFAULT " + ATT_IS_LEFT + ","
						+ CREATED + " INTEGER DEFAULT " + System.currentTimeMillis() + ","
						+ ATT_MAIN_PATH + " INTEGER DEFAULT " + MAIN_PATH_RC + ","
						+ REF_COUNT + " INTEGER DEFAULT 0"
						+ ");";

		private static final String CREATE_INDEX_STMT =
				"CREATE INDEX IF NOT EXISTS IDX_ATT_ID ON " + TABLE_NAME
						+ "(" + ATT_ID + " DESC );";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			db.execSQL(CREATE_INDEX_STMT);
		}

	}

	/**
	 * Outbox Table
	 *
	 * @author simon.yang
	 */
	public static final class RCMOutboxTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private RCMOutboxTable() {
		}

		private static final RCMOutboxTable sInstance = new RCMOutboxTable();

		public static RCMOutboxTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "RCM_OUTBOX_TABLE";

		/* Columns */
		public static final String OUTBOX_ID = "OUTBOX_ID";                // TEXT
		public static final String MSG_TYPE = "MSG_TYPE";                // INTEGER(enum)
		public static final String CREATED = "CREATED";                // LONG
		public static final String SEND_STATUS = "SEND_STATUS";            // INTEGER(enum)
		public static final String FROM_PHONE = "FROM_PHONE";                // TEXT
		public static final String FROM_DISPLAY_NAME = "FROM_DISPLAY_NAME";        // TEXT
		public static final String COVER_PAGE_ID = "COVER_PAGE_ID";            // INTEGER(enum)
		public static final String SUBJECT = "SUBJECT";                // TEXT
		public static final String RESOLUTION = "RESOLUTION";                // INTEGER(enum)
		public static final String QUEUE_SEQUENCE = "QUEUE_SEQUENCE";            // INTEGER
		public static final String ERROR_CODE = "ERROR_CODE";                // INTEGER
		public static final String IS_SEND_NOTIFICATION = "IS_SEND_NOTIFICATION";    // INTEGER(boolean)
		public static final String AVAILABILITY = "AVAILABILITY";            // INTEGER(boolean)
		public static final String COVER_PAGE_LOCALE    = "COVER_PAGE_LOCALE";  // TEXT
		public static final String READ_STATUS = "READ_STATUS";// INTEGER(BOOLEAN)

        /* Columns */

		public static final int MSG_TYPE_FAX = MessagesTable.MSG_TYPE_FAX;

		public static final int SEND_STATUS_INIT = 0;
		public static final int SEND_STATUS_DRAFT = 1;
		public static final int SEND_STATUS_IN_QUEUE = 2;
		public static final int SEND_STATUS_SENDING = 3;
		public static final int SEND_STATUS_SEND_FAILED = 4;
		public static final int SEND_STATUS_CANCELING = 5;
		public static final int SEND_STATUS_DELETING = 6;
		public static final int SEND_STATUS_SUCCESS = 7;
		public static final int SEND_STATUS_TEMP = 8;

		public static final int COVER_PAGE_NONE = 0;
		public static final int COVER_PAGE_CONTEMPORARY = 1;
		public static final int COVER_PAGE_EXPRESS = 2;
		public static final int COVER_PAGE_FORMAL = 3;
		public static final int COVER_PAGE_JAZZY = 4;

		public static final int RESOLUTION_LOW = 0;
		public static final int RESOLUTION_HIGH = 1;

		public static final int ERROR_CODE_NO_NETWORK = RestApiErrorCodes.NETWORK_NOT_AVAILABLE;
		public static final int ERROR_CODE_AIRPLANE_MODE = RestApiErrorCodes.NETWORK_NOT_AVAILABLE_AIRPLANE_ON;
		public static final int ERROR_CODE_NO_TO_PHONE = RestApiErrorCodes.NO_TO_PHONE;
		public static final int ERROR_CODE_NO_ATTACHMENT = RestApiErrorCodes.NO_ATTACHMENT;
		public static final int ERROR_CODE_REST_SESSION_ERROR = RestApiErrorCodes.INVALID_SESSION_STATE;
		public static final int ERROR_CODE_APP_EXIT = RestApiErrorCodes.APP_EXIT;
		public static final int ERROR_CODE_UNKNOW = -99;

		public static final int NOT_SEND_NOTIFICATION = 0;
		public static final int SEND_NOTIFICATION = 1;

		public static final int AVAILABILITY_DELETED = 0;
		public static final int AVAILABILITY_ALIVE = 1;

		public static final int READ_STATUS_READ = 1;
		public static final int READ_STATUS_UNREAD = 0;

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ OUTBOX_ID + " TEXT UNIQUE ON CONFLICT REPLACE,"
						+ MSG_TYPE + " INTEGER DEFAULT " + MSG_TYPE_FAX + ","
						+ CREATED + " INTEGER,"
						+ SEND_STATUS + " INTEGER,"
						+ FROM_PHONE + " TEXT,"
						+ FROM_DISPLAY_NAME + " TEXT,"
						+ COVER_PAGE_ID + " INTEGER DEFAULT " + COVER_PAGE_NONE + ","
						+ SUBJECT + " TEXT,"
						+ RESOLUTION + " INTEGER DEFAULT " + RESOLUTION_HIGH + ","
						+ QUEUE_SEQUENCE + " INTEGER,"
						+ ERROR_CODE + " INTEGER DEFAULT 0,"
						+ IS_SEND_NOTIFICATION + " INTEGER DEFAULT " + NOT_SEND_NOTIFICATION + ","
						+ AVAILABILITY + " INTEGER DEFAULT " + AVAILABILITY_ALIVE + ","
						+ COVER_PAGE_LOCALE + " TEXT " + ","
						+ READ_STATUS +" INTEGER DEFAULT "+ READ_STATUS_READ
						+ ");";

		private static final String CREATE_INDEX_STMT =
				"CREATE INDEX IF NOT EXISTS IDX_OUTBOX_ID ON " + TABLE_NAME
						+ "(" + OUTBOX_ID + " DESC );";

		@Override
		public String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			db.execSQL(CREATE_INDEX_STMT);
		}

	}

	/**
	 * Outbox's child table(store to phone)
	 *
	 * @author simon.yang
	 */
	public static final class RCMOutboxToPhoneTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private RCMOutboxToPhoneTable() {
		}

		private static final RCMOutboxToPhoneTable sInstance = new RCMOutboxToPhoneTable();

		public static RCMOutboxToPhoneTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "RCM_OUTBOX_TO_PHONE_TABLE";

		/* Columns */
		public static final String FK_OUTBOX_ID = "FK_OUTBOX_ID";                // TEXT
		public static final String CONTACT_ID = "CONTACT_ID";                    // LONG
		public static final String LOOKUP_KEY = "LOOKUP_KEY";                    // TEXT
		public static final String NUMBER = "NUMBER";                        // TEXT
		public static final String PHONE_DATA_ID = "PHONE_DATA_ID";                // LONG
		public static final String DISPLAY_NAME = "DISPLAY_NAME";                // TEXT
		public static final String FIRST_NAME = "FIRST_NAME";                    // TEXT
		public static final String LAST_NAME = "LAST_NAME";                    // TEXT
		public static final String BIND_HAS_CONTACT = "BIND_HAS_CONTACT";            // INTEGER (boolean)
		public static final String BIND_CONTACT_TYPE = "BIND_CONTACT_TYPE";   // INTEGER (MENU)
		public static final String RCM_NUMBER_IS_VALID = "RCM_NUMBER_IS_VALID";        // INTEGER (boolean)
		public static final String PHONE_NUMBER_COUNTRY    = "PHONE_NUMBER_COUNTRY";  // TEXT
        /* Columns */

		public static final int NUMBER_ISNOT_VALID = 0;
		public static final int NUMBER_IS_VALID = 1;

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ FK_OUTBOX_ID + " TEXT,"
						+ CONTACT_ID + " INTEGER,"
						+ LOOKUP_KEY + " TEXT,"
						+ NUMBER + " TEXT,"
						+ PHONE_DATA_ID + " INTEGER,"
						+ DISPLAY_NAME + " TEXT,"
						+ FIRST_NAME + " TEXT,"
						+ LAST_NAME + " TEXT,"
						+ BIND_HAS_CONTACT + " INTEGER,"
						+ BIND_CONTACT_TYPE + " INTEGER DEFAULT "+ Contact.ContactType.UNKNOW.ordinal() + ','
						+ RCM_NUMBER_IS_VALID + " INTEGER DEFAULT " + NUMBER_IS_VALID + ","
						+ PHONE_NUMBER_COUNTRY + " TEXT DEFAULT '1'"
						+ ");";

		private static final String CREATE_INDEX_STMT =
				"CREATE INDEX IF NOT EXISTS IDX_FK_OUTBOX_ID ON " + TABLE_NAME
						+ "(" + FK_OUTBOX_ID + " DESC );";

		@Override
		public String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			db.execSQL(CREATE_INDEX_STMT);
		}
	}

	/**
	 * Attachment and Outbox's associative table
	 *
	 * @author simon.yang
	 */
	public static final class RCMAttOutboxTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private RCMAttOutboxTable() {
		}

		private static final RCMAttOutboxTable sInstance = new RCMAttOutboxTable();

		public static RCMAttOutboxTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "RCM_ATT_OUTBOX_TABLE";


		/* Columns */
		public static final String FK_ATT_ID = "FK_ATT_ID";            // TEXT
		public static final String FK_OUTBOX_ID = "FK_OUTBOX_ID";        // TEXT
		public static final String ATT_SEQUENCE = "ATT_SEQUENCE";        // INTEGER
        /* Columns */

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ FK_ATT_ID + " TEXT,"
						+ FK_OUTBOX_ID + " TEXT,"
						+ ATT_SEQUENCE + " INTEGER"
						+ ");";

		private static final String CREATE_INDEX_FK_OUTBOX_ID_STMT =
				"CREATE INDEX IF NOT EXISTS IDX_FK_OUTBOX_ID ON " + TABLE_NAME
						+ "(" + FK_OUTBOX_ID + " DESC );";

		private static final String CREATE_INDEX_FK_ATT_ID_STMT =
				"CREATE INDEX IF NOT EXISTS IDX_FK_ATT_ID ON " + TABLE_NAME
						+ "(" + FK_ATT_ID + " DESC );";

		@Override
		public String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
			db.execSQL(CREATE_INDEX_FK_OUTBOX_ID_STMT);
			db.execSQL(CREATE_INDEX_FK_ATT_ID_STMT);
		}

		public static final String FILE_NAME = "FILE_NAME";                //  This isn't a database field

	}

	public static final class CloudStorageBoxTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private CloudStorageBoxTable() {
		}

		private static final CloudStorageBoxTable sInstance = new CloudStorageBoxTable();

		static CloudStorageBoxTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CLOUD_STORAGE_BOX_TABLE";

		/* Columns */
		public static final String BOX_APP_KEY = "box_app_key";

		public static final String BOX_ACCOUNT_AUTH_TOKEN = "box_account_auth_token";
		public static final String BOX_ACCOUNT_LOGIN_NAME = "box_account_login_name";
		public static final String BOX_ACCOUNT_EMAIL = "box_account_email";
		public static final String BOX_ACCOUNT_USERID = "box_account_userid";
		public static final String BOX_ACCOUNT_EXPIRES_IN = "box_account_expires_in";
		public static final String BOX_ACCOUNT_TOKEN_TYPE = "box_account_token_type";
		public static final String BOX_ACCOUNT_REFRESH_TOKEN = "box_account_refresh_token";
		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ BOX_APP_KEY + " TEXT,"
						+ BOX_ACCOUNT_AUTH_TOKEN + " TEXT,"
						+ BOX_ACCOUNT_LOGIN_NAME + " TEXT,"
						+ BOX_ACCOUNT_EMAIL + " TEXT,"
						+ BOX_ACCOUNT_EXPIRES_IN + " TEXT,"
						+ BOX_ACCOUNT_TOKEN_TYPE + " TEXT,"
						+ BOX_ACCOUNT_REFRESH_TOKEN + " TEXT,"
						+ BOX_ACCOUNT_USERID + " INTEGER"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class CloudStorageGoogleDriveTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private CloudStorageGoogleDriveTable() {
		}

		private static final CloudStorageGoogleDriveTable sInstance = new CloudStorageGoogleDriveTable();

		static CloudStorageGoogleDriveTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CLOUD_STORAGE_GOOGLE_DRIVE_TABLE";

		/* Columns */
		public static final String ACCOUNT_EMAIL = "account_email";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ ACCOUNT_EMAIL + " TEXT"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class DIDFavoriteTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private DIDFavoriteTable() {
		}

		private static final DIDFavoriteTable sInstance = new DIDFavoriteTable();

		static DIDFavoriteTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "DID_FAVORITE_TABLE";

		/* Columns */
		public static final String PHONE_NUMBER = "phone_number";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ PHONE_NUMBER + " INTEGER"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class ContactPersonalGroupTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private ContactPersonalGroupTable() {
		}

		private static final ContactPersonalGroupTable sInstance = new ContactPersonalGroupTable();

		static ContactPersonalGroupTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CONTACT_PERSONAL_GROUP_TABLE";

		/* Columns */
		public static final String GROUP_TYPE = "GROUP_TYPE";
		public static final String GROUP_ACCOUNT = "GROUP_ACCOUNT";
		public static final String GROUP_ID = "GROUP_ID";
		public static final String GROUP_STATUS = "GROUP_STATUS";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ GROUP_TYPE + " TEXT,"
						+ GROUP_ACCOUNT + " TEXT,"
						+ GROUP_ID + " INTEGER,"
						+ GROUP_STATUS + " INTEGER"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

		@Override
		void initTableContent(SQLiteDatabase db) {
			try {
				Resources resources = RingCentralApp.getContextRC().getResources();
				String[] groupTitleArray = resources.getStringArray(R.array.config_group_title);
				String[] groupTypeArray = resources.getStringArray(R.array.config_group_filter);
				final String sql = "UPDATE " + TABLE_NAME + " SET " + GROUP_TYPE + " = ? WHERE " + GROUP_TYPE + " = ? ";
				for (int i = 0; i < groupTitleArray.length; i++) {
					db.execSQL(sql, new Object[] { groupTypeArray[i], groupTitleArray[i] });
				}
			} catch (Exception e) {
				MktLog.e(TAG, "ContactPersonalGroupTable -> initTableContent: " + e.getMessage());
			}

		}
	}

	public static final class PresenceTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private PresenceTable() {
		}

		private static final PresenceTable sInstance = new PresenceTable();

		public static PresenceTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "RCM_PRESENCE";

		/* Columns */
		public static final String EXT_PIN = "EXT_PIN";
		public static final String EXT_MAILBOX_ID = "EXT_MAILBOXID";        // INTEGER (long)
		public static final String TELEPHONY_STATUS = "TELEPHONY_STATUS";        // TEXT
		public static final String DND_STATUS = "DND_STATUS";            // TEXT
		public static final String PRESENCE_STATUS = "PRESENCE_STATUS";        // TEXT
		public static final String USER_STATUS = "USER_STATUS";            // TEXT
		public static final String CUSTOM_MESSAGE_BYTE = "CUSTOM_MESSAGE_BYTE"; // Blob

		/**
		 * String value definition for TELEPHONY_STATUS
		 */
		public static final String TELEPHONY_STATUS_NOCALL = "NoCall";
		public static final String TELEPHONY_STATUS_RINGING = "Ringing";
		public static final String TELEPHONY_STATUS_CALLCONNECTED = "CallConnected";
		public static final String TELEPHONY_STATUS_ONHOLD = "OnHold";

		/**
		 * String value definition for DND_STATUS
		 */
		public static final String DND_STATUS_ACCEPT_ALL = "TakeAllCalls";
		public static final String DND_STATUS_ACCEPT_NONE = "DoNotAcceptAnyCalls";
		public static final String DND_STATUS_NO_DEPARTMENT = "DoNotAcceptDepartmentCalls";

		/**
		 * String value definition for PRESENCE_STATUS and USER_STATUS
		 */
		public static final String PRESENCE_STATUS_OFFLINE = "Offline";
		public static final String PRESENCE_STATUS_BUSY = "Busy";
		public static final String PRESENCE_STATUS_AVAILABLE = "Available";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ EXT_MAILBOX_ID + " INTEGER,"
						+ EXT_PIN + " TEXT,"
						+ TELEPHONY_STATUS + " TEXT,"
						+ DND_STATUS + " TEXT,"
						+ PRESENCE_STATUS + " TEXT,"
						+ USER_STATUS + " TEXT,"
						+ CUSTOM_MESSAGE_BYTE + " BLOB"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	public static final class ForwardingNumberTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private ForwardingNumberTable() {
		}

		private static final ForwardingNumberTable sInstance = new ForwardingNumberTable();

		static ForwardingNumberTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "FORWARDING_NUMBER";

		/* Columns */
		public static final String REST_LABLE = "REST_LABEL";
		public static final String REST_NUMBER = "REST_NUMBER";
		public static final String REST_FLIP_NUMBER = "REST_FLIP_NUMBER";
		public static final String REST_FEATURES_CALL_FLIP = "REST_FEATURES_CALL_FLIP";
		public static final String REST_FEATURES_CALL_FORWARDING = "REST_FEATURES_CALL_FORWARDING";
		public static final String REST_PROCESSED = "REST_PROCESSED";

		private static final String CREATE_TABLE_STMT = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MAILBOX_ID + " INTEGER, "
				+ REST_LABLE + " TEXT, "
				+ REST_NUMBER + " TEXT, "
				+ REST_FLIP_NUMBER + " INTEGER, "
				+ REST_FEATURES_CALL_FLIP + " INTEGER DEFAULT 0, "
				+ REST_FEATURES_CALL_FORWARDING + " INTEGER DEFAULT 0, "
				+ REST_PROCESSED + " INTEGER DEFAULT 1"
				+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class ContactGroupTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private ContactGroupTable() {
		}

		private static final ContactGroupTable sInstance = new ContactGroupTable();

		static ContactGroupTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CONTACT_GROUP";

		/* Columns */
		public static final String CONVERSATION_ID = "conversation_id";
		public static final String GROUP_NAME = "group_name";
		public static final String GROUP_CREATE_TIME = "group_create_time";
		public static final String CONTACT_MEMBER_IDS = "contact_member_ids";
		//		public static final String CONTACT_MEMBER_MAILBOXS			= "contact_member_ids";
		public static final String CONTACT_MEMBER_NUMBERS = "contact_member_numbers";
		public static final String CONTACT_MEMBER_DISPLAY_NAMES = "contact_member_display_names";
		public static final String SEARCH_VALUE = "search_value";

		private static final String CREATE_TABLE_STMT = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MAILBOX_ID + " INTEGER, "
				+ CONVERSATION_ID + " TEXT, "
				+ GROUP_NAME + " TEXT, "
				+ CONTACT_MEMBER_IDS + " TEXT, "
				+ CONTACT_MEMBER_NUMBERS + " TEXT, "
				+ CONTACT_MEMBER_DISPLAY_NAMES + " TEXT, "
				+ GROUP_CREATE_TIME + " INTEGER,"
				+ SEARCH_VALUE + " TEXT"
				+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class SpecialNumberTable extends RCMDbTable implements BaseColumns, RCMColumns {

		private SpecialNumberTable() {
		}

		private static final SpecialNumberTable sInstance = new SpecialNumberTable();

		static SpecialNumberTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "SPECIAL_NUMBER";

		/* Columns */
		public static final String PHONE_NUMBER = "PHONE_NUMBER";
		public static final String DESCRIPTION = "DESCRIPTION";
		public static final String IS_VOIP_ENABLED = "IS_VOIP_ENABLED";
		public static final String VOIP_DISABLED_REASON_ID = "VOIP_DISABLED_REASON_ID";
		public static final String VOIP_DISABLED_REASON_MESSAGE = "VOIP_DISABLED_REASON_MESSAGE";
		public static final String IS_RINGOUT_ENABLED = "IS_RINGOUT_ENABLED";
		public static final String RINGOUT_DISABLED_REASON_ID = "RINGOUT_DISABLED_REASON_ID";
		public static final String RINGOUT_DISABLED_REASON_MESSAGE = "RINGOUT_DISABLED_REASON_MESSAGE";
		public static final String IS_SMS_ENABLED = "IS_SMS_ENABLED";
		public static final String SMS_DISABLED_REASON_ID = "SMS_DISABLED_REASON_ID";
		public static final String SMS_DISABLED_REASON_MESSAGE = "SMS_DISABLED_REASON_MESSAGE";
		public static final String IS_FAXOUT_ENABLED = "IS_FAXOUT_ENABLED";
		public static final String FAXOUT_DISABLED_REASON_ID = "FAXOUT_DISABLED_REASON_ID";
		public static final String FAXOUT_DISABLED_REASON_MESSAGE = "FAXOUT_DISABLED_REASON_MESSAGE";
		public static final String REST_PROCESSED = "REST_processed";       //INTEGER (boolean)

		private static final String CREATE_TABLE_STMT = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MAILBOX_ID + " INTEGER, "
				+ PHONE_NUMBER + " TEXT, "
				+ DESCRIPTION + " TEXT, "
				+ IS_VOIP_ENABLED + " INTEGER DEFAULT 0, "
				+ VOIP_DISABLED_REASON_ID + " TEXT, "
				+ VOIP_DISABLED_REASON_MESSAGE + " TEXT, "
				+ IS_RINGOUT_ENABLED + " INTEGER DEFAULT 0, "
				+ RINGOUT_DISABLED_REASON_ID + " TEXT, "
				+ RINGOUT_DISABLED_REASON_MESSAGE + " TEXT, "
				+ IS_SMS_ENABLED + " INTEGER DEFAULT 0, "
				+ SMS_DISABLED_REASON_ID + " TEXT, "
				+ SMS_DISABLED_REASON_MESSAGE + " TEXT, "
				+ IS_FAXOUT_ENABLED + " INTEGER DEFAULT 0, "
				+ FAXOUT_DISABLED_REASON_ID + " TEXT, "
				+ FAXOUT_DISABLED_REASON_MESSAGE + " TEXT, "
				+ REST_PROCESSED + " INTEGER DEFAULT 0"
				+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}


	public static final class ClientInfoTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private ClientInfoTable() {
		}

		private static final ClientInfoTable sInstance = new ClientInfoTable();

		static ClientInfoTable getInstance() {
			return sInstance;
		}

		public static final String TRIAL_STATUS_NOTEXPIRED = "NotExpired";
		public static final String TRIAL_STATUS_EXPIREDINXDAYS = "ExpiredInXDays";
		public static final String TRIAL_STATUS_EXPIRED = "Expired";

		private static final String TABLE_NAME = "ClientInfoTable";

		public static final String EXPRESSSETUPMOBILE = "expressSetupMobile";
		public static final String MOBILEWEBBILLING = "mobileWebBilling";
		public static final String MOBILEWEBPHONESYSTEM = "mobileWebPhoneSystem";
		public static final String MOBILEWEBUSERSETTINGS = "mobileWebUserSettings";
		public static final String MOBILEWEBTELLAFRIEND = "mobileWebTellAFriend";
		public static final String MOBILEWEBINTERNATIONALCALLING = "mobileWebInternationalCalling";
		public static final String MOBILEWEBCALLHANDLING = "mobileWebCallHandling";
		public static final String MOBILEWEBTRIALUPGRADE = "mobileWebTrialUpgrade";

		public static final String EULA = "eula";
		public static final String EMERGENCYDISCLAIMER = "emergencyDisclaimer";
		public static final String ASSETSHOME = "assetsHome";

		public static final String MOBILEWEBREPORTING = "MOBILE_WEB_REPORTING";            //TEXT
		public static final String WEBURIS_EXPIRETIME = "EXPIRE_TIME";            //TEXT

		public static final String TRIAL_STATE = "TRIAL_STATE";            //TEXT
		public static final String TRIAL_EXPIRETIME = "TRIAL_EXPIRETIME";            //TEXT
		public static final String MOBILEWEBRESETPASSWORD = "mobileWebResetPassword";
		public static final String MOBILEWEBUSERS = "mobileWebUsers";
		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"

						+ EXPRESSSETUPMOBILE + " TEXT,"
						+ MOBILEWEBBILLING + " TEXT,"
						+ MOBILEWEBPHONESYSTEM + " TEXT,"
						+ MOBILEWEBUSERSETTINGS + " TEXT,"
						+ MOBILEWEBTELLAFRIEND + " TEXT,"
						+ MOBILEWEBINTERNATIONALCALLING + " TEXT,"
						+ MOBILEWEBCALLHANDLING + " TEXT,"
						+ MOBILEWEBRESETPASSWORD + " TEXT,"
						+ MOBILEWEBREPORTING + " TEXT,"
						+ MOBILEWEBUSERS + " TEXT,"
						+ MOBILEWEBTRIALUPGRADE + " TEXT,"
						+ WEBURIS_EXPIRETIME + " INTEGER DEFAULT 0,"

						+ TRIAL_STATE + " TEXT,"
						+ TRIAL_EXPIRETIME + " INTEGER DEFAULT 0,"

						+ EULA + " TEXT,"
						+ EMERGENCYDISCLAIMER + " TEXT,"
						+ ASSETSHOME + " TEXT"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	public static final class VersionControlTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private VersionControlTable() {
		}

		private static final VersionControlTable sInstance = new VersionControlTable();

		static VersionControlTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "VersionControlTable";

		public static final String SKIP = "skip";
		public static final String REMIND = "remind_me";
		public static final String DOWNLOADNEWVERSIONURI = "downloadNewVersionUri";
		public static final String VERSION_FROM_SERVER = "version_from_server";
		public static final String LASTSHOWREMIND_MEDIALOGTIME = "lastShowRemind_meDialogTime";            //INTEGER

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER UNIQUE ON CONFLICT REPLACE,"
						+ SKIP + " INTEGER DEFAULT 0, "
						+ REMIND + " INTEGER DEFAULT 0, "
						+ DOWNLOADNEWVERSIONURI + " TEXT,"
						+ VERSION_FROM_SERVER + " TEXT,"
						+ LASTSHOWREMIND_MEDIALOGTIME + " INTEGER DEFAULT 0"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	public static final class LanguagesTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private static final String TABLE_NAME = "LanguagesTable";

		public static final String LANG_ID = "lang_id";
		public static final String LANG_URI = "lang_uri";
		public static final String LANG_NAME = "lang_name";
		public static final String LANG_ISO_CODE = "lang_isoCode";
		public static final String LANG_LOCALE_CODE = "lang_localeCode";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ LANG_ID + " INTEGER,"
						+ LANG_URI + " TEXT, "
						+ LANG_NAME + " TEXT, "
						+ LANG_ISO_CODE + " TEXT, "
						+ LANG_LOCALE_CODE + " TEXT"
						+ ");";

		private LanguagesTable() {
		}

		private static final LanguagesTable sInstance = new LanguagesTable();

		static LanguagesTable getInstance() {
			return sInstance;
		}

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	public static final class GeneralSettingsTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private GeneralSettingsTable() {
		}

		private static final GeneralSettingsTable sInstance = new GeneralSettingsTable();

		static GeneralSettingsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "GeneralSettingsTable";

		/**
		 * Table columns
		 */
		public static final String KEY = "key";
		public static final String VALUE = "value";

		/**
		 * Table columns end
		 */
		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ KEY + " TEXT PRIMARY KEY NOT NULL, "
						+ VALUE + " TEXT DEFAULT '')";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class UserSettingsTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private UserSettingsTable() {
		}

		private static final UserSettingsTable sInstance = new UserSettingsTable();

		static UserSettingsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "UserSettingsTable";

		public static final String KEY = "key";
		public static final String VALUE = "value";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ MAILBOX_ID + " INTEGER NOT NULL, "
						+ KEY + " TEXT NOT NULL, "
						+ VALUE + " TEXT,"
						+ " PRIMARY KEY (" + MAILBOX_ID + ", " + KEY + "))";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class DialingPlansTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private static final String TABLE_NAME = "DialingPlansTable";

		public static final String URI = "uri";
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String ISO_CODE = "iso_code";
		public static final String CALLING_CODE = "calling_code";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER, "
						+ URI + " TEXT, "
						+ ID + " INT,"
						+ NAME + " TEXT, "
						+ ISO_CODE + " TEXT, "
						+ CALLING_CODE + " TEXT "
						+ ");";

		private DialingPlansTable() {
		}

		private static final DialingPlansTable sInstance = new DialingPlansTable();

		static DialingPlansTable getInstance() {
			return sInstance;
		}

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	public enum CloudContactSyncStatus {
		Synced,
		NeedSync,
		SyncFailed,
		Deleted
	}

	public static final class PersonalContactsTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private PersonalContactsTable() {}
		private static final PersonalContactsTable sInstance = new PersonalContactsTable();
		static PersonalContactsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CLOUD_PERSONAL_CONTACTS";
		public static final String ID = "id";                                           //INTEGER, CONTACT ID
		public static final String URI = "uri";                                         //TEXT
		public static final String AVAILABILITY = "availability";                       //TEXT
		public static final String DISPLAY_NAME = "displayName";                        //TEXT
		public static final String FIRST_NAME = "firstName";                            //TEXT
		public static final String LAST_NAME = "lastName";                              //TEXT
		public static final String MIDDLE_NAME = "middleName";                          //TEXT
		public static final String NICK_NAME = "nickName";                              //TEXT
		public static final String COMPANY = "company";                                 //TEXT
		public static final String JOB_TITLE = "jobTitle";                             //TEXT
		public static final String BIRTHDAY = "birthday";                               //TEXT
		public static final String WEB_PAGE = "webPage";                                //TEXT
		public static final String NOTES = "notes";                                     //TEXT
		public static final String SYNC_STATUS = "syncStatus";                          //INTEGER
		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ ID + " INTEGER DEFAULT 0,"
						+ URI + " TEXT,"
						+ AVAILABILITY + " TEXT,"
						+ DISPLAY_NAME + " BLOB,"
						+ FIRST_NAME + " BLOB,"
						+ LAST_NAME + " BLOB,"
						+ MIDDLE_NAME + " BLOB,"
						+ NICK_NAME + " BLOB,"
						+ COMPANY + " BLOB,"
						+ JOB_TITLE + " BLOB,"
						+ BIRTHDAY + " TEXT,"
						+ WEB_PAGE + " BLOB,"
						+ NOTES + " BLOB,"
						+ SYNC_STATUS + " INTEGER DEFAULT " + CloudContactSyncStatus.Synced.ordinal()
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class PersonalPhoneNumberTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private PersonalPhoneNumberTable() {}

		private static final PersonalPhoneNumberTable sInstance = new PersonalPhoneNumberTable();

		static PersonalPhoneNumberTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CLOUD_PERSONAL_PHONE_NUMBER";

		/* Columns */
		public static final String ID = "id";                                           //INTEGER CONTACT ID
		public static final String PHONE_NUMBER = "phoneNumber";                        //TEXT
		public static final String PHONE_TYPE = "phoneType";                            //INTEGER

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ ID + " INTEGER,"
						+ PHONE_NUMBER + " TEXT,"
						+ PHONE_TYPE + " INTEGER"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class PersonalEmailTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private PersonalEmailTable() {}
		private static final PersonalEmailTable sInstance = new PersonalEmailTable();
		static PersonalEmailTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CLOUD_PERSONAL_EMAIL";
		public static final String ID = "id";                                           //INTEGER   CONTACT ID
		public static final String EMAIL = "email";                                     //TEXT
		public static final String TYPE = "type";                                       //TEXT

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ ID + " INTEGER,"
						+ EMAIL + " BLOB,"
						+ TYPE + " TEXT"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class PersonalAddressTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private PersonalAddressTable() {}

		private static final PersonalAddressTable sInstance = new PersonalAddressTable();

		static PersonalAddressTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CLOUD_PERSONAL_ADDRESS";

		/* Columns */
		public static final String ID = "id";                                    //INTEGER CONTACT ID
		public static final String ADDRESS_COUNTRY = "country";                  //TEXT
		public static final String ADDRESS_STATE = "state";                      //TEXT
		public static final String ADDRESS_CITY = "city";                        //TEXT
		public static final String ADDRESS_STREET = "street";                    //TEXT
		public static final String ADDRESS_ZIP = "zip";                          //TEXT
		public static final String ADDRESS_TYPE = "type";                        //INTEGER

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ ID + " INTEGER,"
						+ ADDRESS_COUNTRY + " BLOB,"
						+ ADDRESS_STATE + " BLOB,"
						+ ADDRESS_CITY + " BLOB,"
						+ ADDRESS_STREET + " BLOB,"
						+ ADDRESS_ZIP + " BLOB,"
						+ ADDRESS_TYPE + " INTEGER"
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}


	public static final class PermissionsTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private PermissionsTable() {
		}

		private static final PermissionsTable sInstance = new PermissionsTable();

		static PermissionsTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "PermissionsTable";

		public static final String PERMISSION_ID = "permission_id";
		public static final String PERMISSION_URI = "permission_uri";
		public static final String EFFECTIVEROLE_ID = "effective_role_id";
		public static final String EFFECTIVEROLE_URI = "effective_role_uri";
		public static final String SCOPE = "scope";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ PERMISSION_ID + " TEXT UNIQUE ON CONFLICT REPLACE,"
						+ PERMISSION_URI + " TEXT,"
						+ EFFECTIVEROLE_ID + " TEXT,"
						+ EFFECTIVEROLE_URI + " TEXT,"
						+ SCOPE + " TEXT"
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	public enum CloudFavoriteSyncStatus {
		UNKNOWN,
		CloudTemporary,
		Synced,
		NeedSync,
		Deleted
	}

	public static final class CloudFavoritesTable extends RCMDbTable implements RCMColumns, BaseColumns {
		public CloudFavoritesTable(){

		}

		private static final CloudFavoritesTable sInstance = new CloudFavoritesTable();

		static CloudFavoritesTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CLOUD_FAVORITES";

		/* Columns */
		public static final String CONTACT_TYPE = "contact_type";
		public static final String CONTACT_ID = "contact_id";
		public static final String RCM_SORT = "sort";
		public static final String SYNC_STATUS = "sync_status";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER,"
						+ CONTACT_ID + " INTEGER,"
						+ CONTACT_TYPE + " INTEGER,"
						+ RCM_SORT + " INTEGER,"
						+ SYNC_STATUS + " INTEGER DEFAULT " + CloudFavoriteSyncStatus.UNKNOWN.ordinal()
						+ ");";


		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}


	public static final class CalendarEventAlertTable extends RCMDbTable implements RCMColumns, BaseColumns {

		private CalendarEventAlertTable() {
		}

		private static final CalendarEventAlertTable sInstance = new CalendarEventAlertTable();

		static CalendarEventAlertTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "CalendarEventAlertTable";

		public static final String _ID = "_id";
		public static final String INSTANCE_ID = "instance_id";
		public static final String START_TIME = "start_time";
		public static final String ALARM_TIME = "alarm_time";
		public static final String SOURCE = "source";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ INSTANCE_ID + " INTEGER, "
						+ START_TIME + " INTEGER, "
						+ ALARM_TIME + " INTEGER, "
						+ SOURCE +" INTEGER "
						+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}
	}

	public static final class AccountBadgeTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private AccountBadgeTable() {
		}

		private static final AccountBadgeTable sInstance = new AccountBadgeTable();

		static AccountBadgeTable getInstance() {
			return sInstance;
		}

		private static final String TABLE_NAME = "ACCOUNT_BADGE";

		public static final String _ID = "_id";
		public static final String MESSAGE_BADGE = "message_badge";
		public static final String CALL_LOG_BADGE = "calllog_badge";

		private static final String CREATE_TABLE_STMT =  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MAILBOX_ID + " INTEGER,"
				+ MESSAGE_BADGE + " INTEGER, "
				+ CALL_LOG_BADGE + " INTEGER"
				+ ");";

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	public static final class BLFListTable extends RCMDbTable implements RCMColumns, BaseColumns {
		private static final String TABLE_NAME = "BLF_LIST";

		public static final String URI = "uri";
		public static final String EXTID = "extension_id";
		public static final String EXTNUMBER = "extension_number";
		public static final String TYPE = "type";

		public static final String PARK_LOCATION_TYPE = "ParkLocation";

		private static final String CREATE_TABLE_STMT =
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
						+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ MAILBOX_ID + " INTEGER, "
						+ URI + " TEXT, "
						+ EXTID + " INT,"
						+ EXTNUMBER + " TEXT, "
						+ TYPE + " TEXT"
						+ ");";

		private BLFListTable() {
		}

		private static final BLFListTable sInstance = new BLFListTable();

		static BLFListTable getInstance() {
			return sInstance;
		}

		@Override
		String getName() {
			return TABLE_NAME;
		}

		@Override
		void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_STMT);
		}

	}

	static LinkedHashMap<String, RCMDbTable> sRCMSettingsDbTables = new LinkedHashMap<String, RCMDbTable>();

	static {
		sRCMSettingsDbTables.put(GeneralSettingsTable.TABLE_NAME, GeneralSettingsTable.getInstance());
		sRCMSettingsDbTables.put(UserSettingsTable.TABLE_NAME, UserSettingsTable.getInstance());
	}


	static LinkedHashMap<String, RCMDbTable> sRCMDbTables = new LinkedHashMap<String, RCMDbTable>();

	static {
		sRCMDbTables.put(MailboxCurrentTable.getInstance().getName(), MailboxCurrentTable.getInstance());
		sRCMDbTables.put(DeviceAttribTable.getInstance().getName(), DeviceAttribTable.getInstance());
		sRCMDbTables.put(UserCredentialsTable.getInstance().getName(), UserCredentialsTable.getInstance());
		sRCMDbTables.put(ServiceInfoTable.getInstance().getName(), ServiceInfoTable.getInstance());
		sRCMDbTables.put(AccountInfoTable.getInstance().getName(), AccountInfoTable.getInstance());
		sRCMDbTables.put(ServiceExtensionInfoTable.getInstance().getName(), ServiceExtensionInfoTable.getInstance());
		sRCMDbTables.put(CallerIDsTable.getInstance().getName(), CallerIDsTable.getInstance());
		sRCMDbTables.put(FwNumbersTable.getInstance().getName(), FwNumbersTable.getInstance());
		sRCMDbTables.put(PhoneNumbersTable.getInstance().getName(), PhoneNumbersTable.getInstance());
		sRCMDbTables.put(BlockedNumbersTable.getInstance().getName(), BlockedNumbersTable.getInstance());
		sRCMDbTables.put(ExtensionsTable.getInstance().getName(), ExtensionsTable.getInstance());
		sRCMDbTables.put(FavoritesTable.getInstance().getName(), FavoritesTable.getInstance());
		sRCMDbTables.put(CallLogTable.getInstance().getName(), CallLogTable.getInstance());
		sRCMDbTables.put(MessagesTable.getInstance().getName(), MessagesTable.getInstance());
		sRCMDbTables.put(MessageRecipientsTable.getInstance().getName(), MessageRecipientsTable.getInstance());
		sRCMDbTables.put(MessageListTable.getInstance().getName(), MessageListTable.getInstance());
		sRCMDbTables.put(MessageConversationsTable.getInstance().getName(), MessageConversationsTable.getInstance());
		sRCMDbTables.put(MessageDraftTable.getInstance().getName(), MessageDraftTable.getInstance());
		sRCMDbTables.put(SyncStatusTable.getInstance().getName(), SyncStatusTable.getInstance());
		sRCMDbTables.put(AppConfigTable.getInstance().getName(), AppConfigTable.getInstance());
		sRCMDbTables.put(ConferenceInfoTable.getInstance().getName(), ConferenceInfoTable.getInstance());
		sRCMDbTables.put(MobileWebEnhanceURLParamTable.getInstance().getName(), MobileWebEnhanceURLParamTable.getInstance());
		sRCMDbTables.put(CloudStorageDropBoxTable.getInstance().getName(), CloudStorageDropBoxTable.getInstance());
		sRCMDbTables.put(CloudStorageBoxTable.getInstance().getName(), CloudStorageBoxTable.getInstance());
		sRCMDbTables.put(CloudStorageGoogleDriveTable.getInstance().getName(), CloudStorageGoogleDriveTable.getInstance());
		sRCMDbTables.put(ContactPersonalGroupTable.getInstance().getName(), ContactPersonalGroupTable.getInstance());
		sRCMDbTables.put(ForwardingNumberTable.getInstance().getName(), ForwardingNumberTable.getInstance());
		sRCMDbTables.put(SpecialNumberTable.getInstance().getName(), SpecialNumberTable.getInstance());

		sRCMDbTables.put(RCMAttachmentTable.getInstance().getName(), RCMAttachmentTable.getInstance());
		sRCMDbTables.put(RCMOutboxTable.getInstance().getName(), RCMOutboxTable.getInstance());
		sRCMDbTables.put(RCMOutboxToPhoneTable.getInstance().getName(), RCMOutboxToPhoneTable.getInstance());
		sRCMDbTables.put(RCMAttOutboxTable.getInstance().getName(), RCMAttOutboxTable.getInstance());
		sRCMDbTables.put(DIDFavoriteTable.getInstance().getName(), DIDFavoriteTable.getInstance());
		sRCMDbTables.put(CallLogTokenTable.getInstance().getName(), CallLogTokenTable.getInstance());
		sRCMDbTables.put(PresenceTable.getInstance().getName(), PresenceTable.getInstance());
		sRCMDbTables.put(ContactGroupTable.getInstance().getName(), ContactGroupTable.getInstance());
		sRCMDbTables.put(ClientInfoTable.getInstance().getName(), ClientInfoTable.getInstance());
		sRCMDbTables.put(VersionControlTable.getInstance().getName(), VersionControlTable.getInstance());
		sRCMDbTables.put(LanguagesTable.getInstance().getName(), LanguagesTable.getInstance());
		sRCMDbTables.put(DialingPlansTable.getInstance().getName(), DialingPlansTable.getInstance());
		//sRCMDbTables.put(TempNewPersonalContactsTable.getInstance().getName(), TempNewPersonalContactsTable.getInstance());
		sRCMDbTables.put(PersonalContactsTable.getInstance().getName(), PersonalContactsTable.getInstance());
		sRCMDbTables.put(PermissionsTable.getInstance().getName(), PermissionsTable.getInstance());
		sRCMDbTables.put(PersonalPhoneNumberTable.getInstance().getName(), PersonalPhoneNumberTable.getInstance());
		sRCMDbTables.put(PersonalEmailTable.getInstance().getName(), PersonalEmailTable.getInstance());
		sRCMDbTables.put(PersonalAddressTable.getInstance().getName(), PersonalAddressTable.getInstance());
		sRCMDbTables.put(CloudFavoritesTable.getInstance().getName(), CloudFavoritesTable.getInstance());
		sRCMDbTables.put(CalendarEventAlertTable.getInstance().getName(), CalendarEventAlertTable.getInstance());
		sRCMDbTables.put(AccountBadgeTable.getInstance().getName(), AccountBadgeTable.getInstance());
		sRCMDbTables.put(BLFListTable.getInstance().getName(), BLFListTable.getInstance());

		sRCMDbTables.put(CalendarTable.getInstance().getName(), CalendarTable.getInstance());


	}


	static LinkedHashMap<Integer, HashSet<String>> sRCMDbChangedTables = new LinkedHashMap<Integer, HashSet<String>>();

	static {
		HashSet<String> dbTableHashSet;
		//for 19000, from 9.0
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(PhoneNumbersTable.getInstance().getName());
		dbTableHashSet.add(BLFListTable.getInstance().getName());
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		sRCMDbChangedTables.put(19000, dbTableHashSet);

		//for 18410, from 8.4.1
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(AccountBadgeTable.getInstance().getName());
		dbTableHashSet.add(RCMOutboxTable.getInstance().getName());
		sRCMDbChangedTables.put(18410, dbTableHashSet);

		//for 18400, from 8.4
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		dbTableHashSet.add(CallLogTable.getInstance().getName());
		sRCMDbChangedTables.put(18400, dbTableHashSet);

		//for 18300, from 8.3
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		dbTableHashSet.add(MessagesTable.getInstance().getName());
		sRCMDbChangedTables.put(18300, dbTableHashSet);

		//for 18200, from 8.2
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(CalendarEventAlertTable.getInstance().getName());
		dbTableHashSet.add(ServiceExtensionInfoTable.getInstance().getName());
		dbTableHashSet.add(CloudFavoritesTable.getInstance().getName());
		dbTableHashSet.add(ClientInfoTable.getInstance().getName());
		sRCMDbChangedTables.put(18200, dbTableHashSet);

		//for 18100, from 8.1
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(PersonalContactsTable.getInstance().getName());
		dbTableHashSet.add(ExtensionsTable.getInstance().getName());
		dbTableHashSet.add(ClientInfoTable.getInstance().getName());
		dbTableHashSet.add(CallLogTable.getInstance().getName());
		sRCMDbChangedTables.put(18100, dbTableHashSet);


		//for 18020 changes,from 8.0.2
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(PresenceTable.getInstance().getName());
		sRCMDbChangedTables.put(18020, dbTableHashSet);

		//for 18001 changes,from 8.0.0

		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(PersonalContactsTable.getInstance().getName());
		dbTableHashSet.add(CallLogTable.getInstance().getName());
		dbTableHashSet.add(MessagesTable.getInstance().getName());
		dbTableHashSet.add(RCMOutboxToPhoneTable.getInstance().getName());
		dbTableHashSet.add(PhoneNumbersTable.getInstance().getName());//remove extension name in table
		dbTableHashSet.add(CloudFavoritesTable.getInstance().getName());//add cloud favorite table
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		dbTableHashSet.add(ClientInfoTable.getInstance().getName());
		dbTableHashSet.add(PermissionsTable.getInstance().getName());
		dbTableHashSet.add(ServiceExtensionInfoTable.getInstance().getName());
		sRCMDbChangedTables.put(18000, dbTableHashSet);

		//for 17500 changes,from 7.5.0
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(ServiceExtensionInfoTable.getInstance().getName());
		dbTableHashSet.add(ConferenceInfoTable.getInstance().getName());
		dbTableHashSet.add(PersonalContactsTable.getInstance().getName());
		sRCMDbChangedTables.put(17500, dbTableHashSet);


		//for 17403 changes,from 7.4.3
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		dbTableHashSet.add(UserCredentialsTable.getInstance().getName());
		sRCMDbChangedTables.put(17403, dbTableHashSet);

		//for 17401 changes,from 7.4.1
		//change SERVICE_EXTENSION_INFO
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(ServiceExtensionInfoTable.getInstance().getName());
		sRCMDbChangedTables.put(17401, dbTableHashSet);

		//for 17400 changes,from 7.4.0
		//change ConferenceInfoTable
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(ConferenceInfoTable.getInstance().getName());
		dbTableHashSet.add(MessagesTable.getInstance().getName());
		dbTableHashSet.add(CallLogTable.getInstance().getName());
		sRCMDbChangedTables.put(17400, dbTableHashSet);

		// for 17300 changes, from 7.3.0
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(DialingPlansTable.getInstance().getName());
		dbTableHashSet.add(PhoneNumbersTable.getInstance().getName());
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		dbTableHashSet.add(RCMOutboxToPhoneTable.getInstance().getName());
		sRCMDbChangedTables.put(17300, dbTableHashSet);

		// for 17201 changes, from 7.2
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(RCMOutboxTable.getInstance().getName());
		dbTableHashSet.add(ConferenceInfoTable.getInstance().getName());
		sRCMDbChangedTables.put(17201, dbTableHashSet);

		// for 17102 changes, from 7.1 to 7.1.2
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		sRCMDbChangedTables.put(17102, dbTableHashSet);

		// for 161 changes, from 7.1
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(MessagesTable.getInstance().getName());
		dbTableHashSet.add(ClientInfoTable.getInstance().getName());
		dbTableHashSet.add(UserCredentialsTable.getInstance().getName());
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		sRCMDbChangedTables.put(17101, dbTableHashSet);

		//for 160 changes, from 7.0
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(DeviceAttribTable.getInstance().getName());
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		dbTableHashSet.add(ServiceExtensionInfoTable.getInstance().getName());
		dbTableHashSet.add(LanguagesTable.getInstance().getName());
		sRCMDbChangedTables.put(160, dbTableHashSet);

		//for 158 changes, from 6.6
		dbTableHashSet = new LinkedHashSet<String>();
		dbTableHashSet.add(ClientInfoTable.getInstance().getName());
		dbTableHashSet.add(ServiceExtensionInfoTable.getInstance().getName());
		dbTableHashSet.add(AccountInfoTable.getInstance().getName());
		sRCMDbChangedTables.put(158, dbTableHashSet);

		//for 148 changes,  6.5 ~ 6.5.1
		dbTableHashSet = new LinkedHashSet<String>(sRCMDbTables.keySet());
		sRCMDbChangedTables.put(PREVIOUS_DB_VERSION, dbTableHashSet);
	}

}
