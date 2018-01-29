package com.maybe.android.mobilesafe.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.maybe.android.mobilesafe.R;

/**
 * Class Name:SwitchView
 * Class desc. :自定义控件
 * date    : 2017/12/11
 * author   : Maybe
 */

public class SwitchView extends LinearLayout {

    public Switch sw;
    private TextView tv_switchInfo;
    private String title;
    private String switchOn;
    private String switchOff;
    private TextView tv_title;

    //在代码中实例化的时候使用
    public SwitchView(Context context) {
        super(context);
        initView(context);
    }

    //在布局文件实例化的时候使用
    public SwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        //switchOn = attrs.getAttributeValue(nameSpace, "switch_on");
        switchOff = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "switch_off");
        tv_title.setText(title);
        tv_switchInfo.setText(switchOff);

    }

    private void initView(Context context) {
        //inflate :把布局文件转化成view
        //最后一个参数：添加布局文件的父类,也就是把布局文件挂载在SwitchLayout上
        View.inflate(context, R.layout.setting_item_cardview, this);
        sw = (Switch) findViewById(R.id.switch_item);
        tv_title = findViewById(R.id.text_item_up);
        tv_switchInfo = (TextView) findViewById(R.id.text_item_down);
    }

    public void setSwitchOnClickListener(CompoundButton.OnCheckedChangeListener listener) {
        sw.setOnCheckedChangeListener(listener);
    }


    /**
     * fun desc. :为自定义控件中的textView设置文字
     * params.   :
     *
     * @return :
     */
    public void setText(String string) {
        tv_switchInfo.setText(string);
    }
    public void setTextColor(int color) {
        tv_switchInfo.setTextColor(color);
    }



    /**
     * fun desc. :判断switch状态
     * params.   :
     *
     * @return :打开返回true
     */
//    public void isChecked(final String str1,final String str2){
//        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(sw.isChecked()){
//                    LogUtil.d("sw.isChecked()",sw.isChecked()+"");
//
//                    tv.setText(str1);
//                }else{
//                    LogUtil.d("sw.isChecked()",sw.isChecked()+"");
//                    tv.setText(str2);
//                }
//            }
//        });
//    }
}
