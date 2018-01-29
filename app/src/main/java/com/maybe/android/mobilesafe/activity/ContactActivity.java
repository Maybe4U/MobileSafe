package com.maybe.android.mobilesafe.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.maybe.android.mobilesafe.R;
import com.maybe.android.mobilesafe.utils.Contact;
import com.maybe.android.mobilesafe.utils.ContactAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Name:
 * Class desc. :选取联系人
 * date    : 2017/12/14
 * author   : Maybe
 */

public class ContactActivity extends AppCompatActivity {

    private RecyclerView contactListView;
    List<Contact> contactList = new ArrayList<>();

    Handler handler = new Handler(){
        public void handleMessage(Message message){
            ContactAdapter adapter = new ContactAdapter(contactList);
            contactListView.setAdapter(adapter);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list_view);
        contactListView = (RecyclerView) findViewById(R.id.contact_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1);
        contactListView.setLayoutManager(layoutManager);
        loadContact();
    }
    /**
     * fun desc. :加载联系人，防止UI阻塞
     * params.   :
     * @return   :
     */
    private void loadContact() {
        new Thread(){
            @Override
            public void run() {
                contactList = getAllContacts();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private List<Contact> getAllContacts() {
        String number = "";
        String name = "";
        Contact[] contacts = new Contact[1024];
        int i = 0;
        ContentResolver resolver = getContentResolver();
        Uri raw_contacts_uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri data_uri = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resolver.query(raw_contacts_uri, new String[]{"contact_id"}, null, null, null);
        while(cursor.moveToNext()){
            String contact_id = cursor.getString(0);
            if(contact_id != null){
                Cursor data_cursor = resolver.query(data_uri,new String[]{"data1","mimetype"},"raw_contact_id=?",new String[]{contact_id},null);
                while (data_cursor.moveToNext()){
                    String data1 = data_cursor.getString(0);
                    String mimetype = data_cursor.getString(1);
                    if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                        //电话号码
                        number = data1;
                    }else if("vnd.android.cursor.item/name".equals(mimetype)){
                        //姓名
                        name = data1;
                    }
                }
                Contact contact = new Contact(name,number);
                contacts[i++] = new Contact(name,number);
                data_cursor.close();
            }
        }
        contactList.clear();
        for(int c=0;c<i;c++){
            contactList.add(contacts[c]);
        }
        return contactList;
    }

}
