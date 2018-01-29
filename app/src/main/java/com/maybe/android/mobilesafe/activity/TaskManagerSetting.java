package com.maybe.android.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.service.KillBgProcessService;
import com.maybe.android.mobilesafe.utils.ServiceStateUtil;
import com.maybe.android.mobilesafe.view.SwitchView;

/**
 * Created by Administrator on 2018/1/23.
 */

public class TaskManagerSetting extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_manager_setting);
        SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        final SwitchView show_sys_task = (SwitchView)findViewById(R.id.show_sys_task);
        boolean showSys = sp.getBoolean("showSys",true);

        //初始化显示开关状态
        if(showSys){
            show_sys_task.setText("当前状态：显示系统进程");
        }else{
            show_sys_task.setText("当前状态：隐藏系统进程");
        }
        show_sys_task.sw.setChecked(showSys);

        //开关监听
        show_sys_task.setSwitchOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    show_sys_task.setText("当前状态：显示系统进程");
                }else{
                    show_sys_task.setText("当前状态：隐藏系统进程");
                }
                show_sys_task.sw.setChecked(isChecked);
                editor.putBoolean("showSys",isChecked);
                editor.commit();
            }
        });

        final Intent killIntent = new Intent(TaskManagerSetting.this, KillBgProcessService.class);
        final SwitchView kill_process_bg = (SwitchView)findViewById(R.id.kill_process_bg);
        boolean isRunning = ServiceStateUtil.serviceState(this,"com.maybe.android.mobilesafe.service.KillBgProcessService");
        if(isRunning){
            kill_process_bg.setText("当前状态：锁屏清理");
        }else{
            kill_process_bg.setText("当前状态：锁屏不清理");
        }
        kill_process_bg.sw.setChecked(isRunning);
        kill_process_bg.setSwitchOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    kill_process_bg.setText("当前状态：锁屏清理");
                    startService(killIntent);
                }else{
                    kill_process_bg.setText("当前状态：锁屏不清理");
                    stopService(killIntent);
                }
                kill_process_bg.sw.setChecked(isChecked);
            }
        });
    }
}
