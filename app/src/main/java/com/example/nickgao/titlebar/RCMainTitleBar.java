package com.example.nickgao.titlebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.utils.widget.HeaderPhotoView;



/**
 * Created by nick.gao on 2014/12/8.
 */
public class RCMainTitleBar extends RCBaseTitleBar implements View.OnClickListener {
    private Button mBtnRight = null;
    private ImageButton mBtnRightImg = null;
    public Button mBtnTopLeft = null;
    public ImageButton mBtnTopLeftImage;
    public ImageButton mBtnTopFirstRightImage;
    private static final String TAG = "[RC]RCMainTitleBar";

    public RCMainTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RCMainTitleBar(Context context) {
        super(context, null);
        init(context, null);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        View mainView = inflate(context, R.layout.profile_titlebar_with_rightbutton, this);
        mTitleView = (TextView) mainView.findViewById(R.id.title);
        mBtnTopLeft = (Button) mainView.findViewById(R.id.btnTopLeft);
        mBtnTopLeftImage = (ImageButton)mainView.findViewById(R.id.btnTopLeftImage);
        mBtnTopFirstRightImage = (ImageButton)mainView.findViewById(R.id.btnTopFirstRightImage);
        mPhotoLayout = (LinearLayout) mainView.findViewById(R.id.layout_photo);
        mPhotoView = (HeaderPhotoView) mainView.findViewById(R.id.title_bar_photo);
        ProfileOnClickListener profileOnClickListener = new ProfileOnClickListener();
        mPhotoLayout.setOnClickListener(profileOnClickListener);
        mBtnRightImg = (ImageButton) mainView.findViewById(R.id.btnTopRightImage);
        mBtnRight = (Button) mainView.findViewById(R.id.btnTopRight);
        mBtnRightImg.setOnClickListener(this);
        mBtnTopLeft.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);
        mBtnTopLeft.setVisibility(View.GONE);
        setBackgroundResource(R.color.bgTitleBar);

        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);
        String titleText = a.getString(R.styleable.TitleBar_title_text);
        String rightButtonText = a.getString(R.styleable.TitleBar_title_right_button_label);
        Drawable rightButtonDrawable = a.getDrawable(R.styleable.TitleBar_title_right_button_drawable);
        Drawable rightFirstButtonDrawable = a.getDrawable(R.styleable.TitleBar_title_right_first_button_drawable);
        a.recycle();

        if (null != rightButtonText) {
            mBtnRight.setText(rightButtonText);
            mBtnRight.setVisibility(View.VISIBLE);
            mBtnRight.setOnClickListener(this);
        }else{
            mBtnRight.setVisibility(GONE);
        }

        if (null != titleText) {
            mTitleView.setText(titleText);
        }

        if (null != rightButtonDrawable) {
            mBtnRight.setVisibility(GONE);
            mBtnRightImg.setVisibility(VISIBLE);
            mBtnRightImg.setImageDrawable(rightButtonDrawable);
            mBtnRightImg.setOnClickListener(this);
        }else{
            mBtnRightImg.setVisibility(GONE);
        }

        if(null != rightFirstButtonDrawable) {
            mBtnTopFirstRightImage.setImageDrawable(rightFirstButtonDrawable);
            mBtnTopFirstRightImage.setVisibility(View.VISIBLE);
            mBtnTopFirstRightImage.setOnClickListener(this);
            mBtnRight.setVisibility(GONE);
            setTitleTextMaxWidth(true);
        }else{
            mBtnTopFirstRightImage.setVisibility(View.GONE);
            setTitleTextMaxWidth(false);
        }

    }

    public void setHeaderPhotoViewVisibility(int visibility) {
        if(visibility == View.VISIBLE) {
            mPhotoLayout.setVisibility(View.VISIBLE);
        }else{
            mPhotoLayout.setVisibility(View.GONE);
        }
    }

    public void setTitleTextMaxWidth(boolean hasTwoRightButton) {
        ViewGroup.LayoutParams params = mTitleView.getLayoutParams();
        LayoutParams paramsWrapContent = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_size_medium));
        if(hasTwoRightButton) {
            String mContent = mTitleView.getText().toString();

            int maxWidth = mContext.getResources().getDimensionPixelSize(R.dimen.drop_down_menu_message_title_width);
            String newText = getTruncText(mContent, mTitleView,maxWidth);

            if (newText.length() < mTitleView.length()) {
                params.width = maxWidth;
                mTitleView.setLayoutParams(params);
                mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_titlebar_text_size));
            }else{
                //mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_size_medium));
                mTitleView.setLayoutParams(paramsWrapContent);
            }
        }else{
            mTitleView.setLayoutParams(paramsWrapContent);
        }

    }

    private String getTruncText(String text, TextView view,int maxWidth) {
        final Paint textPaint = view.getPaint();
        final int numChar = textPaint.breakText(text, true, maxWidth, null);
        return text.substring(0, numChar);
    }

    public void setLeftImageVisible() {
        mBtnTopLeftImage.setVisibility(View.VISIBLE);
        mBtnTopLeft.setVisibility(View.GONE);
        mPhotoLayout.setVisibility(View.GONE);
        mPhotoView.setVisibility(GONE);
        mBtnTopLeftImage.setOnClickListener(this);
    }

    @Override
    public void setLeftText(int stringId) {
        mPhotoLayout.setVisibility(View.GONE);
        mPhotoView.setVisibility(GONE);
        mBtnTopLeft.setVisibility(View.VISIBLE);
        mBtnTopLeft.setText(stringId);
    }

    @Override
    public void showProfile() {
        mBtnTopLeft.setVisibility(View.GONE);
        mPhotoLayout.setVisibility(View.VISIBLE);
        mPhotoView.setVisibility(VISIBLE);
    }

    @Override
    public void hideProfile() {
        mPhotoLayout.setVisibility(View.GONE);
        mPhotoView.setVisibility(GONE);
    }

    @Override
    public void setRightText(int stringId) {
        mBtnRightImg.setVisibility(View.GONE);
        mBtnTopFirstRightImage.setVisibility(View.GONE);
        mBtnRight.setVisibility(View.VISIBLE);
        mBtnRight.setText(stringId);
    }

    @Override
    public void setRightImageRes(int resId) {
        mBtnRightImg.setVisibility(View.VISIBLE);
        mBtnRight.setVisibility(View.GONE);
        mBtnRightImg.setImageDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setRightFirstImageRes(int resId) {
        mBtnTopFirstRightImage.setVisibility(View.VISIBLE);
        mBtnRight.setVisibility(View.GONE);
        mBtnTopFirstRightImage.setImageDrawable(getContext().getResources().getDrawable(resId));
        setRightFirstImageResVisiblility(View.VISIBLE);
    }



    @Override
    public void setRightImageVisibility(int visibility) {
        mBtnRightImg.setVisibility(visibility);
        if (VISIBLE == mBtnRightImg.getVisibility()) {
            mBtnRightImg.setOnClickListener(this);
        } else {
            mBtnRightImg.setOnClickListener(null);
        }
        recalculateTitleTextFont();
    }

    @Override
    public void setRightVisibility(int visibility) {
        mBtnRight.setVisibility(visibility);
        if (VISIBLE == mBtnRight.getVisibility()) {
            mBtnRight.setOnClickListener(this);
        } else {
            mBtnRight.setOnClickListener(null);
        }
    }


    public void setRightFirstImageResVisiblility(int visibility) {
        mBtnTopFirstRightImage.setVisibility(visibility);
        if (VISIBLE == mBtnTopFirstRightImage.getVisibility()) {
            mBtnTopFirstRightImage.setOnClickListener(this);
        } else {
            mBtnTopFirstRightImage.setOnClickListener(null);
        }
        recalculateTitleTextFont();
    }

    private void recalculateTitleTextFont() {
        setTitleTextMaxWidth(mBtnRightImg.getVisibility() == VISIBLE && mBtnTopFirstRightImage.getVisibility() == VISIBLE);
    }

    public void setRightFirstImageResEnabled(boolean flag) {
        mBtnTopFirstRightImage.setEnabled(flag);
    }

    @Override
    public void setRightBtnEnabled(boolean flag) {
        mBtnRight.setEnabled(flag);
    }

    @Override
    public void setRightImageBtnEnabled(boolean flag) {
        mBtnRightImg.setEnabled(flag);
    }

    @Override
    public void onClick(View v) {
        if (null == mHeaderClickListener) {
            return;
        }
        switch (v.getId()) {
            case R.id.btnTopLeft:
                mHeaderClickListener.onLeftButtonClicked();
                break;
            case R.id.photo:
                break;
            case R.id.btnTopRight:
            case R.id.btnTopRightImage:
                mHeaderClickListener.onRightButtonClicked();
                break;
            case R.id.btnTopLeftImage:
                mHeaderClickListener.onLeftButtonClicked();
                break;
            case R.id.btnTopFirstRightImage:
                mHeaderClickListener.onRightFirstButtonClicked();
                break;
        }

    }

}
