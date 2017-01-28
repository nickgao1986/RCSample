package com.example.nickgao.contacts.adapters.contactsprovider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry.cai on 11/5/15.
 */
public abstract class Contact implements Comparable<Contact> {
    public static final int FILTER_TYPE_NUMBER = 1;
    public static final int FILTER_TYPE_STR = 0;
    public static final long INVALIDATED_ID = Long.MIN_VALUE;
    public static final String NO_MANE = "No Name";
    public static final String SPLIT_MODE = "[-,.;\' \'|]";

    public enum ContactType {
        UNKNOW,
        DEVICE,
        CLOUD_COMPANY,
        CLOUD_PERSONAL
    }

    public enum MatchType {
        NONE,
        NAME,
        NUMBER,
        EMAIL,
        COMPANY,
    }

    /**
     * Only matchType is number, matchValue is available.
     */
    public static class MatchInfo {
        private MatchType matchType;
        private String matchValue;

        public MatchInfo(MatchType type, String matchValue) {
            this.matchType = type;
            this.matchValue = matchValue;
        }

        public MatchType getMatchType() {
            return matchType;
        }

        public String getMatchValue() {
            return matchValue;
        }
    }

    public static class DataAndType<T1, T2> {
        private T1 type;
        private T2 value;

        public DataAndType() {
        }

        public DataAndType(T1 type, T2 value) {
            this.type = type;
            this.value = value;
        }

        public T1 getType() {
            return this.type;
        }

        public T2 getValue() {
            return this.value;
        }

        public void setType(T1 type) {
            this.type = type;
        }

        public void setValue(T2 value) {
            this.value = value;
        }
    }

    public static class DataArray<T> {
        int maxSize;
        List<T> dataList = new ArrayList<>();

        public DataArray(int maxSize) {
            this.maxSize = maxSize;
        }

        public List<T> getData() {
            return dataList;
        }

        public boolean addData(T data) {
            if (dataList.size() < maxSize) {
                dataList.add(data);
                return true;
            }
            return false;
        }

        public boolean isFull() {
            return dataList.size() == maxSize;
        }

        public void output(List<T> list) {
            for (T item : dataList) {
                list.add(item);
            }
        }
    }

    public static class TypeValue extends DataAndType<Integer, String> {
        public TypeValue() {
            super();
        }

        public TypeValue(Integer type, String value) {
            super(type, value);
        }
    }

    public static class TypeAddress extends DataAndType<Integer, Address> {
        public TypeAddress() {
            super();
        }

        public TypeAddress(Integer type, Address value) {
            super(type, value);
        }
    }

    private ContactType mType = ContactType.UNKNOW;

    private long mId;

    private String mDisplayName;

    private long mNameSimHash;

    protected String mFirstName;
    protected String mMiddleName;
    protected String mLastName;
    protected String mNickName;
    protected String mCompany;
    protected String mJobTitle;
    protected String mBirthday;
    protected List<String> mWebPages = new ArrayList<>();
    protected String mNotes;

    public Contact() {
    }

    public Contact(ContactType type, long id) {
        mType = type;
        mId = id;
    }

    public Contact(ContactType type, long id, String displayName) {
        mType = type;
        mId = id;
        mDisplayName = displayName;
    }

    public ContactType getType() {
        return mType;
    }

