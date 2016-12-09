package com.chengtech.chengtechmt.util;


import com.google.gson.Gson;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.chengtech.chengtechmt.activity.standard.StandardListActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.Header;

public class HttpclientUtil {
    private static AsyncHttpClient client;

    public static synchronized AsyncHttpClient getInstance(Context context) {
        if (client == null) {
            client = new AsyncHttpClient();
            client.setTimeout(10 * 1000);
            PersistentCookieStore cookieStore = new PersistentCookieStore(
                    context);
            client.setCookieStore(cookieStore);

        }
        return client;
    }

    public static void clear() {
        client = null;
    }

    public static void  getData(final Context context, String url, final Handler handler, final int resultCode) {
        AsyncHttpClient client = HttpclientUtil.getInstance(context);

        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                try {
                    String data = new String(arg2, "utf-8");
                    Message message = handler.obtainMessage();
                    message.what = resultCode;
                    message.obj = data;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Toast.makeText(context, "数据解析出错", Toast.LENGTH_SHORT).show();
                }
                super.onSuccess(arg0, arg1, arg2);
            }


            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                super.onFailure(arg0, arg1, arg2, arg3);
                Toast.makeText(context, "连接服务器出错", Toast.LENGTH_SHORT).show();
            }
        };
        client.get(url,
                responseHandler);
    }

}
