package com.example.nickgao.contacts;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContact;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContactLoader;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.utils.RCMConstants;

/**
 * Created by nick.gao on 1/30/17.
 */

public class CloudPersonalContactOperator extends ContactOperator<CloudPersonalContact> {

    public CloudPersonalContactOperator(CloudPersonalContact contact, Activity activity) {
        super(contact, activity);
    }


    @Override
    public void viewDetails(int eventDetailType, String eventDetailName, String eventDetailCompanyFrom) {
        MktLog.d(TAG, "View Contact");
        Intent intent = mContact.getDetailsActivityIntent(mActivity);
        intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_TYPE, eventDetailType);
        intent.putExtra(RCMConstants.EXTRA_EVENT_DETAIL_FLURRY_EVENT_NAME, eventDetailName);
        intent.putExtra(RCMConstants.EXTRA_CONTACT_TYPE_FROM, Contact.ContactType.CLOUD_PERSONAL.ordinal());
        mActivity.startActivity(intent);
    }


    @Override
    public boolean toggleFavoriteFromAddFavorite(String flurryAddFrom, String flurryDeleteFrom) {
        if(CloudPersonalContactLoader.isFavorite(mActivity,mContact.getId())) {
            signalError(R.string.toast_contact_already_add_to_favorite);
            return false;
        }else{
            return CloudPersonalContactLoader.setFavorite(mActivity,mContact.getId(),true);
        }
    }

    private Toast getToast(){
        return Toast.makeText(mActivity, "", Toast.LENGTH_SHORT);
    }


    protected void signalError(int resId){
        Toast toast = getToast();
        toast.setText(mActivity.getString(resId));
        toast.show();
    }
}

