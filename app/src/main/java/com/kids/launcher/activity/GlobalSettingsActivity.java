package com.kids.launcher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.kids.launcher.R;
import com.kids.launcher.activity.UserManagement.UserManagementActivity;
import com.kids.launcher.activity.globalSettingsItems.AdminPassActivity;
import com.kids.launcher.system.TimeCheckerService;

public class GlobalSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "";
    RelativeLayout relLang, relUserMang, relAdminPass, relGlobalLock, relContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: global settings");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_settings);
        relLang = findViewById(R.id.relLang);
        relUserMang = findViewById(R.id.userManagement);
        relAdminPass = findViewById(R.id.relAdmin);
        relGlobalLock = findViewById(R.id.relGlobalLock);
        relContact = findViewById(R.id.relContact);
        relLang.setOnClickListener(this);
        relUserMang.setOnClickListener(this);
        relAdminPass.setOnClickListener(this);
        relGlobalLock.setOnClickListener(this);
        relContact.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relLang:
                Intent intent = new Intent(getBaseContext(), TimeCheckerService.class); // Build the intent for the service
                startService(intent);
                break;
            case R.id.relAdmin:
                startActivity(new Intent(GlobalSettingsActivity.this, AdminPassActivity.class));
                finish();
                break;
            case R.id.relGlobalLock:
            case R.id.relContact:
                break;
            case R.id.userManagement:
                startActivity(new Intent(GlobalSettingsActivity.this, UserManagementActivity.class));
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GlobalSettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}