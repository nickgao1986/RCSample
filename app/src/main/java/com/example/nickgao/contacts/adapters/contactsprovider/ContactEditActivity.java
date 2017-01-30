package com.example.nickgao.contacts.adapters.contactsprovider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nickgao.R;
import com.example.nickgao.androidsample11.ContactsUtils;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.service.model.contact.Address;
import com.example.nickgao.utils.DateUtils;
import com.example.nickgao.utils.widget.HeaderViewBase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by nick.gao on 1/29/17.
 */

public class ContactEditActivity extends Activity implements HeaderViewBase.HeaderButtons,View.OnClickListener{


    private static final String TAG = "[RC]ContactEditActivity";

    private static final String CONTACT_EDIT_TYPE = "CONTACT_EDIT_TYPE";
    private static final String CONTACT_ID = "CONTACT_ID";

    /**
     * Cloud contact parameter type
     */
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String DISPLAY_NAME = "displayName";
    public static final int CLOUD_CONTACT_PARAMETER_NAME = 1;
    public static final int CLOUD_CONTACT_PARAMETER_NUMBER = 2;
    public static final int CLOUD_CONTACT_PARAMETER_EMAIL = 3;
    public static final int CLOUD_CONTACT_PARAMETER_LINK = 4;

    public static final int RESULT = 1;

    private HeaderViewBase mHeader;
    private NamesField mNamesField;
    private LinearLayout mPhoneList;
    private LinearLayout mEmailList;
    private LinearLayout mAddressList;
    private LinearLayout mWebPageList;
    private LinearLayout mBirthdayList;
    private OtherField mOtherField;

    private View mAddPhone;
    private View mAddEmail;
    private View mAddWebPage;
    private View mAddAddress;
    private View mAddBirthday;
    private View mAddField;
    private View mDelete;

    static final int ITEM_MANAGER_ID_PHONE = 1;
    static final int ITEM_MANAGER_ID_EMAIL = 2;
    static final int ITEM_MANAGER_ID_WEBPAGE = 3;
    static final int ITEM_MANAGER_ID_ADDRESS = 4;
    static final int ITEM_MANAGER_ID_BIRTHDAY = 5;
    static final int ITEM_MANAGER_ID_OTHER_FIELD = 6;
    static final int[] OTHER_FIELD_SIZE = {1, 1, 1};

    private int mTagToTraceView = 1;

    private long mContactId;

    protected ItemManager<CloudPersonalContact.AppCloudPhoneType> mPhoneItemManager;
    protected ItemManager<CloudPersonalContact.EmailType> mEmailItemManager;
    protected ItemManager<CloudPersonalContact.WebPageType> mWebPageItemManager;
    protected ItemManager<CloudPersonalContact.AddressType> mAddressItemManager;
    protected ItemManager<CloudPersonalContact.BirthdayType> mBirthdayItemManager;
    protected ItemManager<OtherFiledType> mOtherFieldManager;

    protected int mContactEditType = RequestInfoStorage.REST_CREATE_SINGLE_CONTACT;
    protected CloudPersonalContact mEditContact;



    public static void createNewContact(Context context, String name, int type, String value) {
        Intent intent = new Intent(context, ContactEditActivity.class);
        intent.putExtra(CONTACT_EDIT_TYPE, RequestInfoStorage.REST_CREATE_SINGLE_CONTACT);
        intent.putExtra(DISPLAY_NAME, name);
        intent.putExtra(TYPE, type);
        intent.putExtra(VALUE, value);
        context.startActivity(intent);
    }

    public static void editContact(Context context, long contactId) {
        Intent intent = new Intent(context, ContactEditActivity.class);
        intent.putExtra(CONTACT_EDIT_TYPE, RequestInfoStorage.REST_UPDATE_CONTACT);
        intent.putExtra(CONTACT_ID, contactId);
        context.startActivity(intent);
    }

