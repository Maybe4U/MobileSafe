package com.maybe.android.mobilesafe.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2018\1\5 0005.
 */

public class AppInfo {
    private Drawable icon;
    private String name;
    private String packageName;

    /**
     * true 安装在rom  false 安装在SD card
     */
    private boolean isRom;

    /**
     * true 用户应用  false 安装在系统应用
     */
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean rom) {
        isRom = rom;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", isRom=" + isRom +
                ", isUser=" + isUser +
                '}';
    }
}
