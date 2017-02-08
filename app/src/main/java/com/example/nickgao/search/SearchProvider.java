/**
 * Copyright (C) 2010-2011, RingCentral, Inc. 
 * All Rights Reserved.
 */

package com.example.nickgao.search;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.example.nickgao.BuildConfig;
import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContact;
import com.example.nickgao.contacts.adapters.contactsprovider.CompanyContact;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsLoader;
import com.example.nickgao.contacts.adapters.contactsprovider.DeviceContact;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SearchProvider {
    private static final String TAG = "[RC]SearchProvider";
    private Context mContext;
    private static final int MAX_CONTACT_MATCHED = 1000;

    public SearchProvider(final Context context) {
        mContext = context;

    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String what = selection;

        if (LogSettings.ENGINEERING) {
            MktLog.i(TAG, "Search for \"" + what + "\"...");
        }

        if (TextUtils.isEmpty(what)) {
            return null;
        }

        long time = System.currentTimeMillis();

        int match = sUriMatcher.match(uri);

        if (match == UriMatcher.NO_MATCH) {
            if (LogSettings.MARKET) {
                MktLog.e(TAG, "query(): Wrong URI");
            }
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        List<SearchItem> list;

        switch (match) {
            case FUZZY_SEARCH_ALL_MATCH:
                list = onSearching(what, true, true);
                Collections.sort(list);//sort the matched result
                break;
            case FUZZY_SEARCH_NO_EXT_MATCH:
                list = onSearching(what, false, true);
                Collections.sort(list);//sort the matched result
                break;
            case EXACT_SEARCH_ALL_MATCH:
                list = onSearching(what, true, false);
                break;
            case EXACT_SEARCH_NO_EXT_MATCH:
                list = onSearching(what, false, false);
                break;
            default:
               list = new ArrayList<>();
        }

        //translate to matrix cursor
        MatrixCursor cursor = new MatrixCursor(SearchItem.COLUMN_NAMES);
        Iterator<SearchItem> iterator = list.iterator();
        SearchItem item;
        int id = 0;
        while (iterator.hasNext()) {
            item = iterator.next();
            if(item != null) {
                item.setID(id++);
                cursor.addRow(item.getColumns());
            }
        }
        MktLog.d(TAG, "query: spent=" + (System.currentTimeMillis() - time));
        return cursor;
    }

    public List<SearchItem> onSearching(String what, boolean extension, boolean isFuzzySearch) {
        List<SearchItem> result = new ArrayList<>();

        long time = System.currentTimeMillis();
        final Context context = mContext;
        final String mobilePhoneTag =  "Mobile";
        final String extensionTag = context.getString(R.string.phone_tag_extension);
        final String directFaxTag  = context.getResources().getString(R.string.direct_fax);
        final String directNumberTag  = context.getResources().getString(R.string.direct_number);
        
        String nationalPrefix = "1";
        String countryCode = "+" + "101";

        List<ContactsLoader.ContactMatchInfo> contacts = null;
        // FIXME: 2/8/17
        //List<ContactsLoader.ContactMatchInfo> contacts = ContactsProvider.getInstance().loadContactsWithMatchInfo(extension, isFuzzySearch, what, countryCode, nationalPrefix);
        if(contacts.size() == 0) {
            return result;
        }

        MktLog.d(TAG, "onSearching: loading filter=" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

        int record = 0;
        for(ContactsLoader.ContactMatchInfo contactMatchInfo : contacts) {
            //ignore left items which > MAX_CONTACT_MATCHED
            if(record++ > MAX_CONTACT_MATCHED) {
                break;
            }

            List<Contact.MatchInfo> matchedInfoList = contactMatchInfo.matchList;
            Contact contact = contactMatchInfo.contact;
            boolean isNumberMatch = false;
            Contact.MatchInfo matchedInfo = null;
            for(Contact.MatchInfo item : matchedInfoList) {
                if(item.getMatchType() == Contact.MatchType.NUMBER) {
                    matchedInfo = item;
                    isNumberMatch = true;
                    break;
                }
            }

            if(contact.getType() == Contact.ContactType.CLOUD_PERSONAL) {
                CloudPersonalContact cpContact = (CloudPersonalContact) contact;
                List<Contact.TypeValue> phoneList = cpContact.getE164PhoneNumbers();
                String displayName = cpContact.getDisplayName();

                if (isNumberMatch) {
                    for (int i = 0; i < phoneList.size(); i++) {
                        Contact.TypeValue phone = phoneList.get(i);
                        String phoneNumber = phone.getValue();
                        if (phoneNumber.contains(matchedInfo.getMatchValue()) || (isFuzzySearch && Contact.numberMatch_prefix(phoneNumber, matchedInfo.getMatchValue(), countryCode, nationalPrefix))) {
                            result.add(toSearchItem(context, SearchItem.CLOUD_CONTACT, cpContact.getId(), displayName, phone.getValue(), phone.getType()));
                        }
                    }
                } else {
                    //when matched name, company, job title, email, we should add all phone numbers
                    for (int i = 0; i < phoneList.size(); i++) {
                        Contact.TypeValue phone = phoneList.get(i);
                        result.add(toSearchItem(context, SearchItem.CLOUD_CONTACT, cpContact.getId(), displayName, phone.getValue(), phone.getType()));
                    }
                }
            }else if(contact.getType() ==  Contact.ContactType.CLOUD_COMPANY) {
                CompanyContact companyContact = (CompanyContact) contact;
                String contactName = companyContact.getDisplayName();
                String pin = companyContact.getPin();
                String mobile = companyContact.getMobilePhone();
                long contactId = companyContact.getId();
                Collection<CompanyContact.DirectNumber> directNumbers = companyContact.getDirectNumbers();
                String typeShowString = "";
                if (isNumberMatch) {
                    //also pin if needed
                    if (extension && !TextUtils.isEmpty(pin) && contain(pin, matchedInfo.getMatchValue())) {
                        result.add(new SearchItem(SearchItem.RC_EXTENSION, contactName, pin,
                                SearchItem.EXTENSION_TYPE_PIN,
                                extensionTag + ": ", contactId));
                    }

                    //also mobile phone if needed
                    if (!TextUtils.isEmpty(mobile) && (contain(mobile, matchedInfo.getMatchValue()) || (isFuzzySearch && Contact.numberMatch_prefix(mobile, matchedInfo.getMatchValue(), countryCode, nationalPrefix)))) {
                        result.add(new SearchItem(SearchItem.RC_EXTENSION, contactName, mobile,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                                mobilePhoneTag + ": ", contactId));
                    }

                    for (CompanyContact.DirectNumber directNumber : directNumbers) {
                        if (contain(directNumber.getValue(), matchedInfo.getMatchValue()) || (isFuzzySearch && Contact.numberMatch_prefix(directNumber.getValue(), matchedInfo.getMatchValue(), countryCode, nationalPrefix))) {
                            typeShowString = directNumberTag;
                            result.add(new SearchItem(SearchItem.RC_EXTENSION, contactName, directNumber.getValue(), SearchItem.EXTENSION_TYPE_DID, typeShowString + ": ", contactId));
                        }
                    }
                } else {
                    //also pin if needed
                    if (extension && !TextUtils.isEmpty(pin)) {
                        result.add(new SearchItem(SearchItem.RC_EXTENSION, contactName, pin,
                                SearchItem.EXTENSION_TYPE_PIN,
                                extensionTag + ": ", contactId));
                    }

                    //also mobile phone if needed
                    if (!TextUtils.isEmpty(mobile)) {
                        result.add(new SearchItem(SearchItem.RC_EXTENSION, contactName, mobile,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                                mobilePhoneTag + ": ", contactId));
                    }
                    //// FIXME: 2/8/17 
//                    for (CompanyContact.DirectNumber directNumber : directNumbers) {
//                        if (PhoneNumberType.FaxOnly.name().equalsIgnoreCase(directNumber.getType())) {
//                            typeShowString = directFaxTag;
//                        } else {
//                            typeShowString = directNumberTag;
//                        }
//                        result.add(new SearchItem(SearchItem.RC_EXTENSION, contactName, directNumber.getValue(), SearchItem.EXTENSION_TYPE_DID, typeShowString + ": ", contactId));
//                    }
                }
            }else if(contact.getType() == Contact.ContactType.DEVICE) {
                DeviceContact deviceContact = (DeviceContact) contact;
                long contactId = deviceContact.getContactId();
                String contactName = deviceContact.getDisplayName();
                //if matched type is number, that means we just add matched-number items
                if (isNumberMatch) {
                    List<Contact.TypeValue> listPhoneNumbers = deviceContact.getE164PhoneNumbers();
                    int length = listPhoneNumbers.size();
                    for (int i = 0; i < length; i++) {
                        Contact.TypeValue phoneNumber = listPhoneNumbers.get(i);
                        // FIXME: 2/8/17 
//                        if (contain(phoneNumber.getValue(), matchedInfo.getMatchValue()) || (isFuzzySearch && Contact.numberMatch_prefix(phoneNumber.getValue(), matchedInfo.getMatchValue(), countryCode, nationalPrefix))) {
//                            result.add(new SearchItem(SearchItem.DEVICE_CONTACT, contactName, phoneNumber.getValue(), phoneNumber.getType(), Cont.acts().getPhoneNumberTag(context, phoneNumber.getType()) + ": ", contactId));
//                        }
                    }
                } else {//otherwise, we have to add all phone numbers
                    List<Contact.TypeValue> listPhoneNumbers = deviceContact.getE164PhoneNumbers();
                    int length = listPhoneNumbers.size();
                    for (int i = 0; i < length; i++) {
                        Contact.TypeValue phoneNumber = listPhoneNumbers.get(i);
                        if (!TextUtils.isEmpty(phoneNumber.getValue())) {
                            // FIXME: 2/8/17 
                            //result.add(new SearchItem(SearchItem.DEVICE_CONTACT, contactName, phoneNumber.getValue(), phoneNumber.getType(), Cont.acts().getPhoneNumberTag(context, phoneNumber.getType()) + ": ", contactId));
                        }
                    }
                }
            }
        }

        MktLog.d(TAG, "onSearching: loading cloud personal, device =" + (System.currentTimeMillis() - time) + " item="+ result.size());

        return result;
    }

    private SearchItem toSearchItem(final Context context, int contactType, long contactId, String contactName, String phoneNumber, int phoneType) {
        String tag = "";
        //// FIXME: 2/8/17 
//        if(contactType == SearchItem.CLOUD_CONTACT) {
//            try {
//                tag = ContactsProvider.getInstance().getCloudPhoneTag(phoneType);
//            }catch (Throwable th) {
//                MktLog.e(TAG, "toSearchItem() error="+ th.toString());
//            }
//        }else {
//            tag = Cont.acts().getPhoneNumberTag(context, phoneType);
//        }

        return new SearchItem(contactType, contactName, phoneNumber, phoneType, tag + ": ", contactId);
    }

    protected boolean contain(String src, String dest) {
        return (!TextUtils.isEmpty(src) && src.toLowerCase().contains(dest));
    }

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".search.searchprovider";
    public static final Uri SEARCH_URI = Uri.parse("content://" + AUTHORITY);

    public static final String FUZZY_SEARCH_ALL = "fuzzy_search_all";
    public static final String FUZZY_SEARCH_NO_EXT = "fuzzy_search_no_ext";
    public static final String EXACT_SEARCH_ALL = "exact_search_all";
    public static final String EXACT_SEARCH_NO_EXT = "exact_search_no_ext";

    private static final int FUZZY_SEARCH_ALL_MATCH = 1;
    private static final int FUZZY_SEARCH_NO_EXT_MATCH = 2;
    private static final int EXACT_SEARCH_ALL_MATCH = 3;
    private static final int EXACT_SEARCH_NO_EXT_MATCH = 4;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, FUZZY_SEARCH_ALL, FUZZY_SEARCH_ALL_MATCH);
        sUriMatcher.addURI(AUTHORITY, FUZZY_SEARCH_NO_EXT, FUZZY_SEARCH_NO_EXT_MATCH);
        sUriMatcher.addURI(AUTHORITY, EXACT_SEARCH_ALL, EXACT_SEARCH_ALL_MATCH);
        sUriMatcher.addURI(AUTHORITY, EXACT_SEARCH_NO_EXT, EXACT_SEARCH_NO_EXT_MATCH);
    }
}
