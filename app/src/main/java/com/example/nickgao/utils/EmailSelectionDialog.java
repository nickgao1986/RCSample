package com.example.nickgao.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.SimpleAdapter;

import com.example.nickgao.R;
import com.example.nickgao.contacts.adapters.contactsprovider.CloudPersonalContact;
import com.example.nickgao.contacts.adapters.contactsprovider.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nick.gao on 2/3/17.
 */

public class EmailSelectionDialog implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {

    private Context mContext;
    private AlertDialog mDialog;
    private SimpleAdapter mEmailAdapter;
    private List<HashMap<String, String>> mFillMaps;

    public EmailSelectionDialog(Context context, Contact.ContactType contactType,  List<Contact.TypeValue> emails) {
        mContext = context;
        String[] from = new String[] {
                Mail.EMAIL,
                Mail.TYPE
        };
        int[] to = new int[] {
                android.R.id.text1,
                android.R.id.text2
        };

        mFillMaps = new ArrayList<>();

        for (Contact.TypeValue e : emails) {
            HashMap<String, String> map = new HashMap<>();
            map.put(Mail.EMAIL, e.getValue());
            if(Contact.ContactType.DEVICE == contactType) {
                map.put(Mail.TYPE, "");
            }else if(Contact.ContactType.CLOUD_PERSONAL == contactType) {
                map.put(Mail.TYPE, CloudPersonalContact.EmailType.values()[e.getType()].toString());
            }

            mFillMaps.add(map);
        }
        mEmailAdapter = new SimpleAdapter(mContext, mFillMaps, R.layout.simple_list_item_2, from, to);

        AlertDialog.Builder dialogBuilder =
                RcAlertDialog.getBuilder(mContext).setAdapter(mEmailAdapter, this).setTitle(
                        context.getString(R.string.contacts_send_email_selection_title));
        mDialog = dialogBuilder.create();
    }

    public void show() {
        if (mFillMaps.size() == 0) {
            onClick(mDialog, 0);
            return;
        }
        mDialog.show();
    }

    public void onClick(DialogInterface dialog, int which) {
        EmailSender emailSender = new EmailSender(mContext);
        String[] to = { mFillMaps.get(which).get(Mail.EMAIL) };
        emailSender.sendEmail(to, "", "", null);
    }

    public void onDismiss(DialogInterface dialog) {
        mContext = null;
        mDialog = null;
        mEmailAdapter = null;
        mFillMaps = null;
    }

    private class Mail {
        public static final String EMAIL = "EMAIL";
        public static final String TYPE = "TYPE";
    }
}
