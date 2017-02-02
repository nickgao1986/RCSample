package com.example.nickgao.i18n;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.example.nickgao.database.GeneralSettings;
import com.example.nickgao.datastore.LanguageDataStore;
import com.example.nickgao.service.RestRequestListener;
import com.example.nickgao.service.ServiceFactory;
import com.example.nickgao.service.extensioninfo.ExtensionInfoService;
import com.example.nickgao.service.i18n.I18nResources;
import com.example.nickgao.service.i18n.Language;
import com.example.nickgao.service.model.extensioninfo.ExtensionLanguage;
import com.example.nickgao.utils.NetworkUtils;
import com.example.nickgao.utils.RCMConstants;

/**
 * Created by nick.gao on 2/1/17.
 */

public class DeviceLanguageDetectFlow {

    private static final String TAG = "[RC]DeviceLanguageDetectFlow";

    private static final int SHOW_REMINDER_PROMPT = 1;
    private static final int SHOW_POLLING_PROMPT = 2;
    private static final int SHOW_ERROR_PROMPT = 3;
    private static final int CHANGE_ACTIVITY_LANGUAGE = 4;

    private Context mContext;

    private Language mAppLanguage;
    private Language mServerLanguage;
    private Language mDeviceLanguage;

    private static final int MINIMUM_INTERVAL = 10 * 1000;
    private static final int REMAINDER_ID = -2;

    private static long sLastShowTime = 0;

