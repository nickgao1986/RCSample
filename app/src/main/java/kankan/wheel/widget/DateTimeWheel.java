package kankan.wheel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.nickgao.R;

import java.util.Calendar;

import kankan.wheel.widget.adapters.DayArrayAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;


public class DateTimeWheel extends LinearLayout {
	private Calendar calendar = Calendar.getInstance();
	
	private OnTimeChangedListener timeChangedListener = null;
	
	public DateTimeWheel(Context context) {
		this(context, null);
	}
	WheelView hours;
	WheelView day;
	WheelView mins;
	NumericWheelAdapter hourAdapter;
	NumericWheelAdapter minAdapter;
	DayArrayAdapter dayAdapter;
	public DateTimeWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOrientation(VERTICAL);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.date_time_wheel, this, true);
		
		hours = (WheelView) findViewById(R.id.hour);
		hourAdapter = new NumericWheelAdapter(context, 0, 23, "%02d");
		hourAdapter.setItemResource(R.layout.wheel_text_item);
		hourAdapter.setItemTextResource(R.id.text);
		hours.setViewAdapter(hourAdapter);
		hours.setCyclic(true);

		mins = (WheelView) findViewById(R.id.mins);
		minAdapter = new NumericWheelAdapter(context, 0, 59, "%02d");
		minAdapter.setItemResource(R.layout.wheel_text_item);
		minAdapter.setItemTextResource(R.id.text);
		mins.setViewAdapter(minAdapter);
		mins.setCyclic(true);

		// set current time
		hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
		mins.setCurrentItem(calendar.get(Calendar.MINUTE));
		dayAdapter=new DayArrayAdapter(context, calendar);
		day = (WheelView) findViewById(R.id.day);
		day.setViewAdapter(dayAdapter);
		day.setCyclic(false);
		day.setCurrentItem(0);
	}

	private void fireTimeChanged(long timeInMillis) {
		if (timeChangedListener != null) {
			timeChangedListener.onTimeChanged(timeInMillis);
		}
	}
	
	public void setOnTimeChangedListener(OnTimeChangedListener timeChangedListener) {
		this.timeChangedListener = timeChangedListener;
	}

	public interface OnTimeChangedListener {
		void onTimeChanged(long time);
	}

	public String getSelectedDate(){
		String d=dayAdapter.getItemText(day.getCurrentItem()).toString();
		String h=String.format("%02d", hours.getCurrentItem());
		String m=String.format("%02d", mins.getCurrentItem());
		return d+" "+h+":"+m;
	}
}
