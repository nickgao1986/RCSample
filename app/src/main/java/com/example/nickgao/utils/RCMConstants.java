package com.example.nickgao.utils;

import android.app.AlertDialog;


public class RCMConstants {

	public static final String REST_REQUEST_PATH_MAIN = "/restapi/v1.0/account/~/extension/~/";
	public static final String REST_REQUEST_ACCOUNT_PATH_MAIN = "/restapi/v1.0/account/~/";
	  public static final String REST_VERSION_URI = "/restapi/v1.0/";
	/**
	 * Crittercism constants
	 */
	public static final String CRITTERCISM_APP_ID 			= "5232cf5fd0d8f7141e000005";
	public static final String CRITTERCISM_USER_INFO 		= "UerInfo";
	public static final String CRITTERCISM_DEVICE_VERSION  	= "DeviceVersion";
	public static final String CRITTERCISM_APP_VERSION  	= "AppVersion";
	public static final String CRITTERCISM_BREADCRUMB   	= "RingCentralBreadcrumb";
	
    /**
     * RingOut constants
     */
    public static final int RINGOUT_MODE_MY_ANDROID = 0;
    public static final int RINGOUT_MODE_ANOTHER_PHONE = 1;

    public static final int RINGOUT_RESULT_OK = 1;
    public static final int RINGOUT_RESULT_FAIL = 2;
    public static final int RINGOUT_RESULT_CANCELLED = 3;
    public static final int RINGOUT_RESULT_SHORTCUT = 4;
    
    /**
     * Internal permission name. See Android Manifest.
     */
    public static final String INTERNAL_PERMISSION_NAME = "com.ringcentral.android.INTERNAL_PERMISSION";

    /**
     * Action constants for RCM Intents
     */
    public static final String ACTION_RESTART_APP 		= "com.ringcentral.android.intent.action.RESTART_APP";
    public static final String ACTION_LOGIN_FINISHED 	= "com.ringcentral.android.intent.action.LOGIN_FINISHED";
    public static final String ACTION_FORCE_LOGOUT 		= "com.ringcentral.android.intent.action.FORCE_LOGOUT";
    public static final String ACTION_FORCE_RESTART_UI 	= "com.ringcentral.android.intent.action.FORCE_RESTART_UI";
    public static final String ACTION_RINGOUT			= "com.ringcentral.android.intent.action.RINGOUT";
    public static final String ACTION_RINGOUT_VIEW 		= "com.ringcentral.android.intent.action.RINGOUT_VIEW";
    
    public static final String ACTION_NETWORK_REQUEST_END 		= "com.ringcentral.android.intent.action.NETWORK_REQUEST_END";
    public static final String ACTION_NETWORK_STATE_CHANGED 	= "com.ringcentral.android.intent.action.NETWORK_STATE_CHANGED";
    public static final String ACTION_DND_STATUS_CHANGED 		= "com.ringcentral.android.intent.action.DND_STATUS_CHANGED";
//    public static final String ACTION_UPDATE_MESSAGES_LIST 		= "com.ringcentral.android.intent.action.UPDATE_MESSAGES_LIST";
    
    public static final String ACTION_UPDATE_MESSAGE_DETAIL_VIEW 			= "com.ringcentral.android.intent.action.UPDATE_MESSAGE_DETAIL_VIEW";
    public static final String ACTION_MESSAGES_MOD_COUNTER_CHANGED 			= "com.ringcentral.android.intent.action.MESSAGES_MOD_COUNTER_CHANGED";
    public static final String ACTION_EXTENSIONS_MOD_COUNTER_CHANGED 		= "com.ringcentral.android.intent.action.EXTENSIONS_MOD_COUNTER_CHANGED";
    public static final String ACTION_MESSAGES_BODY_IS_DOWNLOADED 			= "com.ringcentral.android.intent.action.MESSAGES_BODY_IS_DOWNLOADED";
    public static final String ACTION_UPDATE_LOGS_LIST 						= "com.ringcentral.android.intent.action.UPDATE_LOGS_LIST";
    public static final String ACTION_CURRENT_TAB_CHANGED 					= "com.ringcentral.android.intent.action.CURRENT_TAB_CHANGED";
    public static final String ACTION_CURRENT_TAB_CHANGED_VIEW_LOADED 		= "com.ringcentral.android.intent.action.CURRENT_TAB_CHANGED_VIEW_LOADED";
    public static final String ACTION_CURRENT_SINGLE_ACTIVITY_FINISH 		= "com.ringcentral.android.intent.action.CURRENT_SINGLE_ACTIVITY_FINISH";
    
    public static final String ACTION_LIST_RECENT_CALL 		= "com.ringcentral.android.intent.action.RECENT_CALLS"; 
    public static final String ACTION_LIST_CONTACTS 		= "com.ringcentral.android.intent.action.LIST_CONTACTS";
    public static final String ACTION_LIST_FAVORITES 		= "com.ringcentral.android.intent.action.LIST_FAVORITES";
    public static final String ACTION_LIST_EXTENSIONS 		= "com.ringcentral.android.intent.action.LIST_EXTENSIONS";
    public static final String ACTION_LIST_EXTENSIONS_FAV 	= "com.ringcentral.android.intent.action.LIST_EXTENSIONS_FAV";
    public static final String ACTION_LIST_MESSAGES 		= "com.ringcentral.android.intent.action.LIST_MESSAGES";
    public static final String ACTION_LIST_SEARCH 			= "com.ringcentral.android.intent.action.LIST_SEARCH";
    public static final String ACTION_CONFIGURATION_CHANGED = "com.ringcentral.android.intent.action.ACTION_CONFIGURATION_CHANGED";
    public static final String ACTION_HTTP_REGISTERED 		= "com.ringcentral.android.intent.action.HTTP_REGISTERED";
    public static final String ACTION_RC_APP_STARTED 		= "com.ringcentral.android.intent.action.RC_APP_STARTED";
    
    public static final String ACTION_LIST_CONFERENCE 		= "com.ringcentral.android.intent.action.CONFERENCE"; 
    public static final String ACTION_LIST_RC_DOCUMENT 		= "com.ringcentral.android.intent.action.RC_DOCUMENT";
    
    public static final String ACTION_VOIP_CONFIGURATION_CHANGED 	= "com.ringcentral.android.intent.action.VOIP_CONFIGURATION_CHANGED";
    public static final String ACTION_VOIP_DSCP_STATE_CHANGED 		= "com.ringcentral.android.intent.action.VOIP_DSCP_STATE_CHANGED";
    public static final String ACTION_VOIP_STATE_CHANGED 			= "com.ringcentral.android.intent.action.VOIP_STATE_CHANGED";
    public static final String ACTION_UPDATE_MESSAGE_INDICATOR 		= "com.ringcentral.android.intent.action.UPDATE_MESSAGE_INDICATOR";
    public static final String ACTION_VOIP_CALLS_NUMBER_CHANGED 	= "com.ringcentral.android.intent.action.VOIP_CALLS_NUMBER_CHANGED";
    public static final String ACTION_VOIP_NOTIFY_INCOMING_CALL 	= "com.ringcentral.android.intent.action.VOIP_NOTIFY_INCOMING_CALL";
    
