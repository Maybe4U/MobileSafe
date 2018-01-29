package com.maybe.android.mobilesafe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.db.dao.NumAddressQueryDAO;

/**
 * Created by Administrator on 2017/12/16.
 */

public class NumberQuery extends AppCompatActivity {

    private EditText numberInput;
    private TextView result;
    private Button queryBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_check_layout);
        numberInput = (EditText)findViewById(R.id.number_input);
        result = (TextView)findViewById(R.id.query_result);

        //输入号码时动态查询显示到查询结果上
        numberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //1 得到号码
                String number = numberInput.getText().toString().trim();
                //2 判断是否为空
                if(TextUtils.isEmpty(number)){

                }
                //3 开始查询
                String address = NumAddressQueryDAO.getAddress(number);
                result.setText(address);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        queryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //1 得到号码
//                String number = numberInput.getText().toString().trim();
//                //2 判断是否为空
//                if(TextUtils.isEmpty(number)){
//
//                }
//                //3 开始查询
//                String address = NumAddressQueryDAO.getAddress(number);
//                result.setText(address);
//            }
//        });
    }
}
