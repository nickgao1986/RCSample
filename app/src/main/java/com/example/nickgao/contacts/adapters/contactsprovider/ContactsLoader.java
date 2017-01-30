package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by nick.gao on 1/28/17.
 */

public abstract class ContactsLoader {

    private Context mContext;
    protected HashMap<Long, Contact> mCacheContacts = new HashMap<>();
    protected HashMap<String, Long> mCacheNumbers = new HashMap<>();


    private Lock mReadLock = null;
    private Lock mWriteLock = null;

    protected void acquireReadLock() {
        mReadLock.lock();
    }

    protected void releaseReadLock() {
        mReadLock.unlock();
    }

    protected void acquireWriteLock() {
        mWriteLock.lock();
    }

    protected void releaseWriteLock() {
        mWriteLock.unlock();
    }

    //protected volatile boolean
    public static class ContactMatchInfo implements Comparable<ContactMatchInfo>  {
        public ContactMatchInfo(Contact contact, List<Contact.MatchInfo> matchList) {
            this.contact = contact;
            this.matchList = matchList;
        }
        public Contact contact;
        public List<Contact.MatchInfo> matchList;

        @Override
        public int compareTo(ContactMatchInfo item){
            return ContactsSorting.sorting(item.contact.getDisplayName(), item.contact.getDisplayName(), true);
        }
    }

    public ContactsLoader(final Context context, ReadWriteLock readWriteLock) {
        mReadLock = readWriteLock.readLock();
        mWriteLock = readWriteLock.writeLock();
        mContext = context;
        if (context == null){
            throw new IllegalArgumentException("context");
        }
    }

    protected Context getContext() {
        return mContext;
    }

    public static String getSortOrder(String fieldName) {
        return "CASE WHEN substr(UPPER(" + fieldName + "), 1, 1) BETWEEN 'A' AND 'Z' THEN 1 else 10 END," +
                " UPPER(" + fieldName + ") COLLATE LOCALIZED ASC";
    }

    public void releaseCache() {
        acquireWriteLock();
        try{
            mCacheContacts.clear();
            mCacheNumbers.clear();
        }finally {
            releaseWriteLock();
        }
    }

    public Contact getContact(String e164Number, boolean extension, Contact.TypeValue matchValue, boolean copy) {
        Contact result = null;
        acquireReadLock();
        try {
            //MktLog.d(this.getClass().getSimpleName(), "getContact e164number=" + e164Number + " cacheContactSize=" + mCacheContacts.size() + " cacheNumberSize="+ mCacheNumbers.size());
            Long objectId = mCacheNumbers.get(e164Number);
            if(objectId != null) {
                result = getContactById(objectId, copy);
                if(result != null) {
                    result = result.isFullE164NumberMatched(e164Number, extension,matchValue)? result : null;
                }
            }
        }finally {
            releaseReadLock();
        }
        return result;
    }

    public Contact getContact(Long id, boolean copy) {
        Contact result = null;
        acquireReadLock();
        try{
            result = getContactById(id, copy);
        }finally {
            releaseReadLock();
        }

        return result;
    }

    protected Contact getContactById(Long id, boolean copy) {
        Contact result = null;
        Contact temp = mCacheContacts.get(id);
        if (copy && temp != null) {
            switch (temp.getType()) {
                case CLOUD_PERSONAL:
                    result = new CloudPersonalContact();
                    break;
//                case CLOUD_COMPANY:
//                    result = new CompanyContact();
//                    break;
                case DEVICE:
                    result = new DeviceContact();
                    break;
            }
            if (result != null) {
                result.clone(temp);
            }
        } else {
            result = temp;
        }
        return result;
    }

    public int getCount() {
        int count = 0;
        acquireReadLock();
        try {
            count = mCacheContacts.size();
        }finally {
            releaseReadLock();
        }
        return count;
    }

    protected abstract Contact removeCacheContact(Long contactId);
    public abstract void clear();
    public abstract void reloadContacts();
    public abstract List<Contact> loadContacts(Contact.TypeValue[] lowerCaseFilter, boolean isFuzzySearch, boolean includeExtension, String countryCode, String nationalPrefix);
    public abstract List<ContactMatchInfo> loadContactsWithMatchInfo(Contact.TypeValue[] lowerCaseFilter, boolean isFuzzySearch, boolean includeExtension, String countryCode, String nationalPrefix);
}
