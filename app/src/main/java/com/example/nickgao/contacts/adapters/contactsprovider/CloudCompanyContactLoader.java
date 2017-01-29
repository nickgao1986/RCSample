package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.service.model.extensioninfo.ProfileImage;
import com.example.nickgao.service.model.extensioninfo.ProfileUri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by nick.gao on 1/28/17.
 */

public class CloudCompanyContactLoader extends ContactsLoader {

    private static final String TAG = "[RC]CloudCompanyContactLoader";
    private static boolean mIsNeedHidePagingContact = false;
    private HashMap<Long, List<CompanyContact.DirectNumber>> mCacheDirectNumbers = new HashMap<>();
    private List<Contact> mCachePagingOnlyContacts = new ArrayList<>();

    public CloudCompanyContactLoader(Context context, ReadWriteLock readWriteLock) {
        super(context, readWriteLock);
    }

    public static void setShowPagingContactFlag(boolean isNeedHidePagingContact) {
        mIsNeedHidePagingContact = isNeedHidePagingContact;
    }

    public static String getCompanyContactMailAddress(Context context, long contactId) {
        String emailAddress = null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    UriHelper.getUri(RCMProvider.EXTENSIONS, CurrentUserSettings.getSettings(context).getCurrentMailboxId()),
                    new String[]{ RCMDataStore.ExtensionsTable.JEDI_EMAIL }, COMPANY_SELECTION_CONTACT_ID, new String[]{String.valueOf(contactId)}, null);

            if (cursor == null || cursor.getCount() <= 0) {
                return emailAddress;
            }

            if(!cursor.moveToFirst()) {
                return emailAddress;
            }

