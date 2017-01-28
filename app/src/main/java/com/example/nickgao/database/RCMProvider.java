package com.example.nickgao.database;


import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.example.nickgao.BuildConfig;
import com.example.nickgao.database.RCMDataStore.RCMColumns;
import com.example.nickgao.logging.EngLog;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;

import java.util.ArrayList;
public class RCMProvider extends ContentProvider {

    static final boolean DEBUG_ENBL = false; 	// LogSettings.ENGINEERING && true;

    static final String TAG = "[RC]RCMProvider";

    /* URI authority string */
    public static final String AUTHORITY =  buildAuthority();

    /* URI paths names */
    public static final String MAILBOX_CURRENT = "mailbox_current";
    public static final String DEVICE_ATTRIB = "device_attrib";
    public static final String USER_CREDENTIALS = "user_credentials";
    public static final String SERVICE_INFO = "service_info";
    public static final String ACCOUNT_INFO = "account_info";
    public static final String SERVICE_EXTENSION_INFO = "service_extension_info";
    public static final String CALLER_IDS = "caller_ids";
    public static final String FW_NUMBERS = "fw_numbers";
    public static final String PHONE_NUMBERS = "phone_numbers";
    public static final String PHONE_NUMBERS_WITHOUT_MAILBOXID = "phone_numbers_without_mailbox_id";
    public static final String BLOCKED_NUMBERS = "blocked_numbers";
    public static final String EXTENSIONS = "extensions";
    public static final String FAVORITES = "favorites";
    public static final String CALL_LOG = "call_log";
    public static final String CALL_LOG_TOKEN = "call_log_token";
    public static final String MESSAGES = "messages";
    public static final String LOAD_MESSAGES_BODY = "load_messages_body";
    public static final String MESSAGE_RECIPIENTS = "message_recipients";
    public static final String MESSAGE_LIST = "message_list";
    public static final String MESSAGE_CONVERSATIONS = "message_conversations";
    public static final String MESSAGE_DRAFT = "message_draft";
    public static final String SYNC_STATUS = "sync_status";
    public static final String APP_CONFIG = "app_config";
    public static final String CONFERENCE_INFO = "conference_info";
    public static final String MOBILE_WEB_ENHANCE_URL_PARAMETER = "mobile_web_enhance_url_parameter";
    public static final String PRESENCE = "presence";
    public static final String CONTACT_GROUP = "contact_group";

    public static final String TIER_SETTINGS = "tier_settings";        //account_info/tier_settings
    public static final String MAILBOX_STATE = "mailbox_state";        //account_info/mailbox_state
    public static final String EXT_MOD_COUNTER = "ext_mod_counter";    //account_info/ext_mod_counter
    public static final String MSG_MOD_COUNTER = "msg_mod_counter";    //account_info/msg_mod_counter
    public static final String EXT_GROUPS_TYPE = "ext_groups_type";    //account_info/extension_groups_type
    
    public static final String CLOUD_STORAGE_DROPBOX = "cloud_storage_dropbox";
    public static final String CLOUD_STORAGE_BOX = "cloud_storage_box";
    public static final String CLOUD_STORAGE_GOOGLE_DRIVE = "cloud_storage_google_drive";
    
    //Personal Group
    public static final String CONTACT_PERSONAL_GROUP = "contact_personal_group";
    
    public static final String DID_FAVORITE = "did_favorite_table";
    public static final String FORWARDING_NUMBER = "forwarding_number";
    
    public static final String SPECIAL_NUMBER = "special_number";
    
    // Fax Out
    public static final String OUTBOX 				= "outbox";
    public static final String ATTACHMENT 			= "attachment";
    public static final String OUTBOX_TO_PHONE 		= "outbox_to_phone";
    public static final String OUTBOX_ATTACHMENT 	= "outbox_attachment";

    /**
     * sync_status URI
     */
    public static final String LOGIN_STATUS = "login_status";
    public static final String LOGIN_LASTSYNC = "login_lastsync";
    public static final String LOGIN_ERROR = "login_error";

    public static final String ACCOUNT_INFO_STATUS = "account_info_status";
    public static final String ACCOUNT_INFO_LASTSYNC = "account_info_lastsync";
    public static final String ACCOUNT_INFO_ERROR = "account_info_error";

    public static final String ALL_CALL_LOGS_STATUS = "all_call_logs_status";
    public static final String ALL_CALL_LOGS_LASTSYNC = "all_call_logs_lastsync";
    public static final String ALL_CALL_LOGS_ERROR = "all_call_logs_error";

    public static final String MISSED_CALL_LOGS_STATUS = "missed_call_logs_status";
    public static final String MISSED_CALL_LOGS_LASTSYNC = "missed_call_logs_lastsync";
    public static final String MISSED_CALL_LOGS_ERROR = "missed_call_logs_error";

    
    private RCMDbHelper dbHelper;

    private static String buildAuthority() {
        return BuildConfig.APPLICATION_ID + ".provider.RCMProvider";
    }


