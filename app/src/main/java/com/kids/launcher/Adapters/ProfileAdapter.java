package com.kids.launcher.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kids.launcher.R;
import com.kids.launcher.activity.HomeActivity;
import com.kids.launcher.activity.profileManagement.ProfileDetailsActivity;
import com.kids.launcher.activity.profileManagement.ProfileManagement;
import com.kids.launcher.activity.timeLimit.TimeManagementUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {
    private static final String TAG = "";
    Context context;
    List<Profile> profileUser;
    OnRecItemClick onRecItemClick;
    User user;

    public ProfileAdapter(User user, List<Profile> profileUser, Context context) {
        this.profileUser = profileUser;
        this.context = context;
        this.user = user;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);

        return new MyViewHolder(view, onRecItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Profile data = profileUser.get(position);
        holder.tvUserName.setText(data.name);
        if (data.timelimit - TimeManagementUtils.getInstance(context).getTimeByProfileName(data.name) < 0) {
            holder.time.setText("0");
        } else {
            holder.time.setText(String.valueOf(data.timelimit - TimeManagementUtils.getInstance(context).getTimeByProfileName(data.name)));

        }
        if (data.timelimit == -1) {
            holder.time.setText("");
        }
        Bitmap image = data.picdecoded;
        holder.profilePic.setImageBitmap(image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked");
                if (context instanceof ProfileManagement) {
                    Log.d(TAG, "onBindViewHolder: starting profile second");
                    Intent intent = new Intent(context, ProfileDetailsActivity.class);
                    Profile model = profileUser.get(holder.getAdapterPosition());
                    intent.putExtra("profilename", model.name);
                    intent.putExtra("user", user.username);
                    Log.e("", user.username);
                    context.startActivity(intent);
                    notifyDataSetChanged();
                    ((Activity) context).finish();
                } else {
                    Log.d(TAG, "onBindViewHolder: starting profile");
                    Intent intent = new Intent(context, HomeActivity.class);
                    Profile model = profileUser.get(holder.getAdapterPosition());
                    intent.putExtra("profile_name", model.name);
                    context.startActivity(intent);
                    //context.startActivity(new Intent(context,HomeActivity.class));
                    //Here we will trigger timer , Timer will start for the profile
                    //We will code here for timer to start

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return profileUser.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView profilePic;
        TextView tvUserName, birthday, time;
        OnRecItemClick onRecItemClick;
        RelativeLayout relativeLayout;


        public MyViewHolder(@NonNull View itemView, OnRecItemClick onRecItemClick) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profileImage);
            tvUserName = itemView.findViewById(R.id.userName);
            relativeLayout = itemView.findViewById(R.id.relTop);
            time = itemView.findViewById(R.id.txtTime);
            this.onRecItemClick = onRecItemClick;
            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {
            onRecItemClick.onItemClick(getAdapterPosition());
        }
    }


    public interface OnRecItemClick {
        void onItemClick(int position);

    }
}
