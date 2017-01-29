package com.example.nickgao.androidsample11;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.example.nickgao.R;

/**
 * Created by nick.gao on 1/29/17.
 */

public class ContactsActivity extends android.support.v4.app.FragmentActivity {

    public static final int ID_PERMISSION_REQUEST_CONTACT = 40401;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list_content);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestContactPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, ID_PERMISSION_REQUEST_CONTACT);
    }

}