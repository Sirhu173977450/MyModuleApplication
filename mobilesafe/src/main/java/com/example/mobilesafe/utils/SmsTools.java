package com.example.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/6/10.
 */

public class SmsTools {

    /**
     * 声明一个接口，里面提供备份短信的回调函数
     * @author 小B
     *
     */
    public interface BackupCallback{
        /**
         * 短信备份前调用的代码
         * @param max 一共多少条短信需要备份
         */
        public void beforeSmsBackup(int max);
        /**
         * 短信备份过程中调用的代码
         * @param progress 当前备份的进度
         */
        public void onSmsBackup(int progress);
    }


    /**
     * 短信的备份
     * @param context 上下文
     * @param  callback
     */
    public static void backUpSms(Context context, BackupCallback callback){
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            XmlSerializer serializer = Xml.newSerializer();
            File file = new File(Environment.getExternalStorageDirectory(),"smsbackup.xml");
            FileOutputStream fos = new FileOutputStream(file);
            serializer.setOutput(fos, "utf-8");
            serializer.startDocument("utf-8", true);
            serializer.startTag(null, "infos");
            Cursor cursor = resolver.query(uri, new String[]{"address","body","type","date"}, null, null, null);
            //设置进度的最大值，具体是用什么控件设置最大值，小B不关心
            int progress = 0;
            callback.beforeSmsBackup(cursor.getCount());
            while(cursor.moveToNext()){
                serializer.startTag(null, "info");
                String address = cursor.getString(0);
                serializer.startTag(null, "address");
                serializer.text(address);
                serializer.endTag(null, "address");
                String body = cursor.getString(1);
                serializer.startTag(null, "body");
                serializer.text(body);
                serializer.endTag(null, "body");
                String type = cursor.getString(2);
                serializer.startTag(null, "type");
                serializer.text(type);
                serializer.endTag(null, "type");
                String date = cursor.getString(3);
                serializer.startTag(null, "date");
                serializer.text(date);
                serializer.endTag(null, "date");
                serializer.endTag(null, "info");
                SystemClock.sleep(1500);
                //更新进度，具体是用什么控件去更新进度小B也不关心
                progress++;
                callback.onSmsBackup(progress);
            }
            cursor.close();
            serializer.endTag(null, "infos");
            serializer.endDocument();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
