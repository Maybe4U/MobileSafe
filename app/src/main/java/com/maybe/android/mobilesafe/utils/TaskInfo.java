package com.maybe.android.mobilesafe.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;

/**
 * Created by Administrator on 2018\1\13 0013.
 */

public class TaskInfo {
    private Drawable icon;
    private String name;

    private boolean isChecked;

    private  String packageName;
    //单位byte
    private long memoinfosize;

    private boolean isUser;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getMemoinfosize() {
        return memoinfosize;
    }

    public void setMemoinfosize(long memoinfosize) {
        this.memoinfosize = memoinfosize;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", memoinfosize=" + memoinfosize +
                ", isUser=" + isUser +
                '}';
    }
}
