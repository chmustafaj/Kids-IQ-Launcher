package com.kids.launcher.fragment.profileFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kids.launcher.R;

public class CreateProfileFragment extends Fragment {
    private static final String TAG = "";
    EditText firstName, lastName, birthday;
    Button done, check;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_profile, container, false);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        birthday = view.findViewById(R.id.birthday);
        done = view.findViewById(R.id.btnDone);
        check = view.findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment ProfileFrag = new ProfileFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container2, ProfileFrag).commit();
            }
        });
        TextView label = view.findViewById(R.id.lable);
//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (firstName== null && lastName == null && birthday == null) {
//                    label.setText("Enter All Field");
//                } else {
//                    AppDatabase appDatabase = Room.databaseBuilder(requireContext(), AppDatabase.class, "my_database").allowMainThreadQueries()
//                            .fallbackToDestructiveMigration().build();
//                    ProfileDao profileDao = appDatabase.userDao();
//                    Boolean check = profileDao.isExists(Integer.parseInt(firstName.getText().toString()));
//                    if (check == false) {
//                        profileDao.insertAll(new ProfilePojo(Integer.parseInt(firstName.getText().toString()), firstName.getText().toString(), lastName.getText().toString(), birthday.getText().toString()));
//                        firstName.setText("");
//                        lastName.setText("");
//                        birthday.setText("");
//                        label.setText("Success");
//                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show();
//                    } else {
//                        firstName.setText("");
//                        lastName.setText("");
//                        birthday.setText("");
//                        label.setText("Exists");
//                        Toast.makeText(requireContext(), "Exists", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//
//            }
//        });
        return view;
    }

//    class BackgroundWork extends Thread {
//        public void run() {
//            super.run();
//
//
//
//        }
//    }

}