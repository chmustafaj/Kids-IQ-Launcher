package com.kids.launcher.activity.timeLimit.subFeatures;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.kids.launcher.R;

public class MaximumProfileTimeDialog extends DialogFragment {

    public interface
    PassMaximumProfileTimeInterface {
        void setMaximumProfileTime(int time);
    }

    private EditText edtTime;
    private Button btnDone;
    TimePickerDialog picker;
    private PassMaximumProfileTimeInterface passTimeInterface;
    Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_profile_time_picker, null);
        initViews(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view).setTitle("Time in Minutes");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String minutes = edtTime.getText().toString();
                if (!minutes.equals("")) {
                    try {
                        passTimeInterface = (PassMaximumProfileTimeInterface) getActivity();
                        passTimeInterface.setMaximumProfileTime(Integer.parseInt(minutes));
                        dismiss();
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return builder.create();
    }

    void initViews(View view) {
        edtTime = view.findViewById(R.id.edtMaxProfileTime);
        btnDone = view.findViewById(R.id.btnDone);


    }
}
