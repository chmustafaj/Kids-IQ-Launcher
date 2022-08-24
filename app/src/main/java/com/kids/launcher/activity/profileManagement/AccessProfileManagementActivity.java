package com.kids.launcher.activity.profileManagement;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kids.launcher.Adapters.ProfileAdapter;
import com.kids.launcher.R;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.User;

public class AccessProfileManagementActivity extends AppCompatActivity {
    private static final String TAG = "";
    ImageView accessExit;
    RecyclerView accessRecyclerView;
    ProfileAdapter profileAdapter;

    User user;
    Context context;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_profile_management);
        Log.d(TAG, "onCreate:profile started ");
        if (getIntent().getExtras() != null) {
            username = getIntent().getExtras().getString("user");

        }

        accessExit = findViewById(R.id.accessexit);
        accessExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        accessRecyclerView = findViewById(R.id.accessProfileRecyclerView);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //username = getIntent().getExtras().getString("user");
        user = PrefUtils.fetchUser(username, this, new Gson());
        profileAdapter = new ProfileAdapter(user, user.profiles, this);
        accessRecyclerView.setAdapter(profileAdapter);
        accessRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}