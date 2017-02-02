package com.example.nickgao.utils;

/**
 * Copyright (C) 2010-2013, RingCentral, Inc.
 * All Rights Reserved.
 */

import android.app.AlertDialog;

import com.example.nickgao.BuildConfig;
/**
 * Global RCM constants and definitions.
 */

public final class RCMConstants {

    public static final String REST_VERSION_URI = "/restapi/v1.0/";
    public static final String REST_REQUEST_PATH_MAIN = REST_VERSION_URI + "account/~/extension/~/";
    public static final String REST_REQUEST_ACCOUNT_PATH_MAIN = REST_VERSION_URI + "account/~/";

    /**
     * Crittercism constants
     */
    public static final String CRITTERCISM_DEVICE_VERSION = "DeviceVersion";
    public static final String CRITTERCISM_APP_VERSION = "AppVersion";
    public static final String CRITTERCISM_BREADCRUMB = "RingCentralBreadcrumb";

    //for force upgrade
    public static final long SEVEN_DAY = 7 * 24 * 60 * 60 * 1000;
    public static final long ONE_HOUR = 60 * 60 * 1000;
    public static final long FORCE_UPGRADE_REMIND_ME_NEXT_SHOW_UP_TIME = SEVEN_DAY;
    public static final String FORCE_UPGRADE_ACTION = "com.ringcentral.android.intent.action.FORCE_UPGRADE_ACTION";

    //for change password
    public static final String RESET_PASSWORD_ACTION = "com.ringcentral.android.intent.action.RESET_PASSWORD_ACTION";
    public static final String ACTION_START_CHANGE_PASSWORD_SCREEN = "isGoToResetPassword";
    public static final String ACTION_IS_CHANGE_PASSWORD_SCREEN = "isChangePasswordScreen";

    public static final String IS_CHANGE_EMAIL_PASSWORD_SCREEN = "isChangeEmailPasswordScreen";
    public static final String EMBEDEDBROWSER_LINK_URL = "embededBrowserLink";
    public static final String EMBEDEDBROWSER_TITLE = "embededBrowserTitle";


    //for client api done
    public final static String REST_GET_CLINET_INFO_API_COMPLETION_NOTIFICATION
            = BuildConfig.APPLICATION_ID + ".restapi.account.RestClientAPICompletion_NOTIFICATION";

    public static final String CLIENT_API_FETCH_DATA_IS_SUCCESS = "CLIENT_API_FETCH_DATA_IS_SUCCESS";

    /**
     * RingOut constants
     */
    public static final int RINGOUT_MODE_MY_ANDROID = 0;
    public static final int RINGOUT_MODE_ANOTHER_PHONE = 1;

    public static final int RINGOUT_RESULT_OK = 1;
    public static final int RINGOUT_RESULT_FAIL = 2;
    public static final int RINGOUT_RESULT_CANCELLED = 3;
    public static final int RINGOUT_RESULT_SHORTCUT = 4;

    public static final NumericVersion MIN_VERSION_FOR_JUDGE_FAXTER_BY_SERVICE_INFO = new NumericVersion(6, 6, 0, 0);
    public static final NumericVersion MIN_VERSION_FOR_I18N = new NumericVersion(6, 6, 0, 0);

    public static final NumericVersion MIN_VERSION_FOR_CONFERENCE_REQUEST_WITH_COUNTRYID = new NumericVersion(7, 5, 0, 0);

    /**
     * for about,settings,report
     */
    public static final String ACTION_TRANS_TAB_STATE = "action_trans_tab_state";
    public static final int ABOUT_COPYRIGHT_MIN_YEAR = 2014;
    public static final String SETTING_TYPE = "setting_type";

    /**
     * Internal permission name. See Android Manifest.
     */
    public static final String INTERNAL_PERMISSION_NAME = BuildConfig.APPLICATION_ID + ".INTERNAL_PERMISSION";

    /**
     * Action constants for RCM Intents
     */
    public static final String ACTION_PERMISSION_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.permission.changed";
    public static final String ACTION_RESTART_APP = BuildConfig.APPLICATION_ID + ".intent.action.RESTART_APP";
    public static final String ACTION_LOGIN_FINISHED = BuildConfig.APPLICATION_ID + ".intent.action.LOGIN_FINISHED";
    public static final String ACTION_FORCE_LOGOUT = BuildConfig.APPLICATION_ID + ".intent.action.FORCE_LOGOUT";

    public static final String ACTION_NETWORK_REQUEST_END = BuildConfig.APPLICATION_ID + ".intent.action.NETWORK_REQUEST_END";
    public static final String ACTION_NETWORK_STATE_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.NETWORK_STATE_CHANGED";
    public static final String ACTION_DND_STATUS_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.DND_STATUS_CHANGED";
    public static final String ACTION_DND_STATUS_SUCCESS = BuildConfig.APPLICATION_ID + ".intent.action.DND_STATUS_SUCCESS";
    public static final String ACTION_UPDATE_DND_STATUS_BY_SERVER = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_UPDATE_DND_STATUS_BY_SERVER";

    public static final String ACTION_DND_STATUS_CHANG_FAILED = BuildConfig.APPLICATION_ID + ".intent.action.DND_STATUS_CHANG_FAILED";
    public static final String ACTION_CURRENT_TAB_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.CURRENT_TAB_CHANGED";
    public static final String ACTION_CURRENT_TAB_CHANGED_VIEW_LOADED = BuildConfig.APPLICATION_ID + ".intent.action.CURRENT_TAB_CHANGED_VIEW_LOADED";
    public static final String ACTION_LIST_DEVICE_CONTACTS = BuildConfig.APPLICATION_ID + ".intent.action.LIST_DEVICE_CONTACTS";
    public static final String ACTION_LIST_CLOUD_CONTACTS = BuildConfig.APPLICATION_ID + ".intent.action.LIST_CLOUD_CONTACTS";
    public static final String ACTION_LIST_EXTENSIONS = BuildConfig.APPLICATION_ID + ".intent.action.LIST_EXTENSIONS";
    public static final String ACTION_LIST_ALL_CONTACTS = BuildConfig.APPLICATION_ID + ".intent.action.LIST_ALL_CONTACTS";
    public static final String ACTION_LIST_FAVORITES = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_LIST_FAVORITES";
    public static final String ACTION_CONFIGURATION_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_CONFIGURATION_CHANGED";
    public static final String ACTION_RC_APP_STARTED = BuildConfig.APPLICATION_ID + ".intent.action.RC_APP_STARTED";


