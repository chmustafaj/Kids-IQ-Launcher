package com.kids.launcher.activity.timeLimit.subFeatures;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.kids.launcher.R;
import com.kids.launcher.system.UsageTime;

import java.util.Calendar;

public class UsageTimeDialog extends DialogFragment {
    private static final String TAG = "";

    public interface PassUsageTime {
        void addUsageTime(String usageTime);
    }

    private PassUsageTime passUsageTime;
    EditText t1, t2;
    Button done;
    String d1, d2, time1, time2;
    Spinner spinnerOne, spinnerTwo;
    TimePickerDialog picker;
    UsageTime usageTime = new UsageTime();
    String strUsageTime = "";
    String[] arrUsageTime;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.usage_time_popup, null);
        initViews(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view).setTitle("Usage Time");
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                t1.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                t2.setText(sHour + ":" + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (!t1.getText().toString().equals("") && !t2.getText().toString().equals("")) {
                time1 = t1.getText().toString();
                Log.d(TAG, "onClick: time 1 " + time1);
                time2 = t2.getText().toString();
                strUsageTime = time1 + "," + time2 + ",";
//                usageTime.timeOne=timeFromStringToInt(time1);
//                usageTime.timeTwo=timeFromStringToInt(time2);
                //Log.d(TAG, "onClick: spinner "+spinnerOne.toString());
                switch (spinnerOne.getSelectedItem().toString()) {
                    case "Select day":
                        strUsageTime = strUsageTime + "0,";
                        break;
                    case "Monday":
                        strUsageTime = strUsageTime + "2,";
                        break;
                    case "Tuesday":
                        strUsageTime = strUsageTime + "3,";
                        break;
                    case "Wednesday":
                        strUsageTime = strUsageTime + "4,";
                        break;
                    case "Thursday":
                        strUsageTime = strUsageTime + "5,";
                        break;
                    case "Friday":
                        strUsageTime = strUsageTime + "6,";
                        break;
                    case "Saturday":
                        strUsageTime = strUsageTime + "7,";
                        break;
                    case "Sunday":
                        strUsageTime = strUsageTime + "1,";
                        break;

                }
                switch (spinnerTwo.getSelectedItem().toString()) {
                    case "Select day":
                        arrUsageTime = strUsageTime.split(",");
                        Log.d(TAG, "onClick: arr usage time " + arrUsageTime[2]);
                        if (arrUsageTime[2].equals("0")) {
                            strUsageTime = strUsageTime + "0";
                        } else {
                            strUsageTime = strUsageTime + arrUsageTime[2];
                            Log.d(TAG, "onClick: automatically setting second time" + arrUsageTime[2]);
                        }
                        break;
                    case "Monday":
                        strUsageTime = strUsageTime + "2";
                        break;
                    case "Tuesday":
                        strUsageTime = strUsageTime + "3";
                        break;
                    case "Wednesday":
                        strUsageTime = strUsageTime + "4";
                        break;
                    case "Thursday":
                        strUsageTime = strUsageTime + "5";
                        break;
                    case "Friday":
                        strUsageTime = strUsageTime + "6";
                        break;
                    case "Saturday":
                        strUsageTime = strUsageTime + "7";
                        break;
                    case "Sunday":
                        strUsageTime = strUsageTime + "1";
                        break;

                }
                arrUsageTime = strUsageTime.split(",");  //array will be [time 1, time 2, day 1, day 2]
                if (t1.getText().toString().equals("") || t2.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter both times", Toast.LENGTH_SHORT).show();
                } else if (arrUsageTime[2].equals("0")) {
                    Toast.makeText(getActivity(), "Please enter a first day", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        passUsageTime = (PassUsageTime) getActivity();
                        passUsageTime.addUsageTime(strUsageTime);
                        dismiss();
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                }
            }
            //}
        });

        return builder.create();
    }

    void initViews(View view) {
        t1 = view.findViewById(R.id.timeOne);
        t2 = view.findViewById(R.id.timeTwo);
        spinnerOne = view.findViewById(R.id.firstDay);
        spinnerTwo = view.findViewById(R.id.secondDay);
        done = view.findViewById(R.id.btnDone);

    }

    int timeFromStringToInt(String time) {
        Log.d(TAG, "timeFromStringToInt:time  " + time);
        String[] timeArray = time.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int minutes = Integer.parseInt(timeArray[1]);
        return hour + (minutes / 60);
    }
}
