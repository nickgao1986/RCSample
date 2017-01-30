package com.example.nickgao.contacts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.SparseArray;

import com.example.nickgao.R;
import com.example.nickgao.androidsample11.ContactsUtils;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContact;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactMatcher;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.contacts.adapters.contactsprovider.DevicePersonalContactLoader;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.gao on 1/30/17.
 */

public class GetPersonalContactData {

    private static final String TAG = "[RC]GetLocalContactData";

    public PersonalDetailData mDetailData = new PersonalDetailData();
    private boolean mContactNotExsit = false;
    private Long mContactId;
    private Context mContext;
    private ArrayList<Long> mRawContactArray = new ArrayList<>();

    private final static String CONTENT_VALUES_TYPE = "type";
    private final static String CONTENT_VALUES_DATA = "data";
    private final static String CONTENT_VALUES_LABEL = "label";

    private String mNameFromContactsList = "";
    private String mPhoneNumber;
    Contact mContact = null;

    public GetPersonalContactData(Context context, Contact.ContactType contactType, long contactId, String phoneNumber, String nameFromContactsList) {
        this.mContext = context;
        update(contactType, contactId, phoneNumber, nameFromContactsList);
    }

    public void update(Contact.ContactType contactType, long contactId, String phoneNumber, String nameFromContactsList) {
        this.mContactNotExsit = false;
        this.mNameFromContactsList = nameFromContactsList;
        this.mPhoneNumber = phoneNumber;
        if(contactType == Contact.ContactType.DEVICE) {
            //fix bug AB-11630-Contact details screen opened by Context "View" isn't completedly shown when one group item isn't selected
            mRawContactArray.clear();
            fillRawContactId(contactId);
            this.mDetailData.rawContactId = contactId;
            this.mContactId = contactId;
            getDeviceContactData();
        }else if(contactType == Contact.ContactType.CLOUD_PERSONAL) {
            this.mContactId = contactId;
            getCloudContactData();
        }
    }

    public enum EntryType {
        TYPE_CUSTOM, TYPE_HOME, TYPE_MOBILE, TYPE_WORK, TYPE_FAX_WORK, TYPE_FAX_HOME, TYPE_PAGER, TYPE_OTHER
    }

    public enum ImType {
        PROTOCOL_CUSTOM, PROTOCOL_AIM, PROTOCOL_MSN, PROTOCOL_YAHOO, PROTOCOL_SKYPE, PROTOCOL_QQ,
        PROTOCOL_GOOGLE_TALK, PROTOCOL_ICQ, PROTOCOL_JABBER, PROTOCOL_NETMEETING
    }

    public enum ContactsInfo {
        COMPANY, TITLE, DEPARTMENT, NICKNAME
    }


    public enum ListType {
        EMAIL_TYPE, IM_TYPE, WEBSITE_TYPE, ADDRESS_TYPE, EVENT_TYPE, NOTE_TYPE
    }


    public static class PersonalDetailData {

        public SparseArray<ArrayList<ContentValues>> list = new SparseArray<>();
        public ArrayList<PhoneData> phone_list = new ArrayList<>();

        public String notes = "";
        public SparseArray<String> mPhonesForCompany;
        public boolean isFav;
        public String displayName = "";
        public Bitmap mPhotoBitmap;
        public String[] contactsInfo = { "", "", "", "", "", "", "", "" };
        public Long[] contactsIds = { 0L, 0L, 0L, 0L, 0L };
        public long photoId = 0;
        public long rawContactId;

        public PersonalDetailData() {
        }
    }

    public static class PhoneData {
        public String type;
        public String num;
        public int typeIndex;
        public boolean isCustom = false;
        public boolean isBoldStyle = false;
    }

    static class ContactID {
        public ContactID(long rawContactId, long contactId) {
            this.rawContactId = rawContactId;
            this.contactId = contactId;
        }
        public long rawContactId;
        public long contactId;
    }


