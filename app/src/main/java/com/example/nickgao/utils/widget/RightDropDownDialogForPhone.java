package com.example.nickgao.utils.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.nickgao.R;


/**
 * Created by nick.gao on 2015/5/8.
 */
public  class RightDropDownDialogForPhone extends RightDropDownDialog {
    private boolean mShowAnimation = true;
    private boolean mIsInAnimation;
    private boolean mFinishShowDialog = false;

    public RightDropDownDialogForPhone(Context context) {
        super(context);
        mContext = context;
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP | Gravity.FILL_HORIZONTAL;
        lp.flags |= WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drop_down_filter_dialog);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mShowAnimation) {
            Animation downAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.drop_down_menu_down_anim);
            downAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationEnd(Animation animation) {
                    mFinishShowDialog = true;
                    mIsInAnimation = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationStart(Animation animation) {
                }

            });
            mDropDownMenuLayout.startAnimation(downAnimation);
            mIsInAnimation = true;
        } else {
            mFinishShowDialog = true;
        }
    }

    public void hideDropDownFilter(boolean showAnimation) {
        Animation upAnimation = AnimationUtils.loadAnimation(mContext, R.anim.drop_down_menu_up_anim);
        upAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
                mIsInAnimation = false;

            }
        });
        startGridViewUpAnimation(upAnimation);
        mIsInAnimation = true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mFinishShowDialog) {
                if (!mIsInAnimation) {
                    hideDropDownFilter(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mFinishShowDialog) {
                if (!mIsInAnimation) {
                    hideDropDownFilter(true);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    public void showDialog(boolean showAnimation, View banner) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int positionY = mContext.getResources().getDimensionPixelSize(R.dimen.header_hight);
        if (banner == null) {
            lp.y = positionY;
        } else {
            lp.y = positionY + banner.getMeasuredHeight();
        }
        mShowAnimation = showAnimation;
        Activity activity = (Activity)mContext;
        if(activity != null && !activity.isFinishing()) {
            show();
        }

    }

    public void startGridViewUpAnimation(Animation animation) {
        mDropDownMenuLayout.startAnimation(animation);
    }
}