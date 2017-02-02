package com.example.nickgao.service.i18n;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by nick.gao on 2/1/17.
 */

public class LanguageConflictResolveLifeCycle {

    private String TAG = "[RC]LanguageConflictResolveLifeCycle";
    private static LanguageConflictResolveLifeCycle gInstance = null;
    private Receiver mReceiver = null;
//    private I18nHelper.LanguageCheckDialog mCheckDialog = null;
    private boolean mUserCancelFlag = false;
    private static boolean COMPONENT_ENABLE = true;

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            startCheckLanguage(context, true);
        }
    }

    private void startCheckLanguage(final Context context, boolean isFromPolling) {
        //user cancel it
        I18nResources.dLog(TAG, "startCheckLanguage, enable=" + COMPONENT_ENABLE + "; user cancel Flag=" + isFromPolling);

        if (!COMPONENT_ENABLE || mUserCancelFlag) {
            return;
        }

//        if (mCheckDialog != null && mCheckDialog.isStillNeedShowing()) {
//            I18nResources.dLog(TAG, "startCheckLanguage, but previous dialog is still showing.");
//            return;
//        }

        I18nHelper.checkLanguage(context, isFromPolling);
    }

}
