package com.kids.launcher.system;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kids.launcher.R;

public class TimeCheckerService extends Worker {

    Gson gson = new GsonBuilder().serializeNulls().create();

    public TimeCheckerService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);

    }

    private NotificationManager notificationManager;


    @NonNull
    @Override
    public Result doWork() {
//        setForegroundAsync(createForegroundInfo());
//        Pair<Boolean, String> pair = HomeActivity.checkTimeup(getApplicationContext(), gson,"");
//        if (pair.first) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("TIME_UP", pair.second);
//            getApplicationContext().startActivity(intent);
//            Log.e("DETECTED", "TIMEUP");
//        }
        return Result.retry();
    }

    @NonNull
    private ForegroundInfo createForegroundInfo() {
        // Build a notification using bytesRead and contentLength
        Context context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        Notification notification = new NotificationCompat.Builder(context, "TIME_CHECKER")
                .setContentTitle("Kids Launcher is watching your time")
                .setTicker("TimeWatch")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launch)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .build();

        //return new ForegroundInfo(notification);
        return new ForegroundInfo(9999, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create a Notification channel
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("TIME_CHECKER", "TimeWatch", importance);
        channel.setDescription("Kids Launcher watches your time;");
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}