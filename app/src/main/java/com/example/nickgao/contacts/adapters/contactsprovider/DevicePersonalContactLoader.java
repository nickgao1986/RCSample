package com.example.nickgao.contacts.adapters.contactsprovider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import com.example.nickgao.service.model.Address;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.example.nickgao.R;
import com.example.nickgao.database.RCMProviderHelper;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.RCMConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by nick.gao on 1/28/17.
 */

public class DevicePersonalContactLoader extends ContactsLoader {


    static String TAG = "[RC]DevicePersonalContactLoader";

    private volatile boolean mPermission = false;
    private volatile boolean mIsNeedToReload = false;

    static final String[] CONTACT_PROJECTION = new String[]{
            ContactsContract.Data._ID, // 0
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.RAW_CONTACT_ID,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.PHOTO_ID,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.DATA2,
            ContactsContract.Data.DATA3,
            ContactsContract.Data.DATA4,
            ContactsContract.Data.DATA5,
            ContactsContract.Data.DATA6,
            ContactsContract.Data.DATA7,
            ContactsContract.Data.DATA8,
            ContactsContract.Data.DATA9,
            ContactsContract.Data.DATA10,
            ContactsContract.Data.DATA14,
            ContactsContract.Data.DATA15,
    };

    public DevicePersonalContactLoader(final Context context, ReadWriteLock readWriteLock) {
        super(context, readWriteLock);
        mPermission = getDeviceContactsPermission();
    }

    protected boolean getDeviceContactsPermission() {
        return getDeviceContactsPermission(getContext());
    }

    public static boolean getDeviceContactsPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void reloadContacts() {
        MktLog.d(TAG, "reloadContacts");
        try {
            //contacts permission check
            boolean loadDeviceContacts = getDeviceContactsPermission();
            if (!loadDeviceContacts) {
                return;
            }

            Map<Long, Contact> cacheContacts = new HashMap<>();
            Map<String, Long> cacheNumbers = new HashMap<>();

            try {
                loadAllContacts(getContext(), cacheContacts, cacheNumbers);
                //generate display name,sim hash
                MktLog.d(TAG, "reloadContacts, start generating sim hash");
                Iterator item = cacheContacts.entrySet().iterator();
                DeviceContact contact;
                while (item.hasNext()) {
                    Map.Entry pair = (Map.Entry) item.next();
                    contact = (DeviceContact) pair.getValue();
                    contact.formatDisplayName();
                }
                MktLog.d(TAG, "reloadContacts, finish generating sim hash");
            } catch (Throwable th) {
                MktLog.e(TAG, "reloadContacts error=" + th.toString());
            }

            mCacheContacts.clear();
            mCacheNumbers.clear();
            mCacheContacts.putAll(cacheContacts);
            cacheContacts.clear();
            mCacheNumbers.putAll(cacheNumbers);
            cacheNumbers.clear();
        } catch (Throwable th) {
            MktLog.e(TAG, "reloadContacts error=" + th.toString());
        } finally {
            MktLog.d(TAG, "reloadContacts() end size=" + mCacheContacts.size());
            releaseWriteLock();
        }
    }


    @Override
    public List<Contact> loadContacts(Contact.TypeValue[] lowerCaseFilter, boolean isFuzzySearch, boolean includeExtension, String countryCode, String nationalPrefix) {
        long time = System.currentTimeMillis();
        ArrayList<Contact> resultContacts = new ArrayList<>();
        final Context context = getContext();

        acquireReadLock();
        try {
            Iterator item = mCacheContacts.entrySet().iterator();
            DeviceContact contact;
            while (item.hasNext()) {
                Map.Entry pair = (Map.Entry) item.next();
                contact = (DeviceContact) pair.getValue();


                if ((!contact.isDuplicate()) && ((lowerCaseFilter == null) || contact.isMatched(lowerCaseFilter, includeExtension, isFuzzySearch, null, countryCode, nationalPrefix))) {
                    resultContacts.add(contact);
                }
            }
        } catch (Throwable th) {
            MktLog.e(TAG, "loadContacts error=" + th.toString());
        } finally {
            releaseReadLock();
        }

        MktLog.d(TAG, "loadContacts() loaded: size=" + resultContacts.size() + " spent=" + (System.currentTimeMillis() - time));

        return resultContacts;
    }

    @Override
    public List<ContactMatchInfo> loadContactsWithMatchInfo(Contact.TypeValue[] lowerCaseFilter, boolean isFuzzySearch, boolean includeExtension, String countryCode, String nationalPrefix) {
        long time = System.currentTimeMillis();
        ArrayList<ContactMatchInfo> resultContacts = new ArrayList<>();

        acquireReadLock();
        try {
            Iterator item = mCacheContacts.entrySet().iterator();
            DeviceContact contact;
            while (item.hasNext()) {
                Map.Entry pair = (Map.Entry) item.next();
                contact = (DeviceContact) pair.getValue();
                List<Contact.MatchInfo> matchList = new ArrayList<>();
                if ((!contact.isDuplicate()) && ((lowerCaseFilter == null) || contact.isMatched(lowerCaseFilter, includeExtension, isFuzzySearch, matchList, countryCode, nationalPrefix))) {
                    resultContacts.add(new ContactMatchInfo(contact, matchList));
                }
            }
        } catch (Throwable th) {
            MktLog.e(TAG, "loadContactsWithMatchInfo error=" + th.toString());
        } finally {
            releaseReadLock();
        }

        MktLog.d(TAG, "loadContactsWithMatchInfo() loaded: size=" + resultContacts.size() + " spent=" + (System.currentTimeMillis() - time));
        return resultContacts;
    }


