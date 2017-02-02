package com.example.nickgao.utils.widget;

public interface IAnimationDelegate {
    public void onAnimationStart(boolean isToUp);

    public void onAnimationEnd(int tab, boolean isSwitch, boolean isToUp);
}
