package com.maybe.android.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.maybe.android.mobilesafe.R;

/**
 * Created by Administrator on 2017/12/13.
 */

public class WizardActivity01 extends BaseWizardActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_activity_01);
    }
    //显示下一页设置向导
    public void showNext() {
        Intent intent = new Intent(this,WizardActivity02.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void showPre() {

    }

}
