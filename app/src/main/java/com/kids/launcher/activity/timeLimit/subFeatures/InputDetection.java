package com.kids.launcher.activity.timeLimit.subFeatures;

import static com.kids.launcher.activity.HomeActivity.active;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class InputDetection extends Activity {
    public static long DISCONNECT_TIMEOUT = Integer.MAX_VALUE; //This will be the value when no input detection time has been set by the parent

    private static Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // todo
            return true;
        }
    });

    private static final String TAG = "";
    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            active = false;
            Log.d(TAG, "run: time out time " + DISCONNECT_TIMEOUT);
            Log.d(TAG, "run: active no ");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(InputDetection.this, "User Inactive", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
        Log.d(TAG, "run: active");
        active = true;
    }

    public void stopDisconnectTimer() {

        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        resetDisconnectTimer();
//    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}
