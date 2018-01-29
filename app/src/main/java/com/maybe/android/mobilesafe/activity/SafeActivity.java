package com.maybe.android.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.utils.LogUtil;
import com.maybe.android.mobilesafe.view.PwdDialog;
import com.maybe.android.mobilesafe.view.PwdInputView;

/**
 * Created by Administrator on 2017/12/12.
 */

public class SafeActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private TextView safeNumber;
    private TextView isLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sp = getSharedPreferences("config",MODE_PRIVATE);
        //isSet 是否设置向导标志位，false为未设置
        boolean isSet = sp.getBoolean("isSet",false);
        super.onCreate(savedInstanceState);
        if(isSet){
            setContentView(R.layout.activity_safe);
            //显示安全号码
            safeNumber = (TextView)findViewById(R.id.safe_number);
            String number = sp.getString("number","");
            LogUtil.e("number",number);
            safeNumber.setText(number);
            //显示防盗保护状态
            isLock = (TextView)findViewById(R.id.islock_tv);
            boolean isSecurity = sp.getBoolean("isSecurity",false);
            if(isSecurity){
                isLock.setText("开启");
                isLock.setTextColor(Color.parseColor("#6639B3A9"));
            }else {
                isLock.setText("关闭");
                isLock.setTextColor(Color.parseColor("#66FF0000"));
            }
//            //设置按钮-重新进入向导-背景颜色
//            TextView reWizardBtn = (TextView)findViewById(R.id.reWizard_btn) ;
//            reWizardBtn.setBackgroundColor(Color.parseColor("#39B3A9"));

            showCollapsingLayout();
        }else {
            enterWizard();
        }
    }

    /**
     * fun desc. :重新进入设置向导按钮
     * params.   :
     * @return   :
     */
    public void reWizard(View view){
        enterWizard();
    }

    /**
     * fun desc. :进入设置向导界面
     * params.   :
     * @return   :
     */
    private void enterWizard() {
        Intent intent = new Intent(this,WizardActivity01.class);
        startActivity(intent);
        finish();
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
        collapsingToolbar.setTitle("手机防盗");
        Glide.with(this).load(R.drawable.mobile_safe_img).into(funImageView);
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
