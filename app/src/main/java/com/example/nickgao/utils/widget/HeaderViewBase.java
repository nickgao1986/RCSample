package com.example.nickgao.utils.widget;

/**
 * Copyright (C) 2013, RingCentral, Inc.
 * All Rights Reserved.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.utils.ActivityUtils;
import com.example.nickgao.utils.DensityUtils;
import com.example.nickgao.utils.RCMConstants;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class HeaderViewBase extends LinearLayout implements View.OnClickListener {

    private final static int SEND_INIT = 0;
    private final static int SENDING_MODE = 1;
    private final static int SENDING_CUSTOM_MESSAGE_MODE = 2;

    private String leftButton = null;
    private String rightButton = null;
    private boolean isShowMenuButton = false;
    private ImageButton mBtnTopFirstRightImage = null;
    private Button btnRight = null;
    private Button btnLeft = null;
    private ImageButton btnImportDeviceContact = null;
    private ImageButton btnRightImg = null;
    private ImageButton btnLeftImg = null;

    private RelativeLayout progressLayout;
    private ProgressBar progressBar;

    private TextView hdr = null;

    private String header = "";
    private boolean headerAutoSize;

    private HeaderButtons hbc = null;

    private Drawable rightButtonDrawable = null;
    private Drawable leftButtonDrawable = null;
    private Drawable rightFirstButtonDrawable = null;

    private Animation inAnimation;
    private Animation outAnimation;

    private int mCount;
    private Timer mTimer;
    private boolean isEndSending = true;

    private boolean mShouldResetTextColor = false;

    public HeaderViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) getDefaultBottomPadding());
    }

    public HeaderViewBase(Context context) {
        super(context);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) getDefaultBottomPadding());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int leftDiff = hdr.getLeft();
        int rightDiff = r - hdr.getRight();
        final int diff = rightDiff - leftDiff;
        final int padding = Math.abs(diff);
        if (diff > 0) {
            if (hdr.getPaddingLeft() != padding) {
                hdr.setPadding(padding, 0, 0, 0);
            }
        } else if (diff < 0) {
            if (hdr.getPaddingRight() != padding) {
                hdr.setPadding(0, 0, padding, 0);
            }
        } else {
            hdr.setPadding(0, 0, 0, 0);
        }
        if(headerAutoSize) {
            autosizeHeader();
        }
        // AB-12048 "Clear" button is hidden in Call log screen when log in with Non-admin user
        // After changing padding, onLayout doesn't call.
        // Because isLayoutRequested() return true.
        // So we call super.onLayout() one more time
        // to recalculate position for all subviews.
        super.onLayout(changed, l, t, r, b);
    }

/*    protected void initInflate(Context context) {
    }*/

    public float getDefaultBottomPadding() {
        return getResources().getDimensionPixelSize(R.dimen.actoin_bar_border_width);
    }

    private void init(Context context, AttributeSet attrs) {

        View mainView = inflate(context, R.layout.header, this);
        //   setBackgroundResource(R.color.bgTitleBar);
        btnLeft = (Button) mainView.findViewById(R.id.btnTopLeft);
        btnRight = (Button) mainView.findViewById(R.id.btnTopRight);
        mBtnTopFirstRightImage = (ImageButton)mainView.findViewById(R.id.btnTopFirstRightImage);
        hdr = (TextView) mainView.findViewById(R.id.title);

        progressLayout = (RelativeLayout) mainView.findViewById(R.id.text_message_send_title);
        progressBar = (ProgressBar) mainView.findViewById(R.id.send_progressBar);

        btnRightImg = (ImageButton) mainView.findViewById(R.id.btnTopRightImage);
        btnLeftImg = (ImageButton) mainView.findViewById(R.id.btnTopLeftImage);
        btnImportDeviceContact = (ImageButton)mainView.findViewById(R.id.btnImportDeviceContact);
        btnImportDeviceContact.setOnClickListener(this);

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HeaderBar);

        header = a.getString(R.styleable.HeaderBar_header_header_text);
        headerAutoSize = a.getBoolean(R.styleable.HeaderBar_header_header_autosize, false);
        leftButton = a.getString(R.styleable.HeaderBar_header_left_button_label);
        rightButton = a.getString(R.styleable.HeaderBar_header_right_button_label);
        isShowMenuButton = a.getBoolean(R.styleable.HeaderBar_header_menu_button_label, false);

        rightButtonDrawable = a.getDrawable(R.styleable.HeaderBar_header_right_button_drawable);
        leftButtonDrawable = a.getDrawable(R.styleable.HeaderBar_header_left_button_drawable);
        rightFirstButtonDrawable = a.getDrawable(R.styleable.HeaderBar_header_right_first_button_drawable);
        boolean isNeedBackGround = a.getBoolean(R.styleable.HeaderBar_header_is_need_background,true);
        mShouldResetTextColor = a.getBoolean(R.styleable.HeaderBar_header_left_button_is_need_background,true);
        int titleBarTextColor = a.getColor(R.styleable.HeaderBar_header_title_text_color,R.color.text_action_bar_btn_nor);
        a.recycle();

        if (null != rightButton) {
            btnRight.setText(rightButton);
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setOnClickListener(this);
        }

        if (null != leftButton) {
            btnLeft.setText(leftButton);
            btnLeft.setVisibility(View.VISIBLE);
            btnLeft.setOnClickListener(this);
        }

        if (null != header) {
            hdr.setText(header);
        }

        if (null != rightButtonDrawable) {
            btnRight.setVisibility(GONE);

            btnRightImg.setVisibility(VISIBLE);
            btnRightImg.setImageDrawable(rightButtonDrawable);
            btnRightImg.setOnClickListener(this);
        }

        if (null != leftButtonDrawable) {
            btnLeft.setVisibility(GONE);

            btnLeftImg.setVisibility(VISIBLE);
            btnLeftImg.setImageDrawable(leftButtonDrawable);
            btnLeftImg.setOnClickListener(this);
        }

        if(rightFirstButtonDrawable != null) {
            mBtnTopFirstRightImage.setVisibility(VISIBLE);
            mBtnTopFirstRightImage.setImageDrawable(rightFirstButtonDrawable);
            mBtnTopFirstRightImage.setOnClickListener(this);
        }

        if (isShowMenuButton) {
            btnLeft.setVisibility(GONE);
            btnLeftImg.setVisibility(GONE);
        }

        timeHandler = new TimeHandler(this);

        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (btnRightImg != null) {
                    setRightImageBtnEnabled(true);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (btnRightImg != null) {
                    setRightImageBtnEnabled(false);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (btnRightImg != null) {
                    setRightImageBtnEnabled(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if(isNeedBackGround) {
            setBackgroundResource(R.color.bgTitleBar);
        }else{
            //for tablet my profile related activity
            if (null != leftButtonDrawable) {
                ImageButton btnTopLeftImageForTablet = (ImageButton)mainView.findViewById(R.id.btnTopLeftImageForTablet);
                btnTopLeftImageForTablet.setVisibility(View.VISIBLE);
                btnTopLeftImageForTablet.setImageDrawable(leftButtonDrawable);
                btnTopLeftImageForTablet.setOnClickListener(this);
                btnLeftImg.setVisibility(View.GONE);
            }
            hdr.setTextColor(titleBarTextColor);
        }

        if(!mShouldResetTextColor) {
            btnLeft.setBackgroundResource(R.color.transparentColor);
            btnLeft.setTextColor(Color.BLUE);
            btnLeft.setTextColor(getResources().getColor(R.color.bgTitleBar));
            hdr.setTextColor(getResources().getColor(R.color.text_income_color));
        }
    }

    private void autosizeHeader() {
        Paint p = new Paint();
        Rect bounds = new Rect();
        p.setTextSize(1);
        p.getTextBounds(hdr.getText().toString(), 0, hdr.getText().length(), bounds);
        float widthDifference = (hdr.getWidth())/bounds.width();
        if(widthDifference < hdr.getTextSize()) {
            hdr.setTextSize(TypedValue.COMPLEX_UNIT_PX, widthDifference);
        }
    }

    public void setRestPasswordTitleTextSize() {
        int size = getResources().getDimensionPixelSize(R.dimen.font_size_for_reset_password);
        hdr.setTextSize(size);
        hdr.getPaint().setFakeBoldText(true);
        btnLeftImg.setScaleType(ImageView.ScaleType.CENTER);

    }

    public boolean isRightVisible() {
        return btnRight.getVisibility() == View.VISIBLE;
    }

    public void setRightEnabled(boolean enabled) {
        if (btnRight.isEnabled() != enabled) {
            btnRight.setEnabled(enabled);
            titleInvalidate();
        }
    }

    public void setRightEnabledForVoipReplyMessage(boolean enabled) {
        if (btnRight.isEnabled() != enabled) {
            btnRight.setEnabled(enabled);
        }
    }

    public void setRightImageBtnEnabled(boolean enabled) {
        if (btnRightImg.isEnabled() != enabled) {
            btnRightImg.setEnabled(enabled);
        }
    }

    public boolean isRighImageBtnEnabled() {
        return btnRightImg.isEnabled();
    }

    public void setTitleMinWidth(Activity activity) {
        btnRight.setVisibility(View.GONE);
        btnRightImg.setVisibility(View.GONE);
        int offset = DensityUtils.dip2px(activity.getResources().getDimension(R.dimen.header_btn_width));
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screen_width = metric.widthPixels;
        //hdr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));
        hdr.setMinWidth(screen_width - 2 * offset);
    }

    /**
     * Button
     *
     * @param visibility
     */
    public void setRightVisibility(int visibility) {
        btnRight.setVisibility(visibility);
        if (VISIBLE == btnRight.getVisibility()) {
            btnRight.setOnClickListener(this);
        } else {
            btnRight.setOnClickListener(null);
        }
    }

    /**
     * ImageButton
     *
     * @param visibility
     */
    public void setRightImageVisibility(int visibility) {
        btnRightImg.setVisibility(visibility);
        if (VISIBLE == btnRightImg.getVisibility()) {
            btnRightImg.setOnClickListener(this);
        } else {
            btnRightImg.setOnClickListener(null);
        }
    }


    public void setRightFirstImageVisibility(int visibility) {
        mBtnTopFirstRightImage.setVisibility(visibility);
        if (VISIBLE == mBtnTopFirstRightImage.getVisibility()) {
            mBtnTopFirstRightImage.setOnClickListener(this);
        } else {
            mBtnTopFirstRightImage.setOnClickListener(null);
        }
    }

    public ImageButton getRightImageButton() {
        return btnRightImg;
    }

    public void setRightImageRes(int resId) {
        btnRightImg.setImageDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setFirstRightImageRes(int resId) {
        mBtnTopFirstRightImage.setImageDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setLeftEnabled(boolean enabled) {
        if (btnLeft.isEnabled() != enabled) {
            btnLeft.setEnabled(enabled);
            titleInvalidate();
        }
    }

    public void setLeftImageEnabled(boolean enabled) {
        if (btnLeftImg.isEnabled() != enabled) {
            btnLeftImg.setEnabled(enabled);
            titleInvalidate();
        }
    }

    public void setLeftImageVisible(boolean visible) {
        btnLeftImg.setVisibility(visible ? View.VISIBLE : View.GONE);
        titleInvalidate();
    }

    public void setLeftVisibility(int visibility) {
        btnLeft.setVisibility(visibility);
        if (VISIBLE == btnLeft.getVisibility()) {
            btnLeft.setOnClickListener(this);
        } else {
            btnLeft.setOnClickListener(null);
        }
    }

    public void setImportDeviceContactButtonVisibility(int visibility) {
        btnImportDeviceContact.setVisibility(visibility);
    }

    public void setImportDeviceContactButtonImage(int resId) {
        btnImportDeviceContact.setImageResource(resId);
    }

    public void setHeaderBackgroundResource(int resId) {
        setBackgroundResource(resId);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) getDefaultBottomPadding());
    }

    public void setButtonsClickCallback(HeaderButtons _hbc) {
        hbc = _hbc;
    }

    public interface HeaderButtons {
        void onRightButtonClicked();

        void onLeftButtonClicked();

        void onMenuButtonClicked();

        void onRightFirstButtonClicked();
    }

    @Override
    public void onClick(View v) {
        if (null == hbc) {
            return;
        }

        switch (v.getId()) {
            case R.id.btnTopLeft:
            case R.id.btnTopLeftImage:
                hbc.onLeftButtonClicked();
                break;
            case R.id.btnTopRight:
            case R.id.btnTopRightImage:
            case R.id.btnImportDeviceContact:
                hbc.onRightButtonClicked();
                break;
            case R.id.btnTopFirstRightImage:
                hbc.onRightFirstButtonClicked();
                break;
            case R.id.btnTopLeftImageForTablet:
                hbc.onLeftButtonClicked();
                break;
        }
    }

    public void setText(int stringId) {
        hdr.setText(stringId);
    }

    public void setNameText(String name) {
        hdr.setText(name);
    }

    public void setNameText(String name, Activity activity) {
        if (!ActivityUtils.isTablet()) {
            float mTextWidth = hdr.getPaint().measureText(name);
            //fix AB-10659-"About" is not in the centre of screen
            int offset = activity.getResources().getDimensionPixelSize(R.dimen.header_btn_width);
            int paddingLeft = activity.getResources().getDimensionPixelSize(R.dimen.header_padding_left_right);
            float width = RCMConstants.screenWidth - 2 * offset - 2 * paddingLeft;
            //fix AB-10520 Title "Default Launching" and "About Telus VoIP" should be fully shown for 480*800
            if (width < mTextWidth) {
                setTitleMinWidth(activity);
            }
        }
        hdr.setText(name);
    }

    public void setLeftText(int stringId) {
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setText(stringId);
        btnLeft.setOnClickListener(this);

        btnLeftImg.setVisibility(View.GONE);
        btnLeftImg.setImageDrawable(null);
        btnLeftImg.setOnClickListener(this);
    }

    public void setLeftImageRes(final int resId) {
        btnLeft.setVisibility(View.GONE);
        btnLeft.setOnClickListener(null);

        btnLeftImg.setVisibility(View.VISIBLE);
        btnLeftImg.setImageResource(resId);
        btnLeftImg.setOnClickListener(this);
    }


    public void setLeftImageResScaleType() {
        btnLeftImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    public void setRightText(int stringId) {
        btnRight.setText(stringId);
    }

    public void setRightTextBackground(int resId) {
        btnRight.setBackgroundResource(resId);
    }

    public void setRightTextColor(ColorStateList textColor) {
        btnRight.setTextColor(textColor);
    }

    public String getTitleText() {
        if (hdr == null) {
            return null;
        }
        return hdr.getText().toString();
    }

    private OnFinishSendListener onFinishSendListener;

    private Handler timeHandler;

    private static class TimeHandler extends Handler {
        private final WeakReference<HeaderViewBase> mRef;

        public TimeHandler(HeaderViewBase header) {
            mRef = new WeakReference<HeaderViewBase>(header);
        }

        public void handleMessage(Message msg) {
            HeaderViewBase header = mRef.get();
            if (header == null) {
                return;
            }

            switch (msg.what) {
                case SEND_INIT:
                    header.mCount = 0;
                    header.progressBar.setProgress(0);
                    break;

                case SENDING_MODE:
                    header.mCount += 5;
                    if (header.mCount >= 95 && !header.isEndSending) {
                        header.mCount = 95;
                    }
                    if (header.mCount > 100) {
                        header.finishSendingMessage();
                    }
                    header.progressBar.setProgress(header.mCount);
                    break;
                case SENDING_CUSTOM_MESSAGE_MODE:
                    header.mCount += 5;
                    if (header.mCount >= 95 && !header.isEndSending) {
                        header.mCount = 95;
                    }
                    if (header.mCount > 100) {
                        header.finishSendingMessage();
                    }
                    header.progressBar.setProgress(header.mCount);
                    break;
            }

            super.handleMessage(msg);
        }
    }

    private void startSendingMessage() {

        TimerTask taskMessage = new TimerTask() {
            public void run() {
                Message message = timeHandler.obtainMessage();
                message.what = SENDING_MODE;
                timeHandler.sendMessage(message);
            }
        };
        mTimer = new Timer(true);
        mTimer.schedule(taskMessage, 100, 100);
    }

    private void startSendingCustomMessage() {

        TimerTask taskMessage = new TimerTask() {
            public void run() {
                Message message = timeHandler.obtainMessage();
                message.what = SENDING_CUSTOM_MESSAGE_MODE;
                timeHandler.sendMessage(message);
            }
        };
        mTimer = new Timer(true);
        mTimer.schedule(taskMessage, 100, 100);
    }


    private void finishSendingMessage() {
        cancelTimer();
        hdr.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
        mCount = 0;
        isEndSending = true;
        onFinishSendListener.onFinishSend();
    }

    public void newSendingMessage() {
        isEndSending = false;
        cancelTimer();
        Message message = timeHandler.obtainMessage();
        message.what = SEND_INIT;
        timeHandler.sendMessage(message);
        hdr.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        mCount = 0;
        startSendingMessage();
    }

    public void newSendingCustomMessage(Context context, String msg) {
        isEndSending = false;
        cancelTimer();
        Message message = timeHandler.obtainMessage();
        message.what = SEND_INIT;
        timeHandler.sendMessage(message);
        hdr.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        mCount = 0;
        Intent intent = new Intent(RCMConstants.ACTION_SEND_REPLY_MESSAGE);
        intent.putExtra(RCMConstants.VOIP_REPLY_MESSAGE_CONTENT, msg);
        context.sendBroadcast(intent);
        startSendingCustomMessage();
    }


    public void endSendingMessage() {
        mCount = 95;
        isEndSending = true;
    }

    public void sendingMessage() {
        isEndSending = false;
        mCount = 95;
        hdr.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        TimerTask taskMessage = new TimerTask() {
            public void run() {
                Message message = timeHandler.obtainMessage();
                message.what = SENDING_MODE;
                timeHandler.sendMessage(message);
            }
        };
        mTimer = new Timer(true);
        mTimer.schedule(taskMessage, 100, 100);
    }


    public void hideProgress() {
        cancelTimer();
        hdr.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
    }

    public void setOnFinishSendListener(OnFinishSendListener listener) {
        onFinishSendListener = listener;
    }

    public interface OnFinishSendListener {
        void onFinishSend();
    }

    /**
     * A workaround for Android 4.0 or above
     */
    private void titleInvalidate() {
        if (hdr != null) {
            hdr.invalidate();
        }
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void setSlideVisibility(int visibility) {
        if (getVisibility() != visibility) {
            if (visibility == VISIBLE) {
                startAnimation(inAnimation);
            } else {
                startAnimation(outAnimation);
            }
        }
        super.setVisibility(visibility);
    }
    public boolean isEndSending() {
        return  isEndSending;
    }
}
