package com.example.nickgao.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.nickgao.database.RCMDataStore.*;
import com.example.nickgao.logging.BUILD;
import com.example.nickgao.logging.EngLog;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.RCMConstants;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;


public class RCMProviderHelper {

    private static final String TAG = "[RC]RCMProviderHelper";
    private static boolean sMsgCounterEnabled = true;
    private static long CURRENT_MAILBOX_ID = -1;
    private static String SERVICE_API_VERSION = null;
    private static int SERVICE_VERSION = -1;

    public static void setCurrentMailboxId(Context context, long mailbox_id) {
    	
    	if (LogSettings.MARKET) {
    		MktLog.i(TAG, "setCurrentMailboxId");
    	}
    	
    	try {
    		long mailbox_id_current = getCurrentMailboxId(context);
    		if (mailbox_id_current != mailbox_id) {
    			RWL.writeLock().tryLock();
    			if (LogSettings.MARKET) {
    				MktLog.i(TAG, "setCurrentMailboxId(): " + mailbox_id);
    			}
    			CURRENT_MAILBOX_ID = -1;
    			updateSingleValue(context, RCMProvider.MAILBOX_CURRENT, MailboxCurrentTable.MAILBOX_ID, String.valueOf(mailbox_id), null);
    		}
    	} finally {
    		try {
    			if (RWL.isWriteLocked()) {
    				RWL.writeLock().unlock();
    			}
    		} catch (Exception e) {
    		}
     	}
    }
    
    
    public static void setRestRefreshToken(Context context, long mailboxId, String refreshToken) {
        String value;
        if (TextUtils.isEmpty(refreshToken)) {
            value = "";
        } else {
            value = refreshToken;
        }
        updateOrInsertSingleValueByMailboxId(context, mailboxId, RCMProvider.SERVICE_EXTENSION_INFO,
                ServiceExtensionInfoTable.REST_REFRESH_TOKEN, value);
    }
    
    public static void setCurrentPollingStatus(Context context, int pollingStatus) {
    	int currentPollingStatus = getCurrentPollingStatus(context);
    	if (currentPollingStatus != pollingStatus) {
    		if (LogSettings.MARKET) {
    			MktLog.i(TAG, "setPollingStatus(): " + pollingStatus);
    		}
    		updateSingleValue(context, RCMProvider.MAILBOX_CURRENT, MailboxCurrentTable.POLLING_STATUS, String.valueOf(pollingStatus), null);
    	}
    }
    
    private static ReentrantReadWriteLock RWL = new ReentrantReadWriteLock();
    
    public static long getCurrentMailboxId(Context context) {
    	
    	RWL.readLock().tryLock();
    	if (CURRENT_MAILBOX_ID != -1 && CURRENT_MAILBOX_ID != MailboxCurrentTable.MAILBOX_ID_NONE) {
    		try {
    			return CURRENT_MAILBOX_ID;
    		} finally {
    			try {
    				RWL.readLock().unlock();
    			} catch (Exception e) {
    			}
    		}
    	} 

    	try {
    		RWL.writeLock().tryLock();
    	} catch (Exception e) {
    	}
    	
    	CURRENT_MAILBOX_ID = MailboxCurrentTable.MAILBOX_ID_NONE;
        
        Cursor c = null;
        try {
        	c = context.getContentResolver().query(UriHelper.getUri(RCMProvider.MAILBOX_CURRENT), 
        			new String[]{MailboxCurrentTable.MAILBOX_ID}, null, null, null);
        	
        	if (c != null && c.moveToNext()) {
        		CURRENT_MAILBOX_ID = c.getLong(0);
        	}
        	if (LogSettings.MARKET) {
        		MktLog.i(TAG, "CURRENT_MAILBOX_ID : " + CURRENT_MAILBOX_ID);
        	}
        	
        } finally {
        	try {
        		if (c != null && !c.isClosed()) {
        			c.close();
        		}
        	} catch (Exception e) {
        	}
        	
        	try {
        		if (RWL.isWriteLocked()) {
        			RWL.writeLock().unlock();
        		}
        	} catch (Exception e) {
        	}
        }
        
        return CURRENT_MAILBOX_ID;
    }
    
    public static void restoreCurrentMailboxId(Context context) {
    	
    	if (LogSettings.MARKET) {
    		MktLog.i(TAG, "restoreCurrentMailboxId");
    	}
    	
    	try {
    		RWL.writeLock().tryLock();
    		CURRENT_MAILBOX_ID = -1;
    	} finally {
    		try {
    			if (RWL.isWriteLocked()) {
    				RWL.writeLock().unlock();
    			}
    		} catch (Exception e) {
    		}
    	}
    }
    
    public static String getHttpCookie(Context context) {
    	
    	String http_cookie = "";
    	
    	Cursor c = null;
        try {
        	c = context.getContentResolver().query(UriHelper.getUri(RCMProvider.MAILBOX_CURRENT), 
        			new String[]{MailboxCurrentTable.HTTP_COOKIE}, null, null, null);
        	if (c != null && c.moveToNext()) {
        		http_cookie = c.getString(0);
        	}
        } finally {
        	try {
        		if (c != null && !c.isClosed()) {
        			c.close();
        		}
        	} catch (Exception e) {
        	}
        }
        
        return http_cookie;
    }
    