    public static final String ACTION_VOIP_STOP_NOTIFY_INCOMING_CALL 		= "com.ringcentral.android.intent.action.VOIP_STOP_NOTIFY_INCOMING_CALL";
    public static final String ACTION_VOIP_ECHO_CANCELATION_STATE_CHANGED 	= "com.ringcentral.android.intent.action.VOIP_ECHO_CANCELATION_STATE_CHANGED";
    public static final String ACTION_VOIP_RING_STARTED 					= "com.ringcentral.android.intent.action.VOIP_RING_STARTED";
    public static final String ACTION_INVOIP_RINGTONE_STARTED 				= "com.ringcentral.android.intent.action.INVOIP_RINGTONE_STARTED";
    public static final String ACTION_INVOIP_ACTIVITY_STARTED 				= "com.ringcentral.android.intent.action.INVOIP_ACTIVITY_STARTED";
    public static final String ACTION_INVOIP_ANSWER 						= "com.ringcentral.android.intent.action.INVOIP_ANSWER";
    public static final String ACTION_INVOIP_ANSWER_AND_HOLD 				= "com.ringcentral.android.intent.action.INVOIP_ANSWER_AND_HOLD";
    public static final String ACTION_CALL_STATE_ACTIVITY_STARTED 			= "com.ringcentral.android.intent.action.CALL_STATE_ACTIVITY_STARTED";
    
    public static final String ACTION_CLOSE_RINGOUT 			= "com.ringcentral.android.intent.action.CLOSE_RINGOUT_SCREEN";
    public static final String ACTION_RESTART_APPLICATION 		= "com.ringcentral.android.intent.action.ACTION_RESTART_APPLICATION";
    public static final String ACTION_FINISH_ACTIVITY 			= "com.ringcentral.android.intent.action.ACTION_FINISH_ACTIVITY";
    public static final String ACTION_IMPORT_FILE_BROADCAST 	= "com.ringcentral.android.intent.action.ACTION_IMPORT_FILE_BROADCAST";
    public static final String ACTION_OPEN_MAIN_MENU            = "com.ringcentral.android.intent.action.ACTION_OPEN_MAIN_MENU";
    
    public static final String ACTION_SET_IGNORE_INCOMING_CALL_STATE      	= "com.ringcentral.android.intent.action.ACTION_SET_IGNORE_INCOMING_CALL_STATE";
    public static final String ACTION_CURRENT_CALL_FINISHED      			= "com.ringcentral.android.intent.action.ACTION_CURRENT_CALL_FINISHED";
    public static final String ACTION_CURRENT_FLIP_FINISHED                 = "com.ringcentral.android.intent.action.ACTION_CURRENT_FLIP_FINISHED";
    
    public static final String ACTION_ROUTE_CHANGED 		= "com.ringcentral.android.intent.action.ROUTE_CHANGED";
    
    public static final String ACTION_UNREAD_INDICATOR_CHANGED  = "com.ringcentral.android.intent.action.ACTION_UNREAD_INDICATOR_CHANGED";
    
    public static final String ACTION_SET_FCC_DEFAULT_NUMBER_FAILED 	= "com.ringcentral.android.intent.action.ACTION_SET_FCC_DEFAULT_NUMBER_FAILED_BROADCAST";
    /**
     * On phone parser station location change, see
     * {@link #INTERNAL_PERMISSION_NAME}.
     * 
     * Parameters are declared in PhoneUtils.
     * 
     */
    public static final String ACTION_STATION_LOCATION_CHANGE = "com.ringcentral.android.intent.action.STATION_LOCATION_CHANGE";
    
    /**
     * Reply with message
     */
    public static final String ACTION_TIME_CHANGED = "com.android.action.ACTION_TIME_CHANGED";
    public static final String ACTION_STOP_TIME_SERVICE = "com.android.action.ACTION_SOTO_TIME_SERVICE";
    public static final String ACTION_CALL_BE_END = "com.android.action.ACTION_CALL_BE_END";
    
