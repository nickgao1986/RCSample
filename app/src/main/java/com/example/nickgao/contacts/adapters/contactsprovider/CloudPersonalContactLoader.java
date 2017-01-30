package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.model.contact.Address;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by nick.gao on 1/29/17.
 */

public class CloudPersonalContactLoader extends ContactsLoader{

    private static final String TAG = "[RC]CloudPersonalContactLoader";
    private static String UTF8 = "UTF-8";
    private Map<Long, Long> mTempContactIds = new HashMap<>();

    public CloudPersonalContactLoader(Context context, ReadWriteLock readWriteLock) {
        super(context, readWriteLock);
    }

    @Override
    public void clear() {
        acquireWriteLock();
        try {
            mCacheContacts.clear();
            mCacheNumbers.clear();
            mTempContactIds.clear();
        }catch (Throwable th) {
            MktLog.e(TAG, "clear() error=" + th.toString());
        } finally {
            releaseWriteLock();
        }
    }
    /**
     * should be called in write lock
     */
    private void clearCache() {
        mCacheContacts.clear();
        mCacheNumbers.clear();
    }

    private static void loadAllContacts(final Context context, Map<Long, Contact> cacheContacts, Map<String, Long> cacheNumbers) {
        long time = System.currentTimeMillis();
        Map<Long, List<Contact.TypeAddress>> addresses = new HashMap<>();
        Map<Long, List<Contact.TypeValue>> emails = new HashMap<>();
        Map<Long, List<Contact.TypeValue>> e164PhoneNumbers = new HashMap<>();
        Map<Long, List<Contact.TypeValue>> originalPhoneNumbers = new HashMap<>();
        initAddressList(addresses);
        initEmails(emails);
        initPhoneNumbers(e164PhoneNumbers, originalPhoneNumbers, cacheNumbers);

        //to get the projection for db.query
        Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION[] values = Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }
        String where = RCMDataStore.PersonalContactsTable.SYNC_STATUS + " != ? ";
        String[] args = new String[] {String.valueOf(RCMDataStore.CloudContactSyncStatus.Deleted.ordinal())};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    where,
                    args,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return;
            }

            if (!cursor.moveToFirst()) {
                return;
            }

            do {
                CloudPersonalContact contact = getCloudPersonalContactFromCursor(cursor);
                long contactId = contact.getId();
                contact.setAddress(addresses.get(contactId));
                contact.setEmails(emails.get(contactId));
                contact.setOriginalPhoneNumbers(originalPhoneNumbers.get(contactId));
                contact.setE164PhoneNumbers(e164PhoneNumbers.get(contactId));
                cacheContacts.put(contactId, contact);
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }


        MktLog.d(TAG, "initAllContacts() spent=" + (System.currentTimeMillis() - time));
    }

    static void initAddressList(Map<Long, List<Contact.TypeAddress>> addresses) {
        long time = System.currentTimeMillis();
        Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION[] values = Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(
                    UriHelper.getUri(RCMProvider.PERSONAL_ADDRESS, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    null,
                    null,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return;
            }

            if (!cursor.moveToFirst()) {
                return;
            }

            do {
                Long contactId = cursor.getLong(Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.ID.ordinal());
                List<Contact.TypeAddress> list = addresses.get(contactId);
                if(list == null) {
                    list = new ArrayList<>();
                    addresses.put(contactId, list);
                }
                list.add(toAddress(cursor));
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        MktLog.d(TAG, "initAddressList() spent=" + (System.currentTimeMillis() - time));
    }

    static void initEmails(Map<Long, List<Contact.TypeValue>> emails) {
        long time = System.currentTimeMillis();
        Projections.PersonalCloudContacts.PERSONAL_EMAIL_INFO_PROJECTION[] values = Projections.PersonalCloudContacts.PERSONAL_EMAIL_INFO_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(
                    UriHelper.getUri(RCMProvider.PERSONAL_EMAIL, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    null,
                    null,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return;
            }

            if (!cursor.moveToFirst()) {
                return;
            }

            time = System.currentTimeMillis();

            do {
                Long contactId = cursor.getLong(Projections.PersonalCloudContacts.PERSONAL_EMAIL_INFO_PROJECTION.ID.ordinal());
                List<Contact.TypeValue> list = emails.get(contactId);
                if(list == null) {
                    list = new ArrayList<>();
                    emails.put(contactId, list);
                }
                list.add(toEmail(cursor));
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        MktLog.d(TAG, "initEmails() spent=" + (System.currentTimeMillis() - time));
    }

    static void initPhoneNumbers(Map<Long, List<Contact.TypeValue>> e164PhoneNumbers, Map<Long, List<Contact.TypeValue>> originalPhoneNumbers, Map<String, Long> cacheNumbers) {
        long time = System.currentTimeMillis();
        Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION[] values = Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }

        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(
                    UriHelper.getUri(RCMProvider.PERSONAL_PHONE_NUMBER, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    null,
                    null,
                    null);

            MktLog.d(TAG, "initPhoneNumbers() query spent=" + (System.currentTimeMillis() - time));
            if (cursor == null || cursor.getCount() <= 0) {
                return;
            }

            if (!cursor.moveToFirst()) {
                return;
            }

//            ContactPhoneParser phoneParser = new ContactPhoneParser();
            do {
                Long contactId = cursor.getLong(Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION.ID.ordinal());
                String number = cursor.getString(Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION.PHONE_NUMBER.ordinal());
                int type = cursor.getInt(Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION.PHONE_TYPE.ordinal());
                if (!TextUtils.isEmpty(number)) {
                    //save original numbers
                    {
                        Contact.TypeValue item = new Contact.TypeValue(type, number);
                        List<Contact.TypeValue> list = originalPhoneNumbers.get(contactId);
                        if (list == null) {
                            list = new ArrayList<>();
                            originalPhoneNumbers.put(contactId, list);
                        }
                        list.add(item);
                    }

//                    {
//                        String e164Number = phoneParser.getE164(number);
//                        if (!TextUtils.isEmpty(e164Number)) {
//                            cacheNumbers.put(e164Number, contactId);
//                            Contact.TypeValue item = new Contact.TypeValue(type, e164Number);
//                            List<Contact.TypeValue> list = e164PhoneNumbers.get(contactId);
//                            if (list == null) {
//                                list = new ArrayList<>();
//                                e164PhoneNumbers.put(contactId, list);
//                            }
//                            list.add(item);
//                        }
//                    }
                }
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        MktLog.d(TAG, "initPhoneNumbers() spent=" + (System.currentTimeMillis() - time));
    }

    static Contact.TypeAddress toAddress(Cursor cursor) {
        String country = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.ADDRESS_COUNTRY.ordinal()));
        String state = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.ADDRESS_STATE.ordinal()));
        String city = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.ADDRESS_CITY.ordinal()));
        String street = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.ADDRESS_STREET.ordinal()));
        String zip = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.ADDRESS_ZIP.ordinal()));
        int type = cursor.getInt(Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.ADDRESS_TYPE.ordinal());
        return new Contact.TypeAddress(type, new Address(country, state, city, street, zip));
    }

    static List<Contact.TypeAddress> getAddressListFromDB(long contactId) {
        List<Contact.TypeAddress> list = new ArrayList<>();
        Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION[] values = Projections.PersonalCloudContacts.PERSONAL_ADDRESS_INFO_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }
        String where = RCMDataStore.PersonalAddressTable.ID + " = ? ";
        String[] args = new String[] { String.valueOf(contactId)};
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(
                    UriHelper.getUri(RCMProvider.PERSONAL_ADDRESS, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    where,
                    args,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return list;
            }

            if (!cursor.moveToFirst()) {
                return list;
            }

            do {
                list.add(toAddress(cursor));
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    static Contact.TypeValue toEmail(Cursor cursor) {
        String email = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_EMAIL_INFO_PROJECTION.EMAIL.ordinal()));
        int type = cursor.getInt(Projections.PersonalCloudContacts.PERSONAL_EMAIL_INFO_PROJECTION.TYPE.ordinal());
        Contact.TypeValue item = new Contact.TypeValue(type, email);
        return item;
    }

    static List<Contact.TypeValue> getEmailsFromDB(long contactId) {
        List<Contact.TypeValue> list = new ArrayList<>();
        Projections.PersonalCloudContacts.PERSONAL_EMAIL_INFO_PROJECTION[] values = Projections.PersonalCloudContacts.PERSONAL_EMAIL_INFO_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }

        String where = RCMDataStore.PersonalEmailTable.ID + " = ? ";
        String[] args = new String[] { String.valueOf(contactId)};

        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(
                    UriHelper.getUri(RCMProvider.PERSONAL_EMAIL, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    where,
                    args,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return list;
            }

            if (!cursor.moveToFirst()) {
                return list;
            }


            do {
                list.add(toEmail(cursor));
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    static List<Contact.TypeValue> getPhoneNumbersFromDB(long contactId) {
        List<Contact.TypeValue> list = new ArrayList<>();
        Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION[] values = Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }
        String where = RCMDataStore.PersonalPhoneNumberTable.ID + " = ? ";
        String[] args = new String[] { String.valueOf(contactId)};
        Cursor cursor = null;
        try {
            cursor = RingCentralApp.getContextRC().getContentResolver().query(
                    UriHelper.getUri(RCMProvider.PERSONAL_PHONE_NUMBER, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    where,
                    args,
                    null);
            if (cursor == null || cursor.getCount() <= 0) {
                return list;
            }

            if (!cursor.moveToFirst()) {
                return list;
            }

            do {
                String number = cursor.getString(Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION.PHONE_NUMBER.ordinal());
                int type = cursor.getInt(Projections.PersonalCloudContacts.PERSONAL_NUMBER_INFO_PROJECTION.PHONE_TYPE.ordinal());
                if (!TextUtils.isEmpty(number)) {
                    Contact.TypeValue item = new Contact.TypeValue(type, number);
                    list.add(item);
                }
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        return list;
    }

    public long ensureContactId(long contactId) {
        if(CloudPersonalContact.isLocalContact(contactId)) {
            acquireReadLock();
            try {
                Long serverId = mTempContactIds.get(contactId);
                return (serverId != null) ? serverId : contactId;
            }finally {
                releaseReadLock();
            }
        }
        return contactId;
    }



    public static HashMap<Long, Long> loadContactIDs(final Context context, Uri uri, String[] projection, String where, String[] args) {
        HashMap<Long, Long> contactsIds = new HashMap<>();
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    where,
                    args,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return contactsIds;
            }

            if (!cursor.moveToFirst()) {
                return contactsIds;
            }

            do {
                long id = cursor.getLong(0);
                long contactId = cursor.getLong(1);
                contactsIds.put(contactId, id);
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return contactsIds;
    }



    /**
     * load ids map from whole table
     * @param context
     * @param uri
     * @param projection support two, id, _id
     * @return
     */
    public static HashMap<Long, List<String>> loadIds(final Context context, Uri uri, String[] projection) {
        HashMap<Long, List<String>> contactsIds = new HashMap<>();
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return contactsIds;
            }

            if (!cursor.moveToFirst()) {
                return contactsIds;
            }

            do {
                String id = String.valueOf(cursor.getLong(0));
                Long contactId = cursor.getLong(1);
                List<String> ids = contactsIds.get(contactId);
                if(ids == null) {
                    ids = new ArrayList<>();
                    contactsIds.put(contactId, ids);
                }
                ids.add(id);

            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return contactsIds;
    }

    static CloudPersonalContact getCloudPersonalContactFromCursor(Cursor cursor) {
        long contactId = cursor.getLong(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.ID.ordinal());
        String uri = cursor.getString(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.URI.ordinal());
        String availability = cursor.getString(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.AVAILABILITY.ordinal());
        String displayName = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.DISPLAY_NAME.ordinal()));
        String firstName = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.FIRST_NAME.ordinal()));
        String lastName = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.LAST_NAME.ordinal()));
        String middleName = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.MIDDLE_NAME.ordinal()));
        String nickName = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.NICK_NAME.ordinal()));
        String company = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.COMPANY.ordinal()));
        String jobTitle = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.JOB_TITLE.ordinal()));
        String birthday = cursor.getString(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.BIRTHDAY.ordinal());
        String webPage = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.WEB_PAGE.ordinal()));
        String notes = blobToString(cursor.getBlob(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.NOTES.ordinal()));
        int syncStatus = cursor.getInt(Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.SYNC_STATUS.ordinal());
        CloudPersonalContact contact = new CloudPersonalContact(contactId > 0?CloudPersonalContact.CloudContactType.SERVER : CloudPersonalContact.CloudContactType.LOCAL,
                contactId, displayName, firstName, middleName, lastName, nickName, company, jobTitle);
        contact.setUri(uri);
        contact.setAvailability(availability);
        contact.setBirthday(birthday);
        contact.addWebPage(webPage);
        contact.setNotes(notes);
        contact.setSyncStatus(syncStatus);
        return contact;
    }

    public static CloudPersonalContact getContactFromDB(long contactId) {
        final Context context = RingCentralApp.getContextRC();
        final ContentResolver cr = context.getContentResolver();
        CloudPersonalContact contact = null;

        //to get the projection for db.query
        Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION[] values = Projections.PersonalCloudContacts.PERSONAL_CONTACT_ITEM_PROJECTION.values();
        String[] projection = new String[values.length];
        for (int column = 0; column < values.length; column++) {
            projection[column] = values[column].toString();
        }
        String where = RCMDataStore.PersonalContactsTable.ID + " = ? AND (" + RCMDataStore.PersonalContactsTable.SYNC_STATUS + " = ? OR " + RCMDataStore.PersonalContactsTable.SYNC_STATUS + " = ? )";
        String[] args = new String[] {String.valueOf(contactId), String.valueOf(RCMDataStore.CloudContactSyncStatus.NeedSync.ordinal()), String.valueOf(RCMDataStore.CloudContactSyncStatus.Deleted.ordinal())};
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    where,
                    args,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return contact;
            }

            if (!cursor.moveToFirst()) {
                return contact;
            }

            do {
                contact = getCloudPersonalContactFromCursor(cursor);
                List<Contact.TypeAddress> addresses = getAddressListFromDB(contactId);
                List<Contact.TypeValue> emails = getEmailsFromDB(contactId);
                List<Contact.TypeValue> phoneNumbers = getPhoneNumbersFromDB(contactId);
                contact.setAddress(addresses);
                contact.setEmails(emails);
                contact.setOriginalPhoneNumbers(phoneNumbers);
                contact.setE164PhoneNumbers(phoneNumbers);
            } while (false);

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        return contact;
    }

    public static List<Long> loadAllModifiedContacts() {
        long time = System.currentTimeMillis();
        final Context context = RingCentralApp.getContextRC();
        final ContentResolver cr = context.getContentResolver();
        List<Long> list = new ArrayList<>();
        //to get the projection for db.query
        String[] projection = new String[] { RCMDataStore.PersonalContactsTable.ID};
        final int indexOfId = 0;
        String where = RCMDataStore.PersonalContactsTable.SYNC_STATUS + " = ? OR " + RCMDataStore.PersonalContactsTable.SYNC_STATUS + " = ? ";
        String[] args = new String[] {String.valueOf(RCMDataStore.CloudContactSyncStatus.NeedSync.ordinal()), String.valueOf(RCMDataStore.CloudContactSyncStatus.Deleted.ordinal())};
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    where,
                    args,
                    null);

            if (cursor == null || cursor.getCount() <= 0) {
                return list;
            }

            if (!cursor.moveToFirst()) {
                return list;
            }

            do {
                long contactId = cursor.getLong(indexOfId);
                list.add(contactId);
            } while (cursor.moveToNext());

        } catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        MktLog.d(TAG, "loadAllModifiedContacts() spent=" + (System.currentTimeMillis() - time));
        return list;
    }

    public static void deleteAllContactInDB() {
        try {
            final Context context = RingCentralApp.getContextRC();
            long mailBoxId = CurrentUserSettings.getSettings().getCurrentMailboxId();
            final Uri contactUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, mailBoxId);
            //load all contacts which are not local
            HashMap<Long, Long> contactIds = CloudPersonalContactLoader.loadContactIDs(context, contactUriWithId, new String[]{RCMDataStore.PersonalContactsTable._ID, RCMDataStore.PersonalContactsTable.ID},
                    RCMDataStore.PersonalContactsTable.ID + " != ?", new String[] {String.valueOf(0)});
            if (!contactIds.isEmpty()) {
                deleteContactsInDB(contactIds.keySet());
            }
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }
    }

    public static boolean isContactExistInDB(long contactId) {
        boolean ret = false;
        final Context context = RingCentralApp.getContextRC();
        final ContentResolver cr = context.getContentResolver();
        String[] projection = new String[] { RCMDataStore.PersonalContactsTable.ID };
        String where = RCMDataStore.PersonalContactsTable.ID + " = ? ";
        String[] args = new String[] {String.valueOf(contactId)};
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, CurrentUserSettings.getSettings().getCurrentMailboxId()),
                    projection,
                    where,
                    args,
                    null);
            if(cursor != null && cursor.getCount() > 0) {
                ret = true;
            }
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return ret;
    }

    public static boolean updateContactSyncStatusInDB(long contactId, RCMDataStore.CloudContactSyncStatus status) {
        try {

            final Context context = RingCentralApp.getContextRC();
            long mailBoxId = CurrentUserSettings.getSettings().getCurrentMailboxId();
            final ContentResolver cr = context.getContentResolver();
            final Uri contactUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, mailBoxId);
            ContentValues cv = new ContentValues();
            cv.put(RCMDataStore.PersonalContactsTable.SYNC_STATUS, status.ordinal());
            return cr.update(contactUriWithId, cv, RCMDataStore.PersonalContactsTable.ID + " = ? ", new String[]{String.valueOf(contactId)}) > 0;
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }
        return false;
    }

    public static boolean updateContactIdInDB(long contactId, long newContactId) {
        try {
            final Context context = RingCentralApp.getContextRC();
            long mailBoxId = CurrentUserSettings.getSettings().getCurrentMailboxId();
            final ContentResolver cr = context.getContentResolver();
            ArrayList<ContentProviderOperation> contactOps = new ArrayList<>();
            final Uri contactUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, mailBoxId);
            final Uri phoneNumberUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_PHONE_NUMBER, mailBoxId);
            final Uri emailUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_EMAIL, mailBoxId);
            final Uri addressUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_ADDRESS, mailBoxId);
            final String contactSelection = RCMDataStore.PersonalContactsTable.ID + "=?";
            final String phoneNumberSelection = RCMDataStore.PersonalPhoneNumberTable.ID + "=?";
            final String emailSelection = RCMDataStore.PersonalEmailTable.ID + "=?";
            final String addressSelection = RCMDataStore.PersonalAddressTable.ID + "=?";
            String[] args = new String[]{String.valueOf(contactId)};
            ContentValues cv = new ContentValues();
            cv.put(RCMDataStore.PersonalContactsTable.ID, newContactId);
            cv.put(RCMDataStore.PersonalContactsTable.SYNC_STATUS, RCMDataStore.CloudContactSyncStatus.Synced.ordinal());
            contactOps.add(ContentProviderOperation.newUpdate(contactUriWithId).withSelection(contactSelection, args)
                    .withValues(cv)
                    .build());

            cv = new ContentValues();
            cv.put(RCMDataStore.PersonalPhoneNumberTable.ID, newContactId);
            contactOps.add(ContentProviderOperation.newUpdate(phoneNumberUriWithId).withSelection(phoneNumberSelection, args)
                    .withValues(cv)
                    .build());

            cv = new ContentValues();
            cv.put(RCMDataStore.PersonalEmailTable.ID, newContactId);
            contactOps.add(ContentProviderOperation.newUpdate(emailUriWithId).withSelection(emailSelection, args)
                    .withValues(cv)
                    .build());

            cv = new ContentValues();
            cv.put(RCMDataStore.PersonalAddressTable.ID, newContactId);
            contactOps.add(ContentProviderOperation.newUpdate(addressUriWithId).withSelection(addressSelection, args)
                    .withValues(cv)
                    .build());

            cr.applyBatch(RCMProvider.AUTHORITY, contactOps);
            return true;
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }
        return false;
    }

    public static boolean deleteContactInDB(long contactId) {
        boolean ret = false;
        try {
            final Context context = RingCentralApp.getContextRC();
            final ContentResolver cr = context.getContentResolver();
            long mailBoxId = CurrentUserSettings.getSettings().getCurrentMailboxId();
            final Uri contactUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, mailBoxId);
            final Uri phoneNumberUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_PHONE_NUMBER, mailBoxId);
            final Uri emailUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_EMAIL, mailBoxId);
            final Uri addressUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_ADDRESS, mailBoxId);
            List<String> listContactIds = new ArrayList<>();
            List<String> listPhoneIds = new ArrayList<>();
            List<String> listEmailIds = new ArrayList<>();
            List<String> listAddressIds = new ArrayList<>();
            Cursor cursor = cr.query(contactUriWithId, new String[]{RCMDataStore.PersonalContactsTable._ID}, RCMDataStore.PersonalContactsTable.ID + " = ? ", new String[]{ String.valueOf(contactId)}, null);

            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    listContactIds.add(String.valueOf(cursor.getLong(0)));
                }while(cursor.moveToNext());
            }

            cursor = cr.query(phoneNumberUriWithId, new String[]{RCMDataStore.PersonalPhoneNumberTable._ID}, RCMDataStore.PersonalPhoneNumberTable.ID + " = ? ", new String[]{String.valueOf(contactId)}, null);
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do{
                    listPhoneIds.add(String.valueOf(cursor.getLong(0)));
                }while(cursor.moveToNext());
            }

            cursor = cr.query(emailUriWithId, new String[]{RCMDataStore.PersonalEmailTable._ID}, RCMDataStore.PersonalEmailTable.ID + " = ? ", new String[]{String.valueOf(contactId)}, null);
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do{
                    listEmailIds.add(String.valueOf(cursor.getLong(0)));
                }while(cursor.moveToNext());
            }

            cursor = cr.query(addressUriWithId, new String[]{RCMDataStore.PersonalAddressTable._ID}, RCMDataStore.PersonalAddressTable.ID + " = ? ", new String[]{String.valueOf(contactId)}, null);
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do{
                    listAddressIds.add(String.valueOf(cursor.getLong(0)));
                }while(cursor.moveToNext());
            }

            ret = deleteContactsInDB(listContactIds, listPhoneIds, listEmailIds, listAddressIds);
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }

        return ret;
    }


    public static boolean deleteContactsInDB(Collection<Long> contacts) {
        boolean ret = false;
        try {
            if(contacts.isEmpty()) {
                return true;
            }

            final Context context = RingCentralApp.getContextRC();

            long mailBoxId = CurrentUserSettings.getSettings().getCurrentMailboxId();
            final Uri contactUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, mailBoxId);
            final Uri phoneNumberUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_PHONE_NUMBER, mailBoxId);
            final Uri emailUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_EMAIL, mailBoxId);
            final Uri addressUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_ADDRESS, mailBoxId);

            HashMap<Long, Long> contactIds = CloudPersonalContactLoader.loadContactIDs(context, contactUriWithId, new String[]{RCMDataStore.PersonalContactsTable._ID, RCMDataStore.PersonalContactsTable.ID},
                    RCMDataStore.PersonalContactsTable.ID + " != ?", new String[] {String.valueOf(0)});
            HashMap<Long, List<String>> phoneIds = CloudPersonalContactLoader.loadIds(context, phoneNumberUriWithId, new String[]{RCMDataStore.PersonalPhoneNumberTable._ID, RCMDataStore.PersonalPhoneNumberTable.ID});
            HashMap<Long, List<String>> emailIds = CloudPersonalContactLoader.loadIds(context, emailUriWithId, new String[]{RCMDataStore.PersonalEmailTable._ID, RCMDataStore.PersonalEmailTable.ID});
            HashMap<Long, List<String>> addressIds = CloudPersonalContactLoader.loadIds(context, addressUriWithId, new String[]{RCMDataStore.PersonalAddressTable._ID, RCMDataStore.PersonalAddressTable.ID});

            List<String> listContactIds = new ArrayList<>();
            List<String> listPhoneIds = new ArrayList<>();
            List<String> listEmailIds = new ArrayList<>();
            List<String> listAddressIds = new ArrayList<>();
            List<String> _ids;
            Long _id;
            for (long contactId : contacts) {
                _id = contactIds.get(contactId);
                if (_id != null) {
                    listContactIds.add(String.valueOf(_id));
                }

                _ids = phoneIds.get(contactId);
                if (_ids != null && !_ids.isEmpty()) {
                    listPhoneIds.addAll(_ids);
                }

                _ids = emailIds.get(contactId);
                if (_ids != null && !_ids.isEmpty()) {
                    listEmailIds.addAll(_ids);
                }

                _ids = addressIds.get(contactId);
                if (_ids != null && !_ids.isEmpty()) {
                    listAddressIds.addAll(_ids);
                }
            }

            //clear data to save memory
            phoneIds.clear();
            emailIds.clear();
            addressIds.clear();

            ret = CloudPersonalContactLoader.deleteContactsInDB(listContactIds, listPhoneIds, listEmailIds, listAddressIds);
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }
        return ret;
    }

    protected static boolean deleteSingleContactInDB(long contactId) {
        boolean ret = false;
        try {
            final Context context = RingCentralApp.getContextRC();
            final ContentResolver cr = context.getContentResolver();
            long mailBoxId = CurrentUserSettings.getSettings().getCurrentMailboxId();
            ArrayList<ContentProviderOperation> contactOps = new ArrayList<>();

            final Uri contactUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS, mailBoxId);
            final Uri phoneNumberUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_PHONE_NUMBER, mailBoxId);
            final Uri emailUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_EMAIL, mailBoxId);
            final Uri addressUriWithId = UriHelper.getUri(RCMProvider.PERSONAL_ADDRESS, mailBoxId);

            final String contactSelection = RCMDataStore.PersonalContactsTable.ID + "=?";
            final String phoneNumberSelection = RCMDataStore.PersonalPhoneNumberTable.ID + "=?";
            final String emailSelection = RCMDataStore.PersonalEmailTable.ID + "=?";
            final String addressSelection = RCMDataStore.PersonalAddressTable.ID + "=?";
            String[] args = new String[]{String.valueOf(contactId)};

            contactOps.add(ContentProviderOperation.newDelete(contactUriWithId).withSelection(contactSelection, args)
                    .build());
            contactOps.add(ContentProviderOperation.newDelete(phoneNumberUriWithId).withSelection(phoneNumberSelection, args)
                    .build());
            contactOps.add(ContentProviderOperation.newDelete(emailUriWithId).withSelection(emailSelection, args)
                    .build());
            contactOps.add(ContentProviderOperation.newDelete(addressUriWithId).withSelection(addressSelection, args)
                    .build());

            cr.applyBatch(RCMProvider.AUTHORITY, contactOps);
            ret = true;
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }
        return ret;
    }

    private static boolean deleteContactsInDB(Collection<String> listContactIds, Collection<String> listPhoneIds, Collection<String> listEmailIds, Collection<String> listAddressIds) {
        boolean ret = false;
        try {
            final Context context = RingCentralApp.getContextRC();
            final ContentResolver cr = context.getContentResolver();
            final String contactSelection = RCMDataStore.PersonalContactsTable._ID + "=?";
            final String phoneNumberSelection = RCMDataStore.PersonalPhoneNumberTable._ID + "=?";
            final String emailSelection = RCMDataStore.PersonalEmailTable._ID + "=?";
            final String addressSelection = RCMDataStore.PersonalAddressTable._ID + "=?";
            if (!listContactIds.isEmpty()) {
                cr.delete(UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS_ISYNC_DELETE), contactSelection, listContactIds.toArray(new String[0]));
            }

            if (!listPhoneIds.isEmpty()) {
                cr.delete(UriHelper.getUri(RCMProvider.PERSONAL_PHONE_NUMBER_ISYNC_DELETE), phoneNumberSelection, listPhoneIds.toArray(new String[0]));
            }

            if (!listEmailIds.isEmpty()) {
                cr.delete(UriHelper.getUri(RCMProvider.PERSONAL_EMAIL_ISYNC_DELETE), emailSelection, listEmailIds.toArray(new String[0]));
            }

            if (!listAddressIds.isEmpty()) {
                cr.delete(UriHelper.getUri(RCMProvider.PERSONAL_ADDRESS_ISYNC_DELETE), addressSelection, listAddressIds.toArray(new String[0]));
            }
            ret = true;
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }
        return ret;
    }

    public static boolean updateContactInDB(Contact contact) {
        //remove from database
        if(deleteSingleContactInDB(contact.getId())) {
            List<Contact> contacts = new ArrayList<>();
            contacts.add(contact);
            //add to database
            try {
                Map<Long, Contact> added = addContactsToDB(contacts, RCMDataStore.CloudContactSyncStatus.NeedSync);
                return !added.isEmpty();
            }catch (Throwable th) {
                MktLog.e(TAG, "updateContactInDB error=" + th.toString());
            }
        }
        return false;
    }

    public static boolean addContactToDB(Contact contact) {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        try {
            Map<Long, Contact> added = addContactsToDB(contacts, RCMDataStore.CloudContactSyncStatus.NeedSync);
            return !added.isEmpty();
        }catch (Throwable th) {
            MktLog.e(TAG, "addContactToDB error=" + th.toString());
        }
        return false;
    }

    public static Map<Long, Contact> addContactsToDB(List<Contact> contacts, RCMDataStore.CloudContactSyncStatus syncStatus) throws ExecutionException {
        HashMap<Long, Contact> addedContacts = new HashMap<>();
        final ContentResolver cr = RingCentralApp.getContextRC().getContentResolver();
        long mailBoxId = CurrentUserSettings.getSettings().getCurrentMailboxId();
        Uri contactUri = UriHelper.getUri(RCMProvider.PERSONAL_CONTACTS);
        Uri phoneNumberUri = UriHelper.getUri(RCMProvider.PERSONAL_PHONE_NUMBER);
        Uri emailUri = UriHelper.getUri(RCMProvider.PERSONAL_EMAIL);
        Uri addressUri = UriHelper.getUri(RCMProvider.PERSONAL_ADDRESS);

        //bulkInsert items
        List<ContentValues> contactList = new ArrayList<>();
        List<ContentValues> phoneNumberList = new ArrayList<>();
        List<ContentValues> emailList = new ArrayList<>();
        List<ContentValues> addressList = new ArrayList<>();

        CloudPersonalContact personalContact;
        for (Contact contact : contacts) {
            personalContact = (CloudPersonalContact) contact;
            addedContacts.put(personalContact.getId(), personalContact);
            translatePersonalContact(contactList, personalContact, syncStatus, mailBoxId);
            translatePersonalPhoneNumber(phoneNumberList, personalContact.getOriginalPhoneNumbers(), personalContact.getId(), mailBoxId);
            translatePersonalEmail(emailList, personalContact.getEmails(), personalContact.getId(), mailBoxId);
            translatePersonalAddress(addressList, personalContact, mailBoxId);
        }

        if (!contactList.isEmpty()) {
            bulkInsert(cr, contactUri, contactList);
            contactList.clear();
        }

        if (!phoneNumberList.isEmpty()) {
            bulkInsert(cr, phoneNumberUri, phoneNumberList);
            phoneNumberList.clear();
        }

        if (!emailList.isEmpty()) {
            bulkInsert(cr, emailUri, emailList);
            emailList.clear();
        }

        if (!addressList.isEmpty()) {
            bulkInsert(cr, addressUri, addressList);
            addressList.clear();
        }
        return addedContacts;
    }

    public static void bulkInsert(ContentResolver cr, Uri uri, List<ContentValues> dataList) {
        int length = dataList.size();
        try {
            ContentValues[] array = new ContentValues[length];
            for(int i= 0 ; i < length; ++i) {
                array[i] = dataList.get(i);
            }
            cr.bulkInsert(uri, array);
        }catch (OutOfMemoryError th) {
            MktLog.e(TAG, "bulkInsert: error" + th.toString());
            System.gc();
            final int max = 20;
            int start = 0;
            int perLength;
            List<ContentValues> perItems;
            while(start < length){
                perItems = dataList.subList(start, (start + max < length)? max : length - start);
                perLength = perItems.size();
                ContentValues[] array = new ContentValues[perLength];
                for(int i= 0 ; i < length; ++i) {
                    array[i] = perItems.get(i);
                }
                start += max;
                cr.bulkInsert(uri, array);
            }
        }
    }


    protected static void translatePersonalContact(List<ContentValues> list, CloudPersonalContact personalContact, RCMDataStore.CloudContactSyncStatus syncStatus, long mailboxId){
        ContentValues item = translatePersonalContact(personalContact,syncStatus, mailboxId);
        list.add(item);
    }

    protected static ContentValues translatePersonalContact(CloudPersonalContact personalContact, RCMDataStore.CloudContactSyncStatus syncStatus, long mailboxId){
        ContentValues item = new ContentValues();
        item.put(RCMDataStore.PersonalContactsTable.MAILBOX_ID, mailboxId);
        item.put(RCMDataStore.PersonalContactsTable.ID, personalContact.getId());
        item.put(RCMDataStore.PersonalContactsTable.URI, personalContact.getUri());
        item.put(RCMDataStore.PersonalContactsTable.AVAILABILITY, personalContact.getAvailability());
        item.put(RCMDataStore.PersonalContactsTable.DISPLAY_NAME, stringToBlob(personalContact.getDisplayName()));
        item.put(RCMDataStore.PersonalContactsTable.FIRST_NAME, stringToBlob(personalContact.getFirstName()));
        item.put(RCMDataStore.PersonalContactsTable.LAST_NAME, stringToBlob(personalContact.getLastName()));
        item.put(RCMDataStore.PersonalContactsTable.MIDDLE_NAME, stringToBlob(personalContact.getMiddleName()));
        item.put(RCMDataStore.PersonalContactsTable.NICK_NAME, stringToBlob(personalContact.getNickName()));
        item.put(RCMDataStore.PersonalContactsTable.COMPANY, stringToBlob(personalContact.getCompany()));
        item.put(RCMDataStore.PersonalContactsTable.JOB_TITLE, stringToBlob(personalContact.getJobTitle()));
        item.put(RCMDataStore.PersonalContactsTable.BIRTHDAY, personalContact.getBirthday());
        item.put(RCMDataStore.PersonalContactsTable.WEB_PAGE, stringToBlob(personalContact.getWebPage(0)));
        item.put(RCMDataStore.PersonalContactsTable.NOTES, stringToBlob(personalContact.getNotes()));
        item.put(RCMDataStore.PersonalContactsTable.SYNC_STATUS, syncStatus.ordinal());
        return item;
    }

    protected static void translatePersonalPhoneNumber(List<ContentValues> list, List<Contact.TypeValue> phoneNumbers, long contactId, long mailboxId){
        if(!phoneNumbers.isEmpty()) {
            for(Contact.TypeValue phone : phoneNumbers) {
                ContentValues item = new ContentValues();
                item.put(RCMDataStore.PersonalPhoneNumberTable.MAILBOX_ID, mailboxId);
                item.put(RCMDataStore.PersonalPhoneNumberTable.ID, contactId);
                item.put(RCMDataStore.PersonalPhoneNumberTable.PHONE_NUMBER, phone.getValue());
                item.put(RCMDataStore.PersonalPhoneNumberTable.PHONE_TYPE, phone.getType());
                list.add(item);
            }
        }
    }

    protected static void translatePersonalEmail(List<ContentValues> list,  List<Contact.TypeValue> emails, long contactId, long mailboxId){
        if(!emails.isEmpty()) {
            for(Contact.TypeValue email : emails) {
                ContentValues item = new ContentValues();
                item.put(RCMDataStore.PersonalEmailTable.MAILBOX_ID, mailboxId);
                item.put(RCMDataStore.PersonalEmailTable.ID, contactId);
                item.put(RCMDataStore.PersonalEmailTable.EMAIL, stringToBlob(email.getValue()));
                item.put(RCMDataStore.PersonalEmailTable.TYPE, email.getType());
                list.add(item);
            }
        }
    }

    protected static void translatePersonalAddress(List<ContentValues> list, CloudPersonalContact personalContact, long mailboxId){
        ContentValues item;
        List<Contact.TypeAddress> addressList = personalContact.getAddresses();
        for(Contact.TypeAddress typeAddress : addressList) {
            Address address = typeAddress.getValue();
            item = new ContentValues();
            item.put(RCMDataStore.PersonalAddressTable.MAILBOX_ID, mailboxId);
            item.put(RCMDataStore.PersonalAddressTable.ID, personalContact.getId());
            item.put(RCMDataStore.PersonalAddressTable.ADDRESS_CITY, stringToBlob(address.getCity()));
            item.put(RCMDataStore.PersonalAddressTable.ADDRESS_COUNTRY, stringToBlob(address.getCountry()));
            item.put(RCMDataStore.PersonalAddressTable.ADDRESS_STATE, stringToBlob(address.getState()));
            item.put(RCMDataStore.PersonalAddressTable.ADDRESS_STREET, stringToBlob(address.getStreet()));
            item.put(RCMDataStore.PersonalAddressTable.ADDRESS_ZIP, stringToBlob(address.getZip()));
            item.put(RCMDataStore.PersonalAddressTable.ADDRESS_TYPE, typeAddress.getType());
            list.add(item);
        }
    }

    private static byte[] stringToBlob(String value) {
        try {
            return TextUtils.isEmpty(value) ? null : value.getBytes(UTF8);
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }
        return null;
    }

    private static String blobToString(byte[] value) {
        try {
            return value == null ? null : new String(value, UTF8);
        }catch (Throwable th) {
            MktLog.e(TAG, th.toString());
        }
        return null;
    }


    @Override
    protected Contact removeCacheContact(Long contactId) {
        return null;
    }

    @Override
    public void reloadContacts() {
        Map<Long, Contact> cacheContacts = new HashMap<>();
        Map<String, Long> cacheNumbers = new HashMap<>();
        loadAllContacts(getContext(), cacheContacts, cacheNumbers);
        acquireWriteLock();
        try {
            clearCache();
            mCacheContacts.putAll(cacheContacts);
            cacheContacts.clear();
            mCacheNumbers.putAll(cacheNumbers);
            cacheNumbers.clear();
        } catch (Throwable th) {
            MktLog.e(TAG, "startLoadContacts error=" + th.toString());
        } finally {
            MktLog.d(TAG, "startLoadContacts() end: size=" + mCacheContacts.size());
            releaseWriteLock();
        }
    }

    @Override
    public List<Contact> loadContacts(Contact.TypeValue[] lowerCaseFilter, boolean isFuzzySearch, boolean includeExtension, String countryCode, String nationalPrefix) {
        long time = System.currentTimeMillis();
        ArrayList<Contact> resultContacts = new ArrayList<>();
        acquireReadLock();
        try {
            Iterator item = mCacheContacts.entrySet().iterator();
            Contact contact;
            while (item.hasNext()) {
                Map.Entry pair = (Map.Entry)item.next();
                contact = (Contact)pair.getValue();
                if ( (lowerCaseFilter == null) || contact.isMatched(lowerCaseFilter, includeExtension, isFuzzySearch, null, countryCode, nationalPrefix)) {
                    resultContacts.add(contact);
                }
            }
        }finally {
            releaseReadLock();
        }

        MktLog.d(TAG, "loadContacts() loaded: size= " + resultContacts.size() + " spent=" + (System.currentTimeMillis() - time));
        return resultContacts;
    }

    @Override
    public List<ContactMatchInfo> loadContactsWithMatchInfo(Contact.TypeValue[] lowerCaseFilter, boolean isFuzzySearch, boolean includeExtension, String countryCode, String nationalPrefix) {
        long time = System.currentTimeMillis();
        ArrayList<ContactMatchInfo> resultContacts = new ArrayList<>();
        acquireReadLock();
        try {
            Iterator item = mCacheContacts.entrySet().iterator();
            Contact contact;
            while (item.hasNext()) {
                Map.Entry pair = (Map.Entry)item.next();
                contact = (Contact)pair.getValue();
                List<Contact.MatchInfo> matchList = new ArrayList<>();
                if ( (lowerCaseFilter == null) || contact.isMatched(lowerCaseFilter, includeExtension, isFuzzySearch, matchList, countryCode, nationalPrefix)) {
                    resultContacts.add(new ContactMatchInfo(contact, matchList));
                }
            }
        } finally {
            releaseReadLock();
        }

        MktLog.d(TAG, "loadContactsWithMatchInfo() loaded: " + resultContacts.size() + " spent=" + (System.currentTimeMillis() - time));
        return resultContacts;
    }

    public void deleteContactInCache(long contactId) {
        long id = contactId;
        //try to delete the server id
        if(CloudPersonalContact.isLocalContact(contactId)) {
            acquireReadLock();
            try {
                Long serverContactId = mTempContactIds.get(contactId);
                if(serverContactId != null) {
                    id = serverContactId;
                }
            } finally {
                releaseReadLock();
            }
        }

        //double check, make sure delete cache contact
        if(removeCacheContact(id) == null && id != contactId) {
            removeCacheContact(contactId);
        }
    }

    public void addContactInCache(Contact contact) {
        Map<Long, Contact> contacts = new HashMap<>();
        contacts.put(contact.getId(), contact);
        bulkInsert(contacts, false);
    }

    public void updateContactInCache(Contact contact) {
        removeCacheContact(contact.getId());
        addContactInCache(contact);
    }

    public void bulkInsert(Map<Long, Contact> contactsMap, boolean reset) {
        acquireWriteLock();
        try {
            if(reset) {
                //clear
                clearCache();
            }

            //insert into cache
            mCacheContacts.putAll(contactsMap);

            List<Contact.TypeValue> phoneList;
            CloudPersonalContact contact;
            //need to add insert contacts into phone number cache.
            Iterator item = contactsMap.entrySet().iterator();
            while (item.hasNext()) {
                Map.Entry pair = (Map.Entry)item.next();
                contact = (CloudPersonalContact)pair.getValue();
                phoneList = contact.getE164PhoneNumbers();
                if(phoneList != null) {
                    for(Contact.TypeValue typeValue : phoneList) {
                        mCacheNumbers.put(typeValue.getValue(), contact.getId());
                    }
                }
            }
        } catch (Throwable th) {
            MktLog.e(TAG, "bulkInsert error=" + th.toString());
        }finally {
            releaseWriteLock();
        }
    }

}
