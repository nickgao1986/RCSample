package com.example.nickgao.eventdetail;

import android.database.Cursor;
import android.net.Uri;

import com.example.nickgao.service.model.extensioninfo.ProfileImage;

import java.util.List;

/**
 * Created by nick.gao on 1/30/17.
 */

public class EventDetailUtils {

    private static final String TAG = "[RC]EventDetailUtils";

    public static final class EventDetailContactInfo {
        public String displayName;
        public String phoneNumber;
        public long contactID;
        public String extMailboxId;
        public long phoneID;
        public String lookupKey;
        public boolean isPersonal;
        public boolean isIterCom = false;
        public List<ContactField> contactFields;
        //public boolean isRawContentId;
//        public boolean isDeptExtension;
        public String userType;
        public Cursor extCursor;
        public Uri extUri;
        ProfileImage profileImage;

    }

    public static final class ContactField {
        public long phoneID;
        public int fieldType;
        public String fieldTag;
        public String fieldOriginalValue;
        public String fieldDisplayValue;
        public boolean isBold;
        public boolean addedToFavorite;
        public boolean canAddToFavorite;
    }

}
