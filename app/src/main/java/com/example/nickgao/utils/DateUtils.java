package com.example.nickgao.utils;


import android.os.SystemClock;
import android.text.TextUtils;

import com.example.nickgao.logging.LogSettings;
import com.example.nickgao.logging.MktLog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static String TAG = "[RC] DateUtils";
    private static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat ISO_8601_FORMATTER = new SimpleDateFormat(ISO_8601_PATTERN);

    //fix bug AB-11092-Nexus4: Call Log: History records in Missed filter aren't correct while login with 6.0 account
    public static Date parseISO8601Date(String stringDate) {
        Date date = null;
        synchronized (ISO_8601_FORMATTER) {
            try {
                //ISO_8601_FORMATTER.
                ISO_8601_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
                date = ISO_8601_FORMATTER.parse(stringDate);
            } catch (ParseException e) {
                if (LogSettings.MARKET) {
                    MktLog.w(TAG, "parseISO8601Date(): " + e.getMessage());
                }
            }
        }
        return date;
    }

    //    private final static SimpleDateFormat formatter = new SimpleDateFormat("dd/HH:mm:ss", Locale.US);
    private final static String COMMON_DATE = "dd/HH:mm:ss";
//    private SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE, Locale.US);

    public static String getUTCTimeFromElapsedTime(long elapsedTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE);
        return formatter.format(new Date(System.currentTimeMillis() - (SystemClock.elapsedRealtime() - elapsedTime)));
    }

    private static String getDateLabel(long time) {
        return LabelsUtils.getDateLabel(time);
    }

    public static String getRelativeDateFromElapsedTime(long elapsedTime) {
        try {
            int flags = android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;
            long curTime = System.currentTimeMillis();
            long time = curTime - (SystemClock.elapsedRealtime() - elapsedTime);
            if (time > curTime) {
                return getDateLabel(time);
            }
            return android.text.format.DateUtils.getRelativeTimeSpanString(time, curTime, android.text.format.DateUtils.MINUTE_IN_MILLIS, flags)
                    .toString();
        } catch (java.lang.Throwable th) {
            return "TIME_ERROR";
        }
    }

    public static long getExpireTime() {
//        long mill = SystemClock.elapsedRealtime() - CurrentUserSettings.getSettings().getRefreshTokenCreateTime();
//        long oneDay = 24 * 60 * 60 * 1000;
//        return mill/oneDay;
        return 0L;
    }

    public static String getUTCandRelativeDateFromElapsedTime(long elapsedTime) {
        try {
            int flags = android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;
            long curTime = System.currentTimeMillis();
            long time = curTime - (SystemClock.elapsedRealtime() - elapsedTime);
            StringBuffer sb = new StringBuffer();
            SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE);
            sb.append(formatter.format(new Date(time)));
            if (time <= curTime) {
                sb.append('{');
                sb.append(android.text.format.DateUtils.getRelativeTimeSpanString(time, curTime, android.text.format.DateUtils.MINUTE_IN_MILLIS,
                        flags).toString());
                sb.append('}');
            }
            return sb.toString();
        } catch (java.lang.Throwable th) {
            return "TIME_ERROR";
        }
    }

    private static final String ISO_8601_PATTERN_FOR_CONTACT = "yyyy-MM-dd";
//    private static final SimpleDateFormat ISO_8601_FORMATTER_FOR_CONTACT = new SimpleDateFormat(ISO_8601_PATTERN_FOR_CONTACT, Locale.US);

    public static Date parseISO8601DateForContact(String stringDate) {
        Date date = null;
        try {
            SimpleDateFormat ISO_8601_FORMATTER_FOR_CONTACT = new SimpleDateFormat(ISO_8601_PATTERN_FOR_CONTACT);
            ISO_8601_FORMATTER_FOR_CONTACT.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = ISO_8601_FORMATTER_FOR_CONTACT.parse(stringDate);
        } catch (ParseException e) {
            if (LogSettings.MARKET) {
                MktLog.w(TAG, "parseISO8601Date(): " + e.getMessage());
            }
        }
        return date;
    }

    /**
     * Returns label for current time.
     *
     * @return label for current time.
     */
    public static String currentTimeLabel() {
        SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE);
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    /**
     * Returns label for the time.
     *
     * @param time the time to return label for.
     * @return label for the time.
     */
    public static String getTimeLabel(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(COMMON_DATE);
        return formatter.format(new Date(time));
    }

    /**
     * Returns a label for event happened <code>inTime</code> ms
     *
     * @param inTime when event shall be happened
     * @return label for event happened <code>inTime</code> ms
     */
    public static String getLabelForEventInTime(long inTime) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(inTime, 0, 0) + " at " + getTimeLabel(inTime + System.currentTimeMillis());
    }

    public static String getUTCFormatTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        Date date = parseStringToUTCDate(time);
        DateFormat utcFormat = new SimpleDateFormat(UTC_PATTERN, Locale.getDefault());
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormat.format(date);
    }

    public static Calendar getCalendarByTime(String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseStringToUTCDate(time));
        return calendar;
    }

    public static Date parseStringToUTCDate(String time) {
        DateFormat dateFormat = new SimpleDateFormat(ISO_8601_PATTERN_FOR_CONTACT, Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        return date;
    }

    public static String parseToUTCTime(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat(ISO_8601_PATTERN_FOR_CONTACT, Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat.format(dateFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
