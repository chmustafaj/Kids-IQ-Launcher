package com.kids.launcher.activity.timeLimit.subFeatures;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.kids.launcher.R;
import com.kids.launcher.activity.profileManagement.CreateProfileManagement;
import com.kids.launcher.activity.profileManagement.ProfileDetailsActivity;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;

import java.util.List;

public class InputMonitoring extends AppCompatActivity implements SetInputMonitoringTimeDialog.PassScreenMonitoringTimeInterface {
    private static final String TAG = "";
    ImageView exit;
    TextView screen, txtScreenTime, keyboard, txtKeyboardTime;
    private int screenInputDetectionTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_monitoring);
        initViews();
        if (getIntent().getStringExtra("screen_input_time") != null && !getIntent().getStringExtra("screen_input_time").equals("-1")) {
            txtScreenTime.setText(getIntent().getStringExtra("screen_input_time"));
        } else {
            List<User> userlist = PrefUtils.fetchAllUsers(InputMonitoring.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(getIntent().getStringExtra("profile_name"))) {
                        txtScreenTime.setText(p.inputDetectionTime + "s");
                    }
                }
            }
        }
        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetInputMonitoringTimeDialog setInputMonitoringTime = new SetInputMonitoringTimeDialog();
                setInputMonitoringTime.show(getSupportFragmentManager(), "input_time");
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
    }
    void exit(){
        Intent intent;
        if (getIntent().getStringExtra("from_activity").equals("create_profile")) {
            intent = new Intent(InputMonitoring.this, CreateProfileManagement.class);
        } else {
            intent = new Intent(InputMonitoring.this, ProfileDetailsActivity.class);
        }
        if (getIntent() != null) {
            intent.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));  //to make sure usage time recycler view doesn't disappear
            intent.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));  //so max daily time does't disappear when we go back from create profiloe activity from time limit activity and back
            intent.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
            intent.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
            intent.putExtra("break_time", getIntent().getStringExtra("break_time"));
            intent.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
            intent.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
            intent.putExtra("index", getIntent().getStringExtra("index"));
            intent.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
            intent.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
            if (screenInputDetectionTime != -1) {
                intent.putExtra("screen_input_time", String.valueOf(screenInputDetectionTime));
            } else {
                if (getIntent() != null) {
                    if (getIntent().getStringExtra("screen_input_time") != null) {
                        intent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
                    }
                }
            }
            intent.putExtra("user", getIntent().getStringExtra("user"));
            intent.putExtra("profile_name", getIntent().getStringExtra("profile_name"));

        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    void initViews() {
        screen = findViewById(R.id.txtScreen);
        keyboard = findViewById(R.id.txtKeyboard);
        txtScreenTime = findViewById(R.id.txtTimeScreen);
        txtKeyboardTime = findViewById(R.id.txtTimeKeyboard);
        exit = findViewById(R.id.exitInputMonitoring);
    }

    @Override
    public void setScreenMonitoringTime(int time) {
        screenInputDetectionTime = time;
        if (screenInputDetectionTime != -1) {
            txtScreenTime.setText(time + " s");
        }
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            User user = PrefUtils.fetchUser(getIntent().getStringExtra("user"), InputMonitoring.this, new Gson());
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).maximumProfileTime = time;
            PrefUtils.saveUser(user, InputMonitoring.this, new Gson());
        }
    }
}