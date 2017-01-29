package com.example.nickgao.titlebar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.utils.widget.HeaderPhotoView;

/**
 * Created by nick.gao on 2014/12/8.
 */
public class RCBaseTitleBar extends LinearLayout {

    protected TextView mTitleView = null;
    protected HeaderClickListener mHeaderClickListener;
    protected LinearLayout mPhotoLayout;
    protected HeaderPhotoView mPhotoView;
    protected Context mContext;
    protected String mCurrentTabName;

    public interface HeaderClickListener {
        void onRightButtonClicked();
        void onRightFirstButtonClicked();
        void onLeftButtonClicked();
    }

    public RCBaseTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) getDefaultBottomPadding());
    }

    public RCBaseTitleBar(Context context) {
        super(context);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) getDefaultBottomPadding());
    }

    protected void init(Context context, AttributeSet attrs) {
    }

    public float getDefaultBottomPadding() {
        return getResources().getDimensionPixelSize(R.dimen.actoin_bar_border_width);
    }

    public void setNameText(String name) {
        mTitleView.setText(name);
    }

    public void setText(int stringId) {
        mTitleView.setText(stringId);
    }

    public void setButtonsClickCallback(HeaderClickListener _headerClickListener) {
        mHeaderClickListener = _headerClickListener;
    }

    public void setCurrentScreenName(String name) {
        mCurrentTabName = name;
    }

    public void setRightImageRes(int resId) {
    }

    public void setRightFirstImageRes(int resId) {
    }

    public void setRightFirstImageResVisiblility(int visiblility) {

    }

    public void setRightImageVisibility(int visibility) {
    }

    public void setRightVisibility(int visibility) {
    }

    public void setRightBtnEnabled(boolean flag) {
    }

    public void setRightImageBtnEnabled(boolean flag) {
    }

    public void setRightText(int stringId) {
    }

    public void setLeftText(int stringId) {
    }

    public void showProfile() {
    }

    public void hideProfile() {
    }

    public void setTitleVisibility(int visibility) {
    }

    protected class ProfileOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
        }
    }
}
