package com.maybe.android.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.maybe.android.mobilesafe.R;


/**
 * Created by Administrator on 2017/12/15.
 */

public class LockActivity extends AppCompatActivity {

    private DevicePolicyManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_layout);

    }
}
