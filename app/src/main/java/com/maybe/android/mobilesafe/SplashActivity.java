package com.maybe.android.mobilesafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maybe.android.mobilesafe.activity.HomeActivity;
import com.maybe.android.mobilesafe.utils.LogUtil;
import com.maybe.android.mobilesafe.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Project Name:
 * Class desc. :启动页面
 * 1.显示LOGO
 * 2.判断是否有网络
 * 3.是否升级
 * 4.判断合法性
 * 5.校验是否有sdcard
 *     date    : 2017/12/8
 *    author   : Maybe
 **/

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int ENTER_HOME = 1;
    private static final int SHOW_UPDATE_DAILOG = 2;
    private static final int URL_ERROR = 3;
    private static final int NET_ERROR = 4;
    private static final int JSON_ERROR = 5;

    private TextView tvSplashVersion;
    private TextView tvSplashUpdateInfo;

    private String version;
    private String description;
    private String apkurl;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case ENTER_HOME://进入主界面
                    enterHome();
                    LogUtil.e(TAG,"进入主界面");
                    break;
                case SHOW_UPDATE_DAILOG://弹出升级对话框
                    LogUtil.e(TAG,"发现新版本，弹出升级对话框");
                    showUpdateDialog();
                    //enterHome();
                    break;
                case URL_ERROR://URL异常
                    enterHome();
                    Toast.makeText(getApplicationContext(),"URL异常",Toast.LENGTH_SHORT).show();
                    break;
                case NET_ERROR://网络异常
                    enterHome();
                    Toast.makeText(getApplicationContext(),"网络异常",Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR://JSON解析异常
                    enterHome();
                    Toast.makeText(getApplicationContext(),"JSON解析异常",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;


            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
        //新版本升级提示标志位 true为提示
        boolean isCheck = sp.getBoolean("updateStatus",false);

        tvSplashVersion = (TextView)findViewById(R.id.tv_splash_version);
        //设置版本名称
        tvSplashVersion.setText("version: " + getVerionName());

        tvSplashUpdateInfo = (TextView)findViewById(R.id.tv_splash_updateinfo) ;
        //拷贝数据库
        copyDB("address.db");
        copyDB("antivirus.db");
        //软件的升级
        if(isCheck){
            checkVersion();
        }else{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterHome();
                }
            }, 2000);
        }

    }
    /**
     * fun desc. :把assets目录下的本地数据库（电话号码）拷贝到制定目录下
     * params.   :
     * @return   :
     */
    private void copyDB(String dbName) {
        File file = new File(getFilesDir(),dbName);
        if(file.exists()&&file.length()>0){
            //数据库已经存在
        }else {
            try {
                InputStream is = getAssets().open(dbName);
                FileOutputStream fos = new FileOutputStream(file);
                LogUtil.e("getFilesDir()",getFilesDir() + "");
                int len = 0;
                byte buffer [] = new byte[1024];
                while ((len = is.read(buffer)) != -1){
                    fos.write(buffer,0,len);
                }
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * fun desc. :检查版本升级
     * params.   :
     * @return   :
     */
    private void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                //记录启动界面开始时间
                long startTime = System.currentTimeMillis();
                Message msg = Message.obtain();
                //请求网络，得到最新版本信息
                try {
                    URL url = new URL(getString(R.string.serverurl));
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int code = conn.getResponseCode();
                    if(code == 200){
                        //请求成功
                        InputStream is = conn.getInputStream() ;
                        String result = StreamTools.readFromStream(is);

                        LogUtil.e(TAG,"result = " + result);

                        //解析JSON
                        JSONObject object = new JSONObject(result);
                        version = (String) object.get("version");
                        description = (String) object.get("description");
                        apkurl = (String) object.get("apkurl");
                        LogUtil.e(TAG,"version = " + version);
                        //Thread.sleep(2000);
                        if(getVerionName().equals(version)){
                            msg.what = ENTER_HOME;
                        }else{
                            msg.what = SHOW_UPDATE_DAILOG;
                            LogUtil.e(TAG,"msg.what = " + msg.what);
                        }

                    }
                } catch (MalformedURLException e) {
                    //URL异常
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    //网络异常
                    e.printStackTrace();
                    msg.what = NET_ERROR;
                } catch (JSONException e) {
                    //JSON解析异常
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                }finally {
                    //记录启动界面发消息之前时间
                    long endTime = System.currentTimeMillis();
                    long loginTime = endTime - startTime;
                    if(loginTime < 3000){
                        SystemClock.sleep(2000-loginTime);
                    }
                    handler.sendMessage(msg);
                    LogUtil.e(TAG,"发消息");
                }
            }
        }.start();
    }

    /**
     * fun desc. :得到版本号
     * params.   :
     * @return   :
     */
    private String getVerionName(){
        //包管理器
        PackageManager pm = getPackageManager();
        try {
            //功能清单文件
            PackageInfo info = pm.getPackageInfo(getPackageName(),0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * fun desc. :进入主页面
     * params.   :
     * @return   :
     */
    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //关闭启动页面
        finish();
    }
    /**
     * fun desc. :弹出升级对话框
     * params.   :
     * @return   :
     */
    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(description);

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                enterHome();
            }
        });

        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });

        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    FinalHttp fh = new FinalHttp();
                    fh.download(apkurl, Environment.getExternalStorageDirectory() + "/mobilesafe2.0.apk",
                            new AjaxCallBack<File>() {
                                @Override
                                public void onLoading(long count, long current) {
                                    super.onLoading(count, current);
                                    tvSplashUpdateInfo.setVisibility(View.VISIBLE);
                                    int progress = (int) (current * 100 / count);
                                    tvSplashUpdateInfo.setText("下载进度：" + progress + "%");
                                }

                                @Override
                                public void onSuccess(File file) {
                                    super.onSuccess(file);
                                    Toast.makeText(getApplicationContext(),"下载成功...",Toast.LENGTH_SHORT).show();
                                    tvSplashUpdateInfo.setText("下载完成");

                                    //安装APK
                                    installAPK(file);

                                }
                                /**
                                 * fun desc. :安装APK
                                 * params.   :
                                 * android源码中的安装应用的动作
                                 *<action android:name="android.intent.action.VIEW" />
                                 *<action android:name="android.intent.action.INSTALL_PACKAGE" />
                                 *<category android:name="android.intent.category.DEFAULT" />
                                 *<data android:scheme="file" />
                                 *<data android:mimeType="application/vnd.android.package-archive" />
                                 * @return   :
                                 */
                                private void installAPK(File file) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.INSTALL_PACKAGE");
                                    intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Throwable t, int errorNo, String strMsg) {
                                    super.onFailure(t, errorNo, strMsg);
                                    Toast.makeText(getApplicationContext(),"下载失败",Toast.LENGTH_SHORT).show();
                                    t.printStackTrace();
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(),"SDCard异常",Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.show();
    }

}
