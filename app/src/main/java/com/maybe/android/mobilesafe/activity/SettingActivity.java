package com.maybe.android.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.service.BlackNumberService;
import com.maybe.android.mobilesafe.service.NumAddressService;
import com.maybe.android.mobilesafe.service.WatchDogService;
import com.maybe.android.mobilesafe.utils.LogUtil;
import com.maybe.android.mobilesafe.utils.ServiceStateUtil;
import com.maybe.android.mobilesafe.view.ClickView;
import com.maybe.android.mobilesafe.view.SwitchView;


public class SettingActivity extends AppCompatActivity {
    public static final String FUN_NAME = "fun_name";
    public static final String FUN_IMAGE_ID = "fun_image";
    public static final String SWITCH_ON = "当前自动更新已经开启";
    public static final String SWITCH_OFF = "当前自动更新已经关闭";
    public static final String CALL_SWITCH_ON = "来电归属地显示已经开启";
    public static final String CALL_SWITCH_OFF = "来电归属地显示已经关闭";
    public static final String BLOCK_SWITCH_ON = "黑名单拦截已经开启";
    public static final String BLOCK_SWITCH_OFF = "黑名单拦截已经关闭";
    public static final String APP_LOCK_SWITCH_ON = "应用锁已经开启";
    public static final String APP_LOCK_SWITCH_OFF = "应用锁已经关闭";

    private int[] funImageId = {R.drawable.mobile_safe_img, R.drawable.comm_guard_img, R.drawable.app_manager_img,
            R.drawable.pid_control_img, R.drawable.data_count_img, R.drawable.mobile_virus_img,
            R.drawable.cache_clean_img, R.drawable.advanced_tool_img, R.drawable.setting_center_img};

    private SharedPreferences sp;
    private SwitchView sv;
    private boolean isRunning;
    private boolean isBlock;
    private ClickView changeAddDialog;
    private SwitchView blockBlackNum;
    private SwitchView sw_app_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //显示可折叠标题栏
        showCollapsingLayout();

        //新版本提示更新对话框开关
        updateSet();

        //黑名单拦截设置开关
        blackNumberSet();

        //来电归属地设置开关
        callAddressSet();

        //归属地提示框风格设置
        changeAddressDialog();

        //归属地提示框显示位置设置
        changeAddressLoc();