    public static void setHttpCookie(Context context, String cookie) {
    	updateSingleValue(context, RCMProvider.MAILBOX_CURRENT, MailboxCurrentTable.HTTP_COOKIE, cookie, null);
    }
    
    
    public static int getCurrentPollingStatus(Context context) {
    	String result = simpleQuery(context, RCMProvider.MAILBOX_CURRENT, MailboxCurrentTable.POLLING_STATUS);
        int status = TextUtils.isEmpty(result) ? MailboxCurrentTable.POLLING_ENABLE : Integer.valueOf(result);
        return status;
    }

    
    public static String getAccessLevel(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAccessLevel()...");

        String value = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_ACCESS_LEVEL);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAccessLevel(): return " + value);
        return value;
    }

    /**
     * Returns Account tier type (Tier Service type, e.g. "RCMobile", "RCOffice", "RCVoice", "RCFax"). Valid from 5.0.x
     * 
     * @param context the execution context
     * 
     * @return tier service type
     */
    public static String getTierServiceType(Context context) {
        String value = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_TIER_SERVICE_TYPE);
    
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getTierServiceType(): return " + value);
        }
        return value;
    }
    
    public static String getExtensionType(Context context) {
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getExtensionType()...");
        }

        String value = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_EXTENSION_TYPE);

        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getExtensionType(): return " + value);
        }
        return value;
    }

    public static boolean isSystemExtension(Context context) {
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "isSystemExtension()...");
        }        

        String result_str = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_SYSTEM_EXTENSION);
        boolean result = TextUtils.isEmpty(result_str) ? false : (Integer.valueOf(result_str) != 0); //false - default value in case of DB error

        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "isSystemExtension(): return " + result);
        }
        return result;
    }
    

    
    public static int getServiceVersion(Context context) {
    	return getServiceVersion(context, false);
    }
    
    
    /**
     * AB-12580 Unable to get account's "Text""Fax"/"Meetings"/"Conference" permission info and "Settings" info when first login app
     * For 5.01 account
     * 
     * @param context
     * @param needUpdate
     * @return
     */
    public static int getServiceVersion(Context context, boolean needUpdate) {
    	
    	if (SERVICE_VERSION == -1 || needUpdate) {
    		if (LogSettings.ENGINEERING)
                EngLog.d(TAG, "getServiceVersion() from DB...");
    		SERVICE_VERSION = (int) simpleQueryWithMailboxIdLong(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_SERVICE_VERSION, AccountInfoTable.SERVICE_VERSION_4);
    	}

        if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getServiceVersion(): return " + SERVICE_VERSION);

        return SERVICE_VERSION;
    }
    
    /**
     * Save service version
     * 
     * @param context
     * @param value
     */
    public static void saveServiceVersion(Context context, int service_version) {
    	int service_version_current = getServiceVersion(context);
		if (service_version_current != service_version) {
			if (LogSettings.ENGINEERING) {
				MktLog.i(TAG, "saveServiceVersion(): " +  service_version);
			}
			SERVICE_VERSION = -1;
			updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_SERVICE_VERSION, String.valueOf(service_version));
    	}
        
    }

    public static long getTierSettings(Context context) {
        return simpleQueryWithMailboxIdLong(context, RCMProvider.TIER_SETTINGS, AccountInfoTable.JEDI_TIER_SETTINGS, 0 );
    }

    public static String getAccountFree(Context context) {
        if (RCMProvider.DEBUG_ENBL) {
        	EngLog.d(TAG, "getAccountFree()...");
        }

        String result = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_FREE);

        if (RCMProvider.DEBUG_ENBL) {
        	EngLog.d(TAG, "getAccountFree(): return " + result);
        }
        return result;
    }

    /*
     * Extensions Modification Counter
     */
    public static void saveExtModCounter(Context context, long value) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveExtModCounter(): " + value);
        }
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.EXT_MOD_COUNTER, AccountInfoTable.JEDI_EXT_MOD_COUNTER, String.valueOf(value));
    }

    public static long getExtModCounter(Context context) {
        return simpleQueryWithMailboxIdLong(context, RCMProvider.EXT_MOD_COUNTER, AccountInfoTable.JEDI_EXT_MOD_COUNTER, -1 );
    }

    public static long getExtModCounterTemp(Context context) {
        return simpleQueryWithMailboxIdLong(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_EXT_MOD_COUNTER_TEMP, -1 );
    }

    /*
     * Message Modification Counter 
     */
    public static void saveMsgModCounter(Context context, long value) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveMsgModCounter(): " + value);
        }
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.MSG_MOD_COUNTER, AccountInfoTable.JEDI_MSG_MOD_COUNTER, String.valueOf(value));
    }

    public static long getMsgModCounter(Context context) {
        if (!sMsgCounterEnabled){
            return 1;
        }
        return simpleQueryWithMailboxIdLong(context, RCMProvider.MSG_MOD_COUNTER, AccountInfoTable.JEDI_MSG_MOD_COUNTER, -1 );
    }

    public static long getMsgModCounterTemp(Context context) {
        if (!sMsgCounterEnabled){
            return 2;
        }
        return simpleQueryWithMailboxIdLong(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_MSG_MOD_COUNTER_TEMP, -1 );
    }
    
    public static void setMsgCounterEnabled(boolean enabled) {
        if (BUILD.TEST_MODE) {
            sMsgCounterEnabled = enabled;
        }
    }
    
    /*
     * Last Loaded Message ID 
     */
    public static void saveLastLoadedMsgId(Context context, long value) {
        if (LogSettings.MARKET) {
            EngLog.i(TAG, "saveLastLoadedMsgId(): " + value);
        }
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_LAST_LOADED_MSG_ID, String.valueOf(value));
    }
    
    public static long getLastLoadedMsgId(Context context) {
        if (!sMsgCounterEnabled){
            return 0;
        }
        return simpleQueryWithMailboxIdLong(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_LAST_LOADED_MSG_ID, 0 );
    }
    
    /*
     * Last complete setup request
     */
    public static void saveLastCompleteSetupRequest(Context context, long value) {
        if (LogSettings.MARKET) {
            EngLog.i(TAG, "saveLastCompleteSetupRequest(): " + value);
        }
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_LAST_COMPLETE_SETUP_REQ, String.valueOf(value));
    }
    
    public static long getLastCompleteSetupRequest(Context context) {    	
       return simpleQueryWithMailboxIdLong(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_LAST_COMPLETE_SETUP_REQ, 0 );
    }
    
    /*
     * Last expiration reminders
     */
    public static void saveLastExpirationReminder(Context context, long value) {
        if (LogSettings.MARKET) {
            EngLog.i(TAG, "saveLastExpirationReminder(): " + value);
        }
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_LAST_EXPIRED_REQ, String.valueOf(value));
    }
    
    public static long getLastExpirationReminder(Context context) {    	
       return simpleQueryWithMailboxIdLong(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_LAST_EXPIRED_REQ, 0 );
    }
    
    /*
     * Custom Phone Number
     */
    public static void saveCustomPhoneNumber(Context context, String number) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveCustomPhoneNumber(): " + (number == null ? "null":number));
        }
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_CUSTOM_NUMBER, number);
    }

    public static String getCustomPhoneNumber(Context context) {
        return simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_CUSTOM_NUMBER);
    }


    /*
     * Device IMSI
     */
    public static void saveDeviceIMSI(Context context, String IMSI) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveDeviceIMSI(): " + (IMSI == null ? "null":IMSI));
        }
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_IMSI, IMSI, null);
    }

    public static String getDeviceIMSI(Context context) {
        return simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_IMSI);
    }

    public static boolean isUiLaunchedAfterBoot(Context context) {
        String resultString = simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_UI_LAUNCHED_AFTER_BOOT);
        boolean result = TextUtils.isEmpty(resultString) ? true : (Integer.valueOf(resultString) != 0); //true - default value in case of DB error

        if(LogSettings.MARKET) {
            MktLog.i(TAG, "isUiLaunchedAfterBoot(): " + result);
        }

        return result;
    }

    public static void setUiLaunchedAfterBoot(Context context, boolean value) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "setUiLaunchedAfterBoot(): " + value);
        }
        String valueString = String.valueOf(value ? "1" : "0");
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_UI_LAUNCHED_AFTER_BOOT, valueString, null);
    }

    public static int getForcedRestartStatus(Context context) {
        String value = simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_FORCED_RESTART);
        int result = TextUtils.isEmpty(value) ? DeviceAttribTable.FORCED_RESTART_NONE : Integer.valueOf(value);

        if (LogSettings.MARKET) {
            MktLog.i(TAG, "getForcedRestartStatus(): " + result);
        }

        return result;
    }

    public static void setForcedRestartStatus(Context context, int value) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "setForcedRestartStatus(" + value + ")...");
        }

        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_FORCED_RESTART, Integer.toString(value), null);
    }


    public static boolean getDeviceEchoState(Context context) {
        String resultString = simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_ECHO_STATE);
        
        boolean result = TextUtils.isEmpty(resultString) ? false : (Integer.valueOf(resultString) != 0); //false - default value in case of DB error
        
        if(LogSettings.MARKET) {
            MktLog.i(TAG, "getDeviceEchoState " + result);
        }
        
        return result;
    }
    public static void saveDeviceEchoState(Context context, boolean value) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveDeviceEchoState(): " + value);
        }
        String valueString = String.valueOf(value ? "1" : "0");
        
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_ECHO_STATE, valueString, null);
    }
    

    public static boolean isTosAccepted(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isTosAccepted()...");

        String result_str = simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_TOS_ACCEPTED);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isTosAccepted(): result_str == " + result_str);

        boolean result = TextUtils.isEmpty(result_str) ? false : result_str.equals(BUILD.TOS_VERSION+""); //false - default value in case of DB error

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isTosAccepted(): return " + result);
        return result;
    }

    /**
     * 
     * @param context
     * @return 0 - user has never passed dialog
     *         1 - Accept
     *         2 - Decline
     */
    public static int getTos911State(Context context){
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isTos911Accepted()...");

        String result_str = simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_TOS911_ACCEPTED);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isTos911Accepted(): result_str == " + result_str);

        int result = TextUtils.isEmpty(result_str) ? 0 : Integer.parseInt(result_str); //false - default value in case of DB error

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isTos911Accepted(): return " + result);
        return result;
    }
    
    public static boolean isTos911Accepted(Context context){
    	return getTos911State(context) == 1;
    }
    
    public static boolean isTos911Executed(Context context){
    	return getTos911State(context) != 0;
    }
    
    public static int getAppCurrentVersionInDB(Context context){
        if (RCMProvider.DEBUG_ENBL) { 
            EngLog.d(TAG, "getAppCurrentVersionInDB()...");
        }

        String result_str = simpleQueryWithMailboxId(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_APP_VERSION);

        if (RCMProvider.DEBUG_ENBL) { 
            EngLog.d(TAG, "getAppCurrentVersionInDB(): version == " + result_str);
        }

        String result = TextUtils.isEmpty(result_str) ? "0" : result_str; //false - default value in case of DB error

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAppCurrentVersionInDB(): version = " + result);
        return Integer.parseInt(result);
    }
    
    public static boolean isVoipWhatsNewDialogShown(Context context){
        if (RCMProvider.DEBUG_ENBL) { 
            EngLog.d(TAG, "isVoipWhatsNewDialogShown()...");
        }

        String result_str = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_VOIP_WHATS_NEW);

        if (RCMProvider.DEBUG_ENBL) { 
            EngLog.d(TAG, "isVoipWhatsNewDialogShown(): result_str == " + result_str);
        }

        boolean result = TextUtils.isEmpty(result_str) ? false : result_str.equals("1"); //false - default value in case of DB error

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isVoipWhatsNewDialogShown(): return " + result);
        return result;
    }
    public static int getDeviceInternalSpeakerDelay(Context context){
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getInternalSpeakerDelay()...");

        String result_str = simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_ECHO_VALUE_EARPIECE);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getInternalSpeakerDelay(): result_str == " + result_str);

        int result = TextUtils.isEmpty(result_str) ? RCMConstants.DEFAULT_AEC_DELAY_VALUE : Integer.valueOf(result_str); //false - default value in case of DB error

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isTos911Accepted(): return " + result);
        return result;
    }
    public static int getDeviceExternalSpeakerDelay(Context context){
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getExternalSpeakerDelay()...");

        String result_str = simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_ECHO_VALUE_SPEAKER);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getExternalSpeakerDelay(): result_str == " + result_str);

        int result = TextUtils.isEmpty(result_str) ? RCMConstants.DEFAULT_AEC_DELAY_VALUE : Integer.valueOf(result_str);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getExternalSpeakerDelay(): return " + result);
        return result;
    }
    public static boolean getDeviceAudioWizard(Context context){
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getDeviceAudioWizard()...");

        String result_str = simpleQuery(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_AUDIO_SETUP_WIZARD);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getDeviceAudioWizard(): result_str == " + result_str);

        boolean result = TextUtils.isEmpty(result_str) ? false : result_str.equals("1");

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getDeviceAudioWizard(): return " + result);
        
        return result;
    }
    /*
     * Caller ID
     */
    public static void saveCallerID(Context context, String id) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveCallerID(): " + (id == null ? "null":id));
        }
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_CALLER_ID, id);
    }

    
    
    
    
    

    /**
     * Get the "MainNumber" Caller ID
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getMainNumber(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getMainNumber()...");

        String number = simpleSelectionQueryWithMailboxId(
                context,
                RCMProvider.CALLER_IDS,
                CallerIDsTable.JEDI_NUMBER,
                CallerIDsTable.JEDI_USAGE_TYPE + " LIKE '" + CallerIDsTable.USAGE_TYPE_MAIN_NUMBER + "'");

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getMainNumber(): return " + number);
        return number;
    }
    
    /**
     * Defines if <code>number</code> exists in current CallerIds
     * 
     * @param context
     *            the execution context
     * @param number
     *            the number to be search
     * @return <code>true</code> if the <code>number</code> exists in the
     *         CallerIds, otherwise <code>false</code>
     * 
     */
    public static boolean isNumberInCallerIds(Context context, String number) {
        String found = simpleSelectionQueryWithMailboxId(
                context,
                RCMProvider.CALLER_IDS,
                CallerIDsTable.JEDI_NUMBER,
                CallerIDsTable.JEDI_NUMBER + " LIKE '" + number + "'");
        if (TextUtils.isEmpty(found)) {
            return false;
        }
        return true;
    }
    
    
    /**
     * Get account number from ACCOUNT_INFO
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getAccountNumber(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAccountNumber()...");

        String number = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_ACCOUNT_NUMBER);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAccountNumber(): return " + number);
        return number;
    }

    public static String getAccountPin(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAccountPin()...");

        String pin = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_PIN);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAccountPin(): return " + pin);
        return pin;
    }

    /**
     * Save login number
     *
     * @param context
     * @param loginNumber
     */
    public static void saveLoginNumber(Context context, String loginNumber) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveLoginNumber(): " + (loginNumber == null ? "null":loginNumber));
        }
        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.RCM_LOGIN_NUMBER, loginNumber, null);
    }

    /**
     * Get login number
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getLoginNumber(Context context) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginNumber()...");
        }

        String loginNumber = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.RCM_LOGIN_NUMBER);

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginNumber(): return " + loginNumber);
        }
        return loginNumber;
    }

    /**
     * Save login ext
     *
     * @param context
     * @param ext
     */
    public static void saveLoginExt(Context context, String ext) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveLoginExt(): " + (ext == null ? "null":ext));
        }

        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.RCM_LOGIN_EXT, ext, null);
    }

    /**
     * Get login ext
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getLoginExt(Context context) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginExt()...");
        }

        String ext = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.RCM_LOGIN_EXT);

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginExt(): return " + ext);
        }
        return ext;
    }

    /**
     * Save login password
     *
     * @param context
     * @param password
     */
    public static void saveLoginPassword(Context context, String password) {
        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.RCM_PASSWORD, password, null);
    }

    /**
     * Get login password
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getLoginPassword(Context context) {
        String password = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.RCM_PASSWORD);
        return password;
    }

    /**
     * Save login IP address
     *
     * @param context
     * @param address
     */
    public static void saveLoginIPAddress(Context context, String address) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveLoginIPAddress(): " + (address == null ? "null":address));
        }

        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_IP_ADDRESS, address, null);
    }

    /**
     * Get login IP address
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getLoginIPAddress(Context context) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginIPAddress()...");
        }

        String address = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_IP_ADDRESS);

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginIPAddress(): return " + address);
        }
        return address;
    }

    /**
     * Save login request ID
     *
     * @param context
     * @param id
     */
    public static void saveLoginRequestID(Context context, String id) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "saveLoginRequestID(" + id + ")...");
        }

        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_REQUEST_ID, id, null);
    }

    /**
     * Get login request ID
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getLoginRequestID(Context context) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginIPAddress()...");
        }

//        String id = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.RCM_LOGIN_IP_ADDRESS);
        String id = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_REQUEST_ID);

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginRequestID(): return " + id);
        }
        return id;
    }

    /**
     * Save login start time
     *
     * @param context
     * @param startTime
     */
    public static void saveLoginStartTime(Context context, String startTime) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "saveLoginStartTime(" + startTime + ")...");
        }

        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_START_TIME, startTime, null);
    }

    /**
     * Get login start time
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getLoginStartTime(Context context) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginStartTime()...");
        }

        String time = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_START_TIME);

        if (LogSettings.ENGINEERING) {
            EngLog.d(TAG, "getLoginStartTime(): return " + time);
        }
        return time;
    }
    
    /**
     * Save login hash
     *
     * @param context
     * @param hash
     */
    public static void saveLoginHash(Context context, String hash) {

        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_HASH, hash, null);
    }

    /**
     * Get login hash
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getLoginHash(Context context) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginHash()...");
        }

        String hash = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_HASH);

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginHash(): return " + hash);
        }
        return hash;
    }
    
    /**
     * Get login tierId
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getTierId(Context context) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getLoginHash()...");
        }

        String tier_id = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.RCM_TIER_ID);

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getTierId(): return " + tier_id);
        }
        return tier_id;
    }
    

    /**
     * Save login cookies
     *
     * @param context
     * @param cookie
     */
    public static void saveLoginCookie(Context context, String cookie) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveLoginIPAddress(): " + (cookie == null ? "null":cookie));
        }

        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_COOKIE, cookie, null);
    }

    /**
     * Get login cookies
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getLoginCookie(Context context) {
    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getLoginCookie()...");

        String cookie = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_LOGIN_COOKIE);

    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getLoginCookie(): return " + cookie);

    	return cookie;
    }

    /**
     * Save service API version
     * 
     * @param context
     * @param value
     */
    public static void saveServiceApiVersion(Context context, String service_api_version) {
    	String service_api_version_current = getServiceApiVersion(context);
		if (!service_api_version_current.equals(service_api_version)) {
			if (LogSettings.ENGINEERING) {
				MktLog.i(TAG, "saveApiVersion(): " + (TextUtils.isEmpty(service_api_version) ? "null" : service_api_version));
			}
			SERVICE_API_VERSION = null;
			updateOrInsertSingleValueWithMailboxId(context, RCMProvider.SERVICE_INFO, RCMDataStore.ServiceInfoTable.REST_SERVER_VERSION, service_api_version);
    	}
        
    }

    /**
     * DO NOT USE FOR UI ELEMENTS HIDING, PLEASE !!1
     * DO NOT MIX-UP WITH RCMProviderHelper.getServiceVersion !!1
     * 
     * Get service API version
     * used for voip status 
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getServiceApiVersion(Context context) {
    	if (TextUtils.isEmpty(SERVICE_API_VERSION)) {
    		if (LogSettings.ENGINEERING)
                EngLog.d(TAG, "getServiceApiVersion() from DB...");
    		SERVICE_API_VERSION = simpleQueryWithMailboxId(context, RCMProvider.SERVICE_INFO, RCMDataStore.ServiceInfoTable.REST_SERVER_VERSION);
    	}

        if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getServiceApiVersion(): return " + SERVICE_API_VERSION);

        return SERVICE_API_VERSION;
    }
    
    
    /**
     * Save userId
     *
     * @param context
     * @param userId
     */
    public static void saveUserId(Context context, String userId) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveUserId(): " + (userId == null ? "null":userId));
        }
        updateSingleValue(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_USER_ID, userId, null);
    }

    /**
     * Get userId received from Login request 
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getUserId(Context context) {
    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getUserId()...");

        String userId = simpleQuery(context, RCMProvider.USER_CREDENTIALS, RCMDataStore.UserCredentialsTable.JEDI_USER_ID);

    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getUserId(): return " + userId);
    	
        return userId;
    }


    /**
     * Get userId received from AccountInfo request
     * Normally shall be the same as returned by getUserId()
     *
     * @param context
     * @return saved value or empty string
     */
    public static String getAccountInfoUserId(Context context) {
    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getAccountInfoUserId()...");

        String userId = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, RCMDataStore.AccountInfoTable.JEDI_USER_ID);

    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getAccountInfoUserId(): return " + userId);

    	return userId;
    }

    public static boolean isAgent(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isAgent()...");

        String result_str = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_AGENT);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isAgent(): result_str == " + result_str);

        boolean result = TextUtils.isEmpty(result_str) ? false : (Integer.valueOf(result_str) == 1); //false - default value in case of DB error

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isAgent(): return " + result);
        return result;
    }

    public static String getAgentStatus(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAgentStatus()...");

        String status = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.JEDI_AGENT_STATUS);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getAgentStatus(): return " + status);
        return status;
    }
    
    public static void saveRingoutAnotherPhone(Context context, String number) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveRingoutAnotherPhone(): " + (number == null ? "null":number));
        }

        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_RINGOUT_ANOTHER_PHONE, number);
    }

    public static String getRingoutAnotherPhone(Context context) {
    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getRingoutAnotherPhone()...");

        String number = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_RINGOUT_ANOTHER_PHONE);

    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getRingoutAnotherPhone(): return " + number);
        return number;
    }
    
    public static String getRingoutCustomPhone(Context context) {
    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getRingoutAnotherPhone()...");

        String number = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_CUSTOM_NUMBER);

    	if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getRingoutAnotherPhone(): return " + number);
        return number;
    }
    

    public static void saveLastCallNumber(Context context, String number) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveLastCallNumber(): " + (number == null ? "null":number));
        }

        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_LAST_CALL_NUMBER, number);
    }


    public static int getMessagesLoadedCount(Context context) {
        return getRecordsCount(context, RCMProvider.MESSAGES, MessagesTable.RCM_SYNC_STATUS + '=' + SyncStatusEnum.SYNC_STATUS_LOADED);
    }

    public static int getMessagesLoadingCount(Context context) {
        return getRecordsCount(context, RCMProvider.MESSAGES, MessagesTable.RCM_SYNC_STATUS + '=' + SyncStatusEnum.SYNC_STATUS_LOADING);
    }

    public static int getMessagesNotLoadedCount(Context context) {
        return getRecordsCount(context, RCMProvider.MESSAGES, MessagesTable.RCM_SYNC_STATUS + '=' + SyncStatusEnum.SYNC_STATUS_NOT_LOADED);
    }

    public static void saveHttpRegInstanceId(Context context, String id) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveHttpRegInstanceId(" + id + ")");
        }

        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.HTTPREG_INSTANCE_ID, id);
    }

    public static String getHttpRegInstanceId(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getHttpRegInstanceId()...");

        String id = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.HTTPREG_INSTANCE_ID);

        if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getHttpRegInstanceId(): return " + id);
        return id;
    }

    /**
     * Get  VoIP HTTP-REG SIP flags
     */
    public static long getHttpRegSipFlags(Context context) {
        return simpleQueryWithMailboxIdLong(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_VOIP_HTTPREG_SIPFLAGS, 0);
    }
    
    /**
     * Set VoIP HTTP-REG SIP flags
     */
    public static void setHttpRegSipFlags(Context context, long sipFlags) {
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_VOIP_HTTPREG_SIPFLAGS, String.valueOf(sipFlags));
    }