    /**
     * Extra data keys for RCM Intents
     */
    public static final String EXTRA_EVENT_DETAIL_FLURRY_EVENT_NAME = "com.ringcentral.android.intent.extra.event_detail_flurry_event_name";
    public static final String EXTRA_TEXTMESSAGES_FROM = "com.ringcentral.android.intent.extra.TextMessaes_from";
    public static final String EXTRA_EVENT_DETAIL_FILTER_FROM = "com.ringcentral.android.intent.extra.event_detail_filter_from";
    public static final String EXTRA_EVENT_DETAIL_FROM = "com.ringcentral.android.intent.extra.event_detail_from";
    public static final String EXTRA_CREATE_NEW_MESSAGE_FROM = "com.ringcentral.android.intent.extra.create_new_message_from";
    public static final String EXTRA_NEW_CONFIG = "com.ringcentral.android.intent.extra.NEW_CONFIG";
    public static final String EXTRA_RESTART_APP = "com.ringcentral.android.intent.extra.RESTART_APPLICATION";
    public static final String EXTRA_CALL_FROM_NUMBER = "com.ringcentral.android.intent.extra.CALL_FROM_NUMBER";
    public static final String EXTRA_CALL_FROM = "com.ringcentral.android.intent.extra.CALL_FROM";
    public static final String EXTRA_CALL_TO_NUMBER = "com.ringcentral.android.intent.extra.CALL_TO_NUMBER";
    public static final String EXTRA_CALL_TO_NAME = "com.ringcentral.android.intent.extra.CALL_TO_NAME";
    public static final String EXTRA_CALL_CALLER_ID = "com.ringcental.android.intent.extra.CALL_CALLER_ID";
    public static final String EXTRA_CALL_IS_ONE_LEG = "com.ringcental.android.intent.extra.CALL_IS_ONE_LEG";
    public static final String EXTRA_MESSAGE_ID = "com.ringcentral.android.intent.extra.MESSAGE_ID";
    public static final String EXTRA_MESSAGE_POSITION = "com.ringcentral.android.intent.extra.MESSAGE_POSITION";
    public static final String EXTRA_MESSAGES_COUNT = "com.ringcentral.android.intent.extra.MESSAGES_COUNT";
    public static final String EXTRA_MESSAGES_SORT_ORDER = "com.ringcentral.android.intent.extra.MESSAGES_SORT_ORDER";
    public static final String EXTRA_MESSAGES_LIST_MODE = "com.ringcentral.android.intent.extra.MESSAGES_LIST_MODE";
    public static final String EXTRA_MESSAGES_TYPE = "com.ringcentral.android.intent.extra.MESSAGES_TYPE";
    public static final String EXTRA_MESSAGES_SEARCH_INFO = "com.ringcentral.android.intent.extra.SEARCH_INFO";
    public static final String EXTRA_MESSAGES_TO_NAME = "com.ringcentral.android.intent.extra.MESSAGES_TO_NAME";
	public static final String EXTRA_MESSAGES_TO_NUMBER = "com.ringcentral.android.intent.extra.MESSAGES_TO_NUMBER";
	public static final String EXTRA_MESSAGES_FROM_NUMBER = "com.ringcentral.android.intent.extra.MESSAGES_FROM_NUMBER";
	public static final String EXTRA_MESSAGES_MODE = "com.ringcentral.android.intent.extra.MESSAGES_MODE";
	public static final String EXTRA_MESSAGES_SEND_CONTENT = "com.ringcentral.android.intent.extra.SEND_CONTENT";
    public static final String EXTRA_SETTINGS5 = "com.ringcentral.android.intent.extra.SETTINGS5";
    public static final String EXTRA_SHOW_DEVICE_NUMBER_DIALOG_AND_FINISH = "com.ringcentral.android.intent.extra.EXTRA_SHOW_DEVICE_NUMBER_DIALOG_AND_FINISH";
    public static final String EXTRA_VOIP_INCOMING_CALL_INFO = "com.ringcentral.android.intent.extra.VOIP_INCOMING_CALL_INFO";
    public static final String EXTRA_VOIP_INCOMING_CALL_INFO_COUNT = "com.ringcentral.android.intent.extra.VOIP_INCOMING_CALL_INFO_CALL_COUNT";
    public static final String EXTRA_VOIP_INCOMING_CALL_START_TIME = "com.ringcentral.android.intent.extra.VOIP_INCOMING_CALL_START_TIME";
    public static final String EXTRA_VOIP_CONFIGURATION_STATE_CHANGED = "com.ringcentral.android.intent.extra.VOIP_CONF_STATE_CHANGED";
    public static final String EXTRA_VOIP_STATE_CHANGED = "com.ringcentral.android.intent.extra.VOIP_STATE_CHANGED";
    public static final String EXTRA_VOIP_CALLS_NUMBER_CHANGED_TOTAL = "com.ringcentral.android.intent.extra.VOIP_CALLS_NUMBER_CHANGED_TOTAL";
    public static final String EXTRA_VOIP_CALLS_NUMBER_CHANGED_ON_HOLD = "com.ringcentral.android.intent.extra.VOIP_CALLS_NUMBER_CHANGED_ON_HOLD";
    public static final String EXTRA_VOIP_ECHO_CANCELATION_STATE_CHANGED = "com.ringcentral.android.intent.extra.VOIP_CONF_ECHO_STATE_CHANGED";
    public static final String EXTRA_VOIP_OUTGOING_CALL_ID = "com.ringcentral.android.intent.extra.VOIP_OUTGOING_CALL_ID";
    public static final String EXTRA_VOIP_CALL_TYPE = "com.ringcentral.android.intent.extra.VOIP_CALL_TYPE";
    public static final String EXTRA_VOIP_CALL_ACTION = "com.ringcentral.android.intent.extra.VOIP_CALL_ACTION";
    public static final String EXTRA_VOIP_CALL_INFO = "com.ringcentral.android.intent.extra.VOIP_CALL_INFO";
    public static final String EXTRA_VOIP_RING_STARTED = "com.ringcentral.android.intent.extra.VOIP_RING_STARTED";
    public static final String EXTRA_INVOIP_ACTIVITY_STARTED = "com.ringcentral.android.intent.extra.INVOIP_ACTIVITY_STARTED";
    public static final String EXTRA_INVOIP_ANSWER_TIME = "com.ringcentral.android.intent.extra.INVOIP_ANSWER_TIME";
    public static final String EXTRA_CALL_STATE_ACTIVITY_STARTED = "com.ringcentral.android.intent.extra.CALL_STATE_ACTIVITY_STARTED";
    public static final String EXTRA_INVOIP_ANSWER_AND_HOLD_TIME = "com.ringcentral.android.intent.extra.INVOIP_ANSWER_AND_HOLD_TIME";
    public static final String EXTRA_EVENT_DETAIL_TYPE = "com.ringcentral.android.intent.extra.EVENT_DETAIL_TYPE";
    public static final String EXTRA_EVENT_FROM_MESSAGE_VIEW_CONTACT = "com.ringcentral.android.intent.extra.EXTRA_EVENT_FROM_MESSAGE_VIEW_CONTACT";
    public static final String EXTRA_EVENT_DETAIL_COMPANY_TYPE_FROM = "com.ringcentral.android.intent.extra.EVENT_DETAIL_COMPANY_TYPE_FROM";
    public static final String EXTRA_COMPANY_CONTACT_NEED_LIGHT_BLUE = "com.ringcentral.android.intent.extra.EXTRA_COMPANY_CONTACT_NEED_LIGHT_BLUE";
    public static final String EXTRA_FAVORITE_PHONE_NUMBER = "com.ringcentral.android.intent.extra.FAVORITE_PHONE_NUMBER";
    public static final String EXTRA_MESSAGES_CONVERSATION_ID = "com.ringcentral.android.intent.extra.CONVERSATION_ID";
    public static final String EXTRA_FROM_BACKGROUND_NOTIFICATION = "com.ringcentral.android.intent.extra.IS_FROM_BACKGROUND_NOTIFICATION";
    public static final String EXTRA_CREATE_NEW_MESSAGE_FROM_MENU = "com.ringcentral.android.intent.extra.EXTRA_CREATE_NEW_MESSAGE_FROM_MENU";
    public static final String EXTRA_EVENT_EXTENSION_EXT_MAILBOX_ID = "com.ringcentral.android.intent.extra.EVENT_EXTENSION_EXT_MAILBOX_ID";

    public static final String EXTRA_CONTACT_SELECTOR_TYPE = "com.ringcentral.android.intent.extra.CONTACT_SELECTOR_TYPE";

    public static final String EXTRA_WHATS_NEW_FRAGMENT_IMAGE_RES_ID = "com.ringcentral.android.intent.extra.WHATS_NEW_FRAGMENT_IMAGE_RES_ID";
    public static final String EXTRA_WHATS_NEW_FRAGMENT_IS_INTRO = "com.ringcentral.android.intent.extra.WHATS_NEW_FRAGMENT_IS_INTRO";

    public static final String MESSAGES_IN_EDIT_STATE = "com.ringcentral.android.intent.extra.MESSAGES_IN_EDIT_STATE";
    public static final String MESSAGES_OUT_EDIT_STATE = "com.ringcentral.android.intent.extra.MESSAGES_OUT_EDIT_STATE";
    public static final String MESSAGES_SHOW_MENU_OUT_EDIT = "com.ringcentral.android.intent.extra.MESSAGES_SHOW_MENU_OUT_EDIT";

    public static final String EXTRA_IGNORE_INCOMING_CALL_STATE = "com.ringcentral.android.intent.extra.EXTRA_IGNORE_INCOMING_CALL_STATE";
    /**
     * Request codes for startActivityForResult(Intent intent, int requestCode)
     */
    public static final int ACTIVITY_REQUEST_CODE_RINGOUT_CALL = 100;

    /**
     * Notification identifiers
     */
    public static final int NOTIFICATION_ID_RCM_STATUS 			= 1;
    public static final int NOTIFICATION_ID_UNREAD_MESSAGES 	= 20; //Fix AB-9128 App cannot navigate to related screen for Voice/Fax notification
    public static final int NOTIFICATION_ID_FAILED_FAXES 		= 3;