    public static void editContactForResult(Activity context, long contactId) {
        Intent intent = new Intent(context, ContactEditActivity.class);
        intent.putExtra(CONTACT_EDIT_TYPE, RequestInfoStorage.REST_UPDATE_CONTACT);
        intent.putExtra(CONTACT_ID, contactId);
        context.startActivityForResult(intent, RESULT);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhoneItemManager = new ItemManager(ITEM_MANAGER_ID_PHONE, CloudPersonalContact.AppCloudPhoneType.values(), CloudPersonalContact.ALL_PHONE_SIZE);
        mEmailItemManager = new ItemManager(ITEM_MANAGER_ID_EMAIL, CloudPersonalContact.EmailType.values(), CloudPersonalContact.ALL_EMAIL_SIZE);
        mWebPageItemManager = new ItemManager(ITEM_MANAGER_ID_WEBPAGE, CloudPersonalContact.WebPageType.values(), CloudPersonalContact.ALL_WEB_PAGE_SIZE);
        mAddressItemManager = new ItemManager(ITEM_MANAGER_ID_ADDRESS, CloudPersonalContact.AddressType.values(), CloudPersonalContact.ALL_ADDRESS_SIZE);
        mBirthdayItemManager = new ItemManager(ITEM_MANAGER_ID_BIRTHDAY, CloudPersonalContact.BirthdayType.values(), CloudPersonalContact.ALL_BIRTHDAY_SIZE);
        mOtherFieldManager = new ItemManager<>(ITEM_MANAGER_ID_OTHER_FIELD, OtherFiledType.values(), OTHER_FIELD_SIZE);

        setContentView(R.layout.contact_edit);

        //init views
        mHeader = (HeaderViewBase) findViewById(R.id.contactEditorHeader);
        mHeader.setButtonsClickCallback(this);

        mNamesField = new NamesField(this);
        mOtherField = new OtherField(this);
        mPhoneList = (LinearLayout) this.findViewById(R.id.phoneList);
        mEmailList = (LinearLayout) this.findViewById(R.id.emailList);
        mWebPageList = (LinearLayout) this.findViewById(R.id.webPageList);
        mAddressList = (LinearLayout) this.findViewById(R.id.addressList);
        mBirthdayList = (LinearLayout) this.findViewById(R.id.birthdayList);

        mAddPhone = this.findViewById(R.id.addPhone);
        TextView tag = (TextView) mAddPhone.findViewById(R.id.item_tag);
        tag.setText(getResources().getText(R.string.add_phone));
        mAddPhone.setOnClickListener(this);

        mAddEmail = this.findViewById(R.id.addEmail);
        tag = (TextView) mAddEmail.findViewById(R.id.item_tag);
        tag.setText(getResources().getText(R.string.add_email));
        mAddEmail.setOnClickListener(this);

        mAddWebPage = this.findViewById(R.id.addWebPage);
        tag = (TextView) mAddWebPage.findViewById(R.id.item_tag);
        tag.setText(getResources().getText(R.string.add_web_page));
        mAddWebPage.setOnClickListener(this);

        mAddAddress = this.findViewById(R.id.addAddress);
        tag = (TextView) mAddAddress.findViewById(R.id.item_tag);
        tag.setText(getResources().getText(R.string.add_address));
        mAddAddress.setOnClickListener(this);

        mAddBirthday = this.findViewById(R.id.addBirthday);
        tag = (TextView) mAddBirthday.findViewById(R.id.item_tag);
        tag.setText(getResources().getText(R.string.add_birthday));
        mAddBirthday.setOnClickListener(this);

        mAddField = this.findViewById(R.id.addField);
        tag = (TextView) mAddField.findViewById(R.id.item_tag);
        tag.setText(getResources().getText(R.string.add_field));
        mAddField.setOnClickListener(this);

        mDelete = findViewById(R.id.delete);
        mDelete.setOnClickListener(this);

        initData(getIntent());
    }


    private void setText(EditText editText, String value) {
        editText.setText(value);
        if(!TextUtils.isEmpty(value)) {
            editText.setSelection(value.length());
        }
    }




    public static class ItemManager<T> {
        private int mId;
        private TypeItem<T>[] mAllTypeItems;

        public ItemManager(int id, T[] list, int[] sizes) {
            this.mId = id;
            this.mAllTypeItems = new TypeItem[list.length];
            for (int i = 0; i < list.length; i++) {
                T type = list[i];
                this.mAllTypeItems[i] = new TypeItem(sizes[i], type);
            }
        }

        public int getId() {
            return this.mId;
        }

        public TypeItem[] getTypeItems() {
            return this.mAllTypeItems;
        }

        public void resetViewWidth(int id) {
            int maxWidth = 0;
            //find the max width.
            for (TypeItem<T> item : this.mAllTypeItems) {
                List<View> views = item.getItems();
                for (View view : views) {
                    TextView type = (TextView) view.findViewById(id);
                    if (type != null) {
                        int w  = (int)type.getPaint().measureText(type.getText().toString()) + type.getPaddingLeft() + type.getPaddingRight();
                        type.setWidth(w);
                        if (maxWidth < w) {
                            maxWidth = w;
                        }
                    }
                }
            }

            for (TypeItem<T> item : this.mAllTypeItems) {
                List<View> views = item.getItems();
                for (View view : views) {
                    TextView type = (TextView) view.findViewById(id);
                    if (type != null) {
                        type.setWidth(maxWidth);
                    }
                }
            }
        }

        public T add(View view) {
            for (TypeItem<T> item : this.mAllTypeItems) {
                if (item.add(view)) {
                    return item.getType();
                }
            }
            return null;
        }

        public T add(int index, View view) {
            if (index >= 0 && index < this.mAllTypeItems.length) {
                TypeItem<T> item = this.mAllTypeItems[index];
                if (item.add(view)) {
                    return item.getType();
                }
            }
            return null;
        }

        public boolean add(T type, View view) {
            for (TypeItem<T> item : this.mAllTypeItems) {
                if (item.getType() == type) {
                    return item.add(view);
                }
            }
            return false;
        }

        public boolean isAvailable() {
            for (TypeItem item : this.mAllTypeItems) {
                if (item.isAvailable()) {
                    return true;
                }
            }
            return false;
        }

        public boolean remove(View view) {
            for (TypeItem<T> item : this.mAllTypeItems) {
                if (item.remove(view)) {
                    return true;
                }
            }
            return false;
        }

        public View changeType(int targetViewId, T type) {
            View targetView = null;
            for (TypeItem<T> item : this.mAllTypeItems) {
                if (item.getType() != type) {
                    List<View> views = item.getItems();
                    for (View view : views) {
                        if ((Integer) view.getTag() == targetViewId) {
                            targetView = view;
                            item.remove(view);
                            break;
                        }
                    }
                }
            }

            if (targetView != null && add(type, targetView)) {
                return targetView;
            }

            return null;
        }

        public boolean[] toTypeEnableArray() {
            boolean[] array = new boolean[this.mAllTypeItems.length];
            for (int i = 0; i < this.mAllTypeItems.length; i++) {
                array[i] = this.mAllTypeItems[i].isAvailable();
            }
            return array;
        }

        public String[] toTypeDataArray() {
            String[] array = new String[this.mAllTypeItems.length];
            for (int i = 0; i < this.mAllTypeItems.length; i++) {
                array[i] = this.mAllTypeItems[i].getType().toString();
            }
            return array;
        }

        public static class TypeItem<T> {
            int mMax;
            List<View> mItems;
            T mType;

