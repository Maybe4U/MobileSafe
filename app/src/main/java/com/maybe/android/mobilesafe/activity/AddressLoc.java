package com.maybe.android.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.utils.LogUtil;

public class AddressLoc extends AppCompatActivity {

    private ImageView dragView;
    private SharedPreferences sp;
    private WindowManager wm;
    private CardView viewTop;
    private CardView viewBottom;

    //双击事件
    private long[] mHits = new long[2];
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtips_location);
        viewTop = (CardView)findViewById(R.id.card_view);
        viewBottom = (CardView)findViewById(R.id.card_view_bottom);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        dragView = (ImageView) findViewById(R.id.drag_item);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final int mHeight = wm.getDefaultDisplay().getHeight();

        final int mWidth = wm.getDefaultDisplay().getWidth();
        Log.e("height",mHeight + "");
        Log.e("Width",mWidth + "");
        int lastX = sp.getInt("lastX", dragView.getLeft());
        int lastY = sp.getInt("lastY", dragView.getTop());
        Log.e("lastY",lastY + "");
        if(lastY > mHeight / 2){
            viewTop.setVisibility(View.VISIBLE);
            viewBottom.setVisibility(View.INVISIBLE);
        }else {
            viewTop.setVisibility(View.INVISIBLE);
            viewBottom.setVisibility(View.VISIBLE);
        }

        /**
         * 一个控件或者View从创建到显示过程的主要方法
         * 1.构造方法
         * 2.测量-onMeasure(boolean,int,int,int,int);
         * 3.指定位置-onLayout();
         * 4.onDraw(canvas);
         */
        //ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)dragView.getLayoutParams();
        final ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) dragView.getLayoutParams();


        marginParams.leftMargin = lastX;
        marginParams.topMargin = lastY;
        marginParams.setMargins(lastX, lastY, 0, 0);
        dragView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if(mHits[0] >= (SystemClock.uptimeMillis() - 500)){
                    //双击事件 双击居中
                    dragView.layout(mWidth / 2 - dragView.getWidth() / 2, dragView.getTop(), mWidth / 2 + dragView.getWidth() / 2, dragView.getBottom());
                    saveParams();
                }
            }
        });

        //设置触摸事件 在activity中，只对其设置触摸事件，返回true
        //如果还设置了点击事件，则返回false
        dragView.setOnTouchListener(new View.OnTouchListener() {

            float startX = 0;
            float startY = 0;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://手指按下
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE://手指滑动
                        float newX = event.getRawX();
                        float newY = event.getRawY();

                        int dX = (int) (newX - startX);
                        int dY = (int) (newY - startY);
                        //屏蔽非法拖拽
                        int newl = dragView.getLeft() + dX;
                        int newt = dragView.getTop() + dY;
                        int newr = dragView.getRight() + dX;
                        int newb = dragView.getBottom() + dY;

                        if(newl < 0 || newt < 0 || newr > mWidth || newb > mHeight - 60)
                        {
                            break;
                        }
                        dragView.layout(newl, newt, newr, newb);

                        if(newt > mHeight /2){
                            viewTop.setVisibility(View.VISIBLE);
                            viewBottom.setVisibility(View.INVISIBLE);
                        }else {
                            viewTop.setVisibility(View.INVISIBLE);
                            viewBottom.setVisibility(View.VISIBLE);
                        }

                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP://手指抬起
                        saveParams();
                        break;
                }
                return false;
            }


        });
    }
    private void saveParams() {
        editor.putInt("lastX", dragView.getLeft());
        editor.putInt("lastY", dragView.getTop());
        editor.commit();
    }
}