    @Override
    public boolean onCreate() {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "RCMProvider.onCreate()");
        }

        dbHelper = RCMDbHelper.getInstance(getContext());
        return true;
    }
    
    @Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
    	synchronized (dbHelper) {
    		SQLiteDatabase db = dbHelper.getWritableDatabase();
    		db.beginTransaction();
    		try {
    			ContentProviderResult[] results = super.applyBatch(operations);
    			db.setTransactionSuccessful();
    			return results;
    		} finally {
    			db.endTransaction();
    		}
    	}
	}

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "query(" + uri + ",...)");
        }
        int match = sUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "query(): Wrong URI");
            }
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        int where_append_count = 0;

        if (mailboxIdRequired(match)) {
            String mailbox_id_string = uri.getQueryParameter(RCMColumns.MAILBOX_ID);
            if (mailbox_id_string == null) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "query(): Wrong URI: no mailboxID: " + uri);
                }
                throw new IllegalArgumentException("Wrong URI: no mailboxID: " + uri);
            }

            long mailbox_id;
            try {
                mailbox_id = Long.valueOf(mailbox_id_string);
            } catch (NumberFormatException e) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "query(): Wrong mailboxID: " + mailbox_id_string, e);
                }
                
                throw new IllegalArgumentException("Wrong mailboxID: " + mailbox_id_string);
            }

            long current_mailbox_id = RCMProviderHelper.getCurrentMailboxId(getContext());
            if (mailbox_id != current_mailbox_id) {
                if (LogSettings.MARKET) {
                    MktLog.i(TAG, "query(): mailboxID mis-match: " + mailbox_id + ". Current mailboxID: " + current_mailbox_id + "; URI: " + uri);
                }
                return null;
            }
            
            qb.appendWhere((where_append_count++ == 0 ? "" : " AND ") + (RCMColumns.MAILBOX_ID + '=' + mailbox_id));
        }

        qb.setTables(tableName(match));
        if (uriWithID(match)) {
            qb.appendWhere((where_append_count++ == 0 ? "" : " AND ") + (BaseColumns._ID + "=" + uri.getLastPathSegment()));
        }

        if (projection == null) {
            projection = defaultProjection(match);
        }

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = RCMColumns.DEFAULT_SORT_ORDER;
        }

        SQLiteDatabase db;
        try {
            db = dbHelper.getReadableDatabase();
        } catch (SQLiteException e) {
            //TODO Implement proper error handling
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "query(): Error opening readable database", e);
            }
            
            throw e;
        }

        Cursor cursor;
        synchronized (dbHelper) {
            try {
                cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            } catch (Throwable e) {
                if (LogSettings.MARKET) {
                    EngLog.e(TAG, "query(): Exception at db query", e);
                }
                
                throw new RuntimeException("Exception at db query: " + e.getMessage());
            }
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        if (cursor == null) {
            if (RCMProvider.DEBUG_ENBL) {
                EngLog.d(TAG, "query(): null cursor returned from db query");
            }
        }

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "query(): Cursor has " + cursor.getCount() + " rows");
        }
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "insert(" + uri + ", ...)");
        }

        int match = sUriMatcher.match(uri);

        if (match == UriMatcher.NO_MATCH) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "insert(): Wrong URI: " + uri);
            }
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

//        if (uriWithID(match) || uriDerived(match)) {
        if (uriWithID(match)) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "insert(): Insert not allowed for this URI: " + uri);
            }
            throw new IllegalArgumentException("Insert not allowed for this URI: " + uri);
        }


        //MailboxID parameter does not make sense for insert operations and shall not be used
        //So it throws exception on ENGINEERING and QA builds
        //and is ignored on MARKET builds
        if (LogSettings.QA) {
            if (uri.getQueryParameter(RCMColumns.MAILBOX_ID) != null) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "insert(): Insert not allowed for this URI: " + uri);
                }
                throw new IllegalArgumentException("Insert not allowed for this URI: " + uri);
            }
        }
        
        SQLiteDatabase db;
        long rowId;

        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            // TODO Implement proper error handling
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "insert(): Error opening writeable database", e);
            }
            
            throw e;
        }

        synchronized (dbHelper) {
            try {
                rowId = db.insert(tableName(match), null, values);
            } catch (SQLException e) {
                // TODO Implement proper error handling
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "Insert() failed", e);
                }
                
                throw e;
            }
        }

        if (rowId <= 0) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "insert(): Error: insert() returned " + rowId);
            }
            throw new RuntimeException("DB insert failed");
        }

        uri = ContentUris.withAppendedId(UriHelper.removeQuery(uri), rowId);
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "insert(): new uri with rowId: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "insert(): return " + uri);
        }
        return uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "bulkInsert(" + uri + ", ...)");
        }
        int match = sUriMatcher.match(uri);

        if (match == UriMatcher.NO_MATCH) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "bulkInsert(): Wrong URI: " + uri);
            }
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

