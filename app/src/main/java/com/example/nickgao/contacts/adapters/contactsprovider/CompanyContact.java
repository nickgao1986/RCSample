package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.service.model.extensioninfo.ProfileImage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by nick.gao on 1/28/17.
 */

public class CompanyContact extends Contact {
    public static final int PHONE_TYPE_EXTENSION = -1;
    public static final int PHONE_TYPE_MOBILE = -2;
    public static final int PHONE_TYPE_DID = -3;

    private String mPin;
    private String mMobilePhone;
    private String mEmail;
    private String mUseType;
    private boolean mVisible = true;
    private HashMap<String, DirectNumber> mDirectNumbers = new HashMap<>();

    private ProfileImage mProfileImage;
    private String mEtag;

    public static class DirectNumber {
        private String usageType;
        private String directNumberType;
        private String directNumberValue;

        public DirectNumber(String usageType, String phoneType, String number) {
            this.usageType = usageType;
            this.directNumberType = phoneType;
            this.directNumberValue = number;
        }

        public String getUsageType() {
            return this.usageType;
        }

        public String getType() {
            return this.directNumberType;
        }

        public String getValue() {
            return this.directNumberValue;
        }
    }

    public CompanyContact(){
    }

    public CompanyContact(long id, String displayName, String pin, String mobilePhone, String email, String useType) {
        super(ContactType.CLOUD_COMPANY, id, displayName);
        mPin = pin;
        mMobilePhone = mobilePhone;
        mEmail = email;
        mUseType = useType;
    }

    @Override
    public String toString() {
        return "pin=" + mPin + " mobilePhone=" + mMobilePhone + " email="+ mEmail + " useType=" + mUseType;
    }

    @Override
    public Bitmap getImage() {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Intent getDetailsActivityIntent(Context context) {
        return null;
    }

    @Override
    public Uri getContactUri() {
        return UriHelper.getUri(RCMProvider.EXTENSIONS, CurrentUserSettings.getSettings().getCurrentMailboxId(), getId());
    }

    @Override
    protected boolean isInternalMatched(TypeValue search, boolean extension, boolean isFuzzyMatch, List<MatchInfo> matchList, String countryCode, String nationalPrefix) {
        String filter = search.getValue();
        if(commonMatch(this.getDisplayName(), filter, isFuzzyMatch)) {
            this.addMatchInfo(matchList,new MatchInfo(MatchType.NAME, filter));
            return true;
        }

        if(commonMatch(mEmail, filter, isFuzzyMatch)) {
            this.addMatchInfo(matchList,new MatchInfo(MatchType.EMAIL, filter));
            return true;
        }

        if(search.getType() == FILTER_TYPE_NUMBER) {
            if ((extension && numberMatch(mPin, filter, isFuzzyMatch)) || numberMatch(mMobilePhone, filter, isFuzzyMatch)
                    || (isFuzzyMatch && numberMatch_prefix(mMobilePhone, filter, countryCode, nationalPrefix))) {
                this.addMatchInfo(matchList, new MatchInfo(MatchType.NUMBER, filter));
                return true;
            }

            Iterator it = mDirectNumbers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DirectNumber directNumber = (DirectNumber) pair.getValue();
                if (numberMatchNoCheckNull(directNumber.directNumberValue, filter, isFuzzyMatch) || (isFuzzyMatch && numberMatch_prefix(directNumber.directNumberValue, filter, countryCode, nationalPrefix))) {
                    this.addMatchInfo(matchList, new MatchInfo(MatchType.NUMBER, filter));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isFullE164NumberMatched(String e164Number, boolean extension, TypeValue matchValue) {
        if(extension && ContactMatcher.isFullE164NumberMatch(e164Number, mPin)) {
            if(matchValue != null) {
                matchValue.setType(PHONE_TYPE_EXTENSION);
                matchValue.setValue(mPin);
            }
            return true;
        }

        if(ContactMatcher.isFullE164NumberMatch(e164Number, mMobilePhone)) {
            if(matchValue != null) {
                matchValue.setType(PHONE_TYPE_MOBILE);
                matchValue.setValue(mMobilePhone);
            }
            return true;
        }


        Iterator it = mDirectNumbers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            DirectNumber directNumber = (DirectNumber) pair.getValue();
            if(ContactMatcher.isFullE164NumberMatch(e164Number, directNumber.directNumberValue)) {
                if(matchValue != null) {
                    matchValue.setType(PHONE_TYPE_DID);
                    matchValue.setValue(directNumber.directNumberValue);
                }
                return true;
            }
        }

        return false;
    }

    public String getPin() {
        return mPin;
    }

    public String getUseType() {
        return mUseType;
    }

    public String getMobilePhone() {
        return mMobilePhone;
    }

    public String getEtag() {
        return mEtag;
    }

    public void setEtag(String mEtag) {
        this.mEtag = mEtag;
    }

    public ProfileImage getProfileImage() {
        return mProfileImage;
    }

    public void setProfileImage(ProfileImage profileImage) {
        this.mProfileImage = profileImage;
    }

    public String getEmail(Context context) {
        if(mEmail == null) {
            mEmail = CloudCompanyContactLoader.getCompanyContactMailAddress(context, getId());
        }
        return mEmail;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        this.mVisible = visible;
    }

    public void setDirectNumbers(List<DirectNumber> numbers) {
        mDirectNumbers.clear();
        for(int i = 0; i < numbers.size(); i++) {
            DirectNumber directNumber = numbers.get(i);
            mDirectNumbers.put(directNumber.getValue(), directNumber);
        }
    }

    public Collection<DirectNumber> getDirectNumbers() {
        return mDirectNumbers.values();
    }

    public DirectNumber getDirectNumber(String number) {
        return mDirectNumbers.get(number);
    }

    @Override
    public boolean hasPhoneNumber() {
        return true;
    }

    @Override
    public boolean hasEmail() {
        return !TextUtils.isEmpty(mEmail);
    }

    @Override
    public void clone(Contact src) {
        super.clone(src);
        CompanyContact contact = (CompanyContact)src;
        this.mPin = contact.getPin();
        this.mMobilePhone = contact.getMobilePhone();
        this.mEmail = contact.mEmail;
        this.mVisible = contact.isVisible();
        this.mEtag= contact.getEtag();
        this.mProfileImage = contact.getProfileImage();
        this.mUseType = contact.getUseType();
        mDirectNumbers.clear();
        Collection<DirectNumber> directNumbers = contact.getDirectNumbers();
        for(DirectNumber directNumber : directNumbers) {
            mDirectNumbers.put(directNumber.getValue(), new DirectNumber(directNumber.getUsageType(),directNumber.getType(), directNumber.getValue()));
        }
    }
}
