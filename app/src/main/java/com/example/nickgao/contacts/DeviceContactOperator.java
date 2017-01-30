package com.example.nickgao.contacts;

import android.app.Activity;
import android.content.Intent;

import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.DeviceContact;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.RCMConstants;

/**
 * Created by nick.gao on 1/30/17.
 */

public class DeviceContactOperator extends ContactOperator<DeviceContact> {

    public DeviceContactOperator(DeviceContact contact, Activity activity) {
        super(contact, activity);
    }

    @Override
    public void viewDetails(int eventDetailType, String eventDetailName, String eventDetailCompanyFrom) {
        MktLog.d(TAG, "View Contact");
        Intent intent = mContact.getDetailsActivityIntent(mActivity);
        intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_TYPE, eventDetailType);
        intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_FLURRY_EVENT_NAME, eventDetailName);
        intent.putExtra(RCMConstants.EXTRA_CONTACT_TYPE_FROM, Contact.ContactType.DEVICE.ordinal());
        mActivity.startActivity(intent);
    }

}