////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Service functions
     */
    
    public static String simpleQueryWithMailboxId(Context context, String uri_path, String column) {
        long mailbox_id = getCurrentMailboxId(context);
        Uri uri = UriHelper.getUri(uri_path, mailbox_id);
        return simpleQuery(context, uri, column, null);
    }

    public static String simpleQueryByMailboxId(Context context, long mailboxId, String uri_path, String column) {
        Uri uri = UriHelper.getUri(uri_path, mailboxId);
        return simpleQuery(context, uri, column, null);
    }
    
    public static String simpleQuery(Context context, String uri_path, String column ) {
        Uri uri = UriHelper.getUri(uri_path);
        return simpleQuery(context, uri, column, null);
    }

    private static String simpleQuery(Context context, Uri uri, String column, String selection) {
    	if (context == null) {
    		return "";    		
    	}
    	
    	boolean isPassword = false;
		if (column != null && column.indexOf(RCMDataStore.UserCredentialsTable.RCM_PASSWORD) >= 0) {
			isPassword = true;
		}
        if (RCMProvider.DEBUG_ENBL) {
        	EngLog.d(TAG, "simpleQuery(" + uri + ", " + (isPassword?"":column) + ") " + ( selection == null ? "" : " selection: " + selection ) );
        }
        
        Cursor cursor = null;

        try {
        	cursor = context.getContentResolver().query(uri, new String[]{column}, selection, null, null);
        	if (cursor == null) {
        		if (RCMProvider.DEBUG_ENBL) {
        			EngLog.d(TAG, "simpleQuery(): null cursor received; return \"\"");
        		}
        		return "";
        	}
        	
        	if (!cursor.moveToFirst()) {
        		if (RCMProvider.DEBUG_ENBL) {
        			EngLog.d(TAG, "simpleQuery(): empty cursor received; return \"\", count: " + cursor.getCount());
        		}
//        		cursor.close();
        		return "";
        	}
        	
        	String result = cursor.getString(0);
        	if (result == null) {
        		if (RCMProvider.DEBUG_ENBL)
        			EngLog.d(TAG, "simpleQuery(): cursor returned null; return \"\"");
        		result = "";
        	}
        	return result;
        } finally {
        	try {
        		if (cursor != null && !cursor.isClosed()) {
        			cursor.close();
        		}
        	} catch (Exception e) {
        	}
        }

    }

    private static long simpleQueryLong(Context context, String uri_path, String column, long mailbox_id, long def_value ) {
        Uri uri = UriHelper.getUri(uri_path, mailbox_id);
        String result = simpleQuery(context, uri, column, null);
        long value = TextUtils.isEmpty(result) ? def_value : Long.valueOf(result);
        return value;
    }
    
    private static long simpleQueryWithMailboxIdLong(Context context, String uri_path, String column, long def_value ) {
        String result = simpleQueryWithMailboxId(context, uri_path, column );
        long value = TextUtils.isEmpty(result) ? def_value : Long.valueOf(result);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "column: " + column + " result:" + value);
        
        return value;
    }

    private static int simpleQueryWithMailboxIdInt(Context context, String uri_path, String column, int def_value ) {
        String result = simpleQueryWithMailboxId(context, uri_path, column );
        int value = TextUtils.isEmpty(result) ? def_value : Integer.valueOf(result);

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "column: " + column + " result:" + value);
        
        return value;
    }
    

    private static String simpleSelectionQueryWithMailboxId(Context context, String uri_path, String column, String selection) {
        long mailbox_id = getCurrentMailboxId(context);
        Uri uri = UriHelper.getUri(uri_path, mailbox_id);
        return simpleQuery(context, uri, column, selection);
    }

    
    public static int updateSingleValue(Context context, String uri_path, String column, String value, String where) {
        Uri uri = UriHelper.getUri(uri_path);
        ContentValues values = new ContentValues();
        values.put(column, value);
        return context.getContentResolver().update(uri, values, where, null);   //returns the number of updated rows
    }

    public static int updateSingleValueWithMailboxId(Context context, String uri_path, String column, String value, String where) {
        long mailbox_id = getCurrentMailboxId(context);
        String where_where = RCMColumns.MAILBOX_ID + '=' + mailbox_id; 

        if (!TextUtils.isEmpty(where)) {
            where_where += " AND " + where;
        }
        
        return updateSingleValue(context, uri_path, column, value, where_where);
    }
    
    public static void updateOrInsertSingleValueWithMailboxId(Context context, String uri_path, String column, String value) {
        long mailbox_id = getCurrentMailboxId(context);
        String where = RCMColumns.MAILBOX_ID + '=' + mailbox_id;
        
        if (updateSingleValue(context, uri_path, column, value, where) <= 0)    //no record with this mailbox_id
        {
            insertSingleValueWithMailboxId(context, uri_path, column, value);
        }
        
    }
    public static void updateOrInsertSingleValueByMailboxId(Context context, long mailboxId, String uri_path, String column, String value) {
        String where = RCMColumns.MAILBOX_ID + '=' + mailboxId;
        if (updateSingleValue(context, uri_path, column, value, where) <= 0) {
            insertSingleValueWithMailboxId(context, uri_path, column, value);
        }
    }

    private static void insertSingleValueWithMailboxId(Context context, String uri_path, String column, String value) {
        long mailbox_id = getCurrentMailboxId(context);
        ContentValues values = new ContentValues();
        values.put(RCMColumns.MAILBOX_ID, mailbox_id);
        values.put(column, value);
        context.getContentResolver().insert(UriHelper.getUri(uri_path), values);
    }

    private static void clearTableAndInsertEmptyRow(Context context, String uri_path) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(UriHelper.getUri(uri_path), null, null);
        
        ContentValues val = new ContentValues(1);
        val.putNull(BaseColumns._ID);
        resolver.insert(UriHelper.getUri(uri_path), val);
    }
    
    public static int getRecordsCount(Context context, String uri_path, String selection) {
		Cursor cursor = context.getContentResolver().query(
				UriHelper.getUri(uri_path, getCurrentMailboxId(context)), 
				new String[] { BaseColumns._ID }, 
				selection, 
				null, null);
		
		int count = null != cursor ? cursor.getCount() : 0;
		if (null != cursor) {
			cursor.close();
		}
		return count;
    }

    private static int getMaxPosition(Context context, String uri_path, String[] selector, String selectionArgs) {
        Cursor cursor = context.getContentResolver().query(UriHelper.getUri(uri_path, getCurrentMailboxId(context)),
        		selector, selectionArgs, null, null);
        int position = 0;
        if(null != cursor && cursor.moveToFirst()) {
        	position = cursor.getInt(0);
            cursor.close();
        }
        return position;
    }
    
    public static void setTosAccepted(Context context, boolean accepted) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setTosAccepted(" + accepted + ")...");
        
        String value = accepted ? BUILD.TOS_VERSION+"" : "0";
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_TOS_ACCEPTED, value, null);
    }
    /*
    * 0 - user has never passed dialog
    * 1 - Accept
    * 2 - Decline
    */
    public static void set911TosAccepted(Context context, boolean accepted) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "set911TosAccepted(" + accepted + ")...");

        String value = accepted ? "1" : "2";
        int updateSingleValue = updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_TOS911_ACCEPTED, value, null);
        if(LogSettings.MARKET) {
    		MktLog.e(TAG, "set911TosAccepted(" + accepted + ")...affect row "+updateSingleValue);
    	}
    }
    public static void setAccountVoipWhatsNewDialogShown(Context context, boolean shown) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setAccountVoipWhatsNewDialogShown(" + shown + ")...");

        String value = shown ? "1" : "0";
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_VOIP_WHATS_NEW, value);
    }
    
    public static void setAppVersion(Context context,int version){
    	 if (RCMProvider.DEBUG_ENBL)
             EngLog.d(TAG, "setAppVersion(" + version + ")...");
    	 updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_APP_VERSION, String.valueOf(version), null);
    }
    
    public static void setDeviceExternalSpeakerDelay(Context context, int delay) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setAECExternalSpeakerDelay(" + delay + ")...");

        String value = String.valueOf((delay > 0 ? delay : 0));
        
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_ECHO_VALUE_SPEAKER, value, null);
    }
    public static void setDeviceInternalSpeakerDelay(Context context, int delay) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setAECInternalSpeakerDelay(" + delay + ")...");
        
        String value = String.valueOf((delay > 0 ? delay : 0));
        
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_ECHO_VALUE_EARPIECE, value, null);
    }
    public static void setDeviceAudioWizard(Context context, boolean accepted) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setDeviceAudioWizard(" + accepted + ")...");

        String value = accepted ? "1" : "0";
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DEVICE_AUDIO_SETUP_WIZARD, value, null);
    }
    public static long getMessagesLastSync(Context context) {
        long result = 0;
    	if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getMessagesLastSync()...");
        }

        String state = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_MESSAGES_LAST_UPDATED_TIMESTAMP);
        
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getMessagesLastSync(): return " + state);
        }
        
        try {
        	result = (null != state ? Long.valueOf(state) : 0);
        } catch (NumberFormatException e) {
        	if(LogSettings.MARKET) {
        		MktLog.e(TAG, "getMessagesLastSync ");
        	}
		}
        
        return result;
    }
    public static void setMessagesLastSync(Context context, long lastSync) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setMessagesLastSync(" + lastSync + ")...");

        String value = String.valueOf((0 != lastSync) ? lastSync : System.currentTimeMillis());
        updateSingleValue(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_MESSAGES_LAST_UPDATED_TIMESTAMP, value, null);
    }
    
    public static long getConversationLastSync(Context context, long conversationID){
    	long result = 0;
    	if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getConversationLastSync()...");
        }

        String state = simpleSelectionQueryWithMailboxId(context, 
        		RCMProvider.MESSAGE_CONVERSATIONS, 
        		MessageConversationsTable.REST_LOCAL_SYNC_TIME,
        		MessageConversationsTable.RCM_CONVERSATION_ID + "=" + conversationID);
        
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getConversationLastSync(): return " + state);
        }
        
        try {
        	
        	result = (!TextUtils.isEmpty(state) ? Long.valueOf(state) : 0);
        } catch (NumberFormatException e) {
        	if(LogSettings.MARKET) {
        		MktLog.e(TAG, "getConversationLastSync " + e.toString());
        	}
		}
        
        return result;
    }
    public static void setConversationLastSync(Context context, long conversationID, long lastSync){        
    	if (RCMProvider.DEBUG_ENBL)
        EngLog.d(TAG, "setConversationLastSync(" + lastSync + ")...");

        String value = String.valueOf((0 != lastSync) ? lastSync : System.currentTimeMillis());
        
        updateSingleValue(context, 
        		RCMProvider.MESSAGE_CONVERSATIONS, 
        		MessageConversationsTable.REST_LOCAL_SYNC_TIME, 
        		value, 
        		MessageConversationsTable.RCM_CONVERSATION_ID + "=" + conversationID);
    }
    
    public static long getMaxMsgCreateDateInLastSync(Context context) {
        long result = 0;
    	if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getMessagesMaxCreateTime()...");
        }

        String state = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_MAX_TEXT_MESSAGE_ID_IN_LAST_SYNC);
        
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getMessagesLastSync(): return " + state);
        }
        
        try {
        	result = (null != state ? Long.valueOf(state) : 0);
        } catch (NumberFormatException e) {
        	if(LogSettings.MARKET) {
        		MktLog.e(TAG, "getMessagesLastSync ");
        	}
        	result = 0;
		}
        
        return result;
    }
    
    public static void setMaxMsgCreateDateInLastSync(Context context, long lastSync) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setMessagesMaxCreateTime(" + lastSync + ")...");

