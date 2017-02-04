package com.example.nickgao.contacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.example.nickgao.R;
import com.example.nickgao.androidsample11.ContactsUtils;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudContactSyncService;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContact;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContactLoader;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMDataStore.CloudFavoritesTable;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.RCMProviderHelper;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.EngLog;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.utils.RCMConstants;
import com.example.nickgao.utils.RcAlertDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nick.gao on 1/31/17.
 */

public class PersonalFavorites {


    private static final String LOG_TAG = "[RC] PersonalFavorites";
    private static final int DB_CLOUD_COMPANY = 0;
    private static final int DB_CLOUD_PERSONAL = 1;
    private static final int DB_DEVICE=2;
    private static final int DB_UNKNOWN=3;
    public static final int FAVORITES_SERVER_LIMITATION = 100;

    public interface OnAddDeviceFavoriteCallback {
        void onAddDeviceFavorite(Contact contact);
    }

    public static final String[] PROJECTION = new String[]{
            CloudFavoritesTable._ID,
            CloudFavoritesTable.CONTACT_TYPE,
            CloudFavoritesTable.CONTACT_ID,
            CloudFavoritesTable.RCM_SORT,
            CloudFavoritesTable.SYNC_STATUS
    };

    public static final int _ID_INDX = 0;
    public static final int CONTACT_TYPE_INDX = 1;
    public static final int CONTACT_ID_INDX = 2;
    public static final int SORT_INDEX = 3;
    public static final int SYNC_STATUS = 4;

    public static Contact.ContactType matchToContactType(int type) {
        Contact.ContactType contactType;
        switch (type) {
            case DB_CLOUD_COMPANY:
                contactType = Contact.ContactType.CLOUD_COMPANY;
                break;
            case DB_CLOUD_PERSONAL:
                contactType = Contact.ContactType.CLOUD_PERSONAL;
                break;
            case DB_DEVICE:
                contactType = Contact.ContactType.DEVICE;
                break;
            default:
                contactType = Contact.ContactType.UNKNOW;
        }
        return contactType;
    }

    public static int matchToFavoriteDBContactType(Contact.ContactType contactType) {
        int type;
        switch (contactType) {
            case CLOUD_COMPANY:
                type = DB_CLOUD_COMPANY;
                break;
            case CLOUD_PERSONAL:
                type = DB_CLOUD_PERSONAL;
                break;
            case DEVICE:
                type = DB_DEVICE;
                break;
            default:
                type = DB_UNKNOWN;
        }
        return type;
    }

    public static boolean isCloudFavorite(long contactId, Contact.ContactType contactType, Context context) {
        Uri uri = UriHelper.getUri(RCMProvider.CLOUD_FAVORITES, CurrentUserSettings.getSettings(context).getCurrentMailboxId());
        Cursor c = null;
        int type = matchToFavoriteDBContactType(contactType);
        try {
            c = context.getContentResolver().query(uri, PROJECTION, CloudFavoritesTable.CONTACT_ID + " = ? AND " + CloudFavoritesTable.CONTACT_TYPE + " = ?"
                            + " AND " + CloudFavoritesTable.SYNC_STATUS + " <> " + RCMDataStore.CloudFavoriteSyncStatus.Deleted.ordinal()
                            + " AND " + CloudFavoritesTable.SYNC_STATUS + " <> " + RCMDataStore.CloudFavoriteSyncStatus.UNKNOWN.ordinal(),
                    new String[]{String.valueOf(contactId), String.valueOf(type)}, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    return true;
                }
            }
        } catch (java.lang.Throwable error) {

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }




    public static boolean markedAsDeletedInFavorites(long contactId, Contact.ContactType type) {
        //if local cloud, it should delete directly
        if(CloudPersonalContact.isLocalContact(contactId)) {
            deleteFavoritesFromDatabase(contactId);
            return true;
        }
        List<Long> contactIds = new ArrayList<>();
        contactIds.add(contactId);
        return markedAsDeletedInFavorites(contactIds, type);
    }

    public static boolean markedAsDeletedInFavorites(List<Long> contactIds, Contact.ContactType type){
        return modifiedSyncStatusInFavorites(contactIds, type, RCMDataStore.CloudFavoriteSyncStatus.Deleted);
    }

