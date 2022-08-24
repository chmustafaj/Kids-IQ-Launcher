package com.kids.launcher.activity.timeLimit.subFeatures;

import android.app.Dialog;
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

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kids.launcher.R;

public class DailyTimeChargeDialog extends DialogFragment {
    private static final String TAG = "";

    public interface
    PassDailyTimeChargeInterface {
        void setDailyChargeTime(int time);
    }

    private EditText chargeTime;
    private Button btnDone;
    private PassDailyTimeChargeInterface passDailyTimeChargeInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_daily_time_charge, null);
        initViews(view);
        Log.d(TAG, "onCreateDialog: starting dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view).setTitle("Time in Minutes");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String minutes = chargeTime.getText().toString();
                if (!minutes.equals("")) {
                    try {
                        passDailyTimeChargeInterface = (PassDailyTimeChargeInterface) getActivity();
                        passDailyTimeChargeInterface.setDailyChargeTime(Integer.parseInt(minutes));
                        Log.d(TAG, "onClick: setting time");
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
        chargeTime = view.findViewById(R.id.edtDailyTimeCharge);
        btnDone = view.findViewById(R.id.btnDone);
    }
}
