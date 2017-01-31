package com.example.nickgao.contacts;

import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.logging.EngLog;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.network.RestApiErrorCodes;
import com.example.nickgao.network.RestSession;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.RestRequestListener;
import com.example.nickgao.service.ServiceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nick.gao on 1/31/17.
 */

public class CloudFavoriteServiceLoader implements RestRequestListener {

    private static final String TAG = "[RC]CloudFavoriteServiceLoader";

    private static CloudFavoriteServiceLoader sLoader;

    private CloudFavoriteService mLoader;

    public static synchronized CloudFavoriteServiceLoader getLoader(){
        if (sLoader == null){
            sLoader = new CloudFavoriteServiceLoader();
        }

        return sLoader;
    }

    private CloudFavoriteServiceLoader(){
    }

    public void sync() {


        if(!RestSession.validateSession(RingCentralApp.getContextRC())) {
            return;
        }

        if (mLoader != null && !mLoader.executionFinished()){
            return;
        }
        downloadCloudFavorites();
    }


    @Override
    public void onRequestFailure(int errorCode) {
        int type = mLoader.getRequestType();
        MktLog.i(TAG,"=====onRequestFailure type="+type);

        if(errorCode == RestApiErrorCodes.SC_NOT_MODIFIED_304) {
            if(CloudFavoriteService.REQUEST_TYPE_DOWNLOAD == type) {
                uploadCloudFavoritesWithoutServerChanged();
            }else if(CloudFavoriteService.REQUEST_TYPE_UPLOAD == type) {
            }
        }
    }

    @Override
    public void onRequestSuccess() {
        int type = mLoader.getRequestType();
        MktLog.i(TAG,"=====sync favorite success type="+type);
        if(CloudFavoriteService.REQUEST_TYPE_DOWNLOAD == type) {
            uploadCloudFavoritesWithServerChanged(toPersonalFavorites(mLoader.getCloudFavorites()));
        }else if(CloudFavoriteService.REQUEST_TYPE_UPLOAD == type) {
            CurrentUserSettings.getSettings().setCurrentFavoriteOrderChanged(false);
            PersonalFavorites.deleteAndInsertFavoriteListFromDatabase(
                    PersonalFavorites.getFavoriteListFromDatabase(),
                    toPersonalFavorites(mLoader.getCloudFavorites()));
        }
    }

    private void downloadCloudFavorites() {
        mLoader = (CloudFavoriteService) ServiceFactory.getInstance().getService(CloudFavoriteService.class.getName());
        mLoader.setListener(this);
        mLoader.downloadCloudFavorites();
    }

    private void uploadCloudFavoritesWithoutServerChanged() {
        EngLog.d(TAG, "uploadCloudFavoritesWithoutServerChanged");
        List<PersonalFavorites.Favorite> localPersonalFavorites = PersonalFavorites.getFavoriteListFromDatabase();
        List<PersonalFavorites.Favorite> finalPersonalFavorites = new ArrayList<>();

        List<PersonalFavorites.Favorite> deletedFavorites = new ArrayList<>();
        List<PersonalFavorites.Favorite> needSyncFavorites = new ArrayList<>();
        List<PersonalFavorites.Favorite> cloudTempFavorites = new ArrayList<>();
        //find out changes
        for(PersonalFavorites.Favorite favorite : localPersonalFavorites) {
            if(RCMDataStore.CloudFavoriteSyncStatus.Deleted == favorite.getSyncStatus()) {
                deletedFavorites.add(favorite);
            } else if(RCMDataStore.CloudFavoriteSyncStatus.NeedSync == favorite.getSyncStatus()) {
                needSyncFavorites.add(favorite);
                finalPersonalFavorites.add(favorite);
            } else if(RCMDataStore.CloudFavoriteSyncStatus.CloudTemporary == favorite.getSyncStatus()) {
                cloudTempFavorites.add(favorite);
            } else if(RCMDataStore.CloudFavoriteSyncStatus.Synced == favorite.getSyncStatus()) {
                finalPersonalFavorites.add(favorite);
            }
        }

        if(!deletedFavorites.isEmpty() || !needSyncFavorites.isEmpty() || CurrentUserSettings.getSettings().getCurrentFavoriteOrderChanged() ) {
            //re-order
            int order = 1;
            for(PersonalFavorites.Favorite favorite : finalPersonalFavorites) {
                favorite.setOrder(order);
                order++;
            }

            String requestBody = PersonalFavorites.toCloudFavoriteRequestBody(PersonalFavorites.truncateIfExceedServerLimitation(finalPersonalFavorites));

            mLoader = (CloudFavoriteService) ServiceFactory.getInstance().getService(CloudFavoriteService.class.getName());
            mLoader.setListener(this);
            mLoader.uploadCloudFavorites(requestBody);
        } else {
            EngLog.d(TAG, "uploadCloudFavoritesWithoutServerChanged, nothing changed!");
        }
    }

