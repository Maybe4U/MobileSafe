package com.maybe.android.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.db.dao.AntiVirusQueryDAO;
import com.maybe.android.mobilesafe.utils.MD5Utils;
import com.maybe.android.mobilesafe.utils.SmsBackupUtil;

import java.io.File;
import java.util.List;


public class MobileVirusActivity extends AppCompatActivity {

    public static final int SCANNING = 0;  //正在扫描
    public static final int SCANNED = 1;   //扫描完成


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScanInfo scanInfo = (ScanInfo) msg.obj;
            switch (msg.what) {
                case SCANNING:
                    tv_scan_progress.setText("正在扫描： " + scanInfo.name);
                    TextView tv_scaninfo = new TextView(MobileVirusActivity.this);
                    if(scanInfo.isVirus){
                        tv_scaninfo.setTextColor(Color.RED);
                        tv_scaninfo.setText("发现病毒： " + scanInfo.name);
                    }else {
                        tv_scaninfo.setTextColor(Color.BLACK);
                        tv_scaninfo.setText("扫描安全： " + scanInfo.name);
                    }
                    ll_container.addView(tv_scaninfo,0);
                    break;
                case SCANNED:
                    tv_scan_progress.setText("扫描完成！");
                    iv_scan.clearAnimation();
                    Toast.makeText(MobileVirusActivity.this,"您的手机很安全！",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private TextView tv_scan_progress;
    private List<PackageInfo> packageInfos;
    private ImageView iv_scan;
    private LinearLayout ll_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_virus);
        showCollapsingLayout();
        tv_scan_progress = (TextView) findViewById(R.id.tv_scan_progress);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        ll_container = (LinearLayout)findViewById(R.id.ll_container);

        final ProgressBar pb_scan_progress = (ProgressBar) findViewById(R.id.pb_scan_progress);
        //查杀动画
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scan.startAnimation(ra);


        new Thread() {

            @Override
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PackageManager pm = getPackageManager();
                packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                        + PackageManager.GET_SIGNATURES);
                //设置进度条最大值
                pb_scan_progress.setMax(packageInfos.size());
                //初始化进度条进度
                int progress = 0;
                for (PackageInfo info : packageInfos) {
                    ScanInfo scanInfo = new ScanInfo();

                    String name = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.name = name;
                    String packageName = info.packageName;
                    scanInfo.packageName = packageName;

                    String md5 = MD5Utils.encoder(info.signatures[0].toCharsString());
                    String result = AntiVirusQueryDAO.getDesc(md5);
                    if (result != null) {
                        scanInfo.isVirus = true;
                        Log.e("--------->", "是病毒");
                    } else {
                        scanInfo.isVirus = false;
                    }

                    //发消息
                    Message msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = scanInfo;
                    handler.sendMessage(msg);
                    progress++;
                    pb_scan_progress.setProgress(progress);

                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //发消息
                Message msg = Message.obtain();
                msg.what = SCANNED;
                handler.sendMessage(msg);
            }
        }.start();

    }

    /**
     * fun desc. :初始化可折叠标题栏
     * params.   :
     *
     * @return :
     */
    private void showCollapsingLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        ImageView funImageView = (ImageView) findViewById(R.id.fun_image_view);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("手机杀毒");
        Glide.with(this).load(R.drawable.mobile_virus_img).into(funImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ScanInfo {
        String name;
        String packageName;
        boolean isVirus;
    }
}