//        String value = String.valueOf((0 != lastSync) ? lastSync : System.currentTimeMillis());
        updateSingleValue(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_MAX_TEXT_MESSAGE_ID_IN_LAST_SYNC, String.valueOf(lastSync), null);
    }
    
    
    public static long getCallLogLastSync(Context context) {
        long result = 0;
    	if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getCallLogLastSync()...");
        }

        String state = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_CALL_LOG_LAST_UPDATED_TIMESTAMP);
        
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getCallLogLastSync(): return " + state);
        }
        
        try {
        	result = (null != state ? Long.valueOf(state) : 0);
        } catch (NumberFormatException e) {
        	if(LogSettings.MARKET) {
        		MktLog.e(TAG, "getCallLogLastSync ");
        	}
		}
        
        return result;
    }
    
    
    public static long getAdminCallLogLastSync(Context context) {
        long result = 0;
    	if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getAdminCallLogLastSync()...");
        }

        String state = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_ADMIN_CALL_LOG_LAST_UPDATED_TIMESTAMP);
        
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getAdminCallLogLastSync(): return " + state);
        }
        
        try {
        	result = (null != state ? Long.valueOf(state) : 0);
        } catch (NumberFormatException e) {
        	if(LogSettings.MARKET) {
        		MktLog.e(TAG, "getAdminCallLogLastSync ");
        	}
		}
        
        return result;
    }
    
    public static void setCallLogLastSync(Context context, long lastSync) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setCallLogLastSync(" + lastSync + ")...");

        String value = String.valueOf((0 != lastSync) ? lastSync : System.currentTimeMillis());
        updateSingleValue(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_CALL_LOG_LAST_UPDATED_TIMESTAMP, value, null);
    }
    
    public static void setAdminCallLogLastSync(Context context, long lastSync) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "setAdminCallLogLastSync(" + lastSync + ")...");

        String value = String.valueOf((0 != lastSync) ? lastSync : System.currentTimeMillis());
        updateSingleValue(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_ADMIN_CALL_LOG_LAST_UPDATED_TIMESTAMP, value, null);
    }
    
    /**
     * REST API : Account Service Info section
     */
    private static boolean isFeatureEnabled(Context context, long mailboxId, String feature) {
        String result_str = simpleQueryByMailboxId(context, mailboxId, RCMProvider.SERVICE_EXTENSION_INFO, feature);
        boolean result = TextUtils.isEmpty(result_str) ? false : (Integer.valueOf(result_str) != 0);
        return result;
    }

    private static void setFeatureEnabled(Context context, long mailboxId, String feature, boolean enabled) {
        String value = enabled ? "1" : "0";
        updateOrInsertSingleValueByMailboxId(context, mailboxId, RCMProvider.SERVICE_EXTENSION_INFO, feature, value);
    }
    
    /**
     * SMS 
     */
    public static boolean isSMSFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_SMS_ENABLED);
    }

    public static void setSMSFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_SMS_ENABLED, enabled);
    }
    
    /**
     * SMSReceiving 
     */
    public static boolean isSMSReceivingFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_SMSRECEIVING_ENABLED);
    }

    public static void setSMSReceivingFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_SMSRECEIVING_ENABLED, enabled);
    }
    
    /**
     * Pager 
     */
    public static boolean isPagerFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_PAGER_ENABLED);
    }
    
    public static void setPagerFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_PAGER_ENABLED, enabled);
    }
    
    /**
     * PagerReceiving 
     */
    public static boolean isPagerReceivingFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_PAGERRECEIVING_ENABLED);
    }
    
    public static void setPagerReceivingFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_PAGERRECEIVING_ENABLED, enabled);
    }
    
    /**
     * SalesForce 
     */
    public static boolean isSalesForceFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_SALES_FORCE_ENABLED);
    }
    
    public static void setSalesForceFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_SALES_FORCE_ENABLED, enabled);
    }
    
    /**
     * Intercom 
     */
    public static boolean isIntercomFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_INTERCOM_ENABLED);
    }
    
    public static void setIntercomFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_INTERCOM_ENABLED, enabled);
    }
    
    /**
     * Paging 
     */
    public static boolean isPagingFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_PAGING_ENABLED);
    }
    
    public static void setPagingFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_PAGING_ENABLED, enabled);
    }

    
    /**
     * Presence.
     */
    public static boolean isPresenceFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_PRESENCE_ENABLED);
    }
    
    public static void setPresenceFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_PRESENCE_ENABLED, enabled);
    }
    
    /**
     * RingOut.
     */
    public static boolean isRingOutFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_RINGOUT_ENABLED);
    }
    
    public static void setRingOutFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_RINGOUT_ENABLED, enabled);
    }
    
    /**
     * International Calling. 
     */
    public static boolean isInternationalCallingFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_INTERNATIONAL_CALLING_ENABLED);
    }
    
    public static void setInternationalCallingFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_INTERNATIONAL_CALLING_ENABLED, enabled);
    }
    
    /**
     * DND. 
     */
    public static boolean isDNDFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_DND_ENABLED);
    }
    
    public static void setDNDFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_DND_ENABLED, enabled);
    }
    
    /**
     * FAX. 
     */
    public static boolean isFaxFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_FAX_ENABLED);
    }
    
    public static void setFaxFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_FAX_ENABLED, enabled);
    }
    
    /**
     * FAXReceiving. 
     */
    public static boolean isFaxReceivingFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_FAXRECEIVING_ENABLED);
    }
    
    public static void setFaxReceivingFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_FAXRECEIVING_ENABLED, enabled);
    }
    
    /**
     * Voicemail.
     */
    public static boolean isVoicemailFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_VOICEMAIL_ENABLED);
    }
    
    public static void setVoicemailFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_VOICEMAIL_ENABLED, enabled);
    }
    
    /**
     * VOIP Calling.     
     */
    public static boolean isVoipCallingFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_VOIP_CALLING_ENABLED);
    }

    public static void setVoipCallingFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_VOIP_CALLING_ENABLED, enabled);
    }
    
    /**
     * Conferencing.     
     */
    public static boolean isConferencingFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_CONFERENCING_ENABLED);
    }

    public static void setConferencingFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_CONFERENCING_ENABLED, enabled);
    }
    
    /**
     * VideoConferencing.     
     */
    public static boolean isVideoFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_VIDEO_ENABLED);
    }
    
    public static void setVideoFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_VIDEO_ENABLED, enabled);
    }
    
    /**
     * FreeSoftPhoneLines.     
     */
    public static boolean isFreeSoftPhoneLinesFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_FREESOFTPHONELINES_ENABLED);
    }
    
    public static void setFreeSoftPhoneLinesFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_FREESOFTPHONELINES_ENABLED, enabled);
    }
    
    /**
     * HipaaCompliance.     
     */
    public static boolean isHipaaComplianceFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_HIPAACOMPLIANCE_ENABLED);
    }
    
    public static void setHipaaComplianceFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_HIPAACOMPLIANCE_ENABLED, enabled);
    }
    
    /**
     * Call control
     */
    public static boolean isCallParkFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_CALL_PARK_ENABLED);
    }

    public static void setCallParkFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_CALL_PARK_ENABLED, enabled);
    }
    
    public static boolean isCallRecordingFeatureEnabled(Context context, long mailboxId) {
        return isFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_CALL_RECORDING_ENABLED);
    }
    
    public static void setCallRecordingFeatureEnabled(Context context, long mailboxId, boolean enabled) {
        setFeatureEnabled(context, mailboxId, ServiceExtensionInfoTable.REST_CALL_RECORDING_ENABLED, enabled);
    }
    
    /**
     * Stores the LogCat flag.
     * 
     * @param context
     *            the execution context
     * @param enabled
     *            defines LogCat flag
     */
    public static void setLogCatEnabled(Context context, boolean enabled) {
        String value = enabled ? "1" : "0";
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_LOG_CAT_ENABLED, value,
                null);
    }

    /**
     * Returns stored LogCat flag.
     * 
     * @param context
     *            the execution context
     * @return LogCat flag
     */
    public static boolean isLogCatEnabled(Context context) {
        String result_str = simpleQueryWithMailboxId(context, RCMProvider.DEVICE_ATTRIB,
                DeviceAttribTable.RCM_LOG_CAT_ENABLED);
        boolean result = TextUtils.isEmpty(result_str) ? true : (Integer.valueOf(result_str) != 0);
        return result;
    }
    
    /**
     * Stores the DSCP flag.
     * 
     * @param context
     *            the execution context
     * @param enabled
     *            defines DSCP flag
     */
    public static void setDSCPEnabled(Context context, boolean enabled) {
        String value = enabled ? "1" : "0";
        updateSingleValue(context, RCMProvider.DEVICE_ATTRIB, DeviceAttribTable.RCM_DSCP_ENABLED, value,
                null);
    }

    /**
     * Returns stored DSCP flag.
     * 
     * @param context
     *            the execution context
     * @return DSCP flag
     */
    public static boolean isDSCPEnabled(Context context) {
        String result_str = simpleQueryWithMailboxId(context, RCMProvider.DEVICE_ATTRIB,
                DeviceAttribTable.RCM_DSCP_ENABLED);
        boolean result = TextUtils.isEmpty(result_str) ? false : (Integer.valueOf(result_str) != 0);
        return result;
    }
    
    /**
     * Stores REST authorization refresh token.
     * 
     * @param context
     *            the execution context
     * @param mailboxId
     *            the mailbox identifier of the account
     * @return last refresh token, otherwise <code>null</code>
     */
    public static String getRestRefreshToken(Context context, long mailboxId) {
        String value = simpleQueryByMailboxId(context, mailboxId, RCMProvider.SERVICE_EXTENSION_INFO,
                ServiceExtensionInfoTable.REST_REFRESH_TOKEN);

        if (TextUtils.isEmpty(value)) {
            return null;
        }

        return value;
    }
    
    /**
     * Stores REST authorization refresh token.
     * 
     * @param context
     *            the execution context
     * @param mailboxId
     *            the mailbox identifier of the account
     * @param refreshToken
     *            defines refresh token (<code>null</code> erases existing token)
     */

    
    /**
     * Get extension groups type
     * @param 
     */
    public static int getExtensionGroupsType(Context context) {
        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getExtensionGroupsType()...");
        }

        int value = simpleQueryWithMailboxIdInt(context, RCMProvider.EXT_GROUPS_TYPE, AccountInfoTable.EXTENSION_GROUPS_TYPE, AccountInfoTable.TYPE_EXTENSION_ALL_GROUPS);

        if (RCMProvider.DEBUG_ENBL){
            EngLog.d(TAG, "getExtensionGroupsType(): return " + value);
        }
        return value;
    }
    
    /**
     * save extension groups type
     * @param 
     */
    public static void saveExtensionGroupsType(Context context, int type) {
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "saveExtensionGroupsType(): " + type);
        }
        updateOrInsertSingleValueWithMailboxId(context, RCMProvider.EXT_GROUPS_TYPE, AccountInfoTable.EXTENSION_GROUPS_TYPE, String.valueOf(type));
    }
    

    /**
	 * Tip dialog in create new message screen(shown flag)
	 * 
	 * @param
	 */
	public static boolean isCreateNewTipDialogShowed(Context context) {
        if (RCMProvider.DEBUG_ENBL) { 
            EngLog.d(TAG, "isShowCreateNewTipDialog()...");
        }

        String result_str = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_CREATE_MESSAGE_TIP_DIALOG);

        if (RCMProvider.DEBUG_ENBL) { 
            EngLog.d(TAG, "isShowCreateNewTipDialog(): result_str == " + result_str);
        }

        boolean result = TextUtils.isEmpty(result_str) ? false : result_str.equals("1"); //false - default value in case of DB error

        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isShowCreateNewTipDialog(): return " + result);
        return result;
	}
    
	public static void setCreateNewTipDialogShown(Context context, boolean shown) {
		if (RCMProvider.DEBUG_ENBL)
			EngLog.d(TAG, "setCreateNewTipDialogShown(" + shown + ")...");

		String value = shown ? "1" : "0";
		updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, 
				AccountInfoTable.RCM_CREATE_MESSAGE_TIP_DIALOG, value);
	}
	
    
    
    
    public static void saveDefaultTextDID(Context context, String textDID) {
    	 if (LogSettings.MARKET) {
             MktLog.i(TAG, "saveDefaultTextDID(): " + (textDID == null ? "null":textDID));
         }
         updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.DEFAULT_TEXT_DID, textDID);
    }
    
    public static String getDefaultFaxDID(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getDefaultFaxDID()...");

        String faxDID = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.DEFAULT_FAX_DID);

        if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getDefaultFaxDID(): return " + faxDID);
        return faxDID;
    }
    
    public static void saveDefaultFaxDID(Context context, String faxDID) {
    	 if (LogSettings.MARKET) {
             MktLog.i(TAG, "saveDefaultFaxDID(): " + (faxDID == null ? "null":faxDID));
         }
         updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.DEFAULT_FAX_DID, faxDID);
    }
    
    
    public static int getDefaultCallLogType(Context context) {
    	if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "getDefaultCallLogType()...");

        String defaultCallLogType = simpleQueryWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.DEFAULT_CALL_LOG_TYPE);

        if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "getDefaultFaxDID(): return " + defaultCallLogType);
        return TextUtils.isEmpty(defaultCallLogType) ? -1 : Integer.valueOf(defaultCallLogType);
    }
    
    public static void saveDefaultCallLogType(Context context, int defaultCallLogType) {
    	 if (LogSettings.MARKET) {
             MktLog.i(TAG, "saveDefaultCallLogType(): " + defaultCallLogType);
         }
         updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.DEFAULT_CALL_LOG_TYPE, String.valueOf(defaultCallLogType));
    }
    
    public static boolean isFirstTapRecording(Context context) {
        if (RCMProvider.DEBUG_ENBL)
            EngLog.d(TAG, "isFirstTapRecording()...");

        int isFirst = simpleQueryWithMailboxIdInt(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_FIRST_RECORDING, AccountInfoTable.IS_FIRST_RECORD);

        if (LogSettings.ENGINEERING)
            EngLog.d(TAG, "isFirstTapRecording(): return " + (isFirst == AccountInfoTable.IS_FIRST_RECORD));
        return isFirst == AccountInfoTable.IS_FIRST_RECORD;
    }
    
    public static void setNotFirstTapRecording(Context context) {
    	 if (LogSettings.MARKET) {
             MktLog.i(TAG, "setFirstTapRecording()");
         }
         updateOrInsertSingleValueWithMailboxId(context, RCMProvider.ACCOUNT_INFO, AccountInfoTable.RCM_FIRST_RECORDING, String.valueOf(AccountInfoTable.IS_NOT_FIRST_RECORD));
    }
    
    public static boolean isNewFccFeature(Context context) {
    	int value = simpleQueryWithMailboxIdInt(context, RCMProvider.CONFERENCE_INFO, ConferenceInfoTable.SHOW_CHOOSE_DIAL_IN_ICON, 0);
    	return value == 1?true:false;
    	
    }
    
    
    
    public static void updateTelusServiceExtensionInfo(Context context) {
    	ContentValues values = new ContentValues();
    	
    	values.put(ServiceExtensionInfoTable.REST_SMS_ENABLED, 			ServiceExtensionInfoTable.DISABLE);
    	values.put(ServiceExtensionInfoTable.REST_PAGER_ENABLED, 		ServiceExtensionInfoTable.DISABLE);
    	
    	if (BUILD.FCC_ZOOM_HARD_CODE == true) {
    		
    		if (LogSettings.MARKET) {
    			MktLog.i(TAG, "TELUS VoIP Brand Hard-code the conferencing & video permissions");
    		}
    		
    		values.put(ServiceExtensionInfoTable.REST_CONFERENCING_ENABLED, 			ServiceExtensionInfoTable.DISABLE);
    		values.put(ServiceExtensionInfoTable.REST_VIDEO_ENABLED, 					ServiceExtensionInfoTable.DISABLE);
    	}
    	
    	context.getContentResolver().update(
    			UriHelper.getUri(RCMProvider.SERVICE_EXTENSION_INFO, 	RCMProviderHelper.getCurrentMailboxId(context)), 
    			values, null, null);
    	
    }
    
	public static void deleteCallLogsByTypeFromDb(final Context context, final long mailbox_id, final int call_log_type) {

		if (LogSettings.MARKET) {
			MktLog.d(TAG, "Stareted call log list deletion from database");
		}

		context.getContentResolver().delete(UriHelper.getUri(RCMProvider.CALL_LOG, mailbox_id), CallLogTable.RCM_LOG_TYPE + "=?", new String[]{String.valueOf(call_log_type)});
	}

    public static int getPersonalFavoritesMaxPosition(Context context) {
        return getMaxPosition(context, RCMProvider.CLOUD_FAVORITES, new String[]{"MAX(" + FavoritesTable.RCM_SORT + ")"}, null);
    }


}
