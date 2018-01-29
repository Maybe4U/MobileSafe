package com.maybe.android.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.maybe.android.mobilesafe.db.dao.BlackNumberDAO;
import com.maybe.android.mobilesafe.utils.LogUtil;

import java.lang.reflect.Method;
import java.security.Provider;

/**
 * Created by Administrator on 2017/12/27.
 */

public class BlackNumberService extends Service {

    private InnerSmsReceiver receiver;
    private BlackNumberDAO dao;
    private TelephonyManager tm;
    private MyPhoneStateListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //内部短信拦截
    private class InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object [] pdus = (Object[]) intent.getExtras().get("pdus");
            LogUtil.e("this","收到一条短信");
            for (Object pdu : pdus){
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = sms.getOriginatingAddress();
                if(dao.query(sender)){
                    LogUtil.e("this","拦截到一条短信");
                    abortBroadcast();
                }
                String body = sms.getMessageBody();
            }
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDAO(this);
        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver, filter);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                        if(dao.query(incomingNumber)){
                            String mode = dao.queryMode(incomingNumber);
                            if("0".equals(mode) || "2".equals(mode)){
                                try {//1.得到字节码
                                    Class clazz = BlackNumberService.class.getClassLoader().loadClass("android.os.ServiceManager");
                                    //2.得到对应的方法getService
                                    Method method = clazz.getMethod("getService",String.class);

                                    //4.执行这个方法
                                    method.invoke(null,TELEPHONY_SERVICE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        receiver = null;

        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
    }
}