    public List<ContactID> getContactIds(long contactId, boolean isRawContactId) {
        List<ContactID> ids = new ArrayList<>();
        Cursor c = null;
        try {

            mContactNotExsit = false;
            //will search by raw contact id or contact id
            c = mContext.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    new String[] { ContactsContract.RawContacts._ID, ContactsContract.RawContacts.CONTACT_ID },
                    isRawContactId? (ContactsContract.RawContacts._ID + " = ? ") : (ContactsContract.RawContacts.CONTACT_ID + " = ? "),
                    new String[] { String.valueOf(contactId)}, null);

            do{

                if (c == null || c.getCount() == 0) {
                    break;

                }

                if(!c.moveToFirst()) {
                    break;
                }

                do{
                    ids.add(new ContactID(c.getLong(0), c.getLong(1)));
                }
                while (c.moveToNext());

            }while(false);

        } catch (java.lang.Throwable ex) {

        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

        return ids;
    }

    public void fillRawContactId(long contactId) {
        List<ContactID> contactIdMatched = getContactIds(contactId, false);
        mContactNotExsit = contactIdMatched.isEmpty();
        if(!mContactNotExsit) {
            for(ContactID id: contactIdMatched) {
                mRawContactArray.add(id.rawContactId);
            }
        }
    }

    public boolean IsContactIdNotFound() {
        return mContactNotExsit;
    }

    private void clearCache() {
        mDetailData.contactsInfo[ContactsInfo.COMPANY.ordinal()] = "";
        mDetailData.contactsInfo[ContactsInfo.TITLE.ordinal()] = "";
        mDetailData.contactsInfo[ContactsInfo.NICKNAME.ordinal()] = "";
        mDetailData.contactsInfo[ContactsInfo.DEPARTMENT.ordinal()] = "";
        if (mDetailData != null && mDetailData.phone_list != null && !mDetailData.phone_list.isEmpty()) {
            mDetailData.phone_list.clear();
        }
        if (mDetailData != null && mDetailData.list != null) {
            mDetailData.list.clear();
        }
        mDetailData.notes = "";
        mDetailData.displayName = "";
        if (mDetailData.mPhotoBitmap != null) {
            try {
                mDetailData.mPhotoBitmap.recycle();
                mDetailData.mPhotoBitmap = null;
            } catch (Exception ex) {
                MktLog.e(TAG, "mDetailData.mPhotoBitmap recycle : exception : " + ex);
            }
        }
    }

//	private void setEventDetailPhoneNumber(String phoneNumber) {
//		this.mPhoneNumber = phoneNumber;
//	}


    private void getCloudContactData() {
        CloudPersonalContact contact = (CloudPersonalContact) ContactsProvider.getInstance().getContact(Contact.ContactType.CLOUD_PERSONAL, mContactId, true);
        if(contact == null) {
            contact = (mContact instanceof CloudPersonalContact)? (CloudPersonalContact)mContact : null;
        }else {
            mContact = contact;
        }

        clearCache();

        ArrayList<PhoneData> phoneLists = new ArrayList<>();
        ArrayList<ContentValues> addressList = new ArrayList<>();
        ArrayList<ContentValues> emailList = new ArrayList<>();
        ArrayList<ContentValues> websiteList = new ArrayList<>();
        ArrayList<ContentValues> imList = new ArrayList<>();
        ArrayList<ContentValues> eventList = new ArrayList<>();
        ArrayList<ContentValues> noteList = new ArrayList<>();


        if(contact != null) {
            fillCloudContactPhoneList(ContactsUtils.orderedPhones(contact.getE164PhoneNumbers()), phoneLists);
            fillCloudContactMailList(contact.getEmails(), emailList);
            fillCloudContactAddressList(contact, addressList);
            fillCloudContactCompanyField(contact);

            fillCloudContactWebsiteList(contact, websiteList);
            fillCloudContactBirthday(contact, eventList);
            fillCloudContactNoteList(contact, noteList);

            mDetailData.phone_list = phoneLists;
            mDetailData.list.put(ListType.EMAIL_TYPE.ordinal(), emailList);
            mDetailData.list.put(ListType.EVENT_TYPE.ordinal(), eventList);
            mDetailData.list.put(ListType.IM_TYPE.ordinal(), imList);
            mDetailData.list.put(ListType.WEBSITE_TYPE.ordinal(), websiteList);
            mDetailData.list.put(ListType.ADDRESS_TYPE.ordinal(), addressList);
            mDetailData.list.put(ListType.NOTE_TYPE.ordinal(), noteList);
        }
    }



