package com.maybe.android.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.maybe.android.mobilesafe.activity.LockPwdActivity;
import com.maybe.android.mobilesafe.db.dao.AppLockDAO;

import java.util.List;

/**
 * 监听启动程序
 */

public class WatchDogService extends Service {

    private ActivityManager am;
    private AppLockDAO dao;

    private boolean flag = false;
    private String stopPackageName;
    private WatchDogReceiver receiver;
    private List<String> packageNames;
    private MyContentObserver observer;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service","服务已经启动");
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao = new AppLockDAO(this);
        packageNames = dao.queryAll();
        //注册广播接收
        receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.maybe.android.mobilesafe.cancelListener");
        registerReceiver(receiver,filter);
        Uri uri = Uri.parse("content://com.maybe.android.mobilesafe.datachange");
        observer = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(uri,true, observer);

            new Thread(){
                @Override
                public void run() {
                    flag = true;
                    while(flag){
                        ActivityManager.RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
                        String packageName = taskInfo.topActivity.getPackageName();
                        if(packageNames.contains(packageName)){
                            if(packageName.equals(stopPackageName)){
                                //不启动密码界面
                            }else {
                                Intent intent = new Intent(WatchDogService.this, LockPwdActivity.class);
                                intent.putExtra("packageName",packageName);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                        SystemClock.sleep(100);
                    }
                }
            }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
    }

    private class WatchDogReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            stopPackageName = intent.getStringExtra("packageName");
        }
    }

    private class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.e("ContentObserver","数据变化了");
            packageNames = dao.queryAll();
        }
    }
}

