package com.kids.launcher.fragment;

import android.os.Bundle;

import androidx.preference.Preference;

import com.kids.launcher.R;
import com.kids.launcher.activity.HomeActivity;
import com.kids.launcher.viewutil.DialogHelper;

import net.gsantner.opoc.util.ContextUtils;

public class SettingsAppearanceFragment extends SettingsBaseFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        addPreferencesFromResource(R.xml.preferences_appearance);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        HomeActivity homeActivity = HomeActivity._launcher;
        int key = new ContextUtils(homeActivity).getResId(ContextUtils.ResType.STRING, preference.getKey());
        switch (key) {
            case R.string.pref_key__icon_pack:
                DialogHelper.startPickIconPackIntent(getActivity());
                return true;
        }
        return false;
    }
}
