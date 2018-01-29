package com.maybe.android.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2018\1\1 0001.
 */

public class SmsBackupUtil {

    public interface SmsBackupCallBack{
        void backupBefore(int total);

        void backupProgress(int progress);
    }

    public static void smsBackup(Context context, String path, SmsBackupCallBack callBack) throws Exception {

        XmlSerializer serializer = Xml.newSerializer();
        File file = new File(path);
        FileOutputStream os = new FileOutputStream(file);
        serializer.setOutput(os,"utf-8");
        serializer.startDocument("utf-8",true);
        serializer.startTag(null,"smss");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"},
                null, null, null);
        //int max = cursor.getCount();
        callBack.backupBefore(cursor.getCount());
        int progress = 0;
        while(cursor.moveToNext()){
            serializer.startTag(null,"sms");

            serializer.startTag(null,"address");
            String address = cursor.getString(0);
            serializer.endTag(null,"address");

            serializer.startTag(null,"date");
            String date = cursor.getString(1);
            serializer.endTag(null,"date");

            serializer.startTag(null,"type");
            String type = cursor.getString(2);
            serializer.endTag(null,"type");

            serializer.startTag(null,"body");
            String body = cursor.getString(3);
            serializer.endTag(null,"body");

            serializer.endTag(null,"sms");

            progress ++ ;
            //dialog.setProgress(progress);
            callBack.backupProgress(progress);
        }
        cursor.close();

        serializer.endTag(null,"smss");
        serializer.endDocument();
    }
}
