package com.kids.launcher.activity.UserManagement;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kids.launcher.R;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.User;
import com.kids.launcher.util.BitmapManager;

import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateUserActivity extends AppCompatActivity {
    EditText username, birthday, pass;
    CircleImageView profileImage;
    ImageView addImage;
    Button btnDone;
    Bitmap bitmap;
    boolean isAllowed = false;
    ImageView calender;
    User user = new User(); //user to be created
    Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        username = findViewById(R.id.edUserName);
        birthday = findViewById(R.id.edBirthday);
        pass = findViewById(R.id.password);
        profileImage = findViewById(R.id.profileImage);
        addImage = findViewById(R.id.addPhoto);
        btnDone = findViewById(R.id.btnDone);
        //calenderCLick
        findViewById(R.id.calender).setOnClickListener(view -> {
            DatePickerDialog dialog;
            //TODO: Use some DatePickerDialog that is compatible with API < 24 !!
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                dialog = new DatePickerDialog(CreateUserActivity.this);

                dialog.setOnDateSetListener((datePicker, year, month, day) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, day);
                    birthday.setText(day + "/" + month + "/" + year);


                });
                dialog.show();
            }
        });
        birthday.setCursorVisible(false);
        birthday.setFocusable(false);
        birthday.setOnClickListener(view -> {
            DatePickerDialog dialog;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                dialog = new DatePickerDialog(CreateUserActivity.this);
                dialog.setOnDateSetListener((datePicker, year, month, day) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, day);
                    birthday.setText(day + "/" + month + "/" + year);


                });
                dialog.show();
            }

        });


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
                if (PrefUtils.userExists(username, getApplicationContext())) {
                    isAllowed = false;
                    Toast.makeText(getApplicationContext(), "User Name Already Exists, Try different username", Toast.LENGTH_SHORT).show();

                } else {
                    isAllowed = true;
                }
            }
        });
        btnDone.setOnClickListener(view -> {
            if (isAllowed) {
                if (bitmap == null) {
                    Drawable drawable = ContextCompat.getDrawable(CreateUserActivity.this, R.drawable.ic_person);
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                    Log.d("lor", "onClick: " + bitmap.toString());
                    Glide.with(getApplicationContext()).load(bitmap).into(profileImage);
                    //TODO: Save user info

                }
                user.picture = BitmapManager.bitmapToBase64(bitmap);
                user.picdecoded = bitmap;
                user.username = username.getText().toString();
                user.password = pass.getText().toString();
                user.birthday = birthday.getText().toString();
                PrefUtils.saveUser(user, this, gson);

            }
            startActivity(new Intent(CreateUserActivity.this, UserManagementActivity.class));
            finish();

        });


        addImage.setOnClickListener(view -> {
            Dialog dialog = new Dialog(CreateUserActivity.this);
            dialog.setContentView(R.layout.select_camera_gallery);
            dialog.show();
            LinearLayout cam, gal;
            cam = dialog.findViewById(R.id.cameraLay);
            gal = dialog.findViewById(R.id.galleryLay);
            cam.setOnClickListener(view1 -> {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 101);
                dialog.dismiss();
            });
            gal.setOnClickListener(view12 -> {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(pickIntent, "Select Picture"), 102);
                dialog.dismiss();
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            bitmap = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(bitmap);
        } else if (requestCode == 102) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                    bitmap = (Bitmap) data.getExtras().get("data");
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                    bitmap = (Bitmap) data.getExtras().get("data");
            }
            profileImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
