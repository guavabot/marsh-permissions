package com.guavabot.marshpermissions.applist;

import com.guavabot.marshpermissions.model.App;

import java.util.List;

/**
 * View that displays a list of apps.
 */
public interface AppListView {

    /**
     * Navigate to the Android application info screen for a package.
     */
    void startAppInfo(String packageName);

    /**
     * Display this list of apps.
     */
    void setItems(List<App> apps);
}