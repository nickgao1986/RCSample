package com.example.nickgao.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;

/**
 * Created by nick.gao on 2/8/17.
 */

public class RCListFragment extends ListFragment implements RCFragmentInterface{


    private int mFragmentId;
    public Activity mActivity;
    private int mCurrentOrig = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentOrig = getResources().getConfiguration().orientation;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mCurrentOrig != newConfig.orientation) {//orientation changed
            mCurrentOrig = newConfig.orientation;
            onScreenRotation();
        }
    }

    @Override
    public void setFragmentId(int id) {
        mFragmentId = id;
    }

    @Override
    public int getFragmentId() {
        return mFragmentId;
    }

    @Override
    public void changeBottomBar(int state) {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onProcessUriScheme(boolean isTurnByScheme, Intent intent) {

    }

    @Override
    public void onScreenRotation() {

    }

    @Override
    public boolean fragmentDispatchKeyEvent(KeyEvent event) {
        return false;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        return null;
    }

    @Override
    public void onFragmentSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onFragmentActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onFragmentRestart() {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public boolean isPopMenuShowing() {
        return false;
    }
}
