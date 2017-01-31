package com.example.nickgao.eventdetail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nickgao.R;
import com.example.nickgao.androidsample11.ContactsUtils;
import com.example.nickgao.contacts.GetPersonalContactData;
import com.example.nickgao.contacts.GetPersonalContactData.ContactsInfo;
import com.example.nickgao.contacts.GetPersonalContactData.ListType;
import com.example.nickgao.contacts.GetPersonalContactData.PersonalDetailData;
import com.example.nickgao.contacts.GetPersonalContactData.PhoneData;
import com.example.nickgao.contacts.PersonalFavorites;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudContactSyncService;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContact;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactEditActivity;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.contacts.adapters.contactsprovider.DeviceContact;
import com.example.nickgao.eventdetail.EventDetailUtils.EventDetailContactInfo;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.ChromeHelper;
import com.example.nickgao.utils.DateUtils;
import com.example.nickgao.utils.LabelsUtils;
import com.example.nickgao.utils.RCMConstants;
import com.example.nickgao.utils.RcAlertDialog;
import com.example.nickgao.utils.widget.HeaderViewBase;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nick.gao on 1/30/17.
 */

public class CommonEventDetailActivity extends Activity implements PersonalFavorites.OnAddDeviceFavoriteCallback,EventDetailBase.OnBackListener,EventDetailBase.OnContactInfoChangedListener,EventDetailBase.OnEditListener, EventDetailBase.OnImportListener{


    private static final String TAG = "[RC]CommonEventDetailActivity";

    public static final int EVENT_DETAIL_TYPE_DEFAULT = 0;
    public static final int EVENT_DETAIL_TYPE_MESSAGE = 1;
    public static final int EVENT_DETAIL_TYPE_CALLLOG = 2;
    public static final int EVENT_DETAIL_TYPE_OUTBOX = 3;
    public static final int VIEW_PERSONAL_CONTACT = 4;
    public static final int VIEW_COMPANY_CONTACT = 5;
    public static final int VIEW_CLOUD_PERSONAL_CONTACT = 6;
    public static final int VIEW_FAVORITES_CONTACT = 7;

    public static final int RESULT_FOR_EDIT_CONTACT = 10;

    private LinearLayout mPhoneLayout, mEmailLayout, mImLayout, mWebsiteLayout, mAddressLayout, mEventLayout, mNoteLayout;
    private LayoutInflater mInflater;
    private ImageButton mFavoritebtn;
    private boolean mIsKnownContact = false;
    private boolean mIsInitialized = false;
    private int mEventDetailType = EVENT_DETAIL_TYPE_DEFAULT;

    Contact.ContactType mContactType = Contact.ContactType.UNKNOW;

    private EventDetailBase mEventDetail = null;
    private EventDetailContactInfo mContactInfo = null;

    GetPersonalContactData mGetLocalContactData;
    private PersonalDetailData mDetailData = null;
    private ImageView mPresenceView;
    private ImageView mTop_photo_view;
    private TextView mTvDisplayName;
    private TextView mCompanyView;
    private TextView mNickNameView;
    private TextView mDepartmentView;
    private TextView mTitleView;
    // private ImageButton mSendTextForKnowContact, mSendFaxForKnowContact;
    private LinearLayout mOpenGlipLayout;
    private LinearLayout mSendFaxLayout;
    private boolean isNeedBoldFirstNumber = false;

    public final static String CONTENT_VALUES_TYPE = "type";
    public final static String CONTENT_VALUES_DATA = "data";
    public final static String CONTENT_VALUES_LABEL = "label";

    private final static long FOUR_HOUR = 4 * 60 * 60 * 1000;

    private long extMailboxId = 0;
    private long phonenumber_refresh_interval = 0;

    private String mSendFaxFrom = "";
    private String mSendTextFrom = "";
    private String mFavoritePhoneNumber = "";

    private String mExtraFromMessage;
    private boolean isFromMessageTextFilter = false;
    private boolean isFromMessageViewContact = false;

    private String mNameFromContactsList = "";


    private String mViewContactInfoEntity;
    private RelativeLayout mRootLayout;


    private Handler mUIHandler = null;
    private UIRunnable mUIRunnable;
//    private ProfileImageReceiver mImageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = LayoutInflater.from(this);

        /*setSingleActivity();
        this.registerVoIPSettingObserver();*/

        mUIHandler = new Handler();
        mUIRunnable = new UIRunnable();