    public static boolean markedAsDeletedInFavorites(List<Long> contactIds){
        if(contactIds == null || contactIds.isEmpty()) {
            return false;
        }

        List<Long> localCloudContactIds = new ArrayList<>();
        List<Long> cloudFavoriteIds = new ArrayList<>();
        for(Long contactId : contactIds) {
            if(CloudPersonalContact.isLocalContact(contactId)) {
                localCloudContactIds.add(contactId);
            }else {
                cloudFavoriteIds.add(contactId);
            }
        }

        if(!localCloudContactIds.isEmpty()) {
            boolean ret = deleteFavoritesFromDatabase(localCloudContactIds);
            if(localCloudContactIds.size() == contactIds.size()) {
                return ret;
            }
        }
        return modifiedSyncStatusInFavorites(cloudFavoriteIds, RCMDataStore.CloudFavoriteSyncStatus.Deleted);
    }

    public static boolean modifiedSyncStatusInFavorites(List<Long> contactIds,RCMDataStore.CloudFavoriteSyncStatus syncStatus){
        try {
            String idsString = TextUtils.join(",",contactIds);
            EngLog.d(LOG_TAG, "modifiedSyncStatusInFavorites: ids=" + idsString + ", sync status=" + syncStatus.name());
            Context context = RingCentralApp.getContextRC();
            String where = CloudFavoritesTable.CONTACT_ID + " IN (" + idsString +" )";
            long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
            Uri uri = UriHelper.getUri(RCMProvider.CLOUD_FAVORITES, mailboxId);
            ContentValues values = new ContentValues();
            values.put(CloudFavoritesTable.SYNC_STATUS, syncStatus.ordinal());
            int rows = context.getContentResolver().update(uri, values, where, null);
            CloudContactSyncService.sendCommand(context, CloudContactSyncService.FAVORITE_SYNC);
            return rows > 0;
        } catch (Exception ex) {
            if (LogSettings.MARKET) {
                MktLog.e(LOG_TAG, "modified sync status failed: " + ex.getMessage());
            }
            return false;
        }
    }

    public static boolean modifiedSyncStatusInFavorites(long contactId, Contact.ContactType type, RCMDataStore.CloudFavoriteSyncStatus syncStatus){
        List<Long> contactIds = new ArrayList<>();
        contactIds.add(contactId);
        return modifiedSyncStatusInFavorites(contactIds, type, syncStatus);
    }

    public static boolean modifiedSyncStatusInFavorites(List<Long> contactIds, Contact.ContactType type, RCMDataStore.CloudFavoriteSyncStatus syncStatus){
        try {
            String idsString = TextUtils.join(",",contactIds);
            EngLog.d(LOG_TAG, "modifiedSyncStatusInFavorites: ids=" + idsString + ", context type=" + type.name() + ", sync status=" + syncStatus.name());
            Context context = RingCentralApp.getContextRC();
            String where = CloudFavoritesTable.CONTACT_ID + " IN (" + idsString +" ) and " + CloudFavoritesTable.CONTACT_TYPE + " =?";
            long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
            Uri uri = UriHelper.getUri(RCMProvider.CLOUD_FAVORITES, mailboxId);
            ContentValues values = new ContentValues();
            values.put(CloudFavoritesTable.SYNC_STATUS, syncStatus.ordinal());
            int rows = context.getContentResolver().update(uri, values, where, new String[]{String.valueOf(matchToFavoriteDBContactType(type))});
            CloudContactSyncService.sendCommand(context, CloudContactSyncService.FAVORITE_SYNC);
            return rows > 0;
        } catch (Exception ex) {
            if (LogSettings.MARKET) {
                MktLog.e(LOG_TAG, "modified sync status failed: " + ex.getMessage());
            }
            return false;
        }
    }

    public static boolean deleteFavoritesFromDatabase(long contactId){
        List<Long> contactIds = new ArrayList<>();
        contactIds.add(contactId);
        return deleteFavoritesFromDatabase(contactIds);
    }

