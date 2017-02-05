package com.example.nickgao.contacts.adapters.contactsprovider;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.nickgao.androidsample11.ContactsUtils;
import com.example.nickgao.contacts.CloudFavoriteServiceLoader;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.network.RestApiErrorCodes;
import com.example.nickgao.service.RestRequestListener;
import com.example.nickgao.service.ServiceFactory;
import com.example.nickgao.service.request.RcRestRequest;

import org.apache.http.HttpStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nick.gao on 1/30/17.
 */

public class CloudContactSyncService extends Service implements ChangeContactService.ContactSyncListener, RestRequestListener {


    public static final String TAG = "[RC]CloudContactSyncService";
    public static final String ACTION_EXTRA_CMD = "CMD";
    public static final int CONTACT_LOCAL_SYNC_TO_SERVER = 101;
    public static final int CONTACT_SERVER_SYNC_TO_LOCAL = 102;
    public static final int FAVORITE_SYNC = 103;

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    Queue<Long> mNeedSyncContacts = new LinkedList<>();
    Queue<Integer> mSyncCommands = new LinkedList<>();
    boolean mIsNeedToDoISync = false;

    public static void sendCommand(final Context context, int cmd) {
        Intent intent = new Intent(context, CloudContactSyncService.class);
        intent.putExtra(ACTION_EXTRA_CMD, cmd);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LogSettings.MARKET) {
            MktLog.i(TAG, "onCreate()");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            int cmd = intent.getIntExtra(ACTION_EXTRA_CMD, CONTACT_LOCAL_SYNC_TO_SERVER);
            onSyncHandle(cmd);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void onSyncHandle(final int cmd) {
        MktLog.d(TAG, "onSyncHandle");
        switch (cmd) {
            case CONTACT_LOCAL_SYNC_TO_SERVER:
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mSyncCommands.offer(cmd);
                        onSyncContact();
                    }
                });