    private void fillCloudContactPhoneList(List<Contact.TypeValue> phones, ArrayList<PhoneData> phoneLists) {
        for(int i = 0; i < phones.size(); i++) {
            Contact.TypeValue phone = phones.get(i);
            PhoneData data = new PhoneData();
            data.num = phone.getValue();
          //  data.type = phone.getType();
            if(data != null) {
                phoneLists.add(data);
            }
        }
    }

    private void fillCloudContactMailList(List<Contact.TypeValue> emails, ArrayList<ContentValues> emailList) {
        //test data
        final String emailTag = mContext.getString(R.string.phone_tag_email);
        for(int i = 0; i < emails.size(); i++) {
            Contact.TypeValue email = emails.get(i);
            ContentValues values = new ContentValues();
            values.put(CONTENT_VALUES_TYPE, email.getType());
            values.put(CONTENT_VALUES_DATA, email.getValue());
            String tag;
            try {
                tag = CloudPersonalContact.EmailType.values()[email.getType()].toString();
            }catch (Throwable th) {
                tag = emailTag;
            }
            values.put(CONTENT_VALUES_LABEL, tag);
            emailList.add(values);
        }
    }

    private void fillCloudContactAddressList(CloudPersonalContact contact, ArrayList<ContentValues> addressList) {
        //test data
        /*
        cloudPersonalContactInfo.homeAddress = new Address("China","Fujian","Xiamen","Software Part 2 Wanghai Rd 16#201","361008");
        cloudPersonalContactInfo.businessAddress = new Address("China","","","Software Part 2 Wanghai Rd 16#201","361008");
        cloudPersonalContactInfo.otherAddress = new Address("China","","","Software Part 2 Wanghai Rd 16#201","");
        */

        List<Contact.TypeAddress> addresses = ContactsUtils.orderedAddress(contact.getAddresses());
        for(int i = 0; i < addresses.size(); i++) {
            Contact.TypeAddress address = addresses.get(i);
            ContentValues values = new ContentValues();
            values.put(CONTENT_VALUES_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM);
            values.put(CONTENT_VALUES_DATA, address.getValue().value(true));
            String tag;
            try {
                tag = CloudPersonalContact.AddressType.values()[address.getType()].toString();
            }catch (Throwable th) {
                tag = null;
            }
            values.put(CONTENT_VALUES_LABEL,tag);
            addressList.add(values);
        }
    }

    private void fillCloudContactWebsiteList(CloudPersonalContact item, ArrayList<ContentValues> websiteList) {
        if(!TextUtils.isEmpty(item.getWebPage(0))) {
            ContentValues values = new ContentValues();
            values.put(CONTENT_VALUES_TYPE, ContactsContract.CommonDataKinds.Website.TYPE_CUSTOM);
            values.put(CONTENT_VALUES_DATA, item.getWebPage(0));
            values.put(CONTENT_VALUES_LABEL, CloudPersonalContact.WebPageType.WEB_PAGE.toString());
            websiteList.add(values);
        }
    }


    private void fillCloudContactBirthday(CloudPersonalContact item, ArrayList<ContentValues> others) {
        if(!TextUtils.isEmpty(item.getBirthday())) {
            ContentValues values = new ContentValues();
            values.put(CONTENT_VALUES_TYPE, ContactsContract.CommonDataKinds.Event.TYPE_CUSTOM);
            values.put(CONTENT_VALUES_DATA, item.getBirthday());
            values.put(CONTENT_VALUES_LABEL, CloudPersonalContact.BirthdayType.BIRTHDAY_DAY.toString());
            others.add(values);
        }
    }

    private void fillCloudContactNoteList(CloudPersonalContact item, ArrayList<ContentValues> noteList) {
        if(!TextUtils.isEmpty(item.getNotes())) {
            ContentValues values = new ContentValues();
            values.put(CONTENT_VALUES_DATA, item.getNotes());
            noteList.add(values);
        }
    }

