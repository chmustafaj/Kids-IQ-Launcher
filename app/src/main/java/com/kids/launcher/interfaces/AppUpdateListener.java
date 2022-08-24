package com.kids.launcher.interfaces;

import com.kids.launcher.model.App;

import java.util.List;

public interface AppUpdateListener {
    boolean onAppUpdated(List<App> apps);
}