    public static boolean deleteFavoritesFromDatabase(List<Long> contactIds){
        Uri uri = UriHelper.getUri(RCMProvider.CLOUD_FAVORITES, CurrentUserSettings.getSettings().getCurrentMailboxId());
        try {
            Context context = RingCentralApp.getContextRC();
            String where = CloudFavoritesTable.CONTACT_ID + " IN ( " + TextUtils.join(",",contactIds)+" ) ";
            int rows = context.getContentResolver().delete(uri, where, null);
            return rows > 0;
        } catch (java.lang.Throwable error) {
            if (LogSettings.MARKET) {
                MktLog.e(LOG_TAG, "isFavorite(phoneId): " + error.getMessage());
            }
            return false;
        }
    }

    public static boolean addToCloudFavoriteWithLimitationChecking(long contactId, Contact.ContactType contactType, Context activity) {
        return addToCloudFavoriteWithLimitationChecking(contactId, contactType, activity, null);
    }

    public static boolean addToCloudFavoriteWithLimitationChecking(long contactId, Contact.ContactType contactType, Context activity, OnAddDeviceFavoriteCallback deviceFavoriteCallback) {
        if(isCloudFavoritesAchieveServerLimitation(activity)) {
            if(activity instanceof Activity) {
                RcAlertDialog.showOkAlertDialog(activity, R.string.favorite_over_server_limitation_title, R.string.favorite_over_server_limitation_content);
            }
            return false;
        }

        return addToCloudFavoriteInternal(contactId, contactType, activity, deviceFavoriteCallback);
    }

    public static boolean isCloudFavoritesAchieveServerLimitation(Context context) {
        int count = PersonalFavorites.getCloudFavoritesCount(context);
        return FAVORITES_SERVER_LIMITATION <= count;
    }

    private static int getCloudFavoritesCount(Context context) {
        String selection = CloudFavoritesTable.SYNC_STATUS + " <> " + RCMDataStore.CloudFavoriteSyncStatus.Deleted.ordinal()
                + " AND " + CloudFavoritesTable.SYNC_STATUS + " <> " + RCMDataStore.CloudFavoriteSyncStatus.UNKNOWN.ordinal();
        return RCMProviderHelper.getRecordsCount(context, RCMProvider.CLOUD_FAVORITES, selection);
    }

