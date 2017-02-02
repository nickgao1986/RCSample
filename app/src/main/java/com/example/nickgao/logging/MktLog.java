package com.example.nickgao.logging;

import android.util.Log;


public class MktLog {

    public static void d(String tag, String msg) {
        if (null != tag && null != msg) {
            Log.d(tag, msg);
        }
    }


    public static void w(String tag, String msg) {
        if (null != tag && null != msg) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (null != tag && null != msg) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (null != tag && null != msg) {
            Log.d(tag, msg);
        }
    }


    public static void e(String tag, String msg) {
        if (null != tag && null != msg) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (null != tag && null != msg) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, Throwable t) {
        if (null != tag && null != t) {
            Log.d(tag, t.toString());
        }
    }

    public static void e(String tag, Throwable t) {
        if (null != tag && null != t) {
            Log.e(tag, t.toString());
        }
    }


}
