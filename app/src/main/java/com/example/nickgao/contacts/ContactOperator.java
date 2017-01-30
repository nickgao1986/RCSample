package com.example.nickgao.contacts;

import android.app.Activity;

import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContact;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.contacts.adapters.contactsprovider.DeviceContact;
import com.example.nickgao.logging.MktLog;

/**
 * Created by nick.gao on 1/30/17.
 */

public abstract class ContactOperator<T extends Contact> {


    public static ContactOperator getContactsOperator(
            Contact contact,
            Activity activity) {
        MktLog.i("AAA","====contact.getType="+contact.getType());
        switch (contact.getType()){
            case DEVICE:
                return new DeviceContactOperator(
                        (DeviceContact) contact,
                        activity);
            case CLOUD_PERSONAL:
                return new CloudPersonalContactOperator((CloudPersonalContact)contact, activity);
            case CLOUD_COMPANY:
//                return new CompanyContactOperator(
//                        (CompanyContact) contact,
//                        activity);

        }

        throw new IllegalArgumentException("invalid type of the contact: " + contact.getType());
    }

    protected String TAG = this.getClass().getSimpleName();
    protected Activity mActivity;
    protected T mContact;

    public ContactOperator(T contact, Activity activity) {

        if (contact == null) {
            throw new IllegalArgumentException("contact");
        }
        mContact = contact;

        if (activity == null) {
            throw new IllegalArgumentException("activity");
        }
        mActivity = activity;

    }

    public abstract void viewDetails(int eventDetailType, String eventDetailName, String eventDetailCompanyFrom);

}