    private void uploadCloudFavoritesWithServerChanged(List<PersonalFavorites.Favorite> serverPersonalFavorites) {
        EngLog.d(TAG, "uploadCloudFavoritesWithServerChanged");
        List<PersonalFavorites.Favorite> readOnlyLocalPersonalFavorites = PersonalFavorites.getFavoriteListFromDatabase();
        if(readOnlyLocalPersonalFavorites.isEmpty()) {
            PersonalFavorites.deleteAndInsertFavoriteListFromDatabase(null, serverPersonalFavorites);
            return;
        }

        List<PersonalFavorites.Favorite> finalPersonalFavorites = new ArrayList<>();
        List<PersonalFavorites.Favorite> deletedFavorites = new ArrayList<>();
        List<PersonalFavorites.Favorite> needSyncFavorites = new ArrayList<>();
        List<PersonalFavorites.Favorite> cloudTempFavorites = new ArrayList<>();
        //find out changes
        for(PersonalFavorites.Favorite favorite : readOnlyLocalPersonalFavorites) {
            if(RCMDataStore.CloudFavoriteSyncStatus.Deleted == favorite.getSyncStatus()) {
                deletedFavorites.add(favorite);
            } else if(RCMDataStore.CloudFavoriteSyncStatus.NeedSync == favorite.getSyncStatus()) {
                needSyncFavorites.add(favorite);
            } else if(RCMDataStore.CloudFavoriteSyncStatus.CloudTemporary == favorite.getSyncStatus()) {
                cloudTempFavorites.add(favorite);
            }
        }

        //if nothing changed in client side, no need upload
        if(deletedFavorites.isEmpty() && needSyncFavorites.isEmpty() && cloudTempFavorites.isEmpty() && !CurrentUserSettings.getSettings().getCurrentFavoriteOrderChanged()) {
            PersonalFavorites.deleteAndInsertFavoriteListFromDatabase(readOnlyLocalPersonalFavorites, serverPersonalFavorites);
            return;
        }

        //add personal favorites to hash map
        Map<Long, PersonalFavorites.Favorite> mapServerPersonalFavorites = new HashMap<>();
        for(PersonalFavorites.Favorite serverFavorite: serverPersonalFavorites) {
            mapServerPersonalFavorites.put(serverFavorite.getContactId(), serverFavorite);
        }

        //delete favorites
        for(PersonalFavorites.Favorite favorite : deletedFavorites) {
            PersonalFavorites.Favorite serverFavorite = mapServerPersonalFavorites.get(favorite.getContactId());
            if(serverFavorite != null) {
                serverPersonalFavorites.remove(serverFavorite);
            }
        }

        //add new favorite
        for(PersonalFavorites.Favorite favorite : needSyncFavorites) {
            if(!mapServerPersonalFavorites.containsKey(favorite.getContactId())) {
                serverPersonalFavorites.add(favorite);
            }
        }

        //re-order
        int order = 1;
        for(PersonalFavorites.Favorite favorite : serverPersonalFavorites) {
            favorite.setOrder(order);
            finalPersonalFavorites.add(favorite);
            order++;
        }

        //do uploading networking task
        String requestBody = PersonalFavorites.toCloudFavoriteRequestBody(PersonalFavorites.truncateIfExceedServerLimitation(serverPersonalFavorites));
        mLoader = (CloudFavoriteService) ServiceFactory.getInstance().getService(CloudFavoriteService.class.getName());
        mLoader.setListener(this);
        mLoader.uploadCloudFavorites(requestBody);
    }

    private List<PersonalFavorites.Favorite> toPersonalFavorites(List<CloudFavoriteContactInfo> cloudFavorites) {
        List<PersonalFavorites.Favorite> personalFavorites = new ArrayList<>();
        Long contactId;
        Contact.ContactType contactType;
        for(CloudFavoriteContactInfo cloudFavorite : cloudFavorites) {
            contactId = null;
            contactType = Contact.ContactType.UNKNOW;
            PersonalFavorites.Favorite personalFavorite = new PersonalFavorites.Favorite();
            personalFavorite.setOrder(cloudFavorite.id);
            personalFavorite.setSyncStatus(RCMDataStore.CloudFavoriteSyncStatus.Synced);

            if(cloudFavorite.contactId != null) {
                contactId = strToLong(cloudFavorite.contactId);
                contactType = Contact.ContactType.CLOUD_PERSONAL;
            }else if(cloudFavorite.extensionId != null) {
                contactId = strToLong(cloudFavorite.extensionId);
                contactType = Contact.ContactType.CLOUD_COMPANY;
            }

            if(contactId != null) {
                personalFavorite.setContactId(contactId.longValue());
                personalFavorite.setContactType(contactType);
                personalFavorites.add(personalFavorite);
            }
        }

        return personalFavorites;
    }

    private Long strToLong(String longValue) {
        try{
            return Long.valueOf(longValue);
        }catch (Throwable th) {
            MktLog.e(TAG, "strToLong failed: " + th.toString());
        }
        return null;
    }


}
