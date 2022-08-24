package com.kids.launcher.activity.UserManagement;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.GsonBuilder;
import com.kids.launcher.Adapters.UserAdapter;
import com.kids.launcher.R;
import com.kids.launcher.activity.GlobalSettingsActivity;
import com.kids.launcher.system.PrefUtils;

public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnRecItemClick {
    ImageView exit;
    RelativeLayout createProfile;
    RecyclerView profileRecyclerView;
    Dialog dialog;
    UserAdapter profileAdapter;
    //List<ProfileData> list =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mangement);
        exit = findViewById(R.id.exit);
        createProfile = findViewById(R.id.createProfile);
        profileRecyclerView = findViewById(R.id.profileRecyclerView);

        exit.setOnClickListener(view -> {
            startActivity(new Intent(UserManagementActivity.this, GlobalSettingsActivity.class));
            finish();
        });

        //profile Creation
        createProfile.setOnClickListener(view ->
                startActivity(new Intent(UserManagementActivity.this, CreateUserActivity.class)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        profileAdapter = new UserAdapter(PrefUtils.fetchAllUsers(this, new GsonBuilder().create()), this);
        profileRecyclerView.setAdapter(profileAdapter);
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(getApplicationContext(), UserProfileDetailsActivity.class);
//        profileData = list.get(position);
//        list.add(profileData);
//        if(!list.isEmpty()){
//
//        }
//        intent.putExtra("ITEM",profileData);
//        startActivity(intent);
//        Dialog dialog;
//        dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.login_view);
//        dialog.show();
//        EditText edUser = dialog.findViewById(R.id.edlogUsername);
//        EditText edPass = dialog.findViewById(R.id.edlogpass);
//        String user = edUser.getText().toString();
//        String pass = edPass.getText().toString();
//        Button login = dialog.findViewById(R.id.login);
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (profileDao.isLogin(user, pass)) {
//                    new Intent(ProfileActivity.this,)
//
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserManagementActivity.this, GlobalSettingsActivity.class);
        startActivity(intent);
    }
}