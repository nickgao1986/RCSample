package com.example.nickgao.calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.example.nickgao.R;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.utils.CalendarDataStore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by kasni.huang on 5/20/15.
 */
public class ScheduleActivity extends Activity implements DayView.MonthChangeListener, DayView.EventClickListener, DayView.EventLongPressListener, View.OnClickListener {
    private DayView mDayView;
    //TODO 示例用数组，正式数据时，可考虑不维护这个数组，直接在函数中生成新数组
    private List<DayViewEvent> mEvents;
    private String mTitleText;
    private Calendar mCalendar;
    private String mDay;
    private CalendarContentObserver mCalendarObserver;
    private LoadingHandler mLoadingHandler = new LoadingHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);
        Intent intent = getIntent();
        if (intent != null) {
            mTitleText = intent.getStringExtra(ScheduleConstant.SCHEDULE_TIME);
            mCalendar = (Calendar) intent.getSerializableExtra(ScheduleConstant.SCHEDULE_CHOOSED_DAY);
            mDay = intent.getStringExtra(ScheduleConstant.DAY);
        }
        mEvents = new ArrayList<>();
        mDayView = (DayView) findViewById(R.id.day_view);
        mDayView.setOnEventClickListener(this);
        mDayView.setMonthChangeListener(this);
        mDayView.setEventLongPressListener(this);
        setupDateTimeInterpreter(false);
        updateEvent();
        mCalendarObserver = new CalendarContentObserver(mLoadingHandler);
        getContentResolver().registerContentObserver(
                UriHelper.getUri(RCMProvider.CALENDAR_INFO), true, mCalendarObserver);
    }


    //TODO 示例
    @Override
    protected void onResume() {
        super.onResume();
        setDay(mCalendar);
        //TODO 示例用，正式数据时，请选择正确的位置调用该函数
//        updateEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCalendarObserver != null) {
            getContentResolver().unregisterContentObserver(mCalendarObserver);
        }
    }

    private void setDay(Calendar time) {
        if (time == null) {
            time = Calendar.getInstance();
        }
        time.set(Calendar.HOUR_OF_DAY, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        mDayView.setToday(time);
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mDayView.setDateTimeInterpreter(new DateTimeInterpreter() {

            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour + ":00";
            }
        });
    }

    @Override
    public List<DayViewEvent> onMonthChange(int newYear, int newMonth) {
        return getEvents();
    }

    //TODO, 获取事件接口
    private List<DayViewEvent> getEvents() {
        // TODO 底下为生成日程的代码示例
//        List<WeekViewEvent> events = new ArrayList<>();
        CalendarDataStore dataStore = new CalendarDataStore();
        List<CalendarDataStore.CalendarData> datas = dataStore.getCalendarDataAccordingDay(this, mDay);
        if (datas != null) {
            int i = 0;
            for (CalendarDataStore.CalendarData data : datas) {
                Calendar startTime = Calendar.getInstance();
                String day[] = data.day.split(",");
                String sTime[] = data.start_time.split(":");
                String eTime[] = data.end_time.split(":");
                startTime.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day[2]));
                startTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(sTime[0]));
                startTime.set(Calendar.MINUTE, Integer.valueOf(sTime[1]));
                startTime.set(Calendar.MONTH, Integer.valueOf(day[1]) - 1);
                startTime.set(Calendar.YEAR, Integer.valueOf(day[0]));
                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR_OF_DAY, Integer.valueOf(eTime[0]) - Integer.valueOf(sTime[0]));
                endTime.set(Calendar.MINUTE, Integer.valueOf(eTime[1]));
                String title = mDay + " " + data.start_time + "-" + data.end_time + "  " + data.location;
                DayViewEvent event = new DayViewEvent(1, title, data.event, startTime, endTime);
                event.setColor(i % 2 == 0 ? getResources().getColor(R.color.event_color_01) : getResources().getColor(R.color.event_color_02));
//        events.add(event);
                mEvents.add(event);
                i++;
            }
        }
        return mEvents;
//        mDayView.notifyDatasetChanged();
//        mDayView.setRefreshEvents();
//        mDayView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }

    //TODO 更新日程事件接口
    private void updateEvent() {
        mDayView.setRefreshEvents();
        mDayView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    }

    private String getEventTitle(Calendar time) {
        return String.format("打麻将 %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(DayViewEvent event, RectF eventRect) {
        /*Intent intent = new Intent(this, AddScheduleActivity.class);
        intent.putExtra(ScheduleConstant.SCHEDULE_START_TIME, event.getStartTime());
        intent.putExtra(ScheduleConstant.SCHEDULE_END_TIME, event.getEndTime());
        intent.putExtra(ScheduleConstant.SCHEDULE_EVENT_TITLE, event.getName());
        intent.putExtra(ScheduleConstant.SCHEDULE_EVENT_DETAIL, event.getDetail());
        intent.putExtra(ScheduleConstant.SCHEDULE_MODIFY_TYPE, ScheduleConstant.TYPE_MODIFY_SCHEDULE);
        intent.putExtra(ScheduleConstant.SCHEDULE_CHOOSED_DAY, mCalendar);
        startActivity(intent);*/
    }

    @Override
    public void onEventLongPress(DayViewEvent event, RectF eventRect) {
    }

    @Override
    public void onClick(View v) {
    }

    private class CalendarContentObserver extends ContentObserver {

        private Handler handler;

        public CalendarContentObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            this.handler.sendEmptyMessage(0);
        }

    }

    private final class LoadingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            mDayView.notifyDatasetChanged();
        }
    }
}

