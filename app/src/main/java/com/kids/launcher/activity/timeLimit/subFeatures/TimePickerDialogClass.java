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

public class TimePickerDialogClass extends DialogFragment {
    private static final String TAG = "";

    public interface
    PassTimeInterface {
        void setTime(String time);
    }

    private EditText edtTime;
    private Button btnDone;
    TimePickerDialog picker;
    private PassTimeInterface passTimeInterface;
    Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_timelimit_picker, null);
        initViews(view);
        Log.d(TAG, "onCreateDialog: starting dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view).setTitle("Time in Minutes");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String minutes = edtTime.getText().toString();
                if (!minutes.equals("")) {
                    try {
                        passTimeInterface = (PassTimeInterface) getActivity();
                        passTimeInterface.setTime(minutes);
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
        edtTime = view.findViewById(R.id.edtTime);
        btnDone = view.findViewById(R.id.btnDone);


    }
}