    /**
     * User account tier settings
     */
    public static final long TIERS_PHS_VER_AUTO_UPGRDADE = 0x00000000001L;
    public static final long TIERS_PHS_SHOW_ADS = 0x00000000002L;
    public static final long TIERS_PHS_SENDVOICEMAIL = 0x00000000004L;
    public static final long TIERS_PHS_REPLY = 0x00000000008L;
    public static final long TIERS_PHS_TAKECALL = 0x00000000010L;
    public static final long TIERS_PHS_REJECT = 0x00000000020L;
    public static final long TIERS_PHS_SHOW0MINLEFT = 0x00000000040L;
    public static final long TIERS_PHS_SHOWBUYMINUTES = 0x00000000080L;
    public static final long TIERS_PHS_SHOWUPGRADENOW = 0x00000000100L;
    public static final long TIERS_PHS_ALLOW_HIDE_T0 = 0x00000000200L;
    public static final long TIERS_PHS_NOT_ENHANCED = 0x00000000400L;
    public static final long TIERS_PHS_CALL_BACK = 0x00000000800L;
    public static final long TIERS_PHS_NTFY_TRIAL_END = 0x00000001000L;
    public static final long TIERS_PHS_SHOWVERIFICATION = 0x00000002000L;
    public static final long TIERS_PHS_PRIVATE_ACCESS_NUMBER = 0x00000004000L;
    public static final long TIERS_PHS_NOTIFY_MOBILE = 0x00000008000L;
    public static final long TIERS_PHS_CUSTOM_REPLY = 0x00000010000L;
    public static final long TIERS_PHS_SHOW_CALL_LOG = 0x00000020000L;
    public static final long TIERS_PHS_MC2_PERIOD_CHNG_ALLOWED = 0x00000040000L;
    public static final long TIERS_PHS_SHOW_FAQ = 0x00000080000L;
    public static final long TIERS_PHS_SHOW_SEND_FAX = 0x00000100000L;
    public static final long TIERS_PHS_HIDE_TAKECALL = 0x00000200000L;
    public static final long TIERS_PHS_HIDE_SENDVOICEMAIL = 0x00000400000L;
    public static final long TIERS_PHS_NOT_NOTIFIED_ON_NEW_CALL = 0x00000800000L;
    public static final long TIERS_PHS_CALLER_PREVIEW = 0x00001000000L;
    public static final long TIERS_PHS_DIAL_FROM_CLIENT = 0x00002000000L;
    public static final long TIERS_PHS_SHOW_WHATS_NEW = 0x00004000000L;
    public static final long TIERS_PHS_DISABLE_AB_SYNC = 0x00008000000L;
    public static final long TIERS_PHS_USE_CDW = 0x00010000000L;
    public static final long TIERS_PHS_ENABLE_IE_TOOLBAR = 0x00020000000L;
    public static final long TIERS_PHS_HOTKEY_DIAL = 0x00040000000L;
    public static final long TIERS_PHS_HOTKEY_FAX = 0x00080000000L;
    public static final long TIERS_PHS_DISABLE_LOCAL_STORAGE = 0x00100000000L;
    public static final long TIERS_PHS_MULTIPLE_EXTENSIONS = 0x00200000000L;
    public static final long TIERS_PHS_CAN_VIEW_EXTENSIONS = 0x00400000000L;
    public static final long TIERS_PHS_CAN_VIEW_QUEUEAGENT = 0x00800000000L;
    public static final long TIERS_PHS_INTERNATIONAL_CALLING = 0x01000000000L;
    public static final long TIERS_PHS_SYSTEM_EXTENSION = 0x02000000000L;
    public static final long TIERS_PHS_OPERATOR_EXTENSION = 0x04000000000L;
    public static final long TIERS_PHS_DO_NOT_DISTURB = 0x08000000000L;
    public static final long TIERS_PHS_CHANGE_CALLERID = 0x10000000000L;

    /*
     * SIP flags. Received during HTTP registration procedure.
     */
    public static final int SIPFLAGS_SHOW_ANSWER = 0x00000001;
    public static final int SIPFLAGS_SHOW_DIAL = 0x00000002;
    public static final int SIPFLAGS_VOIP_ENABLED = 0x00000004;
    public static final int SIPFLAGS_LINE_ASSIGNED = 0x00000008;
    public static final int SIPFLAGS_LINE_DISABLED = 0x00000010;
    public static final int SIPFLAGS_THIRDPARTYCALLS_ENABLED = 0x00000020;

    /**
     * Service
     */
    public static final int SERVICE_FIRST_RUN_AFTER_CREATING = 2*60 * 1000; // 2 minutes
    public static final int SERVICE_INTERVAL = 10*60; // 10 minutes

    /**
     * Others
     */
    public static final int CALL_LOG_PAGE_SIZE = 25;
    public static final String FILE_PROTOCOL = "file://";

    /*
     * SIP settings
     */

    /**
     * Defines if VoIP enabled in Application VoIP Settings
     */
    public static final long SIP_SETTINGS_USER_VOIP_ENABLED = 0x00000001;

    /**
     * Defines if VoIP via WiFi is enabled in Application VoIP Settings
     */
    public static final long SIP_SETTINGS_USER_WIFI_ENABLED = 0x00000002;

    /**
     * Defines if VoIP via 3G/4G is enabled in Application VoIP Settings
     */
    public static final long SIP_SETTINGS_USER_3G_4G_ENABLED = 0x00000004;

    /**
     * Defines if VoIP incoming calls is enabled in Application VoIP Settings
     */
    public static final long SIP_SETTINGS_USER_INCOMING_CALLS_ENABLED = 0x00000008;

    /**
     * Defines if VoIP (SIP Flag in HTTPREG) is enabled for the account
     */
    public static final long SIP_SETTINGS_SIP_FLAG_HTTPREG_ENABLED = 0x00000010;

    /**
     * Defines if VoIP is enabled for the account per environment (disabled < JEDI 5.x.x) 
     */
    public static final long SIP_SETTINGS_ON_ENVIRONMENT_ENABLED = 0x00000020;

    /**
     * Defines if VoIP is enabled for the account AccountInfo.ServiceMask
     * TIERS_PHS_DIAL_FROM_CLIENT, later can be brand/tier.
     */
    public static final long SIP_SETTINGS_ON_ACCOUNT_ENABLED = 0x00000040;

    /*
     * VoIP status
     */
    public static final long VOIP_STATUS_VOIP_AVAILABLE = 0x00000001;
    public static final long VOIP_STATUS_VOIP_WIFI_AVAILABLE = 0x00000002;
    public static final long VOIP_STATUS_VOIP_3G_4G_AVAILABLE = 0x00000004;
    public static final long VOIP_STATUS_VOIP_NEW_AVAILABLE = 0x00000008;
    public static final long VOIP_STATUS_VOIP_INCOMING_AVAILABLE = 0x00000008;

    /*
     * VoIP call type (incoming/outgoing)
     */
    public static final int VOIP_CALL_TYPE_NONE = -1;
    public static final int VOIP_CALL_TYPE_OUTGOING = 1;
    public static final int VOIP_CALL_TYPE_INCOMING = 2;

