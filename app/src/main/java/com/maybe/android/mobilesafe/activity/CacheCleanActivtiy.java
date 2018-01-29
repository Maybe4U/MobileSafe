package com.maybe.android.mobilesafe.activity;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;

import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.maybe.android.mobilesafe.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2018/1/27.
 */

public class CacheCleanActivtiy extends AppCompatActivity {

    public static final int SCANNING = 0;
    public static final int SCANNED = 1;
    public static final int GETCACHE = 2;
    private TextView tv_scan_progress;
    private ProgressBar pb_scan_progress;
    private Message msg;
    private PackageManager pm;
    private LinearLayout ll_cache_container;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCANNING:
                    String name = (String) msg.obj;
                    tv_scan_progress.setText(name);
                    break;
                case SCANNED:
                    tv_scan_progress.setText("扫描完成");
                    break;
                case GETCACHE:
                    CacheInfo cacheInfo = (CacheInfo) msg.obj;

                    View view = View.inflate(CacheCleanActivtiy.this,R.layout.cache_info_item,null);
                    ImageView app_icon = (ImageView)view.findViewById(R.id.app_icon);
                    TextView app_name = (TextView)view.findViewById(R.id.app_name);
                    TextView cache_size = (TextView)view.findViewById(R.id.cache_size);
                    app_icon.setImageDrawable(cacheInfo.icon);
                    app_name.setText(cacheInfo.name);
                    cache_size.setText(Formatter.formatFileSize(CacheCleanActivtiy.this,cacheInfo.cacheSize));

                    ll_cache_container.addView(view,0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);

        initView();

        setEvent();
    }

    private void setEvent() {
        new Thread(){
            @Override
            public void run() {
                //获取已经安装的软件包信息
                pm = getPackageManager();
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                //初始化进度条
                //设置进度条最大值为包的数量
                pb_scan_progress.setMax(packageInfoList.size());
                int progress = 0;
                //延时，模拟初始化
                SystemClock.sleep(1000);
                for(PackageInfo packageInfo: packageInfoList){
                    String packageName = packageInfo.applicationInfo.packageName;
                    String name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    //利用反射获取缓存大小
                    try {
                        Method method = PackageManager.class.getMethod("getPackageSizeInfo",String.class,IPackageStatsObserver.class);
                        method.invoke(pm, packageName,new MyIPackageStatsObserver());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //设置进度条
                    progress ++;
                    pb_scan_progress.setProgress(progress);
                    //扫描过程中发送消息到Handler
                    msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = name;
                    handler.sendMessage(msg);
                    //延时休眠
                    SystemClock.sleep(50);
                }
                //扫描完成发送消息到Handler
                msg = Message.obtain();
                msg.what = SCANNED;
                handler.sendMessage(msg);
            }
        }.start();

    }

    private void initView() {
        //扫描显示文本
        tv_scan_progress = (TextView)findViewById(R.id.tv_scan_progress);
        //扫描显示进度
        pb_scan_progress = (ProgressBar)findViewById(R.id.pb_scan_progress);
        //显示扫描到的缓存信息
        ll_cache_container = (LinearLayout)findViewById(R.id.ll_cache_container);
    }

    private class CacheInfo{
        /**
         * class Name  :CacheInfo
         * Class desc. :被扫描的包名图标以及缓存大小
         *     date    : 2018/1/27
         *    author   : Maybe
         */
        String name;
        Drawable icon;
        String packageName;
        long cacheSize;
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub{

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize ;
            CacheInfo cacheInfo = new CacheInfo();
            if (cacheSize > 0){
                cacheInfo.cacheSize = cacheSize;
                cacheInfo.packageName = pStats.packageName;
                try {
                    cacheInfo.name =  pm.getApplicationInfo(pStats.packageName,0).loadLabel(pm).toString();
                    cacheInfo.icon = pm.getApplicationInfo(pStats.packageName,0).loadIcon(pm);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = GETCACHE;
                msg.obj = cacheInfo;
                handler.sendMessage(msg);
            }
        }
    }
    //一键清理点击事件
    public void cleanCache(View view){
        Log.e("...","...");
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods){
            if("freeStorageAndNotify".equals(method.getName())){
                try {
                    method.invoke(pm, Integer.MAX_VALUE,new IPackageDataObserver.Stub(){
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
