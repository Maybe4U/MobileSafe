package com.maybe.android.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.security.Provider;

/**
 * Created by Administrator on 2017/12/15.
 */

public class GPSService extends Service {

    private LocationManager lm;
    private MyLocationListener listener;
    private SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //在服务里监听位置的变化
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //设置最优定位方式
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = lm.getBestProvider(criteria,true);
        lm.requestLocationUpdates(provider, 0, 0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(listener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyLocationListener implements LocationListener {
        //当位置改变的时候调用
        @Override
        public void onLocationChanged(Location location) {
            String latitude = "la:" + location.getLatitude() + "\n";
            String longitude = "long" + location.getLongitude() + "\n";
            String accuracy = "ac" + location.getAccuracy() + "\n";
            sp = getSharedPreferences("config",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastLocation",latitude + longitude + accuracy);
            editor.commit();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
