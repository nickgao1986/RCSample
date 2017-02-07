package kankan.wheel.widget.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nickgao.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Day adapter
 * 
 */
public class DayArrayAdapter extends AbstractWheelTextAdapter {
	// Count of days to be shown
	private final int daysCount = 364;

	// Calendar
	Calendar calendar;

	/**
	 * Constructor
	 */
	public DayArrayAdapter(Context context, Calendar calendar) {
		super(context, R.layout.time2_day, NO_RESOURCE);
		this.calendar = calendar;

		setItemTextResource(R.id.time2_monthday);
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.DAY_OF_YEAR, index);

		View view = super.getItem(index, cachedView, parent);

		TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		monthday.setText(format.format(newCalendar.getTime()));
		monthday.setTextColor(0xFF111111);

		return view;
	}

	@Override
	public int getItemsCount() {
		return daysCount + 1;
	}

	@Override
	public CharSequence getItemText(int index) {
		int day = index;
		Calendar newCalendar = (Calendar) calendar.clone();
		newCalendar.add(Calendar.DAY_OF_YEAR, day);
		return new SimpleDateFormat("yyyy-MM-dd").format(newCalendar.getTime());
	}
}
