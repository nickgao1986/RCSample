package com.example.nickgao.utils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by nick.gao on 2/4/17.
 */

public class RelativeLayoutEx extends RelativeLayout {

    private InterceptTouchEventListener mInterceptTouchEventListener;

    public RelativeLayoutEx(Context context) {
        super(context);
    }

    public RelativeLayoutEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mInterceptTouchEventListener != null
                && mInterceptTouchEventListener.onInterceptTouchEvent(ev)) {
            //return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public interface InterceptTouchEventListener {
        boolean onInterceptTouchEvent(MotionEvent ev);
    }

    public void setInterceptTouchEvent(InterceptTouchEventListener listener) {
        mInterceptTouchEventListener = listener;
    }

}