    /**********************************help methods ******************************************************************/


    /**
     * @param phoneNumber
     * @return normalized phone number
     * @brief normalize phone number to skip ' ' and translate a~z A~Z to digit.
     */
    public static String normalizeNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return "";
        }

        phoneNumber = phoneNumber.trim();

        StringBuilder sb = new StringBuilder();
        int len = phoneNumber.length();
        if (len == 0) {
            return "";
        }

        char c;
        for (int i = 0; i < len; i++) {
            c = phoneNumber.charAt(i);
            // Character.digit() supports ASCII and Unicode digits (fullwidth, Arabic-Indic, etc.)
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (i == 0 && c == '+') {
                sb.append(c);
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                return normalizeNumber(PhoneNumberUtils.convertKeypadLettersToDigits(phoneNumber));
            }
        }

        return sb.toString();
    }

    private Set<Long> getRawContactIdsFilteredByGroups(String groupFilter) {
        Context context = getContext();
        Set<Long> ret = new HashSet<>();
        //contacts permission check
        boolean loadDeviceContacts = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED);
        if (!loadDeviceContacts) {
            return ret;
        }

        Cursor cursor = getContext().getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                groupFilter,
                null,
                getSortOrder(ContactsContract.Contacts.DISPLAY_NAME));
        if (cursor == null) {
            return ret;
        }

        if (cursor != null && cursor.moveToNext()) {
            int column = cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
            if (column != -1) {
                MktLog.e(TAG, "saveCurrentList column == -1");
                do {
                    ret.add(cursor.getLong(column));
                } while (cursor.moveToNext());
            }
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        return ret;
    }


    /**
     * get all contacts in Contact table
     *
     * @return The list of all contacts, also phone numbers
     */
    private static void loadAllContacts(final Context context, Map<Long, Contact> cacheContacts, Map<String, Long> cacheNumbers) {
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    CONTACT_PROJECTION,
                    null,
                    null,
                    getSortOrder(ContactsContract.Contacts.DISPLAY_NAME));
            if (cursor == null || cursor.getCount() == 0) {
                return;
            }

            if (!cursor.moveToFirst()) {
                return;
            }

            do {
                long rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                String mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                DeviceContact contact = (DeviceContact) cacheContacts.get(contactId);
                if (contact == null) {
                    contact = new DeviceContact(rawContactId, contactId);
                    cacheContacts.put(contactId, contact);
                }

                if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    //first name, middle name
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                    String firstName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                    String middleName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                    contact.setNames(displayName, firstName, middleName, lastName);
                } else if (ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    //nick name
                    String nickName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                    contact.setNickName(nickName);
                } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    //phone
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int phoneType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    String normalizeNumber = normalizeNumber(number).trim();
                    if (!TextUtils.isEmpty(normalizeNumber) && normalizeNumber.length() > 0) {
                        //e164Number
                        String e164Number = normalizeNumber;
                        contact.addE164PhoneNumber(new Contact.TypeValue(phoneType, e164Number));
                        contact.addOriginalPhoneNumber(new Contact.TypeValue(phoneType, normalizeNumber));
                        cacheNumbers.put(e164Number, contact.getContactId());
                    }
                } else if (ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    //company title
                    String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                    String jobTitle = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    contact.setCompany(company);
                    contact.setJobTitle(jobTitle);
                } else if (ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    //photo
                    long photoId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_ID));
                    long photoFileId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_FILE_ID));
                    contact.setPhotoId(photoId);
                    contact.setPhotoFileId(photoFileId);
                } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    int emailType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                    String emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    if (!TextUtils.isEmpty(emailAddress)) {
                        String trimEmail = emailAddress.trim();
                        if (trimEmail.length() > 0) {
                            contact.addDeviceEmail(new Contact.TypeValue(emailType, trimEmail));
                        }
                    }
                } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                    String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                    String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                    String country = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                    String state = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                    String zip = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                    Address address = new Address(country, state, city, street, zip);
                    contact.addAddress(new Contact.TypeAddress(type, address));
                } else if (ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    String webPage = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.DATA));
                    contact.addWebPage(webPage);
                } else if (ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE));
                    if (ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY == type) {
                        String birthday = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.DATA));
                        if (!TextUtils.isEmpty(birthday)) {
                            contact.setBirthday(birthday);
                        }
                    }
                } else if (ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE.equals(mimeType)) {
                    String notes = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    if (!TextUtils.isEmpty(notes)) {
                        contact.setNotes(notes);
                    }
                }
            } while (cursor.moveToNext());
        } catch (Throwable th) {
            MktLog.e(TAG, "getAllContacts() error=" + th.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }


    @Override
    public void clear() {

    }

    @Override
    protected Contact removeCacheContact(Long contactId) {
        return null;
    }
}
