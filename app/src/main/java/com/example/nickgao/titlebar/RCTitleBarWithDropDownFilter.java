package com.example.nickgao.titlebar;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.nickgao.R;
import com.example.nickgao.rcproject.RingCentralApp;

import java.util.ArrayList;

/**
 * Created by nick.gao on 1/28/17.
 */

public class RCTitleBarWithDropDownFilter extends RCMainTitleBar implements View.OnClickListener {
    private ImageView mBtnDropDownImageview;
    private boolean mIsShowDialog = false;
    private DropDownFilterDialog mDropDownFilterDialog;
    private int mCurrentIndex = 0;
    private int mCurrentState = STATE_ALL;
    private boolean mHasDropDown = false;
    private boolean mIsMessageTitleBar = false;
    private boolean mIsDocumentTitleBar = false;
    public DropDownMenuClicked mDropDownMenuClicked;
    private Context mContext;
    private static final String TAG = "[RC]RCTitleBarWithDropDownFilter";
    public static final int MAX_MESSAGES_COUNT_TO_BE_DISPLAYED = 99;

    public static final int MESSAGE_ALL = 0;
    public static final int MESSAGE_TEXT = 1;
    public static final int MESSAGE_VOICE = 2;
    public static final int MESSAGE_FAX = 3;


    public final static int STATE_ALL = 11;
    public final static int STATE_TEXT = 12;
    public final static int STATE_VOICE = 13;
    public final static int STATE_FAX = 14;

    public static final int CONTACTS_ALL_TAB = 0;
    public static final int CONTACTS_COMPANY_TAB = 1;
    public static final int CONTACTS_DEVICE_TAB = 2;
    public static final int CONTACTS_FAVORITE_TAB = 3;

    public static final int FAVORITES_COMPANY_TAB = 0;
    public static final int FAVORITES_PERSONAL_TAB = 1;

    public static final int ALL_TAB = 0;
    public static final int MISSED_TAB = 1;

    public static final int DOCUMENT_TAB = 0;
    public static final int DRAFTS_TAB = 1;
    public static final int OUTBOX_TAB = 2;

    public static final int MODE_FAX = 1;
    public static final int MODE_GENERAL = 2;


    private ArrayList<DropDownItem> mDropDownItemList;

    private View mBanner;
    private LinearLayout mTitleLayout;

    public int getState() {
        int state = STATE_ALL;
        if(isInFaxTab()) {
            state = STATE_FAX;
        }else if(isInTextTab()) {
            state = STATE_TEXT;
        }else if(isInVoiceTab()) {
            state = STATE_VOICE;
        }
        return state;
    }

    public int getDocumentState() {
        return mCurrentIndex;
    }

    public void setDropDownItemList(ArrayList<DropDownItem> mDropDownItemList) {
        this.mDropDownItemList = mDropDownItemList;
        if(mIsMessageTitleBar) {
            if(mHasDropDown && !isNeedToHide()) {
                mBtnDropDownImageview.setVisibility(View.VISIBLE);
            }else{
                mBtnDropDownImageview.setVisibility(View.GONE);
            }
        }
    }

    public void setDropDownItemList(ArrayList<DropDownItem> mDropDownItemList, boolean hasShowTextPermission, boolean hasVMPermission, boolean hasFaxPermission) {
        this.mDropDownItemList = mDropDownItemList;
        if(mIsMessageTitleBar) {
            if(mHasDropDown && !isNeedToHide(hasShowTextPermission, hasVMPermission, hasFaxPermission)) {
                mBtnDropDownImageview.setVisibility(View.VISIBLE);
            }else{
                mBtnDropDownImageview.setVisibility(View.GONE);
            }
        }
    }

    public RCTitleBarWithDropDownFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RCTitleBarWithDropDownFilter(Context context) {
        super(context, null);
        init(context, null);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);
        mContext = context;
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);
        mHasDropDown = a.getBoolean(R.styleable.TitleBar_title_has_dropdown,false);

        mIsMessageTitleBar = a.getBoolean(R.styleable.TitleBar_is_message_titlebar,false);
        mIsDocumentTitleBar = a.getBoolean(R.styleable.TitleBar_is_document_titlebar,false);
        a.recycle();

        mBtnDropDownImageview = (ImageView) findViewById(R.id.btn_drop_down_imageview);
        if(mIsMessageTitleBar) {
            if(mHasDropDown && !isNeedToHide()) {
                mBtnDropDownImageview.setVisibility(View.VISIBLE);
            }else{
                mBtnDropDownImageview.setVisibility(View.GONE);
            }
        }else{
            if(mHasDropDown) {
                mBtnDropDownImageview.setVisibility(View.VISIBLE);
            }else{
                mBtnDropDownImageview.setVisibility(View.GONE);
            }
        }

        mTitleLayout = (LinearLayout)findViewById(R.id.title_layout);
        mTitleLayout.setOnClickListener(this);
    }


    public void setLeftImgResVisible() {
        mBtnTopLeft.setVisibility(View.VISIBLE);
        mPhotoLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (null == mHeaderClickListener) {
            return;
        }
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_layout:
                if(mBtnDropDownImageview.getVisibility() == View.GONE) {
                    return;
                }
                if (!mIsShowDialog) {
                    // will show dialog
                    showDropDownFilter(true);
                    mIsShowDialog = true;
                } else {
                    mDropDownFilterDialog.hideDropDownFilter(true);
                    mIsShowDialog = false;
                }
                break;
        }

    }

    public void hideDropDownDialog() {
        if(mIsShowDialog && mDropDownFilterDialog != null) {
            rotatePlusButton(mContext, true);
            mDropDownFilterDialog.dismiss();
            mIsShowDialog = false;
        }
    }


    public void setOnDropDownMenuClick(DropDownMenuClicked mDropDownMenuClicked) {
        this.mDropDownMenuClicked = mDropDownMenuClicked;
    }


    public void setTitleBarEditMode(int visible) {
        if(visible == View.VISIBLE) {
            mBtnDropDownImageview.setVisibility(View.VISIBLE);
            setHeaderPhotoViewVisibility(View.VISIBLE);
        }else{
            mBtnDropDownImageview.setVisibility(View.GONE);
            setHeaderPhotoViewVisibility(View.GONE);
        }
    }

    public void setDropDownVisibility(int visibility) {
        mHasDropDown = (View.VISIBLE == visibility);
        mBtnDropDownImageview.setVisibility(visibility);
    }


    public void rotatePlusButton(Context context, boolean isUp) {
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(
                context, isUp ? R.anim.flip_up : R.anim.flip_down);
        animatorSet.setTarget(mBtnDropDownImageview);
        animatorSet.start();
    }



    public String getCurrentTabName() {
        if(mDropDownItemList != null) {
            if(mCurrentIndex > (mDropDownItemList.size()-1)) {
                return mDropDownItemList.get(0).getName();
            }
            DropDownItem dropDownItem = mDropDownItemList.get(mCurrentIndex);
            return dropDownItem.getName();
        }
        return null;
    }

    public  boolean isInAllTab() {
        return RingCentralApp.getContextRC().getResources().getString(
                R.string.messages_bar_item_all).equals(getTabNameByIndex(mCurrentIndex));
    }

    public boolean isInTextTab() {
        return mContext.getResources().getString(
                R.string.messages_bar_item_text).equals(getTabNameByIndex(mCurrentIndex));
    }

    public boolean isInVoiceTab() {
        return mContext.getResources().getString(
                R.string.messages_bar_item_voice).equals(getTabNameByIndex(mCurrentIndex));
    }

    public boolean isInFaxTab() {
        return mContext.getResources().getString(
                R.string.messages_bar_item_fax).equals(getTabNameByIndex(mCurrentIndex));
    }

    private String getNameViaState(int state) {
        String name = "";
        switch (state) {
            case STATE_ALL:
                name = RingCentralApp.getContextRC().getResources().getString(
                        R.string.messages_bar_item_all);
                break;
            case STATE_TEXT:
                name = RingCentralApp.getContextRC().getResources().getString(
                        R.string.messages_bar_item_text);
                break;
            case STATE_VOICE:
                name = RingCentralApp.getContextRC().getResources().getString(
                        R.string.messages_bar_item_voice);
                break;
            case STATE_FAX:
                name = RingCentralApp.getContextRC().getResources().getString(
                        R.string.messages_bar_item_fax);
                break;
        }
        return name;
    }



    public  String getTabNameByIndex(int i) {
        if(mDropDownItemList != null) {
            if(i > (mDropDownItemList.size()-1)) {
                return mDropDownItemList.get(0).getName();
            }
            DropDownItem dropDownItem = mDropDownItemList.get(i);
            return dropDownItem.getName();
        }
        return null;
    }


    private void showDropDownFilter(boolean showAnimation) {
        if (mDropDownFilterDialog == null) {
            mDropDownFilterDialog = new DropDownFilterDialogForPhone(getContext());

        }

        rotatePlusButton(mContext, false);
        mDropDownFilterDialog.setCurrentIndex(mCurrentIndex);

        setNameText(getCurrentTabName());
        mDropDownFilterDialog.setTopMenuItemList(mDropDownItemList);
        mDropDownFilterDialog.showDialog(showAnimation, mBanner);
        mDropDownFilterDialog.setDropDownClickListener(new DropDownFilterDialog.OnDropdownClickListener() {
            @Override
            public void onDropdownHide() {
                rotatePlusButton(mContext, true);
                mIsShowDialog = false;
            }

            @Override
            public void onClickItem(int index) {
                if (index == mCurrentIndex) {
                    return;
                }
                mCurrentIndex = index;
                mDropDownMenuClicked.onDropDownMenuClicked(index);
            }
        });
    }



    /*****************************************init filter*************************************************/
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public void setBanner(View mBanner) {
        this.mBanner = mBanner;
    }



    public boolean isNeedToHide() {
        return false;
    }


    public boolean isNeedToHide(boolean hasShowTextPermission, boolean hasVMPermission, boolean hasFaxPermission) {
        int count = 0;
        if (hasVMPermission) {
            count++;
        }

        if (hasFaxPermission) {
            count++;
        }

        if (hasShowTextPermission) {
            count++;
        }

        return count == 0 || count == 1;
    }


    public void initMessageFilterWithState(int state) {
        String tabNameViaState = getNameViaState(state);
        if(getTabNameByIndex(MESSAGE_ALL).equals(tabNameViaState)) {
            mCurrentIndex = MESSAGE_ALL;
        }else if(getTabNameByIndex(MESSAGE_TEXT).equals(tabNameViaState)) {
            mCurrentIndex = MESSAGE_TEXT;
        }else if(getTabNameByIndex(MESSAGE_VOICE).equals(tabNameViaState)) {
            mCurrentIndex = MESSAGE_VOICE;
        }else if(getTabNameByIndex(MESSAGE_FAX).equals(tabNameViaState)) {
            mCurrentIndex = MESSAGE_FAX;
        }else{
            mCurrentIndex = MESSAGE_ALL;
        }
        setNameText(getCurrentTabName());
    }


    public void initMessageFilterWithIndex(int index) {
        mCurrentIndex = index;
        setNameText(getCurrentTabName());
    }

    public void initContactsFilterWithState(int currentTab) {
        switch (currentTab){
            case CONTACTS_ALL_TAB:
                setText(R.string.filter_name_all);
                mCurrentIndex = CONTACTS_ALL_TAB;
                return;
            case CONTACTS_COMPANY_TAB:
                setText(R.string.filter_name_company);
                mCurrentIndex = CONTACTS_COMPANY_TAB;
                return;
            case CONTACTS_DEVICE_TAB:
                setText(R.string.filter_name_personal);
                mCurrentIndex = CONTACTS_DEVICE_TAB;
                return;
            case CONTACTS_FAVORITE_TAB:
                setText(R.string.filter_name_favorite_contacts);
                mCurrentIndex = CONTACTS_FAVORITE_TAB;
                return;
        }

        throw new IllegalStateException("unknown tab: " + currentTab);
    }

    public void initAddFavoritesFilterWithState(int currentTab) {
        switch (currentTab){
            case CONTACTS_ALL_TAB:
                setText(R.string.filter_name_all);
                mCurrentIndex = CONTACTS_ALL_TAB;
                return;
            case CONTACTS_COMPANY_TAB:
                setText(R.string.filter_name_company);
                mCurrentIndex = CONTACTS_COMPANY_TAB;
                return;
            case CONTACTS_DEVICE_TAB:
                setText(R.string.filter_name_personal);
                mCurrentIndex = CONTACTS_DEVICE_TAB;
                return;
        }
    }


    public void initCallLogFilterWithState(boolean isALL) {
        if(isALL) {
            setText(R.string.calllog_tab_all_title);
            mCurrentIndex = ALL_TAB;
        }else{
            setText(R.string.calllog_tab_missed_title);
            mCurrentIndex = MISSED_TAB;
        }
    }

    public void initDocumentFilterWithState(int index) {
        mCurrentIndex = index;
        switch (index) {
            case DOCUMENT_TAB:
                setText(R.string.fax_out_documents);
                break;
            case DRAFTS_TAB:
                setText(R.string.fax_out_drafts);
                break;
            case OUTBOX_TAB:
                setText(R.string.outbox);
                break;
        }
    }

}