    public static final String ACTION_VOIP_CONFIGURATION_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.VOIP_CONFIGURATION_CHANGED";
    public static final String ACTION_VOIP_DSCP_STATE_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.VOIP_DSCP_STATE_CHANGED";
    public static final String ACTION_VOIP_STATE_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.VOIP_STATE_CHANGED";

    public static final String ACTION_VOIP_RING_STARTED = BuildConfig.APPLICATION_ID + ".intent.action.VOIP_RING_STARTED";
    public static final String ACTION_INVOIP_RINGTONE_STARTED = BuildConfig.APPLICATION_ID + ".intent.action.INVOIP_RINGTONE_STARTED";
    public static final String ACTION_INVOIP_ACTIVITY_STARTED = BuildConfig.APPLICATION_ID + ".intent.action.INVOIP_ACTIVITY_STARTED";
    public static final String ACTION_INVOIP_ANSWER = BuildConfig.APPLICATION_ID + ".intent.action.INVOIP_ANSWER";
    public static final String ACTION_INVOIP_ANSWER_AND_HOLD = BuildConfig.APPLICATION_ID + ".intent.action.INVOIP_ANSWER_AND_HOLD";
    public static final String ACTION_CALL_STATE_ACTIVITY_STARTED = BuildConfig.APPLICATION_ID + ".intent.action.CALL_STATE_ACTIVITY_STARTED";

    public static final String ACTION_RESTART_APPLICATION = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_RESTART_APPLICATION";
    public static final String ACTION_FINISH_ACTIVITY = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_FINISH_ACTIVITY";
    public static final String ACTION_OPEN_MAIN_MENU = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_OPEN_MAIN_MENU";

    public static final String ACTION_CURRENT_CALL_FINISHED = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_CURRENT_CALL_FINISHED";
    public static final String ACTION_CURRENT_FLIP_FINISHED = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_CURRENT_FLIP_FINISHED";

    public static final String ACTION_UNREAD_INDICATOR_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_UNREAD_INDICATOR_CHANGED";

    public static final String ACTION_SET_FCC_DEFAULT_NUMBER_FAILED = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_SET_FCC_DEFAULT_NUMBER_FAILED_BROADCAST";

    /**
     * Reply with message
     */
    public static final String ACTION_TIME_CHANGED = "com.android.action.ACTION_TIME_CHANGED";
    public static final String ACTION_STOP_TIME_SERVICE = "com.android.action.ACTION_SOTO_TIME_SERVICE";
    public static final String ACTION_CALL_BE_END = "com.android.action.ACTION_CALL_BE_END";
    public static final String ACTION_SEND_REPLY_MESSAGE = "com.android.action.ACTION_SEND_REPLY_MESSAGE";
    public static final String ACTION_CALL_PICKUP_FAIL = "com.android.action.ACTION_CALL_PICKUP_FAIL";
    public static final String ACTION_CALL_PARKED_END = "com.android.action.ACTION_CALL_PARKED_END";

    public static final String ACTION_CHECK_LANGUAGE = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_CHECK_LANGUAGE";

    public static final String ACTION_SIP_REGISTER = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_SIP_REGISTER";
    public static final String ACTION_VOIP_REGISTER = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_VOIP_REGISTER";

    public static final String ACTION_PAS_DOWN = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_PAS_DOWN";

    /**
     * Navigation menu
     */
    public static final String ACTION_OPEN_FRAGMENT = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_OPEN_FRAGMENT";

    /**
     * Offline launch action
     */
    public static final String ACTION_OFFLINE_LAUNCH = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_OFFLINE_LAUNCH";
    public static final String ACTION_OFFLINE_LAUNCH_REST_SUCCESS = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_OFFLINE_LAUNCH_REST_SUCCESS";
    public static final String ACTION_OFFLINE_LAUNCH_REST_FAILED = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_OFFLINE_LAUNCH_REST_FAILED";
    public static final String ACTION_OFFLINE_LAUNCH_LOGIN_EXCEPTION = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_OFFLINE_LAUNCH_LOGIN_EXCEPTION";

    /**
     * feature toggle from AWS
     */

    public static final String ACTION_FEATURE_TOGGLE_CHANGE = BuildConfig.APPLICATION_ID + ".intent.action.FEATURE_TOGGLE_CHANGE";
    /**
     * Extra data keys for RCM Intents
     */
    public static final String EXTRA_EVENT_DETAIL_FLURRY_EVENT_NAME = BuildConfig.APPLICATION_ID + ".intent.extra.event_detail_flurry_event_name";
    public static final String EXTRA_TEXTMESSAGES_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.TextMessaes_from";
    public static final String EXTRA_EVENT_DETAIL_FILTER_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.event_detail_filter_from";
    public static final String EXTRA_EVENT_DETAIL_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.event_detail_from";
    public static final String EXTRA_EVENT_DETAIL_FROM_MSG_TYPE = BuildConfig.APPLICATION_ID + ".intent.extra.event_detail_from_msg_type";

