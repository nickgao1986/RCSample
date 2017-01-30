package com.example.nickgao.eventdetail;

import android.view.View;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.utils.widget.HeaderViewBase;

/**
 * Created by nick.gao on 1/30/17.
 */

public  abstract class EventDetailBase implements HeaderViewBase.HeaderButtons {
    protected Contact.ContactType mContactType = Contact.ContactType.UNKNOW;
    protected Contact mContact;
    protected long mContactId;

    /**
     * If the back button is pressed, this listener will be invoked.
     */
    public interface OnBackListener {
        void onBack();
    }

    public interface OnEditListener {
        void onEdit();
    }

    public interface OnImportListener {
        void onImport();
    }

    /**
     * If the contact info of the event detail is changed, this listener will be invoked.
     */
    public interface OnContactInfoChangedListener {
        void onContactInfoChanged();
    }

    /**
     * Keep the OnBackListener
     */
    protected OnBackListener mOnBackListener;

    protected OnEditListener mEditButtonListener;

    protected OnImportListener mImportButtonListener;

    /**
     * Keep the OnContactInfoChangedListener
     */
    protected OnContactInfoChangedListener mOnContactChangedListener;

    /**
     * Keep the HeaderViewBase
     */
    protected HeaderViewBase mHeaderViewBase;

    public void setHeaderViewBase(HeaderViewBase headerViewBase) {
        mHeaderViewBase = headerViewBase;
        if (mHeaderViewBase != null) {
            mHeaderViewBase.setButtonsClickCallback(this);
            mHeaderViewBase.setRightImageVisibility(View.GONE);
            mHeaderViewBase.setRightVisibility(View.GONE);
            mHeaderViewBase.setText(R.string.contact_title);
        }
    }

    public HeaderViewBase getHeaderView() {
        return mHeaderViewBase;
    }

    /**
     * Set the OnBackListener
     */
    public void setOnBackListener(OnBackListener listener) {
        mOnBackListener = listener;
    }

    public void setEditButtonListener(OnEditListener listener) {
        mEditButtonListener = listener;
    }

    public void setImportButtonListener(OnImportListener listener) {
        mImportButtonListener = listener;
    }

    /**
     * Set the OnContactInfoChangedListener
     */
    public void setOnContactInfoChangedListener(OnContactInfoChangedListener listener) {
        mOnContactChangedListener = listener;
    }

    public void orientationChanged() {
    }

    @Override
    public void onLeftButtonClicked() {
        if (mOnBackListener != null) {
            mOnBackListener.onBack();
        }
    }

    @Override
    public void onRightButtonClicked() {
        if(mEditButtonListener != null) {
            mEditButtonListener.onEdit();
        }

        if(mImportButtonListener != null) {
            mImportButtonListener.onImport();
        }
    }

    @Override
    public void onRightFirstButtonClicked() {

    }

    @Override
    public void onMenuButtonClicked() {
    }

    /**
     * Return the contact is known or not
     */
    public abstract boolean isKnownContact();

    /**
     * Put cleaning code in this function
     */
    public abstract void onDestroy();

    /**
     * Put close drop menu in this function
     *
     * @author coa.ke
     * @create 2013-12-19
     */
    public abstract void onPause();

    /**
     * Get the detail info view of the event detail, including info like block, Time, etc
     * The data should be set to UI controls before returned by this function
     */
    public abstract void getDetailBetweenView(View view);

    /**
     * Update the detail info view
     */
    public abstract void updateDetailHeader();

    /**
     * Get the TextView which will display the block state
     */
    public abstract TextView getBlockStateLabel();


    /**
     * Get the related contact info of this event detail
     */
    public abstract EventDetailUtils.EventDetailContactInfo bindContactInfo();

    public abstract void setNameAfterUpdate(TextView mTvDisplayName);

    public void setContactId(long contactId) {
        mContactId = contactId;
    }

    public void setContactType(Contact.ContactType contactType) {
        mContactType = contactType;
    }

    public Contact.ContactType getContactType() {
        return mContactType;
    }

}
