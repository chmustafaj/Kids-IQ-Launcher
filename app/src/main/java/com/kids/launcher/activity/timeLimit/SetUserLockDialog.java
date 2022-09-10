package com.kids.launcher.activity.timeLimit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kids.launcher.R;

public class SetUserLockDialog extends DialogFragment {
    private static final String TAG = "";
    private Button btnDone;
    private SwitchMaterial switchMaterial;
    private PassUserLockBoolean passUserLockBoolean;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.set_lock_popup, null);
        initViews(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view).setTitle("");
        Bundle bundle = getArguments();
        if(bundle!=null){
            switchMaterial.setChecked(bundle.getBoolean("lock"));
        }
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    passUserLockBoolean = (PassUserLockBoolean) getActivity();
                    passUserLockBoolean.setUserLockBoolean(switchMaterial.isChecked());
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
        switchMaterial = view.findViewById(R.id.toggleLock);
        btnDone = view.findViewById(R.id.btnLockDone);
    }

    public interface PassUserLockBoolean  {
        void setUserLockBoolean(boolean lock);
    }
}
