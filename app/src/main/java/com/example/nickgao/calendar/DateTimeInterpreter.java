package com.example.nickgao.calendar;

import java.util.Calendar;

/**
 * Created by kasni.huang on 5/18/15.
 */
public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
