package com.maybe.android.mobilesafe.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/8.
 */

public class StreamTools {
    /**
     * fun desc. :将输入流转换为字符串
     * params.   :is 输入流
     * @return   :String 字符串
     */
    public static String readFromStream(InputStream is) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1){
            baos.write(buffer,0,len);
        }
        is.close();
        String result = baos.toString();
        baos.close();
        return result;
    }
}
