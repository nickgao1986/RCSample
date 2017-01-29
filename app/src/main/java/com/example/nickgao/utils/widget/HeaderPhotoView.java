package com.example.nickgao.utils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.nickgao.R;

/**
 * Created by nick.gao on 1/28/17.
 */

public class HeaderPhotoView extends RelativeLayout {


    private static final String TAG = "[RC]HeaderPhotoView";

    public Context mContext;

    public ImageView mRedDotView;
    public ImageView mMenuButton;
    //TODO keep


    public HeaderPhotoView(Context context) {
        super(context, null);
    }

    public HeaderPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public void initView() {
        View mainView = inflate(mContext, R.layout.header_profile_view, this);
        mainView.setFocusable(true);
        mainView.setFocusableInTouchMode(true);
        mRedDotView = (ImageView) mainView.findViewById(R.id.red_dot_imageview);
        mMenuButton = (ImageView) mainView.findViewById(R.id.btn_main_menu_action_menu);
    }

    public void setListener(OnClickListener mListener) {
        mMenuButton.setOnClickListener(mListener);
    }


}
