package com.kids.launcher.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kids.launcher.R;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.User;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEarnTimeAdapter extends RecyclerView.Adapter<ProfileEarnTimeAdapter.ViewHolder> {
    private static final String TAG = "";
    ArrayList<Profile> profiles = new ArrayList<>(Collections.emptyList());
    Profile profile = new Profile();  //This is the profile to add the 'fun profiles' to
    String fromActivity; //to tell if we are from create profile or profile details activity
    String username;
    int index; //the index from the profile details activity

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (profile.funProfiles.contains(profiles.get(position).name)) {
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#b3b3b3"));
        }
        holder.tvUserName.setText(profiles.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profile.funProfiles.contains(profiles.get(position).name)) {
                    Toast.makeText(view.getContext(), "Profile already added", Toast.LENGTH_SHORT).show();
                } else {
                    profile.funProfiles.add(profiles.get(position).name);
                    profile.isStudyProfile = true;
                    holder.relativeLayout.setBackgroundColor(Color.parseColor("#b3b3b3"));
                    Toast.makeText(view.getContext(), "fun profile added", Toast.LENGTH_SHORT).show();
                    if (fromActivity.equals("profile_details")) {
                        User user = PrefUtils.fetchUser(username, view.getContext(), new Gson());
                        user.profiles.get(index).funProfiles = profile.funProfiles;
                        user.profiles.get(index).isStudyProfile = profile.isStudyProfile;
                        PrefUtils.saveUser(user, view.getContext(), new Gson());
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void setProfiles(ArrayList<Profile> profiles, Profile p) {
        this.profiles = profiles;
        profile = p;
        notifyDataSetChanged();
    }

    public void setUserName(String un) {
        this.username = un;
    }

    public void setFromActivity(String fromActivity) {
        this.fromActivity = fromActivity;
    }

    public void setProfileIndex(int i) {
        this.index = i;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        TextView tvUserName, birthday, time, noOtherProfiles;
        ProfileAdapter.OnRecItemClick onRecItemClick;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profileImage);
            tvUserName = itemView.findViewById(R.id.userName);
            relativeLayout = itemView.findViewById(R.id.relTop);
            time = itemView.findViewById(R.id.txtTime);
            noOtherProfiles = itemView.findViewById(R.id.txtNoOtherProfiles);
        }
    }
}