    private void fillCloudContactCompanyField(CloudPersonalContact contact) {
        if (!TextUtils.isEmpty(contact.getCompany())) {
            mDetailData.contactsInfo[ContactsInfo.COMPANY.ordinal()] = contact.getCompany();
        }

        if (!TextUtils.isEmpty(contact.getJobTitle())) {
            mDetailData.contactsInfo[ContactsInfo.TITLE.ordinal()] = contact.getJobTitle();
        }

        if (!TextUtils.isEmpty(contact.getNickName())) {
            mDetailData.contactsInfo[ContactsInfo.NICKNAME.ordinal()] = contact.getNickName();
        }

        if(!TextUtils.isEmpty(contact.getDisplayName())) {
            mDetailData.displayName = contact.getDisplayName();
        }
    }

    private void getDeviceContactData() {
        clearCache();
        ArrayList<PhoneData> phoneLists = new ArrayList<>();

        ArrayList<ContentValues> addressList = new ArrayList<>();
        ArrayList<ContentValues> emailList = new ArrayList<>();
        ArrayList<ContentValues> websiteList = new ArrayList<>();
        ArrayList<ContentValues> imList = new ArrayList<>();
        ArrayList<ContentValues> eventList = new ArrayList<>();
        ArrayList<ContentValues> noteList = new ArrayList<>();

        for (int i = 0; i < mRawContactArray.size(); i++) {
            getData(mRawContactArray.get(i), phoneLists, addressList, emailList, websiteList, imList, eventList, noteList);
        }
//		mDetailData.cv_list.put(ListType.PHONE_TYPE.ordinal(), 	phoneLists);
        mDetailData.phone_list = phoneLists;

        mDetailData.list.put(ListType.EMAIL_TYPE.ordinal(), emailList);
        mDetailData.list.put(ListType.EVENT_TYPE.ordinal(), eventList);
        mDetailData.list.put(ListType.IM_TYPE.ordinal(), imList);
        mDetailData.list.put(ListType.WEBSITE_TYPE.ordinal(), websiteList);
        mDetailData.list.put(ListType.ADDRESS_TYPE.ordinal(), addressList);
        mDetailData.list.put(ListType.NOTE_TYPE.ordinal(), noteList);

    }

