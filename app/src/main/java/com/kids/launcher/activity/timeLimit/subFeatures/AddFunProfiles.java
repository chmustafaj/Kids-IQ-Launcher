package com.kids.launcher.activity.timeLimit.subFeatures;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kids.launcher.Adapters.ProfileEarnTimeAdapter;
import com.kids.launcher.R;
import com.kids.launcher.activity.timeLimit.TimeLimitActivity;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;

import java.io.Serializable;
import java.util.ArrayList;

public class AddFunProfiles extends AppCompatActivity implements Serializable {
    private static final String TAG = "";
    RecyclerView recyclerView;
    ArrayList<Profile> funProfiles = new ArrayList<>();   //
    Profile studyProfile = new Profile(); //the profile to add the fun profiles to
    User user = new User();
    Button btnDone;
    String profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fun_profiles);
        initViews();
        user = PrefUtils.fetchUser(getIntent().getStringExtra("username"), AddFunProfiles.this, new Gson());
        profileName = getIntent().getStringExtra("study_profile");
        for (Profile profile : user.profiles) {
            if (profile.name.equals(profileName)) {
                studyProfile = profile;
            }
        }
        funProfiles = user.profiles;
        if (funProfiles.contains(studyProfile)) {
            funProfiles.remove(studyProfile);
        }
        ProfileEarnTimeAdapter adapter = new ProfileEarnTimeAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter.setFromActivity(getIntent().getStringExtra("from_activity"));
        adapter.setProfiles(funProfiles, studyProfile);
        adapter.setUserName(getIntent().getStringExtra("username"));
        if (getIntent().getStringExtra("index") != null) {
            adapter.setProfileIndex(Integer.parseInt(getIntent().getStringExtra("index")));
        }
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studyProfile.funProfiles != null) {
                    Log.d(TAG, "onClick: fun profiles " + studyProfile.funProfiles);
                    exit();

                } else {
                    Toast.makeText(AddFunProfiles.this, "Add a few profiles", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void exit() {
        Intent intent = new Intent(AddFunProfiles.this, TimeLimitActivity.class);
        intent.putExtra("fun_profiles", (Serializable) studyProfile.funProfiles);
        intent.putExtra("is_study_profiles", Boolean.toString(studyProfile.isStudyProfile));
        intent.putExtra("user", user.username);
        //pass all intents
        intent.putExtra("from_activity", getIntent().getStringExtra("from_activity"));
        intent.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));
        intent.putExtra("index", getIntent().getStringExtra("index"));  //for the profile details activity
        intent.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));
        intent.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
        intent.putExtra("break_time", getIntent().getStringExtra("break_time"));
        intent.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
        intent.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
        intent.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
        if (getIntent().getStringExtra("screen_input_time") != null) {
            intent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
        }
        Log.d(TAG, "exit: profile name " + profileName);
        if (profileName != null) {
            intent.putExtra("profile_name", getIntent().getStringExtra("profile_name"));
        }
        Log.d(TAG, "onClick: sending fun profiles " + studyProfile.funProfiles);
        intent.putExtra("lock", getIntent().getStringExtra("lock"));
        intent.putExtra("lock_time", getIntent().getStringExtra("lock_time"));
        intent.putExtra("lock_date", getIntent().getStringExtra("lock_date"));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        studyProfile.funProfiles = null;
        exit();
    }

    void initViews() {
        recyclerView = findViewById(R.id.recViewFunProfiles);
        btnDone = findViewById(R.id.btnDone);
    }
}