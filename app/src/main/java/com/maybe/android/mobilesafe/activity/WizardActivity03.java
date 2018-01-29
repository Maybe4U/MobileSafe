package com.maybe.android.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.utils.LogUtil;

/**
 * Created by Administrator on 2017/12/13.
 */

public class WizardActivity03 extends BaseWizardActivity {
    private static final int PICK_CONTACT = 0;
    private static final String TAG1 = "WizardActivity03";
    private String number;
    private String name;
    private EditText safeContact;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wizard_activity_03);
        safeContact = (EditText) findViewById(R.id.safe_contact);
        //EditText获得焦点时自动弹出数字软键盘
        safeContact.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        editor = sp.edit();

            Intent intent = getIntent();
            number = intent.getStringExtra("number");
            if(number == null){
                number = sp.getString("number","");
            }
            safeContact.setText(number);
            //保存安全号码
            editor.putString("number",number);
            editor.commit();



    }

    @Override
    public void showNext() {
        //校验安全号码是否为空并保存安全号码
        String number = safeContact.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            Toast.makeText(this,"安全号码还未设置",Toast.LENGTH_SHORT).show();
            return;
        }
        editor.putString("number",number);
        editor.commit();
        Intent intent = new Intent(this, WizardActivity04.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void showPre() {
        String number = safeContact.getText().toString().trim();
        editor.putString("number",number);
        editor.commit();
        Intent intent = new Intent(this, WizardActivity02.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    //选择联系人
    public void chooseContact(View view) {
        Intent intent = new Intent(this,ContactActivity.class);
        startActivity(intent);
        finish();
    }
}