            emailAddress = cursor.getString(0);

        } catch (Throwable e) {
            MktLog.e(TAG, "Search In Extension precisely failed : ", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return emailAddress;
    }

    @Override
    protected Contact removeCacheContact(Long contactId) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void reloadContacts() {
        long time = System.currentTimeMillis();
        //load contacts
        Map<Long, Contact> cacheContacts = new HashMap<>();
        Map<String, Long> cacheNumbers = new HashMap<>();
        loadAllContacts(getContext(), cacheContacts, cacheNumbers);

        acquireWriteLock();
        try{
            mCacheContacts.clear();
            mCacheNumbers.clear();
            mCacheContacts.putAll(cacheContacts);
            //clear it to save memory
            cacheContacts.clear();

            mCacheNumbers.putAll(cacheNumbers);
            //clear it to save memory
            cacheNumbers.clear();

        } catch (Throwable th) {
            MktLog.e(TAG, "startLoadContacts error=" + th.toString());
        } finally  {
            MktLog.d(TAG, "startLoadContacts() size=" + mCacheContacts.size() + " spent=" + (System.currentTimeMillis() - time));
            releaseWriteLock();
        }
    }

    @Override
    public List<Contact> loadContacts(Contact.TypeValue[] lowerCaseFilter, boolean isFuzzySearch, boolean includeExtension, String countryCode, String nationalPrefix) {
        long time = System.currentTimeMillis();
        List<Contact> resultContacts = new ArrayList<>();
        acquireReadLock();
        try {
            Iterator item = mCacheContacts.entrySet().iterator();
            CompanyContact contact;
            while (item.hasNext()) {
                Map.Entry pair = (Map.Entry)item.next();
                contact = (CompanyContact)pair.getValue();

                if(!contact.isVisible()) {
                    continue;
                }
                if ((lowerCaseFilter == null) || contact.isMatched(lowerCaseFilter, includeExtension, isFuzzySearch, null, countryCode,  nationalPrefix)) {
                    resultContacts.add(contact);
                }
            }
        } catch (Throwable th){
            MktLog.e(TAG, "loadContacts error=" + th.toString());
        }finally {
            releaseReadLock();
        }

        MktLog.d(TAG, "loadContacts() loaded: size= " + resultContacts.size() + " spent=" + (System.currentTimeMillis() - time));
        return resultContacts;
    }

    @Override
    public List<ContactMatchInfo> loadContactsWithMatchInfo(Contact.TypeValue[] lowerCaseFilter, boolean isFuzzySearch, boolean includeExtension, String countryCode, String nationalPrefix) {
        long time = System.currentTimeMillis();
        List<ContactMatchInfo> resultContacts = new ArrayList<>();

        acquireReadLock();
        try {
            Iterator item = mCacheContacts.entrySet().iterator();
            CompanyContact contact;
            while (item.hasNext()) {
                Map.Entry pair = (Map.Entry)item.next();
                contact = (CompanyContact)pair.getValue();
                List<Contact.MatchInfo> matchList = new ArrayList<>();
                if(!contact.isVisible()) {
                    continue;
                }
                if ((lowerCaseFilter == null) || contact.isMatched(lowerCaseFilter, includeExtension, isFuzzySearch, matchList, countryCode, nationalPrefix)) {
                    resultContacts.add(new ContactMatchInfo(contact, matchList));
                }
            }
        } catch (Throwable th) {
            MktLog.e(TAG, "loadContactsWithMatchInfo error=" + th.toString());
        }finally {
            releaseReadLock();
        }

        MktLog.d(TAG, "loadContactsWithMatchInfo() loaded: size=" + resultContacts.size() + " spent=" + (System.currentTimeMillis() - time));
        return resultContacts;
    }



    private static void loadAllContacts(final Context context, Map<Long, Contact> cacheContacts, Map<String, Long> cacheNumbers) {
        MktLog.i(TAG,"====loadCloudCompany contact");
        Cursor cursor = null;
        EXTENSION_PROJECTION[] values = EXTENSION_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }

        try {
            cursor = context.getContentResolver().query(
                    UriHelper.getUri(RCMProvider.EXTENSIONS, CurrentUserSettings.getSettings(context).getCurrentMailboxId()),
                    projection, null, null, getSortOrder(RCMDataStore.ExtensionsTable.RCM_DISPLAY_NAME));

            if (cursor == null || cursor.getCount() <= 0) {
                return;
            }

            while (cursor.moveToNext()) {
                long id = cursor.getLong(EXTENSION_PROJECTION.JEDI_MAILBOX_ID_EXT.ordinal());
                String firstName = cursor.getString(EXTENSION_PROJECTION.JEDI_FIRST_NAME.ordinal());
                String middleName = cursor.getString(EXTENSION_PROJECTION.JEDI_MIDDLE_NAME.ordinal());
                String lastName = cursor.getString(EXTENSION_PROJECTION.JEDI_LAST_NAME.ordinal());
                String name = cursor.getString(EXTENSION_PROJECTION.RCM_DISPLAY_NAME.ordinal());
                String pin = cursor.getString(EXTENSION_PROJECTION.JEDI_PIN.ordinal());
                String mobilePhone = cursor.getString(EXTENSION_PROJECTION.JEDI_MOBILE_PHONE.ordinal());
                String email = cursor.getString(EXTENSION_PROJECTION.JEDI_EMAIL.ordinal());
                String restType = cursor.getString(EXTENSION_PROJECTION.REST_TYPE.ordinal());
                String eTag = cursor.getString(EXTENSION_PROJECTION.RCM_PROFILE_ETAG.ordinal());
                String url = cursor.getString(EXTENSION_PROJECTION.RCM_PROFILE.ordinal());
                ProfileUri smallUri = new ProfileUri();
                smallUri.setUri(cursor.getString(EXTENSION_PROJECTION.PROFILE_IMAGE_SMALL_URI.ordinal()));
                ProfileUri mediumUri = new ProfileUri();
                mediumUri.setUri(cursor.getString(EXTENSION_PROJECTION.PROFILE_IMAGE_MEDIUM_URI.ordinal()));
                ProfileUri largeUri = new ProfileUri();
                largeUri.setUri(cursor.getString(EXTENSION_PROJECTION.PROFILE_IMAGE_LARGE_URI.ordinal()));
                CompanyContact contact = new CompanyContact(id, name, pin, mobilePhone, email, restType);
                contact.setFirstName(firstName);
                contact.setMiddleName(middleName);
                contact.setLastName(lastName);
                if(RCMDataStore.ExtensionsTable.UER_TYPE_PARK_LOCATION.equals(restType) ||
                        RCMDataStore.ExtensionsTable.USER_TYPE_IVR.equals(restType)) {
                    contact.setVisible(false);
                }
                ProfileImage profileImage = new ProfileImage();
                profileImage.setEtag(eTag);
                profileImage.setUri(url);
                ProfileUri[] uris = new ProfileUri[] {smallUri, mediumUri, largeUri};
                profileImage.setScales(uris);
                contact.setProfileImage(profileImage);
                contact.setEtag(eTag);
                //will load it later when match
                cacheContacts.put(id, contact);
                if(!TextUtils.isEmpty(pin)) {
                    cacheNumbers.put(pin, contact.getId());
                }

                if(!TextUtils.isEmpty(mobilePhone)) {
                    cacheNumbers.put(mobilePhone, contact.getId());
                }
            }
        } catch (Throwable e) {
            MktLog.e(TAG, "loadAllContacts failed : ", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        MktLog.i(TAG,"====loadCloudCompany cacheContacts="+cacheContacts);

    }


    static final String COMPANY_SELECTION_CONTACT_ID =
            "("+ RCMDataStore.ExtensionsTable.JEDI_MAILBOX_ID_EXT + " = ? )"
                    + " AND "+ RCMDataStore.ExtensionsTable.REST_TYPE+"!='"+ RCMDataStore.ExtensionsTable.UER_TYPE_PARK_LOCATION+"'"
                    + " AND "+ RCMDataStore.ExtensionsTable.REST_TYPE+"!='"+ RCMDataStore.ExtensionsTable.USER_TYPE_IVR+"'"
            ;

    /**
     * Extension projection.
     */
    enum EXTENSION_PROJECTION {
        ID {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable._ID;
            }
        },

        JEDI_FIRST_NAME {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.JEDI_FIRST_NAME;
            }
        },

        JEDI_MIDDLE_NAME {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.JEDI_MIDDLE_NAME;
            }
        },

        JEDI_LAST_NAME {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.JEDI_LAST_NAME;
            }
        },

        RCM_DISPLAY_NAME {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.RCM_DISPLAY_NAME;
            }
        },
        JEDI_PIN {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.JEDI_PIN;
            }
        },
        JEDI_CONTACT_PHONE {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.JEDI_CONTACT_PHONE;
            }
        },
        JEDI_MOBILE_PHONE {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.JEDI_MOBILE_PHONE;
            }
        },
        JEDI_MAILBOX_ID_EXT {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.JEDI_MAILBOX_ID_EXT;
            }
        },
        JEDI_EMAIL {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.JEDI_EMAIL;
            }
        },
        REST_TYPE {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.REST_TYPE;
            }
        },
        RCM_STARRED {
            @Override
            public String toString() { return RCMDataStore.ExtensionsTable.RCM_STARRED; }
        },
        RCM_PROFILE {
            @Override
            public String toString() { return RCMDataStore.ExtensionsTable.PROFILE_IMAGE; }
        },
        RCM_PROFILE_ETAG {
            @Override
            public String toString() { return RCMDataStore.ExtensionsTable.PROFILE_IMAGE_ETAG; }
        },
        PROFILE_IMAGE_SMALL_URI {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.PROFILE_IMAGE_SMALL_URI;
            }
        },
        PROFILE_IMAGE_MEDIUM_URI {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.PROFILE_IMAGE_MEDIUM_URI;
            }
        },
        PROFILE_IMAGE_LARGE_URI {
            @Override
            public String toString() {
                return RCMDataStore.ExtensionsTable.PROFILE_IMAGE_LARGE_URI;
            }
        }
    }


}
