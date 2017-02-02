package com.example.nickgao.i18n;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.nickgao.R;
import com.example.nickgao.utils.RCMConstants;

/**
 * Created by nick.gao on 2/1/17.
 */

public class LanguageSettingDialog extends ProgressDialog {
    private boolean mCancelledByUser = false;

    public LanguageSettingDialog(Context context) {
        super(context, RCMConstants.ALL_DIALOGS_THEME_ID);
        setMessage(context.getString(R.string.lang_setting_message));
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}