    private static boolean addToCloudFavoriteInternal(long contactId, Contact.ContactType contactType, Context activity, OnAddDeviceFavoriteCallback deviceFavoriteCallback) {
        RCMDataStore.CloudFavoriteSyncStatus syncStatus;
        switch (contactType) {
            case CLOUD_PERSONAL: {
                if(CloudPersonalContact.isLocalContact(contactId)) {
                    syncStatus = RCMDataStore.CloudFavoriteSyncStatus.CloudTemporary;
                }else {
                    syncStatus = RCMDataStore.CloudFavoriteSyncStatus.NeedSync;
                }
                break;
            }
            case CLOUD_COMPANY:
                syncStatus = RCMDataStore.CloudFavoriteSyncStatus.NeedSync;
                break;
            case DEVICE: {
                Contact contact = ContactsUtils.importFromCachedDeviceContactToCloud(activity, contactId);
                if(contact == null) {
                    return false;
                }

                if(deviceFavoriteCallback != null) {
                    deviceFavoriteCallback.onAddDeviceFavorite(contact);
                }
                contactId = contact.getId();
                contactType = Contact.ContactType.CLOUD_PERSONAL;
                syncStatus = RCMDataStore.CloudFavoriteSyncStatus.CloudTemporary;
                break;
            }
            default:
                syncStatus = RCMDataStore.CloudFavoriteSyncStatus.UNKNOWN;
                break;
        }

        if(PersonalFavorites.modifiedSyncStatusInFavorites(contactId, contactType, syncStatus)) {
            return true;
        }

        int type = matchToFavoriteDBContactType(contactType);
        Cursor cursor = null;
        try {
            long mailBoxID = CurrentUserSettings.getSettings(activity).getCurrentMailboxId();
            ContentValues values = new ContentValues();
            values.put(CloudFavoritesTable.MAILBOX_ID, mailBoxID);
            values.put(CloudFavoritesTable.CONTACT_TYPE, type);
            values.put(CloudFavoritesTable.CONTACT_ID, contactId);
            values.put(CloudFavoritesTable.RCM_SORT, RCMProviderHelper.getPersonalFavoritesMaxPosition(activity) + 1);
            values.put(CloudFavoritesTable.SYNC_STATUS, syncStatus.ordinal());
            Uri uri = UriHelper.getUri(RCMProvider.CLOUD_FAVORITES);
            activity.getContentResolver().insert(uri, values);
            CloudContactSyncService.sendCommand(activity, CloudContactSyncService.FAVORITE_SYNC);
        } catch (Exception ex) {
            if (LogSettings.MARKET) {
                MktLog.e(LOG_TAG, "addToPersonalFavorites() failed: " + ex.getMessage());
            }
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return true;
    }

    public static List<Favorite> getFavoriteListFromDatabase() {
        return getFavoriteListFromDatabase(false, Contact.ContactType.UNKNOW);
    }

    public static List<Favorite> getFavoriteListFromDatabase(boolean specifiedContactType, Contact.ContactType contactType) {
        List<Favorite> list = new ArrayList<>();
        try {
            final Context context = RingCentralApp.getContextRC();
            if(context == null) {
                return list;
            }

            String where = null;
            String[] args = null;

            if(specifiedContactType) {
                where = CloudFavoritesTable.CONTACT_TYPE + " = ?";
                args = new String[] { String.valueOf(matchToFavoriteDBContactType(contactType))};
            }

            Cursor cursor = context.getContentResolver().query(UriHelper.getUri(RCMProvider.CLOUD_FAVORITES, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    PersonalFavorites.PROJECTION,
                    where,
                    args,
                    RCMDataStore.CloudFavoritesTable.RCM_SORT);
            if(cursor == null || cursor.getCount() == 0) {
                return list;
            }
            cursor.move(-1);
            while (cursor.moveToNext()) {
                Favorite entity = new Favorite();
                entity.dbId = cursor.getLong(PersonalFavorites._ID_INDX);
                entity.contactId = cursor.getLong(PersonalFavorites.CONTACT_ID_INDX);
                entity.contactType = matchToContactType(cursor.getInt(PersonalFavorites.CONTACT_TYPE_INDX));
                entity.order = cursor.getInt(PersonalFavorites.SORT_INDEX);
                entity.syncStatus = matchToSyncStatus(cursor.getInt(PersonalFavorites.SYNC_STATUS));
                list.add(entity);
            }
        } catch (Exception e) {
            MktLog.e(LOG_TAG, "Fail to load favorites", e);
        }
        return list;
    }

    public static RCMDataStore.CloudFavoriteSyncStatus matchToSyncStatus(int value) {
        RCMDataStore.CloudFavoriteSyncStatus syncStatus;
        if(RCMDataStore.CloudFavoriteSyncStatus.Synced.ordinal() == value) {
            syncStatus = RCMDataStore.CloudFavoriteSyncStatus.Synced;
        } else if(RCMDataStore.CloudFavoriteSyncStatus.NeedSync.ordinal() == value) {
            syncStatus = RCMDataStore.CloudFavoriteSyncStatus.NeedSync;
        } else if(RCMDataStore.CloudFavoriteSyncStatus.CloudTemporary.ordinal() == value) {
            syncStatus = RCMDataStore.CloudFavoriteSyncStatus.CloudTemporary;
        } else if(RCMDataStore.CloudFavoriteSyncStatus.Deleted.ordinal() == value) {
            syncStatus = RCMDataStore.CloudFavoriteSyncStatus.Deleted;
        } else {
            syncStatus = RCMDataStore.CloudFavoriteSyncStatus.UNKNOWN;
        }
        return syncStatus;
    }

    public static class CloudFavoritesRecord {
        public CloudFavoriteContactInfo[] records;
    }


    public static String toCloudFavoriteRequestBody(List<Favorite> favorites) {
        String requestBody = null;
        Favorite favorite;
        CloudFavoritesRecord record = new CloudFavoritesRecord();
        try {
            record.records = new CloudFavoriteContactInfo[favorites.size()];
            for (int i = 0; i < favorites.size(); i++) {
                favorite = favorites.get(i);
                if(favorite.contactType == Contact.ContactType.CLOUD_PERSONAL) {
                    record.records[i] = CloudFavoriteContactInfo.Builder.cloudContactFavorite(favorite.order, String.valueOf(favorite.contactId));
                } else if(favorite.contactType == Contact.ContactType.CLOUD_COMPANY) {
                    record.records[i] = CloudFavoriteContactInfo.Builder.extensionFavorite(favorite.order, String.valueOf(favorite.contactId));
                }
            }

            Gson gson = new Gson();
            requestBody = gson.toJson(record);
        }catch (Throwable th) {
            MktLog.d(LOG_TAG, th.toString());
        }

        EngLog.d(LOG_TAG, "toCloudFavoriteRequestBody request body=" + requestBody);

        return requestBody;
    }

    public static List<Favorite> truncateIfExceedServerLimitation(List<Favorite> favorites) {
        return (favorites.size() > FAVORITES_SERVER_LIMITATION) ? favorites.subList(0, FAVORITES_SERVER_LIMITATION - 1) : favorites;
    }

    public static void onOrderChanges() {
        CurrentUserSettings.getSettings().setCurrentFavoriteOrderChanged(true);
        CloudContactSyncService.sendCommand(RingCentralApp.getContextRC(), CloudContactSyncService.FAVORITE_SYNC);
    }

    public static void deleteAndInsertFavoriteListFromDatabase(List<Favorite> localFavoriteList, List<Favorite> serverFavoriteList) {
        try {
            final Context context = RingCentralApp.getContextRC();
            if(context == null) {
                return;
            }
            long mailBoxID = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
            ContentResolver cr = context.getContentResolver();
            Uri uri = UriHelper.getUri(RCMProvider.CLOUD_FAVORITES);

            //speed up
            Map<Long, Favorite> mapServerPersonalFavorites = new HashMap<>();
            for(PersonalFavorites.Favorite serverFavorite: serverFavoriteList) {
                mapServerPersonalFavorites.put(serverFavorite.getContactId(), serverFavorite);
            }

            List<Favorite> localTempCloudFavorites = new ArrayList<>();
            List<Favorite> localStillNeedSyncFavorites = new ArrayList<>();
            //remove old favorites
            if(localFavoriteList != null && !localFavoriteList.isEmpty()) {
                List<Long> idList = new ArrayList<>();
                for (Favorite favorite : localFavoriteList) {
                    if(favorite.getSyncStatus() != RCMDataStore.CloudFavoriteSyncStatus.CloudTemporary) {
                        if(favorite.getSyncStatus() == RCMDataStore.CloudFavoriteSyncStatus.NeedSync) {
                            //if there is not matched in server returned list, that means this favorite still needing to sync, do not delete
                            if(!mapServerPersonalFavorites.containsKey(favorite.getContactId())) {
                                localStillNeedSyncFavorites.add(favorite);
                                continue;
                            }
                        }
                        idList.add(favorite.getDbId());
                    }else {
                        //local temp cloud should not be deleted
                        localTempCloudFavorites.add(favorite);
                    }
                }

                if (!idList.isEmpty()) {
                    String where = CloudFavoritesTable._ID + " IN ( " + TextUtils.join(",", idList) +" ) ";
                    int rows = cr.delete(uri, where, null);
                    EngLog.d(LOG_TAG, "deleteAndInsertFavoriteListFromDatabase deleted rows =" + rows);
                }
            }

            //add favorites from server
            List<ContentValues> favoriteCRList = new ArrayList<>();
            int order = 0;
            if(serverFavoriteList != null && !serverFavoriteList.isEmpty()) {
                for (Favorite favorite : serverFavoriteList) {
                    order = favorite.getOrder();
                    ContentValues values = new ContentValues();
                    values.put(CloudFavoritesTable.MAILBOX_ID, mailBoxID);
                    values.put(CloudFavoritesTable.CONTACT_TYPE, matchToFavoriteDBContactType(favorite.getContactType()));
                    values.put(CloudFavoritesTable.CONTACT_ID, favorite.getContactId());
                    values.put(CloudFavoritesTable.RCM_SORT, order);
                    values.put(CloudFavoritesTable.SYNC_STATUS, RCMDataStore.CloudFavoriteSyncStatus.Synced.ordinal());
                    favoriteCRList.add(values);
                }
            }

            if (!favoriteCRList.isEmpty()) {
                CloudPersonalContactLoader.bulkInsert(cr, uri, favoriteCRList);
            }

            //re-order or delete favorites
            ArrayList<ContentProviderOperation> favoriteOps = new ArrayList<>();
            //re-order need sync favorites
            if(!localStillNeedSyncFavorites.isEmpty() || !localTempCloudFavorites.isEmpty()) {
                //update
                Uri favoriteUriWithId = UriHelper.getUri(RCMProvider.CLOUD_FAVORITES, mailBoxID);
                String where = CloudFavoritesTable._ID + "=?";
                //still needing sync favorites
                for(Favorite favorite : localStillNeedSyncFavorites) {
                    String[] args = new String[]{String.valueOf(favorite.getDbId())};
                    //any exceed items should be deleted.
                    if(order >= PersonalFavorites.FAVORITES_SERVER_LIMITATION) {
                        favoriteOps.add(ContentProviderOperation.newDelete(favoriteUriWithId).withSelection(where, args).build());
                        continue;
                    }
                    order++;
                    ContentValues values = new ContentValues();
                    values.put(CloudFavoritesTable.RCM_SORT, order);
                    favoriteOps.add(ContentProviderOperation.newUpdate(favoriteUriWithId).withSelection(where, args).withValues(values)
                            .build());
                }

                //temp cloud contacts favorites
                for(Favorite favorite : localTempCloudFavorites) {
                    String[] args = new String[]{String.valueOf(favorite.getDbId())};
                    //any exceed items should be deleted.
                    if(order >= PersonalFavorites.FAVORITES_SERVER_LIMITATION) {
                        favoriteOps.add(ContentProviderOperation.newDelete(favoriteUriWithId).withSelection(where, args).build());
                        continue;
                    }
                    order++;
                    ContentValues values = new ContentValues();
                    values.put(CloudFavoritesTable.RCM_SORT, order);
                    favoriteOps.add(ContentProviderOperation.newUpdate(favoriteUriWithId).withSelection(where, args).withValues(values)
                            .build());
                }
            }

            if(!favoriteOps.isEmpty()) {
                cr.applyBatch(RCMProvider.AUTHORITY, favoriteOps);
            }

            //notify
            if(!favoriteOps.isEmpty() || !favoriteCRList.isEmpty()) {
                context.sendBroadcast(new Intent(RCMConstants.ACTION_CLOUD_FAVORITE_CHANGED));
            }
        } catch (Exception e) {
            MktLog.e(LOG_TAG, "deleteAndInsertFavoriteListFromDatabase:", e);
        }
    }


    public static class Favorite {
        public long dbId;
        public long contactId;
        public int order;
        public Contact.ContactType contactType;
        public RCMDataStore.CloudFavoriteSyncStatus syncStatus = RCMDataStore.CloudFavoriteSyncStatus.UNKNOWN;
        public Favorite() {}
        public Favorite(long dbId, long contactId, int order, Contact.ContactType contactType) {
            this.dbId = dbId;
            this.contactId = contactId;
            this.order = order;
            this.contactType = contactType;
        }

        public void setSyncStatus(RCMDataStore.CloudFavoriteSyncStatus syncStatus) {
            this.syncStatus = syncStatus;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public long getDbId() {
            return dbId;
        }

        public void setDbId(long dbId) {
            this.dbId = dbId;
        }

        public long getContactId() {
            return contactId;
        }

        public void setContactId(long contactId) {
            this.contactId = contactId;
        }

        public int getOrder() {
            return order;
        }

        public Contact.ContactType getContactType() {
            return contactType;
        }

        public void setContactType(Contact.ContactType contactType) {
            this.contactType = contactType;
        }

        public RCMDataStore.CloudFavoriteSyncStatus getSyncStatus() {
            return syncStatus;
        }
    }



}
