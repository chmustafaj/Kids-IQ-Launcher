package com.kids.launcher.activity.timeLimit.subFeatures;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.kids.launcher.R;

public class BreakDialog extends DialogFragment {
    private static final String TAG = "";

    public interface
    PassBreakTimeInterface {
        void setBreakTime(int breakTime, int consecutiveTime);
    }

    private EditText edtBreakTime, edtMaxConsecutiveTime;
    private Button btnDone;
    TimePickerDialog picker;
    private PassBreakTimeInterface passBreakTimeInterface;
    Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_break_time_picker, null);
        initViews(view);
        Log.d(TAG, "onCreateDialog: starting dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view).setTitle("Break time in minutes");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String breakTime = edtBreakTime.getText().toString();
                String maxConsecutiveTime = edtMaxConsecutiveTime.getText().toString();
                if (!breakTime.equals("")) {
                    try {
                        passBreakTimeInterface = (BreakDialog.PassBreakTimeInterface) getActivity();
                        passBreakTimeInterface.setBreakTime(Integer.parseInt(breakTime), Integer.parseInt(maxConsecutiveTime));
                        Log.d(TAG, "onClick: setting time");
                        dismiss();
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                } else {
                    Log.d(TAG, "onClick: fields not filled");
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return builder.create();
    }

    void initViews(View view) {
        edtBreakTime = view.findViewById(R.id.edtBreakTime);
        edtMaxConsecutiveTime = view.findViewById(R.id.edtMaxConsecutiveTime);
        btnDone = view.findViewById(R.id.btnDone);


    }
}