//        if (uriWithID(match) || uriDerived(match)) {
        if (uriWithID(match)) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "bulkInsert(): Insert not allowed for this URI: " + uri);
            }
            throw new IllegalArgumentException("Insert not allowed for this URI: " + uri);
        }

        //MailboxID parameter does not make sense for insert operations and shall not be used
        //So it throws exception on ENGINEERING and QA builds
        //and is ignored on MARKET builds
        if (LogSettings.MARKET) {
            if (uri.getQueryParameter(RCMColumns.MAILBOX_ID) != null) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "bulkInsert(): Insert not allowed for this URI: " + uri);
                }
                throw new IllegalArgumentException("Insert not allowed for this URI: " + uri);
            }
        }

        
        SQLiteDatabase db;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            // TODO Implement proper error handling
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "bulkInsert(): Error opening writable database", e);
            }
            
            throw e;
        }

        int added = 0;
        long rowId = 0;
        String table = tableName(match);

        synchronized (dbHelper) {
            try {
                db.beginTransaction();
                for (int i = 0; i < values.length; i++) {
                    try {
                        rowId = db.insert(table, null, values[i]);
                    } catch (SQLException e) {
                        if (LogSettings.MARKET) {
                            MktLog.e(TAG, "bulkInsert() P1", e);
                        }
                        continue;
                    }

                    if (rowId <= 0) {
                        if (LogSettings.MARKET) {
                            MktLog.e(TAG, "bulkInsert() P2: " + rowId);
                        }
                        continue;
                    }
                    added = added + 1;
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "bulkInsert() P3", e);
                }
                
                throw new RuntimeException("bulkInsert(): DB insert failed: " + e.getMessage());
            } finally {
                db.endTransaction();
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return added;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "update(" + uri + ", ...)");
        }
        int match = sUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "update(): Wrong URI: " + uri);
            }
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (uriWithID(match)) {
            selection = BaseColumns._ID + "=" + uri.getLastPathSegment() + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        }

        String mailbox_id_string = uri.getQueryParameter(RCMColumns.MAILBOX_ID);
        if (!TextUtils.isEmpty(mailbox_id_string)) {
            selection = RCMColumns.MAILBOX_ID + "=" + mailbox_id_string + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        }
        

        SQLiteDatabase db;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            //TODO Implement proper error handling
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "update(): Error opening writable database", e);
            }
            
            throw e;
        }

        int count;
        synchronized (dbHelper) {
            try {
                count = db.update(tableName(match), values, selection, selectionArgs);
            } catch (SQLException e) {
                //TODO Implement proper error handling
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "update() failed", e);
                }
                
                throw e;
            }
        }

        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "update(): " + count + " rows updated");
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (LogSettings.MARKET) {
            EngLog.d(TAG, "delete(" + uri + ", ...)");
        }
        int match = sUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "delete(): Wrong URI: " + uri);
            }
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (uriDerived(match)) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "delete(): Row delete not allowed for this URI: " + uri);
            }
            throw new IllegalArgumentException("Row delete not allowed for this URI: " + uri);
        }

        if (uriWithID(match)) {
            selection = BaseColumns._ID + "=" + uri.getLastPathSegment() + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        }

        String mailbox_id_string = uri.getQueryParameter(RCMColumns.MAILBOX_ID);
        if (!TextUtils.isEmpty(mailbox_id_string)) {
            selection = RCMColumns.MAILBOX_ID + "=" + mailbox_id_string + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        }
        
        SQLiteDatabase db;
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            //TODO Implement proper error handling
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "delete(): Error opening writable database", e);
            }
            
            throw e;
        }

        int count;
        synchronized (dbHelper) {
            try {
                count = db.delete(tableName(match), selection, selectionArgs);
            } catch (SQLException e) {
                //TODO Implement proper error handling
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "delete(): DB rows delete error", e);
                }
                
                throw e;
            }
        }

        if (LogSettings.ENGINEERING) {
        	EngLog.d(TAG, "delete(), " + uri.toString() + "; " + count + " rows deleted");
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }


    @Override
    public String getType(Uri uri) {
        if (RCMProvider.DEBUG_ENBL) {
            EngLog.d(TAG, "getType(" + uri + ')');
        }

        return null;
    }


    private boolean uriWithID(int uri_match) {
        switch (uri_match) {
            case CALLER_IDS_ID_MATCH:
            case FW_NUMBERS_ID_MATCH:
            case PHONE_NUMBERS_ID_MATCH:
            case EXTENSIONS_ID_MATCH:
            case FAVORITES_ID_MATCH:
            case CALL_LOG_ID_MATCH:
            case CALL_LOG_TOKEN_ID_MATCH:
            case MESSAGES_ID_MATCH:
            case MESSAGE_RECIPIENTS_ID_MATCH:
            case MESSAGE_LIST_ID_MATCH:
            case MESSAGE_CONVERSATIONS_ID_MATCH:
            case CONFERENCE_INFO_ID_MATCH:
            case MOBILE_WEB_ENHANCE_URL_PARAMETER_ID_MATCH:
            case OUTBOX_TO_PHONE_ID_MATCH:
            case DID_FAVORITE_ID_MATCH:
            case CONTACT_GROUP_ID_MATCH:
                return true;

            default:
                return false;
        }
    }

    private boolean uriDerived(int uri_match) {
        switch (uri_match) {

            case TIER_SETTINGS_MATCH:
            case MAILBOX_STATE_MATCH:
            case EXT_MOD_COUNTER_MATCH:
            case MSG_MOD_COUNTER_MATCH:
            case EXT_GROUPS_TYPE_MATCH:
            case LOGIN_STATUS_MATCH:
            case LOGIN_LASTSYNC_MATCH:
            case LOGIN_ERROR_MATCH:
            case ACCOUNT_INFO_STATUS_MATCH:
            case ACCOUNT_INFO_LASTSYNC_MATCH:
            case ACCOUNT_INFO_ERROR_MATCH:
            case ALL_CALL_LOGS_STATUS_MATCH:
            case ALL_CALL_LOGS_LASTSYNC_MATCH:
            case ALL_CALL_LOGS_ERROR_MATCH:
            case MISSED_CALL_LOGS_STATUS_MATCH:
            case MISSED_CALL_LOGS_LASTSYNC_MATCH:
            case MISSED_CALL_LOGS_ERROR_MATCH:
                return true;

            default:
                return false;
        }
    }

    private boolean mailboxIdRequired(int uri_match) {
        if (uriWithID(uri_match)) {
            return false;
        }

        switch (uri_match) {
            case MAILBOX_CURRENT_MATCH:
            case DEVICE_ATTRIB_MATCH:
            case USER_CREDENTIALS_MATCH:
            case SYNC_STATUS_MATCH:
            case APP_CONFIG_MATCH:
            case CLOUD_STORAGE_DROPBOX_MATCH:
            case CLOUD_STORAGE_GOOGLE_DRIVE_MATCH:
            case CLOUD_STORAGE_BOX_MATCH:
            case OUTBOX_TO_PHONE_MATCH:
            case OUTBOX_TO_PHONE_ID_MATCH:
            case OUTBOX_ATTACHMENT_MATCH:
            case OUTBOX_ATTACHMENT_ID_MATCH:
            case PHONE_NUMBERS_WITHOUT_MAILBOXID_MATCH:
            case CONTACT_PERSONAL_GROUP_MATCH:
//            case FORWARDING_NUMBER_MATCH:
                return false;
            default:
                return true;
        }
    }

    private String tableName(int uri_match) {
        switch (uri_match) {

            case MAILBOX_CURRENT_MATCH:
                return RCMDataStore.MailboxCurrentTable.getInstance().getName();

            case DEVICE_ATTRIB_MATCH:
                return RCMDataStore.DeviceAttribTable.getInstance().getName();

            case USER_CREDENTIALS_MATCH:
                return RCMDataStore.UserCredentialsTable.getInstance().getName();

            case SERVICE_INFO_MATCH:
                return RCMDataStore.ServiceInfoTable.getInstance().getName();

            case ACCOUNT_INFO_MATCH:
            case TIER_SETTINGS_MATCH:
            case MAILBOX_STATE_MATCH:
            case EXT_MOD_COUNTER_MATCH:
            case MSG_MOD_COUNTER_MATCH:
            case EXT_GROUPS_TYPE_MATCH:
                return RCMDataStore.AccountInfoTable.getInstance().getName();

            case SERVICE_EXTENSION_INFO_MATCH:
                return RCMDataStore.ServiceExtensionInfoTable.getInstance().getName();

            case CALLER_IDS_MATCH:
            case CALLER_IDS_ID_MATCH:
                return RCMDataStore.CallerIDsTable.getInstance().getName();

            case FW_NUMBERS_MATCH:
            case FW_NUMBERS_ID_MATCH:
                return RCMDataStore.FwNumbersTable.getInstance().getName();

            case PHONE_NUMBERS_MATCH:
            case PHONE_NUMBERS_WITHOUT_MAILBOXID_MATCH:
            case PHONE_NUMBERS_ID_MATCH:
                return RCMDataStore.PhoneNumbersTable.getInstance().getName();

            case BLOCKED_NUMBERS_MATCH:
            case BLOCKED_NUMBERS_ID_MATCH:
                return RCMDataStore.BlockedNumbersTable.getInstance().getName();
                
            case EXTENSIONS_MATCH:
            case EXTENSIONS_ID_MATCH:
                return RCMDataStore.ExtensionsTable.getInstance().getName();

            case FAVORITES_MATCH:
            case FAVORITES_ID_MATCH:
                return RCMDataStore.FavoritesTable.getInstance().getName();

            case CALL_LOG_MATCH:
            case CALL_LOG_ID_MATCH:
                return RCMDataStore.CallLogTable.getInstance().getName();
                
            case CALL_LOG_TOKEN_MATCH:
            case CALL_LOG_TOKEN_ID_MATCH:
            	return RCMDataStore.CallLogTokenTable.getInstance().getName();

            case MESSAGES_MATCH:
            case MESSAGES_ID_MATCH:
                return RCMDataStore.MessagesTable.getInstance().getName();
                
            case MESSAGE_RECIPIENTS_MATCH:
            case MESSAGE_RECIPIENTS_ID_MATCH:
                return RCMDataStore.MessageRecipientsTable.getInstance().getName();
                
            case MESSAGE_LIST_MATCH:
            case MESSAGE_LIST_ID_MATCH:
                return RCMDataStore.MessageListTable.getInstance().getName();

            case MESSAGE_CONVERSATIONS_MATCH:
            case MESSAGE_CONVERSATIONS_ID_MATCH:
                return RCMDataStore.MessageConversationsTable.getInstance().getName();

            case MESSAGE_DRAFT_MATCH:
                return RCMDataStore.MessageDraftTable.getInstance().getName();

            case SYNC_STATUS_MATCH:
            case LOGIN_STATUS_MATCH:
            case LOGIN_LASTSYNC_MATCH:
            case LOGIN_ERROR_MATCH:
            case ACCOUNT_INFO_STATUS_MATCH:
            case ACCOUNT_INFO_LASTSYNC_MATCH:
            case ACCOUNT_INFO_ERROR_MATCH:
            case ALL_CALL_LOGS_STATUS_MATCH:
            case ALL_CALL_LOGS_LASTSYNC_MATCH:
            case ALL_CALL_LOGS_ERROR_MATCH:
            case MISSED_CALL_LOGS_STATUS_MATCH:
            case MISSED_CALL_LOGS_LASTSYNC_MATCH:
            case MISSED_CALL_LOGS_ERROR_MATCH:
                return RCMDataStore.SyncStatusTable.getInstance().getName();
            case APP_CONFIG_MATCH:
            	return RCMDataStore.AppConfigTable.getInstance().getName();

            case CONFERENCE_INFO_ID_MATCH:
            case CONFERENCE_INFO_MATCH:
            	return RCMDataStore.ConferenceInfoTable.getInstance().getName();
            	
            case MOBILE_WEB_ENHANCE_URL_PARAMETER_MATCH:
            case MOBILE_WEB_ENHANCE_URL_PARAMETER_ID_MATCH:
            	return RCMDataStore.MobileWebEnhanceURLParamTable.getInstance().getName();
            	
            	
            case CLOUD_STORAGE_DROPBOX_MATCH:
                return RCMDataStore.CloudStorageDropBoxTable.getInstance().getName();
            case CLOUD_STORAGE_BOX_MATCH:
                return RCMDataStore.CloudStorageBoxTable.getInstance().getName();
            case DID_FAVORITE_MATCH:
            case DID_FAVORITE_ID_MATCH:
            	return RCMDataStore.DIDFavoriteTable.getInstance().getName();
            case CLOUD_STORAGE_GOOGLE_DRIVE_MATCH:
                return RCMDataStore.CloudStorageGoogleDriveTable.getInstance().getName();
             
            case FORWARDING_NUMBER_MATCH:
                return RCMDataStore.ForwardingNumberTable.getInstance().getName();
            
            case SPECIAL_NUMBER_MATCH:
            	return RCMDataStore.SpecialNumberTable.getInstance().getName();
               
            case OUTBOX_MATCH:
            case OUTBOX_ID_MATCH:
            	return RCMDataStore.RCMOutboxTable.getInstance().getName();
            	
            case ATTACHMENT_MATCH:
            case ATTACHMENT_ID_MATCH:
            	return RCMDataStore.RCMAttachmentTable.getInstance().getName();
            	
            case OUTBOX_TO_PHONE_MATCH:
            case OUTBOX_TO_PHONE_ID_MATCH:
            	return RCMDataStore.RCMOutboxToPhoneTable.getInstance().getName();
            	
            case OUTBOX_ATTACHMENT_MATCH:
            case OUTBOX_ATTACHMENT_ID_MATCH:
            	return RCMDataStore.RCMAttOutboxTable.getInstance().getName();

            case PRESENCE_MATCH:
            case PRESENCE_ID_MATCH:
                return RCMDataStore.PresenceTable.getInstance().getName();

            case CONTACT_GROUP_MATCH:
            case CONTACT_GROUP_ID_MATCH:
                return RCMDataStore.ContactGroupTable.getInstance().getName();
            default:
                throw new Error(TAG + " No table defined for #" + uri_match);
        }
    }


    private static final String[] TierSettingsProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.AccountInfoTable.JEDI_TIER_SETTINGS};
    private static final String[] ExtModCounterProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.AccountInfoTable.JEDI_EXT_MOD_COUNTER};
    private static final String[] MsgModCounterProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.AccountInfoTable.JEDI_MSG_MOD_COUNTER};
    private static final String[] MailboxStateProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.AccountInfoTable.RCM_MAILBOX_STATE};
    private static final String[] ExtGroupsTypeProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.AccountInfoTable.EXTENSION_GROUPS_TYPE};
    
    private static final String[] LoginStatusProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.LOGIN_STATUS};
    private static final String[] LoginLastSyncProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.LOGIN_LASTSYNC};
    private static final String[] LoginErrorProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.LOGIN_ERROR};
    private static final String[] AccountInfoStatusProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.ACCOUNT_INFO_STATUS};
    private static final String[] AccountInfoLastSyncProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.ACCOUNT_INFO_LASTSYNC};
    private static final String[] AccountInfoErrorProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.ACCOUNT_INFO_ERROR};
    private static final String[] AllCallLogsStatusProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.ALL_CALL_LOGS_STATUS};
    private static final String[] AllCallLogsLastSyncProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.ALL_CALL_LOGS_LASTSYNC};
    private static final String[] AllCallLogsErrorProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.ALL_CALL_LOGS_ERROR};
    private static final String[] MissedCallLogsStatusProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.MISSED_CALL_LOGS_STATUS};
    private static final String[] MissedCallLogsLastSyncProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.MISSED_CALL_LOGS_LASTSYNC};
    private static final String[] MissedCallLogsErrorProjection = {RCMDataStore.RCMColumns.MAILBOX_ID, RCMDataStore.SyncStatusTable.MISSED_CALL_LOGS_ERROR};

    private String[] defaultProjection(int uri_matcher) {
        switch (uri_matcher) {

            case TIER_SETTINGS_MATCH:
                return TierSettingsProjection;
            case MAILBOX_STATE_MATCH:
                return MailboxStateProjection;
            case EXT_MOD_COUNTER_MATCH:
                return ExtModCounterProjection;
            case MSG_MOD_COUNTER_MATCH:
                return MsgModCounterProjection;
            case EXT_GROUPS_TYPE_MATCH:
            	return ExtGroupsTypeProjection;
            case LOGIN_STATUS_MATCH:
                return LoginStatusProjection;
            case LOGIN_LASTSYNC_MATCH:
                return LoginLastSyncProjection;
            case LOGIN_ERROR_MATCH:
                return LoginErrorProjection;
            case ACCOUNT_INFO_STATUS_MATCH:
                return AccountInfoStatusProjection;
            case ACCOUNT_INFO_LASTSYNC_MATCH:
                return AccountInfoLastSyncProjection;
            case ACCOUNT_INFO_ERROR_MATCH:
                return AccountInfoErrorProjection;
            case ALL_CALL_LOGS_STATUS_MATCH:
                return AllCallLogsStatusProjection;
            case ALL_CALL_LOGS_LASTSYNC_MATCH:
                return AllCallLogsLastSyncProjection;
            case ALL_CALL_LOGS_ERROR_MATCH:
                return AllCallLogsErrorProjection;
            case MISSED_CALL_LOGS_STATUS_MATCH:
                return MissedCallLogsStatusProjection;
            case MISSED_CALL_LOGS_LASTSYNC_MATCH:
                return MissedCallLogsLastSyncProjection;
            case MISSED_CALL_LOGS_ERROR_MATCH:
                return MissedCallLogsErrorProjection;

            default:
                return null;
        }
    }


    /* UriMatcher codes */
    private static final int MAILBOX_CURRENT_MATCH = 10;
    private static final int DEVICE_ATTRIB_MATCH = 11;
    private static final int SERVICE_INFO_MATCH = 12;
    private static final int ACCOUNT_INFO_MATCH = 20;
    private static final int SERVICE_EXTENSION_INFO_MATCH = 25;
    private static final int CALLER_IDS_MATCH = 30;
    private static final int CALLER_IDS_ID_MATCH = 31;
    private static final int FW_NUMBERS_MATCH = 40;
    private static final int FW_NUMBERS_ID_MATCH = 41;
    private static final int PHONE_NUMBERS_WITHOUT_MAILBOXID_MATCH = 44;
    private static final int PHONE_NUMBERS_MATCH = 45;
    private static final int PHONE_NUMBERS_ID_MATCH = 46;
    private static final int BLOCKED_NUMBERS_MATCH = 47;
    private static final int BLOCKED_NUMBERS_ID_MATCH = 48;
    private static final int EXTENSIONS_MATCH = 50;
    private static final int EXTENSIONS_ID_MATCH = 51;
    private static final int FAVORITES_MATCH = 55;
    private static final int FAVORITES_ID_MATCH = 56;
    private static final int CALL_LOG_MATCH = 60;
    private static final int CALL_LOG_ID_MATCH = 61;
    private static final int CALL_LOG_TOKEN_MATCH = 62;
    private static final int CALL_LOG_TOKEN_ID_MATCH = 63;
    private static final int MESSAGES_MATCH = 70;
    private static final int MESSAGES_ID_MATCH = 71;
    private static final int MESSAGE_RECIPIENTS_MATCH = 72;
    private static final int MESSAGE_RECIPIENTS_ID_MATCH = 73;
    private static final int MESSAGE_LIST_MATCH = 74;
    private static final int MESSAGE_LIST_ID_MATCH = 75;
    private static final int MESSAGE_CONVERSATIONS_MATCH = 76;
    private static final int MESSAGE_CONVERSATIONS_ID_MATCH = 77;
    private static final int MESSAGE_DRAFT_MATCH = 78;
    private static final int SYNC_STATUS_MATCH = 80;
    private static final int TIER_SETTINGS_MATCH = 90;
    private static final int MAILBOX_STATE_MATCH = 100;
    private static final int EXT_MOD_COUNTER_MATCH = 110;
    private static final int MSG_MOD_COUNTER_MATCH = 120;
    
    public static final int LOGIN_STATUS_MATCH = 130;
    public static final int LOGIN_LASTSYNC_MATCH = 131;
    public static final int LOGIN_ERROR_MATCH = 132;
    public static final int ACCOUNT_INFO_STATUS_MATCH = 140;
    public static final int ACCOUNT_INFO_LASTSYNC_MATCH = 141;
    public static final int ACCOUNT_INFO_ERROR_MATCH = 142;
    public static final int ALL_CALL_LOGS_STATUS_MATCH = 150;
    public static final int ALL_CALL_LOGS_LASTSYNC_MATCH = 151;
    public static final int ALL_CALL_LOGS_ERROR_MATCH = 152;
    public static final int MISSED_CALL_LOGS_STATUS_MATCH = 160;
    public static final int MISSED_CALL_LOGS_LASTSYNC_MATCH = 161;
    public static final int MISSED_CALL_LOGS_ERROR_MATCH = 162;
    
    private static final int EXT_GROUPS_TYPE_MATCH = 170;
    
    public static final int PRESENCE_MATCH = 180;
    public static final int PRESENCE_ID_MATCH = 181;

    public static final int CONTACT_GROUP_MATCH = 190;
    public static final int CONTACT_GROUP_ID_MATCH = 191;
    
    private static final int USER_CREDENTIALS_MATCH = 500;
    private static final int APP_CONFIG_MATCH = 510;
    
    private static final int CONFERENCE_INFO_MATCH = 520;
    private static final int CONFERENCE_INFO_ID_MATCH = 521;
    
    private static final int MOBILE_WEB_ENHANCE_URL_PARAMETER_MATCH = 530;
    private static final int MOBILE_WEB_ENHANCE_URL_PARAMETER_ID_MATCH = 531;
    
    private static final int OUTBOX_MATCH 						= 600;
    private static final int OUTBOX_ID_MATCH 					= 601;
    private static final int OUTBOX_TO_PHONE_MATCH 				= 602;
    private static final int OUTBOX_TO_PHONE_ID_MATCH 			= 603;
    private static final int ATTACHMENT_MATCH 					= 610;
    private static final int ATTACHMENT_ID_MATCH 				= 611;
    private static final int OUTBOX_ATTACHMENT_MATCH 			= 620;
    private static final int OUTBOX_ATTACHMENT_ID_MATCH 		= 621;
    
    private static final int CLOUD_STORAGE_DROPBOX_MATCH      = 700;
    private static final int CLOUD_STORAGE_BOX_MATCH          = 701;
    private static final int CLOUD_STORAGE_GOOGLE_DRIVE_MATCH = 702;
    
    private static final int DID_FAVORITE_MATCH 	= 710;
    private static final int DID_FAVORITE_ID_MATCH 	= 711;
    
    private static final int CONTACT_PERSONAL_GROUP_MATCH = 720;
    private static final int FORWARDING_NUMBER_MATCH      = 730;
    
    private static final int SPECIAL_NUMBER_MATCH	= 740;
    
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, MAILBOX_CURRENT, MAILBOX_CURRENT_MATCH);
        sUriMatcher.addURI(AUTHORITY, DEVICE_ATTRIB, DEVICE_ATTRIB_MATCH);
        sUriMatcher.addURI(AUTHORITY, SERVICE_INFO, SERVICE_INFO_MATCH);
        sUriMatcher.addURI(AUTHORITY, USER_CREDENTIALS, USER_CREDENTIALS_MATCH);
        sUriMatcher.addURI(AUTHORITY, ACCOUNT_INFO, ACCOUNT_INFO_MATCH);
        sUriMatcher.addURI(AUTHORITY, SERVICE_EXTENSION_INFO, SERVICE_EXTENSION_INFO_MATCH);
        sUriMatcher.addURI(AUTHORITY, CALLER_IDS, CALLER_IDS_MATCH);
        sUriMatcher.addURI(AUTHORITY, CALLER_IDS + "/#", CALLER_IDS_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, FW_NUMBERS, FW_NUMBERS_MATCH);
        sUriMatcher.addURI(AUTHORITY, FW_NUMBERS + "/#", FW_NUMBERS_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, PHONE_NUMBERS, PHONE_NUMBERS_MATCH);
        sUriMatcher.addURI(AUTHORITY, PHONE_NUMBERS_WITHOUT_MAILBOXID, PHONE_NUMBERS_WITHOUT_MAILBOXID_MATCH);
        sUriMatcher.addURI(AUTHORITY, PHONE_NUMBERS + "/#", PHONE_NUMBERS_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, BLOCKED_NUMBERS, BLOCKED_NUMBERS_MATCH);
        sUriMatcher.addURI(AUTHORITY, BLOCKED_NUMBERS + "/#", BLOCKED_NUMBERS_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, EXTENSIONS, EXTENSIONS_MATCH);
        sUriMatcher.addURI(AUTHORITY, EXTENSIONS + "/#", EXTENSIONS_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, FAVORITES, FAVORITES_MATCH);
        sUriMatcher.addURI(AUTHORITY, FAVORITES + "/#", FAVORITES_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, CALL_LOG, CALL_LOG_MATCH);
        sUriMatcher.addURI(AUTHORITY, CALL_LOG + "/#", CALL_LOG_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, CALL_LOG_TOKEN, CALL_LOG_TOKEN_MATCH);
        sUriMatcher.addURI(AUTHORITY, CALL_LOG_TOKEN + "/#", CALL_LOG_TOKEN_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGES, MESSAGES_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGES + "/#", MESSAGES_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, LOAD_MESSAGES_BODY, MESSAGES_MATCH);
        sUriMatcher.addURI(AUTHORITY, LOAD_MESSAGES_BODY + "/#", MESSAGES_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGE_RECIPIENTS, MESSAGE_RECIPIENTS_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGE_RECIPIENTS + "/#", MESSAGE_RECIPIENTS_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGE_LIST, MESSAGE_LIST_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGE_LIST + "/#", MESSAGE_LIST_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGE_CONVERSATIONS, MESSAGE_CONVERSATIONS_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGE_CONVERSATIONS + "/#", MESSAGE_CONVERSATIONS_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, MESSAGE_DRAFT, MESSAGE_DRAFT_MATCH);
        sUriMatcher.addURI(AUTHORITY, SYNC_STATUS, SYNC_STATUS_MATCH);
        sUriMatcher.addURI(AUTHORITY, APP_CONFIG, APP_CONFIG_MATCH);
        sUriMatcher.addURI(AUTHORITY, TIER_SETTINGS, TIER_SETTINGS_MATCH);
        sUriMatcher.addURI(AUTHORITY, MAILBOX_STATE, MAILBOX_STATE_MATCH);
        sUriMatcher.addURI(AUTHORITY, EXT_MOD_COUNTER, EXT_MOD_COUNTER_MATCH);
        sUriMatcher.addURI(AUTHORITY, MSG_MOD_COUNTER, MSG_MOD_COUNTER_MATCH);
        sUriMatcher.addURI(AUTHORITY, EXT_GROUPS_TYPE, EXT_GROUPS_TYPE_MATCH);
        sUriMatcher.addURI(AUTHORITY, CONTACT_GROUP, CONTACT_GROUP_MATCH);
        sUriMatcher.addURI(AUTHORITY, CONTACT_GROUP + "/#", CONTACT_GROUP_ID_MATCH);

        sUriMatcher.addURI(AUTHORITY, LOGIN_STATUS, LOGIN_STATUS_MATCH);
        sUriMatcher.addURI(AUTHORITY, LOGIN_LASTSYNC, LOGIN_LASTSYNC_MATCH);
        sUriMatcher.addURI(AUTHORITY, LOGIN_ERROR, LOGIN_ERROR_MATCH);
        sUriMatcher.addURI(AUTHORITY, ACCOUNT_INFO_STATUS, ACCOUNT_INFO_STATUS_MATCH);
        sUriMatcher.addURI(AUTHORITY, ACCOUNT_INFO_LASTSYNC, ACCOUNT_INFO_LASTSYNC_MATCH);
        sUriMatcher.addURI(AUTHORITY, ACCOUNT_INFO_ERROR, ACCOUNT_INFO_ERROR_MATCH);
        sUriMatcher.addURI(AUTHORITY, ALL_CALL_LOGS_STATUS, ALL_CALL_LOGS_STATUS_MATCH);
        sUriMatcher.addURI(AUTHORITY, ALL_CALL_LOGS_LASTSYNC, ALL_CALL_LOGS_LASTSYNC_MATCH);
        sUriMatcher.addURI(AUTHORITY, ALL_CALL_LOGS_ERROR, ALL_CALL_LOGS_ERROR_MATCH);
        sUriMatcher.addURI(AUTHORITY, MISSED_CALL_LOGS_STATUS, MISSED_CALL_LOGS_STATUS_MATCH);
        sUriMatcher.addURI(AUTHORITY, MISSED_CALL_LOGS_LASTSYNC, MISSED_CALL_LOGS_LASTSYNC_MATCH);
        sUriMatcher.addURI(AUTHORITY, MISSED_CALL_LOGS_ERROR, MISSED_CALL_LOGS_ERROR_MATCH);
        sUriMatcher.addURI(AUTHORITY, CONFERENCE_INFO + "/#", CONFERENCE_INFO_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, CONFERENCE_INFO, CONFERENCE_INFO_MATCH);
        sUriMatcher.addURI(AUTHORITY, MOBILE_WEB_ENHANCE_URL_PARAMETER + "/#", MOBILE_WEB_ENHANCE_URL_PARAMETER_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, MOBILE_WEB_ENHANCE_URL_PARAMETER, MOBILE_WEB_ENHANCE_URL_PARAMETER_MATCH);
        sUriMatcher.addURI(AUTHORITY, CLOUD_STORAGE_DROPBOX, CLOUD_STORAGE_DROPBOX_MATCH);
        sUriMatcher.addURI(AUTHORITY, CLOUD_STORAGE_BOX, CLOUD_STORAGE_BOX_MATCH);
        sUriMatcher.addURI(AUTHORITY, DID_FAVORITE, DID_FAVORITE_MATCH);
        sUriMatcher.addURI(AUTHORITY, DID_FAVORITE + "/#", DID_FAVORITE_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, CLOUD_STORAGE_GOOGLE_DRIVE, CLOUD_STORAGE_GOOGLE_DRIVE_MATCH);
        sUriMatcher.addURI(AUTHORITY, CONTACT_PERSONAL_GROUP, CONTACT_PERSONAL_GROUP_MATCH);
        sUriMatcher.addURI(AUTHORITY, FORWARDING_NUMBER, FORWARDING_NUMBER_MATCH);
        sUriMatcher.addURI(AUTHORITY, SPECIAL_NUMBER, SPECIAL_NUMBER_MATCH);
        
        sUriMatcher.addURI(AUTHORITY, OUTBOX, 						OUTBOX_MATCH);
        sUriMatcher.addURI(AUTHORITY, OUTBOX + "/#", 				OUTBOX_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, ATTACHMENT, 					ATTACHMENT_MATCH);
        sUriMatcher.addURI(AUTHORITY, ATTACHMENT + "/#", 			ATTACHMENT_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, OUTBOX_TO_PHONE, 				OUTBOX_TO_PHONE_MATCH);
        sUriMatcher.addURI(AUTHORITY, OUTBOX_TO_PHONE + "/#", 		OUTBOX_TO_PHONE_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, OUTBOX_ATTACHMENT, 			OUTBOX_ATTACHMENT_MATCH);
        sUriMatcher.addURI(AUTHORITY, OUTBOX_ATTACHMENT + "/#", 	OUTBOX_ATTACHMENT_ID_MATCH);
        sUriMatcher.addURI(AUTHORITY, PRESENCE, 		PRESENCE_MATCH);
        sUriMatcher.addURI(AUTHORITY, PRESENCE + "/#", 	PRESENCE_ID_MATCH);
        
    }

}

