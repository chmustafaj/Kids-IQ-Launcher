package com.kids.launcher.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.google.android.material.tabs.TabLayout;
import com.kids.launcher.R;
import com.kids.launcher.activity.timeLimit.subFeatures.TimeUpDialog;
import com.kids.launcher.fragment.HomeFragment;
import com.kids.launcher.fragment.profileFragment.ProfileFragment;
import com.kids.launcher.system.TimeCheckerService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import co.infinum.goldfinger.Goldfinger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "";
    ImageView exit, globalSettings;
    public static boolean patternEnabled = false;
    public static boolean pinEnabled = false;
    PatternLockView mPatternLockView;
    String password;
    public static boolean isFinger = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");

        HomeActivity.active = false;
        Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStart: destroyed");
        globalSettings = findViewById(R.id.global_setting);
        exit = findViewById(R.id.exit);
        //Bridging or Initiating Views
        initView();
        //Creating ViewPager for Swiping RTL
        FragmentManager fm = getSupportFragmentManager();
        ViewStateAdapter sa = new ViewStateAdapter(fm, getLifecycle());
        final ViewPager2 pa = findViewById(R.id.pager);

        pa.setAdapter(sa);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        pa.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
//                tabLayout.selectTab(tabLayout.getTabAt(position));
                tabLayout.getTabAt(position);
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pa.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //calling functions
        isEnabled();
        isPinEnabled();
        isFingerEnabled();
        /* Asking for special permissions */
        grantedDrawPermissions();

        //startTimeupChecker();

        //runPeriodicTimeUpCheck();
//
//        Pair<Boolean, String> pair = PrefUtils.checkTimeup(getApplicationContext(), new Gson());
//        if (pair.first) {
//            showTimeUpPopup(pair.second);
//        }
    }

    @Override
    public void onBackPressed() {
    }

    public void startTimeupChecker() {
        /* Using the latest WorkManager API to handle the time checking policy */
        WorkRequest myWorkRequest =
                new OneTimeWorkRequest.Builder(TimeCheckerService.class)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .addTag("timeup_checker")
                        .build();

        WorkManager.getInstance(this).enqueue(myWorkRequest);
    }

    public boolean grantedDrawPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1010101);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: started");
        Log.d(TAG, "onResume: show time over dialog " + getIntent().getStringExtra("show_time_over_dialog"));
        if (getIntent().getStringExtra("show_time_over_dialog") != null) {
            if (getIntent().getStringExtra("show_time_over_dialog").equals("true")) {
                Log.d(TAG, "onResume: showing time up");
                TimeUpDialog timeUpDialog = new TimeUpDialog();
                Bundle bundle = new Bundle();
                bundle.putString("expired_profile", getIntent().getStringExtra("expired_profile"));
                timeUpDialog.setArguments(bundle);
                timeUpDialog.show(getSupportFragmentManager(), "time_up");
            }
        }
        shareprefData();
        isEnabled();
        isPinEnabled();
        isFingerEnabled();
        if (HomeActivity.active) {
            Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show();
        }
        HomeActivity.active = false;
        Log.d(TAG, "onStart: destroyed");
        //Pair<Boolean, String> pair = PrefUtils.checkTimeup(getApplicationContext(), new Gson());
        // Log.e("Timeup", pair.first.toString());

//        if (getIntent().hasExtra("TIME_UP") || pair.first ) {
//            Log.e("ITERATING", "Time up detected on onResume");
//
//            showTimeUpPopup(getIntent().getStringExtra("TIME_UP"));
//        }
    }