    public static final String EXTRA_CREATE_NEW_MESSAGE_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.create_new_message_from";
    public static final String EXTRA_MAKE_A_CALL_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.make_a_call_from";
    public static final String EXTRA_NEW_CONFIG = BuildConfig.APPLICATION_ID + ".intent.extra.NEW_CONFIG";
    public static final String EXTRA_CALL_FROM_NUMBER = BuildConfig.APPLICATION_ID + ".intent.extra.CALL_FROM_NUMBER";
    public static final String EXTRA_CALL_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.CALL_FROM";
    public static final String EXTRA_CALL_TO_NUMBER = BuildConfig.APPLICATION_ID + ".intent.extra.CALL_TO_NUMBER";
    public static final String EXTRA_CALL_TO_NAME = BuildConfig.APPLICATION_ID + ".intent.extra.CALL_TO_NAME";
    public static final String EXTRA_CALL_CALLER_ID = BuildConfig.APPLICATION_ID + ".intent.extra.CALL_CALLER_ID";
    public static final String EXTRA_CALL_IS_ONE_LEG = ".intent.extra.CALL_IS_ONE_LEG";
    public static final String EXTRA_VOIP_INCOMING_CALL_INFO = BuildConfig.APPLICATION_ID + ".intent.extra.VOIP_INCOMING_CALL_INFO";
    public static final String EXTRA_MESSAGE_ID = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGE_ID";
    public static final String EXTRA_MESSAGE_POSITION = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGE_POSITION";
    public static final String EXTRA_MESSAGES_COUNT = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_COUNT";
    public static final String EXTRA_MESSAGES_SORT_ORDER = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_SORT_ORDER";
    public static final String EXTRA_MESSAGES_LIST_MODE = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_LIST_MODE";
    public static final String EXTRA_MESSAGES_TYPE = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_TYPE";
    public static final String EXTRA_MESSAGES_SEARCH_INFO = BuildConfig.APPLICATION_ID + ".intent.extra.SEARCH_INFO";
    public static final String EXTRA_MESSAGES_TO_NAME = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_TO_NAME";
    public static final String EXTRA_MESSAGES_TO_NUMBER = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_TO_NUMBER";
    public static final String EXTRA_MESSAGES_FROM_NUMBER = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_FROM_NUMBER";
    public static final String EXTRA_MESSAGES_MODE = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_MODE";
    public static final String EXTRA_MESSAGES_SEND_CONTENT = BuildConfig.APPLICATION_ID + ".intent.extra.SEND_CONTENT";
    public static final String EXTRA_RINGOUT_MODE_FILL_SCREEN = "EXTRA_RINGOUT_MODE_FILL_SCREEN";
    public static final String EXTRA_SHOW_DEVICE_NUMBER_DIALOG_AND_FINISH = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_SHOW_DEVICE_NUMBER_DIALOG_AND_FINISH";
    public static final String EXTRA_VOIP_INCOMING_CALL_INFO_COUNT = BuildConfig.APPLICATION_ID + ".intent.extra.VOIP_INCOMING_CALL_INFO_CALL_COUNT";
    public static final String EXTRA_VOIP_CONFIGURATION_STATE_CHANGED = BuildConfig.APPLICATION_ID + ".intent.extra.VOIP_CONF_STATE_CHANGED";

    public static final String EXTRA_VOIP_OUTGOING_CALL_ID = BuildConfig.APPLICATION_ID + ".intent.extra.VOIP_OUTGOING_CALL_ID";
    public static final String EXTRA_VOIP_NEED_REGISTER = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_VOIP_NEED_REGISTER";
    public static final String EXTRA_VOIP_CALL_TYPE = BuildConfig.APPLICATION_ID + ".intent.extra.VOIP_CALL_TYPE";
    public static final String EXTRA_VOIP_CALL_ACTION = BuildConfig.APPLICATION_ID + ".intent.extra.VOIP_CALL_ACTION";
    public static final String EXTRA_VOIP_RING_STARTED = BuildConfig.APPLICATION_ID + ".intent.extra.VOIP_RING_STARTED";
    public static final String EXTRA_INVOIP_ACTIVITY_STARTED = BuildConfig.APPLICATION_ID + ".intent.extra.INVOIP_ACTIVITY_STARTED";
    public static final String EXTRA_INVOIP_ANSWER_TIME = BuildConfig.APPLICATION_ID + ".intent.extra.INVOIP_ANSWER_TIME";
    public static final String EXTRA_CALL_STATE_ACTIVITY_STARTED = BuildConfig.APPLICATION_ID + ".intent.extra.CALL_STATE_ACTIVITY_STARTED";
    public static final String EXTRA_INVOIP_ANSWER_AND_HOLD_TIME = BuildConfig.APPLICATION_ID + ".intent.extra.INVOIP_ANSWER_AND_HOLD_TIME";
    public static final String EXTRA_EVENT_DETAIL_TYPE = BuildConfig.APPLICATION_ID + ".intent.extra.EVENT_DETAIL_TYPE";
    public static final String EXTRA_EVENT_FROM_MESSAGE_VIEW_CONTACT = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_EVENT_FROM_MESSAGE_VIEW_CONTACT";
    public static final String EXTRA_EVENT_DETAIL_COMPANY_TYPE_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.EVENT_DETAIL_COMPANY_TYPE_FROM";
    public static final String EXTRA_CONTACT_TYPE_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_CONTACT_TYPE_FROM";
    public static final String EXTRA_COMPANY_CONTACT_NEED_LIGHT_BLUE = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_COMPANY_CONTACT_NEED_LIGHT_BLUE";
    public static final String EXTRA_CREATE_NEW_MESSAGE_FROM_MENU = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_CREATE_NEW_MESSAGE_FROM_MENU";
    public static final String EXTRA_CREATE_NEW_MESSAGE_FROM_SCHEME = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_CREATE_NEW_MESSAGE_FROM_SCHEME";
    public static final String EXTRA_JOIN_FCC_FROM_SCHEME = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_JOIN_FCC_FROM_SCHEME";
    public static final String EXTRA_CONTACT_SELECTOR_TYPE = BuildConfig.APPLICATION_ID + ".intent.extra.CONTACT_SELECTOR_TYPE";
    public static final String EXTRA_IS_FROM_VOIP_UNAVAILABLE = BuildConfig.APPLICATION_ID + ".intent.extra.IS_FROM_VOIP_UNAVAILABLE";
    public static final String EXTRA_IS_FROM_VOIP_BLOCKING = BuildConfig.APPLICATION_ID + ".intent.extra.IS_FROM_VOIP_BLOCKING";
    public static final String EXTRA_IS_FROM_VOIP_OVER_WWAN_DISABLE = BuildConfig.APPLICATION_ID + ".intent.extra.IS_FROM_VOIP_OVER_WWAN_DISABLE";
    public static final String EXTRA_TRY_NETWORK_QUALITY_DETECTION_AGAIN = BuildConfig.APPLICATION_ID + ".intent.extra.TRY_NETWORK_QUALITY_DETECTION_AGAIN";

    public static final String EXTRA_WHATS_NEW_FRAGMENT_IMAGE_RES_ID = BuildConfig.APPLICATION_ID + ".intent.extra.WHATS_NEW_FRAGMENT_IMAGE_RES_ID";

    public static final String MESSAGES_IN_EDIT_STATE = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_IN_EDIT_STATE";
    public static final String MESSAGES_OUT_EDIT_STATE = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_OUT_EDIT_STATE";
    public static final String MESSAGES_SHOW_MENU_OUT_EDIT = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGES_SHOW_MENU_OUT_EDIT";

    public static final String EXTRA_SIP_REGISTER_RESULT = BuildConfig.APPLICATION_ID + ".intent.action.EXTRA_SIP_REGISTER_RESULT";
    public static final String EXTRA_HTTP_REGISTER_RESULT = BuildConfig.APPLICATION_ID + ".intent.action.EXTRA_HTTP_REGISTER_RESULT";

    public static final String EXTRA_VOIP_UNAVAILABLE = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_VOIP_UNAVAILABLE";

