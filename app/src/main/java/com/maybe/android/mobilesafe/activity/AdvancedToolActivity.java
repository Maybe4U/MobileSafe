package com.maybe.android.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.utils.SmsBackupUtil;

import java.io.File;


public class AdvancedToolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_tool);
        showCollapsingLayout();
        CardView cardView = (CardView)findViewById(R.id.check_number);
        CardView smsBackup = (CardView)findViewById(R.id.sms_backup);
        /**
         * fun desc. :电话号码归属地查询按键
         * params.   :
         * @return   :
         */
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdvancedToolActivity.this,NumberQuery.class);
                startActivity(intent);
            }
        });
        /**
         * fun desc. :备份短信按钮
         * params.   :
         * @return   :
         */
        smsBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    final File file = new File(Environment.getExternalStorageDirectory(),"smsBackup.xml");
                    final ProgressDialog dialog = new ProgressDialog(AdvancedToolActivity.this);
                    dialog.setTitle("正在备份短信...");
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.show();
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                SmsBackupUtil.smsBackup(AdvancedToolActivity.this, file.getAbsolutePath(), new SmsBackupUtil.SmsBackupCallBack() {
                                    @Override
                                    public void backupBefore(int total) {
                                        dialog.setMax(total);
                                    }

                                    @Override
                                    public void backupProgress(int progress) {
                                        dialog.setProgress(progress);
                                    }
                                });
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                                dialog.dismiss();
                            }
                        }
                    }.start();

                }else{
                    Toast.makeText(AdvancedToolActivity.this,"SD卡异常",Toast.LENGTH_SHORT).show();
                }
            }
        });
        /**
         * fun desc. :应用锁按钮
         * params.   :
         * @return   :
         */
        CardView appLock = (CardView)findViewById(R.id.app_lock);
        appLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdvancedToolActivity.this,AppLockActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * fun desc. :初始化可折叠标题栏
     * params.   :
     * @return   :
     */
    private void showCollapsingLayout(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                findViewById(R.id.collapsing_toolbar);
        ImageView funImageView = (ImageView)findViewById(R.id.fun_image_view);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("高级工具");
        Glide.with(this).load(R.drawable.advanced_tool_img).into(funImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
