package com.chengtech.chengtechmt.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.chengtech.chengtechmt.entity.attachment.AttachmentInfo;
import com.chengtech.chengtechmt.util.CommonUtils;
import com.chengtech.chengtechmt.util.DateUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * 作者: LiuFuYingWang on 2016/10/27 10:16.
 */

public class DownCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor cursor = manager.query(query);
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).name("chengtechmt").build();
            Realm realm = Realm.getInstance(realmConfiguration);
            while (cursor.moveToNext()) {
                try {
                    String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    title = title.substring(title.lastIndexOf("/")+1);
                    String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    address = URLDecoder.decode(address,"utf-8");
                    int size = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    String sizef = CommonUtils.ByteConversionGBMBKB(size);
                    long time = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP));
                    Date date = new Date(time);
                    //把对象添加进数据库
                    realm.beginTransaction();
                    AttachmentInfo info = realm.createObject(AttachmentInfo.class);
                    info.setFileName(title);
                    info.setFilePath(address);
                    info.setSize(sizef);
                    info.setTime(DateUtils.convertDate3(date));
                    info.setType(AttachmentInfo.ATTACHMENT_TYPE_DOWNLOADED);
                    realm.commitTransaction();
                    Toast.makeText(context, title + "文件已下载完成", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
            cursor.close();
            realm.close();
        } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS, -1);
            manager.remove(id);
            Toast.makeText(context, "文件取消下载", Toast.LENGTH_SHORT).show();
        }
    }
}
