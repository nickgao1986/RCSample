package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.nickgao.androidsample11.ContactsUtils;
import com.example.nickgao.service.model.contact.Address;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by nick.gao on 1/28/17.
 */

public class DeviceContact extends Contact {
    private long mPhotoId;
    private long mPhotoFileId;
    private long mContactId;
    protected String mFullName;
    private List<TypeValue> mE164PhoneNumbers = new ArrayList<>();
    private List<Contact.TypeValue> mOriginalPhoneNumbers = new ArrayList<>();
    private List<Contact.TypeValue> mEmails = new ArrayList<>();
    private List<Contact.TypeAddress> mAddresses = new ArrayList<>();

    private boolean mDuplicate = false;
    private long mDuplicateContactId = 0l;

    public DeviceContact() {
    }

    public DeviceContact(long rawContactId, long contactId) {
        super(ContactType.DEVICE, rawContactId);
        mContactId = contactId;
    }

    @Override
    public Bitmap getImage() {
        return null;
    }

    @Override
    public Intent getDetailsActivityIntent(Context context) {
//        Intent intent = new Intent();
//        intent.putExtra(ViewPersonalContact.PERSONAL_ID, String.valueOf(mContactId));
//        intent.putExtra(ViewPersonalContact.DISPLAY_NAME, getDisplayName());
//        intent.setClass(context, CommonEventDetailActivity.class);
        return null;
    }

    @Override
    public Uri getContactUri() {
        return ContentUris.withAppendedId(ContactsUtils.Uri_People, mContactId);
    }

    public void formatDisplayName() {
        String displayName = getDisplayName();
        if (!Contact.isEmpty(displayName)) {
            return;
        }

        do {
            //nick name
            if (!isEmpty(mNickName)) {
                displayName = mNickName.trim();
                break;
            }

            //company
            if (!isEmpty(mCompany)) {
                displayName = mCompany.trim();
                break;
            }

            //email
            for (Contact.TypeValue email : mEmails) {
                if (!isEmpty(email.getValue())) {
                    displayName = email.getValue().trim();
                    break;
                }
            }

            if (!Contact.isEmpty(displayName)) {
                break;
            }

            //phone number
            for (Contact.TypeValue phoneNumber : mE164PhoneNumbers) {
                String phone = phoneNumber.getValue();
                if (!isEmpty(phone)) {
                    displayName = phone.trim();
                    break;
                }
            }

        } while (false);

        //opt, make sure next time we need to reset it.
        setDisplayName(isEmpty(displayName) ? NO_MANE : displayName);
    }


    public long getContactId() {
        return mContactId;
    }

