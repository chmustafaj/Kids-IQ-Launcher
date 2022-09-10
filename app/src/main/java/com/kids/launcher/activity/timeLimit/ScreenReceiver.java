package com.kids.launcher.activity.timeLimit;

import static com.kids.launcher.activity.HomeActivity.active;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kids.launcher.activity.HomeActivity;

public class ScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "";
    public static boolean wasScreenOn = true;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            active = false;
            wasScreenOn = false;
            Log.d(TAG, "onReceive: active screen off");

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            active = true;
            Log.d(TAG, "onReceive:active  screen on");
            wasScreenOn = true;
        }
    }
}