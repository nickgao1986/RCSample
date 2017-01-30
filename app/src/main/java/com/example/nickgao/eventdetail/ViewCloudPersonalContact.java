package com.example.nickgao.eventdetail;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.example.nickgao.eventdetail.EventDetailUtils.EventDetailContactInfo;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;

/**
 * Created by nick.gao on 1/30/17.
 */

public class ViewCloudPersonalContact extends EventDetailBase {
    public static final String PERSONAL_ID = "personal_id";
    public static final String DISPLAY_NAME = "display_name";

    private Context mContext;
    private String mDisplayName;

    public ViewCloudPersonalContact(Context context, Intent intent) {
        mContext = context;
        mDisplayName = intent.getStringExtra(ViewCloudPersonalContact.DISPLAY_NAME);
        setContactId(intent.getLongExtra(ViewCloudPersonalContact.PERSONAL_ID, 0));
        setContactType(Contact.ContactType.CLOUD_PERSONAL);
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
    public EventDetailUtils.EventDetailContactInfo bindContactInfo() {
        EventDetailContactInfo contactInfo = null;
        Contact contact = ContactsProvider.getInstance().getContact(Contact.ContactType.CLOUD_PERSONAL, mContactId, true);
        if(contact == null) {
            contact = mContact;
        }else {
            mContact = contact;
        }
        if(contact != null) {
            contactInfo = new EventDetailContactInfo();
            contactInfo.contactID = mContactId;
            String displayName = contact.getDisplayName();
            if(!TextUtils.isEmpty(displayName)) {
                contactInfo.displayName = displayName;
            }else {
                contactInfo.displayName = (!TextUtils.isEmpty(mDisplayName))? mDisplayName : "";
            }
        }
        return contactInfo;
    }

    @Override
    public void setNameAfterUpdate(TextView mTvDisplayName) {
    }

}
