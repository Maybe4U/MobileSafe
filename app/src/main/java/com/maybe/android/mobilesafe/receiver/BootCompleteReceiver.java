package com.maybe.android.mobilesafe.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.maybe.android.mobilesafe.utils.LogUtil;

/**
 * Project Name:
 * Class desc. :BootCompleteReceiver 开机广播接收者
 *     date    : 2017/12/14
 *    author   : Maybe
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TelephonyManager tm;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = sp.edit();

        //if(sp.getBoolean("isSet",false)){
            LogUtil.e("isSet",sp.getBoolean("isSet",false) + "");
            String savedSim = sp.getString("sim", "") + "1";
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //版本校验
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            String currentSim = tm.getSimSerialNumber();
            if(savedSim.equals(currentSim)){

            }else {
                Toast.makeText(context,"SIM卡变更",Toast.LENGTH_SHORT).show();
                LogUtil.e("Receiver","SIM卡变更");
                SmsManager.getDefault().sendTextMessage(sp.getString("number",""),null,"SIM Card is changed!",null,null);
            }
        //}
    }
}