    public void setNames(String fullName, String firstName, String middleName, String lastName) {
        setDisplayName(fullName);
        mFullName = fullName;
        mFirstName = firstName;
        mMiddleName = middleName;
        mLastName = lastName;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setPhotoId(long photoId) {
        mPhotoId = photoId;
    }

    public long getPhotoId() {
        return mPhotoId;
    }

    public long getPhotoFileId() {
        return mPhotoFileId;
    }

    public void setPhotoFileId(long photoFileId) {
        this.mPhotoFileId = photoFileId;
    }


    @Override
    public boolean isInternalMatched(TypeValue search, boolean extension, boolean isFuzzySearch, List<MatchInfo> matchList, String countryCode, String nationalPrefix) {
        String filter = search.getValue();
        if (isDuplicate()) {
            return false;
        }

        if (commonMatch(this.mFullName, filter, isFuzzySearch)
                || commonMatch(this.mNickName, filter, isFuzzySearch)
                || commonMatch(this.mFirstName, filter, isFuzzySearch)
                || commonMatch(this.mMiddleName, filter, isFuzzySearch)
                || commonMatch(this.mLastName, filter, isFuzzySearch)) {
            this.addMatchInfo(matchList, new MatchInfo(MatchType.NAME, filter));
            return true;
        }

        if (commonMatch(this.mCompany, filter, isFuzzySearch) || commonMatch(this.mJobTitle, filter, isFuzzySearch)) {
            this.addMatchInfo(matchList, new MatchInfo(MatchType.COMPANY, filter));
            return true;
        }

        for (Contact.TypeValue email : mEmails) {
            String value = email.getValue();
            if (commonMatchNoCheckNull(value, filter, isFuzzySearch)) {
                this.addMatchInfo(matchList, new MatchInfo(MatchType.EMAIL, filter));
                return true;
            }
        }

        if (search.getType() == FILTER_TYPE_NUMBER) {
            for (Contact.TypeValue phoneNumber : mE164PhoneNumbers) {
                String phone = phoneNumber.getValue();
                if (numberMatchNoCheckNull(phone, filter, isFuzzySearch) || (isFuzzySearch && numberMatch_prefix(phone, filter, countryCode, nationalPrefix))) {
                    this.addMatchInfo(matchList, new MatchInfo(MatchType.NUMBER, filter));
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean isFullE164NumberMatched(String e164Number, boolean extension, TypeValue matchValue) {
        if (isDuplicate()) {
            return false;
        }

        for (Contact.TypeValue phoneNumber : mE164PhoneNumbers) {
            int type = phoneNumber.getType();
            String phone = phoneNumber.getValue();
            if (ContactMatcher.isFullE164NumberMatch(e164Number, phone)) {
                if (matchValue != null) {
                    matchValue.setType(type);
                    matchValue.setValue(phone);
                }
                return true;
            }
        }
        return false;
    }

    public List<Contact.TypeValue> getE164PhoneNumbers() {
        return mE164PhoneNumbers;
    }

    public List<Contact.TypeValue> getOriginalPhoneNumbers() {
        return mOriginalPhoneNumbers;
    }

    public List<Contact.TypeValue> getEmailAddressList() {
        return mEmails;
    }

    public List<Contact.TypeAddress> getAddresses() {
        return mAddresses;
    }

    public void addE164PhoneNumber(Contact.TypeValue phoneNumber) {
        mE164PhoneNumbers.add(phoneNumber);
    }

    public void addOriginalPhoneNumber(Contact.TypeValue phoneNumber) {
        mOriginalPhoneNumbers.add(phoneNumber);
    }

    public void addDeviceEmail(Contact.TypeValue email) {
        mEmails.add(email);
    }

    public void addAddress(Contact.TypeAddress address) {
        mAddresses.add(address);
    }

    public synchronized boolean isDuplicate() {
        return mDuplicate;
    }

    public synchronized void setDuplicate(boolean isDuplicate) {
        this.mDuplicate = isDuplicate;
    }

    public synchronized long getDuplicateContactId() {
        return mDuplicateContactId;
    }

    public synchronized void setDuplicateContactId(long cloudContactId) {
        this.mDuplicateContactId = cloudContactId;
    }

    @Override
    public boolean hasPhoneNumber() {
        return (mE164PhoneNumbers != null && !mE164PhoneNumbers.isEmpty());
    }

    @Override
    public boolean hasEmail() {
        return (mEmails != null && !mEmails.isEmpty());
    }

    @Override
    public void clone(Contact src) {
        super.clone(src);

        DeviceContact contact = (DeviceContact) src;
        this.mPhotoId = contact.getPhotoId();
        this.mPhotoFileId = contact.getPhotoFileId();
        this.mContactId = contact.getContactId();
        this.mFullName = contact.getFullName();

        mE164PhoneNumbers.clear();
        mOriginalPhoneNumbers.clear();
        mEmails.clear();
        mAddresses.clear();

        List<Contact.TypeValue> e164PhoneNumbers = contact.getE164PhoneNumbers();
        List<Contact.TypeValue> originalPhoneNumbers = contact.getOriginalPhoneNumbers();
        List<Contact.TypeValue> emails = contact.getEmailAddressList();
        List<Contact.TypeAddress> addresses = contact.getAddresses();

        for (Contact.TypeValue phone : e164PhoneNumbers) {
            mE164PhoneNumbers.add(new TypeValue(phone.getType(), phone.getValue()));
        }

        for (Contact.TypeValue phone : originalPhoneNumbers) {
            mOriginalPhoneNumbers.add(new TypeValue(phone.getType(), phone.getValue()));
        }

        for (Contact.TypeValue email : emails) {
            mEmails.add(new TypeValue(email.getType(), email.getValue()));
        }

        for (Contact.TypeAddress address : addresses) {
            Address data = address.getValue();
            mAddresses.add(new TypeAddress(address.getType(), new Address(data.getCountry(), data.getState(), data.getCity(), data.getStreet(), data.getZip())));
        }

        this.mDuplicate = contact.isDuplicate();
        this.mDuplicateContactId = contact.getDuplicateContactId();
    }
}
