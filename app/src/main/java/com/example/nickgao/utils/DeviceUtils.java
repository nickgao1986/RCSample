package com.example.nickgao.utils;

import android.os.Build;

/**
 * Created by nick.gao on 1/31/17.
 */

public class DeviceUtils {

    public static boolean isContainOrAboveAndroidOS6_0(){
        return Build.VERSION.SDK_INT>=23;
    }

}
