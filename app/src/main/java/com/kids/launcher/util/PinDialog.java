package com.kids.launcher.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.kids.launcher.R;

import java.util.List;


/**
 * Created by sanyatihan on 27-Dec-16.
 */

public class PinDialog extends Dialog {

    private String message;
    private String title;
    private String btYesText;
    private String btNoText;
    private int icon = 0;
    private View.OnClickListener btYesListener = null;
    private View.OnClickListener btNoListener = null;
    PatternLockView patternLockView;

    public PinDialog(Context context) {
        super(context);
    }

    public PinDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PinDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pattern_for_user);
        patternLockView = findViewById(R.id.pattern_lock_view);
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                // Shared Preferences to save state
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("patternPrefs", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("patternPass", PatternLockUtils.patternToString(patternLockView, pattern));
                editor.apply();
                // Intent to navigate to home screen when password added is true

            }

            @Override
            public void onCleared() {

            }
        });


//        mPinLockView.setPinLockListener(new PinLockListener() {
//            @Override
//            public void onComplete(String pin) {
//
////                if (getContext() instanceof MainActivity) {
////                    SharedPreferences pref = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
////                    String getPin = pref.getString("pref", "Error");
////                    Toast.makeText(getContext(), "" + getPin, Toast.LENGTH_SHORT).show();
////                    if (pin.equals(getPin)) {
////                        getContext().startActivity(new Intent(getContext(), GlobalSettingsActivity.class));
////                    } else {
////                        Toast.makeText(getContext(), "Something Wrong", Toast.LENGTH_SHORT).show();
////                    }
////                }
//
//                SharedPreferences preferences = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
//                preferences.edit().putString(pin, "Error").apply();
//                Toast.makeText(getContext(), ""+pin, Toast.LENGTH_SHORT).show();
//
//                getContext().startActivity(new Intent(getContext(), AdminPassActivity.class));
//
//
//            }
//
//            @Override
//            public void onEmpty() {
//
//            }
//
//            @Override
//            public void onPinChange(int pinLength, String intermediatePin) {
//
//
//            }
//        });

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setPositveButton(String yes, View.OnClickListener onClickListener) {
        dismiss();
        this.btYesText = yes;
        this.btYesListener = onClickListener;


    }

    public void setNegativeButton(String no, View.OnClickListener onClickListener) {
        dismiss();
        this.btNoText = no;
        this.btNoListener = onClickListener;


    }
}