package com.maybe.android.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2017/12/13.
 */

public abstract class BaseWizardActivity extends AppCompatActivity {
    private GestureDetector detector;
    protected SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        editor = sp.edit();
        //创建手势识别器
        detector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                //屏蔽滑动慢
                if (Math.abs(velocityX)<100){
                    return true;
                }
                //屏蔽斜滑
                if (Math.abs(e1.getY()-e2.getY())>100){
                    return true;
                }
                //向右滑
                if((e1.getX()-e2.getX())>200){
                    showNext();
                    return true;
                }
                //向左滑
                if((e2.getX()-e1.getX())>200){
                    showPre();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    //处理手势事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //显示下一页设置向导
    public abstract void showNext();
    //显示上一页设置向导
    public abstract void showPre();

    //下一页按钮点击事件
    public void next(View v){
        showNext();
    }
    //上一页按钮点击事件
    public void pre(View v){
        showPre();
    }
}
