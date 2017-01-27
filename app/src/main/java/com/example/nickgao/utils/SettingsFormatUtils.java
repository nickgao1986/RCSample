package com.example.nickgao.utils;


import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

import java.util.TimeZone;

public class SettingsFormatUtils {
    private static final String TAG = "[RC]SettingsFormatUtils";
    private static String mTimeFormatString = "h:mm a";
    private static String mDateFormatString = "MM/dd/yyyy";
    
    private static TimeZone mTimeZone = java.util.Calendar.getInstance().getTimeZone();

    private Context mContext;
    private DateTimeContentObserver mDateTimeContentObserver;

    public void destroy() {
        if (mDateTimeContentObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(mDateTimeContentObserver);
        }
        mDateTimeContentObserver = null;
        mContext = null;
    }
    
    public SettingsFormatUtils(Context context) {
        mContext = context;
        mDateTimeContentObserver = new DateTimeContentObserver(null);
        mContext.getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.DATE_FORMAT),
                true, mDateTimeContentObserver);

        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(
                Settings.System.TIME_12_24),
                true, mDateTimeContentObserver);

     //   updateData(mContext);
    }

    public static String getTimeFormat() {
        return mTimeFormatString;
    }

    public static String getDateFormat() {
        return mDateFormatString;
    }
    
    private class DateTimeContentObserver extends ContentObserver {

        public DateTimeContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean arg0) {
            super.onChange(arg0);
       //     updateData(mContext);
        }
    }

//    public static void updateData(Context context) {
//        if (DateFormat.is24HourFormat(context)) {
//            mTimeFormatString = "H:mm";
//        } else {
//            mTimeFormatString = "h:mm a";
//        }
//        String format = getDateFormatStringForSetting(context, 
//        		Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT));
//        
//        if(LogSettings.ENGINEERING) {
//        	MktLog.d(TAG, "Format : " + format);
//        }
//        
//        if(!TextUtils.isEmpty(format)) {
//        	mDateFormatString = format;
//        }
//        
//        mTimeZone = java.util.Calendar.getInstance().getTimeZone();
//
//    }
    
//    private static String getDateFormatStringForSetting(Context context, String value) {
//    	
//    	if(LogSettings.ENGINEERING) {
//    		MktLog.d(TAG, "value : " + value);
//    	}
//    	
//        if (value != null) {
//            int month 	= value.indexOf('M');
//            int day 	= value.indexOf('d');
//            int year 	= value.indexOf('y');
//
//            if (month >= 0 && day >= 0 && year >= 0) {
//            	String template = "";
//            	if(!TextUtils.isEmpty(value) && value.contains("EE") && value.contains(" ")) {
//            		if(month < day && day < year) {
//            			template = context.getString(R.string.rcm_numeric_date_space_dot_template);
//            		} else {
//            			template = context.getString(R.string.rcm_numeric_date_space_template);
//            		}
//            	} else {
//            		template = context.getString(R.string.rcm_numeric_date_template);
//            	}
//                
//                if (year < month && year < day) {
//                    if (month < day) {
//                        value = String.format(template, "yyyy", "MM", "dd");
//                    } else {
//                        value = String.format(template, "yyyy", "dd", "MM");
//                    }
//                } else if (month < day) {
//                    if (day < year) {
//                        value = String.format(template, "MM", "dd", "yyyy");
//                    } else { // unlikely
//                        value = String.format(template, "MM", "yyyy", "dd");
//                    }
//                } else { // day < month
//                    if (month < year) {
//                        value = String.format(template, "dd", "MM", "yyyy");
//                    } else { // unlikely
//                        value = String.format(template, "dd", "yyyy", "MM");
//                    }
//                }
//
//                return value;
//            }
//        }
//
//        /*
//         * The setting is not set; use the default.
//         * We use a resource string here instead of just DateFormat.SHORT
//         * so that we get a four-digit year instead a two-digit year.
//         */
//        value = context.getString(R.string.rcm_numeric_date_format);
//        return value;
//    }
    
}
