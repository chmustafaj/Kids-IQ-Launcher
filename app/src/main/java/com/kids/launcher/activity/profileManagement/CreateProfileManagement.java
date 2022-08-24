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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;
import com.kids.launcher.util.BitmapManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("All")
public class CreateProfileManagement extends AppCompatActivity implements View.OnClickListener, Serializable {
    private static final String TAG = "";
    EditText username;
    CircleImageView profileImage;
    ImageView addImage;
    Button btnDone;
    Bitmap bitmap;
    boolean isAllowed = true;
    RelativeLayout timeLimit, appManagement, inputMonitoring, profileLock, deleteProfile;
    Gson gson = new GsonBuilder().serializeNulls().create();
    User user = new User(); //user who owns this profile
    Profile profile = new Profile(); //profile which will be written
    Intent timeIntent;
    String usr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile_management);

        /** Fetching user that owns this profile from the intent that created this activity */
        usr = getIntent().getStringExtra("user");
        user = PrefUtils.fetchUser(usr, this, gson);
        username = findViewById(R.id.edUserName);
        profileImage = findViewById(R.id.profileImage);
        addImage = findViewById(R.id.addPhoto);
        btnDone = findViewById(R.id.btnDone);
        timeLimit = findViewById(R.id.timeLimit);
        appManagement = findViewById(R.id.appManagement);
        inputMonitoring = findViewById(R.id.inputMonitoring);
        profileLock = findViewById(R.id.profileLock);
        deleteProfile = findViewById(R.id.deleteProfile);
        //setting clicks
        addImage.setOnClickListener(this);
        username.setOnClickListener(this);
        timeLimit.setOnClickListener(this);
        appManagement.setOnClickListener(this);
        inputMonitoring.setOnClickListener(this);
        profileLock.setOnClickListener(this);
        deleteProfile.setOnClickListener(this);
        btnDone.setOnClickListener(this);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String username = editable.toString();
                if (user != null) {
                    for (Profile p : user.profiles) {
                        if (p.name == username) {
                            isAllowed = false;
                            Toast.makeText(CreateProfileManagement.this,
                                    "Username Already taken. Try different username",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            isAllowed = true;
                        }
                    }
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 101) {
                bitmap = (Bitmap) data.getExtras().get("data");

                profileImage.setImageBitmap(bitmap);
            } else if (requestCode == 102) {
                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                profileImage.setImageBitmap(bitmap);
                bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Process canceled", Toast.LENGTH_SHORT).show();
        }
    }

    //click listner
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addPhoto:
                Dialog dialog = new Dialog(CreateProfileManagement.this);
                dialog.setContentView(R.layout.select_camera_gallery);
                dialog.show();
                LinearLayout cam, gal;
                cam = dialog.findViewById(R.id.cameraLay);
                gal = dialog.findViewById(R.id.galleryLay);
                cam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 101);
                        dialog.dismiss();

                    }
                });
                gal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");
                        startActivityForResult(Intent.createChooser(pickIntent, "Select Picture"), 102);
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.timeLimit:
                timeIntent = new Intent(CreateProfileManagement.this, TimeLimitActivity.class);
                timeIntent.putExtra("user", usr);
                timeIntent.putExtra("profile_name", username.getText().toString());
                timeIntent.putExtra("from_activity", "create_profile");

                if (getIntent() != null) {
                    timeIntent.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));  //to make sure usage time recycler view doesn't disappear
                    timeIntent.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));  //so max daily time does't disappear when we go back from create profiloe activity from time limit activity and back
                    timeIntent.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
                    timeIntent.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
                    timeIntent.putExtra("break_time", getIntent().getStringExtra("break_time"));
                    timeIntent.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
                    timeIntent.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
                    timeIntent.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
                    timeIntent.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
                    timeIntent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
                    timeIntent.putExtra("lock", getIntent().getStringExtra("lock"));
                    timeIntent.putExtra("lock_time", getIntent().getStringExtra("lock_time"));
                    timeIntent.putExtra("lock_date", getIntent().getStringExtra("lock_date"));

                }
                startActivity(timeIntent);
                break;
            case R.id.appManagement:
                Intent intent2 = new Intent(CreateProfileManagement.this, HideAppsActivity.class);
                startActivity(intent2);
                break;
            case R.id.inputMonitoring:
                Intent intent = new Intent(CreateProfileManagement.this, InputMonitoring.class);

                intent.putExtra("user", usr);
                intent.putExtra("profile_name", username.getText().toString());
                intent.putExtra("from_activity", "create_profile");

                if (getIntent() != null) {
                    intent.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));  //to make sure usage time recycler view doesn't disappear
                    intent.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));  //so max daily time does't disappear when we go back from create profiloe activity from time limit activity and back
                    Log.d(TAG, "onClick: sending time " + getIntent().getStringExtra("max_daily_time"));
                    intent.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
                    intent.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
                    intent.putExtra("break_time", getIntent().getStringExtra("break_time"));
                    intent.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
                    intent.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
                    intent.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
                    intent.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
                    intent.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
                    intent.putExtra("lock", getIntent().getStringExtra("lock"));
                    intent.putExtra("lock_time", getIntent().getStringExtra("lock_time"));
                    intent.putExtra("lock_date", getIntent().getStringExtra("lock_date"));
                    Log.d(TAG, "onClick: sending  time " + getIntent().getStringExtra("screen_input_time"));

                }
                startActivity(intent);
                break;
            case R.id.profileLock:
                Intent intent3 = new Intent(CreateProfileManagement.this, ProfileLock.class);

                intent3.putExtra("user", usr);
                intent3.putExtra("profile_name", username.getText().toString());
                intent3.putExtra("from_activity", "create_profile");

                if (getIntent() != null) {
                    intent3.putExtra("usage_times", getIntent().getSerializableExtra("usage_times"));  //to make sure usage time recycler view doesn't disappear
                    intent3.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));  //so max daily time does't disappear when we go back from create profiloe activity from time limit activity and back
                    intent3.putExtra("daily_time_charge", getIntent().getStringExtra("daily_time_charge"));
                    intent3.putExtra("unused_time_boolean", getIntent().getStringExtra("unused_time_boolean"));
                    intent3.putExtra("break_time", getIntent().getStringExtra("break_time"));
                    intent3.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
                    intent3.putExtra("max_account_time", getIntent().getStringExtra("max_account_time"));
                    intent3.putExtra("fun_profiles", getIntent().getSerializableExtra("fun_profiles"));
                    intent3.putExtra("is_study_profile", getIntent().getStringExtra("is_study_profile"));
                    intent3.putExtra("screen_input_time", getIntent().getStringExtra("screen_input_time"));
                    intent3.putExtra("lock", getIntent().getStringExtra("lock"));
                    intent3.putExtra("lock_time", getIntent().getStringExtra("lock_time"));
                    intent3.putExtra("lock_date", getIntent().getStringExtra("lock_date"));

                }
                startActivity(intent3);
                break;
            case R.id.deleteProfile:
                break;
            case R.id.btnDone:
                if (isAllowed) {
                    if (bitmap == null) {
                        Drawable drawable = ContextCompat.getDrawable(CreateProfileManagement.this, R.drawable.gall);
                        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        Log.d("lor", "onClick: " + bitmap.toString());
                        Glide.with(getApplicationContext()).load(bitmap).into(profileImage);
                    }
                    profile.picture = BitmapManager.bitmapToBase64(bitmap);
                    byte[] image = BitmapManager.bitmapToByte(bitmap);
                    profile.name = username.getText().toString();
                    if (getIntent().getSerializableExtra("usage_times") != null) {
                        Log.d(TAG, "onClick: setting usage times");
                        profile.strUsageTimes = (ArrayList<String>) getIntent().getSerializableExtra("usage_times");
                    }
                    if (getIntent().getStringExtra("max_consecutive_time") != null) {
                        profile.maxConsecutiveTime = Integer.parseInt(getIntent().getStringExtra("max_consecutive_time"));
                    }
                    if (getIntent().getStringExtra("break_time") != null) {
                        profile.breakTime = Integer.parseInt(getIntent().getStringExtra("break_time"));
                    }
                    if (getIntent().getStringExtra("daily_time_charge") != null) {
                        profile.dailyCharge = Integer.parseInt(getIntent().getStringExtra("daily_time_charge"));
                    }
                    if (getIntent().getStringExtra("unused_time_boolean") != null) {
                        Log.d(TAG, "onClick: getting boolean string " + getIntent().getStringExtra("unused_time_boolean"));
                        Log.d(TAG, "onClick: getting boolean bool " + Boolean.parseBoolean(getIntent().getStringExtra("unused_time_boolean")));
                        profile.unusedTimeGoesIntoDailyTime = Boolean.parseBoolean(getIntent().getStringExtra("unused_time_boolean"));
                    }
                    if (getIntent().getStringExtra("max_account_time") != null) {
                        profile.maximumProfileTime = Integer.parseInt(getIntent().getStringExtra("max_account_time"));
                    }
                    if (getIntent().getStringExtra("max_daily_time") != null) {
                        profile.timelimit = Integer.parseInt(getIntent().getStringExtra("max_daily_time"));
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
                    if (getIntent().getStringExtra("lock_date") != null) {
                        profile.lockDate = getIntent().getStringExtra("lock_date");
                    }
                    if (getIntent().getStringExtra("lock_time") != null) {
                        profile.lockTime = getIntent().getStringExtra("lock_time");
                    }
                    Log.d(TAG, "onClick: adding input detection time " + profile.inputDetectionTime);
                    /** Adding the profile to a user's profile set and saving it locally */
                    user.profiles.add(profile);
                    Log.d(TAG, "onClick: fun profiles set " + profile.funProfiles);
                    Log.d(TAG, "onClick: max profile time set " + profile.timelimit);
                    Log.d(TAG, "onClick: input time set " + profile.inputDetectionTime);
                    Log.d(TAG, "onClick: is study profile " + profile.isStudyProfile);
                    Log.d(TAG, "onClick: Usage times set " + profile.strUsageTimes);
                    Log.d(TAG, "onClick: adding profile " + profile);
                    PrefUtils.saveUser(user, this, gson);
                    Log.d(TAG, "onClick: user saved");
                    Intent intent1 = new Intent(CreateProfileManagement.this, ProfileManagement.class);
                    intent1.putExtra("user", usr);
                    startActivity(intent1);

                }
                finish();
            default:

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("profile_name") != null) {
            username.setText(getIntent().getStringExtra("profile_name"));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateProfileManagement.this, ProfileManagement.class);
        intent.putExtra("user", user.username);
        startActivity(intent);
        finish();
    }
}