    public static final String EXTRA_DIALIN_SUFFIX = BuildConfig.APPLICATION_ID + "intent.extra.dialin_suffix";

    /**
     * Request codes for startActivityForResult(Intent intent, int requestCode)
     */
    public static final int ACTIVITY_REQUEST_CODE_RINGOUT_CALL = 100;

    public static final int RESULT_CODE_TO_DIAL_PAD = 205;
    public static final int ACTIVITY_REQUEST_CODE_MY_PROFILE = 203;

    /**
     * Notification identifiers
     */
    public static final long NOTIFICATION_ID_UNREAD_VOICEMAIL = 20;
    public static final long NOTIFICATION_ID_UNREAD_FAX = 2310;


    /*
     * SIP flags. Received during HTTP registration procedure.
     */
    public static final int SIPFLAGS_VOIP_ENABLED = 0x00000004;


    /**
     * Service
     */
    public static final int SERVICE_FIRST_RUN_AFTER_CREATING = 2 * 60 * 1000; // 2 minutes


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

    /**
     * Defines if HD Voice is enabled in Application VoIP Settings
     */
    public static final long SIP_SETTINGS_USER_HDVOICE_ENABLED = 0x00000080;


    /*
     * VoIP call type (incoming/outgoing)
     */
    public static final int VOIP_CALL_TYPE_NONE = -1;
    public static final int VOIP_CALL_TYPE_OUTGOING = 1;
    public static final int VOIP_CALL_TYPE_INCOMING = 2;

    /*
     * VoIP user action for incoming call
     */
    public static final int VOIP_CALL_ACTION_ANSWER = 1;
    public static final int VOIP_CALL_ACTION_ANSWER_AND_HOLD = 2;
    public static final int VOIP_CALL_ACTION_ANSWER_AND_HANGUP = 3;


    /*
     * AEC default delay value
     */
    public static final int DEFAULT_AEC_DELAY_VALUE = 2560; // samples

    /**
     * DSCP debug keys.
     */
    public static final String DSCP_SWITCH_OFF = "*332843727633#";
    public static final String DSCP_SWITCH_ON = "*33284372766#";

    /*
     * for eventDetail
     */
    public static final String CALLLOG_ID = "calllog_id";
    public static final String ISKNOWNCONTACT = "isknowncontact";
    public static final String ISINTERCOM = "istercom";
    public static final String isSMS = "isSMS";

    public static final String RECEIVER_SEARCHBAR_CLEAR = BuildConfig.APPLICATION_ID + ".messages.Messages.searchBar";
    public static final String RECEIVER_TEXT_NOTIFICATION = BuildConfig.APPLICATION_ID + ".messages.Messages.textNotification";

    public static int screenWidth = 480;
    public static int screenHeight = 800;
    public static float density = 1.0f;

    public static final String CALL_LOG_TYPE = "Call Log tpye";
    public static final String IS_FROM_RINGOUT = "is_from_ringout";
    public static final String SET_FILTER_RESULT = "set filter result";
    public static final int SET_FILTER_REQUEST_CODE = 0;

    /**
     * for text messages
     */
    public static final String SMS_CONVERSATION_ID = BuildConfig.APPLICATION_ID + ".messages.SMS_CONVERSATION_ID";

    public static final String IS_FROM_CREATE_NEW_MESSAGE = BuildConfig.APPLICATION_ID + ".messages.is_from_create_new_message";

    public static final String DEFAULT_COUNTRY_CODE = "1";

    public static final int NO_CONVERSATION_ID = 0;
    public static final String EXTRA_FAX_OUT_ENTRIES = BuildConfig.APPLICATION_ID + ".intent.extra.FAX_OUT_ENTRIES";
    public static final String EXTRA_FAX_OUT_IMPORT_EXT = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_FAX_OUT_IMPORT_EXT";

    public static final String EXTRA_CONTACT_NAME = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_CONTACT_NAME";
    public static final String EXTRA_CONTACT_NUMBER = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_CONTACT_NUMBER";
    public static final String EXTRA_CONTACT_ID = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_CONTACT_ID";
    public static final String EXTRA_CONTACT_LOOKUP_KEY = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_CONTACT_LOOKUP_KEY";
    public static final String EXTRA_CONTACT_PHONE_ID = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_CONTACT_PHONE_ID";

    public static final String EXTRA_FAX_ATTACHMENT_PATH = BuildConfig.APPLICATION_ID + ".intent.extra.FAX_OUT_ATTACHMENT_PATH";
    public static final String EXTRA_FAX_ATTACHMENT_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.FAX_OUT_ATTACHMENT_FROM";
    public static final String EXTRA_FAX_CLOUD_ATTACHMENT_FROM = BuildConfig.APPLICATION_ID + ".intent.extra.FAX_OUT_ATTACHMENT_CLOUD_FROM";
    public static final String EXTRA_FAX_ATTACHMENT_GALLERY = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_FAX_ATTACHMENT_GALLERY";
    public static final String EXTRA_FAX_FROM_SCHEME = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_FAX_FROM_SCHEME";

    public static final String ACTION_CLICK_MAIN_TAB_TO_FINISH = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_CLICK_MAIN_TAB_TO_FINISH";

    public static final String ACTION_UPDATE_CALL_LOG_BADGE = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_UPDATE_CALL_LOG_BADGE";
    public static final String ACTION_UPDATE_PARK_LOCATION_BADGE = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_UPDATE_PARK_LOCATION_BADGE";

    public static final String ACTION_IMPORTING_FILE_TO_APP = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_IMPORTING_FILE_TO_APP";
    public static final String ACTION_ADD_DOCUMENTS_TO_FAX_OUT = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_ADD_DOCUMENTS_TO_FAX_OUT";
    public static final String ACTION_DRAFT_TO_FAX_OUT = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_DRAFT_TO_FAX_OUT";
    public static final String EXTRA_FAX_OUT_ADD_DOCUMENT = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_FAX_OUT_ADD_DOCUMENT";
    public static final String EXTRA_FAX_OUT_FILE = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_FAX_OUT_FILE";
    public static final String EXTRA_FAX_COVER_PAGE_INDEX = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_FAX_COVER_PAGE_INDEX";
    public static final String EXTRA_FAX_COVER_PAGE_NOTE = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_FAX_COVER_PAGE_NOTE";
    public static final String EXTRA_DRAFT_TO_FAX_OUT_OUTBOX_ID = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_DRAFT_TO_FAX_OUT_OUTBOX_ID";

    public static final String SCHEME_FILE = "file";
    public static final String SCHEME_CONTENT = "content";

