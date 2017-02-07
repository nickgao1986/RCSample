package com.example.nickgao.calendar;

/**
 * Created by kasni.huang on 5/17/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarPagerAdapter extends FragmentStatePagerAdapter {
	private CalendarInterface mCalendarInterface;
	private HashMap<Integer, CalendarPagerFragment> mPreferenceMap = new HashMap<>();
	private String mUserId;
	private ArrayList<Integer> mMarkDay;

	public CalendarPagerAdapter(FragmentManager fm, CalendarInterface calendarInterface, String userId) {
		super(fm);
		this.mCalendarInterface = calendarInterface;
		this.mUserId = userId;
	}

	public void setMarkDay(ArrayList<Integer> markDay) {
		this.mMarkDay = markDay;
	}

	@Override
	public Fragment getItem(int position) {
		Log.i("====", "position:" + position);
		CalendarPagerFragment fragment = CalendarPagerFragment.create(position, 0, mMarkDay);
//		CalendarPagerFragment fragment = CalendarPagerFragment.create(position, 0);
		fragment.setCalendarInterface(mCalendarInterface);
		/*if (mMarkMonth == position && mIsFirstCreate) {
			Log.i("====", "mark:" + position);
			fragment.updateMark(mMarkDay);
			mIsFirstCreate = false;
		}*/
		mPreferenceMap.put(position, fragment);
		return fragment;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.i("====", "position1:" + position);
		return super.instantiateItem(container, position);
	}

	@Override
	public int getCount() {
		int years = LunarCalendar.getMaxYear() - LunarCalendar.getMinYear();
		return years * 12;
	}

	public CalendarPagerFragment getFragment(int position) {
		return mPreferenceMap.get(position);
	}
/*	public void updateMark(int[] markDay) {
		if (mFragment != null) {
			mFragment.updateMark(markDay);
		}
	}*/

}
