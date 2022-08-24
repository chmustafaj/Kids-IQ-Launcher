package com.kids.launcher.activity.timeLimit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.kids.launcher.R;
import com.kids.launcher.activity.profileManagement.CreateProfileManagement;
import com.kids.launcher.activity.profileManagement.ProfileDetailsActivity;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfileLock extends AppCompatActivity implements SetProfileLockDialog.PassLockBooleanInterface {
    private static final String TAG ="" ;
    Profile profile = new Profile();
    String usr, profileName;
    TextView setLock,showLock, setDate, showDate;
    EditText setLockTime;
    ImageView exit;
    final Calendar myCalendar= Calendar.getInstance();
    TimePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_lock);
        initViews();
        profileName = getIntent().getStringExtra("profile_name");
        usr = getIntent().getStringExtra("user");
        initProfile();
        setLockBooleanText();
        setLockDateText();
        setLockTimeText();
        setLockTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(ProfileLock.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                setLockTime.setText(sHour + ":" + sMinute);
                                profile.lockTime=setLockTime.getText().toString();
                                if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
                                    User user = PrefUtils.fetchUser(usr, ProfileLock.this, new Gson());
                                    user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).lockTime = setLockTime.getText().toString();
                                    PrefUtils.saveUser(user, ProfileLock.this, new Gson());
                                }
                                Log.d(TAG, "onTimeSet: time set "+profile.lockTime);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ProfileLock.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
        setLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetProfileLockDialog setProfileLockBooleanDialog = new SetProfileLockDialog();
                setProfileLockBooleanDialog.show(getSupportFragmentManager(),"set lock");
            }
        });
    }
    void exit(){
        Intent intent;
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            intent = new Intent(ProfileLock.this, ProfileDetailsActivity.class);
        } else {
            intent = new Intent(ProfileLock.this, CreateProfileManagement.class);

        }
        intent.putExtra("index", getIntent().getStringExtra("index"));  //for the profile details activity
        intent.putExtra("user", usr);
        intent.putExtra("max_daily_time", getIntent().getStringExtra("max_daily_time"));
        intent.putExtra("max_consecutive_time", getIntent().getStringExtra("max_consecutive_time"));
        intent.putExtra("break_time", getIntent().getStringExtra("break_time"));
        intent.putExtra("from_activity", getIntent().getStringExtra("from_activity"));
        intent.putExtra("usage_times", getIntent().getStringExtra("usage_times"));
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
        Log.d(TAG, "exit: sending lock "+profile.lock);
        intent.putExtra("lock",String.valueOf(profile.lock));
        intent.putExtra("lock_time", String.valueOf(profile.lockTime));
        intent.putExtra("lock_date", String.valueOf(profile.lockDate));
        startActivity(intent);
    }
    void initViews(){
        setLock=findViewById(R.id.setLock);
        showLock=findViewById(R.id.showLock);
        setLockTime=findViewById(R.id.lockTillTime);
        setDate=findViewById(R.id.txtSetDate);
        showDate=findViewById(R.id.txtDate);
        exit=findViewById(R.id.exit);
    }
    void initProfile() {
        User user = PrefUtils.fetchUser(usr, this, new Gson());
        for (Profile p : user.profiles) {
            if (p.name.equals(profileName)) {
                profile = p;
            }
        }
    }

    @Override
    public void setLock(boolean lock) {
        profile.lock = lock;
        showLock.setText(String.valueOf(lock));
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            User user = PrefUtils.fetchUser(usr, ProfileLock.this, new Gson());
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).lock = lock;
            Log.d(TAG, "setLockBoolean: setting lock");
            Log.d(TAG, "setLockBoolean: username "+usr);
            Log.d(TAG, "setLockBoolean: index "+Integer.parseInt(getIntent().getStringExtra("index")));
            PrefUtils.saveUser(user, ProfileLock.this, new Gson());
        }
    }
    private void updateLabel(){
        String myFormat="dd/MMM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        showDate.setText(dateFormat.format(myCalendar.getTime()));
        profile.lockDate=dateFormat.format(myCalendar.getTime());
        Log.d(TAG, "updateLabel: date set "+profile.lockDate);
        if (getIntent().getStringExtra("from_activity").equals("profile_details")) {
            User user = PrefUtils.fetchUser(usr, ProfileLock.this, new Gson());
            user.profiles.get(Integer.parseInt(getIntent().getStringExtra("index"))).lockDate = dateFormat.format(myCalendar.getTime());
            PrefUtils.saveUser(user, ProfileLock.this, new Gson());
        }
    }
    void setLockBooleanText() {
        if (getIntent().getStringExtra("lock") != null) {
            showLock.setText(getIntent().getStringExtra("lock"));
            profile.lock = Boolean.parseBoolean(getIntent().getStringExtra("lock"));
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            List<User> userlist = PrefUtils.fetchAllUsers(ProfileLock.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        profile.lock = p.lock;
                        showLock.setText(String.valueOf(profile.lock));
                    }
                }
            }
        }
    }
    void setLockTimeText() {
        if (getIntent().getStringExtra("lock_time") != null) {
            setLockTime.setText(getIntent().getStringExtra("lock_time"));
            profile.lockTime = getIntent().getStringExtra("lock_time");
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            List<User> userlist = PrefUtils.fetchAllUsers(ProfileLock.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        profile.lockTime = p.lockTime;
                        setLockTime.setText(profile.lockTime);
                    }
                }
            }
        }
    }
    void setLockDateText() {
        if (getIntent().getStringExtra("lock_date") != null) {
            showDate.setText(getIntent().getStringExtra("lock_date"));
            profile.lockDate = getIntent().getStringExtra("lock_date");
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            List<User> userlist = PrefUtils.fetchAllUsers(ProfileLock.this, new Gson());
            for (User user : userlist) {
                for (Profile p : user.profiles) {
                    if (p.name.equals(profileName)) {
                        if(!p.lockDate.equals("")){
                            profile.lockDate = p.lockDate;
                            setDate.setText(profile.lockDate);
                        }
                    }
                }
            }
        }
    }
}