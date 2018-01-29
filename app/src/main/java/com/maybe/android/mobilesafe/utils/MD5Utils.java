package com.maybe.android.mobilesafe.utils;

import android.os.Message;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Project Name:MD5Utils
 * Class desc. :MD5加密算法
 *     date    : 2017/12/12
 *    author   : Maybe
 */

public class MD5Utils {

    private static StringBuffer buffer;

    /**
     * fun desc. : 传入密码进行MD5加密
     * params.   :密码
     * @return   :返回密文
     */
    public static String encoder(String password){

        try {
            //1.信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            //2.变成byte数组
            byte [] bytes = digest.digest(password.getBytes());
            buffer = new StringBuffer();
            //3.每个byte与8个二进制位做与运算
            for (byte b:bytes) {
               int number = b & 0xff ;
                //4.转换成16进制
                String numberStr = Integer.toHexString(number);
                //5.不足位的补全
                if(numberStr.length() == 1){
                    buffer.append("0");
                }
                buffer.append(numberStr);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
