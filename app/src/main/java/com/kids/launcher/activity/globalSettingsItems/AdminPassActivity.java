package com.kids.launcher.activity.globalSettingsItems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.andrognito.pinlockview.PinLockView;
import com.kids.launcher.R;
import com.kids.launcher.activity.GlobalSettingsActivity;
import com.kids.launcher.activity.globalSettingsItems.patternLock.PatternLockActivity;
import com.kids.launcher.activity.globalSettingsItems.pinLock.PinLockActivity;
import com.reginald.patternlockview.PatternLockView;

public class AdminPassActivity extends AppCompatActivity {
    PinLockView mPinLockView;
    PatternLockView mPattern;
    SwitchCompat pinSwitch, patternSwitch, fingerPrintSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pass);
        pinSwitch = findViewById(R.id.pinSwitch);
        patternSwitch = findViewById(R.id.patternSwitch);
        fingerPrintSwitch = findViewById(R.id.fingerPrintSwitch);
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        boolean silent = settings.getBoolean("switchkey", false);
        boolean silent2 = settings.getBoolean("switchkey2", false);
        boolean silent3 = settings.getBoolean("switchkey3", false);
        pinSwitch.setChecked(silent);
        patternSwitch.setChecked(silent2);
        fingerPrintSwitch.setChecked(silent3);

        pinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(AdminPassActivity.this, "Pin Enabled", Toast.LENGTH_SHORT).show();


                } else {

                }
                SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.commit();
            }
        });
        patternSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.setEnabled(false);
                if (isChecked) {
                    Toast.makeText(AdminPassActivity.this, "Pattern Enabled", Toast.LENGTH_SHORT).show();

                } else {

                }
                SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey2", isChecked);
                editor.commit();
            }
        });
        fingerPrintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
//                    pinSwitch.setChecked(false);
                    Toast.makeText(AdminPassActivity.this, "Finger Enabled", Toast.LENGTH_SHORT).show();

                } else {

                }
                SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey3", isChecked);
                editor.commit();
            }
        });

        findViewById(R.id.pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PinLockActivity.class));
                fingerPrintSwitch.setChecked(false);
                pinSwitch.setChecked(true);
                patternSwitch.setChecked(false);


            }
        });
        findViewById(R.id.pattern).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), PatternLockActivity.class));
                fingerPrintSwitch.setChecked(false);
                pinSwitch.setChecked(false);
                patternSwitch.setChecked(true);

            }
        });
        findViewById(R.id.fingerPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fingerPrintSwitch.setChecked(true);
                pinSwitch.setChecked(false);
                patternSwitch.setChecked(false);


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), GlobalSettingsActivity.class));
        finish();
    }
}