package com.kids.launcher.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kids.launcher.R;
import com.kids.launcher.activity.HomeActivity;
import com.kids.launcher.activity.MainActivity;
import com.kids.launcher.activity.UserManagement.UserDetailsActivity;
import com.kids.launcher.activity.UserManagement.UserManagementActivity;
import com.kids.launcher.activity.profileManagement.AccessProfileManagementActivity;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private static final String TAG = "";
    Context context;
    List<User> profileUser;
    OnRecItemClick onRecItemClick;
    Bitmap bitmap;

    public UserAdapter(List<User> profileUser, Context context) {
        this.profileUser = profileUser;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (context instanceof UserManagementActivity) {
            view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        } else if (context instanceof MainActivity) {
            view = LayoutInflater.from(context).inflate(R.layout.user_item_2, parent, false);

        }

        return new MyViewHolder(view, onRecItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = profileUser.get(position);
        holder.tvUserName.setText(user.username);
        Bitmap image = user.picdecoded; //TODO: Pass a valid pic
        holder.profilePic.setImageBitmap(image);


    }

    @Override
    public int getItemCount() {
        return profileUser.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView tvUserName, birthday;
        OnRecItemClick onRecItemClick;
        RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView, OnRecItemClick onRecItemClick) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profileImage);
            tvUserName = itemView.findViewById(R.id.userName);
            relativeLayout = itemView.findViewById(R.id.relTop);
            this.onRecItemClick = onRecItemClick;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "MyViewHolder: going user clicked");

                    if (context instanceof UserManagementActivity) {
                        Log.d(TAG, "MyViewHolder: from user management activity");
                        Intent intent = new Intent(context, UserDetailsActivity.class);
                        User model = profileUser.get(getAdapterPosition());
                        intent.putExtra("user", model.username);
                        context.startActivity(intent);
                        //notifyDataSetChanged();
                    } else {
                        User model = profileUser.get(getAdapterPosition());
                        if(!checkIfUserBlocked(model.username)){
                            Intent myintent = new Intent(context, AccessProfileManagementActivity.class);
                            Log.d(TAG, "MyViewHolder: launching profile manager");
                            myintent.putExtra("user", model.username);
                            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(myintent);
                            //notifyDataSetChanged();

                            // User model = profileUser.get(getAdapterPosition());
//                    intent.putExtra("user", model.username);
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            Intent intent = new Intent(itemView.getContext(), MainActivity.class);
                            intent.putExtra("show_time_over_dialog", "true");
                            intent.putExtra("expired_profile", model.username);
                            context.startActivity(intent);
                        }


                    }
                }

            });
        }
        boolean checkIfUserBlocked(String username){
            User user = PrefUtils.fetchUser(username, itemView.getContext(), new Gson());
            Log.d(TAG, "checkIfUserBlocked: user name "+user.username);
            Log.d(TAG, "checkIfUserBlocked: user lock date  "+user.lockDate);
            Log.d(TAG, "checkIfUserBlocked: user lock time  "+user.lockTime);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
            String strCurrentTime = mdformat.format(calendar.getTime());
            String currentDate = df.format(calendar.getTime());
            Date lockDate=null;
            Date lockTime=null;
            Date currTime=null;
            Date curr=null;
            if(user.lock){
                try {
                    curr = df.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    currTime=mdformat.parse(strCurrentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    lockDate =df.parse(user.lockDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    lockTime=mdformat.parse(user.lockTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(lockDate!=null&&curr!=null&&lockTime!=null){
                    if(lockDate.after(curr)){
                        Toast.makeText(context, "User Locked", Toast.LENGTH_SHORT).show();
                        return true;
                    }else if(curr.equals(lockDate)){
                        if(lockTime.after(currTime)){
                            Toast.makeText(context, "User Locked", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                }
            }

            return false;
        }

//        @Override
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.relTop:
//                    if (context instanceof UserManagementActivity) {
//                        showDetails(profileUser.get(getAdapterPosition()), context, getAdapterPosition());
//                    } else if (context instanceof MainActivity) {
//                        context.startActivity(new Intent(context, ProfileManagement.class));
//                    }
//                    break;
//                default:
//
//            }
//        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);

    }

//    private void showDetails(ProfileData profileData, Context ctx, int position) {
//        ImageView exit;
//        ImageView profilePic, updateImage, updateUsername, updateBirthday, updatePass;
//        EditText user, birth, pass;
//        TextView profileManagementBtn;
//        AppDatabase db;
//        ProfileDao profileDao;
//        Button deleteProfile, blockProfile;
//        Dialog dialog = new Dialog(ctx);
//        dialog.setContentView(R.layout.activity_user_profile_details);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        //database initializing
//        db = Room.databaseBuilder(context, AppDatabase.class, "kidsDb")
//                .allowMainThreadQueries()
//                .fallbackToDestructiveMigration()
//                .build();
//        profileDao = db.userDao();
//
//
//        //bridging
//
//        profilePic = dialog.findViewById(R.id.profilePic);
//        user = dialog.findViewById(R.id.TvUserName);
//        disableEditText(user);
//
//
//        birth = dialog.findViewById(R.id.tvBirthday);
//
//        disableEditText(birth);
//
//        pass = dialog.findViewById(R.id.tvPass);
//        disableEditText(pass);
//
//        profileManagementBtn = dialog.findViewById(R.id.profileManageBtn);
//        blockProfile = dialog.findViewById(R.id.blockProfile);
//        updateImage = dialog.findViewById(R.id.updateImage);
//        updateUsername = dialog.findViewById(R.id.updateUserName);
//        updateBirthday = dialog.findViewById(R.id.updateBirthday);
//        updatePass = dialog.findViewById(R.id.updatePass);
//
//        //setting Clicks For Updating Data
//
//        updateImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });
//
//
////click for exit and blockProfile
//        exit = dialog.findViewById(R.id.exit);
//        blockProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, BlockActivity.class);
//                intent.putExtra("ID", profileUser.get(position));
//                context.startActivity(intent);
//
//                dialog.dismiss();
//
//            }
//        });
//        //click on BirthdayEditText
//        birth.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog dialog = new DatePickerDialog(context);
//                dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                        Calendar newDate = Calendar.getInstance();
//                        newDate.set(year, month, day);
//                        birth.setText(day + "/" + month + "/" + year);
//
//
//                    }
//                });
//                dialog.show();
//            }
//        });
//        exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        //ProfileManagementButton
//        profileManagementBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                context.startActivity(new Intent(ctx, ProfileManagement.class));
//            }
//        });
//
//        //Setting text
//
//        if (user != null) {
//            user.setText(profileData.getUserName());
//        }
//        if (birth != null) {
//            birth.setText(profileData.getBirthday());
//        }
//        pass.setText(profileData.getPassword());
//
////deleting userProfile
//        deleteProfile = dialog.findViewById(R.id.deleteProfile);
//        profilePic.setImageBitmap(BitmapManager.byteToBitmap(profileData.getImage()));
//        deleteProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
//                alertDialog.setTitle("Delete Profile");
//                alertDialog.setMessage("Are You sure to delete");
//                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "kidsDb")
//                                .allowMainThreadQueries()
//                                .fallbackToDestructiveMigration()
//                                .build();
//                        ProfileData profileData = profileUser.get(position);
//                        db.userDao().delete(profileData);
//                        profileUser.remove(position);
//                        notifyItemRemoved(position);
//                        dialog.dismiss();
//                    }
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//
//                    }
//                });
//                alertDialog.show();
//
//            }
//        });
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        Window window = dialog.getWindow();
//        window.setGravity(Gravity.CENTER);
//
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
//
//    }

    public interface OnRecItemClick {
        void onItemClick(int position);

    }
}
