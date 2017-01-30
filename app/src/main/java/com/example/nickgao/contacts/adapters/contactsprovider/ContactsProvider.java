package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.utils.RCMConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by nick.gao on 1/29/17.
 */

public class ContactsProvider {


    private static final String TAG = "[RC]ContactsProvider";

    private static ContactsProvider instance;
    private static long sMailBoxId = -1;
    private Context mContext;

    protected ReadWriteLock mPersonalContactReadWriteLock = new ReentrantReadWriteLock();
    protected ReadWriteLock mCompanyContactReadWriteLock = new ReentrantReadWriteLock();

    private CloudCompanyContactLoader mCompanyContactsLoader;
    private DevicePersonalContactLoader mDeviceContactsLoader;
    private CloudPersonalContactLoader mPersonalContactsLoader;

    private boolean mHasStarted = false;

    //use single thread pool to load contacts
    static volatile ExecutorService sPersonalContactService = Executors.newSingleThreadExecutor();
    static volatile ExecutorService sCompanyContactService = Executors.newSingleThreadExecutor();


    private ContentObserver mCompanyExtensionObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            MktLog.i(TAG,"====extension change");
            onCompanyContactsChanged();
        }
    };

    public static void onContactChangedForUIUpdate() {
        RingCentralApp.getContextRC().sendBroadcast(new Intent(RCMConstants.ACTION_UI_CONTACT_CHANGED));
    }

    public ContactsProvider(final Context context){
        MktLog.d(TAG, "ContactsProvider()");
        mContext = context;

        if (context == null){
            throw new IllegalArgumentException("context");
        }
        mCompanyContactsLoader = new CloudCompanyContactLoader(context, mCompanyContactReadWriteLock);

        mPersonalContactsLoader = new CloudPersonalContactLoader(context, mPersonalContactReadWriteLock);

        mDeviceContactsLoader = new DevicePersonalContactLoader(context, mPersonalContactReadWriteLock);

        register();
    }

    @Override
    protected void finalize() throws Throwable {
        unregister();
        super.finalize();
    }

    /**
     * init function should be called once
     * @param context
     */
    public static void init(final Context context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            MktLog.w(TAG, "ContactsProvider init in non-ui thread");
        }
        instance = new ContactsProvider(context);
    }

    public static ContactsProvider getInstance() {
        return instance;
    }

    public long ensureCloudContactId(long contactId) {
        return mPersonalContactsLoader.ensureContactId(contactId);
    }

    public List<Contact> loadContacts(boolean includeDevice, boolean includeCompany, boolean includePersonal, boolean includeExtension, boolean isFuzzy, String filter){
        String searchValue = (filter == null || filter.trim().length() == 0)? null: filter.trim();
        String[] lowerCaseFilter = null;
        Contact.TypeValue[] search = null;
        if(!TextUtils.isEmpty(searchValue)) {
            lowerCaseFilter = searchValue.toLowerCase().split(Contact.SPLIT_MODE);
        }

        if(lowerCaseFilter != null && lowerCaseFilter.length > 0) {
            search = new Contact.TypeValue[lowerCaseFilter.length];
            for (int i= 0; i< lowerCaseFilter.length; i++) {
                search[i] = new Contact.TypeValue(isPhoneNumber(lowerCaseFilter[i])?Contact.FILTER_TYPE_NUMBER : Contact.FILTER_TYPE_STR, lowerCaseFilter[i]);
            }
        }

        String nationalPrefix = "";
        String countryCode = "+" + "";

        List<Contact> contacts = new ArrayList<>();

        List<Contact> personalContacts;
        //cloud personal contacts
        if (includePersonal) {
            personalContacts = mPersonalContactsLoader.loadContacts(search, isFuzzy, includeExtension, countryCode, nationalPrefix);
            contacts.addAll(personalContacts);
            MktLog.d(TAG, "Personal Contacts. Loaded " + personalContacts.size());
        }

        //device contacts
        if (includeDevice) {
            List<Contact> deviceContacts = mDeviceContactsLoader.loadContacts(search, isFuzzy, includeExtension, countryCode, nationalPrefix);
            contacts.addAll(deviceContacts);
            MktLog.d(TAG, "Device Contacts. Loaded " + deviceContacts.size());
        }

        //company contacts
        if (includeCompany) {
            List<Contact> companyContacts = mCompanyContactsLoader.loadContacts(search, isFuzzy, includeExtension, countryCode, nationalPrefix);
            contacts.addAll(companyContacts);
            MktLog.d(TAG, "Company Contacts. Loaded " + companyContacts.size());
        }

        Collections.sort(contacts);

        return contacts;
    }

    public static boolean isPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }

        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            // Character.digit() supports ASCII and Unicode digits (fullwidth, Arabic-Indic, etc.)
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                continue;
            } else if (i == 0 && c == '+') {
                continue;
            } else if(c == ' ' || c == ')' || c == '(' || c == '-') {
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }


    /**
     * start to load contacts, callee should provide mailboxId to access database
     * @param mailBoxId
     */
    public void start(long mailBoxId) {
        start(mailBoxId, null, 0l);
    }

    /**
     * start to load contacts, callee should provide mailboxId to access database
     * @param mailBoxId
     */
    public void start(long mailBoxId, Handler uiHandler, long delay) {
        synchronized (this) {
            if (mailBoxId <= 0 || mailBoxId == sMailBoxId) {
                MktLog.d(TAG, "start to load contacts with old account id=" + sMailBoxId + "; new account id=" + mailBoxId);
                return;
            }
        }

        clear();

        synchronized (this) {
            mHasStarted = true;
            sMailBoxId = mailBoxId;
        }

        MktLog.d(TAG, "start to load contacts with account id=" + mailBoxId);

        if(uiHandler != null) {
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadContacts();
                }
            }, delay);
        }else {
            loadContacts();
        }


    }

    private void clear() {
        synchronized (this) {
            MktLog.d(TAG, "clear() with mailboxId=" + sMailBoxId);
            mHasStarted = false;
        }
        mDeviceContactsLoader.clear();
        mPersonalContactsLoader.clear();
        mCompanyContactsLoader.clear();
    }



    private void loadContacts() {
        sPersonalContactService.execute(new Runnable() {
            @Override
            public void run() {
                mPersonalContactsLoader.reloadContacts();
               // onRemoveDuplicateContact();
            }
        });

        sPersonalContactService.execute(new Runnable() {
            @Override
            public void run() {
                mDeviceContactsLoader.reloadContacts();
               // onRemoveDuplicateContact();
            }
        });

        sCompanyContactService.execute(new Runnable() {
            @Override
            public void run() {
                mCompanyContactsLoader.reloadContacts();
                onContactChangedForUIUpdate();
            }
        });
    }


    private void register() {
        try {
            mContext.getContentResolver().registerContentObserver(ContactsContract.Data.CONTENT_URI, false, mDeviceContactObserver);
        }catch (Throwable th) {
            MktLog.e(TAG, "ContactsProvider(), register device contacts observer, error=" + th.toString());
        }

        try{
            mContext.getContentResolver().registerContentObserver(UriHelper.getUri(RCMProvider.EXTENSIONS),
                    true,
                    mCompanyExtensionObserver);
        }catch (Throwable th) {
            MktLog.e(TAG, "ContactsProvider register company contacts observer, error=" + th.toString());
        }


    }

    private void unregister() {
        try{
            if(mDeviceContactObserver != null) {
                mContext.getContentResolver().unregisterContentObserver(mDeviceContactObserver);
            }
        } catch (Throwable th) {
            MktLog.e(TAG, "finalize(), unregister device contacts observer, error=" + th.toString());
        }

        try{
            if(mCompanyExtensionObserver != null) {
                mContext.getContentResolver().unregisterContentObserver(mCompanyExtensionObserver);
            }
        } catch (Throwable th) {
            MktLog.e(TAG, "finalize(), unregister company contacts observer, error=" + th.toString());
        }


    }


    protected void onCompanyContactsChanged() {
        synchronized (this) {
            if(!mHasStarted) {
                return;
            }
        }

        sCompanyContactService.execute(new Runnable() {
            @Override
            public void run() {
                mCompanyContactsLoader.reloadContacts();
                onContactChangedForUIUpdate();
            }
        });
    }

    public void onDeviceContactsChanged() {
        try{
            MktLog.d(TAG, "device contact changed, need to reload later!");
            if(mDeviceContactsLoader != null) {
                mDeviceContactsLoader.setIfNeedToReload(true);
            }
        }catch (Throwable th) {
            MktLog.e(TAG, "device contact observer, error=" + th.toString());
        }
    }


    private ContentObserver mDeviceContactObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onDeviceContactsChanged();
        }
    };


    public Contact getContact(Contact.ContactType type, long contactId, boolean copy) {
        try {
            switch (type) {
                case CLOUD_PERSONAL:
                    return mPersonalContactsLoader.getContact(contactId, copy);
                case DEVICE:
                    return mDeviceContactsLoader.getContact(Long.valueOf(contactId), copy);
                case CLOUD_COMPANY:
                    return mCompanyContactsLoader.getContact(Long.valueOf(contactId), copy);
                default:
                    return null;
            }
        }catch (Throwable th) {
            MktLog.e(TAG, "getContact, error=" + th.toString());
        }
        return null;
    }

    public void deleteContactInCache(final long contactId, Contact.ContactType contactType) {
        switch (contactType) {
            case CLOUD_PERSONAL:
                mPersonalContactsLoader.deleteContactInCache(contactId);
                break;
            case DEVICE:
                //not implement
                break;
            case CLOUD_COMPANY:
                //not implement
                break;
            default:
                break;
        }
    }

    public void addContact(final Contact contact) {
        switch (contact.getType()) {
            case CLOUD_PERSONAL:
                //add contact to database
                if(CloudPersonalContactLoader.addContactToDB(contact)) {
                    //add contact to cache
                    mPersonalContactsLoader.addContactInCache(contact);
                  //  onRemoveDuplicateContact();
                }
                //send sync cmd
                CloudContactSyncService.sendCommand(mContext, CloudContactSyncService.CONTACT_LOCAL_SYNC_TO_SERVER);
                break;
            case DEVICE:
                //not implement
                break;
            case CLOUD_COMPANY:
                //not implement
                break;
            default:
                break;
        }
    }

    public void updateContact(final Contact contact) {
        switch (contact.getType()) {
            case CLOUD_PERSONAL:
                if(CloudPersonalContactLoader.updateContactInDB(contact)) {
                    mPersonalContactsLoader.updateContactInCache(contact);
//                    onRemoveDuplicateContact();
                }

                CloudContactSyncService.sendCommand(mContext, CloudContactSyncService.CONTACT_LOCAL_SYNC_TO_SERVER);
                break;
            case DEVICE:
                //not implement
                break;
            case CLOUD_COMPANY:
                //not implement
                break;
            default:
                break;
        }
    }

}
