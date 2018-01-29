package com.maybe.android.mobilesafe.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.view.SwitchView;

/**
 * Created by Administrator on 2017/12/13.
 */

public class WizardActivity02 extends BaseWizardActivity {
    private static final String SIM_BIND = "SIM卡已绑定";
    private static final String SIM_UNBIND = "SIM卡没有绑定";
    private TelephonyManager tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_activity_02);
        //检测绑定sim卡
        bindSIMCard();
    }

    @Override
    public void showNext() {
        String sim = sp.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(WizardActivity02.this, "请绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent intent = new Intent(this, WizardActivity03.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, WizardActivity01.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * fun desc. :检测SIM卡是否绑定
     * params.   :
     *
     * @return :
     */
    private void bindSIMCard() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        final SwitchView switchView = (SwitchView) findViewById(R.id.slt);

        //返回后再次进入保持之前状态
        String sim = sp.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            switchView.sw.setChecked(false);
            switchView.setText(SIM_UNBIND);
            switchView.setTextColor(Color.parseColor("#66FF0000"));
        } else {
            switchView.sw.setChecked(true);
            switchView.setText(SIM_BIND);
            switchView.setTextColor(Color.parseColor("#66000000"));
        }
        switchView.setSwitchOnClickListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchView.sw.isChecked()) {
                    if (ActivityCompat.checkSelfPermission(WizardActivity02.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String sim = tm.getSimSerialNumber();
                    editor.putString("sim", sim);
                    switchView.setText(SIM_BIND);
                    switchView.setTextColor(Color.parseColor("#66000000"));

                } else {
                    editor.putString("sim", null);
                    switchView.setText(SIM_UNBIND);
                    switchView.setTextColor(Color.parseColor("#66FF0000"));
                }
                editor.commit();
            }
        });
    }
//
//    public void onBackPressed(){
//        Toast.makeText(this,"请点击 上一步 返回",Toast.LENGTH_SHORT).show();
//        return;
//    }
}
