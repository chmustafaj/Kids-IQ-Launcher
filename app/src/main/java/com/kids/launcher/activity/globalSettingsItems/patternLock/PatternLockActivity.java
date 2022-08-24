package com.kids.launcher.activity.globalSettingsItems.patternLock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.kids.launcher.R;
import com.kids.launcher.activity.globalSettingsItems.AdminPassActivity;

import java.util.List;

public class PatternLockActivity extends AppCompatActivity {

    // Initialize pattern lock view
    PatternLockView mPatternLockView;
    Button BtnContinue;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PatternLockActivity.this, AdminPassActivity.class));
        finish();
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_lock);
        BtnContinue = findViewById(R.id.btnContinue);
        BtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminPassActivity.class);
                startActivity(intent);
                finish();
                finishAffinity();
            }
        });
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                // Shared Preferences to save state
                SharedPreferences sharedPreferences = getSharedPreferences("patternPrefs", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("patternPass", PatternLockUtils.patternToString(mPatternLockView, pattern));
                editor.apply();
                BtnContinue.setVisibility(View.VISIBLE);
                // Intent to navigate to home screen when password added is true

            }

            @Override
            public void onCleared() {

            }
        });
    }
}
