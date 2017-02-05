package com.example.nickgao.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.example.nickgao.R;
import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;

import java.io.File;
import java.util.List;

/**
 * Created by nick.gao on 2/3/17.
 */

public class EmailSender {

    private static final String PLAIN_TEXT = "plain/text";
    private Context mContext;

    public EmailSender(Context ctx) {
        mContext = ctx;
    }

    /**
     * Send <code>ACTION_SEND</code> request.
     *
     * @param to                  to
     * @param subject             subject
     * @param body                body
     * @param attachementFilePath attachment file path or <code>null</code>
     */
    public boolean sendEmail(String to[], String subject, String body, String attachementFilePath) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        if (attachementFilePath != null) {
            Uri attachmentUri = null;
            try {
                File file = new File(attachementFilePath);
                if (file == null) {
                    if (LogSettings.MARKET) {
                        MktLog.w("[RC] Mail", "File error: " + attachementFilePath);
                    }
                } else if (!file.exists()) {
                    if (LogSettings.MARKET) {
                        MktLog.w("[RC] Mail", "File does not exist: " + attachementFilePath);
                    }
                } else if (!file.canRead()) {
                    if (LogSettings.MARKET) {
                        MktLog.w("[RC] Mail", "File can't be read: " + attachementFilePath);
                    }
                } else if (!file.isFile()) {
                    if (LogSettings.MARKET) {
                        MktLog.w("[RC] Mail", "Invalid file: " + attachementFilePath);
                    }
                } else {
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    attachmentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName(), file);
                    if (LogSettings.MARKET) {
                        MktLog.i("[RC] Mail", "Attachement path[size=" + file.length() + "]: " + attachementFilePath);
                        MktLog.i("[RC] Mail", "Attachement URI: " + attachmentUri.toString());
                    }
                }
            } catch (java.lang.Throwable ex) {
                if (LogSettings.MARKET) {
                    MktLog.w("[RC] Mail", "Error: " + ex.toString());
                }
            }

            if (attachmentUri != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, attachmentUri);
            }
        }
        emailIntent.setType(PLAIN_TEXT);
        List<ResolveInfo> availableSoft = mContext.getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (availableSoft.size() <= 0) {
            return false;
        }
        mContext.startActivity(Intent.createChooser(emailIntent, mContext.getResources().getString(R.string.menu_sendEmail)));

        return true;
    }

}
