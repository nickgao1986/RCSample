package com.example.nickgao.utils;

import com.example.nickgao.rcproject.RingCentralApp;

/**
 * Created by nick.gao on 1/29/17.
 */

public class DensityUtils {

    public static int dip2px(float dpValue) {
        final float scale = RingCentralApp.getContextRC().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = RingCentralApp.getContextRC().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
