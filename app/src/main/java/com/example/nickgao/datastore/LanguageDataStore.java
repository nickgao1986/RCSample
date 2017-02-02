package com.example.nickgao.datastore;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.RCMProviderHelper;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.i18n.Language;
import com.example.nickgao.service.model.extensioninfo.ExtensionLanguage;
import com.example.nickgao.service.model.i18n.LanguageRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.gao on 2/1/17.
 */

public class LanguageDataStore {

    public static final String LOG_TAG = "[RC]LanguageDataStore";
    public static String logTag = "[RC]LanguageDataStore";

    public static int setUserServerLanguageInLocalDB(int userServerLangId) {
        if (LogSettings.ENGINEERING) {
            MktLog.d(LOG_TAG, "setUserServerLanguageInLocalDB: language id =" + userServerLangId);
        }
        ExtensionLanguage extensionLanguage = LanguageDataStore.getExtensionLanguageById(userServerLangId);
        if (extensionLanguage != null) {
            final Context context = RingCentralApp.getContextRC();
            long mailbox_id = CurrentUserSettings.getSettings().getCurrentMailboxId();
            Uri uri = UriHelper.getUri(RCMProvider.SERVICE_EXTENSION_INFO);
            ContentValues values = new ContentValues();
            values.put(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_ID, String.valueOf(userServerLangId));
            values.put(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_NAME, extensionLanguage.getName());
            values.put(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_LOCALE, extensionLanguage.getLocaleCode());
            String where = RCMDataStore.RCMColumns.MAILBOX_ID + '=' + mailbox_id;
            return context.getContentResolver().update(uri, values, where, null);
        } else {
            if (LogSettings.ENGINEERING) {
                MktLog.e(LOG_TAG, "can not find extension language from language list by language id =" + userServerLangId);
            }
            return RCMProviderHelper.updateSingleValueWithMailboxId(RingCentralApp.getContextRC(), RCMProvider.SERVICE_EXTENSION_INFO, RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_ID, String.valueOf(userServerLangId), null);
        }
    }

    public static int getUserServerLanguage() {
        String value = RCMProviderHelper.simpleQueryWithMailboxId(RingCentralApp.getContextRC(), RCMProvider.SERVICE_EXTENSION_INFO, RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_ID);
        return TextUtils.isEmpty(value) ? Language.INVALID_ID : Integer.valueOf(value);
    }

    public static int getUserGreetingLanguage() {
        String value = RCMProviderHelper.simpleQueryWithMailboxId(RingCentralApp.getContextRC(), RCMProvider.SERVICE_EXTENSION_INFO, RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_ID);
        return TextUtils.isEmpty(value) ? Language.INVALID_ID : Integer.valueOf(value);
    }

    public static int getUserFormattingLanguage() {
        String value = RCMProviderHelper.simpleQueryWithMailboxId(RingCentralApp.getContextRC(), RCMProvider.SERVICE_EXTENSION_INFO, RCMDataStore.ServiceExtensionInfoTable.USER_FORMATTING_LANGUAGE_ID);
        return TextUtils.isEmpty(value) ? Language.INVALID_ID : Integer.valueOf(value);
    }

    public static ExtensionLanguage getExtensionUserServerLanguage() {
        ExtensionLanguage result = null;
        int langId = Language.INVALID_ID;
        String langName = null;
        String langLocale = null;
        long mailbox_id = CurrentUserSettings.getSettings().getCurrentMailboxId();
        Uri uri = UriHelper.getUri(RCMProvider.SERVICE_EXTENSION_INFO, mailbox_id);
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(uri, new String[] { RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_ID, RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_NAME, RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_LOCALE }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                langId = Integer.valueOf(cursor.getString(cursor.getColumnIndex(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_ID))).intValue();
                langName = cursor.getString(cursor.getColumnIndex(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_NAME));
                langLocale = cursor.getString(cursor.getColumnIndex(RCMDataStore.ServiceExtensionInfoTable.USER_SERVER_LANGUAGE_LOCALE));
                if (LogSettings.ENGINEERING) {
                    MktLog.d(LOG_TAG, "getExtensionUserServerLanguage(), langId=" + langId + "; langName=" + langName + "; langLocale=" + langLocale);
                }
                result = new ExtensionLanguage(langId, langName, langLocale);
            }
        } catch (Throwable th) {
            result = null;
            MktLog.e(LOG_TAG, th);
        } finally {
            if (cursor != null) {
                try {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                } catch (Throwable err) {
                    MktLog.e(LOG_TAG, err);
                }
            }
        }