    /*
     * VoIP user action for incoming call
     */
    public static final int VOIP_CALL_ACTION_NONE = -1;
    public static final int VOIP_CALL_ACTION_ANSWER = 1;
    public static final int VOIP_CALL_ACTION_ANSWER_AND_HOLD = 2;
    public static final int VOIP_CALL_ACTION_ANSWER_AND_HANGUP = 3;

    /*
     * VoIP calls handling delays
     * (AB-445: workaround for UI performance bottlenecks on slow phones)
     */
    public static final long VOIP_ANSWER_DELAY = 2000; // msec
    public static final long VOIP_START_CALL_DELAY = 2000; // msec

    /*
     * AEC default delay value
     */
    public static final int DEFAULT_AEC_DELAY_VALUE = 2560; // samples

    /**
     * DSCP debug keys.
     */
    public static final String DSCP_SWITCH_OFF = "*332843727633#";
    public static final String DSCP_SWITCH_ON  = "*33284372766#";
    
    /*
     * for eventDetail
     */
    public static final String CALLLOG_ID = "calllog_id";
    public static final String ISKNOWNCONTACT = "isknowncontact";
    public static final String ISINTERCOM = "istercom";
    public static final String isSMS = "isSMS";
    public static final String CALLLOGID="calllogid";
    public static final String LASTDATE = "lastdate";
    public static final String FROMNUMBER = "fromnumber";
    public static final String TONUMBER = "tonumber";
    public static final String ISFROMTEXTMESSAGE = "isfromtextmessage";
    
    public static final String RECEIVER_SEARCHBAR_CLEAR 	= "com.ringcentral.android.messages.Messages.searchBar";
    public static final String RECEIVER_TEXT_NOTIFICATION 	= "com.ringcentral.android.messages.Messages.textNotification";
    
    public static int screenWidth = 480;
    public static float density = 1.0f;
    
    
    public static final int CALLLOG_ADMIN_CHOOSE_TYPE = 0;
    public static final int EXTENSION_CHOOSE_GROUP = 1;
    
    public static final String CALL_LOG_TYPE = "Call Log tpye";
    public static final String IS_FROM_RINGOUT = "is_from_ringout";
    public static final String SET_FILTER_RESULT = "set filter result";
	public static final int SET_FILTER_REQUEST_CODE = 0;
    /**
     * Defines minimal version when stop to obtain JEDI API version.
     */
    public static final NumericVersion MIN_VERSION_TO_STOP_CHECK_JEDI_API = new NumericVersion(5,10,0,0);
    
    public static final NumericVersion CALLLOG_MIN_VERSION_TO_STOP_CHECK_JEDI_API = new NumericVersion(6,0,1,0);
    
    public static final NumericVersion EXTENSION_INFO_MIN_VERSION_TO_STOP_CHECK_JEDI_API = new NumericVersion(5,17,0,0);
    
    /**
     * Defines minimal version for REST API operations.
     */
    public static final NumericVersion MIN_VERSION_FOR_REST_API = MIN_VERSION_TO_STOP_CHECK_JEDI_API;
    
    /**
     * Defines minimal version to support FaxOut.
     */
    public static final NumericVersion MIN_VERSION_TO_SUPPORT_FAX_OUT = new  NumericVersion(5,12,0,0);
    
    /**
     * Defines minimal version to support video meetings.
     */
    public static final NumericVersion VIDEO_MEETINGS_MIN_VERSION_TO_STOP_CHECK_JEDI_API = new  NumericVersion(5,15,0,0);
    
    /**
     * Defines minimal version for FaxOut.
     */
    public static final NumericVersion MIN_VERSION_FOR_FAX_OUT = MIN_VERSION_TO_SUPPORT_FAX_OUT;
    
    /**
     * Defines minimal version to support FaxOut and multi-attachments.
     */
    public static final NumericVersion MIN_VERSION_TO_SUPPORT_FAX_OUT_MULTI_SUPPORT = new  NumericVersion(5,13,0,0);
    
    /**
     * Defines minimal version for FaxOut to support multi-attachments.
     */
    public static final NumericVersion MIN_VERSION_FOR_FAX_OUT_MULTI_SUPPORT = MIN_VERSION_TO_SUPPORT_FAX_OUT_MULTI_SUPPORT;
  
    public static final NumericVersion MIN_VERSION_FOR_DID = new NumericVersion(5,14,0,0);
    
    public static final NumericVersion MIN_VERSION_FOR_CALL_CONTROL = new NumericVersion(6,01,0,0);
    
    public static final NumericVersion MIN_VERSION_FOR_NEW_API_CALL_CONTROL = new NumericVersion(6, 02, 0, 0);
    
    public static final NumericVersion MIN_VERSION_FOR_PRESENCE = new NumericVersion(6,01,0,0);
    
    public static final NumericVersion MIN_VERSION_FOR_SPECIAL_NUMBER = new NumericVersion(6,04,0,0);
    
    /**
     * for text messages
     */
    public static final String SMS_CONVERSATION_ID = "com.ringcentral.android.messages.SMS_CONVERSATION_ID";
    
    public static final String SMS_UPDATE_MESSAGES = "com.ringcentral.android.messages.SMS_UPDATE_MESSAGES";
    public static final String UPDATE_CONVERSATION_ID = "CONVERSATION_ID"; 
    
    public static final String IS_FROM_CREATE_NEW_MESSAGE = "com.ringcentral.android.messages.is_from_create_new_message";
    
    public static final String DEFAULT_COUNTRY_CODE = "1";
    
    public static final int NO_CONVERSATION_ID = 0;
    public static final String EXTRA_FAX_OUT_ENTRIES 		= "com.ringcentral.android.intent.extra.FAX_OUT_ENTRIES";
    public static final String EXTRA_FAX_OUT_IMPORT_EXT 	= "com.ringcentral.android.intent.extra.EXTRA_FAX_OUT_IMPORT_EXT";
    
    public static final String EXTRA_CONTACT_NAME 			= "com.ringcentral.android.intent.extra.EXTRA_CONTACT_NAME";
    public static final String EXTRA_CONTACT_NUMBER 		= "com.ringcentral.android.intent.extra.EXTRA_CONTACT_NUMBER";
    public static final String EXTRA_CONTACT_ID 			= "com.ringcentral.android.intent.extra.EXTRA_CONTACT_ID";
    public static final String EXTRA_CONTACT_LOOKUP_KEY 	= "com.ringcentral.android.intent.extra.EXTRA_CONTACT_LOOKUP_KEY";
    public static final String EXTRA_CONTACT_PHONE_ID 		= "com.ringcentral.android.intent.extra.EXTRA_CONTACT_PHONE_ID";
    
    public static final String EXTRA_FAX_ATTACHMENT_PATH 		= "com.ringcentral.android.intent.extra.FAX_OUT_ATTACHMENT_PATH";
    public static final String EXTRA_FAX_ATTACHMENT_TYPE 		= "com.ringcentral.android.intent.extra.FAX_OUT_ATTACHMENT_TYPE";
    public static final String EXTRA_FAX_ATTACHMENT_FROM 		= "com.ringcentral.android.intent.extra.FAX_OUT_ATTACHMENT_FROM";
    public static final String EXTRA_FAX_CLOUD_ATTACHMENT_FROM 	= "com.ringcentral.android.intent.extra.FAX_OUT_ATTACHMENT_CLOUD_FROM";
    public static final String EXTRA_FAX_ATTACHMENT_GALLERY 	= "com.ringcentral.android.intent.extra.EXTRA_FAX_ATTACHMENT_GALLERY";
    
