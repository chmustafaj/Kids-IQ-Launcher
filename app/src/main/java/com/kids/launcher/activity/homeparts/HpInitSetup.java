package com.kids.launcher.activity.homeparts;

import android.content.Context;

import androidx.annotation.NonNull;

import com.kids.launcher.AppObject;
import com.kids.launcher.activity.HomeActivity;
import com.kids.launcher.manager.Setup;
import com.kids.launcher.util.AppManager;
import com.kids.launcher.util.AppSettings;
import com.kids.launcher.util.DatabaseHelper;
import com.kids.launcher.viewutil.DesktopGestureListener.DesktopGestureCallback;

public final class HpInitSetup extends Setup {
    private final AppManager _appLoader;
    private final DatabaseHelper _dataManager;
    private final HpGestureCallback _desktopGestureCallback;
    private final HpEventHandler _eventHandler;
    private final AppSettings _appSettings;

    public HpInitSetup(HomeActivity homeActivity) {
        _appSettings = AppSettings.get();
        _desktopGestureCallback = new HpGestureCallback(_appSettings);
        _dataManager = new DatabaseHelper(homeActivity);
        _appLoader = AppManager.getInstance(homeActivity);
        _eventHandler = new HpEventHandler();
    }

    @NonNull
    public Context getAppContext() {
        return AppObject.get();
    }

    @NonNull
    public AppSettings getAppSettings() {
        return _appSettings;
    }

    @NonNull
    public DesktopGestureCallback getDesktopGestureCallback() {
        return _desktopGestureCallback;
    }

    @NonNull
    public DatabaseHelper getDataManager() {
        return _dataManager;
    }

    @NonNull
    public AppManager getAppLoader() {
        return _appLoader;
    }

    @NonNull
    public EventHandler getEventHandler() {
        return _eventHandler;
    }
}