    public static final String ACTION_SEND_FAX_TO_FINISH = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_SEND_FAX_TO_FINISH";

    public static final String CALL_TYPE = "call_type";
    /**
     * Outbox event detail
     */
    public static final String EXTRA_OUTBOX_ID = BuildConfig.APPLICATION_ID + ".intent.extra.FAX_OUT_OUTBOX_ID";
    public static final String EXTRA_OUTBOX_PHONE_ID = BuildConfig.APPLICATION_ID + ".intent.extra.OUT_OUTBOX_PHONE_ID";
    public static final String EXTRA_OUTBOX_MULTIPLE_RECIPIENTS = BuildConfig.APPLICATION_ID + ".intent.extra.OUT_OUTBOX_MULTIPLE_RECIPIENTS";

    /**
     * Navigation menu
     */
    public static final String EXTRA_FRAGMENT_TAB = BuildConfig.APPLICATION_ID + ".intent.extra.EXTRA_FRAGMENT_TAB";

    public static final long SINGLE_FILE_MAXIMUM_SIZE_BYTE = 20 * 1024 * 1024;    // 20M
    public static final int SINGLE_FILE_MAXIMUM_SIZE_MB = (int) SINGLE_FILE_MAXIMUM_SIZE_BYTE / (1024 * 1024) ;

    public static final int MAX_SQL_VARIABLES_COUNT = 100;

    /**
     * Message multiple recipients details
     */
    public static final String EXTRA_MESSAGE_GROUP_ID = BuildConfig.APPLICATION_ID + ".intent.extra.MESSAGE_GROUP_ID";
    /**
     * Brand Id
     */
    public static final int BRAND_ID_SKYPE_FAX = 3010;
    public static final int BRAND_ID_RINGCENTRAL = 1210;
    public static final int BRAND_ID_RINGCENTRAL_ATT = 3410;
    public static final int BRAND_ID_BUZME = 10;
    public static final int BRAND_ID_EXTREME_FAX = 1270;
    public static final int BRAND_ID_RINGCENTRAL_UK = 3710;
    public static final int BRAND_ID_RINGCENTRAL_CANADA = 3610;
    public static final int BRAND_ID_CLEARWIRE = 3810;
    public static final int BRAND_ID_PAGOO_ICW = 20;
    public static final int BRAND_ID_PAGOO = 30;
    public static final int BRAND_ID_CLEAR_FAX = 4010;
    public static final int BRAND_ID_ROGERS_HOSTED_IP_VOICE = 4110;
    public static final int BRAND_ID_ALIBABA = 3910;
    public static final int BRAND_ID_RINGCENTRAL_ATT_VIRTUAL = 3420;
    public static final int BRAND_ID_GO_DADDY = 3920;
    public static final int BRAND_ID_RING_SHUFFLE = 4310;
    public static final int BRAND_ID_TELUS = 7310;
    public static final int BRAND_ID_BT = 7710;
    public static final int BRAND_ID_TMOBILE = 8510;
    public static final int BRAND_ID_RINGCENTRAL_EU = 2010;


    /**
     * voip call
     */
    public static final String VOIP_CALLING_CHANGE = BuildConfig.APPLICATION_ID + ".intent.action.VoIPCallingChange";
    public static final String IS_VOIP_ON = "isVoipOn";

    /**
     * INTERCOM
     */
    public static final String INTERCOM_START = "*85";
    public static final String INTERCOM_START_WITH_EXT = "*85*";


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

    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_OF_FAVORITES = "Tap Contact Number in Contact Info of Favorites";

    public static final String TAP_CONTACT_NUMBER_IN_CONTACT_INFO_IN_OUTBOX = "Tap Contact Number in Contact Info in Outbox";
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_ALL_MESSAGE = "Select Call in Context Menu of All Message";

    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_VOICE = "Select Call in Context Menu of Voice";
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_FAX = "Select Call in Context Menu of Fax";
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_TEXT = "Select Call in Context Menu of Text";

    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_FAVORITES = "Select Call in Context Menu of Favorites";
    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_CONTACTS = "Select Call in Context Menu of Contacts";
    public static final String TAP_A_CONTACT_IN_CONTACTS_SCREEN = "Tap a Contact in Contacts Screen";
    public static final String TAP_A_CALL_ICON_IN_FAVORITES_SCREEN = "Tap Call Icon in Favorites Screen";

    public static final String SELECT_CALL_IN_CONTEXT_MENU_OF_ACTIVITY_LOG = "Select Call in Context Menu of Call Log";
    public static final String TAP_CONTACT_NUMBER_IN_ACTIVITY_LOG = "Tap Contact Number in Call Log";

    public static final String TAP_CALL_ICON_IN_FAX_SCREEN = "Tap Call Icon in Fax Screen";
    public static final String TAP_CALL_ICON_IN_VOICEMAIL_SCREEN = "Tap Call Icon in Voicemail Screen";
    public static final String TAP_CALL_ICON_IN_TEXT_MESSAGE_SCREEN = "Tap Call Button in Text Message Screen";


    //for call supplementary
    public static final String EVENT_DETAIL_FROM_MESSAGE_VOICE = "event_detail_from_message_voice";
    public static final String EVENT_DETAIL_FROM_MESSAGE_FAX = "event_detail_from_message_fax";
    public static final String EVENT_DETAIL_FROM_MESSAGE_TEXT = "event_detail_from_message_text";
    public static final String EVENT_DETAIL_FROM_ACTIVITY_LOG = "event_detail_from_activity_log";
    public static final String EVENT_DETAIL_FROM_FAVORITE = "event_detail_from_favorite";


