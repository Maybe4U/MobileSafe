package com.maybe.android.mobilesafe.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2018\1\12 0012.
 */

public class SystemInfoUtil {
    /**
     * 获取正在运行的进程个数
     * @param context
     * @return
     */
    public static int getRunningProcess(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int num = am.getRunningAppProcesses().size();
        return num;
    }

    /**
     * 获取可用内存
     * @param context
     * @return
     */
    public static long getAvailRam(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);
        return info.availMem;
    }


    /**
     * 获取总内存
     * @param context
     * @return
     */
    public static long getTotalRam(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(info);
        return info.totalMem;
    }
}
