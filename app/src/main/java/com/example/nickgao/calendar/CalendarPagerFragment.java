package com.example.nickgao.calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.nickgao.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kasni.huang on 5/17/15.
 */
public class CalendarPagerFragment extends Fragment {

	public static final String ARG_PAGE = "page";
	public static final String MARK_DAY = "mark_day";
	public static final String USER_ID = "user_id";

	private int mMonthIndex;
	private int mUserId;
	private CalendarInterface mCalendarInterface;
	private HashMap<Integer, CalendarCellWidget> mDayCells;
	private ArrayList<Integer> mMarkDay;

	public static CalendarPagerFragment create(int monthIndex, int userId){
		CalendarPagerFragment fragment = new CalendarPagerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, monthIndex);
		args.putInt(USER_ID, userId);
		fragment.setArguments(args);
		return fragment;
	}

	public static CalendarPagerFragment create(int monthIndex, int userId, ArrayList<Integer> markDay){
		CalendarPagerFragment fragment = new CalendarPagerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, monthIndex);
		args.putInt(USER_ID, userId);
		args.putIntegerArrayList(MARK_DAY, markDay);
		fragment.setArguments(args);
		return fragment;
	}

	public void setCalendarInterface(CalendarInterface calendarInterface) {
		this.mCalendarInterface = calendarInterface;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mMonthIndex = bundle.getInt(ARG_PAGE);
		mUserId = bundle.getInt(USER_ID);
		mMarkDay = bundle.getIntegerArrayList(MARK_DAY);
//		generateData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout linearLayout;
		CalendarCellWidget cellView;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
		View view = inflater.inflate(R.layout.view_calendar_table, container, false);
		LinearLayout dayLayout = (LinearLayout) view.findViewById(R.id.layout_day);
		dayLayout.setOrientation(LinearLayout.VERTICAL);
		CalendarTableCellProvider adpt = new CalendarTableCellProvider(getResources(), mMonthIndex);
		if (mDayCells == null) {
			mDayCells = new HashMap<>();
		} else {
			mDayCells.clear();
		}
		for(int row = 0; row < 6; row++){
			linearLayout = new LinearLayout(getActivity());
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setLayoutParams(params);
			for(int column = 0; column < 8; column++){
				cellView = (CalendarCellWidget) adpt.getView(mCalendarInterface, getActivity(), row * 8 + column, inflater, linearLayout);
//				cellView.setOnFocusChangeListener((View.OnFocusChangeListener)container.getContext());
				if (cellView != null) {
					LunarCalendar lunarCalendar = (LunarCalendar) cellView.getTag();
					if (lunarCalendar != null) {
						if (lunarCalendar.getGregorianDate(Calendar.MONTH) == mMonthIndex % 12) {
							mDayCells.put(lunarCalendar.getGregorianDate(Calendar.DAY_OF_MONTH), cellView);
						}
					}
					cellView.setGravity(Gravity.CENTER);
					cellView.setLayoutParams(params);
					linearLayout.addView(cellView);
				}
			}
			dayLayout.addView(linearLayout);
		}

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		updateMark(mMarkDay);
	}

	public void updateMark(ArrayList<Integer> markDay) {
		if (mDayCells != null && mDayCells.size() > 0) {
			for (Object o : mDayCells.entrySet()) {
				Map.Entry entry = (Map.Entry) o;
				CalendarCellWidget val = (CalendarCellWidget) entry.getValue();
				val.setBackgroundResource(R.drawable.selector_calendar_normal);
			}
            if (markDay != null && markDay.size() > 0) {
                for (int aMarkDay : markDay) {
                    CalendarCellWidget cell = mDayCells.get(aMarkDay);
                    cell.setBackgroundResource(R.drawable.selector_calendar_mark);
                }
            }
		}
	}

	private void resetMark(int[] markDay) {
		if (markDay != null && markDay.length > 0 && mDayCells != null && mDayCells.size() > 0) {
			for (int aMarkDay : markDay) {
				CalendarCellWidget cell = mDayCells.get(aMarkDay);
				cell.setBackgroundResource(R.drawable.selector_calendar_normal);
			}
		}
	}
}

