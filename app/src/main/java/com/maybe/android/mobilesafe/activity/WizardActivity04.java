package com.maybe.android.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.receiver.MyAdmin;
import com.maybe.android.mobilesafe.utils.LogUtil;
import com.maybe.android.mobilesafe.view.SwitchView;

/**
 * Created by Administrator on 2017/12/13.
 */

public class WizardActivity04 extends BaseWizardActivity {
    private SharedPreferences.Editor editor;
    private SwitchView switchView;
    private boolean isSecurity;
    private DevicePolicyManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_activity_04);
        securitySwitch();
    }
    /**
     * fun desc. :防盗保护开关
     * params.   :
     * @return   :
     */
    private void securitySwitch() {

        editor = sp.edit();
        switchView = (SwitchView)findViewById(R.id.slt);
        isSecurity = sp.getBoolean("isSecurity",false);
        if(isSecurity){
            switchView.setText("当前已开启防盗保护");
            switchView.setTextColor(Color.parseColor("#66000000"));
        }else {
            switchView.setText("当前未开启防盗保护");
            switchView.setTextColor(Color.parseColor("#66FF0000"));
        }
        switchView.sw.setChecked(isSecurity);
        LogUtil.e("securitySwitch",isSecurity + "");

        switchView.setSwitchOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean("isSecurity",true);
                    switchView.setText("当前已开启防盗保护");
                    switchView.setTextColor(Color.parseColor("#66000000"));

                    ComponentName name = new ComponentName(WizardActivity04.this, MyAdmin.class);
                    manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    if(!manager.isAdminActive(name)){
                        Intent AdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        AdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, name);
                        AdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "该功能仅当安全号码向该手机发送锁屏指令短信时使用。");
                        startActivityForResult(AdminIntent,1);
                    }
                }else{
                    editor.putBoolean("isSecurity",false);
                    switchView.setText("当前未开启防盗保护");
                    switchView.setTextColor(Color.parseColor("#66FF0000"));
                }
                editor.commit();
            }
        });
    }

    @Override
    public void showNext() {
            editor.putBoolean("isSet",true);
            editor.commit();
            Intent intent = new Intent(this,SafeActivity.class);
            startActivity(intent);
            Toast.makeText(this,"设置成功",Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this,WizardActivity03.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                break;
            case RESULT_CANCELED:
            case RESULT_FIRST_USER:
                editor.putBoolean("isSecurity",false);
                switchView.setText("当前未开启防盗保护");
                switchView.setTextColor(Color.parseColor("#66FF0000"));
                switchView.sw.setChecked(false);
                editor.commit();
              break;
        }
    }
}
