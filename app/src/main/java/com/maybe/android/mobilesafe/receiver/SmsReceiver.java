package com.maybe.android.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.service.GPSService;
import com.maybe.android.mobilesafe.utils.LogUtil;

import java.io.IOException;

import static android.content.Context.DEVICE_POLICY_SERVICE;

/**
 * Project Name:SmsReceiver
 * Class desc. :短信接收广播，安全号码向该手机发送指令
 *     date    : 2017/12/15
 *    author   : Maybe
 */

public class SmsReceiver extends BroadcastReceiver {
    private SharedPreferences sp;
    private DevicePolicyManager manager;
    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for(Object pdu : pdus){
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);

            //得到发送者
            String sender = sms.getOriginatingAddress();
            String number = sp.getString("number","");
            //得到内容
            String body = sms.getMessageBody();
            //模拟器这么做
            if(sender.contains(number)){
                if("#location#".equals(body)){
                    //得到手机GPS位置
                    getCurrentPosition(context, sender);
                    LogUtil.e("不能用SMS","得到手机位置");
                }else if ("#alarm#".equals(body)){
                    //播放报警音乐
                    playAlarm(context);
                    LogUtil.e("不能用","播放报警音乐");
                }else if ("#wipdata#".equals(body)){
                    //远程删除数据
                    LogUtil.e("不能用","远程删除数据");
                }else if ("#lockscreen#".equals(body)){
                    //远程锁屏
                    LogUtil.e("不能用SMS","远程锁屏");
                    ComponentName name = new ComponentName(context,MyAdmin.class);
                    manager = (DevicePolicyManager) context.getSystemService(DEVICE_POLICY_SERVICE);
                    if(manager.isAdminActive(name)){
                        manager.lockNow();
                    }else{

                    }

                }

            }
        }
    }

    private void getCurrentPosition(Context context, String sender) {
        Intent locationIntent = new Intent(context, GPSService.class);
        context.startService(locationIntent);
        String lastLocation = sp.getString("lastLocation","");
        if(TextUtils.isEmpty(lastLocation)){
            SmsManager.getDefault().sendTextMessage(sender,null,"No Move",null,null);
        }else{
            SmsManager.getDefault().sendTextMessage(sender,null,lastLocation,null,null);
        }
    }

    private void playAlarm(Context context) {
        MediaPlayer player = MediaPlayer.create(context, R.raw.xqt);
        player.setVolume(1.0f,1.0f);
        player.start();
    }
}
