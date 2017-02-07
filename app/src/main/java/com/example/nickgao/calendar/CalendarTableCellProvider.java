package com.example.nickgao.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nickgao.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by kasni.huang on 5/18/15.
 */
public class CalendarTableCellProvider {

	private long firstDayMillis = 0;
	private int solarTerm1 = 0;
	private int solarTerm2 = 0;
	private DateFormatter fomater;
	
	public CalendarTableCellProvider(Resources resources, int monthIndex){
		int year = LunarCalendar.getMinYear() + (monthIndex / 12);
		int month = monthIndex % 12;
		Calendar date = new GregorianCalendar(year, month, 1);
		int offset = 1 - date.get(Calendar.DAY_OF_WEEK);
		date.add(Calendar.DAY_OF_MONTH, offset);
		firstDayMillis = date.getTimeInMillis();
		solarTerm1 = LunarCalendar.getSolarTerm(year, month * 2 + 1);
		solarTerm2 = LunarCalendar.getSolarTerm(year, month * 2 + 2);
		fomater = new DateFormatter(resources);
	}
	
	public View getView(CalendarInterface calendarInterface, Context context, int position, LayoutInflater inflater, ViewGroup container) {
		CalendarCellWidget rootView = null;
		LunarCalendar date = new LunarCalendar(firstDayMillis +
				(position - (position / 8) - 1) * LunarCalendar.DAY_MILLIS);
		if (position % 8 == 0) {
			return rootView;
		}
		boolean isFestival = false, isSolarTerm = false;
		rootView = new CalendarCellWidget(context);
		rootView.setCalendarInterface(calendarInterface);
		TextView txtCellGregorian = (TextView)rootView.findViewById(R.id.txtCellGregorian);
		TextView txtCellLunar = (TextView)rootView.findViewById(R.id.txtCellLunar);
		int gregorianDay = date.getGregorianDate(Calendar.DAY_OF_MONTH);
		boolean isOutOfRange = ((position % 8 != 0) &&
				(position < 8 && gregorianDay > 8) || (position > 8 && gregorianDay < position - 7 - 6));
		txtCellGregorian.setText(String.valueOf(gregorianDay));

		int index = date.getLunarFestival();
		if (index >= 0){
			txtCellLunar.setText(fomater.getLunarFestivalName(index));
			isFestival = true;
		}else{
			index = date.getGregorianFestival();
			if (index >= 0){
				txtCellLunar.setText(fomater.getGregorianFestivalName(index));
				isFestival = true;
			}else if (date.getLunar(LunarCalendar.LUNAR_DAY) == 1){
				txtCellLunar.setText(fomater.getMonthName(date));
			}else if(!isOutOfRange && gregorianDay == solarTerm1){
				txtCellLunar.setText(fomater.getSolarTermName(date.getGregorianDate(Calendar.MONTH) * 2));
				isSolarTerm = true;
			}else if(!isOutOfRange && gregorianDay == solarTerm2){
				txtCellLunar.setText(fomater.getSolarTermName(date.getGregorianDate(Calendar.MONTH) * 2 + 1));
				isSolarTerm = true;
			}else{
				txtCellLunar.setText(fomater.getDayName(date));
			}
		}
		
		Resources resources = container.getResources();
		if (isOutOfRange){
			rootView.setBackgroundResource(R.drawable.selector_calendar_outrange);
			txtCellGregorian.setTextColor(resources.getColor(R.color.us_palette_black_lighter_1));
			txtCellLunar.setTextColor(resources.getColor(R.color.us_palette_black_lighter_1));
		}else if(isFestival){
			txtCellLunar.setTextColor(resources.getColor(R.color.color_calendar_festival));
		}else if(isSolarTerm){
			txtCellLunar.setTextColor(resources.getColor(R.color.color_calendar_solarterm));
		}
		if (date.isToday()){
			txtCellGregorian.setTextColor(resources.getColor(android.R.color.holo_red_dark));
			txtCellLunar.setTextColor(resources.getColor(android.R.color.holo_red_dark));
			txtCellGregorian.setTextSize(24);
		}
		rootView.setTag(date);
		return rootView;
	}

}

