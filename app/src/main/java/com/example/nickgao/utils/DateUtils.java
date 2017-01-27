package com.example.nickgao.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import android.os.SystemClock;
import com.example.nickgao.logging.*;

public class DateUtils {
    private static String TAG = "[RC] DateUtils";
    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat ISO_8601_FORMATTER = new SimpleDateFormat(ISO_8601_PATTERN);
    //fix bug AB-11092-Nexus4: Call Log: History records in Missed filter aren't correct while login with 6.0 account
    public  static  Date parseISO8601Date(String stringDate) {
    	Date date = null;
    	synchronized(ISO_8601_FORMATTER){
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
    
    private final static SimpleDateFormat formatter = new SimpleDateFormat("dd/HH:mm:ss");
    public static String getUTCTimeFromElapsedTime(long elapsedTime) {
        return formatter.format(new Date(System.currentTimeMillis() - (SystemClock.elapsedRealtime() - elapsedTime)));
    }
    private static String getDateLabel(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(SettingsFormatUtils.getDateFormat());
        return sdf.format(new Date(time));
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
        } catch (Throwable th) {
            return "TIME_ERROR";
        }
    }

    public static String getUTCandRelativeDateFromElapsedTime(long elapsedTime) {
        try {
            int flags = android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;
            long curTime = System.currentTimeMillis();
            long time = curTime - (SystemClock.elapsedRealtime() - elapsedTime);
            StringBuffer sb = new StringBuffer();
            sb.append(formatter.format(new Date(time)));
            if (time <= curTime) {
                sb.append('{');
                sb.append(android.text.format.DateUtils.getRelativeTimeSpanString(time, curTime, android.text.format.DateUtils.MINUTE_IN_MILLIS,
                        flags).toString());
                sb.append('}');
            }
            return sb.toString();
        } catch (Throwable th) {
            return "TIME_ERROR";
        }
    }
    
    private static final String ISO_8601_PATTERN_FOR_CONTACT = "yyyy-MM-dd";
    private static final SimpleDateFormat ISO_8601_FORMATTER_FOR_CONTACT = new SimpleDateFormat(ISO_8601_PATTERN_FOR_CONTACT);
    public static Date parseISO8601DateForContact(String stringDate) {
        Date date = null;
        try {
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
        return formatter.format(new Date(System.currentTimeMillis()));
    }
    
    /**
     * Returns label for the time.
     * 
     * @param time
     *            the time to return label for.
     * @return label for the time.
     */
    public static String getTimeLabel(long time) {
        return formatter.format(new Date(time));
    }
    
    /**
     * Returns a label for event happened <code>inTime</code> ms
     * 
     * @param intime
     *            when event shall be happened
     * @return label for event happened <code>inTime</code> ms
     */
    public static String getLabelForEventInTime(long inTime) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(inTime, 0, 0) + " at " + getTimeLabel(inTime + System.currentTimeMillis());
    }
    
}
