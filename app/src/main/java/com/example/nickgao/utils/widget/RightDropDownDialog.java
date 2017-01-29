package com.example.nickgao.utils.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.rcproject.RingCentralApp;
import com.example.nickgao.titlebar.DropDownItem;
import com.example.nickgao.titlebar.RCTitleBarWithDropDownFilter;

import java.util.ArrayList;

public abstract class RightDropDownDialog extends Dialog {

    protected ArrayList<DropDownItem> mTopMenuList;
    protected OnDropdownClickListener mDropdownClickListener;
    protected LinearLayout mDropDownMenuLayout;
    public Context mContext;

    public void setTopMenuItemList(ArrayList<DropDownItem> mTopMenuList) {
        this.mTopMenuList = mTopMenuList;
    }

    public void setDropDownClickListener(RightDropDownDialog.OnDropdownClickListener dropdownClickListener) {
        mDropdownClickListener = dropdownClickListener;
    }

    public RightDropDownDialog(Context context) {
        super(context, R.style.DropdownFilterDialogStyle);
        mContext = context;
    }

    public interface OnDropdownClickListener {
        void onClickItem(int index);
        void onHide();
    }


    public  void hideDropDownFilter(boolean showAnimation){
        if(mDropdownClickListener != null) {
            mDropdownClickListener.onHide();
        }
    }

    public abstract void showDialog(boolean showAnimation, View banner);



    public void init() {
        mDropDownMenuLayout = (LinearLayout) findViewById(R.id.text_chat_menu_linear);
        constructButton(mTopMenuList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    public void constructButton(ArrayList<DropDownItem> list) {
                mDropDownMenuLayout.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDropDownMenuLayout.addView(View.inflate(mContext,
                R.layout.view_top_menu_separator, null), params);
        final int size = list.size();
        final int lastPosition = size - 1;
        for (int i = 0; i < size; i++) {
            final DropDownItem item = list.get(i);
            View view = View.inflate(mContext, R.layout.dropdown_menu_item_layout,
                    null);
            view.setTag(item.getIndex());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDropDownFilter(true);
                    mDropdownClickListener.onClickItem((Integer) view.getTag());
                }
            });

            TextView btn = (TextView) view
                    .findViewById(R.id.top_menu_button);
            btn.setText(item.getName());
            btn.setTag(item.getIndex());
            TextView tab_indicator_counter = (TextView) view.findViewById(R.id.indicator_counter);
            if (item.getCouter() == 0) {
                tab_indicator_counter.setVisibility(View.GONE);
            } else {
                tab_indicator_counter.setText((item.getCouter() <= RCTitleBarWithDropDownFilter.MAX_MESSAGES_COUNT_TO_BE_DISPLAYED ?
                        String.valueOf(item.getCouter()) : RingCentralApp.getContextRC().getString(R.string.menu_more_than_99)));
            }

            boolean showSeparator = true;
            if (size == 1) {
                showSeparator = true;
            } else if (i == lastPosition) {
                showSeparator = false;
            }
            view.setBackgroundResource(R.drawable.drop_down_filter_item_background_selector);
            mDropDownMenuLayout.addView(view, params);
            if (showSeparator) {
                LinearLayout.LayoutParams paramsForLine = new LinearLayout.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.top_menu_vertical_separator));
                paramsForLine.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.drop_down_menu_item_margin_left_right);
                paramsForLine.rightMargin = paramsForLine.leftMargin;
                mDropDownMenuLayout.addView(View.inflate(mContext,
                        R.layout.drop_down_menu_divider_layout, null), paramsForLine);
            }
        }

        mDropDownMenuLayout.invalidate();
    }

}