    public void setType(ContactType type) {
        this.mType = type;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setNameSimHash(long nameSimHash) {
        this.mNameSimHash = nameSimHash;
    }

    public long getId() {
        return mId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public long getNameSimHash() {
        return mNameSimHash;
    }

    public void setSimHash(long nameHash) {
        this.mNameSimHash = nameHash;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.mMiddleName = middleName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public void setNickName(String nickName) {
        this.mNickName = nickName;
    }

    public void setCompany(String company) {
        this.mCompany = company;
    }

    public void setJobTitle(String jobTitle) {
        this.mJobTitle = jobTitle;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getMiddleName() {
        return mMiddleName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getNickName() {
        return mNickName;
    }

    public String getCompany() {
        return mCompany;
    }

    public String getJobTitle() {
        return mJobTitle;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(String birthday) {
        this.mBirthday = birthday;
    }

    public List<String> getWebPages() {
        return mWebPages;
    }

    public void addWebPage(String webPage) {
        this.mWebPages.add(webPage);
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        this.mNotes = notes;
    }

    public final boolean isMatched(TypeValue[] lowerCaseFilter, boolean extension, boolean isFuzzyMatch, List<MatchInfo> matchList, String countryCode, String nationalPrefix) {
        for (TypeValue splitFilter : lowerCaseFilter) {
            if (!isInternalMatched(splitFilter, extension, isFuzzyMatch, matchList, countryCode, nationalPrefix)) {
                return false;
            }
        }
        return true;
    }

    protected void addMatchInfo(List<MatchInfo> matchList, MatchInfo matchInfo) {
        if (matchList != null) {
            matchList.add(matchInfo);
        }
    }

    protected abstract boolean isInternalMatched(TypeValue filter, boolean extension, boolean isFuzzyMatch, List<MatchInfo> matchList, String countryCode, String nationalPrefix);

    public abstract boolean isFullE164NumberMatched(String e164Number, boolean extension, TypeValue matchValue);

    private boolean contain(String src, String dest) {
        return !TextUtils.isEmpty(src) && src.toLowerCase().contains(dest);
    }

    protected boolean numberMatch(String number1, String number2, boolean isFuzzy) {
//        return isFuzzy ? contain(number1, number2) : ContactMatcher.isE164NumberMatch(number1, number2);
          return false;
    }

    public static boolean numberMatch_prefix(String number1, String number2, String countryCode, String nationalPrefix) {//+4429, number2: 0
        return !TextUtils.isEmpty(number1) && !TextUtils.isEmpty(nationalPrefix)
                && compareNumber(number1, number2, countryCode, nationalPrefix);
    }

    protected static boolean compareNumber(String number, String search, String countryCode, String nationalPrefix) {
        if (number.startsWith(countryCode)) {
            number = nationalPrefix + number.substring(countryCode.length());
            return number.startsWith(search);
        }

        return false;

        /*
        boolean result = false;
        int size = number.length();
        int searchSize = search.length();
        int countryCodeSize = countryCode.length() + 1;
        int nationalPrefixSize = nationalPrefix.length();
        int index = 0;
        for(int i = 0; i < nationalPrefixSize; i++) {
            if(index < searchSize && search.charAt(i) == nationalPrefix.charAt(i)) {
                index++;
                continue;
            }
        }

        if(index == nationalPrefixSize) {//0
            if(index == searchSize) {
                result = true;
            }else {
                int i = index, j = countryCodeSize, cursor = 0;
                for (; i < searchSize && j < size; i++, j++) {
                    if (number.charAt(j) == search.charAt(i)) {
                        cursor++;
                        continue;
                    }
                    break;
                }
            }
         */
    }

    protected boolean numberMatchNoCheckNull(String number1, String number2, boolean isFuzzy) {
//        return isFuzzy ? number1.toLowerCase().contains(number2) : ContactMatcher.isE164NumberMatch(number1, number2);
        return true;
    }

    protected boolean commonMatchNoCheckNull(String src, String dest, boolean isFuzzy) {
        return isFuzzy ? src.toLowerCase().contains(dest) : (!TextUtils.isEmpty(src) && src.toLowerCase().equals(dest));
    }

    protected boolean commonMatch(String src, String dest, boolean isFuzzy) {
        return isFuzzy ? contain(src, dest) : (!TextUtils.isEmpty(src) && src.toLowerCase().equals(dest));
    }

    protected boolean commonMatch(List<TypeValue> src, String dest, boolean isFuzzy) {
        for (Contact.TypeValue typeValue : src) {
            if (commonMatch(typeValue.getValue(), dest, isFuzzy)) {
                return true;
            }
        }
        return false;
    }

    protected boolean numberMatch(List<TypeValue> src, String dest, boolean isFuzzy) {
        for (Contact.TypeValue typeValue : src) {
            if (numberMatch(typeValue.getValue(), dest, isFuzzy)) {
                return true;
            }
        }
        return false;
    }

    public abstract Bitmap getImage();

    public abstract Intent getDetailsActivityIntent(Context context);

    public abstract Uri getContactUri();

    public abstract boolean hasPhoneNumber();

    public abstract boolean hasEmail();

    public void clone(Contact contact) {
        this.mId = contact.getId();
        this.mType = contact.getType();
        this.mDisplayName = contact.getDisplayName();
        this.mNameSimHash = contact.getNameSimHash();
        this.mFirstName = contact.getFirstName();
        this.mMiddleName = contact.getMiddleName();
        this.mLastName = contact.getLastName();
        this.mNickName = contact.getNickName();
        this.mCompany = contact.getCompany();
        this.mJobTitle = contact.getJobTitle();
        this.mBirthday = contact.getBirthday();
        List<String> webPages = contact.getWebPages();
        for (String webPage : webPages) {
            this.addWebPage(webPage);
        }
        this.mNotes = contact.getNotes();
    }

    @Override
    public int compareTo(Contact contact) {
//        return ContactsSorting.sorting(getDisplayName(), contact.getDisplayName(), true);
        return 1;
    }

    public static boolean isEmpty(String value) {
        return (value == null) || value.trim().length() == 0;
    }
}
