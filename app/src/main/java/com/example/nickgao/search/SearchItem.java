/**
 * Copyright (C) 2010-2011, RingCentral, Inc. 
 * All Rights Reserved.
 */

package com.example.nickgao.search;

import android.app.SearchManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.SparseArray;

import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsSorting;



public class SearchItem implements Comparable<SearchItem> {
    public static final int RC_EXTENSION = -1;         //Don't change this constants!!!
    public static final int DEVICE_CONTACT = 1;       //(they are used by ItemComparator class)
    public static final int CLOUD_CONTACT = 2;

    public static final int EXTENSION_TYPE_PIN = -2;
    public static final int EXTENSION_TYPE_DID = -1;

    private static final String ID = BaseColumns._ID;
    private static final String NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    private static final String NUMBER = SearchManager.SUGGEST_COLUMN_INTENT_DATA;
    private static final String LABEL = "label";
    private static final String PHONE_ID = "phone_id";
    private static final String CONTACT_ID = "contact_id";
    private static final String LOOKUP_KEY = "lookup_key";
    private static final String CONTACT_TYPE = "contact_type";

    static final String[] COLUMN_NAMES = { ID, NAME, NUMBER, LABEL, PHONE_ID, CONTACT_ID, LOOKUP_KEY, CONTACT_TYPE };
    static int COLUMN_ID = 0;
    static int COLUMN_NAME = 1;
    /*
    static int COLUMN_NUMBER = 2;
    static int COLUMN_LABEL = 3;
    static int COLUMN_PHONE_ID = 4;
    static int COLUMN_CONTACT_ID = 5;
    static int COLUMN_LOOKUP_KEY = 6;
    static int COLUMN_CONTACT_TYPE = 7;
    */

    public int contactType;
    private int phoneType;
    private String[] columns;

    public SearchItem(int contactType, String name, String number, int phoneType, String label,
                      long contactId) {
        this.contactType = contactType;
        this.phoneType = phoneType;
        String contactIdStr = String.valueOf(contactId);

        Contact.ContactType type = Contact.ContactType.UNKNOW;
        switch (this.contactType) {
            case RC_EXTENSION:
                type = Contact.ContactType.CLOUD_COMPANY;
                break;
            case CLOUD_CONTACT:
                type = Contact.ContactType.CLOUD_PERSONAL;
                break;
            case DEVICE_CONTACT:
                type = Contact.ContactType.DEVICE;
                break;
        }
        columns = new String[] { "0", name, number, label, contactIdStr, contactIdStr, "", String.valueOf(type.ordinal())};
    }

    String[] getColumns() {
        return columns;
    }

    void setID(int id) {
        columns[COLUMN_ID] = String.valueOf(id);
    }

    private static SparseArray<Integer> sPhoneTypePriorityMap = new SparseArray<>();
    static {
        sPhoneTypePriorityMap.put(EXTENSION_TYPE_PIN, EXTENSION_TYPE_PIN);
        sPhoneTypePriorityMap.put(EXTENSION_TYPE_DID, EXTENSION_TYPE_DID);
        sPhoneTypePriorityMap.put(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, 1);
    }

    @Override
    public int compareTo(SearchItem contact){
        int result;

        //Compare names
        result = ContactsSorting.sorting(this.columns[COLUMN_NAME], contact.columns[COLUMN_NAME], true);
        if (result != 0) {
            return result;
        }

        int temp = this.contactType - contact.contactType;
        if(temp > 0) {
            return -1;
        }else if(temp < 0) {
            return 1;
        }else {
            if(this.contactType == RC_EXTENSION) {
                if (this.phoneType != contact.phoneType) {
                    int priority1 = sPhoneTypePriorityMap.get(this.phoneType, Integer.MAX_VALUE);
                    int priority2 = sPhoneTypePriorityMap.get(contact.phoneType, Integer.MAX_VALUE);
                    if(priority1 == priority2) {
                        return (this.phoneType < contact.phoneType)? -1: +1;
                    } else {
                        return (priority1 < priority2)? -1: +1;
                    }
                }
            }
            return result;
        }
    }
}
