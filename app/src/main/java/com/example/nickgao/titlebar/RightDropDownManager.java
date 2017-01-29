package com.example.nickgao.titlebar;

import android.content.Context;
import android.view.View;

import com.example.nickgao.utils.widget.RightDropDownDialog;
import com.example.nickgao.utils.widget.RightDropDownDialogForPhone;

import java.util.ArrayList;

/**
 * Created by nick.gao on 1/29/17.
 */

public class RightDropDownManager {

    private RightDropDownDialog mRightDropDownFilterDialog;
    private ArrayList<DropDownItem> mDropDownItemList;
    protected RightDropDownDialog.OnDropdownClickListener mDropdownClickListener;
    public Context mContext;

    public RightDropDownManager(Context context) {
        mContext = context;
    }

    public void setDropDownClickListener(RightDropDownDialog.OnDropdownClickListener dropdownClickListener) {
        mDropdownClickListener = dropdownClickListener;
    }



    public void showDialog(boolean showAnimation, View banner){
        if (mRightDropDownFilterDialog == null) {
            mRightDropDownFilterDialog = new RightDropDownDialogForPhone(mContext);
        }

        mRightDropDownFilterDialog.setTopMenuItemList(mDropDownItemList);
        mRightDropDownFilterDialog.showDialog(showAnimation, banner);
        mRightDropDownFilterDialog.setDropDownClickListener(mDropdownClickListener);
    }

    public void setDropDownItemList(ArrayList<DropDownItem> mDropDownItemList) {
        this.mDropDownItemList = mDropDownItemList;
    }

    public void dismissDialog() {
        if(mRightDropDownFilterDialog != null) {
            mRightDropDownFilterDialog.dismiss();
        }
    }


    public boolean isShowing() {
        if(mRightDropDownFilterDialog != null) {
            return mRightDropDownFilterDialog.isShowing();
        }
        return false;
    }

}
