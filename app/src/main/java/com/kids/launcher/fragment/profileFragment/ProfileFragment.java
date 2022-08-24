package com.kids.launcher.fragment.profileFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kids.launcher.Adapters.UserAdapter;
import com.kids.launcher.R;
import com.kids.launcher.model.ProfileModel;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;

public class ProfileFragment extends Fragment {
    ProfileModel profileModel;
    UserAdapter profileAdapter;
    RecyclerView recyclerViewProfile;
    RelativeLayout relCreateProfile;
    EditText edUserName, edBirthday;
    Profile profile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        relCreateProfile = view.findViewById(R.id.relCreateProfile);
        recyclerViewProfile = view.findViewById(R.id.profileRecyclerView);

        relCreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment createProfileFragment = new CreateProfileFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, createProfileFragment).commit();

            }
        });


        profileAdapter = new UserAdapter(PrefUtils.fetchAllUsers(requireContext(), new Gson()), requireContext());
        recyclerViewProfile.setAdapter(profileAdapter);
        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(requireContext()));

        return view;
    }

}