    //for sms
    public static final String TAP_SEND_TEXT_BUTTON_IN_TOOL_BAR = "Tap Send Text Button in Tool Bar";
    public static final String SEND_TEXT_MESSAGE_FROM_URI_SCHEME = "Send text message from URI Scheme";
    public static final String SEND_TAX_FROM_URI_SCHEME = "Send a fax from URI Scheme";
    public static final String MAKE_A_CALL_FROM_URI_SCHEME = "Make a call from URI Scheme";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_SCREEN_OF_VOICE = "Tap Send Text Button in Contact Info Screen of Voice";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_SCREEN_OF_TEXT = "Tap Send Text Button in Contact Info Screen of Text ";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_SCREEN_OF_FAX = "Tap Send Text Button in Contact Info Screen of Fax";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_OF_ACTIVITY_LOG = "Tap Send Text Button in Contact Info of Call Log";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_OF_CONTACTS = "Tap Send Text Button in Contact Info of Contacts";
    public static final String TAP_SEND_TEXT_BUTTON_IN_CONTACT_INFO_OF_FAVORITES = "Tap Send Text Button in Contact Info of Favorites";
    public static final String TAP_SEND_TEXT_BUTTON_BUTTON_IN_CONTACT_INFO_OF_OUTBOX = "Tap Send Text Button Button in Contact Info of Outbox";

    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_OF_MESSAGE = "Select Send Text in Context Menu of Message";
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_IN_FAVORITES = "Tap Text Icon in Favorites Screen";
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_IN_CONTACTS = "Select Send Text in Context Menu in Contacts";
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_IN_ACTIVITY_LOG = "Select Send Text in Context Menu in Call Log";
    public static final String SELECT_SEND_TEXT_IN_CONTEXT_MENU_IN_MESSAGE_BUNDLE = "Select Send Text in Context Menu in Message Bundle";
    public static final String TAP_SEND_TEXT_ICON_IN_FAX_SCREEN = "Tap Send Text Icon in Fax Screen";
    public static final String TAP_SEND_TEXT_ICON_IN_VOICEMAIL_SCREEN = "Tap Send Text Icon in Voicemail Screen";
    public static final String FORWARD_TEXT_MESSAGE = "Forward Text Message";
    public static final String REPLY_TEXT_MESSAGE = "Reply Text Message";
    public static final String SEND_TEXT_MASSAGE_FOR_INVITATION_FOR_CONFERENCE = "Send Text Massage for Invitation for Conference";
    public static final String SEND_MESSAGE_FROM_CONTACT_GROUP_LIST_SCREEN = "Tap Send Text Button in Contact Info of Group";
    public static final String CALL_FROM_CONTACT_GROUP_LIST_SCREEN = "Tap Call Button in Contact Info of Group";


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

    public static final String TAP_VOIP_CALLING_BUTTON_IN_MY_MOBILE_APP_SETTINGS = "Tap VoIP Calling Button in My Mobile App Settings";

    public static final String MAP_KEY_ENTRIES = "Entries";
    public static final String MAP_KEY_RECIPIENTS_COUNT = "Recipients Count";
    public static final String MAP_KEY_DOCUMENT_COUNT = "Document Count";
    public static final String MAP_KEY_DOCUMENT_SIZE_BUCKET = "Document Size Bucket";
    public static final String MAP_KEY_COVER_PAGE = "Cover Page";
    public static final String MAP_KEY_COVER_PAGE_STATUS = "Status";
    public static final String MAP_KEY_COVER_PAGE_LANGUAGE = "App Language";
    public static final String MAP_VALUE_COVER_PAG_STATUS_CHANGE = "Change";
    public static final String MAP_VALUE_COVER_PAG_STATUS_NOT_CHANGE = "Not change";

    public static final String MAP_KEY_ACTION = "Action";
    public static final String MAP_KEY_CAUSE = "Cause";

    public static final String ATTACHMENT_SIZE_BUCKET_0_1MB = "0 ~ 1Mb";
    public static final String ATTACHMENT_SIZE_BUCKET_1_5MB = "1 ~ 5Mb";
    public static final String ATTACHMENT_SIZE_BUCKET_5_10MB = "5 ~ 10Mb";
    public static final String ATTACHMENT_SIZE_BUCKET_10_15MB = "10 ~ 15Mb";
    public static final String ATTACHMENT_SIZE_BUCKET_15_20MB = "15 ~ 20Mb";
    public static final String ATTACHMENT_SIZE_BUCKET_ABOVE_20MB = "> 20Mb";

    public static final int MAX_MESSAGE_LENGTH = 1000;
    public static final String TEXT_LENGTH_BUCKET_1_20 = "1 ~ 20";
    public static final String TEXT_LENGTH_BUCKET_21_50 = "21 ~ 50";
    public static final String TEXT_LENGTH_BUCKET_51_100 = "51 ~ 100";
    public static final String TEXT_LENGTH_BUCKET_101_160 = "101 ~ 160";
    public static final String TEXT_LENGTH_BUCKET_161_250 = "161 ~ 250";
    public static final String TEXT_LENGTH_BUCKET_251_500 = "251 ~ 500";
    public static final String TEXT_LENGTH_BUCKET_ABOVE_501 = "501 and above";

    public static final String MAP_KEY_MAILBOXID = "MailboxID";

    public static final String MAP_KEY_TEXT_LENGTH = "Length";
    public static final String MAP_KEY_TEXT_LENGTH_BUCKET = "Length Bucket";
    public static final String MAP_KEY_MESSAGE_SMS_COUNT = "SMS Count";
    public static final String MAP_KEY_DEPARTMENT_PAGER = "Department Pager";
    public static final String MAP_KEY_PERSONAL_PAGER = "Personal Pager";

    public static final String MAP_KEY_INCOMINGCALL_TYPE = "Type";

    public static final String INCOMINGCALLTYPE_SINGLE = "Single";
    public static final String INCOMINGCALLTYPE_MULTIPLE = "Multiple";

    public static final String MAP_KEY_CALL_NETWORK_TYPES = "Network Type";
    public static final String MAP_KEY_CALL_TYPES = "Types";

    public static final String CALL_FROM_CONFERENCE = "Conference";
    public static final String CALL_FROM_CALENDAR = "Calendar join meetings through call and dial in";
    public static final String CALL_FROM_CALLLOG = "Call from call log";

    public static final String KEY_WORK_EMAIL = "email";
    public static final String KEY_WORK_GEMAIL = "gmail";


    public static final String SMS = "SMS";
    public static final String PAGER = "Pager";
    public static final String VOICEMAIL = "VoiceMail";
    public static final String FAX = "FAX";

    public static final int FROM_RIGHT_ARROW_BUTTON = 0;
    public static final int FROM_CONTEXT_MENU = 1;

    public static final int VERSION_EIGHT = 8;
    public static final int VERSION_TEN = 10;
    public static final int VERSION_ELEVEN = 11;
    public static final int VERSION_TWELVE = 12;
    public static final int VERSION_THIRTEEN = 13;
    public static final int VERSION_FOURTEEN = 14;
    public static final int VERSION_FIFTEEN = 15;
    public static final int VERSION_SIXTEEN = 16;
    public static final int VERSION_SEVENTEEN = 17;
    public static final int VERSION_EIGHTEEN = 18;
    public static final int VERSION_NINETEEN = 19;
    public static final int VERSION_TWENTY = 20;
    public static final int VERSION_TWENTY_ONE = 21;

