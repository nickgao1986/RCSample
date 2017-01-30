package com.example.nickgao.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.example.nickgao.R;

/**
 * Created by nick.gao on 1/30/17.
 */

public class RcAlertDialog extends AlertDialog {

    protected RcAlertDialog(Context context) {
        super(context, RCMConstants.ALL_DIALOGS_THEME_ID);
    }

    public static Builder getBuilder(Context context) {
        return new AlertDialog.Builder(context, RCMConstants.ALL_DIALOGS_THEME_ID)
                .setOnKeyListener(new OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return keyCode == KeyEvent.KEYCODE_SEARCH;
                    }
                });
    }

    public static void showOkAlertDialog(Context context, int titleId, int msgId) {
        AlertDialog.Builder builder = RcAlertDialog.getBuilder(context);
        builder.setTitle(titleId).setMessage(msgId).setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public static Builder getForceUpgradeDialog(Context context, String serverVersion, String currentVersion) {
        Builder builder = new AlertDialog.Builder(context, RCMConstants.ALL_DIALOGS_THEME_ID).setCancelable(false);
        builder.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        String content = context.getString(R.string.force_upgrade_content, serverVersion, currentVersion);
        builder.setTitle(R.string.upgrade_dialog_title).setMessage(content);
        return builder;
    }


    public static Builder getNeedUpgradeDialog(Context context, String serverVersion, String currentVersion) {
        Builder builder = getBuilder(context);
        String content = context.getString(R.string.optional_upgrade_content, serverVersion, currentVersion);
        builder.setTitle(R.string.upgrade_dialog_title).setMessage(content);

        return builder;
    }


    public static Builder getUserCredentialExpiredInDialog(Context context, String date) {
        Builder builder = getBuilder(context);
        String content = context.getString(R.string.credential_expiredIn_dialog_content, date);
        builder.setTitle(R.string.credential_expiredIn_dialog_title).setMessage(content);
        builder.setCancelable(true);
        return builder;
    }

    public static Builder getUserCredentialExpiredDialog(Context context) {
        Builder builder = new AlertDialog.Builder(context, RCMConstants.ALL_DIALOGS_THEME_ID).setCancelable(false);
        builder.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        String content = context.getString(R.string.credential_expired_dialog_content);
        builder.setTitle(R.string.credential_expired_dialog_title).setMessage(content);
        return builder;
    }
}
