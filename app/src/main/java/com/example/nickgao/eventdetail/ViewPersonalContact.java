package com.example.nickgao.eventdetail;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.eventdetail.EventDetailUtils.EventDetailContactInfo;

/**
 * Created by nick.gao on 1/30/17.
 */

public class ViewPersonalContact extends EventDetailBase {

    public static final String PERSONAL_ID = "personal_id";
    public static final String DISPLAY_NAME = "display_name";
    private String mDisplayName = "";

    public ViewPersonalContact(Intent intent) {
        String idStr = intent.getStringExtra(ViewPersonalContact.PERSONAL_ID);
        mDisplayName = intent.getStringExtra(ViewPersonalContact.DISPLAY_NAME);
        setContactId(Long.parseLong(idStr));
        setContactType(Contact.ContactType.DEVICE);
    }

    @Override
    public boolean isKnownContact() {
        return true;
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void getDetailBetweenView(View view) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void updateDetailHeader() {

    }

    @Override
    public TextView getBlockStateLabel() {
        return null;
    }

    @Override
    public EventDetailContactInfo bindContactInfo() {
        EventDetailContactInfo contactInfo = new EventDetailContactInfo();
        contactInfo.contactID = mContactId;
        contactInfo.displayName = mDisplayName;
        return contactInfo;
    }

    @Override
    public void setNameAfterUpdate(TextView mTvDisplayName) {

    }
}