    private void getData(long rawContentId, ArrayList<PhoneData> phoneLists, ArrayList<ContentValues> addressList, ArrayList<ContentValues> emailList
            , ArrayList<ContentValues> websiteList, ArrayList<ContentValues> imList, ArrayList<ContentValues> eventList
            , ArrayList<ContentValues> noteList) {
        ContentResolver resolver = mContext.getContentResolver();

        String nickname = "";
        String company = "";
        String title = "";
        String department = "";
        String displayName = "";
        Cursor cursor = null;
        try {
            cursor = resolver.query(ContactsContract.Data.CONTENT_URI,
                    new String[] { ContactsContract.Data.DATA1, ContactsContract.Data.DATA2, ContactsContract.Data.DATA3, ContactsContract.Data.DATA4, "mimetype", ContactsContract.Data._ID, ContactsContract.Data.DATA5, ContactsContract.Data.DATA6, ContactsContract.Data.DATA15 },
                    ContactsContract.Data.RAW_CONTACT_ID + " = ?",
                    new String[] { String.valueOf(rawContentId) }, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    String data1 = cursor.getString(0);
                    String data2 = cursor.getString(1);
                    String data3 = cursor.getString(2);
                    String data4 = cursor.getString(3);
                    String mimetype = cursor.getString(4);
                    Long id = cursor.getLong(5);
                    String data5 = cursor.getString(6);
                    String data6 = cursor.getString(7);
                    if (ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        company = data1;
                        title = data4;
                        department = data5;
                    } else if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        // data3 last_name data2 first_name
                        displayName = data1;
                    } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        // data1 email_address data2 type data3 label
                        //AB-9219 [HTC Evo 4G]Updated info won't be synchronized to RC contact detail screen if modify a contact created by 3rd party Contact App using Native Contact
                        int type = ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
                        if (!TextUtils.isEmpty(data2)) {
                            type = Integer.parseInt(data2);
                        }
                        String data = data1;
                        String label = data3;
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        ContentValues values = new ContentValues();
                        values.put(CONTENT_VALUES_TYPE, type);
                        values.put(CONTENT_VALUES_DATA, data);
                        values.put(CONTENT_VALUES_LABEL, label);
                        emailList.add(values);
                    } else if (ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        // data1 email_address data2 type data3 label
                        String data = data1;
                        String type = data5;
                        String label = data6;
                        if (TextUtils.isEmpty(data) || TextUtils.isEmpty(type)) {
                            continue;
                        }
                        ContentValues values = new ContentValues();
                        values.put(CONTENT_VALUES_TYPE, type);
                        values.put(CONTENT_VALUES_DATA, data);
                        values.put(CONTENT_VALUES_LABEL, label);
                        try {
                            Integer typeObject = values.getAsInteger(CONTENT_VALUES_TYPE);
                            if(typeObject == null) {
                                continue;
                            }
                        } catch (Throwable th) {
                            continue;
                        }
                        imList.add(values);
                    } else if (ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        // data1 website data2 type data3 label
                        int type = ContactsContract.CommonDataKinds.Website.TYPE_OTHER;
                        if (!TextUtils.isEmpty(data2)) {
                            type = Integer.parseInt(data2);
                        }
                        String data = data1;
                        String label = data3;
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        ContentValues values = new ContentValues();
                        values.put(CONTENT_VALUES_TYPE, type);
                        values.put(CONTENT_VALUES_DATA, data);
                        values.put(CONTENT_VALUES_LABEL, label);
                        websiteList.add(values);
                    } else if (ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        // data1 START_DATE data2 type data3 label
                        int type = ContactsContract.CommonDataKinds.Event.TYPE_OTHER;
                        if (!TextUtils.isEmpty(data2)) {
                            type = Integer.valueOf(data2);
                        }
                        String data = data1;
                        String label = data3;
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        ContentValues values = new ContentValues();
                        values.put(CONTENT_VALUES_TYPE, type);
                        values.put(CONTENT_VALUES_DATA, data);
                        values.put(CONTENT_VALUES_LABEL, label);
                        eventList.add(values);

                    } else if (ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        //mDetailData.notes = data1;
                        if (!TextUtils.isEmpty(data1)) {
                            ContentValues values = new ContentValues();
                            values.put(CONTENT_VALUES_DATA, data1);
                            noteList.add(values);
                        }
                    } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        int type = ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER;
                        if (!TextUtils.isEmpty(data2)) {
                            type = Integer.valueOf(data2);
                        }
                        String data = data1;
                        String label = data3;
                        if (TextUtils.isEmpty(data)) {
                            continue;
                        }
                        ContentValues values = new ContentValues();
                        values.put(CONTENT_VALUES_TYPE, type);
                        values.put(CONTENT_VALUES_DATA, data);
                        values.put(CONTENT_VALUES_LABEL, label);
                        addressList.add(values);

                    } else if (ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        nickname = data1;
                    } else if (ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        BitmapFactory.Options options = null;
                        byte[] photoData = cursor.getBlob(8);
                        try {
                            if(photoData != null) {
                                if (options == null) {
                                    options = new BitmapFactory.Options();
                                }
                                options.inTempStorage = new byte[16 * 1024];
                                options.inSampleSize = 1;
                                mDetailData.mPhotoBitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length, options);
                            }
                        } catch (java.lang.Throwable error) {
                            if (LogSettings.MARKET) {
                            }
                        }
                    } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        try {
                            String number = DevicePersonalContactLoader.normalizeNumber(data1);
                            int type = ContactsContract.CommonDataKinds.Phone.TYPE_OTHER;
                            if (!TextUtils.isEmpty(data2)) {
                                type = Integer.valueOf(data2);
                            }
                            String type_str = getPhoneNumberTag(mContext, type);
                            String label = data3;
                            long phoneId = id;

                            PhoneData data = new PhoneData();

                            if (!TextUtils.isEmpty(mPhoneNumber)) {
                                mPhoneNumber = mPhoneNumber.replaceAll("[ -]", "");
                            }

                            if (!TextUtils.isEmpty(mPhoneNumber) && ContactMatcher.isFullE164NumberMatch(mPhoneNumber, number)) {
                                data.isBoldStyle = true;
                                if (type == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) {
                                    data.type = label;
                                    data.typeIndex = type;
                                    data.isCustom = true;
                                    data.num = number;
                                    phoneLists.add(0, data);
                                } else {
                                    data.type = type_str;
                                    data.typeIndex = type;
                                    data.isCustom = false;
                                    data.num = number;
                                    phoneLists.add(0, data);
                                }
                                continue;
                            }
                            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) {
                                data.type = label;
                                data.typeIndex = type;
                                data.isCustom = true;
                                data.num = number;
                                phoneLists.add(data);
                            } else {
                                data.type = type_str;
                                data.typeIndex = type;
                                data.isCustom = false;
                                data.num = number;
                                phoneLists.add(data);
                            }
                        } catch (java.lang.Throwable error) {

                        }
                    }// end phone cursor
                }
            }

        } catch (java.lang.Throwable ex) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (!TextUtils.isEmpty(mNameFromContactsList) && mNameFromContactsList.equals(displayName)) {
            if (!TextUtils.isEmpty(company)) {
                mDetailData.contactsInfo[ContactsInfo.COMPANY.ordinal()] = company;
            }

            if (!TextUtils.isEmpty(title)) {
                mDetailData.contactsInfo[ContactsInfo.TITLE.ordinal()] = title;
            }

            if (!TextUtils.isEmpty(department)) {
                mDetailData.contactsInfo[ContactsInfo.DEPARTMENT.ordinal()] = department;
            }

            if (!TextUtils.isEmpty(nickname)) {
                mDetailData.contactsInfo[ContactsInfo.NICKNAME.ordinal()] = nickname;
            }

            mDetailData.displayName = displayName;
        }
    }


    public String getPhoneNumberTag(Context context, long tag) {
        switch ((int) tag) {
            case (ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM):
                return context.getString(R.string.phone_tag_custom);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE):
                return context.getString(R.string.phone_tag_mobile);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_HOME):
                return context.getString(R.string.phone_tag_home);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_WORK):
                return context.getString(R.string.phone_tag_work);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_OTHER):
                return context.getString(R.string.phone_tag_other);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_MAIN):
                return context.getString(R.string.phone_tag2_main);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_CAR):
                return context.getString(R.string.phone_tag2_car);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN):
                return context.getString(R.string.phone_tag2_company_main);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE):
                return context.getString(R.string.phone_tag2_work_mobile);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK):
                return context.getString(R.string.phone_tag_fax_work);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT):
                return context.getString(R.string.phone_tag2_assistant);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK):
                return context.getString(R.string.phone_tag2_callback);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME):
                return context.getString(R.string.phone_tag_fax_home);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_ISDN):
                return context.getString(R.string.phone_tag2_isdn);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_MMS):
                return context.getString(R.string.phone_tag2_mms);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX):
                return context.getString(R.string.phone_tag2_other_fax);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_PAGER):
                return context.getString(R.string.phone_tag_pager);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER):
                return context.getString(R.string.phone_tag2_work_pager);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_RADIO):
                return context.getString(R.string.phone_tag2_radio);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_TELEX):
                return context.getString(R.string.phone_tag2_telex);
            case (ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD):
                return context.getString(R.string.phone_tag2_tty_tdd);
        }
        return context.getString(R.string.phone_tag_other);
    }



