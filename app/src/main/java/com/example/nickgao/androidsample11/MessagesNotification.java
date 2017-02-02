package com.example.nickgao.androidsample11;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.nickgao.R;
import com.example.nickgao.logging.MktLog;
import com.example.nickgao.rcproject.RingCentralApp;

import java.util.List;

/**
 * Created by nick.gao on 2/2/17.
 */

public class MessagesNotification extends Activity {

    private static final String TAG = "[RC]MessagesNotification";
    private static ActivityManager mActivityManager;
    private static NotificationManager mNotificationMgr;


    private boolean isAppInBackground() {
        try {
            String packageName = getApplicationContext().getPackageName();
            List<ActivityManager.RunningTaskInfo> appProcesses = getActivityManager().getRunningTasks(1);

            if (appProcesses == null || appProcesses.size() == 0) {
                return false;
            }


            if (!appProcesses.get(0).topActivity.getPackageName().equals(packageName)) {
                return false;
            }

            if (appProcesses.get(0).numActivities <= 1) {

                return true;
            }
        } catch (Throwable e) {
            MktLog.e(TAG, "isAppInBackground() : " + e.toString());
        }

        return false;
    }


    private static ActivityManager getActivityManager() {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) RingCentralApp.getContextRC().getSystemService(Context.ACTIVITY_SERVICE);
        }

        return mActivityManager;
    }


    private static boolean sendTextNotification(Context context, boolean isAlreadySounded) {
        final String notifLine1 = context.getResources().getString(R.string.app_name);
        final String tickerText = context.getResources().getString(R.string.messages_notification_ticker);

        final String notifLine2 = context.getString(R.string.messages_notification_line2_mult_text, 3);

        Intent notificationIntent = new Intent(context, MessagesNotification.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 123,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(notifLine1)
                .setContentText(notifLine2)
                .setSmallIcon(R.drawable.ic_stat_notify_message)
                .setContentIntent(contentIntent)
                .setTicker(tickerText)
                .setAutoCancel(true);
        if (!isAlreadySounded) {
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            isAlreadySounded = true;
        }

        getNotificationManager(context).notify(123, builder.build());

        MktLog.d(TAG, "isAlreadySounded : " + isAlreadySounded);
        return isAlreadySounded;
    }


    private static NotificationManager getNotificationManager(Context context) {
        if (mNotificationMgr == null) {
            mNotificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mNotificationMgr;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