        mRootLayout = (RelativeLayout) mInflater.inflate(R.layout.common_event_detail_screen, null);
        setContentView(mRootLayout);
        initEventDetail(getIntent());
    }


    private void initEventDetail(Intent intent) {
        mContactType = Contact.ContactType.values()[intent.getIntExtra(RCMConstants.EXTRA_CONTACT_TYPE_FROM, Contact.ContactType.UNKNOW.ordinal())];
        mEventDetailType = intent.getIntExtra(RCMConstants.EXTRA_EVENT_DETAIL_TYPE, 0);

        switch (mContactType) {
            case DEVICE:
                mEventDetail = new ViewPersonalContact(intent);
                mEventDetailType = VIEW_PERSONAL_CONTACT;
                break;
            case CLOUD_PERSONAL:
                mEventDetail = new ViewCloudPersonalContact(this, intent);
                mEventDetailType = VIEW_CLOUD_PERSONAL_CONTACT;
                break;
        }
        isNeedBoldFirstNumber = false;

        mEventDetail.setOnBackListener(CommonEventDetailActivity.this);
        mEventDetail.setOnContactInfoChangedListener(CommonEventDetailActivity.this);

    }

    private ContactObserver mContactObserver = new ContactObserver();
    private boolean mRegisterCloudContactObserver = false;

    private class ContactObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onContactInfoChanged();
        }
    }

    @Override
    public void onAddDeviceFavorite(Contact contact) {
        if (mEventDetail instanceof ViewPersonalContact) {
            mEventDetail.setContactType(Contact.ContactType.CLOUD_PERSONAL);
            mEventDetail.setContactId(contact.getId());
            mContactInfo = mEventDetail.bindContactInfo();
            mContactType = mEventDetail.getContactType();
        }
    }

    @Override
    public void onContactInfoChanged() {
        if (mEventDetail != null) {
            mContactInfo = mEventDetail.bindContactInfo();
            mContactType = mEventDetail.getContactType();
            if (mContactType == Contact.ContactType.CLOUD_COMPANY) {
                if (mContactInfo == null) {
                    finish();
                }
            }
            setUI();
        }
    }

    class UIRunnable implements Runnable {
        @Override
        public void run() {
            try {
                if (extMailboxId != 0) {
                    if (LogSettings.MARKET) {
                        MktLog.i(TAG, "UIRefreshRunnable:refresh did number ,extMailboxId= " + extMailboxId);
                    }
                    setUI();
                } else {
                    if (LogSettings.MARKET) {
                        MktLog.i(TAG, "UIRefreshRunnable:refresh did number,but extMailboxId=0");
                    }
                }

            } catch (Exception err) {
                if (LogSettings.MARKET) {
                    MktLog.e(TAG, "UIRefreshRunnable: error= " + err.toString());
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            if (mRegisterCloudContactObserver && mContactObserver != null) {
                this.unregisterReceiver(mContactObserver);
                mRegisterCloudContactObserver = false;
            }

        } catch (Throwable th) {
            MktLog.e(TAG, "onPause() error=" + th.toString());
        }

        if (mEventDetail != null) {
            mEventDetail.onPause();
        }
    }


    private void setUI() {
        if (!mIsInitialized) {
            if (mEventDetail.isKnownContact()) {
                initKnownContactUI();
            } else {
               // initUnknownContactUI();
            }
            mIsInitialized = true;
            mIsKnownContact = mEventDetail.isKnownContact();
        } else {
            boolean isPreviousKnownContact = mIsKnownContact;
            mIsKnownContact = mEventDetail.isKnownContact();
            if (mIsKnownContact) {
                if (isPreviousKnownContact) {
                    updateKnownContactUI();
                } else {
                    initKnownContactUI();
                }
            } else {
                if (isPreviousKnownContact) {
                 //   initUnknownContactUI();
                } else {
                 //   updateUnknownContactUI();
                }
            }
        }

        updateTopRightButton();
    }

    private void updateKnownContactUI() {
        if (!isFromMessageViewContact) {
            mEventDetail.updateDetailHeader();
        }
        if (mContactType == Contact.ContactType.CLOUD_COMPANY) {
//            addViewCompanyDetailsFlurry();
//            fillCompanyData();
        } else {
            fillPersonalData(mContactInfo.contactID, mContactInfo.phoneNumber);
        }
    }


    private void initTitle() {
        HeaderViewBase headerViewBase = (HeaderViewBase) findViewById(R.id.header);
        mEventDetail.setHeaderViewBase(headerViewBase);
    }

    private void initKnownContactUI() {
        View view = mInflater.inflate(R.layout.event_detail_known_contact, null, true);
        LinearLayout above_content = (LinearLayout) mRootLayout.findViewById(R.id.above_content);
        if (above_content.getChildCount() > 1) {
            above_content.removeViewAt(1);
        }
        above_content.addView(view);

        initTitle();
        View between_root = findViewById(R.id.event_detail_conversation_between);
        mFavoritebtn = (ImageButton) findViewById(R.id.favoritebtn);
        mEventDetail.getDetailBetweenView(between_root);
        mPhoneLayout = (LinearLayout) findViewById(R.id.phoneLayout);
        mEmailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        mImLayout = (LinearLayout) findViewById(R.id.imLayout);
        mWebsiteLayout = (LinearLayout) findViewById(R.id.websiteLayout);
        mAddressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        mEventLayout = (LinearLayout) findViewById(R.id.eventLayout);
        mNoteLayout = (LinearLayout) findViewById(R.id.noteLayout);
        mEmailLayout.setVisibility(View.GONE);
        mImLayout.setVisibility(View.GONE);
        mWebsiteLayout.setVisibility(View.GONE);
        mAddressLayout.setVisibility(View.GONE);
        mEventLayout.setVisibility(View.GONE);
        mNoteLayout.setVisibility(View.GONE);

        mOpenGlipLayout = (LinearLayout) findViewById(R.id.open_glip_layout);

        initKnownTitle();

        switch (mContactType) {
            case CLOUD_PERSONAL: {
                fillPersonalData(mContactInfo.contactID, mContactInfo.phoneNumber);
            }
            break;
            case DEVICE: {
                fillPersonalData(mContactInfo.contactID, mContactInfo.phoneNumber);
            }
            break;
            case CLOUD_COMPANY:
//                addViewCompanyDetailsFlurry();
//                fillCompanyData();
                break;
        }

        if (!isFromMessageViewContact) {
            mEventDetail.updateDetailHeader();
        }
    }

    private void initKnownTitle() {
        mPresenceView = (ImageView) findViewById(R.id.presence_imageview);
        mTop_photo_view = (ImageView) findViewById(R.id.top_photo_view);
        mTvDisplayName = (TextView) findViewById(R.id.name);
        mCompanyView = (TextView) findViewById(R.id.company);
        mNickNameView = (TextView) findViewById(R.id.contact_nickname);
        mTitleView = (TextView) findViewById(R.id.contact_company_title);
        mDepartmentView = (TextView) findViewById(R.id.contact_department);
    }


    private void fillPersonalData(long contactId, String phoneData) {
        if (mGetLocalContactData == null) {
            mGetLocalContactData = new GetPersonalContactData(this, mContactType, contactId, phoneData, mNameFromContactsList);
        } else {
            mGetLocalContactData.update(mContactType, contactId, phoneData, mNameFromContactsList);
        }
        if (mGetLocalContactData.IsContactIdNotFound()) {
            finish();
        }
        mDetailData = mGetLocalContactData.mDetailData;
//		mRawContentId = mDetailData.rawContactId;
        if (mDetailData.mPhotoBitmap != null) {
            mDetailData.mPhotoBitmap = getRoundBitmap(mDetailData.mPhotoBitmap);
            mTop_photo_view.setImageBitmap(mDetailData.mPhotoBitmap);
        } else {
            mTop_photo_view.setImageResource(R.drawable.ic_event_photo_default);
        }

        setKnowTitleData();
        boolean hasPhoneField = initPhoneField();
        boolean hasEmailField = initEmailFild();
        initImField();
        initWebSiteField();
        initAddressField();
        initEventField();
        initNoteField();
//        if (!hasPhoneField) {
//            mSendFaxLayout.setVisibility(View.GONE);
//            View view = findViewById(R.id.send_fax_layout_line);
//            if (ActivityUtils.isTablet()) {
//                view.setVisibility(View.GONE);
//            } else {
//                view.setVisibility(View.VISIBLE);
//            }
//        } else {
//            boolean hasFaxOutPermission = false;
//            if (hasFaxOutPermission) {
//                mSendFaxLayout.setVisibility(View.VISIBLE);
//            }
//            View view = findViewById(R.id.send_fax_layout_line);
//            if (!ActivityUtils.isTablet()) {
//                view.setVisibility(View.VISIBLE);
//            }
//        }

        //setOpenGlipLayoutVisibility(hasEmailField || hasPhoneField);
        mOpenGlipLayout.setVisibility(View.GONE);
    }

    private void setKnowTitleData() {
        String title = mDetailData.contactsInfo[ContactsInfo.TITLE.ordinal()];
        String department = mDetailData.contactsInfo[ContactsInfo.DEPARTMENT.ordinal()];
        String company = mDetailData.contactsInfo[ContactsInfo.COMPANY.ordinal()];
        String nickName = mDetailData.contactsInfo[ContactsInfo.NICKNAME.ordinal()];
//		boolean isSecondLineEmpty = false;
        //	mContactInfo.displayName = mDetailData.displayName;
        if (TextUtils.isEmpty(nickName)) {
            mNickNameView.setVisibility(View.GONE);
        } else {
            mNickNameView.setText(nickName);
            mNickNameView.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(title)) {
            mTitleView.setVisibility(View.GONE);
        } else {
            mTitleView.setText(title);
            mTitleView.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(department)) {
            mDepartmentView.setVisibility(View.GONE);
        } else {
            mDepartmentView.setText(department);
            mDepartmentView.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(company)) {
            mCompanyView.setVisibility(View.GONE);
        } else {
            mCompanyView.setText(company);
            mCompanyView.setVisibility(View.VISIBLE);
        }

        mTvDisplayName.setText((mContactType == Contact.ContactType.CLOUD_PERSONAL && !Contact.isEmpty(mDetailData.displayName)) ? mDetailData.displayName : mContactInfo.displayName);
    }


    private void updateTopRightButton() {
        HeaderViewBase headerView = mEventDetail.getHeaderView();
        switch (mContactType) {
            case CLOUD_PERSONAL: {
                headerView.setImportDeviceContactButtonVisibility(View.GONE);
                mEventDetail.setImportButtonListener(null);
                mEventDetail.setEditButtonListener(this);
                headerView.setRightImageRes(R.drawable.ic_action_contact_edit);
                headerView.setRightImageVisibility(View.VISIBLE);
            }
            break;
            case DEVICE: {
                headerView.setRightImageVisibility(View.GONE);
                mEventDetail.setEditButtonListener(null);
                if (mContactInfo != null) {
                    DeviceContact contact = (DeviceContact) ContactsProvider.getInstance().getContact(Contact.ContactType.DEVICE, mContactInfo.contactID, true);
                    if (contact != null) {
                        //When import a device contact to cloud, if current contact doesn't have any one of those four fields:
                        //No "import" button for "No Name" device contact
                        if ((contact.getEmailAddressList() != null && contact.getEmailAddressList().size() > 0) ||
                                ContactsUtils.validate(contact.getFirstName(), contact.getLastName(), contact.getNickName(), contact.getCompany(), contact.getE164PhoneNumbers())) {
                            mEventDetail.setImportButtonListener(this);
                            headerView.setImportDeviceContactButtonImage(R.drawable.ic_action_import);
                            headerView.setImportDeviceContactButtonVisibility(View.VISIBLE);
//                            onViewRootResume();
                        } else {
                            mEventDetail.setImportButtonListener(null);
                            headerView.setImportDeviceContactButtonVisibility(View.GONE);
                        }
                    }
                }
            }
            break;
            case CLOUD_COMPANY:
            default: {
                headerView.setRightImageVisibility(View.GONE);
                headerView.setImportDeviceContactButtonVisibility(View.GONE);
            }
            break;
        }
    }


    /***************************init field***********************************/

    private boolean initPhoneField() {
        ArrayList<PhoneData> phoneLists = mDetailData.phone_list;
        boolean hasPhoneField = false;
        int phoneSize = phoneLists.size();
        ViewGroup detailItem;
        mPhoneLayout.removeAllViews();
        if (phoneSize == 0) {
            mPhoneLayout.setVisibility(View.GONE);
        } else {
            hasPhoneField = true;
            mPhoneLayout.setVisibility(View.VISIBLE);
            View separator = mInflater.inflate(R.layout.contacts_detail_separator, mPhoneLayout, false);
            separator.setBackgroundResource(R.drawable.contact_item_divider);
            mPhoneLayout.addView(separator);
        }
//        boolean isFav = false;
        for (int i = 0; i < phoneSize; i++) {
            PhoneData data = phoneLists.get(i);
            detailItem = (ViewGroup) mInflater.inflate(R.layout.view_personal_contact_phone_item, null);
            final ViewGroup detail_layout = (ViewGroup) detailItem.findViewById(R.id.detail_layout);

            ImageView btn_send_message = (ImageView) detailItem.findViewById(R.id.btn_send_message);
            boolean hasTextPermission = false;
            if (!hasTextPermission) {
                btn_send_message.setVisibility(View.GONE);
            }
//            ImageView fav_phonenumber = (ImageView) detailItem.findViewById(R.id.fav_phonenumber);
            ImageView btn_call = (ImageView) detailItem.findViewById(R.id.btn_call);

            btn_send_message.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });


//            boolean fav = PersonalFavorites.isFavorite(num.id, this);
//            if(fav) {
//                fav_phonenumber.setVisibility(View.VISIBLE);
//            }
            TextView typeView = (TextView) detailItem.findViewById(R.id.type);
            final TextView valueView = (TextView) detailItem.findViewById(R.id.value);
            //if (i == 0)
            {
                if (isNeedBoldFirstNumber && data.isBoldStyle) {
                    ColorStateList colorStateList = getResources().getColorStateList(R.color.contact_detail_phone_related_color);
                    valueView.setTextColor(colorStateList);
                    typeView.setTextColor(getResources().getColor(R.color.contact_detail_phone_related_color_nor));
                }
            }
            typeView.setText(data.type);
            final String phoneNumber = data.num;
            valueView.setText(phoneNumber);
            final String oriNumber = phoneNumber;

            btn_call.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });

//            detail_layout.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    makeCallViaPhoneNumber(oriNumber);
//                }
//            });
            detailItem.setContentDescription(String.valueOf(i));
            mPhoneLayout.addView(detailItem);
            detail_layout.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
//                    ContactUtils.handleTapEvent(CommonEventDetailActivity.this, valueView);
                    return false;
                }
            });
        }

        if (mContactInfo != null) {
            if (PersonalFavorites.isCloudFavorite(mContactInfo.contactID, mContactType, CommonEventDetailActivity.this)) {
                mFavoritebtn.setImageResource(R.drawable.contact_detail_fav_yes_selector);
            } else {
                mFavoritebtn.setImageResource(R.drawable.contact_detail_fav_no_selector);
            }
        } else {
            mFavoritebtn.setVisibility(View.GONE);
        }
        mFavoritebtn.setOnClickListener(mAddPersonalFav);
        return hasPhoneField;
    }

    private View.OnClickListener mAddPersonalFav = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (mDetailData != null) {
                final String action;
                if (PersonalFavorites.isCloudFavorite(mContactInfo.contactID, mContactType, CommonEventDetailActivity.this)) {
                    PersonalFavorites.markedAsDeletedInFavorites(mContactInfo.contactID, mContactType);
                } else {
                    PersonalFavorites.addToCloudFavoriteWithLimitationChecking(mContactInfo.contactID, mContactType, CommonEventDetailActivity.this, (mContactType == Contact.ContactType.DEVICE) ? CommonEventDetailActivity.this : null);
                }
                setUI();
            }
        }
    };


    private boolean initEmailFild() {
        boolean hasEmailField = false;
        ArrayList<ContentValues> emailList = mDetailData.list.get(ListType.EMAIL_TYPE.ordinal());
        mEmailLayout.removeAllViews();
        int emailSize = 0;
        if (emailList != null) {
            emailSize = emailList.size();
        }
        if (emailSize == 0) {
            mEmailLayout.setVisibility(View.GONE);
        } else {
            hasEmailField = true;
            mEmailLayout.setVisibility(View.VISIBLE);
            View separator = mInflater.inflate(R.layout.contacts_detail_separator, mEmailLayout, false);
            mEmailLayout.addView(separator);
        }
        for (int i = 0; i < emailSize; i++) {
            ContentValues values = emailList.get(i);
            final LinearLayout emailItem = (LinearLayout) mInflater.inflate(R.layout.contacts_detail_info_item_nor, null);
            TextView typeView = (TextView) emailItem.findViewById(R.id.type);
            final TextView valueView = (TextView) emailItem.findViewById(R.id.value);
            String label = values.getAsString(CONTENT_VALUES_LABEL);
            int type = values.getAsInteger(CONTENT_VALUES_TYPE);
            if (mContactType == Contact.ContactType.CLOUD_PERSONAL) {
                typeView.setText(label);
            } else {
                typeView.setText(GetPersonalContactData.getEmailType(this, type, label));
            }

            final String data = values.getAsString(CONTENT_VALUES_DATA);
            valueView.setText(data);
            mEmailLayout.addView(emailItem);

            emailItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    EmailSender emailSender = new EmailSender(CommonEventDetailActivity.this);
//                    String[] to = {data};
//                    emailSender.sendEmail(to, "", "", null);
                }
            });
            emailItem.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
