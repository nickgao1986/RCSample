package com.example.nickgao.rcproject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.androidsample11.TestActivity;
import com.example.nickgao.contacts.adapters.contactsprovider.ContactsProvider;
import com.example.nickgao.logging.MktLog;

public class RingCentralApp extends Application{
    /**
     * Defines logging tag.
     */
	private static final String TAG = "[RC]RingCentralApp";
    private WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
    private WindowManager mWindowManager = null;
    private View mLangSettingFloatLayout = null;

    /**
	 * Keeps application execution context.
	 */
	private static Context sApplicationContext = null;
	
	
    @Override
    public void onCreate() {
        super.onCreate();      
        // Fixed bug AB-8845 Polling didn't work for Messages 
        // LoginScreen.setLoginIn();
        sApplicationContext = getApplicationContext();
        ContactsProvider.init(getApplicationContext());

    }
    
    public static Context getContextRC(){    	
        return sApplicationContext;
    }

    public static void restartTopActivity(final Context context, String message) {
        if (context instanceof Activity) {
            Activity act = (Activity) context;
            RingCentralApp app = (RingCentralApp) act.getApplication();
            app.createLanguageSettingFloatViewForAWhile(act, message);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setClass(context, TestActivity.class);
                context.startActivity(i);
            }
        }, 200);
    }


    private void createLanguageSettingFloatViewForAWhile(Activity act, String message) {
        createLanguageSettingFloatView(act, message);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RingCentralApp.this.clearLanguageSettingFloatView();
            }
        }, 1800);
    }


    public View createLanguageSettingFloatView(Activity act, String message) {
        View floatView = null;

        Activity parent = act.getParent();
        if (parent != null) {
            act = parent;
        }
        View viewRoot = act.getWindow().getDecorView();
        if (viewRoot != null) {
            View mainView = viewRoot.findViewById(R.id.root_view);
            if (mainView != null) {
                viewRoot = mainView;
            }

            RingCentralApp app = (RingCentralApp) act.getApplication();

            int w = viewRoot.getWidth();
            int h = viewRoot.getHeight();

            Bitmap bitmap = null;
            Canvas canvas = null;
            try {
                bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);
                viewRoot.draw(canvas);
            } catch (Throwable thr) {
                bitmap = null;
                MktLog.e(TAG, "restartTopActivity--->" + thr.toString());
            } finally {
                canvas = null;
            }
            floatView = app.createFloatView(act, message, bitmap, w, h);
        }
        return floatView;
    }

    public void clearLanguageSettingFloatView() {
        try {
            if (mLangSettingFloatLayout != null && mWindowManager != null) {
                MktLog.d(TAG, "clearLanguageSettingFloatView--->");
                mWindowManager.removeView(mLangSettingFloatLayout);
                mLangSettingFloatLayout = null;
            }
        } catch (Throwable th) {
            MktLog.e(TAG, "clearLanguageSettingFloatView--->" + th);
        }
    }


    private View createFloatView(final Activity act, String message, Bitmap bgBitmap, int width, int height) {
        try {
            MktLog.d(TAG, "createLanguageSettingFloatView--->" + mWindowManager);
            clearLanguageSettingFloatView();
            if (mWindowManager == null) {
                mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            }

            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            wmParams.format = PixelFormat.RGBA_8888;
            //wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.gravity = Gravity.LEFT | Gravity.TOP;
            wmParams.x = 0;
            wmParams.y = 0;
            wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            LayoutInflater inflater = LayoutInflater.from(this);
            mLangSettingFloatLayout = inflater.inflate(R.layout.float_view_waiting, null);
            ImageView ivBg = (ImageView) mLangSettingFloatLayout.findViewById(R.id.rootView);
            if (bgBitmap != null) {
                ivBg.setImageBitmap(bgBitmap);
            } else {
                MktLog.d(TAG, "createLanguageSettingFloatView set background color--->Color.WHITE");
                ivBg.setBackgroundColor(Color.WHITE);
            }

            ivBg.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            View monitorDialog = mLangSettingFloatLayout.findViewById(R.id.body);

            //defualt portrait
            int dialogWidthResId = R.integer.dialog_min_width_minor;

            try {
                int dialogWidthValue = getResources().getInteger(dialogWidthResId);
                monitorDialog.setMinimumWidth(width * dialogWidthValue / 100);
            } catch (Resources.NotFoundException err) {
                MktLog.e(TAG, "createLanguageSettingFloatView set dialog width--->" + err);
            }

            TextView tvMessage = (TextView) monitorDialog.findViewById(R.id.message);
            tvMessage.setText(message);

            mWindowManager.addView(mLangSettingFloatLayout, wmParams);

        } catch (Throwable th) {
            MktLog.e(TAG, "createLanguageSettingFloatView--->" + th);
        }
        return mLangSettingFloatLayout;
    }

}
