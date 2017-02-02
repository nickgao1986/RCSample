package com.example.nickgao.service.i18n;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import com.example.nickgao.R;
import com.example.nickgao.datastore.LanguageDataStore;
import com.example.nickgao.i18n.LanguageSetting;
import com.example.nickgao.service.model.extensioninfo.ExtensionLanguage;
import com.example.nickgao.service.model.i18n.LanguageRecord;
import com.example.nickgao.utils.RCMConstants;
import com.example.nickgao.utils.RcAlertDialog;

import java.util.ArrayList;

/**
 * Created by nick.gao on 2/1/17.
 */

public class I18nHelper {

    private static final String TAG = "[RC]I18nHelper";


    public static AlertDialog.Builder createNewLanguageSupportedBuilder(final Context act, final String langName) {
        return new RcAlertDialog.Builder(act, RCMConstants.ALL_DIALOGS_THEME_ID)
                .setIcon(R.drawable.symbol_exclamation)
                .setTitle(R.string.lang_conflict_resolving_title)
                .setMessage(String.format(act.getString(R.string.lang_support_new_content), langName));
    }


    public static AlertDialog.Builder createOnlyOneLanguageSupportedBuilder(final Context act, final Language lang) {
        return new RcAlertDialog.Builder(act, RCMConstants.ALL_DIALOGS_THEME_ID)
                .setIcon(R.drawable.symbol_exclamation)
                .setTitle(R.string.lang_conflict_resolving_title)
                .setMessage(String.format(act.getString(R.string.lang_only_one_language_support_message), lang.getDisplayName(act), lang.getDisplayName(act)));
    }

    public static void checkLanguage(final Context act, boolean isFromPolling) {

        do {
            ExtensionLanguage extensionSerLang = LanguageDataStore.getExtensionUserServerLanguage();

            if (extensionSerLang == null) {
                I18nResources.dLog(TAG, "Check language process: User language in SW is empty.");
                break;
            }


        } while (false);


    }


    private static boolean onlyOneLanguageSupportConflictResolvingFlow(final Context act, ExtensionLanguage extensionSerLang) {

//        if (CurrentUserSettings.getSettings().getKeyIsForceChangeLanguageShowBefore()) {
//            return false;
//        }

        final Language serLang = I18nHelper.findLanguageById(extensionSerLang.getId());
        final Language appLang = I18nResources.getResources().getCurrentLanguage();

        final int appLangId = appLang.getLanguageId();
        if (Language.INVALID_ID == appLangId && serLang != null) {
            final Language mLanguage = I18nResources.getResources().getMostSimilarLanguage(appLang.getIsoLocale());

        }
        return false;
    }

    public static Language findLanguageById(int langId) {
        LanguageRecord lr = null;
        ArrayList<LanguageRecord> languages = LanguageDataStore.getAllItemsFromDb();
        for (LanguageRecord lang : languages) {
            if (lang != null && langId == lang.getId()) {
                lr = lang;
                break;
            }
        }
        return (lr != null) ? new Language(lr.getLocaleCode(), false) : null;
    }

    public static class LanguageCheckDialog {
        protected LanguageSetting langSetting;
        protected Dialog langConflictDialog;

        public boolean isStillNeedShowing() {
            return langSetting != null && langSetting.isStillSettingLanguage();

        }

        public void safeDismissDialog() {
            try {
                if (langSetting != null) {
                    langSetting.dismissDialog();
                }

                if (langConflictDialog != null && langConflictDialog.isShowing()) {
                    langConflictDialog.dismiss();
                }
            } catch (Exception err) {
                langConflictDialog = null;
            }
        }
    }


}