    public static final String ACTION_CLICK_MAIN_TAB_TO_FINISH = "com.ringcentral.android.intent.action.ACTION_CLICK_MAIN_TAB_TO_FINISH";
    
    public static final String ACTION_IMPORTING_FILE_TO_APP 			= "com.ringcentral.android.intent.action.ACTION_IMPORTING_FILE_TO_APP";
    public static final String ACTION_ADD_DOCUMENTS_TO_FAX_OUT 			= "com.ringcentral.android.intent.action.ACTION_ADD_DOCUMENTS_TO_FAX_OUT";
    public static final String ACTION_DRAFT_TO_FAX_OUT 					= "com.ringcentral.android.intent.action.ACTION_DRAFT_TO_FAX_OUT";
    public static final String EXTRA_FAX_OUT_ADD_DOCUMENT 				= "com.ringcentral.android.intent.extra.EXTRA_FAX_OUT_ADD_DOCUMENT";
    public static final String EXTRA_FAX_OUT_FILE 						= "com.ringcentral.android.intent.extra.EXTRA_FAX_OUT_FILE";
    public static final String EXTRA_FAX_COVER_PAGE_INDEX 				= "com.ringcentral.android.intent.extra.EXTRA_FAX_COVER_PAGE_INDEX";
    public static final String EXTRA_FAX_COVER_PAGE_NOTE 				= "com.ringcentral.android.intent.extra.EXTRA_FAX_COVER_PAGE_NOTE";
    public static final String EXTRA_DRAFT_TO_FAX_OUT_OUTBOX_ID 		= "com.ringcentral.android.intent.extra.EXTRA_DRAFT_TO_FAX_OUT_OUTBOX_ID";
    
    public static final String SCHEME_FILE 		= 	"file";
    public static final String SCHEME_CONTENT	=	"content";
    
    public static final String ACTION_SEND_FAX_TO_FINISH = "com.ringcentral.android.intent.action.ACTION_SEND_FAX_TO_FINISH";
    
    public static String CALL_TYPE = "call_type";
    /**
     * Outbox event detail
     */
    public static final String EXTRA_OUTBOX_ID = "com.ringcentral.android.intent.extra.FAX_OUT_OUTBOX_ID";
    public static final String EXTRA_OUTBOX_PHONE_ID = "com.ringcentral.android.intent.extra.OUT_OUTBOX_PHONE_ID";
    public static final String EXTRA_OUTBOX_MULTIPLE_RECIPIENTS = "com.ringcentral.android.intent.extra.OUT_OUTBOX_MULTIPLE_RECIPIENTS";
    
    public static final long FAXOUT_FILE_MAXIMUM_SIZE_BYTE = 30 * 1024 * 1024;	// 30M
    
    public static final int MAX_SQL_VARIABLES_COUNT = 100;

    /**
     * Message multiple recipients details
     */
    public static final String EXTRA_MESSAGE_GROUP_ID = "com.ringcentral.android.intent.extra.MESSAGE_GROUP_ID";
    /**
     * Brand Id
     */
    public static final int BRAND_ID_SKYPE_FAX 					= 3010; 
    public static final int BRAND_ID_RINGCENTRAL 				= 1210; 
    public static final int BRAND_ID_RINGCENTRAL_ATT 			= 3410; 
    public static final int BRAND_ID_BUZME 						= 10; 
    public static final int BRAND_ID_EXTREME_FAX 				= 1270; 
    public static final int BRAND_ID_RINGCENTRAL_UK 			= 3710; 
    public static final int BRAND_ID_RINGCENTRAL_CANADA 		= 3610; 
    public static final int BRAND_ID_CLEARWIRE 					= 3810; 
    public static final int BRAND_ID_PAGOO_ICW 					= 20; 
    public static final int BRAND_ID_PAGOO 						= 30; 
    public static final int BRAND_ID_CLEAR_FAX 					= 4010; 
    public static final int BRAND_ID_ROGERS_HOSTED_IP_VOICE 	= 4110;
    public static final int BRAND_ID_ALIBABA 					= 3910; 
    public static final int BRAND_ID_RINGCENTRAL_ATT_VIRTUAL 	= 3420;
    public static final int BRAND_ID_GO_DADDY 					= 3920; 
    public static final int BRAND_ID_RING_SHUFFLE 				= 4310;
    
    public static final int BRAND_ID_TELUS						= 7310;
    
    /**
     * voip call
     * 
     */
	public static final String VOIP_CALLING_CHANGE = "com.ringcentral.android.intent.action.VoIPCallingChange";
	public static final String IS_VOIP_ON = "isVoipOn";
	
	/**
	 * INTERCOM
	 */
	public static final String INTERCOM_START 			= "*85";
	public static final String INTERCOM_START_WITH_EXT 	= "*85*";
	
	
	
	/**
	 * for flurry
	 */
    //for call 
    public static final String TAP_CALL_BUTTON = "Tap Call Button in Dial Pad";
    public static final String TAP_CALL_BUTTON_IN_CONTACT_INFO_SCREEN_OF_VOICE = "Tap Call Button in Contact Info Screen of Voice";
    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_SCREEN_OF_VOICE = "Tap Contact Number in Contact Info Screen of Voice";
    
    public static final String TAP_CALL_BUTTON_IN_CONTACT_INFO_SCREEN_OF_TEXT = "Tap Call Button in Contact Info Screen of Text";
    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_SCREEN_OF_TEXT = "Tap Contact Number in Contact Info Screen of Text";
    
    public static final String TAP_CALL_BUTTON_IN_CONTACT_INFO_SCREEN_OF_FAX = "Tap Call Button in Contact Info Screen of Fax";
    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_SCREEN_OF_FAX = "Tap Contact Number in Contact Info Screen of Fax";
    
    public static final String TAP_CALL_BUTTON_IN_CONTACT_INFO_SCREEN_OF_ACTIVITY_LOG = "Tap Call Button in Contact Info Screen of Call Log";
    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_SCREEN_OF_ACTIVITY_LOG = "Tap Contact Number in Contact Info Screen of Call Log";
    
    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_SCREEN_OF_CONTACTS = "Tap Contact Number in Contact Info Screen of Contacts";
    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_OF_FAVORITES = "Tap Contact Number in Contact Info of Favorites";
    
    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_IN_OUTBOX = "Tap Contact Number in Contact Info in Outbox";
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_ALL_MESSAGE = "Select Call in Context Menu of All Message";
    
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_VOICE = "Select Call in Context Menu of Voice";
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_FAX = "Select Call in Context Menu of Fax";
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_TEXT = "Select Call in Context Menu of Text";
    
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_FAVORITES = "Select Call in Context Menu of Favorites";
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_CONTACTS = "Select Call in Context Menu of Contacts";
    public static final String TAP_A_CONTACT_IN_CONTACTS_SCREEN = "Tap a Contact in Contacts Screen";
    public static final String TAP_A_CONTACT_IN_FAVORITES_SCREEN = "Tap a Contact in Favorites Screen";            
    
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_ACTIVITY_LOG = "Select Call in Context Menu of Call Log";
    public static final String TAP_CONTACT_NUMBER_IN_ACTIVITY_LOG = "Tap Contact Number in Call Log";
    
