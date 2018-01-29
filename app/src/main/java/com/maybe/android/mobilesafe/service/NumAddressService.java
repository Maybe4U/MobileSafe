package com.maybe.android.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.db.dao.NumAddressQueryDAO;
import com.maybe.android.mobilesafe.utils.LogUtil;

import static android.telephony.PhoneStateListener.LISTEN_CALL_STATE;

/**
 * Project Name:NumAddressService
 * Class desc. :监听来电并显示来电归属地
 * date    : 2017/12/16
 * author   : Maybe
 */

public class NumAddressService extends Service {

    private TelephonyManager tm;
    private MyCallListener listener;
    private OutCallReceiver receiver;
    private WindowManager wm;
    private View view;
    private WindowManager.LayoutParams params;
    private Context mContext;
    private SharedPreferences sp;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        listener = new MyCallListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(listener, LISTEN_CALL_STATE);
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        LogUtil.e("onCreate", receiver + "");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
        unregisterReceiver(receiver);

    }

    private class MyCallListener extends PhoneStateListener {

//        public MyCallListener(Context context) {
//            mContext = context;
//        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = NumAddressQueryDAO.getAddress(incomingNumber);
                    showLocationToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e("MyCallListener", "IDLE");
                    if (view != null && wm != null) {
                        wm.removeView(view);
                    }
//                    dismissLocationToast();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * class Name  :OutCallReceiver
     * Class desc. :打电话时显示去电归属地
     * date    : 2017/12/16
     * author   : Maybe
     */
    private class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            LogUtil.e("number", number);
            String address = NumAddressQueryDAO.getAddress(number);
            showLocationToast(address);
        }
    }

    /**
     * method desc.  :showLocationToast  自定义Toast 显示来电归属地
     * params        :[address]    归属地信息
     * return        :void
     */
    private void showLocationToast(String address) {
        int[] items = {R.drawable.location_color_blue, R.drawable.location_color_green, R.drawable.location_color_pink};
        sp = getSharedPreferences("config", MODE_PRIVATE);
        view = View.inflate(this, R.layout.num_address_item, null);
        TextView showAdd = (TextView) view.findViewById(R.id.show_address);
        showAdd.setText(address);
        int i = sp.getInt("which", 0);
        view.setBackgroundResource(items[i]);
        //获取自定义位置的坐标
        final int lastX = sp.getInt("lastX", 0);
        int lastY = sp.getInt("lastY", 0);

        view.setOnTouchListener(new View.OnTouchListener() {
            int startX = 0;
            int startY = 0;

            public boolean onTouch(View v, MotionEvent event) {
                LogUtil.e("TAG","触发");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();

                        int dX = newX - startX;
                        int dY = newY - startY;
                        params.x += dX;
                        params.y += dY;
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > wm.getDefaultDisplay().getWidth() - view.getWidth()) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }
                        if (params.y > wm.getDefaultDisplay().getHeight() - view.getHeight()) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }
                        wm.updateViewLayout(view, params);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastX", params.x);
                        editor.putInt("lastY", params.y);
                        editor.commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();

        params.gravity = Gravity.TOP + Gravity.LEFT;
        params.x = lastX;
        params.y = lastY;

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                ;
        wm.addView(view, params);
    }
}
