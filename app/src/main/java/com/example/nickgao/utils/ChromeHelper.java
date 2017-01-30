package com.example.nickgao.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.example.nickgao.R;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;

import java.util.List;

/**
 * Created by nick.gao on 1/30/17.
 */

public class ChromeHelper {

    private static final String INTENT_MAP_SCHEME = "geo";
    private static final String LOG = "[RC]ChromeHelper";
    private static final String PREFIX_OF_GOOGLE_STORE_URL = "https://play.google.com/store/apps/details?id=";
    private static final String PARAM_NAME_LANGUAGE = "&hl=";

    public static void startActiviyByChromeIfExists(Context context, Intent intent) {

        try {
            if (LogSettings.MARKET) {
                MktLog.d(LOG, "Intent Scheme: " + intent.getScheme());
            }
        } catch (Exception e) {
        }

        if (context != null && intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            List<ResolveInfo> availableSoft = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo info : availableSoft) {
                if ("com.android.chrome".equals(info.activityInfo.packageName)) {
                    intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
                    context.startActivity(intent);
                    return;
                }
            }
            if (availableSoft.size() == 0) {
                try {

                    int messageId;
                    if (INTENT_MAP_SCHEME.equalsIgnoreCase(intent.getScheme())) {
                        messageId = R.string.setting_no_map_app_installed;
                    } else {
                        messageId = R.string.setting_no_browser_installed;
                    }

                    RcAlertDialog.getBuilder(context).setTitle(R.string.app_name)
                            .setMessage(messageId)
                            .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    return;
                                }
                            }).create().show();
                } catch (Exception e) {
                    Log.e(LOG, e.getMessage());
                }

            } else {
                context.startActivity(intent);
            }
        }
    }

//    public static void startGoogleStoreOrBrowser(Context context, String packageName) {
//        Intent intent = null;
//        String MARKET_PACKAGE_NAME = "com.android.vending";
//        if (RCMPackageUtils.isAppInstalled(context, MARKET_PACKAGE_NAME)) {
//            try {
//                intent = context.getPackageManager().getLaunchIntentForPackage(MARKET_PACKAGE_NAME);
//            } catch (Exception e) {
//                intent = null;
//                if (LogSettings.MARKET) {
//                    MktLog.e(LOG, "Cannot get intent via getLaunchIntentForPackage()");
//                }
//            }
//        }
//
//        if (intent == null) {
//            intent = new Intent();
//        }
//
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        String currentLanguage = I18nResources.getResources().getCurrentLanguage().getLocale().getLanguage();
//        StringBuilder sbUri = new StringBuilder()
//                .append(context.getResources().getString(R.string.settings_about_market_link)).append(packageName)
//                .append(PARAM_NAME_LANGUAGE).append(currentLanguage);
//        intent.setData(Uri.parse(sbUri.toString()));
//
//        //If google play store doesn't install, check whether existing other market app.
//        List<ResolveInfo> availableSoft = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//
//        if (availableSoft.size() > 0) {
//            try {
//                context.startActivity(intent);
//            } catch (Exception e) {
//                if (LogSettings.MARKET) {
//                    MktLog.e(LOG, "search market app failed: " + e);
//                }
//            }
//        } else {
//            StringBuilder uriString = new StringBuilder().append(PREFIX_OF_GOOGLE_STORE_URL).append(packageName)
//                    .append(PARAM_NAME_LANGUAGE).append(currentLanguage);
//            Uri uri = Uri.parse(uriString.toString());
//            ChromeHelper.startActiviyByChromeIfExists(RingCentralApp.getContextRC(), new Intent(Intent.ACTION_VIEW, uri));
//        }
//    }
}
