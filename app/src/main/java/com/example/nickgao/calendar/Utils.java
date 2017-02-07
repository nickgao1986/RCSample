package com.example.nickgao.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.nickgao.R;

import java.util.Calendar;

/**
 * Created by kasni.huang on 5/21/15.
 */
public class Utils {
    public static final int WEEKDAYS = 7;

    /**
     * 日期变量转成对应的星期字符串
     * @param calendar
     * @return
     */
    public static String calendarToWeek(Context context, Calendar calendar) {
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            return null;
        }

        return context.getResources().getStringArray(R.array.week)[dayIndex - 1];
    }

    //，设置标题栏和背景颜色
    public static void setColor(Context context, String titleColor, String backColor) {
        SharedPreferences.Editor editor = context.getSharedPreferences(ScheduleConstant.COLOR_PREFERENCE, Activity.MODE_PRIVATE).edit();
        editor.putString(ScheduleConstant.TITLE_COLOR, titleColor);
        editor.putString(ScheduleConstant.ROOT_VIEW_COLOR, backColor);
        editor.apply();
    }
}
