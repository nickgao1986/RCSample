package com.example.nickgao.database;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;

import org.apache.commons.lang.StringUtils;

public class GeneralSettings extends AbstractDbSettings {

	private static final long MAILBOX_ID_NOT_SET = -2;
	private static long sCurrentMailboxId = MAILBOX_ID_NOT_SET;
    private static final String KEY_CURRENT_USER = "current_user";
    private final Context mContext;
    private static final String KEY_CURRENT_MAILBOX_ID = "current_mailbox_id";
    private static final String KEY_REST_REFRESH_TOKEN = "user_rest_refresh_token";
    private static final String KEY_REST_REFRESH_TOKEN_LOST = "user_rest_refresh_token_lost";

    
    public static GeneralSettings getSettings() {
        return new GeneralSettings(RingCentralApp.getContextRC(), "no_brand");
    }
    
    
    public String getRefreshToken() {
        MktLog.d(TAG, "get refresh token " + getString(KEY_REST_REFRESH_TOKEN, null));
        return getString(KEY_REST_REFRESH_TOKEN, null);
    }

    /**
     * Stores REST authorization refresh token
     *
     * @param refreshToken defines <b>refresh token</b> (<code>null</code> erases existing token)
     */
    public void setRefreshToken(String refreshToken) {
        MktLog.d(TAG, "set refresh token " + refreshToken);
        setString(KEY_REST_REFRESH_TOKEN, refreshToken);
        if (StringUtils.isNotEmpty(refreshToken)){
            GeneralSettings.getSettings().setRefreshTokenLost(false);
        }
    }

    public void clearRefreshToken() {
        MktLog.d(TAG, "clear refresh token");
        setString(KEY_REST_REFRESH_TOKEN, null);
        GeneralSettings.getSettings().setRefreshTokenLost(true);
    }
    
    public void setRefreshTokenLost(boolean value) {
        setBoolean(KEY_REST_REFRESH_TOKEN_LOST, value);
    }
    

    private GeneralSettings(Context context, String brandId) {
        if (context == null) {
            throw new IllegalArgumentException("context");
        }
        mContext = context.getApplicationContext();
    }

    public boolean isRefreshTokenLost() {
        return getBoolean(KEY_REST_REFRESH_TOKEN_LOST, false);
    }
    
    
	public long getCurrentUserId() {
		if (sCurrentMailboxId == MAILBOX_ID_NOT_SET) {
			sCurrentMailboxId = getLong(KEY_CURRENT_USER, -1);
		}

		return sCurrentMailboxId;
	}

    public long getCurrentMailboxId() {
        if (sCurrentMailboxId != MAILBOX_ID_NOT_SET){
            return sCurrentMailboxId;
        }

        return getLong(KEY_CURRENT_MAILBOX_ID, 0);
    }
    
	public void setCurrentUser(long mailboxId) {
		MktLog.d(TAG, "setCurrentUser: " + mailboxId);
		boolean idChanged = mailboxId != getCurrentUserId();

		sCurrentMailboxId = mailboxId;
		setLong(KEY_CURRENT_USER, mailboxId);

	}
	
	public void setCurrentMailboxId(long mailboxId) {
	        sCurrentMailboxId = mailboxId;
	        setLong(KEY_CURRENT_MAILBOX_ID, mailboxId);
	}

    @Override
    protected Context getContext() {
        return mContext;
    }

    @Override
    protected Uri getUri() {
        return UriHelper.getSettingsUri(RCMSettingsProvider.GENERAL_SETTINGS);
    }

    public void setLanguageOrReminderShowedBefore(String localeCode) {
        if (!isLanguageOrReminderShowedBefore(localeCode)) {
            setString(localeCode, "1");
        }
    }

    public boolean isLanguageOrReminderShowedBefore(String localeCode) {
        String result = getString(localeCode, "0");
        return TextUtils.equals(result, "1");
    }

}
