package com.example.nickgao.contacts;

import android.text.TextUtils;

import com.example.nickgao.contacts.adapters.contactsprovider.Contact;
import com.example.nickgao.logging.MktLog;
import com.ringcentral.simhash.ISimHashAlgorithm;
import com.ringcentral.simhash.ShHashType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick.gao on 2/3/17.
 */

/**
 * Created by jerry.cai on 11/30/15.
 */
public class SimHashHelper {
    static {
        System.loadLibrary("simhash");
    }

    static final int kDefaultThreshold = 1;
    static final int kDefaultDisplayNameWeight = 4;
    static final int kDefaultFirstNameWeight = 4;
    static final int kDefaultLastNameWeight = 4;
    static final int kDefaultMiddleNameWeight = 4;
    static final int kDefaultPhoneNumberWeight = 4;
    static final int kDefaultEmailWeight = 4;

    public static long generateNameSimHash(final String firstName, final String middleName, final String lastName,
                                           List<Contact.TypeValue> phoneNumbers,
                                           List<Contact.TypeValue> emails
    ) {
        //NAME
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(firstName)) {
            sb.append(firstName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            sb.append(" ").append(lastName);
        }
        String displayName = sb.toString().trim();

        return generateNameSimHash(displayName, middleName, phoneNumbers, emails);
    }

    public static long generateNameSimHash(final String displayName, final String middleName,
                                           List<Contact.TypeValue> phoneNumbers,
                                           List<Contact.TypeValue> emails
    ) {
        ArrayList<String> fields = new ArrayList<>();
        ArrayList<Integer> weights = new ArrayList<>();
        ArrayList<ShHashType> types = new ArrayList<>();

        //display name
        if (!isEmpty(displayName)) {
            fields.add(displayName.trim());
            weights.add(kDefaultDisplayNameWeight);
            types.add(ShHashType.HASH_TYPE_BKDR);
        }

        if (!isEmpty(middleName)) {
            fields.add(middleName.trim());
            weights.add(kDefaultMiddleNameWeight);
            types.add(ShHashType.HASH_TYPE_BKDR);
        }

        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            //PHONES
            for (Contact.TypeValue phoneNumber : phoneNumbers) {
                if (!isEmpty(phoneNumber.getValue())) {
                    fields.add(phoneNumber.getValue().trim());
                    weights.add(kDefaultPhoneNumberWeight);
                    types.add(ShHashType.HASH_TYPE_BKDR);
                }
            }
        }

        //EMAILS
        if (emails != null && !emails.isEmpty()) {
            for (Contact.TypeValue email : emails) {
                if (!isEmpty(email.getValue())) {
                    fields.add(email.getValue().trim());
                    weights.add(kDefaultEmailWeight);
                    types.add(ShHashType.HASH_TYPE_BKDR);
                }
            }
        }

        try {
            if (!fields.isEmpty()) {
                return ISimHashAlgorithm.toHash(fields, weights, types);
            } else {
                MktLog.d("SimHashHelper", "generateNameSimHash(), empty info!");
            }
        } catch (Throwable th) {
            MktLog.e("SimHashHelper", th.toString());
        } finally {
            fields.clear();
            weights.clear();
            types.clear();
        }

        return 0;
    }

    public static long generateNameSimHashWithDisplayName(final String fullName, final String firstName, final String middleName, final String lastName,
                                                          List<Contact.TypeValue> phoneNumbers,
                                                          List<Contact.TypeValue> emails
    ) {
        if (!TextUtils.isEmpty(fullName)) {
            String _fullName = fullName.trim();
            if (_fullName.length() > 0) {
                return generateNameSimHash(_fullName, middleName, phoneNumbers, emails);
            }
        }

        return generateNameSimHash(firstName, middleName, lastName, phoneNumbers, emails);
    }

    /*
    public static long generatePhoneSimHash(List<Contact.TypeValue> phoneNumbers) {
        ArrayList<ISimHashTuple> list = new ArrayList<>();
        for (Contact.TypeValue phoneNumber : phoneNumbers) {
            if(!TextUtils.isEmpty(phoneNumber.getValue())) {
                list.add(ISimHashTuple.generate(phoneNumber.getValue(), kDefaultPhoneNumberWeight, ShHashType.HASH_TYPE_BKDR));
            }
        }
        return ISimHashAlgorithm.hashValue(list);
    }

    public static long generateEmailSimHash(List<Contact.TypeValue> emails) {
        ArrayList<ISimHashTuple> list = new ArrayList<>();
        for (Contact.TypeValue email : emails) {
            if(!TextUtils.isEmpty(email.getValue())) {
                list.add(ISimHashTuple.generate(email.getValue(), kDefaultEmailWeight, ShHashType.HASH_TYPE_BKDR));
            }
        }
        return ISimHashAlgorithm.hashValue(list);
    }*/

    public static boolean isNameHashEqual(long h1, long h2) {
        return ISimHashAlgorithm.isEqual(h1, h2, kDefaultThreshold);
    }

    /*
    public static boolean isPhoneHashEqual(long h1, long h2) {
        return ISimHashAlgorithm.isEqual(h1,h2, kDefaultPhoneThreshold);
    }

    public static boolean isEmailHashEqual(long h1, long h2) {
        return ISimHashAlgorithm.isEqual(h1,h2, kDefaultEmailThreshold);
    }*/

    private static boolean isEmpty(String str) {
        return (str == null || TextUtils.isEmpty(str.trim()));
    }
}