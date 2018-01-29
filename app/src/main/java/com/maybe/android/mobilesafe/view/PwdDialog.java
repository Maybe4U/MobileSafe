package com.maybe.android.mobilesafe.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.utils.LogUtil;

/**
 * Created by Administrator on 2017/12/12.
 */

public class PwdDialog extends BaseDialog {
    private PwdInputView pwdInputView;
    private TextView tvTitle;
    public PwdDialog(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.input_dialog_layout, null);
        tvTitle = (TextView)view.findViewById(R.id.tv_title);

    }

    public void setTvTitle(String string){
        tvTitle.setText(string);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_dialog_layout);

        pwdInputView = (PwdInputView) findViewById(R.id.piv);

        pwdInputView.initStyle(R.drawable.edit_num_bg, 6, 0.5f, R.color.colorFunTheme, R.color.colorFunTheme, 20);
        pwdInputView.setOnTextFinishListener(new PwdInputView.OnTextFinishListener() {

            public void onFinish(String str) {//密码输入完后的回调
//                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
//                SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                String password_1 = sp.getString("password_1",null);
//                if(TextUtils.isEmpty(password_1)){
//                    editor.putString("password_1",str);
//                    Toast.makeText(context, "设置成功！", Toast.LENGTH_SHORT).show();
//                }else{
//                    editor.putString("password_2",str);
//                }

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pwdInputView.setFocus();
            }
        }, 100);

    }
}
