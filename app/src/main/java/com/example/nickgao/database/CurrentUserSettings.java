package com.example.nickgao.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;

import com.example.nickgao.BuildConfig;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcfragments.ContactsTabFragment;
import com.example.nickgao.rcproject.RingCentralApp;

/**
 * Created by nick.gao on 1/28/17.
 */

public class CurrentUserSettings extends AbstractDbSettings {


    private static final String KEY_CURRENT_MAILBOX_ID = "current_mailbox_id";
    private static final long MAILBOX_ID_NOT_SET = -2;
    private static final String KEY_CONTACTS_CURRENT_TAB = "ContactsCurrentTab";

    private static final String SAVING_STATE_SHARED_PREF_NAME = BuildConfig.APPLICATION_ID + ".contacts.list";
    private static final String SAVING_STATE_SHARED_PREF_PARAMETER = "isCompany";
    private static volatile long sCurrentMailboxId = MAILBOX_ID_NOT_SET;
    private static final String KEY_PERSONAL_CONTACTS_SYNC_TOKEN = "CloudPersonalContactsSyncToken";


    private Context mContext;
    private long mCurrentUserId;

    public static CurrentUserSettings getSettings() {
        return new CurrentUserSettings(RingCentralApp.getContextRC());
    }

    public static CurrentUserSettings getSettings(Context context) {
        return new CurrentUserSettings(context);
    }

    private CurrentUserSettings(Context context) {
        //TODO check userId format

        mContext = context;

        mCurrentUserId = GeneralSettings.getSettings().getCurrentUserId();
        if (mCurrentUserId <= 0) {
            MktLog.w(TAG, "current user is null");
        }
    }

    @Override
    protected Context getContext() {
        return mContext;
    }

    @Override
    protected Uri getUri() {
        return UriHelper.getSettingsUri(RCMSettingsProvider.USER_SETTINGS, mCurrentUserId);
    }

    public void setCurrentMailboxId(long mailboxId) {
        sCurrentMailboxId = mailboxId;
        setLong(KEY_CURRENT_MAILBOX_ID, mailboxId);
    }

    public long getCurrentMailboxId() {
        if (sCurrentMailboxId != MAILBOX_ID_NOT_SET){
            return sCurrentMailboxId;
        }

        return getLong(KEY_CURRENT_MAILBOX_ID, 0);
    }

    protected void setLong(final String key, final long value) {
        setString(key, Long.toString(value));
    }

    protected long getLong(final String key, final long def) {
        final String result = getString(key, null);
        return TextUtils.isEmpty(result) ? def : Long.parseLong(result);
    }

    public void setContactsCurrentTab(ContactsTabFragment.Tabs tab){
        setString(KEY_CONTACTS_CURRENT_TAB, tab.name());
    }

    public ContactsTabFragment.Tabs getContactsCurrentTab() {
        String currentTab = getString(KEY_CONTACTS_CURRENT_TAB, "");
        if (TextUtils.isEmpty(currentTab)){
            SharedPreferences preferences = getContext().getSharedPreferences(SAVING_STATE_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            if (preferences.contains(SAVING_STATE_SHARED_PREF_PARAMETER)){
                if (preferences.getBoolean(SAVING_STATE_SHARED_PREF_PARAMETER, true)){
                    setContactsCurrentTab(ContactsTabFragment.Tabs.COMPANY);
                } else {
                    setContactsCurrentTab(ContactsTabFragment.Tabs.DEVICE);
                }
            } else {
                setContactsCurrentTab(ContactsTabFragment.Tabs.ALL);
            }
            return getContactsCurrentTab();
        }

        return ContactsTabFragment.Tabs.valueOf(currentTab);
    }

    public void setPersonalContactsSyncToken(String token) {
        setString(KEY_PERSONAL_CONTACTS_SYNC_TOKEN, token);
    }

    public String getPersonalContactsSyncToken(){
        return getString(KEY_PERSONAL_CONTACTS_SYNC_TOKEN, "");
    }

}
