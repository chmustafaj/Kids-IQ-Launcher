package com.kids.launcher.activity.timeLimit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.kids.launcher.R;
import com.kids.launcher.activity.profileManagement.CreateProfileManagement;
import com.kids.launcher.activity.profileManagement.ProfileDetailsActivity;
import com.kids.launcher.activity.timeLimit.subFeatures.AddFunProfiles;
import com.kids.launcher.activity.timeLimit.subFeatures.BreakDialog;
import com.kids.launcher.activity.timeLimit.subFeatures.DailyTimeChargeDialog;
import com.kids.launcher.activity.timeLimit.subFeatures.MaximumProfileTimeDialog;
import com.kids.launcher.activity.timeLimit.subFeatures.TimePickerDialogClass;
import com.kids.launcher.activity.timeLimit.subFeatures.UnusedTimeDialog;
import com.kids.launcher.activity.timeLimit.subFeatures.UsageTimeLimit;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;

import java.util.List;

public class TimeLimitActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialogClass.PassTimeInterface, BreakDialog.PassBreakTimeInterface, DailyTimeChargeDialog.PassDailyTimeChargeInterface, MaximumProfileTimeDialog.PassMaximumProfileTimeInterface, UnusedTimeDialog.PassUnusedTimeBoolInterface {
    private static final String TAG = "";
    RelativeLayout usageTimeLimit, topUpDailyTime, unusedTime,
            maximumAccountTime, maximumDayTime, earnTime,
            breakTime;
    TextView dailyTime, showBreakTime, showDailyCharge, showSwitchState, showMaxAccountTime;
    ImageView backBtn;
    String profileName, usr;
    Profile profile = new Profile(); //profile which will be written

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_limit);
        initViews();
        profileName = getIntent().getStringExtra("profile_name");
        usr = getIntent().getStringExtra("user");
        initProfile();
        setMaxProfileTime();
        setBreakTimeText();
        setDailyChargeTimeText();
        setMaxAccountTimeText();
        setUnusedTimeBooleanText();

        //Setting Click Listener
        usageTimeLimit.setOnClickListener(this);
        topUpDailyTime.setOnClickListener(this);
        unusedTime.setOnClickListener(this);
        maximumAccountTime.setOnClickListener(this);
        maximumDayTime.setOnClickListener(this);
        earnTime.setOnClickListener(this);
        breakTime.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    //from here you will set intent for some sub features like Usage times Activity, and Full screen