        return result;
    }

    public static ExtensionLanguage getExtensionGreetingLanguage() {
        ExtensionLanguage result = null;
        int langId = Language.INVALID_ID;
        String langName = null;
        String langLocale = null;
        long mailbox_id = CurrentUserSettings.getSettings().getCurrentMailboxId();
        Uri uri = UriHelper.getUri(RCMProvider.SERVICE_EXTENSION_INFO, mailbox_id);
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(uri, new String[] { RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_ID, RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_NAME, RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_LOCALE }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                langId = Integer.valueOf(cursor.getString(cursor.getColumnIndex(RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_ID))).intValue();
                langName = cursor.getString(cursor.getColumnIndex(RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_NAME));
                langLocale = cursor.getString(cursor.getColumnIndex(RCMDataStore.ServiceExtensionInfoTable.USER_GREETING_LANGUAGE_LOCALE));
                if (LogSettings.ENGINEERING) {
                    MktLog.d(LOG_TAG, "getExtensionGreetingLanguage(), langId=" + langId + "; langName=" + langName + "; langLocale=" + langLocale);
                }
                result = new ExtensionLanguage(langId, langName, langLocale);
            }
        } catch (Throwable th) {
            result = null;
            MktLog.e(LOG_TAG, th);
        } finally {
            if (cursor != null) {
                try {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                } catch (Throwable err) {
                    MktLog.e(LOG_TAG, err);
                }
            }
        }

        return result;
    }


    public static int getLanguageId(String isoLocaleCode) {
        int result = Language.INVALID_ID;
        Uri uri = UriHelper.getUri(RCMProvider.LANGUAGES_TABLE);
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(uri, new String[] { RCMDataStore.LanguagesTable.LANG_ID }, RCMDataStore.LanguagesTable.LANG_LOCALE_CODE + "='" + isoLocaleCode + "'", null, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_ID));
            }
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, th);
        } finally {
            if (cursor != null) {
                try {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                } catch (Throwable err) {
                    MktLog.e(LOG_TAG, err);
                }
            }
        }

        return result;
    }

    public static String getLanguageName(String isoLocaleCode) {
        String result = null;
        if (isoLocaleCode == null) {
            return result;
        }

        Uri uri = UriHelper.getUri(RCMProvider.LANGUAGES_TABLE);
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(uri, new String[] { RCMDataStore.LanguagesTable.LANG_NAME }, RCMDataStore.LanguagesTable.LANG_LOCALE_CODE + "='" + isoLocaleCode + "'", null, null);
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_NAME));
            }
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, th);
        } finally {
            if (cursor != null) {
                try {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                } catch (Throwable err) {
                    MktLog.e(LOG_TAG, err);
                }
            }
        }

        return result;
    }

    public static ExtensionLanguage getExtensionLanguageById(int langId) {
        ExtensionLanguage result = null;
        String langName = null;
        String langLocale = null;
        Uri uri = UriHelper.getUri(RCMProvider.LANGUAGES_TABLE);
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(uri, new String[] { RCMDataStore.LanguagesTable.LANG_NAME, RCMDataStore.LanguagesTable.LANG_LOCALE_CODE }, RCMDataStore.LanguagesTable.LANG_ID + "=" + langId, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                langName = cursor.getString(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_NAME));
                langLocale = cursor.getString(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_LOCALE_CODE));
                if (LogSettings.ENGINEERING) {
                    MktLog.d(LOG_TAG, "getExtensionLanguageById(), langId=" + langId + "; langName=" + langName + "; langLocale=" + langLocale);
                }
                result = new ExtensionLanguage(langId, langName, langLocale);
            }
        } catch (Throwable th) {
            result = null;
            MktLog.e(LOG_TAG, th);
        } finally {
            if (cursor != null) {
                try {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                } catch (Throwable err) {
                    MktLog.e(LOG_TAG, err);
                }
            }
        }

        return result;
    }

    public static ArrayList<LanguageRecord> getAllItemsFromDb() {
        ArrayList<LanguageRecord> dbRecords = new ArrayList<LanguageRecord>();
        String[] projection = new String[] {
                RCMDataStore.LanguagesTable.LANG_ID,
                RCMDataStore.LanguagesTable.LANG_URI,
                RCMDataStore.LanguagesTable.LANG_NAME,
                RCMDataStore.LanguagesTable.LANG_ISO_CODE,
                RCMDataStore.LanguagesTable.LANG_LOCALE_CODE
        };
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(UriHelper.getUri(RCMProvider.LANGUAGES_TABLE),
                    projection,
                    null,
                    null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                LanguageRecord item;
                do {
                    item = new LanguageRecord();
                    item.setId(cursor.getInt(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_ID)));
                    item.setUri(cursor.getString(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_URI)));
                    item.setName(cursor.getString(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_NAME)));
                    item.setIsoCode(cursor.getString(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_ISO_CODE)));
                    item.setLocaleCode(cursor.getString(cursor.getColumnIndex(RCMDataStore.LanguagesTable.LANG_LOCALE_CODE)));
                    dbRecords.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Throwable th) {
            MktLog.e(LOG_TAG, th);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            } catch (Throwable err) {
                MktLog.e(LOG_TAG, err);
            }
        }
        return dbRecords;
    }

    public static void processPage(List<LanguageRecord> serverRecords) throws IOException {
        ArrayList<LanguageRecord> dbRecords = getAllItemsFromDb();
        boolean isServerRecordValid = (serverRecords != null && !serverRecords.isEmpty());

        ArrayList<LanguageRecord> newItems = new ArrayList<LanguageRecord>();
        ArrayList<LanguageRecord> modifyItems = new ArrayList<LanguageRecord>();
        ArrayList<LanguageRecord> deleteItems = new ArrayList<LanguageRecord>();

        if (dbRecords.isEmpty()) {
            if (isServerRecordValid) {
                for (LanguageRecord lrServer : serverRecords) {
                    newItems.add(lrServer);
                }
            } else {
                if (LogSettings.ENGINEERING) {
                    MktLog.e(LOG_TAG, "processPage(), there is not any records in local and server.");
                }
                return;
            }
        } else if (isServerRecordValid) {
            //find which is new
            boolean isFound = false;
            for (LanguageRecord lrOld : dbRecords) {
                isFound = false;
                for (LanguageRecord lrServer : serverRecords) {
                    if (lrOld.getId() == lrServer.getId()) {
                        isFound = true;
                        if (!lrOld.equals(lrServer)) {
                            modifyItems.add(lrServer);
                        }
                        break;
                    }
                }
                if (!isFound) {
                    deleteItems.add(lrOld);
                }
            }

            for (LanguageRecord lrServer : serverRecords) {
                isFound = false;
                for (LanguageRecord lrOld : dbRecords) {
                    if (lrServer.getId() == lrOld.getId()) {
                        isFound = true;
                        break;
                    }
                }

                if (!isFound) {
                    newItems.add(lrServer);
                }
            }
        } else {
            if (LogSettings.ENGINEERING) {
                MktLog.e(LOG_TAG, "processPage(), there is not records in server, but some records are in local.");
            }
            return;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        if (!deleteItems.isEmpty()) {
            for (LanguageRecord lrDel : deleteItems) {
                String[] args = { String.valueOf(lrDel.getId()) };
                ops.add(ContentProviderOperation.newDelete(UriHelper.getUri(RCMProvider.LANGUAGES_TABLE)).withSelection(RCMDataStore.LanguagesTable.LANG_ID + "=?", args).build());
            }
        }
        /*
        if(!modifyItems.isEmpty()){
            for(LanguageRecord lrModify: modifyItems){
                ops.add(ContentProviderOperation.newUpdate(UriHelper.getUri(RCMProvider.LANGUAGES_TABLE)).withSelection(RCM)
            }
        }*/

        if (!newItems.isEmpty()) {
            for (LanguageRecord lrNew : newItems) {
                ops.add(ContentProviderOperation.newInsert(UriHelper.getUri(RCMProvider.LANGUAGES_TABLE))
                        .withValue(RCMDataStore.LanguagesTable.LANG_ID, lrNew.getId())
                        .withValue(RCMDataStore.LanguagesTable.LANG_URI, lrNew.getUri())
                        .withValue(RCMDataStore.LanguagesTable.LANG_NAME, lrNew.getName())
                        .withValue(RCMDataStore.LanguagesTable.LANG_ISO_CODE, lrNew.getIsoCode())
                        .withValue(RCMDataStore.LanguagesTable.LANG_LOCALE_CODE, lrNew.getLocaleCode())
                        .build());
            }
        }

        try {
            if (!ops.isEmpty()) {
                RingCentralApp.getContextRC().getContentResolver().applyBatch(RCMProvider.AUTHORITY, ops);
            }
        } catch (Exception e) {
            if (LogSettings.ENGINEERING) {
                MktLog.e(LOG_TAG, "insertGroupData(), " + e.getMessage());
            }
        }
    }

}