    public static final String TAP_CALL_ICON_IN_FAX_SCREEN = "Tap Call Icon in Fax Screen";
    public static final String TAP_CALL_ICON_IN_VOICEMAIL_SCREEN = "Tap Call Icon in Voicemail Screen";
    public static final String TAP_CALL_ICON_IN_TEXT_MESSAGE_SCREEN = "Tap Call Button in Text Message Screen";

    public static final String SELECT_CALL_IN_FAX_PREVIEW_SCREEN = "Select Call Fax Preview Screen";
    public static final String SELECT_TEXT_IN_FAX_PREVIEW_SCREEN = "Select Text Fax Preview Screen";

    
    //for call supplementary
    public static final String EVENT_DETAIL_FROM_MESSAGE_VOICE = "event_detail_from_message_voice";
    public static final String EVENT_DETAIL_FROM_MESSAGE_FAX = "event_detail_from_message_fax";
    public static final String EVENT_DETAIL_FROM_MESSAGE_TEXT = "event_detail_from_message_text";
    public static final String EVENT_DETAIL_FROM_ACTIVITY_LOG = "event_detail_from_activity_log";
    public static final String EVENT_DETAIL_FROM_CONTACT = "event_detail_from_contact";
    public static final String EVENT_DETAIL_FROM_FAVORITE = "event_detail_from_favorite";
    
    
    //for sms
    public static final String TAP_SEND_TEXT_BUTTON_IN_TOOL_BAR = "Tap Send Text Button in Tool Bar";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_SCREEN_OF_VOICE = "Tap Send Text Button in Contact Info Screen of Voice";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_SCREEN_OF_TEXT = "Tap Send Text Button in Contact Info Screen of Text ";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_SCREEN_OF_FAX = "Tap Send Text Button in Contact Info Screen of Fax";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_OF_ACTIVITY_LOG = "Tap Send Text Button in Contact Info of Call Log";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_OF_CONTACTS = "Tap Send Text Button in Contact Info of Contacts";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_OF_FAVORITES = "Tap Send Text Button in Contact Info of Favorites";
    public static final String TAP_SEND_TEXT_BUTTON_BUTTON_IN_CONTACT_INFO_OF_OUTBOX = "Tap Send Text Button Button in Contact Info of Outbox";
    
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_OF_MESSAGE = "Select Send Text in Context Menu of Message";
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_IN_FAVORITES = "Select Send Text in Context Menu in Favorites";
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_IN_CONTACTS = "Select Send Text in Context Menu in Contacts";
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_IN_ACTIVITY_LOG = "Select Send Text in Context Menu in Call Log";
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_IN_MESSAGE_BUNDLE = "Select Send Text in Context Menu in Message Bundle";
    public static final String TAP_SEND_TEXT_ICON_IN_FAX_SCREEN = "Tap Send Text Icon in Fax Screen";
    public static final String TAP_SEND_TEXT_ICON_IN_VOICEMAIL_SCREEN = "Tap Send Text Icon in Voicemail Screen";
    public static final String FORWARD_TEXT_MESSAGE = "Forward Text Message";
    public static final String REPLY_TEXT_MESSAGE = "Reply Text Message";
    public static final String SEND_TEXT_MASSAGE_FOR_INVITATION_FOR_CONFERENCE = "Send Text Massage for Invitation for Conference";
    
    
    //for fax
    public static final String TAP_SEND_FAX_BUTTON_IN_TOOL_BAR = "Tap Send Fax Button in Tool Bar";
    public static final String FORWARD_FAX_IN_FAX_DETAILS_SCREEN = "Forward Fax in Fax Details Screen";
    public static final String TAP_SEND_FAX_BUTTON_IN_CONTACT_INFO_OF_ACTIVITY_LOG = "Tap Send Fax Button in Contact Info of Call Log";
    public static final String TAP_SEND_FAX_BUTTON_IN_CONTACT_INFO_SCREEN_OF_VOICE = "Tap Send Fax Button in Contact Info Screen of Voice";
    public static final String TAP_SEND_FAX_BUTTON_IN_CONTACT_INFO_SCREEN_OF_TEXT = "Tap Send Fax Button in Contact Info Screen of Text";
    public static final String TAP_SEND_FAX_BUTTON_IN_CONTACT_INFO_SCREEN_OF_FAX = "Tap Send Fax Button in Contact Info Screen of Fax";
    
    public static final String TAP_SEND_FAX_BUTTON_IN_CONTACT_INFO_OF_CONTACTS = "Tap Send Fax Button in Contact Info of Contacts";
    public static final String TAP_SEND_FAX_BUTTON_IN_CONTACT_INFO_OF_FAVORITES = "Tap Send Fax Button in Contact Info of Favorites";
    public static final String TAP_SEND_FAX_BUTTON_IN_CONTACT_INFO_OF_OUTBOX = "Tap Send Fax Button in Contact Info of Outbox";
    
    public static final String SELECT_FORWARD_FAX_IN_CONTEXT_MENU_OF_FAX = "Select Forward Fax in Context Menu of Fax";
    public static final String SELECT_SEND_FAX_IN_CONTEXT_MENU_OF_MESSAGE = "Select Send Fax in Context Menu of Message";
    public static final String SELECT_SEND_FAX_IN_CONTEXT_MENU_OF_FAVORITES = "Select Send Fax in Context Menu of Favorites";
    public static final String SELECT_SEND_FAX_IN_CONTEXT_MENU_OF_CONTACTS = "Select Send Fax in Context Menu of Contacts";
    public static final String SELECT_SEND_FAX_IN_CONTEXT_MENU_OF_ACTIVITY_LOG = "Select Send Fax in Context Menu of Call Log";
    public static final String SELECT_SEND_FAX_IN_CONTEXT_MENU_OF_DOCUMENTS = "Select Send Fax in Context Menu of Documents";
    
    public static final String SEND_FAX_FROM_DRAFTS = "Send Fax from Drafts";
    public static final String RETRY_SENDING_FAX_IN_OUTBOX = "Retry Sending Fax in Outbox";
    
    public static final String TAP_VOIP_CALLING_BUTTON_IN_NAVIGATION_MENU = "Tap VoIP Calling Button in Navigation Menu";
    public static final String TAP_VOIP_CALLING_BUTTON_IN_MY_MOBILE_APP_SETTINGS = "Tap VoIP Calling Button in My Mobile App Settings";
    
	public static final String MAP_KEY_ENTRIES = "Entries";
	public static final String MAP_KEY_RECIPIENTS_COUNT = "Recipients Count";
	public static final String MAP_KEY_DOCUMENT_COUNT = "Document Count";
	public static final String MAP_KEY_DOCUMENT_SIZE = "Document Size";
	public static final String MAP_KEY_AVERAGE_DOCUMENT_SIZE = "Average Document Size";
	public static final String MAP_KEY_COVER_PAGE = "Cover Page";
	