        //应用锁服务开启和关闭设置
        appLockSet();
    }




    @Override
    protected void onResume() {
        super.onResume();
        isRunning = ServiceStateUtil.serviceState(SettingActivity.this, "com.maybe.android.mobilesafe.service.NumAddressService");
        if (isRunning) {
            sv.sw.setChecked(true);
            sv.setText(CALL_SWITCH_ON);
            sv.setTextColor(Color.parseColor("#66000000"));
        } else {
            sv.sw.setChecked(false);
            sv.setTextColor(Color.parseColor("#66FF0000"));
            sv.setText(CALL_SWITCH_OFF);
        }


        isBlock = ServiceStateUtil.serviceState(SettingActivity.this, "com.maybe.android.mobilesafe.service.BlackNumberService");
        if (isBlock) {
            blockBlackNum.sw.setChecked(true);
            blockBlackNum.setText(BLOCK_SWITCH_ON);
            blockBlackNum.setTextColor(Color.parseColor("#66000000"));
        } else {
            blockBlackNum.sw.setChecked(false);
            blockBlackNum.setTextColor(Color.parseColor("#66FF0000"));
            blockBlackNum.setText(BLOCK_SWITCH_OFF);
        }
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
        collapsingToolbar.setTitle("设置中心");
        Glide.with(this).load(R.drawable.setting_center_img).into(funImageView);
    }


    /**
     * fun desc. :新版本提示更新对话框开关
     * params.   :
     *
     * @return :
     */
    private void updateSet() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        final SwitchView switchView = (SwitchView) findViewById(R.id.slt);

        //返回后再次进入保持之前状态
        boolean isCheckedFlag = sp.getBoolean("updateStatus", false);
        if (isCheckedFlag) {
            switchView.sw.setChecked(true);
            switchView.setText(SWITCH_ON);
            switchView.setTextColor(Color.parseColor("#66000000"));
        } else {
            switchView.sw.setChecked(false);
            switchView.setTextColor(Color.parseColor("#66FF0000"));
            //switchView.setText(SWITCH_OFF);
        }
        switchView.setSwitchOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchView.sw.isChecked()) {
                    //LogUtil.show(SettingActivity.this,"ON");
                    editor.putBoolean("updateStatus", true);
                    switchView.setText(SWITCH_ON);
                    switchView.setTextColor(Color.parseColor("#66000000"));

                } else {
                    //LogUtil.show(SettingActivity.this,"OFF");
                    editor.putBoolean("updateStatus", false);
                    switchView.setTextColor(Color.parseColor("#66FF0000"));
                    //switchView.setText(SWITCH_OFF);
                }
                editor.commit();
            }
        });
    }
    /**
     * method desc.  :appLockSet 应用锁服务开启和关闭
     * params        :[]
     * return        :void
     */
    private void appLockSet() {
        sw_app_lock = (SwitchView)findViewById(R.id.sw_app_lock);
        final Intent appLockIntent = new Intent(SettingActivity.this, WatchDogService.class);

        //返回后再次进入保持之前状态
        isBlock = ServiceStateUtil.serviceState(SettingActivity.this, "com.maybe.android.mobilesafe.service.WatchDogService");
        if (isBlock) {
            sw_app_lock.sw.setChecked(true);
            sw_app_lock.setText(APP_LOCK_SWITCH_ON);
            sw_app_lock.setTextColor(Color.parseColor("#66000000"));
        } else {
            sw_app_lock.sw.setChecked(false);
            sw_app_lock.setTextColor(Color.parseColor("#66FF0000"));
            sw_app_lock.setText(APP_LOCK_SWITCH_OFF);
        }

        sw_app_lock.setSwitchOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sw_app_lock.sw.isChecked()) {
                    sw_app_lock.setText(APP_LOCK_SWITCH_ON);
                    sw_app_lock.setTextColor(Color.parseColor("#66000000"));
                    startService(appLockIntent);
                } else {
                    sw_app_lock.setTextColor(Color.parseColor("#66FF0000"));
                    stopService(appLockIntent);
                    sw_app_lock.setText(APP_LOCK_SWITCH_OFF);
                }
            }
        });
    }
    /**
     * method desc.  :blackNumberSet 黑名单拦截
     * params        :[]
     * return        :void
     */
    private void blackNumberSet() {
        blockBlackNum = (SwitchView)findViewById(R.id.block_black_num);
        final Intent BlockIntent = new Intent(SettingActivity.this, BlackNumberService.class);

        //返回后再次进入保持之前状态
        isBlock = ServiceStateUtil.serviceState(SettingActivity.this, "com.maybe.android.mobilesafe.service.BlackNumberService");
        if (isBlock) {
            blockBlackNum.sw.setChecked(true);
            blockBlackNum.setText(BLOCK_SWITCH_ON);
            blockBlackNum.setTextColor(Color.parseColor("#66000000"));
        } else {
            blockBlackNum.sw.setChecked(false);
            blockBlackNum.setTextColor(Color.parseColor("#66FF0000"));
            blockBlackNum.setText(BLOCK_SWITCH_OFF);
        }

        blockBlackNum.setSwitchOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (blockBlackNum.sw.isChecked()) {
                    blockBlackNum.setText(BLOCK_SWITCH_ON);
                    blockBlackNum.setTextColor(Color.parseColor("#66000000"));
                    startService(BlockIntent);
                } else {
                    blockBlackNum.setTextColor(Color.parseColor("#66FF0000"));
                    stopService(BlockIntent);
                    blockBlackNum.setText(BLOCK_SWITCH_OFF);
                }
            }
        });

    }
    /**
     * method desc.  :callAddressSet 来电归属地设置开关
     * params        :[]
     * return        :void
     */
    private void callAddressSet() {
        final Intent callAddIntent = new Intent(SettingActivity.this, NumAddressService.class);

        sv = (SwitchView) findViewById(R.id.num_add);

        //返回后再次进入保持之前状态
        isRunning = ServiceStateUtil.serviceState(SettingActivity.this, "com.maybe.android.mobilesafe.service.NumAddressService");
        if (isRunning) {
            sv.sw.setChecked(true);
            sv.setText(CALL_SWITCH_ON);
            sv.setTextColor(Color.parseColor("#66000000"));
        } else {
            sv.sw.setChecked(false);
            sv.setTextColor(Color.parseColor("#66FF0000"));
            sv.setText(CALL_SWITCH_OFF);
        }
        sv.setSwitchOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sv.sw.isChecked()) {
                    sv.setText(CALL_SWITCH_ON);
                    sv.setTextColor(Color.parseColor("#66000000"));
                    startService(callAddIntent);
                } else {
                    sv.setTextColor(Color.parseColor("#66FF0000"));
                    stopService(callAddIntent);
                    sv.setText(CALL_SWITCH_OFF);
                }
            }
        });
    }


    /**
     * method desc.  :changeAddressDialog 归属地提示框风格设置选择
     * params        :[]
     * return        :void
     */
    private void changeAddressDialog() {
        changeAddDialog = (ClickView) findViewById(R.id.num_add_tips);
        final String[] items = new String[]{"草地绿", "天空蓝", "少女粉"};
        final int i = sp.getInt("which", 0);
        changeAddDialog.setText(items[i]);
        changeAddDialog.setCardOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder choice = new AlertDialog.Builder(SettingActivity.this);
                choice.setTitle("归属地提示框风格")
                        .setSingleChoiceItems(items, i, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //保存选择
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("which", which);
                                editor.commit();
                                //显示选择
                                changeAddDialog.setText(items[which]);
                                dialog.dismiss();
                            }
                        }).show();
            }
        });


    }

    /**
     * method desc.  :changeAddressLoc 修改提示框显示位置
     * params        :[]
     * return        :void
     */
    private void changeAddressLoc() {
        ClickView addTipsLoc = (ClickView)findViewById(R.id.add_tips_loc);
        addTipsLoc.setCardOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,AddressLoc.class);
                startActivity(intent);
            }
        });

    }


    /**
     * fun desc. :返回键
     * params.   :
     *
     * @return :
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
