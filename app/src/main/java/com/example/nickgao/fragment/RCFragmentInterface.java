package com.example.nickgao.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * Created by nick.gao on 2/8/17.
 */

public interface RCFragmentInterface {

    void setFragmentId(int id);
    int getFragmentId();
    void changeBottomBar(int state);
    boolean onBackPressed();
    void onProcessUriScheme(boolean isTurnByScheme, Intent intent);
    void onScreenRotation();
    boolean fragmentDispatchKeyEvent(KeyEvent event);
    Dialog onCreateDialog(int id);
    void onFragmentSaveInstanceState(Bundle outState);
    void onFragmentActivityResult(int requestCode, int resultCode, Intent data);
    void onFragmentRestart();
    void onNewIntent(Intent intent);
    boolean isPopMenuShowing();
}
