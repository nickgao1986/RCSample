package com.example.nickgao.database;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;

/**
 * Created by nick.gao on 1/28/17.
 */

public class CurrentUserSettings extends AbstractDbSettings {


    private static final String KEY_CURRENT_MAILBOX_ID = "current_mailbox_id";
    private static final long MAILBOX_ID_NOT_SET = -2;


    private static volatile long sCurrentMailboxId = MAILBOX_ID_NOT_SET;


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
}
