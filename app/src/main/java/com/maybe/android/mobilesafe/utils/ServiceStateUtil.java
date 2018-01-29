package com.maybe.android.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * class Name  :ServiceStateUtil
 * Class desc. :监听服务运行状态
 *     date    : 2017/12/16
 *    author   : Maybe
 */

public class ServiceStateUtil {
    /**
     * method desc.  :serviceState 判断当前服务是否运行
     * params        :[context,name]       当前服务
     * return        :boolean       true 在运行  false 不运行
     */
    public static boolean serviceState(Context context, String name){
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo serviceInfo:serviceInfoList) {
            String serviceName = serviceInfo.service.getClassName();
            if(name.equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
