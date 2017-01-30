package com.example.nickgao.utils;

import com.example.nickgao.R;
import com.example.nickgao.rcproject.RingCentralApp;

import org.apache.commons.lang.time.FastDateFormat;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by nick.gao on 1/30/17.
 */

public class LabelsUtils {

    private static final String TAG = "[RC] LabelsUtils";
    private static String mDateFormatString = "MM/dd/yyyy";

    private static int[] weekArray = new int[] {
            R.string.Sunday,
            R.string.Monday,
            R.string.Tuesday,
            R.string.Wednesday,
            R.string.Thursday,
            R.string.Friday,
            R.string.Saturday,
    };



    public static String getDateLabel(Date date) {

        return getDateLabel(date.getTime());
    }

    public static String getDateLabel(long time) {
        String dateStr;
        try {
            DateFormat df = android.text.format.DateFormat.getDateFormat(RingCentralApp.getContextRC());
            dateStr = df.format(new Date(time));

        } catch (IllegalArgumentException ex) {
            FastDateFormat fdf = FastDateFormat.getInstance(mDateFormatString);
            dateStr = fdf.format(time);
        }

        return dateStr;
    }


}
