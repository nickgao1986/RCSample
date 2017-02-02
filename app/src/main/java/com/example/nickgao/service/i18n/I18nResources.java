package com.example.nickgao.service.i18n;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.example.nickgao.BuildConfig;
import com.example.nickgao.R;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.GeneralSettings;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by nick.gao on 2/1/17.
 */

public class I18nResources {

    private static String LANGUAGE_ADAPTION_TAG = "[RC]LanguageAdaption";
    private static String TAG = "[RC]I18nResources";
    /**
     * file for storing lang.
     */
    //private static String SHARED_PREFERENCE = "LANGUAGE_SETTING";

    /**
     * LangCode,CountryCode
     */
    //private static String LANGUAGE_CODE = "_languageCode";
    //private static String COUNTRY_CODE = "_countryCode";

    /**
     * Lang types
     */
    private static String LANG_TYPE_APPLICATION = "app_language";
    private static String LANG_TYPE_USER_LOCAL = "user_local_language";

    /**
     * User lang state
     */
    public static final int LANGUAGE_STATE_SERVER_EMPTY = -2;
    public static final int LANGUAGE_STATE_APP_EMPTY = -1;
    public static final int LANGUAGE_STATE_NOT_CHANGED = 0;
    public static final int LANGUAGE_STATE_CHANGED = 1;

    /**
     * singleton I18nResources
     */
    private static I18nResources gInstance = null;
    private Language mLanguage = null;

    private ArrayList<Language> supportLanguages;
    public static void dLog(String tag, String msg) {
        if (LogSettings.ENGINEERING) {
            MktLog.d(LANGUAGE_ADAPTION_TAG, tag + ": " + msg);
        }
    }

    private void initSupportLanguages(){
        supportLanguages = new ArrayList<Language>();
       // supportLanguages.addAll(Arrays.asList(BUILD.LANGUAGES));
        supportLanguages.addAll(Arrays.asList(BuildConfig.LANGUAGES));
    }

    private I18nResources() {
        initSupportLanguages();
    }

    public ArrayList<Language> getSupportLanguages(){
        return this.supportLanguages;
    }


    public static synchronized I18nResources getResources() {
        if (gInstance == null) {
            gInstance = new I18nResources();
            dLog(TAG, "instance obj=" + gInstance.toString());
        }
        return gInstance;
    }

    public Language getMostSimilarLanguage(String isoCode) {
        Locale srcLocale = Language.generalLocal(isoCode);
        Language result = getLanguageFromBrand(srcLocale);
        if (result == null) {
            dLog(TAG, "getMostSimilarLanguage() brand languages do not support device language=" + isoCode);
            for (Language tmp : supportLanguages) {
                if (Language.isValidate(tmp)) {
                    if (tmp.getLocale().getLanguage().equals(srcLocale.getLanguage())) {
                        dLog(TAG, "getMostSimilarLanguage() brand languages can support language=" + isoCode);
                        result = tmp;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public Language getLanguageFromBrand(Locale locale) {
        Language result = null;
        for (Language lang : supportLanguages) {
            if (Language.isValidate(lang)) {
                if (lang.getLocale().equals(locale)) {
                    result = lang;
                    break;
                }
            }
        }
        return result;
    }

    public Language getCurrentLanguage() {
        return mLanguage;
    }

    private void updateConfiguration(Context context, Language lang) {
        if (lang == null) {
            dLog(TAG, "updateConfiguration: error, lang=null.");
            return;
        }
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = lang.getLocale();
        resources.updateConfiguration(config, dm);
    }


    public void changeActivityLanguage(Context context, Language lang, boolean restart) {
        dLog(TAG, "changeActivityLanguage()");
        if (!Language.isValidate(lang) || lang.equals(mLanguage)) {
            return;
        }

        mLanguage = lang;
        String message = context.getString(R.string.lang_setting_message);
        updateConfiguration(context, mLanguage);

//        setApplicationLanguage(context, mLanguage);
//        setUserLocalLanguage(context, mLanguage);
//
//        GeneralSettings.getSettings().setLanguageOrReminderShowedBefore(lang.getIsoLocale());
        if (restart) {
            RingCentralApp.restartTopActivity(context, message);
        }
    }


    public boolean setApplicationLanguage(Context context, Language lang) {
        dLog(TAG, "setApplicationLanguage()");
        return setLanguage(context, lang, LANG_TYPE_APPLICATION, false);
    }

    public Language getApplicationLanguage(Context context) {
        dLog(TAG, "getApplicationLanguage()");
        return getLanguage(context, LANG_TYPE_APPLICATION, false);
    }

    public boolean setUserLocalLanguage(Context context, Language lang) {
        dLog(TAG, "setUserLocaleLanguage()");
        return setLanguage(context, lang, LANG_TYPE_USER_LOCAL, true);
    }

    public Language getUserLocalLanguage(Context context) {
        dLog(TAG, "getUserLanguage()");
        return getLanguage(context, LANG_TYPE_USER_LOCAL, true);
    }

    private boolean setLanguage(Context context, Language lang, String langType, boolean isUserTag) {
        boolean result = false;
        do {
            dLog(TAG, "setLanguage(), langType=" + langType);
            if (context == null) {
                dLog(TAG, "setLanguage(), context = null");
                break;
            }

            if (langType == null) {
                dLog(TAG, "setLanguage(), tag = null");
                break;
            }

            if (!Language.isValidate(lang)) {
                dLog(TAG, "setLanguage(), language = null");
                break;
            }

            if (isUserTag) {
                CurrentUserSettings.getSettings().setString(langType, lang.getLocale().toString());
            } else {
                GeneralSettings.getSettings().setString(langType, lang.getLocale().toString());
            }

            dLog(TAG, "setLanguage(), language=" + lang.getLocale().toString());

            result = true;
        } while (false);

        return result;
    }

    private Language getLanguage(Context context, String langType, boolean isUserTag) {
        Language lang = null;
        do {
            dLog(TAG, "getLanguage(), langType=" + langType);
            if (context == null) {
                dLog(TAG, "getLanguage(), context = null");
                break;
            }

            if (langType == null) {
                dLog(TAG, "getLanguage(), tag = null");
                return null;
            }

            String isoCode;
            if (isUserTag) {
                isoCode = CurrentUserSettings.getSettings().getString(langType, "");
            } else {
                isoCode = GeneralSettings.getSettings().getString(langType, "");
            }

            if (isoCode.isEmpty()) {
                break;
            }

            lang = new Language(isoCode, false);

            dLog(TAG, "getLanguage(), language=" + lang.getLocale().toString());

        } while (false);

        return lang;
    }


    public Language getDeviceMapLanguage() {
        Language result = null;
        Locale locale = Locale.getDefault();
        if (locale != null) {
            StringBuilder isoBuilder = new StringBuilder();
            isoBuilder.append(locale.getLanguage()).append("_").append(locale.getCountry());
            result = getMostSimilarLanguage(isoBuilder.toString());
        }
        return result;
    }


}
