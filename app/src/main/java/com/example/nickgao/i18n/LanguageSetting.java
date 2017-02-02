package com.example.nickgao.i18n;

import android.content.Context;

import com.example.nickgao.service.RestRequestListener;

/**
 * Created by nick.gao on 2/1/17.
 */

public abstract class LanguageSetting implements RestRequestListener {
    static final String TAG = "[RC]LanguageSetting";
    protected LanguageSettingDialog mSettingDialog = null;
    protected Context mContext = null;
    protected int mLanguageId;

    public LanguageSetting() {
    }

    public abstract void dismissDialog();

    public abstract boolean isStillSettingLanguage();

    public abstract void syncUserLanguage(int appLangId);
}
