package com.maybe.android.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018/1/23.
 */

public class KillBgProcessService extends Service {

    private IntentFilter filter;
    private ScreenOffReceiver receiver;

    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new ScreenOffReceiver();
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        receiver = null;
        filter = null;
    }

    private class ScreenOffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            for(ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()){
                am.killBackgroundProcesses(info.processName);
            }

        }
    }
}