            public TypeItem(int max, T type) {
                this.mMax = max;
                this.mItems = new ArrayList<>();
                this.mType = type;
            }

            public boolean add(View view) {
                if (this.mItems.size() < this.mMax) {
                    this.mItems.add(view);
                    return true;
                }
                return false;
            }

            public boolean remove(View view) {
                for (View vi : this.mItems) {
                    if (vi.equals(view)) {
                        this.mItems.remove(view);
                        return true;
                    }
                }
                return false;
            }

            public boolean isAvailable() {
                return this.mItems.size() < this.mMax;
            }

            public List<View> getItems() {
                return this.mItems;
            }

            public T getType() {
                return this.mType;
            }
        }
    }

    private class TypeSelection implements View.OnClickListener {
        private final Activity mContext;
        private final ItemManager mItemManager;
        private final View mViewHolder;

        public TypeSelection(Activity context, ItemManager manager, View itemView) {
            this.mContext = context;
            this.mItemManager = manager;
            this.mViewHolder = itemView;
        }

        @Override
        public void onClick(View v) {
//            ContactTypeSelectionActivity.startContactTypeSelectionActivity(this.mContext, (Integer) this.mViewHolder.getTag(), this.mItemManager.getId(),
//                    this.mItemManager.toTypeDataArray(), this.mItemManager.toTypeEnableArray(), (Integer) v.getTag(), R.string.label);
        }
    }

    private class NamesField {
        private EditText mFirstName;
        private EditText mMiddleName;
        private EditText mLastName;
        private EditText mNickname;

        public NamesField(Activity root) {
            this.mFirstName = (EditText) root.findViewById(R.id.firstName);
            this.mMiddleName = (EditText) root.findViewById(R.id.middleName);
            this.mLastName = (EditText) root.findViewById(R.id.lastName);
            this.mNickname = (EditText) root.findViewById(R.id.nickName);
        }

        public String getFirstName() {
            return this.mFirstName == null ? null : this.mFirstName.getText().toString().trim();
        }

        public void setFirstName(String firstName) {
            if (this.mFirstName != null && !TextUtils.isEmpty(firstName)) {
                setText(this.mFirstName,firstName);
            }
        }

        public String getMiddleName() {
            return this.mMiddleName == null ? null : this.mMiddleName.getText().toString().trim();
        }

        public void setMiddleName(String middleName) {
            if (this.mMiddleName != null && !TextUtils.isEmpty(middleName)) {
                setText(this.mMiddleName, middleName);
            }
        }

        public String getLastName() {
            return this.mLastName == null ? null : this.mLastName.getText().toString().trim();
        }

        public void setLastName(String lastName) {
            if (this.mLastName != null && !TextUtils.isEmpty(lastName)) {
                setText(this.mLastName, lastName);
            }
        }

        public String getNickname() {
            return this.mNickname == null ? null : this.mNickname.getText().toString().trim();
        }

        public void setNickname(String nickname) {
            if (this.mNickname != null && !TextUtils.isEmpty(nickname)) {
                this.mNickname.setVisibility(View.VISIBLE);
                setText(this.mNickname, nickname);
            }
        }

    }

    private class OtherField {
        private EditText mCompany;
        private EditText mJobTitle;
        private EditText mNotes;

        public OtherField(Activity root) {
            this.mCompany = (EditText) root.findViewById(R.id.company);
            this.mJobTitle = (EditText) root.findViewById(R.id.jobTitle);
            this.mNotes = (EditText) root.findViewById(R.id.notes);
        }

        public String getCompany() {
            return this.mCompany == null ? null : this.mCompany.getText().toString().trim();
        }

        public void setCompany(String company) {
            if (this.mCompany != null && !TextUtils.isEmpty(company)) {
                setText(this.mCompany, company);
            }
        }

        public String getJobTitle() {
            return this.mJobTitle == null ? null : this.mJobTitle.getText().toString().trim();
        }

        public void setJobTitle(String jobTitle) {
            if (this.mJobTitle != null && !TextUtils.isEmpty(jobTitle)) {
                this.mJobTitle.setVisibility(View.VISIBLE);
                setText(this.mJobTitle,jobTitle);
            }
        }

        public String getNotes() {
            return this.mNotes == null ? null : this.mNotes.getText().toString().trim();
        }

        public void setNotes(String notes) {
            if (this.mNotes != null && !TextUtils.isEmpty(notes)) {
                setText(this.mNotes,notes);
            }
        }

    }