	public static final String MAP_KEY_MAILBOXID = "MailboxID";
	
	public static final String MAP_KEY_TEXT_LENGTH = "Length";
	public static final String MAP_KEY_RECIPIENTS_TYPE = "Recipient Type";
	public static final String MAP_KEY_MESSAGE_TYPE = "Message Type";
	public static final String MAP_KEY_MESSAGE_SMS_COUNT = "SMS Count";
	public static final String MAP_KEY_DEPARTMENT_PAGER = "Department Pager";
	public static final String MAP_KEY_PERSONAL_PAGER = "Personal Pager";
	public static final String MAP_KEY_GROUP_MESSAGE = "Group Message";
	
	public static final String MAP_VALUE_GROUP_MESSAGE_YES = "YES";
	public static final String MAP_VALUE_GROUP_MESSAGE_NO = "NO";

	public static final String MAP_KEY_INCOMINGCALL_TYPE = "Type";
	public static final String MAP_KEY_INCOMINGCALL_OPERATIONS = "Operations";
	
	public static final String INCOMINGCALLTYPE_SINGLE = "Single";
	public static final String INCOMINGCALLTYPE_MULTIPLE = "Multiple";
	
	public static final String MAP_KEY_CALL_NETWORK_TYPES = "Network Type";
	public static final String MAP_KEY_CALL_TYPES = "Types";
	
	public static final String CALL_FROM_CONFERENCE = "Conference";
	
	public static final String CALL_FROM_TEXT_MESSAGE = "Text Message";
	public static final String KEY_WORK_EMAIL = "email";
	public static final String KEY_WORK_GEMAIL = "gmail";
	
	public static final int FROM_RIGHT_ARROW_BUTTON       = 0;
	public static final int FROM_CONTEXT_MENU       = 1; 
	
	public static final int VERSION_EIGHT 		= 8;
	public static final int VERSION_TEN 		= 10;
	public static final int VERSION_ELEVEN 		= 11;
	public static final int VERSION_TWELVE 		= 12;
	public static final int VERSION_THIRTEEN 	= 13;
	public static final int VERSION_FOURTEEN 	= 14;
	public static final int VERSION_FIFTEEN 	= 15;
	public static final int VERSION_SIXTEEN 	= 16;
	public static final int VERSION_SEVENTEEN 	= 17;
	public static final int VERSION_EIGHTEEN 	= 18;
	public static final int VERSION_NINETEEN	= 19;
	
	public static final String COMPANY_FROM_CONTACT_LIST = "company_contact_from_contact_list";
	public static final String COMPANY_FROM_FAVORITE_LIST = "company_contact_from_favorite_list";
	public static final String COMPANY_FROM_MESSAGE_LIST = "company_contact_from_message_list";
	
	public static final String CALLER_ID_BLOCKED_NAME 	= "Blocked";
	public static final String CALLER_ID_BLOCKED_VALUE 	= "anonymous";
	public static final String CALLER_ID_BLOCKED_VALUE_FOR_LEG 	= "";
	
	public static final String SHARED_PREFERENCES_CONFERENCE_FLAG 	= "shared_preferences_conference_flag";
	public static final String SHARED_PREFERENCES_CONFERENCE_KEY 	= "shared_preferences_conference_key_conference";
	public static final String SHARED_PREFERENCES_CONFERENCE_FROM_FLAG = "shared_preferences_conference_from_flag";
	public static final String SHARED_PREFERENCES_CONFERENCE_NUMBER = "shared_preferences_conference_number";
	public static final String SHARED_PREFERENCES_CONFERENCE_CODE = "shared_preferences_conference_code";
	public static final String SHARED_PREFERENCES_CONFERENCE_DSIPLAY_CODE = "shared_preferences_conference_display_code";
	public static final int SHARED_PREFERENCES_CONFERENCE_FROM_TEXT = 1;
	public static final int SHARED_PREFERENCES_CONFERENCE_FROM_JOIN = 2;
	public static final String MESSAGES_SHOULD_DISMISS_STATE_RESTORE 	= "shouldDismissStateRestore";

	//Contact Group
	public static final int GROUP_SHOW_ALL_STATUS 		= 100; 
	public static final int GROUP_HIDE_ALL_STATUS 		= 101;
	public static final int GROUP_NORMAL_STATUS 		= 102;
	public static final int GROUP_ALL_CONTACT_FLAG 		= -100;
	public static final int GROUP_ALL_GROUP_FLAG 		= -200;
	public static final int GROUP_NORMAL_ACCOUNT_FLAG 	= -300;
	
	//Reply with message
	public static final String VOIP_REPLY_MESSAGE_LEFT_TIME = "voip_reply_message_left_time";
	public static final String VOIP_REPLY_MESSAGE_CONTENT   = "voip_reply_message_content";
	public static final String VOIP_REPLY_CUSTOM_MESSAGE_TIME_OUT   = "voip_reply_custom_message_time_out";
	public static final String VOIP_REPLY_CUSTOM_MESSAGE_BACK   = "voip_reply_custom_message_back";
	public static final String VOIP_REPLY_CUSTOM_MESSAGE_SAVED_CONTENT   = "voip_reply_custom_message_saved_content";
	public static final int VOIP_REPLY_MESSAGE_RESULT_CODE  = 100;
	public static final int VOPI_REPLY_MESSAGE_REQUEST_CODE = 1000;
	public static final int VOPI_REPLY_CUSTOM_MESSAGE_REQUEST_CODE = 1001;
    // for new "What's New" dialog
    public static final boolean WHATS_NEW_SHOW_IN_UPDATES_ONLY = true;
    public static final NumericVersion WHATS_NEW_MINIMAL_POD = new NumericVersion(6,0,1,0);
    public static final int WHATS_NEW_MINIMAL_VERSION = 39000;

    public static final String WHATS_NEW_SCREENS_ARRAY_RES_ID_INTENT_PARAMETER = "screens";
    public static final String WHATS_NEW_GO_TO_BACKGROUND_ON_BACK = "go_to_background_on_back";
    public static final String WHATS_NEW_SHOULD_CHECK_VOIP_CALLS = "should_check_voip_calls";
    public static final String WHATS_NEW_SHOULD_SAVE_STATE = "should_save_state";
    
    public static final int ALL_DIALOGS_THEME_ID = AlertDialog.THEME_HOLO_LIGHT;

	public static final String SHARED_PREFERENCES_SWITCH_TO_CARRIER 	= "shared_preferences_switch_to_carrier";
	
	public static final String SPLIT_MULTIPLE_SYMBOL 	= ";";
	public static final String DISPLAY_MULTIPLE_SYMBOL 	= ",";

    // Sending fax without attachments
    public static final NumericVersion FAX_SENDING_WITHOUT_ATTACHMENTS_MINIMAL_POD = new NumericVersion(6, 2, 0, 0);


    public static final String PDF_PREVIEW_FILE_PATH = "filePath";
    public static final String PDF_PREVIEW_MESSAGE_ID = "messageId";
    
    //For fcc
    public static final String FCC_ONE_NUMBER_KEY = "default";
    public static final String FCC_NO_LOCATION_KEY = "location";
}
