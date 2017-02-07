package com.example.nickgao.utils;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;

import com.example.nickgao.R;
import com.example.nickgao.database.CurrentUserSettings;
import com.example.nickgao.database.RCMDataStore;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.RCMProviderHelper;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.logging.MktLog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nick.gao on 2/7/17.
 */

public class CalendarDataStore {

    private static final String TAG = "CalendarDataStore";
    private static final int WAITING_MINUTE = 2;
    public class CalendarData {
        public String day;
        public String end_time;
        public String event;
        public String location;
        public String start_time;
        public String month;
        public boolean isCallReminder;
        @Override
        public String toString() {
            return "CalendarData{" +
                    "day='" + day + '\'' +
                    ", end_time='" + end_time + '\'' +
                    ", event='" + event + '\'' +
                    ", month='" + month + '\'' +
                    ", location='" + location + '\'' +
                    ", start_time='" + start_time + '\'' +
                    '}';
        }
    }

    public CalendarData getCalendarData() {
        return new CalendarData();
    }

    public ArrayList<CalendarData> getCalendarData(Context context) {
        long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                UriHelper.getUri(RCMProvider.CALENDAR_INFO, mailboxId),
                null,
                null,
                null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        CalendarData record = new CalendarData();
        cursor.moveToPosition(-1);
        ArrayList<CalendarData> mCalendarInfoList = new ArrayList<CalendarData>();
        while (cursor.moveToNext()) {
            record.day = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.DAY));
            record.location = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.LOCATION));
            record.event = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.MESSAGE));
            record.start_time = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.START_TIME));
            record.end_time =  cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.END_TIME));
            record.month =  cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.MONTH));
            MktLog.i(TAG,"======record="+record);
            mCalendarInfoList.add(record);
        }
        cursor.close();
        return mCalendarInfoList;
    }


    public ArrayList<CalendarData> getCalendarDataAccordingDay(Context context, String day) {
        ContentResolver resolver = context.getContentResolver();
        long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        Cursor cursor = resolver.query(
                UriHelper.getUri(RCMProvider.CALENDAR_INFO, mailboxId),
                null,
                RCMDataStore.CalendarTable.DAY + " = ? ",
                new String[] { day }, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        CalendarData record = new CalendarData();
        cursor.moveToPosition(-1);
        ArrayList<CalendarData> mCalendarInfoList = new ArrayList<CalendarData>();
        while (cursor.moveToNext()) {
            record.day = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.DAY));
            record.location = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.LOCATION));
            record.event = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.MESSAGE));
            record.start_time = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.START_TIME));
            record.end_time =  cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.END_TIME));
            record.month =  cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.MONTH));
            mCalendarInfoList.add(record);
        }

        return mCalendarInfoList;
    }


    public void saveCalendarData(Context context,CalendarData calendarData) {
        long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        ContentValues cv = new ContentValues();
        cv.put(RCMDataStore.CalendarTable.MAILBOX_ID, mailboxId);
        cv.put(RCMDataStore.CalendarTable.DAY, calendarData.day);
        cv.put(RCMDataStore.CalendarTable.START_TIME, calendarData.start_time);
        cv.put(RCMDataStore.CalendarTable.END_TIME, calendarData.end_time);
        cv.put(RCMDataStore.CalendarTable.LOCATION, calendarData.location);
        cv.put(RCMDataStore.CalendarTable.MESSAGE, calendarData.event);
        cv.put(RCMDataStore.CalendarTable.MONTH, calendarData.month);
        cv.put(RCMDataStore.CalendarTable.IS_CALL_REMINDER, calendarData.isCallReminder ? 1 : 0);
        Uri uri = context.getContentResolver().insert(UriHelper.getUri(RCMProvider.CALENDAR_INFO), cv);
        MktLog.i(TAG,"======uri="+uri);
    }

    public void saveCalendarListData(Context context,ArrayList<CalendarData> calendarDataList) {
        long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        try{
            for (int i = 0; i < calendarDataList.size(); i++) {
                CalendarData calendarData = calendarDataList.get(i);
                ContentValues cv = new ContentValues();
                cv.put(RCMDataStore.CalendarTable.MAILBOX_ID, mailboxId);
                cv.put(RCMDataStore.CalendarTable.DAY, calendarData.day);
                cv.put(RCMDataStore.CalendarTable.START_TIME, calendarData.start_time);
                cv.put(RCMDataStore.CalendarTable.END_TIME, calendarData.end_time);
                cv.put(RCMDataStore.CalendarTable.LOCATION, calendarData.location);
                cv.put(RCMDataStore.CalendarTable.MESSAGE, calendarData.event);
                cv.put(RCMDataStore.CalendarTable.MONTH, calendarData.month);
                ops.add(ContentProviderOperation.newInsert(UriHelper.getUri(RCMProvider.CALENDAR_INFO)).withValues(cv).build());
            }

            context.getContentResolver().applyBatch(RCMProvider.AUTHORITY, ops);
        } catch (java.lang.Throwable error) {
            MktLog.e(TAG, "======error="+error);
        }
    }


    public void testData(Context context) {
        ArrayList<CalendarData> calendarDatas = new ArrayList<CalendarData>();
        CalendarData calendarData = new CalendarData();
        calendarData.day = "2015,8,20";
        calendarData.month = "2015,8";
        calendarData.start_time = "20:00";
        calendarData.end_time = "21:00";
        calendarData.event = "attend meeting";
        calendarData.location = "beijing";
        calendarDatas.add(calendarData);
        saveCalendarData(context,calendarData);

        CalendarData calendarData1 = new CalendarData();
        calendarData1.day = "2015,7,22";
        calendarData1.month = "2015,7";
        calendarData1.start_time = "20:00";
        calendarData1.end_time = "21:00";
        calendarData1.event = "watch movie";
        calendarData1.location = "Film station";
        calendarDatas.add(calendarData1);
        saveCalendarData(context, calendarData1);


        CalendarData calendarData2 = new CalendarData();
        calendarData2.day = "2015,8,25";
        calendarData2.month = "2015,8";
        calendarData2.start_time = "20:00";
        calendarData2.end_time = "21:00";
        calendarData2.event = "go home";
        calendarData2.location = "XX";
        calendarDatas.add(calendarData2);
        saveCalendarData(context, calendarData2);

        long mailbox_id = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        //saveCalendarListData(context,calendarDatas);
    }

    public static void setCallReminder(Context context, String phoneNumber) {
        CalendarDataStore dataStore = new CalendarDataStore();
        CalendarData calendarData = dataStore.getCalendarData();

        calendarData.event = "call:"+phoneNumber;
        calendarData.location = "";

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        MktLog.i(TAG,"====minute="+minute+"hour="+hour+"day="+day+"month="+(month+1)+"phoneNumber="+phoneNumber);
        String mon = "";
        if (month < 10) {
            mon = "0" + (month + 1);
        } else {
            mon = "" + (month + 1);
        }
        String d = "";
        if (day < 10) {
            d = "0" + day;
        } else {
            d = "" + day;
        }
        calendarData.day = year+","+mon+","+d;
        calendarData.month = year+","+mon;
        calendarData.start_time = hour+":"+(minute+WAITING_MINUTE) ;
        calendarData.end_time = (hour + 1)+":"+(minute+WAITING_MINUTE);
        calendarData.isCallReminder = true;
        dataStore.saveCalendarData(context, calendarData);
    }


    public static boolean isNeedReminder(CalendarData calendarData) {
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String startTime = calendarData.start_time;
        String[] array = startTime.split(":");

        int recordMinute = Integer.valueOf(array[1]);
        int recordHour = Integer.valueOf(array[0]);

        if(hour >= recordHour) {
            return true;
        }else if(minute >= recordMinute) {
            return true;
        }
        return false;
    }

    public  void checkNeedReminder(final Context context) {
        CalendarData calendarData = getCalendarCallData(context);

        if(calendarData != null) {
            boolean isNeedReminder = isNeedReminder(calendarData);
            MktLog.i(TAG,"==============checkNeedReminder="+isNeedReminder);
            if(isNeedReminder) {
                String message = calendarData.event;
                final String phoneNumber = message.substring("call:".length(),message.length());
                MktLog.i(TAG, "====message=" + message + "Phonenumber=" + phoneNumber);
                updateMessageIsCalendarField(context);
//                RcAlertDialog .getBuilder(context).setTitle(R.string.CallReminder)
//                        .setMessage(context.getResources().getString(R.string.callRemindertext, phoneNumber)).setIcon(R.drawable.symbol_exclamation)
//                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                RingOutAgent mRingOutAgent = new RingOutAgent((Activity) context);
//                                mRingOutAgent.call(phoneNumber, null);
//                                dialog.dismiss();
//                            }
//                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(context,RCMConstants.ALL_DIALOGS_THEME_ID);
                builder.setTitle(R.string.CallReminder)
                        .setMessage(context.getResources().getString(R.string.callRemindertext, phoneNumber)).setIcon(R.drawable.symbol_exclamation)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
//                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            }
        }
    }



    public CalendarData getCalendarCallData(Context context) {
        long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                UriHelper.getUri(RCMProvider.CALENDAR_INFO, mailboxId),
                null,
                RCMDataStore.CalendarTable.IS_CALL_REMINDER + " = ? ",
                new String[]{ "1" }, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        CalendarData record = new CalendarData();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            record.day = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.DAY));
            record.location = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.LOCATION));
            record.event = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.MESSAGE));
            record.start_time = cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.START_TIME));
            record.end_time =  cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.END_TIME));
            record.month =  cursor.getString(cursor.getColumnIndex(RCMDataStore.CalendarTable.MONTH));
        }
        cursor.close();
        MktLog.i(TAG, "======getCalendarCallData record.event="+record.event);
        return record;
    }

    public static String removeZero(String str){
        if(str.startsWith("0")) {
            str = str.substring(1,str.length());
        }
        return str;
    }




    public ArrayList<String> getAllDaysInOneMonth(Context context,String month) {
        ArrayList<String> dayArray = new ArrayList<String>();
        long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                UriHelper.getUri(RCMProvider.CALENDAR_INFO, mailboxId),
                new String[]{RCMDataStore.CalendarTable.DAY},
                RCMDataStore.CalendarTable.MONTH + " = ? ",
                new String[] { month }, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        CalendarData record = new CalendarData();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            dayArray.add(cursor.getString(0));

        }
        cursor.close();
        MktLog.i(TAG, "======getAllDaysInOneMonth dayArray size="+dayArray.size());
        for(int i=0;i<dayArray.size();i++) {
            String str = dayArray.get(i);
            MktLog.i(TAG,"=====str="+str);
        }
        return dayArray;
    }

    public static ArrayList<Integer> getAllDaysInMonth(Context context,String month) {
        ArrayList<Integer> dayArray = new ArrayList<Integer>();
        long mailboxId = CurrentUserSettings.getSettings(context).getCurrentMailboxId();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                UriHelper.getUri(RCMProvider.CALENDAR_INFO, mailboxId),
                new String[]{RCMDataStore.CalendarTable.DAY},
                RCMDataStore.CalendarTable.MONTH + " = ? ",
                new String[] { month }, null);

        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String day = cursor.getString(0);
            String[] days = day.split(",");
            dayArray.add(Integer.valueOf(days[days.length - 1]));

        }
        cursor.close();
        MktLog.i(TAG, "======getAllDaysInOneMonth dayArray size="+dayArray.size());
        for(int i=0;i<dayArray.size();i++) {
            int str = dayArray.get(i);
            MktLog.i(TAG,"=====str="+str);
        }
        return dayArray;
    }

    public void updateMessageIsCalendarField(Context context) {
        RCMProviderHelper.updateSingleValue(context, RCMProvider.CALENDAR_INFO,
                RCMDataStore.CalendarTable.IS_CALL_REMINDER, String.valueOf(0), null);
    }

}
