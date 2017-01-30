package com.example.nickgao.contacts.adapters.contactsprovider;

/**
 * Created by nick.gao on 1/29/17.
 */

public class RequestInfoStorage {

    public static final int LOGIN = 10;
    public static final int RELOGIN_INTERNALLY = 11;
    public static final int LOGIN_CHECK = 12;
    //    public static final int GET_API_VERSION = 20;
    //public static final int GET_USER_INFO = 25;
    public static final int GET_ACCOUNT_INFO = 30;
    public static final int GET_ACCOUNT_INFO_CHECK_COUNTERS = 31;
    public static final int GET_ACCOUNT_INFO_CHECK_EXT_COUNTER = 32;
    public static final int GET_ACCOUNT_INFO_CHECK_MSG_COUNTER = 33;
    public static final int SET_DND_STATUS = 40;
    public static final int SET_EXTENDED_DND_STATUS = 45;
    public static final int GET_CALLER_IDS = 50;
    //public static final int GET_MESSAGES = 60;
    //public static final int GET_MESSAGE = 70;
    public static final int LIST_EXTENSIONS = 90;
    //public static final int SET_FORWARDING_NUMBERS = 100;
    public static final int RINGOUT_CALL = 110;
    public static final int CALL_STATUS = 120;
    public static final int RINGOUT_CANCEL = 130;
    public static final int GET_ALL_CALL_LOGS = 140;
    public static final int GET_MISSED_CALL_LOGS = 150;
    public static final int DIRECT_RINGOUT = 160;
    public static final int SET_SETUP_WIZARD_STATE = 170;

    public static final int REST_API_VERSION = 200;
    public static final int REST_AUTHORIZATION = 201;
    public static final int REST_SERVICE_INFO = 202;
    public static final int REST_PHONE_NUMBERS = 203;
    public static final int REST_SPECIAL_NUMBERS = 204;

    public static final int REST_CREATE_SINGLE_CONTACT = 260;
    public static final int REST_UPDATE_CONTACT = 261;
    public static final int REST_DELETE_CONTACT = 262;
    public static final int REST_IMPORT_SINGLE_CONTACT = 263;
    public static final int REST_ADD_TO_EXISTING_CONTACT = 264;

    public static final int GET_MOBILE_WEB_URL_PARAMETER_INFO = 301;

}
