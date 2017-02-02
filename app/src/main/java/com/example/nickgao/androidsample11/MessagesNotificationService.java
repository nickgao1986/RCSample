package com.example.nickgao.androidsample11;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.nickgao.R;
import com.example.nickgao.utils.DeviceUtils;
import com.example.nickgao.utils.RCMConstants;

import static com.example.nickgao.utils.RCMConstants.NOTIFICATION_GROUP;

/**
 * Created by nick.gao on 2/2/17.
 */

public class MessagesNotificationService {


    private static class TextNotification {
        long conversationID;
        int unreadCount;
        String displayName;
        String text;
    }

    public static void updateTextNotification(Context context) {
        TextNotification textNotification = new TextNotification();
        textNotification.conversationID = 32432l;
        textNotification.unreadCount = 2;
        textNotification.displayName = "Test";

        updateTextMessageNotification(context,textNotification);
    }


    public static void updateTextMessageNotification(Context context, TextNotification textNotification) {
        final String tickerText = context.getResources().getString(R.string.messages_notification_ticker);
        String content;
        if (textNotification.unreadCount <= 1) {
            content = textNotification.text;
        } else {
            content = context.getString(R.string.messages_notification_line2_mult_text, textNotification.unreadCount);
        }

        Intent notificationIntent = new Intent(context, MessagesNotification.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra(RCMConstants.SMS_CONVERSATION_ID, textNotification.conversationID);
        notificationIntent.putExtra(RCMConstants.NOTIFICATION_ACTION, true);


        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        boolean isContainAbove5_0 = DeviceUtils.isContainOrAboveAndroidOS5_0();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(textNotification.displayName)
                .setShowWhen(true)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notify_text_message_4x)
                .setContentIntent(contentIntent)
                .setTicker(tickerText)
                .setAutoCancel(true)
                .setGroup(NOTIFICATION_GROUP)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        if (isContainAbove5_0) {
            builder.setColor(context.getResources().getColor(R.color.notification_icon_bg));
        }
        if (textNotification.unreadCount <= 1) {
            Intent callIntent = new Intent(context, MessagesNotification.class);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            callIntent.putExtra(RCMConstants.NOTIFICATION_CANCEL_ID, textNotification.conversationID);
            callIntent.putExtra(RCMConstants.NOTIFICATION_TEXT_CALL_ACTION, true);
            callIntent.putExtra(RCMConstants.NOTIFICATION_ACTION, true);
            PendingIntent callPendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis() + 5,
                    callIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(isContainAbove5_0 ? R.drawable.ic_notify_interactive_reply_5x :
                    R.drawable.ic_notify_interactive_reply_4x, context.getResources().getString(R.string.notification_btn_reply), contentIntent);
            builder.addAction(isContainAbove5_0 ? R.drawable.ic_notify_interactive_call_5x :
                    R.drawable.ic_notify_interactive_call_4x, context.getResources().getString(R.string.call), callPendingIntent);
        }

        NotificationUtils.notify(context, (int) textNotification.conversationID, builder.build());
        // updateNotificationSummary(context);
//        sTextConversations.add(textNotification.conversationID);
//        sendTextNotificationBroadcast(context);
    }

}
