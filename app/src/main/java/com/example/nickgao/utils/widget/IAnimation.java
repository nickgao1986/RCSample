package com.example.nickgao.utils.widget;

import android.view.View;

public interface IAnimation {
	public void setAnimationDelegate(IAnimationDelegate aniDelegate);

	public void close(View view, int duration, int tab, boolean isSwitch);

	public void open(View view, int duration);
}
