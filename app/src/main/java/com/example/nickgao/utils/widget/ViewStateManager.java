package com.example.nickgao.utils.widget;

import android.view.View;
import android.view.ViewGroup;

public class ViewStateManager {
    protected void setViewState(View view, int viewState, boolean enable) {
        view.setVisibility(viewState);
        if (view instanceof ViewGroup) {
            ViewGroup panel = (ViewGroup) view;
            for (int i = 0; i < panel.getChildCount(); i++) {
                panel.getChildAt(i).setVisibility(viewState);
                panel.getChildAt(i).setClickable(enable);
                panel.getChildAt(i).setFocusable(enable);
            }
        }
    }
}
