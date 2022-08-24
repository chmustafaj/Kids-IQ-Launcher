package com.kids.launcher.activity.profileManagement;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kids.launcher.R;
import com.kids.launcher.activity.HideAppsActivity;
import com.kids.launcher.activity.timeLimit.ProfileLock;
import com.kids.launcher.activity.timeLimit.subFeatures.InputMonitoring;
import com.kids.launcher.activity.timeLimit.TimeLimitActivity;
import com.kids.launcher.activity.timeLimit.subFeatures.SetInputMonitoringTimeDialog;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;
import com.kids.launcher.util.BitmapManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailsActivity extends AppCompatActivity implements View.OnClickListener, Serializable, SetInputMonitoringTimeDialog.PassScreenMonitoringTimeInterface {
    private static final String TAG = "";
    CircleImageView profilePic;
    ImageView updatePic;
    EditText profileName;
    RelativeLayout timeLimit, appManagement, inputMonitoring, profileLock, deleteProfile;
    String owner, profilename;
    Button update;
    Profile profile = new Profile();
    Profile profileBeforeUpdating = new Profile();
    private int inputDetectionTime = -1;
    boolean isAllowed = true;
    Bitmap bitmap;
    User user; //owner
    int index; //index of profile in user.profiles
    Gson gson = new GsonBuilder().serializeNulls().create();
    String strProfileName, usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        profilePic = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.edUserName);
        updatePic = findViewById(R.id.addPhoto);
        update = findViewById(R.id.update);
        timeLimit = findViewById(R.id.timeLimit);
        appManagement = findViewById(R.id.appManagement);
        inputMonitoring = findViewById(R.id.inputMonitoring);
        profileLock = findViewById(R.id.profileLock);
        deleteProfile = findViewById(R.id.deleteProfile);

        getValuesFromIntents();
        user = PrefUtils.fetchUser(owner, this, gson);
        profile = user.getProfile(profilename);
        profileBeforeUpdating = profile;
        index = user.profiles.indexOf(profile);

        Glide.with(this).load(profile.picdecoded).into(profilePic);
        profileName.setText(profile.name);

        updatePic.setOnClickListener(this);
        profileName.setOnClickListener(this);
        timeLimit.setOnClickListener(this);
        appManagement.setOnClickListener(this);
        inputMonitoring.setOnClickListener(this);
        profileLock.setOnClickListener(this);
        deleteProfile.setOnClickListener(this);
        update.setOnClickListener(this);
    }

    void getValuesFromIntents() {
        Log.d(TAG, "getValuesFromIntents: started");
        owner = getIntent().getExtras().getString("user"); //TODO
        profilename = getIntent().getExtras().getString("profilename"); //TODO
        //String profilename = profileName.getText().toString();
        usr = getIntent().getStringExtra("user");
        Log.d(TAG, "onCreate: username " + usr);
        if (getIntent().getStringExtra("from_activity") != null) {
            if (getIntent().getStringExtra("from_activity").equals("time"))
                user = PrefUtils.fetchUser(usr, this, gson);
        }
        if (getIntent().getIntExtra("profile_time", 0) != 0) {
            Log.d(TAG, "onCreate: getting time");
            profile.timelimit = getIntent().getIntExtra("profile_time", 0);

        }
        if (getIntent().getStringExtra("max_consecutive_time") != null) {
            profile.maxConsecutiveTime = Integer.parseInt(getIntent().getStringExtra("max_consecutive_time"));
        }
        if (getIntent().getStringExtra("break_time") != null) {
            profile.breakTime = Integer.parseInt(getIntent().getStringExtra("break_time"));
        }
        if (getIntent().getStringExtra("index") != null) {
            index = Integer.parseInt(getIntent().getStringExtra("index"));   //this will get the value of the index when we are coming back from the time limit activity
        }
        Log.d(TAG, "onCreate: index " + index);
        if (getIntent().getStringExtra("profile_name") != null) {
            profileName.setText(getIntent().getStringExtra("profile_name"));
        }
        if (getIntent().getStringExtra("max_daily_time") != null) {
            profile.timelimit = Integer.parseInt(getIntent().getStringExtra("max_daily_time"));
        }
        if (getIntent().getSerializableExtra("usage_times") != null) {
            profile.strUsageTimes = (ArrayList<String>) getIntent().getSerializableExtra("usage_times");
        }
        if (getIntent().getStringExtra("daily_time_charge") != null) {
            profile.dailyCharge = Integer.parseInt(getIntent().getStringExtra("daily_time_charge"));
        }
        if (getIntent().getStringExtra("unused_time_boolean") != null) {
            profile.unusedTimeGoesIntoDailyTime = Boolean.parseBoolean(getIntent().getStringExtra("unused_time_boolean"));
        }
        if (getIntent().getStringExtra("max_account_time") != null) {
            profile.maximumProfileTime = Integer.parseInt(getIntent().getStringExtra("max_account_time"));
        }
        if (getIntent().getSerializableExtra("fun_profiles") != null) {
            profile.funProfiles = (ArrayList<String>) getIntent().getSerializableExtra("fun_profiles");
        }
        if (getIntent().getStringExtra("is_study_profile") != null) {
            profile.isStudyProfile = Boolean.parseBoolean(getIntent().getStringExtra("is_study_profile"));
        }
        if (getIntent().getStringExtra("screen_input_time") != null) {
            profile.inputDetectionTime = Integer.parseInt(getIntent().getStringExtra("screen_input_time"));
        }
        if (getIntent().getStringExtra("lock") != null) {
            profile.lock = Boolean.parseBoolean(getIntent().getStringExtra("lock"));
        }
        if (getIntent().getStringExtra("lock_time") != null) {
            profile.lockTime = getIntent().getStringExtra("lock_time");
        }
        if (getIntent().getStringExtra("lock_date") != null) {
            profile.lockDate = getIntent().getStringExtra("lock_date");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPhoto:
                Dialog dialog = new Dialog(ProfileDetailsActivity.this);
                dialog.setContentView(R.layout.select_camera_gallery);
                dialog.show();
                LinearLayout cam, gal;
                cam = dialog.findViewById(R.id.cameraLay);
                gal = dialog.findViewById(R.id.galleryLay);
                cam.setOnClickListener(view12 -> {
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 101);
                    dialog.dismiss();

                });
                gal.setOnClickListener(view1 -> {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK);
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(Intent.createChooser(pickIntent, "Select Picture"), 102);
                    dialog.dismiss();

                });
                break;
            case R.id.timeLimit:
                Intent timeIntent = new Intent(ProfileDetailsActivity.this, TimeLimitActivity.class);
                timeIntent.putExtra("user", usr);
                timeIntent.putExtra("index", String.valueOf(index));
                timeIntent.putExtra("profile_name", profileName.getText().toString());
                timeIntent.putExtra("from_activity", "profile_details");
                if (getIntent() != null) {
                    timeIntent.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));  //to make sure usage time recycler view doesn't disappear
                    timeIntent.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));  //so max daily time doesn't disappear when we go back from create profiloe activity from time limit activity and back
                    timeIntent.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
                    timeIntent.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
                    timeIntent.putExtra("break_time", getIntent().getStringExtra("break_time"));
                    timeIntent.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
                    timeIntent.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
                    timeIntent.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
                    timeIntent.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
                    timeIntent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));

                }
                startActivity(timeIntent);
                break;
            case R.id.appManagement:
                Intent intent = new Intent(ProfileDetailsActivity.this, HideAppsActivity.class);
                startActivity(intent);
                break;
            case R.id.inputMonitoring:
                Intent intent1 = new Intent(ProfileDetailsActivity.this, InputMonitoring.class);

                intent1.putExtra("user", usr);
                intent1.putExtra("profile_name", profileName.getText().toString());
                intent1.putExtra("index", String.valueOf(index));
                intent1.putExtra("from_activity", "profile_details");

                if (getIntent() != null) {
                    intent1.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));  //to make sure usage time recycler view doesn't disappear
                    intent1.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));  //so max daily time does't disappear when we go back from create profiloe activity from time limit activity and back
                    intent1.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
                    intent1.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
                    intent1.putExtra("break_time", getIntent().getStringExtra("break_time"));
                    intent1.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
                    intent1.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
                    intent1.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
                    intent1.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
                    intent1.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));

                }
                startActivity(intent1);
                break;
            case R.id.profileLock:
                Intent intent2 = new Intent(ProfileDetailsActivity.this, ProfileLock.class);

                intent2.putExtra("user", usr);
                intent2.putExtra("profile_name", profileName.getText().toString());
                intent2.putExtra("index", String.valueOf(index));
                intent2.putExtra("from_activity", "profile_details");

                if (getIntent() != null) {
                    intent2.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));  //to make sure usage time recycler view doesn't disappear
                    intent2.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));  //so max daily time does't disappear when we go back from create profiloe activity from time limit activity and back
                    intent2.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
                    intent2.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
                    intent2.putExtra("break_time", getIntent().getStringExtra("break_time"));
                    intent2.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
                    intent2.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
                    intent2.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
                    intent2.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
                    intent2.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
                    intent2.putExtra("lock", getIntent().getStringExtra("lock"));
                    intent2.putExtra("lock_time", getIntent().getStringExtra("lock_time"));
                    intent2.putExtra("lock_date", getIntent().getStringExtra("lock_date"));
                }
                startActivity(intent2);
                break;
            case R.id.deleteProfile:
                break;
            case R.id.update:
                if (isAllowed) {
                    if (bitmap == null) {
                        Drawable drawable = ContextCompat.getDrawable(ProfileDetailsActivity.this, R.drawable.gall);
                        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        Glide.with(getApplicationContext()).load(bitmap).into(profilePic);
                    }

                }
                user.profiles.get(index).picture = BitmapManager.bitmapToBase64(bitmap);
                user.profiles.get(index).name = profileName.getText().toString();
                Log.d(TAG, "onClick: lock "+user.profiles.get(index).lock);
                Log.d(TAG, "onClick: setting lock time "+user.profiles.get(index).lockTime);
                Log.d(TAG, "onClick: setting lock date "+user.profiles.get(index).lockDate);
                PrefUtils.saveUser(user, this, gson);
                Intent i = new Intent(ProfileDetailsActivity.this, ProfileManagement.class);
                i.putExtra("user", user.username);
                startActivity(i);
                finish();
            default:

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 101) {
                bitmap = (Bitmap) data.getExtras().get("data");

                profilePic.setImageBitmap(bitmap);
            } else if (requestCode == 102) {

                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    bitmap = (Bitmap) data.getExtras().get("data");

                }


                profilePic.setImageBitmap(bitmap);
                bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Process canceled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ProfileDetailsActivity.this, ProfileManagement.class);
        i.putExtra("user", user.username);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getValuesFromIntents();
    }

    @Override
    public void setScreenMonitoringTime(int time) {
        inputDetectionTime = time;
    }
}