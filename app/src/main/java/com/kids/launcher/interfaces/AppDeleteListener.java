package com.kids.launcher.interfaces;

import com.kids.launcher.model.App;

import java.util.List;

public interface AppDeleteListener {
    boolean onAppDeleted(List<App> apps);
}
