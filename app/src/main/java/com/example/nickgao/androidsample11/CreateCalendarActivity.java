package com.example.nickgao.androidsample11;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nickgao.R;
import com.example.nickgao.calendar.DateTimePickFragment;
import com.example.nickgao.utils.CalendarDataStore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nick.gao on 2/7/17.
 */

public class CreateCalendarActivity extends FragmentActivity implements View.OnClickListener{

    private View startDateRow;
    private View endDateRow;
    private TextView startDate;
    private TextView endDate;
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private EditText eventName;
    private EditText locaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_pick);
        startDateRow=findViewById(R.id.start_date_row);
        endDateRow=findViewById(R.id.end_date_row);
        startDate= (TextView) findViewById(R.id.start_date);
        startDate.setText(simpleDateFormat.format(new Date()));
        endDate= (TextView) findViewById(R.id.end_date);
        endDate.setText(simpleDateFormat.format(new Date()));
        startDateRow.setOnClickListener(this);
        endDateRow.setOnClickListener(this);
//        ((HeaderViewBase) findViewById(R.id.create_new_message_top)).setButtonsClickCallback(this);
        eventName= (EditText) findViewById(R.id.event_name);
        locaction= (EditText) findViewById(R.id.location);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_date_row:
                DateTimePickFragment startDialog=new DateTimePickFragment();
                startDialog.setDateTimeInterface(new DateTimePickFragment.DateTimeInterface() {
                    @Override
                    public void setDateTime(String s) {
                        startDate.setText(s);
                    }
                });
                startDialog.show(getSupportFragmentManager(),"start");
                break;
            case R.id.end_date_row:
                DateTimePickFragment endDialog=new DateTimePickFragment();
                endDialog.setDateTimeInterface(new DateTimePickFragment.DateTimeInterface() {
                    @Override
                    public void setDateTime(String s) {
                        endDate.setText(s);
                    }
                });
                endDialog.show(getSupportFragmentManager(),"end");
                break;
        }
    }


    public void onRightButtonClicked() {
        CalendarDataStore dataStore = new CalendarDataStore();
        CalendarDataStore.CalendarData data= dataStore.getCalendarData();
        data.event=eventName.getText().toString();
        data.location=locaction.getText().toString();
        String start;
        String end;
        String month;
        String day;
        Date startDate=null;
        Date endDate=null;
        try {
            startDate=simpleDateFormat.parse(this.startDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endDate=simpleDateFormat.parse(this.endDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(startDate!=null) {
            data.day = new SimpleDateFormat("yyyy,MM,dd").format(startDate);
            data.month =new SimpleDateFormat("yyyy,MM").format(startDate);
            data.start_time = new SimpleDateFormat("HH:mm").format(startDate);
        }

        if(endDate!=null){
            data.end_time =  new SimpleDateFormat("HH:mm").format(endDate);
        }
        dataStore.saveCalendarData(this, data);

        StringBuilder sb=new StringBuilder();
        sb.append("Start Date:").append(this.startDate.getText()).append("\n");
        sb.append("End Date:").append(this.endDate.getText()).append("\n");
        sb.append("Location:").append(data.location).append("\n");
        sb.append("Event:").append(data.event);


        Intent i=new Intent();
        i.putExtra("text", sb.toString());
        setResult(RESULT_OK, i);
        finish();
    }


}
