package com.maybe.android.mobilesafe.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.maybe.android.mobilesafe.R;

/**
 * Class Name:ClickView
 * Class desc. :自定义控件
 * date    : 2017/12/11
 * author   : Maybe
 */

public class ClickView extends LinearLayout {

    private TextView tv_switchInfo;
    private String title;
    private TextView tv_title;
    private CardView cardView;

    //在代码中实例化的时候使用
    public ClickView(Context context) {
        super(context);
        initView(context);
    }

    //在布局文件实例化的时候使用
    public ClickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        tv_title.setText(title);

    }

    private void initView(Context context) {
        //inflate :把布局文件转化成view
        //最后一个参数：添加布局文件的父类,也就是把布局文件挂载在SwitchLayout上
        View.inflate(context, R.layout.setting_click_cardview, this);
        tv_title = findViewById(R.id.text_item_up);
        tv_switchInfo = (TextView) findViewById(R.id.text_item_down);
        cardView = (CardView)findViewById(R.id.card_view);
    }

    public void setCardOnClickListener(CompoundButton.OnClickListener listener) {
        cardView.setOnClickListener(listener);
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

}
