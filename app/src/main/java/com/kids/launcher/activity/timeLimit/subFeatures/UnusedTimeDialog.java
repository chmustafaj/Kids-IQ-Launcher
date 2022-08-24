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

public class UnusedTimeDialog extends DialogFragment {
    private static final String TAG = "";
    Context context;
    private EditText chargeTime;
    private Button btnDone;
    private SwitchMaterial switchMaterial;
    private PassUnusedTimeBoolInterface passUnusedTimeBoolInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_unused_time, null);
        initViews(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view).setTitle("");
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    try {
                        passUnusedTimeBoolInterface = (PassUnusedTimeBoolInterface) getActivity();
                        passUnusedTimeBoolInterface.setUnusedTimeBool(switchMaterial.isChecked());
                        dismiss();
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        dismiss();
                    }
                }
        });

        return builder.create();
    }

    void initViews(View view) {
        switchMaterial = view.findViewById(R.id.toggle);
        btnDone = view.findViewById(R.id.btnDoneUnusedTime);
    }

    public interface PassUnusedTimeBoolInterface  {
        void setUnusedTimeBool(boolean unusedTimeGoesIntoDailyTime);
    }
}
