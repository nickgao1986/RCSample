package com.example.nickgao.androidsample11;

import android.os.Bundle;

import com.example.nickgao.R;

/**
 * Created by nick.gao on 1/29/17.
 */

public class CalendarActivity extends android.support.v4.app.FragmentActivity {

    public static final int ID_PERMISSION_REQUEST_CONTACT = 40401;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_list_content);
    }

}