    public static final String COMPANY_FROM_CONTACT_LIST = "company_contact_from_contact_list";
    public static final String COMPANY_FROM_FAVORITE_LIST = "company_contact_from_favorite_list";
    public static final String COMPANY_FROM_MESSAGE_LIST = "company_contact_from_message_list";

    //REST API RETURNS PHONE NUMBER CONSTANTS
    public static final String REST_DIRECT_NUMBER = "DirectNumber";//usageType
    public static final String REST_MAIN_COMPANY_NUMBER = "MainCompanyNumber";//usageType
    public static final String REST_TOLL_FREE = "TollFree";
    public static final String REST_LOCAL = "Local";

    public static final String CALLER_ID_BLOCKED_VALUE = "anonymous";
    public static final String CALLER_ID_BLOCKED_VALUE_FOR_LEG = "";

    public static final int SHARED_PREFERENCES_CONFERENCE_FROM_TEXT = 1;

    //Contact Group
    public static final int GROUP_SHOW_ALL_STATUS = 100;
    public static final int GROUP_HIDE_ALL_STATUS = 101;
    public static final int GROUP_NORMAL_STATUS = 102;
    public static final int GROUP_ALL_CONTACT_FLAG = -100;
    public static final int GROUP_ALL_GROUP_FLAG = -200;
    public static final int GROUP_NORMAL_ACCOUNT_FLAG = -300;

    //Reply with message
    public static final String VOIP_REPLY_MESSAGE_LEFT_TIME = "voip_reply_message_left_time";
    public static final String VOIP_REPLY_MESSAGE_CONTENT = "voip_reply_message_content";
    public static final String VOIP_REPLY_CUSTOM_MESSAGE_TIME_OUT = "voip_reply_custom_message_time_out";
    public static final String VOIP_REPLY_CUSTOM_MESSAGE_BACK = "voip_reply_custom_message_back";
    public static final String VOIP_REPLY_CUSTOM_MESSAGE_SAVED_CONTENT = "voip_reply_custom_message_saved_content";
    public static final int VOIP_REPLY_MESSAGE_RESULT_CODE = 100;
    public static final int VOPI_REPLY_MESSAGE_REQUEST_CODE = 1000;
    public static final int VOPI_REPLY_CUSTOM_MESSAGE_REQUEST_CODE = 1001;
    public static final int VOPI_REPLY_MESSAGE_DELAY_DETECT_TIME = 10 * 1000;
    // for new "What's New" dialog
    public static final boolean WHATS_NEW_SHOW_IN_UPDATES_ONLY = true;
    public static final NumericVersion WHATS_NEW_MINIMAL_POD = new NumericVersion(6, 0, 1, 0);
    public static final int WHATS_NEW_MINIMAL_VERSION = 39000;

    public static final String WHATS_NEW_SCREENS_ARRAY_RES_ID_INTENT_PARAMETER = "screens";
    public static final String WHATS_NEW_GO_TO_BACKGROUND_ON_BACK = "go_to_background_on_back";
    public static final String WHATS_NEW_SHOULD_CHECK_VOIP_CALLS = "should_check_voip_calls";
    public static final String WHATS_NEW_SHOULD_SAVE_STATE = "should_save_state";
    public static final String WHATS_NEW_SHOW_LANGUAGE_CONFLICT_DIALOG = "show_conflict_dialog";

    public static final int ALL_DIALOGS_THEME_ID = AlertDialog.THEME_HOLO_LIGHT;

    public static final String SPLIT_MULTIPLE_SYMBOL = ";";
    public static final String DISPLAY_MULTIPLE_SYMBOL = ", ";

    // Sending fax without attachments
    public static final NumericVersion FAX_SENDING_WITHOUT_ATTACHMENTS_MINIMAL_POD = new NumericVersion(6, 2, 0, 0);

    public static final NumericVersion FETCH_PERMISSION_LIST_POD = new NumericVersion(8, 0, 0, 0);

    public static final NumericVersion FETCH_PERMISSION_MEETING = new NumericVersion(8, 1, 0, 0);

    public static final NumericVersion FETCH_PERMISSION_CALENDAR = new NumericVersion(8, 2, 0, 0);

    public static final NumericVersion FETCH_MISSED_CALLS = new NumericVersion(8, 4, 0, 0);

    public static final String PDF_PREVIEW_FILE_PATH = "filePath";
    public static final String PDF_PREVIEW_ORIGINAL_FILE_PATH = "originalFilePath";
    public static final String PDF_PREVIEW_MESSAGE_ID = "messageId";

    //Page
    public static final int PAGE_START_INDEX = 1;
    public static final int PAGE_SIZE = 1000;

    //dialog ID
    public static final int VOIP_AUTO_TURN_ON_DIALOG = 8888;

    public static final String CALL_MEDIA_MODE_PLAIN = "plain";
    public static final String CALL_MEDIA_MODE_SECURE = "secure";

    public static final String CALL_TRANSPORT_TYPE_TLS = "TLS";

    // Profile Image
    public static final String ACTION_PROFILE_IMAGE_UPDATED = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_PROFILE_IMAGE_UPDATED";

    // Profile Image
    public static final String ACTION_PROFILE_EXTENSION_IMAGE_UPDATED = BuildConfig.APPLICATION_ID + ".intent.action.ACTION_PROFILE_EXTENSION_IMAGE_UPDATED";


    // Encryption
    public static final String DB_FILE = "rcm.db";
    public static final String DB_FILE_TMP = "rcm_tmp.db";
    public static final String ACTION_PROVIDER_START = BuildConfig.APPLICATION_ID + "intent.action.ACTION_PROVIDER_START";
    public static final String ACTION_PROVIDER_STARTED = BuildConfig.APPLICATION_ID + "intent.action.ACTION_PROVIDER_STARTED";
    public static final String ACTION_PROVIDER_STOP = BuildConfig.APPLICATION_ID + "intent.action.ACTION_PROVIDER_STOP";
    public static final String ACTION_PROVIDER_STOPPED = BuildConfig.APPLICATION_ID + "intent.action.ACTION_PROVIDER_STOPPED";

    public static final String ACTION_PROVIDER_START_DB_PATH = BuildConfig.APPLICATION_ID + ".intent.extra.ACTION_PROVIDER_START_DB_PATH";
    public static final String ACTION_PROVIDER_START_ENCRYPTED = BuildConfig.APPLICATION_ID + ".intent.extra.ACTION_PROVIDER_START_ENCRYPTED";
    public static final String ACTION_PROVIDER_START_KEY = BuildConfig.APPLICATION_ID + ".intent.extra.ACTION_PROVIDER_START_KEY";
    public static final String ACTION_START_TEMP_DB = BuildConfig.APPLICATION_ID + ".intent.extra.ACTION_START_TEMP_DB";

