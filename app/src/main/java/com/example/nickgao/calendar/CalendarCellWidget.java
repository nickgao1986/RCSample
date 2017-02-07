package com.example.nickgao.calendar;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nickgao.R;

/**
 * Created by kasni.huang on 5/17/15.
 */
public class CalendarCellWidget extends LinearLayout {
    private TextView mTvGregorian;
    private TextView mTvLunar;
    private CalendarInterface mCalendarInterface;

    public void setCalendarInterface(CalendarInterface calendarInterface) {
        this.mCalendarInterface = calendarInterface;
    }

    public CalendarCellWidget(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_calendar_day_cell, this);
        this.setBackgroundResource(R.drawable.selector_calendar_normal);
        mTvGregorian = (TextView) findViewById(R.id.txtCellGregorian);
        mTvLunar = (TextView) findViewById(R.id.txtCellLunar);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarInterface.onCellClick(CalendarCellWidget.this);
            }
        });
    }

    public void updateBackground(int resId) {
        this.setBackgroundResource(resId);
    }

    public void setGregorianColor(int color) {
        mTvGregorian.setTextColor(color);
    }

    public void setLunarColor(int color) {
        mTvLunar.setTextColor(color);
    }
}
