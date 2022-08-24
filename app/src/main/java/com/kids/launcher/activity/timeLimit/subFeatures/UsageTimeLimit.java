package com.kids.launcher.activity.timeLimit.subFeatures;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kids.launcher.Adapters.UsageTimeAdapter;
import com.kids.launcher.R;
import com.kids.launcher.activity.timeLimit.TimeLimitActivity;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.UsageTime;
import com.kids.launcher.system.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UsageTimeLimit extends AppCompatActivity implements UsageTimeDialog.PassUsageTime, Serializable {
    private static final String TAG = "";
    Button done, btnUsage;
    RecyclerView recyclerView;
    UsageTimeAdapter usageTimeAdapter;
    String usr;
    Gson gson = new GsonBuilder().serializeNulls().create();
    User user; //user who owns this profile
    Profile profile = new Profile(); //profile which will be written
    ArrayList<UsageTime> usageTimeForRecyView;
    String profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_management);
        initViews();
        profileName = getIntent().getStringExtra("profile_name");

        profile.strUsageTimes = new ArrayList<>();
        initRecyclerView();
        usr = getIntent().getStringExtra("user");
        user = PrefUtils.fetchUser(usr, this, gson);
        btnUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsageTimeDialog usageTimeDialog = new UsageTimeDialog();
                usageTimeDialog.show(getSupportFragmentManager(), "usage dialog");
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
    }

    void initViews() {
        done = findViewById(R.id.btnTimeDone);
        btnUsage = findViewById(R.id.usageTimeLimit);
        recyclerView = findViewById(R.id.recViewUsageTimes);
    }

    void initRecyclerView() {
        usageTimeForRecyView = convertUsageTimeInStringToObj(profile.strUsageTimes);
        usageTimeAdapter = new UsageTimeAdapter();

        recyclerView.setAdapter(usageTimeAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        if (usageTimeForRecyView.size() == 0) {
            List<User> userlist = PrefUtils.fetchAllUsers(UsageTimeLimit.this, gson);
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        usageTimeForRecyView = convertUsageTimeInStringToObj(p.strUsageTimes);
                    }
                }
            }
        }
        if (usageTimeForRecyView.size() == 0) {
            if (getIntent() != null) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList = (ArrayList<String>) getIntent().getSerializableExtra("usage_times");
                if (arrayList != null) {
                    usageTimeForRecyView = convertUsageTimeInStringToObj(arrayList);
                    profile.strUsageTimes = arrayList;
                }
            }
        }
        if (usageTimeForRecyView != null) {
            usageTimeAdapter.setUsageTimes(usageTimeForRecyView);
        }
    }


    @Override
    public void addUsageTime(String usageTime) {
        if (usageTime != null) {
            List<User> userlist = PrefUtils.fetchAllUsers(UsageTimeLimit.this, gson);
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        profile.strUsageTimes.addAll(p.strUsageTimes); //getting the usage times already set previously, before adding a new time to be set for the recycler view
                    }
                }
            }
            profile.strUsageTimes.add(usageTime);
            initRecyclerView();
            if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
                User user = PrefUtils.fetchUser(usr, UsageTimeLimit.this, new Gson());
                user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).strUsageTimes = profile.strUsageTimes;
                PrefUtils.saveUser(user, UsageTimeLimit.this, new Gson());
            }
        }
    }

    public ArrayList<UsageTime> convertUsageTimeInStringToObj(ArrayList<String> strUsageTimes) {
        ArrayList<UsageTime> usageTimes = new ArrayList<>();
        for (int i = 0; i < strUsageTimes.size(); i++) {
            UsageTime usageTime = new UsageTime();
            String[] arrUsageTime = strUsageTimes.get(i).split(",");
            usageTime.timeOne = timeFromStringToInt(arrUsageTime[0]);
            usageTime.timeTwo = timeFromStringToInt(arrUsageTime[1]);
            usageTime.dayOne = Integer.parseInt(arrUsageTime[2]);
            usageTime.dayTwo = Integer.parseInt(arrUsageTime[3]);
            Log.d(TAG, "convertUsageTimeInStringToObj: time 1"+usageTime.timeOne);

            usageTimes.add(usageTime);

        }
        return usageTimes;
    }

    static float timeFromStringToInt(String time) {
        Log.d(TAG, "timeFromStringToInt: time " + time);
        String[] timeArray = time.split(":");
        float hour = Integer.parseInt(timeArray[0]);
        float minutes = Integer.parseInt(timeArray[1]);
        return hour + (minutes / 60);
    }

    private void exit() {
        Intent intent = new Intent(UsageTimeLimit.this, TimeLimitActivity.class);
        intent.putExtra("index", getIntent().getStringExtra("index"));  //for the profile details activity
        intent.putExtra("user", usr);
        intent.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));
        intent.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
        intent.putExtra("break_time", getIntent().getStringExtra("break_time"));
        intent.putExtra("from_activity", getIntent().getStringExtra("from_activity"));
        intent.putExtra("usage_times", (Serializable) profile.strUsageTimes);
        intent.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
        intent.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
        intent.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
        intent.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
        intent.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
        if (getIntent().getStringExtra("screen_input_time") != null) {
            intent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
        }
        if (profileName != null) {
            intent.putExtra("profile_name", profileName);
        }
        intent.putExtra("lock", getIntent().getStringExtra("lock"));
        intent.putExtra("lock_time", getIntent().getStringExtra("lock_time"));
        intent.putExtra("lock_date", getIntent().getStringExtra("lock_date"));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}