// dialogues for others subFeatures
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.usageTimes:  // TODO: 08/08/2022 add rest of the parameters
                Intent timeIntent = new Intent(TimeLimitActivity.this, UsageTimeLimit.class);
                timeIntent.putExtra("user", usr);
                timeIntent.putExtra("profile_name", getIntent().getStringExtra("profile_name"));
                timeIntent.putExtra("index", getIntent().getStringExtra("index"));
                if (getIntent() != null) {
                    timeIntent.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));  //to make sure usage time recycler view doesn't disappear
                }
                if (getIntent().getStringExtra("from_activity") != null) {
                    timeIntent.putExtra("from_activity", getIntent().getStringExtra("from_activity"));    //passing the activity we came from to usage tme activity, so when we come back from usage time activity, we come back to the correct activity
                }
                timeIntent.putExtra("max_daily_time", String.valueOf(profile.timelimit));
                timeIntent.putExtra("max_consecutive_time", String.valueOf(profile.maxConsecutiveTime));
                timeIntent.putExtra("break_time", String.valueOf(profile.breakTime));
                timeIntent.putExtra("daily_time_charge", String.valueOf(profile.dailyCharge));
                timeIntent.putExtra("unused_time_boolean", String.valueOf(profile.unusedTimeGoesIntoDailyTime));
                timeIntent.putExtra("max_account_time", String.valueOf(profile.maximumProfileTime));
                timeIntent.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
                timeIntent.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
                if (getIntent().getStringExtra("screen_input_time") != null) {
                    timeIntent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
                }
                startActivity(timeIntent);
                break;
            case R.id.TopUpDailyTime:
                DailyTimeChargeDialog dailyTimeChargeDialog = new DailyTimeChargeDialog();
                dailyTimeChargeDialog.show(getSupportFragmentManager(), "daily time charge dialog");
                break;
            case R.id.UnusedTime:
                UnusedTimeDialog unusedTimeDialog = new UnusedTimeDialog();
                unusedTimeDialog.show(getSupportFragmentManager(),"unused time dialog");
                break;
            case R.id.MaximumAccountTime:
                MaximumProfileTimeDialog maximumProfileTimeDialog = new MaximumProfileTimeDialog();
                maximumProfileTimeDialog.show(getSupportFragmentManager(), "maximum time picker dialog");
                break;
            case R.id.MaximumDayTime:
                TimePickerDialogClass timePickerDialog = new TimePickerDialogClass();
                timePickerDialog.show(getSupportFragmentManager(), "time picker dialog");
                break;
            case R.id.earnTime:
                Intent myIntent = new Intent(TimeLimitActivity.this, AddFunProfiles.class);
                myIntent.putExtra("study_profile", profile.name);
                myIntent.putExtra("username", usr);
                myIntent.putExtra("profile_name", getIntent().getStringExtra("profile_name"));
                myIntent.putExtra("index", getIntent().getStringExtra("index"));
                if (getIntent().getStringExtra("from_activity") != null) {
                    myIntent.putExtra("from_activity", getIntent().getStringExtra("from_activity"));    //passing the activity we came from to usage tme activity, so when we come back from usage time activity, we come back to the correct activity
                }
                myIntent.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));
                myIntent.putExtra("max_daily_time", String.valueOf(profile.timelimit));
                myIntent.putExtra("max_consecutive_time", String.valueOf(profile.maxConsecutiveTime));
                myIntent.putExtra("break_time", String.valueOf(profile.breakTime));
                myIntent.putExtra("daily_time_charge", String.valueOf(profile.dailyCharge));
                myIntent.putExtra("unused_time_boolean", String.valueOf(profile.unusedTimeGoesIntoDailyTime));
                myIntent.putExtra("max_account_time", String.valueOf(profile.maximumProfileTime));
                if (getIntent().getStringExtra("screen_input_time") != null) {
                    myIntent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
                }
                User user = PrefUtils.fetchUser(usr, TimeLimitActivity.this, new Gson());
                if (user.profiles.size() >= 1) {    //The activity will not start if there are no other profiles to add
                    startActivity(myIntent);
                } else {
                    Toast.makeText(this, "User has no other profiles", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.breakTime:
                BreakDialog breakDialog = new BreakDialog();
                breakDialog.show(getSupportFragmentManager(), "break time dialog");
                break;
            case R.id.exit:
                exit();
                break;
            default:

        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    void exit() {
        Intent intent;
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            intent = new Intent(TimeLimitActivity.this, ProfileDetailsActivity.class);
        } else {
            intent = new Intent(TimeLimitActivity.this, CreateProfileManagement.class);

        }
        intent.putExtra("user", usr);
        intent.putExtra("from_activity", "time");
        intent.putExtra("max_daily_time", String.valueOf(profile.timelimit));
        intent.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));
        intent.putExtra("max_consecutive_time", String.valueOf(profile.maxConsecutiveTime));
        intent.putExtra("break_time", String.valueOf(profile.breakTime));
        intent.putExtra("index", getIntent().getStringExtra("index"));  //the index for keeping track of which profile we are on in the profile details activity
        if (profileName != null) {
            intent.putExtra("profile_name", profileName);
        }
        intent.putExtra("daily_time_charge", String.valueOf(profile.dailyCharge));
        intent.putExtra("unused_time_boolean", String.valueOf(profile.unusedTimeGoesIntoDailyTime));
        intent.putExtra("max_account_time", String.valueOf(profile.maximumProfileTime));
        intent.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
        intent.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
        if (getIntent().getStringExtra("screen_input_time") != null) {
            intent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
        }
        intent.putExtra("lock", getIntent().getStringExtra("lock"));
        intent.putExtra("lock_time", getIntent().getStringExtra("lock_time"));
        intent.putExtra("lock_date", getIntent().getStringExtra("lock_date"));
        startActivity(intent);
    }

    void initViews() {
        usageTimeLimit = findViewById(R.id.usageTimes);
        topUpDailyTime = findViewById(R.id.TopUpDailyTime);
        unusedTime = findViewById(R.id.UnusedTime);
        maximumAccountTime = findViewById(R.id.MaximumAccountTime);
        maximumDayTime = findViewById(R.id.MaximumDayTime);
        earnTime = findViewById(R.id.earnTime);
        breakTime = findViewById(R.id.breakTime);
        backBtn = findViewById(R.id.exit);
        dailyTime = findViewById(R.id.showDailyTime);
        showBreakTime = findViewById(R.id.showBreakTime);
        showDailyCharge = findViewById(R.id.showDailyCharge);
        showSwitchState = findViewById(R.id.showSwitchState);
        showMaxAccountTime = findViewById(R.id.showMaxAccTime);
    }

    void setMaxProfileTime() {
        if (getIntent().getStringExtra("max_daily_time") != null) {
            dailyTime.setText(getIntent().getStringExtra("max_daily_time"));
            profile.timelimit = Integer.parseInt(getIntent().getStringExtra("max_daily_time"));
        } else {
            List<User> userlist = PrefUtils.fetchAllUsers(TimeLimitActivity.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        profile.timelimit = p.timelimit;
                        dailyTime.setText(String.valueOf(profile.timelimit));
                    }
                }
            }
        }
        if (profile.timelimit == -1) {     //if time is not set
            dailyTime.setText("Time");
        }
    }

    void setMaxAccountTimeText() {
        if (getIntent().getStringExtra("max_account_time") != null) {
            showMaxAccountTime.setText(getIntent().getStringExtra("max_account_time"));
            profile.maximumProfileTime = Integer.parseInt(getIntent().getStringExtra("max_account_time"));
        } else {
            List<User> userlist = PrefUtils.fetchAllUsers(TimeLimitActivity.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        profile.maximumProfileTime = p.maximumProfileTime;
                        showMaxAccountTime.setText(String.valueOf(profile.maximumProfileTime));
                    }
                }
            }
        }
        if (profile.maximumProfileTime == -1) {     //if time is not set
            showMaxAccountTime.setText("Time");
        }
    }

    void setDailyChargeTimeText() {
        if (getIntent().getStringExtra("daily_time_charge") != null) {
            showDailyCharge.setText(getIntent().getStringExtra("daily_time_charge"));
            profile.dailyCharge = Integer.parseInt(getIntent().getStringExtra("daily_time_charge"));
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            List<User> userlist = PrefUtils.fetchAllUsers(TimeLimitActivity.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        profile.dailyCharge = p.dailyCharge;
                        showDailyCharge.setText(String.valueOf(profile.dailyCharge));
                    }
                }
            }
        }
        if (profile.dailyCharge == -1) {     //if time is not set
            showDailyCharge.setText("Time");
        }
    }

    void setBreakTimeText() {
        if (getIntent().getStringExtra("break_time") != null) {
            showBreakTime.setText(getIntent().getStringExtra("break_time"));
            profile.breakTime = Integer.parseInt(getIntent().getStringExtra("break_time"));
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            List<User> userlist = PrefUtils.fetchAllUsers(TimeLimitActivity.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        profile.breakTime = p.breakTime;
                        showBreakTime.setText(String.valueOf(profile.breakTime));
                    }
                }
            }
        }
        if (profile.breakTime == -1) {     //if time is not set
            showBreakTime.setText("Time");
        }
    }

    void setUnusedTimeBooleanText() {
        if (getIntent().getStringExtra("unused_time_boolean") != null) {
            showSwitchState.setText(getIntent().getStringExtra("unused_time_boolean"));
            profile.unusedTimeGoesIntoDailyTime = Boolean.parseBoolean(getIntent().getStringExtra("unused_time_boolean"));
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            List<User> userlist = PrefUtils.fetchAllUsers(TimeLimitActivity.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        profile.unusedTimeGoesIntoDailyTime = p.unusedTimeGoesIntoDailyTime;
                        showSwitchState.setText(String.valueOf(profile.unusedTimeGoesIntoDailyTime));
                    }
                }
            }
        }
    }

    @Override
    public void setTime(String time) {
        if (time != null && time != "") {
            profile.timelimit = Integer.parseInt(time);
            dailyTime.setText(time);
        }
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            User user = PrefUtils.fetchUser(usr, TimeLimitActivity.this, new Gson());
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).timelimit = Integer.parseInt(time);
            PrefUtils.saveUser(user, TimeLimitActivity.this, new Gson());
        }
    }

    @Override
    public void setBreakTime(int breakTime, int consecutiveTime) {
        if (breakTime != 0 && consecutiveTime != 0)
            profile.breakTime = breakTime;
        profile.maxConsecutiveTime = consecutiveTime;
        showBreakTime.setText(String.valueOf(breakTime));
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            User user = PrefUtils.fetchUser(usr, TimeLimitActivity.this, new Gson());
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).breakTime = breakTime;
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).maxConsecutiveTime = consecutiveTime;
            PrefUtils.saveUser(user, TimeLimitActivity.this, new Gson());
        }
    }

    void initProfile() {
        User user = PrefUtils.fetchUser(usr, this, new Gson());
        for (Profile p : user.profiles) {
            if (p.name.equals(profileName)) {
                profile = p;
            }
        }
    }

    @Override
    public void setDailyChargeTime(int time) {
        profile.dailyCharge = time;
        showDailyCharge.setText(String.valueOf(time));
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            User user = PrefUtils.fetchUser(usr, TimeLimitActivity.this, new Gson());
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).dailyCharge = time;
            PrefUtils.saveUser(user, TimeLimitActivity.this, new Gson());
        }
    }

    @Override
    public void setMaximumProfileTime(int time) {
        profile.maximumProfileTime = time;
        Log.d(TAG, "setMaximumProfileTime: setting " + profile.maximumProfileTime);
        showMaxAccountTime.setText(String.valueOf(time));
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {     //saving
            User user = PrefUtils.fetchUser(usr, TimeLimitActivity.this, new Gson());
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).maximumProfileTime = time;
            PrefUtils.saveUser(user, TimeLimitActivity.this, new Gson());
        }
    }

    @Override
    public void setUnusedTimeBool(boolean unusedTimeGoesIntoDailyTime) {
        profile.unusedTimeGoesIntoDailyTime = unusedTimeGoesIntoDailyTime;
        showSwitchState.setText(String.valueOf(unusedTimeGoesIntoDailyTime));
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            User user = PrefUtils.fetchUser(usr, TimeLimitActivity.this, new Gson());
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).unusedTimeGoesIntoDailyTime = unusedTimeGoesIntoDailyTime;
            PrefUtils.saveUser(user, TimeLimitActivity.this, new Gson());
        }
    }
}