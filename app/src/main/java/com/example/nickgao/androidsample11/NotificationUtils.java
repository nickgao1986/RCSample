package com.example.nickgao.androidsample11;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.example.nickgao.rcproject.RingCentralApp;

/**
 * Created by nick.gao on 2/2/17.
 */

public class NotificationUtils {

    private static final String TAG = "NotificationUtils";
    private static NotificationManager mNotificationMgr;
    private static ActivityManager mActivityManager;
    public static final int NOTIFICATION_GROUP_SUMMARY_ID = 88;

    public static NotificationManager getNotificationManager(Context context) {
        if (mNotificationMgr == null) {
            mNotificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mNotificationMgr;
    }

    private static ActivityManager getActivityManager() {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) RingCentralApp.getContextRC().getSystemService(Context.ACTIVITY_SERVICE);
        }

        return mActivityManager;
    }


    @Deprecated
    public static void cancelAll(Context context) {

        getNotificationManager(context).cancelAll();
    }

    public static void notify(Context context, int id, Notification notification) {
        getNotificationManager(context).notify(id, notification);
    }

}