    enum OtherFiledType {
        MiddleName {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.middle_name_field);
            }
        },

        NickName {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.nick_name_field);
            }
        },

        JobTitle {
            @Override
            public String toString() {
                return RingCentralApp.getContextRC().getResources().getString(R.string.job_title_field);
            }
        },
    }

    private void initData(Intent intent) {
        if (intent != null) {
            mContactEditType = intent.getIntExtra(CONTACT_EDIT_TYPE, RequestInfoStorage.REST_CREATE_SINGLE_CONTACT);
            switch (mContactEditType) {
                case RequestInfoStorage.REST_CREATE_SINGLE_CONTACT: {
                    String displayName = intent.getStringExtra(DISPLAY_NAME);
                    int type = intent.getIntExtra(TYPE, CLOUD_CONTACT_PARAMETER_NAME);
                    String value = intent.getStringExtra(VALUE);
                    createNewContactInit(displayName, type, value);
                }
                break;
                case RequestInfoStorage.REST_UPDATE_CONTACT: {
                    mContactId = intent.getLongExtra(CONTACT_ID, 0);
                    mHeader.setText(R.string.menu_editContact);
                    editContactInit(mContactId);
                    mDelete.setVisibility(View.VISIBLE);
                }
                break;
                case RequestInfoStorage.REST_IMPORT_SINGLE_CONTACT:
                    break;
                default:
                    break;
            }
        }
    }

    private void createNewContactInit(String displayName, int type, String value) {
//        initNamesField(TextUtils.isEmpty(displayName) ? "" : displayName, "", "", "");
        if (!TextUtils.isEmpty(value)) {
            switch (type) {
                case CLOUD_CONTACT_PARAMETER_NAME:
                    break;
                case CLOUD_CONTACT_PARAMETER_NUMBER: {
                    List<Contact.TypeValue> phoneList = new ArrayList<>();
                    phoneList.add(new Contact.TypeValue(CloudPersonalContact.PhoneType.MOBILE_PHONE.ordinal(), value));
                    initPhoneItemList(phoneList);
                }
                break;
                case CLOUD_CONTACT_PARAMETER_EMAIL: {
                    List<Contact.TypeValue> emailList = new ArrayList<>();
                    emailList.add(new Contact.TypeValue(CloudPersonalContact.EmailType.EMAIL.ordinal(), value));
                    initEmailItemList(emailList);
                }
                break;
                case CLOUD_CONTACT_PARAMETER_LINK: {
                    List<Contact.TypeValue> webPageList = new ArrayList<>();
                    webPageList.add(new Contact.TypeValue(CloudPersonalContact.WebPageType.WEB_PAGE.ordinal(), value));
                    initWebPageList(webPageList);
                }
                break;
                default:
                    break;
            }
        }
    }

    private void editContactInit(long contactId) {
        mEditContact = (CloudPersonalContact) ContactsProvider.getInstance().getContact(Contact.ContactType.CLOUD_PERSONAL, contactId, true);
        CloudPersonalContact contact = mEditContact;
        if (contact != null) {
            initNamesField(contact.getFirstName(), contact.getMiddleName(), contact.getLastName(), contact.getNickName());
            initOtherField(contact.getCompany(), contact.getJobTitle(), contact.getNotes());
            initPhoneItemList(contact.getE164PhoneNumbers());
            initEmailItemList(contact.getEmails());
            initAddressItemList(contact.getAddresses());
            String webPage = contact.getWebPage(0);
            if (!TextUtils.isEmpty(webPage)) {
                List<Contact.TypeValue> webPageList = new ArrayList<>();
                webPageList.add(new Contact.TypeValue(CloudPersonalContact.WebPageType.WEB_PAGE.ordinal(), webPage));
                initWebPageList(webPageList);
            }

            String birthday = contact.getBirthday();
            if (!TextUtils.isEmpty(birthday)) {
                List<Contact.TypeValue> birthdayList = new ArrayList<>();
                birthdayList.add(new Contact.TypeValue(CloudPersonalContact.BirthdayType.BIRTHDAY_DAY.ordinal(), birthday));
                initBirthdayList(birthdayList);
            }
        }else {
            contactNotFound();
            this.finish();
        }
    }

    private void contactNotFound() {
        Toast.makeText(this, R.string.cloud_contact_not_found, Toast.LENGTH_LONG).show();
    }


    private void initNamesField(String firstName, String middleName, String lastName, String nickname) {
        if (mNamesField == null) {
            throw new IllegalArgumentException("name fields of controls did not be initial.");
        }

        mNamesField.setFirstName(firstName);
        if (!TextUtils.isEmpty(middleName)) {
            mNamesField.mMiddleName.setVisibility(View.VISIBLE);
            onAddOtherFieldItem(OtherFiledType.MiddleName, mNamesField.mMiddleName);
            mNamesField.setMiddleName(middleName);
        }
        mNamesField.setLastName(lastName);
        if (!TextUtils.isEmpty(nickname)) {
            mNamesField.mNickname.setVisibility(View.VISIBLE);
            onAddOtherFieldItem(OtherFiledType.NickName, mNamesField.mNickname);
            mNamesField.setNickname(nickname);
        }
    }

    private void initOtherField(String company, String jobTitle, String notes) {
        if (mOtherField == null) {
            throw new IllegalArgumentException("other fields of controls did not be initial.");
        }
        mOtherField.setCompany(company);
        if (!TextUtils.isEmpty(jobTitle)) {
            mOtherField.mJobTitle.setVisibility(View.VISIBLE);
            mOtherField.setJobTitle(jobTitle);
            onAddOtherFieldItem(OtherFiledType.JobTitle, mOtherField.mJobTitle);
        }
        mOtherField.setNotes(notes);
    }

    public void initWebPageList(List<Contact.TypeValue> webPageList) {
        for (Contact.TypeValue webPage : webPageList) {
            onAddWebPageItem(webPage);
        }
    }

    public void initBirthdayList(List<Contact.TypeValue> birthdayList) {
        for (Contact.TypeValue birthday : birthdayList) {
            onAddBirthdayItem(birthday);
        }
    }


    @Override
    public void onRightButtonClicked() {
           //onSaveContact();
    }

    @Override
    public void onLeftButtonClicked() {
           this.finish();
    }

    @Override
    public void onMenuButtonClicked() {

    }

    @Override
    public void onRightFirstButtonClicked() {

    }



    /**
     * translate phoneType to appCloudPhoneType, then add into views
     *
     * @param phoneList
     */
    private void initPhoneItemList(List<Contact.TypeValue> phoneList) {
        List<Contact.TypeValue> orderedPhoneList = new ArrayList<>();
        List<Contact.TypeValue> mobilePhones = new ArrayList<>();
        List<Contact.TypeValue> businessPhones = new ArrayList<>();
        List<Contact.TypeValue> homePhones = new ArrayList<>();
        List<Contact.TypeValue> companyPhones = new ArrayList<>();
        List<Contact.TypeValue> faxPhones = new ArrayList<>();
        List<Contact.TypeValue> assistantPhones = new ArrayList<>();
        List<Contact.TypeValue> carPhones = new ArrayList<>();
        List<Contact.TypeValue> otherPhones = new ArrayList<>();

        for (Contact.TypeValue phone : phoneList) {
            int phoneType = phone.getType();
            String value = phone.getValue();
            if (phoneType == CloudPersonalContact.PhoneType.MOBILE_PHONE.ordinal()) {
                mobilePhones.add(new Contact.TypeValue(CloudPersonalContact.AppCloudPhoneType.MOBILE_PHONE.ordinal(), value));
            } else if (phoneType == CloudPersonalContact.PhoneType.BUSINESS_PHONE.ordinal() ||
                    phoneType == CloudPersonalContact.PhoneType.BUSINESS_PHONE2.ordinal()) {
                businessPhones.add(new Contact.TypeValue(CloudPersonalContact.AppCloudPhoneType.BUSINESS_PHONE.ordinal(), value));
            } else if (phoneType == CloudPersonalContact.PhoneType.HOME_PHONE.ordinal() ||
                    phoneType == CloudPersonalContact.PhoneType.HOME_PHONE2.ordinal()) {
                homePhones.add(new Contact.TypeValue(CloudPersonalContact.AppCloudPhoneType.HOME_PHONE.ordinal(), value));
            } else if (phoneType == CloudPersonalContact.PhoneType.COMPANY_PHONE.ordinal()) {
                companyPhones.add(new Contact.TypeValue(CloudPersonalContact.AppCloudPhoneType.COMPANY_PHONE.ordinal(), value));
            } else if (phoneType == CloudPersonalContact.PhoneType.BUSINESS_FAX.ordinal() ||
                    phoneType == CloudPersonalContact.PhoneType.OTHER_FAX.ordinal()) {
                faxPhones.add(new Contact.TypeValue(CloudPersonalContact.AppCloudPhoneType.FAX.ordinal(), value));
            } else if (phoneType == CloudPersonalContact.PhoneType.ASSISTANT_PHONE.ordinal()) {
                assistantPhones.add(new Contact.TypeValue(CloudPersonalContact.AppCloudPhoneType.ASSISTANT_PHONE.ordinal(), value));
            } else if (phoneType == CloudPersonalContact.PhoneType.CAR_PHONE.ordinal()) {
                carPhones.add(new Contact.TypeValue(CloudPersonalContact.AppCloudPhoneType.CAR_PHONE.ordinal(), value));
            } else if (phoneType == CloudPersonalContact.PhoneType.CALLBACK_PHONE.ordinal() ||
                    phoneType == CloudPersonalContact.PhoneType.OTHER_PHONE.ordinal()) {
                otherPhones.add(new Contact.TypeValue(CloudPersonalContact.AppCloudPhoneType.OTHER_PHONE.ordinal(), value));
            } else {
                //error handling
            }
        }

        orderedPhoneList.addAll(mobilePhones);
        orderedPhoneList.addAll(businessPhones);
        orderedPhoneList.addAll(homePhones);
        orderedPhoneList.addAll(companyPhones);
        orderedPhoneList.addAll(faxPhones);
        orderedPhoneList.addAll(assistantPhones);
        orderedPhoneList.addAll(carPhones);
        orderedPhoneList.addAll(otherPhones);

        for (Contact.TypeValue phone : orderedPhoneList) {
            onAddPhoneItem(phone);
        }
    }

    private void initEmailItemList(List<Contact.TypeValue> emailList) {
        for (Contact.TypeValue email : emailList) {
            onAddEmailItem(email);
        }
    }

    private void initAddressItemList(List<Contact.TypeAddress> addressList) {
        List<Contact.TypeAddress> orderedAddresses = ContactsUtils.orderedAddress(addressList);
        for (Contact.TypeAddress address : orderedAddresses) {
            onAddAddressItem(address);
        }
    }

    private void onAddPhoneItem(Contact.TypeValue item) {
        try {
            mPhoneList.addView(newPhoneItem(item), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (Throwable th) {
            //LOG
        }
        if (!mPhoneItemManager.isAvailable()) {
            mAddPhone.setVisibility(View.GONE);
        }
    }

    private void onAddEmailItem(Contact.TypeValue item) {
        try {
            mEmailList.addView(newEmailItem(item), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (Throwable th) {
            //LOG
        }
        if (!mEmailItemManager.isAvailable()) {
            mAddEmail.setVisibility(View.GONE);
        }
    }

    private void onAddAddressItem(Contact.TypeAddress item) {
        try {
            mAddressList.addView(newAddressItem(item), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (Throwable th) {
            //LOG
        }
        if (!mAddressItemManager.isAvailable()) {
            mAddAddress.setVisibility(View.GONE);
        }
    }

    private void onAddWebPageItem(Contact.TypeValue item) {
        try {
            mWebPageList.addView(newWebPageItem(item), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (Throwable th) {
            //LOG
        }
        if (!mWebPageItemManager.isAvailable()) {
            mAddWebPage.setVisibility(View.GONE);
        }
    }

    private void onAddBirthdayItem(Contact.TypeValue item) {
        try {
            mBirthdayList.addView(newBirthdayItem(item), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (Throwable th) {
            //LOG
        }
        if (!mBirthdayItemManager.isAvailable()) {
            mAddBirthday.setVisibility(View.GONE);
        }
    }

    private View newEmailItem(Contact.TypeValue initItem) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.contact_email_item, null);
        View icon = item.findViewById(R.id.item_icon);
        icon.setOnClickListener(new ItemDelete(mEmailList, item, mAddEmail, mEmailItemManager));
        TextView type = (TextView) item.findViewById(R.id.item_type);
        EditText dataItem = (EditText) item.findViewById(R.id.item_data);
        CloudPersonalContact.EmailType emailType;
        if (initItem != null) {
            setText(dataItem,initItem.getValue());
            emailType = mEmailItemManager.add(initItem.getType(), item);
        } else {
            emailType = mEmailItemManager.add(item);
            dataItem.requestFocus();
        }
        if (emailType != null) {
            type.setText(emailType.toString());
        } else {
            //error handling
            throw new IllegalStateException("EmailType is nil, probably out of arrange!");
        }
        return item;
    }

    private View newAddressItem(Contact.TypeAddress initItem) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.contact_address_item, null);
        item.setTag(mTagToTraceView++);
        View icon = item.findViewById(R.id.item_icon);
        icon.setOnClickListener(new ItemDelete(mAddressList, item, mAddAddress, mAddressItemManager));
        TextView type = (TextView) item.findViewById(R.id.item_type);
        CloudPersonalContact.AddressType addressType;
        EditText street = (EditText) item.findViewById(R.id.street);
        EditText zip = (EditText) item.findViewById(R.id.zip);
        if (initItem != null) {
            Address address = initItem.getValue();
            EditText state = (EditText) item.findViewById(R.id.state);
            EditText city = (EditText) item.findViewById(R.id.city);
            setText(state,address.getState());
            setText(city,address.getCity());
            setText(street,address.getStreet());
            setText(zip,ContactsUtils.getValidZipCode(address.getZip()));
            addressType = mAddressItemManager.add(initItem.getType(), item);
        } else {
            addressType = mAddressItemManager.add(item);
            street.requestFocus();
        }
        if (addressType != null) {
            type.setText(addressType.toString());
        } else {
            //error handling
            throw new IllegalStateException("AddressType is nil, probably out of arrange!");
        }
        mAddressItemManager.resetViewWidth(R.id.item_type);
        View arrow = item.findViewById(R.id.item_arrow);
        TypeSelection addressTypeSelection = new TypeSelection(this, mAddressItemManager, item);
        type.setOnClickListener(addressTypeSelection);
        arrow.setOnClickListener(addressTypeSelection);
        updateSelectTypePosition(item, addressType.ordinal());
        return item;
    }

    private View newWebPageItem(Contact.TypeValue initItem) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.contact_webpage_item, null);
        View icon = item.findViewById(R.id.item_icon);
        icon.setOnClickListener(new ItemDelete(mWebPageList, item, mAddWebPage, mWebPageItemManager));
        TextView type = (TextView) item.findViewById(R.id.item_type);
        CloudPersonalContact.WebPageType webPageType;
        EditText dataItem = (EditText) item.findViewById(R.id.item_data);
        if (initItem != null) {
            setText(dataItem,initItem.getValue());
            webPageType = mWebPageItemManager.add(initItem.getType(), item);
        } else {
            webPageType = mWebPageItemManager.add(item);
            dataItem.requestFocus();
        }
        if (webPageType != null) {
            type.setText(webPageType.toString());
        } else {
            //error handling
            throw new IllegalStateException("WebPageType is nil, probably out of arrange!");
        }
        return item;
    }

    private View newBirthdayItem(Contact.TypeValue initItem) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View item = inflater.inflate(R.layout.contact_birthday_item, null);
        final View icon = item.findViewById(R.id.item_icon);
        icon.setOnClickListener(new ItemDelete(mBirthdayList, item, mAddBirthday, mBirthdayItemManager));
        TextView type = (TextView) item.findViewById(R.id.item_type);
        final TextView dataItem = (TextView) item.findViewById(R.id.item_data);
        CloudPersonalContact.BirthdayType birthdayType;
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        if (initItem != null) {
            dataItem.setText(DateUtils.parseToUTCTime(initItem.getValue()));
            birthdayType = mBirthdayItemManager.add(initItem.getType(), item);
        } else {
            dataItem.setText(formatDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
            birthdayType = mBirthdayItemManager.add(item);
        }

        dataItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = dataItem.getText().toString();
                Calendar calendar = DateUtils.getCalendarByTime(time);
                DatePickerDialog dialog = new DatePickerDialog(ContactEditActivity.this,
                        new DataPickerDialogSelectedListener(item),
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });


        if (birthdayType != null) {
            type.setText(birthdayType.toString());
        } else {
            //error handling
            throw new IllegalStateException("birthday type is nil, probably out of arrange!");
        }
        return item;
    }

    private class DataPickerDialogSelectedListener implements DatePickerDialog.OnDateSetListener {
        private View mItemHolder;

        public DataPickerDialogSelectedListener(View itemHolder) {
            this.mItemHolder = itemHolder;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            TextView dataItem = (TextView) this.mItemHolder.findViewById(R.id.item_data);
            dataItem.setText(formatDateString(year, monthOfYear, dayOfMonth));
        }
    }


    private String formatDateString(int year, int monthOfYear, int dayOfMonth) {
        monthOfYear += 1;
        StringBuilder sb = new StringBuilder(10);
        sb.append(year).append("-");
        if(monthOfYear < 10) {
            sb.append("0");
        }
        sb.append(monthOfYear).append("-");
        if(dayOfMonth < 10) {
            sb.append("0");
        }
        sb.append(dayOfMonth);

        return sb.toString();
    }

    /**
     * Add middle Name/Nick Name/Job Title
     * @param view
     */
    private void onAddOtherFieldItem(OtherFiledType type, View view) {
        mOtherFieldManager.add(type, view);
        if(!mOtherFieldManager.isAvailable()) {
            mAddField.setVisibility(View.GONE);
        }
    }

    private View newPhoneItem(Contact.TypeValue initItem) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item = inflater.inflate(R.layout.contact_phone_item, null);
        item.setTag(mTagToTraceView++);
        View icon = item.findViewById(R.id.item_icon);
        icon.setOnClickListener(new ItemDelete(mPhoneList, item, mAddPhone, mPhoneItemManager));
        TextView type = (TextView) item.findViewById(R.id.item_type);
        CloudPersonalContact.AppCloudPhoneType phoneType;

        EditText dataItem = (EditText) item.findViewById(R.id.item_data);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dStart, int dEnd) {
                return ContactsUtils.phoneNumberNormalize(source.toString(), start, end);
            }
        };
        dataItem.setFilters(new InputFilter[]{filter});

        if (initItem != null) {
            setText(dataItem, initItem.getValue());
            phoneType = mPhoneItemManager.add(initItem.getType(), item);
        } else {
            phoneType = mPhoneItemManager.add(item);
            dataItem.requestFocus();
        }
        if (phoneType != null) {
            type.setText(phoneType.toString());
        } else {
            //error handling
            throw new IllegalStateException("PhoneType is nil, probably out of arrange!");
        }

        mPhoneItemManager.resetViewWidth(R.id.item_type);

        View arrow = item.findViewById(R.id.item_arrow);
        TypeSelection phoneTypeSelection = new TypeSelection(this, mPhoneItemManager, item);
        type.setOnClickListener(phoneTypeSelection);
        arrow.setOnClickListener(phoneTypeSelection);
        updateSelectTypePosition(item, phoneType.ordinal());
        return item;
    }

    public void updateSelectTypePosition(View view, int position) {
        View typeView = view.findViewById(R.id.item_type);
        if (typeView != null) {
            typeView.setTag(position);
        }
        View arrowView = view.findViewById(R.id.item_arrow);
        if (arrowView != null) {
            arrowView.setTag(position);
        }
    }


    private class ItemDelete implements View.OnClickListener {
        private final LinearLayout mContainerHolder;
        private final View mItemHolder;
        private final View mAddView;
        private final ItemManager mItemManager;

        public ItemDelete(LinearLayout container, View item, View addView, ItemManager manager) {
            this.mContainerHolder = container;
            this.mItemHolder = item;
            this.mAddView = addView;
            this.mItemManager = manager;
        }

        @Override
        public void onClick(View v) {
            this.mContainerHolder.removeView(this.mItemHolder);
            this.mItemManager.remove(this.mItemHolder);
            this.mItemManager.resetViewWidth(R.id.item_type);
            if (this.mAddView.getVisibility() != View.VISIBLE) {
                this.mAddView.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addPhone:
                onAddPhoneItem(null);
                break;
            case R.id.addEmail:
                onAddEmailItem(null);
                break;
            case R.id.addAddress:
                onAddAddressItem(null);
                break;
            case R.id.addField:
                break;
            case R.id.addWebPage:
                onAddWebPageItem(null);
                break;
            case R.id.addBirthday:
                onAddBirthdayItem(null);
                break;
            case R.id.delete:
                showDelConfirmDialog();
                break;
        }
    }



    private void showDelConfirmDialog() {
//        AlertDialog.Builder builder = RcAlertDialog.getBuilder(this);
//        builder.setTitle(R.string.delete_contact).setMessage(R.string.delete_contact_message).setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                RCFlurryHelper.editContactFlurry(ContactEditActivity.this, FlurryTypes.VALUE_EDIT_EXISTING_CONTACT, FlurryTypes.DELETE);
//                //update contact sync status
//                CloudPersonalContactLoader.updateContactSyncStatusInDB(mContactId, RCMDataStore.CloudContactSyncStatus.Deleted);
//                ContactsProvider.getInstance().deleteContactInCache(mContactId, Contact.ContactType.CLOUD_PERSONAL);
//                CloudContactSyncService.sendCommand(ContactEditActivity.this, CloudContactSyncService.CONTACT_LOCAL_SYNC_TO_SERVER);
//                PersonalFavorites.markedAsDeletedInFavorites(mContactId,Contact.ContactType.CLOUD_PERSONAL);
//                setResult(CommonEventDetailActivity.RESULT_FOR_EDIT_CONTACT);
//                finish();
//            }
//        }).setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        }).show();
    }

    private void onSaveContact() {
        //should we need to verify data todo??
        List<Contact.TypeValue> emails = validateAndGetEmailList();
//        if (emails == null) {
//            showInvalidEmailDialog();
//            return;
//        }
        List<Contact.TypeValue> phones = getPhoneList();
        List<Contact.TypeAddress> addresses = getAddressList();
        List<Contact.TypeValue> webPages = getWebPageList();
        List<Contact.TypeValue> birthdays = getBirthdayList();
        CloudPersonalContact contact = new CloudPersonalContact(CloudPersonalContact.CloudContactType.LOCAL, CloudPersonalContact.getLocalContactId(),
                "", mNamesField.getFirstName(), mNamesField.getMiddleName(), mNamesField.getLastName(), mNamesField.getNickname(), mOtherField.getCompany(), mOtherField.getJobTitle());
        contact.setOriginalPhoneNumbers(phones);
        contact.setE164PhoneNumbers(phones);
        contact.setEmails(emails);
        contact.setAddress(addresses);
        contact.addWebPage(webPages.isEmpty() ? "" : webPages.get(0).getValue());
        contact.setBirthday(birthdays.isEmpty() ? "" : birthdays.get(0).getValue());
        contact.setNotes(mOtherField.getNotes());
        //AB-20193 [Create Cloud Contact] Contact without any data shouldn't be created successfully
        //if(contact.isEmpty()) {
        //    this.finish();
        //    return;
        //}
        //AB-20025 Validate required fields when create/import contacts
        if(!ContactsUtils.validate(contact.getFirstName(), contact.getLastName(), contact.getCompany(), contact.getE164PhoneNumbers())) {
           // RcAlertDialog.showOkAlertDialog(this, R.string.invalidated_contact_title, R.string.invalidated_contact_msg);
            return;
        }

        contact.generateDisplayName();


        switch (mContactEditType) {
            case RequestInfoStorage.REST_CREATE_SINGLE_CONTACT:
            case RequestInfoStorage.REST_IMPORT_SINGLE_CONTACT: {
                ContactsProvider.getInstance().addContact(contact);
            }
            break;
            case RequestInfoStorage.REST_UPDATE_CONTACT: {
                if (mEditContact != null) {
                    contact.setId(mEditContact.getId());
                    contact.setUri(mEditContact.getUri());
                    contact.setAvailability(mEditContact.getAvailability());
                    ContactsProvider.getInstance().updateContact(contact);
                }else {
                    contactNotFound();
                }
            }
            break;
            default:
                break;
        }
        this.finish();
    }



    public List<Contact.TypeValue> getPhoneList() {
        List<Contact.TypeValue> phones = new ArrayList<>();
        ItemManager.TypeItem[] typeItems = mPhoneItemManager.getTypeItems();
        for (ItemManager.TypeItem<CloudPersonalContact.AppCloudPhoneType> typeItem : typeItems) {
            List<View> viewItems = typeItem.getItems();
            for (int i = 0; i < viewItems.size(); i++) {
                EditText data = (EditText) viewItems.get(i).findViewById(R.id.item_data);
                String phone = data.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    phones.add(new Contact.TypeValue(CloudPersonalContact.MAP_TO_PHONE_TYPE[typeItem.getType().ordinal()][i], phone));
                }
            }
        }

        return ContactsUtils.orderedPhones(phones);
    }

    /**
     * If has invalidate email, return null to mark has invalidate email
     * @return
     */
    public List<Contact.TypeValue> validateAndGetEmailList() {
        List<Contact.TypeValue> emails = new ArrayList<>();
        ItemManager.TypeItem[] typeItems = mEmailItemManager.getTypeItems();
        for (ItemManager.TypeItem<CloudPersonalContact.EmailType> typeItem : typeItems) {
            List<View> viewItems = typeItem.getItems();
            for (int i = 0; i < viewItems.size(); i++) {
                EditText data = (EditText) viewItems.get(i).findViewById(R.id.item_data);
                String email = data.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    if (ContactsUtils.validateEmail(email)) {
                        emails.add(new Contact.TypeValue(CloudPersonalContact.MAP_TO_EMAIL_TYPE[typeItem.getType().ordinal()][i], email));
                    } else {
                        data.requestFocus();
                        return null;
                    }
                }
            }
        }

        return emails;
    }

    public List<Contact.TypeAddress> getAddressList() {
        List<Contact.TypeAddress> addresses = new ArrayList<>();
        ItemManager.TypeItem[] typeItems = mAddressItemManager.getTypeItems();
        for (ItemManager.TypeItem<CloudPersonalContact.AddressType> typeItem : typeItems) {
            List<View> viewItems = typeItem.getItems();
            for (int i = 0; i < viewItems.size(); i++) {
                View item = viewItems.get(i);
                EditText state = (EditText) item.findViewById(R.id.state);
                EditText city = (EditText) item.findViewById(R.id.city);
                EditText street = (EditText) item.findViewById(R.id.street);
                EditText zip = (EditText) item.findViewById(R.id.zip);
                addresses.add(new Contact.TypeAddress(CloudPersonalContact.MAP_TO_ADDRESS_TYPE[typeItem.getType().ordinal()][i],
                        new Address(null, state.getText().toString(), city.getText().toString(), street.getText().toString(), zip.getText().toString())));
            }
        }

        return ContactsUtils.orderedAddress(addresses);
    }

    public List<Contact.TypeValue> getWebPageList() {
        List<Contact.TypeValue> webPages = new ArrayList<>();
        ItemManager.TypeItem[] typeItems = mWebPageItemManager.getTypeItems();
        for (ItemManager.TypeItem<CloudPersonalContact.WebPageType> typeItem : typeItems) {
            List<View> viewItems = typeItem.getItems();
            for (int i = 0; i < viewItems.size(); i++) {
                EditText data = (EditText) viewItems.get(i).findViewById(R.id.item_data);
                String email = data.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    webPages.add(new Contact.TypeValue(CloudPersonalContact.MAP_TO_WEB_PAGE_TYPE[typeItem.getType().ordinal()][i], email));
                }
            }
        }

        return webPages;
    }

    public List<Contact.TypeValue> getBirthdayList() {
        List<Contact.TypeValue> birthdays = new ArrayList<>();
        ItemManager.TypeItem[] typeItems = mBirthdayItemManager.getTypeItems();
        for (ItemManager.TypeItem<CloudPersonalContact.BirthdayType> typeItem : typeItems) {
            List<View> viewItems = typeItem.getItems();
            for (int i = 0; i < viewItems.size(); i++) {
                TextView data = (TextView) viewItems.get(i).findViewById(R.id.item_data);
                String birthday = data.getText().toString().trim();
                if (!TextUtils.isEmpty(birthday)) {
                    birthdays.add(new Contact.TypeValue(CloudPersonalContact.MAP_TO_BIRTHDAY_TYPE[typeItem.getType().ordinal()][i], birthday));
                }
            }
        }

        return birthdays;
    }

}
