package com.kids.launcher.activity.timeLimit.subFeatures;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.kids.launcher.R;

public class TimeUpDialog extends DialogFragment {
    ImageView imgExit;
    TextView expiredProfile;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_time_up, null);
        initViews(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view).setTitle("");
        Bundle bundle = getArguments();
        expiredProfile.setText(bundle.getString("expired_profile"));
        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return builder.create();
    }

    void initViews(View view) {
        imgExit = view.findViewById(R.id.exit);
        expiredProfile = view.findViewById(R.id.profileName);
    }
}
