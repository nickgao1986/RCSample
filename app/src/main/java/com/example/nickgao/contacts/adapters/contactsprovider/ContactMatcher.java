package com.example.nickgao.contacts.adapters.contactsprovider;

import android.text.TextUtils;

/**
 * Created by nick.gao on 1/28/17.
 */

public class ContactMatcher {

    public static boolean isE164NumberMatch(String e164Number, String number2) {
        if(TextUtils.isEmpty(e164Number) || TextUtils.isEmpty(number2)) {
            return false;
        }

        return e164Number.equals(number2);
    }


    public static boolean isFullE164NumberMatch(String number1, String number2) {
        if(TextUtils.isEmpty(number1) || TextUtils.isEmpty(number2)) {
            return false;
        }

        return number1.equals(number2);
    }
}
