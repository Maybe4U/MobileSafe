package com.maybe.android.mobilesafe.engine;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.maybe.android.mobilesafe.utils.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\1\13 0013.
 */

public class TaskInfoProvider {

    public static List<TaskInfo> getAllTaskInfos(Context context){
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : processInfos) {
            TaskInfo taskInfo = new TaskInfo();
            String packageName = appProcessInfo.processName;
            taskInfo.setPackageName(packageName);
            Debug.MemoryInfo memoInfo = am.getProcessMemoryInfo(new int[]{appProcessInfo.pid})[0];
            long memoInfoSize = memoInfo.getTotalPrivateDirty() * 1024;
            taskInfo.setMemoinfosize(memoInfoSize);
            try {
                //图标
                Drawable icon = pm.getPackageInfo(packageName,0).applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);
                //名称
                String name = pm.getPackageInfo(packageName,0).applicationInfo.loadLabel(pm).toString();
                taskInfo.setName(name);
                int flag = pm.getPackageInfo(packageName,0).applicationInfo.flags;

                if((flag & ApplicationInfo.FLAG_SYSTEM) == 0){
                    //系统进程
                    taskInfo.setUser(true);
                }else {
                    //用户进程
                    taskInfo.setUser(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
