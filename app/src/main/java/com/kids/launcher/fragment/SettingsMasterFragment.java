package com.kids.launcher.fragment;

import static com.kids.launcher.widget.AppDrawerController.Mode.GRID;
import static com.kids.launcher.widget.AppDrawerController.Mode.PAGE;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;

import com.kids.launcher.R;
import com.kids.launcher.activity.HideAppsActivity;
import com.kids.launcher.activity.HomeActivity;
import com.kids.launcher.util.AppSettings;

import net.gsantner.opoc.util.ContextUtils;

import java.util.Locale;

public class SettingsMasterFragment extends SettingsBaseFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        addPreferencesFromResource(R.xml.preferences_master);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        super.onPreferenceTreeClick(preference);
        HomeActivity homeActivity = HomeActivity._launcher;
        int key = new ContextUtils(homeActivity).getResId(ContextUtils.ResType.STRING, preference.getKey());
        switch (key) {
            case R.string.pref_key__cat_hide_apps:
                Intent intent = new Intent(getActivity(), HideAppsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;

        }

        return false;
    }

    @Override
    public void updateSummaries() {
        Preference categoryDesktop = findPreference(getString(R.string.pref_key__cat_desktop));
        Preference categoryDock = findPreference(getString(R.string.pref_key__cat_dock));
        Preference categoryAppDrawer = findPreference(getString(R.string.pref_key__cat_app_drawer));
        Preference categoryAppearance = findPreference(getString(R.string.pref_key__cat_appearance));

        categoryDesktop.setSummary(String.format(Locale.ENGLISH, "%s: %d x %d", getString(R.string.pref_title__size), AppSettings.get().getDesktopColumnCount(), AppSettings.get().getDesktopRowCount()));
        categoryDock.setSummary(String.format(Locale.ENGLISH, "%s: %d x %d", getString(R.string.pref_title__size), AppSettings.get().getDockColumnCount(), AppSettings.get().getDockRowCount()));
        categoryAppearance.setSummary(String.format(Locale.ENGLISH, "%s: %ddp", getString(R.string.pref_title__icons), AppSettings.get().getIconSize()));

        switch (AppSettings.get().getDrawerStyle()) {
            case GRID:
                categoryAppDrawer.setSummary(String.format("%s: %s", getString(R.string.pref_title__style), getString(R.string.vertical_scroll_drawer)));
                break;
            case PAGE:
            default:
                categoryAppDrawer.setSummary(String.format("%s: %s", getString(R.string.pref_title__style), getString(R.string.horizontal_paged_drawer)));
                break;
        }
    }
}
