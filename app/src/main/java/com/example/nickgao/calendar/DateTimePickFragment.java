package com.example.nickgao.calendar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.nickgao.R;

import kankan.wheel.widget.DateTimeWheel;

/**
 * Created by nick.gao on 2/7/17.
 */

public class DateTimePickFragment extends DialogFragment {



    public DateTimePickFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    DateTimeWheel dateTimeWheel;
    Button okBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_time_pick, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dateTimeWheel= (DateTimeWheel) view.findViewById(R.id.datepicker);
        okBtn= (Button) view.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=dateTimeWheel.getSelectedDate();
                if(dateTimeInterface!=null){
                    dateTimeInterface.setDateTime(s);
                }
                dismiss();
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    DateTimeInterface dateTimeInterface;
    public void setDateTimeInterface(DateTimeInterface inteface){
        dateTimeInterface=inteface;
    }
    public interface DateTimeInterface{
        public void setDateTime(String s);
    }
}
