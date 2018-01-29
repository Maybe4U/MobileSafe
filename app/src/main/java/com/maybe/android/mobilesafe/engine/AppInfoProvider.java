package com.maybe.android.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.maybe.android.mobilesafe.utils.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\1\5 0005.
 */

public class AppInfoProvider {
    /**
     * 获得手机里面所有的应用信息
     *
     */
    public static List<AppInfo> getAllAppInfo(Context context){
        List<AppInfo> appInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        for (PackageInfo info:
             infos) {
            AppInfo appInfo = new AppInfo();
            String packageName = info.packageName;
            appInfo.setPackageName(packageName);
            Drawable icon = info.applicationInfo.loadIcon(pm);
            appInfo.setIcon(icon);
            String name = info.applicationInfo.loadLabel(pm).toString();
            appInfo.setName(name);

            int flag = info.applicationInfo.flags;
            if((flag & ApplicationInfo.FLAG_SYSTEM) == 0){
                appInfo.setUser(true);
            }else {
                appInfo.setUser(false);
            }

            if((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
                appInfo.setRom(true);
            }else {
                appInfo.setRom(false);
            }

            appInfos.add(appInfo);
        }
        return appInfos;
    }

}
