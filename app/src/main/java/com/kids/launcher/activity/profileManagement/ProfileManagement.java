package com.kids.launcher.activity.profileManagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kids.launcher.Adapters.ProfileAdapter;
import com.kids.launcher.R;
import com.kids.launcher.activity.UserManagement.UserDetailsActivity;
import com.kids.launcher.activity.UserManagement.UserManagementActivity;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.User;

public class ProfileManagement extends AppCompatActivity {
    private static final String TAG = "";
    ImageView exit;
    RelativeLayout createProfile;
    RecyclerView profileRecyclerView;
    Dialog dialog;
    ProfileAdapter profileAdapter;
    User user;
    Context context;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);


        exit = findViewById(R.id.exit);
        createProfile = findViewById(R.id.createProfile);
        profileRecyclerView = findViewById(R.id.profileRecyclerView);
        //if(getIntent().getExtras()!=null){
        if (getIntent().getExtras() != null) {
            username = getIntent().getExtras().getString("user");
        }
        // }

        exit.setOnClickListener(view -> {
            startActivity(new Intent(ProfileManagement.this, UserManagementActivity.class));
            finish();
        });

        createProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileManagement.this, CreateProfileManagement.class);
            intent.putExtra("user", user.username);
            startActivity(intent);

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        username = getIntent().getStringExtra("user");
        Log.d(TAG, "onResume: username " + username);
        user = null;
        user = PrefUtils.fetchUser(username, this, new Gson());
        profileAdapter = new ProfileAdapter(user, user.profiles, this);
        profileRecyclerView.setAdapter(profileAdapter);
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileManagement.this, UserDetailsActivity.class);
        intent.putExtra("user", username);
        startActivity(intent);
    }
}