//                    ContactUtils.handleTapEvent(CommonEventDetailActivity.this, valueView);
                    return false;
                }
            });
        }
        return hasEmailField;
    }

    private boolean initImField() {
        boolean hasImField = false;
        ArrayList<ContentValues> imList = mDetailData.list.get(ListType.IM_TYPE.ordinal());
        LinearLayout imItem;
        mImLayout.removeAllViews();
        int imSize = 0;
        if (imList != null) {
            imSize = imList.size();
        }
        if (imSize == 0) {
            mImLayout.setVisibility(View.GONE);
        } else {
            hasImField = true;
            mImLayout.setVisibility(View.VISIBLE);
            View separator = mInflater.inflate(R.layout.contacts_detail_separator, mImLayout, false);
            mImLayout.addView(separator);
        }
        for (int i = 0; i < imSize; i++) {
            try {
                ContentValues values = imList.get(i);
                imItem = (LinearLayout) mInflater.inflate(R.layout.contacts_detail_info_item_nor, null);
                imItem.setClickable(false);
                TextView typeView = (TextView) imItem.findViewById(R.id.type);
                TextView valueView = (TextView) imItem.findViewById(R.id.value);
                String label = values.getAsString(CONTENT_VALUES_LABEL);
                int type = values.getAsInteger(CONTENT_VALUES_TYPE);
                typeView.setText(mGetLocalContactData.getImType(type, label));
                valueView.setText(values.getAsString(CONTENT_VALUES_DATA));
                mImLayout.addView(imItem);
            } catch (Throwable th) {
                MktLog.e(TAG, th.toString());
            }
        }
        return hasImField;
    }

    private boolean initAddressField() {
        boolean hasAddressField = false;
        ArrayList<ContentValues> addressList = mDetailData.list.get(ListType.ADDRESS_TYPE.ordinal());
        LinearLayout mAddressItem;
        mAddressLayout.removeAllViews();
        int addressSize = 0;
        if (addressList != null) {
            addressSize = addressList.size();
        }
        if (addressSize == 0) {
            mAddressLayout.setVisibility(View.GONE);
        } else {
            hasAddressField = true;
            mAddressLayout.setVisibility(View.VISIBLE);
            View separator = mInflater.inflate(R.layout.contacts_detail_separator, mAddressLayout, false);
            mAddressLayout.addView(separator);
        }
        for (int i = 0; i < addressSize; i++) {
            ContentValues values = addressList.get(i);
            mAddressItem = (LinearLayout) mInflater.inflate(R.layout.contacts_detail_info_item_nor, null);
            mInflater.inflate(R.layout.contacts_detail_info_item_nor, null);
            final String data = values.getAsString(CONTENT_VALUES_DATA);
            TextView typeView = (TextView) mAddressItem.findViewById(R.id.type);
            TextView valueView = (TextView) mAddressItem.findViewById(R.id.value);
            String label = values.getAsString(CONTENT_VALUES_LABEL);
            int type = values.getAsInteger(CONTENT_VALUES_TYPE);
            typeView.setText(GetPersonalContactData.getAddressType(this, type, label));
            valueView.setText(data);
            mAddressItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        Uri uri = Uri.parse("geo:0,0?q=" + data);
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        ChromeHelper.startActiviyByChromeIfExists(CommonEventDetailActivity.this, it);
                    } catch (ActivityNotFoundException ex) {
                        if (LogSettings.MARKET) {
                        }
                    }
                }
            });
            mAddressLayout.addView(mAddressItem);
        }
        return hasAddressField;
    }

    private boolean initEventField() {
        boolean hasEventField = false;
        ArrayList<ContentValues> eventList = mDetailData.list.get(ListType.EVENT_TYPE.ordinal());
        LinearLayout mEventItem;
        mEventLayout.removeAllViews();
        int eventSize = 0;
        if (eventList != null) {
            eventSize = eventList.size();
        }
        if (eventSize == 0) {
            mEventLayout.setVisibility(View.GONE);
        } else {
            hasEventField = true;
            mEventLayout.setVisibility(View.VISIBLE);
            View separator = mInflater.inflate(R.layout.contacts_detail_separator, mEventLayout, false);
            mEventLayout.addView(separator);
        }
        for (int i = 0; i < eventSize; i++) {
            ContentValues values = eventList.get(i);
            mEventItem = (LinearLayout) mInflater.inflate(R.layout.contacts_detail_info_item_nor, null);
            mEventItem.setClickable(false);
            TextView typeView = (TextView) mEventItem.findViewById(R.id.type);
            TextView valueView = (TextView) mEventItem.findViewById(R.id.value);
            String label = values.getAsString(CONTENT_VALUES_LABEL);
            int type = values.getAsInteger(CONTENT_VALUES_TYPE);
            typeView.setText(mGetLocalContactData.getEventType(type, label));
            String dateStr = values.getAsString(CONTENT_VALUES_DATA);
            if (dateStr.startsWith("--")) {//some event date may not have year field
                valueView.setText(dateStr.replaceFirst("--", "").replace("-", "/"));
            } else {
                Date d = DateUtils.parseISO8601DateForContact(dateStr);
                if (d == null) {
                    try {
                        d = new Date(dateStr);
                    } catch (IllegalArgumentException e) {
                        if (LogSettings.MARKET) {
                            MktLog.w(TAG, "date format: '" + dateStr + "'" + e.getMessage());
                        }
                    }
                }

                valueView.setText(d != null ? LabelsUtils.getDateLabel(d.getTime()) : dateStr);
            }
            mEventLayout.addView(mEventItem);
        }
        return hasEventField;
    }

    private boolean initNoteField() {
        boolean hasNoteField = false;
        ArrayList<ContentValues> noteList = mDetailData.list.get(ListType.NOTE_TYPE.ordinal());
        LinearLayout mNoteItem;
        mNoteLayout.removeAllViews();
        int noteSize = 0;
        if (noteList != null) {
            noteSize = noteList.size();
        }
        if (noteSize == 0) {
            mNoteLayout.setVisibility(View.GONE);
        } else {
            hasNoteField = true;
            mNoteLayout.setVisibility(View.VISIBLE);
            View separator = mInflater.inflate(R.layout.contacts_detail_separator, mNoteLayout, false);
            mNoteLayout.addView(separator);
        }
        for (int i = 0; i < noteSize; i++) {
            ContentValues values = noteList.get(i);
            mNoteItem = (LinearLayout) mInflater.inflate(R.layout.contacts_detail_info_item_nor, null);
            mNoteItem.setClickable(false);
            TextView typeView = (TextView) mNoteItem.findViewById(R.id.type);
            TextView valueView = (TextView) mNoteItem.findViewById(R.id.value);
            typeView.setText(getString(R.string.contact_another_field_menu_notes));
            valueView.setText(values.getAsString(CONTENT_VALUES_DATA));
            mNoteLayout.addView(mNoteItem);
        }
        return hasNoteField;
    }

    private boolean initWebSiteField() {
        boolean hasWebSiteField = false;
        ArrayList<ContentValues> websiteList = mDetailData.list.get(ListType.WEBSITE_TYPE.ordinal());
        LinearLayout mWebSiteItem;
        mWebsiteLayout.removeAllViews();

        int websiteSize = 0;
        if (websiteList != null) {
            websiteSize = websiteList.size();
        }
        if (websiteSize == 0) {
            mWebsiteLayout.setVisibility(View.GONE);
        } else {
            hasWebSiteField = true;
            mWebsiteLayout.setVisibility(View.VISIBLE);
            View separator = mInflater.inflate(R.layout.contacts_detail_separator, mWebsiteLayout, false);
            mWebsiteLayout.addView(separator);
        }
        for (int i = 0; i < websiteSize; i++) {
            ContentValues values = websiteList.get(i);
            mWebSiteItem = (LinearLayout) mInflater.inflate(R.layout.contacts_detail_info_item_nor, null);
            mWebSiteItem.setClickable(false);
            TextView typeView = (TextView) mWebSiteItem.findViewById(R.id.type);
            TextView valueView = (TextView) mWebSiteItem.findViewById(R.id.value);
            String label = values.getAsString(CONTENT_VALUES_LABEL);
            int type = values.getAsInteger(CONTENT_VALUES_TYPE);
            typeView.setText(mGetLocalContactData.getWebSiteType(type, label));
            final String data = values.getAsString(CONTENT_VALUES_DATA);
            valueView.setText(data);
            mWebsiteLayout.addView(mWebSiteItem);
            mWebSiteItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String websize = data;
                    if (!data.startsWith("http://") || !data.startsWith("https://")) {
                        websize = "http://" + data;
                    }
                    Uri uri = Uri.parse(websize);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    ChromeHelper.startActiviyByChromeIfExists(CommonEventDetailActivity.this, intent);
                }
            });
        }
        return hasWebSiteField;
    }

    private Bitmap getRoundBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return bitmap;
        }
        Bitmap output;
        try {
            final int roundPx = 360;
            final Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_event_photo_default);
            output = Bitmap.createBitmap(defaultBitmap.getWidth(), defaultBitmap.getHeight(), defaultBitmap.getConfig());

            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, defaultBitmap.getWidth(), defaultBitmap.getHeight());
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            Matrix matrix = new Matrix();
            matrix.setScale((float) defaultBitmap.getWidth() / bitmap.getWidth(), (float) defaultBitmap.getHeight() / bitmap.getHeight());
            canvas.drawBitmap(bitmap, matrix, paint);
            bitmap.recycle();
            bitmap = null;
        } catch (Exception err) {
            if (LogSettings.MARKET) {
                MktLog.d(TAG, "getRoundBitmap-exception:" + err.toString());
            }
            output = bitmap;
        }
        return output;
    }

    @Override
    public void onEdit() {
        if (mContactInfo != null) {
            ContactEditActivity.editContactForResult(this, mContactInfo.contactID);
        }
    }

    @Override
    public void onImport() {
        ContactsUtils.TranslateResult translateResult = new ContactsUtils.TranslateResult();
        final CloudPersonalContact contact = ContactsUtils.translateDeviceContactToCloud(mContactInfo.contactID, translateResult);
        if (contact != null && !contact.isEmpty()) {
            if (translateResult.flag != 0) {
                AlertDialog.Builder builder = RcAlertDialog.getBuilder(this);
                builder.setTitle(R.string.dialog_title_warning)
                        .setMessage(R.string.device_contact_translate_to_cloud_out_of_range_msg)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onImportContact(contact);
                            }
                        }).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            } else {
                onImportContact(contact);
            }
        }
    }

    private void onImportContact(CloudPersonalContact contact) {

        ContactsProvider.getInstance().addContact(contact);


        //make toast show
        Toast.makeText(this, String.format(getString(R.string.device_contact_translate_to_cloud_success, getString(R.string.app_name))), Toast.LENGTH_LONG).show();

        //update UI
        //previously the contact info is device, after import to cloud, we have to update screen to cloud info
        if (mEventDetail instanceof ViewPersonalContact) {
            mEventDetail.setContactType(Contact.ContactType.CLOUD_PERSONAL);
            mEventDetail.setContactId(contact.getId());
            mContactInfo = mEventDetail.bindContactInfo();
            mContactType = mEventDetail.getContactType();
            setUI();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        try {
            if (mEventDetail != null) {
                mContactInfo = mEventDetail.bindContactInfo();
                mContactType = mEventDetail.getContactType();

                //if the contact type is cloud contact, we need to do iSync when user back to the detail screen
                if (!mRegisterCloudContactObserver) {
                    this.registerReceiver(mContactObserver, new IntentFilter(RCMConstants.ACTION_UI_CONTACT_CHANGED));
                    mRegisterCloudContactObserver = true;
                }
                if (mContactType == Contact.ContactType.CLOUD_PERSONAL) {
                    CloudContactSyncService.sendCommand(this, CloudContactSyncService.CONTACT_SERVER_SYNC_TO_LOCAL);
                }
            } else {
                MktLog.e(TAG, "CommonEventDetailActivity onResume mEventDetail= null");
                finish();
            }
            if (mContactInfo == null) {
                MktLog.e(TAG, "CommonEventDetailActivity onResume mContactInfo= null");
                if (mEventDetailType == EVENT_DETAIL_TYPE_CALLLOG) {
                    Toast.makeText(this, getString(R.string.calllog_item_view_error), Toast.LENGTH_LONG).show();
                }
                finish();
            }

            mNameFromContactsList = mContactInfo.displayName;
            if (mEventDetailType == VIEW_FAVORITES_CONTACT) {
                mContactInfo.phoneNumber = mFavoritePhoneNumber;
            }
            setUI();


        } catch (Exception e) {
            MktLog.e(TAG, "onResume() : " + e.getMessage());
        }
    }

    @Override
    public void onBack() {
        finish();
    }
}