                break;
            case CONTACT_SERVER_SYNC_TO_LOCAL:
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mIsNeedToDoISync = true;
                        mSyncCommands.offer(CONTACT_LOCAL_SYNC_TO_SERVER);
                        onSyncContact();
                    }
                });
                break;
            case FAVORITE_SYNC:
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        onSyncFavorites();
                        MktLog.i(TAG,"====sync favorite");
                    }
                });
                break;
        }
    }

    private void onSyncFavorites() {
        CloudFavoriteServiceLoader.getLoader().sync();
    }


    private void onSyncContact() {

        MktLog.d(TAG, "onSyncContact");
        if(mNeedSyncContacts.isEmpty()) {

            List<Long> needSyncContacts = CloudPersonalContactLoader.loadAllModifiedContacts();
            MktLog.d(TAG, "onSyncContact contact size=" + needSyncContacts.size());
            for(Long contactId : needSyncContacts) {
                if(!mNeedSyncContacts.contains(contactId)) {
                    mNeedSyncContacts.offer(contactId);
                }
            }

            if(mNeedSyncContacts.isEmpty()) {
                onContactSyncCallback();
            }else{
                Long nextSyncContact = mNeedSyncContacts.peek();
                while (!syncContactRequest(nextSyncContact)) {
                    mNeedSyncContacts.poll();
                    if(mNeedSyncContacts.isEmpty()) {
                        onContactSyncCallback();
                        break;
                    }

                    nextSyncContact = mNeedSyncContacts.peek();
                }
            }
        }

    }

    private void onContactSyncCallback() {
        if(!mNeedSyncContacts.isEmpty()) {
            mNeedSyncContacts.poll();
            if(!mNeedSyncContacts.isEmpty()) {
                Long nextSyncContact = mNeedSyncContacts.peek();
                while(!syncContactRequest(nextSyncContact)) {
                    mNeedSyncContacts.poll();
                    if(mNeedSyncContacts.isEmpty()) {
                        onContactSyncCallback();
                        break;
                    }
                    nextSyncContact = mNeedSyncContacts.peek();
                }
            }else {
                onContactSyncCallback();
            }

        }else {
            if(!mSyncCommands.isEmpty()) {
                mSyncCommands.poll();
                List<Long> needSyncContacts = CloudPersonalContactLoader.loadAllModifiedContacts();
                for(Long contactId : needSyncContacts) {
                    if(!mNeedSyncContacts.contains(contactId)) {
                        mNeedSyncContacts.offer(contactId);
                    }
                }

                if(mNeedSyncContacts.isEmpty()) {
                    onContactSyncCallback();
                }else {
                    Long nextSyncContact = mNeedSyncContacts.peek();
                    while(!syncContactRequest(nextSyncContact)) {
                        mNeedSyncContacts.poll();
                        if(mNeedSyncContacts.isEmpty()) {
                            onContactSyncCallback();
                            break;
                        }
                        nextSyncContact = mNeedSyncContacts.peek();
                    }
                }
            }

            if(mIsNeedToDoISync) {
                mIsNeedToDoISync = false;
                CloudPersonalContactsServiceLoader.getLoader().updatePersonalContacts(CloudContactSyncService.this, null);
            }
        }
    }


    private boolean syncContactRequest(Long contactId) {
        MktLog.d(TAG, "sync request id="+ contactId);

        if(contactId == null) {
            return false;
        }

        CloudPersonalContact contact = CloudPersonalContactLoader.getContactFromDB(contactId);

        if(contact == null) {
            return false;
        }

        if(contact.getSyncStatus() == RCMDataStore.CloudContactSyncStatus.Deleted.ordinal()) {
            if(CloudPersonalContact.isLocalContact(contactId)) {
                CloudPersonalContactLoader.deleteContactInDB(contactId);
                return false;
            }{
                MktLog.i(TAG,"===sync contact delete");
                doContactSyncRequest(RequestInfoStorage.REST_DELETE_CONTACT, contactId, "");
            }


        }else{
            CloudPersonalContactInfo contactInfo = ContactsUtils.translateToCloudPersonalContactInfo(contact);
            if(CloudPersonalContact.isLocalContact(contactId)) {
                MktLog.d(TAG, "sync request create");
                doContactSyncRequest(RequestInfoStorage.REST_CREATE_SINGLE_CONTACT, contactId, contactInfo.toJson());
            }else{
                MktLog.d(TAG, "sync request update");
                doContactSyncRequest(RequestInfoStorage.REST_UPDATE_CONTACT, contactId, contactInfo.toJson());
            }
        }
        return true;
    }


    private void doContactSyncRequest(int requestType, long id, String requestBody) {
        ChangeContactService contactService = (ChangeContactService) ServiceFactory.getInstance().getService(ChangeContactService.class.getName());
        contactService.setContactSyncListener(this);
        contactService.updateData(requestType, id, requestBody);
    }

    private void deleteContact(long contactId) {
        CloudPersonalContactLoader.deleteContactInDB(contactId);
        ContactsProvider.getInstance().deleteContactInCache(contactId, Contact.ContactType.CLOUD_PERSONAL);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onRequestSuccess() {

    }

    @Override
    public void onRequestFailure(int errorCode) {

    }

    @Override
    public void onContactSyncSuccess(int requestType, long contactId, CloudPersonalContactInfo response) {
        switch (requestType) {
            case RequestInfoStorage.REST_CREATE_SINGLE_CONTACT:
                if(CloudPersonalContactLoader.isContactExistInDB(response.id)) {
                    deleteContact(contactId);
                   // PersonalFavorites.convertLocalCloudContactToSyncedCloudFavorite(contactId, response.id, getApplicationContext());
                }else {
                    MktLog.d(TAG, "create contact server returns: local id=" + contactId +" server id=" + response.id);
                    if(CloudPersonalContactLoader.updateContactIdInDB(contactId, response.id)) {
                        ContactsProvider.getInstance().replaceWithServerContactId(contactId, response.id);
                     //   PersonalFavorites.convertLocalCloudContactToSyncedCloudFavorite(contactId, response.id, getApplicationContext());
                    }
                }
                break;

            case RequestInfoStorage.REST_UPDATE_CONTACT:
                CloudPersonalContactLoader.updateContactSyncStatusInDB(contactId, RCMDataStore.CloudContactSyncStatus.Synced);
                break;

            case RequestInfoStorage.REST_DELETE_CONTACT:
                deleteContact(contactId);
                break;
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                onContactSyncCallback();
            }
        });
    }

    @Override
    public void onContactSyncFailure(int requestType, long contactId, RcRestRequest<CloudPersonalContactInfo> request, int error) {
        if(RestApiErrorCodes.NETWORK_NOT_AVAILABLE != error
                && RestApiErrorCodes.NETWORK_NOT_AVAILABLE_AIRPLANE_ON != error
                && RestApiErrorCodes.CLIENT_INVALID_ERROR != error
                ) {
            if (request != null) {
                int httpCode = request.getHttpCode();
                switch (requestType) {
                    case RequestInfoStorage.REST_CREATE_SINGLE_CONTACT:
                        if (httpCode == RestApiErrorCodes.SC_BAD_REQUEST_400) {
                            MktLog.i(TAG, "onContactSyncFailure create contact has invalid parameter" );
                            //Has invalid parameter, update the sync status
                            CloudPersonalContactLoader.updateContactSyncStatusInDB(contactId, RCMDataStore.CloudContactSyncStatus.SyncFailed);
                        }
                        break;

                    case RequestInfoStorage.REST_UPDATE_CONTACT:
                        if (httpCode == HttpStatus.SC_NOT_FOUND) {
                            MktLog.i(TAG, "onContactSyncFailure update contact id not found" );
                            //Contact has deleted, please delete in db and cache
                            deleteContact(contactId);
                        } else if (httpCode == RestApiErrorCodes.SC_BAD_REQUEST_400) {
                            MktLog.i(TAG, "onContactSyncFailure update contact has invalid parameter" );
                            //Change the sync status
                            CloudPersonalContactLoader.updateContactSyncStatusInDB(contactId, RCMDataStore.CloudContactSyncStatus.SyncFailed);
                        }
                        break;

                    case RequestInfoStorage.REST_DELETE_CONTACT:
                        if (httpCode == HttpStatus.SC_NOT_FOUND) {
                            MktLog.i(TAG, "onSyncFailure delete contact id not found" );
                            //Contact has deleted, please delete in db and cache
                            deleteContact(contactId);
                        } else if (httpCode == RestApiErrorCodes.SC_BAD_REQUEST_400) {
                            MktLog.i(TAG, "onSyncFailure delete contact has invalid parameter" );
                            //Change the sync status
                            CloudPersonalContactLoader.updateContactSyncStatusInDB(contactId, RCMDataStore.CloudContactSyncStatus.SyncFailed);
                        }
                        break;
                }
            }
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                onContactSyncCallback();
            }
        });
    }
}
