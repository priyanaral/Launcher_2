package com.android.myapplication;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String appName;
    private Drawable icon;
    private String packageName;

    public AppInfo(String appName, Drawable icon, String packageName) {
        this.appName = appName;
        this.icon = icon;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() {
        return packageName;
    }
}