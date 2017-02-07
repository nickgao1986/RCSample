package com.example.nickgao.calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.nickgao.R;
import com.example.nickgao.database.RCMProvider;
import com.example.nickgao.database.UriHelper;
import com.example.nickgao.utils.CalendarDataStore;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by kasni.huang on 5/17/15.
 */
public class CalendarFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener, CalendarInterface{

    private ViewPager mPager;
    private CalendarPagerAdapter mPagerAdapter;
    private DateFormatter formatter;
//    private ContactItem mContactItem;
    private Activity mActivity;

    private String mUserId;
    private int mYear;
    private int mMonth;
    private CalendarContentObserver mCalendarObserver;
    private LoadingHandler mLoadingHandler = new LoadingHandler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        formatter = new DateFormatter(this.getResources());
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new CalendarPagerAdapter(getActivity().getSupportFragmentManager(), this, mUserId);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new simplePageChangeListener());
        mPager.setCurrentItem(getTodayMonthIndex());
        mCalendarObserver = new CalendarContentObserver(mLoadingHandler);
        mActivity.getContentResolver().registerContentObserver(
                UriHelper.getUri(RCMProvider.CALENDAR_INFO), true, mCalendarObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        updateMarkDay(0, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCalendarObserver != null) {
           // mActivity.getContentResolver().unregisterContentObserver(mCalendarObserver);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            /*CalendarPagerFragment fragment = mPagerAdapter.getFragment(getTodayMonthIndex());
            if (fragment != null) {
//                fragment.resetMark(mPreMarkDay);
                fragment.updateMark(mMarkDay);
            }*/
         }
    }

/*    private void getMarkDaysByTime() {
        Calendar today = Calendar.getInstance();
        String month = today.
    }*/

    // 对已经mark过的月份进行更新（重新mark）
    private void updateMarkDay(int userId, int year, int month) {
        String mon = "";
        if (month < 10) {
            mon = "0" + (month + 1);
        }
        ArrayList<Integer> months = CalendarDataStore.getAllDaysInMonth(mActivity, year + "," + mon);
        mPagerAdapter.setMarkDay(months);
        CalendarPagerFragment fragment = mPagerAdapter.getFragment(getYearMonthIndex(year, month));
        if (fragment != null) {
            fragment.updateMark(months);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity)context;
    }

    @Override
    public void onCellClick(View view) {
        LunarCalendar lc = (LunarCalendar) view.getTag();
        Calendar calendar = Calendar.getInstance();
        int gregorianDay = lc.getGregorianDate(Calendar.DAY_OF_MONTH)       ;
        calendar.set(Calendar.DAY_OF_MONTH, gregorianDay);
        int gregorianMonth = lc.getGregorianDate(Calendar.MONTH) + 1;
        calendar.set(Calendar.MONTH, gregorianMonth - 1);
        int gregorianYear = lc.getGregorianDate(Calendar.YEAR);
        calendar.set(Calendar.YEAR, gregorianYear);
        String weekDay = Utils.calendarToWeek(getActivity(), calendar);
        String time = String.valueOf(gregorianYear) + "-" + gregorianMonth + "-" + gregorianDay +
                "-" + " " + weekDay;
        Intent intent = new Intent(getActivity(), ScheduleActivity.class);
        intent.putExtra(ScheduleConstant.SCHEDULE_CHOOSED_DAY, calendar);
        intent.putExtra(ScheduleConstant.SCHEDULE_TIME, time);
//        intent.putExtra(ScheduleConstant.YEAR, gregorianYear);
//        intent.putExtra(ScheduleConstant.MONTH, gregorianMonth);
        String month = "";
        if (gregorianMonth < 10) {
            month = "0" + gregorianMonth;
        } else {
            month = String.valueOf(gregorianMonth);
        }
        String day = "";
        if (gregorianDay < 10) {
            day = "0" + gregorianDay;
        } else {
            day = String.valueOf(gregorianDay);
        }
        intent.putExtra(ScheduleConstant.DAY, gregorianYear + "," + month + "," + day);
        intent.putExtra(CalendarPagerFragment.USER_ID, mUserId);

        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        int offset = (year - LunarCalendar.getMinYear()) * 12 + monthOfYear;
        mPager.setCurrentItem(offset);
    }

    private int getTodayMonthIndex() {
        Calendar today = Calendar.getInstance();
        return (today.get(Calendar.YEAR) - LunarCalendar.getMinYear())
                * 12 + today.get(Calendar.MONTH);
    }

    private int getYearMonthIndex(int year, int month) {
        return (year - LunarCalendar.getMinYear()) * 12 + month;
    }

//    @Override
//    public void onRightButtonClicked() {
//        if (mPager != null) {
//            mPager.setCurrentItem(getTodayMonthIndex());
//        }
//    }
//
//    @Override
//    public void onLeftButtonClicked() {
//        mActivity.finish();
//    }



    private class simplePageChangeListener extends
            ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            StringBuilder title = new StringBuilder();
            int year = LunarCalendar.getMinYear() + (position / 12);
            title.append(year);
            title.append('-');
            int month = (position % 12) + 1;
            if (month < 10) {
                title.append('0');
            }
            title.append(month);
//            updateTitleBar(title.toString());
            mYear = year;
            mMonth = month;
            // 如果你不需要更新已经mark的日程，不需要调下面这个函数
            updateMarkDay(1008, year, month - 1);
        }
    }

    private final class LoadingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            updateMarkDay(0, mYear, mMonth);
        }
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
}
