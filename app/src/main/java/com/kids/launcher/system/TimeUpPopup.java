package com.kids.launcher.system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.kids.launcher.R;

import razerdp.basepopup.BasePopupWindow;

@SuppressLint("SetTextI18n")
public class TimeUpPopup extends BasePopupWindow {

    public TimeUpPopup(Activity activity, String expiredProf) {
        super(activity.getBaseContext());
        setContentView(R.layout.popup_timeout);

        new Handler(Looper.getMainLooper()).post(() -> {
            MaterialButton btn = findViewById(R.id.add_time);
            TextView txt = findViewById(R.id.notice);
            if (txt != null) txt.setText("Time is up for the following profile: " + expiredProf);
        });
        Log.e("POPUP?", "POOOOPUP?");
    }

}
