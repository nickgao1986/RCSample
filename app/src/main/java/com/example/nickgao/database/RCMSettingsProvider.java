package com.example.nickgao.database;


import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.nickgao.logging.EngLog;
import com.example.nickgao.logging.MktLog;

/**
 * Created by Antonenko Viacheslav on 02/10/14.
 */
public class RCMSettingsProvider extends ContentProvider {

    private static final String TAG = "[RC]SettingsProvider";
    private static final boolean DEBUG = false;

    /**
     * The authority of the data provider
     */
    public static final String AUTHORITY = buildAuthority();
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String GENERAL_SETTINGS = "general_settings";
    public static final String USER_SETTINGS = "user_settings";
    public static final String IM_MEMORY_SETTINGS = "in_memory_settings";

    private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();
    private RCMSettingsDbHelper mDbHelper;

    private Map<String, String> mInMemoryStorage;

    private static String buildAuthority() {
        return "com.example.nickgao.database.RCMSettingsProvider";
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new RCMSettingsDbHelper(getContext());

        mInMemoryStorage = new HashMap<String, String>();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG) {
            EngLog.d(TAG, "query(" + uri + "), ...");
        }

        final int match = URI_MATCHER.match(uri);

        if (match == UriMatcher.NO_MATCH) {
            MktLog.e(TAG, "query(): Wrong URI: " + uri);

            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (DEBUG) {
            EngLog.d(TAG, "match " + match);
        }


        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        boolean whereAppended = false;

        if (mailboxIdRequired(match)) {
            final String mailboxIdString = uri.getQueryParameter(RCMDataStore.RCMColumns.MAILBOX_ID);

            long mailboxId;
            if (TextUtils.isEmpty(mailboxIdString)) {
                mailboxId = RCMDataStore.MailboxCurrentTable.MAILBOX_ID_NONE;
            } else {
                mailboxId = Long.valueOf(mailboxIdString);
            }

            queryBuilder.appendWhere(RCMDataStore.RCMColumns.MAILBOX_ID + " = " + mailboxId);
            whereAppended = true;
        }

        queryBuilder.setTables(getTableName(match));

        if (uriWithID(match)) {
            queryBuilder.appendWhere((!whereAppended ? "" : " AND ") + (BaseColumns._ID + "=" + uri.getLastPathSegment()));
        }

        if (projection == null) {
            projection = defaultProjection(match);
        }

        final SQLiteDatabase db;
        try {
            db = mDbHelper.getReadableDatabase();
        } catch (SQLiteException e) {
            MktLog.e(TAG, "query(): Error opening readable database", e);

            throw e;
        }

        final Cursor cursor;
        try {
            cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        } catch (Throwable e) {
            MktLog.e(TAG, "query(): Exception at db query", e);

            throw new RuntimeException("Exception at db query: " + e.getMessage());
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            if (DEBUG) {
                EngLog.d(TAG, "query(): Cursor has " + cursor.getCount() + " rows");
            }
        } else if (DEBUG) {
            EngLog.d(TAG, "query(): null cursor returned from db query");
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        if (DEBUG) {
            EngLog.d(TAG, "getType(" + uri + ")");
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) {
            EngLog.d(TAG, "insert(" + uri + ", ...)");
        }

        final int match = URI_MATCHER.match(uri);

        if (match == UriMatcher.NO_MATCH) {
            MktLog.e(TAG, "insert(): Wrong URI: " + uri);

            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (match == IM_MEMORY_SETTINGS_ID_MATCH){
            MktLog.d(TAG, "insert " + values.size());
            saveInMemory(values);
            return getUriForId(1, UriHelper.removeQuery(uri));
        }

        if (uriWithID(match)) {
            MktLog.e(TAG, "insert(): Insert not allowed for this URI: " + uri);

            throw new IllegalArgumentException("Insert not allowed for this URI: " + uri);
        }

        final SQLiteDatabase db;

        try {
            db = mDbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            MktLog.e(TAG, "insert(): Error opening writable database", e);
            throw e;
        }

        final long rowId;
        try {
            rowId = db.insert(getTableName(match), null, values);
        } catch (SQLiteException e) {
            MktLog.e(TAG, "insert() failed", e);

            throw e;
        }
        MktLog.i(TAG,"RCMSettingsProvider insert rowId="+rowId);
        final Uri uriForId = getUriForId(rowId, UriHelper.removeQuery(uri));

        if (DEBUG) {
            EngLog.d(TAG, "insert(): return " + uriForId);
        }

        return uriForId;
    }

    private void saveInMemory(ContentValues values){
        try {
            mInMemoryStorage.put(values.get(RCMDataStore.GeneralSettingsTable.KEY).toString(),
                    values.get(RCMDataStore.GeneralSettingsTable.VALUE).toString());
        } catch (Exception e) {
            MktLog.e(TAG, "e:" + e.getMessage());
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) {
            EngLog.d(TAG, "delete(" + uri + ", ...");
        }

        final int match = URI_MATCHER.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            MktLog.e(TAG, "delete(): Wrong URI: " + uri);

            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (uriWithID(match)) {
            selection = BaseColumns._ID + "=" + uri.getLastPathSegment()
                    + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
        }

        //TODO: Maybe add mailbox id

        final SQLiteDatabase db;
        try {
            db = mDbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            MktLog.e(TAG, "delete(): Error opening writable database", e);
            throw e;
        }

        final int delCount;
        try {
            delCount = db.delete(getTableName(match), selection, selectionArgs);
        } catch (SQLiteException e) {
            MktLog.e(TAG, "delete(): DB rows delete error", e);

            throw e;
        }

        if (DEBUG) {
            EngLog.d(TAG, "delete(), " + uri.toString() + "; " + delCount + " rows deleted");
        }
        if (delCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) {
            EngLog.d(TAG, "update(" + uri + ", ...)");
        }

        final int match = URI_MATCHER.match(uri);

        if (match == UriMatcher.NO_MATCH) {
            MktLog.e(TAG, "update(): Wrong URI: " + uri);

            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (match == IM_MEMORY_SETTINGS_ID_MATCH){
            saveInMemory(values);
            return 1;
        }

        if (uriWithID(match)) {
            selection = BaseColumns._ID + " = " + uri.getLastPathSegment()
                    + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
        }

        final String mailboxId = uri.getQueryParameter(RCMDataStore.RCMColumns.MAILBOX_ID);
        if (!TextUtils.isEmpty(mailboxId)) {
            selection = RCMDataStore.RCMColumns.MAILBOX_ID + "=" + mailboxId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
        }
        final SQLiteDatabase db;
        try {
            db = mDbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            MktLog.e(TAG, "update(): Error opening writable database", e);

            throw e;
        }

        final int updateCount;
        try {
            updateCount = db.update(getTableName(match), values, selection, selectionArgs);
        } catch (SQLiteException e) {
            MktLog.e(TAG, "update() failed", e);

            throw e;
        }

        if (DEBUG) {
            EngLog.d(TAG, "update(): " + updateCount + " rows updated");
        }

        if (updateCount > 0 && !isInBatchMode()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateCount;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) {
            EngLog.d(TAG, "bulkInsert(" + uri + ", ...)");
        }

        final int match = URI_MATCHER.match(uri);

        if (match == UriMatcher.NO_MATCH) {
            MktLog.e(TAG, "bulkInsert(): Wrong URI: " + uri);

            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        if (uriWithID(match)) {
            MktLog.e(TAG, "bulkInsert(): Insert not allowed for this URI: " + uri);

            throw new IllegalArgumentException("Insert not allowed for this URI: " + uri);
        }

        final SQLiteDatabase db;
        try {
            db = mDbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            MktLog.e(TAG, "bulkInsert(): Error opening writable database", e);

            throw e;
        }

        int added = 0;
        long rowID;
        final String tableName = getTableName(match);

        try {
            db.beginTransaction();
            for (ContentValues value : values) {
                try {
                    rowID = db.insert(tableName, null, value);
                } catch (SQLiteException e) {
                    MktLog.e(TAG, "bulkInsert() P1", e);

                    continue;
                }

                if (rowID <= 0) {
                    MktLog.e(TAG, "bulkInsert() P2: " + rowID);

                    continue;
                }
                added++;
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            MktLog.e(TAG, "bulkInsert() P3", e);

            throw new RuntimeException("bulkInsert(): DB insert failed: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return added;
    }


    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mIsInBatchMode.set(true);
        db.beginTransaction();
        try {
            final ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            return results;
        } finally {
            mIsInBatchMode.remove();
            db.endTransaction();
        }
    }

    private boolean isInBatchMode() {
        return mIsInBatchMode.get() != null && mIsInBatchMode.get();
    }

    private String getTableName(int uriMatch) {
        switch (uriMatch) {
            case GENERAL_SETTINGS_ID_MATCH:
            case GENERAL_SETTINGS_LIST_MATCH:
                return RCMDataStore.GeneralSettingsTable.getInstance().getName();
            case USER_SETTINGS_ID_MATCH:
            case USER_SETTINGS_LIST_MATCH:
                return RCMDataStore.UserSettingsTable.getInstance().getName();
            default:
                throw new Error(TAG + " No table defined for #" + uriMatch);
        }
    }

    private boolean uriWithID(int uriMatch) {
        switch (uriMatch) {
            case GENERAL_SETTINGS_ID_MATCH:
            case USER_SETTINGS_ID_MATCH:
                return true;

            default:
                return false;
        }
    }

    private boolean mailboxIdRequired(int uriMatch) {
        if (uriWithID(uriMatch)) {
            return false;
        }

        switch (uriMatch) {
            case USER_SETTINGS_LIST_MATCH:
            case USER_SETTINGS_ID_MATCH:
                return true;
            default:
                return false;
        }
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id >= 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                // notify all listeners of changes and return itemUri
                getContext().getContentResolver().notifyChange(itemUri, null);
            }
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException("Problem while inserting into uri: " + uri);
    }

    private static final int GENERAL_SETTINGS_LIST_MATCH = 1;
    private static final int GENERAL_SETTINGS_ID_MATCH = 2;
    private static final int USER_SETTINGS_LIST_MATCH = 3;
    private static final int USER_SETTINGS_ID_MATCH = 4;
    private static final int IM_MEMORY_SETTINGS_ID_MATCH = 5;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, GENERAL_SETTINGS, GENERAL_SETTINGS_LIST_MATCH);
        URI_MATCHER.addURI(AUTHORITY, GENERAL_SETTINGS + "/#", GENERAL_SETTINGS_ID_MATCH);
        URI_MATCHER.addURI(AUTHORITY, USER_SETTINGS, USER_SETTINGS_LIST_MATCH);
        URI_MATCHER.addURI(AUTHORITY, USER_SETTINGS + "/#", USER_SETTINGS_ID_MATCH);
        URI_MATCHER.addURI(AUTHORITY, IM_MEMORY_SETTINGS, IM_MEMORY_SETTINGS_ID_MATCH);
    }

    private String[] defaultProjection(int uriMatch) {
        switch (uriMatch) {
            case GENERAL_SETTINGS_ID_MATCH:
            case GENERAL_SETTINGS_LIST_MATCH:
                return GeneralSettingsProjection;
            case USER_SETTINGS_ID_MATCH:
            case USER_SETTINGS_LIST_MATCH:
                return UserSettingsProjection;
            default:
                return null;
        }
    }

    private static final String[] GeneralSettingsProjection = {
            RCMDataStore.GeneralSettingsTable._ID,
            RCMDataStore.GeneralSettingsTable.KEY,
            RCMDataStore.GeneralSettingsTable.VALUE
    };

    private static final String[] UserSettingsProjection = {
            RCMDataStore.UserSettingsTable._ID,
            RCMDataStore.UserSettingsTable.MAILBOX_ID,
            RCMDataStore.UserSettingsTable.KEY,
            RCMDataStore.UserSettingsTable.VALUE
    };

}