//	private boolean isPhoneNumberEquals(String localNumber) {
//		if(TextUtils.isEmpty(localNumber)) {
//			return false;
//		}
//		localNumber = localNumber.replaceAll("[ -]", "");
//		String loginNumber = RCMProviderHelper.getLoginNumber(mContext);
//		PhoneNumber loginNumberParsed = PhoneUtils.getParser().parse(loginNumber);
//		String countryCode = loginNumberParsed.countryCode;
//		if (!localNumber.startsWith("+") && !localNumber.startsWith(countryCode) && !countryCode.startsWith(localNumber)) {
//			localNumber = loginNumberParsed.countryCode + localNumber;
//		}
//
//		if (!this.mPhoneNumber.startsWith("+") && !this.mPhoneNumber.startsWith(countryCode) && !countryCode.startsWith(this.mPhoneNumber)) {
//			this.mPhoneNumber = loginNumberParsed.countryCode + this.mPhoneNumber;
//		}
//
//		if (localNumber.equals(this.mPhoneNumber)) {
//			return true;
//		} else {
//			return false;
//		}
//
//	}


    /**
     * ****************************************************************************************************************
     * get Type
     * <p/>
     * *****************************************************************************************************************
     */

    public static String getAddressType(final Context context, int type, String label) {
        String data = context.getResources().getString(R.string.phone_tag_home);
        switch (type) {
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM:
                if (TextUtils.isEmpty(label)) {
                    data = context.getResources().getString(R.string.phone_tag_custom);
                } else {
                    data = label;
                }
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                data = context.getResources().getString(R.string.phone_tag_home);
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                data = context.getResources().getString(R.string.phone_tag_work);
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
                data = context.getResources().getString(R.string.phone_tag_other);
                break;
        }
        return data;
    }

    public static String getEmailType(final Context context, int type, String label) {
        String data = context.getResources().getString(R.string.phone_tag_home);
        switch (type) {
            case ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM:
                if (TextUtils.isEmpty(label)) {
                    data = context.getResources().getString(R.string.phone_tag_custom);
                } else {
                    data = label;
                }
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                data = context.getResources().getString(R.string.phone_tag_home);
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                data = context.getResources().getString(R.string.phone_tag_work);
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                data = context.getResources().getString(R.string.phone_tag_other);
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                data = context.getResources().getString(R.string.phone_tag_mobile);
                break;
        }
        return data;
    }

    public String getImType(int type, String label) {
        String data = mContext.getResources().getString(R.string.contact_im_type_menu_aim);
        switch (type) {
            case -1:
                if (TextUtils.isEmpty(label)) {
                    data = mContext.getResources().getString(R.string.phone_tag_custom);
                } else {
                    data = label;
                }
                break;
            case 0:
                break;
            case 1:
                data = mContext.getResources().getString(R.string.contact_im_type_menu_windows_live);
                break;
            case 2:
                data = mContext.getResources().getString(R.string.contact_im_type_menu_yahoo);
                break;
            case 3:
                data = mContext.getResources().getString(R.string.contact_im_type_menu_skype);
                break;
            case 4:
                data = mContext.getResources().getString(R.string.contact_im_type_menu_qq);
                break;
            case 5:
                data = mContext.getResources().getString(R.string.contact_im_type_menu_google_talk);
                break;
            case 6:
                data = mContext.getResources().getString(R.string.contact_im_type_menu_icq);
                break;
            case 7:
                data = mContext.getResources().getString(R.string.contact_im_type_menu_jabber);
                break;
            case 8:
                data = mContext.getResources().getString(R.string.contact_im_type_menu_netmeeting);
                break;
        }
        return data;
    }

    public String getWebSiteType(int type, String label) {
        String data = mContext.getResources().getString(R.string.contact_website_type_menu_homepage);
        switch (type) {
            case 0:
                if (TextUtils.isEmpty(label)) {
                    data = mContext.getResources().getString(R.string.phone_tag_custom);
                } else {
                    data = label;
                }
                break;
            case 1:
                data = mContext.getResources().getString(R.string.contact_website_type_menu_homepage);
                break;
            case 2:
                data = mContext.getResources().getString(R.string.contact_website_type_menu_blog);
                break;
            case 3:
                data = mContext.getResources().getString(R.string.contact_website_type_menu_profile);
                break;
            case 4:
                data = mContext.getResources().getString(R.string.contact_website_type_menu_home);
                break;
            case 5:
                data = mContext.getResources().getString(R.string.contact_website_type_menu_work);
                break;
            case 6:
                data = mContext.getResources().getString(R.string.contact_website_type_menu_ftp);
                break;
            case 7:
                data = mContext.getResources().getString(R.string.contact_website_type_menu_other);
                break;
        }
        return data;
    }

    public String getEventType(int type, String label) {
        String data = mContext.getResources().getString(R.string.contact_events_type_menu_anniversary);
        switch (type) {
            case 0:
                if (TextUtils.isEmpty(label)) {
                    data = mContext.getResources().getString(R.string.phone_tag_custom);
                } else {
                    data = label;
                }
                break;
            case 1:
                data = mContext.getResources().getString(R.string.contact_events_type_menu_anniversary);
                break;
            case 2:
                data = mContext.getResources().getString(R.string.contact_events_type_menu_other);
                break;
            case 3:
                data = mContext.getResources().getString(R.string.contact_events_type_menu_birthday);
                break;
        }
        return data;
    }

}
