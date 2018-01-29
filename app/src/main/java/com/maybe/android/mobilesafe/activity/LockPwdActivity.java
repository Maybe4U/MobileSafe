package com.maybe.android.mobilesafe.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.utils.AppInfo;

/**
 * Created by Administrator on 2018/1/25.
 */

public class LockPwdActivity extends AppCompatActivity {

    private ImageView iv_app_icon;
    private TextView tv_app_name;
    private EditText lock_pwd;
    private Button lock_pwd_ok;
    private Intent intent;
    private ActivityManager am;
    private String packageName;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_lock_pwd);
        //初始化view
        initView();

        setEvents();
    }
    public void pwdOk(View v){
        //1.得到密码
        String pwd = lock_pwd.getText().toString().trim();
        //2.判断是否为空
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(pwd.equals("123")){
            Intent cancelIntent = new Intent("com.maybe.android.mobilesafe.cancelListener");
            cancelIntent.putExtra("packageName",packageName);
            sendBroadcast(cancelIntent);
            finish();
        }else {
            Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
            //return;
        }
    }
    //处理事件
    private void setEvents() {
        intent = getIntent();
        packageName = intent.getStringExtra("packageName");
        pm = getPackageManager();
        try {
            Drawable icon = pm.getApplicationInfo(packageName,0).loadIcon(pm);
            iv_app_icon.setImageDrawable(icon);
            String name = pm.getApplicationInfo(packageName,0).loadLabel(pm).toString();
            tv_app_name.setText(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //初始化view
    private void initView() {
        iv_app_icon = (ImageView)findViewById(R.id.iv_app_icon);
        tv_app_name = (TextView)findViewById(R.id.tv_app_name);
        lock_pwd = (EditText)findViewById(R.id.lock_pwd);
        lock_pwd_ok = (Button)findViewById(R.id.lock_pwd_ok);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();//看不见时执行
    }
}
