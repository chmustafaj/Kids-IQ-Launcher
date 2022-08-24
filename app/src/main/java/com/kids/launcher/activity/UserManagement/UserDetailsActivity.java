package com.kids.launcher.activity.UserManagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kids.launcher.Adapters.UserAdapter;
import com.kids.launcher.R;
import com.kids.launcher.activity.profileManagement.ProfileManagement;
import com.kids.launcher.activity.timeLimit.UserLock;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.User;
import com.kids.launcher.util.BitmapManager;

import java.io.IOException;
import java.util.Calendar;

public class UserDetailsActivity extends AppCompatActivity {
    private static final String TAG ="" ;
    ImageView exit;
    ImageView profilePic, updateImage, updateUsername, calender, updatePass;
    EditText useredittext, birth, pass;
    TextView profileManagementBtn;
    User user; //user to be adjusted
    Gson gson = new GsonBuilder().serializeNulls().create();
    boolean isAllowed = false;
    Button updatebBtn, blockUserBtn, deleteUserBtn;
    Bitmap bitmap;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_details);


        String userToFetch = getIntent().getExtras().getString("user");
        user = PrefUtils.fetchUser(userToFetch, this, gson);
        bitmap = user.picdecoded;
        //Setting Image
        profilePic = findViewById(R.id.profilePic);
        Glide.with(this).load(user.picdecoded).into(profilePic);
        //setting Username
        useredittext = findViewById(R.id.TvUserName);
        disableEditText(useredittext);
        useredittext.setText(user.username);
        useredittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String username = editable.toString();
                if (PrefUtils.userExists(username, UserDetailsActivity.this)) {
                    isAllowed = false;
                    Toast.makeText(UserDetailsActivity.this, "User Name Already Exists, Try different username", Toast.LENGTH_SHORT).show();

                } else {
                    isAllowed = true;
                }


            }
        });

        //settingBirthday
        birth = findViewById(R.id.tvBirthday);
        birth.setFocusable(false);
        birth.setFocusableInTouchMode(false);
        birth.setEnabled(false);
        birth.setOnKeyListener(null);
        birth.setText(user.birthday);
        birth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(UserDetailsActivity.this);
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, day);
                        birth.setText(day + "/" + month + "/" + year);


                    }
                });
                dialog.show();

            }

        });

        //settingPassword
        pass = findViewById(R.id.tvPass);
        disableEditText(pass);
        pass.setText(user.password);

        //You can manage Profile Management button here
        profileManagementBtn = findViewById(R.id.profileManageBtn);
        profileManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDetailsActivity.this, ProfileManagement.class);
                intent.putExtra("user", user.username);
                startActivity(intent);
            }
        });

        // You can delete user from here
        blockUserBtn = findViewById(R.id.blockProfile);
        blockUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDetailsActivity.this, UserLock.class);
                intent.putExtra("user",user.username);
                if(getIntent().getStringExtra("lock")!=null){
                    intent.putExtra("lock",getIntent().getStringExtra("lock"));
                }
                if(getIntent().getStringExtra("lock_date")!=null){
                    intent.putExtra("lock_date",getIntent().getStringExtra("lock_date"));
                }
                if(getIntent().getStringExtra("lock_time")!=null){
                    intent.putExtra("lock_time",getIntent().getStringExtra("lock_time"));
                }
                startActivity(intent);
            }
        });
        deleteUserBtn = findViewById(R.id.deleteProfile);
        deleteUserBtn.setOnClickListener(view -> {
            UserAdapter userAdapter = new UserAdapter(PrefUtils
                    .fetchAllUsers(UserDetailsActivity.this, gson), UserDetailsActivity.this);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserDetailsActivity.this);
            alertDialog.setTitle("Delete Profile");
            alertDialog.setMessage("Are You sure to delete");
            alertDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                PrefUtils.deleteUser(user, this, gson);
                userAdapter.notifyDataSetChanged();

                startActivity(new Intent(UserDetailsActivity.this, UserManagementActivity.class));
                finish();

            }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            alertDialog.show();
        });

        //update username
        findViewById(R.id.updateUserName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditText(useredittext);
            }
        });
        //calender Setting
        calender = findViewById(R.id.calender);
        calender.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(UserDetailsActivity.this);
                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, day);
                        birth.setText(day + "/" + month + "/" + year);


                    }
                });
                dialog.show();
            }
        });
        updatePass = findViewById(R.id.updatePass);

        //Update Image
        updateImage = findViewById(R.id.updateImage);
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(UserDetailsActivity.this);
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
                        ImagePicker.with(UserDetailsActivity.this).cropSquare().compress(1024).galleryOnly().start(102);
//                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        pickIntent.setType("image/*");
//                        startActivityForResult(Intent.createChooser(pickIntent, "Select Picture"), 102);
                        dialog.dismiss();

                    }
                });
            }
        });

        //Update Data
        updatebBtn = findViewById(R.id.update);
        updatebBtn.setOnClickListener(view -> {
            if (bitmap == null) {
                Drawable drawable = ContextCompat.getDrawable(UserDetailsActivity.this, R.drawable.ic_android);
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                Log.d("lor", "onClick: " + bitmap.toString());
                Glide.with(getApplicationContext()).load(bitmap).into(profilePic);
            } else {
                bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
            }
            user.picture = BitmapManager.bitmapToBase64(bitmap);
            user.picdecoded = bitmap;
            String useroldname = user.username;
            user.username = useredittext.getText().toString();
            user.password = pass.getText().toString();
            user.birthday = birth.getText().toString();
            Log.d(TAG, "onCreate: lock "+user.lock);
            Log.d(TAG, "onCreate: lock date "+user.lockDate);
            Log.d(TAG, "onCreate: lock time "+user.lockTime);
            PrefUtils.updateUser(user, useroldname, this, gson);
            finish();
            Intent intent = new Intent(UserDetailsActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            }


            profilePic.setImageBitmap(bitmap);
        }

    }

    private void disableEditText(EditText editText) {


    }

    private void enableEditText(EditText editText) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserDetailsActivity.this, UserManagementActivity.class);
        startActivity(intent);
    }
}
