package com.example.nickgao.utils;

import com.example.nickgao.R;
import com.example.nickgao.rcproject.RingCentralApp;

/**
 * Created by nick.gao on 1/29/17.
 */

public class ActivityUtils {

    public static boolean isTablet() {
        return RingCentralApp.getContextRC().getResources().getBoolean(R.bool.isTablet);
    }

}