//    public void showTimeUpPopup(String expiredProfile) {
//        Log.e("Timeup"," TIME IS UP");
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            Toast.makeText(this, "time is up for "+expiredProfile, Toast.LENGTH_SHORT).show();
//            AlertDialog dialog = builder.setTitle("TIME UP")
//                    .setMessage("Time is up for " + expiredProfile)
//                    .setCancelable(true).create();
//            dialog.show();
//        }, 1000);
//    }

    private void shareprefData() {
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        pinEnabled = settings.getBoolean("switchkey", false);
        patternEnabled = settings.getBoolean("switchkey2", false);
        isFinger = settings.getBoolean("switchkey3", false);
        //Toast.makeText(this, "" + patternEnabled + pinEnabled, Toast.LENGTH_SHORT).show();
        if (!pinEnabled && !patternEnabled && !isFinger) {
            globalSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: setting clicked");
                    Intent intent = new Intent(MainActivity.this, GlobalSettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showOverLay();

                }
            });

        }

    }

    public boolean isPinEnabled() {
        if (pinEnabled) {
            initViewPin();
        }

        return pinEnabled;
    }

    private void initViewPin() {

        globalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.activity_pin_lock);
                SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                String getPin = sharedPreferences.getString("password", "0");
                PinLockView pinLockView = dialog.findViewById(R.id.pin_lock);
                IndicatorDots indicatorDots = dialog.findViewById(R.id.indicator_dots);
                pinLockView.attachIndicatorDots(indicatorDots);
                pinLockView.setPinLockListener(new PinLockListener() {
                    @Override
                    public void onComplete(String pin) {

                        if (pin.equals(getPin)) {
                            Log.d(TAG, "onComplete: starting activity");
                            Intent intent = new Intent(MainActivity.this, GlobalSettingsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        }

                    }

                    @Override
                    public void onEmpty() {
                        Log.d(TAG, "onEmpty: empty");
                    }

                    @Override
                    public void onPinChange(int pinLength, String intermediatePin) {

                    }
                });
                dialog.show();

            }

        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.activity_pin_lock);
                SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                String getPin = sharedPreferences.getString("password", "0");
                PinLockView pinLockView = dialog.findViewById(R.id.pin_lock);
                IndicatorDots indicatorDots = dialog.findViewById(R.id.indicator_dots);
                pinLockView.attachIndicatorDots(indicatorDots);
                pinLockView.setPinLockListener(new PinLockListener() {
                    @Override
                    public void onComplete(String pin) {

                        if (pin.equals(getPin)) {
                            showOverLay();


                        }

                    }

                    @Override
                    public void onEmpty() {

                    }

                    @Override
                    public void onPinChange(int pinLength, String intermediatePin) {

                    }
                });
                dialog.show();

            }
        });
    }

    public boolean isFingerEnabled() {
        if (isFinger) {
            showFinger();
        }
        return isFinger;
    }

    private void showFinger() {
        globalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Goldfinger goldfinger = new Goldfinger.Builder(MainActivity.this).build();


                Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(MainActivity.this).title("Finger Print")
                        .negativeButtonText("Cancel")
                        .description("Finger Print Authentication")
                        .build();
//                goldfinger.encrypt(params,"key","1234",);

                goldfinger.authenticate(params, new Goldfinger.Callback() {
                    @Override
                    public void onResult(@NonNull Goldfinger.Result result) {
                        if (result.type() == Goldfinger.Type.SUCCESS) {
                            startActivity(new Intent(MainActivity.this, GlobalSettingsActivity.class));

                        }


                    }


                    @Override
                    public void onError(@NonNull Exception e) {

                    }
                });
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Goldfinger goldfinger = new Goldfinger.Builder(MainActivity.this).build();

                Goldfinger.PromptParams params = new Goldfinger.PromptParams.Builder(MainActivity.this).title("Finger Print")
                        .negativeButtonText("Cancel")
                        .description("Finger Print Authentication")
                        .build();
                goldfinger.authenticate(params, new Goldfinger.Callback() {
                    @Override
                    public void onResult(@NonNull Goldfinger.Result result) {
                        //Toast.makeText(MainActivity.this, "" + result.reason() + result.value() + result.type(), Toast.LENGTH_SHORT).show();
                        showOverLay();

                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        //Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    public boolean isEnabled() {
        if (patternEnabled) {
            initView();


        }

        return patternEnabled;


    }

    private void showOverLay() {
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            Dialog overlayDialog = new Dialog(this, 0);
            Objects.requireNonNull(overlayDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            overlayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            overlayDialog.setContentView(R.layout.layout_default_launcher);
            Window window = overlayDialog.getWindow();

            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            window.setAttributes(params);
            overlayDialog.getWindow().setLayout(WindowManager.LayoutParams
                    .MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            //overlayDialog.setCancelable(false);
            overlayDialog.setCanceledOnTouchOutside(false);


            Button btnEnable = overlayDialog.findViewById(R.id.btnEnable);
            Button btnLater = overlayDialog.findViewById(R.id.btnLater);
            btnEnable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlayDialog.dismiss();
                    try {
                        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                }
            });
            overlayDialog.show();

            btnLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlayDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {


        globalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.pattern_view);

                SharedPreferences sharedPreferences = getSharedPreferences("patternPrefs", 0);
                password = sharedPreferences.getString("patternPass", "0");

                mPatternLockView = dialog.findViewById(R.id.pattern_lock_view);

                mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onProgress(List<PatternLockView.Dot> progressPattern) {


                    }

                    @Override
                    public void onComplete(List<PatternLockView.Dot> pattern) {
                        // if drawn pattern is equal to created pattern you will navigate to home screen
                        if (password.equals(PatternLockUtils.patternToString(mPatternLockView, pattern))) {
                            Intent intent = new Intent(getApplicationContext(), GlobalSettingsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            // other wise you will get error wrong password
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            mPatternLockView.clearPattern();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCleared() {

                    }
                });

                dialog.show();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.pattern_view);

                SharedPreferences sharedPreferences = getSharedPreferences("patternPrefs", 0);
                password = sharedPreferences.getString("patternPass", "0");

                mPatternLockView = dialog.findViewById(R.id.pattern_lock_view);

                mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onProgress(List<PatternLockView.Dot> progressPattern) {


                    }

                    @Override
                    public void onComplete(List<PatternLockView.Dot> pattern) {
                        // if drawn pattern is equal to created pattern you will navigate to home screen
                        if (password.equals(PatternLockUtils.patternToString(mPatternLockView, pattern))) {
                            showOverLay();

                        } else {
                            // other wise you will get error wrong password
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            mPatternLockView.clearPattern();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCleared() {

                    }
                });

                dialog.show();
            }
        });


    }

    private class ViewStateAdapter extends FragmentStateAdapter {

        public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Hardcoded in this order, you'll want to use lists and make sure the titles match
            if (position == 0) {
                return new HomeFragment();
            }
            return new ProfileFragment();
        }

        @Override
        public int getItemCount() {
            // Hardcoded, use lists
            return 2;
        }
    }

//    public AtomicBoolean runPeriodicTimeUpCheck() {
//        AtomicBoolean timeUp= new AtomicBoolean(false);
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            Pair<Boolean, String> pair = PrefUtils.checkTimeup(getApplicationContext(), new Gson());
//            Log.e("Timeup", pair.first.toString());
//            if (pair.first) {
//                Log.e("ITERATING", "Time up detected on periodic foreground check");
//                timeUp.set(true);
//                //showTimeUpPopup(getIntent().getStringExtra("TIME_UP"));
//            }
//            runPeriodicTimeUpCheck();
//        }, 15000);
//        return timeUp;
//    }
}