    public DeviceLanguageDetectFlow(Context context) {
        mContext = context;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_REMINDER_PROMPT:
                   // showRemainder(mContext, mDeviceLanguage);
                    break;

                case SHOW_POLLING_PROMPT:
                    showPollingPrompt(mServerLanguage);
                    break;

                case SHOW_ERROR_PROMPT:
                 //   showFailPrompt(mDeviceLanguage);
                    break;

                case CHANGE_ACTIVITY_LANGUAGE:
                 //   changeActivityLanguage(mDeviceLanguage, false);
                    break;
            }
        }
    };

    private void showPollingPrompt(final Language serverLanguage) {
//        AlertDialog.Builder langConflictBuilder = I18nHelper.createPollingLanguageChangedBuilder(mContext, serverLanguage)
//                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                        changeActivityLanguage(serverLanguage, true);
//                    }
//                }).setCancelable(false);
//
//        final I18nHelper.LanguageCheckDialog checkDialog = new I18nHelper.LanguageCheckDialog();
//        checkDialog.langConflictDialog = langConflictBuilder.create();
//        checkDialog.langConflictDialog.show();
//        LanguageConflictResolveLifeCycle.getInstance().setCurrentDialog(checkDialog);
    }

    private void changeActivityLanguage(final Language language, boolean showLoading) {
        I18nResources.getResources().changeActivityLanguage(mContext, language, true);
    }



    public boolean startFlow() {

        boolean result = false;

        SharedPreferences spLanguages = mContext.getSharedPreferences(RCMConstants.SP_LANGUAGE_EDITOR, 0);

        boolean isJustLogin = spLanguages.getBoolean(RCMConstants.SP_LANGUAGE_JUST_LOGIN, false);
        if (isJustLogin) {
            spLanguages.edit().putBoolean(RCMConstants.SP_LANGUAGE_JUST_LOGIN, false);
            spLanguages.edit().commit();
        }

        if (!NetworkUtils.isRCAvaliable(mContext)) {
            I18nResources.dLog(TAG, "startFlow: isRCAvaliable = false");
            return result;
        }

        mDeviceLanguage = I18nResources.getResources().getDeviceMapLanguage();
        if (mDeviceLanguage == null) {
            I18nResources.dLog(TAG, "startFlow: not supported language, return");
            return result;
        }

        mAppLanguage = I18nResources.getResources().getCurrentLanguage();

        I18nResources.dLog(TAG, "startFlow: mAppLocaleCode = " + mAppLanguage.getIsoLocale() + "; curDeviceLocaleCode =" + mDeviceLanguage.getIsoLocale());

        if (mDeviceLanguage.equals(mAppLanguage)) {
            I18nResources.dLog(TAG, "startFlow: appLocaleCode = currentLocaleCode, return");
            return result;
        }
        if (GeneralSettings.getSettings().isLanguageOrReminderShowedBefore(mDeviceLanguage.getIsoLocale())) {
            I18nResources.dLog(TAG, "startFlow: language or reminder already showed before, return");
            return result;
        }

        if (isJustLogin) {
            checkAfterFetchServerLanguage();
        } else {
            ExtensionInfoService service = (ExtensionInfoService) ServiceFactory.getInstance().getService(ExtensionInfoService.class.getName());
            service.setListener(new RestRequestListener() {

                @Override
                public void onRequestSuccess() {
                    checkAfterFetchServerLanguage();
                }

                @Override
                public void onRequestFailure(int errorCode) {
                    checkAfterFetchServerLanguage();
                }
            });
            service.updateData();
        }
        result = true;

        return result;
    }

    private void checkAfterFetchServerLanguage() {
        if(((Activity) mContext).isFinishing()) {
            I18nResources.dLog(TAG, "checkAfterFetchServerLanguage: the activity: " + ((Activity) mContext).getLocalClassName() + " is finishing");
            return;
        }
        I18nResources.dLog(TAG, "checkAfterFetchServerLanguage");
        ExtensionLanguage serverUserLanguage = LanguageDataStore.getExtensionUserServerLanguage();
        if (serverUserLanguage == null) {
            I18nResources.dLog(TAG, "checkAfterFetchServerLanguage: no user server language");
            return;
        }

        mServerLanguage = new Language(serverUserLanguage.getLocaleCode(), false);
        if (mServerLanguage.equals(mAppLanguage)) {
            mHandler.sendEmptyMessage(SHOW_REMINDER_PROMPT);
        } else {
            if (mServerLanguage.equals(mDeviceLanguage)) {
                GeneralSettings.getSettings().setLanguageOrReminderShowedBefore(mDeviceLanguage.getIsoLocale());
            }

            mHandler.sendEmptyMessage(SHOW_POLLING_PROMPT);
        }
    }


    private void syncUserLanguageToServer(final Language language) {
        I18nResources.dLog(TAG, "syncUserLanguageToServer");
        final int languageId = LanguageDataStore.getLanguageId(language.getFormatLocale());
        if (languageId <= 0) {
            I18nResources.dLog(TAG, "syncUserLanguageToServer: no such language, id: " + languageId);
            return;
        }

//        final I18nHelper.LanguageCheckDialog checkDialog = new I18nHelper.LanguageCheckDialog();
//        checkDialog.langSetting = new DeviceLanguageSetting(mContext, languageId);
//        checkDialog.langSetting.syncUserLanguage(languageId);
//        LanguageConflictResolveLifeCycle.getInstance().setCurrentDialog(checkDialog);
    }


    public class DeviceLanguageSetting extends LanguageSetting {
        public DeviceLanguageSetting(Context act, int langId) {
            this.mContext = act;
            this.mLanguageId = langId;

            if(mLanguageId == REMAINDER_ID) {
                sLastShowTime = System.currentTimeMillis();
            }
        }

        public void dismissDialog() {
            try {
                if (mSettingDialog != null && mSettingDialog.isShowing()) {
                    mSettingDialog.dismiss();
                }
            } catch (Exception err) {
                I18nResources.dLog(TAG, "err:" + err.toString());
            }

            mSettingDialog = null;

        }

        public boolean isStillSettingLanguage() {
            boolean needShow = false;
            if(mLanguageId == REMAINDER_ID) {
                //Fix AB-15461 [Galaxy Nexus7] HW back is still disabled after tapping [No] button in device langauge prompt
                long currentTime = System.currentTimeMillis();
                if(currentTime - sLastShowTime < MINIMUM_INTERVAL) {
                    I18nResources.dLog(TAG, "isStillSettingLanguage, true; currentTime:" + currentTime + " sLastShowTime:" + sLastShowTime);
                    needShow = true;
                }
            } else {
                needShow = (mSettingDialog == null) ? false : true;
            }
            return needShow;
        }

        public void syncUserLanguage(int appLangId) {
            I18nResources.dLog(TAG, "syncUserLanguage, time=" + System.currentTimeMillis());
            mSettingDialog = new LanguageSettingDialog(mContext);
            mSettingDialog.show();
           // I18nHelper.syncUserLanguageToServer(appLangId, this);
        }

        @Override
        public void onRequestSuccess() {
            I18nResources.dLog(TAG, "DeviceLanguageSetting: onRequestSuccess");
            LanguageDataStore.setUserServerLanguageInLocalDB(mLanguageId);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                }
            });
            mHandler.sendEmptyMessage(CHANGE_ACTIVITY_LANGUAGE);
        }

        @Override
        public void onRequestFailure(int errorCode) {
            I18nResources.dLog(TAG, "DeviceLanguageSetting: onRequestFailure");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                }
            });
            mHandler.sendEmptyMessage(SHOW_ERROR_PROMPT);
        }
    }

//    private void showRemainder(final Context context, final Language language) {
//        AlertDialog.Builder langConflictBuilder = I18nHelper.createNewLanguageSupportedBuilder(mContext, language.getDisplayName(context))
//                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                        syncUserLanguageToServer(language);
//                    }
//                })
//                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                        GeneralSettings.getSettings().setLanguageOrReminderShowedBefore(language.getIsoLocale());
//                    }
//                }).setCancelable(false);
//
//        final I18nHelper.LanguageCheckDialog checkDialog = new I18nHelper.LanguageCheckDialog();
//        checkDialog.langConflictDialog = langConflictBuilder.create();
//        checkDialog.langConflictDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                checkDialog.langSetting = null;
//            }
//        });
//        checkDialog.langConflictDialog.show();
//        checkDialog.langSetting = new DeviceLanguageSetting(mContext, REMAINDER_ID);
//        LanguageConflictResolveLifeCycle.getInstance().setCurrentDialog(checkDialog);
//    }


}
