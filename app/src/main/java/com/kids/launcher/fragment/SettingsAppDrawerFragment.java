package com.kids.launcher.fragment;

import android.os.Bundle;

import com.kids.launcher.R;

public class SettingsAppDrawerFragment extends SettingsBaseFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        addPreferencesFromResource(R.xml.preferences_app_drawer);
    }
}