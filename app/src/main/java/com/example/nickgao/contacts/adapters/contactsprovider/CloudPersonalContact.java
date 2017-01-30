package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.example.nickgao.R;
import com.example.nickgao.eventdetail.CommonEventDetailActivity;
import com.example.nickgao.eventdetail.ViewCloudPersonalContact;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.model.contact.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry.cai on 11/5/15.
 */
public class CloudPersonalContact extends Contact {
    public enum EmailType {
        EMAIL {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.email1_tag);
            }
        },
        EMAIL2 {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.email2_tag);
            }
        },
        EMAIL3 {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.email3_tag);
            }
        },
    }

    public enum AddressType {
        HOME_ADDRESS {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.home_address_tag);
            }
        },
        BUSINESS_ADDRESS {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.business_address_tag);
            }
        },
        OTHER_ADDRESS {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.other_address_tag);
            }
        },
    }

    public enum PhoneType {
        HOME_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_home_phone);
            }
        },
        HOME_PHONE2 {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_home_phone2);
            }
        },
        BUSINESS_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_business_phone);
            }
        },
        BUSINESS_PHONE2 {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_business_phone2);
            }
        },
        MOBILE_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_mobile_phone);
            }
        },
        BUSINESS_FAX {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_business_fax);
            }
        },
        COMPANY_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_company_phone);
            }
        },
        ASSISTANT_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_assistant_phone);
            }
        },
        CAR_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_car_phone);
            }
        },
        OTHER_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_other_phone);
            }
        },
        OTHER_FAX {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_other_fax);
            }
        },
        CALLBACK_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_callback_phone);
            }
        },
    }

    public enum AppCloudPhoneType {
        MOBILE_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_mobile_phone);
            }
        },

        BUSINESS_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_business_phone);
            }
        },

        HOME_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_home_phone);
            }
        },

        COMPANY_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_company_phone);
            }
        },

        FAX {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_business_fax);
            }
        },

        ASSISTANT_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_assistant_phone);
            }
        },
        CAR_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_car_phone);
            }
        },
        OTHER_PHONE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.phone_tag_other_phone);
            }
        }
    }

    public enum WebPageType {
        WEB_PAGE {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.web_page_tag);
            }
        }
    }

    public enum BirthdayType {
        BIRTHDAY_DAY {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.birthday_tag);
            }
        }
    }

    public enum CloudContactType {
        SERVER,
        LOCAL,
    }

    public static final int[] PHONE_TYPE_MOBILE = {PhoneType.MOBILE_PHONE.ordinal()};
    public static final int[] PHONE_TYPE_BUSINESS = {PhoneType.BUSINESS_PHONE.ordinal(), PhoneType.BUSINESS_PHONE2.ordinal()};
    public static final int[] PHONE_TYPE_HOME = {PhoneType.HOME_PHONE.ordinal(), PhoneType.HOME_PHONE2.ordinal()};
    public static final int[] PHONE_TYPE_COMPANY = {PhoneType.COMPANY_PHONE.ordinal()};
    public static final int[] PHONE_TYPE_FAX = {PhoneType.BUSINESS_FAX.ordinal(), PhoneType.OTHER_FAX.ordinal()};
    public static final int[] PHONE_TYPE_ASSISTANT = {PhoneType.ASSISTANT_PHONE.ordinal()};
    public static final int[] PHONE_TYPE_CAR = {PhoneType.CAR_PHONE.ordinal()};
    public static final int[] PHONE_TYPE_OTHER = {PhoneType.CALLBACK_PHONE.ordinal(), PhoneType.OTHER_PHONE.ordinal()};

    public static final int MOBILE_PHONE_SIZE = PHONE_TYPE_MOBILE.length;
    public static final int BUSINESS_PHONE_SIZE = PHONE_TYPE_BUSINESS.length;
    public static final int HOME_PHONE_SIZE = PHONE_TYPE_HOME.length;
    public static final int COMPANY_PHONE_SIZE = PHONE_TYPE_COMPANY.length;
    public static final int FAX_PHONE_SIZE = PHONE_TYPE_FAX.length;
    public static final int ASSISTANT_PHONE_SIZE = PHONE_TYPE_ASSISTANT.length;
    public static final int CAR_PHONE_SIZE = PHONE_TYPE_CAR.length;
    public static final int OTHER_PHONE_SIZE = PHONE_TYPE_OTHER.length;

    public static final int[] ADDRESS_TYPE_HOME = {AddressType.HOME_ADDRESS.ordinal()};
    public static final int[] ADDRESS_TYPE_WORK = {AddressType.BUSINESS_ADDRESS.ordinal()};
    public static final int[] ADDRESS_TYPE_OTHER = {AddressType.OTHER_ADDRESS.ordinal()};
    public static final int HOME_ADDRESS_SIZE = ADDRESS_TYPE_HOME.length;
    public static final int WORK_ADDRESS_SIZE = ADDRESS_TYPE_WORK.length;
    public static final int OTHER_ADDRESS_SIZE = ADDRESS_TYPE_OTHER.length;

    public static final int[] EMAIL_TYPE_1 = {EmailType.EMAIL.ordinal()};
    public static final int[] EMAIL_TYPE_2 = {EmailType.EMAIL2.ordinal()};
    public static final int[] EMAIL_TYPE_3 = {EmailType.EMAIL3.ordinal()};

    public static final int EMAIL_TYPE_1_SIZE = EMAIL_TYPE_1.length;
    public static final int EMAIL_TYPE_2_SIZE = EMAIL_TYPE_2.length;
    public static final int EMAIL_TYPE_3_SIZE = EMAIL_TYPE_3.length;

    //order should keep the same as AppPhoneType
    public static final int[] ALL_PHONE_SIZE = {MOBILE_PHONE_SIZE, BUSINESS_PHONE_SIZE, HOME_PHONE_SIZE, COMPANY_PHONE_SIZE, FAX_PHONE_SIZE, ASSISTANT_PHONE_SIZE, CAR_PHONE_SIZE, OTHER_PHONE_SIZE};
    public static final int[] ALL_EMAIL_SIZE = {EMAIL_TYPE_1_SIZE, EMAIL_TYPE_2_SIZE, EMAIL_TYPE_3_SIZE};
    public static final int[] ALL_WEB_PAGE_SIZE = {1};
    public static final int[] ALL_ADDRESS_SIZE = {HOME_ADDRESS_SIZE, WORK_ADDRESS_SIZE, OTHER_ADDRESS_SIZE};
    public static final int[] ALL_BIRTHDAY_SIZE = {1};
    public static final int[][] MAP_TO_PHONE_TYPE = {PHONE_TYPE_MOBILE, PHONE_TYPE_BUSINESS, PHONE_TYPE_HOME, PHONE_TYPE_COMPANY, PHONE_TYPE_FAX, PHONE_TYPE_ASSISTANT, PHONE_TYPE_CAR, PHONE_TYPE_OTHER};
    public static final int[][] MAP_TO_EMAIL_TYPE = {EMAIL_TYPE_1, EMAIL_TYPE_2, EMAIL_TYPE_3};
    public static final int[][] MAP_TO_ADDRESS_TYPE = {ADDRESS_TYPE_HOME, ADDRESS_TYPE_WORK, ADDRESS_TYPE_OTHER};
    public static final int[][] MAP_TO_WEB_PAGE_TYPE = {new int[]{WebPageType.WEB_PAGE.ordinal()}};
    public static final int[][] MAP_TO_BIRTHDAY_TYPE = {new int[]{BirthdayType.BIRTHDAY_DAY.ordinal()}};


    static final int MAX_LENGTH_FIRST_NAME = 64;
    static final int MAX_LENGTH_LAST_NAME = 64;
    static final int MAX_LENGTH_MIDDLE_NAME = 32;
    static final int MAX_LENGTH_NICK_NAME = 32;
    static final int MAX_LENGTH_JOB_TITLE = 64;
    static final int MAX_LENGTH_COMPANY = 128;
    static final int MAX_LENGTH_DEPARTMENT = 64;
    static final int MAX_LENGTH_WEB_PAGE = 256;
    static final int MAX_LENGTH_NOTES = 2000;

    static final int MAX_LENGTH_ADDRESS_COUNTRY = 64;
    static final int MAX_LENGTH_ADDRESS_STATE = 64;
    static final int MAX_LENGTH_ADDRESS_CITY = 64;
    static final int MAX_LENGTH_ADDRESS_STREET = 256;
    static final int MAX_LENGTH_ADDRESS_ZIP_CODE = 32;

    private CloudContactType mCloudContactType = CloudContactType.SERVER;
    private String mUri;
    private String mAvailability;
    private int mSyncStatus;
    private List<TypeValue> mE164PhoneNumbers = new ArrayList<>();
    private List<Contact.TypeValue> mOriginalPhoneNumbers = new ArrayList<>();
    private List<Contact.TypeValue> mEmails = new ArrayList<>();
    private List<Contact.TypeAddress> mAddresses = new ArrayList<>();

    public static long getLocalContactId() {
        return -System.currentTimeMillis();
    }

    public static boolean isLocalContact(long contactId) {
        return contactId < 0;
    }

    public CloudPersonalContact() {
    }

    public CloudPersonalContact(CloudContactType type, long contactId, String displayName, String firstName, String middleName, String lastName, String nickName, String company, String jobTitle) {
        super(Contact.ContactType.CLOUD_PERSONAL, contactId, displayName);
        mCloudContactType = type;
        mFirstName = valueWithLimitation(firstName, MAX_LENGTH_FIRST_NAME);
        mMiddleName = valueWithLimitation(middleName, MAX_LENGTH_MIDDLE_NAME);
        mLastName = valueWithLimitation(lastName, MAX_LENGTH_LAST_NAME);
        mNickName = valueWithLimitation(nickName, MAX_LENGTH_NICK_NAME);
        mCompany = valueWithLimitation(company, MAX_LENGTH_COMPANY);
        mJobTitle = valueWithLimitation(jobTitle, MAX_LENGTH_JOB_TITLE);
    }

    public boolean isLocalContact() {
        return getId() < 0;
    }

    @Override
    public long getId() {
        return ContactsProvider.getInstance().ensureCloudContactId(super.getId());
    }

    public String getWebPage(int index) {
        return (index < mWebPages.size()) ? mWebPages.get(index) : null;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public String getAvailability() {
        return mAvailability;
    }

    public void setAvailability(String availability) {
        mAvailability = availability;
    }

    public CloudContactType getCloudContactType() {
        return mCloudContactType;
    }

    public void setCloudContactType(CloudContactType cloudContactType) {
        mCloudContactType = cloudContactType;
    }

    public void setEmails(List<Contact.TypeValue> list) {
        mEmails.clear();
        if (list != null) {
            mEmails.addAll(list);
        }
    }

    public void setE164PhoneNumbers(List<Contact.TypeValue> list) {
        mE164PhoneNumbers.clear();
        if (list != null) {
            mE164PhoneNumbers.addAll(list);
        }
    }

    public void setOriginalPhoneNumbers(List<Contact.TypeValue> list) {
        mOriginalPhoneNumbers.clear();
        if (list != null) {
            mOriginalPhoneNumbers.addAll(list);
        }
    }

    public void setAddress(List<Contact.TypeAddress> list) {
        mAddresses.clear();
        if (list != null) {
            for (Contact.TypeAddress typeAddress : list) {
                //String country, String state, String city, String street, String zip
                Address address = typeAddress.getValue();
                mAddresses.add(new Contact.TypeAddress(typeAddress.getType(), new Address(
                        valueWithLimitation(address.getCountry(), MAX_LENGTH_ADDRESS_COUNTRY),
                        valueWithLimitation(address.getState(), MAX_LENGTH_ADDRESS_STATE),
                        valueWithLimitation(address.getCity(), MAX_LENGTH_ADDRESS_CITY),
                        valueWithLimitation(address.getStreet(), MAX_LENGTH_ADDRESS_STREET),
                        valueWithLimitation(address.getZip(), MAX_LENGTH_ADDRESS_ZIP_CODE))
                ));
            }
        }
    }


    @Override
    public void addWebPage(String webPage) {
        super.addWebPage(valueWithLimitation(webPage, MAX_LENGTH_WEB_PAGE));
    }

    @Override
    public void setNotes(String notes) {
        super.setNotes(valueWithLimitation(notes, MAX_LENGTH_NOTES));
    }

    public int getSyncStatus() {
        return mSyncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.mSyncStatus = syncStatus;
    }



    public void generateDisplayName() {
        StringBuilder sb = new StringBuilder();
        if (!isEmpty(mFirstName)) {
            sb.append(mFirstName.trim());
        }
        if (!isEmpty(mMiddleName)) {
            sb.append(" ").append(mMiddleName.trim());
        }

        if (!isEmpty(mLastName)) {
            sb.append(" ").append(mLastName.trim());
        }
        String name = sb.toString().trim();

        do {
            if (!isEmpty(name)) {
                break;
            }

            //nick name
            if (!isEmpty(mNickName)) {
                name = mNickName.trim();
                break;
            }

            //company
            if (!isEmpty(mCompany)) {
                name = mCompany.trim();
                break;
            }

            //mail address
            Contact.TypeValue typeValue;
            List<Contact.TypeValue> listMail = getEmails();
            if (!listMail.isEmpty()) {
                for (int i = 0; i < listMail.size(); i++) {
                    typeValue = listMail.get(i);
                    name = typeValue.getValue().trim();
                    if (!isEmpty(name)) {
                        break;
                    }
                }
            }

            if (!isEmpty(name)) {
                break;
            }

            //phone number
            List<Contact.TypeValue> listPhone = getOriginalPhoneNumbers();
            if (!listPhone.isEmpty()) {
                for (int i = 0; i < listPhone.size(); i++) {
                    typeValue = listPhone.get(i);
                    name = typeValue.getValue().trim();
                    if (!isEmpty(name)) {
                        break;
                    }
                }
            }

            if (!isEmpty(name)) {
                break;
            }

            name = Contact.NO_MANE;

        } while (false);

        setDisplayName(name);
    }

    @Override
    public Bitmap getImage() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Intent getDetailsActivityIntent(Context context) {
        Intent intent = new Intent();
        intent.putExtra(ViewCloudPersonalContact.PERSONAL_ID, getId());
        intent.putExtra(ViewCloudPersonalContact.DISPLAY_NAME, getDisplayName());
        intent.setClass(context, CommonEventDetailActivity.class);
        return intent;
    }

    @Override
    public Uri getContactUri() {
        return null;
    }

    @Override
    public boolean isInternalMatched(TypeValue search, boolean extension, boolean isFuzzySearch, List<MatchInfo> matchList, String countryCode, String nationalPrefix) {
        String filter = search.getValue();

        if (commonMatch(getDisplayName(), filter, isFuzzySearch) || commonMatch(mNickName, filter, isFuzzySearch)) {
            this.addMatchInfo(matchList, new MatchInfo(MatchType.NAME, filter));
            return true;
        }

        if (commonMatch(mCompany, filter, isFuzzySearch) || commonMatch(mJobTitle, filter, isFuzzySearch)) {
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

    public List<TypeValue> getE164PhoneNumbers() {
        return mE164PhoneNumbers;
    }

    public List<TypeValue> getOriginalPhoneNumbers() {
        return mOriginalPhoneNumbers;
    }

    public List<TypeValue> getEmails() {
        return mEmails;
    }

    public List<TypeAddress> getAddresses() {
        return mAddresses;
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
        CloudPersonalContact contact = (CloudPersonalContact) src;
        this.setCloudContactType(contact.getCloudContactType());
        this.setSyncStatus(contact.getSyncStatus());
        this.setUri(contact.getUri());
        this.setAvailability(contact.getAvailability());

        mE164PhoneNumbers.clear();
        mEmails.clear();
        mAddresses.clear();

        List<Contact.TypeValue> e164PhoneNumbers = contact.getE164PhoneNumbers();
        List<Contact.TypeValue> originalPhoneNumbers = contact.getOriginalPhoneNumbers();
        List<Contact.TypeValue> emails = contact.getEmails();
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
    }

    public boolean isEmpty() {
        boolean isEmpty = false;
        do {
            if (!mE164PhoneNumbers.isEmpty()) {
                break;
            }

            if (!mEmails.isEmpty()) {
                break;
            }

            if (!mAddresses.isEmpty()) {
                break;
            }

            //name fields
            if (!isEmpty(mFirstName) || !isEmpty(mMiddleName) || !isEmpty(mLastName) || !isEmpty(mNickName)) {
                break;
            }

            //company, job title
            if (!isEmpty(mCompany) || !isEmpty(mJobTitle)) {
                break;
            }

            //birthday
            if (!isEmpty(mBirthday)) {
                break;
            }

            if (!mWebPages.isEmpty()) {
                break;
            }

            if (!isEmpty(mNotes)) {
                break;
            }

            isEmpty = true;
        } while (false);

        return isEmpty;
    }

    private String valueWithLimitation(String value, int limitation) {
        return TextUtils.isEmpty(value) ? value : (value.length() > limitation) ? value.substring(0, limitation) : value;
    }
}