    public static final String ACTION_MIGRATION_DECRYPT = BuildConfig.APPLICATION_ID + ".intent.extra.ACTION_MIGRATION_DECRYPT";
    public static final String ACTION_MIGRATION_KEY = BuildConfig.APPLICATION_ID + ".intent.extra.ACTION_MIGRATION_KEY";

    public static final String NOTIFICATION_GROUP = BuildConfig.APPLICATION_ID + "NOTIFICATION_GROUP";


    public static final int MIGRATION_MINIMUM_PROGRESS_TIME_IN_MS = 500;
    public static final int MIGRATION_START_BATTERY_MIN_LEVEL = 5;

    public static final float MIGRATION_START_STORAGE_KOEFF = 1.3f;
    public static final long MIGRATION_MIN_STORAGE_SIZE = 20 * 1024 * 1024;  // 20 mb limit

    public static final int ENCRYPTION_CLEAN_UP_LOGS_FOR_VERSIONS = 650100000;
    public static final int ENCRYPTION_APP_MIGRATION_VERSIONS = 660000000;
    public static final int ENCRYPTION_APP_VOIP_VERSIONS = 701000000;

    public static final int MESSAGE_MIGRATION_VERSIONS = 704000000;

    public static final int DEVICE_NUMBER_VERSIONS = 704300000;

    public static final int CONFERNECE_MESSAGE_MIGRATION_VERSIONS = 705000000;

    public static final int FAVORTIES_MIGRATION_VERSIONS = 800000000;

    public static final int CLOUD_FAVORITES_MIGRATION_VERSIONS = 802000000;

    public static final int BLOCKNUMBER_MIGRATION_VERSIONS = 801000000;

    public static final int CALL_SETTINGS_MIGRATION_VERSIONS = 802000000;

    public static final int LANGUAGE_CONFLICT_VERSIONS = 803000000;

    public static final int VERSION_841 = 804100000;

    //The limit number of batch request
    public static final int LIMIT_NUMBER_OF_BATCH_REQUEST = 30;

    public static final String PARAM_LOCALE_ID_NAME = "localeId";

    public static final String ACTION_GENERAL_ERROR_TEXT = BuildConfig.APPLICATION_ID + ".intent.extra.ACTION_GENERAL_ERROR_TEXT";
    public static final String ACTION_GENERAL_ERROR_HEADER = BuildConfig.APPLICATION_ID + ".intent.extra.ACTION_GENERAL_ERROR_HEADER";

    public static final int HTTP_REGISTER_RESULT_SUCCEED = 0;
    public static final int HTTP_REGISTER_RESULT_FAILED = 1;
    public static final int HTTP_REGISTER_RESULT_VOIP_BLOCK = 2;

    public static final String SP_INSTANCE_ID_EDITOR = BuildConfig.APPLICATION_ID + ".shared_preferences_instance_id_editor";
    public static final String SP_INSTANCE_ID_PARAM = BuildConfig.APPLICATION_ID + ".shared_preferences_instance_id";

    public static final String FRAGMENT_ARGUMENT_INTENT = "FRAGMENT_ARGUMENT_INTENT";
    public static final String FRAGMENT_ARGUMENT_SCHEME = "FRAGMENT_ARGUMENT_SCHEME";

    public static final int FAX_OUT_FROM_FIRST_LEVEL = 1;
    public static final int FAX_OUT_FROM_SECOND_LEVEL = 2;

    public static final String FAX_OUT_FROM_LEVEL = "FAX_OUT_FROM_LEVEL";

    public static final NumericVersion IBO_SERVICE_API_VERSION = new NumericVersion(7, 3, 0, 0);

    public static final NumericVersion RIONG_OUT_WITH_COUNTRY_ID_API_VERSION = new NumericVersion(7, 5, 0, 0);

    public static final String RCC_MODE="RCC";

    public static final String ACTION_UI_CONTACT_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.ui_contact_changed";

    public static final String ACTION_PHOTO_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.photo_changed";

    public static final String ACTION_DIRECT_NUMBER_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.direct_number_changed";
    public static final String ACTION_CLOUD_CONTACT_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.cloud_contact_changed";
    public static final String ACTION_CLOUD_FAVORITE_CHANGED = BuildConfig.APPLICATION_ID + ".intent.action.cloud_favorite_changed";

    public static final String INTENT_FROM_UPDATE_INCOMING_CALL_NOTIFICATION = BuildConfig.APPLICATION_ID+".intent.action.INTENT_FROM_UPDATE_INCOMING_CALL_NOTIFICATION";
    public static final String STATUS_CODE = BuildConfig.APPLICATION_ID+".intent.action.STATUS_CODE";

    public static final String NOTIFICATION_VM_PLAY_FLAG = "NOTIFICATION_VM_PLAY_FLAG";
    public static final String NOTIFICATION_CANCEL_ID = "NOTIFICATION_CANCEL_ID";
    public static final String NOTIFICATION_TEXT_REPLY_ACTION = "NOTIFICATION_TEXT_REPLY_ACTION";
    public static final String NOTIFICATION_TEXT_CALL_ACTION = "NOTIFICATION_TEXT_CALL_ACTION";
    public static final String NOTIFICATION_VOICEMAIL_PLAY = "NOTIFICATION_VOICEMAIL_PLAY";
    public static final String NOTIFICATION_VOICEMAIL_PLAY_ACTION = "NOTIFICATION_VOICEMAIL_PLAY_ACTION";
    public static final String NOTIFICATION_VOICEMAIL_CALL_ACTION = "NOTIFICATION_VOICEMAIL_CALL_ACTION";
    public static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";

    public static final String LAUNCH_FIRST_TIME = "LAUNCH_FIRST_TIME";

    public static final int LAUNCH_TYPE_TEXT_MESSAGE = 10;
    public static final int LAUNCH_TYPE_MESSAGE_DETAIL = 11;

    public static final int EVENT_DELAY_TIME = 500;
    public static final int EVENT_UPDATE_DELAY_TIME = 50;

    public static final int CALENDAR_SOURCE_LOCAL = 1;

    public static final String PAGING_SERVICE_NUMBER = "*84";

    public static final String SP_LANGUAGE_EDITOR = "RC_LANGUAGE_SETTINGS";
    public static final String SP_LANGUAGE_JUST_LOGIN = "RC_LANGUAGE_JUST_LOGIN";

}
