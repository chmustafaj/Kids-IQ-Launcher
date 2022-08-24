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
import com.kids.launcher.activity.UserManagement.UserDetailsActivity;
import com.kids.launcher.activity.profileManagement.CreateProfileManagement;
import com.kids.launcher.activity.profileManagement.ProfileDetailsActivity;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UserLock extends AppCompatActivity  implements SetUserLockDialog.PassUserLockBoolean {

    private static final String TAG ="" ;
    User user = new User();
    String usr;
    TextView setLock,showLock,setDate, showDate;
    EditText setLockTime;
    ImageView exit;
    final Calendar myCalendar= Calendar.getInstance();
    TimePickerDialog picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_lock);
        initViews();
        usr = getIntent().getStringExtra("user");
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
                picker = new TimePickerDialog(UserLock.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                setLockTime.setText(sHour + ":" + sMinute);
                                user.lockTime=setLockTime.getText().toString();
                                User u = PrefUtils.fetchUser(usr, UserLock.this, new Gson());
                                u.lockTime=setLockTime.getText().toString();
                                PrefUtils.saveUser(u, UserLock.this, new Gson());
                                Log.d(TAG, "onTimeSet: time set "+user.lockTime);
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
                new DatePickerDialog(UserLock.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
                SetUserLockDialog setUserLockDialog = new SetUserLockDialog();
                setUserLockDialog.show(getSupportFragmentManager(),"set lock");
            }
        });
    }
    private void updateLabel(){
        String myFormat="dd/MMM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        showDate.setText(dateFormat.format(myCalendar.getTime()));
        user.lockDate=dateFormat.format(myCalendar.getTime());
        Log.d(TAG, "updateLabel: date set "+user.lockDate);
        User u = PrefUtils.fetchUser(usr, UserLock.this, new Gson());
        u.lockDate=dateFormat.format(myCalendar.getTime());
        PrefUtils.saveUser(u, UserLock.this, new Gson());

    }
    void exit(){
        Intent intent= new Intent(UserLock.this, UserDetailsActivity.class);
        intent.putExtra("user", usr);
        Log.d(TAG, "exit: sending lock "+user.lock);
        intent.putExtra("lock",String.valueOf(user.lock));
        intent.putExtra("lock_time",user.lockTime);
        intent.putExtra("lock_date",user.lockDate);
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


    @Override
    public void setUserLockBoolean(boolean lock) {
        user =PrefUtils.fetchUser(usr,UserLock.this,new Gson());
        user.lock=lock;
        PrefUtils.saveUser(user, UserLock.this, new Gson());
        showLock.setText(String.valueOf(lock));

    }
    void setLockBooleanText() {
        if (getIntent().getStringExtra("lock") != null) {
            showLock.setText(getIntent().getStringExtra("lock"));
            user.lock = Boolean.parseBoolean(getIntent().getStringExtra("lock"));
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            User u =PrefUtils.fetchUser(usr, UserLock.this, new Gson());
            user.lock=u.lock;
            showLock.setText(String.valueOf(user.lock));
        }
    }
    void setLockTimeText() {
        if (getIntent().getStringExtra("lock_time") != null) {
            setLockTime.setText(getIntent().getStringExtra("lock_time"));
            user.lockTime = getIntent().getStringExtra("lock_time");
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            List<User> userlist = PrefUtils.fetchAllUsers(UserLock.this, new Gson());
            for (User u : userlist) {
                user.lockTime=u.lockTime;
                setLockTime.setText(u.lockTime);
            }
        }
    }
    void setLockDateText() {
        if (getIntent().getStringExtra("lock_date") != null) {
            showDate.setText(getIntent().getStringExtra("lock_date"));
            user.lockDate = getIntent().getStringExtra("lock_date");
        } else {         //time limit will be taken from the database if we are coming from the profile details activity, in which case the data from the intent will be null
            User u = PrefUtils.fetchUser(usr, UserLock.this, new Gson());
            user.lockDate = u.lockDate;
            showDate.setText(user.lockDate);
        }
    }
}